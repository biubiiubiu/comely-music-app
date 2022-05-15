package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.network.response.MusicSelectResponse;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListHolder> {
    private List<MusicSelectResponse.MusicInfo> musicList;
    private AdapterClickListener listener;

    public MusicListAdapter(List<MusicSelectResponse.MusicInfo> list) {
        musicList = list;
    }

    public List<MusicSelectResponse.MusicInfo> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicSelectResponse.MusicInfo> musicList) {
        this.musicList = musicList;
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_playlist_or_music, null);
        return new MusicListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListHolder holder, @SuppressLint("RecyclerView") int position) {
        if (musicList != null && musicList.size() >= position) {
            holder.setDataOnView(musicList.get(position));
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
                    listener.onClickEditableBtn(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }

    public static class MusicListHolder extends RecyclerView.ViewHolder {
        private ImageView musicCover, moreBtn, copyright;
        private TextView musicName, artistName;

        public MusicListHolder(@NonNull View itemView) {
            super(itemView);
            initIcons(itemView);
        }

        private void initIcons(View itemView) {
            musicCover = itemView.findViewById(R.id.item_playlist_cover);
            musicName = itemView.findViewById(R.id.item_playlist_name);
            artistName = itemView.findViewById(R.id.item_playlist_music_num);
            moreBtn = itemView.findViewById(R.id.item_playlist_visibility_image);
            copyright = itemView.findViewById(R.id.item_playlist_editableBtn);

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setDataOnView(MusicSelectResponse.MusicInfo musicInfo) {
            musicCover.setImageDrawable(itemView.getResources().getDrawable(R.drawable.bk_01));
            musicName.setText(musicInfo.getName());
            artistName.setText(musicInfo.getArtistName() != null ? musicInfo.getArtistName() : "未知歌手");

            // 默认样式
            moreBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_menu_three_point));
            copyright.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_v));
        }
    }
}
