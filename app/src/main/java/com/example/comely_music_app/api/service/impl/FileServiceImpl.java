package com.example.comely_music_app.api.service.impl;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.comely_music_app.api.apis.FileApi;
import com.example.comely_music_app.api.base.ApiManager;
import com.example.comely_music_app.api.base.BaseObserver;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.file.FileCommonRequest;
import com.example.comely_music_app.api.request.file.FileUploadRequest;
import com.example.comely_music_app.api.response.file.FileUploadResponse;
import com.example.comely_music_app.api.response.file.OssTokenInfo;
import com.example.comely_music_app.api.service.FileService;
import com.example.comely_music_app.config.FileConfig;
import com.example.comely_music_app.utils.FileOperationUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FileServiceImpl implements FileService {

    private final FileApi fileApi = ApiManager.getInstance().getApiService(FileApi.class);

    // 上传文件根文件夹
    private final static String BASE_UPLOAD_DIR = FileConfig.BASE_PATH;
    // 下载文件根文件夹
    private final static String BASE_DOWNLOAD_DIR = FileConfig.BASE_PATH;

    /**
     * 上传文件，文件位置 BASE_DIR/filename
     */
    @Override
    public void uploadFile(Context context, FileUploadRequest request) {
        Observable<BaseResult<FileUploadResponse>> uploadingObservable = fileApi.upLoading(request);
        uploadingObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<FileUploadResponse>(false) {
                    @Override
                    public void onSuccess(FileUploadResponse response) {
                        FileCommonRequest request = new FileCommonRequest();
                        Map<String, FileCommonRequest.CommonInfo> fileKeyInfoMap = new HashMap<>();

                        OssTokenInfo ossTokenInfo = response.getOssTokenInfo();
                        Map<String, String> filename2StorageUrl = response.getFileStorageUrlMap();
                        for (Map.Entry<String, String> entry : filename2StorageUrl.entrySet()) {

                            String filename = entry.getKey();
                            String storageUrl = entry.getValue();
                            String fileKey = storageUrl.substring(storageUrl.lastIndexOf("/") + 1);
                            String localFilePath = BASE_UPLOAD_DIR + filename;
                            File file = new File(localFilePath);
                            long size = file.length();
                            // 上传
                            upload(context, ossTokenInfo, storageUrl, localFilePath);
                            // 需要存mysql的信息放在map里
                            fileKeyInfoMap.put(fileKey, new FileCommonRequest.CommonInfo(filename, storageUrl, size));
                        }
                        // 上传成功的文件信息存数据库
                        request.setFileKeyInfoMap(fileKeyInfoMap);
                        setUploadSuccessResult(request);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, FileUploadResponse response) {
                        Log.e("Failed", "上传失败!");
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("ERROR", "上传失败!");
                    }
                });
    }

    private void upload(Context context, OssTokenInfo tokenResponseBody, String storageUrl, String localFilePath) {
        OSS oss = getOssClient(context, tokenResponseBody);
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(tokenResponseBody.getBucketName(), storageUrl, localFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback((request, currentSize, totalSize) -> {
            Log.d("PutObject", "currentSize:" + currentSize + " totalSize:" + totalSize);
        });

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("TAG", localFilePath + "onSuccess: 上传成功！");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    /**
     * 下载单个文件到指定目录，文件位置 BASE_DIR/filename
     */
    @Override
    public void downloadFile(Context context, String username, String ossStorageUrl) {
        Observable<BaseResult<OssTokenInfo>> ossTokenObservable = fileApi.getOssToken(username);
        ossTokenObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OssTokenInfo>(false) {
                    @Override
                    public void onSuccess(OssTokenInfo ossTokenInfo) {
                        download(context, ossTokenInfo, ossStorageUrl, BASE_DOWNLOAD_DIR + ossStorageUrl);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, OssTokenInfo response) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    /**
     * 批量下载文件到指定目录，文件位置 BASE_DIR/filename
     */
    @Override
    public void batchDownloadFile(Context context, String username, List<String> storageUrlList) {
        Observable<BaseResult<OssTokenInfo>> ossTokenObservable = fileApi.getOssToken(username);
        ossTokenObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OssTokenInfo>(false) {
                    @Override
                    public void onSuccess(OssTokenInfo ossTokenInfo) {
                        for (String storageUrl : storageUrlList) {
                            download(context, ossTokenInfo, storageUrl, BASE_DOWNLOAD_DIR + storageUrl);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, OssTokenInfo response) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    private void download(Context context, OssTokenInfo tokenResponseBody, String storageUrl, String downloadPath) {
        // 获取ossclient
        OSS oss = getOssClient(context, tokenResponseBody);
        GetObjectRequest get = new GetObjectRequest(tokenResponseBody.getBucketName(), storageUrl);

        oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 开始读取数据。
                long length = result.getContentLength();
                if (length > 0) {
                    byte[] buffer = new byte[(int) length];
                    int readCount = 0;
                    while (readCount < length) {
                        try {
                            readCount += result.getObjectContent().read(buffer, readCount, (int) length - readCount);
                        } catch (Exception e) {
                            OSSLog.logInfo(e.toString());
                        }
                    }
                    // 将下载后的文件存放在指定的本地路径downloadPath
                    try {
                        FileOperationUtils.writeBytesToFile(buffer, downloadPath);
//                        File file = new File(downloadPath);
//                        FileOutputStream outputStream = new FileOutputStream(file);
//                        outputStream.write(buffer);
//                        outputStream.flush();
//                        outputStream.close();
                        Log.d("TAG", "下载完成！" + storageUrl);
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException,
                                  ServiceException serviceException) {
                Log.e("TAG", "下载失败！");
                clientException.printStackTrace();
                serviceException.printStackTrace();
            }
        });
    }

    private OSS getOssClient(Context context, OssTokenInfo stsToken) {
        String endpoint = stsToken.getEndpoint();
        OSSCredentialProvider credentialProvider =
                new OSSStsTokenCredentialProvider(stsToken.getAccessKeyId(), stsToken.getAccessKeySecret(),
                        stsToken.getSecurityToken());
        return new OSSClient(context, endpoint, credentialProvider);
    }

    private void setUploadSuccessResult(FileCommonRequest request) {
        Observable<BaseResult<Boolean>> uploadingObservable = fileApi.setUploadSuccess(request);
        uploadingObservable.subscribe(new BaseObserver<Boolean>(false) {
            @Override
            public void onSuccess(Boolean o) {
                Log.d("TAG", "onSuccess: 上传完成的文件信息存入mysql完成！");
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Boolean response) {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
