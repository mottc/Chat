package com.mottc.chat.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mottc.chat.Constant;
import com.mottc.chat.R;
import com.mottc.chat.data.Model;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/23
 * Time: 16:59
 */
public class AvatarUtils {
    public static void setPersonAvatar(Context context, String username, ImageView imageView) {

        String time = Model.getInstance().getAvatarInfo(username);
        String Url = Constant.AVATAR_URL + username + ".png?v=" + time;
        Glide
                .with(context)
                .load(Url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.mipmap.avatar)
                .into(imageView);
    }

    public static void setGroupAvatar(Context context, String groupId, ImageView imageView){
        String Url = Constant.AVATAR_URL + groupId + ".png";
        Glide
                .with(context)
                .load(Url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.group_icon)
                .into(imageView);
    }
}
