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
 * Time: 16:59
 */
public class PersonAvatarUtils {
    public static void setAvatar(Context context, String username, ImageView imageView){

//        String Url = AvatarURLDownloadUtils.downLoad(username);
        String Url = "http://7xktkd.com1.z0.glb.clouddn.com/"+username+".png";
        Glide
                .with(context)
                .load(Url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.mipmap.avatar)
                .into(imageView);
    }
}
