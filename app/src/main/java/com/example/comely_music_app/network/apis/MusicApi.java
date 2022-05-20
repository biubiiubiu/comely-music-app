package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.MusicCreateRequest;
import com.example.comely_music_app.network.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.network.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.network.response.MusicBatchCreateResponse;
import com.example.comely_music_app.network.response.MusicSelectResponse;
import com.example.comely_music_app.network.response.OssTokenInfo;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MusicApi extends BaseApi{
    @POST("generate/music/get-list")
    Observable<BaseResult<MusicSelectResponse>> getMusicListByModule(@Body MusicSelectByModuleRequest musicSelectByModuleRequest);

    @POST("generate/music/batch-create")
    Observable<BaseResult<MusicBatchCreateResponse>> batchCreateMusic(@Body List<MusicCreateRequest> requestsList);

    @POST("generate/music/get-list-by-tags")
    Observable<BaseResult<MusicSelectResponse>> getMusicListByTags(@Body MusicSelectByTagsRequest musicSelectByTagsRequest);

    @GET("generate/music/fuzzy-search-name/{name}")
    Observable<BaseResult<MusicSelectResponse>> fuzzySearchMusic(@Path("name") String searchContent);

    @GET("generate/music/fuzzy-search-name-limit/{name}")
    Observable<BaseResult<MusicSelectResponse>> fuzzySearchMusicLimit(@Path("name") String searchContent);
}
