package com.leonside.dataroad.dashboard.configuration;

import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.dashboard.utils.NetUtil;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * @author leon
 */
@ConfigurationProperties(prefix = "dataroad")
@Component
@Data
public class DataroadProperties {

    private String serverAddress;

    private String webUi;

    private String dataroadDist;

    private boolean sampleEnabled = false;

    public File getDataroadDistMainJar(){

        File dataroadDistFile = new File(dataroadDist);
        if(!dataroadDistFile.exists()){
            throw new JobConfigException("dataroad dist is not exsist:" + dataroadDist);
        }

        File[] files = dataroadDistFile.listFiles();

        Optional<File> dataroadFile = Arrays.stream(files).filter(file -> {
            return file.getName().startsWith("dataroad") && file.getName().endsWith(".jar");
        }).findFirst();

        if(!dataroadFile.isPresent()){
            throw new JobConfigException("dataroad main jar is not exsist,check dataroad-dist is correct");
        }

        return dataroadFile.get() ;
    }

    public String getDataroadPluginPath() {
        return Options.getPluginDir(dataroadDist);
    }

    public String getJobJarSubmitURL(){
        return webUi + "/jars/upload";
    }

    public String getJobJarListURL(){
        return webUi + "/jars";
    }

    public String getJobSubmitURL(String jarId){
        return webUi + "/jars/"+jarId+"/run";
    }

    public String getConfURL(String jobId){
        return getServerURL() + "/api/jobflowjson/" + jobId;
    }

    public String getComponentArchiveURL(String endPath){
        return getServerURL() + "/api/component/archive/" + endPath;
    }

    public String getServerAddress(){
        return StringUtils.isEmpty(serverAddress) ? NetUtil.getLocalAddress().getHostAddress() : serverAddress;
    }

    public String getServerURL() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String url = request.getScheme() +"://" + getServerAddress() + ":" +request.getServerPort() ;
        return StringUtils.isNotEmpty(request.getContextPath()) ? url + "/" + request.getContextPath() : url;
    }
}
