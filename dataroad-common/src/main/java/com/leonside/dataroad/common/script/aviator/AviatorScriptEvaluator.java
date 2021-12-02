//package com.leonside.dataroad.common.script.aviator;
//
//import com.googlecode.aviator.AviatorEvaluator;
//import com.googlecode.aviator.Expression;
//import com.leonside.dataroad.common.context.CommonConstant;
//import com.leonside.dataroad.common.script.ScriptEvaluator;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author leon
// */
//public class AviatorScriptEvaluator implements ScriptEvaluator {
//
//    private String scriptSource;
//
//    public AviatorScriptEvaluator(String scriptSource){
//        this.scriptSource = scriptSource;
//    }
//
//    @Override
//    public Object evaluate(Object row, Map<String, Object> params) throws Exception {
//
//        Map<String,Object> ctx = (params == null) ? new HashMap<>() :params ;
//
//        ctx.put(CommonConstant.SCRIPT_CONTEXT_KEY_ROW, row);
//
//        Expression compiledExp = AviatorEvaluator.compile(scriptSource);
//
//        //获取引擎上下文
//        return compiledExp.execute(ctx);
//    }
//}
