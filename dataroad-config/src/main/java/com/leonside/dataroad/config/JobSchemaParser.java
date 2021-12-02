package com.leonside.dataroad.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.core.Job;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

public interface JobSchemaParser extends Serializable {

    JobConfigs parserJSON(String json) throws JsonProcessingException;

    JobConfigs parserJSONPath(String path) throws IOException, URISyntaxException;

}
