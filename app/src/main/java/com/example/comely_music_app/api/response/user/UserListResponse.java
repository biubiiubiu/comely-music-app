package com.example.comely_music_app.api.response.user;

import com.example.comely_music_app.entity.User;

import java.util.List;

import lombok.Data;

@Data
public class UserListResponse {
    List<User> userList;
}
