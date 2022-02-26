package com.leonside.dataroad.dashboard.listener;

import com.leonside.dataroad.dashboard.configuration.DataroadProperties;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
public class ApplicationSampleInitListener implements ApplicationRunner {
    @Autowired
    private DataroadProperties dataroadProperties;
    @Autowired
    private JobFlowService jobFlowService;

    @Override
    public void run(ApplicationArguments args)  {

        if(dataroadProperties.isSampleEnabled()){
            try {
                jobFlowService.initSampleConfig();
            } catch (Exception e) {
                log.error("Failed to initialize sample data",e);
            }
        }

    }
}
