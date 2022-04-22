package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;

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
    }
}