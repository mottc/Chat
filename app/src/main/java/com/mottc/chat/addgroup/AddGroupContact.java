package com.mottc.chat.addgroup;

import com.hyphenate.chat.EMGroup;
import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 16:11
 */
public interface AddGroupContact {
    interface View extends BaseView {
        void showAskSuccess();

        void showAskFailure(String error);

        void showGroupNumIsEmpty();

        void showSearchDialog();

        void searchDialogDismiss();

        void canNotFindGroup();

        void findGroupFailure();

        void showAskDialog();

        void askDialogDismiss();

        void showSearchedLayout(EMGroup value);
    }

    interface Presenter extends BasePresenter {
        void searchGroup(String groupNum);

        void applyJoinToGroup(String groupNum);
    }
}
