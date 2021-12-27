
package com.leonside.dataroad.common.context;

public class ErrorLimitConfig  {

    public static final int DEFAULT_ERROR_RECORD_LIMIT = 0;
    public static final double DEFAULT_ERROR_PERCENTAGE_LIMIT = 0.0;

    private int record = 0;
    private double percentage = 0.0;

    public static ErrorLimitConfig defaultConfig(){
        return new ErrorLimitConfig();
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}