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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.network.request.LoginRequest;
import com.example.comely_music_app.network.request.PlaylistSelectRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.PlaylistService;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.PlaylistServiceImpl;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.ui.enums.PlaylistSelectScene;
import com.example.comely_music_app.ui.models.PlaylistModel;
import com.example.comely_music_app.ui.viewmodels.PlayingViewModel;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;

import java.util.Objects;

import lombok.Data;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView usernameText, passwordText;
    private String username, password;

    private UserService userService;
    private PlaylistService playlistService;
    private UserInfoViewModel userInfoViewModel;
    private PlayingViewModel playingViewModel;

    private final MutableLiveData<LoadDataSuccessFlag> flagMutableLiveData = new MutableLiveData<>(new LoadDataSuccessFlag());

    @Data
    static
    class LoadDataSuccessFlag {
        boolean loadMyCreatedSuccess;
        boolean loadMyLikeSuccess;
        boolean loadCollectSuccess;
        boolean loadRecentlySuccess;

        LoadDataSuccessFlag() {
            loadMyCreatedSuccess = false;
            loadMyLikeSuccess = false;
            loadCollectSuccess = true;
            loadRecentlySuccess = false;
        }

        boolean isReady() {
            return loadMyCreatedSuccess && loadMyLikeSuccess && loadCollectSuccess && loadRecentlySuccess;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        playingViewModel = ViewModelProviders.of(this).get(PlayingViewModel.class);
        userService = new UserServiceImpl(userInfoViewModel);
        playlistService = new PlaylistServiceImpl(playingViewModel);
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
            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
        }
    }

    public void setObserveOnUserInfoLivedata() {
        userInfoViewModel.getIsNewUser().observe(this, isNewUser -> {
            if (isNewUser != null) {
                if (isNewUser) {
                    EditText editText = new EditText(LoginActivity.this);
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("???????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("??????", (dialog1, which) -> {
                                // ???????????????
                                String pwd2 = editText.getText().toString();
                                if (TextUtils.isEmpty(pwd2)) {
                                    Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                                } else if (!password.equals(pwd2)) {
                                    Toast.makeText(getApplicationContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
                                }
                                if (password.equals(pwd2)) {
                                    userService.loginOrRegister(new LoginRequest(username, password));
                                }
                            })
                            .setNegativeButton("????????????", null)
                            .create().show();
                } else {
                    userService.loginOrRegister(new LoginRequest(username, password));
                }
            }
        });

        userInfoViewModel.getUserInfo().observe(this, userInfo -> {
            if (userInfo != null) {
                if (userInfo.getUsername() == null) {
                    Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "????????????...", Toast.LENGTH_SHORT).show();

                    ShpUtils.writeCurrentUserinfoToShp(this, userInfo);
                    // ????????????????????????
                    playlistService.selectAllCreatedPlaylistByUsername(userInfo.getUsername());
                    // ?????????????????????
                    PlaylistSelectRequest request = new PlaylistSelectRequest();
                    request.setUsername(userInfo.getUsername()).setPlaylistName(userInfo.getUsername() + "???????????????");
                    playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.MY_LIKE);
                    // ????????????????????????
                    PlaylistSelectRequest request1 = new PlaylistSelectRequest();
                    request1.setUsername(userInfo.getUsername()).setPlaylistName(userInfo.getUsername() + "???????????????");
                    playlistService.selectPlaylistDetailsByScene(request, PlaylistSelectScene.RECENTLY_PLAY);
                }
            }
        });

        // ??????????????????????????????????????????????????????
        flagMutableLiveData.observe(this, loadDataSuccessFlag -> {
            userInfoViewModel.setIsLogin(Objects.requireNonNull(flagMutableLiveData.getValue()).isReady());
        });

        if (playingViewModel != null) {
            playingViewModel.getMyCreatedPlaylists().observe(this, playlistModels -> {
                // ??????shp?????????????????????????????????????????????????????????
                ShpUtils.writeMyCreatePlaylistToShp(this, playlistModels);
                LoadDataSuccessFlag value = flagMutableLiveData.getValue();
                if (value == null) {
                    value = new LoadDataSuccessFlag();
                }
                value.setLoadMyCreatedSuccess(true);
                flagMutableLiveData.setValue(value);
            });

            playingViewModel.getMyLikePlaylistDetails().observe(this, mylikePlaylistDetails -> {
                if (mylikePlaylistDetails == null || mylikePlaylistDetails.getMusicModelList() == null) {
                    return;
                }
                UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
                String username = userInfo.getUsername();
                String nickname = userInfo.getNickname();
                PlaylistModel info = new PlaylistModel();
                info.setName(username + "???????????????");
                info.setCreatedUserNickname(nickname);
                info.setVisibility(0);
                info.setMusicNum(mylikePlaylistDetails.getMusicModelList().size());
                mylikePlaylistDetails.setPlaylistInfo(info);

                ShpUtils.writePlaylistDetailsIntoShp(this, mylikePlaylistDetails);
                LoadDataSuccessFlag value = flagMutableLiveData.getValue();
                if (value == null) {
                    value = new LoadDataSuccessFlag();
                }
                value.setLoadMyLikeSuccess(true);
                flagMutableLiveData.setValue(value);
            });

            playingViewModel.getRecentlyPlaylistDetails().observe(this, recentlyPlaylistDetails -> {
                if (recentlyPlaylistDetails == null || recentlyPlaylistDetails.getMusicModelList() == null) {
                    return;
                }
                UserInfo userInfo = Objects.requireNonNull(ShpUtils.getCurrentUserinfoFromShp(this));
                String username = userInfo.getUsername();
                String nickname = userInfo.getNickname();
                PlaylistModel info = new PlaylistModel();
                info.setName(username + "???????????????");
                info.setCreatedUserNickname(nickname);
                info.setVisibility(0);
                info.setMusicNum(recentlyPlaylistDetails.getMusicModelList().size());
                recentlyPlaylistDetails.setPlaylistInfo(info);

                ShpUtils.writePlaylistDetailsIntoShp(this, recentlyPlaylistDetails);
                LoadDataSuccessFlag value = flagMutableLiveData.getValue();
                if (value == null) {
                    value = new LoadDataSuccessFlag();
                }
                value.setLoadRecentlySuccess(true);
                flagMutableLiveData.setValue(value);
            });
        }

        if (userInfoViewModel != null) {
            userInfoViewModel.getIsLogin().observe(this, isLogin -> {
                if (isLogin != null && isLogin) {
                    // ???????????? (???????????????????????????settingFragment????????????loginActivity????????????????????????)
                    // ?????????main
                    Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
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