package com.leonside.dataroad.core.constant;

import java.io.Serializable;

/**
 * @author leon
 */
public class JobCoreConstants implements Serializable {
    public static final String JOBFLOW_NAME_PREFIX_OUTPUTTAG_DECIDER = "outputTag-" ;

    public static final String JOBFLOW_NAME_PREFIX_UNION_PROCESSOR = "union-" ;

    public static final String JOBFLOW_NAME_PREFIX_AGGERATION = "aggeration-" ;

    public static final String JOBFLOW_NAME_KEY_PREDICATE_OTHERWISE = "otherwise";

    public static final String JOBSCHEMA_PATH_PREFIX_CLASSPATH = "classpath:";
    public static final String JOBSCHEMA_PATH_PREFIX_FILESYSTEM = "filesystem:";


    public static final String AGGERATION_KEY_AGGFIELD = "aggField";
    public static final String AGGERATION_KEY_BEGINTIME = "beginTime";
    public static final String AGGERATION_KEY_ENDTIME = "endTime";
    public static final String AGGERATION_KEY_DUMPTIME = "dumpTime";

}
