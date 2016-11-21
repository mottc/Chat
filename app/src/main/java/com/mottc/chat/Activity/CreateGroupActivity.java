package com.mottc.chat.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.R;
import com.mottc.chat.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.groupName)
    EditText mGroupName;
    @BindView(R.id.groupIntroduce)
    EditText mGroupIntroduce;
    @BindView(R.id.create)
    Button mCreate;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
    }


    @OnClick({R.id.back, R.id.create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.create:
                String groupName = mGroupName.getText().toString().trim();
                String desc = mGroupIntroduce.getText().toString().trim();
                String reason = EMClient.getInstance().getCurrentUser() + "邀请加入群" + groupName;
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(this, "群组名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("正在创建群组");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    createNewGroup(groupName, desc, new String[0], reason);
                }
                break;
        }
    }

    private void createNewGroup(final String groupName, final String desc, final String[] allMembers, final String reason) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 200;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
                    group.getGroupId();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this, "创建群组失败"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*点击非键盘区，键盘落下*/
        DisplayUtils.hideInputWhenTouchOtherView(this, event, null);
        return super.dispatchTouchEvent(event);
    }

    public void back(View v) {
        finish();
    }
}
