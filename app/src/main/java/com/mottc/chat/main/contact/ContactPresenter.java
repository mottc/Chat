package com.mottc.chat.main.contact;

import com.mottc.chat.data.IModel;
import com.mottc.chat.data.Model;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 19:20
 */
class ContactPresenter implements ContactContract.Presenter{

    private ContactContract.View mView;
    private IModel mModel;

    ContactPresenter(ContactContract.View view) {
        mView = view;
        mModel = new Model();
    }


    @Override
    public void start() {
        mView.loadContact(mModel.getAllContact());
    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}
