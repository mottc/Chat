package com.mottc.chat.invitemembers;

import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 21:10
 */
public class InviteMembersPresenter implements InviteMembersContract.Presenter {

    private EMGroup group;
    private String groupId;
    private InviteMembersContract.View mView;

    public InviteMembersPresenter(InviteMembersContract.View view) {
        mView = view;
    }

    @Override
    public void start() {
        groupId = ((InviteMembersActivity)mView).getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void inviteMembers(String username) {
        if (TextUtils.isEmpty(username)) {
            mView.showUsernameIsEmpty();
            return;
        }
        if (group != null) {
            List<String> members = group.getMembers();
            if (members.contains(username)) {
                mView.showUserHasBeenInGroup();
            } else {
                final String[] newMembers = new String[]{username};
                EMClient.getInstance().groupManager().asyncAddUsersToGroup(groupId, newMembers, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        ((InviteMembersActivity)mView).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.inviteSuccess();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String error) {
                        ((InviteMembersActivity)mView).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.inviteFailure();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        } else {
            mView.showError();
        }
    }
}
