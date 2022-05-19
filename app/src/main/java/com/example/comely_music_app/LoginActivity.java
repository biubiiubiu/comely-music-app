package com.example.comely_music_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.network.request.LoginRequest;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.ui.viewmodels.PlaylistViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView usernameText, passwordText;
    private String username, password;

    private UserService userService;
    private PlaylistService playlistService;
    private UserInfoViewModel userInfoViewModel;
    private PlaylistViewModel playlistViewModel;
    private ShpUtils shpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shpUtils = new ShpUtils(this);

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
                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();

                    shpUtils.writeCurrentUserinfoToShp(userInfo);
                    playlistService.selectAllCreatedPlaylistByUsername(userInfo.getUsername());
                }
            }
        });

        if (playlistViewModel != null) {
            playlistViewModel.getMyCreatedPlaylists().observe(this, playlistModels -> {
                // 写入shp，下次直接打开应用不需要联网就可以加载
                shpUtils.writeMyCreatePlaylistToShp(playlistModels);
                userInfoViewModel.setIsLogin(true);
            });
        }

        if (userInfoViewModel != null) {
            userInfoViewModel.getIsLogin().observe(this, isLogin -> {
                if (isLogin != null && isLogin) {
                    // 登录成功 (注：退出登录动作在settingFragment里触发，loginActivity里面只有登录成功)
                    // 跳转到main
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
        Button loginBtn = findViewById(R.id.login_btn);
        TextView forget_password = findViewById(R.id.forget_password);

        loginBtn.setOnClickListener(this);
        forget_password.setOnClickListener(this);
    }
}