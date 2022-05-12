package com.example.comely_music_app.network.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传数据格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadInfo{
        /**
         * 包含后缀，例如：许嵩 - 雨幕.mp3
         */
        private String originalFilename;
        private Long size;
    }
    /**
     * 用户名
     */
    private String username;

    /**
     * 需要上传的文件信息
     */
    List<FileUploadInfo> fileUploadInfoList;
}
