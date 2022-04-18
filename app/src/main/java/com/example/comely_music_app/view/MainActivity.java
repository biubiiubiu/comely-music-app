package com.example.comely_music_app.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import com.example.comely_music_app.R;
import com.example.comely_music_app.api.request.FileUploadRequest;
import com.example.comely_music_app.api.service.FileService;
import com.example.comely_music_app.api.service.impl.FileServiceImpl;
import com.example.comely_music_app.utils.FileOperationUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List imagePaths = null;
    List imageNames = null;
    List<Map<String, Object>> imageListItems;

    List musicPaths = null;
    List musicNames = null;
    List<Map<String, Object>> musicListItems;
    FileService fileService = new FileServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            List<FileUploadRequest.FileUploadInfo> list = new ArrayList<>();
            list.add(new FileUploadRequest.FileUploadInfo("uploadTest.jpg",503642L));
            FileUploadRequest requests = new FileUploadRequest("zt001",list);
            fileService.uploadFile(this, requests);
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(v -> {
            fileService.downloadFile(this,"zt002","IMAGE/2022/04/17/1de9a246-2a3e-4887-b2f7-27d2beb3d234.jpg");
        });
    }

    void GetImagesPath() {

        imagePaths = new ArrayList();
        imageNames = new ArrayList();

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            // 获取图片的绝对路径
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            imagePaths.add(path);
            imageNames.add(name);

            Log.i("GetImagesPath", "GetImagesPath: name = " + name + "  path = " + path);
        }
        imageListItems = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", imagePaths.get(i));
            map.put("desc", imageNames.get(i));
            imageListItems.add(map);
        }
    }

    void GetMusicPath() {

        musicPaths = new ArrayList();
        musicNames = new ArrayList();

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            // 获取图片的绝对路径
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            String path = cursor.getString(column_index);

            musicPaths.add(path);
            musicNames.add(name);
            File file = new File(path);
            Log.i("GetAudioPath", "GetAudioPath: name=" + name + "  path=" + path + "  size=" + file.length());
        }
        musicListItems = new ArrayList<>();
        for (int i = 0; i < musicPaths.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", musicPaths.get(i));
            map.put("desc", musicPaths.get(i));
            musicListItems.add(map);
        }
    }

    //根据路径获取图片
    private Bitmap getImgFromDesc(String path) {
        Bitmap bmp = null;
        File file = new File(path);
        // 动态申请权限
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        final int REQUEST_CODE = 10001;

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取

            for (String permission : permissions) {
                //  GRANTED---授权  DINIED---拒绝
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
                }
            }
        }

        boolean permission_readStorage = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean permission_camera = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(path);
        } else {
            Log.d("ImgActivity ", "getImgFromDesc: 该图片不存在！");
        }
        return bmp;
    }

    // bitmap转byte[]
    private byte[] bitmap2Bytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


}