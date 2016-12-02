package com.mottc.chat.utils;

import android.content.Context;
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
import retrofit2.http.Query;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/28
 * Time: 11:10
 */
public class AvatarURLDownloadUtils {
    public String avatarURL;

    public void downLoad(final String username, final Context context, final ImageView imageView, final boolean isGroup) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://7xktkd.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        QiniuService service = retrofit.create(QiniuService.class);
        final Call<UserAvatarURL> repos = service.listRepos(username + ".json", TimeUtils.getCurrentTimeAsNumber());
        repos.enqueue(new Callback<UserAvatarURL>() {
                          @Override
                          public void onResponse(Call<UserAvatarURL> call, Response<UserAvatarURL> response) {
                              if (response.isSuccessful()) {
                                  Log.i("AvatarURLDownloadUtils", "onResponse: " + response.body().getURL());
                                  avatarURL = response.body().getURL();
                                  if (isGroup) {
                                      GroupAvatarUtils.setAvatar(context, avatarURL, imageView);
                                  } else {
                                      PersonAvatarUtils.setAvatar(context, avatarURL, imageView);
                                  }
                              }
                          }

                          @Override
                          public void onFailure(Call<UserAvatarURL> call, Throwable t) {
                              avatarURL = "http://7xktkd.com1.z0.glb.clouddn.com/" + username + ".png";
                          }
                      }
        );
    }

    interface QiniuService {
        @GET(value = "{user}")
        Call<UserAvatarURL> listRepos(@Path("user") String user, @Query("v") String sort);
    }
}