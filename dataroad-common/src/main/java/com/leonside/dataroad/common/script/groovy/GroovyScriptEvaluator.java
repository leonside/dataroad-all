package com.leonside.dataroad.common.script.groovy;

import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class GroovyScriptEvaluator implements ScriptEvaluator {

    private String scriptSource;

    private volatile ScriptEvaluator scriptEvaluator;

    private String[] packages;

    public GroovyScriptEvaluator(String scriptSource, String... packages){
        this.scriptSource = scriptSource;
        this.packages = packages;

    }

    @Override
    public Object evaluate(Object record, Map<String, Object> params) throws Exception {

        if(scriptEvaluator == null){
            synchronized(GroovyScriptEvaluator.class){
                if(scriptEvaluator == null){
                    scriptEvaluator = initGroovyScriptEvaluator(scriptSource, packages);
                }
            }
        }

        return scriptEvaluator.evaluate(record, params);
    }

    private ScriptEvaluator initGroovyScriptEvaluator(String scriptSource, String... extraPackage) {

        GroovyClassLoader loader = new GroovyClassLoader(GroovyScriptEvaluator.class.getClassLoader());
        String groovyRule = getGroovyRule(scriptSource, extraPackage);

        Class groovyClass;
        try {
            groovyClass = loader.parseClass(groovyRule);
            return (ScriptEvaluator) groovyClass.newInstance();
        } catch (Throwable cfe) {
            throw new ScriptExecuteException("script compilation failed",cfe);
        }

    }

    private String getGroovyRule(String expression, String... packages) {
        expression = expression.endsWith(";") ? expression : expression + ";";
        StringBuffer sb = new StringBuffer();
        if(packages!=null) {
            for (String extraPackagesStr : packages) {
                if (StringUtils.isNotEmpty(extraPackagesStr)) {
                    sb.append(extraPackagesStr);
                }
            }
        }
        sb.append("import com.leonside.dataroad.common.script.ScriptEvaluator;");
        sb.append("import com.leonside.dataroad.common.exception.*;");
        sb.append("import java.util.*;");
        sb.append("public class GroovyRULE implements ScriptEvaluator").append("{");
        sb.append("public Object evaluate(Object row,  Map<String, Object> params) throws ScriptExecuteException {");
        sb.append(expression);
        sb.append("}}");

        return sb.toString();
    }


}
