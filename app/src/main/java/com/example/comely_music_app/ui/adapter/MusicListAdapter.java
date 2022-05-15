package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.utils.CoverBkUtils;

import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListHolder> {
    private List<MusicModel> musicList;
    private AdapterClickListener listener;

    public MusicListAdapter(List<MusicModel> list) {
        musicList = list;
    }

    public List<MusicModel> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicModel> musicList) {
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

    @SneakyThrows
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
        public void setDataOnView(MusicModel musicModel) throws IOException {
            Drawable imageSource = CoverBkUtils.getImageSourceFromMusicModel(musicModel, itemView);
            Glide.with(itemView.getContext())
                    .load(imageSource)
                    .into(musicCover);
            musicName.setText(musicModel.getName());
            artistName.setText(musicModel.getArtistName() != null ? musicModel.getArtistName() : "未知歌手");

            // 默认样式
            moreBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_menu_three_point));
            copyright.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_v));
        }
    }
}
