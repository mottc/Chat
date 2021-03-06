package com.mottc.chat.main.group;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.mottc.chat.main.group.GroupContract.Presenter;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 20:48
 */
public class GroupPresenter implements Presenter {
    private GroupContract.View mView;

    public GroupPresenter(GroupContract.View view) {
        mView = view;
    }

    @Override
    public void start() {
        EMClient.getInstance().groupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(final List<EMGroup> value) {
                ((GroupFragment)mView).getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.loadGroups(value);
                    }
                });
            }

            @Override
            public void onError(int error, final String errorMsg) {
                ((GroupFragment)mView).getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.loadGroupsError(errorMsg);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {

        mView = null;
    }
}
