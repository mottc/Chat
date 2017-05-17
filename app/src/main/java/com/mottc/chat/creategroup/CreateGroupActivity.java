package com.mottc.chat.creategroup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.R;
import com.mottc.chat.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity implements CreateGroupContract.View{

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
    private CreateGroupContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        mPresenter = new CreateGroupPresenter(this);
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
                mPresenter.createGroup(groupName, desc);
                break;
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
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

    @Override
    public void showGroupNameIsEmpty() {
        Toast.makeText(this, "群组名称不能为空", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showSuccess() {

        Toast.makeText(CreateGroupActivity.this, "创建成功", Toast.LENGTH_LONG).show();

    }

    @Override
    public void showFailure(String error) {

        Toast.makeText(CreateGroupActivity.this, "创建群组失败"+error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在创建群组");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public void dialogDismiss() {

        progressDialog.dismiss();
    }
}
