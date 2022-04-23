package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
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

public class PlayingViewHolder extends RecyclerView.ViewHolder {
    Context TAG;
    View itemView;
    ImageButton checkModuleBtn, searchBtn;
    TextView titleText, lyrics;
    ImageView coverImage;
    FrameLayout blankFrame;
    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;
    // 进度条
    SeekBar seekBar;

    private boolean isLike, showCover;

    public PlayingViewHolder(@NonNull View itemView) {
        super(itemView);
        TAG = itemView.getContext();
        this.itemView = itemView;
        initViewBind(itemView);
        initViewContents();
        setOnClick();
    }

    /**
     * 初始化界面数据
     */
    private void initViewContents() {
        // 初始化歌曲封面和背景
        initCoverAndBackground();
        // 初始化歌词
        initLyrics();
    }

    private void initLyrics() {

    }

    public void initCoverAndBackground() {
        String path = "/storage/emulated/0/$MuMu共享文件夹/uploadTest.jpg";
        BitmapDrawable bd = (BitmapDrawable) Drawable.createFromPath(path);
        // 设置背景毛玻璃
        Blurry.with(itemView.getContext()).radius(50).from(bd.getBitmap()).into(coverImage);
        itemView.setBackground(coverImage.getDrawable());
        coverImage.setImageDrawable(Drawable.createFromPath(path));
    }


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
}
