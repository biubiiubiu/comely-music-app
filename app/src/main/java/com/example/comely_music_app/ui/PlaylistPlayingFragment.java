package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.OtherPlayingViewAdapter;
import com.example.comely_music_app.ui.animation.ZoomOutPageTransformer;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.List;
import java.util.Objects;

public class PlaylistPlayingFragment extends Fragment implements View.OnClickListener {

    ImageButton backBtn;
    TextView playlistNameText;

    ViewPager2 viewPager2;
    OtherPlayingViewAdapter adapter;
    PlayingViewModel playingViewModel;
    MutableLiveData<Integer> detailsViewCtrlLiveData;
    private FragmentActivity mActivity;

    public PlaylistPlayingFragment(MutableLiveData<Integer> ctrlLiveData) {
        this.detailsViewCtrlLiveData = ctrlLiveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(mActivity).getApplication(), mActivity);

        playingViewModel = ViewModelProviders.of(mActivity, savedState).get(PlayingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView = inflater.inflate(R.layout.fragment_playlist_playing, container, false);

        initIcons(inflateView);

        String curPlaylistName = "歌单播放";
        if (playingViewModel != null && playingViewModel.getCurrentPlaylistDetails().getValue() != null
                && playingViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo() != null) {
            curPlaylistName = playingViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo().getName();
        }
        adapter = new OtherPlayingViewAdapter(playingViewModel, curPlaylistName);
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData_playlistModule().getValue()).size() > position) {
                    playingViewModel.setCurrentPlayMusic(playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position));
                    Log.d("TAG", "onPageSelected: 当前选择position:" + position + " "
                            + playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position).getName());


//                    PlaylistDetailsModel curDetails = playingViewModel.getCurrentPlaylistDetails().getValue();
//                    if (curDetails != null && curDetails.getMusicModelList() != null) {
//                        curDetails.getMusicModelList().size();
//                        List<MusicModel> curMusicModelList = curDetails.getMusicModelList();
//                        playingViewModel.setMusicListLiveData_playlistModule(curMusicModelList);
//                    }
                }
            }
        });
        Log.d("TAG", "onCreateView: 创建playingFragment");
        return inflateView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //添加引用
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //销毁引用
        mActivity = null;
        playingViewModel = null;
        detailsViewCtrlLiveData = null;
    }

    public void setViewPager2(ViewPager2 vp2) {
        this.viewPager2 = vp2;
    }

    public void setAdapter(OtherPlayingViewAdapter adapter) {
        this.adapter = adapter;
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
    public void onClick(View v) {
        if (v.getId() == R.id.exit_other_playing_viewpage) {
            detailsViewCtrlLiveData.setValue(0);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurItem(int position) {
        if (position >= 0 && position < adapter.getItemCount()) {
            viewPager2.setCurrentItem(position, false);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initDatas() {
        // 刷新歌单歌曲列表
        if (playingViewModel != null && playingViewModel.getCurrentPlaylistDetails() != null && playingViewModel.getCurrentPlaylistDetails().getValue() != null) {
            List<MusicModel> currentPlayingingMusicList = playingViewModel.getCurrentPlaylistDetails().getValue().getMusicModelList();

            if (currentPlayingingMusicList != null) {
                adapter.setMusicList_playlistModule(currentPlayingingMusicList);
            }
            adapter.notifyDataSetChanged();
            Log.d("TAG", "initDatas: 展示歌曲列表");
        }
    }
}