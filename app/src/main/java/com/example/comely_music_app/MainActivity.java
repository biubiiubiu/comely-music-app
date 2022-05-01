package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager manager;

    private View frameBlank, findBtn, myBtn;
    private ImageButton playPauseBtn;
    private ViewPager2 viewPager;

    private PlayingViewModel playingViewModel;
    private PlayingViewListAdapter viewPagerAdapter;

//    public final static String KEY_IS_PLAYING = "KEY_IS_PLAYING";
//    public final static String KEY_PAGE_STATUS = "KEY_PAGE_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initIcon();
// 存活时间更久
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(getApplication(), this);
        playingViewModel = ViewModelProviders.of(this, savedState).get(PlayingViewModel.class);

        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPagerAdapter = new PlayingViewListAdapter(playingViewModel);
        viewPager.setAdapter(viewPagerAdapter);

        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        setObserveOnOkayingViewModel();


    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    private void setObserveOnOkayingViewModel() {
        // 切换页面状态
        playingViewModel.getPageStatusLiveData().observe(this, status -> {
            ImageButton myBtn = findViewById(R.id.my_btn);
            ImageButton findBtn = findViewById(R.id.find_btn);
            switch (status) {
                case MY:
                    TextView myText = findViewById(R.id.my_text);
                    checkout2TargetFragment(new MyFragment());
                    myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
                    myText.setTextColor(R.color.theme_green_light);
                    break;
                case FINDING:
                    TextView findText = findViewById(R.id.find_text);
                    checkout2TargetFragment(new FindingFragment());
                    findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
                    findText.setTextColor(R.color.theme_green_light);
                    break;
                case PLAYING:
                    checkout2Playing();
                    break;
            }
        });
        // 切换播放状态
        playingViewModel.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (isPlaying) {
                // 更改图标
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
            } else {
                playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
            }
        });

        // 点赞 取消
        playingViewModel.getIsLikeLiveData().observe(this, isLike -> {
            Toast.makeText(getApplicationContext(),"点赞写数据库",Toast.LENGTH_SHORT).show();
        });

        playingViewModel.getMusicListLiveData().observe(this, new Observer<List<MusicModel>>() {
            @Override
            public void onChanged(List<MusicModel> musicModels) {
                Toast.makeText(getApplicationContext(), "获取了" + musicModels.size() + "首音乐", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化界面组件
     */
    private void initIcon() {
        playPauseBtn = findViewById(R.id.play_pause_btn);
        findBtn = findViewById(R.id.find_btn);
        myBtn = findViewById(R.id.my_btn);
        frameBlank = findViewById(R.id.frame_blank);
        playPauseBtn = findViewById(R.id.play_pause_btn);
        viewPager = findViewById(R.id.viewpage_playing);

        playPauseBtn.setOnClickListener(this);
        findBtn.setOnClickListener(this);
        myBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_pause_btn) {
            playingViewModel.changeIsPlayingLiveData();
            playingViewModel.changePageStatusLiveData(PageStatus.PLAYING);
        } else if (v.getId() == R.id.find_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.FINDING);
        } else if (v.getId() == R.id.my_btn) {
            playingViewModel.changePageStatusLiveData(PageStatus.MY);
        }
    }

    private void checkout2TargetFragment(Fragment targetFragment) {
        FragmentTransaction ft = manager.beginTransaction();
        viewPager.setVisibility(View.INVISIBLE);
        ft.replace(R.id.frame_blank, targetFragment);
        ft.commit();
        frameBlank.setVisibility(View.VISIBLE);
    }

    private void checkout2Playing() {
        frameBlank.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }


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