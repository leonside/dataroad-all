package com.leonside.dataroad.flink.processor.union;

import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
import com.leonside.dataroad.common.spi.ItemUnionProcessor;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
public class GenericItemUnionProcessor extends ComponentNameAutoGenerator implements ItemUnionProcessor<FlinkExecuteContext, List<DataStream<Row>>, DataStream<Row>> {

    private int[] flowindexs;

    public GenericItemUnionProcessor(){
        super(JobConfigKeyConstants.COMPONENT_PREFIX_UNION_PROCESSOR);
    }

    @Override
    public void initializeUnionFlowIndex(int[] indexs) {
        this.flowindexs = flowindexs;
    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, List<DataStream<Row>> dataStreams) {

        List<DataStream<Row>> unionDataStreamList;
        if(flowindexs != null){
            unionDataStreamList = new ArrayList<>();
            Arrays.stream(flowindexs).forEach(it->{
                unionDataStreamList.add(dataStreams.get(it));
            });
        }else{
            unionDataStreamList = dataStreams;
        }

        Optional<DataStream<Row>> reduceStream = unionDataStreamList.stream()
                .reduce((dataStream, dataStream2) -> dataStream.union(dataStream2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

}
