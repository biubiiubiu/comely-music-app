package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PlayingViewModel extends AndroidViewModel {
    // =========== 界面UI控制 ============
    private MutableLiveData<PageStatus> pageStatusLiveData;
    private MutableLiveData<Boolean> showBottomNevBar;
    private MutableLiveData<Integer> showMainBottomSheetDialog;

    // 点击自建歌单/收藏歌单时+1，触发回调，展示歌单详情页
    private MutableLiveData<Integer> showCreated, showCollect, showMylike, showRecentlyPlay, showSearchPlaylist;
    // 创建/删除歌单 成功/失败时设置+1，触发回调
    private MutableLiveData<Integer> createSuccessFlag, deleteSuccessFlag, createFailedFlag, deleteFailedFlag,
            updateSuccessFlag, updateFailedFlag;


    // =========== 播放器控制，从上到下分别为：暂停/播放、播放进度、播放队列、播放模式 =============
    private MutableLiveData<Boolean> isPlayingLiveData;
    private MutableLiveData<Integer> currentPointFromUser;
    private MutableLiveData<List<MusicModel>> musicListLiveData_endlessModule, musicListLiveData_playlistModule;
    private MutableLiveData<PlayerModule> playerModule;


    // =========== 音乐控制：当前播放的音乐（在上下滑动播放页时设置）、当前选中的音乐（在歌单详情页点击rightBtn三个点图标时设置） =============
    private MutableLiveData<MusicModel> currentPlayMusic, currentCheckMusic;
    // =========== 音乐控制：当前播放的音乐、当前选中的音乐 是否被点赞 =============
    private MutableLiveData<Boolean> currentPlayMusicIsLiked, currentCheckMusicIsLiked;

    //    // =========== 歌单选中控制：用户创建的歌单列表（点击进入歌单时刷新当前选中歌单类型: 0-未选择，1-当前界面是我喜欢歌单，2-自建歌单，3-收藏歌单，4-最近播放，5-推荐歌单） ==============
//    private MutableLiveData<Integer> currentCheckPlaylist;
    // =========== 歌单数据控制：用户创建的歌单列表（登录时初始化、每次修改歌单信息时刷新） ==============
    private MutableLiveData<List<PlaylistModel>> myCreatedPlaylists;
    // =========== 歌单数据控制：当前选中的歌单详情页（第一次点击进入时初始化，之后每次修改歌单信息/增加修改歌曲时设置） ==============
    private MutableLiveData<PlaylistDetailsModel> currentPlaylistDetails;
    // =========== 歌单数据控制：”我喜欢“歌单详情页（登陆时初始化，之后每次点赞/取消点赞时（播放页、各单详情页）设置）
    private MutableLiveData<PlaylistDetailsModel> myLikePlaylistDetails;
    // =========== 歌单数据控制：”最近播放“歌单详情页（登陆时初始化，之后每次播放歌曲时（或者从最近播放中移除时）设置）
    private MutableLiveData<PlaylistDetailsModel> recentlyPlaylistDetails;
    // (因为点赞和取消点赞是频繁的，不能每次都请求数据库，所以在关闭activity时刷新到数据库)
    // 于是分别存放需要在数据库“我喜欢”歌单中 添加与删除 的音乐，用于增量刷新数据库中我喜欢歌单
    private List<MusicModel> toAddIntoMyLike = new ArrayList<>();
    private List<MusicModel> toRemoveFromMyLike = new ArrayList<>();

    // =========== 模糊搜索的结果 =============
    private MutableLiveData<List<MusicModel>> fuzzySearchResultMusicList;
    private MutableLiveData<List<PlaylistDetailsModel>> fuzzySearchResultPlaylists;


    public PlayingViewModel(@Nullable Application application) {
        super(Objects.requireNonNull(application));
    }

    /**
     * ============================================ 界面UI ========================================================
     */

    public MutableLiveData<PageStatus> getPageStatusLiveData() {
        if (pageStatusLiveData == null) {
            pageStatusLiveData = new MutableLiveData<>(PageStatus.PLAYING);
        }
        return pageStatusLiveData;
    }

    /**
     * 更改页面状态
     */
    public void changePageStatusLiveData(PageStatus status) {
        if (pageStatusLiveData == null) {
            pageStatusLiveData = getPageStatusLiveData();
        }
        if (!status.equals(pageStatusLiveData.getValue())) {
            pageStatusLiveData.setValue(status);
        }
    }

    public MutableLiveData<Boolean> getShowBottomNevBar() {
        if (showBottomNevBar == null) {
            showBottomNevBar = new MutableLiveData<>(true);
        }
        return showBottomNevBar;
    }

    public void setShowBottomNevBar(Boolean isShow) {
        if (showBottomNevBar == null) {
            showBottomNevBar = getShowBottomNevBar();
        }
        showBottomNevBar.setValue(isShow);
    }

    public MutableLiveData<Integer> getShowMainBottomSheetDialog() {
        if (showMainBottomSheetDialog == null) {
            showMainBottomSheetDialog = new MutableLiveData<>(0);
        }
        return showMainBottomSheetDialog;
    }

    public void setShowMainBottomSheetDialog() {
        if (showMainBottomSheetDialog == null) {
            showMainBottomSheetDialog = getShowMainBottomSheetDialog();
        }
        if (showMainBottomSheetDialog.getValue() == null) {
            showMainBottomSheetDialog.setValue(0);
        }
        showMainBottomSheetDialog.setValue(showMainBottomSheetDialog.getValue() + 1);
    }

    public MutableLiveData<Integer> getShowCreated() {
        if (showCreated == null) {
            showCreated = new MutableLiveData<>();
        }
        return showCreated;
    }

    public void setShowCreated() {
        if (showCreated == null) {
            showCreated = getShowCreated();
        }
        if (showCreated.getValue() == null) {
            showCreated.setValue(0);
        }
        showCreated.setValue(showCreated.getValue() + 1);
    }

    public MutableLiveData<Integer> getShowCollect() {
        if (showCollect == null) {
            showCollect = new MutableLiveData<>();
        }
        return showCollect;
    }

    public void setShowCollect() {
        if (showCollect == null) {
            showCollect = getShowCollect();
        }
        if (showCollect.getValue() == null) {
            showCollect.setValue(0);
        }
        showCollect.setValue(showCollect.getValue() + 1);
    }

    public MutableLiveData<Integer> getShowMylike() {
        if (showMylike == null) {
            showMylike = new MutableLiveData<>();
        }
        return showMylike;
    }

    public void setShowMylike() {
        if (showMylike == null) {
            showMylike = getShowMylike();
        }
        if (showMylike.getValue() == null) {
            showMylike.setValue(0);
        }
        showMylike.setValue(showMylike.getValue() + 1);
    }

    public MutableLiveData<Integer> getShowRecentlyPlay() {
        if (showRecentlyPlay == null) {
            showRecentlyPlay = new MutableLiveData<>();
        }
        return showRecentlyPlay;
    }

    public void setShowRecentlyPlay() {
        if (showRecentlyPlay == null) {
            showRecentlyPlay = getShowRecentlyPlay();
        }
        if (showRecentlyPlay.getValue() == null) {
            showRecentlyPlay.setValue(0);
        }
        showRecentlyPlay.setValue(showRecentlyPlay.getValue() + 1);
    }

    public MutableLiveData<Integer> getShowSearchPlaylist() {
        if (showSearchPlaylist == null) {
            showSearchPlaylist = new MutableLiveData<>();
        }
        return showSearchPlaylist;
    }

    public void setShowSearchPlaylist() {
        if (showSearchPlaylist == null) {
            showSearchPlaylist = getShowRecentlyPlay();
        }
        if (showSearchPlaylist.getValue() == null) {
            showSearchPlaylist.setValue(0);
        }
        showSearchPlaylist.setValue(showSearchPlaylist.getValue() + 1);
    }

    // ====================== flag控制 =============================
    public MutableLiveData<Integer> getCreateSuccessFlag() {
        if (createSuccessFlag == null) {
            createSuccessFlag = new MutableLiveData<>();
        }
        return createSuccessFlag;
    }

    public MutableLiveData<Integer> getDeleteSuccessFlag() {
        if (deleteSuccessFlag == null) {
            deleteSuccessFlag = new MutableLiveData<>();
        }
        return deleteSuccessFlag;
    }

    public MutableLiveData<Integer> getUpdateSuccessFlag() {
        if (updateSuccessFlag == null) {
            updateSuccessFlag = new MutableLiveData<>();
        }
        return updateSuccessFlag;
    }

    public MutableLiveData<Integer> getCreateFailedFlag() {
        if (createFailedFlag == null) {
            createFailedFlag = new MutableLiveData<>();
        }
        return createFailedFlag;
    }

    public MutableLiveData<Integer> getDeleteFailedFlag() {
        if (deleteFailedFlag == null) {
            deleteFailedFlag = new MutableLiveData<>();
        }
        return deleteFailedFlag;
    }

    public MutableLiveData<Integer> getUpdateFailedFlag() {
        if (updateFailedFlag == null) {
            updateFailedFlag = new MutableLiveData<>();
        }
        return updateFailedFlag;
    }

    public void setCreateSuccessFlag() {
        if (createSuccessFlag == null) {
            createSuccessFlag = getCreateSuccessFlag();
        }
        if (createSuccessFlag.getValue() == null) {
            createSuccessFlag.setValue(1);
        } else {
            createSuccessFlag.setValue(createSuccessFlag.getValue() + 1);
        }
    }

    public void setDeleteSuccessFlag() {
        if (deleteSuccessFlag == null) {
            deleteSuccessFlag = getDeleteSuccessFlag();
        }
        if (deleteSuccessFlag.getValue() == null) {
            deleteSuccessFlag.setValue(1);
        } else {
            deleteSuccessFlag.setValue(deleteSuccessFlag.getValue() + 1);
        }
    }

    public void setUpdateSuccessFlag() {
        if (updateSuccessFlag == null) {
            updateSuccessFlag = getUpdateSuccessFlag();
        }
        if (updateSuccessFlag.getValue() == null) {
            updateSuccessFlag.setValue(1);
        } else {
            updateSuccessFlag.setValue(updateSuccessFlag.getValue() + 1);
        }
    }

    public void setCreateFailedFlag() {
        if (createFailedFlag == null) {
            createFailedFlag = getCreateFailedFlag();
        }
        if (createFailedFlag.getValue() == null) {
            createFailedFlag.setValue(1);
        } else {
            createFailedFlag.setValue(createFailedFlag.getValue() + 1);
        }
    }

    public void setDeleteFailedFlag() {
        if (deleteFailedFlag == null) {
            deleteFailedFlag = getDeleteFailedFlag();
        }
        if (deleteFailedFlag.getValue() == null) {
            deleteFailedFlag.setValue(1);
        } else {
            deleteFailedFlag.setValue(deleteFailedFlag.getValue() + 1);
        }
    }

    public void setUpdateFailedFlag() {
        if (updateFailedFlag == null) {
            updateFailedFlag = getUpdateFailedFlag();
        }
        if (updateFailedFlag.getValue() == null) {
            updateFailedFlag.setValue(1);
        } else {
            updateFailedFlag.setValue(updateFailedFlag.getValue() + 1);
        }
    }

    /**
     * =============================================== 播放器控制 =======================================================
     */
    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        // 如果没有就初始化为isPlaying=true
        if (isPlayingLiveData == null) {
            isPlayingLiveData = new MutableLiveData<>(true);
        }
        return isPlayingLiveData;
    }

    /**
     * 改变播放状态isPlaying，取反
     */
    public void changeIsPlayingLiveData() {
        if (isPlayingLiveData == null) {
            isPlayingLiveData = getIsPlayingLiveData();
        }
        Boolean isPlaying = isPlayingLiveData.getValue();
        isPlaying = isPlaying == null || isPlaying;
        this.isPlayingLiveData.setValue(!isPlaying);
    }

    public void setIsPlayingLiveData(Boolean isPlaying) {
        if (isPlayingLiveData == null) {
            isPlayingLiveData = getIsPlayingLiveData();
        }
        this.isPlayingLiveData.setValue(isPlaying);
    }

    public MutableLiveData<Integer> getCurrentPointFromUser() {
        if (currentPointFromUser == null) {
            currentPointFromUser = new MutableLiveData<>(0);
        }
        return currentPointFromUser;
    }

    public void setCurrentPointFromUser(Integer point) {
        if (currentPointFromUser == null) {
            currentPointFromUser = getCurrentPointFromUser();
        }
        currentPointFromUser.setValue(point);
    }

    public MutableLiveData<List<MusicModel>> getMusicListLiveData_endlessModule() {
        if (musicListLiveData_endlessModule == null) {
            musicListLiveData_endlessModule = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData_endlessModule;
    }

    /**
     * 重置list
     */
    public void setMusicListLiveData_endlessModule(List<MusicModel> list) {
        if (musicListLiveData_endlessModule == null) {
            musicListLiveData_endlessModule = getMusicListLiveData_endlessModule();
        }
        musicListLiveData_endlessModule.setValue(list);
    }

    /**
     * 追加list（只有无限播放模式可以追加，歌单模式每次都是点击播放歌单音乐时一次性初始化的）
     */
    public void addMusicListLiveData(List<MusicModel> list) {
        List<MusicModel> value = getMusicListLiveData_endlessModule().getValue();
        if (value != null) {
            value.addAll(list);
        }
        musicListLiveData_endlessModule.setValue(value);
    }

    public MutableLiveData<List<MusicModel>> getMusicListLiveData_playlistModule() {
        if (musicListLiveData_playlistModule == null) {
            musicListLiveData_playlistModule = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData_playlistModule;
    }

    /**
     * 每次都是在歌单模式点击播放音乐时初始化
     */
    public void setMusicListLiveData_playlistModule(List<MusicModel> list) {
        if (musicListLiveData_playlistModule == null) {
            musicListLiveData_playlistModule = getMusicListLiveData_playlistModule();
        }
        musicListLiveData_playlistModule.setValue(list);
    }

    public MutableLiveData<PlayerModule> getPlayerModule() {
        if (playerModule == null) {
            playerModule = new MutableLiveData<>(PlayerModule.ENDLESS);
        }
        return playerModule;
    }

    /**
     * 设置播放模式，用于控制显示哪个viewpage2
     */
    public void setPlayerModule(PlayerModule module) {
        if (playerModule == null) {
            playerModule = getPlayerModule();
        }
        playerModule.setValue(module);
    }

    /**
     * ============================================ 音乐控制 =========================================================
     */
    public MutableLiveData<MusicModel> getCurrentPlayMusic() {
        if (currentPlayMusic == null) {
            currentPlayMusic = new MutableLiveData<>();
        }
        return currentPlayMusic;
    }

    /**
     * 设置当前播放的music, 上下滑动时调用
     */
    public void setCurrentPlayMusic(MusicModel current) {
        if (currentPlayMusic == null) {
            currentPlayMusic = getCurrentPlayMusic();
        }
        currentPlayMusic.setValue(current);
    }

    public MutableLiveData<MusicModel> getCurrentCheckMusic() {
        if (currentCheckMusic == null) {
            currentCheckMusic = new MutableLiveData<>();
        }
        return currentCheckMusic;
    }

    /**
     * 设置当前选中的music, 在歌单界面选中时调用
     */
    public void setCurrentCheckMusic(MusicModel current) {
        if (currentCheckMusic == null) {
            currentCheckMusic = getCurrentCheckMusic();
        }
        currentCheckMusic.setValue(current);
    }

    public MutableLiveData<Boolean> getCurrentPlayMusicIsLiked() {
        if (currentPlayMusicIsLiked == null) {
            currentPlayMusicIsLiked = new MutableLiveData<>(false);
        }
        MusicModel currentPlay = getCurrentPlayMusic().getValue();
        PlaylistDetailsModel mylikeDetails = getMyLikePlaylistDetails().getValue();
        if (currentPlay != null && mylikeDetails != null && mylikeDetails.getMusicModelList() != null) {
            List<MusicModel> musicModelList = mylikeDetails.getMusicModelList();
            currentPlayMusicIsLiked.setValue(musicModelList.contains(currentPlay));
        }
        return currentPlayMusicIsLiked;
    }

    /**
     * 改变当前播放音乐的点赞，取反
     */
    public void changeCurrentPlayMusicIsLiked() {
        if (currentPlayMusicIsLiked == null) {
            currentPlayMusicIsLiked = getCurrentPlayMusicIsLiked();
        }
        Boolean isLike = currentPlayMusicIsLiked.getValue();
        isLike = isLike == null || isLike;
        this.currentPlayMusicIsLiked.setValue(!isLike);
    }

    public MutableLiveData<Boolean> getCurrentCheckMusicIsLiked() {
        if (currentCheckMusicIsLiked == null) {
            currentCheckMusicIsLiked = new MutableLiveData<>(false);
        }
        MusicModel currentCheck = getCurrentCheckMusic().getValue();
        PlaylistDetailsModel mylikeDetails = getMyLikePlaylistDetails().getValue();
        if (currentCheck != null && mylikeDetails != null && mylikeDetails.getMusicModelList() != null) {
            List<MusicModel> musicModelList = mylikeDetails.getMusicModelList();
            boolean contains = musicModelList.contains(currentCheck);
            currentCheckMusicIsLiked.setValue(contains);
        }
        return currentCheckMusicIsLiked;
    }

    /**
     * 改变当前选中音乐的点赞，取反
     */
    public void changeCurrentCheckMusicIsLiked() {
        if (currentCheckMusicIsLiked == null) {
            currentCheckMusicIsLiked = getCurrentCheckMusicIsLiked();
        }
        Boolean isLike = currentCheckMusicIsLiked.getValue();
        isLike = isLike == null || isLike;
        this.currentCheckMusicIsLiked.setValue(!isLike);
    }


    /**
     * =================================================== 歌单控制 =====================================================
     */
    // ================= myFragment界面歌单列表数据控制 ============
//    public MutableLiveData<Integer> getCurrentCheckPlaylist() {
//        if(currentCheckPlaylist == null) {
//            // 0-未选择，1-当前界面是我喜欢歌单，2-自建歌单，3-收藏歌单，4-最近播放，5-推荐歌单
//            currentCheckPlaylist = new MutableLiveData<>(0);
//        }
//        return currentCheckPlaylist;
//    }
//
//    // 0-未选择，1-当前界面是我喜欢歌单，2-自建歌单，3-收藏歌单，4-最近播放，5-推荐歌单
//    public void setCurrentCheckPlaylist(Integer i) {
//        if(currentCheckPlaylist==null){
//            currentCheckPlaylist = getCurrentCheckPlaylist();
//        }
//        currentCheckPlaylist.setValue(i);
//    }
    public MutableLiveData<List<PlaylistModel>> getMyCreatedPlaylists() {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = new MutableLiveData<>();
        }
        return myCreatedPlaylists;
    }

    public void setMyCreatedPlaylists(List<PlaylistModel> playlistModels) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        myCreatedPlaylists.setValue(playlistModels);
    }

    public void addPlaylist2MyCreatedPlaylists(PlaylistModel playlistModel) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(playlistModel);
        myCreatedPlaylists.setValue(values);
    }

    public void deletePlaylistInCreatedPlaylists(PlaylistModel playlistModel) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values != null) {
            Iterator<PlaylistModel> iterator = values.iterator();
            while (iterator.hasNext()) {
                PlaylistModel model = iterator.next();
                if (model.getName().equals(playlistModel.getName())) {
                    iterator.remove();
                }
            }
        }
        myCreatedPlaylists.setValue(values);
    }

    public void updateCreatedPlaylistByName(String oldName, PlaylistModel model) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values != null) {
            for (PlaylistModel oldModel : values) {
                if (oldModel.getName().equals(oldName)) {
                    if (model.getName() != null) {
                        oldModel.setName(model.getName());
                    }
                    if (model.getVisibility() != null) {
                        oldModel.setVisibility(model.getVisibility());
                    }
                    if (model.getDescription() != null) {
                        oldModel.setDescription(model.getDescription());
                    }
                    if (model.getMusicNum() != null) {
                        oldModel.setMusicNum(model.getMusicNum());
                    }
                }
            }
        }
        myCreatedPlaylists.setValue(values);
    }


    // =========== 歌单详情界面数据 =============
    public MutableLiveData<PlaylistDetailsModel> getCurrentPlaylistDetails() {
        if (currentPlaylistDetails == null) {
            currentPlaylistDetails = new MutableLiveData<>();
        }
        return currentPlaylistDetails;
    }

    public void setCurrentPlaylistDetails(PlaylistDetailsModel detailsModel) {
        if (currentPlaylistDetails == null) {
            currentPlaylistDetails = getCurrentPlaylistDetails();
        }
        currentPlaylistDetails.setValue(detailsModel);
    }

    public void deleteMusicInCurrentPlaylistDetails(List<PlaylistMusicAddRequest.MusicAddInfo> musicAddInfoList) {
        List<MusicModel> newMusicModelList = new ArrayList<>();
        PlaylistDetailsModel currentDetails = getCurrentPlaylistDetails().getValue();
        if (currentDetails != null && currentDetails.getMusicModelList() != null) {
            for (MusicModel model : currentDetails.getMusicModelList()) {
                PlaylistMusicAddRequest.MusicAddInfo info = new PlaylistMusicAddRequest.MusicAddInfo(model.getName(),
                        model.getArtistName());
                if (!musicAddInfoList.contains(info)) {
                    newMusicModelList.add(model);
                }
            }
            currentDetails.setMusicModelList(newMusicModelList);
            currentDetails.getPlaylistInfo().setMusicNum(newMusicModelList.size());
            setCurrentPlaylistDetails(currentDetails);
        }
    }

    public MutableLiveData<PlaylistDetailsModel> getMyLikePlaylistDetails() {
        if (myLikePlaylistDetails == null) {
            myLikePlaylistDetails = new MutableLiveData<>(new PlaylistDetailsModel());
        }
        return myLikePlaylistDetails;
    }

    // ============ ”我喜欢“歌单界面数据控制 ==============
    public void setMyLikePlaylistDetails(PlaylistDetailsModel detailsModel) {
        if (myLikePlaylistDetails == null) {
            myLikePlaylistDetails = getCurrentPlaylistDetails();
        }
        myLikePlaylistDetails.setValue(detailsModel);
    }

    public void addIntoMyLikePlaylist(List<MusicModel> toAddList) {
        if (toAddList == null || toAddList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getMyLikePlaylistDetails().getValue();
        if (details != null) {
            if (details.getMusicModelList() == null || details.getMusicModelList().size() == 0) {
                details.setMusicModelList(toAddList);
            } else {
                List<MusicModel> oldList = details.getMusicModelList();
                Collections.reverse(oldList);
                for (MusicModel model : toAddList) {
                    oldList.remove(model);
                    oldList.add(model);
                }
                Collections.reverse(oldList);
                details.setMusicModelList(oldList);
            }
            setMyLikePlaylistDetails(details);
        }
    }

    public void removeFromMyLikePlaylist(List<MusicModel> toRemoveList) {
        if (toRemoveList == null || toRemoveList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getMyLikePlaylistDetails().getValue();
        if (details != null && details.getMusicModelList() != null) {
            List<MusicModel> newList = new ArrayList<>();
            List<MusicModel> oldList = details.getMusicModelList();
            for (MusicModel model : oldList) {
                if (!toRemoveList.contains(model)) {
                    newList.add(model);
                }
            }
            details.setMusicModelList(newList);
            setMyLikePlaylistDetails(details);
        }
    }

    // ============ ”最近播放“歌单界面数据控制 ==============
    public MutableLiveData<PlaylistDetailsModel> getRecentlyPlaylistDetails() {
        if (recentlyPlaylistDetails == null) {
            recentlyPlaylistDetails = new MutableLiveData<>(new PlaylistDetailsModel());
        }
        return recentlyPlaylistDetails;
    }

    public void setRecentlyPlaylistDetails(PlaylistDetailsModel detailsModel) {
        if (recentlyPlaylistDetails == null) {
            recentlyPlaylistDetails = getRecentlyPlaylistDetails();
        }
        recentlyPlaylistDetails.setValue(detailsModel);
    }

    public void addIntoRecentlyPlaylist(List<MusicModel> toAddList) {
        if (toAddList == null || toAddList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getRecentlyPlaylistDetails().getValue();
        if (details != null) {
            if (details.getMusicModelList() == null || details.getMusicModelList().size() == 0) {
                details.setMusicModelList(toAddList);
            } else {
                List<MusicModel> oldList = details.getMusicModelList();
                Collections.reverse(oldList);
                for (MusicModel model : toAddList) {
                    oldList.remove(model);
                    oldList.add(model);
                }
                Collections.reverse(oldList);
                details.setMusicModelList(oldList);
            }
            setRecentlyPlaylistDetails(details);
        }
    }

    public void removeFromRecentlyPlaylist(List<MusicModel> toRemoveList) {
        if (toRemoveList == null || toRemoveList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getRecentlyPlaylistDetails().getValue();
        if (details != null && details.getMusicModelList() != null) {
            List<MusicModel> newList = new ArrayList<>();
            List<MusicModel> oldList = details.getMusicModelList();
            for (MusicModel model : oldList) {
                if (!toRemoveList.contains(model)) {
                    newList.add(model);
                }
            }
            details.setMusicModelList(newList);
            setRecentlyPlaylistDetails(details);
        }
    }

    public List<MusicModel> getToAddIntoMyLike() {
        return toAddIntoMyLike;
    }

    public List<MusicModel> getToRemoveFromMyLike() {
        return toRemoveFromMyLike;
    }

    public void like(List<MusicModel> musicModels) {
        // 添加到toadd
        for (MusicModel next : musicModels) {
            if (!toAddIntoMyLike.contains(next)) {
                toAddIntoMyLike.add(next);
            }
        }
        // 从toremove中删除
        Iterator<MusicModel> iterator = toRemoveFromMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    public void dislike(List<MusicModel> musicModels) {
        // 添加到toremove
        for (MusicModel next : musicModels) {
            if (!toRemoveFromMyLike.contains(next)) {
                toRemoveFromMyLike.add(next);
            }
        }
        // 从toadd中删除
        Iterator<MusicModel> iterator = toAddIntoMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    private void addInMylikeAddList(List<MusicModel> musicModels) {
        for (MusicModel next : musicModels) {
            if (!toAddIntoMyLike.contains(next)) {
                toAddIntoMyLike.add(next);
            }
        }
    }

    private void removeFromMylikeAddList(List<MusicModel> musicModels) {
        Iterator<MusicModel> iterator = toAddIntoMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    private void addInMylikeRemoveList(MusicModel musicModel) {
        toRemoveFromMyLike.add(musicModel);
    }

    private void removeFromMylikeRemoveList(MusicModel musicModel) {
        Iterator<MusicModel> iterator = toRemoveFromMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (next.equals(musicModel)) {
                iterator.remove();
            }
        }
    }

    public MutableLiveData<List<MusicModel>> getFuzzySearchResultMusicList() {
        if (fuzzySearchResultMusicList == null) {
            fuzzySearchResultMusicList = new MutableLiveData<>();
        }
        return fuzzySearchResultMusicList;
    }

    public void setFuzzySearchResultMusicList(List<MusicModel> resultMusicList) {
        if (fuzzySearchResultMusicList == null) {
            fuzzySearchResultMusicList = getFuzzySearchResultMusicList();
        }
        fuzzySearchResultMusicList.setValue(resultMusicList);
    }

    public MutableLiveData<List<PlaylistDetailsModel>> getFuzzySearchResultPlaylists() {
        if (fuzzySearchResultPlaylists == null) {
            fuzzySearchResultPlaylists = new MutableLiveData<>();
        }
        return fuzzySearchResultPlaylists;
    }

    public void setFuzzySearchResultPlaylists(List<PlaylistDetailsModel> responseList) {
        if (fuzzySearchResultPlaylists == null) {
            fuzzySearchResultPlaylists = getFuzzySearchResultPlaylists();
        }
        fuzzySearchResultPlaylists.setValue(responseList);
    }
}
