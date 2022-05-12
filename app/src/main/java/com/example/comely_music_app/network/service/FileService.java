package com.example.comely_music_app.network.service;

import android.content.Context;

import com.example.comely_music_app.network.request.FileCommonRequest;
import com.example.comely_music_app.network.request.FileUploadRequest;
import com.example.comely_music_app.ui.viewmodels.FileServiceViewModel;

import java.util.List;

public interface FileService {
    void uploadFile(Context context, FileUploadRequest fileUploadRequest, String localBaseDir,
                    FileServiceViewModel fileServiceViewModel);

    void downloadFile(Context context, String username, String storageUrl);

    void batchDownloadFile(Context context, String username, List<String> storageUrlList);

    void setUploadSuccessResult(FileCommonRequest request, FileServiceViewModel fileServiceViewModel);
}
