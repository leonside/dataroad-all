package com.leonside.dataroad.common.constant;

/**
 * @author leon
 */
public abstract class JobConfigConstants {

    public static final String SINGLE_QUOTE_MARK_SYMBOL = "'";
    public static final String DOUBLE_QUOTE_MARK_SYMBOL = "\"";

    public static final String LEFT_PARENTHESIS_SYMBOL = "(";
    public static final String RIGHT_PARENTHESIS_SYMBOL = ")";
    public static final String SINGLE_SLASH_SYMBOL = "/";
    public static final String DOUBLE_SLASH_SYMBOL = "//";
    public static final String KEY_HTTP = "http";
    public static final String PROTOCOL_HTTP = "http://";

    public static final String CONFIG_STAR_SYMBOL = "*";
    public static final String CONFIG_POINT_SYMBOL = ".";


    //script config param
    public static final String CONFIG_SCRIPT_EXPRESSION = "expression";

    public static final String CONFIG_SCRIPT_LANGUAGE = "language";

    //agg config param
    public static final String CONFIG_AGG_WINDOWSIZE = "windowSize";

    public static final String CONFIG_AGG_WINDOWSLIDE = "windowSlide";

    public static final String CONFIG_AGG_KEYBY = "keyby";

    public static final String CONFIG_AGG_FIELDAGG = "agg";

    //JDBC config param
    public static final String CONFIG_JDBC_PROTOCOL_MYSQL = "jdbc:mysql://";

    public static final int MAX_BATCH_SIZE = 200000;
}
