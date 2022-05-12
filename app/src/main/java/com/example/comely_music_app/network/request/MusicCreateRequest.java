package com.example.comely_music_app.network.request;

import lombok.Data;

/**
 * description: 音乐上传数据约束
 *
 * @author: zhangtian
 * @since: 2022-04-23 21:15
 */
@Data
public class MusicCreateRequest {
    /**
     * 不为空
     */
    private String name;
    private String artistName;

    /**
     * 可以缺省
     */
    private String description;
    private String coverId;
    private String mp3Id;
    private String lyricId;
    private String status;
    private String playerModule;
}