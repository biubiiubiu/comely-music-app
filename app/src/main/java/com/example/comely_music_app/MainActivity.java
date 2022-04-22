package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.comely_music_app.ui.FindingFragemnt;
import com.example.comely_music_app.ui.MyFragemnt;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fs;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewpage_playing);
        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPager.setPageTransformer(new DepthPageTransformer());

        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        viewPager.setAdapter(adapter);

        fs = getSupportFragmentManager();
        ft = fs.beginTransaction();

        View play = findViewById(R.id.play_pause_btn);
        play.setOnClickListener(v -> {
            Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show();
        });

        View find = findViewById(R.id.find_btn);
        find.setOnClickListener(v -> {
            ft.replace(R.id.frameblank,new FindingFragemnt());
            ft.addToBackStack(null);
            Toast.makeText(this, "发现", Toast.LENGTH_SHORT).show();
        });

        View my = findViewById(R.id.my_btn);
        my.setOnClickListener(v -> {
            ft.replace(R.id.frameblank,new MyFragemnt());
            ft.addToBackStack(null);
            Toast.makeText(this, "我的", Toast.LENGTH_SHORT).show();
        });
    }
}