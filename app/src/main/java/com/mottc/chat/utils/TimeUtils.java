package com.mottc.chat.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/28
 * Time: 10:57
 */
public class TimeUtils {
    public static String getCurrentTimeAsNumber() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        returnStr = f.format(date);
        return new BigDecimal(returnStr).toString();
    }
}
