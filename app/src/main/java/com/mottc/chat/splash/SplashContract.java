package com.mottc.chat.splash;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/9
 * Time: 10:49
 */
public interface SplashContract {
    interface View extends BaseView {
        void go2MainActivity();

        void go2LoginActivity();

    }

    interface Presenter extends BasePresenter {

    }
}
