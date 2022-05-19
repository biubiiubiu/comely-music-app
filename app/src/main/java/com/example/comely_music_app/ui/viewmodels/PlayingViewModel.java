package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.comely_music_app.enums.PlayerModule;
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

    private MutableLiveData<List<MusicModel>> musicListLiveData_endlessModule, musicListLiveData_playlistModule;
    private MutableLiveData<MusicModel> currentMusic;

    private MutableLiveData<PlayerModule> playerModule;

    private MutableLiveData<Boolean> showBottomNevBar;

    public PlayingViewModel(@Nullable Application application) {
        super(Objects.requireNonNull(application));
    }

    // ============================ getter ===============================================

    public MutableLiveData<PageStatus> getPageStatusLiveData() {
        if (pageStatusLiveData == null) {
            pageStatusLiveData = new MutableLiveData<>(PageStatus.PLAYING);
        }
        return pageStatusLiveData;
    }


    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        // 如果没有就初始化为isPlaying=true
        if (isPlayingLiveData == null) {
            isPlayingLiveData = new MutableLiveData<>(false);
        }
        return isPlayingLiveData;
    }


    public MutableLiveData<Boolean> getIsLikeLiveData() {
        if (isLikeLiveData == null) {
            isLikeLiveData = new MutableLiveData<>(false);
        }
        return isLikeLiveData;
    }

    public MutableLiveData<List<MusicModel>> getMusicListLiveData_endlessModule() {
        if (musicListLiveData_endlessModule == null) {
            musicListLiveData_endlessModule = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData_endlessModule;
    }

    public MutableLiveData<List<MusicModel>> getMusicListLiveData_playlistModule() {
        if (musicListLiveData_playlistModule == null) {
            musicListLiveData_playlistModule = new MutableLiveData<>(new ArrayList<>());
        }
        return musicListLiveData_playlistModule;
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

    public MutableLiveData<PlayerModule> getPlayerModule() {
        if (playerModule == null) {
            playerModule = new MutableLiveData<>(PlayerModule.ENDLESS);
        }
        return playerModule;
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
        if (pageStatusLiveData == null) {
            pageStatusLiveData = getPageStatusLiveData();
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
        List<MusicModel> value = getMusicListLiveData_endlessModule().getValue();
        if (value != null) {
            value.addAll(list);
        }
        musicListLiveData_endlessModule.setValue(value);
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

    public void setMusicListLiveData_playlistModule(List<MusicModel> list) {
        if (musicListLiveData_playlistModule == null) {
            musicListLiveData_playlistModule = getMusicListLiveData_playlistModule();
        }
        musicListLiveData_playlistModule.setValue(list);
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

    /**
     * 设置播放模式，用于控制显示哪个viewpage2
     */
    public void setPlayerModule(PlayerModule module) {
        if (playerModule == null) {
            playerModule = getPlayerModule();
        }
        playerModule.setValue(module);
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
}
