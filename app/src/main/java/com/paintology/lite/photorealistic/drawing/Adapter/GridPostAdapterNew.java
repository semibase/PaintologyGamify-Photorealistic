package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.Community.BaseViewHolderCommunity;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class GridPostAdapterNew extends BaseViewHolderCommunity {

    Context _context;
    String currentUserID = "";
    StringConstants constants;
    home_fragment_operation _interface;
    public ImageView thumbnail, iv_like, iv_comment_icon, iv_view;
    public TextView tv_total_likes, tv_total_comment, tv_total_views;
    int formatType = 4;


    public GridPostAdapterNew(View view, home_fragment_operation _interface, Context context, int formatType) {
        super(view);
        this._interface = _interface;
        _context = context;
        this.formatType = formatType;
        constants = new StringConstants();
        currentUserID = constants.getString(constants.UserId, _context);

        iv_view = (ImageView) view.findViewById(R.id.iv_view);
        thumbnail = (ImageView) view.findViewById(R.id.image);
        tv_total_views = (TextView) view.findViewById(R.id.tv_total_views);
        iv_like = (ImageView) view.findViewById(R.id.iv_heart_icon);
        tv_total_likes = (TextView) view.findViewById(R.id.tv_total_likes);
        iv_comment_icon = (ImageView) view.findViewById(R.id.iv_comment);
        tv_total_comment = (TextView) view.findViewById(R.id.tv_total_comment);


        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_interface != null)
                    _interface.selectItem(getAdapterPosition(), false);

            }
        });
    }

    @Override
    public void onBindView(CommunityPost object) {

        if (object != null) {
            try {
                if (object.getImages() != null) {
                    String _url = "";
                    if (formatType >= 4) {
                        _url = object.getImages().getContent_resized();
                    } else if (formatType == 3)
                        _url = object.getImages().getContent_resized();
                    else if (formatType == 2)
                        _url = object.getImages().getContent_resized();

                    Glide.with(_context)
                            .load(_url)
                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(thumbnail);
                } else
                    Glide.with(_context)
                            .load(object.getImages().getContent())
                            .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(thumbnail);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at set Type " + e.getMessage(), e);
            }
            if (object.getStatistic().getComments() == 0) {
                tv_total_comment.setVisibility(View.GONE);
                iv_comment_icon.setImageResource(R.drawable.c_icon);
            } else {
                tv_total_comment.setVisibility(View.VISIBLE);
                tv_total_comment.setText(object.getStatistic().getComments()+"");
                iv_comment_icon.setImageResource(R.drawable.comment_icon_small);
            }

            if (object.getStatistic().getLikes() == 0) {
                tv_total_likes.setVisibility(View.GONE);
                iv_like.setImageResource(R.drawable.like_white);
            } else {
                iv_like.setImageResource(R.drawable.heart_icon_white_small);
                tv_total_likes.setVisibility(View.VISIBLE);
                tv_total_likes.setText(object.getStatistic().getLikes() + "");
            }

            if (object.getStatistic().getViews() == 0)
                tv_total_views.setVisibility(View.GONE);
            else {
                tv_total_views.setVisibility(View.VISIBLE);
                tv_total_views.setText(object.getStatistic().getViews() + "");
            }
        }
    }
}
