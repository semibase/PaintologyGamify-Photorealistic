package com.paintology.lite.photorealistic.drawing.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;

public class SingleRecyclerViewAdapter extends RecyclerView.Adapter<SingleRecyclerViewAdapter.DataObjectHolder> {

    private String[] mData;
    private Integer[] mFlagData;
    private static SingleClickListener sClickListener;
    private static int sSelected = -1;

    public SingleRecyclerViewAdapter(String[] mData, Integer[] listFlags, int selected) {
        this.mData = mData;
        this.mFlagData = listFlags;
        sSelected = selected;
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mFlag;
        TextView mTextView;
        RadioButton mRadioButton;

        public DataObjectHolder(View itemView) {
            super(itemView);
            this.mFlag = (ImageView) itemView.findViewById(R.id.single_list_item_flag);
            this.mTextView = (TextView) itemView.findViewById(R.id.single_list_item_text);
            mTextView.setTextDirection(View.TEXT_DIRECTION_LTR);
            this.mRadioButton = (RadioButton) itemView.findViewById(R.id.single_list_item_check_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sSelected = getAdapterPosition();
            sClickListener.onItemClickListener(getAdapterPosition(), view);
        }
    }

    public void selectedItem(int position) {
        notifyDataSetChanged();
    }

    void setOnItemClickListener(SingleClickListener clickListener) {
        sClickListener = clickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_list_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.mTextView.setText(mData[position]);

        if (sSelected == position) {
            holder.mRadioButton.setChecked(true);
        } else {
            holder.mRadioButton.setChecked(false);
        }

        holder.mFlag.setImageResource(mFlagData[position]);

    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    interface SingleClickListener {
        void onItemClickListener(int position, View view);
    }

}

