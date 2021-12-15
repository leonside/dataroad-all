package com.leonside.dataroad.plugin.jdbc;

import java.io.Serializable;

/**
 */
public interface ParameterValuesProvider  {

    /**
     * 获取分片信息
     * TODO 优化这部分逻辑
     *
     * @return 分片信息
     */
    Serializable[][] getParameterValues();
}
