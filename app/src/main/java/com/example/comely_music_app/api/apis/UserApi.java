package com.example.comely_music_app.api.apis;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.user.LoginRequest;
import com.example.comely_music_app.api.request.user.UserUpdateRequest;
import com.example.comely_music_app.api.response.user.UserInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi extends BaseApi {
//    @GET("generate/user/list")
//    Call<ResponseBody> getUserList();

    @GET("generate/user/list")
    Call<R> getUserList();

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
