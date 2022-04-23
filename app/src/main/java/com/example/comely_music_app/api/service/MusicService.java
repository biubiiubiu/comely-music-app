package com.example.comely_music_app.api.service;

import com.example.comely_music_app.entity.Music;

import java.util.List;

public interface MusicService {
    /**
     * 获取一定数量的music，用于播放界面的viewPager2使用
     */
    List<Music> getMusicList(int num);
}
