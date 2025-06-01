package com.paintology.lite.photorealistic.drawing.Community;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;

public abstract class BaseViewHolderCommunity extends RecyclerView.ViewHolder {
    public BaseViewHolderCommunity(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindView(CommunityPost object);
}
