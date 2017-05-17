package com.mottc.chat.main;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.Constant;
import com.mottc.chat.data.IModel;
import com.mottc.chat.data.Model;
import com.mottc.chat.data.bean.ChatInviteMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:18
 */
class ChatGroupChangeListener implements EMGroupChangeListener {

    private MainContract.View mView;
    private IModel mModel;

    ChatGroupChangeListener(MainContract.View mView) {
        this.mView = mView;
        mModel = new Model();
    }

    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        mView.showInvitationReceived(inviter, groupName);
        List<ChatInviteMessage> inviteMessages = mModel.getAllInviteMessage();

        for (ChatInviteMessage inviteMessage : inviteMessages) {
            if (inviteMessage.getGroupId().equals(groupId) && inviteMessage.getFrom().equals(inviter)) {
                mModel.deleteMessage(inviter,groupId);
            }
        }
        // 自己封装的javabean
        ChatInviteMessage chatInviteMessage = new ChatInviteMessage();
        chatInviteMessage.setFrom(inviter);
        chatInviteMessage.setTime(System.currentTimeMillis());
        chatInviteMessage.setGroupId(groupId);
        chatInviteMessage.setGroupName(groupName);
        chatInviteMessage.setReason(reason);
        chatInviteMessage.setStatus(Constant.GROUPINVITEDUNHANDLE);

        mModel.addMessage(chatInviteMessage);
    }

    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

        mView.showRequestToJoinReceived(applicant, groupName, reason);

        mView.sendNewFriendsAddGroupNotification(applicant, reason, groupName);


        List<ChatInviteMessage> inviteMessages = mModel.getAllInviteMessage();

        for (ChatInviteMessage inviteMessage : inviteMessages) {
            if (inviteMessage.getGroupId().equals(groupId) && inviteMessage.getFrom().equals(applicant)) {
                mModel.deleteMessage(applicant,groupId);
            }
        }
        // 自己封装的javabean
        ChatInviteMessage chatInviteMessage = new ChatInviteMessage();
        chatInviteMessage.setFrom(applicant);
        chatInviteMessage.setTime(System.currentTimeMillis());
        chatInviteMessage.setGroupId(groupId);
        chatInviteMessage.setGroupName(groupName);
        chatInviteMessage.setReason(reason);
        chatInviteMessage.setStatus(Constant.GROUPASKDUNHANDLE);

        mModel.addMessage(chatInviteMessage);
    }

    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

        //加群申请被同意
        try {
            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        mView.showRequestToJoinAccepted(groupName);
    }

    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

        mView.showRequestToJoinDeclined(groupName);
    }

    @Override
    public void onInvitationAccepted(String groupId, String invitee, String reason) {

        mView.showInvitationAccepted(invitee, groupId);


        final String id = groupId;
        final String name = invitee;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().addUsersToGroup(id, new String[]{name});//需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {

        mView.showInvitationDeclined(invitee, groupId);
    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
        //当前用户被管理员移除出群组
        try {
            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        mView.showUserRemoved(groupName);
    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {

        mView.showGroupDestroyed(groupName);
    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

    }
}
