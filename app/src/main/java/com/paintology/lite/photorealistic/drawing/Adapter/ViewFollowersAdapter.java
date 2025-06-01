package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.Follower;
import com.paintology.lite.photorealistic.drawing.Model.Following;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.List;

public class ViewFollowersAdapter extends RecyclerView.Adapter<ViewFollowersAdapter.MyViewHolder> {


    Context _context;
    List<Follower> _lst_followers;
    ItemClickListener _listener;
    boolean isFromFollowers;
    List<Following> _lst_following;
    private boolean fromOtherUserProfile;

    public ViewFollowersAdapter(Context _context, List<Follower> _lst_followers,
                                ItemClickListener _listener, List<Following> _lst_following,
                                boolean isFromFollowers, boolean fromOtherUserProfile) {
        this._context = _context;
        this._lst_followers = _lst_followers;
        this._listener = _listener;
        this.isFromFollowers = isFromFollowers;
        this._lst_following = _lst_following;
        this.fromOtherUserProfile = fromOtherUserProfile;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.followers_list_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int pos) {

        if (isFromFollowers) {
            Follower follower = _lst_followers.get(pos);
            String userId = follower.getUserID();
            getUserOnlineStatus(userId, myViewHolder, myViewHolder.getAdapterPosition(), isFromFollowers);
            myViewHolder.tv_user_name.setText(follower.getUsername() != null ? follower.getUsername() : "");

            if (follower.getProfilePic() != null && !follower.getProfilePic().isEmpty())

                Glide.with(_context)
                        .load(follower.getProfilePic())
                        .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(myViewHolder.iv_profile);

            try {
                if (follower.getIsOnline()) {
                    myViewHolder.view_online.setVisibility(View.VISIBLE);
                    myViewHolder.view_offline.setVisibility(View.GONE);
                } else {
                    myViewHolder.view_online.setVisibility(View.GONE);
                    myViewHolder.view_offline.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("ViewFollowersAdapter", e.getMessage());
            }
        } else {
            Following following = _lst_following.get(pos);
            String userId = following.getUserID();
            getUserOnlineStatus(userId, myViewHolder, myViewHolder.getAdapterPosition(), isFromFollowers);
            myViewHolder.tv_user_name.setText(following.getUsername() != null ? following.getUsername() : "");

            if (following.getProfilePic() != null && !following.getProfilePic().isEmpty())
                Glide.with(_context)
                        .load(following.getProfilePic())
                        .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(myViewHolder.iv_profile);

            try {
                if (following.getIsOnline()) {
                    myViewHolder.view_online.setVisibility(View.VISIBLE);
                    myViewHolder.view_offline.setVisibility(View.GONE);
                } else {
                    myViewHolder.view_online.setVisibility(View.GONE);
                    myViewHolder.view_offline.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("ViewFollowersAdapter", e.getMessage());
            }
        }

        myViewHolder.ll_dots.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(_context, "follower_following_more_menu_click", Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(_context, "follower_following_more_menu_click");
            if (isFromFollowers) {
                showDialog(v, _lst_followers.get(myViewHolder.getAdapterPosition()).getUserID(),
                        _lst_followers.get(myViewHolder.getAdapterPosition()).getUsername(),
                        myViewHolder.getAdapterPosition(), isFromFollowers);
            } else {
                showDialog(v, _lst_following.get(myViewHolder.getAdapterPosition()).getUserID(),
                        _lst_following.get(myViewHolder.getAdapterPosition()).getUsername(),
                        myViewHolder.getAdapterPosition(), isFromFollowers);
            }

        });

    }

    private void showDialog(View view, String userID, String userName, int position, boolean isFromFollowers) {
        PopupMenu popupMenu = new PopupMenu(_context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_follower_following_menu, popupMenu.getMenu());

        MenuItem unfollow = popupMenu.getMenu().findItem(R.id.action_unfollow);

        unfollow.setVisible(!fromOtherUserProfile && !isFromFollowers);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.action_view_profile:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(_context, StringConstants.follower_following_more_menu_view_profile_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, StringConstants.follower_following_more_menu_view_profile_click);
                    view.setTag(position);
                    _listener.viewItemClicked(view);
                    break;
                case R.id.action_chat:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(_context, StringConstants.follower_following_more_menu_chat_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, StringConstants.follower_following_more_menu_chat_click);
                    _listener.chatMenuClicked(view, userID, userName);
                    break;
                case R.id.action_unfollow:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(_context, StringConstants.follower_following_more_menu_unfollow_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(_context, StringConstants.follower_following_more_menu_unfollow_click);
                    _listener.unfollowMenuClicked(view, userID, userName, isFromFollowers, _lst_following.get(position), position);
                    break;
            }
            return false;
        });
        // Showing the popup menu
        popupMenu.show();
    }

    private void getUserOnlineStatus(String userId, MyViewHolder myViewHolder, int adapterPosition,
                                     boolean isFromFollowers) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = reference.child(new StringConstants().firebase_user_list)
                .child(String.valueOf(userId))
                .child("is_online");

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String online = (String) snapshot.getValue();

                boolean isOnline = Boolean.parseBoolean(online);

                if (isFromFollowers) {
                    Follower follower = _lst_followers.get(adapterPosition);
                    follower.setIsOnline(isOnline);
                    notifyItemChanged(adapterPosition);
                } else {
                    Following following = _lst_following.get(adapterPosition);
                    following.setIsOnline(isOnline);
                    notifyItemChanged(adapterPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (isFromFollowers)
            return (_lst_followers != null ? _lst_followers.size() : 0);
        else
            return (_lst_following != null ? _lst_following.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_user_name;
        ImageView iv_profile;
        CardView card_view;
        View view_online, view_offline;
        LinearLayout ll_dots;

        public MyViewHolder(View view) {
            super(view);

            tv_user_name = (TextView) view.findViewById(R.id.tv_username);
            iv_profile = (ImageView) view.findViewById(R.id.iv_profile_pic);
            card_view = (CardView) view.findViewById(R.id.card_view);
            view_online = view.findViewById(R.id.view_online);
            view_offline = view.findViewById(R.id.view_offline);
            ll_dots = view.findViewById(R.id.ll_dots);

            view_offline.setVisibility(View.GONE);
            view_online.setVisibility(View.GONE);

            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setTag(getAdapterPosition());
                    _listener.viewItemClicked(view);
                }
            });

        }
    }

    public interface ItemClickListener {
        void viewItemClicked(View view);

        void chatMenuClicked(View view, String userID, String userName);

        void unfollowMenuClicked(View view, String userID, String userName, boolean isFromFollowers, Following following, int position);
    }
}
