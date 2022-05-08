package com.example.comely_music_app.api.service;

import com.example.comely_music_app.api.request.MusicCreateRequest;
import com.example.comely_music_app.api.request.MusicSelectRequest;
import com.example.comely_music_app.ui.viewmodels.MusicServiceViewModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.List;

public interface MusicService {
    /**
     * 获取一定数量的music，用于播放界面的viewPager2使用
     */
    void getMusicList(MusicSelectRequest request, PlayingViewModel playingViewModel);

    /**
     * 批量创建音乐
     */
    void batchCreateMusic(List<MusicCreateRequest> requestList, MusicServiceViewModel musicServiceViewModel);
}
