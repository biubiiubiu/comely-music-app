package com.example.comely_music_app.api.service.impl;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.api.apis.UserApi;
import com.example.comely_music_app.api.base.ApiManager;
import com.example.comely_music_app.api.base.BaseObserver;
import com.example.comely_music_app.api.base.BaseResult;
import com.example.comely_music_app.api.request.user.LoginRequest;
import com.example.comely_music_app.api.response.user.UserInfo;
import com.example.comely_music_app.api.service.UserService;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;

import io.reactivex.rxjava3.core.Observable;

public class UserServiceImpl implements UserService {
    UserApi userApi;
    UserInfoViewModel userInfoViewModel;

    public UserServiceImpl(UserInfoViewModel viewModel) {
        userApi = ApiManager.getInstance().getApiService(UserApi.class);
        userInfoViewModel = viewModel;
    }

    @Override
    public void judgeNewUser(LoginRequest loginRequest) {
        Observable<BaseResult<Boolean>> loginObservable = userApi.judgeNewUser(loginRequest);
        loginObservable.subscribe(new BaseObserver<Boolean>(true) {
            @Override
            public void onSuccess(Boolean isNewUser) {
                userInfoViewModel.setIsNewUser(isNewUser);
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Boolean response) {
                Log.e("Login", "网络错误！" + errorMsg);
            }

            @Override
            public void onError(String msg) {
                Log.e("Login", "Error！" + msg);
            }
        });
    }

    @Override
    public void loginOrRegister(LoginRequest loginRequest) {
        Observable<BaseResult<UserInfo>> loginObservable = userApi.loginOrRegister(loginRequest);
        loginObservable.subscribe(new BaseObserver<UserInfo>(true) {
            @Override
            public void onSuccess(UserInfo response) {
                userInfoViewModel.setUserInfo(response);
            }

            @Override
            public void onFail(int errorCode, String errorMsg, UserInfo response) {
                UserInfo info = new UserInfo();
                // 这里触发 livedata报密码错误
                userInfoViewModel.setUserInfo(info);
                Log.e("Login", "网络错误！" + errorMsg);
            }

            @Override
            public void onError(String msg) {
                Log.e("Login", "Error！" + msg);
            }
        });
    }

    @Override
    public void getLoginStatus(String username) {
        Observable<BaseResult<Boolean>> loginStatus = userApi.getLoginStatus(username);
        loginStatus.subscribe(new BaseObserver<Boolean>(true) {
            @Override
            public void onSuccess(Boolean isLogin) {
                userInfoViewModel.setIsLogin(isLogin);
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Boolean response) {
                Log.e("LoginStatus", "网络错误！" + errorMsg);
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
