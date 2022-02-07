package com.leonside.dataroad.dashboard.domian;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class ComponentParameter {
    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;
    //String、boolean、enum、object类型，用于前端页面渲染
    private String fieldType;
    private String[] fieldEnumList;


}
