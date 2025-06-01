package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.events.RefreshFavoriteEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class UserFavAdapter extends RecyclerView.Adapter<UserFavAdapter.MyViewHolder> {

    Context context;
    List<UserProfileFav> list;
    StringConstants constants;

    public UserFavAdapter(Context context, List<UserProfileFav> list) {
        this.context = context;
        this.list = list;
        constants = new StringConstants();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_fav_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserProfileFav userProfileFav = list.get(position);
        holder.UserLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMenu(v, userProfileFav.getUserId(), position);
            }
        });

        Glide.with(context).load(Uri.parse(userProfileFav.getProfileImage())) // image url
                .error(R.drawable.img_default_avatar)  // any image in case of error// resizing
                .centerCrop()
                .into(holder.UserImage);

        holder.UserNameTxt.setText(userProfileFav.getUsername());
        holder.DescriptionTxt.setText(userProfileFav.getDescription());

//        Glide.with(context)
//                .load(Uri.parse("https://raw.githubusercontent.com/lipis/flag-icons/main/flags/4x3/"+userProfileFav.getCountry().toLowerCase()+".svg"))
//                .centerCrop()
//                .into(holder.CountryFlag);


        holder.CountryFlag.setBackgroundDrawable(context.getDrawable(CountriesList.Companion.getCountryFlagResource(userProfileFav.getCountry())));

        holder.ParentCard.setOnClickListener(v -> {
            Log.e("TAGRR", userProfileFav.getId() + " " + userProfileFav.getUserId());
            if (userProfileFav.getUserId() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", userProfileFav.getUserId());
                ContextKt.sendUserEventWithParam(context, StringConstants.favorites_users_open, bundle);
            }
            FireUtils.openProfileScreen(context, userProfileFav.getUserId());
        });

    }

    private void ShowMenu(View v, String userId, int pos) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.user_fav_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove:
                        DrawingRepository drawingRepository = new DrawingRepository(context);
                        drawingRepository.deleteUserProfile(userId);
                        notifyItemRemoved(pos);
                        list.remove(pos);
                        EventBus.getDefault().post(new RefreshFavoriteEvent(0));
                        break;
                    case R.id.open:
                        FireUtils.openProfileScreen(context, userId);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView UserLikeBtn;
        private TextView DescriptionTxt;
        private TextView UserNameTxt;
        private ShapeableImageView UserImage;
        private com.google.android.material.imageview.ShapeableImageView CountryFlag;
        private CardView ParentCard;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            UserLikeBtn = itemView.findViewById(R.id.UserLikeBtn);
            DescriptionTxt = itemView.findViewById(R.id.DescriptionTxt);
            UserNameTxt = itemView.findViewById(R.id.UserNameTxt);
            UserImage = itemView.findViewById(R.id.UserImage);
            CountryFlag = itemView.findViewById(R.id.CountryFlag);
            ParentCard = itemView.findViewById(R.id.ParentCard);

        }
    }


}
