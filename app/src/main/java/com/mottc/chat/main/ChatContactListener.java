package com.mottc.chat.main;

import com.hyphenate.EMContactListener;
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
    public void onContactAdded(String username) {

        mModel.addContact(username);
        mView.showContactAdded(username);
    }

    @Override
    public void onContactDeleted(String username) {
        mModel.deleteContact(username);
        mView.showContactDeleted(username);
    }

    @Override
    public void onContactInvited(String username, String reason) {
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
        chatInviteMessage.setStatus(0);

        mModel.addMessage(chatInviteMessage);
        mView.showContactInvited(username);
        mView.sendNewFriendsNotification(username, reason);
    }

    @Override
    public void onFriendRequestAccepted(String username) {
        mView.showFriendRequestAccepted(username);
    }

    @Override
    public void onFriendRequestDeclined(String username) {
        mView.showFriendRequestDeclined(username);
    }
}
