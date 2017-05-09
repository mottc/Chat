package com.mottc.chat.splash;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/9
 * Time: 10:54
 */
public class SplashPresenter implements SplashContract.Presenter {

    private static final int sleepTime = 2500;
    private SplashContract.View mView;

    public SplashPresenter(SplashContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

        new Thread(new Runnable() {
            public void run() {

                if (EMClient.getInstance().isLoggedInBefore()) {

                    long start = System.currentTimeMillis();
                    try {
                        EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;

                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mView.go2MainActivity();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ignored) {
                    }
                    mView.go2LoginActivity();
                }
            }
        }).start();


    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
