package com.example.comely_music_app.api.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    /**
     * 这一批文件的ossToken
     */
    private OssTokenInfo ossTokenInfo;

    /**
     * 这一批文件的map(originalName,storageUrl)
     */
    private Map<String, String> fileStorageUrlMap;
}

