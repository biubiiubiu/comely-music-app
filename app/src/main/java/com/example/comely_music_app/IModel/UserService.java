package com.example.comely_music_app.IModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("generate/user/list")
    Call<ResponseBody> getUserList();
}
