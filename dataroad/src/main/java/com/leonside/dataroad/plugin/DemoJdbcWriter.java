package com.leonside.dataroad.plugin;

import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class DemoJdbcWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>> {
    @Override
    public String getName() {
        return "writer1";
    }

    @Override
    public void write(FlinkExecuteContext executeContext, DataStream<Row> dataSource) {

        DataStreamSink tDataStreamSink = dataSource.addSink(org.apache.flink.connector.jdbc.JdbcSink.sink(
                "insert into student1 (id,name,sex,age,address,idcard,phone,code,create_time,area_code ) values (?,?,?,?,?,?,?,?,?,?) ",
                (ps, t) -> {
                    ps.setObject(1, t.getField("id"));
                    ps.setObject(2, t.getField("name"));
                    ps.setObject(3, t.getField("sex"));
                    ps.setObject(4, t.getField("age"));
                    ps.setObject(5, t.getField("address"));
                    ps.setObject(6, t.getField("idcard"));
                    ps.setObject(7, t.getField("phone"));
                    ps.setObject(8, t.getField("code"));
                    ps.setObject(9, t.getField("create_time"));
                    ps.setObject(10, t.getField("area_code"));
                },
                JdbcExecutionOptions.defaults(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://10.254.10.31:3306/duceap_job_demo?useunicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai")
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("duceap")
                        .withPassword("123")
                        .build()));
    }
}
