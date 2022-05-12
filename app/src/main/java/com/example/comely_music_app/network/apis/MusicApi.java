package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.MusicCreateRequest;
import com.example.comely_music_app.network.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.network.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.network.response.MusicBatchCreateResponse;
import com.example.comely_music_app.network.response.MusicSelectResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MusicApi extends BaseApi{
    @POST("generate/music/get-list")
    Observable<BaseResult<MusicSelectResponse>> getMusicListByModule(@Body MusicSelectByModuleRequest musicSelectByModuleRequest);

    @POST("generate/music/batch-create")
    Observable<BaseResult<MusicBatchCreateResponse>> batchCreateMusic(@Body List<MusicCreateRequest> requestsList);

    @POST("generate/music/get-list-by-tags")
    Observable<BaseResult<MusicSelectResponse>> getMusicListByTags(@Body MusicSelectByTagsRequest musicSelectByTagsRequest);
}
