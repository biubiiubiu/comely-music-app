package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.provider.MusicModelProvider;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.FileOperationUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import lombok.SneakyThrows;

/**
 * 用于修改PlayingViewPager界面数据
 */
public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {
    private View item;

    private MusicModelProvider modelProvider;

    /**
     * 一次获取10首音乐
     */
    private final static int NUM = 6;
    private List<MusicModel> musicModelList;

    private PlayingViewHolder holder;
    private int position;

    private final PlayingViewModel playingViewModel;

    public PlayingViewListAdapter(Context applicationContext, PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
        // 初始化各个item list的数据
        modelProvider = new MusicModelProvider(applicationContext);
        initMusicModelList(PlayerModule.RANDOM);
    }

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);
        return new PlayingViewHolder(item, playingViewModel);
    }

    /**
     * 每次切换当前item调用
     */
    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重新获取item位置、当前位置的holder
        this.position = position;
        this.holder = holder;

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


    /**
     * Retrofit2 MusicService，初始化一次音乐信息，一次获取NUM首
     */
    private void initMusicModelList(PlayerModule module) {
        MusicSelectRequest request = new MusicSelectRequest(module, NUM);
        modelProvider.getPatchMusicModel(request, playingViewModel);
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
                    holder.setBackground(getDefaultBk());
                    holder.setCover(getDefaultCover());
                    return;
                }
            }
            holder.setBackground(getBkFromPath(coverPath));
            holder.setCover(getCoverFromPath(coverPath));
        }
    }

    private Drawable getDefaultCover() {
        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bd
                = (BitmapDrawable) item.getResources().getDrawable(R.drawable.avatar_music);
        // 设置背景毛玻璃
        Blurry.with(item.getContext()).radius(50).from(bd.getBitmap()).into(holder.coverImage);
        return holder.coverImage.getDrawable();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getDefaultBk() {
        return item.getResources().getDrawable(R.drawable.avatar_music);
    }

    private Drawable getBkFromPath(String path) {
        // 注意这里用到了cover的ImageView，所以需要先初始化背景，然后在初始化cover
        BitmapDrawable bd = (BitmapDrawable) Drawable.createFromPath(path);
        // 设置背景毛玻璃
        Blurry.with(item.getContext()).radius(50).from(bd.getBitmap()).into(holder.coverImage);
        return holder.coverImage.getDrawable();
    }

    private Drawable getCoverFromPath(String path) {
        return Drawable.createFromPath(path);
    }

}
