package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.CoverBkUtils;

import java.io.IOException;

/**
 * 用来解析item_playing_view界面，变量绑定绑定控件
 */
public class MainPlayingViewHolder extends RecyclerView.ViewHolder {
    Context context;
    View itemView;
    // 这里按照界面布局从上往下写，代码可读性好
    FrameLayout blankFrame;
    TextView titleText, lyrics;
    ImageView coverImage;

    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;

    // 进度条
    SeekBar seekBar;

    private final PlayingViewModel playingViewModel;
    private boolean showCover;

    public MainPlayingViewHolder(@NonNull View itemView, PlayingViewModel playingViewModel) {
        super(itemView);

        context = itemView.getContext();
        this.itemView = itemView;
        this.playingViewModel = playingViewModel;
        initViewBind(itemView);
        setOnClick();

        Animation mAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotaterepeat);
        coverImage.startAnimation(mAnimation);
        coverImage.setVisibility(View.VISIBLE);
        lyrics.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ResourceType")
    private void initViewBind(View itemView) {
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
        blankFrame.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                changeCover2LyricStatus();
            }

            @Override
            public void doubleClick() {
                changeLikeStatus();
            }
        }));
        likeBtn.setOnClickListener(v -> changeLikeStatus());
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
                playingViewModel.setCurrentPointFromUser(seekBar.getProgress());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void changeLikeStatus() {
        playingViewModel.changeIsLikeLiveData();
        Boolean isLike = playingViewModel.getIsLikeLiveData().getValue();
        if (isLike != null && isLike) {
            likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_liked));
        } else {
            likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_dislike));
        }
    }

    public void changeCover2LyricStatus() {
        showCover = !showCover;
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
        Toast.makeText(context, "更多", Toast.LENGTH_SHORT).show();
    }

    private void download() {
        Toast.makeText(context, "下载", Toast.LENGTH_SHORT).show();
    }

    private void comment() {
        Toast.makeText(context, "评论", Toast.LENGTH_SHORT).show();
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setCoverAndBk(MusicModel currentModel) throws IOException {
        Drawable imageSource = CoverBkUtils.getImageSourceFromMusicModel(currentModel, itemView);
        if (imageSource != null) {
            coverImage.setImageDrawable(imageSource);
            itemView.findViewById(R.id.item_plying_bk).setBackground(imageSource);
        }

        // 获取背景毛玻璃, 已弃用
        //        Bitmap blurBitmap = Blurry.with(itemView.getContext())
        //                .sampling(8).radius(50).capture(coverImage).get();
        //        BitmapDrawable blurDrawable = new BitmapDrawable(itemView.getContext().getResources(), blurBitmap);
        //        itemView.findViewById(R.id.item_plying_bk).setBackground(blurDrawable);
        //        // 注意这里用到了cover的ImageView，所以需要先初始化背景，然后在初始化cover
        //        BitmapDrawable bd = (BitmapDrawable) Drawable.createFromPath(path);
        //        // 设置背景毛玻璃
        //        Blurry.with(itemView.getContext()).radius(50).from(bd.getBitmap()).into(itemView.findViewById(R.id.item_plying_bk));
    }
}
