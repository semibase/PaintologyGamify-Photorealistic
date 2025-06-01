package com.paintology.lite.photorealistic.drawing.Chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.paintology.lite.photorealistic.drawing.R;


public class ChatDisplayDateAdapter extends BaseViewHolderChat {

    TextView tv_date_time;

    public ChatDisplayDateAdapter(@NonNull View itemView) {
        super(itemView);
        tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
    }

    @Override
    public void onBindView(Chat object) {
        tv_date_time.setText(object.getDate());
    }
}
