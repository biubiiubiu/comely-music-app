package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.databinding.ActivityMainBinding;
import com.example.comely_music_app.databinding.ItemPlayingViewBinding;
import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.viewmodels.MainViewModel;


public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    private Animation mAnimation;

    private MainViewModel mainViewModel;
    private ActivityMainBinding mainBinding;
    private ItemPlayingViewBinding itemPlayingViewBinding;

    public final static String KEY_IS_PLAYING = "KEY_IS_PLAYING";
    public final static String KEY_PAGE_STATUS = "KEY_PAGE_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        itemPlayingViewBinding = DataBindingUtil.setContentView(this, R.layout.item_playing_view);

        mainBinding.viewpagePlaying.setOrientation(ORIENTATION_VERTICAL);
        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        mainBinding.viewpagePlaying.setAdapter(adapter);

        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        // 存活时间更久
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(getApplication(), this);
        mainViewModel = ViewModelProviders.of(this, savedState).get(MainViewModel.class);
        mainBinding.setData(mainViewModel);
        mainBinding.setLifecycleOwner(this);

        mainViewModel.getIsPlayingLiveData().observe(this, new Observer<Boolean>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(Boolean isPlaying) {
                if(isPlaying){
                    mainBinding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                    // 封面旋转
                    mAnimation = AnimationUtils.loadAnimation(getApplication(), R.anim.rotaterepeat);
                    itemPlayingViewBinding.musicCoverImg.startAnimation(mAnimation);
                } else {
                    mainBinding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    // 封面停止旋转
                    itemPlayingViewBinding.musicCoverImg.clearAnimation();
                }
            }
        });


        mainViewModel.getPageStatusLiveData().observe(this, new Observer<Integer>() {
            @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
            @Override
            public void onChanged(Integer status) {
                switch (status) {
                    case 1:
                        checkout2TargetFragment(new MyFragment());
                        mainBinding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
                        mainBinding.myText.setTextColor(R.color.theme_green_light);
                        break;
                    case 2:
                        checkout2TargetFragment(new FindingFragment());
                        mainBinding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
                        mainBinding.findText.setTextColor(R.color.theme_green_light);
                        break;
                    case 3:
                        checkout2Playing();
                        break;
                }
            }
        });
    }

//    /**
//     * 页面点击响应
//     */
//    private void onClick() {
//        binding.playPauseBtn.setOnClickListener(v -> {
//            isPlaying = !isPlaying;
//            status = isPlaying ? PageStatus.PLAYING : PageStatus.PAUSE;
//            checkout2Playing();
//            changeIconByStatus(status);
//        });
//
//        binding.findBtn.setOnClickListener(v -> {
//            status = PageStatus.FINDING;
//            checkout2TargetFragment(new FindingFragment());
//            changeIconByStatus(status);
//        });
//        binding.myBtn.setOnClickListener(v -> {
//            status = PageStatus.MY;
//            checkout2TargetFragment(new MyFragment());
//            changeIconByStatus(status);
//        });
//    }

//    /**
//     * 改变页面状态
//     */
//    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor", "ResourceType"})
//    private void changeIconByStatus(PageStatus status) {
//        binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_down));
//        binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_down));
//        TextView myText = findViewById(R.id.my_text);
//        TextView findText = findViewById(R.id.find_text);
//        myText.setTextColor(Color.WHITE);
//        findText.setTextColor(Color.WHITE);
//        switch (status) {
//            case MY:
//                binding.myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
//                myText.setTextColor(R.color.theme_green_light);
//                break;
//            case FINDING:
//                binding.findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
//                findText.setTextColor(R.color.theme_green_light);
//                break;
//            case PLAYING:
//                binding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
//                View image = findViewById(R.id.music_cover_img);
//                mAnimation = AnimationUtils.loadAnimation(this, R.anim.rotaterepeat);
//                image.startAnimation(mAnimation);
//                break;
//            case PAUSE:
//                binding.playPauseBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
//                View image1 = findViewById(R.id.music_cover_img);
//                image1.clearAnimation();
//                break;
//        }
//    }

    private void checkout2TargetFragment(Fragment targetFragment) {
        FragmentTransaction ft = manager.beginTransaction();
        mainBinding.viewpagePlaying.setVisibility(View.INVISIBLE);
        ft.replace(mainBinding.frameBlank.getId(), targetFragment);
        ft.commit();
        mainBinding.frameBlank.setVisibility(View.VISIBLE);
    }

    private void checkout2Playing() {
        mainBinding.frameBlank.setVisibility(View.INVISIBLE);
        mainBinding.viewpagePlaying.setVisibility(View.VISIBLE);
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