package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;

/**
 * 用于修改PlayingViewPager界面数据
 */
public class MainPlayingViewAdapter2 extends RecyclerView.Adapter<MainPlayingViewHolder> {
    private List<MusicModel> musicList_playlistModule;

    private int position;

    private final PlayingViewModel playingViewModel;

    public MainPlayingViewAdapter2(PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
    }

    /**
     * 每次切换当前item调用
     */
    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull MainPlayingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重新获取item位置、当前位置的holder
        this.position = position;

        // 初始化音乐队列
        initCurrentViewContents(holder);
    }

    @Override
    public int getItemCount() {
        return musicList_playlistModule != null ? musicList_playlistModule.size() : 0;
    }

    @NonNull
    @Override
    public MainPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);

        return new MainPlayingViewHolder(item, playingViewModel);
    }

    public void setMusicList_playlistModule(List<MusicModel> list) {
        if (list != null && list.size() > 0) {
            musicList_playlistModule = list;
        }
    }

    /**
     * 解析数据到界面上
     */
    private void initCurrentViewContents(MainPlayingViewHolder holder) throws IOException {
        if (musicList_playlistModule != null) {
            MusicModel currentModel = musicList_playlistModule.get(position);
            holder.setTitle(currentModel.getName());
            holder.setCoverAndBk(currentModel);
        }
    }
}
