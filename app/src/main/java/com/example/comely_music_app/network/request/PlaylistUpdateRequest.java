package com.example.comely_music_app.network.request;

import lombok.Data;

/**
 * description: 歌单信息修改约束
 *
 * @author: zhangtian
 * @since: 2022-05-07 12:30
 */
@Data
public class PlaylistUpdateRequest {
    /**
     * 这两个为了确定是谁修改了歌单
     */
    private String oldName;
    private String oldUsername;

    /**
     * 这些是可修改项
     */
    private String newName;
    private String newUsername;
    private String coverId;
    private Integer musicNum;
    private String description;

    public PlaylistUpdateRequest setOldName(String oldName) {
        this.oldName = oldName;
        return this;
    }

    public PlaylistUpdateRequest setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
        return this;
    }

    public PlaylistUpdateRequest setNewName(String newName) {
        this.newName = newName;
        return this;
    }

    public PlaylistUpdateRequest setNewUsername(String newUsername) {
        this.newUsername = newUsername;
        return this;
    }

    public PlaylistUpdateRequest setCoverId(String coverId) {
        this.coverId = coverId;
        return this;
    }

    public PlaylistUpdateRequest setMusicNum(Integer musicNum) {
        this.musicNum = musicNum;
        return this;
    }

    public PlaylistUpdateRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public PlaylistUpdateRequest setVisibility(Integer visibility) {
        this.visibility = visibility;
        return this;
    }

    public PlaylistUpdateRequest setCollectionNum(Integer collectionNum) {
        this.collectionNum = collectionNum;
        return this;
    }

    private Integer visibility;
    private Integer collectionNum;
}