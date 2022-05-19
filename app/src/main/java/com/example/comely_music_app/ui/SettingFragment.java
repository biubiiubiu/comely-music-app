package com.example.comely_music_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import com.example.comely_music_app.LoginActivity;
import com.example.comely_music_app.R;
import com.example.comely_music_app.network.request.UserUpdateRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;

import java.util.Objects;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private EditText nicknameEdit;
    private UserService userService;
    private MutableLiveData<Integer> myFragmentViewsCtrlLiveData;

    private ShpUtils shpUtils;

    public SettingFragment(MutableLiveData<Integer> liveData) {
        myFragmentViewsCtrlLiveData = liveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initIcons(View view) {
        ImageButton back = view.findViewById(R.id.settings_back);
        ImageView avatar = view.findViewById(R.id.settings_avatar);
        Button confirmBtn = view.findViewById(R.id.settings_confirm_update);
        Button logoutBtn = view.findViewById(R.id.settings_logout);
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
        View inflateView = inflater.inflate(R.layout.fragment_setting, container, false);
        shpUtils = new ShpUtils(getActivity());

        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), getActivity());
        UserInfoViewModel userInfoViewModel = ViewModelProviders.of(getActivity(), savedState).get(UserInfoViewModel.class);
        userService = new UserServiceImpl(userInfoViewModel);

        initIcons(inflateView);
        initNickname();
        return inflateView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myFragmentViewsCtrlLiveData = null;
    }

    private void initNickname() {
        UserInfo info = shpUtils.getCurrentUserinfoFromShp();
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
            myFragmentViewsCtrlLiveData.setValue(0);
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
        UserInfo userInfo = shpUtils.getCurrentUserinfoFromShp();
        if (userInfo != null) {
            String username = userInfo.getUsername();
            if (username != null && username.length() > 0) {
                userService.logout(username);
            }
        } else {
            Log.d("SettingsFragment", "logout: 用户状态异常");
        }

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

    }

    private void updateUserInfo() {
        String nickname = nicknameEdit.getText().toString();
        UserInfo oldInfo = shpUtils.getCurrentUserinfoFromShp();
        if (oldInfo != null) {
            // todo 这里可以修改其它内容
            if (!nickname.equals(oldInfo.getNickname())) {
                UserUpdateRequest request = new UserUpdateRequest();
                request.setUsername(oldInfo.getUsername());
                request.setNickname(nickname);
                userService.update(request);
                Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_SHORT).show();
                myFragmentViewsCtrlLiveData.setValue(0);
            }
        }
    }

}