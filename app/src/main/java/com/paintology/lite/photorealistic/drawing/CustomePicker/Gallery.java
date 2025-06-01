package com.paintology.lite.photorealistic.drawing.CustomePicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.OpenGallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectionTitle;
    public static String title;
    public static int maxSelection;
    public static int maxVideoSelection = 1;
    public int mode;
    public static int MultiCrop = 114;
    ArrayList<String> mImageList;

    public static CurrentSelectionType Currentselected;

    FloatingActionButton btn_next;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageList = new ArrayList<>();
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OpenGallery.imagesSelected == null)
                    return;
                if (OpenGallery.imagesSelected.size() != 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putStringArrayListExtra("result", OpenGallery.imagesSelected);
                    returnIntent.putExtra("filetype", OpenGallery.isImageSelected);

                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else
                    Toast.makeText(Gallery.this, "Please select an image!", Toast.LENGTH_SHORT).show();
            }
        });

        title = getIntent().getExtras().getString("title");
        maxSelection = getIntent().getExtras().getInt("maxSelection");
        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE;
        mode = getIntent().getExtras().getInt("mode");
        setTitle(title);
        selectionTitle = 0;
        btn_next = (FloatingActionButton) findViewById(R.id.btn_next);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        OpenGallery.selected.clear();
        OpenGallery.imagesSelected.clear();


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra("isFromImport")) {
                    if (OpenGallery.imagesSelected == null)
                        return;
                    if (OpenGallery.imagesSelected.size() != 0) {
                        Intent returnIntent = new Intent();
                        returnIntent.putStringArrayListExtra("result", OpenGallery.imagesSelected);
                        returnIntent.putExtra("filetype", OpenGallery.isImageSelected);

                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else
                        Toast.makeText(Gallery.this, "Please select an image!", Toast.LENGTH_SHORT).show();
                } else {
                    if (OpenGallery.imagesSelected.size() != 0) {
                        returnResult(OpenGallery.imagesSelected);
                    } else {
                        Toast.makeText(Gallery.this, "Please select any image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (selectionTitle > 0) {
            setTitle(String.valueOf(selectionTitle) + " " + getResources().getString(R.string.image_selected));
        }
    }

    //This method set up the tab view for images and videos
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (mode == 1) {
            adapter.addFragment(new ImageFragment(), "PICK IMAGES FROM RESOURCES");
        }
        if (getIntent().hasExtra("isFromNewPost")) {
            btn_next.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
//            if (mode == 1)
//                adapter.addFragment(new VideoFragment(), "Videos");
        } else if (getIntent().hasExtra("isFromImport")) {
            btn_next.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        } else {
            btn_next.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void returnResult(ArrayList<String> mImageList) {
        Intent returnIntent = new Intent(this, PostActivity.class);
        returnIntent.putStringArrayListExtra("result", mImageList);
        returnIntent.putExtra("filetype", OpenGallery.isImageSelected);
//        setResult(RESULT_OK, returnIntent);
        startActivity(returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAGGG", "Clear Check onActivityResult >>> " + requestCode + "<>" + resultCode + " Candy > ");
        if (OpenGallery.imagesSelected.size() != 0) {
            btn_next.setVisibility(View.VISIBLE);
        } else {
            btn_next.setVisibility(View.GONE);
        }
//        if (resultCode == RESULT_OK) {
//            if (!OpenGallery.isImageSelected) {
//                Intent returnIntent = new Intent();
//                returnIntent.putStringArrayListExtra("result", OpenGallery.imagesSelected);
//                returnIntent.putExtra("filetype", OpenGallery.isImageSelected);
//                setResult(RESULT_OK, returnIntent);
//                finish();
//            }
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* private void cropImage(ArrayList<String> imagePathList) {
        String cropDirectory = getAppRootPath(this).getAbsolutePath();
//        String cropDirectory = Utilities.getCameraCacheDir(true, false).getAbsolutePath();


        Log.i("CropSample", "Save directory: " + cropDirectory);

        Durban.with(Gallery.this)
                .statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .toolBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryBlack))
                // Image path list/array.
                .inputImagePaths(imagePathList)
                // Image output directory.
                .outputDirectory(cropDirectory)
                // Image size limit.
                .maxWidthHeight(720, 720)
                // Aspect ratio.
                .aspectRatio(1, 1)
                // Output format: JPEG, PNG.
                .compressFormat(Durban.COMPRESS_JPEG)
                // Compress quality, see Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                .compressQuality(100)
                // Gesture: ROTATE, SCALE, ALL, NONE.
                .gesture(Durban.GESTURE_ALL)
                .controller(Controller.newBuilder()
                        .enable(false)
                        .rotation(true)
                        .rotationTitle(true)
                        .scale(true)
                        .scaleTitle(true)
                        .build())
                .requestCode(200)
                .start();
    }*/

    public File getAppRootPath(Context context) {
        if (sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    public boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().canWrite();
        } else
            return false;
    }

    public enum CurrentSelectionType {
        videoSelected, imageSelected;
    }
}