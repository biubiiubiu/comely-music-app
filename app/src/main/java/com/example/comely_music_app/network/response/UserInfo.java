package com.example.comely_music_app.network.response;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo info = (UserInfo) o;
        return Objects.equals(isNewUser, info.isNewUser) && Objects.equals(username, info.username) && Objects.equals(nickname, info.nickname) && Objects.equals(gender, info.gender) && Objects.equals(avatarId, info.avatarId) && Objects.equals(role, info.role) && Objects.equals(loginToken, info.loginToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNewUser, username, nickname, gender, avatarId, role, loginToken);
    }
}

