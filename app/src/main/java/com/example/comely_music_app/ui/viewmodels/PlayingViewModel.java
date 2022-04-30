package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.comely_music_app.MainActivity;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.models.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayingViewModel extends AndroidViewModel {
    private MutableLiveData<PageStatus> pageStatusLiveData;

    private MutableLiveData<Boolean> isPlayingLiveData;
    private MutableLiveData<Boolean> isLikeLiveData;
    private MutableLiveData<Boolean> isShowCoverLiveData;

    private MutableLiveData<List<MusicModel>> musicListLiveData;
//    private final SavedStateHandle handle;

    public PlayingViewModel(@Nullable Application application, SavedStateHandle handle) {
        super(Objects.requireNonNull(application));
//        this.handle = handle;
    }


    public MutableLiveData<PageStatus> getPageStatusLiveData() {
//        if (!handle.contains(MainActivity.KEY_PAGE_STATUS)) {
//            handle.set(MainActivity.KEY_PAGE_STATUS, PageStatus.PLAYING);
//        }
//        return handle.getLiveData(MainActivity.KEY_PAGE_STATUS);
        if (pageStatusLiveData == null) {
            pageStatusLiveData = new MutableLiveData<>(PageStatus.PLAYING);
        }
        return pageStatusLiveData;
    }


    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        // 如果没有就初始化为isPlaying=true
//        if (!handle.contains(MainActivity.KEY_IS_PLAYING)) {
//            handle.set(MainActivity.KEY_IS_PLAYING, true);
//        }
//        return handle.getLiveData(MainActivity.KEY_IS_PLAYING);
        if (isPlayingLiveData == null) {
            isPlayingLiveData = new MutableLiveData<>(true);
        }
        return isPlayingLiveData;
    }


    public MutableLiveData<Boolean> getIsLikeLiveData() {
        if (isLikeLiveData == null) {
            isLikeLiveData = new MutableLiveData<>(false);
        }
        return isLikeLiveData;
    }

    public MutableLiveData<Boolean> getIsShowCoverLiveData() {
        if (isShowCoverLiveData == null) {
            isShowCoverLiveData = new MutableLiveData<>(true);
        }
        return isShowCoverLiveData;
    }

    public MutableLiveData<List<MusicModel>> getMusicListLiveData() {
        if (musicListLiveData == null) {
            musicListLiveData = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData;
    }

    public void addMusicListLiveData(List<MusicModel> list) {
        List<MusicModel> value = getMusicListLiveData().getValue();
        if (value != null) {
            value.addAll(list);
        }
        musicListLiveData.setValue(value);
    }

    public void setMusicListLiveData(List<MusicModel> list){
        musicListLiveData.setValue(list);
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

    /**
     * 更改页面状态
     */
    public void changePageStatusLiveData(PageStatus status) {
        if (isPlayingLiveData == null) {
            isPlayingLiveData = getIsPlayingLiveData();
        }
        pageStatusLiveData.setValue(status);
    }

    /**
     * 改变点赞，取反
     */
    public void changeIsLikeLiveData() {
        if (isLikeLiveData == null) {
            isLikeLiveData = getIsPlayingLiveData();
        }
        Boolean isLike = isLikeLiveData.getValue();
        isLike = isLike == null || isLike;
        this.isLikeLiveData.setValue(!isLike);
    }

    /**
     * 改变展示封面，取反
     */
    public void changeIsShowCoverLiveData() {
        if (isShowCoverLiveData == null) {
            isShowCoverLiveData = getIsPlayingLiveData();
        }
        Boolean isShowCover = isShowCoverLiveData.getValue();
        isShowCover = isShowCover == null || isShowCover;
        this.isShowCoverLiveData.setValue(!isShowCover);
    }

//    private void load(){
//
//    }
//
//    public void save(){
//
//    }
}
