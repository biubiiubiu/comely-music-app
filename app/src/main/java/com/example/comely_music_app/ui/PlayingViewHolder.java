package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.animation.MyClickListener;

public class PlayingViewHolder extends RecyclerView.ViewHolder {
    private Context TAG;
    View itemView;
    ImageButton checkModuleBtn, searchBtn;
    TextView titleText;
    ImageView coverImage;
    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;
    private boolean isLike = false;
    private PlayingViewModel mViewModel;
    // 进度条
    private SeekBar seekBar;

    public PlayingViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        TAG = itemView.getContext();
        initViewBind(itemView);
        setOnClick();
    }


    private void initViewBind(View itemView) {
        checkModuleBtn = itemView.findViewById(R.id.check_module);
        searchBtn = itemView.findViewById(R.id.search_btn);
        titleText = itemView.findViewById(R.id.music_title_text);
        coverImage = itemView.findViewById(R.id.music_cover_img);
        likeBtn = itemView.findViewById(R.id.like_btn);
        commentBtn = itemView.findViewById(R.id.comment_btn);
        downloadBtn = itemView.findViewById(R.id.download_btn);
        moreBtn = itemView.findViewById(R.id.more_btn);

        seekBar = itemView.findViewById(R.id.process_sb);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClick() {
        checkModuleBtn.setOnClickListener(v -> Toast.makeText(TAG, "切换模式", Toast.LENGTH_SHORT).show());
        searchBtn.setOnClickListener(v -> search());
        coverImage.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                Toast.makeText(itemView.getContext(),"单击",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doubleClick() {
                isLike = true;
                changeIconStatus();
            }
        }));
        likeBtn.setOnClickListener(v -> changeLikeDisLike());
        commentBtn.setOnClickListener(v -> comment());
        downloadBtn.setOnClickListener(v -> download());
        moreBtn.setOnClickListener(v -> getMore());
        seekBar.setProgress(50);
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
    private void changeIconStatus() {
        if (isLike) {
            likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_liked));
        } else {
            likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_dislike));
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
        changeIconStatus();
    }

    private void changeCoverLyric() {
        Toast.makeText(TAG, "点击封面", Toast.LENGTH_SHORT).show();
    }

    private void search() {
        Toast.makeText(TAG, "搜索", Toast.LENGTH_SHORT).show();
    }
}
