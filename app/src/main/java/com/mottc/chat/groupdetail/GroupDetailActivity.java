package com.mottc.chat.groupdetail;

import android.content.Intent;
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
import com.mottc.chat.ChatApplication;
import com.mottc.chat.R;
import com.mottc.chat.invitemembers.InviteMembersActivity;
import com.mottc.chat.userdetail.UserDetailActivity;
import com.mottc.chat.utils.AvatarUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mottc.chat.R.id.detail_group_avatar;
import static com.mottc.chat.R.id.members;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailContract.View {


    @BindView(detail_group_avatar)
    ImageView mDetailGroupAvatar;
    @BindView(R.id.detail_group_name)
    TextView mDetailGroupName;
    @BindView(members)
    TextView mMembers;
    @BindView(R.id.detail_group_id)
    TextView mDetailGroupId;
    @BindView(R.id.members_list)
    RecyclerView mMembersList;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.invite)
    ImageView mInvite;


    private GroupMembersAdapter groupMembersAdapter;
    private GroupDetailContract.Presenter mPresenter;
    public String groupOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        mPresenter = new GroupDetailPresenter(this);
        mPresenter.start();
        mMembersList.setLayoutManager(new LinearLayoutManager(this));
    }


    @OnClick({detail_group_avatar, R.id.detail_group_name, R.id.back, R.id.invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_group_avatar:
                if (ChatApplication.getInstance().getCurrentUserName().equals(groupOwner)) {
                    mPresenter.pickPic();
                }
                break;
            case R.id.invite:
                mPresenter.go2Invite();
                break;
            case R.id.detail_group_name:
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onPickResult(resultCode, data);
    }


    @Override
    public void setGroupId(String groupId) {
        mDetailGroupId.setText(groupId);
    }

    @Override
    public void setGroupName(String groupName) {
        mDetailGroupName.setText(groupName);
    }

    @Override
    public void setGroupAvatar(String groupId) {
        AvatarUtils.setGroupAvatar(this, groupId, mDetailGroupAvatar);
    }

    @Override
    public void setGroupSize(int size) {
        mMembers.setText("群组成员列表(" + size + ")");
    }

    @Override
    public void showInvite() {
        mInvite.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNotPickPic() {
        Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void go2Invite(String groupId) {
        startActivity(new Intent(this, InviteMembersActivity.class).putExtra("groupId", groupId));
    }

    @Override
    public void showNewAvatar(Uri uri) {
        Glide
                .with(GroupDetailActivity.this)
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mDetailGroupAvatar);
    }

    @Override
    public void showUploadSuccess() {
        Toast.makeText(this, "头像上传成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addGroupMembers(List<String> groupMembers) {
        groupMembersAdapter = new GroupMembersAdapter(groupOwner);
        mMembersList.setAdapter(groupMembersAdapter);

        groupMembersAdapter.setOnGroupMembersListClickListener(new GroupMembersAdapter.OnGroupMembersListClickListener() {
            @Override
            public void OnGroupMembersListClick(String item) {
                startActivity(new Intent(GroupDetailActivity.this, UserDetailActivity.class).putExtra("username", item));
            }
        });
        groupMembersAdapter.addAllMembers(groupMembers);
    }
}
