package com.example.comely_music_app.network.apis;

import com.example.comely_music_app.network.base.BaseResult;
import com.example.comely_music_app.network.request.LoginRequest;
import com.example.comely_music_app.network.request.UserUpdateRequest;
import com.example.comely_music_app.network.response.UserInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi extends BaseApi {
//    @GET("generate/user/list")
//    Call<ResponseBody> getUserList();

    @POST("generate/user/judge-newuser")
    Observable<BaseResult<Boolean>> judgeNewUser(@Body LoginRequest loginRequest);

    @POST("generate/user/login-register")
    Observable<BaseResult<UserInfo>> loginOrRegister(@Body LoginRequest loginRequest);

    @GET("generate/user/login-status/{username}")
    Observable<BaseResult<Boolean>> getLoginStatus(@Path("username") String username);

    @PUT("generate/user/logout/{username}")
    Observable<BaseResult<Void>> logout(@Path("username") String username);

    @PUT("generate/user/update")
    Observable<BaseResult<Void>> updateUserInfo(@Body UserUpdateRequest userUpdateRequest);

}
