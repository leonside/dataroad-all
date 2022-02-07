package com.leonside.dataroad.dashboard.domian;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class CodeRecord {

    private String code;
    private String desc;

    public CodeRecord(){

    }
    public CodeRecord(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
