package com.example.comely_music_app.network.service.impl;

import android.util.Log;

import com.example.comely_music_app.network.apis.ArtistApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.ArtistCreateRequest;
import com.example.comely_music_app.network.service.ArtistService;

import io.reactivex.rxjava3.core.Observable;

public class ArtistServiceImpl implements ArtistService {
    ArtistApi artistApi;

    public ArtistServiceImpl() {
        artistApi = ApiManager.getInstance().getApiService(ArtistApi.class);
    }

    @Override
    public void create(ArtistCreateRequest request) {
        Observable<BaseResult<Void>> createResult = artistApi.createArtist(request);
        createResult.subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                Log.d("创建音乐人", "onSuccess: 音乐人" + request.getArtistName() + "创建成功！");
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {
                Log.e("创建音乐人", "onSuccess: 音乐人" + request.getArtistName() + "创建失败！", null);
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
