package com.mottc.chat.creategroup;

import android.text.TextUtils;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 18:57
 */
public class CreateGroupPresenter implements CreateGroupContract.Presenter {

    private CreateGroupContract.View mView;

    public CreateGroupPresenter(CreateGroupContract.View view) {
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
    public void createGroup(String groupName,String desc) {
        if (TextUtils.isEmpty(groupName)) {
            mView.showGroupNameIsEmpty();
        } else {
            mView.showDialog();
            createNewGroup(groupName, desc);
        }

    }

    private void createNewGroup(String groupName, String desc) {
        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
        EMClient.getInstance().groupManager().asyncCreateGroup(groupName, desc, new String[0], null, option, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {
                mView.dialogDismiss();
                mView.showSuccess();
            }

            @Override
            public void onError(int error, String errorMsg) {

                mView.dialogDismiss();
                mView.showFailure(errorMsg);

            }
        });
    }
}
