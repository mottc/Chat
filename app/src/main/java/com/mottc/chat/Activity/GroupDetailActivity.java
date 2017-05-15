package com.mottc.chat.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.Activity.Adapter.GroupMembersAdapter;
import com.mottc.chat.R;
import com.mottc.chat.utils.GroupAvatarUtils;
import com.mottc.chat.utils.QiniuTokenUtils;
import com.mottc.chat.utils.TimeUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mottc.chat.R.id.detail_group_avatar;

public class GroupDetailActivity extends AppCompatActivity {


    @BindView(detail_group_avatar)
    ImageView mDetailGroupAvatar;
    @BindView(R.id.detail_group_name)
    TextView mDetailGroupName;
    @BindView(R.id.members)
    TextView mMembers;
    @BindView(R.id.detail_group_id)
    TextView mDetailGroupId;
    @BindView(R.id.members_list)
    RecyclerView mMembersList;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.invite)
    ImageView mInvite;


    EMGroup group = null;
    List<String> members;
    String owner;
    String groupId;
    String groupName;
    GroupMembersAdapter groupMembersAdapter;
    Uri uri;
    private UploadManager uploadManager;
    final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        init();
        uploadManager = new UploadManager();
        mMembersList.setLayoutManager(new LinearLayoutManager(this));

        groupMembersAdapter = new GroupMembersAdapter(members, owner);
        mMembersList.setAdapter(groupMembersAdapter);
        mMembers.setText("群组成员列表(" + members.size() + ")");
        if (EMClient.getInstance().getCurrentUser().equals(owner)) {
            mInvite.setVisibility(View.VISIBLE);
        }
        groupMembersAdapter.setOnGroupMembersListClickListener(new GroupMembersAdapter.OnGroupMembersListClickListener() {
            @Override
            public void OnGroupMembersListClick(String item) {
                startActivity(new Intent(GroupDetailActivity.this, UserDetailActivity.class).putExtra("username", item));
            }
        });
    }

    private void init() {
        groupId = this.getIntent().getStringExtra("groupId");
        mDetailGroupId.setText(groupId);
        groupName = EMClient.getInstance().groupManager().getGroup(groupId).getGroupName();
        mDetailGroupName.setText(groupName);

        GroupAvatarUtils.setAvatar(this, groupId, mDetailGroupAvatar);
        //根据群组ID从服务器获取群组基本信息


        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
           e.printStackTrace();
        }
////      本地获取群组信息
        group = EMClient.getInstance().groupManager().getGroup(groupId);


        if (group != null) {
            members = group.getMembers();//获取群成员
            owner = group.getOwner();//获取群主
        }

    }


    @OnClick({detail_group_avatar, R.id.detail_group_name, R.id.back, R.id.invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case detail_group_avatar:
                if (EMClient.getInstance().getCurrentUser().equals(owner)) {
                    pick();
                } else {
                    if (DBManager.getInstance().getAvatarInfo(groupId) != null) {
                        DBManager.getInstance().updateAvatarInfo(groupId, TimeUtils.getCurrentTimeAsNumber());
                    } else {
                        DBManager.getInstance().saveAvatarInfo(groupId, TimeUtils.getCurrentTimeAsNumber());
                    }
                    GroupAvatarUtils.setAvatar(this, groupId, mDetailGroupAvatar);
                }
                break;
            case R.id.invite:
                startActivity(new Intent(this, InviteMembersActivity.class).putExtra("groupId", groupId));
                break;

            case R.id.detail_group_name:
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    private void upload(Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide
                        .with(GroupDetailActivity.this)
                        .load(uri)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mDetailGroupAvatar);
            }
        });
        if (DBManager.getInstance().getAvatarInfo(groupId) != null) {
            DBManager.getInstance().updateAvatarInfo(groupId, TimeUtils.getCurrentTimeAsNumber());
        } else {
            DBManager.getInstance().saveAvatarInfo(groupId, TimeUtils.getCurrentTimeAsNumber());
        }

        String token = QiniuTokenUtils.creatImageToken(groupId);
//        mDetailAvatar.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //设置上传后文件的key
        String upkey = groupId + ".png";
        uploadManager.put(data, upkey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo rinfo, JSONObject response) {

//
//                Log.i("MainActivity", "complete: " + key);
//                String info = "{\"URL\":\"" + "http://7xktkd.com1.z0.glb.clouddn.com/" + key + "?v=" + TimeUtils.getCurrentTimeAsNumber() + "\"}";
//                byte[] filedata = info.getBytes();
//                String fileName = groupId + ".json";
//                String fileToken = QiniuTokenUtils.CreatJsonToken(groupId);
//                uploadManager.put(filedata, fileName, fileToken, new UpCompletionHandler() {
//                    @Override
//                    public void complete(String key, ResponseInfo info, JSONObject response) {
//
//                    }
//                }, null);

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
