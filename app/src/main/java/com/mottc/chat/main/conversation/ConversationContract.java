package com.mottc.chat.main.conversation;

import com.hyphenate.chat.EMConversation;
import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 21:24
 */
public interface ConversationContract {
    interface View extends BaseView {
        void loadAllConversation(List<EMConversation> conversationList);

        void update();
    }

    interface Presenter extends BasePresenter {

    }
}
