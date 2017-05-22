package com.mottc.chat.message;

import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.mottc.chat.Constant;
import com.mottc.chat.data.IModel;
import com.mottc.chat.data.Model;
import com.mottc.chat.data.bean.ChatInviteMessage;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 21:27
 */
public class MessagePresenter implements MessageContract.Presenter {
    private MessageContract.View mView;
    private IModel mModel;

    public MessagePresenter(MessageContract.View view) {
        mView = view;
        mModel = Model.getInstance();
    }

    @Override
    public void start() {
        mView.addAllMessages(mModel.getAllInviteMessage());
    }

    @Override
    public void onDestroy() {
        mView = null;
    }


    /**
     * 同意好友请求或者群申请
     */

    @Override
    public void acceptInvitation(final Button buttonAgree, final ChatInviteMessage msg) {
        mView.showDialog();


        if (msg.getStatus() == Constant.FRIENDUNHANDLE) {//同意好友请求
            EMClient.getInstance().contactManager().asyncAcceptInvitation(msg.getFrom(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mModel.setMessageAgree(msg);
                            mView.agree();
                            mView.dialogDismiss();
                            buttonAgree.setText("已同意");
                            buttonAgree.setBackgroundDrawable(null);
                            buttonAgree.setEnabled(false);
                        }
                    });

                }

                @Override
                public void onError(int code, String error) {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.tryAgain();
                            mView.dialogDismiss();
                        }
                    });

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        } else if (msg.getStatus() == Constant.GROUPASKDUNHANDLE) {
            EMClient.getInstance().groupManager().asyncAcceptApplication(msg.getFrom(), msg.getGroupId(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mModel.setMessageAgree(msg);
                            mView.agree();
                            mView.dialogDismiss();
                            buttonAgree.setText("已同意");
                            buttonAgree.setBackgroundDrawable(null);
                            buttonAgree.setEnabled(false);
                        }
                    });

                }

                @Override
                public void onError(int code, String error) {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.tryAgain();
                            mView.dialogDismiss();
                        }
                    });

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        } else if (msg.getStatus() == Constant.GROUPINVITEDUNHANDLE) {
            EMClient.getInstance().groupManager().asyncAcceptInvitation(msg.getGroupId(), msg.getFrom(), new EMValueCallBack<EMGroup>() {
                @Override
                public void onSuccess(EMGroup value) {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mModel.setMessageAgree(msg);
                            mView.agree();
                            mView.dialogDismiss();
                            buttonAgree.setText("已同意");
                            buttonAgree.setBackgroundDrawable(null);
                            buttonAgree.setEnabled(false);
                        }
                    });

                }

                @Override
                public void onError(int error, String errorMsg) {
                    ((MessageActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.tryAgain();
                            mView.dialogDismiss();
                        }
                    });

                }
            });
        }
    }
}
