package com.yue.Spider.util;

/**
 * Created by andrew on 16/3/1.
 */
public class StringUtil {

    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.isEmpty())
            return true;
        return false;
    }
}
