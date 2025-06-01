package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.interfaces.SubCategoryItemClickListener;

import java.util.List;

public class NewSubCategoryAdapter extends RecyclerView.Adapter<NewSubCategoryAdapter.MyViewHolder> {

    List<GetCategoryPostModel.postData> _list;
    Context context;

    SubCategoryItemClickListener _obj_interface;


    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;

    ImageLoader mImageLoader;
    private boolean isChild;
    private String subCategoryName;

    public NewSubCategoryAdapter(List<GetCategoryPostModel.postData> _list, Context context,
                                 SubCategoryItemClickListener _obj_interface, boolean isChild,
                                 String title) {
        this._list = _list;
        this.context = context;
        this._obj_interface = _obj_interface;
        this.isChild = isChild;
        this.subCategoryName = title;

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
                /*.showImageOnLoading(R.drawable.loading_bg)
                .showImageOnLoading(R.drawable.loading_bg)*/
                .cacheInMemory(false)
                .cacheOnDisc(false)
                .build();

        conf = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .writeDebugLogs()
                .threadPoolSize(5)
                .build();


//        mDisplayImageOptions = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
//                /*.showImageOnLoading(R.drawable.loading_bg)
//                .showImageOnLoading(R.drawable.loading_bg)*/
//                .cacheInMemory(true)
//                .cacheOnDisc(true)
//                .build();

//        conf = new ImageLoaderConfiguration.Builder(context)
//                .defaultDisplayImageOptions(mDisplayImageOptions)
//                .memoryCacheSize(50 * 1024 * 1024)
//                .discCacheSize(50 * 1024 * 1024)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheExtraOptions(250, 250, null)
//                .threadPoolSize(5)
//                .writeDebugLogs()
//                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_subcategory_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        GetCategoryPostModel.postData _obj = _list.get(i);
        if (_obj != null) {
            myViewHolder.tv_id.setText(_obj.getObjdata().ID);
            myViewHolder.tv_category.setText(Html.fromHtml(subCategoryName));
            myViewHolder.tv_tutorial_name.setText(_obj.getObjdata().getPost_title() != null ? _obj.getObjdata().getPost_title() : "");

            String ytLink = _obj.getObjdata().getYoutube_link();
            if (!TextUtils.isEmpty(ytLink)) {
                myViewHolder.iv_yt.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.iv_yt.setVisibility(View.GONE);
            }

            if (_obj.getResize() != null && !_obj.getResize().equalsIgnoreCase("false")) {

                String imageLink = _obj.getResize();

                Log.d("imagesLinks", "onBindViewHolder: " + imageLink);
                RequestOptions options = new RequestOptions()
//                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.thumbnaildefault)
                        .error(R.drawable.thumbnaildefault);

                Glide.with(context).load(imageLink).apply(options).into(myViewHolder.thumbnail);

            }
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail, iv_yt;
        public TextView tv_category;
        public TextView tv_tutorial_name;
        public TextView tv_id;
        public ImageView iv_more;
        public ConstraintLayout more_container;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.iv_tutorial_category);
            iv_yt = (ImageView) view.findViewById(R.id.iv_yt);
            tv_category = (TextView) view.findViewById(R.id.tv_category);
            tv_tutorial_name = (TextView) view.findViewById(R.id.tv_category_desc);
            tv_id = (TextView) view.findViewById(R.id.tv_id);
            iv_more = (ImageView) view.findViewById(R.id.iv_more);
            more_container = (ConstraintLayout) view.findViewById(R.id.more_container);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isChild) {
                        _obj_interface.selectChildItem(_list.get(getAdapterPosition()), subCategoryName);
                    } else {
                        _obj_interface.selectItem(getAdapterPosition(), false);
                    }
                }
            });

            iv_more.setOnClickListener(v -> _obj_interface.onSubMenuClick(iv_more,
                    _list.get(getAdapterPosition()), getAdapterPosition()));
            more_container.setOnClickListener(v -> _obj_interface.onSubMenuClick(iv_more,
                    _list.get(getAdapterPosition()), getAdapterPosition()));
            tv_category.setOnClickListener(v -> _obj_interface.onSubMenuClick(iv_more,
                    _list.get(getAdapterPosition()), getAdapterPosition()));

        }
    }
}
