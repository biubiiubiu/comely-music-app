package com.example.comely_music_app.api.apis;

import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.FileCommonRequest;
import com.example.comely_music_app.api.request.FileUploadRequest;
import com.example.comely_music_app.api.response.file.FileUploadResponse;
import com.example.comely_music_app.api.response.file.OssTokenInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileApi extends BaseApi {
    @POST("generate/file/uploading")
    Observable<BaseResult<FileUploadResponse>> upLoading(@Body FileUploadRequest fileUploadRequest);

    @POST("generate/file/upload-success")
    Observable<BaseResult<Boolean>> setUploadSuccess(@Body FileCommonRequest commonRequest);

    @GET("generate/file/oss-token/{username}")
    Observable<BaseResult<OssTokenInfo>> getOssToken(@Path("username") String username);
}
