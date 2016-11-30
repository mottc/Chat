package com.mottc.chat.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mottc.chat.Constant;
import com.mottc.chat.R;
import com.mottc.chat.db.DBManager;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/23
 * Time: 16:59
 */
public class PersonAvatarUtils {
    public static void setAvatar(Context context, String username, ImageView imageView) {

        String time = DBManager.getInstance().getAvatarInfo(username);
//        String Url = AvatarURLDownloadUtils.downLoad(username);
        String Url = Constant.AVATAR_URL + username + ".png?v=" + time;
        Log.i("PersonAvatarUtils", "setAvatar: " + Url);

        Glide
                .with(context)
                .load(Url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.mipmap.avatar)
                .into(imageView);
    }
}
