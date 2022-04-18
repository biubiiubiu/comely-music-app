package com.example.comely_music_app.api.base;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class BaseObserver<T> extends DisposableObserver<BaseResult<T>> {

    private boolean isShowDialog;   //是否需要显示Dialog
    /**
     * 解析数据失败
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络问题
     */
    public static final int BAD_NETWORK = 1002;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1003;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 1004;

    /**
     * @param isShowDialog 是否需要展示Dialog
     */
    public BaseObserver(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    @Override
    protected void onStart() {
        if (isShowDialog) {
            showLoading();
        }
    }

    @Override
    public void onNext(BaseResult<T> response) {
        //根据架构自定义如何返回
        if (response.getCode() == 20000) {
            onSuccess(response.getData());
        } else {
            int errorCode = response.getCode();
            String message = response.getMessage();
            onFail(errorCode, message, response.getData());
        }
    }

    @Override
    public void onError(Throwable error) {
        dismissDialog();
        if (error instanceof HttpException) {
            //   HTTP错误
            onException(BAD_NETWORK);
        } else if (error instanceof ConnectException
                || error instanceof UnknownHostException) {
            //   连接错误
            onException(CONNECT_ERROR);
        } else if (error instanceof InterruptedIOException) {
            //  连接超时
            onException(CONNECT_TIMEOUT);
        } else if (error instanceof JsonParseException
                || error instanceof JSONException
                || error instanceof ParseException) {
            //  解析错误
            onException(PARSE_ERROR);
        } else {
            if (error != null) {
                onError(error.toString());
            } else {
                onError("未知错误");
            }
        }
    }

    private void onException(int unknownError) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError("连接错误");
                break;

            case CONNECT_TIMEOUT:
                onError("连接超时");
                break;

            case BAD_NETWORK:
                onError("网络问题");
                break;

            case PARSE_ERROR:
                onError("解析数据失败");
                break;

            default:
                break;
        }
    }

    @Override
    public void onComplete() {
        if (isShowDialog) {
            dismissDialog();
        }
    }

    public abstract void onSuccess(T o);

    public abstract void onFail(int errorCode, String errorMsg, T response);

    public abstract void onError(String msg);

    /**
     * 显示Dialog.
     * 体现思路,实现略
     */
    public void showLoading() {
        //TODO 略
    }

    /**
     * 隐藏Dialog
     * 体现思路,实现略
     */
    public void dismissDialog() {
        //TODO 略
    }

}

