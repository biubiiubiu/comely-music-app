package com.example.comely_music_app.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperationUtils {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBytesToFile(byte[] buffer, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            String directory = getDirectory(filePath);
            String filename = getFilename(filePath);
            file = makeFile(directory, filename);
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
    }

    private static String getDirectory(String filePath) {
        if (filePath.contains("/")) {
            return filePath.substring(0, filePath.lastIndexOf("/"));
        }
        return "/";
    }

    private static String getFilename(String filePath) {
        if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }


    //生成文件
    private static File makeFile(String filePath, String fileName) {
        File file = null;
        boolean isCreated = true;
        if (makeRootDirectory(filePath)) {
            try {
                file = new File(filePath + "/" + fileName);
                if (!file.exists()) {
                    isCreated = false;
                    isCreated = file.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isCreated ? file : null;
    }

    private static boolean makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            //不存在就新建
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
        return true;
    }
}
