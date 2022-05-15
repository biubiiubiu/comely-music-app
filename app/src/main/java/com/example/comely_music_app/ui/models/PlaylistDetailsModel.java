package com.example.comely_music_app.ui.models;

import java.util.List;

import lombok.Data;

@Data
public class PlaylistDetailsModel {
    private PlaylistModel playlistInfo;
    private List<MusicModel> musicModelList;
}
