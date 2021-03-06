package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.network.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.provider.MusicModelProvider;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

/**
 * 用于修改PlayingViewPager界面数据
 */
public class MainPlayingViewAdapter extends RecyclerView.Adapter<MainPlayingViewHolder> {
    private final MusicModelProvider modelProvider;

    private final static int INIT_NUM = 6;
    private final static int ADD_NUM = 3;
    private List<MusicModel> musicList_endlessModule;

    private int position;

    private final PlayingViewModel playingViewModel;

    public MainPlayingViewAdapter(PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
        // 初始化各个item list的数据
        modelProvider = new MusicModelProvider();

        // 从远端获取
        List<String> tags = new ArrayList<>();
        tags.add("古风");
        initMusicModelListByTags(tags);
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
        return musicList_endlessModule != null ? musicList_endlessModule.size() : 0;
    }

    @NonNull
    @Override
    public MainPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);

        Log.d("TAG", "onCreateViewHolder: create.....");
        return new MainPlayingViewHolder(item, playingViewModel);
    }

    /**
     * Retrofit2 MusicService，初始化一次音乐信息，一次获取NUM首
     */
    private void initMusicModelListByTags(List<String> tags) {
        MusicSelectByTagsRequest request = new MusicSelectByTagsRequest();
        request.setNum(INIT_NUM).setTags(tags);
        modelProvider.getPatchMusicModelByTag(request, playingViewModel);
    }

//    private void initMusicModelListByModule(PlayerModule module) {
//        MusicSelectByModuleRequest request = new MusicSelectByModuleRequest(module, NUM);
//        modelProvider.getPatchMusicModelByModule(request, playingViewModel);
//    }

    public void addMusicListByTags(List<String> tags) {
        MusicSelectByTagsRequest request = new MusicSelectByTagsRequest();
        request.setNum(ADD_NUM).setTags(tags);
        modelProvider.getPatchMusicModelByTag(request, playingViewModel);
    }


    public void setMusicList_endlessModule(List<MusicModel> list) {
        if (list != null && list.size() > 0) {
            musicList_endlessModule = list;
        }
    }


    public List<MusicModel> getMusicList_endlessModule() {
        return musicList_endlessModule;
    }


    /**
     * 解析数据到界面上
     */
    private void initCurrentViewContents(MainPlayingViewHolder holder) throws IOException {
        MusicModel currentModel = musicList_endlessModule.get(position);
        holder.setTitle(currentModel.getName());
        holder.setCoverAndBk(currentModel);
    }
}
