package com.mottc.chat.db;

import com.hyphenate.chat.EMContact;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:22
 */
public class EaseUser extends EMContact{

    /**
     * 昵称首字母
     */
    protected String initialLetter;
    /**
     * 用户头像
     */
    protected String avatar;

    public EaseUser(String username){
        this.username = username;
    }

    public String getInitialLetter() {
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EaseUser)) {
            return false;
        }
        return getUsername().equals(((EaseUser) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }

}
