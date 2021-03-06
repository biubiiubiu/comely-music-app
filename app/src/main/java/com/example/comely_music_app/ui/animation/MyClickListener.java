package com.example.comely_music_app.ui.animation;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class MyClickListener implements View.OnTouchListener {

    private static final int timeout = 400;//双击间四百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private final Handler handler;
    private final MyClickCallBack myClickCallBack;

    public interface MyClickCallBack {
        void oneClick();//点击一次的回调

        void doubleClick();//连续点击两次的回调

    }


    public MyClickListener(MyClickCallBack myClickCallBack) {
        this.myClickCallBack = myClickCallBack;
        handler = new Handler();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            clickCount++;
            handler.postDelayed(() -> {
                if (clickCount == 1) {
                    myClickCallBack.oneClick();
                } else if (clickCount == 2) {
                    myClickCallBack.doubleClick();
                }
                handler.removeCallbacksAndMessages(null);
                //清空handler延时，并防内存泄漏
                clickCount = 0;//计数清零
            }, timeout);//延时timeout后执行run方法中的代码
        }
        return true;//让点击事件继续传播，方便再给View添加其他事件监听,如果是false只能监听到ACTION_DOWN
    }
}