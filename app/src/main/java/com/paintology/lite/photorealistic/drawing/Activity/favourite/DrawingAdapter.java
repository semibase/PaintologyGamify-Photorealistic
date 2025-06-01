package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import static com.paintology.lite.photorealistic.drawing.Activity.utils.ExtensionsKt.openActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingViewActivity;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.GalleryTutorailsFragment;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils;
import com.paintology.lite.photorealistic.drawing.Activity.utils.ExtensionsKt;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.events.RefreshFavoriteEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {
    private Context context;
    StringConstants constants = new StringConstants();
    private List<com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing> drawings;

    public DrawingAdapter(Context context, List<com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing> drawings) {
        this.context = context;
        this.drawings = drawings;
    }

    public static class DrawingViewHolder extends RecyclerView.ViewHolder {

        public ShapeableImageView imgThumbnail;
        public AppCompatTextView tvTutorialContent;
        public AppCompatTextView tvComments;
        public AppCompatButton btnDrawing;
        public AppCompatTextView tvLikes;
        public AppCompatTextView tvName;
        public me.zhanghai.android.materialratingbar.MaterialRatingBar RatingBar;
        public AppCompatImageView appCompatImageView7;
        public AppCompatImageView imgMenu12;
        // Add other views

        public DrawingViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgMenu12 = itemView.findViewById(R.id.imgMenu12);
            tvTutorialContent = itemView.findViewById(R.id.tvTutorialContent);
            btnDrawing = itemView.findViewById(R.id.btnDrawing);
            tvComments = itemView.findViewById(R.id.tvComments);
            RatingBar = itemView.findViewById(R.id.RatingBar);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvName = itemView.findViewById(R.id.tvName);
            appCompatImageView7 = itemView.findViewById(R.id.appCompatImageView7);

        }
    }

    @Override
    public DrawingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_gallery_tutorial_item, parent, false);
        return new DrawingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing drawing = drawings.get(position);

        holder.tvTutorialContent.setText(drawing.getDescription());

        holder.tvName.setText(drawing.getAuthor().getName());
        holder.btnDrawing.setText(drawing.getTitle());

        holder.imgMenu12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMenu(holder.getAdapterPosition(), v);

            }
        });

        holder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("post_id", drawings.get(position).getId());
                ContextKt.sendUserEventWithParam(context, StringConstants.favorites_gallery_open, bundle);
                ExtensionsKt.startNewDrawingActivity(context, drawings.get(position), DrawingViewActivity.class, false);
            }
        });

//        holder.RatingBar.setNumStars(drawing.getStatistic().getRatings().intValue());

        holder.tvLikes.setText(String.valueOf(drawing.getStatistic().getLikes().intValue()));
        holder.tvComments.setText(String.valueOf(drawing.getStatistic().getComments().intValue()));
//        holder.appCompatImageView7.setText(drawing.getStatistic().getComments());

        Glide.with(context)
                .load(drawing.getImages().getContent()) // image url
                .placeholder(R.drawable.img_cat_dummy) // any placeholder to load at start// any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.imgThumbnail);

        // Set other data
    }

    @Override
    public int getItemCount() {
        return drawings.size();
    }

    public void setDrawings(List<com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing> drawings) {
        this.drawings = drawings;
        notifyDataSetChanged();
    }

    private void ShowMenu(int position, View view) {
        PopupMenu popup = new PopupMenu(context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.gallery_tut_fav_remove_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.openItem1) {
                    ExtensionsKt.startNewDrawingActivity(context, drawings.get(position), DrawingViewActivity.class, false);
                    return true;
                } else if (item.getItemId() == R.id.doTutorialItem1) {

                    if (drawings.get(position).getMetadata().getTutorialId().isEmpty()) {
                        openActivity(context, CategoryActivity.class);
                    } else {
                        FireUtils.showProgressDialog(
                                context,
                                context.getResources().getString(R.string.please_wait)
                        );
                        new TutorialUtils(context).parseTutorial(drawings.get(position).getMetadata().getTutorialId());
                    }

                    return true;
                } else if (item.getItemId() == R.id.shareItem11) {
                    GalleryTutorailsFragment fragment = new GalleryTutorailsFragment();
                    fragment.loadImageAndSave(drawings.get(position).getImages().getContent(), context, drawings.get(position));
                    return true;
                } else if (item.getItemId() == R.id.RemoveItem1) {

                    DrawingRepository repository = new DrawingRepository(context);
                    repository.RemoveDrawing(drawings.get(position).getId());
                    drawings.remove(position);
                    notifyItemRemoved(position);
                    EventBus.getDefault().post(new RefreshFavoriteEvent(1));
                    return true;
                }
                return false;

            }
        });

        popup.show();
    }


}
