package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.TagCreateRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TagApi {
    @POST("/generate/entityTag/create")
    Observable<BaseResult<Void>> createTag(@Body TagCreateRequest request);

    @POST("/generate/entityTag/batch-create")
    Observable<BaseResult<Void>> batchCreateTag(@Body List<TagCreateRequest> requests);
}
