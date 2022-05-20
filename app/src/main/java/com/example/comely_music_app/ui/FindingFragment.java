package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.network.service.MusicService;
import com.example.comely_music_app.network.service.impl.MusicServiceImpl;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.MusicListAdapter;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FindingFragment extends Fragment {
    private TextView searchBtn;
    private EditText searchEdit;
    private RecyclerView searchResultRecycleView;
    private MusicListAdapter musicListAdapter;
    private FragmentActivity mActivity;
    private PlayingViewModel playingViewModel;
    private MusicService musicService;
    private int currentItemPosition = 0;
    private final MutableLiveData<Integer> detailsViewCtrlLiveData = new MutableLiveData<>(0);
    private View searchMore;
    private View cardview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(mActivity).getApplication(), mActivity);
        playingViewModel = ViewModelProviders.of(mActivity, savedState).get(PlayingViewModel.class);
        musicService = new MusicServiceImpl();
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
                    Log.d("TAG", "播放歌曲：111");
                    currentItemPosition = position;
                    detailsViewCtrlLiveData.setValue(1);
                }
            }

            @Override
            public void onLongClick(View v, int position) {
                // 删除当前歌曲

            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
                Toast.makeText(getContext(), "支持正版音乐~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickRightBtn(View v, int position) {
                // 修改当前歌曲
                showBottomSheetDialog(position);
            }
        });
        searchResultRecycleView.setAdapter(musicListAdapter);

        setObserveOnViewModels();
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheetDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_music);
        //给布局设置透明背景色，让图片突出来
        View viewById = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (viewById != null) {
            viewById.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        boolean itemMusicIsLiked = false;

        TextView musicNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_music_name);
        TextView artistNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_artist_name);
        List<MusicModel> musicModels = musicListAdapter.getMusicList();
        if (musicModels != null && musicModels.size() >= position) {
            MusicModel model = musicModels.get(position);
            // 设置当前选中音乐
            if (model != null) {
                playingViewModel.setCurrentCheckMusic(model);
            }
            if (model != null && musicNameText != null && artistNameText != null) {
                musicNameText.setText(model.getName());
                artistNameText.setText(model.getArtistName());
            }
            // 判断当前音乐是否在喜欢歌单，用于控制likeBtn图标样式
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
        if (add2Playlist != null) {
            add2Playlist.setOnClickListener(v12 -> Toast.makeText(getContext(), "抱歉，该功能暂未开放哦~", Toast.LENGTH_SHORT).show());
        }
        bottomSheetDialog.show();
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
        musicService = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setObserveOnViewModels() {
        if (playingViewModel != null) {
            playingViewModel.getFuzzySearchResultMusicList().observe(mActivity, musicModels -> {
                if (musicModels != null) {
                    if (musicModels.size() == 7 && searchMore != null) {
                        // 显示查看更多
                        searchMore.setVisibility(View.VISIBLE);
                    }
                    musicListAdapter.setMusicList(musicModels);
                    musicListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initIcons(View view) {
        cardview = view.findViewById(R.id.finding_search_cardview);
        searchBtn = view.findViewById(R.id.finding_search);
        searchEdit = view.findViewById(R.id.finding_editText);
        searchResultRecycleView = view.findViewById(R.id.finding_search_result_recv);

        searchMore = view.findViewById(R.id.finding_search_more);
        searchMore.setVisibility(View.INVISIBLE);

        searchBtn.setOnClickListener(v -> {
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
            historyList.add("清空历史记录");
            final String[] historyArray = historyList.toArray(new String[0]);
            listPopupWindow.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_popup_window, historyArray));
            listPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.btn_gray));
            listPopupWindow.setAnchorView(cardview);
            listPopupWindow.setModal(true);

            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == historyArray.length - 1) {
                        ShpUtils.clearSearchHistoryList(mActivity);
                    } else {
                        searchEdit.setText(historyArray[i]);
                    }
                    listPopupWindow.dismiss();
                }
            });
            listPopupWindow.show();
        });
    }

}