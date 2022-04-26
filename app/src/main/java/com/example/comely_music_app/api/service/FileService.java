package com.example.comely_music_app.api.service;

import android.content.Context;

import com.example.comely_music_app.api.request.file.FileUploadRequest;

import java.util.List;

public interface FileService {
    void uploadFile(Context context, FileUploadRequest fileUploadRequest);

    void downloadFile(Context context, String username, String storageUrl);

    void batchDownloadFile(Context context, String username, List<String> storageUrlList);
}
