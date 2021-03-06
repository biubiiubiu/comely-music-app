package com.example.comely_music_app.network.service.impl;

import android.content.Context;
import android.util.Log;

import com.example.comely_music_app.network.apis.PlaylistApi;
import com.example.comely_music_app.network.base.ApiManager;
import com.example.comely_music_app.network.base.BaseObserver;
import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.MusicSelectResponse;
import com.example.comely_music_app.network.response.PlaylistInfoWithMusicListResponse;
import com.example.comely_music_app.network.response.UserPlaylistsSelectResponse;
import com.example.comely_music_app.network.service.MusicService;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistApi playlistApi;
    private final PlayingViewModel playingViewModel;
    private final MusicService musicService;

    public PlaylistServiceImpl(PlayingViewModel viewModel) {
        playlistApi = ApiManager.getInstance().getApiService(PlaylistApi.class);
        playingViewModel = viewModel;
        musicService = new MusicServiceImpl();
    }

    public PlaylistServiceImpl(PlayingViewModel viewModel, Context context) {
        playlistApi = ApiManager.getInstance().getApiService(PlaylistApi.class);
        playingViewModel = viewModel;
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
                        if (playingViewModel != null) {
                            PlaylistModel model = new PlaylistModel();
                            model.setName(request.getName());
                            model.setMusicNum(request.getMusicNum());
                            playingViewModel.addPlaylist2MyCreatedPlaylists(model);
                            playingViewModel.setCreateSuccessFlag();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {
                        Log.e("TAG", "onFail: ????????????????????????", null);
                        playingViewModel.setCreateFailedFlag();
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
                        if (playingViewModel != null) {
                            PlaylistModel model = new PlaylistModel();
                            model.setName(request.getPlaylistName());
                            playingViewModel.deletePlaylistInCreatedPlaylists(model);
                            playingViewModel.setDeleteSuccessFlag();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {
                        Log.e("TAG", "onFail: ????????????????????????", null);
                        playingViewModel.setDeleteFailedFlag();
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
                        playingViewModel.setMyCreatedPlaylists(response.getPlaylistInfoList());
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, UserPlaylistsSelectResponse response) {
                        Log.e("TAG", "onFail: ??????????????????????????????", null);
                        playingViewModel.setUpdateFailedFlag();
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
                        playingViewModel.updateCreatedPlaylistByName(request.getOldName(), model);
                        playingViewModel.setUpdateSuccessFlag();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {

                        Log.e("TAG", "onFail: ????????????????????????", null);
                        playingViewModel.setUpdateFailedFlag();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    /**
     * ?????????????????????item????????? ??????shp????????????????????? ????????????
     */
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
                            PlaylistDetailsModel currentDetails = playingViewModel.getCurrentPlaylistDetails().getValue();
                            if (currentDetails == null) {
                                currentDetails = new PlaylistDetailsModel();
                            }
                            currentDetails.setPlaylistInfo(response.getPlaylistInfo());
                            currentDetails.setMusicModelList(musicModelList);
                            // ???????????????????????????
                            playingViewModel.setCurrentPlaylistDetails(currentDetails);
                        } else if (scene.equals(PlaylistSelectScene.COLLECT_PLAYLIST)) {
                            // todo ?????????????????????????????????
                        } else if (scene.equals(PlaylistSelectScene.MY_LIKE)) {
                            // ?????????????????????????????????
                            List<MusicModel> musicModelList = musicService.transMusicInfo2Models(response.getMusicInfoList());
                            PlaylistDetailsModel mylikeDetails = playingViewModel.getMyLikePlaylistDetails().getValue();
                            if (mylikeDetails == null) {
                                mylikeDetails = new PlaylistDetailsModel();
                            }
                            mylikeDetails.setPlaylistInfo(response.getPlaylistInfo());
                            mylikeDetails.setMusicModelList(musicModelList);
                            // ??????????????????
                            playingViewModel.setMyLikePlaylistDetails(mylikeDetails);
                            // ???????????????????????????
                            playingViewModel.setCurrentPlaylistDetails(mylikeDetails);
                        } else if (scene.equals(PlaylistSelectScene.RECENTLY_PLAY)) {
                            // ?????????????????????????????????
                            List<MusicModel> musicModelList = musicService.transMusicInfo2Models(response.getMusicInfoList());
                            PlaylistDetailsModel recentlyPlayDetails = playingViewModel.getRecentlyPlaylistDetails().getValue();
                            if (recentlyPlayDetails == null) {
                                recentlyPlayDetails = new PlaylistDetailsModel();
                            }
                            recentlyPlayDetails.setPlaylistInfo(response.getPlaylistInfo());
                            recentlyPlayDetails.setMusicModelList(musicModelList);
                            // ??????????????????
                            playingViewModel.setRecentlyPlaylistDetails(recentlyPlayDetails);
                            // ???????????????????????????
                            playingViewModel.setCurrentPlaylistDetails(recentlyPlayDetails);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, PlaylistInfoWithMusicListResponse response) {
                        Log.e("TAG", "selectPlaylistAndMusicsByScene(): ?????????????????????????????????", null);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addMusicIntoPlaylist(PlaylistMusicAddRequest request) {
        Observable<BaseResult<Void>> result = playlistApi.addMusicIntoPlaylist(request);
        result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
                        Log.d("TAG", "addMusicIntoPlaylist: ???????????????????????????");
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
    public void deleteMusicFromPlaylist(PlaylistMusicAddRequest request) {
        Observable<BaseResult<Void>> delete = playlistApi.deleteMusicFromPlaylist(request);
        delete.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Void>(false) {
                    @Override
                    public void onSuccess(Void o) {
                        playingViewModel.deleteMusicInCurrentPlaylistDetails(request.getMusicAddInfoList());
                        playingViewModel.setDeleteSuccessFlag();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, Void response) {
                        StringBuilder sb = new StringBuilder();
                        for (PlaylistMusicAddRequest.MusicAddInfo info : request.getMusicAddInfoList()) {
                            sb.append("  ").append(info.getTitle());
                        }
                        Log.e("TAG", "onFail: ??????????????????????????????????????????????????????" + sb.toString(), null);
                        playingViewModel.setDeleteFailedFlag();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addMusicIntoMyLike(PlaylistMusicAddRequest request) {
        Observable<BaseResult<MusicSelectResponse>> baseResultObservable = playlistApi.addMusicToMylike(request);
        baseResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(false) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        // ????????????????????????????????????viewmodel???????????????activity onPause()??????????????????
                        List<MusicSelectResponse.MusicInfo> infoList = response.getMusicList();
                        for (MusicSelectResponse.MusicInfo info : infoList) {
                            Log.d("TAG", "addMusicIntoMyLike: ???????????????????????????" + info.getName() + info.getArtistName());
                        }
//                        List<MusicModel> modelList = musicService.transMusicInfo2Models(infoList);
//                        playlistViewModel.addIntoMyLikePlaylist(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("TAG", "addMusicIntoMyLike: ????????????????????????", null);
                    }


                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void removeMusicFromMyLike(PlaylistMusicAddRequest request) {
        Observable<BaseResult<MusicSelectResponse>> baseResultObservable = playlistApi.removeMusicFromMylike(request);
        baseResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(false) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        // ????????????????????????????????????viewmodel???????????????activity onPause()??????????????????
                        List<MusicSelectResponse.MusicInfo> infoList = response.getMusicList();
                        for (MusicSelectResponse.MusicInfo info : infoList) {
                            Log.d("TAG",
                                    "removeMusicFromMyLike: ????????????????????????????????????" + info.getName() + info.getArtistName());
                        }
//                        List<MusicModel> modelList = musicService.transMusicInfo2Models(infoList);
//                        playlistViewModel.addIntoMyLikePlaylist(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("TAG", "removeMusicFromMyLike: ?????????????????????????????????", null);
                    }


                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addMusicIntoRecentlyPlay(PlaylistMusicAddRequest request) {
        Observable<BaseResult<MusicSelectResponse>> baseResultObservable = playlistApi.addMusicToRecentlyPlay(request);
        baseResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(false) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        // ????????????
                        List<MusicSelectResponse.MusicInfo> infoList = response.getMusicList();
                        for (MusicSelectResponse.MusicInfo info : infoList) {
                            Log.d("TAG", "addMusicIntoMyLike: ???????????????????????????" + info.getName() + info.getArtistName());
                        }
//                        List<MusicModel> modelList = musicService.transMusicInfo2Models(infoList);
//                        playingViewModel.addIntoRecentlyPlaylist(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("TAG", "addMusicIntoMyLike: ????????????????????????", null);
                    }


                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void removeMusicFromRecentlyPlay(PlaylistMusicAddRequest request) {
        Observable<BaseResult<MusicSelectResponse>> baseResultObservable = playlistApi.removeMusicFromRecentlyPlay(request);
        baseResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MusicSelectResponse>(false) {
                    @Override
                    public void onSuccess(MusicSelectResponse response) {
                        // ????????????
                        List<MusicSelectResponse.MusicInfo> infoList = response.getMusicList();
                        for (MusicSelectResponse.MusicInfo info : infoList) {
                            Log.d("TAG", "removeMusicFromMyLike: ???????????????????????????" + info.getName() + info.getArtistName());
                        }
                        List<MusicModel> modelList = musicService.transMusicInfo2Models(infoList);
                        playingViewModel.removeFromRecentlyPlaylist(modelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, MusicSelectResponse response) {
                        Log.e("TAG", "removeMusicFromMyLike: ????????????????????????", null);
                    }


                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void fuzzySearchPlaylist(String searchContent) {
        Observable<BaseResult<List<PlaylistInfoWithMusicListResponse>>> resultObservable = playlistApi.fuzzySearchPlaylist(searchContent);
        resultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<PlaylistInfoWithMusicListResponse>>(false) {
                    @Override
                    public void onSuccess(List<PlaylistInfoWithMusicListResponse> responseList) {
                        if (responseList == null) {
                            return;
                        }
                        Log.e("TAG", "fuzzySearchPlaylist: ???????????????????????????" + responseList.size() + "???", null);
                        List<PlaylistDetailsModel> detailsModelList = new ArrayList<>();
                        for (PlaylistInfoWithMusicListResponse response : responseList) {
                            PlaylistDetailsModel detailsModel = new PlaylistDetailsModel();
                            List<MusicModel> musicModelList = musicService.transMusicInfo2Models(response.getMusicInfoList());
                            detailsModel.setMusicModelList(musicModelList);
                            detailsModel.setPlaylistInfo(response.getPlaylistInfo());
                            detailsModelList.add(detailsModel);
                        }
                        playingViewModel.setFuzzySearchResultPlaylists(detailsModelList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, List<PlaylistInfoWithMusicListResponse> response) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


    @Override
    public List<PlaylistMusicAddRequest.MusicAddInfo> transMusicModel2AddInfos(List<MusicModel> musicModels) {
        if (musicModels == null || musicModels.size() == 0) {
            return null;
        }
        List<PlaylistMusicAddRequest.MusicAddInfo> addInfos = new ArrayList<>();
        for (MusicModel model : musicModels) {
            if (model == null) {
                continue;
            }
            PlaylistMusicAddRequest.MusicAddInfo info = new PlaylistMusicAddRequest.MusicAddInfo();
            info.setTitle(model.getName());
            info.setArtistName(model.getArtistName());
            addInfos.add(info);
        }
        return addInfos;
    }


}
