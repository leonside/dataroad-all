package com.leonside.dataroad.dashboard.listener;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ApplicationEnvPreparedInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String ENV_WEB_UI = "WEB_UI";
    public static final String ENV_DATAROAD_DIST = "DATAROAD_DIST";
    public static final String ENV_HOST_ADDRESS = "HOST_ADDRESS";
    public static final String ENV_SAMPLE_ENABLED = "SAMPLE_ENABLED";

    public ApplicationEnvPreparedInitializer() {
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        String webUI = applicationContext.getEnvironment().getProperty(ENV_WEB_UI);
        if(StringUtils.isNotEmpty(webUI)){
            applicationContext.getEnvironment().getSystemProperties().put("dataroad.web-ui", webUI);
        }

        String dataroadDist = applicationContext.getEnvironment().getProperty(ENV_DATAROAD_DIST);
        if(StringUtils.isNotEmpty(dataroadDist)){
            applicationContext.getEnvironment().getSystemProperties().put("dataroad.dataroad-dist", dataroadDist);
        }

        String serverAddress = applicationContext.getEnvironment().getProperty(ENV_HOST_ADDRESS);
        if(StringUtils.isNotEmpty(serverAddress)){
            applicationContext.getEnvironment().getSystemProperties().put("dataroad.server-address", serverAddress);
        }

        String sampleEnabled = applicationContext.getEnvironment().getProperty(ENV_SAMPLE_ENABLED);
        if(StringUtils.isNotEmpty(sampleEnabled)){
            applicationContext.getEnvironment().getSystemProperties().put("dataroad.sample-enabled", sampleEnabled);
        }

    }
}