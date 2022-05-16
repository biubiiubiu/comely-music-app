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
public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {
    private final MusicModelProvider modelProvider;

    private final static int INIT_NUM = 6;
    private final static int ADD_NUM = 3;
    private List<MusicModel> musicModelList;

    private int position;

    private final PlayingViewModel playingViewModel;

    public PlayingViewListAdapter(PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
        // 初始化各个item list的数据
        modelProvider = new MusicModelProvider();

        List<String> tags = new ArrayList<>();
        tags.add("古风");
        initMusicModelListByTags(tags);
    }

//    public PlayingViewListAdapter(Context applicationContext, PlayingViewModel playingViewModel) {
//        this.playingViewModel = playingViewModel;
//        // 初始化各个item list的数据
//        modelProvider = new MusicModelProvider(applicationContext);
//        List<String> tags = new ArrayList<>();
//        tags.add("古风");
//        initMusicModelListByTags(tags);
//    }

    /**
     * 每次切换当前item调用
     */
    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("TAG", "onBindViewHolder: bind..." + position);
        // 重新获取item位置、当前位置的holder
        this.position = position;

        // 初始化音乐队列
        initCurrentViewContents(holder);
    }

    @Override
    public int getItemCount() {
        if (musicModelList != null) {
            return musicModelList.size();
        }
        return INIT_NUM;
    }

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);

        Log.d("TAG", "onCreateViewHolder: create.....");
        return new PlayingViewHolder(item, playingViewModel);
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

    public void setMusicModelList(List<MusicModel> list) {
        if (list != null && list.size() > 0) {
            musicModelList = list;
        }
    }

    /**
     * 解析数据到界面上
     */
    private void initCurrentViewContents(PlayingViewHolder holder) throws IOException {
        if (musicModelList != null) {
            MusicModel currentModel = musicModelList.get(position);
            holder.setTitle(currentModel.getName());
            holder.setCoverAndBk(currentModel);
        }
    }
}
