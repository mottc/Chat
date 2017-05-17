package com.mottc.chat.invitemembers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteMembersActivity extends AppCompatActivity implements InviteMembersContract.View {


    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.invite_username)
    EditText mInviteUsername;
    @BindView(R.id.btn_invite)
    Button mBtnInvite;


    private InviteMembersContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_members);
        ButterKnife.bind(this);
        mPresenter = new InviteMembersPresenter(this);
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @OnClick({R.id.back, R.id.btn_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_invite:
                String username = mInviteUsername.getText().toString().trim();
                mPresenter.inviteMembers(username);
                break;
        }
    }

    @Override
    public void showUsernameIsEmpty() {
        Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserHasBeenInGroup() {
        Toast.makeText(this, "该用户已是群组成员", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void inviteSuccess() {
        Toast.makeText(this, "成功发出邀请", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void inviteFailure() {
        Toast.makeText(this, "发出邀请失败，请稍后重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError() {
        Toast.makeText(this, "发生错误：请重启软件！", Toast.LENGTH_SHORT).show();
    }
}
