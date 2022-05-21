package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.MusicSelectResponse;
import com.example.comely_music_app.network.response.PlaylistInfoWithMusicListResponse;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;

import java.util.List;

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
    Observable<BaseResult<MusicSelectResponse>> addMusicToPlaylist(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/delete-music-from-playlist")
    Observable<BaseResult<Void>> deleteMusicFromPlaylist(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/add-music-into-my-like")
    Observable<BaseResult<MusicSelectResponse>> addMusicToMylike(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/remove-music-from-my-like")
    Observable<BaseResult<MusicSelectResponse>> removeMusicFromMylike(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/add-music-into-recently-play")
    Observable<BaseResult<MusicSelectResponse>> addMusicToRecentlyPlay(@Body PlaylistMusicAddRequest request);

    @POST("generate/playlist/remove-music-from-recently-play")
    Observable<BaseResult<MusicSelectResponse>> removeMusicFromRecentlyPlay(@Body PlaylistMusicAddRequest request);

    @GET("generate/playlist/fuzzy-search-playlist/{searchContent}")
    Observable<BaseResult<List<PlaylistInfoWithMusicListResponse>>> fuzzySearchPlaylist(@Path("searchContent") String searchContent);
}
