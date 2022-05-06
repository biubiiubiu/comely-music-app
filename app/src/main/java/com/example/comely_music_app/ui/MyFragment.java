package com.example.comely_music_app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.comely_music_app.R;

public class MyFragment extends Fragment implements View.OnClickListener {
    private ImageView avatarImg;
    private TextView nicknameTxt;
    private View settingFrameBlank;

    private FragmentManager manager;

    public MyFragment(FragmentManager fm) {
        manager = fm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initIcons(view);
        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: 点击了头像");
                if (manager != null) {
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame_blank_for_setting, new SettingFragment());
                    ft.commit();
                    settingFrameBlank.setVisibility(View.VISIBLE);

                }
            }
        });
        return view;
    }

    private void initIcons(View view) {
        avatarImg = view.findViewById(R.id.avatar_image);
        nicknameTxt = view.findViewById(R.id.nickname);
        settingFrameBlank = view.findViewById(R.id.frame_blank_for_setting);
    }

    @Override
    public void onClick(View v) {
    }
}