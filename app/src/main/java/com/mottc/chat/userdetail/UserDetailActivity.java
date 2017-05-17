package com.mottc.chat.userdetail;

import android.content.Intent;
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
import com.mottc.chat.ChatApplication;
import com.mottc.chat.R;
import com.mottc.chat.addcontact.AddContactActivity;
import com.mottc.chat.chat.ChatActivity;
import com.mottc.chat.utils.AvatarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDetailActivity extends AppCompatActivity implements UserDetailContract.View {


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


    private String userName;
    private UserDetailContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        mPresenter = new UserDetailPresenter(this);

        userName = this.getIntent().getStringExtra("username");
        mDetailName.setText(userName);
        AvatarUtils.setPersonAvatar(this, userName, mDetailAvatar);

        mPresenter.isFriend(userName);
    }


    @OnClick({R.id.back, R.id.detail_avatar, R.id.detail_name, R.id.add_f, R.id.send_m})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.detail_avatar:
                //更换头像
                if (ChatApplication.getInstance().getCurrentUserName().equals(userName)) {
                    mPresenter.pickPic();
                } else {
                    mPresenter.updateAvatarInfo(userName);
                    AvatarUtils.setPersonAvatar(this, userName, mDetailAvatar);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onPickResult(resultCode, data);
    }

    @Override
    public void showNotPickPic() {
        Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setAddVisible() {
        mAddF.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSendVisible() {
        mSendM.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNewAvatar(Uri uri) {
        Glide
                .with(UserDetailActivity.this)
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mDetailAvatar);
    }

    @Override
    public void showUploadSuccess() {
        Toast.makeText(this, "头像上传成功！", Toast.LENGTH_SHORT).show();

    }
}
