package com.leonside.dataroad.flink.utils;

import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.context.ComponentHolder;
import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.core.component.ComponentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Slf4j
public class PluginJarHelper {

    private static Logger logger = LoggerFactory.getLogger(PluginJarHelper.class);

    public static Set<String> findDependOnJars(String pluginDir, List<ComponentHolder> componentHolders){
        Set<String> allJars = new HashSet<>();
        componentHolders.stream().forEach(cmp -> {
            File dependOnJar = findDependOnJar(pluginDir, cmp);
            if(dependOnJar != null){
                allJars.add(dependOnJar.toURI().toString());
            }
        });
        log.debug("Get the dependent Component Jar：" + StringUtils.join(allJars, ","));
        return allJars;
    }
    public static File findDependOnJar(String pluginDir, ComponentHolder componentHolder) {
        if(StringUtils.isEmpty(pluginDir)){
            return null;
        }

        Collection<File> files = FileUtils.listFiles(new File(pluginDir), null , true);
        Optional<File> first = files.stream().filter(file -> {
            boolean isMatch = false;
            try {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    ComponentType componentType = ComponentType.valueOfNotException(componentHolder.getType());
                    if (componentType != null && jarEntry.getName().equals("META-INF/services/" + componentType.getSpi().getName())) {
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = bufferedReader.readLine();

                        while (StringUtils.isNotEmpty(line)) {
                            String type = line.split("=")[0];
                            if (type.equals(componentHolder.getPluginName())) {
                                return true;
                            }
                            line = bufferedReader.readLine();
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
            return isMatch;
        }).findFirst();
        return first.isPresent() ? first.get() : null;
    }

    /**
     * 加载 extLibPath 及
     * @param environment
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public static void loadJar( StreamExecutionEnvironment environment, ExecuteContext executeContext) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {

        Set<String> allJars = new HashSet<>();

        //pluginRootDir不为空，根据配置的插件获取对应的jar路径
        String pluginDir = Options.getPluginDir(executeContext.getOptions().getPluginRootDir());
        if(StringUtils.isNotEmpty(pluginDir)){
            Set<String> dependOnJars = findDependOnJars(pluginDir, executeContext.getComponentHolders());
            allJars.addAll(dependOnJars);
        }

        //合并extLibPath配置的jar，并加载
        String[] extLibPath = executeContext.getOptions().getExtLibPath();
        if(ArrayUtils.isNotEmpty(extLibPath)){
            allJars.addAll(Arrays.asList(extLibPath));
        }

        loadJar(allJars, environment);
    }
    public static void loadJar(Set<String> libPaths, StreamExecutionEnvironment environment) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        if(CollectionUtils.isEmpty(libPaths)){
            return;
        }

        List<URL> urls = libPaths.stream().map(path -> {
            try {
                if(path.startsWith(JobCommonConstant.JOBSCHEMA_PATH_PREFIX_FILESYSTEM)){
                    File file = null;
                    try {
                        file = new File(new URL(path).toURI());
                    } catch (URISyntaxException e) {
                        logger.error("url is error: "+ path,e);
                    }
                    if(!file.exists()){
                        throw new JobConfigException("extLibPath is not exists: " + path);
                    }
                    return file.toURI().toURL();
                }else{
                    return new URL(path);
                }
            } catch (MalformedURLException e) {
                throw new JobConfigException("extLibPath config is illegal:" + path, e);
            }
        }).collect(Collectors.toList());

        log.info("add extlib :" + urls.stream().map(url->url.toString()).collect(Collectors.joining(",")));

        //classloader加载jar
        addURLToClassLoader(urls);

        //添加至pipeline.classpaths中
        addURLToExecutionEnvironment(environment, urls);

    }

    private static void addURLToExecutionEnvironment(StreamExecutionEnvironment environment, List<URL> urls) throws NoSuchFieldException, IllegalAccessException {
        Field configuration = StreamExecutionEnvironment.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Configuration o = (Configuration)configuration.get(environment);

        Field confData = Configuration.class.getDeclaredField("confData");
        confData.setAccessible(true);
        Map<String,Object> temp = (Map<String,Object>)confData.get(o);
        List<String> jarList = urls.stream().map(url -> url.toString()).collect(Collectors.toList());
        temp.put("pipeline.classpaths",jarList);
    }

    private static void addURLToClassLoader(List<URL> urls) throws NoSuchMethodException {
        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        boolean accessible = method.isAccessible();
        try {
            if (accessible == false) {
                method.setAccessible(true);
            }
            URLClassLoader classLoader = (URLClassLoader) PluginJarHelper.class.getClassLoader();

            //jar路径加入到系统url路径里
            urls.stream().forEach(url-> {
                try {
                    method.invoke(classLoader, url);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new JobConfigException("invoke exception",e);
                }
            });

        }  finally {
            method.setAccessible(accessible);
        }
    }

//    public static void main(String[] args) throws IOException {
//        File dependOnJar = findDependOnJar("D:\\myblog\\dataroad-dist\\plugins", new ComponentHolder("reader", "mysqlReader"));
//        System.out.println(dependOnJar);
//    }
}
