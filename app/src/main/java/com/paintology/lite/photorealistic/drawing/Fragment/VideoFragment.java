package com.paintology.lite.photorealistic.drawing.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.CustomePicker.BucketsAdapter;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.CustomePicker.MarginDecoration;
import com.paintology.lite.photorealistic.drawing.CustomePicker.bucketModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.OpenGallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class VideoFragment extends Fragment {
    private RecyclerView recyclerView;
    private BucketsAdapter mAdapter;
    private List<bucketModel> bucketNames = new ArrayList<>();
    private List<String> bitmapList = new ArrayList<>();
    private final String[] projection = new String[]{MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA};
    private final String[] projection2 = new String[]{MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA};
    public static List<String> videosList = new ArrayList<>();
    public static List<Boolean> selected = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bucket names reloaded
        bucketNames.clear();
        bitmapList.clear();
        videosList.clear();
        getVideoBuckets();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        populateRecyclerView();
        return v;
    }

    private void populateRecyclerView() {
        mAdapter = new BucketsAdapter(bucketNames, bitmapList, getContext());
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        int colmn = 2;
        if (tabletSize) {
            colmn = 3;
        } else {
            colmn = 2;
        }
      /*  RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), colmn);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/

        mLayoutManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
        //recyclerView = (AutoFitGridRecyclerView) _view.findViewById(R.id.recyclerview_channel);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
      //  recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                getVideos(bucketNames.get(position).getBucketNames());
                Intent intent = new Intent(getContext(), OpenGallery.class);
                intent.putExtra("FROM", "Videos");
                Gallery.maxVideoSelection = 5;
                startActivityForResult(intent, 102);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mAdapter.notifyDataSetChanged();
    }

    public void getVideos(String bucket) {
        selected.clear();

        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp4");
        String[] selectionArgsvideo = new String[]{mimeType};

        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " =? AND " + selection, new String[]{bucket, mimeType}, MediaStore.Video.Media.DATE_ADDED);
        ArrayList<String> imagesTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String path = cursor.getString(cursor.getColumnIndex(projection2[1]));
                file = new File(path);
                if (file.exists() && !albumSet.contains(path)) {
                    imagesTEMP.add(path);
                    albumSet.add(path);
                    selected.add(false);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (imagesTEMP == null) {

            imagesTEMP = new ArrayList<>();
        }
        videosList.clear();
        videosList.addAll(imagesTEMP);
    }

    public void getVideoBuckets() {
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Video.Media.DATE_ADDED);
        ArrayList<bucketModel> bucketNamesTEMP = new ArrayList<>(cursor.getCount());
        ArrayList<String> bitmapListTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                String image = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(image);
                if (file.exists() && !albumSet.contains(album)) {
                    bucketModel _model = new bucketModel();
                    _model.setBucketNames(album);
                    _model.setBucketSize(cursor.getCount());
                    bucketNamesTEMP.add(_model);
                    bitmapListTEMP.add(image);
                    albumSet.add(album);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (bucketNamesTEMP == null) {
            bucketNames = new ArrayList<>();
        }
        bucketNames.clear();
        bitmapList.clear();

        bucketNames.addAll(bucketNamesTEMP);
        bitmapList.addAll(bitmapListTEMP);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private VideoFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final VideoFragment.ClickListener clickListener) {
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
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
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
}