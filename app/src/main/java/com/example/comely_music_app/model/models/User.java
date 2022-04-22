package com.example.comely_music_app.model.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class User extends BaseEntity<User> {

    private String id;
    private String username;
    private String nickname;
    private String password;
    private String gender;
    private String avatarId;

    /**
     * 用户身份，0-用户，1-管理员，2-歌手
     */
    private Integer role = 0;

    public User(){
        super();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("id=").append(getId())
                .append(", username=").append(getUsername())
                .append(", nickname=").append(getNickname())
                .append(", gender=").append(getGender())
                .append(", avatar_id=").append(getAvatarId())
                .append(", role=").append(getRole())
                .append(", created_time=").append(getCreatedTime())
                .append(", updated_time=").append(getUpdatedTime())
                .append("]");
        return sb.toString();
    }

}