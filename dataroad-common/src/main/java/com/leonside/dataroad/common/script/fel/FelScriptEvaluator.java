package com.leonside.dataroad.common.script.fel;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.script.ScriptEvaluator;

import java.util.Map;

/**
 * @author leon
 */
public class FelScriptEvaluator implements ScriptEvaluator {

    private String scriptSource;

    private volatile FelEngine felEngine;

    public FelScriptEvaluator(String scriptSource, String... packages){
        this.scriptSource = scriptSource;
       ;
    }

    @Override
    public Object evaluate(Object row, Map<String, Object> params) throws Exception {


        if(felEngine == null){
            synchronized(FelScriptEvaluator.class){
                if(felEngine == null){
                    felEngine = new FelEngineImpl();
                }
            }
        }
        //获取引擎上下文
        FelContext felContext = felEngine.getContext();

        if(params != null){
            params.forEach((key,value)->{
                felContext.set(key, params);
            });
        }
        felContext.set(JobCommonConstant.SCRIPT_CONTEXT_KEY_ROW, row);


        Object eval = felEngine.eval(scriptSource);
        return eval;
    }
}
