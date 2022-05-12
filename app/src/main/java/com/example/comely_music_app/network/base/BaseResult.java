package com.example.comely_music_app.network.base;

import androidx.annotation.NonNull;

import lombok.Data;

@Data
public class BaseResult<T> {
    private Integer code;
    private Boolean success;
    private String message;
    private T data;

    @NonNull
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
