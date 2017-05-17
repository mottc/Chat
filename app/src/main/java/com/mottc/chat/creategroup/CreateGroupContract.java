package com.mottc.chat.creategroup;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 18:53
 */
public interface CreateGroupContract {
    interface View extends BaseView {
        void showGroupNameIsEmpty();

        void showSuccess();

        void showFailure(String error);

        void showDialog();

        void dialogDismiss();
    }

    interface Presenter extends BasePresenter {
        void createGroup(String groupName, String desc);

    }
}
