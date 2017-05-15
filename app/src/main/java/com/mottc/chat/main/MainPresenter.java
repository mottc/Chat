package com.mottc.chat.main;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 16:31
 */
public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}
