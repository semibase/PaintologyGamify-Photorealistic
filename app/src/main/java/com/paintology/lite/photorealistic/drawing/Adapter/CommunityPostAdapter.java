package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Community.BaseViewHolderCommunity;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.util.notifyPostList;

import java.util.List;

public class CommunityPostAdapter  extends RecyclerView.Adapter<BaseViewHolderCommunity> implements notifyPostList {
    List<CommunityPost> _userPostList;
    Context _context;
    home_fragment_operation _interface;
    public int formatType = 4;
    PostOperation obj_interface;
    public static notifyPostList _objInterface;
    int lastpos = 0;
    boolean IsFromFav = false;
    boolean isFromProfileScreen;
    public CommunityPostAdapter(List<CommunityPost> lst, Context _context, home_fragment_operation _interface, PostOperation post_interface, boolean IsFromFav, boolean... FromProfileScreen) {
        this._userPostList = lst;
        this._context = _context;
        this.IsFromFav = IsFromFav;
        this._interface = _interface;
        this.obj_interface = post_interface;
        _objInterface = this;
        if (FromProfileScreen != null && FromProfileScreen.length != 0)
            isFromProfileScreen = FromProfileScreen[0];
    }

    public void refresh(List<CommunityPost> lst)
    {
        this._userPostList = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolderCommunity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (formatType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_detail_item, parent, false);
            return new CommunityPostView(itemView, obj_interface, _context, _interface, isFromProfileScreen,IsFromFav);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_feed_item, parent, false);
            return new GridPostAdapterNew(itemView, _interface, _context, formatType);
        }
      }


    @Override
    public int getItemViewType(int position) {
        return formatType;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolderCommunity holder, int position) {
        CommunityPost obj = _userPostList.get(holder.getAdapterPosition());
        holder.onBindView(obj);
        lastpos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return _userPostList.size();
    }

    public void ItemRemoved(int lastpos){
        _userPostList.remove(lastpos);
        notifyItemRemoved(lastpos);
    }

    @Override
    public void notifyItem(String _post_id) {
        for (int i = 0; i < _userPostList.size(); i++) {
            if (_post_id.equalsIgnoreCase(_userPostList.get(i).getPost_id().toString())) {
                _userPostList.get(i).setLiked(true);
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void notifyItemView(String post_id, int totalViews) {
        for (int i = 0; i < _userPostList.size(); i++) {
            if (post_id.equalsIgnoreCase(_userPostList.get(i).getPost_id().toString())) {
//                Views objView = _userPostList.getObjData().getPost_list().get(i).getObjView();
//                objView.setTotal_views(String.valueOf(totalViews));
//                _userPostList.getObjData().getPost_list().get(i).setObjView(objView);
                notifyItemChanged(i);
            }
        }
    }

    public void addNewData(List<CommunityPost> userPostFromApi) {
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
