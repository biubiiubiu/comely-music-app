package com.example.comely_music_app.ui.provider;

import com.example.comely_music_app.api.response.music.MusicResponse;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.api.service.impl.MusicServiceImpl;
import com.example.comely_music_app.config.FileConfig;
import com.example.comely_music_app.ui.models.MusicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用musicService获取music model
 */
public class MusicModelProvider {
    private MusicService musicService;

    public MusicModelProvider() {
        musicService = new MusicServiceImpl();
    }

    /**
     * 批量获取10首歌曲Model
     */
    public List<MusicModel> getPatchMusicModel(int num) {
        List<MusicModel> modelList = new ArrayList<>();
        List<MusicResponse> musicInfoList = musicService.getMusicList(num);
        for (MusicResponse info : musicInfoList) {
            MusicModel model = new MusicModel();
            model.setName(info.getName());
            model.setArtistName(info.getArtistName());
            model.setAudioPath(FileConfig.BASE_PATH + info.getAudioStoragePath());
            model.setLyricPath(FileConfig.BASE_PATH + info.getLyricStoragePath());
            model.setCoverPath(FileConfig.BASE_PATH + info.getCoverStoragePath());
            modelList.add(model);
        }
        return modelList;
    }
}
