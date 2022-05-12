package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.MusicCreateRequest;
import com.example.comely_music_app.network.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.network.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.ui.viewmodels.MusicServiceViewModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.List;

public interface MusicService {
    /**
     * 按module获取一定数量的music，用于播放界面的viewPager2使用
     */
    void getMusicListByModule(MusicSelectByModuleRequest request, PlayingViewModel playingViewModel);

    /**
     * 批量创建音乐
     */
    void batchCreateMusic(List<MusicCreateRequest> requestList, MusicServiceViewModel musicServiceViewModel);


    /**
     * 按tag获取一定数量的music，用于播放界面的viewPager2使用
     */
    void getMusicListByTags(MusicSelectByTagsRequest request, PlayingViewModel playingViewModel);
}
