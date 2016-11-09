package com.mottc.chat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDetailActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton mBack;
    @BindView(R.id.detail_avatar)
    ImageView mDetailAvatar;
    @BindView(R.id.detail_name)
    TextView mDetailName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
    }
}
