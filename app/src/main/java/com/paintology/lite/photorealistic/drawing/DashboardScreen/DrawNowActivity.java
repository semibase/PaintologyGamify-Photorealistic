package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity;
import com.paintology.lite.photorealistic.drawing.Activity.MyConstantsKt;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.BannerCallBack;
import com.paintology.lite.photorealistic.drawing.ads.enums.NativeType;
import com.paintology.lite.photorealistic.drawing.camerax.CameraActivity;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBannerDrawBinding;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtil;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DrawNowActivity extends BaseActivity {


    String target_name = "";
    StringConstants constants = new StringConstants();
    private int SELECT_BACKGROUND_COLOR_REQUEST = 300;
    Intent mlIntent;
    Uri image;
    String mCameraFileName;
    private int SELECT_PHOTO_REQUEST = 400;
    public int CAMERA_REQUEST = 150;
    public int PICK_IMAGE_CAMERA = 151;

    private String currentPhotoPath;


    private File output = null;
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";
    private final String PHOTOS = "photos";
    private final String FILENAME = System.currentTimeMillis() + "_img.jpeg";

    //    ImageView iv_own_adv, iv_own_adv_;
    //    InMobiBanner mBannerAd;


    ApiInterface apiInterface;
    ImageView draw_banner1;
    ImageView draw_banner2;
    private boolean showExitDialog;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String _local = constants.getString(constants.selected_language, DrawNowActivity.this);
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        FacebookSdk.sdkInitialize(this);
//        InMobiSdk.init(this, "3c3c1201fbf94be0ba072798ba99ad57");
        setContentView(R.layout.activity_draw_now);


        frameLayout = findViewById(R.id.ads_place_holdersdraw);
        if (BuildConfig.DEBUG) {
            getDiComponent().getAdmobNativeAds().loadNativeAds(this,
                    frameLayout,
                    "ca-app-pub-3940256099942544/2247696110",
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeCommunity(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    NativeType.CUSTOM_DOWN,
                    new BannerCallBack() {
                        @Override
                        public void onAdCloseFullScreenNative() {

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull String adError) {

                        }

                        @Override
                        public void onAdLoaded() {

                        }

                        @Override
                        public void onAdImpression() {

                        }

                        @Override
                        public void onPreloaded() {

                        }

                        @Override
                        public void onAdClicked() {

                        }

                        @Override
                        public void onAdClosed() {

                        }

                        @Override
                        public void onAdOpened() {

                        }

                        @Override
                        public void onAdSwipeGestureClicked() {

                        }
                    });
        } else {
            getDiComponent().getAdmobNativeAds().loadNativeAds(this,
                    frameLayout,
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeID(),
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeCommunity(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    NativeType.CUSTOM_DOWN,
                    new BannerCallBack() {
                        @Override
                        public void onAdCloseFullScreenNative() {

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull String adError) {

                        }

                        @Override
                        public void onAdLoaded() {

                        }

                        @Override
                        public void onAdImpression() {

                        }

                        @Override
                        public void onPreloaded() {

                        }

                        @Override
                        public void onAdClicked() {

                        }

                        @Override
                        public void onAdClosed() {

                        }

                        @Override
                        public void onAdOpened() {

                        }

                        @Override
                        public void onAdSwipeGestureClicked() {

                        }
                    });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.draw));

        MyConstantsKt.checkForIntroVideo(this, StringConstants.intro_draw);


        Intent intent = getIntent();
        showExitDialog = intent.getBooleanExtra("showExitDialog", false);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);



        ArrayList<BannerModel> data = AppUtils.getDrawBanners(this);
        for (int i = 0; i < data.size(); i++) {
            LayoutBannerDrawBinding bannerBinding = LayoutBannerDrawBinding.inflate(getLayoutInflater());
            Picasso.get().load(Uri.parse(data.get(i).bannerImageUrl)).into(bannerBinding.ivOwnAdv);
            int finalI = i;
            bannerBinding.ivOwnAdv.setOnClickListener(v -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(DrawNowActivity.this,
                                constants.ad_XX_draw_banner_click.replace("XX", String.valueOf(finalI)),
                                Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(
                            DrawNowActivity.this,
                            constants.ad_XX_draw_banner_click.replace("XX", String.valueOf(finalI))
                    );
                    KGlobal.openInBrowser(DrawNowActivity.this, data.get(finalI).bannerLInk);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            });

            ((LinearLayout) findViewById(R.id.llAds)).addView(bannerBinding.getRoot());
        }

        if (getIntent() != null && getIntent().hasExtra("target_name")) {
            target_name = getIntent().getStringExtra("target_name");

            if (target_name != null) {
                switch (target_name) {
                    case "trace":
                        pickImageFromGallery(findViewById(R.id.ll_parent));
                        break;
                    case "overlay":
                        pickImageFromGallery(findViewById(R.id.iv_overlay_image));
                        break;
                    case "camera":
                        showDialogForCamera();
                        break;
                    case "camera trace":
                    case "camera overlay":
                        pickFromCamera(target_name);
                        break;
                }
            }
        }

//        try {
//            String iv_1_url = constants.getString("dating_image_url", this);
//            Log.e("TAGGG", "FB URL check " + iv_1_url);
//            //Glide.with(DrawNowActivity.this).load(iv_1_url).into(iv_own_adv).onLoadStarted(getResources().getDrawable(R.drawable.jumpdates_featured));
//
//           /* Glide.with(DrawNowActivity.this)
//                    .load(iv_1_url)
//                    .apply(new RequestOptions().placeholder(R.drawable.jumpdates_featured).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                    .into(iv_own_adv);*/
//
//            iv_own_adv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        if (BuildConfig.DEBUG) {
//                            Toast.makeText(DrawNowActivity.this, constants.drawpage_banner_paintology_youtube, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.drawpage_banner_paintology_youtube);
//
//                        String url_1 = "https://www.youtube.com/channel/UCrR1Ya_KHuHyudP48FiR99A";
////                        String url_1 = "https://www.amazon.com/gp/product/B084GRP9CR";
////                        String url_1 = "https://paintology.com/my-account/?utm_source=app&utm_medium=banner_image&utm_campaign=app_banner_click";
//                        /*Intent browesIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_1));
//                        startActivity(browesIntent);*/
//                        KGlobal.openInBrowser(DrawNowActivity.this, url_1);
//                    } catch (ActivityNotFoundException e) {
//                        Log.e("TAGGG", "Exception at view " + e.getMessage());
//                    } catch (Exception e) {
//                        Log.e("TAGG", "Exception " + e.getMessage());
//                    }
//                }
//            });
//        } catch (Exception e) {
//
//        }

//        iv_own_adv_.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(DrawNowActivity.this, constants.drawpage_banner_ferdouse_youtube, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(DrawNowActivity.this, constants.drawpage_banner_ferdouse_youtube);
////                startActivity(new Intent(DrawNowActivity.this, Community.class));
////                finish();
//
//                try {
//
//                    String url_1 = "https://www.youtube.com/c/Ferdouse";
//                    KGlobal.openInBrowser(DrawNowActivity.this, url_1);
//
//                } catch (ActivityNotFoundException e) {
//                    Log.e("TAGGG", "Exception at view " + e.getMessage());
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception " + e.getMessage());
//                }
//            }
//        });


//        findViewById(R.id.iv_udemy_ad).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    String url_1 = "https://www.udemy.com/courses/search/?p=2&q=paintology";
//                    KGlobal.openInBrowser(DrawNowActivity.this, url_1);
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(DrawNowActivity.this, StringConstants.drawpage_banner_udemy, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(DrawNowActivity.this, StringConstants.drawpage_banner_udemy);
//                } catch (ActivityNotFoundException e) {
//                    Log.e("TAGGG", "Exception at view " + e.getMessage());
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception " + e.getMessage());
//                }
//            }
//        });

//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

//        loadFbRectBanner();
//        loadAdmobRectangeAdd();
    }


//    private void loadFbRectBanner() {
//        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
//        com.facebook.ads.AdView fAdView = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_banner_rect_unit), AdSize.RECTANGLE_HEIGHT_250);
//        // Add the ad view to your activity layout
//        adContainer.addView(fAdView);
//        com.facebook.ads.AdListener adListener = new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Ad error callback
//                Toast.makeText(
//                        DrawNowActivity.this,
//                        "Error: " + adError.getErrorMessage(),
//                        Toast.LENGTH_LONG)
//                        .show();
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Ad loaded callback
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//            }
//        };
//
//        // Request an ad
//        fAdView.loadAd(fAdView.buildLoadAdConfig().withAdListener(adListener).build());
//    }
//
//    void loadAdmobRectangeAdd() {
//
//        com.google.android.gms.ads.AdView mAdView = findViewById(R.id.adView_rectangle);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//    }


    public int REQ_BLANK_CANVAS = 231;

    /*This method will redirect user to the canvas screen where user can draw their arts.
     * This method will called when user click on square image in dashboard screen.*/
    public void beginDoodle(View v) {
//        if (!PermissionUtils.checkStoragePermission(DrawNowActivity.this)) {
//            // We don't have permission so prompt the user
//            PermissionUtils.requestStoragePermission(DrawNowActivity.this, REQ_BLANK_CANVAS);
//            return;
//        }

//        int permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//
//            ActivityCompat.requestPermissions(
//                    DrawNowActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQ_BLANK_CANVAS
//            );
//            return;
//        }

        ContextKt.setSharedNo(this);
        int mPrefBackgroundColor = -1;
        Intent lIntent1 = new Intent();
        String str = "New Paint";
        lIntent1.setAction(str);
        if (BuildConfig.DEBUG) {
            Toast.makeText(DrawNowActivity.this, constants.draw_screen_blank_canvas, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(DrawNowActivity.this, constants.draw_screen_blank_canvas);
        constants.putInt("background_color", mPrefBackgroundColor, DrawNowActivity.this);
        lIntent1.putExtra("background_color", mPrefBackgroundColor);
        mlIntent = lIntent1.setClass(this, PaintActivity.class);
        startActivity(lIntent1);
    }

    //Allows user to pick image from device local storage.
    public void pickImageFromGallery(View view) {


        if (view.getId() == R.id.iv_overlay_image) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(DrawNowActivity.this, constants.draw_screen_overlay_image, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.draw_screen_overlay_image);
            SELECT_PHOTO_REQUEST = 100;
        } else {
            if (BuildConfig.DEBUG) {
                Toast.makeText(DrawNowActivity.this, constants.draw_screen_trace_image, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.draw_screen_trace_image);
            SELECT_PHOTO_REQUEST = 400;
        }

        boolean isStoragePassed = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // android 13 and above
            if (ContextCompat.checkSelfPermission(
                    DrawNowActivity.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED
            ) {
                isStoragePassed = true;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android 11 and above
            if (ContextCompat.checkSelfPermission(
                    DrawNowActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
            ) {
                isStoragePassed = true;
            }
        } else if (!PermissionUtils.checkStoragePermission(DrawNowActivity.this)) {
            // We don't have permission so prompt the user
            PermissionUtils.requestStoragePermission(DrawNowActivity.this, 1);
            return;
        }

        if (!isStoragePassed) {
            PermissionUtils.requestStoragePermission(DrawNowActivity.this, 1);
            return;
        }

//        if (!PermissionUtils.checkCameraPermission(DrawNowActivity.this)) {
//            // We don't have permission so prompt the user
//            PermissionUtils.requestCameraPermission(DrawNowActivity.this, 2);
//            return;
//        }


        try {

//            if (!PermissionUtils.checkReadStoragePermission(DrawNowActivity.this)) {
//                // We don't have permission so prompt the user
//                PermissionUtils.requestStoragePermission(DrawNowActivity.this, SELECT_PHOTO_REQUEST);
//                return;
//            }

//            int permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                // We don't have permission so prompt the user
//                ActivityCompat.requestPermissions(
//                        DrawNowActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        1
//                );
//                return;
//            }

            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO_REQUEST);


        } catch (Exception e) {
            Log.e(DrawNowActivity.class.getName(), e.getMessage());
        }
    }

    private String saveImageToStorage(Bitmap bitmap) {
        File directory = getApplicationContext().getFilesDir();
        File imagePath = new File(directory, "image.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return imagePath.getAbsolutePath();
    }

    private static String getParentFolderPath(String imagePath) {
        File imageFile = new File(imagePath);
        File parentFolder = imageFile.getParentFile();
        if (parentFolder != null) {
            return parentFolder.getAbsolutePath();
        } else {
            return null; // Image path doesn't have a parent folder
        }
    }

    private static String getImageName(String imagePath) {
        File imageFile = new File(imagePath);
        return imageFile.getName();
    }

    // Function to rotate the bitmap image
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private Bitmap handleImageOrientation(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /*This method will call once user pick image from their local storgae, when user pick color from color picker,call once user logged in via
  google and facebook and the result accordingle.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAGGG", "OnActivity Result Call DrawScreen > " + requestCode + " Candy " + (resultCode == RESULT_OK && requestCode == 111));
        if (resultCode != RESULT_CANCELED) {
            boolean isFromOverraid = false;


            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            /*Bitmap photo = (Bitmap) data.getExtras().get("data");
            String imagePath;
            if (Build.VERSION.SDK_INT <= 24) {
                Bitmap rotatedPhoto = rotateImage(photo, 90);
                imagePath = saveImageToStorage(rotatedPhoto);
            } else {
                imagePath = saveImageToStorage(photo);
            }*/

//            Bitmap photo = (Bitmap) data.getExtras().get("data");

                if (currentPhotoPath != null) {
                    try {
                        File imgFile = new File(currentPhotoPath);
                        if (imgFile.exists()) {
                            String imagePath = imgFile.getAbsolutePath(); // Get the absolute path
                            if (CAMERA_OPERATION == 1) {
                                ContextKt.setSharedNo(this);
                                Intent intent = new Intent(this, PaintActivity.class);
                                intent.setAction("LoadWithoutTraceFromCamera");
                                intent.putExtra("path", getImageName(imagePath));
                                intent.putExtra("ParentFolderPath", getParentFolderPath(imagePath));
                                intent.putExtra("isPickFromOverlaid", false);
                                startActivity(intent);
                            } else if (CAMERA_OPERATION == 2) {
                                startDoodle(imagePath, false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


//            MediaStore.Images.Media.insertImage(getContentResolver(), photo, "ourTitle" , "yourDescription");
//
//            String imagePath;
//            if (Build.VERSION.SDK_INT <= 24) {
//                Bitmap rotatedPhoto = rotateImage(photo, 90);
//                imagePath = saveImageToStorage(rotatedPhoto);
//            } else {
//
//                imagePath = saveImageToStorage(photo);
//                Bitmap bitmapWithCorrectOrientation = handleImageOrientation(photo, imagePath);
//                imagePath = saveImageToStorage(bitmapWithCorrectOrientation);
//            }


            }


            if (requestCode == PICK_IMAGE_CAMERA) {
//            Uri selectedImage = data.getData();


                if (CAMERA_OPERATION == 1) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY);

                    try {

                        Uri savedUri = data.getData();
                        File destination = new File(savedUri.getPath());


                        ContextKt.setSharedNo(this);

                        String imgPath = destination.getAbsolutePath();

                        Intent intent = new Intent(DrawNowActivity.this, PaintActivity.class);
                        intent.setAction("LoadWithoutTraceFromCamera");
                        intent.putExtra("path", destination.getName());
                        intent.putExtra("ParentFolderPath", destination.getParentFile().getAbsolutePath());
                        intent.putExtra("isPickFromOverlaid", true);
                        startActivity(intent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                File file = new File(selectedImage.getPath());
//
//                Intent intent = new Intent(DrawNowActivity.this, Paintor.class);
//                intent.setAction("LoadWithoutTraceFromCamera");
//                intent.putExtra("path", file.getName());
//                intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
//                intent.putExtra("isPickFromOverlaid", true);
//                startActivity(intent);
                } else if (CAMERA_OPERATION == 2) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_TRACE, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_TRACE);

                    try {

                        Uri savedUri = data.getData();
                        File destination = new File(savedUri.getPath());

//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//
//                    Log.e("Activity", "Pick from Camera::>>> ");
//
//                    String rootFolderPath = KGlobal.getDrawImageFolderPath(DrawNowActivity.this);
//
//                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
////                    File destination = new File(rootFolderPath + "/" +
////                            getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
//
//                    File destination = new File(rootFolderPath, "IMG_" + timeStamp + ".jpg");
//                    FileOutputStream fo;
//                    try {
//                        destination.createNewFile();
//                        fo = new FileOutputStream(destination);
//                        fo.write(bytes.toByteArray());
//                        fo.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                        String imgPath = destination.getAbsolutePath();

                        startDoodle(imgPath, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                startDoodle(selectedImage.getPath(), false);
                }
            } else if (requestCode == 100)
                isFromOverraid = true;
            try {
                if (requestCode == 400) {
                    if (data == null)
                        return;

                    try {
                        drawTraceRewardPoint();

                        Uri imageUri = data.getData();
                        String str14 = getPath(imageUri);
                        if (str14 == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DrawNowActivity.this);
                            builder.setTitle("Can't Load");
                            builder.setMessage("Selected image is not on your local storage, please download and pick image from there.");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return;
                        }
                        ArrayList<TraceReference> traceListFromPref = listFromPreference();
                        String selectedFileName = new File(str14).getName();

                        if (traceListFromPref != null) {
                            for (int i = 0; i < traceListFromPref.size(); i++) {
                                Log.e("TAGGG", "Both Name selectedFileName " + selectedFileName + " " + traceListFromPref.get(i).getTraceImageName());
                                if (str14.equalsIgnoreCase(traceListFromPref.get(i).getTraceImageName())) {
//                                File file = new File(Environment.getExternalStorageDirectory(), "/Paintology/" + selectedFileName);
                                    File file = new File(str14);
                                    showAlertDialog(traceListFromPref.get(i).getUserPaintingName(), file.getAbsolutePath());
                                    return;
                                }
                            }
                            startDoodle(str14, isFromOverraid);
                        } else {
                            startDoodle(str14, isFromOverraid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == SELECT_BACKGROUND_COLOR_REQUEST) {
                    try {
                        ContextKt.setSharedNo(this);
                        int mPrefBackgroundColor = data.getIntExtra("color-selected", -65536);
                        Intent lIntent1 = new Intent();
                        String str = "New Paint";
                        lIntent1.setAction(str);
                        constants.putInt("background_color", mPrefBackgroundColor, DrawNowActivity.this);
                        lIntent1.putExtra("background_color", mPrefBackgroundColor);
                        lIntent1.putExtra("isFromDrawScreen", true);
                        mlIntent = lIntent1.setClass(this, PaintActivity.class);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, constants.getPICK_NEW_CANVAS(), Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(this, constants.getPICK_NEW_CANVAS());
                        startActivityForResult(lIntent1, 111);
                    } catch (Exception e) {

                    }
                } else if (requestCode == 100) {
                    try {

                        ContextKt.setSharedNo(this);
                        drawOverlayRewardPoint();

                        Uri imageUri = data.getData();
                        String str14 = getPath(imageUri);

                        File file = new File(str14);
                        Intent intent = new Intent(this, PaintActivity.class);
                        intent.setAction("LoadWithoutTrace");
                        intent.putExtra("path", file.getName());
                        intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                        intent.putExtra("isPickFromOverlaid", true);
                        startActivityForResult(intent, 111);
                    } catch (Exception e) {

                    }
                } else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
                    try {
//                    Uri selectedImage = FileProvider.getUriForFile(this, AUTHORITY, output);
//                    String path = getRealPath(selectedImage);
//                    Log.e("TAGG", "Path of CameraImage " + path + " selectedImage " + selectedImage);
//                    File file = new File(path);

                  /*  Intent intent = new Intent(this, Paintor.class);
                    intent.setAction("LoadWithoutTraceFromCamera");
                    intent.putExtra("path", output.getName());
                    intent.putExtra("ParentFolderPath", output.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivityForResult(intent, 111);*/

//                    showDialogForCamera();

                        drawCameraRewardPoint();

                        try {

                            File _file = new File(output.getAbsolutePath());
                            if (CAMERA_OPERATION == 1) {
                                ContextKt.setSharedNo(this);
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY);
                                Intent intent = new Intent(DrawNowActivity.this, PaintActivity.class);
                                intent.setAction("LoadWithoutTraceFromCamera");
                                intent.putExtra("path", _file.getName());
                                intent.putExtra("ParentFolderPath", _file.getParentFile().getAbsolutePath());
                                intent.putExtra("isPickFromOverlaid", true);
                                startActivity(intent);
                            } else if (CAMERA_OPERATION == 2) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_TRACE, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(DrawNowActivity.this, constants.DIALOG_PICK_CAMERA_TRACE);
                                startDoodle(_file.getAbsolutePath(), false);
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            Log.e("TAG", "File Name Of Camera " + output.getParentFile().getAbsolutePath());
                        } catch (Exception e) {
                            Log.e("TAG", "Exception at open " + e.getMessage());
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Error Got Here > " + e.getMessage(), e);
                        Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
//                    addCamera();
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_OK && requestCode == 111) {
                    try {
                        if (data.hasExtra("actions")) {
                            String _str_action = data.getStringExtra("actions");
                            Log.e("TAGGG", "Error Got Here RETURN > " + _str_action);
                            if (_str_action.equals("New Paint")) {
                        /*Intent lIntent1 = new Intent();
                        lIntent1.setClass(this, ColorPickerActivity.class);
                        lIntent1.putExtra("for_brush", false);
                        lIntent1.putExtra("current_color", constants.getInt("background_color", DrawNowActivity.this));
                        startActivityForResult(lIntent1, SELECT_BACKGROUND_COLOR_REQUEST);*/

                                ContextKt.setSharedNo(this);
                                int mPrefBackgroundColor = -1;
                                Intent lIntent1 = new Intent();
                                String str = "New Paint";
                                lIntent1.setAction(str);
                                constants.putInt("background_color", mPrefBackgroundColor, DrawNowActivity.this);
                                lIntent1.putExtra("background_color", mPrefBackgroundColor);
                                mlIntent = lIntent1.setClass(this, PaintActivity.class);
//                    FirebaseUtils.logEvents(this,constants.getPICK_NEW_CANVAS());
//        FirebaseUtils.logEvents(this, constants.getPICK_NEW_CANVAS());

                                startActivity(lIntent1);
                            } else if (_str_action.equalsIgnoreCase("LoadWithoutTrace")) {
                                LinearLayout _img_view = findViewById(R.id.iv_overlay_image);
                                pickImageFromGallery(_img_view);
                            } else if (_str_action.equals("Edit Paint")) {
                                LinearLayout _img_view = findViewById(R.id.iv_trace_image);
                                pickImageFromGallery(_img_view);

                            } else if (_str_action.equals("LoadWithoutTraceFromCamera")) {
                                LinearLayout _img_view = findViewById(R.id.iv_camera_icon);
                                captureImage(_img_view);

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

                Log.e("TAGGG", "OnActivity Result Call Exception " + e.getMessage(), e);
            }
        }
    }


    /*This method will get real path of sected image from the system generated path*/
    private String getPath(Uri uri) {
        String[] projection;
        Cursor cursor;
        int column_index;
        projection = new String[]{MediaStore.Images.Media.DATA};
        cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String path = cursor.getString(column_index);

//        cursor.close();
        return path;
    }

    /*This method will give all the values from the app preference*/
    public ArrayList<TraceReference> listFromPreference() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");
        String overlayJson = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();
        ArrayList<TraceReference> traceList = gson.fromJson(json, type);
        if (traceList == null) {
            traceList = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(overlayJson)) {
            traceList.addAll(gson.fromJson(overlayJson, type));
        }
        return traceList;
    }

    /*This method will redirect user to canvas screen that allows user other feature and draw their art.*/
    private void startDoodle(String paint_name, boolean isOverraid) {
        ContextKt.setSharedNo(this);
        Intent lIntent1 = new Intent();
        lIntent1.setClass(this, PaintActivity.class);
//        lIntent1.setAction(Paintor.EDIT_PAINT);
        lIntent1.setAction("Edit Paint");
        lIntent1.putExtra("FromLocal", true);
        lIntent1.putExtra("paint_name", paint_name);
        lIntent1.putExtra("isOverraid", isOverraid);
        Log.e("TAGGG", "startDoodle paint_name " + paint_name);
        startActivityForResult(lIntent1, 111);
    }

    /*This method prompt the dialog to user when there are alredy trace image for specific image that user has selected.*/
    private void showAlertDialog(String drawingName, String traceImagePath) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setMessage("You already have a drawing with this traced image, do you want to continue ?").setCancelable(true);
        lBuilder1.setPositiveButton("Yes Resume it.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                ContextKt.setSharedNo(DrawNowActivity.this);
                Intent intent = new Intent(DrawNowActivity.this, PaintActivity.class);
                intent.setAction("Reload Painting");
                intent.putExtra("isTutorialmode", true);
                intent.putExtra("path", traceImagePath);
                intent.putExtra("drawingPath", drawingName);
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(DrawNowActivity.this));
                startActivityForResult(intent, 111);
                finish();
            }
        });
        lBuilder1.setNegativeButton("Start As Fresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                startDoodle(traceImagePath, false);
            }
        });
        lBuilder1.create().show();
    }


    boolean is_Open = false;

    public void captureImage(View v) {
        showDialogForCamera();
    }


    public int CAMERA_OPERATION = 0;


    public void addCamera() {
        if (BuildConfig.DEBUG) {

            Toast.makeText(DrawNowActivity.this, constants.Click_Camera_Selection, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.Click_Camera_Selection);

        is_Open = false;

        // Check permission for both storage and camera on Android Q and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (ContextCompat.checkSelfPermission(DrawNowActivity.this,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(DrawNowActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                // Request both storage and camera permissions if not granted
                String[] permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(DrawNowActivity.this, permissions, 1);

            }
        } else {
            // Check permissions on older versions
            if (!PermissionUtils.checkStoragePermission(DrawNowActivity.this) ||
                    !PermissionUtils.checkCameraPermission(DrawNowActivity.this)) {
                // Request permissions if not granted
                PermissionUtils.requestStorageAndCameraPermissions(DrawNowActivity.this, 1);
            }
//            else {
//                startActivityForResult(new Intent(DrawNowActivity.this, CameraActivity.class), PICK_IMAGE_CAMERA);
//                return;
//            }
        }


        // Launch camera intent based on API level
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, PICK_IMAGE_CAMERA);

        if (ContextCompat.checkSelfPermission(DrawNowActivity.this,
                Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(DrawNowActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {


//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
//
//            startActivityForResult(cameraIntent, CAMERA_REQUEST);


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
//                Toast.makeText(this, "not null resolver", Toast.LENGTH_SHORT).show();

            File photoFile = null;
            try {
                photoFile = createImageFile();
                Toast.makeText(this, String.valueOf(photoFile), Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getString(R.string.authority), // Update with your package name
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } else {
            }
//            }else {
//                Toast.makeText(this, "null resolver", Toast.LENGTH_SHORT).show();
//            }


            Log.e("HH=", "CCM112");
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /*This method will return the original path from the given URL of the specific file*/
    public String getRealPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(DrawNowActivity.this, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(DrawNowActivity.this, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(DrawNowActivity.this, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(DrawNowActivity.this, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /*This method will check there are external storage is available for their device OR not*/
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /*This method will check there is document pick or not*/
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /*This method verify the selected URI is valid or not once pick file from the local */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*This method will query to find specific file path in device through the cursor.*/
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e("tmessages", "Exception " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            startActivityForResult(new Intent(DrawNowActivity.this, CameraActivity.class), PICK_IMAGE_CAMERA);

//            if (PermissionUtils.checkReadStoragePermission(this)) {
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                startActivityForResult(intent, PICK_IMAGE_CAMERA);
//
//                startActivityForResult(new Intent(DrawNowActivity.this, CameraActivity.class), PICK_IMAGE_CAMERA);
//            }

            return;
        } else if (requestCode == 100) {
//            int camera_permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.CAMERA);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                int storage_permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (storage_permission != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DrawNowActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                    return;
                }
            }

        }
        /*else if (requestCode == 1) {
//            int camera_permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.CAMERA);
            int permission = 0;
            if (permissions[0].equalsIgnoreCase("android.permission.CAMERA")) {
                permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.CAMERA);
            }
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.camera_permission_msg), Toast.LENGTH_LONG).show();
            }else {
                showDialogForCamera();
            }

        }*/

        try {
            boolean STORAGE = false, CAMERA = false, RECORDING = false;
            if (permissions != null && permissions.length > 0) {

                int permission = 0;
                if (permissions[0].equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    STORAGE = true;
                    permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permission = PackageManager.PERMISSION_GRANTED;
                    }
                } else if (permissions[0].equalsIgnoreCase("android.permission.CAMERA")) {
                    CAMERA = true;
                    permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.CAMERA);
                } else if (permissions[0].equalsIgnoreCase("android.permission.RECORD_AUDIO")) {
                    RECORDING = true;
                    permission = ActivityCompat.checkSelfPermission(DrawNowActivity.this, Manifest.permission.RECORD_AUDIO);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    if (STORAGE) {
                        Toast.makeText(this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.deny_storage_permission);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.deny_storage_permission, Toast.LENGTH_SHORT).show();
                        }
                    } else if (CAMERA) {
                        Toast.makeText(this, getResources().getString(R.string.camera_permission_msg), Toast.LENGTH_LONG).show();
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.deny_camera_permission);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.deny_camera_permission, Toast.LENGTH_SHORT).show();
                        }
                    } else if (RECORDING) {
                        Toast.makeText(this, getResources().getString(R.string.recording_permission_msg), Toast.LENGTH_LONG).show();
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.deny_recording_permission);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.deny_recording_permission, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (STORAGE) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.allow_storage_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.allow_storage_permission);
                        if (requestCode == REQ_BLANK_CANVAS) {
                            int mPrefBackgroundColor = -1;
                            ContextKt.setSharedNo(this);
                            Intent lIntent1 = new Intent();
                            String str = "New Paint";
                            lIntent1.setAction(str);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(DrawNowActivity.this, constants.draw_screen_blank_canvas, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(DrawNowActivity.this, constants.draw_screen_blank_canvas);
                            constants.putInt("background_color", mPrefBackgroundColor, DrawNowActivity.this);
                            lIntent1.putExtra("background_color", mPrefBackgroundColor);
                            mlIntent = lIntent1.setClass(this, PaintActivity.class);
                            startActivity(lIntent1);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_PHOTO_REQUEST);
                        }
                    } else if (CAMERA) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.allow_camera_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.allow_camera_permission);
                        int permission_storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permission_storage == PackageManager.PERMISSION_GRANTED) {
//                            addCamera();
                            showDialogForCamera();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                        }
                    } else if (RECORDING) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(DrawNowActivity.this, constants.allow_recording_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(DrawNowActivity.this, constants.allow_recording_permission);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private int getMarginWidth(int bannerWidth) {
        // Get Screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        //Get Banner actual width
        final float scale = getResources().getDisplayMetrics().density;


        int width = (int) (bannerWidth * scale + 0.5f);
        return (screenWidth - width) / 2;
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(DrawNowActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, DrawNowActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, DrawNowActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called DrawNow 1176");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(DrawNowActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, DrawNowActivity.this);
            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, DrawNowActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called DrawNow 1198");
                    MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "Orientation change");
//        Toast.makeText(this, "Orientation change", Toast.LENGTH_SHORT).show();
    }


    public void pickFromCamera(String target_name) {

        if (target_name.equalsIgnoreCase("camera trace")) {
            if (PermissionUtil.INSTANCE.checkPermissions(DrawNowActivity.this, PermissionUtil.INSTANCE.getAllPermissions())) {
                CAMERA_OPERATION = 2;
                addCamera();
            } else {
                PermissionUtil.INSTANCE.requestPermission(DrawNowActivity.this,
                        PermissionUtil.INSTANCE.getAllPermissions(), DrawNowActivity.this,
                        new Function1<Boolean, Unit>() {
                            @Override
                            public Unit invoke(Boolean granted) {
                                if (granted) {
                                    CAMERA_OPERATION = 2;
                                    addCamera();
                                }
                                return Unit.INSTANCE;
                            }
                        });
            }
        } else {
            if (PermissionUtil.INSTANCE.checkPermissions(DrawNowActivity.this, PermissionUtil.INSTANCE.getAllPermissions())) {
                CAMERA_OPERATION = 1;
                addCamera();
            } else {
                PermissionUtil.INSTANCE.requestPermission(DrawNowActivity.this,
                        PermissionUtil.INSTANCE.getAllPermissions(), DrawNowActivity.this,
                        granted -> {
                            if (granted) {
                                CAMERA_OPERATION = 1;
                                addCamera();
                            }
                            return Unit.INSTANCE;
                        });
            }
        }
    }

    public void showDialogForCamera() {

        if (BuildConfig.DEBUG) {
            Toast.makeText(DrawNowActivity.this, constants.draw_screen_camera_mode, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(DrawNowActivity.this, constants.draw_screen_camera_mode);
        final Dialog dialog = new Dialog(DrawNowActivity.this);
        dialog.setContentView(R.layout.camera_image_op_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tv_image_path = dialog.findViewById(R.id.tv_image_path);
        LinearLayout tv_use_overlay = dialog.findViewById(R.id.ll_overlay);
        LinearLayout tv_use_trace = dialog.findViewById(R.id.ll_trace);

        tv_use_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(DrawNowActivity.this, Paintor.class);
                intent.setAction("LoadWithoutTraceFromCamera");
                intent.putExtra("path", _file.getName());
                intent.putExtra("ParentFolderPath", _file.getParentFile().getAbsolutePath());
                intent.putExtra("isPickFromOverlaid", true);
                startActivity(intent);
                dialog.dismiss();*/
                dialog.dismiss();
                if (PermissionUtil.INSTANCE.checkPermissions(DrawNowActivity.this, PermissionUtil.INSTANCE.getAllPermissions())) {
                    CAMERA_OPERATION = 1;
                    addCamera();
                } else {
                    PermissionUtil.INSTANCE.requestPermission(DrawNowActivity.this,
                            PermissionUtil.INSTANCE.getAllPermissions(), DrawNowActivity.this,
                            granted -> {
                                if (granted) {
                                    CAMERA_OPERATION = 1;
                                    addCamera();
                                }
                                return Unit.INSTANCE;
                            });
                }
            }
        });
        tv_use_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startDoodle(_path, false);
                dialog.dismiss();*/
                dialog.dismiss();
                if (PermissionUtil.INSTANCE.checkPermissions(DrawNowActivity.this, PermissionUtil.INSTANCE.getAllPermissions())) {
                    CAMERA_OPERATION = 2;
                    addCamera();
                } else {
                    PermissionUtil.INSTANCE.requestPermission(DrawNowActivity.this,
                            PermissionUtil.INSTANCE.getAllPermissions(), DrawNowActivity.this,
                            new Function1<Boolean, Unit>() {
                                @Override
                                public Unit invoke(Boolean granted) {
                                    if (granted) {
                                        CAMERA_OPERATION = 2;
                                        addCamera();
                                    }
                                    return Unit.INSTANCE;
                                }
                            });
                }
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (showExitDialog) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.exit_app_msg))
                    .setPositiveButton(
                            getString(R.string.quit),
                            (dialogInterface, i) -> {
                                MyApplication.setAppUsedCountSeen(false);
                                finishAffinity();
                            }
                    )
                    .setNegativeButton(
                            getString(R.string.cancel),
                            (dialogInterface, i) -> {
                                startActivity(new Intent(DrawNowActivity.this, GalleryDashboard.class));
                                finish();
                            }
                    )
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void drawOverlayRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.draw_overlay_image, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(DrawNowActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "draw_overlay_image",
                        rewardSetup.getDraw_overlay_image() == null ? 0 : rewardSetup.getDraw_overlay_image(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    private void drawTraceRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.draw_trace_image, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(DrawNowActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "draw_trace_image",
                        rewardSetup.getDraw_trace_image() == null ? 0 : rewardSetup.getDraw_trace_image(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    private void drawCameraRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.draw_camera_image, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(DrawNowActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "draw_camera_image",
                        rewardSetup.getDraw_camera_image() == null ? 0 : rewardSetup.getDraw_camera_image(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    private void googleClassroomRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.google_classroom, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(DrawNowActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "google_classroom",
                        rewardSetup.getGoogle_classroom() == null ? 0 : rewardSetup.getGoogle_classroom(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    private void quoraPaintologyRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.quora_paintology, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(DrawNowActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "quora_paintology",
                        rewardSetup.getQuora_paintology() == null ? 0 : rewardSetup.getQuora_paintology(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MyConstantsKt.commonMenuClick(this, item, StringConstants.intro_draw);
    }


}
