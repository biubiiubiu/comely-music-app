package com.example.comely_music_app.network.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicSelectByTagsRequest {
    private List<String> tags;
    private Integer num;

    public MusicSelectByTagsRequest setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public MusicSelectByTagsRequest setNum(Integer num) {
        this.num = num;
        return this;
    }
}
