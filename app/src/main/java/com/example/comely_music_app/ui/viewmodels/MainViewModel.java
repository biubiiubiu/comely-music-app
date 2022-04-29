package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.comely_music_app.MainActivity;
import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.enums.PageStatus;

import java.util.Objects;

public class MainViewModel extends AndroidViewModel {
    private SavedStateHandle handle;
    private MutableLiveData<Boolean> isPlayingLiveData;
    /**
     * 1-发现页，2-播放页，3-我的
     */
    private MutableLiveData<Integer> pageStatusLiveData;

    public final static String KEY_IS_PLAYING = "KEY_IS_PLAYING";
    public final static String KEY_PAGE_STATUS = "KEY_PAGE_STATUS";

    private Animation mAnimation;

    public MainViewModel(@Nullable Application application, SavedStateHandle handle) {
        super(Objects.requireNonNull(application));
        this.handle = handle;
        mAnimation = AnimationUtils.loadAnimation(getApplication(), R.anim.rotaterepeat);
    }

    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        // 如果没有就初始化为isPlaying=true
        if (!handle.contains(KEY_IS_PLAYING)) {
            handle.set(KEY_IS_PLAYING, true);
        }
        return handle.getLiveData(KEY_IS_PLAYING);
    }

    public MutableLiveData<Integer> getPageStatusLiveData() {
        if (!handle.contains(KEY_PAGE_STATUS)) {
            handle.set(KEY_PAGE_STATUS, 2);
        }
        return handle.getLiveData(KEY_PAGE_STATUS);
    }

    /**
     * 改变播放状态isPlaying，取反
     */
    public void changeIsPlayingLiveData() {
        Boolean isPlaying = getIsPlayingLiveData().getValue();
        isPlaying = isPlaying == null || isPlaying;
        isPlayingLiveData.setValue(!isPlaying);
    }

    public void changeIsPlayingAndPageStatusLiveData(){
        changeIsPlayingLiveData();
        changePageStatusLiveData(2);
    }

    /**
     * 更改页面状态
     */
    public void changePageStatusLiveData(Integer status) {
        pageStatusLiveData.setValue(status);
    }

//    private void load(){
//
//    }
//
//    public void save(){
//
//    }
}
