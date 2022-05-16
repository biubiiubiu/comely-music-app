package com.example.comely_music_app.network.service.impl;

import android.content.Context;
import android.util.Log;

import com.example.comely_music_app.config.FileConfig;
import com.example.comely_music_app.network.apis.MusicApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.MusicCreateRequest;
import com.example.comely_music_app.network.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.network.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.network.response.MusicBatchCreateResponse;
import com.example.comely_music_app.network.response.MusicSelectResponse;
import com.example.comely_music_app.network.service.FileService;
import com.example.comely_music_app.network.service.MusicService;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.MusicServiceViewModel;
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
    private final static String USERNAME = "admin";

    public MusicServiceImpl(Context context) {
        // 用于oss可用的情况
        this.fileService = new FileServiceImpl();
        this.context = context;
    }

    public MusicServiceImpl() {
        // 用于oss不可用的情况
        this.fileService = new FileServiceImpl();
        this.context = null;
    }


    @Override
    public void getMusicListByModule(MusicSelectByModuleRequest request, PlayingViewModel playingViewModel) {
        Observable<BaseResult<MusicSelectResponse>> musicObservable = musicApi.getMusicListByModule(request);
        musicObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(true) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        List<MusicModel> modelList = transMusicInfo2Models(response.getMusicList());
                        // 放到modelview中
                        playingViewModel.setMusicListLiveData(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("获取音乐list", errorMsg, null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void getMusicListByTags(MusicSelectByTagsRequest request, PlayingViewModel playingViewModel) {
        Observable<BaseResult<MusicSelectResponse>> musicListByTags = musicApi.getMusicListByTags(request);
        musicListByTags.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(false) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        List<MusicModel> modelList = transMusicInfo2Models(response.getMusicList());
                        // 放到modelview中
                        playingViewModel.addMusicListLiveData(modelList);
//                        playingViewModel.setMusicListLiveData(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


    @Override
    public void batchCreateMusic(List<MusicCreateRequest> requestList, MusicServiceViewModel musicServiceViewModel) {
        Observable<BaseResult<MusicBatchCreateResponse>> batchCreateObservable = musicApi.batchCreateMusic(requestList);
        batchCreateObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicBatchCreateResponse>(true) {
                    @Override
                    public void onSuccess(MusicBatchCreateResponse response) {
                        Log.d("批量创建音乐", "创建成功: " + response.getSuccessNum() + "，创建失败：" + response.getFailedNum());
                        if (musicServiceViewModel != null) {
                            musicServiceViewModel.setBatchCreateLiveData(response);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicBatchCreateResponse response) {
                        Log.e("批量创建音乐", errorMsg, null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    public List<MusicModel> transMusicInfo2Models(List<MusicSelectResponse.MusicInfo> musicInfoList) {
        if (musicInfoList == null) {
            return null;
        }
        List<MusicModel> modelList = new ArrayList<>();
        for (MusicSelectResponse.MusicInfo info : musicInfoList) {
            MusicModel model = new MusicModel();
            model.setName(info.getName());
            model.setArtistName(info.getArtistName());
            String audioLocalPath = getFileFromOSS(info.getAudioStoragePath());
            String coverLocalPath = getFileFromOSS(info.getCoverStoragePath());
            String lyricLocalPath = getFileFromOSS(info.getLyricStoragePath());
            model.setAudioLocalPath(audioLocalPath);
            model.setCoverLocalPath(coverLocalPath);
            model.setLyricLocalPath(lyricLocalPath);
            modelList.add(model);
        }
        return modelList;
    }


    /**
     * 检查本地是否存在此MusicModel信息，有的话就直接加载，没有就从oss下载
     *
     * @param storagePath oss存储位置
     * @return 本地存储位置
     */
    private String getFileFromOSS(String storagePath) {
        if (storagePath == null) {
            return null;
        }
        File file = new File(FileConfig.BASE_PATH + storagePath);
        if (!file.exists()) {
            // 本地不存在就下载
            if (context != null) {
                fileService.downloadFile(context, USERNAME, storagePath);
            } else {
                return null;
            }
        }
        return FileConfig.BASE_PATH + storagePath;
    }
}
