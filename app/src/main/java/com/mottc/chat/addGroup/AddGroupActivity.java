package com.mottc.chat.addGroup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.R;
import com.mottc.chat.utils.DisplayUtils;
import com.mottc.chat.utils.GroupAvatarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGroupActivity extends AppCompatActivity implements AddGroupContact.View{

    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.group_num)
    EditText mGroupNum;
    @BindView(R.id.search_view)
    LinearLayout mSearchView;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.add)
    Button mAdd;
    @BindView(R.id.rl_searched_group)
    RelativeLayout mRlSearchedGroup;
    @BindView(R.id.search_for_group)
    Button mSearchForGroup;
    @BindView(R.id.avatar)
    ImageView mAvatar;


    private AddGroupContact.Presenter mPresenter;
    private String groupNum;
    private ProgressDialog pd;

    private Boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
        mPresenter = new AddGroupPresenter(this);
    }


    @OnClick({R.id.back, R.id.search_for_group, R.id.add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (isVisible) {
                    mRlSearchedGroup.setVisibility(View.GONE);
                    mSearchView.setVisibility(View.VISIBLE);
                    isVisible = false;
                } else {
                    finish();
                }
                break;
            case R.id.search_for_group:
                groupNum = mGroupNum.getText().toString().trim();
                mPresenter.searchGroup(groupNum);
                break;
            case R.id.add:
                mPresenter.applyJoinToGroup(groupNum);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (isVisible) {
                mRlSearchedGroup.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);
                isVisible = false;
                return false;
            } else {
                finish();
                return false;
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void showAskSuccess() {
        Toast.makeText(AddGroupActivity.this, "请求已发送，等待同意", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAskFailure(String error) {
        Toast.makeText(AddGroupActivity.this, "加入群组失败" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGroupNumIsEmpty() {

        Toast.makeText(this, "群号不能为空", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSearchDialog() {

        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.searching));
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void searchDialogDismiss() {
        pd.dismiss();
    }

    @Override
    public void canNotFindGroup() {

        Toast.makeText(this, getResources().getString(R.string.group_not_existed), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void findGroupFailure() {

        Toast.makeText(this, getResources().getString(R.string.group_search_failed) + " : " + getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAskDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage("正在发送请求");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    @Override
    public void askDialogDismiss() {
        pd.dismiss();
    }

    @Override
    public void showSearchedLayout(EMGroup value) {
        mRlSearchedGroup.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
        isVisible = true;
        mName.setText(value.getGroupName());
        GroupAvatarUtils.setAvatar(this, groupNum, mAvatar);
    }
}
