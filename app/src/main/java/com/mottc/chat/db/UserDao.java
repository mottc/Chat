package com.mottc.chat.db;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:24
 */
public class UserDao {

    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";


    public UserDao(Context context) {
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<EaseUser> contactList) {
        DBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {

        return DBManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人
     * @param username
     */
    public void deleteContact(String username){
        DBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContact(EaseUser user){
        DBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups){
        DBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String>  getDisabledGroups(){
        return DBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids){
        DBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds(){
        return DBManager.getInstance().getDisabledIds();
    }

}
