package com.mottc.chat.message;

import android.widget.Button;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;
import com.mottc.chat.data.bean.ChatInviteMessage;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 21:22
 */
public interface MessageContract {
    interface View extends BaseView {

        void addAllMessages(List<ChatInviteMessage> messages);

        void agree();

        void tryAgain();

        void acceptInvitation(Button button, ChatInviteMessage chatInviteMessage);

        void showDialog();

        void dialogDismiss();
    }

    interface Presenter extends BasePresenter {
        void acceptInvitation(Button buttonAgree, ChatInviteMessage msg);
    }
}
