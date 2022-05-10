package com.example.comely_music_app.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.comely_music_app.api.request.ArtistCreateRequest;
import com.example.comely_music_app.api.request.FileCommonRequest;
import com.example.comely_music_app.api.request.FileUploadRequest;
import com.example.comely_music_app.api.request.MusicCreateRequest;
import com.example.comely_music_app.api.request.TagCreateRequest;
import com.example.comely_music_app.api.service.ArtistService;
import com.example.comely_music_app.api.service.FileService;
import com.example.comely_music_app.api.service.MusicService;
import com.example.comely_music_app.api.service.TagService;
import com.example.comely_music_app.api.service.impl.ArtistServiceImpl;
import com.example.comely_music_app.api.service.impl.FileServiceImpl;
import com.example.comely_music_app.api.service.impl.MusicServiceImpl;
import com.example.comely_music_app.api.service.impl.TagServiceImpl;
import com.example.comely_music_app.enums.TagType;
import com.example.comely_music_app.ui.viewmodels.FileServiceViewModel;
import com.example.comely_music_app.ui.viewmodels.MusicServiceViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 音乐上传脚本，暂时取代后台上传
 */
public class AdminUploadMusicUtils {
    private final LifecycleOwner lifecycleOwner;
    private final Context applicationContext;
    private final MusicService musicService;
    private final FileService fileService;
    private final ArtistService artistService;
    private final TagService tagService;

    private final MusicServiceViewModel musicServiceViewModel;
    private final FileServiceViewModel fileServiceViewModel;

    /**
     * 这里默认是admin用户上传
     */
    private final static String USERNAME = "admin";

    public AdminUploadMusicUtils(LifecycleOwner owner, Context context, MusicServiceViewModel mViewModel,
                                 FileServiceViewModel fViewModel) {
        lifecycleOwner = owner;
        applicationContext = context;
        musicServiceViewModel = mViewModel;
        fileServiceViewModel = fViewModel;
        musicService = new MusicServiceImpl(context);
        fileService = new FileServiceImpl();
        artistService = new ArtistServiceImpl();
        tagService = new TagServiceImpl();
    }

    /**
     * 上传音乐文件，同一批上传的音乐需要在同一个文件夹里
     *
     * @param localBaseDir         以/结尾，例如"/storage/emulated/0/$MuMu共享文件夹/音乐/纯音乐/"
     * @param originalFilenameList 文件名list, 例如："稻香 -周杰伦.mp3" ，包含后缀
     */
    public void uploadFiles(String localBaseDir, String baseStorageUrl, List<String> originalFilenameList) {
        FileCommonRequest request = new FileCommonRequest();
        Map<String, FileCommonRequest.CommonInfo> fileKeyInfoMap = new HashMap<>();
        for (String filename : originalFilenameList) {
            FileCommonRequest.CommonInfo info = new FileCommonRequest.CommonInfo();
            String storageUrl = baseStorageUrl + filename;

            // 这里用filename作为filekey
            File file = new File(localBaseDir + filename);
            if (file.exists()) {
                info.setFilename(filename);
                info.setStorageUrl(storageUrl);
                info.setSize(file.length());
                fileKeyInfoMap.put(filename, info);
            }
        }
        request.setFileKeyInfoMap(fileKeyInfoMap);
        fileService.setUploadSuccessResult(request, null);
    }

//    public void uploadFiles(String localBaseDir, List<String> originalFilenameList) {
//        fileServiceViewModel.getIsUploading().observe(lifecycleOwner, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean isUploading) {
//                Integer index = fileServiceViewModel.getCurrentFileIndex().getValue();
//                Log.d("TAG", "开始上传第:" + index + "个文件");
//                if (index != null && index < originalFilenameList.size()) {
//                    String filename = originalFilenameList.get(index);
//                    File file = new File(localBaseDir + filename);
//                    if (file.exists()) {
//                        FileUploadRequest request = new FileUploadRequest();
//                        List<FileUploadRequest.FileUploadInfo> fileUploadInfoList = new ArrayList<>();
//
//                        FileUploadRequest.FileUploadInfo info = new FileUploadRequest.FileUploadInfo();
//                        info.setOriginalFilename(filename);
//                        info.setSize(file.length());
//                        fileUploadInfoList.add(info);
//
//                        request.setUsername(USERNAME);
//                        request.setFileUploadInfoList(fileUploadInfoList);
//                        fileService.uploadFile(applicationContext, request, localBaseDir, fileServiceViewModel);
//                    } else {
//                        Log.e("UploadMusicUtils", "uploadFiles: " + filename + "文件不存在！", null);
//                    }
//                }
//            }
//        });
//    }

    /**
     * 批量创建artist
     *
     * @param originalFilenameList 文件名列表
     */
    public void createArtist(List<String> originalFilenameList) {
        Set<String> artists = new HashSet<>();
        for (String filename : originalFilenameList) {
            List<String> artistNames = getArtistNameFromFilename(filename);
            artists.addAll(artistNames);
        }
        for (String name : artists) {
            ArtistCreateRequest request = new ArtistCreateRequest();
            request.setArtistName(name);
            artistService.create(request);
        }
    }

    /**
     * 创建音乐
     *
     * @param originalFilenameList 音乐文件名
     */
    public void createMusics(List<String> originalFilenameList) {
        List<MusicCreateRequest> requestList = new ArrayList<>();
        for (String filename : originalFilenameList) {
            List<String> artistNames = getArtistNameFromFilename(filename);
            for (String artistName : artistNames) {
                MusicCreateRequest request = new MusicCreateRequest();
                request.setName(filename.substring(0, filename.lastIndexOf(".")));
                request.setArtistName(artistName);
                requestList.add(request);
            }
        }
        musicService.batchCreateMusic(requestList, musicServiceViewModel);
    }

    /**
     * 给音乐添加标签
     *
     * @param originalFilenameList 音乐文件名
     */
    public void createTags(String tagName, TagType type, List<String> originalFilenameList) {
        List<TagCreateRequest> requestList = new ArrayList<>();
        for (String filename : originalFilenameList) {
            TagCreateRequest request = new TagCreateRequest();
            request.setTagName(tagName);
            request.setType(type);
            request.setEntityName(filename.substring(0, filename.lastIndexOf(".")));
            request.setUsername("admin");
            requestList.add(request);
        }
        tagService.batchCreateTag(requestList);
    }

    // ======================================================================================

    /**
     * 从文件名中获取artistName，可能有多个歌手合唱一首音乐
     *
     * @param filename 规范文件名，例如："周杰伦 - 稻香" "周杰伦_杨瑞代 - 等你下课"
     * @return artistName，例如："周杰伦" “list（周杰伦 杨瑞代）”
     */
    private List<String> getArtistNameFromFilename(String filename) {
        if (filename == null || filename.length() == 0) {
            return null;
        } else {
            List<String> artistList = new ArrayList<>();
            String artists = filename.split("-")[0].trim();
            String[] s = artists.split("_");
            for (String ss : s) {
                artistList.add(ss.trim());
            }
            return artistList;
        }
    }

}
