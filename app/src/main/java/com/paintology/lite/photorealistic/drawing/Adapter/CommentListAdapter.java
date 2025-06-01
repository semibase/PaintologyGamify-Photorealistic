package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {


    Context _context;
    ArrayList<AllCommentModel.data.all_comments> _lst;
    PostOperation obj_interface;
    home_fragment_operation interface_home_fragment;

    String user_id = "";
    StringConstants constants = new StringConstants();

    public CommentListAdapter(Context _context, ArrayList<AllCommentModel.data.all_comments> _lst, PostOperation obj_interface, home_fragment_operation interface_home_fragment) {
        this._context = _context;
        this._lst = _lst;
        this.obj_interface = obj_interface;
        this.interface_home_fragment = interface_home_fragment;
        user_id = constants.getString(constants.UserId, _context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (_lst.get(i).getUsername() != null) {
            myViewHolder.tv_user_name.setText(_lst.get(i).getUsername().isEmpty() ? "N/A" : _lst.get(i).getUsername());
        } else
            myViewHolder.tv_user_name.setText("N/A");

        if (_lst.get(i).getComment_content() != null) {
            myViewHolder.tv_comment.setText(_lst.get(i).getComment_content());
        }

        if (_lst.get(i).getComment_date() != null) {
            myViewHolder.tv_date_time.setText(_lst.get(i).getComment_date());
        }
    }

    @Override
    public int getItemCount() {
        return (_lst != null ? _lst.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_user_name, tv_date_time;
        AutoLinkTextView tv_comment;

        public MyViewHolder(View view) {
            super(view);

            tv_user_name = view.findViewById(R.id.tv_comment_username);
            tv_comment = view.findViewById(R.id.tv_user_comment);
            tv_date_time = view.findViewById(R.id.tv_date_time);
            tv_comment.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_MENTION);

            tv_comment.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
                if (autoLinkMode.equals(AutoLinkMode.MODE_MENTION)) {
                    String _matchText = matchedText.replace("@", "");
                    for (int i = 0; i < interface_home_fragment.getFirebaseUserList().size(); i++) {
                        String _name = interface_home_fragment.getFirebaseUserList().get(i).getUser_name().replace(" ", "");

                        if (_matchText.trim().equalsIgnoreCase(_name.trim())) {
                            FireUtils.openProfileScreen(_context, interface_home_fragment.getFirebaseUserList().get(i).getKey());
                            break;
                        }
                    }
                } else {
                    obj_interface.seachByHashTag(matchedText);
                }
            });
        }
    }
}
