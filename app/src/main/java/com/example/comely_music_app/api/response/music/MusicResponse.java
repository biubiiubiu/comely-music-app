package com.example.comely_music_app.api.response.music;

import java.util.Map;

import lombok.Data;

@Data
public class MusicResponse {

    private String name;
    private String artistName;
    /**
     * 在oss上存储的位置
     */
    private String audioStoragePath;
    private String coverStoragePath;
    private String lyricStoragePath;
}
