package com.example.comely_music_app.ui;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;

public class PlayingFragment extends Fragment implements View.OnClickListener {

    private ImageButton backBtn, searchBtn;
    private TextView titleText;
    private ImageView coverImage;
    private ImageButton likeBtn, commentBtn, downloadBtn, moreBtn;
    private ImageButton changePlayPause;

    private volatile boolean isPalying = false, isLike = false;
    private PlayingViewModel mViewModel;
    private Context TAG;

    public static PlayingFragment newInstance() {
        return new PlayingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TAG = getContext();
        View itemView = inflater.inflate(R.layout.playing_fragment, container, false);
        initViewBind(itemView);
        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlayingViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViewBind(View itemView) {
        changePlayPause = getActivity().findViewById(R.id.play_pause_btn);
        backBtn = itemView.findViewById(R.id.back_btn);
        searchBtn = itemView.findViewById(R.id.search_btn);
        titleText = itemView.findViewById(R.id.music_title_text);
        coverImage = itemView.findViewById(R.id.music_cover_img);
        likeBtn = itemView.findViewById(R.id.like_btn);
        commentBtn = itemView.findViewById(R.id.comment_btn);
        downloadBtn = itemView.findViewById(R.id.download_btn);
        moreBtn = itemView.findViewById(R.id.more_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause_btn:
                changePlayPauseStatus();
                break;
            case R.id.back_btn:
                Toast.makeText(TAG, "返回", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_btn:
                search();
                break;
            case R.id.music_cover_img:
                changeCoverLyric();
                break;
            case R.id.like_btn:
                changeLikeDisLike();
                break;
            case R.id.comment_btn:
                comment();
                break;
            case R.id.download_btn:
                download();
                break;
            case R.id.more_btn:
                getMore();
                break;
        }
    }

    private void getMore() {
        Toast.makeText(TAG, "更多", Toast.LENGTH_SHORT).show();
    }

    private void download() {
        Toast.makeText(TAG, "下载", Toast.LENGTH_SHORT).show();
    }

    private void comment() {
        Toast.makeText(TAG, "评论", Toast.LENGTH_SHORT).show();
    }

    private void changeLikeDisLike() {
        if (isPalying) {
            isPalying = false;
            Toast.makeText(getActivity(), "取消点赞", Toast.LENGTH_SHORT).show();
        } else {
            isPalying = true;
            Toast.makeText(getActivity(), "点赞", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeCoverLyric() {
        Toast.makeText(TAG, "点击封面", Toast.LENGTH_SHORT).show();
    }

    private void search() {
        Toast.makeText(TAG, "搜索", Toast.LENGTH_SHORT).show();
    }

    private void changePlayPauseStatus() {
        if (isPalying) {
            isPalying = false;
            Toast.makeText(getActivity(), "暂停", Toast.LENGTH_SHORT).show();
        } else {
            isPalying = true;
            Toast.makeText(getActivity(), "播放", Toast.LENGTH_SHORT).show();
        }
    }
}