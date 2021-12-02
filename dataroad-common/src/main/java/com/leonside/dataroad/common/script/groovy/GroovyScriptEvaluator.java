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

    private ScriptEvaluator scriptEvaluator;

    public GroovyScriptEvaluator(String scriptSource, String... packages){
        this.scriptSource = scriptSource;
        scriptEvaluator = initGroovyScriptEvaluator(scriptSource, packages);
    }

    @Override
    public Object evaluate(Object record, Map<String, Object> params) throws Exception {
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
        sb.append("public class GroovyRULE extends ScriptEvaluator").append("{");
        sb.append("public Object evaluate(Object row,  Map<String, Object> params) throws ScriptExecuteException {");
        sb.append(expression);
        sb.append("}}");

        return sb.toString();
    }


}
