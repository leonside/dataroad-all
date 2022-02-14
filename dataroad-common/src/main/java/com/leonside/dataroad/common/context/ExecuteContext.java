package com.leonside.dataroad.common.context;

import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.JobExecutionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2021/4/6.
 */
public class ExecuteContext implements Serializable {

	private JobSetting jobSetting = new JobSetting();

	private Options options ;

	private List<ComponentHolder> componentHolders = new ArrayList<>();

	private transient Map<String,Object> jobFlowOutputStore = new ConcurrentHashMap<>();

	public void putJobFlowOutput(String key, Object value){
		if(jobFlowOutputStore.containsKey(key)){
			throw new JobFlowException("Contains duplicate Jobflows Output[" + key + "]");
		}
		jobFlowOutputStore.put(key, value);
	}

	public Object getJobFlowOutput(String key){
		return jobFlowOutputStore.get(key);
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public Options getOptions() {
		return options;
	}

	public JobSetting getJobSetting() {
		return jobSetting;
	}

	public List<ComponentHolder> getComponentHolders() {
		return componentHolders;
	}

	public void setComponentHolders(List<ComponentHolder> componentHolders) {
		this.componentHolders = componentHolders;
	}

	public void setJobSetting(JobSetting jobSetting) {
		this.jobSetting = jobSetting;
	}

	public static ExecuteContext of(JobSetting jobSetting,List<ComponentHolder> componentHolders, Options options){
		ExecuteContext executeContext = new ExecuteContext();
		executeContext.setJobSetting(jobSetting);
		executeContext.setOptions(options);
		executeContext.setComponentHolders(componentHolders);
		return executeContext;
	}

}
