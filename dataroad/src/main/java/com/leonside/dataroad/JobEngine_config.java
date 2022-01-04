package com.leonside.dataroad;

import com.google.gson.Gson;
import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.config.job.JsonJobCreator;
import com.leonside.dataroad.config.job.JsonJobSchemaParser;
import com.leonside.dataroad.core.Job;

import java.util.List;

/**
 * @author leon
 */
public class JobEngine_config {

    public static void main(String[] args) throws Exception {


        JsonJobCreator jsonJobCreator = new JsonJobCreator(new JsonJobSchemaParser());
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_splitpk_lookuplru_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_aggtumbling_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_decider_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_sqltrans_lookup_es.json");
        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_filter_mysql.json");


        jobs.forEach(job ->{

            try {
                job.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}
