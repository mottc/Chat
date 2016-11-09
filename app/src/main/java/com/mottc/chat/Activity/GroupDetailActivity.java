package com.mottc.chat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.detail_group_avatar)
    ImageView mDetailGroupAvatar;
    @BindView(R.id.detail_group_name)
    TextView mDetailGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
    }
}
