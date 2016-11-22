package com.mottc.chat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.mottc.chat.MyApplication;
import com.mottc.chat.R;
import com.mottc.chat.db.EaseUser;

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
    ImageButton mAddF;

    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        userName = this.getIntent().getStringExtra("username");
        mDetailName.setText(userName);
        Map<String, EaseUser> localUsers = MyApplication.getInstance().getContactList();
        if ((!localUsers.containsKey(userName))&&(!userName.equals(EMClient.getInstance().getCurrentUser()))) {
            mAddF.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.back, R.id.detail_avatar, R.id.detail_name,R.id.add_f})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.detail_avatar:
                //更换头像
                break;
            case R.id.detail_name:
                //修改用户名
                break;
            case R.id.add_f:
                startActivity(new Intent(UserDetailActivity.this, AddContactActivity.class).putExtra("username", userName));
                break;
        }
    }

}
