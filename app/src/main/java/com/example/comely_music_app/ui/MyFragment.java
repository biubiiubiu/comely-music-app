package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.response.UserInfo;
import com.example.comely_music_app.config.ShpConfig;
import com.example.comely_music_app.ui.adapter.PlaylistViewListAdapter;
import com.example.comely_music_app.ui.viewmodels.UserInfoViewModel;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.Objects;

public class MyFragment extends Fragment implements View.OnClickListener {
    private ImageView avatarImg;
    private TextView nicknameTxt;
    private View settingFrameBlank;

    private final FragmentManager manager;

    private UserInfoViewModel userInfoViewModel;

    private RecyclerView playlistRecycleView;

    public MyFragment(FragmentManager fm) {
        manager = fm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        SavedStateViewModelFactory savedState = new SavedStateViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), getActivity());
        userInfoViewModel = ViewModelProviders.of(getActivity(), savedState).get(UserInfoViewModel.class);

        initIcons(view);
        avatarImg.setOnClickListener(v -> {
            if (manager != null) {
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frame_blank_for_setting, new SettingFragment());
                ft.commit();
                settingFrameBlank.setVisibility(View.VISIBLE);
            }
        });
        setObserveOnUserViewModel();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        playlistRecycleView.setLayoutManager(manager);
        PlaylistViewListAdapter adapter = new PlaylistViewListAdapter(null);
        playlistRecycleView.setAdapter(adapter);
        return view;
    }

    private void initIcons(View view) {
        avatarImg = view.findViewById(R.id.avatar_image);
        nicknameTxt = view.findViewById(R.id.nickname);
        settingFrameBlank = view.findViewById(R.id.frame_blank_for_setting);
        playlistRecycleView = view.findViewById(R.id.playlist_list);

        UserInfo info = ShpUtils.getUserInfoFromShp(getActivity());
        if (info != null) {
            String nickname = info.getNickname();
            if (nickname != null && nickname.length() > 0) {
                nicknameTxt.setText(nickname);
            }
        }
    }

    private void setObserveOnUserViewModel() {
        if (userInfoViewModel != null) {
            userInfoViewModel.getUserInfo().observe(Objects.requireNonNull(getActivity()), new Observer<UserInfo>() {
                @Override
                public void onChanged(UserInfo userInfo) {
                    if (userInfo != null && userInfo.getNickname() != null && userInfo.getNickname().length() > 0) {
                        // 刷界面
                        nicknameTxt.setText(userInfo.getNickname());
                        // 写入shp
                        SharedPreferences shp = Objects.requireNonNull(getActivity()).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
                        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
                        if (!userInfoStr.equals("")) {
                            Gson gson = new Gson();
                            UserInfo info = gson.fromJson(userInfoStr, UserInfo.class);
                            if (!info.equals(userInfo)) {
                                SharedPreferences.Editor editor = shp.edit();
                                String newInfoStr = gson.toJson(userInfo);
                                editor.putString(ShpConfig.CURRENT_USER, newInfoStr);
                                editor.apply();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
    }
}