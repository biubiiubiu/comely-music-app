package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.comely_music_app.LoginActivity;
import com.example.comely_music_app.R;
import com.example.comely_music_app.api.request.user.UserUpdateRequest;
import com.example.comely_music_app.api.response.user.UserInfo;
import com.example.comely_music_app.api.service.UserService;
import com.example.comely_music_app.api.service.impl.UserServiceImpl;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.Objects;

public class SettingFragment extends Fragment implements View.OnClickListener {
    View inflateView;
    private ImageButton back;
    private ImageView avatar;
    private EditText nicknameEdit;
    private Button confirmBtn, logoutBtn;

    private UserService userService;

    private UserInfoViewModel userInfoViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initIcons(View view) {
        back = view.findViewById(R.id.settings_back);
        avatar = view.findViewById(R.id.settings_avatar);
        confirmBtn = view.findViewById(R.id.settings_confirm_update);
        logoutBtn = view.findViewById(R.id.settings_logout);
        nicknameEdit = view.findViewById(R.id.settings_nickname);
        back.setOnClickListener(this);
        avatar.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflateView = inflater.inflate(R.layout.fragment_setting, container, false);

        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), getActivity());
        userInfoViewModel = ViewModelProviders.of(getActivity(), savedState).get(UserInfoViewModel.class);

        userService = new UserServiceImpl(userInfoViewModel);

        initIcons(inflateView);
        initNickname();
        return inflateView;
    }

    private void initNickname() {
        UserInfo info = ShpUtils.getUserInfoFromShp(getActivity());
        if (info != null) {
            // 加载用户名
            String nickname = info.getNickname();
            if (nickname != null && nickname.length() > 0) {
                nicknameEdit.setText(nickname);
            }
            // 加载头像
            String avatarId = info.getAvatarId();
            if (avatarId != null && avatarId.length() > 0) {
                // todo 根据avatarId获取fileStorage，这里后端写一下
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings_back) {
            inflateView.setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.settings_avatar) {
            Log.d("TAG", "onClick: 头像");
        } else if (v.getId() == R.id.settings_logout) {
            // 退出登录
            logout();
        } else if (v.getId() == R.id.settings_confirm_update) {
            // 提交用户信息修改
            updateUserInfo();
        }
    }

    private void logout() {
        // 清除当前登录用户的username
        FragmentActivity activity = getActivity();
        if (activity != null) {
            SharedPreferences shp = activity.getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);

            String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
            SharedPreferences.Editor editor = shp.edit();
            editor.putString(ShpConfig.CURRENT_USER, "");
            editor.apply();

            if (!userInfoStr.equals("")) {
                Gson gson = new Gson();
                UserInfo info = gson.fromJson(userInfoStr, UserInfo.class);
                String username = info.getUsername();
                if (username != null && username.length() > 0) {
                    userService.logout(username);
                }
            } else {
                Log.d("SettingsFragment", "logout: 用户状态异常");
            }

            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateUserInfo() {
        String nickname = nicknameEdit.getText().toString();
        UserInfo oldInfo = ShpUtils.getUserInfoFromShp(getActivity());
        if (oldInfo != null) {
            // todo 这里可以修改其它内容
            if (!nickname.equals(oldInfo.getNickname())) {
                UserUpdateRequest request = new UserUpdateRequest();
                request.setUsername(oldInfo.getUsername());
                request.setNickname(nickname);
                userService.update(request);
                Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_SHORT).show();
                inflateView.setVisibility(View.INVISIBLE);
            }
        }
    }

//    private UserInfo getUserInfoFromShp() {
//        SharedPreferences shp = Objects.requireNonNull(getActivity()).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
//        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
//        if (!userInfoStr.equals("")) {
//            Gson gson = new Gson();
//            return gson.fromJson(userInfoStr, UserInfo.class);
//        }
//        return null;
//    }
}