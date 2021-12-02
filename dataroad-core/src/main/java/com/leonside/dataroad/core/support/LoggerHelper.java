package com.leonside.dataroad.core.support;

import com.leonside.dataroad.common.context.LogConfig;

/**
 * @author leon
 */
public class LoggerHelper {

    private static String level = "info";

    public static boolean isLogger = false;

    public static void init(LogConfig logConfig){
        LoggerHelper.level = logConfig.getLevel();
        LoggerHelper.isLogger = logConfig.isLogger();
    }

    public static String getLevel(){
        return level;
    }

    public static boolean isLogger(){
        return isLogger;
    }
}
