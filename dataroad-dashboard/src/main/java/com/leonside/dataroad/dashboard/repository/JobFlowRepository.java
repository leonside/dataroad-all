package com.leonside.dataroad.dashboard.repository;

import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author leon
 */
@Repository
public interface JobFlowRepository {

    void save(JobFlowConfig jobFlowConfig) throws Exception;

    JobFlowConfig get(String id);

    List<JobFlowConfig> loadAll() throws Exception;

    boolean containJobFlow(String id);

    void delete(String id) throws Exception;

    void updateJobFlow(JobFlowConfig jobFlowConfig) throws Exception;

    void updateJobFlowBase(JobFlowConfig jobFlowConfig) throws Exception;

    void init(InputStream sampleConfig) throws Exception;
}
