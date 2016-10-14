package com.mottc.chat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.hyphenate.chat.EMClient;
import com.mottc.chat.R;

public class SplashActivity extends Activity {

    private RelativeLayout rootLayout;
    private ImageView imageView;
    public static final String TAG = "Splash_TAG";
    RequestQueue queue;
    private static final int sleepTime = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.splash_image);
        loadSplashImage();

//      淡入动画
        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
        animation.setDuration(sleepTime);
        rootLayout.startAnimation(animation);
    }

    private void loadSplashImage() {
        queue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(
                "http://7xktkd.com1.z0.glb.clouddn.com/splash.png",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.mipmap.splash);
            }
        });
        imageRequest.setTag(TAG);
        queue.add(imageRequest);
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
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


}
