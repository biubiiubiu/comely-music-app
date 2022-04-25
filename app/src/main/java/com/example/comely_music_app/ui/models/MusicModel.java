package com.example.comely_music_app.ui.models;

import java.io.File;

import lombok.Data;

/**
 * 音乐Model，用于解析数据到View上
 */
@Data
public class MusicModel {
    private String name;
    private String artistName;
    private String lyricPath;
    /**
     * 根据mp3可以获取时长
     */
    private String audioPath;
    private String coverPath;
}
