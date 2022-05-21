package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.AdapterClickListener;
import com.example.comely_music_app.ui.adapter.MainPlayingViewAdapter;
import com.example.comely_music_app.ui.adapter.OtherPlayingViewAdapter;
import com.example.comely_music_app.ui.adapter.PlaylistViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;
import com.example.comely_music_app.ui.animation.ZoomOutPageTransformer;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager manager;

    private TextView playerModuleTxt;
    private ImageButton checkModuleBtn;

    private View frameBlank;
    private ImageButton playPauseBtn;

    private ViewPager2 viewPagerEndlessModule, viewPagerPlaylistModule;
    private MainPlayingViewAdapter adapterEndlessModule;
    private OtherPlayingViewAdapter adapterPlaylistModule;

    private PlayingViewModel playingViewModel;
    private UserInfoViewModel userInfoViewModel;

    private UserService userService;
    private MediaPlayer mediaPlayer;

    private MyFragment myFragment;
    private FindingFragment findingFragment;

    private PlaylistService playlistService;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        // 存活时间更久
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(getApplication(), this);
        playingViewModel = ViewModelProviders.of(this, savedState).get(PlayingViewModel.class);
        userInfoViewModel = ViewModelProviders.of(this, savedState).get(UserInfoViewModel.class);

        playlistService = new PlaylistServiceImpl(playingViewModel);
        userService = new UserServiceImpl(userInfoViewModel);

        mediaPlayer = new MediaPlayer();

        // 检测登录状态
        checkLoginStatus();

        initIcon();

        adapterEndlessModule = new MainPlayingViewAdapter(playingViewModel);
        viewPagerEndlessModule.setAdapter(adapterEndlessModule);
        // 滑动页面时更改当前音乐
        viewPagerEndlessModule.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 停止当前音乐播放
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData_endlessModule().getValue()).size() > position) {
                    playingViewModel.setCurrentPlayMusic(playingViewModel.getMusicListLiveData_endlessModule().getValue().get(position));
                    Log.d("TAG", "onPageSelected: 当前选择position:" + position + " "
                            + playingViewModel.getMusicListLiveData_endlessModule().getValue().get(position).getName());
                }
                // 最后一个item的时候再次获取一批
                if (position == adapterEndlessModule.getItemCount() - 1) {
                    List<String> tags = new ArrayList<>();
                    tags.add("古风");
                    adapterEndlessModule.addMusicListByTags(tags);
                }
            }
        });

        adapterPlaylistModule = new OtherPlayingViewAdapter(playingViewModel, "歌单模式");
        viewPagerPlaylistModule.setAdapter(adapterPlaylistModule);
        // 滑动页面时更改当前音乐
        viewPagerPlaylistModule.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 停止当前音乐播放
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData_playlistModule().getValue()).size() > position) {
                    playingViewModel.setCurrentPlayMusic(playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position));
                    Log.d("TAG", "onPageSelected: 当前选择position:" + position + " "
                            + playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position).getName());
                }
            }
        });
        // 进度条及时刷新
