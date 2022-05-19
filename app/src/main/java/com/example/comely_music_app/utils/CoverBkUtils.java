package com.example.comely_music_app.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.view.View;

import com.example.comely_music_app.R;
import com.example.comely_music_app.ui.models.MusicModel;

import java.io.File;
import java.io.IOException;

public class CoverBkUtils {
    /**
     * 获取cover和background的图片drawable，如果music资源中没有就返回默认的资源
     *
     * @param musicModel 当前itemview的数据，即music资源
     * @param itemView   当前itemview
     * @return 图片资源drawable
     * @throws IOException 文件异常
     */
    public Drawable getImageSourceFromMusicModel(MusicModel musicModel, View itemView, boolean iscover) throws IOException {
        if (musicModel == null) {
            return null;
        }
        String coverPath = musicModel.getCoverLocalPath();
        String audioPath = musicModel.getAudioLocalPath();
        if (coverPath == null) {
            coverPath = audioPath.substring(0, audioPath.lastIndexOf(".")) + ".jpg";
        }
        File file = new File(coverPath);
        if (!file.exists()) {
            // 如果没有指定封面，就从mp3内嵌图片中获取图片
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(audioPath);
            byte[] embedCover = mmr.getEmbeddedPicture();
            if (embedCover != null && embedCover.length > 0) {
                FileOperationUtils fileOperationUtils = new FileOperationUtils();
                fileOperationUtils.writeBytesToFile(embedCover, coverPath);
            } else {
                // 使用默认图片作为封面
                return getDefaultCoverImage(itemView, iscover);
            }
        }
        return Drawable.createFromPath(coverPath);
    }

    /**
     * 获取默认图片
     *
     * @param itemView 能够获取到resource的view
     * @param isCover
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getDefaultCoverImage(View itemView, boolean isCover) {
        int index = (int) (Math.random() * 7) + 1;
        int drawableId = isCover ? R.drawable.bk_01 : R.drawable.player_bk_01;
        switch (index) {
            case 1:
                drawableId = isCover ? R.drawable.bk_01 : R.drawable.player_bk_01;
                break;
            case 2:
                drawableId = isCover ? R.drawable.bk_02 : R.drawable.player_bk_01;
                break;
            case 3:
                drawableId = isCover ? R.drawable.bk_03 : R.drawable.player_bk_02;
                break;
            case 4:
                drawableId = isCover ? R.drawable.bk_06 : R.drawable.player_bk_02;
                break;
            case 5:
                drawableId = isCover ? R.drawable.bk_05 : R.drawable.player_bk_02;
                break;
            case 6:
                drawableId = isCover ? R.drawable.bk_06 : R.drawable.player_bk_03;
                break;
            case 7:
                drawableId = isCover ? R.drawable.bk_07 : R.drawable.player_bk_03;
                break;
        }
        return itemView.getResources().getDrawable(drawableId);
    }
}
