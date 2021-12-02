package com.leonside.dataroad.common.script.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import com.leonside.dataroad.common.context.CommonConstant;
import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

/**
 * @author leon
 */
public class BshScriptEvaluator implements ScriptEvaluator {

    private String scriptSource;

    public BshScriptEvaluator(String scriptSource){
        this.scriptSource = scriptSource;
    }


    @Override
    public Object evaluate(Object record, Map<String, Object> param) throws Exception {

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(BshScriptEvaluator.class.getClassLoader());
            if (param != null) {
                Iterator var4 = param.entrySet().iterator();

                while(var4.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var4.next();
                    interpreter.set((String)entry.getKey(), entry.getValue());
                }
            }

            interpreter.set(CommonConstant.SCRIPT_CONTEXT_KEY_ROW, record);

            return interpreter.eval(scriptSource);
        } catch (Exception var7) {
            throw new ScriptExecuteException("script execute failed", var7);
        }

    }
}
