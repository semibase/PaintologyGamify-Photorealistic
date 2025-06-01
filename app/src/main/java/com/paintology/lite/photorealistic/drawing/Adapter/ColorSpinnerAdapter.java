package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.MainInterface;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;

public class ColorSpinnerAdapter extends RecyclerView.Adapter<ColorSpinnerAdapter.MyViewHolder> {


    Context _context;
    String currentUserID = "";
    StringConstants constants;
    ArrayList<String> _list_item;
    MainInterface _interface;

    public ColorSpinnerAdapter(ArrayList<String> _spiner_item, Context _context, MainInterface _interface) {

        this._context = _context;
        constants = new StringConstants();
        this._list_item = _spiner_item;
        this._interface = _interface;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_dialog_layout_color, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


//        Log.e("TAGGG", "m_viewColorPanel Check Print data > " + Integer.parseInt(_list_item.get(i)));
        if (!_list_item.get(i).isEmpty()) {
            myViewHolder.image_color.setBackgroundColor(Integer.parseInt(_list_item.get(i)));
            myViewHolder.image_color.setTag(_list_item.get(i));
        }

    }

    @Override
    public int getItemCount() {
        return (_list_item.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image_color;

        public MyViewHolder(View view) {
            super(view);
            image_color = (ImageView) view.findViewById(R.id.image_color);

            image_color.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ImageView _imgview = (ImageView) view;
                    /*String key = "top_pick_color";
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
                    SharedPreferences.Editor editor = preferences.edit();
                    String lastColor = preferences.getString(key, "");

                    ArrayList<String> _lst_color = new ArrayList<>();

                    Log.e("TAG", "Color String Before " + lastColor);
                    String[] list_color = lastColor.split(",");
                    for (String _str : list_color) {
                        _lst_color.add(_str);
                    }

                    for (int i = 0; i < _lst_color.size(); i++) {
                        Log.e("TAG", "Color List Before " + _lst_color.get(i));
                    }

                    _lst_color.add(0, _lst_color.get(getAdapterPosition()));

                    _lst_color.remove(getAdapterPosition());
                    for (int i = 0; i < _lst_color.size(); i++) {
                        Log.e("TAG", "Color List After " + _lst_color.get(i));
                    }


                    StringBuilder _sb = new StringBuilder();
                    for (int i = 0; i < _lst_color.size(); i++) {
                        _sb.append(_lst_color.get(i) + ",");
//                    Log.e("TAGGG", "m_viewColorPanel COLOR > " + i + " COLOR " + list_color[i]);
                    }
                    editor.putString(key, _sb.toString());
                    editor.commit();
                    Log.e("TAG", "Color String After " + _sb.toString());*/


                    _interface.GetCatchColor((String) _imgview.getTag());

                }
            });
        }
    }
}
