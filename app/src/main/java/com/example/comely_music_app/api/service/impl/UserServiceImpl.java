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
import com.example.comely_music_app.api.request.user.UserUpdateRequest;
import com.example.comely_music_app.api.response.user.UserInfo;
import com.example.comely_music_app.api.service.UserService;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;

import io.reactivex.rxjava3.core.Observable;

public class UserServiceImpl implements UserService {
    UserApi userApi;
    UserInfoViewModel userInfoViewModel;

    public UserServiceImpl() {
    }

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

    @Override
    public void logout(String username) {
        Observable<BaseResult<Void>> logout = userApi.logout(username);
        logout.subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                Log.d("Logout", "onSuccess: 退出登录");
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {
                Log.e("Logout", "网络错误！" + errorMsg);
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void update(UserUpdateRequest request) {
        Observable<BaseResult<Void>> updateResult = userApi.updateUserInfo(request);
        updateResult.subscribe(new BaseObserver<Void>(false) {
            @Override
            public void onSuccess(Void o) {
                // 修改成功
                UserInfo newInfo = new UserInfo();
                if (request.getUsername() != null) {
                    newInfo.setUsername(request.getUsername());
                }
                if (request.getNickname() != null && request.getNickname().length() > 0) {
                    newInfo.setNickname(request.getNickname());
                }
                if (request.getGender() != null) {
                    newInfo.setGender(request.getGender());
                }
                if (request.getRole() != null) {
                    newInfo.setRole(request.getRole());
                }
                userInfoViewModel.setUserInfo(newInfo);
            }

            @Override
            public void onFail(int errorCode, String errorMsg, Void response) {
                // 修改失败
                Log.e("UpdateUserInfo", "网络错误！" + errorMsg);
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
