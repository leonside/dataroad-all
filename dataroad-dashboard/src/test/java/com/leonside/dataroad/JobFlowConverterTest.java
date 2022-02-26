package com.leonside.dataroad;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.JobFlowDesigner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author leon
 */
public class JobFlowConverterTest {

    public static String json1 = "{ \"class\": \"go.GraphLinksModel\",\n" +
            "  \"modelData\": {\"position\":\"-5 -5\"},\n" +
            "  \"nodeDataArray\": [ \n" +
            "{\"key\":\"1fc52903-c664-444b-a063-f880fd94a23d\", \"text\":\"reader\", \"figure\":\"Circle\", \"fill\":\"#4fba4f\", \"nodeType\":\"step\", \"pluginType\":\"reader\", \"size\":\"100 100\", \"loc\":\"260 150\", \"parameter\":{\"stepName\":\"reader\", \"type\":\"reader\", \"pluginName\":\"mysqlReader\", \"jdbcUrl\":\"a\", \"splitKey\":\"\", \"username\":\"a\", \"password\":\"a\", \"where\":\"\", \"fetchSize\":\"0\", \"queryTimeOut\":\"0\", \"requestAccumulatorInterval\":\"\", \"increColumn\":\"\", \"startLocation\":\"\", \"customSql\":\"\", \"orderByColumn\":\"\", \"useMaxFunc\":\"\", \"polling\":\"false\", \"pollingInterval\":\"\", \"table\":\"\", \"column\":\"\", \"id\":\"mysqlReader-1fc52903-c664-444b-a063-f880fd94a23d\"}},\n" +
            "{\"key\":\"6e87027d-1d92-42c6-8338-d0003b75fe33\", \"text\":\"t1\", \"remark\":\"SQL转换\", \"nodeType\":\"step\", \"pluginType\":\"processor\", \"pluginName\":\"sqlTransformer\", \"loc\":\"420 70\", \"parameter\":{\"stepName\":\"t1\", \"type\":\"processor\", \"pluginName\":\"sqlTransformer\", \"sql\":\"1\", \"tableName\":\"1\", \"id\":\"sqlTransformer-6e87027d-1d92-42c6-8338-d0003b75fe33\"}},\n" +
            "{\"key\":\"6e87027d-1d92-42c6-8338-d0003b75fe332\", \"text\":\"t2\", \"remark\":\"SQL转换\", \"nodeType\":\"step\", \"pluginType\":\"processor\", \"pluginName\":\"sqlTransformer\", \"loc\":\"440 180\", \"parameter\":{\"stepName\":\"t2\", \"type\":\"processor\", \"pluginName\":\"sqlTransformer\", \"sql\":\"2\", \"tableName\":\"2\", \"id\":\"sqlTransformer-6e87027d-1d92-42c6-8338-d0003b75fe332\"}},\n" +
            "{\"key\":\"5861f8dc-e08f-4649-b536-5b395238d93f\", \"text\":\"w2\", \"figure\":\"Circle\", \"fill\":\"#CE0620\", \"nodeType\":\"step\", \"pluginType\":\"writer\", \"size\":\"100 100\", \"loc\":\"600 140\", \"parameter\":{\"stepName\":\"w2\", \"type\":\"writer\", \"pluginName\":\"mysqlWriter\", \"jdbcUrl\":\"a\", \"table\":\"a\", \"writeMode\":\"\", \"username\":\"a\", \"password\":\"a\", \"preSql\":\"\", \"postSql\":\"\", \"batchSize\":\"1024\", \"column\":\"\", \"fullColumn\":\"\", \"updateKey\":\"\", \"insertSqlMode\":\"\", \"id\":\"mysqlWriter-5861f8dc-e08f-4649-b536-5b395238d93f\"}},\n" +
            "{\"key\":\"6e87027d-1d92-42c6-8338-d0003b75fe333\", \"text\":\"t3\", \"remark\":\"SQL转换\", \"nodeType\":\"step\", \"pluginType\":\"processor\", \"pluginName\":\"sqlTransformer\", \"loc\":\"440 250\", \"parameter\":{\"stepName\":\"t3\", \"type\":\"processor\", \"pluginName\":\"sqlTransformer\", \"sql\":\"3\", \"tableName\":\"3\", \"id\":\"sqlTransformer-6e87027d-1d92-42c6-8338-d0003b75fe333\"}},\n" +
            "{\"key\":\"5861f8dc-e08f-4649-b536-5b395238d93f2\", \"text\":\"esw2\", \"figure\":\"Circle\", \"fill\":\"#CE0620\", \"nodeType\":\"step\", \"pluginType\":\"writer\", \"size\":\"100 100\", \"loc\":\"590 270\", \"parameter\":{\"stepName\":\"esw2\", \"type\":\"writer\", \"pluginName\":\"esWriter\", \"id\":\"esWriter-5861f8dc-e08f-4649-b536-5b395238d93f2\"}}\n" +
            " ],\n" +
            "  \"linkDataArray\": [ \n" +
            "{\"from\":\"1fc52903-c664-444b-a063-f880fd94a23d\", \"to\":\"6e87027d-1d92-42c6-8338-d0003b75fe33\"},\n" +
            "{\"from\":\"1fc52903-c664-444b-a063-f880fd94a23d\", \"to\":\"6e87027d-1d92-42c6-8338-d0003b75fe332\"},\n" +
            "{\"from\":\"6e87027d-1d92-42c6-8338-d0003b75fe33\", \"to\":\"5861f8dc-e08f-4649-b536-5b395238d93f\"},\n" +
            "{\"from\":\"6e87027d-1d92-42c6-8338-d0003b75fe332\", \"to\":\"5861f8dc-e08f-4649-b536-5b395238d93f\"},\n" +
            "{\"from\":\"1fc52903-c664-444b-a063-f880fd94a23d\", \"to\":\"6e87027d-1d92-42c6-8338-d0003b75fe333\"},\n" +
            "{\"from\":\"6e87027d-1d92-42c6-8338-d0003b75fe333\", \"to\":\"5861f8dc-e08f-4649-b536-5b395238d93f2\"}\n" +
            " ]}";

