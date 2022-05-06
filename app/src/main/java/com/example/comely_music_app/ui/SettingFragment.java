package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.comely_music_app.LoginActivity;
import com.example.comely_music_app.R;
import com.example.comely_music_app.api.service.UserService;
import com.example.comely_music_app.api.service.impl.UserServiceImpl;
import com.example.comely_music_app.config.ShpConfig;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private ImageButton back;
    private ImageView avatar;
    private Button logoutBtn;

    private UserService userService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = new UserServiceImpl();
    }

    private void initIcons(View view) {
        back = view.findViewById(R.id.settings_back);
        avatar = view.findViewById(R.id.settings_avatar);
        logoutBtn = view.findViewById(R.id.settings_logout);
        back.setOnClickListener(this);
        avatar.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initIcons(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings_back) {
            Log.d("TAG", "onClick: 返回");
        } else if (v.getId() == R.id.settings_avatar) {
            Log.d("TAG", "onClick: 头像");
        } else if (v.getId() == R.id.settings_logout) {
            // 退出登录
            logout();
        }
    }

    private void logout() {
        // 清除当前登录用户的username
        FragmentActivity activity = getActivity();
        if (activity != null) {
            SharedPreferences shp = activity.getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
            String curUsername = shp.getString(ShpConfig.CURRENT_USERNAME, "");
            SharedPreferences.Editor editor = shp.edit();
            editor.putString(ShpConfig.CURRENT_USERNAME, "");
            editor.apply();

            if (!curUsername.equals("")) {
                userService.logout(curUsername);
            } else {
                Log.d("SettingsFragment", "logout: 用户状态异常");
            }

            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
        }
    }
}