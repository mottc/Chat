package com.mottc.chat.db;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/28
 * Time: 16:22
 */
public class AvatarInfo {
    //用户名
    private String username;

    //头像刷新时间
    private String time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
