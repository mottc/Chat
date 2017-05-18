package com.mottc.chat.main;

import com.hyphenate.EMContactListener;
import com.mottc.chat.Constant;
import com.mottc.chat.data.IModel;
import com.mottc.chat.data.Model;
import com.mottc.chat.data.bean.ChatInviteMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:38
 */
class ChatContactListener implements EMContactListener {
    private MainContract.View mView;
    private IModel mModel;

    ChatContactListener(MainContract.View view) {
        mView = view;
        mModel = new Model();
    }

    @Override
    public void onContactAdded(final String username) {

        mModel.addContact(username);

        ((MainActivity) mView).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showContactAdded(username);
            }
        });
    }

    @Override
    public void onContactDeleted(final String username) {
        mModel.deleteContact(username);
        ((MainActivity) mView).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showContactDeleted(username);

            }
        });
    }

    @Override
    public void onContactInvited(final String username, final String reason) {
        List<ChatInviteMessage> inviteMessages = mModel.getAllInviteMessage();
        for (ChatInviteMessage inviteMessage : inviteMessages) {
            if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                mModel.deleteMessage(username,null);
            }
        }
        ChatInviteMessage chatInviteMessage = new ChatInviteMessage();
        chatInviteMessage.setFrom(username);
        chatInviteMessage.setTime(System.currentTimeMillis());
        chatInviteMessage.setReason(reason);
        chatInviteMessage.setStatus(Constant.FRIENDUNHANDLE);

        mModel.addMessage(chatInviteMessage);

        ((MainActivity) mView).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showContactInvited(username);
                mView.sendNewFriendsNotification(username, reason);
            }
        });

    }

    @Override
    public void onFriendRequestAccepted(final String username) {
        ((MainActivity) mView).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showFriendRequestAccepted(username);

            }
        });
    }

    @Override
    public void onFriendRequestDeclined(final String username) {
        ((MainActivity) mView).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showFriendRequestDeclined(username);
            }
        });
    }
}
