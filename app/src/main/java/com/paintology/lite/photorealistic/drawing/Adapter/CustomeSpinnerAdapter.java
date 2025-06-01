package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Model.Spinner_Dialog_Item;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;

public class CustomeSpinnerAdapter extends RecyclerView.Adapter<CustomeSpinnerAdapter.MyViewHolder> {

    Context _context;
    String currentUserID = "";
    StringConstants constants;
    ArrayList<Spinner_Dialog_Item> _list_item;

    public CustomeSpinnerAdapter(ArrayList<Spinner_Dialog_Item> _spiner_item, Context _context) {

        this._context = _context;
        constants = new StringConstants();
        this._list_item = _spiner_item;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_dialog_layout_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        myViewHolder.cb_1.setText(_list_item.get(i).getItem_name());
        myViewHolder.cb_1.setChecked(_list_item.get(i).getChecked());


//        myViewHolder.tv_uname.setText(_lstFeed.get(i).getUserName());

//        if (currentUserID.equalsIgnoreCase(_lstFeed.get(i).getUser_id()))
//            myViewHolder.iv_menu_icon.setVisibility(View.GONE);
//        else
//            myViewHolder.iv_menu_icon.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return (_list_item.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatCheckBox cb_1;

        public MyViewHolder(View view) {
            super(view);
            cb_1 = (AppCompatCheckBox) view.findViewById(R.id.cb_1);

            cb_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _list_item.get(getAdapterPosition()).setChecked(!_list_item.get(getAdapterPosition()).getChecked());
                }
            });
        }
    }
}
