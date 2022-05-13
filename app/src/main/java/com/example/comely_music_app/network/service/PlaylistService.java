package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;

public interface PlaylistService {
    void createPlaylist(PlaylistCreateRequest request);

    void deletePlaylist(PlaylistSelectRequest request);

    void selectAllCreatedPlaylistByUsername(String username);
}
