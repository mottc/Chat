package com.mottc.chat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.R;

public class SplashActivity extends Activity {

    private RelativeLayout rootLayout;
    private ImageView imageView;
    private static final int sleepTime = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.splash_image);
        String Url = "http://7xktkd.com1.z0.glb.clouddn.com/splash.png";
        Glide
                .with(this)
                .load(Url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);

//      渐深动画
        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
        animation.setDuration(sleepTime);
        rootLayout.startAnimation(animation);
    }


    @Override
    protected void onStart() {

        super.onStart();

        new Thread(new Runnable() {
            public void run() {

                if (EMClient.getInstance().isLoggedInBefore()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    try {
                        EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Glide.get(this).clearMemory();//清理内存缓存 在UI主线程中进行
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(SplashActivity.this).clearDiskCache();//清理磁盘缓存 不能在UI主线程中进行
//            }
//        }).start();

    }
}
