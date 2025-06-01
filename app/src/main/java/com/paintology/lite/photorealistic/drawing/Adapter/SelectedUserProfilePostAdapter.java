package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.Model.UserPostFromApi;
import com.paintology.lite.photorealistic.drawing.Model.UserPostList;
import com.paintology.lite.photorealistic.drawing.Model.Views;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.util.BaseViewHolder;
import com.paintology.lite.photorealistic.drawing.util.notifyPostList;

public class SelectedUserProfilePostAdapter extends RecyclerView.Adapter<BaseViewHolder> implements notifyPostList {

    UserPostFromApi _userPostList;
    Context _context;
    home_fragment_operation _interface;
    public int formatType = 4;
    PostOperation obj_interface;
    public static notifyPostList _objInterface;
    int lastpos = 0;
    boolean isFromProfileScreen;

    public SelectedUserProfilePostAdapter(UserPostFromApi _userPostList, Context _context, home_fragment_operation _interface, PostOperation post_interface, boolean... FromProfileScreen) {
        this._userPostList = _userPostList;
        this._context = _context;
        this._interface = _interface;
        this.obj_interface = post_interface;
        _objInterface = this;
        if (FromProfileScreen != null && FromProfileScreen.length != 0)
            isFromProfileScreen = FromProfileScreen[0];
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_user_post_item, parent, false);
        return new UserPostViewHolder(itemView, obj_interface, _context, _interface, isFromProfileScreen);

//        if (formatType == 1) {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_detail_item, parent, false);
//            return new DetailViewAdapter_Custome(itemView, obj_interface, _context, _interface, isFromProfileScreen);
//        } else {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_feed_item, parent, false);
//            return new GridPostAdapter(itemView, _interface, _context, formatType);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        UserPostList obj = _userPostList.getObjData().getPost_list().get(holder.getAdapterPosition());
        holder.onBindView(obj);
        lastpos = holder.getAdapterPosition();
    }

    @Override
    public int getItemViewType(int position) {
        return formatType;
    }


    @Override
    public int getItemCount() {
        return (_userPostList.getObjData().getPost_list().size() == 0 ? 0 : _userPostList.getObjData().getPost_list().size());
    }

    @Override
    public void notifyItem(String _post_id) {
        for (int i = 0; i < _userPostList.getObjData().getPost_list().size(); i++) {
            if (_post_id.equalsIgnoreCase(_userPostList.getObjData().getPost_list().get(i).getPost_id())) {
                _userPostList.getObjData().getPost_list().get(i).setLiked(true);
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void notifyItemView(String post_id, int totalViews) {
        for (int i = 0; i < _userPostList.getObjData().getPost_list().size(); i++) {
            if (post_id.equalsIgnoreCase(_userPostList.getObjData().getPost_list().get(i).getPost_id())) {
                Views objView = _userPostList.getObjData().getPost_list().get(i).getObjView();
                objView.setTotal_views(String.valueOf(totalViews));
                _userPostList.getObjData().getPost_list().get(i).setObjView(objView);
                notifyItemChanged(i);
            }
        }
    }

    public void addNewData(UserPostFromApi userPostFromApi) {
        Log.e("TAGGG", "Size Before " + _userPostList.getObjData().getPost_list().size());
        for (int i = 0; i < userPostFromApi.getObjData().getPost_list().size(); i++) {
            _userPostList.getObjData().getPost_list().add(userPostFromApi.getObjData().getPost_list().get(i));
            notifyItemInserted(i);
        }
        notifyDataSetChanged();
        Log.e("TAGGG", "Size After Add " + _userPostList.getObjData().getPost_list().size());
    }

    //TODO Remove all data from the list , and add new 10 records.
    public void removeAllData() {
        Log.e("TAGGG", "Size >> Before " + _userPostList.getObjData().getPost_list().size());
        _userPostList.getObjData().getPost_list().clear();
        Log.e("TAGGG", "Size >> After Add " + _userPostList.getObjData().getPost_list().size());
        notifyDataSetChanged();
    }

    public void notifyList(int f_Type) {
        Log.e("TAGGG", "Format Type in notify list " + f_Type);
        this.formatType = f_Type;
        notifyDataSetChanged();
    }


    public int getLastPosition() {
        return lastpos;
    }
}
