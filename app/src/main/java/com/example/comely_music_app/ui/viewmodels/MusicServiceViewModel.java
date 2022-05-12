package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comely_music_app.network.response.MusicBatchCreateResponse;

/**
 * 音乐增删改查的view model
 */
public class MusicServiceViewModel extends ViewModel {
    private MutableLiveData<MusicBatchCreateResponse> batchCreateLiveData;

    public MutableLiveData<MusicBatchCreateResponse> getBatchCreateLiveData() {
        if (batchCreateLiveData == null) {
            batchCreateLiveData = new MutableLiveData<>();
        }
        return batchCreateLiveData;
    }

    public void setBatchCreateLiveData(MusicBatchCreateResponse response) {
        if (batchCreateLiveData == null) {
            batchCreateLiveData = getBatchCreateLiveData();
        }
        batchCreateLiveData.setValue(response);
    }
}
