package com.paintology.lite.photorealistic.drawing.Chat;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolderChat extends RecyclerView.ViewHolder {
    public BaseViewHolderChat(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindView(Chat object);
}
