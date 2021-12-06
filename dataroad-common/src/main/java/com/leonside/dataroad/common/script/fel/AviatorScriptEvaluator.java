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
public class AviatorScriptEvaluator implements ScriptEvaluator {

    private String scriptSource;

    public AviatorScriptEvaluator(String scriptSource, String... packages){
        this.scriptSource = scriptSource;
       ;
    }

    @Override
    public Object evaluate(Object row, Map<String, Object> params) throws Exception {

long l = System.currentTimeMillis();
        FelEngine felEngine = new FelEngineImpl();
        //获取引擎上下文
        FelContext felContext = felEngine.getContext();

        if(params != null){
            params.forEach((key,value)->{
                felContext.set(key, params);
            });
        }
        felContext.set(JobCommonConstant.SCRIPT_CONTEXT_KEY_ROW, row);

System.out.println("cost:"+( System.currentTimeMillis() - l));
        return felEngine.eval(scriptSource);
    }
}
