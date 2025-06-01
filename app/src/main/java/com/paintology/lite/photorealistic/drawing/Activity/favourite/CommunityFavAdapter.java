package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.R;

import java.util.List;

public class CommunityFavAdapter extends RecyclerView.Adapter<CommunityFavAdapter.MyViewHolder> {

    Context context;
    List<CommunityPost> communityPostLists;

    public CommunityFavAdapter(Context context, List<com.paintology.lite.photorealistic.drawing.Model.CommunityPost> communityPostLists) {
        this.context = context;
        this.communityPostLists = communityPostLists;
    }

    @NonNull
    @Override
    public CommunityFavAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community_detail_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityFavAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
