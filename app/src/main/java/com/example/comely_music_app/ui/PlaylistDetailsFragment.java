package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.MusicListAdapter;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;
import com.example.comely_music_app.utils.ScreenUtils;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistDetailsFragment extends Fragment implements View.OnClickListener {
    //    private MutableLiveData<PlaylistModel> currentShowingPlaylist;
    private final MutableLiveData<Integer> myFragmentViewsCtrlLiveData;
    private ImageView playlistAvatar;
    private TextView playlistName, description, createdUsername, musicNum;
    private ImageButton collectBtn;
    private RecyclerView musicListRecycleView;
    private PlaylistViewModel playlistViewModel;
    private MusicListAdapter musicListAdapter;
    private FragmentActivity mActivity;
    private final PlaylistService playlistService;

    private PlayingViewModel playingViewModel;

    private View detailsContent, frameBlankPlaying;

    private final MutableLiveData<Integer> detailsViewCtrlLiveData = new MutableLiveData<>(0);
    private PlaylistPlayingFragment playlistPlayingFragment;

    public PlaylistDetailsFragment(MutableLiveData<Integer> liveData, PlaylistViewModel playlistViewModel, PlayingViewModel playingViewModel) {
        myFragmentViewsCtrlLiveData = liveData;
        this.playlistViewModel = playlistViewModel;
        this.playingViewModel = playingViewModel;
        playlistService = new PlaylistServiceImpl(this.playlistViewModel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        initIcons(inflateView);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        musicListRecycleView.setLayoutManager(manager);
        musicListAdapter = new MusicListAdapter(playlistViewModel.getCurrentPlaylistDetails().getValue());
        musicListAdapter.setListener(new AdapterClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View itemView, int position) {

                Log.d("TAG", "播放歌曲：");
                if (musicListAdapter.getMusicList() != null && musicListAdapter.getMusicList().size() >= position) {
                    Log.d("TAG", "播放歌曲：111");
                    detailsViewCtrlLiveData.setValue(1);

//                    MusicModel musicModel = musicListAdapter.getMusicList().get(position);
//                    playingViewModel.setCurrentMusic(musicModel);
//                    viewPagerAdapter.setMusicList_playlistModule(musicListAdapter.getMusicList());
//                    viewPager.setCurrentItem(position);
//                    viewPagerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onLongClick(View v, int position) {
                // 删除当前歌单
                PlaylistDetailsModel detailsModel = playlistViewModel.getCurrentPlaylistDetails().getValue();
                if (detailsModel != null && detailsModel.getPlaylistInfo() != null) {
                    String playlistName = detailsModel.getPlaylistInfo().getName();
                    showDeleteDialog(playlistName, musicListAdapter.getMusicList().get(position));
                    Log.d("TAG", "onClick: 删除音乐");
                }
            }

            @Override
            public void onClickEditableBtn(View v, int position) {
                // 修改当前歌单
                Log.d("TAG", "onClick: 支持正版音乐哦~");
            }
        });
        musicListRecycleView.setAdapter(musicListAdapter);

        initDatas();

        if (playlistPlayingFragment == null) {
            playlistPlayingFragment = new PlaylistPlayingFragment(detailsViewCtrlLiveData, playingViewModel, playlistViewModel);
        }
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_blank_for_playing_viewpager, playlistPlayingFragment);
        ft.commit();

        setObserveOnViewModels();
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
        playlistViewModel = null;
        playingViewModel = null;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void setObserveOnViewModels() {
        if (playlistViewModel != null) {
            playlistViewModel.getCurrentPlaylistDetails().observe(mActivity, detailsModel -> {
                musicListAdapter.setPlaylistDetails(detailsModel);
                ShpUtils.writePlaylistDetailsIntoShp(mActivity, detailsModel);
                // initDatas()包含adapter.notifyDataSetChanged();
                initDatas();
            });
        }

        detailsViewCtrlLiveData.observe(mActivity, integer -> {
            if (integer == 0) {
                Log.d("TAG", "setObserveOnViewModels: 展示歌单详情页");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(playlistPlayingFragment);
                ft.commit();
                hidePlayingFrameBlank();
            } else if (integer == 1) {
                Log.d("TAG", "setObserveOnViewModels: 展示歌单播放页");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistPlayingFragment);
                ft.commit();
                showPlayingFrameBlank();
            }
        });

    }

    private void showPlayingFrameBlank() {
        playingViewModel.setShowBottomNevBar(false);
        if (frameBlankPlaying.getVisibility() == View.INVISIBLE) {
            frameBlankPlaying.setVisibility(View.VISIBLE);
        }
        if (detailsContent.getVisibility() == View.VISIBLE) {
            detailsContent.setVisibility(View.INVISIBLE);
        }
    }

    private void hidePlayingFrameBlank() {

        playingViewModel.setShowBottomNevBar(true);
        if (frameBlankPlaying.getVisibility() == View.VISIBLE) {
            frameBlankPlaying.setVisibility(View.INVISIBLE);
        }
        if (detailsContent.getVisibility() == View.INVISIBLE) {
            detailsContent.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDeleteDialog(String playlistName, MusicModel musicModel) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_delete_playlist, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(mActivity)).setView(view).create();

        TextView title = view.findViewById(R.id.dialog_delete_title);
        title.setText("是否从歌单中移除此音乐？");

        TextView cancel = view.findViewById(R.id.dialog_delete_cancel);
        TextView complete = view.findViewById(R.id.dialog_delete_complete);
        TextView playlistNameTxt = view.findViewById(R.id.dialog_delete_playlist_name);
        playlistNameTxt.setText(musicModel.getName());

        cancel.setOnClickListener(v -> dialog.dismiss());

        complete.setOnClickListener(v -> {
            PlaylistMusicAddRequest request = new PlaylistMusicAddRequest();
            String username = Objects.requireNonNull(getCurrentUserinfoFromShp()).getUsername();
            request.setPlaylistName(playlistName).setUsername(username);
            PlaylistMusicAddRequest.MusicAddInfo info = new PlaylistMusicAddRequest.MusicAddInfo(musicModel.getName(), musicModel.getArtistName());
            List<PlaylistMusicAddRequest.MusicAddInfo> infos = new ArrayList<>();
            infos.add(info);
            request.setMusicAddInfoList(infos);
            // 删除数据库歌曲
            playlistService.deleteMusicFromPlaylist(request);

            // 触发删除shp
            playlistViewModel.deleteMusicInCurrentMusic(infos);
            //... To-do
            dialog.dismiss();
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的4/5  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setBackgroundDrawable(Objects.requireNonNull(mActivity).getDrawable(R.color.ps_color_transparent));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(mActivity)) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void initDatas() {
        // 刷新歌单基本信息
        if (playlistViewModel != null && playlistViewModel.getCurrentPlaylistDetails() != null
                && playlistViewModel.getCurrentPlaylistDetails().getValue() != null) {
            PlaylistModel model = playlistViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo();
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
        if (playlistViewModel != null && playlistViewModel.getCurrentPlaylistDetails() != null && playlistViewModel.getCurrentPlaylistDetails().getValue() != null) {
            List<MusicModel> currentShowingMusicList = playlistViewModel.getCurrentPlaylistDetails().getValue().getMusicModelList();

            if (currentShowingMusicList != null) {
                musicNum.setText(currentShowingMusicList.size() + "首");
                musicListAdapter.setMusicList(currentShowingMusicList);
            }
//            MusicModel model = currentShowingMusicList.get(currentShowingMusicList.size() - 1);
//            String lastMusicCoverPath = model.getCoverLocalPath();
//            Glide.with(Objects.requireNonNull(getContext()))
//                    .load(lastMusicCoverPath)
//                    .into(playlistAvatar);

            musicListAdapter.notifyDataSetChanged();
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
        musicListRecycleView = view.findViewById(R.id.playlist_details_music_list);

        detailsContent = view.findViewById(R.id.playlist_details_content);
        frameBlankPlaying = view.findViewById(R.id.frame_blank_for_playing_viewpager);

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

    private UserInfo getCurrentUserinfoFromShp() {
        SharedPreferences shp = Objects.requireNonNull(mActivity).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }
}