package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.MusicModel;

import java.util.List;

public interface PlaylistService {
    void createPlaylist(PlaylistCreateRequest request);

    void deletePlaylist(PlaylistSelectRequest request);

    void selectAllCreatedPlaylistByUsername(String username);

    void updatePlaylist(PlaylistUpdateRequest request);

    void selectPlaylistDetailsByScene(PlaylistSelectRequest request, PlaylistSelectScene scene);

    void deleteMusicFromPlaylist(PlaylistMusicAddRequest request);

    void addMusicIntoMyLike(PlaylistMusicAddRequest request);

    void removeMusicFromMyLike(PlaylistMusicAddRequest request);

    void addMusicIntoRecentlyPlay(PlaylistMusicAddRequest request);

    void removeMusicFromRecentlyPlay(PlaylistMusicAddRequest request);

    List<PlaylistMusicAddRequest.MusicAddInfo> transMusicModel2AddInfos(List<MusicModel> musicModels);
}
