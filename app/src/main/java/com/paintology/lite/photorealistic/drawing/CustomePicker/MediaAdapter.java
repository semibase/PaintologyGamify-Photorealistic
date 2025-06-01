package com.paintology.lite.photorealistic.drawing.CustomePicker;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {
    private List<AlbumImage> fileList;
    private List<Boolean> selected;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail, check;
        public TextView tv_fileName;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.image);
            check = (ImageView) view.findViewById(R.id.image2);
            tv_fileName = (TextView) view.findViewById(R.id.tv_file_name);
        }
    }

    public MediaAdapter(List<AlbumImage> fileList, List<Boolean> selected, Context context) {
        this.fileList = fileList;
        this.context = context;
        this.selected = selected;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Glide.with(context).load("file://" + bitmapList.get(position)).apply(new RequestOptions().override(300, 300).centerCrop().dontAnimate().skipMemoryCache(true)).transition(withCrossFade()).into(holder.thumbnail);
        /*Glide.with(context)
                .load("file://" + bitmapList.get(position))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.blue_circle)
                        .centerCrop())
                .into(holder.thumbnail);*/
        try {
            Glide.with(context)
                    .load("file://" + fileList.get(position).getFilePath())
                    .apply(new RequestOptions().placeholder(R.drawable.blue_circle).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.thumbnail);
            if (selected.get(position).equals(true)) {
                holder.check.setVisibility(View.VISIBLE);
//                holder.check.setAlpha(150);
            } else {
                holder.check.setVisibility(View.GONE);
            }
            holder.tv_fileName.setText(fileList.get(position).getFileName());
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}

