package com.mottc.chat.data;

import com.mottc.chat.data.bean.ChatInviteMessage;
import com.mottc.chat.data.bean.ChatUser;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 16:50
 */
public interface IModel {
    void refreshAllContact(List<String> userNames);

    List<ChatUser> getAllContact();

    void addContact(String userName);

    void deleteContact(String userName);

    List<ChatInviteMessage> getAllInviteMessage();

    void addMessage(ChatInviteMessage chatInviteMessage);

    void deleteMessage(String username,String groupId);

}
