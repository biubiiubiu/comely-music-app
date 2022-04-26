package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.databinding.ActivityMainBinding;
import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.viewmodels.MainViewModel;


public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;

    private volatile PageStatus status;
    private boolean isPlaying;
    private Animation mAnimation;

    MainViewModel mainViewModel;
    ActivityMainBinding binding;

    public final static String KEY_IS_PLAYING = "KEY_IS_PLAYING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


//        viewPager = findViewById(R.id.viewpage_playing);
        binding.viewpagePlaying.setOrientation(ORIENTATION_VERTICAL);
//        viewPager.setOrientation(ORIENTATION_VERTICAL);
//        viewPager.setPageTransformer(new DepthPageTransformer());
        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        binding.viewpagePlaying.setAdapter(adapter);
//        viewPager.setAdapter(adapter);

        isPlaying = false;
        if (manager == null) {
            manager = getSupportFragmentManager();
        }
//        frameBlank = findViewById(R.id.frame_blank);

        if (status == null) {
            status = PageStatus.PLAYING;
        }
//        initIcon();
        onClick();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setData(mainViewModel);
        binding.setLifecycleOwner(this);
    }

//    /**
//     * 初始化界面组件
//     */
//    private void initIcon() {
//        playBtn = findViewById(R.id.play_pause_btn);
//        findBtn = findViewById(R.id.find_btn);
//        myBtn = findViewById(R.id.my_btn);
//    }


    /**
     * 页面点击响应
     */
    private void onClick() {
        binding.playPauseBtn.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            status = isPlaying ? PageStatus.PLAYING : PageStatus.PAUSE;
            checkout2Playing();
            changeIconByStatus(status);
        });
//        playBtn.setOnClickListener(v -> {
//            isPlaying = !isPlaying;
//            status = isPlaying ? PageStatus.PLAYING : PageStatus.PAUSE;
//            checkout2Playing();
//            changeIconByStatus(status);
//        });

        binding.findBtn.setOnClickListener(v -> {
            status = PageStatus.FINDING;
            checkout2TargetFragment(new FindingFragment());
            changeIconByStatus(status);
        });
//        findBtn.setOnClickListener(v -> {
//            status = PageStatus.FINDING;
//            checkout2TargetFragment(new FindingFragment());
//            changeIconByStatus(status);
//        });
        binding.myBtn.setOnClickListener(v -> {
            status = PageStatus.MY;
            checkout2TargetFragment(new MyFragment());
            changeIconByStatus(status);
        });
//        myBtn.setOnClickListener(v -> {
//            status = PageStatus.MY;
//            checkout2TargetFragment(new MyFragment());
//            changeIconByStatus(status);
//        });
    }

    /**
     * 改变页面状态
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor", "ResourceType"})
    private void changeIconByStatus(PageStatus status) {
        binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_down));
        binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_down));
//        myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_down));
//        findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_down));
        TextView myText = findViewById(R.id.my_text);
        TextView findText = findViewById(R.id.find_text);
        myText.setTextColor(Color.WHITE);
        findText.setTextColor(Color.WHITE);
        switch (status) {
            case MY:
                binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
                myText.setTextColor(R.color.theme_green_light);
                break;
            case FINDING:
                binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
                findText.setTextColor(R.color.theme_green_light);
                break;
            case PLAYING:
                binding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                View image = findViewById(R.id.music_cover_img);
                mAnimation = AnimationUtils.loadAnimation(this, R.anim.rotaterepeat);
                image.startAnimation(mAnimation);
                break;
            case PAUSE:
                binding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
                View image1 = findViewById(R.id.music_cover_img);
                image1.clearAnimation();
                break;
        }
    }

    private void checkout2TargetFragment(Fragment targetFragment) {
        FragmentTransaction ft = manager.beginTransaction();
        binding.viewpagePlaying.setVisibility(View.INVISIBLE);
        ft.replace(R.id.frame_blank, targetFragment);
        ft.commit();
        binding.frameBlank.setVisibility(View.VISIBLE);
    }

    private void checkout2Playing() {
        binding.frameBlank.setVisibility(View.INVISIBLE);
        binding.viewpagePlaying.setVisibility(View.VISIBLE);
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