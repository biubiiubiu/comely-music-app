package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

/**
 * 用于修改PlayingViewPager界面数据
 */
public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {
    private View item;

    private MusicModelProvider modelProvider;

    /**
     * 一次获取10首音乐
     */
    private final static int NUM = 10;
    private List<String> titleList;
    private List<Drawable> coverList, backgroundList;

    private PlayingViewHolder holder;
    private int position;

    private PlayingViewModel playingViewModel;

    public PlayingViewListAdapter(PlayingViewModel playingViewModel) {
        this.playingViewModel = playingViewModel;
    }

    public PlayingViewHolder getHolder() {
        return holder;
    }

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);
        // 初始化各个item list的数据
        modelProvider = new MusicModelProvider(item.getContext());
        return new PlayingViewHolder(item, playingViewModel);
    }

    /**
     * 每次切换当前item调用
     */
    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重新获取item位置、当前位置的holder
        this.position = position;
        this.holder = holder;

        if (titleList == null) {
            titleList = initTitleList();
        }
        if (backgroundList == null) {
            // 这里需要先设置背景，因为背景借用了封面的imageView
            backgroundList = initBackgroundList();
        }
        if (coverList == null) {
            coverList = initCoverList();
        }
        // 刷新点赞按钮样式、封面歌词状态

        // 使用holder把数据解析到当前位置的itemView上
        initViewContents(holder);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    /**
     * ==================================获取数据================================
     */
    /**
     * Retrofit2 MusicService，初始化一次音乐信息，一次获取NUM首
     */
    private List<MusicModel> initMusicModelList(PlayerModule module) {
        MusicSelectRequest request = new MusicSelectRequest(module, NUM);
        return modelProvider.getPatchMusicModel(request, playingViewModel);
    }

    private List<String> initTitleList() {
        List<String> ttList = new ArrayList<>();
        ttList.add("稻香-周杰伦");
        ttList.add("江南-林俊杰");
        ttList.add("本草纲目-周杰伦");
        ttList.add("TA-不是花火啊");
        ttList.add("起风了-买辣椒也用券");
        ttList.add("嘉宾-张远");
        ttList.add("哪里都是你-队长");
        ttList.add("勇气-梁静茹");
        ttList.add("明明就-周杰伦");
        ttList.add("庐州月-许嵩");
        return ttList;
    }

    private List<Drawable> initBackgroundList() {
        List<Drawable> bkList = new ArrayList<>();
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        bkList.add(getBkFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        return bkList;
    }

    private List<Drawable> initCoverList() {
        List<Drawable> coverList = new ArrayList<>();
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        coverList.add(getCoverFromPath("/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg"));
        return coverList;
    }

    private List<String> initLyrics() {
        List<String> lyricList = new ArrayList<>();
        lyricList.add("稻香-周杰伦-歌词");
        lyricList.add("江南-林俊杰-歌词");
        lyricList.add("本草纲目-周杰伦-歌词");
        lyricList.add("TA-不是花火啊-歌词");
        lyricList.add("起风了-买辣椒也用券-歌词");
        lyricList.add("嘉宾-张远-歌词");
        lyricList.add("哪里都是你-队长-歌词");
        lyricList.add("勇气-梁静茹-歌词");
        lyricList.add("明明就-周杰伦-歌词");
        lyricList.add("庐州月-许嵩-歌词");
        return lyricList;
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

    /** ==================================利用holder把数据解析到view上================================ */

    /**
     * 初始化界面数据
     */
    private void initViewContents(PlayingViewHolder holder) {
        holder.setTitle(titleList.get(position));
        holder.setBackground(backgroundList.get(position));
        holder.setCover(coverList.get(position));

        // 初始化歌曲封面和背景
//        initCoverAndBackground();
        // 初始化歌词
//        initLyrics();
    }


}
