package com.mottc.chat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.Activity.Adapter.GroupMembersAdapter;
import com.mottc.chat.R;
import com.mottc.chat.utils.GroupAvatarUtils;

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



    EMGroup group = null;
    List<String> members;
    String owner;
    String groupId;
    String groupName;
    GroupMembersAdapter groupMembersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        init();
        mMembersList.setLayoutManager(new LinearLayoutManager(this));
        groupMembersAdapter = new GroupMembersAdapter(members, owner);
        mMembersList.setAdapter(groupMembersAdapter);
        mMembers.setText("群组成员列表(" + members.size() + ")");

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
        GroupAvatarUtils.setAvatar(this, groupName, mDetailGroupAvatar);
        //根据群组ID从服务器获取群组基本信息

        new Thread(new Runnable() {
            public void run() {
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
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

        if (group != null) {
            members = group.getMembers();//获取群成员
            owner = group.getOwner();//获取群主
        }
    }


    @OnClick({detail_group_avatar, R.id.detail_group_name, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case detail_group_avatar:
                break;
            case R.id.detail_group_name:
                break;
            case R.id.back:
                finish();
                break;
        }
    }

}
