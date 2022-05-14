package com.example.comely_music_app.network.service.impl;

import android.util.Log;

import com.example.comely_music_app.network.apis.PlaylistApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.PlaylistInfoWithMusicListResponse;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
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
    public void deletePlaylist(PlaylistSelectRequest request) {
        Observable<BaseResult<Void>> createResult = playlistApi.deletePlaylist(request);
        createResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
                        if (playlistViewModel != null) {
                            PlaylistModel model = new PlaylistModel();
                            model.setName(request.getPlaylistName());
                            playlistViewModel.deletePlaylistInCreatedPlaylists(model);
                            playlistViewModel.setDeleteSuccessFlag();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {
                        Log.e("TAG", "onFail: 后端创建歌单失败", null);
                        playlistViewModel.setDeleteFailedFlag();
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
                        playlistViewModel.setMyCreatedPlaylists(response.getPlaylistInfoList());
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, UserPlaylistsSelectResponse response) {
                        Log.e("TAG", "onFail: 获取用户自建歌单失败", null);
                        playlistViewModel.setUpdateFailedFlag();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void updatePlaylist(PlaylistUpdateRequest request) {
        Observable<BaseResult<Void>> updateResult = playlistApi.updatePlaylist(request);
        updateResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
                        PlaylistModel model = new PlaylistModel();
                        if (request.getNewName() != null) {
                            model.setName(request.getNewName());
                        }
                        if (request.getVisibility() != null) {
                            model.setVisibility(request.getVisibility());
                        }
                        if (request.getMusicNum() != null) {
                            model.setMusicNum(request.getMusicNum());
                        }
                        if (request.getDescription() != null) {
                            model.setDescription(request.getDescription());
                        }
                        playlistViewModel.updateCreatedPlaylistByName(request.getOldName(), model);
                        playlistViewModel.setUpdateSuccessFlag();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {

                        Log.e("TAG", "onFail: 后端修改歌单失败", null);
                        playlistViewModel.setUpdateFailedFlag();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void selectPlaylistAndMusicsByScene(PlaylistSelectRequest request, PlaylistSelectScene scene) {
        Observable<BaseResult<PlaylistInfoWithMusicListResponse>> result = playlistApi.selectPlaylistWithMusicList(request);
        result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PlaylistInfoWithMusicListResponse>(false) {
                    @Override
                    public void onSuccess(PlaylistInfoWithMusicListResponse response) {
                        if (scene.equals(PlaylistSelectScene.MY_CREATE_PLAYLIST)) {
                            // 展示用户自建歌单详情页
                            playlistViewModel.setCurrentShowingPlaylist(response.getPlaylistInfo());
                            playlistViewModel.setCurrentShowingMusicList(response.getMusicInfoList());
                            playlistViewModel.setShowCreated();
                        } else if (scene.equals(PlaylistSelectScene.COLLECT_PLAYLIST)) {
                            // 展示用户收藏歌单详情页

                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, PlaylistInfoWithMusicListResponse response) {
                        Log.e("TAG", "selectPlaylistAndMusicsByScene(): 查询歌单详情页信息失败", null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

}
