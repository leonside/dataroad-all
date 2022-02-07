package com.leonside.dataroad.dashboard.converter;

import com.google.common.collect.Lists;
import com.leonside.dataroad.config.domain.GenericComponentConfig;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.dashboard.domian.JobFlowDesigner;
import com.leonside.dataroad.dashboard.domian.JobFlowSchema;
import com.leonside.dataroad.dashboard.domian.JobFlowSchemas;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class JobFlowDesignerWrapper {

    private JobFlowDesigner jobFlowDesigner;

    private Map<String, JobFlowDesigner.NodeData> nodeDataMap = new LinkedHashMap<>();

    private ConcurrentHashMap<String, List<JobFlowDesigner.LinkData>> linkDataFromMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, List<JobFlowDesigner.LinkData>> linkDataToMap = new ConcurrentHashMap<>();

    private List<String> orderedKeys = new ArrayList<>();

    public JobFlowDesignerWrapper(JobFlowDesigner jobFlowDesigner) {
        this.jobFlowDesigner = jobFlowDesigner;

        initComponentRelation();
    }

    private JobFlowDesigner.NodeData insertNodeData(JobFlowDesigner.LinkData linkData, JobFlowDesigner.NodeData nodeData){

        if(!jobFlowDesigner.getNodeDataArray().contains(nodeData)){
            int insertAfterNode = jobFlowDesigner.getNodeDataArray().indexOf(nodeDataMap.get(linkData.getFrom()));
            jobFlowDesigner.getNodeDataArray().add(insertAfterNode, nodeData);
        }

        jobFlowDesigner.getLinkDataArray().remove(linkData);

        jobFlowDesigner.getLinkDataArray().add(new JobFlowDesigner.LinkData(linkData.getFrom(),nodeData.getKey()));

        jobFlowDesigner.getLinkDataArray().add(new JobFlowDesigner.LinkData(nodeData.getKey(),linkData.getTo()));

        return nodeData;
    }

    public JobFlowSchemas toJobConfigs(){

        //判断是否存在分支，存在则插入Decider节点
        insertDeciderNode();

        //判断是否存在合并，存在则插入union节点
        insertUnionNode();

        //重新计算节点关系
        initComponentRelation();

        //排序NodeData
        orderNodeData();

        //创建Job Content
        Map<String, GenericComponentConfig> componentConfigMap = createJobContent();

        JobFlowSchema jobConfig = new JobFlowSchema();
        jobConfig.setSetting(jobFlowDesigner.getGlobalSettting());
        jobConfig.setContent(Lists.newArrayList(componentConfigMap));

        return JobFlowSchemas.of(jobConfig);
    }

    private Map<String, GenericComponentConfig> createJobContent() {
        Map<String, GenericComponentConfig> componentConfigMap = new LinkedHashMap<>();
        jobFlowDesigner.getNodeDataArray().sort((o1, o2) -> o1.getOrder() > o2.getOrder() ? 1 : -1);

        jobFlowDesigner.getNodeDataArray().stream().forEach(nodeData -> {
            GenericComponentConfig genericComponentConfig = createGenericComponentConfig(nodeData);
            componentConfigMap.put(genericComponentConfig.getName(), genericComponentConfig);
        });
        return componentConfigMap;
    }

    private void orderNodeData() {
        //查找根节点
        Optional<String> rootNode = nodeDataMap
                .keySet()
                .stream()
                .filter(key -> !linkDataToMap.containsKey(key)).findFirst();
        String rootKey = rootNode.get();

        AtomicInteger idx = new AtomicInteger();

        doOrderNodeData(rootKey, idx);
    }

    private void doOrderNodeData(String key, AtomicInteger idx) {
        JobFlowDesigner.NodeData nodeData = nodeDataMap.get(key);
        nodeData.setOrder(idx.getAndIncrement());
        idx.incrementAndGet();
        List<JobFlowDesigner.LinkData> linkDataList = linkDataFromMap.get(nodeData.getKey());
        if(CollectionUtils.isNotEmpty(linkDataList)){
            linkDataList.stream().forEach(linkData -> {
                doOrderNodeData(linkData.getTo(), idx);
            });
        }
    }

    private void insertUnionNode() {
        linkDataToMap.entrySet().forEach(entry->{
            List<JobFlowDesigner.LinkData> linkDataList = entry.getValue();
            if(linkDataList.size() > 1){
                JobFlowDesigner.NodeData nodeData = JobFlowDesigner.defaultUnionNodeData(UUID.randomUUID().toString());
                AtomicInteger idx = new AtomicInteger();
                linkDataList.forEach(linkData -> {
                    insertNodeData(linkData, nodeData);
                    idx.incrementAndGet();
                });
            }
        });
    }

    private void insertDeciderNode() {
        linkDataFromMap.entrySet().forEach(entry->{
            List<JobFlowDesigner.LinkData> linkDataList = entry.getValue();
            if(linkDataList.size() > 1){
                AtomicInteger idx = new AtomicInteger();
                linkDataList.forEach(linkData -> {
                    String key =  linkData.getFrom() + "-" + linkData.getTo() + "-" + idx;
                    JobFlowDesigner.NodeData nodeData = MapUtils.isEmpty(linkData.getParameter()) ?
                            JobFlowDesigner.defaultExpressionNodeData(key):
                            new JobFlowDesigner.NodeData(key, linkData.getParameter());
                    insertNodeData(linkData, nodeData);
                    idx.incrementAndGet();
                });
            }
        });
    }

    private GenericComponentConfig createGenericComponentConfig(JobFlowDesigner.NodeData nodeData) {

        GenericComponentConfig config = new GenericComponentConfig();
        String id = (String)nodeData.getParameter().get(JobFlowDesigner.PARAMETER_ID);
        config.setName(id);
        config.setParameter(nodeData.getFilteredParameter());
        config.setPluginName((String)nodeData.getParameter().get(JobFlowDesigner.PARAMETER_PLUGINNAME));
        config.setType(ComponentType.valueOf((String)nodeData.getParameter().get(JobFlowDesigner.PARAMETER_TYPE)));

        List<JobFlowDesigner.LinkData> linkDataList = linkDataToMap.get(nodeData.getKey());
        if(CollectionUtils.isNotEmpty(linkDataList)){
            String[] dependencies = linkDataList.stream().map(linkData -> {
                return nodeDataMap.get(linkData.getFrom()).getId();
            }).toArray(String[]::new);
            config.setDependencies(dependencies);
        }

        return config;
    }


    private void initComponentRelation() {
        nodeDataMap.clear();
        linkDataFromMap.clear();
        linkDataToMap.clear();

        jobFlowDesigner.getNodeDataArray().stream().forEach(nodeData -> {
            nodeDataMap.put(nodeData.getKey(), nodeData);
        });

        jobFlowDesigner.getLinkDataArray().stream().forEach(linkData -> {
            List<JobFlowDesigner.LinkData> linkDataList = linkDataFromMap.computeIfAbsent(linkData.getFrom(), key -> new ArrayList<>());
            linkDataList.add(linkData);
        });

        jobFlowDesigner.getLinkDataArray().stream().forEach(linkData -> {
            List<JobFlowDesigner.LinkData> linkDataList = linkDataToMap.computeIfAbsent(linkData.getTo(), key -> new ArrayList<>());
            linkDataList.add(linkData);
        });
    }

}
