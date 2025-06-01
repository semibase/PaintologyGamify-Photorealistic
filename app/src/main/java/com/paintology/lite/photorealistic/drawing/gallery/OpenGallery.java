package com.paintology.lite.photorealistic.drawing.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.CustomePicker.AutofitRecyclerView;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.CustomePicker.ImageFragment;
import com.paintology.lite.photorealistic.drawing.CustomePicker.MediaAdapter;
import com.paintology.lite.photorealistic.drawing.Fragment.VideoFragment;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OpenGallery extends AppCompatActivity {
    private AutofitRecyclerView recyclerView;
    private MediaAdapter mAdapter;
    private List<String> mediaList = new ArrayList<>();
    public static List<Boolean> selected = new ArrayList<>();
    public static ArrayList<String> imagesSelected = new ArrayList<>();

    public static String parent;
    public static boolean isImageSelected = true;
    private String bucketName;
    private CircleProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_open_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAGGG", "Click to finish");
                if (imagesSelected.size() != 0) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(OpenGallery.this, "Please select an image!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //toolbar.setNavigationIcon(R.drawable.arrow_back);
        setTitle(Gallery.title);
        if (imagesSelected.size() > 0) {
            setTitle(String.valueOf(imagesSelected.size()) + " " + getResources().getString(R.string.image_selected));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        parent = getIntent().getExtras().getString("FROM");
        bucketName = getIntent().getExtras().getString("BucketName");
        setTitle(bucketName);
        mediaList.clear();
        selected.clear();
        if (parent.equals("Images")) {
            isImageSelected = true;
            mediaList.addAll(ImageFragment.imagesList);
            selected.addAll(ImageFragment.selected);

        } else {
            isImageSelected = false;
            mediaList.addAll(VideoFragment.videosList);
            selected.addAll(VideoFragment.selected);

        }
        populateRecyclerView();

        if (isImageSelected) {
            if (Gallery.Currentselected == Gallery.CurrentSelectionType.videoSelected) {
                for (int i = 0; i < selected.size(); i++) {
                    selected.set(i, false);
                }
                imagesSelected.clear();
            }
        } else {
            if (Gallery.Currentselected == Gallery.CurrentSelectionType.imageSelected) {
                for (int i = 0; i < selected.size(); i++) {
                    selected.set(i, false);
                }
                imagesSelected.clear();
            }
        }
        try {
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    private void populateRecyclerView() {
        try {
            if (selected == null || mediaList == null)
                return;

            showProgress();
            for (int i = 0; i < selected.size(); i++) {
                if (imagesSelected.contains(mediaList.get(i))) {
                    selected.set(i, true);
                } else {
                    selected.set(i, false);
                }
            }
            ArrayList<AlbumImage> _list = new ArrayList<>();
            for (int i = 0; i < mediaList.size(); i++) {
                AlbumImage obj = new AlbumImage();
                obj.setFilePath(mediaList.get(i));
                String filename = new File(mediaList.get(i)).getName();
                obj.setFileName(filename);
                _list.add(obj);
            }
            mAdapter = new MediaAdapter(_list, selected, getApplicationContext());
        /*RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getItemAnimator().setChangeDuration(0);*/
            hideProgress();

            RecyclerView.LayoutManager mLayoutManager;
            int orientation = getResources().getConfiguration().orientation;
            boolean isTablet = getResources().getBoolean(R.bool.isTablet);
            if (orientation == 1) {
                if (isTablet)
                    mLayoutManager = new GridLayoutManager(OpenGallery.this, 3, RecyclerView.VERTICAL, false);
                else
                    mLayoutManager = new GridLayoutManager(OpenGallery.this, 2, RecyclerView.VERTICAL, false);
            } else {
                if (isTablet)
                    mLayoutManager = new GridLayoutManager(OpenGallery.this, 4, RecyclerView.VERTICAL, false);
                else
                    mLayoutManager = new GridLayoutManager(OpenGallery.this, 3, RecyclerView.VERTICAL, false);
            }

            //recyclerView = (AutoFitGridRecyclerView) _view.findViewById(R.id.recyclerview_channel);
//        LayoutManager = new GridLayoutManager(getApplicationContext(), colmn, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new MarginDecoration(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (parent.equals("Videos")) {
                        try {
                            if (!selected.get(position).equals(true) && imagesSelected.size() < Gallery.maxVideoSelection) {
                                imagesSelected.add(mediaList.get(position));
                                selected.set(position, !selected.get(position));
                                mAdapter.notifyItemChanged(position);
                            } else if (selected.get(position).equals(true)) {
                                if (imagesSelected.indexOf(mediaList.get(position)) != -1) {
                                    imagesSelected.remove(imagesSelected.indexOf(mediaList.get(position)));
                                    selected.set(position, !selected.get(position));
                                    mAdapter.notifyItemChanged(position);
                                }
                            } else
                                Toast.makeText(OpenGallery.this, "Not allowed!", Toast.LENGTH_SHORT).show();

                            Gallery.Currentselected = Gallery.CurrentSelectionType.videoSelected;
                        } catch (Exception e) {

                        }
                    } else {
                        try {
                            if (!selected.get(position).equals(true) && imagesSelected.size() < Gallery.maxSelection) {
                                imagesSelected.add(mediaList.get(position));
                                selected.set(position, !selected.get(position));
                                mAdapter.notifyItemChanged(position);
                            } else if (selected.get(position).equals(true)) {
                                if (imagesSelected.indexOf(mediaList.get(position)) != -1) {
                                    imagesSelected.remove(imagesSelected.indexOf(mediaList.get(position)));
                                    selected.set(position, !selected.get(position));
                                    mAdapter.notifyItemChanged(position);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Can't share more then 10 images.", Toast.LENGTH_LONG).show();
                            }
                            Gallery.Currentselected = Gallery.CurrentSelectionType.imageSelected;
                        } catch (Exception e) {

                        }
                    }
                    try {
                        Gallery.selectionTitle = imagesSelected.size();
                        if (imagesSelected.size() != 0) {
                            setTitle(String.format(bucketName + "(%s selected)", imagesSelected.size()));
                        } else {
                            setTitle(bucketName);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at populate " + e.getMessage());
            hideProgress();
        }

    }

    void showProgress() {
        try {
            progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("ImageFragment", e.getMessage());
        }
    }

    void hideProgress() {
        try {
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("ImageFragment", e.getMessage());
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private OpenGallery.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OpenGallery.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            try {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                    clickListener.onClick(child, rv.getChildPosition(child));
                }
            } catch (Exception e1) {

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    class MarginDecoration extends RecyclerView.ItemDecoration {
        private int margin;

        public MarginDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin_feed);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }
    }

}

