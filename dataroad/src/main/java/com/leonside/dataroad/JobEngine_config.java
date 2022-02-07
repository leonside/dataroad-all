package com.leonside.dataroad;

import com.google.gson.Gson;
import com.leonside.dataroad.config.JobCreator;
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


        JobCreator jsonJobCreator = new JsonJobCreator(new JsonJobSchemaParser());
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_splitpk_lookuplru_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_aggtumbling_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_decider_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_sqltrans_lookup_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_filter_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_incrpolling_writer_restore_fromcheckpoint.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath("classpath:/mysql_splitpk_filter_mysql.json");

        String homePath = "filesystem:/C:\\Users\\Administrator\\.dataroad-dashboard\\schema\\";
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_splitpk_filter_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_incrpolling_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_incrpolling_mysql_checkpoint.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_customsql_decider_parallel_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_customsql_decider_exclusive_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_customsql_decider_union_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_sqltrans_es.json");

//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_scripttrans_lookup_direct_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_lookup_lru_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_lookup_none_mysql.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysqlstream_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysqlstream_mysqlstreamwriter_chekcpoint.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_aggcount_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_aggsliding_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "mysql_aggtumbling_es.json");
//        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "es_filter_es.json");
        List<Job> jobs = jsonJobCreator.createJobByPath(homePath + "oracle_incrpoll_filter_oracle.json");



        jobs.forEach(job ->{

            try {
                job.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}
