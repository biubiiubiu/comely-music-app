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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.MusicListAdapter;
import com.example.comely_music_app.ui.adapter.PlaylistViewListAdapter;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.ScreenUtils;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlaylistDetailsFragment extends Fragment implements View.OnClickListener {
    //    private MutableLiveData<PlaylistModel> currentShowingPlaylist;
    private final MutableLiveData<Integer> myFragmentViewsCtrlLiveData;
    private TextView playlistName, description, createdUsername, musicNum;
    private ImageButton collectBtn;
    private RecyclerView musicListRecycleView;
    private MusicListAdapter musicListAdapter;
    private FragmentActivity mActivity;
    private PlaylistService playlistService;

    private PlayingViewModel playingViewModel;

    private View detailsContent, frameBlankPlaying;

    private final MutableLiveData<Integer> detailsViewCtrlLiveData = new MutableLiveData<>(0);
    private PlaylistPlayingFragment playlistPlayingFragment;
    private int currentItemPosition = 0;
    private View infoView1, infoView2, listView;

    public PlaylistDetailsFragment(MutableLiveData<Integer> liveData) {
        myFragmentViewsCtrlLiveData = liveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(mActivity).getApplication(), mActivity);

        playingViewModel = ViewModelProviders.of(mActivity, savedState).get(PlayingViewModel.class);
        playlistService = new PlaylistServiceImpl(playingViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        initIcons(inflateView);


        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        musicListRecycleView.setLayoutManager(manager);
        musicListAdapter = new MusicListAdapter(playingViewModel.getCurrentPlaylistDetails().getValue());
        musicListAdapter.setListener(new AdapterClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View itemView, int position) {
                if (musicListAdapter.getMusicList() != null && musicListAdapter.getMusicList().size() >= position) {
                    Log.d("TAG", "???????????????111");
                    currentItemPosition = position;
                    detailsViewCtrlLiveData.setValue(1);
                }
            }

            @Override
            public void onLongClick(View v, int position) {
                // ??????????????????
                PlaylistDetailsModel detailsModel = playingViewModel.getCurrentPlaylistDetails().getValue();
                if (detailsModel != null && detailsModel.getPlaylistInfo() != null) {
                    String playlistName = detailsModel.getPlaylistInfo().getName();
                    showDeleteDialog(playlistName, musicListAdapter.getMusicList().get(position));
                    Log.d("TAG", "onClick: ????????????");
                }
            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
                Toast.makeText(getContext(), "??????????????????~", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClickRightBtn(View v, int position) {
                // ??????????????????
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
                bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_music);
                //???????????????????????????????????????????????????
                View viewById = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
                if (viewById != null) {
                    viewById.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
//                ??????????????????
//                ImageView cover = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_item_cover);

                boolean itemMusicIsLiked = false;

                TextView musicNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_music_name);
                TextView artistNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_artist_name);
                List<MusicModel> musicModels = musicListAdapter.getMusicList();

                MusicModel curModel = null;
                if (musicModels != null && musicModels.size() >= position) {
                    MusicModel model = musicModels.get(position);
                    // ????????????????????????
                    if (model != null) {
                        curModel = model;
                        playingViewModel.setCurrentCheckMusic(model);
                    }
                    if (model != null && musicNameText != null && artistNameText != null) {
                        musicNameText.setText(model.getName());
                        artistNameText.setText(model.getArtistName());
                    }
                    // ??????????????????????????????????????????????????????likeBtn????????????
                    Boolean isLiked = playingViewModel.getCurrentCheckMusicIsLiked().getValue();
                    itemMusicIsLiked = isLiked != null && isLiked;
                }

                ImageButton likeBtn = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_like_btn);
                if (likeBtn != null) {
                    if (itemMusicIsLiked) {
                        likeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
                    } else {
                        likeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike));
                    }
                }
                if (likeBtn != null) {
                    likeBtn.setOnClickListener(v13 -> {
                        playingViewModel.changeCurrentCheckMusicIsLiked();
                        Boolean isLike = playingViewModel.getCurrentCheckMusicIsLiked().getValue();
                        if (isLike != null && isLike) {
                            likeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
                        } else {
                            likeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike));
                        }
                    });
                }

                View delete = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_delete_music);
                if (delete != null) {
                    delete.setOnClickListener(v1 -> {
                        // ??????????????????
                        PlaylistDetailsModel detailsModel = playingViewModel.getCurrentPlaylistDetails().getValue();
                        if (detailsModel != null && detailsModel.getPlaylistInfo() != null) {
                            String playlistName = detailsModel.getPlaylistInfo().getName();
                            showDeleteDialog(playlistName, musicListAdapter.getMusicList().get(position));
                        }
                    });
                }

                View add2Playlist = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist);
                if (add2Playlist != null && curModel != null) {
                    MusicModel finalCurModel = curModel;
                    add2Playlist.setOnClickListener(v12 -> showBottomAdd2CreatedDialog(finalCurModel));
                }
                bottomSheetDialog.show();
            }
        });
        musicListRecycleView.setAdapter(musicListAdapter);

        setObserveOnViewModels();
        return inflateView;
    }


    private void showBottomAdd2CreatedDialog(MusicModel model) {
        // ??????????????????
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_add_into_mycreated);
        //???????????????????????????????????????????????????
        View viewById = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (viewById != null) {
            viewById.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        TextView musicNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist_music_name);
        TextView artistNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist_artist_name);
        if (model != null && musicNameText != null && artistNameText != null) {
            musicNameText.setText(model.getName());
            artistNameText.setText(model.getArtistName());
        }
        // ??????mycreated??????recycleview
        RecyclerView createdRecv = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist_mycreated_recv);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mActivity.getApplicationContext());
        if (createdRecv == null) {
            return;
        }
        createdRecv.setLayoutManager(manager);
        List<PlaylistModel> myCreatePlaylistFromShp = ShpUtils.getMyCreatePlaylistFromShp(mActivity);
        PlaylistViewListAdapter adapter = new PlaylistViewListAdapter(myCreatePlaylistFromShp);
        adapter.setListener(new AdapterClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                // ??????????????????
                PlaylistModel clickPlaylistItem = adapter.getPlaylistData().get(position);
                addMusicIntoCreatedPlaylistByName(model, clickPlaylistItem.getName());
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onLongClick(View v, int position) {
            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
            }

            @Override
            public void onClickRightBtn(View v, int position) {
            }
        });
        createdRecv.setAdapter(adapter);
        bottomSheetDialog.show();
    }

    private void addMusicIntoCreatedPlaylistByName(MusicModel model, String targetCreatedPlaylistName) {
        // ???musicModel???????????????????????????
        PlaylistDetailsModel targetPlaylistDetails = ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity,
                targetCreatedPlaylistName);
        if (targetPlaylistDetails == null) {
            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            targetPlaylistDetails = new PlaylistDetailsModel();
            PlaylistModel info = new PlaylistModel();
            info.setName(targetCreatedPlaylistName);
            info.setMusicNum(0);
            UserInfo userinfo = ShpUtils.getCurrentUserinfoFromShp(mActivity);
            if (userinfo == null) {
                return;
            }
            info.setCreatedUserNickname(userinfo.getNickname());
            targetPlaylistDetails.setPlaylistInfo(info);
            List<MusicModel> musicList = new ArrayList<>();
            targetPlaylistDetails.setMusicModelList(musicList);
        }
        if (targetPlaylistDetails.getMusicModelList() == null) {
            List<MusicModel> musicList = new ArrayList<>();
            targetPlaylistDetails.setMusicModelList(musicList);
        }
        List<MusicModel> list = targetPlaylistDetails.getMusicModelList();
        Collections.reverse(list);
        if (!list.contains(model)) {
            list.add(model);
        }
        Collections.reverse(list);
        targetPlaylistDetails.setMusicModelList(list);

        PlaylistModel newInfo = targetPlaylistDetails.getPlaylistInfo();
        newInfo.setMusicNum(newInfo.getMusicNum() + 1);
        targetPlaylistDetails.setPlaylistInfo(newInfo);

        ShpUtils.writePlaylistDetailsIntoShp(mActivity, targetPlaylistDetails);
        playingViewModel.updateCreatedPlaylistByName(targetCreatedPlaylistName, targetPlaylistDetails.getPlaylistInfo());
        Toast.makeText(mActivity.getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //????????????
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //????????????
        mActivity = null;
        playingViewModel = null;
        playlistService = null;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void setObserveOnViewModels() {
        if (playingViewModel != null) {
            playingViewModel.getCurrentPlaylistDetails().observe(mActivity, detailsModel -> {
                // ?????????????????????????????????????????????
                musicListAdapter.setPlaylistDetails(detailsModel);
                ShpUtils.writePlaylistDetailsIntoShp(mActivity, detailsModel);
                // initDatas()??????adapter.notifyDataSetChanged();
                initDatas();
            });
        }

        detailsViewCtrlLiveData.observe(mActivity, integer -> {
            if (playlistPlayingFragment == null) {
                playlistPlayingFragment = new PlaylistPlayingFragment(detailsViewCtrlLiveData);
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.add(R.id.frame_blank_for_playing_viewpager, playlistPlayingFragment);
                ft.commit();
            }
            if (integer == 0) {
                Log.d("TAG", "setObserveOnViewModels: ?????????????????????");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(playlistPlayingFragment);
                ft.commit();
                hidePlayingFrameBlank();
            } else if (integer == 1) {
                playingViewModel.setPlayerModule(PlayerModule.PLAYLIST);
                playlistPlayingFragment.initDatas();
                playlistPlayingFragment.setCurItem(currentItemPosition);
                Log.d("TAG", "setObserveOnViewModels: ?????????????????????");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistPlayingFragment);
                ft.commit();
                showPlayingFrameBlank();
            }
        });

    }

    private void showPlayingFrameBlank() {
        if (detailsContent.getVisibility() == View.VISIBLE) {
            detailsContent.setVisibility(View.INVISIBLE);
        }
        playingViewModel.setShowBottomNevBar(false);
        if (frameBlankPlaying.getVisibility() == View.INVISIBLE) {
            frameBlankPlaying.setVisibility(View.VISIBLE);
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
        title.setText("????????????????????????????????????");

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
            // ?????????????????????
            playlistService.deleteMusicFromPlaylist(request);

            // ????????????shp
            playingViewModel.deleteMusicInCurrentPlaylistDetails(infos);
            //... To-do
            dialog.dismiss();
        });

        dialog.show();
        //???????????????????????????????????????????????????????????????????????????4/5  ??????????????????show????????????????????????????????????????????????????????????????????????
        dialog.getWindow().setBackgroundDrawable(Objects.requireNonNull(mActivity).getDrawable(R.color.ps_color_transparent));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(mActivity)) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void initDatas() {
        // ????????????????????????
        if (playingViewModel != null && playingViewModel.getCurrentPlaylistDetails() != null
                && playingViewModel.getCurrentPlaylistDetails().getValue() != null) {
            PlaylistModel model = playingViewModel.getCurrentPlaylistDetails().getValue().getPlaylistInfo();
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
        // ????????????????????????
        if (playingViewModel != null && playingViewModel.getCurrentPlaylistDetails() != null && playingViewModel.getCurrentPlaylistDetails().getValue() != null) {
            List<MusicModel> currentShowingMusicList = playingViewModel.getCurrentPlaylistDetails().getValue().getMusicModelList();

            if (currentShowingMusicList != null) {
                musicNum.setText(currentShowingMusicList.size() + "???");
                musicListAdapter.setMusicList(currentShowingMusicList);
            }
//            MusicModel model = currentShowingMusicList.get(currentShowingMusicList.size() - 1);
//            String lastMusicCoverPath = model.getCoverLocalPath();
//            Glide.with(Objects.requireNonNull(getContext()))
//                    .load(lastMusicCoverPath)
//                    .into(playlistAvatar);

            musicListAdapter.notifyDataSetChanged();
            Log.d("TAG", "initDatas: ??????????????????");
        }
    }

    private void initIcons(View view) {
        playlistName = view.findViewById(R.id.playlist_details_playlistName);
//        ImageView playlistAvatar = view.findViewById(R.id.playlist_details_avatar);
        createdUsername = view.findViewById(R.id.playlist_details_createdUsername);
        description = view.findViewById(R.id.playlist_details_description);
        musicNum = view.findViewById(R.id.playlist_details_music_num);
        collectBtn = view.findViewById(R.id.playlist_details_collect);
        musicListRecycleView = view.findViewById(R.id.playlist_details_music_list);

        detailsContent = view.findViewById(R.id.playlist_details_content);
        frameBlankPlaying = view.findViewById(R.id.frame_blank_for_playing_viewpager);

        infoView1 = view.findViewById(R.id.playlist_details_info_view1);
        infoView2 = view.findViewById(R.id.playlist_details_info_view2);
        listView = view.findViewById(R.id.playlist_details_info_view3);

        view.findViewById(R.id.playlist_details_back).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_play_all).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_multi_check).setOnClickListener(this);
        view.findViewById(R.id.playlist_details_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playlist_details_back) {
            // ??????
            myFragmentViewsCtrlLiveData.setValue(0);
        } else if (v.getId() == R.id.playlist_details_play_all) {
            // ???????????????????????????
            currentItemPosition = 0;
            detailsViewCtrlLiveData.setValue(1);
        } else if (v.getId() == R.id.playlist_details_multi_check) {
            // ??????????????????????????????
            Toast.makeText(getContext(), "??????????????????????????????~", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.playlist_details_share) {
            // ????????????
            Toast.makeText(getContext(), "??????????????????????????????~", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCollectNotAllowed() {
        if (collectBtn != null) {
            collectBtn.setOnClickListener(v -> Toast.makeText(getContext(), "??????????????????????????????~", Toast.LENGTH_SHORT).show());
        }
    }

//    public void setPlaylistInfoNotShowed() {
//        if (infoView1 != null) {
//            infoView1.setVisibility(View.INVISIBLE);
//        }
//        if (infoView2 != null) {
//            infoView2.setVisibility(View.INVISIBLE);
//        }
//        if (listView != null) {
//            listView.setTop(100);
//        }
//    }

    public void setCollectAllowed() {
        if (collectBtn != null) {
            collectBtn.setOnClickListener(v -> Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show());
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