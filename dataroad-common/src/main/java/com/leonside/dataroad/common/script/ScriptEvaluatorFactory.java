package com.leonside.dataroad.common.script;

import com.leonside.dataroad.common.script.bsh.BshScriptEvaluator;
import com.leonside.dataroad.common.script.fel.FelScriptEvaluator;
import com.leonside.dataroad.common.script.groovy.GroovyScriptEvaluator;
import com.leonside.dataroad.common.script.javascript.JavaScriptEvaluator;
import com.leonside.dataroad.common.utils.Asserts;

/**
 * @author leon
 */
public class ScriptEvaluatorFactory {

    public static ScriptEvaluator createScriptEvalutor(ScriptEngine scriptEngine, String scriptSource, String... packages){

        Asserts.notNull(scriptSource, "scriptSource can not be null");

        ScriptEvaluator scriptEvaluator = null;
        switch (scriptEngine){
            case bsh:
                scriptEvaluator = new BshScriptEvaluator(scriptSource);
                break;
            case groovy:
                scriptEvaluator = new GroovyScriptEvaluator(scriptSource,packages);
                break;
            case javascript:
                scriptEvaluator = new JavaScriptEvaluator(scriptSource);
                break;
            case fel:
                scriptEvaluator = new FelScriptEvaluator(scriptSource);
                break;
            default:
                throw new UnsupportedOperationException("unsupport script engine ["+ scriptEngine +"]");
        }
        return scriptEvaluator;
    }

    public enum ScriptEngine{
        bsh,
        javascript,
        groovy,
        fel;


    }
}
