package com.example.comely_music_app.network.response;

import com.example.comely_music_app.ui.models.PlaylistModel;
import java.util.List;

import lombok.Data;

/**
 * description: 查询某个用户的歌单
 *
 * @author: zhangtian
 * @since: 2022-05-12 20:47
 */
@Data
public class UserPlaylistsSelectResponse {
    List<PlaylistModel> playlistInfoList;
}
