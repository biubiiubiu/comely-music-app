package com.example.comely_music_app.network.service.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.comely_music_app.network.apis.PlaylistApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistApi playlistApi;
    private final PlaylistViewModel playlistViewModel;

    public PlaylistServiceImpl(PlaylistViewModel viewModel) {
        playlistApi = ApiManager.getInstance().getApiService(PlaylistApi.class);
        playlistViewModel = viewModel;
    }

    @Override
    public void createPlaylist(PlaylistCreateRequest request) {
        Observable<BaseResult<Void>> createResult = playlistApi.createPlaylist(request);
        createResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                if (playlistViewModel != null) {
                    PlaylistModel model = new PlaylistModel();
                    model.setName(request.getName());
                    model.setMusicNum(request.getMusicNum());
                    playlistViewModel.addPlaylist2MyCreatedPlaylists(model);
                    playlistViewModel.setCreateSuccessFlag();
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {
                Log.e("TAG", "onFail: 后端创建歌单失败", null);
                playlistViewModel.setCreateFailedFlag();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void selectAllCreatedPlaylistByUsername(String username) {
        Observable<BaseResult<UserPlaylistsSelectResponse>> result = playlistApi.selectAllCreatedPlaylistByUsername(username);
        result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<UserPlaylistsSelectResponse>(false) {
                    @Override
                    public void onSuccess(UserPlaylistsSelectResponse response) {
                        playlistViewModel.setMyCreatedPlaylists(response.getPlaylistModelList());
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, UserPlaylistsSelectResponse response) {
                        Log.e("TAG", "onFail: 获取用户自建歌单失败", null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

}