    public static String json2 = "{ \"class\": \"go.GraphLinksModel\",\n" +
            "  \"modelData\": {\"position\":\"-5 -5\"},\n" +
            "  \"nodeDataArray\": [ \n" +
            "{\"key\":\"6a0224c7-c920-4a6f-8c85-f3c485b6a729\", \"text\":\"a2\", \"remark\":\"SQL转换\", \"nodeType\":\"step\", \"pluginType\":\"processor\", \"pluginName\":\"sqlTransformer\", \"loc\":\"300 120\", \"parameter\":{\"stepName\":\"a2\", \"type\":\"processor\", \"pluginName\":\"sqlTransformer\", \"sql\":\"a2\", \"tableName\":\"a2\", \"id\":\"sqlTransformer-6a0224c7-c920-4a6f-8c85-f3c485b6a729\"}},\n" +
            "{\"key\":\"f7ce390d-b1cd-418d-bce6-c9c810d6b1f9\", \"text\":\"b1\", \"remark\":\"SQL转换\", \"nodeType\":\"step\", \"pluginType\":\"processor\", \"pluginName\":\"sqlTransformer\", \"loc\":\"440 130\", \"parameter\":{\"stepName\":\"b1\", \"type\":\"processor\", \"pluginName\":\"sqlTransformer\", \"sql\":\"b1\", \"tableName\":\"b1\", \"id\":\"sqlTransformer-f7ce390d-b1cd-418d-bce6-c9c810d6b1f9\"}},\n" +
            "{\"key\":\"b99287d5-b1a7-4ae8-a84b-96cd1166f94e\", \"text\":\"reader\", \"figure\":\"Circle\", \"fill\":\"#4fba4f\", \"nodeType\":\"step\", \"pluginType\":\"reader\", \"size\":\"100 100\", \"loc\":\"160 130\", \"parameter\":{\"stepName\":\"reader\", \"type\":\"reader\", \"pluginName\":\"mysqlReader\", \"jdbcUrl\":\"a\", \"splitKey\":\"\", \"username\":\"a\", \"password\":\"a\", \"where\":\"\", \"fetchSize\":\"0\", \"queryTimeOut\":\"0\", \"requestAccumulatorInterval\":\"\", \"increColumn\":\"\", \"startLocation\":\"\", \"customSql\":\"\", \"orderByColumn\":\"\", \"useMaxFunc\":\"\", \"polling\":\"false\", \"pollingInterval\":\"\", \"table\":\"\", \"column\":\"\", \"id\":\"mysqlReader-b99287d5-b1a7-4ae8-a84b-96cd1166f94e\"}},\n" +
            "{\"key\":\"c785b314-3f85-4275-bf84-4f71cd4facbc\", \"text\":\"mysqlw\", \"figure\":\"Circle\", \"fill\":\"#CE0620\", \"nodeType\":\"step\", \"pluginType\":\"writer\", \"size\":\"100 100\", \"loc\":\"570 130\", \"parameter\":{\"stepName\":\"mysqlw\", \"type\":\"writer\", \"pluginName\":\"mysqlWriter\", \"jdbcUrl\":\"a\", \"table\":\"a\", \"writeMode\":\"\", \"username\":\"a\", \"password\":\"a\", \"preSql\":\"\", \"postSql\":\"\", \"batchSize\":\"1024\", \"column\":\"\", \"fullColumn\":\"\", \"updateKey\":\"\", \"insertSqlMode\":\"\", \"id\":\"mysqlWriter-c785b314-3f85-4275-bf84-4f71cd4facbc\"}}\n" +
            " ],\n" +
            "  \"linkDataArray\": [ \n" +
            "{\"from\":\"6a0224c7-c920-4a6f-8c85-f3c485b6a729\", \"to\":\"f7ce390d-b1cd-418d-bce6-c9c810d6b1f9\", \"text\":\"步骤\", \"parameter\":{\"type\":\"deciderOn\", \"pluginName\":\"expressionPredicate\", \"linkName\":\"步骤\", \"languge\":\"aviator\", \"expression\":\"1=2\", \"id\":\"expressionPredicate-undefined\"}, \"pFill\":{\"class\":\"go.Brush\", \"type\":\"Radial\", \"start\":{\"class\":\"go.Spot\", \"x\":0.5, \"y\":0.5, \"offsetX\":0, \"offsetY\":0}, \"end\":{\"class\":\"go.Spot\", \"x\":0.5, \"y\":0.5, \"offsetX\":0, \"offsetY\":0}, \"colorStops\":{\"0\":\"rgb(240, 240, 240)\", \"1\":\"rgba(240, 240, 240, 0)\", \"0.3\":\"rgb(240, 240, 240)\"}}},\n" +
            "{\"from\":\"b99287d5-b1a7-4ae8-a84b-96cd1166f94e\", \"to\":\"6a0224c7-c920-4a6f-8c85-f3c485b6a729\"},\n" +
            "{\"from\":\"f7ce390d-b1cd-418d-bce6-c9c810d6b1f9\", \"to\":\"c785b314-3f85-4275-bf84-4f71cd4facbc,aadsasd\"}\n" +
            " ]}";

    public static void main(String[] args) throws IOException {



        JobFlowConverter jobFlowConverter = new JobFlowConverter(json1, "{}");

        String jobFlowJson = jobFlowConverter.convert();

        System.out.println(jobFlowJson);

        JSONObject jsonObject = JsonUtil.getInstance().readJson(json2, JSONObject.class);
        String json = JsonUtil.getInstance().prettyJson(jsonObject);

        FileUtils.write(new File("D:\\tmp\\test\\aa.json"), json, StandardCharsets.UTF_8);
    }
}
