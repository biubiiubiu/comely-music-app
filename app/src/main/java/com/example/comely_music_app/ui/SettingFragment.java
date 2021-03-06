package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
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
import com.example.comely_music_app.network.request.UserUpdateRequest;
import com.example.comely_music_app.network.response.UserInfo;
import com.example.comely_music_app.network.service.UserService;
import com.example.comely_music_app.network.service.impl.UserServiceImpl;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.Objects;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private EditText nicknameEdit;
    private UserService userService;
    private MutableLiveData<Integer> myFragmentViewsCtrlLiveData;

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
        UserInfo info = ShpUtils.getCurrentUserinfoFromShp(getActivity());
        if (info != null) {
            // ???????????????
            String nickname = info.getNickname();
            if (nickname != null && nickname.length() > 0) {
                nicknameEdit.setText(nickname);
            }
            // ????????????
            String avatarId = info.getAvatarId();
            if (avatarId != null && avatarId.length() > 0) {
                // todo ??????avatarId??????fileStorage????????????????????????
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings_back) {
            myFragmentViewsCtrlLiveData.setValue(0);
        } else if (v.getId() == R.id.settings_avatar) {
            Log.d("TAG", "onClick: ??????");
        } else if (v.getId() == R.id.settings_logout) {
            // ????????????
            logout();
        } else if (v.getId() == R.id.settings_confirm_update) {
            // ????????????????????????
            updateUserInfo();
        }
    }

    private void logout() {
        // ???????????????????????????username
        FragmentActivity activity = getActivity();
        if (activity != null) {
            UserInfo userInfo = ShpUtils.getCurrentUserinfoFromShp(activity);
            if(userInfo!=null){
                String username = userInfo.getUsername();
                if (username != null && username.length() > 0) {
                    userService.logout(username);
                }
            } else {
                Log.d("SettingsFragment", "logout: ??????????????????");
            }

            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateUserInfo() {
        String nickname = nicknameEdit.getText().toString();
        UserInfo oldInfo = ShpUtils.getCurrentUserinfoFromShp(getActivity());
        if (oldInfo != null) {
            // todo ??????????????????????????????
            if (!nickname.equals(oldInfo.getNickname())) {
                UserUpdateRequest request = new UserUpdateRequest();
                request.setUsername(oldInfo.getUsername());
                request.setNickname(nickname);
                userService.update(request);
                Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                myFragmentViewsCtrlLiveData.setValue(0);
            }
        }
    }

}