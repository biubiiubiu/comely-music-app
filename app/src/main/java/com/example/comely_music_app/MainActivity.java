package com.example.comely_music_app;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;


import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.comely_music_app.ui.adapter.PlayingViewListAdapter;
import com.example.comely_music_app.ui.animation.DepthPageTransformer;
import com.example.comely_music_app.ui.fragments.PlayingFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private FragmentManager fm;
    private ImageButton enterPlayingPage;

    private PlayingFragment playingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavControllerAndAppBarConfiguration();
        if (fm == null) {
            fm = getSupportFragmentManager();
        }
        enterPlayingPage = findViewById(R.id.ic_play);
        enterPlayingPage.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "playing", Toast.LENGTH_SHORT).show();
            if (playingFragment == null) {
                playingFragment = new PlayingFragment();
            }
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_layout, playingFragment);
            ft.commit();
        });
    }

    // 汉堡图绑定拉出导航的事件
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setNavControllerAndAppBarConfiguration() {
        // 设置新的 toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // navController 根据控制行为替换掉 HostFragment 中的 Fragment
        // navController 由于拿到了 R.id.host_fragment，故也拿到了其中包含的 navigation.xml
        // navigation.xml 拿到了三个 fragment 布局，并映射到对应的 Fragment
        navController = Navigation.findNavController(this, R.id.hostFragment);
        // drawerLayout 包含背景 HostFragment 以及导航 navigationView
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        // appBarConfiguration 自动获取 toolbar 并将各个 Fragment 页面注册到其中
        appBarConfiguration = new AppBarConfiguration
//                .Builder(navController.getGraph())
                .Builder(R.id.textFragment, R.id.listFragment, R.id.pagerFragment)
                .setDrawerLayout(drawerLayout)
                .build();
        // 将 toolbar 和 navController 关联
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // 将 navigationView 和 navController 关联
        // navigationView 中的 menu 将会自动根据 navigation.xml 中的各 id 来绑定对应的 Fragment
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


//    FileService fileService = new FileServiceImpl();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Button button = findViewById(R.id.button);
//        button.setOnClickListener(v -> {
//            List<FileUploadRequest.FileUploadInfo> list = new ArrayList<>();
//            list.add(new FileUploadRequest.FileUploadInfo("uploadTest.jpg",503642L));
//            FileUploadRequest requests = new FileUploadRequest("zt001",list);
//            fileService.uploadFile(this, requests);
//        });
//
//        Button button1 = findViewById(R.id.button1);
//        button1.setOnClickListener(v -> {
//            fileService.downloadFile(this,"zt002","IMAGE/2022/04/17/1de9a246-2a3e-4887-b2f7-27d2beb3d234.jpg");
//        });
//    }


}