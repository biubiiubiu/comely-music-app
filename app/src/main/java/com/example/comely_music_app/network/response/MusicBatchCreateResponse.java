package com.example.comely_music_app.network.response;

import java.util.List;

import lombok.Data;

/**
 * description: 批量创建music结果
 *
 * @author: zhangtian
 * @since: 2022-05-08 11:26
 */
@Data
public class MusicBatchCreateResponse {
    /**
     * 创建成功数量
     */
    private Integer successNum;

    /**
     * 创建失败数量
     */
    private Integer failedNum;

    /**
     * 创建成功音乐名list
     */
    private List<String> successMusicList;

    /**
     * 创建失败音乐名list
     */
    private List<String> failedMusicList;

    public MusicBatchCreateResponse setSuccessNum(Integer successNum) {
        this.successNum = successNum;
        return this;
    }

    public MusicBatchCreateResponse setFailedNum(Integer failedNum) {
        this.failedNum = failedNum;
        return this;
    }

    public MusicBatchCreateResponse setSuccessMusicList(List<String> successMusicList) {
        this.successMusicList = successMusicList;
        return this;
    }

    public MusicBatchCreateResponse setFailedMusicList(List<String> failedMusicList) {
        this.failedMusicList = failedMusicList;
        return this;
    }
}
