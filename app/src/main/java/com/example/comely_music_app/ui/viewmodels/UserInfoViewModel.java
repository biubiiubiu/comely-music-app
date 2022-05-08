package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comely_music_app.api.response.UserInfo;

public class UserInfoViewModel extends ViewModel {
    private MutableLiveData<Boolean> isNewUser;
    private MutableLiveData<UserInfo> userInfo;
    private MutableLiveData<Boolean> isLogin;

    public MutableLiveData<Boolean> getIsNewUser() {
        if (isNewUser == null) {
            isNewUser = new MutableLiveData<>(null);
        }
        return isNewUser;
    }

    public void setIsNewUser(Boolean isnew) {
        if (isNewUser == null) {
            isNewUser = getIsNewUser();
        }
        isNewUser.postValue(isnew);
    }

    public MutableLiveData<UserInfo> getUserInfo() {
        if (userInfo == null) {
            userInfo = new MutableLiveData<>(null);
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo info) {
        if (userInfo == null) {
            userInfo = getUserInfo();
        }
        userInfo.postValue(info);
    }

    public MutableLiveData<Boolean> getIsLogin() {
        if (isLogin == null) {
            isLogin = new MutableLiveData<>(null);
        }
        return isLogin;
    }

    public void setIsLogin(Boolean isLog) {
        if (isLogin == null) {
            isLogin = getIsLogin();
        }
        isLogin.postValue(isLog);
    }
}
