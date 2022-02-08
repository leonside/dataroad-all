package com.leonside.dataroad.common;

import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class GroovyScriptTest {

    public static void main(String[] args) throws Exception {
        String exp = "row.getField('score')>=500 ";

        Row row = Row.withNames();
        row.setField("name","zhangsan");
        row.setField("score", 599);

        long l = System.currentTimeMillis();

        ScriptEvaluator scriptEvalutor = ScriptEvaluatorFactory.createScriptEvalutor(ScriptEvaluatorFactory.ScriptEngine.groovy, exp, new String[]{"import org.apache.flink.types.Row;"});
        for (int i = 0; i < 10000; i++) {

            Object evaluate = scriptEvalutor.evaluate(row, null);
//            System.out.println(evaluate);
        }
//        1w耗时：1978  	耗时：N久
        System.out.println("耗时："+ (System.currentTimeMillis() - l));


    }
}
