package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;

import lombok.ToString;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewpage_playing);
        viewPager.setOrientation(ORIENTATION_VERTICAL);
        viewPager.setPageTransformer(new DepthPageTransformer());

        PlayingViewListAdapter adapter = new PlayingViewListAdapter();
        viewPager.setAdapter(adapter);

        View play = findViewById(R.id.play_pause_btn);
        play.setOnClickListener(v->{
            Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show();
        });

        View find = findViewById(R.id.find_btn);
        find.setOnClickListener(v->{
            Toast.makeText(this, "发现", Toast.LENGTH_SHORT).show();
        });

        View my = findViewById(R.id.my_btn);
        my.setOnClickListener(v->{
            Toast.makeText(this, "我的", Toast.LENGTH_SHORT).show();
        });
    }
}