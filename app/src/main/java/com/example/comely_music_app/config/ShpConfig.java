package com.example.comely_music_app.config;

public class ShpConfig {
    public final static String SHP_NAME = "MY_DATA";

    // 存userinfo
    public final static String CURRENT_USER = "CURRENT_USER";

    // 存个人创建的歌单列表，这里也可以每次都请求网络，但是用shp可以在没联网的情况也能展示
    public final static String MY_CREATE_PLAYLIST = "MY_CREATE_PLAYLIST";

    // 存个人创建的歌单详情信息，这里也可以每次都请求网络，但是用shp可以弥补网络查询速度慢的弱点
    // 使用时，用PLAYLIST_DETAILS+playlistName作为key
    public final static String PLAYLIST_DETAILS = "PLAYLIST_DETAILS";
}
