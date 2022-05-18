package com.example.comely_music_app.network.service.impl;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.comely_music_app.network.apis.PlaylistApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.PlaylistInfoWithMusicListResponse;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;
import com.example.comely_music_app.network.service.MusicService;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistApi playlistApi;
    private final PlaylistViewModel playlistViewModel;
    private final MusicService musicService;

    public PlaylistServiceImpl(PlaylistViewModel viewModel) {
        playlistApi = ApiManager.getInstance().getApiService(PlaylistApi.class);
        playlistViewModel = viewModel;
        musicService = new MusicServiceImpl();
    }

    public PlaylistServiceImpl(PlaylistViewModel viewModel, Context context) {
        playlistApi = ApiManager.getInstance().getApiService(PlaylistApi.class);
        playlistViewModel = viewModel;
        musicService = new MusicServiceImpl(context);
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
    public void selectPlaylistDetailsByScene(PlaylistSelectRequest request, PlaylistSelectScene scene) {
        Observable<BaseResult<PlaylistInfoWithMusicListResponse>> result = playlistApi.selectPlaylistWithMusicList(request);
        result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PlaylistInfoWithMusicListResponse>(false) {
                    @Override
                    public void onSuccess(PlaylistInfoWithMusicListResponse response) {
                        if (scene.equals(PlaylistSelectScene.MY_CREATE_PLAYLIST)) {
                            List<MusicModel> musicModelList = musicService.transMusicInfo2Models(response.getMusicInfoList());
                            PlaylistDetailsModel currentDetails = playlistViewModel.getCurrentPlaylistDetails().getValue();
                            if (currentDetails == null) {
                                currentDetails = new PlaylistDetailsModel();
                            }
                            currentDetails.setPlaylistInfo(response.getPlaylistInfo());
                            currentDetails.setMusicModelList(musicModelList);
                            playlistViewModel.setCurrentPlaylistDetails(currentDetails);
                        } else if (scene.equals(PlaylistSelectScene.COLLECT_PLAYLIST)) {
                            // todo 展示用户收藏歌单详情页
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

    @Override
    public void deleteMusicFromPlaylist(PlaylistMusicAddRequest request) {
        Observable<BaseResult<Void>> delete = playlistApi.deleteMusicFromPlaylist(request);
        delete.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
                        playlistViewModel.deleteMusicInCurrentMusic(request.getMusicAddInfoList());
                        playlistViewModel.setDeleteSuccessFlag();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {
                        StringBuilder sb = new StringBuilder();
                        for (PlaylistMusicAddRequest.MusicAddInfo info : request.getMusicAddInfoList()) {
                            sb.append("  ").append(info.getTitle());
                        }
                        Log.e("TAG", "onFail: 后端从歌单中删除歌曲失败！失败歌曲：" + sb.toString(), null);
                        playlistViewModel.setDeleteFailedFlag();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addMusicIntoMyLike(PlaylistMusicAddRequest request) {
        Observable<BaseResult<Void>> baseResultObservable = playlistApi.addMusicToMylike(request);
        baseResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
//                        添加成功

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
