package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.comely_music_app.ui.enums.PageStatus;

public class MainViewModel extends ViewModel {
    private boolean isPlaying = true;
    private PageStatus status = PageStatus.PLAYING;
    private SavedStateHandle handle;

    public MainViewModel(SavedStateHandle handle) {
        this.handle = handle;
    }

    public void playBtnClick() {
        isPlaying = !isPlaying;
        status = isPlaying ? PageStatus.PLAYING : PageStatus.PAUSE;
        checkout2Playing();
        changeIconByStatus(status);
    }

    private void changeIconByStatus(PageStatus status) {

    }

    private void checkout2Playing() {

    }
}
