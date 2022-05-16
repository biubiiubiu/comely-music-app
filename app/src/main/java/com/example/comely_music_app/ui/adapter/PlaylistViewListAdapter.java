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

    public List<PlaylistModel> getPlaylistData() {
        return playlistData;
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_playlist_or_music, null);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (playlistData != null && playlistData.size() >= position) {
            holder.setDataOnView(playlistData.get(position));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(v, position);
                }
                return true;
            }
        });
        holder.itemView.findViewById(R.id.item_playlist_editableBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickEditableBtn(v, position);
                }
            }
        });
        holder.itemView.findViewById(R.id.item_playlist_name).setOnClickListener(new View.OnClickListener() {
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
        private ImageView playlistCover, visibilityImage;
        private TextView playlistName, playlistMusicNum;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            initIcons(itemView);
        }

        private void initIcons(@NonNull View itemView) {
            playlistCover = itemView.findViewById(R.id.item_playlist_cover);
            playlistName = itemView.findViewById(R.id.item_playlist_name);
            playlistMusicNum = itemView.findViewById(R.id.item_playlist_music_num);
            visibilityImage = itemView.findViewById(R.id.item_playlist_visibility_image);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        private void setDataOnView(PlaylistModel playlistModel) {
            playlistCover.setImageDrawable(itemView.getResources().getDrawable(R.drawable.bk_01));
            playlistName.setText(playlistModel.getName());
            int num = playlistModel.getMusicNum() == null ? 0 : playlistModel.getMusicNum();
            playlistMusicNum.setText(num + "首");
            visibilityImage.setVisibility(View.VISIBLE);
            if (playlistModel.getVisibility() == null || playlistModel.getVisibility() == 1) {
                visibilityImage.setVisibility(View.INVISIBLE);
            } else if (playlistModel.getVisibility() == 0) {
                visibilityImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_lock_white_24dp));
            } else if (playlistModel.getVisibility() == 2) {
                visibilityImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_friends));
            }
        }
    }
}
