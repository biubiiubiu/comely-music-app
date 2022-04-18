package com.example.comely_music_app.api.service;

import android.content.Context;

import com.example.comely_music_app.api.request.FileDownloadRequest;
import com.example.comely_music_app.api.request.FileUploadRequest;
import com.example.comely_music_app.api.response.file.OssTokenInfo;

import java.util.List;

public interface FileService {
    void uploadFile(Context context, FileUploadRequest fileUploadRequest);

    void downloadFile(Context context, String username, String storageUrl);

    void batchDownloadFile(Context context, String username, List<String> storageUrlList);
}
