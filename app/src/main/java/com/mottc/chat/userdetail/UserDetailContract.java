package com.mottc.chat.userdetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 19:12
 */
public class UserDetailContract {
    interface View extends BaseView {
        void showNotPickPic();

        void setAddVisible();

        void setSendVisible();

        void showNewAvatar(Uri uri);

        void showUploadSuccess();
    }

    interface Presenter extends BasePresenter {
        void isFriend(String username);

        void uploadPic(Bitmap bitmap);

        void pickPic();

        void updateAvatarInfo(String username);

        void onPickResult(int resultCode, Intent data);
    }
}
