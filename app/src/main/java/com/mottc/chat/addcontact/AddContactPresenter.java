package com.mottc.chat.addcontact;

import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 15:49
 */
public class AddContactPresenter implements AddContactContract.Presenter {
    private AddContactContract.View mView;

    public AddContactPresenter(AddContactContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void onDestroy() {

        mView = null;
    }

    @Override
    public void addContact(String username, String reason) {
        if (TextUtils.isEmpty(username)) {
            mView.showUsernameIsEmpty();
            return;
        }
        mView.showDialog();
        EMClient.getInstance().contactManager().aysncAddContact(username, reason, new EMCallBack() {
            @Override
            public void onSuccess() {
                mView.dialogDismiss();
                mView.showSuccess();
            }

            @Override
            public void onError(int code, String error) {
                mView.dialogDismiss();
                mView.showFailure(error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
}
