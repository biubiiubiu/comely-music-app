package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;

import java.util.List;

/**
 * playlist、music的列表展示的RecycleView的Adapter的基类
 * @param <T>
 */
public abstract class RecycleViewBaseAdapter<T> extends RecyclerView.Adapter<RecycleViewBaseAdapter<T>.RecycleViewBaseHolder> {
    private List<T> itemDataList;
    private AdapterClickListener listener;

    public List<T> getItemDataList() {
        return itemDataList;
    }

    public void setItemDataList(List<T> itemDataList) {
        this.itemDataList = itemDataList;
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public abstract RecycleViewBaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecycleViewBaseHolder holder, @SuppressLint("RecyclerView") int position){
        if (itemDataList != null && itemDataList.size() >= position) {
            holder.setDataOnView(itemDataList.get(position));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(v, position);
            }
            return true;
        });
        holder.itemView.findViewById(R.id.item_playlist_editableBtn).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickEditableBtn(v, position);
            }
        });
        holder.itemView.findViewById(R.id.item_playlist_name).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickEditableBtn(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList != null ? itemDataList.size() : 0;
    }

    public abstract class RecycleViewBaseHolder extends RecyclerView.ViewHolder {
        public RecycleViewBaseHolder(@NonNull View itemView) {
            super(itemView);
            initIcons();
        }

        /**
         * 初始化item界面的控件绑定
         */
        protected abstract void initIcons();

        /**
         * 初始化item的界面数据
         */
        public abstract void setDataOnView(T t);
    }
}
