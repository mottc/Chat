package com.mottc.chat.chat;

import com.hyphenate.chat.EMMessage;
import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 10:57
 */
public interface ChatContract {
    interface View extends BaseView {
        void setChatToUsername(int chatType, String username);

        void sendNotification(String username, String info);

        void clearContent();

        void addMessage(EMMessage message);

        void gotoListBottom();

        void gotoUserDetailActivity(String toUsername);

        void gotoGroupDetailActivity(String toUsername);

        void addMessages(List<EMMessage> messages);


    }

    interface Presenter extends BasePresenter {

        void sendMessage(String content);

        void markAllMessagesAsRead();

        void gotoDetailActivity();

        void setInfo(String toChatUsername, int chatType);

        void loadAllMessages();


    }
}
