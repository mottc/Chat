package com.mottc.chat.utils;

import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/30
 * Time: 16:33
 */
public class ImageUtils extends com.hyphenate.util.ImageUtils{

    public static String getImagePath(String remoteUrl){
        String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

}