package com.mottc.chat.main;

import android.app.Activity;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/12
 * Time: 19:14
 */
public class ChatConnectionListener implements EMConnectionListener {
    private Activity mActivity;

    public ChatConnectionListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(final int error) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    Toast.makeText(mActivity, "帐号已经被移除", Toast.LENGTH_SHORT).show();
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    Toast.makeText(mActivity, "帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtils.hasNetwork(mActivity)) {
                        //连接不到聊天服务器
                        Toast.makeText(mActivity, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(mActivity, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
