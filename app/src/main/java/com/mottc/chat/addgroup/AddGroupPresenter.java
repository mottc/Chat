package com.mottc.chat.addgroup;

import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/16
 * Time: 16:26
 */
public class AddGroupPresenter implements AddGroupContact.Presenter {

    private AddGroupContact.View mView;

    public AddGroupPresenter(AddGroupContact.View view) {
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
    public void searchGroup(String groupNum) {
        if (TextUtils.isEmpty(groupNum)) {
            mView.showGroupNumIsEmpty();
        } else {

            mView.showSearchDialog();

            EMClient.getInstance().groupManager().asyncGetGroupFromServer(groupNum, new EMValueCallBack<EMGroup>() {
                @Override
                public void onSuccess(final EMGroup value) {

                    ((AddGroupActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.searchDialogDismiss();
                            mView.showSearchedLayout(value);
                        }
                    });
                }

                @Override
                public void onError(final int error, String errorMsg) {

                    ((AddGroupActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.searchDialogDismiss();
                            if (error == EMError.GROUP_INVALID_ID) {
                                mView.canNotFindGroup();
                            } else {
                                mView.findGroupFailure();
                            }
                        }
                    });

                }
            });
        }
    }

    @Override
    public void applyJoinToGroup(String groupNum) {

        mView.showAskDialog();
        EMClient.getInstance().groupManager().asyncApplyJoinToGroup(groupNum, "", new EMCallBack() {
            @Override
            public void onSuccess() {

                ((AddGroupActivity) mView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.askDialogDismiss();
                        mView.showAskSuccess();
                    }
                });

            }

            @Override
            public void onError(int code, final String error) {

                ((AddGroupActivity) mView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.askDialogDismiss();
                        mView.showAskFailure(error);
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
}
