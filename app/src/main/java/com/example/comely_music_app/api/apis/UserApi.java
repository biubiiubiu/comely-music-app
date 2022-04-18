package com.example.comely_music_app.api.apis;

import com.example.comely_music_app.R;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi extends BaseApi {
//    @GET("generate/user/list")
//    Call<ResponseBody> getUserList();

    @GET("generate/user/list")
    Call<R> getUserList();
}
