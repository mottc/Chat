package com.mottc.chat.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.mottc.chat.MyApplication;
import com.mottc.chat.R;
import com.mottc.chat.db.DBManager;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.utils.PersonAvatarUtils;
import com.mottc.chat.utils.QiniuTokenUtils;
import com.mottc.chat.utils.TimeUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDetailActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.detail_avatar)
    ImageView mDetailAvatar;
    @BindView(R.id.detail_name)
    TextView mDetailName;
    @BindView(R.id.add_f)
    Button mAddF;
    @BindView(R.id.send_m)
    Button mSendM;
    String userName;

    Uri uri;
    private UploadManager uploadManager;
    final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        userName = this.getIntent().getStringExtra("username");
        mDetailName.setText(userName);

//        new AvatarURLDownloadUtils().downLoad(userName, this, mDetailAvatar,false);
        PersonAvatarUtils.setAvatar(this, userName, mDetailAvatar);

        Map<String, EaseUser> localUsers = MyApplication.getInstance().getContactList();
        if ((!localUsers.containsKey(userName)) && (!userName.equals(EMClient.getInstance().getCurrentUser()))) {
            mAddF.setVisibility(View.VISIBLE);
        }
        if (localUsers.containsKey(userName)) {
            mSendM.setVisibility(View.VISIBLE);
        }
        uploadManager = new UploadManager();
    }


    @OnClick({R.id.back, R.id.detail_avatar, R.id.detail_name, R.id.add_f, R.id.send_m})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.detail_avatar:
                //更换头像
                if (EMClient.getInstance().getCurrentUser().equals(userName)) {
                    pick();
                } else {
                    if (DBManager.getInstance().getAvatarInfo(userName) != null) {
                        DBManager.getInstance().updateAvatarInfo(userName, TimeUtils.getCurrentTimeAsNumber());
                    } else {
                        DBManager.getInstance().saveAvatarInfo(userName, TimeUtils.getCurrentTimeAsNumber());
                    }
                    PersonAvatarUtils.setAvatar(this, userName, mDetailAvatar);
                }
                break;
            case R.id.detail_name:
                //修改用户名
                break;
            case R.id.add_f:
                startActivity(new Intent(UserDetailActivity.this, AddContactActivity.class).putExtra("username", userName));
                finish();
                break;
            case R.id.send_m:
                startActivity(new Intent(UserDetailActivity.this, ChatActivity.class).putExtra("username", userName).putExtra("type", 1));
                finish();
                break;

        }
    }

    private void upload(Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide
                        .with(UserDetailActivity.this)
                        .load(uri)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mDetailAvatar);
            }
        });

        if (DBManager.getInstance().getAvatarInfo(userName) != null) {
            DBManager.getInstance().updateAvatarInfo(userName, TimeUtils.getCurrentTimeAsNumber());
        } else {
            DBManager.getInstance().saveAvatarInfo(userName, TimeUtils.getCurrentTimeAsNumber());
        }

        String token = QiniuTokenUtils.creatImageToken(userName);
//        mDetailAvatar.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //设置上传后文件的key
        String upkey = userName + ".png";
        uploadManager.put(data, upkey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo rinfo, JSONObject response) {
//
//                Log.i("MainActivity", "complete: " + key);
//                String info = "{\"URL\":\"" + "http://7xktkd.com1.z0.glb.clouddn.com/"+key +"?v="+ TimeUtils.getCurrentTimeAsNumber()+"\"}";
//                byte[] filedata = info.getBytes();
//                String fileName = userName + ".json";
//                String fileToken = QiniuTokenUtils.CreatJsonToken(userName);
//                uploadManager.put(filedata, fileName, fileToken, new UpCompletionHandler() {
//                    @Override
//                    public void complete(String key, ResponseInfo info, JSONObject response) {
//
//                    }
//                },null );
            }
        }, null);
    }

    private void pick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            //选择图片
            uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                if (bitmap != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap.recycle();
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
        }

        if (bitmap != null) {
            final Bitmap finalBitmap = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    upload(finalBitmap);
                }
            }).start();
        }
    }

}
