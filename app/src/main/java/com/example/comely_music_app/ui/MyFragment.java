package com.example.comely_music_app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.network.request.PlaylistCreateRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.request.PlaylistUpdateRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.PlaylistViewListAdapter;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ScreenUtils;
import com.example.comely_music_app.utils.ShpUtils;

import java.util.List;
import java.util.Objects;

public class MyFragment extends Fragment implements View.OnClickListener {
    private TextView nicknameTxt;
    private View settingOrDetailsFrameBlank;
    private View appbar, scrollView;
    /**
     * 用于控制界面
     */
    private final MutableLiveData<Integer> myFragmentViewsCtrlLiveData = new MutableLiveData<>(0);

    private UserInfoViewModel userInfoViewModel;

    private RecyclerView myCreatedPlaylistRecycleView;
    private PlaylistViewListAdapter playlistsAdapter;

    private PlaylistService playlistService;
    private FragmentActivity mActivity;
    private SettingFragment settingFragment;
    private PlaylistDetailsFragment playlistDetailsFragment;
    private PlayingViewModel playingViewModel;

    public MyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(mActivity).getApplication(), mActivity);

        userInfoViewModel = ViewModelProviders.of(mActivity, savedState).get(UserInfoViewModel.class);
        playingViewModel = ViewModelProviders.of(mActivity, savedState).get(PlayingViewModel.class);
        playlistService = new PlaylistServiceImpl(playingViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        initIcons(view);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        myCreatedPlaylistRecycleView.setLayoutManager(manager);
        List<PlaylistModel> myCreatePlaylistFromShp = ShpUtils.getMyCreatePlaylistFromShp(mActivity);
        playlistsAdapter = new PlaylistViewListAdapter(myCreatePlaylistFromShp);
        playlistsAdapter.setListener(new AdapterClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                // 进入歌单界面
                PlaylistModel clickPlaylistItem = playlistsAdapter.getPlaylistData().get(position);
                String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
                PlaylistSelectRequest request = new PlaylistSelectRequest();
                request.setUsername(username).setPlaylistName(clickPlaylistItem.getName());
                // 先查本地shp缓存
                PlaylistDetailsModel createdDetails =
                        ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity, clickPlaylistItem.getName());
                if (createdDetails != null && createdDetails.getPlaylistInfo() != null
                        && createdDetails.getMusicModelList() != null) {
                    // 本地缓存有就直接使用
                    playingViewModel.setCurrentPlaylistDetails(createdDetails);
                } else {
                    // 本地shp缓存没有的话再查数据库
                    playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_CREATE_PLAYLIST);
                }
                // 触发展示用户自建歌单详情页
                playingViewModel.setShowCreated();
            }

            @Override
            public void onLongClick(View v, int position) {
                // 删除当前歌单
                showDeleteDialog(playlistsAdapter.getPlaylistData().get(position).getName());
            }

            @Override
            public void onClickBtnBehindTitle(View v, int position) {
                // 修改当前歌单
                PlaylistModel model = playlistsAdapter.getPlaylistData().get(position);
                showUpdateDialog(model.getName(), model.getVisibility());
            }

            @Override
            public void onClickRightBtn(View v, int position) {
            }
        });
        myCreatedPlaylistRecycleView.setAdapter(playlistsAdapter);

        initDatas();

        // Lazy加载
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        settingFragment = new SettingFragment(myFragmentViewsCtrlLiveData);
        ft.add(R.id.frame_blank_for_setting, settingFragment);
        ft.commit();

        FragmentTransaction ft1 = mActivity.getSupportFragmentManager().beginTransaction();
        playlistDetailsFragment = new PlaylistDetailsFragment(myFragmentViewsCtrlLiveData);
        ft1.add(R.id.frame_blank_for_setting, playlistDetailsFragment);
        ft1.commit();


        setObserveOnViewModels();
        return view;
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
    }

    private void initDatas() {
        UserInfo info = ShpUtils.getCurrentUserinfoFromShp(mActivity);
        if (info != null) {
            // 用户昵称
            String nickname = info.getNickname();
            if (nickname != null && nickname.length() > 0) {
                nicknameTxt.setText(nickname);
            }

            // 用户歌单
            playlistService.selectAllCreatedPlaylistByUsername(info.getUsername());
        }
    }

    private void initIcons(View view) {
        ImageView avatarImg = view.findViewById(R.id.avatar_image);
        nicknameTxt = view.findViewById(R.id.nickname);
        settingOrDetailsFrameBlank = view.findViewById(R.id.frame_blank_for_setting);
        appbar = view.findViewById(R.id.appbar);
        scrollView = view.findViewById(R.id.my_nested_scroll_view);
        ImageButton addPlaylist = view.findViewById(R.id.add_playlist);
        myCreatedPlaylistRecycleView = view.findViewById(R.id.created_playlist_list);

        View mylikeBtn = view.findViewById(R.id.my_like_playlist);
        View recentlyPlayBtn = view.findViewById(R.id.my_recently_played);

        avatarImg.setOnClickListener(this);
        addPlaylist.setOnClickListener(this);
        mylikeBtn.setOnClickListener(this);
        recentlyPlayBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_playlist) {
            showCreateDialog();
        } else if (v.getId() == R.id.avatar_image) {
            myFragmentViewsCtrlLiveData.setValue(1);
        } else if (v.getId() == R.id.my_like_playlist) {
            // 进入我喜欢歌单界面
            // 先查本地shp缓存
            String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
            PlaylistDetailsModel mylikeDetails = ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity, username + "的喜欢歌单");
            if (mylikeDetails != null && mylikeDetails.getMusicModelList() != null) {
                // 本地缓存有就直接使用
                playingViewModel.setMyLikePlaylistDetails(mylikeDetails);
                playingViewModel.setCurrentPlaylistDetails(mylikeDetails);
            } else {
                // 本地shp缓存没有的话再查数据库
                PlaylistSelectRequest request = new PlaylistSelectRequest();
                request.setUsername(username).setPlaylistName(username + "的喜欢歌单");
                playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_LIKE);
            }
            // 触发展示用户我喜欢歌单详情页
            playingViewModel.setShowMylike();
        } else if (v.getId() == R.id.my_recently_played) {
            // 进入最近播放歌单界面
            // 先查本地shp缓存
            String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
            PlaylistDetailsModel recentlyPlay = ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity, username + "的最近播放");
            if (recentlyPlay != null && recentlyPlay.getMusicModelList() != null) {
                // 本地缓存有就直接使用
                playingViewModel.setRecentlyPlaylistDetails(recentlyPlay);
                playingViewModel.setCurrentPlaylistDetails(recentlyPlay);
            } else {
                // 本地shp缓存没有的话再查数据库
                PlaylistSelectRequest request = new PlaylistSelectRequest();
                request.setUsername(username).setPlaylistName(username + "的最近播放");
                playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.RECENTLY_PLAY);
            }
            // 触发展示用户最近播放歌单详情页
            playingViewModel.setShowRecentlyPlay();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setObserveOnViewModels() {
        if (userInfoViewModel != null) {
            // 这里是在MainActivity里监听修改用户信息
            userInfoViewModel.getUserInfo().observe(Objects.requireNonNull(mActivity), userInfo -> {
                if (userInfo != null && userInfo.getNickname() != null && userInfo.getNickname().length() > 0) {
                    // 刷界面
                    nicknameTxt.setText(userInfo.getNickname());
                    // 写入shp
                    UserInfo currentUserinfoFromShp = ShpUtils.getCurrentUserinfoFromShp(mActivity);
                    List<PlaylistModel> myCreatePlaylist = ShpUtils.getMyCreatePlaylistFromShp(mActivity);
                    if (myCreatePlaylist != null) {
                        for (PlaylistModel model : myCreatePlaylist) {
                            // 修改自建歌单的创建者用户名
                            PlaylistDetailsModel details = ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity, model.getName());
                            if (details != null) {
                                details.getPlaylistInfo().setCreatedUserNickname(userInfo.getNickname());
                                ShpUtils.writePlaylistDetailsIntoShp(mActivity, details);
                            }
                        }
                    }
                    if (!userInfo.equals(currentUserinfoFromShp)) {
                        ShpUtils.writeCurrentUserinfoToShp(mActivity, userInfo);
                    }
                }
            });
        }

        if (playingViewModel != null) {
            playingViewModel.getMyCreatedPlaylists().observe(Objects.requireNonNull(mActivity), playlistModels -> {
                playlistsAdapter.setPlaylistData(playlistModels);
                // 写入shp，下次直接打开应用不需要联网就可以加载
                ShpUtils.writeMyCreatePlaylistToShp(mActivity, playlistModels);
                playlistsAdapter.notifyDataSetChanged();
                Log.d("TAG", "writeMyCreatePlaylistToShp: 写入shp");
            });
        }

        if (playingViewModel != null) {
            playingViewModel.getShowCreated().observe(Objects.requireNonNull(mActivity), i -> {
                PlaylistDetailsModel detailsModel = playingViewModel.getCurrentPlaylistDetails().getValue();
                ShpUtils.writePlaylistDetailsIntoShp(mActivity, detailsModel);
                myFragmentViewsCtrlLiveData.setValue(2);
            });
            playingViewModel.getShowCollect().observe(Objects.requireNonNull(mActivity),
                    i -> myFragmentViewsCtrlLiveData.setValue(3));
            playingViewModel.getShowMylike().observe(Objects.requireNonNull(mActivity),
                    i -> myFragmentViewsCtrlLiveData.setValue(4));
            playingViewModel.getShowRecentlyPlay().observe(Objects.requireNonNull(mActivity),
                    i -> myFragmentViewsCtrlLiveData.setValue(5));
        }

        if (playingViewModel != null) {
            // 当前歌单数据变化，修改myfragment歌单列表的自建歌单列表
            playingViewModel.getCurrentPlaylistDetails().observe(Objects.requireNonNull(mActivity), detailsModel -> {
                PlaylistModel playlistInfo = detailsModel.getPlaylistInfo();
                UserInfo userinfo = ShpUtils.getCurrentUserinfoFromShp(mActivity);
                if (userinfo != null) {
                    if (userinfo.getNickname().equals(playlistInfo.getCreatedUserNickname())) {
                        // 这里根据歌单名称来修改created歌单中的info
                        playingViewModel.updateCreatedPlaylistByName(playlistInfo.getName(), playlistInfo);
                    }
                }
            });
        }

        observeOnSuccessToShowToast();

        myFragmentViewsCtrlLiveData.observe(Objects.requireNonNull(mActivity), integer -> {
            if (integer == 0) {
                // myFragment界面
                Log.d("TAG", "setObserveOnViewModels: 进入myfragment界面");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(settingFragment);
                ft.hide(playlistDetailsFragment);
                ft.commit();
                hideSettingOrDetailsFrameBlank();
            } else if (integer == 1) {
                // settings界面
                Log.d("TAG", "setObserveOnViewModels: 进入settings界面");
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(settingFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 2) {
                playlistDetailsFragment.initDatas();
                // 不可收藏
                Log.d("TAG", "setObserveOnViewModels: 进入自建歌单界面，设置为不可收藏");
                playlistDetailsFragment.setCollectNotAllowed();
                // 自建歌单界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistDetailsFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 3) {
                playlistDetailsFragment.initDatas();
                // 可收藏
                Log.d("TAG", "setObserveOnViewModels: 进入收藏歌单界面，设置为可收藏");
                playlistDetailsFragment.setCollectAllowed();
                // 收藏歌单界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistDetailsFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 4) {
                playlistDetailsFragment.initDatas();
                playlistDetailsFragment.setCollectNotAllowed();
                // 可收藏
                Log.d("TAG", "setObserveOnViewModels: 进入我喜欢歌单界面，我喜欢歌单");
                // 喜欢歌单界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistDetailsFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 5) {
                playlistDetailsFragment.initDatas();
                playlistDetailsFragment.setCollectNotAllowed();
//                playlistDetailsFragment.setPlaylistInfoNotShowed();
                // 可收藏
                Log.d("TAG", "setObserveOnViewModels: 进入最近播放歌单界面，最近播放歌单");
                // 最近播放歌单界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistDetailsFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            }
        });
    }

    private void showSettingOrDetailsFrameBlank() {

        if (appbar.getVisibility() == View.VISIBLE) {
            appbar.setVisibility(View.INVISIBLE);
        }
        if (scrollView.getVisibility() == View.VISIBLE) {
            scrollView.setVisibility(View.INVISIBLE);
        }
        if (settingOrDetailsFrameBlank.getVisibility() == View.INVISIBLE) {
            settingOrDetailsFrameBlank.setVisibility(View.VISIBLE);
        }
    }

    private void hideSettingOrDetailsFrameBlank() {
        if (appbar.getVisibility() == View.INVISIBLE) {
            appbar.setVisibility(View.VISIBLE);
        }
        if (scrollView.getVisibility() == View.INVISIBLE) {
            scrollView.setVisibility(View.VISIBLE);
        }
        if (settingOrDetailsFrameBlank.getVisibility() == View.VISIBLE) {
            settingOrDetailsFrameBlank.setVisibility(View.INVISIBLE);
        }
    }

    private void observeOnSuccessToShowToast() {
        if (playingViewModel != null) {
            playingViewModel.getCreateSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "创建成功！", Toast.LENGTH_SHORT).show());
        }

        if (playingViewModel != null) {
            playingViewModel.getCreateFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "创建失败，请检查网络", Toast.LENGTH_SHORT).show());
        }

        if (playingViewModel != null) {
            playingViewModel.getDeleteSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "删除成功！", Toast.LENGTH_SHORT).show());
        }

        if (playingViewModel != null) {
            playingViewModel.getDeleteFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "删除失败，请检查网络", Toast.LENGTH_SHORT).show());
        }

        if (playingViewModel != null) {
            playingViewModel.getUpdateSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "修改成功！", Toast.LENGTH_SHORT).show());
        }

        if (playingViewModel != null) {
            playingViewModel.getUpdateFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "修改失败，请检查网络", Toast.LENGTH_SHORT).show());
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void showCreateDialog() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_create_playlist, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(mActivity)).setView(view).create();

        TextView cancel = view.findViewById(R.id.dialog_create_cancel);
        TextView complete = view.findViewById(R.id.dialog_create_complete);
        EditText editText = view.findViewById(R.id.dialog_create_playlist_name);
        RadioButton rb = view.findViewById(R.id.dialog_create_playlist_visibility_rb);

        cancel.setOnClickListener(v -> dialog.dismiss());

        complete.setOnClickListener(v -> {
            String playlistName = editText.getText().toString();
            if (playlistName.length() == 0) {
                Toast.makeText(mActivity, "歌单名称不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                int visibility = 1;
                if (rb.isChecked()) {
                    visibility = 0;
                }
                PlaylistCreateRequest request = new PlaylistCreateRequest();
                String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
                // relation=0是我喜欢，1-自建歌单，2-收藏歌单
                request.setName(playlistName).setUsername(username).setVisibility(visibility).setMusicNum(0).setRelation(1);
                playlistService.createPlaylist(request);
                //... To-do
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的4/5  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setBackgroundDrawable(Objects.requireNonNull(mActivity).getDrawable(R.color.ps_color_transparent));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(mActivity)) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showUpdateDialog(String playlistName, Integer visibility) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_update_playlist, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(mActivity)).setView(view).create();

        TextView cancel = view.findViewById(R.id.dialog_update_cancel);
        TextView complete = view.findViewById(R.id.dialog_update_complete);
        EditText editText = view.findViewById(R.id.dialog_update_playlist_name);
        RadioButton rb = view.findViewById(R.id.dialog_update_playlist_visibility_rb);

        cancel.setOnClickListener(v -> dialog.dismiss());

        complete.setOnClickListener(v -> {
            String newPlaylistName = editText.getText().toString();
            int newVisibility = 1;
            if (rb.isChecked()) {
                newVisibility = 0;
            }
            if (newPlaylistName.length() == 0) {
                Toast.makeText(mActivity, "歌单名称不能为空！", Toast.LENGTH_SHORT).show();
            } else if (newPlaylistName.equals(playlistName) && newVisibility == visibility) {
                Toast.makeText(mActivity, "您还未做出任何修改~", Toast.LENGTH_SHORT).show();
            } else {
                PlaylistUpdateRequest request = new PlaylistUpdateRequest();
                String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
                request.setOldName(playlistName).setOldUsername(username)
                        .setNewName(newPlaylistName).setVisibility(newVisibility);
                playlistService.updatePlaylist(request);
                //... To-do
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的4/5  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setBackgroundDrawable(Objects.requireNonNull(mActivity).getDrawable(R.color.ps_color_transparent));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(mActivity)) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDeleteDialog(String playlistName) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_delete_playlist, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(mActivity)).setView(view).create();

        TextView title = view.findViewById(R.id.dialog_delete_title);
        title.setText("是否从列表中移除此歌单？");
        TextView cancel = view.findViewById(R.id.dialog_delete_cancel);
        TextView complete = view.findViewById(R.id.dialog_delete_complete);
        TextView playlistNameTxt = view.findViewById(R.id.dialog_delete_playlist_name);
        playlistNameTxt.setText(playlistName);

        cancel.setOnClickListener(v -> dialog.dismiss());

        complete.setOnClickListener(v -> {

            // 删除数据库歌单
            PlaylistSelectRequest request = new PlaylistSelectRequest();
            String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
            request.setPlaylistName(playlistName).setUsername(username);
            playlistService.deletePlaylist(request);
            //... To-do
            dialog.dismiss();
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的4/5  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setBackgroundDrawable(Objects.requireNonNull(mActivity).getDrawable(R.color.ps_color_transparent));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(mActivity)) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}