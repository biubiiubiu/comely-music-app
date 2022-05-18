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

    // 用户与歌单的关系，0-我喜欢（不对外开放接口），1-创建歌单，2-收藏歌单
    private Integer relation;

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

    public PlaylistCreateRequest setRelation(Integer relation) {
        this.relation = relation;
        return this;
    }
}