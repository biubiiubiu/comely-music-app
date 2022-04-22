package com.example.comely_music_app.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;

public class PlayingViewHolder extends RecyclerView.ViewHolder{
    ImageButton backBtn, searchBtn;
    TextView titleText;
    ImageView coverImage;
    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;
    private volatile boolean isPalying = false, isLike = false;
    private PlayingViewModel mViewModel;
    private Context TAG;

    public PlayingViewHolder(@NonNull View itemView) {
        super(itemView);
        TAG = itemView.getContext();
        initViewBind(itemView);
        setOnClick();
    }

    private void initViewBind(View itemView) {
        backBtn = itemView.findViewById(R.id.check_module);
        searchBtn = itemView.findViewById(R.id.search_btn);
        titleText = itemView.findViewById(R.id.music_title_text);
        coverImage = itemView.findViewById(R.id.music_cover_img);
        likeBtn = itemView.findViewById(R.id.like_btn);
        commentBtn = itemView.findViewById(R.id.comment_btn);
        downloadBtn = itemView.findViewById(R.id.download_btn);
        moreBtn = itemView.findViewById(R.id.more_btn);
    }

    private void setOnClick(){
        backBtn.setOnClickListener(v -> Toast.makeText(TAG, "返回", Toast.LENGTH_SHORT).show());
        searchBtn.setOnClickListener(v->search());
        coverImage.setOnClickListener(v->changeCoverLyric());
        likeBtn.setOnClickListener(v->changeLikeDisLike());
        commentBtn.setOnClickListener(v->comment());
        downloadBtn.setOnClickListener(v->comment());
        moreBtn.setOnClickListener(v->getMore());
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
        if (isPalying) {
            isPalying = false;
            Toast.makeText(TAG, "取消点赞", Toast.LENGTH_SHORT).show();
        } else {
            isPalying = true;
            Toast.makeText(TAG, "点赞", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeCoverLyric() {
        Toast.makeText(TAG, "点击封面", Toast.LENGTH_SHORT).show();
    }

    private void search() {
        Toast.makeText(TAG, "搜索", Toast.LENGTH_SHORT).show();
    }

    private void changePlayPauseStatus() {
        if (isPalying) {
            isPalying = false;
            Toast.makeText(TAG, "暂停", Toast.LENGTH_SHORT).show();
        } else {
            isPalying = true;
            Toast.makeText(TAG, "播放", Toast.LENGTH_SHORT).show();
        }
    }
}
