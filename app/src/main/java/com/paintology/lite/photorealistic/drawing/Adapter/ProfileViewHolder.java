package com.paintology.lite.photorealistic.drawing.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.paintology.lite.photorealistic.drawing.R;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    RoundedImageView ivUserProfile;
    AppCompatTextView tvUname;
    AppCompatTextView tvDateTime;
    AppCompatTextView time_txt;
    AppCompatTextView tvComment;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
        tvUname = itemView.findViewById(R.id.tv_uname);
        tvDateTime = itemView.findViewById(R.id.tv_date_time);
        tvComment = itemView.findViewById(R.id.tv_comment);
        time_txt = itemView.findViewById(R.id.time_txt);
    }
}