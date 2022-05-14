package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;

public class PlaylistDetailsFragment extends Fragment implements View.OnClickListener {
    //    private MutableLiveData<PlaylistModel> currentShowingPlaylist;
    private final MutableLiveData<Integer> myFragmentViewsCtrlLiveData;
    private ImageView playlistAvatar;
    private TextView playlistName, description, createdUsername, musicNum;
    private ImageButton collectBtn;
    private RecyclerView musicListRecycle;
    private PlaylistViewModel playlistViewModel;

    public PlaylistDetailsFragment(MutableLiveData<Integer> liveData, PlaylistViewModel viewModel) {
        myFragmentViewsCtrlLiveData = liveData;
        playlistViewModel = viewModel;
    }

//    public MutableLiveData<PlaylistModel> getCurrentShowingPlaylist() {
//        if (currentShowingPlaylist == null) {
//            currentShowingPlaylist = new MutableLiveData<>();
//        }
//        return currentShowingPlaylist;
//    }
//
//    public void setCurrentShowingPlaylist(PlaylistModel playlistModel) {
//        if (currentShowingPlaylist == null) {
//            currentShowingPlaylist = getCurrentShowingPlaylist();
//        }
//        currentShowingPlaylist.setValue(playlistModel);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        initIcons(inflateView);

        initDatas();
        return inflateView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        playlistViewModel = null;
    }

    @SuppressLint("SetTextI18n")
    public void initDatas() {
        // 刷新歌单基本信息
        if (playlistViewModel != null && playlistViewModel.getCurrentShowingPlaylist() != null) {
            PlaylistModel model = playlistViewModel.getCurrentShowingPlaylist();
            musicNum.setText(model.getMusicNum() + "");
            if (model.getName() != null) {
                playlistName.setText(model.getName());
            }
            if (model.getCreatedUserNickname() != null) {
                createdUsername.setText(model.getCreatedUserNickname());
            }
            if (model.getDescription() != null) {
                description.setText(model.getDescription());
            }
        }
        // 刷新歌单歌曲列表
        if (playlistViewModel != null && playlistViewModel.getCurrentShowingMusicList() != null) {
            Log.d("TAG", "initDatas: 展示歌曲列表");
        }
    }

    private void initIcons(View view) {
        playlistName = view.findViewById(R.id.playlist_details_playlistName);
        playlistAvatar = view.findViewById(R.id.playlist_details_avatar);
        createdUsername = view.findViewById(R.id.playlist_details_createdUsername);
        description = view.findViewById(R.id.playlist_details_description);
        musicNum = view.findViewById(R.id.playlist_details_music_num);
        collectBtn = view.findViewById(R.id.playlist_details_collect);
        musicListRecycle = view.findViewById(R.id.playlist_details_music_list);

        view.findViewById(R.id.playlist_details_back).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_play_all).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_multi_check).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playlist_details_back) {
            // 返回
            myFragmentViewsCtrlLiveData.setValue(0);
        } else if (v.getId() == R.id.playlist_details_play_all) {
            // 播放此歌单全部歌曲
            Toast.makeText(getContext(), "抱歉，该功能暂未开放~", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.playlist_details_multi_check) {
            // 多选操作，跳转新界面
            Toast.makeText(getContext(), "抱歉，该功能暂未开放~", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.playlist_details_share) {
            // 分享歌单
            Toast.makeText(getContext(), "抱歉，该功能暂未开放~", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCollectNotAllowed() {
        if (collectBtn != null) {
            collectBtn.setOnClickListener(v -> Toast.makeText(getContext(), "不能收藏自己的歌单哦~", Toast.LENGTH_SHORT).show());
        }
    }

    public void setCollectAllowed() {
        if (collectBtn != null) {
            collectBtn.setOnClickListener(v -> Toast.makeText(getContext(), "收藏成功！", Toast.LENGTH_SHORT).show());
        }
    }
}