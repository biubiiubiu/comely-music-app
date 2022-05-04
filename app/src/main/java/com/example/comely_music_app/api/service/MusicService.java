package com.example.comely_music_app.api.service;

import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.api.response.music.MusicSelectResponse;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

public interface MusicService {
    /**
     * 获取一定数量的music，用于播放界面的viewPager2使用
     */
    void getMusicList(MusicSelectRequest request, PlayingViewModel playingViewModel);
}
