package com.mottc.chat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.R;

import java.util.List;

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
    EMGroup group;
    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_members);
        ButterKnife.bind(this);
        groupId = this.getIntent().getStringExtra("groupId");

        group = EMClient.getInstance().groupManager().getGroup(groupId);


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
                    break;
                }
                if (group != null) {
                    List<String> members = group.getMembers();
                    Log.i("InviteMembersActivity", "onClick: " + "空");
                    if (members.contains(username)) {
                        Toast.makeText(this, "该用户已是群组成员", Toast.LENGTH_SHORT).show();
                    } else {
                        final String[] newMembers = new String[]{username};

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EMClient.getInstance().groupManager().addUsersToGroup(groupId, newMembers);//需异步处理
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(this, "发生错误：请重启软件！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
