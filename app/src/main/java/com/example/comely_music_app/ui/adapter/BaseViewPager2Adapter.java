package com.example.comely_music_app.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewPager2Adapter extends RecyclerView.Adapter<PlayingViewHolder>{
    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
