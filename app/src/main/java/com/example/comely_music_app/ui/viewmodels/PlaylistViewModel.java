package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comely_music_app.ui.models.PlaylistModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistViewModel extends ViewModel {
    private MutableLiveData<List<PlaylistModel>> myCreatedPlaylists;
    /**
     * 创建/删除歌单 成功/失败时设置+1，触发回调
     */
    private MutableLiveData<Integer> createSuccessFlag, deleteSuccessFlag, createFailedFlag, deleteFailedFlag;

    public MutableLiveData<List<PlaylistModel>> getMyCreatedPlaylists() {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = new MutableLiveData<>();
        }
        return myCreatedPlaylists;
    }

    public void setMyCreatedPlaylists(List<PlaylistModel> playlistModels) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        myCreatedPlaylists.setValue(playlistModels);
    }

    public void addPlaylist2MyCreatedPlaylists(PlaylistModel playlistModel) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(playlistModel);
        myCreatedPlaylists.setValue(values);
    }

    public MutableLiveData<Integer> getCreateSuccessFlag() {
        if (createSuccessFlag == null) {
            createSuccessFlag = new MutableLiveData<>();
        }
        return createSuccessFlag;
    }

    public MutableLiveData<Integer> getDeleteSuccessFlag() {
        if (deleteSuccessFlag == null) {
            deleteSuccessFlag = new MutableLiveData<>();
        }
        return deleteSuccessFlag;
    }

    public MutableLiveData<Integer> getCreateFailedFlag() {
        if (createFailedFlag == null) {
            createFailedFlag = new MutableLiveData<>();
        }
        return createFailedFlag;
    }

    public MutableLiveData<Integer> getDeleteFailedFlag() {
        if (deleteFailedFlag == null) {
            deleteFailedFlag = new MutableLiveData<>();
        }
        return deleteFailedFlag;
    }

    public void setCreateSuccessFlag() {
        if (createSuccessFlag == null) {
            createSuccessFlag = getCreateSuccessFlag();
        }
        if (createSuccessFlag.getValue() == null) {
            createSuccessFlag.setValue(1);
        } else {
            createSuccessFlag.setValue(createSuccessFlag.getValue() + 1);
        }
    }

    public void setDeleteSuccessFlag() {
        if (deleteSuccessFlag == null) {
            deleteSuccessFlag = getCreateSuccessFlag();
        }
        if (deleteSuccessFlag.getValue() == null) {
            deleteSuccessFlag.setValue(1);
        } else {
            deleteSuccessFlag.setValue(deleteSuccessFlag.getValue() + 1);
        }
    }

    public void setCreateFailedFlag() {
        if (createFailedFlag == null) {
            createFailedFlag = getCreateSuccessFlag();
        }
        if (createFailedFlag.getValue() == null) {
            createFailedFlag.setValue(1);
        } else {
            createFailedFlag.setValue(createFailedFlag.getValue() + 1);
        }
    }

    public void setDeleteFailedFlag() {
        if (deleteFailedFlag == null) {
            deleteFailedFlag = getCreateSuccessFlag();
        }
        if (deleteFailedFlag.getValue() == null) {
            deleteFailedFlag.setValue(1);
        } else {
            deleteFailedFlag.setValue(deleteFailedFlag.getValue() + 1);
        }
    }
}
