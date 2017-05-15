package com.mottc.chat.data.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 16:23
 */
@Entity
public class ChatUser {
    //不能用int
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String userName;
    private String avatar;
    @Generated(hash = 372292852)
    public ChatUser(Long id, String userName, String avatar) {
        this.id = id;
        this.userName = userName;
        this.avatar = avatar;
    }
    @Generated(hash = 450922767)
    public ChatUser() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
