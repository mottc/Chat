package com.mottc.chat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupDetailActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.detail_group_avatar)
    ImageView mDetailGroupAvatar;
    @BindView(R.id.detail_group_name)
    TextView mDetailGroupName;
    @BindView(R.id.members)
    TextView mMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.back, R.id.detail_group_avatar, R.id.detail_group_name, R.id.members})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.detail_group_avatar:

                break;
            case R.id.detail_group_name:
                break;
            case R.id.members:
                break;
        }
    }
}
