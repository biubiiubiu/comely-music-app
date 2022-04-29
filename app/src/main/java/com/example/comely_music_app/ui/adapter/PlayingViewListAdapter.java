package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.request.music.MusicSelectRequest;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.ui.animation.MyClickListener;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.provider.MusicModelProvider;
import com.example.comely_music_app.ui.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewListAdapter.PlayingViewHolder> {
    private Context context;
    private View item;
    private boolean isLike, showCover;
    //    // 进度条
//    private SeekBar seekBar;
    private MusicModelProvider modelProvider;

    /**
     * 音乐信息列表
     */
    private List<MusicModel> musicModelList;

    /**
     * 一次获取10首音乐
     */
    private final static int NUM = 10;

    private List<String> titleList;
    private List<Drawable> coverList, backgroundList;

    private PlayingViewHolder holder;
    private int position;

    public PlayingViewListAdapter() {

    }

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false);
        // 初始化各个item list的数据
        context = item.getContext();
        modelProvider = new MusicModelProvider(context);
        return new PlayingViewHolder(item);
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
        isLike = false;
        showCover = true;

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
        return modelProvider.getPatchMusicModel(request);
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


    /**
     * 用来解析item_playing_view界面，变量绑定绑定控件
     */
    public static class PlayingViewHolder extends RecyclerView.ViewHolder {
        Context TAG;
        View itemView;
        // 这里按照界面布局从上往下写，代码可读性好
        ImageButton checkModuleBtn, searchBtn;

        FrameLayout blankFrame;
        TextView titleText, lyrics;
        ImageView coverImage;

        ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;

        // 进度条
        SeekBar seekBar;

        private boolean isLike, showCover;

        public PlayingViewHolder(@NonNull View itemView) {
            super(itemView);
            TAG = itemView.getContext();
            isLike = false;
            showCover = false;
            this.itemView = itemView;
            initViewBind(itemView);
//            initViewContents();
            setOnClick();

        }

        /**
         * 初始化界面数据
         */
//        private void initViewContents() {
        // 初始化歌曲封面和背景
//            initCoverAndBackground();
        // 初始化歌词
//            initLyrics();
//        }
//
//        private void initLyrics() {
//
//        }

//        public void initCoverAndBackground() {
//            String path = "/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg";
//            BitmapDrawable bd = (BitmapDrawable) Drawable.createFromPath(path);
//            // 设置背景毛玻璃
//            Blurry.with(itemView.getContext()).radius(50).from(bd.getBitmap()).into(coverImage);
//            itemView.setBackground(coverImage.getDrawable());
//            coverImage.setImageDrawable(Drawable.createFromPath(path));
//        }
        @SuppressLint("ResourceType")
        private void initViewBind(View itemView) {
            checkModuleBtn = itemView.findViewById(R.id.check_module);
            searchBtn = itemView.findViewById(R.id.search_btn);
            titleText = itemView.findViewById(R.id.music_title_text);
            coverImage = itemView.findViewById(R.id.music_cover_img);
            likeBtn = itemView.findViewById(R.id.like_btn);
            commentBtn = itemView.findViewById(R.id.comment_btn);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            moreBtn = itemView.findViewById(R.id.more_btn);

            blankFrame = itemView.findViewById(R.id.frame_blank_for_cover_lyrics);
            lyrics = itemView.findViewById(R.id.lyrics);
            seekBar = itemView.findViewById(R.id.process_sb);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setOnClick() {
            checkModuleBtn.setOnClickListener(v -> Toast.makeText(TAG, "切换模式", Toast.LENGTH_SHORT).show());
            searchBtn.setOnClickListener(v -> search());
            blankFrame.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
                @Override
                public void oneClick() {
                    showCover = !showCover;
                    changeCover2LyricStatus();
                }

                @Override
                public void doubleClick() {
                    isLike = true;
                    changeLikeStatus();
                }
            }));
            likeBtn.setOnClickListener(v -> changeLikeDisLike());
            commentBtn.setOnClickListener(v -> comment());
            downloadBtn.setOnClickListener(v -> download());
            moreBtn.setOnClickListener(v -> getMore());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 进度变化回调
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 触碰
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 放开
                }
            });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void changeLikeStatus() {
            if (isLike) {
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_liked));
            } else {
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_dislike));
            }
        }

        public void changeCover2LyricStatus() {
            if (showCover) {
                Animation mAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotaterepeat);
                coverImage.startAnimation(mAnimation);
                coverImage.setVisibility(View.VISIBLE);
                lyrics.setVisibility(View.INVISIBLE);
            } else {
                coverImage.clearAnimation();
                coverImage.setVisibility(View.INVISIBLE);
                lyrics.setVisibility(View.VISIBLE);
            }
        }

        private void getMore() {
            Toast.makeText(TAG, "更多", Toast.LENGTH_SHORT).show();
        }

        private void download() {
            Toast.makeText(TAG, "下载", Toast.LENGTH_SHORT).show();
        }

        private void comment() {
            Toast.makeText(TAG, "评论", Toast.LENGTH_SHORT).show();
        }

        private void changeLikeDisLike() {
            isLike = !isLike;
            changeLikeStatus();
        }

        private void search() {
            Toast.makeText(TAG, "搜索", Toast.LENGTH_SHORT).show();
        }

        public void setBackground(Drawable drawable) {
            itemView.setBackground(drawable);
        }

        public void setTitle(String title) {
            titleText.setText(title);
        }

        public void setCover(Drawable drawable) {
            coverImage.setImageDrawable(drawable);
        }
    }
}
