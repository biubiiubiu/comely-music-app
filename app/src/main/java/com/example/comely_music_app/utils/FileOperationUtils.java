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

//    List imagePaths = null;
//    List imageNames = null;
//    List<Map<String, Object>> imageListItems;
//
//    List musicPaths = null;
//    List musicNames = null;
//    List<Map<String, Object>> musicListItems;

    public void verifyStoragePermissions(Activity activity) {
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

    public void writeBytesToFile(byte[] buffer, String filePath) throws IOException {
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
//
//    void GetImagesPath() {
//
//        imagePaths = new ArrayList();
//        imageNames = new ArrayList();
//
//        Cursor cursor = getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            //获取图片的名称
//            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//            // 获取图片的绝对路径
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            String path = cursor.getString(column_index);
//            imagePaths.add(path);
//            imageNames.add(name);
//
//            Log.i("GetImagesPath", "GetImagesPath: name = " + name + "  path = " + path);
//        }
//        imageListItems = new ArrayList<>();
//        for (int i = 0; i < imagePaths.size(); i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", imagePaths.get(i));
//            map.put("desc", imageNames.get(i));
//            imageListItems.add(map);
//        }
//    }
//
//    void GetMusicPath() {
//
//        musicPaths = new ArrayList();
//        musicNames = new ArrayList();
//
//        Cursor cursor = getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            //获取图片的名称
//            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//            // 获取图片的绝对路径
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            String path = cursor.getString(column_index);
//
//            musicPaths.add(path);
//            musicNames.add(name);
//            File file = new File(path);
//            Log.i("GetAudioPath", "GetAudioPath: name=" + name + "  path=" + path + "  size=" + file.length());
//        }
//        musicListItems = new ArrayList<>();
//        for (int i = 0; i < musicPaths.size(); i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", musicPaths.get(i));
//            map.put("desc", musicPaths.get(i));
//            musicListItems.add(map);
//        }
//    }

    /*** ============================================================================================== ***/

    private String getDirectory(String filePath) {
        if (filePath.contains("/")) {
            return filePath.substring(0, filePath.lastIndexOf("/"));
        }
        return "/";
    }

    private String getFilename(String filePath) {
        if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }


    //生成文件
    private File makeFile(String filePath, String fileName) {
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

    private boolean makeRootDirectory(String filePath) {
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
//
//    //根据路径获取图片
//    private Bitmap getImgFromDesc(Activity activity, String path) {
//        Bitmap bmp = null;
//        File file = new File(path);
//        // 动态申请权限
//        String[] permissions = {
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA};
//        final int REQUEST_CODE = 10001;
//
//        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 检查该权限是否已经获取
//
//            for (String permission : permissions) {
//                //  GRANTED---授权  DINIED---拒绝
//                if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
//                }
//            }
//        }
//
//        boolean permission_readStorage = (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
//        boolean permission_camera = (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
//                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
//        if (file.exists()) {
//            bmp = BitmapFactory.decodeFile(path);
//        } else {
//            Log.d("ImgActivity ", "getImgFromDesc: 该图片不存在！");
//        }
//        return bmp;
//    }
//
//    // bitmap转byte[]
//    private byte[] bitmap2Bytes(Bitmap bmp) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        return stream.toByteArray();
//    }
}
