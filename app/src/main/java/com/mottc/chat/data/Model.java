package com.mottc.chat.data;

import com.mottc.chat.ChatApplication;
import com.mottc.chat.data.bean.ChatInviteMessage;
import com.mottc.chat.data.bean.ChatUser;
import com.mottc.chat.data.local.ChatInviteMessageDao;
import com.mottc.chat.data.local.ChatUserDao;
import com.mottc.chat.data.local.DaoSession;
import com.mottc.chat.utils.TimeUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 16:51
 */
public class Model implements IModel {

    private ChatUserDao mChatUserDao;
    private ChatInviteMessageDao mChatInviteMessageDao;

    public Model() {
        DaoSession daoSession = ChatApplication.getInstance().getDaoSession();
        mChatUserDao = daoSession.getChatUserDao();
        mChatInviteMessageDao = daoSession.getChatInviteMessageDao();
    }

    @Override
    public void refreshAllContact(List<String> userNames) {
        mChatUserDao.deleteAll();

        for (String userName : userNames) {
            ChatUser cozeUser = new ChatUser(null, userName, null);
            mChatUserDao.insert(cozeUser);
        }
    }

    @Override
    public List<ChatUser> getAllContact() {
        return mChatUserDao.loadAll();
    }

    @Override
    public void addContact(String userName) {
        ChatUser addedChatUser = mChatUserDao
                .queryBuilder()
                .where(ChatUserDao.Properties.UserName.eq(userName))
                .build()
                .unique();
        if (addedChatUser == null) {
            ChatUser chatUser = new ChatUser(null, userName, null);
            mChatUserDao.insert(chatUser);
        }
    }

    @Override
    public void deleteContact(String userName) {
        ChatUser deletedChatUser = mChatUserDao
                .queryBuilder()
                .where(ChatUserDao.Properties.UserName.eq(userName))
                .build()
                .unique();
        mChatUserDao.deleteByKey(deletedChatUser.getId());
    }

    @Override
    public List<ChatInviteMessage> getAllInviteMessage() {
        return mChatInviteMessageDao.loadAll();
    }

    @Override
    public void addMessage(ChatInviteMessage chatInviteMessage) {
        mChatInviteMessageDao.insert(chatInviteMessage);
    }

    @Override
    public void deleteMessage(String username, String groupId) {
        ChatInviteMessage deletedChatInviteMessage = mChatInviteMessageDao
                .queryBuilder()
                .where(ChatInviteMessageDao.Properties.From.eq(username))
                .where(ChatInviteMessageDao.Properties.GroupName.eq(groupId))
                .build()
                .unique();
        mChatInviteMessageDao.deleteByKey(deletedChatInviteMessage.getId());
    }

    @Override
    public String getAvatarInfo(String username) {
        ChatUser chatUser = mChatUserDao
                .queryBuilder()
                .where(ChatUserDao.Properties.UserName.eq(username))
                .build()
                .unique();
        if (chatUser != null) {
            return chatUser.getAvatar();
        } else {
            return null;
        }
    }

    @Override
    public void updateAvatarInfo(String username) {
        ChatUser chatUser = mChatUserDao
                .queryBuilder()
                .where(ChatUserDao.Properties.UserName.eq(username))
                .build()
                .unique();
        if (chatUser == null) {
            return;
        }
        ChatUser newChatUser = new ChatUser();
        newChatUser.setId(chatUser.getId());
        newChatUser.setUserName(chatUser.getUserName());
        newChatUser.setAvatar(TimeUtils.getCurrentTimeAsNumber());
        mChatUserDao.update(newChatUser);
    }

    @Override
    public boolean hasFriend(String username) {
        ChatUser chatUser = mChatUserDao
                .queryBuilder()
                .where(ChatUserDao.Properties.UserName.eq(username))
                .build()
                .unique();
        return chatUser != null;
    }
}
