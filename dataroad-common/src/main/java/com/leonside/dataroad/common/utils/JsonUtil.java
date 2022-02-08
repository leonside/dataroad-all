package com.leonside.dataroad.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.JSONParser;
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
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
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

	public <T> T readValue(String content, TypeReference<T> valueTypeRef)  {
		T obj = null;
		try {
			obj = mapper.readValue(content, valueTypeRef);
		} catch (Exception e) {
			throw new RuntimeException("JSON parsing exception",e);
		}
		return obj;
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


	public String prettyJson(Object obj) {
		String str = "";
		try {
			str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			log.error("JSON parsing exception",e);
		}
		return str;
	}

	private String getSpaceOrTab(boolean isTab, int tabNum) {
		StringBuffer sbTab = new StringBuffer();
		for (int i = 0; i < tabNum; i++) {
			if (isTab) {
				sbTab.append('\t');
			} else {
				sbTab.append("    ");
			}
		}
		return sbTab.toString();
	}
}