package com.mottc.chat.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.mottc.chat.ChatApplication;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.db.InviteMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:38
 */
public class ChatContactListener implements EMContactListener {
    private Context mContext;

    public ChatContactListener(Context context) {
        mContext = context;
    }

    @Override
    public void onContactAdded(final String username) {
        // 保存增加的联系人
        Map<String, EaseUser> localUsers = ChatApplication.getInstance().getContactList();
        Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
        EaseUser user = new EaseUser(username);
        // 添加好友时可能会回调added方法两次
        if (!localUsers.containsKey(username)) {
            userDao.saveContact(user);
        }
        toAddUsers.put(username, user);
        localUsers.putAll(toAddUsers);

        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mContext, "增加联系人：+" + username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onContactDeleted(final String username) {
        // 被删除
        Map<String, EaseUser> localUsers = ChatApplication.getInstance().getContactList();
        localUsers.remove(username);
        userDao.deleteContact(username);
        inviteMessageDao.deleteMessage(username);

        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(mContext, "删除联系人：+" + username, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onContactInvited(final String username, String reason) {
        // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
        List<InviteMessage> msgs = inviteMessageDao.getMessagesList();

        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                inviteMessageDao.deleteMessage(username);
            }
        }
        // 自己封装的javabean
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());
        msg.setReason(reason);

        // 设置相应status
        msg.setStatus(InviteMessage.InviteMessageStatus.BEINVITEED);
        notifyNewIviteMessage(msg);
        sendNewFriendsNotification(username, reason);
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mContext, "收到好友申请：+" + username, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFriendRequestAccepted(final String username) {
        List<InviteMessage> msgs = inviteMessageDao.getMessagesList();
        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getFrom().equals(username)) {
                return;
            }
        }
        // 自己封装的javabean
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());

        msg.setStatus(InviteMessage.InviteMessageStatus.BEAGREED);
        notifyNewIviteMessage(msg);
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mContext, "好友申请同意：+" + username, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFriendRequestDeclined(String username) {

        // 参考同意，被邀请实现此功能,demo未实现
        Log.d(username, username + "拒绝了你的好友请求");
    }
}
