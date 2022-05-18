package com.example.comely_music_app.network.base;

import com.example.comely_music_app.config.ServerConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiManager {
    private static ApiManager apiManager;
    private final Retrofit retrofit;

    public static ApiManager getInstance() {
        if (apiManager == null) {
            synchronized (Object.class) {
                if (apiManager == null) {
                    apiManager = new ApiManager();
                }
            }
        }
        return apiManager;
    }

    public ApiManager() {
        //添加log拦截器
        //okhttp默认的10秒
        OkHttpClient client = new OkHttpClient.Builder()
                //添加log拦截器
//                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)) //okhttp默认的
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerConfig.BASE_RUL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                //支持RxJava
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();
    }

    public <T> T getApiService(final Class<T> service) {
        return retrofit.create(service);
    }

}
