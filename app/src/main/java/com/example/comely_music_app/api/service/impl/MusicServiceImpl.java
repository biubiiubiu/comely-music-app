package com.example.comely_music_app.api.service.impl;

import com.example.comely_music_app.api.apis.MusicApi;
import com.example.comely_music_app.api.base.ApiManager;
import com.example.comely_music_app.api.base.BaseObserver;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.api.response.music.MusicSelectResponse;
import com.example.comely_music_app.api.service.MusicService;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicServiceImpl implements MusicService {
    MusicApi musicApi = ApiManager.getInstance().getApiService(MusicApi.class);

    @Override
    public MusicSelectResponse getMusicList(MusicSelectRequest request) {
        MusicSelectResponse response = new MusicSelectResponse();
        Observable<BaseResult<MusicSelectResponse>> musicObservable = musicApi.getMusicListByModule(request);
        musicObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(true) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
        List<MusicSelectResponse.MusicInfo> musicList = response.getMusicList();
        musicList.add(new MusicSelectResponse.MusicInfo());
        return null;
    }
}
