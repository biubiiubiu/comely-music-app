package com.example.comely_music_app.network.service;

import com.example.comely_music_app.network.request.LoginRequest;
import com.example.comely_music_app.network.request.UserUpdateRequest;

public interface UserService {
    void judgeNewUser(LoginRequest loginRequest);

    void loginOrRegister(LoginRequest loginRequest);

    void getLoginStatus(String username);

    void logout(String username);

    void update(UserUpdateRequest userUpdateRequest);
}
