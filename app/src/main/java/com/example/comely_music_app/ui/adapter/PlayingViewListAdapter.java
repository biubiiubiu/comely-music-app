package com.example.comely_music_app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.fragments.PlayingFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayingViewListAdapter extends RecyclerView.Adapter<PlayingViewHolder> {

    @NonNull
    @Override
    public PlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playing_page,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }


//    public static final List<Integer> ICON_LIST;
//    private boolean isPager;
//
//    static {
//        int[] arr = {
//                R.drawable.ic_list_1,
//                R.drawable.ic_list_2,
//                R.drawable.ic_list_3,
//                R.drawable.ic_list_4,
//                R.drawable.ic_list_5,
//                R.drawable.ic_list_6,
//                R.drawable.ic_list_7,
//                R.drawable.ic_list_8
//        };
//        ICON_LIST = new ArrayList<>();
//        int len = arr.length;
//        Random random = new Random();
//        for (int i = 0; i < 50; i++) {
//            ICON_LIST.add(arr[random.nextInt(len)]);
//        }
//    }
//
//    private static DiffUtil.ItemCallback<View> diffCallback =
//            new DiffUtil.ItemCallback<View>() {
//
//                @Override
//                public boolean areItemsTheSame(@NonNull View oldItem, @NonNull View newItem) {
//                    return oldItem.equals(newItem);
//                }
//
//                @SuppressLint("DiffUtilEquals")
//                @Override
//                public boolean areContentsTheSame(@NonNull View oldItem, @NonNull View newItem) {
//                    return oldItem.equals(newItem);
//                }
//            };
//
//    public PlayingViewListAdapter() {
//        this(false);
//    }
//
//    public PlayingViewListAdapter(boolean isPager) {
//        super(diffCallback);
//        this.isPager = isPager;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.view_holder, parent, false);
//        if (isPager) {
//            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//            ViewGroup.LayoutParams params = view.findViewById(R.id.cellPageView).getLayoutParams();
//            params.height = 0;
//            params.width = 0;
//        }
//        return new RecyclerView.ViewHolder(view) {};
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
////        ((ImageView) holder.itemView.findViewById(R.id.cellImageView))
////                .setImageResource(getItem(position));
//    }
}


