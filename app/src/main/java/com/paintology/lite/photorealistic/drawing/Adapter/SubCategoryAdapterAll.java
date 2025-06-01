package com.paintology.lite.photorealistic.drawing.Adapter;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialcategory;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.interfaces.SubCategoryItemClickListener;

import java.util.List;

public class SubCategoryAdapterAll extends RecyclerView.Adapter<SubCategoryAdapterAll.MyViewHolder> {

    public List<Tutorialdatum> _list;
    Context context;

    SubCategoryItemClickListener _obj_interface;


    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;

    ImageLoader mImageLoader;
    private boolean isChild;
    private String subCategoryName;

//    private ArrayList<Boolean> IsYoutubeExits = new ArrayList<>();


    public SubCategoryAdapterAll(List<Tutorialdatum> _list, Context context,
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void gotoUrl(String url) {
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(url));
            context.startActivity(viewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isContainsQuora(List<Tutorialcategory> tutorialcategories) {
        boolean isCheck = false;
        for (int i = 0; i < tutorialcategories.size(); i++) {
            if (tutorialcategories.get(i).getName().equalsIgnoreCase("quora")) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
//        Tutorialdatum _obj = _list.get(i);

        int i = myViewHolder.getAdapterPosition();

        myViewHolder.setIsRecyclable(false);

        if (_list.get(i) != null) {

            myViewHolder.tv_category.setText(_list.get(i).getTitle());
            myViewHolder.tv_tutorial_name.setText(_list.get(i).getContent());

            if (!_list.get(i).getExternal().equalsIgnoreCase("")) {
                myViewHolder.iv_link.setVisibility(View.VISIBLE);
                myViewHolder.iv_link.setOnClickListener(v -> {
                    gotoUrl(_list.get(i).getExternal());
                });
            }else {
                myViewHolder.iv_link.setVisibility(View.GONE);
            }

            myViewHolder.tv_id.setText(_list.get(i).getId());

            String ytLink = _list.get(i).getYoutube_link();

//
//            if (IsYoutubeExits.get(i)){
//                myViewHolder.iv_yt.setVisibility(View.VISIBLE);
//
//            }else{
//                myViewHolder.iv_yt.setVisibility(View.INVISIBLE);
//
//            }

            if (!TextUtils.isEmpty(ytLink)) {
                Log.e("youtubes Linked", ytLink + " " + _list.get(i).getId());
                myViewHolder.iv_yt.setVisibility(View.VISIBLE);

            } else {
                Log.e("youtubes Linked", "Empty " + _list.get(i).getId());
                myViewHolder.iv_yt.setVisibility(View.INVISIBLE);

            }

            //   if (_obj.getTutorialimages().getThumbnailResized() != null && !_obj.getTutorialimages().getThumbnailResized().equalsIgnoreCase("false")) {
            if (_list.get(i).getTutorialimages().getThumbnail() != null && !_list.get(i).getTutorialimages().getThumbnail().equalsIgnoreCase("false")) {

                //String imageLink = _obj.getTutorialimages().getThumbnailResized();
                String imageLink = _list.get(i).getTutorialimages().getThumbnail();

                if (i == 0) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("key", imageLink);
                    clipboard.setPrimaryClip(clip);
                }

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

    public void RemoveItem(int pos) {
        _list.remove(pos);
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail, iv_yt, iv_link;
        public TextView tv_category;
        public LinearLayout ll_title;
        public TextView tv_tutorial_name;
        public TextView tv_id;
        public ImageView iv_more;
        public ConstraintLayout more_container;

        public MyViewHolder(View view) {
            super(view);
            ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
            iv_link = (ImageView) view.findViewById(R.id.iv_link);
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
                        _obj_interface.selectChildItemAll(_list.get(getLayoutPosition()), subCategoryName);
                    } else {
                        _obj_interface.selectItem(getLayoutPosition(), false);
                    }
                }
            });

            iv_more.setOnClickListener(v -> _obj_interface.onSubMenuClickAll(iv_more,
                    _list.get(getLayoutPosition()), getLayoutPosition()));
            more_container.setOnClickListener(v -> _obj_interface.onSubMenuClickAll(iv_more,
                    _list.get(getLayoutPosition()), getLayoutPosition()));
            tv_category.setOnClickListener(v -> _obj_interface.onSubMenuClickAll(iv_more,
                    _list.get(getLayoutPosition()), getLayoutPosition()));

        }
    }
}
