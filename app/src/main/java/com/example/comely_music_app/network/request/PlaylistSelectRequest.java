package com.example.comely_music_app.network.request;

import lombok.Data;

/**
 * description: 查询、删除歌单约束,都不能为null
 *
 * @author: zhangtian
 * @since: 2022-05-07 11:25
 */
@Data
public class PlaylistSelectRequest {
    private String username;
    private String playlistName;

    public PlaylistSelectRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public PlaylistSelectRequest setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
        return this;
    }
}
