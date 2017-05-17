package com.mottc.chat.groupdetail;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.mottc.chat.ChatApplication;
import com.mottc.chat.utils.QiniuTokenUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/17
 * Time: 20:21
 */
class GroupDetailPresenter implements GroupDetailContract.Presenter {

    private List<String> members;
    private String owner;
    private String groupId;
    private Uri uri;
    private UploadManager uploadManager;
    private GroupDetailContract.View mView;

    GroupDetailPresenter(GroupDetailContract.View mView) {
        this.mView = mView;
        uploadManager = new UploadManager();
    }

    @Override
    public void start() {
        groupId = ((GroupDetailActivity)mView).getIntent().getStringExtra("groupId");
        mView.setGroupId(groupId);

        String groupName = EMClient.getInstance().groupManager().getGroup(groupId).getGroupName();
        mView.setGroupName(groupName);
        mView.setGroupAvatar(groupId);


        EMClient.getInstance().groupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {
                members = value.getMembers();//获取群成员
                owner = value.getOwner();//获取群主
                if (ChatApplication.getInstance().getCurrentUserName().equals(owner)) {
                    mView.showInvite();
                    ((GroupDetailActivity) mView).groupOwner = owner;
                }
                mView.setGroupSize(members.size());
                mView.addGroupMembers(members);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void pickPic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((GroupDetailActivity) mView).startActivityForResult(Intent.createChooser(intent, "选择图片"), 1);
    }

    @Override
    public void uploadPic(Bitmap bitmap) {
        mView.showNewAvatar(uri);

        String token = QiniuTokenUtils.creatImageToken(groupId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //设置上传后文件的key
        String upKey = groupId + ".png";
        uploadManager.put(data, upKey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo rinfo, JSONObject response) {
                mView.showUploadSuccess();
            }
        }, null);
    }

    @Override
    public void onPickResult(int resultCode, Intent data) {

        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            //选择图片
            uri = data.getData();
            ContentResolver cr = ((GroupDetailActivity) mView).getContentResolver();
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

    @Override
    public void go2Invite() {
        mView.go2Invite(groupId);
    }

}
