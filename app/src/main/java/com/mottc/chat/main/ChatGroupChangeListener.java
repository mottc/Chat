package com.mottc.chat.main;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.db.InviteMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:18
 */
class ChatGroupChangeListener implements EMGroupChangeListener {

    private View mView;

    ChatGroupChangeListener(View view) {
        mView = view;
    }

    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        //收到加入群组的邀请
        Snackbar.make(mView, inviter + "邀请您加入群组" + groupName, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

        //收到加群申请
        Snackbar.make(mView, applicant + "申请加入" + groupName + "：" + reason, Snackbar.LENGTH_LONG).show();

        List<InviteMessage> inviteMessages = inviteMessageDao.getMessagesList();

        for (InviteMessage inviteMessage : inviteMessages) {
            if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(applicant)) {
                inviteMessageDao.deleteMessage(applicant);
            }
        }
        // 自己封装的javabean
        InviteMessage msg = new InviteMessage();
        msg.setFrom(applicant);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(groupName);
        msg.setReason(reason);

        // 设置相应status
        msg.setStatus(InviteMessage.InviteMessageStatus.BEAPPLYED);
        notifyNewIviteMessage(msg);
        sendNewFriendsAddGroupNotification(applicant, reason, groupName);
    }

    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

        //加群申请被同意
        try {
            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        Snackbar.make(mView, "加入" + groupName + "群组的请求已被同意", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

        //加群申请被拒绝
        Snackbar.make(mView, "加入" + groupName + "群组的请求已被拒绝", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onInvitationAccepted(String groupId, String invitee, String reason) {

        //群组邀请被接受
        Snackbar.make(mView, invitee + "已接受加入群组" + EMClient.getInstance().groupManager().getGroup(groupId).getGroupName(), Snackbar.LENGTH_LONG)
                .show();
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

        //群组邀请被拒绝
        Snackbar.make(mView, invitee + "拒绝加入群组" + EMClient.getInstance().groupManager().getGroup(groupId).getGroupName(), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
        //当前用户被管理员移除出群组
        try {
            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        Snackbar.make(mView, "您已被移除出群组" + groupName, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {

        //群组被创建者解散
        Snackbar.make(mView, "群组" + groupName + "已被解散", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

    }
}
