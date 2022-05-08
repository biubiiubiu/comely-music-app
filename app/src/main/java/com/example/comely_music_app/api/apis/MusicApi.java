package com.example.comely_music_app.api.apis;

import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.MusicCreateRequest;
import com.example.comely_music_app.api.request.MusicSelectRequest;
import com.example.comely_music_app.api.response.MusicBatchCreateResponse;
import com.example.comely_music_app.api.response.MusicSelectResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MusicApi extends BaseApi{
    @POST("generate/music/get-list")
    Observable<BaseResult<MusicSelectResponse>> getMusicListByModule(@Body MusicSelectRequest musicSelectRequest);

    @POST("generate/music/batch-create")
    Observable<BaseResult<MusicBatchCreateResponse>> batchCreateMusic(@Body List<MusicCreateRequest> requestsList);
}
