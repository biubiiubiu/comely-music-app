package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.ArtistCreateRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ArtistApi {
    @POST("generate/artist/create")
    Observable<BaseResult<Void>> createArtist(@Body ArtistCreateRequest artistCreateRequest);
}
