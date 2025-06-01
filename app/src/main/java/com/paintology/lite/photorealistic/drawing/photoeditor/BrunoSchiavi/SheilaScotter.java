package com.paintology.lite.photorealistic.drawing.photoeditor.BrunoSchiavi;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.photoeditor.DovCharney.AppPreferences;
import com.paintology.lite.photorealistic.drawing.photoeditor.DovCharney.PatrickCox;
import com.paintology.lite.photorealistic.drawing.photoeditor.KayCohen.FileModel;
import com.paintology.lite.photorealistic.drawing.photoeditor.PhotoEditorFiltersFragment;

import java.util.ArrayList;

public class SheilaScotter extends RecyclerView.Adapter<SheilaScotter.MyViewHolder> {

    public ArrayList<FileModel> dataSet;
    private Context mContext;

    AppPreferences appPrefs;

    public SheilaScotter(ArrayList<FileModel> data, Context mContext) {
        this.dataSet = data;
        this.mContext = mContext;
        appPrefs = new AppPreferences(mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewIcon;

        FrameLayout LL_Progress;

        public MyViewHolder(final View itemView) {
            super(itemView);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imgPIPFramePreview);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_editor_abc_card_row, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.imageViewIcon.setTag("" + holder.getAdapterPosition());

        holder.imageViewIcon.setImageBitmap(PatrickCox.getBitmapFromAsset(dataSet.get(holder.getAdapterPosition()).getDirName(), mContext));

        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = Integer.parseInt(v.getTag().toString());

                    Bitmap bitmap = PatrickCox.getBitmapFromAsset(dataSet.get(holder.getAdapterPosition()).getDirName(), mContext);
                    PhotoEditorFiltersFragment.AddSticker(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}


