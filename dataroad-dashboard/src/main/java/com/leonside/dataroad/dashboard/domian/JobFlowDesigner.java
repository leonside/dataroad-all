package com.leonside.dataroad.dashboard.domian;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.dashboard.utils.ObjectUtils;
import com.leonside.dataroad.flink.predicate.TrueExpressionPredicate;
import com.leonside.dataroad.flink.processor.union.GenericItemUnionProcessor;
import lombok.Data;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * @author leon
 */
@Data
public class JobFlowDesigner {

    public static final String PARAMETER_ID = "id";
    public static final String PARAMETER_PLUGINNAME = "pluginName";
    public static final String PARAMETER_TYPE = "type";
    public static final String[] PARAMETER_EXTEND_KEY = new String[]{PARAMETER_ID,PARAMETER_PLUGINNAME,"stepName","linkName",PARAMETER_TYPE};

    private List<NodeData> nodeDataArray = new ArrayList<>();

    private List<LinkData> linkDataArray = new ArrayList<>();

    private Map globalSettting;


    public static NodeData defaultUnionNodeData(String key){
        NodeData nodeData = new NodeData();
        nodeData.setPluginName(GenericItemUnionProcessor.COMPONENT_ID);
        nodeData.setPluginType(ComponentType.union.name());
        nodeData.setKey(key);
        nodeData.getParameter().put(PARAMETER_ID, nodeData.generateId());
        nodeData.getParameter().put(PARAMETER_PLUGINNAME,nodeData.getPluginName());
        nodeData.getParameter().put(PARAMETER_TYPE, nodeData.getPluginType());
        return nodeData;
    }

    public static NodeData defaultExpressionNodeData(String key){
        NodeData nodeData = new NodeData();
        nodeData.setPluginName(TrueExpressionPredicate.COMPONENT_ID);
        nodeData.setPluginType(ComponentType.deciderOn.name());
        nodeData.setKey(key);
        nodeData.getParameter().put(PARAMETER_ID, nodeData.generateId());
        nodeData.getParameter().put(PARAMETER_PLUGINNAME,nodeData.getPluginName());
        nodeData.getParameter().put(PARAMETER_TYPE, nodeData.getPluginType());
        return nodeData;
    }

    @Data
    public static class NodeData implements ParameterData{
        private String key;
        private String text;
        private String nodeType;
        private String pluginType;
        private String pluginName;
        private Map<String,Object> parameter = new LinkedHashMap<>();
        private int order;

        public NodeData(){
        }
        public NodeData(String key, Map<String,Object> parameter ) {
            this.key = key;
            this.parameter = parameter;
        }

        public String getId(){
            return (String)parameter.get(PARAMETER_ID);
        }
        public String generateId(){
            return pluginName + "-" + key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeData nodeData = (NodeData) o;
            return Objects.equals(key, nodeData.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
    @Data
    public static class LinkData implements ParameterData{

        private String from;
        private String to;
        private Map<String,Object> parameter = new LinkedHashMap<>();

        public LinkData(){
        }
        public LinkData(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LinkData linkData = (LinkData) o;
            return Objects.equals(from, linkData.from) && Objects.equals(to, linkData.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    public interface ParameterData{
        Map<String,Object> getParameter();

        default Map<String,Object> getFilteredParameter(){
            Map<String, Object> parameter = getParameter();
            if(MapUtils.isEmpty(parameter)){
                return parameter;
            }

            Map<String,Object> filteredParameter = new LinkedHashMap<>();
            parameter.entrySet().stream().forEach(entry -> {
                if(!ArrayUtils.contains(PARAMETER_EXTEND_KEY, entry.getKey()) && (!ObjectUtils.isNullOrEmptyString(entry.getValue()))){
                    Object value = entry.getValue();
                    if(value instanceof String){
                        try{
                            //转换Component Parameter中的JSON字符串为JSON对象
                            Object parseObj = JSON.parse((String) value);
                            Object parseObjValue = (parseObj instanceof JSONObject || parseObj instanceof JSONArray) ? parseObj : value;
                            filteredParameter.put(entry.getKey(), parseObjValue);
                        }catch (Exception exception){
                            filteredParameter.put(entry.getKey(), value);
                        }
                    }else{
                        filteredParameter.put(entry.getKey(), value);
                    }
                }
            });
            return filteredParameter;
        }

    }

    public static void main(String[] args) {
        Object aaa = JSON.parse("aaa");
        System.out.println(aaa);
    }
}
