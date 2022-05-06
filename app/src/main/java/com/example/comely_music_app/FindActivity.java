package com.example.comely_music_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FindActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initIcons();
    }

    private void initIcons() {
        findViewById(R.id.find_play_pause_btn).setOnClickListener(this);
        findViewById(R.id.find_find_btn).setOnClickListener(this);
        findViewById(R.id.find_home_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.find_find_btn) {
            // 刷新当前页面
        } else if (v.getId() == R.id.find_play_pause_btn) {
            Intent toPlayingIntent = new Intent(FindActivity.this, MainActivity.class);
            startActivity(toPlayingIntent);
        } else if (v.getId() == R.id.find_home_btn) {
            Intent toHomeIntent = new Intent(FindActivity.this, HomeActivity.class);
            startActivity(toHomeIntent);
        }
    }
}