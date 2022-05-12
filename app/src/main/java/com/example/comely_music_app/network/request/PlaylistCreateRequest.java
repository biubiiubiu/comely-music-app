package com.example.comely_music_app.network.request;

import lombok.Data;

/**
 * description: 歌单创建约束
 *
 * @author: zhangtian
 * @since: 2022-05-07 10:40
 */
@Data
public class PlaylistCreateRequest {
    private String name;
    private String username;
    private Integer musicNum;
    private String description;
    private Integer visibility;

    public PlaylistCreateRequest setName(String name) {
        this.name = name;
        return this;
    }

    public PlaylistCreateRequest setUsername(String username) {
        this.username = username;
        return this;
    }
    public PlaylistCreateRequest setMusicNum(Integer musicNum) {
        this.musicNum = musicNum;
        return this;
    }

    public PlaylistCreateRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public PlaylistCreateRequest setVisibility(Integer visibility) {
        this.visibility = visibility;
        return this;
    }
}