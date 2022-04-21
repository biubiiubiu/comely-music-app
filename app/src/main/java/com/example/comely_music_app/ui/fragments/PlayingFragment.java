package com.example.comely_music_app.ui.fragments;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;
import com.example.comely_music_app.ui.viewmodel.PlayingViewModel;

public class PlayingFragment extends Fragment {

    private PlayingViewModel mViewModel;
    private volatile boolean isPalying = false;
    private ImageButton changePlayPause;

    public static PlayingFragment newInstance() {
        return new PlayingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View in = inflater.inflate(R.layout.playing_fragment, container, false);
        changePlayPause = in.findViewById(R.id.play_pause);
        changePlayPause.setOnClickListener(v -> {
            if (isPalying) {
                isPalying = false;
                Toast.makeText(getActivity(), "暂停", Toast.LENGTH_SHORT).show();
            } else {
                isPalying = true;
                Toast.makeText(getActivity(), "播放", Toast.LENGTH_SHORT).show();
            }
        });
        return in;
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

        ViewPager2 viewPager = requireActivity().findViewById(R.id.viewpage_playing);
        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPager.setPageTransformer(new DepthPageTransformer());

        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        viewPager.setAdapter(adapter);
    }
}