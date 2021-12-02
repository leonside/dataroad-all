package com.leonside.dataroad.common.utils;

import java.util.UUID;

/**
 * @author leon
 */
public class IdGenerator {

    public static String generate(String prefix){
        return prefix + UUID.randomUUID();
    }
}
