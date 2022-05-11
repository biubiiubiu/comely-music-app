package com.example.comely_music_app.ui.provider;

import android.content.Context;

import com.example.comely_music_app.api.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.api.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.api.service.impl.MusicServiceImpl;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

/**
 * 调用musicService获取music model
 */
public class MusicModelProvider {
    private final MusicService musicService;
    private final Context context;

    public MusicModelProvider(Context context) {
        this.context = context;
        musicService = new MusicServiceImpl(context);
    }

    /**
     * 根据播放模式批量获取10首歌曲Model
     */
    public void getPatchMusicModelByModule(MusicSelectByModuleRequest request, PlayingViewModel playingViewModel) {
        musicService.getMusicListByModule(request, playingViewModel);
    }

    /**
     * 根据Tag批量获取10首歌曲Model
     */
    public void getPatchMusicModelByTag(MusicSelectByTagsRequest request, PlayingViewModel playingViewModel) {
        musicService.getMusicListByTags(request, playingViewModel);
    }

    // ====================================================================

}
