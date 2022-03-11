package com.leonside.dataroad.dashboard.domian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.DataroadEngine;
import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.config.job.JsonJobSchemaParser;
import com.leonside.dataroad.dashboard.configuration.DataroadProperties;
import com.leonside.dataroad.flink.utils.PluginJarHelper;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Data
public class JobRequestParam {

    private String jobId;
    private Integer parallelism;
    private String allowNonRestoredState;
    private String savepointPath;
    private String confProp;

}
