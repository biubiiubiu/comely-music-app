package com.example.comely_music_app.api.service.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.comely_music_app.api.apis.MusicApi;
import com.example.comely_music_app.api.base.ApiManager;
import com.example.comely_music_app.api.base.BaseObserver;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.api.response.music.MusicSelectResponse;
import com.example.comely_music_app.api.service.FileService;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.config.FileConfig;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicServiceImpl implements MusicService {
    MusicApi musicApi = ApiManager.getInstance().getApiService(MusicApi.class);
    private final FileService fileService;
    private final Context context;

    public MusicServiceImpl(Context context) {
        this.fileService = new FileServiceImpl();
        this.context = context;
    }


    @Override
    public MusicSelectResponse getMusicList(MusicSelectRequest request, PlayingViewModel playingViewModel) {
        MusicSelectResponse response = new MusicSelectResponse();
        Observable<BaseResult<MusicSelectResponse>> musicObservable = musicApi.getMusicListByModule(request);
        musicObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(true) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        List<MusicModel> modelList = new ArrayList<>();
                        for (MusicSelectResponse.MusicInfo info : response.getMusicList()) {
                            MusicModel model = new MusicModel();
                            model.setName(info.getName());
                            model.setArtistName(info.getArtistName());
                            String audioLocalPath = getFileFromOSS(info.getAudioStoragePath());
                            String coverLocalPath = getFileFromOSS(info.getCoverStoragePath());
                            String lyricLocalPath = getFileFromOSS(info.getLyricStoragePath());
                            model.setAudioLocalPath(audioLocalPath);
                            model.setAudioLocalPath(coverLocalPath);
                            model.setAudioLocalPath(lyricLocalPath);
                            modelList.add(model);
                        }
                        // 放到modelview中
                        playingViewModel.addMusicListLiveData(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("获取音乐list", errorMsg, null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
        List<MusicSelectResponse.MusicInfo> musicList = response.getMusicList();
        musicList.add(new MusicSelectResponse.MusicInfo());
        return null;
    }

    /**
     * 检查本地是否存在此MusicModel信息，有的话就直接加载，没有就从oss下载
     *
     * @param storagePath oss存储位置
     * @return 本地存储位置
     */
    private String getFileFromOSS(String storagePath) {
        File file = new File(FileConfig.BASE_PATH + storagePath);
        if (!file.exists()) {
            // 本地不存在就下载
            fileService.downloadFile(context, "zt001", storagePath);
        }
        return FileConfig.BASE_PATH + storagePath;
    }
}
