package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.paintology.lite.photorealistic.drawing.Model.CommunityComment;
import com.paintology.lite.photorealistic.drawing.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommunityCommentsAdapter extends RecyclerView.Adapter<ProfileViewHolder> {
    private List<CommunityComment> profileList;
    private Context context;

    public CommunityCommentsAdapter(List<CommunityComment> profileList, Context context) {
        this.profileList = profileList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_comments_view, parent, false); // Assuming your XML layout is named item_profile.xml
        return new ProfileViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        CommunityComment profile = profileList.get(position);

        Glide.with(context)
                .load(profile.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.ivUserProfile);
        holder.tvUname.setText(profile.getName());

        System.out.println("DocumentSnapshot : "  + profile.getCreated_at());


        String time = convertTimestampToLocal(profile.getCreated_at() );
        holder.tvDateTime.setText(time);
        holder.tvComment.setText(profile.getComment());
        holder.time_txt.setText(convertFirebaseTimestampToTime(profile.getCreated_at()));
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }


    private String convertFirebaseTimestampToTime(Timestamp timestamp) {
        // Check if the timestamp is null
        if (timestamp == null) {
            return null;
        }

        // Convert the Firebase Timestamp to a Date object
        Date date = new Date(timestamp.getSeconds() * 1000L + timestamp.getNanoseconds() / 1000000L);

        // Define the format for the time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Return the formatted time string
        return sdf.format(date);
    }

    private String convertTimestampToLocal(Timestamp timestamp) {

        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault()); // Set to local time zone
        return sdf.format(date);
    }
}