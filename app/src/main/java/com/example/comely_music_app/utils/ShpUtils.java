package com.example.comely_music_app.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import com.example.comely_music_app.MainActivity;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Objects;

public class ShpUtils {

    public static UserInfo getCurrentUserinfoFromShp(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }

    public static void writeCurrentUserinfoToShp(Activity mActivity, UserInfo userInfo) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        String newInfoStr = gson.toJson(userInfo);
        editor.putString(ShpConfig.CURRENT_USER, newInfoStr);
        editor.apply();
    }

    public static List<PlaylistModel> getMyCreatePlaylistFromShp(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String myCreatePlaylistStr = shp.getString(ShpConfig.MY_CREATE_PLAYLIST, "");
        if (!myCreatePlaylistStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(myCreatePlaylistStr, new TypeToken<List<PlaylistModel>>() {
            }.getType());
        }
        return null;
    }

    public static void writeMyCreatePlaylistToShp(Activity mActivity, List<PlaylistModel> list) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        String myCreatedPlaylistStr = gson.toJson(list);
        editor.putString(ShpConfig.MY_CREATE_PLAYLIST, myCreatedPlaylistStr);
        editor.apply();
    }


    public static PlaylistDetailsModel getPlaylistDetailsFromShpByPlaylistName(Activity mActivity, String playlistName) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String playlistDetailsStr = shp.getString(ShpConfig.PLAYLIST_DETAILS + playlistName, "");
        if (!playlistDetailsStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(playlistDetailsStr, new TypeToken<PlaylistDetailsModel>() {
            }.getType());
        }
        return null;
    }

    public static void writePlaylistDetailsIntoShp(Activity mActivity, PlaylistDetailsModel detailsModel) {
        if (detailsModel != null && detailsModel.getPlaylistInfo() != null) {
            SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            Gson gson = new Gson();
            String str = gson.toJson(detailsModel);
            editor.putString(ShpConfig.PLAYLIST_DETAILS + detailsModel.getPlaylistInfo().getName(), str);
            editor.apply();
        }
    }

    public static PlaylistDetailsModel getMyLikePlaylistDetailsFromShp(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String playlistDetailsStr = shp.getString(ShpConfig.MY_LIKE_PLAYLIST, "");
        if (!playlistDetailsStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(playlistDetailsStr, new TypeToken<PlaylistDetailsModel>() {
            }.getType());
        }
        return null;
    }

    public static void writeMyLikePlaylistDetailsIntoShp(Activity mActivity, PlaylistDetailsModel myLikeDetails) {
        if (myLikeDetails == null) {
            return;
        }
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        if (myLikeDetails.getPlaylistInfo() == null) {
            PlaylistModel playlistInfo = new PlaylistModel();
            UserInfo userinfo = getCurrentUserinfoFromShp(mActivity);
            if (userinfo != null) {
                playlistInfo.setName(userinfo.getUsername() + "的喜欢歌单");
                playlistInfo.setCreatedUserNickname(userinfo.getNickname());
            }
            myLikeDetails.setPlaylistInfo(playlistInfo);
        }
        String str = gson.toJson(myLikeDetails);
        editor.putString(ShpConfig.MY_LIKE_PLAYLIST, str);
        editor.apply();
    }

    // ============================= clear =================================

    public static void clearCurrentUserInfo(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(ShpConfig.CURRENT_USER, "");
        editor.apply();
    }

    public static void clearCreatedPlaylist(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(ShpConfig.MY_CREATE_PLAYLIST, "");
        editor.apply();
    }

    public static void clearAllCreatedPlaylistDetails(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        List<PlaylistModel> playlistModels = getMyCreatePlaylistFromShp(mActivity);
        if (playlistModels != null) {
            for (PlaylistModel model : playlistModels) {
                editor.putString(ShpConfig.PLAYLIST_DETAILS + model.getName(), "");
            }
        }
        editor.apply();
    }

    public static void clearMylikePlaylist(Activity mActivity) {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(ShpConfig.MY_LIKE_PLAYLIST, "");
        editor.apply();
    }
}
