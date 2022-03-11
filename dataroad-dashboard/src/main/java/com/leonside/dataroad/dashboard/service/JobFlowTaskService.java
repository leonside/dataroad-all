package com.leonside.dataroad.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.dashboard.domian.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author leon
 */
public interface JobFlowTaskService {

    JobSubmitReponse submitJobFlow(JobRequestParam jobRequestParam) throws JsonProcessingException, UnsupportedEncodingException;

    JobUploadReponse uploadDataroadJar() throws IOException;

    JobJarsReponse listDataroadJar();

    void checkDataroadJar();
}
