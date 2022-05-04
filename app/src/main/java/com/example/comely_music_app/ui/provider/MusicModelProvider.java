package com.example.comely_music_app.ui.provider;

import android.content.Context;

import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.api.response.music.MusicSelectResponse;
import com.example.comely_music_app.api.service.FileService;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.api.service.impl.FileServiceImpl;
import com.example.comely_music_app.api.service.impl.MusicServiceImpl;
import com.example.comely_music_app.config.FileConfig;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        musicService.getMusicList(request,playingViewModel);
    }

    // ====================================================================

}
