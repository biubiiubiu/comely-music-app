package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.models.PlaylistModel;

import java.util.List;

public class PlaylistViewListAdapter extends RecyclerView.Adapter<PlaylistViewListAdapter.PlaylistViewHolder> {
    private List<PlaylistModel> playlistData;
    private AdapterClickListener listener;

    public PlaylistViewListAdapter(List<PlaylistModel> playlistData) {
        this.playlistData = playlistData;
    }

    public void setPlaylistData(List<PlaylistModel> playlistData) {
        this.playlistData = playlistData;
    }

    public void addPlaylistData(PlaylistModel model) {
        playlistData.add(model);
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    public interface AdapterClickListener {
        void onClick(View itemView, int position);
    }


    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_playlist, null);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setDataOnView(playlistData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (playlistData != null) {
            return playlistData.size();
        }
        return 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ImageView playlistCover;
        private TextView playlistName, playlistMusicNum;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            initIcons(itemView);
        }

        private void initIcons(@NonNull View itemView) {
            playlistCover = itemView.findViewById(R.id.item_playlist_cover);
            playlistName = itemView.findViewById(R.id.item_playlist_name);
            playlistMusicNum = itemView.findViewById(R.id.item_playlist_music_num);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        private void setDataOnView(PlaylistModel playlistModel) {
            // todo 设置封面
            playlistCover.setImageDrawable(itemView.getResources().getDrawable(R.drawable.avatar_music));
            playlistName.setText(playlistModel.getName());
            int num = playlistModel.getMusicNum() == null ? 0 : playlistModel.getMusicNum();
            playlistMusicNum.setText(num + "首");
        }
    }
}
