package com.mottc.chat.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import com.mottc.chat.Model.UserAvatarURL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/28
 * Time: 11:10
 */
public class AvatarURLDownloadUtils {
    public static String avatarURL;

    public static void downLoad(final String username, final Activity activity, final ImageView imageView, final boolean isGroup) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://7xktkd.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        QiniuService service = retrofit.create(QiniuService.class);

        Call<UserAvatarURL> repos = service.listRepos(username + ".json");
        repos.enqueue(new Callback<UserAvatarURL>() {
                          @Override
                          public void onResponse(Call<UserAvatarURL> call, Response<UserAvatarURL> response) {
                              if (response.isSuccessful()) {
                                  Log.i("JsonActivity", "onResponse: " + response.body().getURL());
                                  avatarURL = response.body().getURL();
                                  activity.runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (isGroup) {
                                              GroupAvatarUtils.setAvatar(activity, avatarURL, imageView);
                                          } else {
                                              Log.i("AvatarURLDownloadUtils", "run: " + "**************************" + avatarURL);
                                              PersonAvatarUtils.setAvatar(activity, avatarURL, imageView);
                                          }
                                      }
                                  });
                              }
                          }
                          @Override
                          public void onFailure(Call<UserAvatarURL> call, Throwable t) {
                              avatarURL = "http://7xktkd.com1.z0.glb.clouddn.com/" + username + ".png";
                          }
                      }
        );

        Log.i("AvatarURLDownloadUtils", "downLoad: " + avatarURL);



    }

    interface QiniuService {
        @GET("{user}")
        Call<UserAvatarURL> listRepos(@Path("user") String user);
    }
}
