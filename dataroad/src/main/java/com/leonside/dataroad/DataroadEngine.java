package com.leonside.dataroad;

import com.leonside.dataroad.config.JobCreator;
import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.config.job.JsonJobCreator;
import com.leonside.dataroad.config.job.JsonJobSchemaParser;
import com.leonside.dataroad.core.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
public class DataroadEngine {

    public static void main(String[] args) throws Exception {

        ParameterTool parameters = ParameterTool.fromArgs(args);

        Options options = Options.of(parameters.toMap());

        if(StringUtils.isEmpty(options.getConf())){
            throw new UnsupportedOperationException("schema path can not be null!");
        }

        log.info("Start job，schema path is [{}]...." , options.getConf() );

        JobCreator jsonJobCreator = new JsonJobCreator(new JsonJobSchemaParser(), options);

        List<Job> jobs = jsonJobCreator.createJob();

        jobs.forEach(job ->{

            try {
                job.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}
