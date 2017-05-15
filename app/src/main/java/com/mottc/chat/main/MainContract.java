package com.mottc.chat.main;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 15:06
 */
public interface MainContract {
    interface View extends BaseView {
        void showDisconnectedInfo(int error);

        void sendNewFriendsAddGroupNotification(String applyer, String reason, String groupName);

        void sendNewFriendsNotification(String username, String reason);

        void showInvitationReceived(String inviter, String groupName);

        void showRequestToJoinReceived(String applicant, String groupName, String reason);

        void showRequestToJoinAccepted(String groupName);

        void showRequestToJoinDeclined(String groupName);

        void showInvitationAccepted(String invitee, String groupId);

        void showInvitationDeclined(String invitee, String groupId);

        void showUserRemoved(String groupName);

        void showGroupDestroyed(String groupName);

        void showContactAdded(String username);

        void showContactDeleted(String username);

        void showFriendRequestDeclined(String username);

        void showContactInvited(String username);

        void showFriendRequestAccepted(String username);
    }

    interface Presenter extends BasePresenter {

    }
}
