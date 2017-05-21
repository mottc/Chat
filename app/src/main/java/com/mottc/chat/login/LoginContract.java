package com.mottc.chat.login;

import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/8
 * Time: 19:50
 */
public interface LoginContract {

    interface View extends BaseView {
        void showNoNet();

        void showLoginProgressDialog();

        void cancelLoginProgressDialog();

        void showRegisterProgressDialog();

        void cancelRegisterProgressDialog();

        void go2MainActivity();

        void showCanNotLogin(String message);

        void showCanNotRegister(HyphenateException e);

        void showRegisterSuccessfully();

        void go2LoginActivity();

    }

    interface Presenter extends BasePresenter {

        void login(String loginUserName, String loginPassword);

        void register(String RegisterUserName, String RegisterPassword);

        void autoLogin();

    }

    interface RefreshAllContactListener {
        void onSuccess();

        void onFailure();
    }

}
