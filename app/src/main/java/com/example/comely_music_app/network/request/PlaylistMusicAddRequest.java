package com.example.comely_music_app.network.request;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: 歌单-歌曲关联表创建约束
 *
 * @author: zhangtian
 * @since: 2022-05-07 11:51
 */
@Data
public class PlaylistMusicAddRequest {
    /**
     * 用户名与歌单名唯一确定一个歌单
     */
    private String username;
    private String playlistName;
    /**
     * 需要添加的歌曲名与歌手
     */
    private List<MusicAddInfo> musicAddInfoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MusicAddInfo {
        /**
         * 歌曲名与歌手可以唯一确定一首Music
         */
        private String title;
        private String artistName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MusicAddInfo info = (MusicAddInfo) o;
            return Objects.equals(title, info.title) && Objects.equals(artistName, info.artistName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, artistName);
        }
    }

    public PlaylistMusicAddRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public PlaylistMusicAddRequest setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
        return this;
    }

    public PlaylistMusicAddRequest setMusicAddInfoList(List<MusicAddInfo> musicAddInfoList) {
        this.musicAddInfoList = musicAddInfoList;
        return this;
    }
}
