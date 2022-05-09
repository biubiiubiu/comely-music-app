package com.example.comely_music_app.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FileServiceViewModel extends ViewModel {
    /**
     * 开始上传置为true，当前文件上传完成之后，会置为false，使下一个文件开始上传
     */
    private MutableLiveData<Boolean> isUploading;

    /**
     * 当前上传文件的位置，上传完成后，自增1，继续上传后面的文件
     */
    private MutableLiveData<Integer> currentFileIndex;

    public MutableLiveData<Boolean> getIsUploading() {
        if (isUploading == null) {
            isUploading = new MutableLiveData<>(false);
        }
        return isUploading;
    }

    public void setIsUploading(Boolean isUpload) {
        if (isUploading == null) {
            isUploading = getIsUploading();
        }
        isUploading.setValue(isUpload);
    }

    public void changeIsUploading() {
        if (isUploading == null) {
            isUploading = getIsUploading();
        }
        isUploading.setValue(!isUploading.getValue());
    }

    public MutableLiveData<Integer> getCurrentFileIndex() {
        if (currentFileIndex == null) {
            currentFileIndex = new MutableLiveData<>(0);
        }
        return currentFileIndex;
    }

    public void setCurrentFileIndex(Integer index) {
        if (currentFileIndex == null) {
            currentFileIndex = getCurrentFileIndex();
        }
        currentFileIndex.setValue(index);
    }
}
