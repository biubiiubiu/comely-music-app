package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
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

    @GET("generate/userPlaylist/select-created-playlists/{username}")
    Observable<BaseResult<UserPlaylistsSelectResponse>> selectAllCreatedPlaylistByUsername(@Path("username") String username);

    @POST("generate/playlist/delete")
    Observable<BaseResult<Void>> deletePlaylist(@Body PlaylistSelectRequest request);
}
