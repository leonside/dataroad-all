/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.leonside.dataroad.common.context;

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
public class
ExecuteContext implements Serializable {

	private JobSetting jobSetting = new JobSetting();

	private List<JobExecutionListener> jobExecutionListeners = new ArrayList<>();

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

	public JobSetting getJobSetting() {
		return jobSetting;
	}

	public void setJobSetting(JobSetting jobSetting) {
		this.jobSetting = jobSetting;
	}

	public List<JobExecutionListener> getJobExecutionListeners() {
		return jobExecutionListeners;
	}

	public void setJobExecutionListeners(List<JobExecutionListener> jobExecutionListeners) {
		this.jobExecutionListeners = jobExecutionListeners;
	}

	public static ExecuteContext of(JobSetting jobSetting, List<JobExecutionListener> jobExecutionListeners){
		ExecuteContext executeContext = new ExecuteContext();
		executeContext.setJobSetting(jobSetting);
		executeContext.setJobExecutionListeners(jobExecutionListeners);
		return executeContext;
	}

}
