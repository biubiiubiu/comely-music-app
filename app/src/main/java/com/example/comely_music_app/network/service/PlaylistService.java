package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;

public interface PlaylistService {
    void createPlaylist(PlaylistCreateRequest request);

    void deletePlaylist(PlaylistSelectRequest request);

    void selectAllCreatedPlaylistByUsername(String username);

    void updatePlaylist(PlaylistUpdateRequest request);

    void selectPlaylistDetailsByScene(PlaylistSelectRequest request, PlaylistSelectScene scene);

    void deleteMusicFromPlaylist(PlaylistMusicAddRequest request);
}
