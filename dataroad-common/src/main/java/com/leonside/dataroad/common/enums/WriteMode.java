
package com.leonside.dataroad.common.enums;

/**
 * Data write type
 */
public enum WriteMode {

    /**
     * insert into
     */
    INSERT,

    /**
     * insert into ... on duplicate key update
     */
    UPDATE,

    /**
     * replace into
     */
    REPLACE,

    UPSERT
}
