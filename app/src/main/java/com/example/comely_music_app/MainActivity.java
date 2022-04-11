package com.example.comely_music_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comely_music_app.IModel.UserService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile();
            }
        });
    }

    /**
     * 获取Userlist
     */
    private void connect() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .build();

        UserService service = retrofit.create(UserService.class);
        Call<ResponseBody> call = service.getUserList();
        // 用法和OkHttp的call如出一辙
        // 不同的是如果是Android系统回调方法执行在主线程
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(
                    Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getFile() {
        System.out.println(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));

        String basePath = "/storage/emulated/0/$MuMu共享文件夹";
        ///mnt/sdcard/Music
        File file = new File(basePath);
        File[] baseFile = file.listFiles();
        if (baseFile == null) {
            Log.e("error", "空目录");
        }
        List<String> s = new ArrayList<>();
        for (int i = 0; i < baseFile.length; i++) {
            s.add(baseFile[i].getAbsolutePath());
            System.out.println(baseFile[i].getAbsolutePath());
        }
        List<File> files = new ArrayList<>();
        for (String path : s) {
            files.add(new File(basePath + "/" + path));
        }
        System.out.println();
    }

    /**
     * 上传文件
     */
    private void uploadFile() {
        // /storage/emulated
        File file = new File("/0/DCIM/hello.jpg");
        Log.i("file:", file.getName());
    }

    /**
     * 获取相册
     */
    private void getPhotos() {
//        PictureSelector.create(this)
//                .openGallery(PictureMimeType.ofImage())
//                .theme(R.style.picture_default_style)
//                .maxSelectNum(1)
//                .minSelectNum(1)
//                .imageSpanCount(3)
//                .previewImage(false)
//                .previewVideo(false)
//                .selectionMode(PictureConfig.SINGLE)
//                .previewImage(false)
//                .isCamera(false)
//                .withAspectRatio(1, 1)
//                .imageFormat(PictureMimeType.PNG)
//                .circleDimmedLayer(true)
//                .enableCrop(true)
//                .compress(true)
//                .showCropFrame(false)
//                .showCropGrid(false)
//                .rotateEnabled(false)
//                .scaleEnabled(true)
//                .isGif(false)
//                .minimumCompressSize(100)
//                .synOrAsy(true)
//                .forResult(PictureConfig.CHOOSE_REQUEST);
    }
}