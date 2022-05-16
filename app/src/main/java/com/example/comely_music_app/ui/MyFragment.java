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
import androidx.lifecycle.Observer;
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
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;
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
    private PlaylistViewModel playlistViewModel;

    private RecyclerView playlistRecycleView;
    private PlaylistViewListAdapter adapter;

    private PlaylistService playlistService;
    private FragmentActivity mActivity;
    private SettingFragment settingFragment;
    private PlaylistDetailsFragment playlistDetailsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(
                Objects.requireNonNull(mActivity).getApplication(), mActivity);
        userInfoViewModel = ViewModelProviders.of(mActivity, savedState).get(UserInfoViewModel.class);
        playlistViewModel = ViewModelProviders.of(mActivity, savedState).get(PlaylistViewModel.class);

        playlistService = new PlaylistServiceImpl(playlistViewModel);
        initIcons(view);


        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        playlistRecycleView.setLayoutManager(manager);
        List<PlaylistModel> myCreatePlaylistFromShp = ShpUtils.getMyCreatePlaylistFromShp(mActivity);
        adapter = new PlaylistViewListAdapter(myCreatePlaylistFromShp);
        adapter.setListener(new AdapterClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                // 进入歌单界面
                PlaylistModel clickPlaylistItem = adapter.getPlaylistData().get(position);
                String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(mActivity)).getUsername();
                PlaylistSelectRequest request = new PlaylistSelectRequest();
                request.setUsername(username).setPlaylistName(clickPlaylistItem.getName());
                // 先查本地shp缓存
                PlaylistDetailsModel detailsModel =
                        ShpUtils.getPlaylistDetailsFromShpByPlaylistName(mActivity, clickPlaylistItem.getName());
                if (detailsModel != null && detailsModel.getPlaylistInfo() != null
                        && detailsModel.getMusicModelList() != null) {
                    // 本地缓存有就直接使用
                    playlistViewModel.setCurrentPlaylistDetails(detailsModel);
//                    playlistViewModel.setCurrentShowingPlaylist(detailsModel.getPlaylistInfo());
//                    playlistViewModel.setCurrentShowingMusicList(detailsModel.getMusicModelList());
                } else {
                    // 本地shp缓存没有的话再查数据库
                    playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_CREATE_PLAYLIST);
                }
                // 触发展示用户自建歌单详情页
                playlistViewModel.setShowCreated();
            }

            @Override
            public void onLongClick(View v, int position) {
                // 删除当前歌单
                showDeleteDialog(adapter.getPlaylistData().get(position).getName());
            }

            @Override
            public void onClickEditableBtn(View v, int position) {
                // 修改当前歌单
                PlaylistModel model = adapter.getPlaylistData().get(position);
                showUpdateDialog(model.getName(), model.getVisibility());
            }
        });
        playlistRecycleView.setAdapter(adapter);

        initDatas();

        // Lazy加载
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        settingFragment = new SettingFragment(myFragmentViewsCtrlLiveData);
        ft.add(R.id.frame_blank_for_setting, settingFragment);
        ft.commit();

        FragmentTransaction ft1 = mActivity.getSupportFragmentManager().beginTransaction();
        playlistDetailsFragment = new PlaylistDetailsFragment(myFragmentViewsCtrlLiveData, playlistViewModel);
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
        playlistRecycleView = view.findViewById(R.id.created_playlist_list);

        avatarImg.setOnClickListener(this);
        addPlaylist.setOnClickListener(this);
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

        if (playlistViewModel != null) {
            playlistViewModel.getMyCreatedPlaylists().observe(Objects.requireNonNull(mActivity), playlistModels -> {
                adapter.setPlaylistData(playlistModels);
                // 写入shp，下次直接打开应用不需要联网就可以加载
                ShpUtils.writeMyCreatePlaylistToShp(mActivity, playlistModels);
                adapter.notifyDataSetChanged();
                Log.d("TAG", "writeMyCreatePlaylistToShp: 写入shp");
            });
        }

        if (playlistViewModel != null) {
            playlistViewModel.getShowCreated().observe(Objects.requireNonNull(mActivity), model -> {
                PlaylistDetailsModel detailsModel = playlistViewModel.getCurrentPlaylistDetails().getValue();
                ShpUtils.writePlaylistDetailsIntoShp(mActivity, detailsModel);
                myFragmentViewsCtrlLiveData.setValue(2);
            });
            playlistViewModel.getShowCollect().observe(Objects.requireNonNull(mActivity),
                    model -> myFragmentViewsCtrlLiveData.setValue(3));
        }

        if (playlistViewModel != null) {
            playlistViewModel.getCurrentPlaylistDetails().observe(Objects.requireNonNull(mActivity), new Observer<PlaylistDetailsModel>() {
                @Override
                public void onChanged(PlaylistDetailsModel detailsModel) {
                    PlaylistModel playlistInfo = detailsModel.getPlaylistInfo();
                    playlistViewModel.updateCreatedPlaylistByName(playlistInfo.getName(), playlistInfo);
                }
            });
        }

        observeOnSuccessToShowToast();

        myFragmentViewsCtrlLiveData.observe(Objects.requireNonNull(mActivity), integer -> {
            if (integer == 0) {
                // myFragment界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.hide(settingFragment);
                ft.hide(playlistDetailsFragment);
                ft.commit();
                hideSettingOrDetailsFrameBlank();
            } else if (integer == 1) {
                // settings界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(settingFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 2) {
                playlistDetailsFragment.initDatas();
                // 不可收藏
                playlistDetailsFragment.setCollectNotAllowed();
                // 自建歌单界面
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.show(playlistDetailsFragment);
                ft.commit();
                showSettingOrDetailsFrameBlank();
            } else if (integer == 3) {
                playlistDetailsFragment.initDatas();
                // 可收藏
                Log.d("TAG", "setObserveOnViewModels: 设置为可收藏");
                // 自建歌单界面
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
        if (playlistViewModel != null) {
            playlistViewModel.getCreateSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "创建成功！", Toast.LENGTH_SHORT).show());
        }

        if (playlistViewModel != null) {
            playlistViewModel.getCreateFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "创建失败，请检查网络", Toast.LENGTH_SHORT).show());
        }

        if (playlistViewModel != null) {
            playlistViewModel.getDeleteSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "删除成功！", Toast.LENGTH_SHORT).show());
        }

        if (playlistViewModel != null) {
            playlistViewModel.getDeleteFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "删除失败，请检查网络", Toast.LENGTH_SHORT).show());
        }

        if (playlistViewModel != null) {
            playlistViewModel.getUpdateSuccessFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "修改成功！", Toast.LENGTH_SHORT).show());
        }

        if (playlistViewModel != null) {
            playlistViewModel.getUpdateFailedFlag().observe(Objects.requireNonNull(mActivity),
                    integer -> Toast.makeText(mActivity, "修改失败，请检查网络", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_playlist) {
            showCreateDialog();
        } else if (v.getId() == R.id.avatar_image) {
            myFragmentViewsCtrlLiveData.setValue(1);
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
                request.setName(playlistName).setUsername(username).setVisibility(visibility).setMusicNum(0);
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