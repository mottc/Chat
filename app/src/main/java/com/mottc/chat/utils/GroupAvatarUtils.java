package com.mottc.chat.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mottc.chat.R;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/23
 * Time: 18:01
 */
public class GroupAvatarUtils {
    public static void setAvatar(Context context, String groupId, ImageView imageView){

        String Url = "http://7xktkd.com1.z0.glb.clouddn.com/" + groupId + ".png";
        Glide
                .with(context)
                .load(Url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.drawable.group_icon)
                .into(imageView);
    }
}
