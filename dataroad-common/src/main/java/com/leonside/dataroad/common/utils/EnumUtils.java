package com.leonside.dataroad.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class EnumUtils {

    public static String[] getEnumNameArray(Class<? extends Enum> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(item-> item.name()).toArray(String[]::new);
    }

}
