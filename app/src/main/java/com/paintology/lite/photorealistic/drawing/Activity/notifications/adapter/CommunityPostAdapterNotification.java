package com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities.CommunityPostViewNotification;
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.CommunityPostNotification;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.util.notifyPostList;

import java.util.List;

public class CommunityPostAdapterNotification extends RecyclerView.Adapter<BaseViewHolderCommunityNotification> implements notifyPostList {
    List<CommunityPostNotification> _userPostList;
    Context _context;
    home_fragment_operation _interface;
    public int formatType = 4;
    PostOperation obj_interface;
    public static notifyPostList _objInterface;
    int lastpos = 0;
    boolean isFromProfileScreen;

    public CommunityPostAdapterNotification(List<CommunityPostNotification> lst, Context _context, boolean... FromProfileScreen) {
        this._userPostList = lst;
        this._context = _context;
        if (FromProfileScreen != null && FromProfileScreen.length != 0)
            isFromProfileScreen = FromProfileScreen[0];
    }


    @NonNull
    @Override
    public BaseViewHolderCommunityNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_detail_item_noti, parent, false);
        return new CommunityPostViewNotification(itemView, obj_interface, _context, _interface, isFromProfileScreen);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolderCommunityNotification holder, int position) {
        CommunityPostNotification obj = _userPostList.get(holder.getAdapterPosition());
        holder.onBindView(obj);
        lastpos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return _userPostList.size();
    }


    @Override
    public void notifyItem(String _post_id) {
        for (int i = 0; i < _userPostList.size(); i++) {
            if (_post_id.equalsIgnoreCase(_userPostList.get(i).getPostId().toString())) {
                _userPostList.get(i).setLiked(true);
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void notifyItemView(String post_id, int totalViews) {
        for (int i = 0; i < _userPostList.size(); i++) {
            if (post_id.equalsIgnoreCase(_userPostList.get(i).getPostId().toString())) {
//                Views objView = _userPostList.getObjData().getPost_list().get(i).getObjView();
//                objView.setTotal_views(String.valueOf(totalViews));
//                _userPostList.getObjData().getPost_list().get(i).setObjView(objView);
                notifyItemChanged(i);
            }
        }
    }

    public void addNewData(List<CommunityPostNotification> userPostFromApi) {
        Log.e("TAGGG", "Size Before " + _userPostList.size());
        for (int i = 0; i < userPostFromApi.size(); i++) {
            _userPostList.add(userPostFromApi.get(i));
            notifyItemInserted(i);
        }
        notifyDataSetChanged();
//        Log.e("TAGGG", "Size After Add " + _userPostList.getObjData().getPost_list().size());
    }

    //TODO Remove all data from the list , and add new 10 records.
    public void removeAllData() {
//        Log.e("TAGGG", "Size >> Before " + _userPostList.getObjData().getPost_list().size());
//        _userPostList.getObjData().getPost_list().clear();
//        Log.e("TAGGG", "Size >> After Add " + _userPostList.getObjData().getPost_list().size());
//        notifyDataSetChanged();
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
