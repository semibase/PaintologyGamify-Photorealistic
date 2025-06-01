package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;

public class ShowAllHashTagAdapter extends RecyclerView.Adapter<ShowAllHashTagAdapter.MyViewHolder> {


    Context _context;
    StringConstants constants;
    ArrayList<String> _list_item;

    public ShowAllHashTagAdapter(ArrayList<String> _spiner_item, Context _context) {

        this._context = _context;
        constants = new StringConstants();
        this._list_item = _spiner_item;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_all_hashtag_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_hashtag_name.setText(_list_item.get(i));
    }

    @Override
    public int getItemCount() {
        return (_list_item.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_hashtag_name;

        public MyViewHolder(View view) {
            super(view);
            tv_hashtag_name = (TextView) view.findViewById(R.id.tv_hashtag);
        }
    }
}
