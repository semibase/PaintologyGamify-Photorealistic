package com.paintology.lite.photorealistic.drawing.Chat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.R;

public class ChatReceiverAdapter extends BaseViewHolderChat {
    ImageView iv_profile_icon;
    TextView tv_msg, tv_time_stemp;
    String _img_url;
    Context _context;

    public ChatReceiverAdapter(View view, String _img_url, Context _context) {
        super(view);
        iv_profile_icon = (ImageView) view.findViewById(R.id.iv_p_icon);
        tv_msg = (TextView) view.findViewById(R.id.tv_show_msg);
        tv_time_stemp = (TextView) view.findViewById(R.id.tv_time_stemp);
        this._img_url = _img_url;
        this._context = _context;
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
    }
}
