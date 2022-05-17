package com.example.comely_music_app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.OtherPlayingViewAdapter;
import com.example.comely_music_app.ui.animation.ZoomOutPageTransformer;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;

import java.util.Objects;

public class PlaylistPlayingFragment extends Fragment implements View.OnClickListener {

    ImageButton backBtn;
    TextView playlistNameText;

    ViewPager2 viewPager2;
    OtherPlayingViewAdapter adapter;
    PlayingViewModel playingViewModel;
    PlaylistViewModel playlistViewModel;
    MutableLiveData<Integer> detailsViewCtrlLiveData;

    public PlaylistPlayingFragment(MutableLiveData<Integer> ctrlLiveData, PlayingViewModel playingViewModel,
                                   PlaylistViewModel playlistViewModel) {
        this.playingViewModel = playingViewModel;
        this.playlistViewModel = playlistViewModel;
        this.detailsViewCtrlLiveData = ctrlLiveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView = inflater.inflate(R.layout.fragment_playlist_playing, container, false);

        initIcons(inflateView);

        String curPlaylistName = "歌单播放";
        if (playlistViewModel != null && playlistViewModel.getCurrentPlaylistDetails().getValue() != null
                && playlistViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo() != null) {
            curPlaylistName = playlistViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo().getName();
        }
        adapter = new OtherPlayingViewAdapter(playingViewModel, curPlaylistName);
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData_playlistModule().getValue()).size() > position) {
                    playingViewModel.setCurrentMusic(playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position));
                    Log.d("TAG", "onPageSelected: 当前选择position:" + position + " "
                            + playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position).getName());
                }
            }
        });
        Log.d("TAG", "onCreateView: 创建playingFragment");
        return inflateView;
    }

    private void initIcons(View inflateView) {

        backBtn = inflateView.findViewById(R.id.exit_other_playing_viewpage);
        playlistNameText = inflateView.findViewById(R.id.player_module_txt_playlist_module);

        backBtn.setOnClickListener(this);

        viewPager2 = inflateView.findViewById(R.id.viewpager_playing_fragment);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setPageTransformer(ZoomOutPageTransformer.getZoomOutPageTransformer());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        playingViewModel = null;
        playlistViewModel = null;
        detailsViewCtrlLiveData = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_other_playing_viewpage) {
            detailsViewCtrlLiveData.setValue(0);
        }
    }
}