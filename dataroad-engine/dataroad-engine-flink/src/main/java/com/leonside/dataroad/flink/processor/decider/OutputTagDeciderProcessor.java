package com.leonside.dataroad.flink.processor.decider;


import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.constant.JobCoreConstants;
import com.leonside.dataroad.core.flow.JobFlow;
import com.leonside.dataroad.core.spi.ItemDeciderProcessor;
import com.leonside.dataroad.core.spi.JobPredicate;
import com.leonside.dataroad.core.predicate.OtherwisePredicate;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leon
 */
//SPI

public class OutputTagDeciderProcessor extends ComponentNameAutoGenerator implements ItemDeciderProcessor<FlinkExecuteContext, DataStream<Row>,Map<JobPredicate, DataStream<Row>>> {

    private Map<JobPredicate, JobFlow> jobFlowDeciders;

    public OutputTagDeciderProcessor(){
        super(JobCoreConstants.JOBFLOW_NAME_PREFIX_OUTPUTTAG_DECIDER);
    }

    @Override
    public void initialize(Map<JobPredicate, JobFlow> predicateJobFlowMap) {
        Asserts.notNull(predicateJobFlowMap, "job flow deciders can not be null, Check whether the configuration is correct");
        this.jobFlowDeciders = predicateJobFlowMap;
    }

    private JobPredicate obtainOtherwiseStatusPredicate(Map<JobPredicate, JobFlow> jobFlowDeciders) {
        List<JobPredicate> collect = jobFlowDeciders.keySet().stream().filter(it -> it instanceof OtherwisePredicate).collect(Collectors.toList());
        return CollectionUtils.isEmpty(collect) ? null :collect.get(0);
    }

    @Override
    public Map<JobPredicate, JobFlow> getJobFlowDeciders() {
        return jobFlowDeciders;
    }


    @Override
    public Map<JobPredicate, DataStream<Row>> process(FlinkExecuteContext executeContext, DataStream<Row> rowDataStream) {

        JobPredicate otherwiseStatusPredicate = obtainOtherwiseStatusPredicate(jobFlowDeciders);
        //创建OutputTag Map
        Map<JobPredicate, OutputTag<Row>> predicateOutputTagMap = createDeciderOutputTagMap();

        //匹配JobPredicate, 其中如果全部不匹配则判断是否存在Otherwise分支，有则放入此分支
        SingleOutputStreamOperator<Row> singleOutputStream = rowDataStream.process(new ProcessFunction<Row, Row>() {
            @Override
            public void processElement(Row row, Context context, Collector<Row> collector) throws Exception {

                boolean isOneMatch = false;
                Iterator<JobPredicate> iterator = jobFlowDeciders.keySet().iterator();
                while (iterator.hasNext()){
                     JobPredicate predicate = iterator.next();
                    if(!(predicate instanceof OtherwisePredicate) && predicate.test(executeContext, row)){
                        context.output(predicateOutputTagMap.get(predicate), row);
                        isOneMatch = true;
                    }
                }

                if(!isOneMatch && otherwiseStatusPredicate != null){
                    context.output(predicateOutputTagMap.get(otherwiseStatusPredicate), row);
                }
            }
        });

        Map<JobPredicate, DataStream<Row>> sideOutputDataStreamMap = deciderOutputStream(singleOutputStream, predicateOutputTagMap);

        return sideOutputDataStreamMap;
    }

    private Map<JobPredicate, DataStream<Row>> deciderOutputStream(SingleOutputStreamOperator<Row> process, Map<JobPredicate, OutputTag<Row>> predicateOutputTagMap) {
        Map<JobPredicate, DataStream<Row>> sideOutputDataStreamMap = new HashMap<>();
        jobFlowDeciders.entrySet().forEach(it->{
            DataStream<Row> sideOutputDataStream = process.getSideOutput(new OutputTag<Row>(String.valueOf(it.getKey().hashCode())) {
            });
            sideOutputDataStreamMap.put(it.getKey(), sideOutputDataStream);
        });
        return sideOutputDataStreamMap;
    }



    private Map<JobPredicate, OutputTag<Row>> createDeciderOutputTagMap() {
        Map<JobPredicate, OutputTag<Row>> predicateOutputTagMap = new HashMap<>();

        jobFlowDeciders.keySet().forEach(it->{
            predicateOutputTagMap.put(it, new OutputTag<Row>(String.valueOf(it.hashCode())){});
        });
        return predicateOutputTagMap;
    }
}
