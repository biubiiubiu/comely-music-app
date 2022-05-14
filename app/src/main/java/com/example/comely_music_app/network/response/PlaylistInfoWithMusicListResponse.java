package com.example.comely_music_app.network.response;

import com.example.comely_music_app.ui.models.PlaylistModel;

import java.util.List;

import lombok.Data;

@Data
public class PlaylistInfoWithMusicListResponse {
    private PlaylistModel playlistInfo;
    private List<MusicSelectResponse.MusicInfo> musicInfoList;
}
