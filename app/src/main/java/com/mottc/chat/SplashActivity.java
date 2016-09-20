package com.mottc.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

    private RelativeLayout rootLayout;

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(sleepTime);

        rootLayout.startAnimation(animation);
    }


    @Override
    protected void onStart() {

        super.onStart();

        new Thread(new Runnable() {
            public void run() {

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                /*TODO:*/
                //进入主页面
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();

            }
        }).start();
    }

}
