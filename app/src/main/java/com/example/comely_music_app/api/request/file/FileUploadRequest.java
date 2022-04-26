package com.example.comely_music_app.api.request.file;

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
