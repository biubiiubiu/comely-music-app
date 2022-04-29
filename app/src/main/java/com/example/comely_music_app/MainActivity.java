package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.databinding.ActivityMainBinding;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.enums.PageStatus;
import com.example.comely_music_app.ui.viewmodels.MainViewModel;


public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    MainViewModel mainViewModel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.viewpagePlaying.setOrientation(ORIENTATION_VERTICAL);

        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        binding.viewpagePlaying.setAdapter(adapter);

        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(getApplication(), this);
        mainViewModel = ViewModelProviders.of(this, savedState).get(MainViewModel.class);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);

        mainViewModel.getIsPlayingLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
        });
    }

    /**
     * 改变页面状态
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor", "ResourceType"})
    private void changeIconByStatus(PageStatus status) {
        binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_down));
        binding.myText.setTextColor(Color.WHITE);
        binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_down));
        binding.findText.setTextColor(Color.WHITE);
        switch (status) {
            case MY:
                binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
                binding.myText.setTextColor(R.color.theme_green_light);
                break;
            case FINDING:
                binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
                binding.findText.setTextColor(R.color.theme_green_light);
                break;
            case PLAYING:
                binding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                View image = findViewById(R.id.music_cover_img);
//                mAnimation = AnimationUtils.loadAnimation(this, R.anim.rotaterepeat);
//                image.startAnimation(mAnimation);
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