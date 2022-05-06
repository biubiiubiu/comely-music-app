package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.api.service.UserService;
import com.example.comely_music_app.api.service.impl.UserServiceImpl;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;

import java.io.IOException;
import java.util.Objects;

import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager manager;

    private View frameBlank;
    private ImageButton playPauseBtn;
    private ViewPager2 viewPager;

    // 进度条
    SeekBar seekBar;

    private PlayingViewModel playingViewModel;
    private PlayingViewListAdapter viewPagerAdapter;

    private UserInfoViewModel userInfoViewModel;
    private UserService userService;

    private MediaPlayer mediaPlayer;


//    public final static String KEY_IS_PLAYING = "KEY_IS_PLAYING";
//    public final static String KEY_PAGE_STATUS = "KEY_PAGE_STATUS";

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
        userService = new UserServiceImpl(userInfoViewModel);
        mediaPlayer = new MediaPlayer();

        initIcons();

        // 检测登录状态
        checkLoginStatus();


        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPagerAdapter = new PlayingViewListAdapter(getApplicationContext(), playingViewModel);
        viewPager.setAdapter(viewPagerAdapter);
        // 滑动页面时更改当前音乐
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 停止当前音乐播放
                mediaPlayer.stop();
                if (Objects.requireNonNull(playingViewModel.getMusicListLiveData().getValue()).size() > position) {
                    playingViewModel.setCurrentMusic(playingViewModel.getMusicListLiveData().getValue().get(position));
                    // 播放的时候刷新seekbar
                }
            }
        });

        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        setObserveOnPlayingViewModel();
        setObserveOnUserInfoViewModel();
    }


    /**
     * 检查登录状态
     */
    private void checkLoginStatus() {
        SharedPreferences shp = getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String username = shp.getString(ShpConfig.USERNAME, "");
        if (!username.equals("")) {
            userService.getLoginStatus(username);
            return;
        }
        // 未登录，跳转到LoginActivity
        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent1);
    }

    private void setObserveOnUserInfoViewModel() {
        userInfoViewModel.getIsLogin().observe(this, isLogin -> {
            if (isLogin != null && !isLogin) {
                SharedPreferences shp = getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = shp.edit();
                editor.remove(ShpConfig.USERNAME);
                editor.apply();
            }
        });
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    private void setObserveOnPlayingViewModel() {
        // 切换播放状态
        playingViewModel.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (isPlaying) {
                // 更改图标
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                mediaPlayer.start();
                // 进度条及时刷新
//                new Thread() {
//                    @Override
//                    public void run() {
//                        while (playingViewModel.getIsPlayingLiveData().getValue()) {
//                            try {
//                                Thread.sleep(300);
//                                seekBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }.start();
            } else {
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
                mediaPlayer.pause();
            }
        });

        // 点赞 取消
        playingViewModel.getIsLikeLiveData().observe(this, isLike -> Toast.makeText(getApplicationContext(), "点赞写数据库:" + isLike, Toast.LENGTH_SHORT).show());

        // 获取musicModelList信息，存储到本地，并生成界面可使用的list
        playingViewModel.getMusicListLiveData().observe(this, musicModels -> {
            viewPagerAdapter.setMusicModelList(musicModels);
            // todo 把封面、歌词和MP3下载下来
            Toast.makeText(getApplicationContext(), "获取了" + musicModels.size() + "首音乐", Toast.LENGTH_SHORT).show();
        });

        // 当前界面滑动时，播放当前界面音乐
        playingViewModel.getCurrentMusic().observe(this, currentMusic -> {
            if (currentMusic != null) {
                String path = currentMusic.getAudioLocalPath();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    playingViewModel.setIsPlayingLiveData(true);
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
    }

    /**
     * 初始化界面组件
     */
    private void initIcons() {
        findViewById(R.id.main_find_btn).setOnClickListener(this);
        findViewById(R.id.main_home_btn).setOnClickListener(this);

        playPauseBtn = findViewById(R.id.main_play_pause_btn);
        frameBlank = findViewById(R.id.frame_blank);
        playPauseBtn = findViewById(R.id.main_play_pause_btn);
        viewPager = findViewById(R.id.viewpage_playing);

        seekBar = findViewById(R.id.process_sb);

        playPauseBtn.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 进度变化回调
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 触碰
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 放开
                playingViewModel.setCurrentPointFromUser(seekBar.getProgress());
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_play_pause_btn) {
            playingViewModel.changeIsPlayingLiveData();
        } else if (v.getId() == R.id.main_find_btn) {
            Intent toFindIntent = new Intent(MainActivity.this, FindActivity.class);
            startActivity(toFindIntent);
        } else if (v.getId() == R.id.main_home_btn) {
            Intent toHomeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(toHomeIntent);
        }
    }

//    private void checkout2TargetFragment(Fragment targetFragment) {
//        FragmentTransaction ft = manager.beginTransaction();
//        viewPager.setVisibility(View.INVISIBLE);
//        ft.replace(R.id.frame_blank, targetFragment);
//        ft.commit();
//        frameBlank.setVisibility(View.VISIBLE);
//    }

//    private void checkout2Playing() {
//        frameBlank.setVisibility(View.INVISIBLE);
//        viewPager.setVisibility(View.VISIBLE);
//    }


//    // 临时上传文件测试
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
}