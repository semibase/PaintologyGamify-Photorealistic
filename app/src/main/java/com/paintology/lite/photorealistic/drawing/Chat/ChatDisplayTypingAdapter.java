package com.paintology.lite.photorealistic.drawing.Chat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.R;


public class ChatDisplayTypingAdapter extends BaseViewHolderChat {

    ImageView iv_p_icon_sender;
    Context context;
    String senderImageUrl;

    public ChatDisplayTypingAdapter(@NonNull View itemView, Context context, String senderImageUrl) {
        super(itemView);
        iv_p_icon_sender = (ImageView) itemView.findViewById(R.id.iv_p_icon_sender);
        this.context = context;
        this.senderImageUrl = senderImageUrl;
    }

    @Override
    public void onBindView(Chat object) {

        if (!senderImageUrl.isEmpty()) {
            try {
                Glide.with(context)
                        .load(senderImageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(iv_p_icon_sender);
            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage());
            }
        }
    }
}
