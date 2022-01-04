package com.leonside.dataroad.flink.processor.aggeration.function;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.io.Serializable;

/**
 * @author leon
 */
public interface WindowAggerationFunction<T>  extends Serializable {

    DataStream<T> processWindow(DataStream<Row> dataStream);

    DataStream<T> processKeyByWindow(DataStream<Row> dataStream);



}
