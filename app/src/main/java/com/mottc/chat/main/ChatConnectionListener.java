package com.mottc.chat.main;

import com.hyphenate.EMConnectionListener;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:14
 */
class ChatConnectionListener implements EMConnectionListener {
    private MainContract.View view;

    ChatConnectionListener(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(final int error) {
        ((MainActivity) view).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showDisconnectedInfo(error);
            }
        });
    }
}
