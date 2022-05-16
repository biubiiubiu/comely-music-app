package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.models.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayingViewModel extends AndroidViewModel {
    // =========== 界面 =============
    private MutableLiveData<PageStatus> pageStatusLiveData;
    private MutableLiveData<Integer> currentPointFromUser;

    // =========== 写入数据库 ===============
    private MutableLiveData<Boolean> isPlayingLiveData;
    private MutableLiveData<Boolean> isLikeLiveData;

    //=========== 读取数据库 ================
    private MutableLiveData<List<MusicModel>> musicListLiveData;
    private MutableLiveData<MusicModel> currentMusic;
//    private final SavedStateHandle handle;

    public PlayingViewModel(@Nullable Application application, SavedStateHandle handle) {
        super(Objects.requireNonNull(application));
    }

    // ============================ getter ===============================================

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

    public MutableLiveData<List<MusicModel>> getMusicListLiveData() {
        if (musicListLiveData == null) {
            musicListLiveData = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData;
    }

    public MutableLiveData<MusicModel> getCurrentMusic() {
        if (currentMusic == null) {
            currentMusic = new MutableLiveData<>();
        }
        return currentMusic;
    }

    public MutableLiveData<Integer> getCurrentPointFromUser() {
        if (currentPointFromUser == null) {
            currentPointFromUser = new MutableLiveData<>(0);
        }
        return currentPointFromUser;
    }

    // ================================= setter ==============================================


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

    /**
     * 更改页面状态
     */
    public void changePageStatusLiveData(PageStatus status) {
        if (isPlayingLiveData == null) {
            isPlayingLiveData = getIsPlayingLiveData();
        }
        if (!status.equals(pageStatusLiveData.getValue())) {
            pageStatusLiveData.setValue(status);
        }
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
     * 追加list
     */
    public void addMusicListLiveData(List<MusicModel> list) {
        List<MusicModel> value = getMusicListLiveData().getValue();
        if (value != null) {
            value.addAll(list);
        }
        musicListLiveData.setValue(value);
    }

    /**
     * 重置list
     */
    public void setMusicListLiveData(List<MusicModel> list) {
        if (musicListLiveData == null) {
            musicListLiveData = getMusicListLiveData();
        }
        musicListLiveData.setValue(list);
    }

    /**
     * 设置当前播放的music, 上下滑动时调用
     */
    public void setCurrentMusic(MusicModel current) {
        if (currentMusic == null) {
            currentMusic = getCurrentMusic();
        }
        currentMusic.setValue(current);
    }

    public void setCurrentPointFromUser(Integer point) {
        if (currentPointFromUser == null) {
            currentPointFromUser = getCurrentPointFromUser();
        }
        currentPointFromUser.setValue(point);
    }

    //    private void load(){
//
//    }
//
//    public void save(){
//
//    }
}
