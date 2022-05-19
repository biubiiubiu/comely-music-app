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
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.enums.PlayerModule;
import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.MainPlayingViewAdapter1;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;
import com.example.comely_music_app.ui.animation.ZoomOutPageTransformer;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
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
    private MainPlayingViewAdapter1 viewPagerAdapterEndlessModule, viewPagerAdapterPlaylistModule;

    private PlayingViewModel playingViewModel;
    private UserInfoViewModel userInfoViewModel;

    private UserService userService;
    private MediaPlayer mediaPlayer;

    private MyFragment myFragment;
    private FindingFragment findingFragment;

    private PlaylistService playlistService;
    private PlaylistViewModel playlistViewModel;

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
        playlistViewModel = ViewModelProviders.of(this, savedState).get(PlaylistViewModel.class);

        playlistService = new PlaylistServiceImpl(playlistViewModel);
        userService = new UserServiceImpl(userInfoViewModel);

        mediaPlayer = new MediaPlayer();

        // 检测登录状态
        checkLoginStatus();

        initIcon();

        viewPagerAdapterEndlessModule = new MainPlayingViewAdapter1(playingViewModel, PlayerModule.ENDLESS);
        viewPagerEndlessModule.setAdapter(viewPagerAdapterEndlessModule);
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
                    playingViewModel.setCurrentMusic(playingViewModel.getMusicListLiveData_endlessModule().getValue().get(position));
                    Log.d("TAG", "onPageSelected: 当前选择position:" + position + " "
                            + playingViewModel.getMusicListLiveData_endlessModule().getValue().get(position).getName());
                }
                // 最后一个item的时候再次获取一批
                if (playingViewModel.getPlayerModule().getValue() == PlayerModule.ENDLESS &&
                        position == viewPagerAdapterEndlessModule.getItemCount() - 1) {
                    List<String> tags = new ArrayList<>();
                    tags.add("古风");
                    viewPagerAdapterEndlessModule.addMusicListByTags(tags);
                }
            }
        });

        viewPagerAdapterPlaylistModule = new MainPlayingViewAdapter1(playingViewModel, PlayerModule.PLAYLIST);
        viewPagerPlaylistModule.setAdapter(viewPagerAdapterPlaylistModule);
        viewPagerPlaylistModule.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 停止当前音乐播放
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData_playlistModule().getValue()).size() > position) {
                    playingViewModel.setCurrentMusic(playingViewModel.getMusicListLiveData_playlistModule().getValue().get(position));
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
        myFragment = new MyFragment(playingViewModel);
        findingFragment = new FindingFragment();
        ft.add(R.id.frame_blank, myFragment);
        ft.add(R.id.frame_blank, findingFragment);
        ft.commit();

        setObserveOnPlayingViewModel();
        setObserveOnUserInfoViewModel();
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
        List<MusicModel> toAddIntoMyLike = playlistViewModel.getToAddIntoMyLike();
        List<MusicModel> toRemoveFromMyLike = playlistViewModel.getToRemoveFromMyLike();

        List<PlaylistMusicAddRequest.MusicAddInfo> toAddInfos = playlistService.transMusicModel2AddInfos(toAddIntoMyLike);
        List<PlaylistMusicAddRequest.MusicAddInfo> toRemoveInfos = playlistService.transMusicModel2AddInfos(toRemoveFromMyLike);

        String username = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this)).getUsername();

        PlaylistMusicAddRequest addRequest = new PlaylistMusicAddRequest();
        addRequest.setUsername(username).setMusicAddInfoList(toAddInfos);
        playlistService.addMusicIntoMyLike(addRequest);

        PlaylistMusicAddRequest removeRequest = new PlaylistMusicAddRequest();
        removeRequest.setUsername(username).setMusicAddInfoList(toRemoveInfos);
        playlistService.addMusicIntoMyLike(removeRequest);
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
                ShpUtils.clearAllCreatedPlaylistDetails(this);
                // 清除用户自建歌单缓存,注意先后顺序
                ShpUtils.clearCreatedPlaylist(this);
                // 清除用户喜欢歌单
                ShpUtils.clearMylikePlaylist(this);
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

        // 点赞 取消
        playingViewModel.getIsLikeLiveData().observe(this, isLike -> {
            MusicModel likedMusic = playingViewModel.getCurrentMusic().getValue();
            List<MusicModel> list = new ArrayList<>();
            list.add(likedMusic);
            if (isLike) {
                // 加入viewmodel
                playlistViewModel.addIntoMyLikePlaylist(list);
                playlistViewModel.like(list);
            } else {
                // 从viewmodel删除
                playlistViewModel.removeFromMyLikePlaylist(list);
                playlistViewModel.dislike(list);
            }
        });

        playlistViewModel.getMyLikePlaylistDetails().observe(this, detailsModel -> {
            // 把”我喜欢“存入本地缓存
            PlaylistDetailsModel myLikeDetails = playlistViewModel.getMyLikePlaylistDetails().getValue();
            playlistViewModel.setCurrentPlaylistDetails(myLikeDetails);
            ShpUtils.writeMyLikePlaylistDetailsIntoShp(this, myLikeDetails);
        });

        // 从后端获取musicModelList信息，刷新给adapter-endless，并生成界面可使用的list，并且notify一下
        playingViewModel.getMusicListLiveData_endlessModule().observe(this, musicModels -> {
            if (musicModels != null && musicModels.size() != 0) {
                viewPagerAdapterEndlessModule.setMusicList_endlessModule(musicModels);
                viewPagerAdapterEndlessModule.notifyDataSetChanged();
//                Toast.makeText(getApplicationContext(), "获取了" + musicModels.size() + "首音乐", Toast.LENGTH_SHORT).show();
            }
        });

        playingViewModel.getMusicListLiveData_playlistModule().observe(this, musicModels -> {
            if (musicModels != null && musicModels.size() != 0) {
                viewPagerAdapterPlaylistModule.setMusicList_playlistModule(musicModels);
                viewPagerAdapterPlaylistModule.notifyDataSetChanged();
            }
        });

        // 当前界面滑动时，播放当前界面音乐
        playingViewModel.getCurrentMusic().observe(this, currentMusic -> {
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
                // 显示歌单播放的viewpager
                if (viewPagerEndlessModule != null) {
                    viewPagerEndlessModule.setVisibility(View.INVISIBLE);
                }
                if (viewPagerPlaylistModule != null) {
                    viewPagerPlaylistModule.setVisibility(View.VISIBLE);
                }
                checkModuleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ps_ic_normal_back));
                playerModuleTxt.setText("歌单模式");
            }
        });

        playingViewModel.getShowBottomNevBar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                if (isShow) {
                    findViewById(R.id.bottom_nev_bar).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.bottom_nev_bar).setVisibility(View.INVISIBLE);
                }
            }
        });
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
        frameBlank = findViewById(R.id.frame_blank);
        playPauseBtn = findViewById(R.id.play_pause_btn);

        viewPagerEndlessModule = findViewById(R.id.viewpage_playing_endless_module);
        viewPagerEndlessModule.setOrientation(ORIENTATION_VERTICAL);
        viewPagerEndlessModule.setPageTransformer(DepthPageTransformer.getDepthPageTransformer());

        viewPagerPlaylistModule = findViewById(R.id.viewpage_playing_playlist_module);
        viewPagerPlaylistModule.setOrientation(ORIENTATION_VERTICAL);
        viewPagerPlaylistModule.setPageTransformer(ZoomOutPageTransformer.getZoomOutPageTransformer());
        viewPagerPlaylistModule.setVisibility(View.INVISIBLE);

        playerModuleTxt = findViewById(R.id.player_module_txt);
        checkModuleBtn = findViewById(R.id.check_module_btn);

        playPauseBtn.setOnClickListener(this);
        findBtn.setOnClickListener(this);
        myBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_pause_btn) {
            if (PageStatus.PLAYING.equals(playingViewModel.getPageStatusLiveData().getValue())) {
                playingViewModel.changeIsPlayingLiveData();
            } else {
                playingViewModel.changePageStatusLiveData(PageStatus.PLAYING);
            }
        } else if (v.getId() == R.id.find_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.FINDING);
        } else if (v.getId() == R.id.my_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.MY);
        } else if (v.getId() == R.id.check_module_btn) {
            if (playingViewModel != null && PlayerModule.PLAYLIST.equals(playingViewModel.getPlayerModule().getValue())) {
                playingViewModel.setPlayerModule(PlayerModule.ENDLESS);
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
    }

    private void checkoutIntoPlaying() {
        frameBlank.setVisibility(View.INVISIBLE);
        findViewById(R.id.main_top_bar).setVisibility(View.VISIBLE);
        viewPagerEndlessModule.setVisibility(View.VISIBLE);
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