package com.mottc.chat.main.group;

import com.hyphenate.chat.EMGroup;
import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 20:46
 */
public interface GroupContract {
    interface View extends BaseView {
        void loadGroups(List<EMGroup> groupList);

        void loadGroupsError(String errorMsg);
    }

    interface Presenter extends BasePresenter {
    }
}
