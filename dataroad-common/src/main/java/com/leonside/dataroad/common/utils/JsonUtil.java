package com.leonside.dataroad.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class JsonUtil {
	private static volatile JsonUtil mJosnUtil  = null;
	private ObjectMapper mapper     = null;

	public static JsonUtil getInstance() {
		if(mJosnUtil == null){
			synchronized (JsonUtil.class) {
				if(mJosnUtil == null){
					mJosnUtil = new JsonUtil();
				}
			}
		}
		return mJosnUtil;
	}

	private JsonUtil(){
		mapper = new ObjectMapper();
//		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	public ObjectMapper getMapper(){
		return this.mapper;
	}
	public String writeJson(Object entity){
		String str = "";
		try {
			str = mapper.writeValueAsString(entity);
		} catch (Exception e) {
            log.error("JSON parsing exception",e);
		}
		return str;
	}
	
	public <T> T readJson(String jsonStr, Class<T> T){
		T obj = null;
		try {
			obj = mapper.readValue(jsonStr, T);
		} catch (Exception e) {
            throw new RuntimeException("JSON parsing exception",e);
		}
		return obj;
	}

	public <T> T readJson(File file, Class<T> T){
		T obj = null;
		try {
			obj = mapper.readValue(file, T);
		} catch (Exception e) {
			throw new RuntimeException("JSON parsing exception",e);
		}
		return obj;
	}



}