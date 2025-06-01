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
import com.paintology.lite.photorealistic.drawing.R;

import java.util.List;


public class BucketsAdapter extends RecyclerView.Adapter<BucketsAdapter.MyViewHolder> {
    private List<bucketModel> bucketNames;
    private List<String> bitmapList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, title_size;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            title_size = (TextView) view.findViewById(R.id.title_size);
            thumbnail = (ImageView) view.findViewById(R.id.image);
        }
    }

    public BucketsAdapter(List<bucketModel> bucketNames, List<String> bitmapList, Context context) {
        this.bucketNames = bucketNames;
        this.bitmapList = bitmapList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        bucketNames.get(position);
        holder.title.setText(bucketNames.get(position).getBucketNames());
        holder.title_size.setText(bucketNames.get(position).getBucketSize() + "");
        //Glide.with(context).load("file://"+bitmapList.get(position)).apply(new RequestOptions().override(300,300).centerCrop()).into(holder.thumbnail);
       /* Glide.with(context)
                .load("file://" + bitmapList.get(position))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.blue_circle)
                        .centerCrop())
                .into(holder.thumbnail);*/

        Glide.with(context)
                .load("file://" + bitmapList.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.thumbnaildefault).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return bucketNames.size();
    }
}

