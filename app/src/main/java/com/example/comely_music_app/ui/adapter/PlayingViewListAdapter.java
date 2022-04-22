package com.example.comely_music_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.PlayingViewHolder;

public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_view,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
