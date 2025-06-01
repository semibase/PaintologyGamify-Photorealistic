package com.paintology.lite.photorealistic.drawing.CustomePicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.OpenGallery;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ImageFragment extends Fragment {
    private RecyclerView recyclerView;
    private BucketsAdapter mAdapter;
    private final String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
    private final String[] projection2 = new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
    private List<bucketModel> bucketNames = new ArrayList<>();
    private List<String> bitmapList = new ArrayList<>();
    public static List<String> imagesList = new ArrayList<>();
    public static List<Boolean> selected = new ArrayList<>();
    //    private ProgressDialog progressDialog;
    private CircleProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bucket names reloaded
        bitmapList.clear();
        imagesList.clear();
        bucketNames.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);
        progressBar = v.findViewById(R.id.progress_bar);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress();
        populateRecyclerView();
        String readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        }

        int requestCode = 10;
        if (ContextCompat.checkSelfPermission(getContext(), readImagePermission) != PackageManager.PERMISSION_GRANTED) {
            //permission not granted
//                PermissionUtils.requestStoragePermission(getActivity(), 10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        requestCode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);
            } else {
                //below android 11
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);
            }
        } else {
            getPicBuckets();
        }
    }

    private void populateRecyclerView() {
        mAdapter = new BucketsAdapter(bucketNames, bitmapList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        /*recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());*/

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);


        int orientation = getResources().getConfiguration().orientation;

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (orientation == 1) {
            if (isTablet)
                mLayoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
            else
                mLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        } else {
            if (isTablet)
                mLayoutManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
            else
                mLayoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        }
//        mLayoutManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);

        //recyclerView = (AutoFitGridRecyclerView) _view.findViewById(R.id.recyclerview_channel);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    getPictures(bucketNames.get(position).getBucketNames());
                    Intent intent = new Intent(getContext(), OpenGallery.class);
                    intent.putExtra("FROM", "Images");
                    intent.putExtra("BucketName", bucketNames.get(position).getBucketNames());
                    startActivityForResult(intent, 101);
                } catch (Exception e) {

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public void getPicBuckets() {

        try {

//            if (!PermissionUtils.checkReadStoragePermission(getActivity())) {
//                // We don't have permission so prompt the user
//                PermissionUtils.requestStoragePermission(getActivity(), 1);
//                return;
//            }
//            int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                // We don't have permission so prompt the user
//
//                ActivityCompat.requestPermissions(
//                        getActivity(),
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        1
//                );
//                return;
//            }


            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showProgress();
                }

                @Override
                protected Void doInBackground(Void... voids) {

                    String selection = MediaStore.Images.Media.MIME_TYPE + "!=?";
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif");
                    String[] selectionArgsvideo = new String[]{mimeType};

                    Cursor cursor = getContext().getContentResolver()
                            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                                    selection, selectionArgsvideo, MediaStore.Images.Media.DATE_ADDED);
                    ArrayList<bucketModel> bucketNamesTEMP = new ArrayList<>(cursor.getCount());
                    ArrayList<String> bitmapListTEMP = new ArrayList<>(cursor.getCount());
                    HashSet<String> albumSet = new HashSet<>();
                    File file;
                    try {
                        if (cursor.moveToLast()) {
                            do {
                                if (Thread.interrupted() || cursor.isClosed()) {
//                                    hideProgress();
                                    return null;
                                }
                                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                                String image = cursor.getString(cursor.getColumnIndex(projection[1]));
                                file = new File(image);
                                if (file.exists() && !albumSet.contains(album)) {
                                    bucketModel _model = new bucketModel();

                                    _model.setBucketNames(album);
                                    _model.setBucketSize(cursor.getCount());

                                    bucketNames.add(_model);
                                    bitmapList.add(image);
                                    albumSet.add(album);
                                }
                            } while (cursor.moveToPrevious());
                        }
                    } catch (Exception e) {
                        Log.e("ImageFragment", e.getMessage());
                    } finally {
                        cursor.close();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    hideProgress();
                    mAdapter.notifyItemInserted(bucketNames.size() - 1);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }.execute();
        } catch (Exception e) {
            hideProgress();
        } finally {
            hideProgress();
        }
    }

    public void getPictures(String bucket) {
        try {
            selected.clear();
            Cursor cursor = getContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{bucket}, MediaStore.Images.Media.DATE_ADDED);
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
            imagesList.clear();
            imagesList.addAll(imagesTEMP);
        } catch (IllegalStateException e1) {

        } catch (Exception e) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ImageFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ImageFragment.ClickListener clickListener) {
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

    StringConstants constants = new StringConstants();

    /*This is the method where user can get confirmation about app permission, this method give the result of permission dialog and says that accepted or not*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            String readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                readImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
            }

            if (ContextCompat.checkSelfPermission(getContext(), readImagePermission) == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                getPicBuckets();
            }

        } else {
            try {
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    Toast.makeText(getContext(), getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                    FirebaseUtils.logEvents(getActivity(), constants.deny_storage_permission);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(getActivity(), constants.deny_storage_permission, Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    FirebaseUtils.logEvents(getActivity(), constants.allow_storage_permission);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(getActivity(), constants.allow_storage_permission, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Log.e(ImageFragment.class.getName(), e.getMessage());
            }
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

}



