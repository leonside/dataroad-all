package com.leonside.dataroad.dashboard.domian;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leonside.dataroad.config.domain.GenericComponentConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobFlowSchema {

    private Map setting = new LinkedHashMap();

    public List<Map<String, GenericComponentConfig>> content;

}
