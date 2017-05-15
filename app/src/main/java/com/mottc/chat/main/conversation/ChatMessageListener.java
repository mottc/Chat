package com.mottc.chat.main.conversation;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 21:43
 */
public class ChatMessageListener implements EMMessageListener {

    private ConversationContract.View mView;
    public ChatMessageListener(ConversationContract.View view) {
        mView = view;
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        mView.update();
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        mView.update();
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        mView.update();
    }
}
