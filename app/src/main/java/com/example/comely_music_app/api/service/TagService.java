package com.example.comely_music_app.api.service;

import com.example.comely_music_app.api.request.TagCreateRequest;

import java.util.List;

public interface TagService {
    void createTag(TagCreateRequest request);

    void batchCreateTag(List<TagCreateRequest> requests);
}
