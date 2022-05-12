package com.example.comely_music_app.network.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String password;
    private String nickname;
    private String gender;
    // "用户身份，0-用户，1-管理员，2-歌手"
    private Integer role = 0;

}
