package com.mottc.chat.chat;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.mottc.chat.Constant;
import com.mottc.chat.utils.CommonUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 11:00
 */
public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View mView;
    private String toChatUsername;
    private int chatType;
    private EMConversation conversation;

    public ChatPresenter(ChatContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                CommonUtils.getConversationType(chatType), true);

        conversation.markAllMessagesAsRead();

        List<EMMessage> messages = conversation.getAllMessages();
        int msgCount = messages != null ? messages.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < 20) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, 20 - msgCount);
        }

    }

    @Override
    public void onDestroy() {

        mView = null;
    }



    @Override
    public void sendMessage(String content) {

        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
        mView.addMessage(message);
        mView.clearContent();

    }

    @Override
    public void markAllMessagesAsRead() {
        conversation.markAllMessagesAsRead();
    }

    @Override
    public void gotoDetailActivity() {
        if (chatType == 1) {
            mView.gotoUserDetailActivity(toChatUsername);
        } else {
            mView.gotoGroupDetailActivity(toChatUsername);
        }

    }

    @Override
    public void setInfo(String toChatUsername, int chatType) {
        this.toChatUsername = toChatUsername;
        this.chatType = chatType;
    }

    @Override
    public void loadAllMessages() {
        mView.addMessages(conversation.getAllMessages());
    }
}
