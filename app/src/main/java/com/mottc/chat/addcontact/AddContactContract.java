package com.mottc.chat.addcontact;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 15:46
 */
public interface AddContactContract {
    interface View extends BaseView {
        void showUsernameIsEmpty();

        void showSuccess();

        void showFailure(String error);

        void showDialog();

        void dialogDismiss();
    }

    interface Presenter extends BasePresenter {
        void addContact(String username, String reason);

    }
}
