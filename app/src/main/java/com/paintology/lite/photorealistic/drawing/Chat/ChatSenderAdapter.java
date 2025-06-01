package com.paintology.lite.photorealistic.drawing.Chat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class ChatSenderAdapter extends BaseViewHolderChat {

    ImageView iv_profile_icon;
    TextView tv_msg, tv_time_stemp;
    String _img_url;
    Context _context;
    StringConstants _constant = new StringConstants();

    String isTyping;

    RelativeLayout rl_typing;

    public ChatSenderAdapter(View view, Context _context, String isTyping) {
        super(view);
        iv_profile_icon = (ImageView) view.findViewById(R.id.iv_p_icon);
        tv_msg = (TextView) view.findViewById(R.id.tv_show_msg);
        tv_time_stemp = (TextView) view.findViewById(R.id.tv_time_stemp);
        this._context = _context;
        _img_url = _constant.getString(_constant.ProfilePicsUrl, _context);
        this.isTyping = isTyping;
        rl_typing = (RelativeLayout) view.findViewById(R.id.rl_typing);
    }

    @Override
    public void onBindView(Chat _obj) {
        tv_msg.setText(_obj.getMessage());
        tv_time_stemp.setText(_obj.getTime());
        if (!_img_url.isEmpty()) {
            try {
                Glide.with(_context)
                        .load(_img_url)
                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(iv_profile_icon);
            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage());
            }
        }
        if (isTyping.equalsIgnoreCase("true")) {
            rl_typing.setVisibility(View.VISIBLE);
        } else
            rl_typing.setVisibility(View.GONE);
    }
}
