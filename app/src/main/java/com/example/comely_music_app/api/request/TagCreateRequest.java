package com.example.comely_music_app.api.request;

import com.example.comely_music_app.enums.TagType;

import lombok.Data;

@Data
public class TagCreateRequest {
    /**
     * 标签，由用户创建
     */
    private String tagName;
    /**
     * 实体类型
     */
    private TagType type;
    /**
     * 实体名称
     */
    private String entityName;
    /**
     * 歌单的用户名
     */
    private String username;
}

