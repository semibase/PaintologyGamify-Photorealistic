package com.paintology.lite.photorealistic.drawing.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.Autocomplete.RecyclerViewPresenter;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.R;

import java.util.ArrayList;
import java.util.List;


public class UserPresenter extends RecyclerViewPresenter<Firebase_User> {

    @SuppressWarnings("WeakerAccess")
    protected Adapter adapter;

    ArrayList<Firebase_User> _user_list;
    Context context;

    @SuppressWarnings("WeakerAccess")
    public UserPresenter(@NonNull Context context, ArrayList<Firebase_User> _user_list) {
        super(context);
        this._user_list = _user_list;
        this.context = context;
       /* Collections.sort(_user_list, new Comparator<Firebase_User>() {
            public int compare(Firebase_User v1, Firebase_User v2) {
                return v1.getUser_name().compareTo(v2.getUser_name());
            }
        });*/

    }

    @NonNull
    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    String queryString = "";

    @Override
    protected void onQuery(@Nullable CharSequence query) {

        if (TextUtils.isEmpty(query)) {
            adapter.setData(_user_list);
        } else {
            query = query.toString().toLowerCase();
            List<Firebase_User> list = new ArrayList<>();
            for (Firebase_User u : _user_list) {
                if (u.getUser_name().toLowerCase().contains(query) ||
                        u.getUser_email().toLowerCase().contains(query)) {
                    list.add(u);
                }
            }
            queryString = query.toString();
            adapter.setData(list);
            Log.e("UserPresenter", "found " + list.size() + " users for query " + query);
        }
        adapter.notifyDataSetChanged();
    }

    protected class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<Firebase_User> data;

        @SuppressWarnings("WeakerAccess")
        protected class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView fullname;
            private TextView username;
            private ImageView iv_profile_pic;

            Holder(View itemView) {
                super(itemView);
                root = itemView;
                fullname = itemView.findViewById(R.id.fullname);
                username = itemView.findViewById(R.id.username);
                iv_profile_pic = itemView.findViewById(R.id.iv_profile_pic);
            }
        }

        @SuppressWarnings("WeakerAccess")
        protected void setData(@Nullable List<Firebase_User> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (isEmpty()) {
                holder.fullname.setText("No user here!");
                holder.username.setText("Sorry!");
                holder.root.setOnClickListener(null);
                return;
            }
            final Firebase_User user = data.get(position);
            holder.fullname.setText("@" + user.getUser_name());
            holder.username.setText(user.getUser_email());
            if (!user.getUser_profile_pic().isEmpty()) {
                try {
                    String url = user.getUser_profile_pic();
                    Glide.with(context)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(holder.iv_profile_pic);
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(user);
                }
            });
        }
    }
}
