package com.mottc.chat.login;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.ChatApplication;
import com.mottc.chat.data.Model;
import com.mottc.chat.utils.EaseCommonUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/8
 * Time: 19:49
 */
public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private Model mModel;


    public LoginPresenter(LoginContract.View view) {
        mView = view;
        mModel = new Model();
    }

    @Override
    public void start() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    public void login(String loginUserName, String loginPassword) {

        if (!EaseCommonUtils.isNetWorkConnected((LoginActivity)mView)) {
            mView.showNoNet();
            return;
        }

        // reset current loginUserName name before login
        ChatApplication.getInstance().setCurrentUserName(loginUserName);
        // close it before login to make sure DemoDB not overlap
        DBManager.getInstance().closeDB();

        mView.showLoginProgressDialog();

        EMClient.getInstance().login(loginUserName, loginPassword, new EMCallBack() {

            @Override
            public void onSuccess() {

                ((LoginActivity) mView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.cancelLoginProgressDialog();
                    }
                });

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and

                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                getFriends();

                ((LoginActivity) mView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.go2MainActivity();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                ((LoginActivity) mView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.showCanNotLogin(message);
                    }
                });
            }
        });
    }

    @Override
    public void register(final String RegisterUserName, final String RegisterPassword) {
        mView.showRegisterProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMClient.getInstance().createAccount(RegisterUserName, RegisterPassword);
                    ChatApplication.getInstance().setCurrentUserName(RegisterUserName);

                    ((LoginActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.cancelRegisterProgressDialog();
                            mView.showRegisterSuccessfully();
                            mView.go2LoginActivity();
                        }
                    });
                } catch (final HyphenateException e) {
                    ((LoginActivity) mView).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.cancelRegisterProgressDialog();
                            mView.showCanNotRegister(e);
                        }
                    });
                }
            }
        }).start();
    }


    private void getFriends() {
        try {
            List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            mModel.refreshAllContact(userNames);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void autoLogin() {
        //如果登录成功过，直接进入主页面
        if (EMClient.getInstance().isLoggedInBefore()) {
            mView.go2MainActivity();
        }
    }
}
