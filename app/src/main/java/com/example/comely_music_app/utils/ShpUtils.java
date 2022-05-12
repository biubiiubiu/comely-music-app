package com.example.comely_music_app.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.config.ShpConfig;
import com.google.gson.Gson;

import java.util.Objects;

public class ShpUtils {
    public static UserInfo getUserInfoFromShp(Activity activity) {
        SharedPreferences shp = Objects.requireNonNull(activity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }
}
