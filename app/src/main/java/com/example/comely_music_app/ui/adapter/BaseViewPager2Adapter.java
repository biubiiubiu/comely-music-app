package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.List;

import lombok.SneakyThrows;

public abstract class BaseViewPager2Adapter extends RecyclerView.Adapter<MainPlayingViewHolder> {
    private int position;
    private final PlayingViewModel playingViewModel;
    private List<MusicModel> musicModelList;

    public BaseViewPager2Adapter(PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
    }

    @NonNull
    @Override
    public MainPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
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

    public abstract void initCurrentViewContents(MainPlayingViewHolder holder);

    @Override
    public int getItemCount() {
        return musicModelList == null ? 0 : musicModelList.size();
    }
}
