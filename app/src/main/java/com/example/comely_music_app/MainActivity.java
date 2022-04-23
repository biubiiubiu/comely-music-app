package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;
import com.example.comely_music_app.ui.enums.PageStatus;


public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    private View frameBlank;
    private ViewPager2 viewPager;
    private volatile PageStatus status;
    private ImageButton findBtn, playBtn, myBtn, checkModuleBtn, searchBtn;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpage_playing);
        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPager.setPageTransformer(new DepthPageTransformer());
        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        viewPager.setAdapter(adapter);


        if (manager == null) {
            manager = getSupportFragmentManager();
        }
        frameBlank = findViewById(R.id.frame_blank);

        if (status == null) {
            status = PageStatus.PLAYING;
        }
        initIcon();
        onClick();
    }

    /**
     * 初始化界面组件
     */
    private void initIcon() {
        playBtn = findViewById(R.id.play_pause_btn);
        findBtn = findViewById(R.id.find_btn);
        myBtn = findViewById(R.id.my_btn);
    }


    /**
     * 页面点击响应
     */
    private void onClick() {
        playBtn.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            status = isPlaying ? PageStatus.PLAYING : PageStatus.PAUSE;
            checkout2Playing();
            changeIconByStatus(status);
        });

        findBtn.setOnClickListener(v -> {
            status = PageStatus.FINDING;
            checkout2TargetFragment(new FindingFragment());
            changeIconByStatus(status);
        });

        myBtn.setOnClickListener(v -> {
            status = PageStatus.MY;
            checkout2TargetFragment(new MyFragment());
            changeIconByStatus(status);
        });
    }

    /**
     * 改变页面状态
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeIconByStatus(PageStatus status) {
        myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_down));
        findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_down));
        switch (status) {
            case MY:
                myBtn.setImageDrawable(getDrawable(R.drawable.ic_my_up));
                break;
            case FINDING:
                findBtn.setImageDrawable(getDrawable(R.drawable.ic_find_up));
                break;
            case PLAYING:
                playBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                break;
            case PAUSE:
                playBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
                break;
            default:
                playBtn.setImageDrawable(getDrawable(R.drawable.ps_image_placeholder));
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
}