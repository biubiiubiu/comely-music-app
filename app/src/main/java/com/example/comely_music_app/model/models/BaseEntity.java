package com.example.comely_music_app.model.models;

import java.util.Date;

import lombok.Data;

@Data
public abstract class BaseEntity<T> {
    private Date createdTime;

    private Date updatedTime;

    public BaseEntity() {
        Date date = new Date();
        createdTime = date;
        updatedTime = date;
    }
}
