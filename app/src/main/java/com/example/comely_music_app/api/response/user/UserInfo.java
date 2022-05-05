package com.example.comely_music_app.api.response.user;

import lombok.Data;

/**
 * description: 登录返回结果
 *
 */
@Data
public class UserInfo {
    // 首次登录
    private Boolean isNewUser;
    private String username;
    private String nickname;
    private String gender;
    private String avatarId;
    private Integer role;
    private String loginToken;

    public UserInfo setIsNewUser(Boolean isNewUser){
        this.isNewUser=isNewUser;
        return this;
    }

    public UserInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserInfo setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserInfo setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserInfo setAvatarId(String avatarId) {
        this.avatarId = avatarId;
        return this;
    }

    public UserInfo setRole(Integer role) {
        this.role = role;
        return this;
    }

    public UserInfo setLoginToken(String loginToken) {
        this.loginToken = loginToken;
        return this;
    }
}

