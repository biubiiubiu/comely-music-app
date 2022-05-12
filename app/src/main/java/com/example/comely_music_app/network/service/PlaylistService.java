package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.PlaylistCreateRequest;

public interface PlaylistService {
    void createPlaylist(PlaylistCreateRequest request);

    void selectAllCreatedPlaylistByUsername(String username);
}