//        new SeekBarThread(playingViewModel, seekBar, mediaPlayer).start();

        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        FragmentTransaction ft = manager.beginTransaction();
        myFragment = new MyFragment();
        findingFragment = new FindingFragment();
        ft.add(R.id.frame_blank, myFragment);
        ft.add(R.id.frame_blank, findingFragment);
        ft.commit();

        // 进入app需要从后端获取一次我喜欢、最近播放歌单
        initPlaylistDatas();

        setObserveOnPlayingViewModel();
        setObserveOnUserInfoViewModel();
    }

    private void initPlaylistDatas() {
        UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
        // 获取用户创建歌单
        playlistService.selectAllCreatedPlaylistByUsername(userInfo.getUsername());
        // 获取我喜欢歌单
        PlaylistSelectRequest request = new PlaylistSelectRequest();
        request.setUsername(userInfo.getUsername()).setPlaylistName(userInfo.getUsername() + "的喜欢歌单");
        playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_LIKE);
        // 获取最近播放歌单
        PlaylistSelectRequest request1 = new PlaylistSelectRequest();
        request1.setUsername(userInfo.getUsername()).setPlaylistName(userInfo.getUsername() + "的最近播放");
        playlistService.selectPlaylistDetailsByScene(request1, PlaylistSelectScene.RECENTLY_PLAY);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        storeAndUploadMyLikeList();

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void storeAndUploadMyLikeList() {
        // 上传到数据库
        // 在mysql里：把toadd添加到我喜欢，把toremove从我喜欢里删除
        List<MusicModel> toAddIntoMyLike = playingViewModel.getToAddIntoMyLike();
        List<MusicModel> toRemoveFromMyLike = playingViewModel.getToRemoveFromMyLike();

        List<PlaylistMusicAddRequest.MusicAddInfo> toAddInfos = playlistService.transMusicModel2AddInfos(toAddIntoMyLike);
        List<PlaylistMusicAddRequest.MusicAddInfo> toRemoveInfos = playlistService.transMusicModel2AddInfos(toRemoveFromMyLike);

        UserInfo userInfo = ShpUtils.getCurrentUserinfoFromShp(this);
        if (userInfo != null) {
            String username = userInfo.getUsername();
            PlaylistMusicAddRequest addRequest = new PlaylistMusicAddRequest();
            addRequest.setUsername(username).setMusicAddInfoList(toAddInfos);
            playlistService.addMusicIntoMyLike(addRequest);

            PlaylistMusicAddRequest removeRequest = new PlaylistMusicAddRequest();
            removeRequest.setUsername(username).setMusicAddInfoList(toRemoveInfos);
            playlistService.removeMusicFromMyLike(removeRequest);
        }

    }

    /**
     * 检查登录状态
     */
    private void checkLoginStatus() {
        SharedPreferences shp = getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            UserInfo info = gson.fromJson(userInfoStr, UserInfo.class);
            String username = info.getUsername();
            if (username != null) {
                userService.getLoginStatus(username);
            }
            return;
        }
        // 未登录，跳转到LoginActivity
        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent1);

    }

    private void setObserveOnUserInfoViewModel() {
        userInfoViewModel.getIsLogin().observe(this, isLogin -> {
            if (isLogin != null && !isLogin) {
                // 退出登录 (注：登录成功动作在loginActivity里触发，这里只能触发settingFragment的退出登录)
                // 清除当前用户信息缓存
                ShpUtils.clearCurrentUserInfo(this);
                // 清除用户所有自建歌单详情信息（包括音乐列表）
//                ShpUtils.clearAllCreatedPlaylistDetails(this);
                // 清除用户自建歌单缓存,注意先后顺序
                ShpUtils.clearCreatedPlaylist(this);

            }
        });
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor", "NotifyDataSetChanged"})
    private void setObserveOnPlayingViewModel() {
        // 切换播放状态
        playingViewModel.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (isPlaying) {
                // 更改图标
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                mediaPlayer.start();
            } else {
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
                mediaPlayer.pause();
            }
        });

        // 切换页面状态
        playingViewModel.getPageStatusLiveData().observe(this, status -> {
            ImageButton myBtn = findViewById(R.id.my_btn);
            ImageButton findBtn = findViewById(R.id.find_btn);
            myBtn.setImageDrawable(getDrawable(R.drawable.ic_home_down));
            findBtn.setImageDrawable(getDrawable(R.drawable.ic_finding_down));
            TextView myText = findViewById(R.id.my_text);
            TextView findText = findViewById(R.id.find_text);
            myText.setTextColor(R.color.white);
            findText.setTextColor(R.color.white);
            switch (status) {
                case MY:
                    changeFinding2myFragment();
                    checkoutOffPlaying();
                    myBtn.setImageDrawable(getDrawable(R.drawable.ic_home_up));
                    myText.setTextColor(R.color.theme_green_light);
                    break;
                case FINDING:
                    changeMy2findingFragment();
                    checkoutOffPlaying();
                    findBtn.setImageDrawable(getDrawable(R.drawable.ic_finding_up));
                    findText.setTextColor(R.color.theme_green_light);
                    break;
                case PLAYING:
                    checkoutIntoPlaying();
                    break;
            }
        });

        // 点赞 取消(当前播放的音乐)
        playingViewModel.getCurrentPlayMusicIsLiked().observe(this, isLike -> {
            MusicModel currentPlay = playingViewModel.getCurrentPlayMusic().getValue();
            List<MusicModel> list = new ArrayList<>();
            if (currentPlay != null) {
                list.add(currentPlay);
                if (isLike) {
                    // 加入viewmodel
                    playingViewModel.addIntoMyLikePlaylist(list);
                    playingViewModel.like(list);
                } else {
                    // 从viewmodel删除
                    playingViewModel.removeFromMyLikePlaylist(list);
                    playingViewModel.dislike(list);
                }
            }
        });
        // 点赞 取消(当前歌单详情界面选中的音乐)
        playingViewModel.getCurrentCheckMusicIsLiked().observe(this, isLike -> {
            MusicModel currentCheck = playingViewModel.getCurrentCheckMusic().getValue();
            List<MusicModel> list = new ArrayList<>();
            if (currentCheck != null) {
                list.add(currentCheck);
                if (isLike) {
                    // 加入viewmodel
                    playingViewModel.addIntoMyLikePlaylist(list);
                    playingViewModel.like(list);
                } else {
                    // 从viewmodel删除
                    playingViewModel.removeFromMyLikePlaylist(list);
                    playingViewModel.dislike(list);
                    // todo 用adapter.notifyItemChanged(position)去刷新界面数据
                }
            }
        });

        playingViewModel.getMyLikePlaylistDetails().observe(this, mylikePlaylistDetails -> {
            // 把”我喜欢“存入本地缓存
            if (mylikePlaylistDetails == null || mylikePlaylistDetails.getMusicModelList() == null) {
                return;
            }
            UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
            String username = userInfo.getUsername();
            String nickname = userInfo.getNickname();
            PlaylistModel info = new PlaylistModel();
            info.setName(username + "的喜欢歌单");
            info.setCreatedUserNickname(nickname);
            info.setVisibility(0);
            if (mylikePlaylistDetails.getMusicModelList() != null) {
                info.setMusicNum(mylikePlaylistDetails.getMusicModelList().size());
            } else {
                info.setMusicNum(0);
            }
            mylikePlaylistDetails.setPlaylistInfo(info);
            ShpUtils.writePlaylistDetailsIntoShp(this, mylikePlaylistDetails);
            // 如果当前展示的歌单是我喜欢歌单，那就通知界面刷新一下
            PlaylistDetailsModel curPlaylist = playingViewModel.getCurrentPlaylistDetails().getValue();
            if (curPlaylist != null && curPlaylist.getPlaylistInfo() != null) {
                if (info.getName().equals(curPlaylist.getPlaylistInfo().getName())) {
                    playingViewModel.setCurrentPlaylistDetails(mylikePlaylistDetails);
                }
            }
        });

        playingViewModel.getRecentlyPlaylistDetails().observe(this, recentlyPlaylistDetails -> {
            // 把”最近播放“存入本地缓存
            if (recentlyPlaylistDetails == null || recentlyPlaylistDetails.getMusicModelList() == null) {
                return;
            }
            UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
            String username = userInfo.getUsername();
            String nickname = userInfo.getNickname();
            PlaylistModel info = new PlaylistModel();
            info.setName(username + "的最近播放");
            info.setCreatedUserNickname(nickname);
            info.setVisibility(0);
            if (recentlyPlaylistDetails.getMusicModelList() != null) {
                info.setMusicNum(recentlyPlaylistDetails.getMusicModelList().size());
            } else {
                info.setMusicNum(0);
            }
            recentlyPlaylistDetails.setPlaylistInfo(info);
            ShpUtils.writePlaylistDetailsIntoShp(this, recentlyPlaylistDetails);
        });

        // 从后端获取musicModelList信息，刷新给adapter-endless，并生成界面可使用的list，并且notify一下
        playingViewModel.getMusicListLiveData_endlessModule().observe(this, musicModels -> {
            if (musicModels != null && musicModels.size() != 0) {
                adapterEndlessModule.setMusicList_endlessModule(musicModels);
                adapterEndlessModule.notifyDataSetChanged();
//                Toast.makeText(getApplicationContext(), "获取了" + musicModels.size() + "首音乐", Toast.LENGTH_SHORT).show();
            }
        });

        playingViewModel.getMusicListLiveData_playlistModule().observe(this, musicModels -> {
            if (musicModels != null && musicModels.size() != 0) {
                adapterPlaylistModule.setMusicList_playlistModule(musicModels);
                MusicModel curPlay = playingViewModel.getCurrentPlayMusic().getValue();
                int position = 0;
                for (int i = 0; i < musicModels.size(); i++) {
                    if (musicModels.get(i).equals(curPlay)) {
                        position = i;
                        break;
                    }
                }
                if (position < adapterPlaylistModule.getItemCount()) {
                    viewPagerPlaylistModule.setCurrentItem(position, false);
                }
                adapterPlaylistModule.notifyDataSetChanged();
            }
        });

        // 当前界面滑动时，播放当前界面音乐
        playingViewModel.getCurrentPlayMusic().observe(this, currentMusic -> {
            if (currentMusic != null) {
                String path = currentMusic.getAudioLocalPath();
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    playingViewModel.setIsPlayingLiveData(true);

                    TextView total = findViewById(R.id.playing_total_duration);
                    total.setText(formatTime(mediaPlayer.getDuration()));
                    // 访问后端接口，加入到最近播放（mysql）
                    PlaylistMusicAddRequest request = new PlaylistMusicAddRequest();
                    UserInfo userInfo = ShpUtils.getCurrentUserinfoFromShp(this);
                    if (userInfo != null) {
                        request.setUsername(userInfo.getUsername());
                        List<PlaylistMusicAddRequest.MusicAddInfo> infoList = new ArrayList<>();
                        PlaylistMusicAddRequest.MusicAddInfo info = new PlaylistMusicAddRequest.MusicAddInfo();
                        info.setTitle(currentMusic.getName());
                        info.setArtistName(currentMusic.getArtistName());
                        infoList.add(info);
                        request.setMusicAddInfoList(infoList);
                        playlistService.addMusicIntoRecentlyPlay(request);

                        List<MusicModel> list = new ArrayList<>();
                        list.add(currentMusic);
                        playingViewModel.addIntoRecentlyPlaylist(list);
                    }

                    // 刷新主页的播放歌单界面的播放位置
                    if (PlayerModule.PLAYLIST.equals(playingViewModel.getPlayerModule().getValue())) {
                        MusicModel curPlay = playingViewModel.getCurrentPlayMusic().getValue();
                        List<MusicModel> musicModels = playingViewModel.getMusicListLiveData_playlistModule().getValue();
                        if (musicModels != null) {
                            int position = 0;
                            for (int i = 0; i < musicModels.size(); i++) {
                                if (musicModels.get(i).equals(curPlay)) {
                                    position = i;
                                    break;
                                }
                            }
                            if (position < adapterPlaylistModule.getItemCount()) {
                                viewPagerPlaylistModule.setCurrentItem(position, false);
                            }
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "播放错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        playingViewModel.getCurrentPointFromUser().observe(this, integer -> {
            if (mediaPlayer != null) {
                int progress = (int) ((float) (mediaPlayer.getDuration() * integer) / 100.0);
                mediaPlayer.seekTo(progress);
            }
        });

        playingViewModel.getPlayerModule().observe(this, module -> {
            if (module.equals(PlayerModule.ENDLESS)) {
                // 显示无限播放的viewpager
                if (viewPagerEndlessModule != null) {
                    viewPagerEndlessModule.setVisibility(View.VISIBLE);
                }
                if (viewPagerPlaylistModule != null) {
                    viewPagerPlaylistModule.setVisibility(View.INVISIBLE);
                }
                checkModuleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_endless_module));
                playerModuleTxt.setText("随机推荐");
            }
            if (module.equals(PlayerModule.PLAYLIST)) {
                checkModuleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ps_ic_normal_back));
                playerModuleTxt.setText("歌单模式");
                // 刷新当前播放歌单的播放界面适配数据
                PlaylistDetailsModel curDetails = playingViewModel.getCurrentPlaylistDetails().getValue();
                if (curDetails != null && curDetails.getMusicModelList() != null) {
                    curDetails.getMusicModelList().size();
                    List<MusicModel> curMusicModelList = curDetails.getMusicModelList();
                    playingViewModel.setMusicListLiveData_playlistModule(curMusicModelList);
                }
            }
        });

        // 控制底部导航按钮
        playingViewModel.getShowBottomNevBar().observe(this, isShow -> {
            if (isShow) {
                findViewById(R.id.bottom_nev_bar).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.bottom_nev_bar).setVisibility(View.INVISIBLE);
            }
        });

        // 控制播放主界面的底部菜单是否弹出
        playingViewModel.getShowMainBottomSheetDialog().observe(this, i -> {
            if (i == 0) {
                // 第一次打开app会初始化，此时不展示弹窗
                return;
            }
            // 修改当前歌曲
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_music);
            //给布局设置透明背景色，让图片突出来
            View viewById = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
            if (viewById != null) {
                viewById.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
            TextView musicNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_music_name);
            TextView artistNameText = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_artist_name);
            MusicModel model = playingViewModel.getCurrentPlayMusic().getValue();
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

            ImageButton likeBtn = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_like_btn);
            if (likeBtn != null) {
                if (isLiked != null && isLiked) {
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

            // 播放主界面不能删除
            TextView delete = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_delete_txt);
            if (delete != null) {
                delete.setText("（当前音乐不能删除）");
            }

            View add2Playlist = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist);
            if (add2Playlist != null) {
                add2Playlist.setOnClickListener(v12 -> showBottomAdd2CreatedDialog(model));
            }
            bottomSheetDialog.show();
        });
    }

    private void showBottomAdd2CreatedDialog(MusicModel model) {
        // 修改当前歌曲
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_layout_add_into_mycreated);
        //给布局设置透明背景色，让图片突出来
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
        // 获取mycreated歌单recycleview
        RecyclerView createdRecv = bottomSheetDialog.getDelegate().findViewById(R.id.bt_dialog_add_to_playlist_mycreated_recv);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        if (createdRecv == null) {
            return;
        }
        createdRecv.setLayoutManager(manager);
        List<PlaylistModel> myCreatePlaylistFromShp = ShpUtils.getMyCreatePlaylistFromShp(this);
        PlaylistViewListAdapter adapter = new PlaylistViewListAdapter(myCreatePlaylistFromShp);
        adapter.setListener(new AdapterClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                // 进入歌单界面
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
        // 把musicModel添加进目标自建歌单
        PlaylistDetailsModel targetPlaylistDetails = ShpUtils.getPlaylistDetailsFromShpByPlaylistName(this, targetCreatedPlaylistName);
        if (targetPlaylistDetails == null) {
            // 如果没有这个自建歌单详情信息，就新建一个（也可以在新建自建歌单的时候会随之创建歌单详情信息）
            targetPlaylistDetails = new PlaylistDetailsModel();
            PlaylistModel info = new PlaylistModel();
            info.setName(targetCreatedPlaylistName);
            info.setMusicNum(0);
            UserInfo userinfo = ShpUtils.getCurrentUserinfoFromShp(this);
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

        ShpUtils.writePlaylistDetailsIntoShp(this, targetPlaylistDetails);
        playingViewModel.updateCreatedPlaylistByName(targetCreatedPlaylistName, targetPlaylistDetails.getPlaylistInfo());

        // 生成添加到歌单的信息，刷给mysql
        PlaylistMusicAddRequest request = new PlaylistMusicAddRequest();
        UserInfo userinfo = ShpUtils.getCurrentUserinfoFromShp(this);
        if (userinfo == null) {
            return;
        }
        request.setUsername(userinfo.getUsername()).setPlaylistName(targetCreatedPlaylistName);
        List<PlaylistMusicAddRequest.MusicAddInfo> infos = new ArrayList<>();
        infos.add(new PlaylistMusicAddRequest.MusicAddInfo(model.getName(), model.getArtistName()));
        request.setMusicAddInfoList(infos);
        playlistService.addMusicIntoPlaylist(request);
        Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int duration) {
        duration /= 1000;
        int min = duration / 60;
        int sec = duration - min * 60;
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * 初始化界面组件
     */
    private void initIcon() {
        playPauseBtn = findViewById(R.id.play_pause_btn);
        View findBtn = findViewById(R.id.find_btn);
        View myBtn = findViewById(R.id.my_btn);
        View searchBtn = findViewById(R.id.search_btn);
        frameBlank = findViewById(R.id.frame_blank);
        playPauseBtn = findViewById(R.id.play_pause_btn);

        viewPagerEndlessModule = findViewById(R.id.viewpage_playing_endless_module);
        viewPagerEndlessModule.setOrientation(ORIENTATION_VERTICAL);
        viewPagerEndlessModule.setPageTransformer(ZoomOutPageTransformer.getZoomOutPageTransformer());

        viewPagerPlaylistModule = findViewById(R.id.viewpage_playing_playlist_module);
        viewPagerPlaylistModule.setOrientation(ORIENTATION_VERTICAL);
        viewPagerPlaylistModule.setPageTransformer(DepthPageTransformer.getDepthPageTransformer());
        viewPagerPlaylistModule.setVisibility(View.INVISIBLE);

        playerModuleTxt = findViewById(R.id.player_module_txt);
        checkModuleBtn = findViewById(R.id.check_module_btn);

        playPauseBtn.setOnClickListener(this);
        findBtn.setOnClickListener(this);
        myBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        checkModuleBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_pause_btn) {
            if (PageStatus.PLAYING.equals(playingViewModel.getPageStatusLiveData().getValue())) {
                playingViewModel.changeIsPlayingLiveData();
            } else {
                playingViewModel.changePageStatusLiveData(PageStatus.PLAYING);
            }
        } else if (v.getId() == R.id.find_btn || v.getId() == R.id.search_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.FINDING);
        } else if (v.getId() == R.id.my_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.MY);
        } else if (v.getId() == R.id.check_module_btn) {
            if (playingViewModel != null && PlayerModule.PLAYLIST.equals(playingViewModel.getPlayerModule().getValue())) {
                playingViewModel.setPlayerModule(PlayerModule.ENDLESS);
                int currentItem = viewPagerEndlessModule.getCurrentItem();
                MusicModel curPlay = adapterEndlessModule.getMusicList_endlessModule().get(currentItem);
                if (curPlay != null) {
                    playingViewModel.setCurrentPlayMusic(curPlay);
                }
            }
        }
    }

    private void changeFinding2myFragment() {
        FragmentTransaction ft = manager.beginTransaction();
        ft.hide(findingFragment);
        ft.show(myFragment);
        ft.commit();
    }

    private void changeMy2findingFragment() {
        FragmentTransaction ft = manager.beginTransaction();
        ft.hide(myFragment);
        ft.show(findingFragment);
        ft.commit();
    }

    private void checkoutOffPlaying() {
        frameBlank.setVisibility(View.VISIBLE);
        findViewById(R.id.main_top_bar).setVisibility(View.INVISIBLE);
        viewPagerEndlessModule.setVisibility(View.INVISIBLE);
        viewPagerPlaylistModule.setVisibility(View.INVISIBLE);
    }

    private void checkoutIntoPlaying() {
        frameBlank.setVisibility(View.INVISIBLE);
        findViewById(R.id.main_top_bar).setVisibility(View.VISIBLE);
        if (PlayerModule.PLAYLIST.equals(playingViewModel.getPlayerModule().getValue())) {
            viewPagerPlaylistModule.setVisibility(View.VISIBLE);
        } else {
            viewPagerEndlessModule.setVisibility(View.VISIBLE);
        }
    }


