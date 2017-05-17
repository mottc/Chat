package com.mottc.chat.invitemembers;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 21:07
 */
public interface InviteMembersContract {
    interface View extends BaseView {
        void showUsernameIsEmpty();

        void showUserHasBeenInGroup();

        void inviteSuccess();

        void inviteFailure();

        void showError();

    }

    interface Presenter extends BasePresenter {
        void inviteMembers(String username);
    }
}
