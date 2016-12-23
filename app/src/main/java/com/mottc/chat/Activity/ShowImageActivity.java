package com.mottc.chat.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mottc.chat.Model.ImageCache;
import com.mottc.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowImageActivity extends AppCompatActivity {

    @BindView(R.id.chat_image)
    ImageView mChatImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ButterKnife.bind(this);
        String thumbPath = this.getIntent().getStringExtra("thumbPath");
        Bitmap bitmap = ImageCache.getInstance().get(thumbPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            mChatImage.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.chat_image)
    public void onClick() {
        finish();
    }
}
