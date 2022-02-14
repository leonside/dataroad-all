package com.leonside.dataroad.common.utils;

import com.leonside.dataroad.common.exception.JobFlowException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class HttpUtil {

    public static String doGet(String url) throws IOException {
        StringBuffer sbf = new StringBuffer();
        HttpURLConnection conn = null;
        BufferedReader br = null;
        String content = null;
        try{
            URL u = new URL(url);
            conn = (HttpURLConnection)u.openConnection();
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(60000);
            if(conn.getResponseCode()==200){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                while((content=br.readLine())!=null){
                    sbf.append(content);
                }
            }
        }finally{
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("关闭"+ url + "路径异常",e);
                }
            }
            conn.disconnect();
        }
        return sbf.toString();
    }

}