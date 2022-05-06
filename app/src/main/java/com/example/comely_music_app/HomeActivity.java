package com.example.comely_music_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initIcons();
    }

    private void initIcons() {
        findViewById(R.id.home_home_btn).setOnClickListener(this);
        findViewById(R.id.home_play_pause_btn).setOnClickListener(this);
        findViewById(R.id.home_find_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home_home_btn) {
            // 刷新当前页面
        } else if (v.getId() == R.id.home_play_pause_btn) {
            Intent toPlayingIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(toPlayingIntent);
        } else if (v.getId() == R.id.home_find_btn) {
            Intent toHomeIntent = new Intent(HomeActivity.this, FindActivity.class);
            startActivity(toHomeIntent);
        }
    }
}