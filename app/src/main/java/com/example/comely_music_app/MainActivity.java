package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.comely_music_app.ui.FindingFragment;
import com.example.comely_music_app.ui.MyFragment;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;


public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    private View frameBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewpage_playing);
        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPager.setPageTransformer(new DepthPageTransformer());

        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        viewPager.setAdapter(adapter);
        if (manager == null) {
            manager = getSupportFragmentManager();
        }
        frameBlank = findViewById(R.id.frame_blank);

        View play = findViewById(R.id.play_pause_btn);
        play.setOnClickListener(v -> {
            frameBlank.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        });

        View find = findViewById(R.id.find_btn);
        find.setOnClickListener(v -> {
            FragmentTransaction ft = manager.beginTransaction();
            viewPager.setVisibility(View.INVISIBLE);
            ft.replace(R.id.frame_blank, new FindingFragment());
            ft.commit();
            frameBlank.setVisibility(View.VISIBLE);
        });

        View my = findViewById(R.id.my_btn);
        my.setOnClickListener(v -> {
            FragmentTransaction ft = manager.beginTransaction();
            viewPager.setVisibility(View.INVISIBLE);
            ft.replace(R.id.frame_blank, new MyFragment());
//            transaction.add(new MyFragment(),"my");
            ft.commit();
            frameBlank.setVisibility(View.VISIBLE);
        });
    }

}