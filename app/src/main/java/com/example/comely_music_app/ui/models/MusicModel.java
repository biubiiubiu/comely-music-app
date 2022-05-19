package com.example.comely_music_app.ui.models;

import java.io.File;
import java.util.Objects;

import lombok.Data;

/**
 * 音乐Model，用于解析数据到View上
 */
@Data
public class MusicModel {
    private String name;
    private String artistName;
    private String lyricLocalPath;
    /**
     * 根据mp3可以获取时长
     */
    private String audioLocalPath;
    private String coverLocalPath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicModel model = (MusicModel) o;
        return Objects.equals(name, model.name) && Objects.equals(artistName, model.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artistName);
    }
}
