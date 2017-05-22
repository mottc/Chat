package com.mottc.chat.userdetail;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.mottc.chat.ChatApplication;
import com.mottc.chat.data.IModel;
import com.mottc.chat.data.Model;
import com.mottc.chat.utils.QiniuTokenUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 19:20
 */
public class UserDetailPresenter implements UserDetailContract.Presenter {

    private Uri uri;
    private UploadManager uploadManager;
    private UserDetailContract.View mView;
    private IModel mModel;

    public UserDetailPresenter(UserDetailContract.View mView) {
        this.mView = mView;
        uploadManager = new UploadManager();
        mModel = Model.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void isFriend(String username) {
        if (username.equals(ChatApplication.getInstance().getCurrentUserName())) {
            return;
        }
        if (mModel.hasFriend(username)) {
            mView.setSendVisible();
        } else {
            mView.setAddVisible();
        }
    }

    @Override
    public void uploadPic(Bitmap bitmap) {

        mView.showNewAvatar(uri);
        String userName = ChatApplication.getInstance().getCurrentUserName();
        String token = QiniuTokenUtils.creatImageToken(userName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //设置上传后文件的key
        String upKey = userName + ".png";
        uploadManager.put(data, upKey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo rinfo, JSONObject response) {
                mView.showUploadSuccess();
            }
        }, null);

    }

    @Override
    public void pickPic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((UserDetailActivity)mView).startActivityForResult(Intent.createChooser(intent, "选择图片"), 1);
    }

    @Override
    public void updateAvatarInfo(String username) {
        mModel.updateAvatarInfo(username);
    }

    @Override
    public void onPickResult(int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            //选择图片
            uri = data.getData();
            ContentResolver cr = ((UserDetailActivity)mView).getContentResolver();
            try {
                if (bitmap != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap.recycle();
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            mView.showNotPickPic();
        }

        if (bitmap != null) {
            uploadPic(bitmap);
        }
    }
}
