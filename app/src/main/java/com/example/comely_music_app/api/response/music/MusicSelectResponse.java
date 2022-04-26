package com.example.comely_music_app.api.response.music;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MusicSelectResponse {

    List<MusicInfo> musicList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MusicInfo {
        private String name;
        private String artistName;
        /**
         * 在oss上存储的位置
         */
        private String audioStoragePath;
        private String coverStoragePath;
        private String lyricStoragePath;
    }
}
