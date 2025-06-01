package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item;
import com.paintology.lite.photorealistic.drawing.gallery.model_DownloadedTutorial;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.util.ArrayList;

public class DownloadedAdapter extends BaseAdapter {
    private Context mContext;

    ArrayList<model_DownloadedTutorial> downloadedList;
    LayoutInflater inflater;
    int prevSelectedpos = 0;

    ImageLoader mImageLoader;
    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;

    Interface_select_item objInterface;

    StringConstants constants = new StringConstants();

    // Constructor
    public DownloadedAdapter(Context c, ArrayList<model_DownloadedTutorial> list, Interface_select_item objInterface) {
        mContext = c;
        downloadedList = list;
        this.objInterface = objInterface;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
                /*.showImageOnLoading(R.drawable.loading_bg)
                .showImageOnLoading(R.drawable.loading_bg)*/
                .cacheInMemory(false)
                .cacheOnDisc(false)
                .build();

        conf = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .writeDebugLogs()
                .threadPoolSize(5)
                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

    }

    @Override
    public int getCount() {
        return downloadedList.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View pView, ViewGroup pViewGroup) {

        Holder holder = new Holder();
        View rowView;

        if (pView == null)
            rowView = inflater.inflate(R.layout.downloaded_file_item, null);
        else
            rowView = pView;
        TextView tv_name = rowView.findViewById(R.id.tv_filename);
        ImageView iv_thumb = rowView.findViewById(R.id.iv_thumb);
        LinearLayout iv_more = rowView.findViewById(R.id.root_more);
        ImageView iv_movie_icon = rowView.findViewById(R.id.iv_movie_icon);
        ImageView edit = rowView.findViewById(R.id.edit);
        ImageView delete = rowView.findViewById(R.id.delete);
        ImageView share = rowView.findViewById(R.id.share);
//        ImageView post = rowView.findViewById(R.id.post);

        if (downloadedList.get(position).getDownloadedFileName().equalsIgnoreCase("Get Started"))
            tv_name.setText(mContext.getResources().getString(R.string.quick_video_guide));
        else
            tv_name.setText(downloadedList.get(position).getDownloadedFileName());
//        holder.iv_thumb.setBackground(mContext.getResources().getDrawable(R.drawable.paintology_logo));
        try {
            String _name = downloadedList.get(position).getDownloadedFileName();
            if (_name.equalsIgnoreCase("Get Started")) {
               /* String filepath = downloadedList.get(position).getDownloadedFilePath();
                Bitmap myBitmap = BitmapFactory.decodeFile(filepath);
                iv_thumb.setImageBitmap(myBitmap);*/

                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
//                post.setVisibility(View.GONE);

                String url = constants.getString(constants.mymovies_youtube_thumb, mContext);
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().placeholder(R.drawable.my_paintings_default_video_guide_thumb).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(iv_thumb);
            } else {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
//                post.setVisibility(View.VISIBLE);

                File file = new File(downloadedList.get(position).getDownloadedFilePath());
//                String filepath = KGlobal.getMyPaintingFolderPath(mContext) + "/" + getFileNameWithoutExtension(file) + ".png";
//                Log.e("TAGGG", "File Path of my movies " + filepath);
//                iv_thumb.setImageURI(Uri.fromFile(new File(filepath)));

                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                iv_thumb.setImageBitmap(bMap);
            }
        } catch (Exception e) {
        }


        if (downloadedList.get(position).getSelected()) {
            Log.e("TAGGG", "Selected post " + position);
//            iv_thumb.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            iv_thumb.setBackground(mContext.getResources().getDrawable(R.drawable.grid_small));
        } else
            iv_thumb.setBackgroundColor(mContext.getResources().getColor(R.color.gray_holo_light));

        tv_name.setOnClickListener(view -> objInterface.onSubMenuClick(iv_more, downloadedList.get(position), position));
        iv_more.setOnClickListener(view -> objInterface.onSubMenuClick(view, downloadedList.get(position), position));
        iv_movie_icon.setOnClickListener(view -> objInterface.onMovieIconClick(view, downloadedList.get(position), position));
        edit.setOnClickListener(view -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(mContext, constants.my_movies_icon_open, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(mContext, constants.my_movies_icon_open);
            objInterface.onEditClick(view, downloadedList.get(position), position);
        });
        delete.setOnClickListener(view -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(mContext, constants.my_movies_icon_delete, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(mContext, constants.my_movies_icon_delete);
            objInterface.onDeleteClick(view, downloadedList.get(position), position);
        });
        share.setOnClickListener(view -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(mContext, constants.my_movies_icon_share, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(mContext, constants.my_movies_icon_share);
            objInterface.onShareClick(view, downloadedList.get(position), position);
        });
//        post.setOnClickListener(view -> objInterface.onPostClick(view, downloadedList.get(position), position));

        return rowView;
    }

    private String getFileNameWithoutExtension(File file) {
        String fileName = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                fileName = name.replaceFirst("[.][^.]+$", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "";
        }

        return fileName;

    }

    public int getSelectedPos() {
        for (int i = 0; i < downloadedList.size(); i++) {
            if (downloadedList.get(i).getSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void resetSelection() {
        prevSelectedpos = 0;

    }


    public class Holder {
        TextView tv_name;
        ImageView iv_thumb;
    }
}
