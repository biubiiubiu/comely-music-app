package com.example.comely_music_app.network.request;

import com.example.comely_music_app.enums.PlayerModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicSelectByModuleRequest {
    private PlayerModule module;
    private Integer num;
}
