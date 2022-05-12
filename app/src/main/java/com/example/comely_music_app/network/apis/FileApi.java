package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.FileCommonRequest;
import com.example.comely_music_app.network.request.FileUploadRequest;
import com.example.comely_music_app.network.response.FileUploadResponse;
import com.example.comely_music_app.network.response.OssTokenInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FileApi extends BaseApi {
    /**
     * 根据文件基本信息，从后端获取Oss信息，包括临时密钥、文件上传后存储位置等等
     *
     * @param fileUploadRequest 上传者、List(文件名+文件大小)
     * @return 文件临时密钥、文件名--上传后的存储位置 映射map
     */
    @POST("generate/file/uploading")
    Observable<BaseResult<FileUploadResponse>> upLoading(@Body FileUploadRequest fileUploadRequest);

    /**
     * 上传完成，通知后端刷新文件信息到数据库
     *
     * @param commonRequest 文件的key--文件信息 映射集合
     * @return 是否保存成功
     */
    @POST("generate/file/upload-success")
    Observable<BaseResult<Boolean>> setUploadSuccess(@Body FileCommonRequest commonRequest);

    @GET("generate/file/oss-token/{username}")
    Observable<BaseResult<OssTokenInfo>> getOssToken(@Path("username") String username);
}
