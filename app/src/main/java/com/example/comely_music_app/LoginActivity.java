package com.example.comely_music_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.network.request.LoginRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView usernameText, passwordText, forget_password;
    private Button loginBtn;
    private String username, password;

    private UserService userService;
    private PlaylistService playlistService;
    private UserInfoViewModel userInfoViewModel;
    private PlaylistViewModel playlistViewModel;

    private final MutableLiveData<LoadDataSuccessFlag> flagMutableLiveData = new MutableLiveData<>(new LoadDataSuccessFlag());

    @Data
    static
    class LoadDataSuccessFlag {
        boolean loadMyCreatedSuccess;
        boolean loadMyLikeSuccess;
        boolean loadCollectSuccess;

        LoadDataSuccessFlag() {
            loadMyCreatedSuccess = false;
            loadMyLikeSuccess = false;
            loadCollectSuccess = false;
        }

        boolean isReady() {
            return loadMyCreatedSuccess && loadMyLikeSuccess && loadCollectSuccess;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        playlistViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);
        userService = new UserServiceImpl(userInfoViewModel);
        playlistService = new PlaylistServiceImpl(playlistViewModel);
        initIcons();
        setObserveOnUserInfoLivedata();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn) {
            if (checkInputNotNull()) {
                checkUsernameAndPassword();
            }
        } else if (v.getId() == R.id.forget_password) {
            Toast.makeText(getApplicationContext(), "您再想想呢", Toast.LENGTH_SHORT).show();
        }
    }

    public void setObserveOnUserInfoLivedata() {
        userInfoViewModel.getIsNewUser().observe(this, isNewUser -> {
            if (isNewUser != null) {
                if (isNewUser) {
                    EditText editText = new EditText(LoginActivity.this);
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("检测当前账号为新用户，是否使用当前新账号注册并登录？（再次输入密码以确认）")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("确定", (dialog1, which) -> {
                                // 注册并登录
                                String pwd2 = editText.getText().toString();
                                if (TextUtils.isEmpty(pwd2)) {
                                    Toast.makeText(getApplicationContext(), "请再次输入密码以确认！", Toast.LENGTH_SHORT).show();
                                } else if (!password.equals(pwd2)) {
                                    Toast.makeText(getApplicationContext(), "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
                                }
                                if (password.equals(pwd2)) {
                                    userService.loginOrRegister(new LoginRequest(username, password));
                                }
                            })
                            .setNegativeButton("我再想想", null)
                            .create().show();
                } else {
                    userService.loginOrRegister(new LoginRequest(username, password));
                }
            }
        });

        userInfoViewModel.getUserInfo().observe(this, userInfo -> {
            if (userInfo != null) {
                if (userInfo.getUsername() == null) {
                    Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "正在登录...", Toast.LENGTH_SHORT).show();

                    ShpUtils.writeCurrentUserinfoToShp(this, userInfo);
                    // 获取用户创建歌单
                    playlistService.selectAllCreatedPlaylistByUsername(userInfo.getUsername());
                    // 获取我喜欢歌单
                    PlaylistSelectRequest request = new PlaylistSelectRequest();
                    request.setUsername(userInfo.getUsername()).setPlaylistName(userInfo.getUsername() + "的喜欢歌单");
                    playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_LIKE);
                }
            }
        });

        // 控制所有数据都加载完成才能进入主界面
        flagMutableLiveData.observe(this, loadDataSuccessFlag -> {
            userInfoViewModel.setIsLogin(Objects.requireNonNull(flagMutableLiveData.getValue()).isReady());
        });

        if (playlistViewModel != null) {
            playlistViewModel.getMyCreatedPlaylists().observe(this, playlistModels -> {
                // 写入shp，下次直接打开应用不需要联网就可以加载
                ShpUtils.writeMyCreatePlaylistToShp(this, playlistModels);
                LoadDataSuccessFlag value = flagMutableLiveData.getValue();
                if (value == null) {
                    value = new LoadDataSuccessFlag();
                }
                value.setLoadMyCreatedSuccess(true);
                flagMutableLiveData.setValue(value);
            });

            playlistViewModel.getMyLikePlaylistDetails().observe(this, mylikePlaylistDetails -> {
                if (mylikePlaylistDetails == null || mylikePlaylistDetails.getMusicModelList() == null) {
                    return;
                }
                UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
                String username = userInfo.getUsername();
                String nickname = userInfo.getNickname();
                PlaylistModel info = new PlaylistModel();
                info.setName(username + "的喜欢歌单");
                info.setCreatedUserNickname(nickname);
                info.setVisibility(0);
                info.setMusicNum(mylikePlaylistDetails.getMusicModelList().size());
                mylikePlaylistDetails.setPlaylistInfo(info);

                ShpUtils.writePlaylistDetailsIntoShp(this, mylikePlaylistDetails);LoadDataSuccessFlag value = flagMutableLiveData.getValue();
                if (value == null) {
                    value = new LoadDataSuccessFlag();
                }
                value.setLoadMyLikeSuccess(true);
                flagMutableLiveData.setValue(value);
                value.setLoadCollectSuccess(true);
                flagMutableLiveData.setValue(value);
            });
        }

        if (userInfoViewModel != null) {
            userInfoViewModel.getIsLogin().observe(this, isLogin -> {
                if (isLogin != null && isLogin) {
                    // 登录成功 (注：退出登录动作在settingFragment里触发，loginActivity里面只有登录成功)
                    // 跳转到main
                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void checkUsernameAndPassword() {
        LoginRequest request = new LoginRequest(username, password);
        userService.judgeNewUser(request);
    }

    private boolean checkInputNotNull() {
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initIcons() {
        usernameText = findViewById(R.id.username_text);
        passwordText = findViewById(R.id.password_text);
        loginBtn = findViewById(R.id.login_btn);
        forget_password = findViewById(R.id.forget_password);

        loginBtn.setOnClickListener(this);
        forget_password.setOnClickListener(this);
    }
}