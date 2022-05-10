package com.example.comely_music_app.api.service.impl;

import android.util.Log;

import com.example.comely_music_app.api.apis.TagApi;
import com.example.comely_music_app.api.base.ApiManager;
import com.example.comely_music_app.api.base.BaseObserver;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.TagCreateRequest;
import com.example.comely_music_app.api.service.TagService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class TagServiceImpl implements TagService {
    private final TagApi tagApi;

    public TagServiceImpl() {
        tagApi = ApiManager.getInstance().getApiService(TagApi.class);
    }

    @Override
    public void createTag(TagCreateRequest request) {
        if (request.getTagName() == null || request.getTagName().length() == 0 || request.getType() == null
                || request.getEntityName() == null || request.getEntityName().length() == 0) {
            Log.d("createTag", "非法请求！");
            return;
        }
        Observable<BaseResult<Void>> result = tagApi.createTag(request);
        result.subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                Log.d("createTag", "onSuccess: 创建成功" + request.getTagName()
                        + request.getEntityName() + request.getType().toString());
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void batchCreateTag(List<TagCreateRequest> requests) {
        if (requests == null || requests.size() == 0) {
            Log.d("createTag", "非法请求！");
            return;
        }
        Observable<BaseResult<Void>> batchCreateTag = tagApi.batchCreateTag(requests);
        batchCreateTag.subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                Log.d("createTag", "onSuccess: 创建成功! -- " + requests.size());
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }


}
