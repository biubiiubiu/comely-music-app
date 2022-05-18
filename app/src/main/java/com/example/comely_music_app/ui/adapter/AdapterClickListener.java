package com.example.comely_music_app.ui.adapter;

import android.view.View;

public interface AdapterClickListener {
    void onClick(View itemView, int position);

    void onLongClick(View v, int position);

    void onClickBtnBehindTitle(View v, int position);

    void onClickRightBtn(View v, int position);
}
