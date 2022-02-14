package com.leonside.dataroad.flink;

import com.google.common.collect.Maps;
import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.context.ComponentHolder;
import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.JobExecutionListener;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.spi.JobEngineProvider;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.core.support.LoggerHelper;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.utils.PluginJarHelper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author leon
 */
public class FlinkJobProvider implements JobEngineProvider<FlinkExecuteContext> {
    @Override
    public Job createJob(FlinkExecuteContext executeContext, SimpleJobFlow startJobFlow) {

        initLoggerSetting(executeContext);

//        buildJobExecuteListener();

        return new FlinkJob(executeContext, startJobFlow);
    }

    @Override
    public FlinkExecuteContext createExecuteContext(JobSetting jobSetting, List<ComponentHolder> componentHolders, Options options) {

        ExecuteContext executeContext = ExecuteContext.of(jobSetting, componentHolders, options);

        StreamExecutionEnvironment environment = buildStreamExecutionEnvironment(executeContext);


        if(jobSetting.getRestore().isRestore()){
            //// TODO: 2021/12/3
            if(jobSetting.getRestore().getSavepointInterval() != null){
                environment.enableCheckpointing(jobSetting.getRestore().getSavepointInterval());
            }else {
                environment.enableCheckpointing();
            }
            environment.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            environment.setStateBackend(new FsStateBackend(jobSetting.getRestore().getSavepointPath()));
        }

        return buildFlinkExecuteContext(executeContext, environment, options);
    }

    private void buildGlobalSetting(FlinkExecuteContext executeContext, Options options) {

        //设置配置中的全局设置
        if(executeContext.getJobSetting().getSpeed().getChannel() > 0){
            executeContext.getEnvironment().setParallelism(executeContext.getJobSetting().getSpeed().getChannel());
        }

        //设置jobName
        if(StringUtils.isNotEmpty(options.getJobName())){
            executeContext.getJobSetting().setName(options.getJobName());
        }

    }

    private void initLoggerSetting(ExecuteContext executeContext) {
        LoggerHelper.init(executeContext.getJobSetting().getLog());
    }


    private FlinkExecuteContext buildFlinkExecuteContext(ExecuteContext executeContext, StreamExecutionEnvironment environment, Options options) {
        FlinkExecuteContext flinkExecuteContext = new FlinkExecuteContext();
        flinkExecuteContext.setEnvironment(environment);
        flinkExecuteContext.setOptions(executeContext.getOptions());
        flinkExecuteContext.setJobSetting(executeContext.getJobSetting());


        //设置检查点
        if(flinkExecuteContext.getEnvironment() instanceof LocalStreamEnvironment) {
            if(executeContext.getJobSetting().getRestore().isRestore() && StringUtils.isNotEmpty(executeContext.getJobSetting().getRestore().getSavepointRestorePath())){
                ((LocalStreamEnvironment) flinkExecuteContext.getEnvironment()).setSettings(SavepointRestoreSettings.forPath(executeContext.getJobSetting().getRestore().getSavepointRestorePath()));
            }
        }

        buildGlobalSetting(flinkExecuteContext, options);

        return flinkExecuteContext;
    }

    private StreamExecutionEnvironment buildStreamExecutionEnvironment(ExecuteContext executeContext) {
        Map<String, String> confProp = executeContext.getOptions().getConfProp();
        Configuration configuration = MapUtils.isNotEmpty(confProp) ? Configuration.fromMap(confProp) : new Configuration();

        StreamExecutionEnvironment environment =
                (!executeContext.getJobSetting().isLocal()) ?
                StreamExecutionEnvironment.getExecutionEnvironment(configuration):
                new LocalStreamEnvironment(configuration);

        try {
            PluginJarHelper.loadJar(environment, executeContext);
        } catch (Exception e) {
            throw new JobFlowException("load Jar exception",e );
        }
        return environment;
    }


}
