package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item;

import java.util.ArrayList;

public class Youtube_video_adapter extends RecyclerView.Adapter<Youtube_video_adapter.MyViewHolder> {


    public ArrayList<ContentSectionModel> list;
    Context context;
    Interface_select_item objSelect;

    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;

    ImageLoader mImageLoader;


    public Youtube_video_adapter(ArrayList<ContentSectionModel> list, Context context, Interface_select_item objSelect) {
        this.list = list;
        this.context = context;
        this.objSelect = objSelect;

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
                /*.showImageOnLoading(R.drawable.loading_bg)
                .showImageOnLoading(R.drawable.loading_bg)*/
                .cacheInMemory(false)
                .cacheOnDisc(false)
                .build();

        conf = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .writeDebugLogs()
                .threadPoolSize(5)
                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.youtube_video_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ContentSectionModel obj_content = list.get(i);
        if (obj_content != null) {
//            if (obj_content.getUrl() != null)
//                mImageLoader.displayImage(obj_content.getUrl(), myViewHolder.thumbnail);

            if (obj_content.getCaption() != null && !obj_content.getCaption().isEmpty()) {
                myViewHolder.tv_tutorial_name.setText(obj_content.getCaption());
            }

            if (obj_content.getVideoContent() != null) {
                if (obj_content.getVideoContent() == true) {
                    myViewHolder.video_icon.setVisibility(View.VISIBLE);
                    mImageLoader.displayImage(obj_content.getYoutube_url(), myViewHolder.thumbnail);
                } else {
                    myViewHolder.video_icon.setVisibility(View.GONE);
                    mImageLoader.displayImage(obj_content.getUrl(), myViewHolder.thumbnail);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail, video_icon;
        public TextView tv_tutorial_name;
        public FrameLayout frm_default;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.iv_tutorial_category);
            video_icon = (ImageView) view.findViewById(R.id.iv_video_icon);
            tv_tutorial_name = (TextView) view.findViewById(R.id.tv_category_name);
            frm_default = (FrameLayout) view.findViewById(R.id.frm_default);

            video_icon.setVisibility(View.GONE);
            frm_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    objSelect.selectItem(getAdapterPosition(), false);
                }
            });
        }
    }

    public void clearList() {
        list.clear();
        notifyDataSetChanged();
    }
}
