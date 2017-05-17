package com.mottc.chat.groupdetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.mottc.chat.BasePresenter;
import com.mottc.chat.BaseView;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 20:13
 */
public class GroupDetailContract {
    interface View extends BaseView {
        void setGroupId(String groupId);

        void setGroupName(String groupName);

        void setGroupAvatar(String groupId);

        void setGroupSize(int size);

        void showInvite();

        void showNotPickPic();

        void go2Invite(String groupId);

        void showNewAvatar(Uri uri);

        void showUploadSuccess();

        void addGroupMembers(List<String> groupMembers);

    }

    interface Presenter extends BasePresenter {
        void pickPic();

        void uploadPic(Bitmap bitmap);

        void onPickResult(int resultCode, Intent data);


        void go2Invite();
    }
}
