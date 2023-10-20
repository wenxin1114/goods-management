package com.wenxin.sm.utils;


public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
