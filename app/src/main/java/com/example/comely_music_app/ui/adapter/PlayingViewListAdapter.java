package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.request.MusicSelectByModuleRequest;
import com.example.comely_music_app.api.request.MusicSelectByTagsRequest;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.provider.MusicModelProvider;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.FileOperationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

/**
 * 用于修改PlayingViewPager界面数据
 */
public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {
    private final MusicModelProvider modelProvider;

    private final static int NUM = 6;
    private List<MusicModel> musicModelList;

    private int position;

    private final PlayingViewModel playingViewModel;

    public PlayingViewListAdapter(Context applicationContext, PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
        // 初始化各个item list的数据
        modelProvider = new MusicModelProvider(applicationContext);
        initMusicModelList();
    }

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
        return NUM;
    }


    @Override
    public void onViewRecycled(@NonNull PlayingViewHolder holder) {
        super.onViewRecycled(holder);

        Log.d("TAG", "=============== onViewRecycled...回收.."+holder.getItemId());
    }

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);

        Log.d("TAG", "onCreateViewHolder: create.....");
        return new PlayingViewHolder(item, playingViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        Log.d("TAG", "=============== onCreateViewHolder: create....."+position);
    }

    @Override
    public int findRelativeAdapterPositionIn(@NonNull RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, @NonNull RecyclerView.ViewHolder viewHolder, int localPosition) {

        Log.d("TAG", "=============== onCreateViewHolder: create....."+localPosition);
        return super.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition);

    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        Log.d("TAG", "=============== setHasStableIds.....");
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        Log.d("TAG", "=============== getItemId....."+position);
        return super.getItemId(position);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull PlayingViewHolder holder) {
        Log.d("TAG", "=============== onFailedToRecycleView.."+holder.getItemId());
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PlayingViewHolder holder) {
        Log.d("TAG", "=============== onViewAttachedToWindow..."+holder.getItemId());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PlayingViewHolder holder) {
        Log.d("TAG", "=============== onViewDetachedFromWindow.."+holder.getItemId());
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        Log.d("TAG", "=============== registerAdapterDataObserver...");
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        Log.d("TAG", "=============== unregisterAdapterDataObserver....");
        super.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("TAG", "=============== onAttachedToRecyclerView.....");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("TAG", "=============== onDetachedFromRecyclerView....");
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void setStateRestorationPolicy(@NonNull StateRestorationPolicy strategy) {
        Log.d("TAG", "=============== setStateRestorationPolicy.....");
        super.setStateRestorationPolicy(strategy);
    }


    /**
     * Retrofit2 MusicService，初始化一次音乐信息，一次获取NUM首
     */
    private void initMusicModelList() {
//        MusicSelectByModuleRequest request = new MusicSelectByModuleRequest(PlayerModule.RANDOM, NUM);
//        modelProvider.getPatchMusicModelByModule(request, playingViewModel);

        List<String> tags = new ArrayList<>();
        tags.add("古风");
        MusicSelectByTagsRequest request = new MusicSelectByTagsRequest();
        request.setNum(NUM).setTags(tags);
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
            String coverPath = currentModel.getCoverLocalPath();
            if (coverPath == null) {
                String audioPath = currentModel.getAudioLocalPath();
                coverPath = audioPath.substring(0, audioPath.lastIndexOf(".")) + ".jpg";
            }
            File file = new File(coverPath);
            if (!file.exists()) {
                // 如果没有指定封面，就从mp3内嵌图片中获取图片
                String audioLocalPath = musicModelList.get(position).getAudioLocalPath();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(audioLocalPath);
                byte[] embedCover = mmr.getEmbeddedPicture();
                if (embedCover != null && embedCover.length > 0) {
                    FileOperationUtils.writeBytesToFile(embedCover, coverPath);
                } else {
                    // 使用默认图片作为封面
                    int index = (int) (Math.random() * 7) + 1;
                    holder.setDefaultCoverAndBk(index);
//                    holder.setDefaultBk(index);
//                    holder.setDefaultCover(index);
                    return;
                }
            }
            holder.setCoverAndBkFromPath(coverPath);
        }
    }
}
