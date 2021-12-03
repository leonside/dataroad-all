package com.leonside.dataroad.config;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.common.spi.Component;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.config.domain.GenericComponentConfig;
import com.leonside.dataroad.core.component.JobExtensionLoader;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.spi.JobPredicate;

/**
 * @author leon
 */
public class ComponentFactory {

//    public static ItemReader getItemReader( GenericComponentConfig componentConfig){
//        return JobExtensionLoader.getSingleComponent(ComponentType.reader);
//    }
//    public static ItemWriter getItemWriter( GenericComponentConfig componentConfig){
//        ItemWriter component = JobExtensionLoader.getComponent(ComponentType.writer, componentConfig.getPluginName());
//        initComponent(component, componentConfig);
//        return component;
//    }
//    public static ItemProcessor getItemProcessor( GenericComponentConfig componentConfig){
//        ItemProcessor component = JobExtensionLoader.getComponent(ComponentType.processor, componentConfig.getPluginName());
//        initComponent(component, componentConfig);
//        component.setName(componentConfig.getName());
//        return component;
//    }
//    private static Component getItemAggProcessor(GenericComponentConfig componentConfig) {
//        ItemProcessor component = JobExtensionLoader.getComponent(ComponentType.agg, componentConfig.getPluginName());
//        initComponent(component, componentConfig);
//        component.setName(componentConfig.getName());
//        return component;
//    }

    public static <T extends Component>  T getComponent(ExecuteContext executeContext, GenericComponentConfig componentConfig){

        Component component = null;
        switch (componentConfig.getType()){
            case reader:
                component = getComponent(executeContext,ComponentType.reader, componentConfig);
                break;
            case writer:
                component =  getComponent(executeContext,ComponentType.writer, componentConfig);
                break;
            case processor:
                component =  getComponent(executeContext,ComponentType.processor, componentConfig);
                break;
            case agg:
                component = getComponent(executeContext,ComponentType.agg, componentConfig);
                break;
            default:
                break;
        }

        return (T) component;
    }

    private static Component getComponent(ExecuteContext executeContext, ComponentType componentType, GenericComponentConfig componentConfig) {
        Component component = JobExtensionLoader.getComponent(componentType, componentConfig.getPluginName());
        if(component == null){
            throw new JobConfigException("Unknown Component, please check whether the configuration is valid. ComponentType ["+ componentType +"], Component PluginName name [" + componentConfig.getPluginName() + "]");
        }

        initComponent(component, executeContext, componentConfig);
        component.setName(componentConfig.getName());
        return component;
    }

    public static JobPredicate getJobPredicate(ExecuteContext executeContext,GenericComponentConfig componentConfig){

        if(componentConfig.getType() != ComponentType.deciderOn ){
            throw new JobConfigException("JobPredicate Component type must be deciderOn,Check whether the configuration is correct. ");
        }

        JobPredicate jobPredicate = (JobPredicate) JobExtensionLoader.getExtensionLoader(ComponentType.deciderOn.getSpi(), componentConfig.getPluginName());

        initComponent(jobPredicate, executeContext, componentConfig);

        return jobPredicate;
    }

    private static <T extends ExecuteContext> void initComponent(Object component, T executeContext,GenericComponentConfig componentConfig) {
        if(component instanceof ComponentInitialization){
            ((ComponentInitialization<ExecuteContext>) component).initialize(executeContext,componentConfig.getParameter());
        }
    }
}
