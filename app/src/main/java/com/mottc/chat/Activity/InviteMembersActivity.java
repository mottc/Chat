package com.mottc.chat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteMembersActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.invite_username)
    EditText mInviteUsername;
    @BindView(R.id.btn_invite)
    Button mBtnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_members);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.back, R.id.btn_invite})
    public void onClick(View view) {
        switch (view.getId()) {
           case R.id.back:
               finish();
                break;
            case R.id.btn_invite:
                String username = mInviteUsername.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: 2016/11/25 !!!!!
//                EMClient.getInstance().groupManager().addUsersToGroup(groupId, newmembers);//需异步处理
                break;
        }
    }
}
