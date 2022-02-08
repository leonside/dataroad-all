package com.leonside.dataroad.common.script.javascript;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;

import javax.script.*;
import java.util.Map;

/**
 * @author leon
 */
public class JavaScriptEvaluator implements ScriptEvaluator {

    private String scriptSource;
    private volatile ScriptEngine scriptEngine ;

    public JavaScriptEvaluator(String scriptSource){
        this.scriptSource = scriptSource;
    }

    @Override
    public Object evaluate(Object record, Map<String, Object> param) throws ScriptExecuteException {

        if(scriptEngine == null){
            synchronized(JavaScriptEvaluator.class){
                if(scriptEngine == null){
                    scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
                }
            }
        }

        SimpleBindings simpleBindings = new SimpleBindings();
        simpleBindings.put(JobCommonConstant.SCRIPT_CONTEXT_KEY_ROW, record);
        try {
            return scriptEngine.eval(scriptSource, simpleBindings);
        } catch (ScriptException e) {
            throw new ScriptExecuteException("javascript execute exception",e);
        }
    }


}
