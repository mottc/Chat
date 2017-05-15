package com.mottc.chat.main.contact;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;
import com.mottc.chat.data.bean.ChatUser;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 19:56
 */
public interface ContactContract {
    interface View extends BaseView {
        void loadContact(List<ChatUser> userNames);
    }

    interface Presenter extends BasePresenter {

    }
}
