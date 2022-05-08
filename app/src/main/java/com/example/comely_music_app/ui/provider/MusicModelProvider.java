package com.example.comely_music_app.ui.provider;

import android.content.Context;

import com.example.comely_music_app.api.request.MusicSelectRequest;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.api.service.impl.MusicServiceImpl;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

/**
 * 调用musicService获取music model
 */
public class MusicModelProvider {
    private final MusicService musicService;
    private Context context;

    public MusicModelProvider(Context context) {
        this.context = context;
        musicService = new MusicServiceImpl(context);
    }

    /**
     * 批量获取10首歌曲Model
     */
    public void getPatchMusicModel(MusicSelectRequest request, PlayingViewModel playingViewModel) {
        musicService.getMusicList(request, playingViewModel);
    }

    // ====================================================================

}
