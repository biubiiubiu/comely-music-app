package com.example.comely_music_app.ui.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;

public class PlayingViewHolder extends RecyclerView.ViewHolder {
    ImageButton backBtn, searchBtn;
    TextView titleText;
    ImageView coverImage;
    ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;

    public PlayingViewHolder(@NonNull View itemView) {
        super(itemView);
        backBtn = itemView.findViewById(R.id.back_btn);
        searchBtn = itemView.findViewById(R.id.search_btn);
        titleText = itemView.findViewById(R.id.music_title_text);
        coverImage = itemView.findViewById(R.id.music_cover_img);

        likeBtn = itemView.findViewById(R.id.like_btn);
        commentBtn = itemView.findViewById(R.id.comment_btn);
        downloadBtn = itemView.findViewById(R.id.download_btn);
        moreBtn = itemView.findViewById(R.id.more_btn);
//        musicTitle = itemView.findViewById(R.id.music_title_text);
//        musicCover = itemView.findViewById(R.id.music_cover_img);
    }
}
