package com.leonside.dataroad.flink.writer;

import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.core.component.ComponentNameable;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

/**
 * @author leon
 */

public class PrintItemWriter extends ComponentNameable implements ItemWriter<FlinkExecuteContext, DataStream<Row>> {

    @Override
    public void write(FlinkExecuteContext executeContext, DataStream<Row> items) {
        items.print();
    }
}
