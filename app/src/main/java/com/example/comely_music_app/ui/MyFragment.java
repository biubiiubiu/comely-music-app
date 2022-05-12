package com.example.comely_music_app.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.example.comely_music_app.utils.ScreenUtils;
import com.example.comely_music_app.utils.ShpUtils;
import com.google.gson.Gson;

import java.util.Objects;

public class MyFragment extends Fragment implements View.OnClickListener {
    private ImageView avatarImg;
    private TextView nicknameTxt;
    private View settingFrameBlank;
    private ImageButton addPlaylist;

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
        addPlaylist = view.findViewById(R.id.add_playlist);
        playlistRecycleView = view.findViewById(R.id.playlist_list);

        addPlaylist.setOnClickListener(this);

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
                        UserInfo currentUserinfoFromShp = getCurrentUserinfoFromShp();
                        if (!userInfo.equals(currentUserinfoFromShp)) {
                            writeCurrentUserinfoToShp(userInfo);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_playlist) {
            showCreateDialog();
        }
    }

    private UserInfo getCurrentUserinfoFromShp() {
        SharedPreferences shp = Objects.requireNonNull(getActivity()).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        String userInfoStr = shp.getString(ShpConfig.CURRENT_USER, "");
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }

    private void writeCurrentUserinfoToShp(UserInfo userInfo) {
        SharedPreferences shp = Objects.requireNonNull(getActivity()).getSharedPreferences(ShpConfig.SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        Gson gson = new Gson();
        String newInfoStr = gson.toJson(userInfo);
        editor.putString(ShpConfig.CURRENT_USER, newInfoStr);
        editor.apply();
    }

    private void showCreateDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_playlist, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).setView(view).create();

        TextView cancel = view.findViewById(R.id.dialog_create_cancel);
        TextView complete = view.findViewById(R.id.dialog_create_complete);
        EditText editText = view.findViewById(R.id.dialog_create_playlist_name);
        RadioButton rb = view.findViewById(R.id.dialog_playlist_visibility_rb);

        cancel.setOnClickListener(v -> {
            Log.d("创建歌单", "onClick: 取消");
            //... To-do
            dialog.dismiss();
        });

        complete.setOnClickListener(v -> {
            String playlistName = editText.getText().toString();
            if (playlistName.length() == 0) {
                Toast.makeText(getActivity(), "歌单名称不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                int visibility = 1;
                if (rb.isChecked()) {
                    visibility = 0;
                }
                Log.d("创建歌单", "onClick: 完成" + playlistName + " 可见性" + visibility);
                //... To-do
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的4/5  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(Objects.requireNonNull(getActivity())) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

}