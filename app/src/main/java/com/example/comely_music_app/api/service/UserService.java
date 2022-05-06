package com.example.comely_music_app.api.service;

import com.example.comely_music_app.api.request.user.LoginRequest;
import com.example.comely_music_app.api.request.user.UserUpdateRequest;

public interface UserService {
    void judgeNewUser(LoginRequest loginRequest);

    void loginOrRegister(LoginRequest loginRequest);

    void getLoginStatus(String username);

    void logout(String username);

    void update(UserUpdateRequest userUpdateRequest);
}
