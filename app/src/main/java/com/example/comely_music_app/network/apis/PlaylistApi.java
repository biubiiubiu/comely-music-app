package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.PlaylistInfoWithMusicListResponse;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaylistApi {
    @POST("generate/playlist/create")
    Observable<BaseResult<Void>> createPlaylist(@Body PlaylistCreateRequest request);

    @POST("generate/playlist/delete")
    Observable<BaseResult<Void>> deletePlaylist(@Body PlaylistSelectRequest request);

    @POST("generate/playlist/update")
    Observable<BaseResult<Void>> updatePlaylist(@Body PlaylistUpdateRequest request);

    @GET("generate/playlist/select-created-playlists/{username}")
    Observable<BaseResult<UserPlaylistsSelectResponse>> selectAllCreatedPlaylistByUsername(@Path("username") String username);

    @POST("generate/playlist/select-playlist-with-music-list")
    Observable<BaseResult<PlaylistInfoWithMusicListResponse>> selectPlaylistWithMusicList(@Body PlaylistSelectRequest request);

    @POST("generate/playlist/add-music-into-playlist")
    Observable<BaseResult<Void>> addMusicToPlaylist(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/delete-music-from-playlist")
    Observable<BaseResult<Void>> deleteMusicFromPlaylist(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/add-music-into-my-like")
    Observable<BaseResult<Void>> addMusicToMylike(@Body PlaylistMusicAddRequest request);
}
