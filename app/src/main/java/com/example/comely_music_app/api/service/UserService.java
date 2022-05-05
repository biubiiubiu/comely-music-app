package com.example.comely_music_app.api.service;

import com.example.comely_music_app.api.request.user.LoginRequest;

public interface UserService {
    void judgeNewUser(LoginRequest loginRequest);

    void loginOrRegister(LoginRequest loginRequest);

    void getLoginStatus(String username);
}
