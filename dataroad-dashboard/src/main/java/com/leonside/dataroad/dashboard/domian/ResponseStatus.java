package com.leonside.dataroad.dashboard.domian;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class ResponseStatus<T>  {
    private int code;
    private String msg;
    private int count;
    private T data;

    public ResponseStatus(int status, String message) {
        this.code = status;
        this.msg = message;
    }

    public ResponseStatus(int status, String message,int count, T data) {
        this.code = status;
        this.msg = message;
        this.data = data;
        this.count = count;
    }

    public ResponseStatus() {
    }

    public static <T> ResponseStatus success(String message){
        return new ResponseStatus(0, message);
    }

    public static <T> ResponseStatus success(String message,int count, T data){
        return new ResponseStatus(0, message, count, data);
    }

    public static ResponseStatus success(){
        return new ResponseStatus(0, "操作成功！");
    }

    public static ResponseStatus error(){
        return new ResponseStatus(1, "操作失败！");
    }

    public static ResponseStatus error(String mesage){
        return new ResponseStatus(1, mesage);
    }
}
