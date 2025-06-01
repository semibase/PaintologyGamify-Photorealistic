package com.paintology.lite.photorealistic.drawing.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<BaseViewHolderChat> {

    public final int MSG_TYPE_LEFT = 0;
    public final int MSG_TYPE_RIGHT = 1;
    public final int MSG_TYPE_DATE = 2;
    public final int MSG_TYPE_TYPING = 3;
    Context _context;
    List<Chat> _list;
    String _img_url = "";

    public String isTyping = "false";

    public MessageAdapter(Context _context, List<Chat> user_list, String _img_url) {
        this._context = _context;
        this._list = user_list;
        this._img_url = _img_url;
    }

    @NonNull
    @Override
    public BaseViewHolderChat onCreateViewHolder(@NonNull ViewGroup viewGroup, int view_type) {
        Log.e("TAGGG", "View Type " + view_type);
        if (view_type == MSG_TYPE_RIGHT) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_right, viewGroup, false);
            return new ChatSenderAdapter(itemView, _context, isTyping);
        } else if (view_type == MSG_TYPE_LEFT) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_left, viewGroup, false);
            return new ChatReceiverAdapter(itemView, _img_url, _context);
        } else if (view_type == MSG_TYPE_DATE) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_date, viewGroup, false);
            return new ChatDisplayDateAdapter(itemView);
        } else {
            Log.e("TAGGG", "Display Chat Typing");
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_typing, viewGroup, false);
            return new ChatDisplayTypingAdapter(itemView, _context, _img_url);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolderChat holder, int position) {
        holder.onBindView(_list.get(position));
    }


    @Override
    public int getItemViewType(int position) {
//        fuser = FirebaseAuth.getInstance().getCurrentUser();
//        if (fuser.getUid().equalsIgnoreCase(_list.get(position).getSender()))
//            return MSG_TYPE_RIGHT;
//        else
//            return MSG_TYPE_LEFT;
        if (_list.get(position).getMsg_type() == MSG_TYPE_RIGHT)
            return MSG_TYPE_RIGHT;
        else if (_list.get(position).getMsg_type() == MSG_TYPE_LEFT)
            return MSG_TYPE_LEFT;
        else if (_list.get(position).getMsg_type() == MSG_TYPE_DATE)
            return MSG_TYPE_DATE;
        else
            return MSG_TYPE_TYPING;
    }

   /* @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Chat _obj = _list.get(i);
        if (_obj.getMsg_type() == MSG_TYPE_LEFT || _obj.getMsg_type() == MSG_TYPE_RIGHT) {
            myViewHolder.tv_msg.setText(_obj.getMessage());
            if (!_img_url.isEmpty()) {
                try {
                    Glide.with(_context)
                            .load(_img_url)
                            .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(myViewHolder.iv_profile_icon);
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
        } else {

        }
    }*/

    @Override
    public int getItemCount() {
        return _list.size();
    }

    /*public void setTyping(String isTyping) {
        this.isTyping = isTyping;
        notifyDataSetChanged();
    }*/


}
