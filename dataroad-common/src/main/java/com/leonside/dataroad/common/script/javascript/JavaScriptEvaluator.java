package com.leonside.dataroad.common.script.javascript;

import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * @author leon
 */
public class JavaScriptEvaluator implements ScriptEvaluator {


    private String scriptSource;
    public JavaScriptEvaluator(String scriptSource){
        this.scriptSource = scriptSource;
    }

    @Override
    public Object evaluate(Object record, Map<String, Object> param) throws ScriptExecuteException {
        //todo
        return null;
    }


}
