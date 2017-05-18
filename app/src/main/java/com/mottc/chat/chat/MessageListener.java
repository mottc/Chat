package com.mottc.chat.chat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 12:20
 */
public class MessageListener implements EMMessageListener {
    private String toChatUsername;
    private ChatContract.View mView;

    public MessageListener(String toChatUsername, ChatContract.View view) {
        this.toChatUsername = toChatUsername;
        mView = view;
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {

        for (final EMMessage message : messages) {
            String username = null;
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            } else {
                username = message.getFrom();
            }
            final String finalUsername = username;
            ((ChatActivity)mView).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalUsername.equals(toChatUsername)) {
                        mView.addMessage(message);
                    } else {
                        //获取发来的消息
                        String info = message.toString();
                        int start = info.indexOf("txt:\"");
                        int end = info.lastIndexOf("\"");
                        info = info.substring((start + 5), end);
                        mView.sendNotification(finalUsername, info);//在通知栏发出通知
                    }
                }
            });

        }
        // 收到消息
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }


}