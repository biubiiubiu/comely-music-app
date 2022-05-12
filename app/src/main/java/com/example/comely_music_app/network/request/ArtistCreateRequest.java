package com.example.comely_music_app.network.request;

import lombok.Data;

/**
 * description: 音乐人创建规则
 *
 * @author: zhangtian
 * @since: 2022-04-30 16:51
 */
@Data
public class ArtistCreateRequest {
    /**
     * 不为空
     */
    private String artistName;

    /**
     * 尽量不为空
     */
    private String remark;
    private String photoId;
    private String worksPlaylist_id;

    /**
     * 后端有缺省
     */
    private Integer fansNum;
    private String status;
    private Integer recommendFactor;
}