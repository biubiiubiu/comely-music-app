package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comely_music_app.network.request.PlaylistMusicAddRequest;
import com.example.comely_music_app.ui.models.MusicModel;
import com.example.comely_music_app.ui.models.PlaylistDetailsModel;
import com.example.comely_music_app.ui.models.PlaylistModel;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlaylistViewModel extends ViewModel {
    private MutableLiveData<List<PlaylistModel>> myCreatedPlaylists;
    private MutableLiveData<PlaylistDetailsModel> currentPlaylistDetails;
    private MutableLiveData<PlaylistDetailsModel> myLikePlaylistDetails;

    // (因为点赞和取消点赞是频繁的，不能每次都请求数据库，所以在关闭activity时刷新到数据库)
    // 于是分别存放需要在数据库“我喜欢”歌单中 添加与删除 的音乐，用于增量刷新数据库中我喜欢歌单
    private List<MusicModel> toAddIntoMyLike = new ArrayList<>();
    private List<MusicModel> toRemoveFromMyLike = new ArrayList<>();

    public List<MusicModel> getToAddIntoMyLike() {
        return toAddIntoMyLike;
    }

    public List<MusicModel> getToRemoveFromMyLike() {
        return toRemoveFromMyLike;
    }

    public void like(List<MusicModel> musicModels) {
        // 添加到toadd
        for (MusicModel next : musicModels) {
            if (!toAddIntoMyLike.contains(next)) {
                toAddIntoMyLike.add(next);
            }
        }
        // 从toremove中删除
        Iterator<MusicModel> iterator = toRemoveFromMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    public void dislike(List<MusicModel> musicModels) {
        // 添加到toremove
        for (MusicModel next : musicModels) {
            if (!toRemoveFromMyLike.contains(next)) {
                toRemoveFromMyLike.add(next);
            }
        }
        // 从toadd中删除
        Iterator<MusicModel> iterator = toAddIntoMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    private void addInMylikeAddList(List<MusicModel> musicModels) {
        for (MusicModel next : musicModels) {
            if (!toAddIntoMyLike.contains(next)) {
                toAddIntoMyLike.add(next);
            }
        }
    }

    private void removeFromMylikeAddList(List<MusicModel> musicModels) {
        Iterator<MusicModel> iterator = toAddIntoMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (musicModels.contains(next)) {
                iterator.remove();
            }
        }
    }

    private void addInMylikeRemoveList(MusicModel musicModel) {
        toRemoveFromMyLike.add(musicModel);
    }

    private void removeFromMylikeRemoveList(MusicModel musicModel) {
        Iterator<MusicModel> iterator = toRemoveFromMyLike.iterator();
        while (iterator.hasNext()) {
            MusicModel next = iterator.next();
            if (next.equals(musicModel)) {
                iterator.remove();
            }
        }
    }


    /**
     * 点击自建歌单/收藏歌单时+1，触发回调，展示歌单详情页
     */
    private MutableLiveData<Integer> showCreated, showCollect, showMylike;

    /**
     * 创建/删除歌单 成功/失败时设置+1，触发回调
     */
    private MutableLiveData<Integer> createSuccessFlag, deleteSuccessFlag, createFailedFlag, deleteFailedFlag,
            updateSuccessFlag, updateFailedFlag;

// ===================================== myFragment界面歌单列表数据控制 ===============================================

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

    public void deletePlaylistInCreatedPlaylists(PlaylistModel playlistModel) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values != null) {
            Iterator<PlaylistModel> iterator = values.iterator();
            while (iterator.hasNext()) {
                PlaylistModel model = iterator.next();
                if (model.getName().equals(playlistModel.getName())) {
                    iterator.remove();
                }
            }
        }
        myCreatedPlaylists.setValue(values);
    }

    public void deleteMusicInCurrentMusic(List<PlaylistMusicAddRequest.MusicAddInfo> musicAddInfoList) {
        List<MusicModel> newMusicModelList = new ArrayList<>();
        PlaylistDetailsModel currentDetails = getCurrentPlaylistDetails().getValue();
        if (currentDetails != null && currentDetails.getMusicModelList() != null) {
            for (MusicModel model : currentDetails.getMusicModelList()) {
                PlaylistMusicAddRequest.MusicAddInfo info = new PlaylistMusicAddRequest.MusicAddInfo(model.getName(),
                        model.getArtistName());
                if (!musicAddInfoList.contains(info)) {
                    newMusicModelList.add(model);
                }
            }
            currentDetails.setMusicModelList(newMusicModelList);
            currentDetails.getPlaylistInfo().setMusicNum(newMusicModelList.size());
            setCurrentPlaylistDetails(currentDetails);
        }
    }

    public void updateCreatedPlaylistByName(String oldName, PlaylistModel model) {
        if (myCreatedPlaylists == null) {
            myCreatedPlaylists = getMyCreatedPlaylists();
        }
        List<PlaylistModel> values = myCreatedPlaylists.getValue();
        if (values != null) {
            for (PlaylistModel oldModel : values) {
                if (oldModel.getName().equals(oldName)) {
                    if (model.getName() != null) {
                        oldModel.setName(model.getName());
                    }
                    if (model.getVisibility() != null) {
                        oldModel.setVisibility(model.getVisibility());
                    }
                    if (model.getDescription() != null) {
                        oldModel.setDescription(model.getDescription());
                    }
                    if (model.getMusicNum() != null) {
                        oldModel.setMusicNum(model.getMusicNum());
                    }
                }
            }
        }
        myCreatedPlaylists.setValue(values);
    }

    // ===================================== 歌单详情界面数据 =============================================
    public MutableLiveData<PlaylistDetailsModel> getCurrentPlaylistDetails() {
        if (currentPlaylistDetails == null) {
            currentPlaylistDetails = new MutableLiveData<>();
        }
        return currentPlaylistDetails;
    }

    public void setCurrentPlaylistDetails(PlaylistDetailsModel detailsModel) {
        if (currentPlaylistDetails == null) {
            currentPlaylistDetails = getCurrentPlaylistDetails();
        }
        currentPlaylistDetails.setValue(detailsModel);
    }

    public MutableLiveData<PlaylistDetailsModel> getMyLikePlaylistDetails() {
        if (myLikePlaylistDetails == null) {
            myLikePlaylistDetails = new MutableLiveData<>(new PlaylistDetailsModel());
        }
        return myLikePlaylistDetails;
    }

    public void setMyLikePlaylistDetails(PlaylistDetailsModel detailsModel) {
        if (myLikePlaylistDetails == null) {
            myLikePlaylistDetails = getCurrentPlaylistDetails();
        }
        myLikePlaylistDetails.setValue(detailsModel);
    }

    public void addIntoMyLikePlaylist(List<MusicModel> toAddList) {
        if (toAddList == null || toAddList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getMyLikePlaylistDetails().getValue();
        if (details != null) {
            if (details.getMusicModelList() == null || details.getMusicModelList().size() == 0) {
                details.setMusicModelList(toAddList);
            } else {
                List<MusicModel> oldList = details.getMusicModelList();
                for (MusicModel model : toAddList) {
                    if (!oldList.contains(model)) {
                        oldList.add(model);
                    }
                }
                details.setMusicModelList(oldList);
            }
            setMyLikePlaylistDetails(details);
        }
    }

    public void removeFromMyLikePlaylist(List<MusicModel> toRemoveList) {
        if (toRemoveList == null || toRemoveList.size() == 0) {
            return;
        }
        PlaylistDetailsModel details = getMyLikePlaylistDetails().getValue();
        if (details != null && details.getMusicModelList() != null) {
            List<MusicModel> newList = new ArrayList<>();
            List<MusicModel> oldList = details.getMusicModelList();
            for (MusicModel model : oldList) {
                if (!toRemoveList.contains(model)) {
                    newList.add(model);
                }
            }
            details.setMusicModelList(newList);
            setMyLikePlaylistDetails(details);
        }
    }


    // ====================================== flag控制 ==================================================

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

    public MutableLiveData<Integer> getUpdateSuccessFlag() {
        if (updateSuccessFlag == null) {
            updateSuccessFlag = new MutableLiveData<>();
        }
        return updateSuccessFlag;
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

    public MutableLiveData<Integer> getUpdateFailedFlag() {
        if (updateFailedFlag == null) {
            updateFailedFlag = new MutableLiveData<>();
        }
        return updateFailedFlag;
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
            deleteSuccessFlag = getDeleteSuccessFlag();
        }
        if (deleteSuccessFlag.getValue() == null) {
            deleteSuccessFlag.setValue(1);
        } else {
            deleteSuccessFlag.setValue(deleteSuccessFlag.getValue() + 1);
        }
    }

    public void setUpdateSuccessFlag() {
        if (updateSuccessFlag == null) {
            updateSuccessFlag = getUpdateSuccessFlag();
        }
        if (updateSuccessFlag.getValue() == null) {
            updateSuccessFlag.setValue(1);
        } else {
            updateSuccessFlag.setValue(updateSuccessFlag.getValue() + 1);
        }
    }

    public void setCreateFailedFlag() {
        if (createFailedFlag == null) {
            createFailedFlag = getCreateFailedFlag();
        }
        if (createFailedFlag.getValue() == null) {
            createFailedFlag.setValue(1);
        } else {
            createFailedFlag.setValue(createFailedFlag.getValue() + 1);
        }
    }

    public void setDeleteFailedFlag() {
        if (deleteFailedFlag == null) {
            deleteFailedFlag = getDeleteFailedFlag();
        }
        if (deleteFailedFlag.getValue() == null) {
            deleteFailedFlag.setValue(1);
        } else {
            deleteFailedFlag.setValue(deleteFailedFlag.getValue() + 1);
        }
    }

    public void setUpdateFailedFlag() {
        if (updateFailedFlag == null) {
            updateFailedFlag = getUpdateFailedFlag();
        }
        if (updateFailedFlag.getValue() == null) {
            updateFailedFlag.setValue(1);
        } else {
            updateFailedFlag.setValue(updateFailedFlag.getValue() + 1);
        }
    }

    public MutableLiveData<Integer> getShowCreated() {
        if (showCreated == null) {
            showCreated = new MutableLiveData<>();
        }
        return showCreated;
    }

    public MutableLiveData<Integer> getShowCollect() {
        if (showCollect == null) {
            showCollect = new MutableLiveData<>();
        }
        return showCollect;
    }

    public MutableLiveData<Integer> getShowMylike() {
        if (showMylike == null) {
            showMylike = new MutableLiveData<>();
        }
        return showMylike;
    }


    public void setShowCreated() {
        if (showCreated == null) {
            showCreated = getShowCreated();
        }
        if (showCreated.getValue() == null) {
            showCreated.setValue(0);
        }
        showCreated.setValue(showCreated.getValue() + 1);
    }

    public void setShowCollect() {
        if (showCollect == null) {
            showCollect = getShowCollect();
        }
        if (showCollect.getValue() == null) {
            showCollect.setValue(0);
        }
        showCollect.setValue(showCollect.getValue() + 1);
    }

    public void setShowMylike() {
        if (showMylike == null) {
            showMylike = getShowMylike();
        }
        if (showMylike.getValue() == null) {
            showMylike.setValue(0);
        }
        showMylike.setValue(showMylike.getValue() + 1);
    }
}
