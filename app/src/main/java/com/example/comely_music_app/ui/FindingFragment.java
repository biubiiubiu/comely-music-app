package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.R;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.MusicService;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.impl.MusicServiceImpl;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.MusicListAdapter;
import com.example.comely_music_app.ui.adapter.OtherPlayingViewAdapter;
import com.example.comely_music_app.ui.adapter.PlaylistViewListAdapter;
import com.example.comely_music_app.ui.animation.ZoomOutPageTransformer;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FindingFragment extends Fragment {
    private EditText searchEdit;
    private RecyclerView searchResultRecycleView;
    private MusicListAdapter musicListAdapter;
    private PlaylistViewListAdapter playlistsAdapter;

    private FragmentActivity mActivity;
    private PlayingViewModel playingViewModel;
    private MusicService musicService;
    private int currentItemPosition = 0;
    private final MutableLiveData<Integer> findingViewCtrlLiveData = new MutableLiveData<>(0);
    private PlaylistPlayingFragment playingFragment;
    private PlaylistDetailsFragment playlistDetailsFragment;
    private View searchMore;
    private View cardview;
    private View searchResultDetailsContent, frameBlankPlaylistDetails, frameBlankPlayingViewPager;
    private PlaylistService playlistService;
    private ViewPager2 viewPager2;
    private OtherPlayingViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(mActivity).getApplication(), mActivity);
        playingViewModel = ViewModelProviders.of(mActivity, savedState).get(PlayingViewModel.class);
        musicService = new MusicServiceImpl();
        playlistService = new PlaylistServiceImpl(playingViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finding, container, false);
        initIcons(view);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        searchResultRecycleView.setLayoutManager(manager);
        musicListAdapter = new MusicListAdapter(new PlaylistDetailsModel());
        musicListAdapter.setListener(new AdapterClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View itemView, int position) {
                if (musicListAdapter.getMusicList() != null && musicListAdapter.getMusicList().size() >= position) {
                    Log.d("TAG", "???????????????111");
                    currentItemPosition = position;
                    findingViewCtrlLiveData.setValue(1);
                }
            }

            @Override
            public void onLongClick(View v, int position) {
                // ??????????????????

            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
                Toast.makeText(getContext(), "??????????????????~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickRightBtn(View v, int position) {
                // ??????????????????
                showBottomSheetDialog(position);
            }
        });
        searchResultRecycleView.setAdapter(musicListAdapter);

        List<PlaylistModel> list = new ArrayList<>();
        playlistsAdapter = new PlaylistViewListAdapter(list);
        playlistsAdapter.setListener(new AdapterClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                // ??????????????????
                List<PlaylistDetailsModel> playlistDetailsModels = playingViewModel.getFuzzySearchResultPlaylists().getValue();
                if (playlistDetailsModels != null && playlistDetailsModels.size() >= position) {
                    PlaylistDetailsModel detailsModel = playlistDetailsModels.get(position);
                    playingViewModel.setCurrentPlaylistDetails(detailsModel);
                }
                // ???????????????????????????????????????
                playingViewModel.setShowSearchPlaylist();
            }

            @Override
            public void onLongClick(View v, int position) {
            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
                // ??????????????????
                Toast.makeText(mActivity.getApplicationContext(), "????????????????????????~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickRightBtn(View v, int position) {
            }
        });

        setObserveOnViewModels();
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheetDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_music);
        //???????????????????????????????????????????????????
        View viewById = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (viewById != null) {
            viewById.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

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
            delete.setVisibility(View.INVISIBLE);
        }

        View add2Playlist = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist);
        if (add2Playlist != null && curModel != null) {
            MusicModel finalCurModel = curModel;
            add2Playlist.setOnClickListener(v12 -> showBottomAdd2CreatedDialog(finalCurModel));
        }
        bottomSheetDialog.show();
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
        musicService = null;
        playlistService = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setObserveOnViewModels() {
        if (playingViewModel != null) {
            playingViewModel.getFuzzySearchResultMusicList().observe(mActivity, musicModels -> {
                if (musicModels != null) {
                    if (musicModels.size() == 7 && searchMore != null) {
                        // ??????????????????
                        searchMore.setVisibility(View.VISIBLE);
                    }
                    musicListAdapter.setMusicList(musicModels);
                    searchResultRecycleView.setAdapter(musicListAdapter);
                    musicListAdapter.notifyDataSetChanged();

                    PlaylistDetailsModel detailsModel = new PlaylistDetailsModel();
                    PlaylistModel playlistInfo = new PlaylistModel();
                    playlistInfo.setName("????????????");
                    playlistInfo.setMusicNum(musicModels.size());
                    detailsModel.setPlaylistInfo(playlistInfo);
                    detailsModel.setMusicModelList(musicModels);
                    playingViewModel.setCurrentPlaylistDetails(detailsModel);
                }
            });

            playingViewModel.getFuzzySearchResultPlaylists().observe(mActivity, playlistDetailsModels -> {
                if (playlistDetailsModels != null) {
                    List<PlaylistModel> playlistModels = new ArrayList<>();
                    for (PlaylistDetailsModel model : playlistDetailsModels) {
                        playlistModels.add(model.getPlaylistInfo());
                    }
                    playlistsAdapter.setPlaylistData(playlistModels);
                    searchResultRecycleView.setAdapter(playlistsAdapter);
                    playlistsAdapter.notifyDataSetChanged();
                }
            });

            playingViewModel.getShowSearchPlaylist().observe(mActivity, integer -> findingViewCtrlLiveData.setValue(2));
        }

        findingViewCtrlLiveData.observe(mActivity, integer -> {
            if (playingFragment == null) {
                playingFragment = new PlaylistPlayingFragment(findingViewCtrlLiveData);
                playingFragment.setViewPager2(viewPager2);
                playingFragment.setAdapter(adapter);
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.add(R.id.finding_frame_blank_for_playing_details, playingFragment);
                ft.commit();
            }
            if (playlistDetailsFragment == null) {
                playlistDetailsFragment = new PlaylistDetailsFragment(findingViewCtrlLiveData);
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.add(R.id.finding_frame_blank_for_playing_details, playlistDetailsFragment);
                ft.commit();
            }
            if (integer == 0) {
                Log.d("TAG", "setObserveOnViewModels: ?????????????????????");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(playingFragment);
                ft.hide(playlistDetailsFragment);
                ft.commit();
                showSearchResultFrameBlank();
            } else if (integer == 1) {
                playingViewModel.setPlayerModule(PlayerModule.PLAYLIST);
                playingFragment.initDatas();
                playingFragment.setCurItem(currentItemPosition);
                Log.d("TAG", "setObserveOnViewModels: ?????????????????????");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(playlistDetailsFragment);
                ft.show(playingFragment);
                ft.commit();
                showPlayingViewpager2FrameBlank();
            } else if (integer == 2) {
                Log.d("TAG", "setObserveOnViewModels: ?????????????????????");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(playingFragment);
                ft.show(playlistDetailsFragment);
                ft.commit();
                showPlaylistDetailsFrameBlank();
            }
        });

    }

    // ?????????????????????
    private void showPlaylistDetailsFrameBlank() {
        if (searchResultDetailsContent.getVisibility() == View.VISIBLE) {
            searchResultDetailsContent.setVisibility(View.INVISIBLE);
        }
        playingViewModel.setShowBottomNevBar(false);
        if (frameBlankPlaylistDetails.getVisibility() == View.INVISIBLE) {
            frameBlankPlaylistDetails.setVisibility(View.VISIBLE);
        }
        if (frameBlankPlayingViewPager.getVisibility() == View.VISIBLE) {
            frameBlankPlayingViewPager.setVisibility(View.INVISIBLE);
        }
        if (viewPager2 != null) {
            viewPager2.setVisibility(View.INVISIBLE);
        }
    }

    // ???????????????
    private void showPlayingViewpager2FrameBlank() {
        if (searchResultDetailsContent.getVisibility() == View.VISIBLE) {
            searchResultDetailsContent.setVisibility(View.INVISIBLE);
        }
        playingViewModel.setShowBottomNevBar(false);
        if (frameBlankPlaylistDetails.getVisibility() == View.VISIBLE) {
            frameBlankPlaylistDetails.setVisibility(View.INVISIBLE);
        }
        if (frameBlankPlayingViewPager.getVisibility() == View.INVISIBLE) {
            frameBlankPlayingViewPager.setVisibility(View.VISIBLE);
        }
        if (viewPager2 != null) {
            viewPager2.setVisibility(View.VISIBLE);
        }
    }

    // ??????????????????
    private void showSearchResultFrameBlank() {
        playingViewModel.setShowBottomNevBar(true);
        if (frameBlankPlaylistDetails.getVisibility() == View.VISIBLE) {
            frameBlankPlaylistDetails.setVisibility(View.INVISIBLE);
        }
        if (searchResultDetailsContent.getVisibility() == View.INVISIBLE) {
            searchResultDetailsContent.setVisibility(View.VISIBLE);
        }
        if (frameBlankPlayingViewPager.getVisibility() == View.VISIBLE) {
            frameBlankPlayingViewPager.setVisibility(View.INVISIBLE);
        }
        if (viewPager2 != null) {
            viewPager2.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initIcons(View view) {
        cardview = view.findViewById(R.id.finding_search_cardview);
        TextView searchMusicBtn = view.findViewById(R.id.finding_search_music);
        TextView searchPlaylistBtn = view.findViewById(R.id.finding_search_playlist);
        searchEdit = view.findViewById(R.id.finding_editText);
        searchResultRecycleView = view.findViewById(R.id.finding_search_result_recv);

        searchMore = view.findViewById(R.id.finding_search_more);
        searchMore.setVisibility(View.INVISIBLE);

        searchResultDetailsContent = view.findViewById(R.id.finding_playlist_details_content);
        frameBlankPlaylistDetails = view.findViewById(R.id.finding_frame_blank_for_playing_details);
        frameBlankPlayingViewPager = view.findViewById(R.id.finding_frame_blank_for_playing_viewpager);

        viewPager2 = view.findViewById(R.id.finding_viewpager_playing_fragment);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setPageTransformer(ZoomOutPageTransformer.getZoomOutPageTransformer());

        String curPlaylistName = "????????????";
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
                    Log.d("TAG", "onPageSelected: ????????????position:" + position + " "
                            + playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position).getName());
                }
            }
        });

        searchMusicBtn.setOnClickListener(v -> {
            String searchContent = searchEdit.getText().toString().trim();
            if (searchContent.length() > 0) {
                List<String> historyList = ShpUtils.getHistorySearchList(mActivity);
                Set<String> set = new HashSet<>(historyList);
                set.add(searchContent);
                historyList = new ArrayList<>(set);
                historyList.remove("");
                ShpUtils.writeHistorySearchList(mActivity, historyList);
                musicService.fuzzySearchMusicByNameLimit(searchContent, playingViewModel);
            }
        });

        searchPlaylistBtn.setOnClickListener(v -> {
            String searchContent = searchEdit.getText().toString().trim();
            if (searchContent.length() > 0) {
                List<String> historyList = ShpUtils.getHistorySearchList(mActivity);
                Set<String> set = new HashSet<>(historyList);
                set.add(searchContent);
                historyList = new ArrayList<>(set);
                historyList.remove("");
                ShpUtils.writeHistorySearchList(mActivity, historyList);
                playlistService.fuzzySearchPlaylist(searchContent);
            }
        });

        searchMore.setOnClickListener(v -> {
            String searchContent = searchEdit.getText().toString();
            if (searchContent.length() > 0) {
                musicService.fuzzySearchMusicByName(searchContent, playingViewModel);
                searchMore.setVisibility(View.INVISIBLE);
            }
        });

        searchEdit.setOnClickListener(v -> {
            ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
            List<String> historyList = ShpUtils.getHistorySearchList(mActivity);
            historyList.add("??????????????????");
            final String[] historyArray = historyList.toArray(new String[0]);
            listPopupWindow.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_popup_window, historyArray));
            listPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.btn_gray));
            listPopupWindow.setAnchorView(cardview);
            listPopupWindow.setModal(true);

            listPopupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
                if (i == historyArray.length - 1) {
                    ShpUtils.clearSearchHistoryList(mActivity);
                } else {
                    searchEdit.setText(historyArray[i]);
                }
                listPopupWindow.dismiss();
            });
            listPopupWindow.show();
        });
    }

}