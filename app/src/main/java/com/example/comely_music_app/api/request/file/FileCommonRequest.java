package com.example.comely_music_app.api.request.file;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一般file服务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCommonRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonInfo {
        private String filename;
        private String storageUrl;
        private Long size;
    }

    /**
     * map(fileKey,commonInfo)
     */
    private Map<String, CommonInfo> fileKeyInfoMap;
}
