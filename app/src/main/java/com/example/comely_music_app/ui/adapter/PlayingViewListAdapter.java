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
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.animation.MyClickListener;

import jp.wasabeef.blurry.Blurry;

public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {
//    private PlayingViewHolder holder;
//    private Context TAG;
//    View item;
//    ImageButton checkModuleBtn, searchBtn;
//    TextView titleText, lyrics;
//    ImageView coverImage;
//    FrameLayout blankFrame;
//    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;
//    private boolean isLike, showCover;
//    // 进度条
//    private SeekBar seekBar;

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, int position) {
//        this.holder = holder;
//        item = holder.itemView;
//        TAG = item.getContext();
//        isLike = false;
//        showCover = true;
//        initViewBind(holder);
//        initViewContents();
//        setOnClick();
    }
//
    @Override
    public int getItemCount() {
        return 10;
    }
//
//    /**
//     * 初始化界面数据
//     */
//    private void initViewContents() {
//        // 初始化歌曲封面和背景
//        initCoverAndBackground();
//        // 初始化歌词
//        initLyrics();
//    }
//
//    private void initLyrics() {
//
//    }
//
//    private void initCoverAndBackground() {
//        String path = "/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg";
//        holder.initCoverAndBackground(path);
//    }
//
//
//    @SuppressLint("ResourceType")
//    private void initViewBind(PlayingViewHolder holder) {
//        checkModuleBtn = holder.checkModuleBtn;
//        searchBtn = holder.searchBtn;
//        titleText = holder.titleText;
//        coverImage = holder.coverImage;
//        likeBtn = holder.likeBtn;
//        commentBtn = holder.commentBtn;
//        downloadBtn = holder.downloadBtn;
//        moreBtn = holder.moreBtn;
//
//        blankFrame = holder.blankFrame;
//        lyrics = holder.lyrics;
//        seekBar = holder.seekBar;
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setOnClick() {
//        checkModuleBtn.setOnClickListener(v -> Toast.makeText(TAG, "切换模式", Toast.LENGTH_SHORT).show());
//        searchBtn.setOnClickListener(v -> search());
//        blankFrame.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
//            @Override
//            public void oneClick() {
//                showCover = !showCover;
//                changeCover2LyricStatus();
//            }
//
//            @Override
//            public void doubleClick() {
//                isLike = true;
//                changeLikeStatus();
//            }
//        }));
//        likeBtn.setOnClickListener(v -> changeLikeDisLike());
//        commentBtn.setOnClickListener(v -> comment());
//        downloadBtn.setOnClickListener(v -> download());
//        moreBtn.setOnClickListener(v -> getMore());
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // 进度变化回调
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // 触碰
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // 放开
//            }
//        });
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void changeLikeStatus() {
//        holder.changeLikeStatus(isLike);
//    }
//
//    private void changeCover2LyricStatus() {
//        holder.changeCover2LyricStatus(showCover);
//    }
//
//    private void getMore() {
//        Toast.makeText(TAG, "更多", Toast.LENGTH_SHORT).show();
//    }
//
//    private void download() {
//        Toast.makeText(TAG, "下载", Toast.LENGTH_SHORT).show();
//    }
//
//    private void comment() {
//        Toast.makeText(TAG, "评论", Toast.LENGTH_SHORT).show();
//    }
//
//    private void changeLikeDisLike() {
//        isLike = !isLike;
//        changeLikeStatus();
//    }
//
//    private void changeCoverLyric() {
//        Toast.makeText(TAG, "点击封面", Toast.LENGTH_SHORT).show();
//    }
//
//    private void search() {
//        Toast.makeText(TAG, "搜索", Toast.LENGTH_SHORT).show();
//    }
}
