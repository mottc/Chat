package com.mottc.chat.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mottc.chat.main.MainActivity;
import com.mottc.chat.Constant;
import com.mottc.chat.R;
import com.mottc.chat.login.LoginActivity;

import java.util.Random;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {


    private SplashContract.Presenter mSplashPresenter;
    private ImageView chatLogo;
    private ImageView splashPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashPresenter = new SplashPresenter(this);
        splashPic = (ImageView) findViewById(R.id.splash_image);
        chatLogo = (ImageView) findViewById(R.id.chat_logo);

        setSplashPic();
        showAnimation();
    }



    @Override
    protected void onStart() {

        super.onStart();
        mSplashPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashPresenter.onDestroy();
        getWindow().setBackgroundDrawable(null);
    }


    public void setSplashPic() {
        int splashNum = new Random().nextInt(10);
        String Url = Constant.AVATAR_URL + "ChatSplash" + splashNum + ".png";
        Glide
                .with(this)
                .load(Url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(splashPic);
    }

    private void showAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, -250, 0, 650);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(2000);
        chatLogo.startAnimation(translateAnimation);
    }

    @Override
    public void go2MainActivity() {
        //进入主页面
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void go2LoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
