package com.mottc.chat;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.mottc.chat.data.local.DaoMaster;
import com.mottc.chat.data.local.DaoSession;
import com.mottc.chat.data.sharedPreferences.Myinfo;

import org.greenrobot.greendao.database.Database;

import java.util.Iterator;
import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:11
 */

public class ChatApplication extends Application {

    public static Context applicationContext;
    private static ChatApplication instance;
    private String username = "";
    public DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        // 初始化环信sdk
        init(applicationContext);
    }

    public static ChatApplication getInstance() {
        return instance;
    }

	/*
     * 第一步：sdk的一些参数配置 EMOptions
	 * 第二步：将配置参数封装类 传入SDK初始化
	 */

    public void init(Context context) {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(context, options);
        if (success) {

//          TODO:设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
        }
    }


    public void setCurrentUserName(String username) {
        this.username = username;
        Myinfo.getInstance(instance).setUserInfo(Constant.KEY_USERNAME, username);
    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = Myinfo.getInstance(instance).getUserInfo(Constant.KEY_USERNAME);
        }
        Log.i("ChatApplication", "getCurrentUserName: " + username);
        return username;

    }


    private EMOptions initChatOptions() {

        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        return options;
    }

    private boolean sdkInited = false;

    public synchronized boolean initSDK(Context context, EMOptions options) {

        if (sdkInited) {
            return true;
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(applicationContext.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }

        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        sdkInited = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then
     * we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }


    public DaoSession getDaoSession() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), getCurrentUserName() + ".db");
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
        return mDaoSession;
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {

        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {

                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {

                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }
}
