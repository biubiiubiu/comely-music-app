package com.example.comely_music_app.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Objects;

public class ShpUtils {
    private Activity mActivity;

    public ShpUtils(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void release() {
        // 释放引用
        this.mActivity = null;
    }

    public UserInfo getCurrentUserinfoFromShp() {
        if (mActivity == null) {
            return null;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }

    public void writeCurrentUserinfoToShp(UserInfo userInfo) {
        if (mActivity == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        String newInfoStr = gson.toJson(userInfo);
        editor.putString(ShpConfig.CURRENT_USER, newInfoStr);
        editor.apply();
    }

    public List<PlaylistModel> getMyCreatePlaylistFromShp() {
        if (mActivity == null) {
            return null;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String myCreatePlaylistStr = shp.getString(ShpConfig.MY_CREATE_PLAYLIST, "");
        if (!myCreatePlaylistStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(myCreatePlaylistStr, new TypeToken<List<PlaylistModel>>() {
            }.getType());
        }
        return null;
    }

    public void writeMyCreatePlaylistToShp(List<PlaylistModel> list) {
        if (mActivity == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        String myCreatedPlaylistStr = gson.toJson(list);
        editor.putString(ShpConfig.MY_CREATE_PLAYLIST, myCreatedPlaylistStr);
        editor.apply();
    }


    public PlaylistDetailsModel getPlaylistDetailsFromShpByPlaylistName(String playlistName) {
        if (mActivity == null) {
            return null;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String playlistDetailsStr = shp.getString(ShpConfig.PLAYLIST_DETAILS + playlistName, "");
        if (!playlistDetailsStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(playlistDetailsStr, new TypeToken<PlaylistDetailsModel>() {
            }.getType());
        }
        return null;
    }

    public void writePlaylistDetailsIntoShp(PlaylistDetailsModel detailsModel) {
        if (mActivity == null) {
            return;
        }
        if (detailsModel != null && detailsModel.getPlaylistInfo() != null) {
            SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            Gson gson = new Gson();
            String str = gson.toJson(detailsModel);
            editor.putString(ShpConfig.PLAYLIST_DETAILS + detailsModel.getPlaylistInfo().getName(), str);
            editor.apply();
        }
    }

    // ============================= clear =================================

    public void clearCurrentUserInfo() {
        if (mActivity == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(ShpConfig.CURRENT_USER, "");
        editor.apply();
    }

    public void clearCreatedPlaylist() {
        if (mActivity == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(ShpConfig.MY_CREATE_PLAYLIST, "");
        editor.apply();
    }

    public void clearAllCreatedPlaylistDetails() {
        if (mActivity == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        List<PlaylistModel> playlistModels = getMyCreatePlaylistFromShp();
        if (playlistModels != null) {
            for (PlaylistModel model : playlistModels) {
                editor.putString(ShpConfig.PLAYLIST_DETAILS + model.getName(), "");
            }
        }
        editor.apply();
    }
}
