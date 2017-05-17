package com.mottc.chat.utils;

import com.qiniu.util.Auth;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/24
 * Time: 10:51
 */
public class QiniuTokenUtils {
    public static String creatImageToken(String userName) {
        String bucketName = "jungle:" + userName + ".png";
        Auth auth = Auth.create("thx5mKjSsksUU1I24M8XTt5q0DSjgs9tXpMB54gr", "Xw2OGDoefwxGEAuJP_SWHnvm32PssnJgTJRGeHTB");
        String token = auth.uploadToken(bucketName);
        return token;
    }
}
