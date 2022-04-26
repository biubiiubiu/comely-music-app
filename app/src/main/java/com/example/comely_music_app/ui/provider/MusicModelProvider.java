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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用musicService获取music model
 */
public class MusicModelProvider {
    private final MusicService musicService;
    private FileService fileService;
    private Context context;

    public MusicModelProvider(Context context) {
        this.context = context;
        musicService = new MusicServiceImpl();
        fileService = new FileServiceImpl();
    }

    /**
     * 批量获取10首歌曲Model
     */
    public List<MusicModel> getPatchMusicModel(MusicSelectRequest request) {
        List<MusicModel> modelList = new ArrayList<>();
        MusicSelectResponse response = musicService.getMusicList(request);
        for (MusicSelectResponse.MusicInfo info : response.getMusicList()) {
            MusicModel model = new MusicModel();
            model.setName(info.getName());
            model.setArtistName(info.getArtistName());
            String audioLocalPath = getFileFromOSS(info.getAudioStoragePath());
            String coverLocalPath = getFileFromOSS(info.getCoverStoragePath());
            String lyricLocalPath = getFileFromOSS(info.getLyricStoragePath());
            model.setAudioLocalPath(audioLocalPath);
            model.setAudioLocalPath(coverLocalPath);
            model.setAudioLocalPath(lyricLocalPath);
            modelList.add(model);
        }
        return modelList;
    }

    // ====================================================================

    /**
     * 检查本地是否存在此MusicModel信息，有的话就直接加载，没有就从oss下载
     * @param storagePath oss存储位置
     * @return 本地存储位置
     */
    private String getFileFromOSS(String storagePath) {
        File file = new File(FileConfig.BASE_PATH + storagePath);
        if (!file.exists()) {
            // 本地不存在就下载
            fileService.downloadFile(context, "zt001", storagePath);
        }
        return FileConfig.BASE_PATH + storagePath;
    }
}