//    public static class SeekBarThread extends Thread {
//        private final PlayingViewModel playingViewModel;
//        private final SeekBar seekBar;
//        private final MediaPlayer mediaPlayer;
//
//        public SeekBarThread(PlayingViewModel vm, SeekBar sb, MediaPlayer mp) {
//            playingViewModel = vm;
//            seekBar = sb;
//            mediaPlayer = mp;
//        }
//
//        @Override
//        public void run() {
//            if (playingViewModel != null && mediaPlayer != null && seekBar != null) {
//                while (playingViewModel.getIsPlayingLiveData().getValue() != null
//                        && playingViewModel.getIsPlayingLiveData().getValue()) {
//                    try {
//                        Thread.sleep(300);
//                        seekBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }


//    // 临时上传文件测试脚本
//    private void uploadFileTest() {
//        FileService fileService = new FileServiceImpl();
//        FileUploadRequest request = new FileUploadRequest();
//        List<FileUploadRequest.FileUploadInfo> list = new ArrayList<>();
//
//        List<String> filenames = new ArrayList<>();
//        filenames.add("许嵩 - 雨幕.mp3");
//        filenames.add("周杰伦 - 稻香.mp3");
//        filenames.add("文武贝 - 燕归巢.mp3");
//        filenames.add("不是花火呀 - TA.mp3");
//        filenames.add("队长 - 哪里都是你.mp3");
//        filenames.add("文武贝 - 南山南.mp3");
//
//        for (String filename : filenames) {
//            File file = new File("/storage/emulated/0/$MuMu共享文件夹/" + filename);
//            list.add(new FileUploadRequest.FileUploadInfo(filename, file.length()));
//        }
//
//        request.setFileUploadInfoList(list);
//        fileService.uploadFile(this, request);
//    }

//    private void addMusicTest() {
//        getFileList();
//
//    }

//    private void createMusicTest() {
//        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(getApplication(), this);
//        FileServiceViewModel fileServiceViewModel = ViewModelProviders.of(this, savedState).get(FileServiceViewModel.class);
//
//
//        AdminUploadMusicUtils utils = new AdminUploadMusicUtils(this, getApplicationContext(), null,
//                fileServiceViewModel);
//
//        String localBaseDir = FileConfig.BASE_PATH + "音乐/西方古典/";
//
//        File baseDirFile = new File(localBaseDir);
//        File[] files = baseDirFile.listFiles();
//        List<String> originalFilenameList = new ArrayList<>();
//        assert files != null;
//        for (File file : files) {
//            originalFilenameList.add(file.getName());
//        }
//
//        // 要手动写！！上传音乐文件
////        utils.uploadFiles(localBaseDir,"AUDIO/2022/05/07/", originalFilenameList);
//
//
////        上传歌手信息
////        utils.createArtist(originalFilenameList);
//
////        批量创建音乐
////        utils.createMusics(originalFilenameList);
//
//        // 要手动写！！创建音乐标签
////        utils.createTags("纯音乐", TagType.MUSIC, originalFilenameList);
//
//    }


}