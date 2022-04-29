package com.example.comely_music_app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.comely_music_app.MainActivity;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> isPlayingLiveData;
    private MutableLiveData<Integer> pageStatusLiveData;
    private SavedStateHandle handle;

    public MainViewModel(@Nullable Application application, SavedStateHandle handle) {
        super(application);
        this.handle = handle;
    }

    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        // 如果没有就初始化为isPlaying=true
        if (!handle.contains(MainActivity.KEY_IS_PLAYING)) {
            handle.set(MainActivity.KEY_IS_PLAYING, true);
        }
        return handle.getLiveData(MainActivity.KEY_IS_PLAYING);
    }

    public MutableLiveData<Integer> getPageStatusLiveData() {
        if (!handle.contains(MainActivity.KEY_PAGE_STATUS)) {
            handle.set(MainActivity.KEY_PAGE_STATUS, 2);
        }
        return handle.getLiveData(MainActivity.KEY_PAGE_STATUS);
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
