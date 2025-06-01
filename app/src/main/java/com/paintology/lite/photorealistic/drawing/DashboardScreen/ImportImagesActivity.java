package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBannerResourcesBinding;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.PaintingDao;
import com.paintology.lite.photorealistic.drawing.room.entities.PaintingEntity;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.UploadZipResponse;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.Retrofit.ProgressRequestBody;
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportImagesActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, ProgressRequestBody.UploadCallbacks {

    StringConstants constants = new StringConstants();
    final int PICK_IMAGE_TO_IMPORT = 500;

    private GoogleSignInClient mGoogleSignInClient;

    int RC_SIGN_IN = 7;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

//    private SignInButton btnSignIn, googleSignInButton;

    GoogleApiClient googleApiClient;

    GoogleSignInAccount account;

    LoginButton facebook_login_btn;

    boolean isLoggedIn;

    ApiInterface apiInterface;
    TextView tv_view_sketch_photo;
    ProgressDialog progressDialog = null;

    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String LoginInPaintology;
//    private com.facebook.ads.AdView kAdView, fb_banner_small;
//    InterstitialAd mInterstitialAd;
//    InterstitialAd.InterstitialLoadAdConfig MLoadAdConfigInterstitial;


//    InMobiBanner mBannerAd;

    List<BannerModel> data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        InMobiSdk.init(this, "3c3c1201fbf94be0ba072798ba99ad57");
        String _local = constants.getString(constants.selected_language, ImportImagesActivity.this);
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_import_images);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Import Images");

//        AdSettings.addTestDevice("29dd48fe-86a9-4d67-bc9a-95304c0d429c");


//        kAdView = new com.facebook.ads.AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.RECTANGLE_HEIGHT_250);

//        fb_banner_small = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_banner_unit), AdSize.BANNER_HEIGHT_50);
//        fb_banner_small = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_banner_unit), AdSize.BANNER_HEIGHT_50);
//        LinearLayout adContainer_btm = (LinearLayout) findViewById(R.id.banner_container_bottom);
//        adContainer_btm.addView(fb_banner_small);
//        fb_banner_small.loadAd();




       /* mInterstitialAd = new InterstitialAd(this, getResources().getString(R.string.fb_interstitial_unit));

        InterstitialAdListener mInterstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mInterstitialAd.loadAd(MLoadAdConfigInterstitial);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("fbads", "e: " + adError.getErrorMessage());

            }

            @Override
            public void onAdLoaded(Ad ad) {

                if (mInterstitialAd.isAdLoaded()) {
                    mInterstitialAd.show();
                }

                Log.e("fbads", "showed");
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        MLoadAdConfigInterstitial = mInterstitialAd.buildLoadAdConfig()
                .withAdListener(mInterstitialAdListener)
                .build();


        mInterstitialAd.loadAd(MLoadAdConfigInterstitial);*/

//        googleSignInButton = findViewById(R.id.btn_sign_in);
//        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
//        btnSignIn.setOnClickListener(this);
        facebook_login_btn = (LoginButton) findViewById(R.id.login_button);

        tv_view_sketch_photo = (TextView) findViewById(R.id.tv_view_sketch_photo);
        tv_view_sketch_photo.setOnClickListener(this);

        facebook_login_btn.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        facebook_login_btn.registerCallback(callbackManager, callback);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.client_id))
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(ImportImagesActivity.this, gso);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);

        LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
        if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
            if (constants.getString(constants.IsFileUploaded, ImportImagesActivity.this).equalsIgnoreCase("true"))
                tv_view_sketch_photo.setVisibility(View.VISIBLE);
        }

        fetchData();

        ImageView iv_top_bannr = findViewById(R.id.iv_top_bannr);
        iv_top_bannr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImportImagesActivity.this.onClick(findViewById(R.id.tv_import_sketch_photo));
            }
        });


//        loadFBAdv();
//        loadFbRectBanner();
//        loadAdmobRectangeAdd();
    }

//    private void loadFbRectBanner() {
//        // Find the Ad Container
//        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
////        kAdView = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_banner_rect_unit), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
//        kAdView = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_banner_rect_unit), AdSize.RECTANGLE_HEIGHT_250);
//
//        // Add the ad view to your activity layout
//        adContainer.addView(kAdView);
//
//        com.facebook.ads.AdListener adListener = new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Ad error callback
//                Toast.makeText(
//                        ImportImagesActivity.this,
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
//        kAdView.loadAd(kAdView.buildLoadAdConfig().withAdListener(adListener).build());
//    }
//
//
//    void loadAdmobRectangeAdd() {
//
//        com.google.android.gms.ads.AdView mAdView = findViewById(R.id.adView_rectangle);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//    }


  /*  private AdView adView;

    void loadFBAdv() {
        AudienceNetworkAds.initialize(ImportImagesActivity.this);
        adView = new AdView(this, "478223519733083_480930342795734", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(ImportImagesActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("TAG", "Facebook onError " + adError.getErrorMessage() + " " + adError.getErrorCode());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        adView.loadAd();

    }*/


    private void fetchData() {
        data = AppUtils.getBanners(this);
        for (int i = 0; i < data.size(); i++) {
            LayoutBannerResourcesBinding bannerBinding = LayoutBannerResourcesBinding.inflate(getLayoutInflater());
            Picasso.get().load(Uri.parse(data.get(i).bannerImageUrl)).into(bannerBinding.ivOwnAdv);
            int finalI = i;
            bannerBinding.cvAdv.setOnClickListener(v -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ImportImagesActivity.this,
                                constants.ad_XX_import_banner_click.replace("XX", String.valueOf(finalI)),
                                Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(
                            ImportImagesActivity.this,
                            constants.ad_XX_import_banner_click.replace("XX", String.valueOf(finalI))
                    );
                    KGlobal.openInBrowser(ImportImagesActivity.this, data.get(finalI).bannerLInk);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            });

            ((LinearLayout) findViewById(R.id.llAds)).addView(bannerBinding.getRoot());
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.tv_how_to_export: {
                if (KGlobal.isInternetAvailable(ImportImagesActivity.this)) {
                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.VISIT_HOW_TO_EXPORT);
                    String url = "https://www.paintology.com/sony-sketch-app-how-to-export-data/";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } else
                    Toast.makeText(this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
            }
            break;*/
            case R.id.tv_import_sketch_photo: {
//                int permission = ActivityCompat.checkSelfPermission(ImportImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(
//                            ImportImagesActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1
//                    );
//                    return;
//                }

//                if (!PermissionUtils.checkStoragePermission(ImportImagesActivity.this)) {
//                    // We don't have permission so prompt the user
//                    PermissionUtils.requestStoragePermission(ImportImagesActivity.this, 1);
//                    return;
//                }

                Intent intent = new Intent(ImportImagesActivity.this, Gallery.class);
                intent.putExtra("title", "Select Images");
                intent.putExtra("mode", 1);
                intent.putExtra("maxSelection", 500);
                intent.putExtra("isFromImport", true);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.IMPORT_SCREEN_IMPORT_IMAGES, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.IMPORT_SCREEN_IMPORT_IMAGES);
                startActivityForResult(intent, PICK_IMAGE_TO_IMPORT);
            }
            break;
            case R.id.tv_upload_zip_file: {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.IMPORT_SCREEN_SAVE_IMAGES, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.IMPORT_SCREEN_SAVE_IMAGES);
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                isLoggedIn = accessToken != null && !accessToken.isExpired();
                account = GoogleSignIn.getLastSignedInAccount(this);
                LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
                if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
                    Toast.makeText(this, "Browse zip file or image from local storage", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
//                    i.setType("image/*|file/zip");
                    i.setType("*/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(i, "abc"), 108);
                } else {
                    showLoginDialog();
                }
            }
            break;
            case R.id.tv_view_sketch_photo: {
              /*  FirebaseUtils.logEvents(this, constants.VISIT_SKETCH_PHOTO);
                String url = "https://www.paintology.com/s3-bucket?salt=" + constants.getString(constants.Salt, ImportImagesActivity.this);
                Log.e("TAGGG", "Salt Url " + url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                startActivity(browserIntent);*/
                String url = "https://www.paintology.com/s3-bucket?salt=" + constants.getString(constants.Salt, ImportImagesActivity.this);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.VISIT_SKETCH_PHOTO, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.VISIT_SKETCH_PHOTO);
                KGlobal.openInBrowser(ImportImagesActivity.this, url);

            }
            break;
        }
    }


    /*This method will called when user click on facebook login button, and app will open facebook login page*/
    public void onClickFacebookButton(View view) {
        Log.e("TAGGG", "Facebook Event onSuccess  facebook_login_btn click");
        facebook_login_btn.performClick();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_TO_IMPORT) {

            if (data.hasExtra("result")) {
                final ArrayList<String> selectedImage = data.getStringArrayListExtra("result");

                ArrayList<String> lst_filtered = new ArrayList<>();
                File targetLocation = null;
                for (int i = 0; i < selectedImage.size(); i++) {
                    File sourceLocation = new File(selectedImage.get(i));
                    if (sourceLocation.getName().toLowerCase().endsWith(".webp")) {
                        StringTokenizer tokens = new StringTokenizer(sourceLocation.getName(), ".");
                        String first = tokens.nextToken();// this will contain "Fruit"
                        targetLocation = new File(KGlobal.getMyPaintingFolderPath(this) + "/" + first + ".png");
                    } else
                        targetLocation = new File(KGlobal.getMyPaintingFolderPath(this) + "/" + sourceLocation.getName());

                    if (!targetLocation.exists()) {
                        lst_filtered.add(selectedImage.get(i));
                    }
                    Log.e("TAGGG", "Clear Check onActivityResult List <--> POST " + selectedImage.get(i));
                }
                new importSelectedFile(lst_filtered).execute();
            }
        } else if (requestCode == RC_SIGN_IN) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                account = task.getResult(ApiException.class);

                Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
                constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), ImportImagesActivity.this);
                constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), ImportImagesActivity.this);
                constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), ImportImagesActivity.this);
                LoginRequestModel model = new LoginRequestModel(
                        (account.getId() != null ? account.getId() : ""),
                        (account.getDisplayName() != null ? account.getDisplayName() : ""),
                        (account.getEmail() != null ? account.getEmail() : ""),
                        ""
                );

                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE);
            } catch (ApiException e) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginFailed);
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
            }
        } else if (resultCode == RESULT_OK && requestCode == 108) {
            Uri imageUri = data.getData();
            String str14 = getRealPath(imageUri);
            if (str14 == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImportImagesActivity.this);
                builder.setTitle("Can't Load");
                builder.setMessage("Selected zip file is not on your local storage, please download and pick file from there.");
                builder.setNegativeButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
//                    Toast.makeText(this, str14 + " ", Toast.LENGTH_SHORT).show();
                Log.e("TAGGG", "Data From Preference username " + constants.getString(constants.Username, ImportImagesActivity.this) + " :: Pass " + constants.getString(constants.Password, ImportImagesActivity.this) + " :: Email " + constants.getString(constants.Email, ImportImagesActivity.this));
                uploadFile(imageUri);
            }
            Log.e("TAGGG", "Selected File " + str14 + " imageUri " + imageUri);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onProgressUpdate(int percentage) {
        Log.e("TAGGG", "Retrofit onProgressUpdate " + percentage);
        if (progressDialog != null)
            progressDialog.setProgress(percentage);
    }

    @Override
    public void onError() {
        Log.e("TAGGG", "Retrofit OnError");
    }

    @Override
    public void onFinish() {
        Log.e("TAGGG", "Retrofit onFinish");
    }

    @Override
    public void uploadStart() {
        Log.e("TAGGG", "Retrofit uploadStart");
        if (progressDialog != null)
            progressDialog.setProgress(0);
    }


    /*This is the class who is responsible to import images from user local storage folder to My Painting folders.
     * This class called and execute when user click on Import Sketch File.
     * */
    public class importSelectedFile extends AsyncTask<Void, Void, Boolean> {

        ArrayList<String> list_path;
        String TAG = "Do in background";
        ProgressDialog pdialog;

        public importSelectedFile(ArrayList<String> path) {
            this.list_path = path;
            // the file to be moved or copie
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pdialog = new ProgressDialog(ImportImagesActivity.this);
                pdialog.setTitle(getString(R.string.please_wait));
                pdialog.setMessage("Importing selected images...");
                pdialog.setCancelable(false);
                pdialog.setCanceledOnTouchOutside(false);
                pdialog.show();
            } catch (Exception e) {

            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (int i = 0; i < list_path.size(); i++) {
                    File sourceLocation = new File(list_path.get(i));
                    // make sure your target location folder exists!
                    File targetLocation = null;

                    File dir = new File(KGlobal.getMyPaintingFolderPath(ImportImagesActivity.this));
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    String new_name;
                    if (sourceLocation.getName().toLowerCase().endsWith(".webp")) {
                        StringTokenizer tokens = new StringTokenizer(sourceLocation.getName(), ".");
                        String first = tokens.nextToken();// this will contain "Fruit"

                        new_name = first + ".png";
                        targetLocation = new File(KGlobal.getMyPaintingFolderPath(ImportImagesActivity.this) + "/" + new_name);
                    } else {
                        new_name = sourceLocation.getName();
                        targetLocation = new File(KGlobal.getMyPaintingFolderPath(ImportImagesActivity.this) + "/" + new_name);
                    }

                    // just to take note of the location sources

                    Log.v(TAG, "sourceLocation: " + sourceLocation);
                    Log.v(TAG, "targetLocation: " + targetLocation);

                    // make sure the target file exists
                    if (sourceLocation.exists() && !targetLocation.exists()) {
                        InputStream in = new FileInputStream(sourceLocation);
                        OutputStream out = new FileOutputStream(targetLocation);
                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();
                        Log.v(TAG, "Copy file successful.");
                    } else {
                        Log.v(TAG, "Copy file failed. Source file missing.");
                    }

                    SharedPreferences appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(ImportImagesActivity.this);

                    Gson gson = new Gson();
                    String json = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");

                    Log.e("TAG", "storeInTraceList json " + json);
                    Type type = new TypeToken<ArrayList<TraceReference>>() {
                    }.getType();
                    ArrayList<TraceReference> traceList = gson.fromJson(json, type);

                    if (traceList == null)
                        traceList = new ArrayList<>();

                    TraceReference trace_image = new TraceReference();
                    trace_image.setUserPaintingName(new_name);
                    trace_image.set_drawing_type(drawing_type.ImportImage);

                    traceList.add(trace_image);

                    AppDatabase appDatabase = MyApplication.getDb();
                    PaintingDao paintingDao = appDatabase.paintingDao();
                    PaintingEntity painting = new PaintingEntity(new Random().nextInt(10000), new_name, "import", false);
                    paintingDao.insertPainting(painting);


                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    String json_1 = gson.toJson(traceList);
                    prefsEditor.putString(constants.getOverlayList_Gson_Key(), json_1);
                    prefsEditor.apply();



                }
                return true;
            } catch (Exception e) {
//                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.IMPORT_SKETCH_PHOTO_FAIL);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if (isSuccess) {
                if (list_path.size() == 1) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ImportImagesActivity.this, constants.import_image_single_to_success, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.import_image_single_to_success);
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ImportImagesActivity.this, constants.import_image_multiple_to_success, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.import_image_multiple_to_success);
                }

                Dialog dialog = new Dialog(ImportImagesActivity.this);
                dialog.setContentView(R.layout.import_image_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                TextView tvMessage = dialog.findViewById(R.id.tvMessage);
                ImageView imgCross = dialog.findViewById(R.id.imgCross);
                AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                AppCompatButton btn_projectnamesave = dialog.findViewById(R.id.btn_projectnamesave);


                tvMessage.setText(String.valueOf(list_path.size())+" image added to My Paintings!.");

                imgCross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                btn_projectnamesave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ImportImagesActivity.this, MyPaintingsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dialog.show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(ImportImagesActivity.this);
//                builder.setMessage(getString(R.string.you_have_successfully_imported_image, list_path.size()))
//                        .setPositiveButton(R.string.goto_my_paintings, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(ImportImagesActivity.this, MyPaintingsActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        })
//                        .setNegativeButton(R.string.ok_label, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();

            } else {
                if (list_path.size() == 1) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ImportImagesActivity.this, constants.import_image_single_to_fail, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.import_image_single_to_fail);
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ImportImagesActivity.this, constants.import_image_multiple_to_fail, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.import_image_multiple_to_fail);
                }
            }
//            Toast.makeText(ImportImagesActivity.this, "Images are imported to My Paintings " + isSuccess, Toast.LENGTH_SHORT).show();
            pdialog.dismiss();
            Log.e("TAGGG", "On Post Excute called");
        }
    }

    /*This method will prompt social media login dialog when user click on upload zip file.*/
    public void showLoginDialog() {
        Intent intent = new Intent(ImportImagesActivity.this, LoginActivity.class);
        startActivity(intent);
    }

//    public void showLoginDialog() {
//        final Dialog dialog = new Dialog(ImportImagesActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.social_media_login_layout);
//
//        if (BuildConfig.DEBUG){
//            Toast.makeText(ImportImagesActivity.this, constants.open_social_login_import_dialog, Toast.LENGTH_SHORT).show();
//        }
//        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.open_social_login_import_dialog);
//
//        TextView tv_community_link = (TextView) dialog.findViewById(R.id.tv_community_link);
//        tv_community_link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(ImportImagesActivity.this, constants.open_social_login_import_dialog_link_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.open_social_login_import_dialog_link_click);
//                startActivity(new Intent(ImportImagesActivity.this, Community.class));
//                dialog.dismiss();
//            }
//        });
//
//        Button btn_fb = (Button) dialog.findViewById(R.id.fb);
//        btn_fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Perfome Action
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(ImportImagesActivity.this, constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.FACEBOOK_LOGIN);
//                facebook_login_btn.performClick();
//                dialog.dismiss();
//            }
//        });
//        Button btn_google = (Button) dialog.findViewById(R.id.google);
//        btn_google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Perfome Action
//                signIn();
//                dialog.dismiss();
//
//            }
//        });
//
//        Button btn_paintology = (Button) dialog.findViewById(R.id.btn_paintology);
//        btn_paintology.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(ImportImagesActivity.this, constants.Social_Paintology_Login, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.Social_Paintology_Login);
//                showDialog();
//            }
//        });
//        dialog.show();
//    }

    /*This is the callback of facebook login , once user do login successfully this method will get called and do further operation respectively*/
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("TAGG", "Facebook Event onSuccess FB login onsuccess called");
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ImportImagesActivity.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ImportImagesActivity.this, constants.FacebookLoginSuccess);
                            // Application code
                            try {
                                Log.d("tttttt", object.getString("id"));
                                String birthday = "";
                                if (object.has("birthday")) {
                                    birthday = object.getString("birthday"); // 01/31/1980 format
                                }

                                String fnm = object.has("first_name") ? object.getString("first_name") : "";
                                String lnm = object.has("last_name") ? object.getString("last_name") : "";
                                String mail = object.has("email") ? object.getString("email") : "";
                                String gender = object.has("gender") ? object.getString("gender") : "";
                                String fid = object.has("id") ? object.getString("id") : "";
//                                Log.e("aswwww", "https://graph.facebook.com/" + fid + "/picture?type=large");
//                                Log.e("TAGGG", "FB login Profile Info fnm " + fnm + " lnm " + lnm + " mail " + mail + " gender " + gender + " fid " + fid + " birthday " + birthday);
                                isLoggedIn = true;
                                constants.putString(constants.Username, fnm, ImportImagesActivity.this);
                                constants.putString(constants.Password, fid, ImportImagesActivity.this);
                                constants.putString(constants.Email, mail, ImportImagesActivity.this);
                                LoginRequestModel model = new LoginRequestModel(fid, fnm, mail, "");
                                addUser(model, LOGIN_FROM_FB);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("TAGGG", "FB login Exception while get detail " + e.getMessage());
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.e("TAGGG", "Facebook Event OnError onCancel ");
        }

        @Override
        public void onError(FacebookException error) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(ImportImagesActivity.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(ImportImagesActivity.this, constants.FacebookLoginFailed);
            Log.e("TAGGG", "Facebook Event OnError Called " + error.getMessage(), error);
        }
    };

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType) {

        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_name != null) ? model.user_name : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");

        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, ImportImagesActivity.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, ImportImagesActivity.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, ImportImagesActivity.this));

        try {

            String _ip = constants.getString(constants.IpAddress, ImportImagesActivity.this);
            String _country = constants.getString(constants.UserCountry, ImportImagesActivity.this);
            String _city = constants.getString(constants.UserCity, ImportImagesActivity.this);

            Log.e("TAGG", "Region Data  _ip " + _ip + " _country " + _country + " _city " + _city);
        } catch (Exception e) {

        }

        _map.put("user_ip", req_ip_address);
        _map.put("country", req_ip_country);
        _map.put("city", req_ip_city);

        _map.put("user_id", userId);
        _map.put("user_name", userName);
        _map.put("user_email", userEmail);
        if (loginType == LOGIN_FROM_FB) {
            RequestBody l_Type = RequestBody.create(MediaType.parse("text/plain"), getResources().getString(R.string.type_facebook));
            _map.put("flag", l_Type);
        } else if (loginType == LOGIN_FROM_GOOGLE) {
            RequestBody l_Type = RequestBody.create(MediaType.parse("text/plain"), getResources().getString(R.string.type_google));
            _map.put("flag", l_Type);
        } else {
            RequestBody l_Type = RequestBody.create(MediaType.parse("text/plain"), getResources().getString(R.string.type_paintology));
            _map.put("flag", l_Type);
        }

        Call<LoginResponseModel> call = apiInterface.addUserData(ApiClient.SECRET_KEY, _map);
        showProgress(false);
        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {
                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                tv_view_sketch_photo.setVisibility(View.VISIBLE);
                                constants.putString(constants.IsFileUploaded, "true", ImportImagesActivity.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", ImportImagesActivity.this);

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", ImportImagesActivity.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), ImportImagesActivity.this);
                            Log.e("TAGGG", "Salt Value is " + response.body().getObjData().getSalt());

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                constants.putString(constants.Username, model.user_name, ImportImagesActivity.this);
                                constants.putString(constants.Password, model.user_id, ImportImagesActivity.this);
                                constants.putString(constants.Email, model.user_email, ImportImagesActivity.this);
                                constants.putString(constants.LoginInPaintology, "true", ImportImagesActivity.this);
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, ImportImagesActivity.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ImportImagesActivity.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ImportImagesActivity.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ImportImagesActivity.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ImportImagesActivity.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.GoogleRegistration);
                                    }
                                }
                            }

                            String _user_id = constants.getString(constants.UserId, ImportImagesActivity.this);
                            MyApplication.get_realTimeDbUtils(ImportImagesActivity.this).autoLoginRegister(response.body().getObjData().getStatus());

                            if (KGlobal.isInternetAvailable(ImportImagesActivity.this) && _user_id != null && !_user_id.isEmpty()) {
                                startService(new Intent(ImportImagesActivity.this, SendDeviceToken.class));
                            }
                        }
                    } else {
                        if (loginType == LOGIN_FROM_FB)
                            LoginManager.getInstance().logOut();
                        else if (loginType == LOGIN_FROM_GOOGLE) {
                            account = null;
                            Auth.GoogleSignInApi.signOut(googleApiClient);
                        } else {
                            LoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ImportImagesActivity.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ImportImagesActivity.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ImportImagesActivity.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.event_failed_to_adduser);
                        Toast.makeText(ImportImagesActivity.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    }
//                    new SaveTask(model).execute();
                    hideProgress();
                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    Log.e("TAGG", "add user in failure " + t.getMessage(), t);
                    hideProgress();
                    try {
                        if (loginType == LOGIN_FROM_FB) {
                            LoginManager.getInstance().logOut();
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            isLoggedIn = accessToken != null && !accessToken.isExpired();
                        } else if (loginType == LOGIN_FROM_GOOGLE) {
                            account = null;
                            Auth.GoogleSignInApi.signOut(googleApiClient);
                        } else {
                            LoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ImportImagesActivity.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ImportImagesActivity.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ImportImagesActivity.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ImportImagesActivity.this, constants.event_failed_to_adduser);
                        Toast.makeText(ImportImagesActivity.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "add user in Exception " + e.getMessage(), e);
            hideProgress();
        }
    }

    void showProgress(Boolean isFromUpload) {
        try {
            progressDialog = new ProgressDialog(ImportImagesActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            if (isFromUpload) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.setMessage("Uploading File");
            }
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        try {
            if (ImportImagesActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    /*User can do their google sign in using this method*/
    private void signIn() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(ImportImagesActivity.this, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.GOOGLE_LOGIN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void uploadFile(Uri fileUri) {
        Log.e("TAGGG", "uploadFile Called");

        try {
            String str14 = getRealPath(fileUri);
            File file = new File(str14);
            showProgress(true);
            ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, ImportImagesActivity.this));
            Call<UploadZipResponse> call = apiInterface.uploadZip(ApiClient.SECRET_KEY, fileToUpload, user_id);

            try {
                call.enqueue(new Callback<UploadZipResponse>() {
                    @Override
                    public void onResponse(Call<UploadZipResponse> call, Response<UploadZipResponse> response) {
                        Log.e("TAGGG", "OnResponse Success");
                        try {
                            if (response != null && response.isSuccessful()) {
                                Log.e("TAGGG", "Api called on success");
                                if (response.body() != null && response.body().getData() != null && response.body().getData().toString().equalsIgnoreCase("Success")) {
                                    constants.putString(constants.IsFileUploaded, "true", ImportImagesActivity.this);
                                    tv_view_sketch_photo.setVisibility(View.VISIBLE);
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(ImportImagesActivity.this, constants.UploadZipFileSuccess, Toast.LENGTH_SHORT).show();
                                    }
                                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.UploadZipFileSuccess);

                                    Dialog dialog = new Dialog(ImportImagesActivity.this);
                                    dialog.setContentView(R.layout.import_image_dialog);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.setCancelable(false);
                                    dialog.setCanceledOnTouchOutside(false);


                                    TextView tvMessage = dialog.findViewById(R.id.tvMessage);
                                    ImageView imgCross = dialog.findViewById(R.id.imgCross);
                                    AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                                    AppCompatButton btn_projectnamesave = dialog.findViewById(R.id.btn_projectnamesave);


//                                    tvMessage.setText(getString(R.string.you_have_successfully_imported_image ));
                                    tvMessage.setText(getString(R.string.image_added_to_my_paintings));

                                    imgCross.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();

                                        }
                                    });

                                    btn_projectnamesave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ImportImagesActivity.this, MyPaintingsActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                        }
                                    });

                                    dialog.show();

//                                    AlertDialog.Builder builder = new AlertDialog.Builder(ImportImagesActivity.this);
//                                    builder.setMessage(getString(R.string.you_have_successfully_imported_image, 1))
//                                            .setPositiveButton("Go To My Painting", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    Intent intent = new Intent(ImportImagesActivity.this, MyPaintingsActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            })
//                                            .setNegativeButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            })
//                                            .show();

                                } else {
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(ImportImagesActivity.this, constants.UploadZipFileFail, Toast.LENGTH_SHORT).show();
                                    }
                                    FirebaseUtils.logEvents(ImportImagesActivity.this, constants.UploadZipFileFail);
                                }
                                Toast.makeText(ImportImagesActivity.this, response.body().getData(), Toast.LENGTH_SHORT).show();
                            } else
                                Log.e("TAGGG", "Api called on failed");
                            hideProgress();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<UploadZipResponse> call, Throwable t) {
                        Log.e("TAGGG", "OnResponse Failed");
                        hideProgress();
                    }
                });
            } catch (Exception e) {
                Log.e("TAGG", "OnResponse Exception " + e.getMessage(), e);
                hideProgress();

            }
        } catch (IllegalStateException | IllegalArgumentException e) {

        } catch (Exception e) {

        }
    }

    /*This method will return the original path from the given URL of the specific file*/
    public String getRealPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(ImportImagesActivity.this, uri)) {
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
                return getDataColumn(ImportImagesActivity.this, contentUri, null, null);
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

                return getDataColumn(ImportImagesActivity.this, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(ImportImagesActivity.this, uri, null, null);
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


//    public void showDialog() {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        final Dialog dialog = new Dialog(ImportImagesActivity.this);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        dialog.setContentView(R.layout.login_to_paintology_layout);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        EditText edt_uname = dialog.findViewById(R.id.edt_uname);
//        EditText edt_email = dialog.findViewById(R.id.edt_email);
//        EditText edt_pass = dialog.findViewById(R.id.edt_pass);
//        Button btn_proceed = dialog.findViewById(R.id.btn_proceed);
//        ImageView iv_close_dialog = dialog.findViewById(R.id.iv_close_dialog);
//
//        AppCompatCheckBox cb_viewpass = (AppCompatCheckBox) dialog.findViewById(R.id.cb_viewpass);
//
//        cb_viewpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked) {
//
//                    // show password
//                    edt_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    Log.i("checker", "true");
//                } else {
//                    Log.i("checker", "false");
//                    // hide password
//                    edt_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }
//            }
//        });
//
//        btn_proceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (edt_email.getText().toString().isEmpty()) {
//                    edt_email.setError(getResources().getString(R.string.required));
//                    edt_email.requestFocus();
//                } else if (!edt_email.getText().toString().trim().matches(emailPattern)) {
//                    edt_email.setError(getResources().getString(R.string.not_valid));
//                    edt_email.requestFocus();
//                } else if (edt_pass.getText().toString().trim().isEmpty()) {
//                    edt_pass.setError(getResources().getString(R.string.required));
//                    edt_pass.requestFocus();
//                } else if (edt_pass.getText().toString().trim().length() <= 4) {
//                    edt_pass.setError(getResources().getString(R.string.too_weak));
//                    edt_pass.requestFocus();
//                } else if (edt_uname.getText().toString().isEmpty()) {
//                    edt_uname.setError(getResources().getString(R.string.required));
//                    edt_uname.requestFocus();
//                } else if (edt_uname.getText().toString().trim().contains(" ")) {
//                    edt_uname.setError("Blank space not allowed");
//                    edt_uname.requestFocus();
//                } else {
////                    LoginRequestModel model = new LoginRequestModel((account.getDisplayName() != null ? account.getDisplayName() : ""), (account.getId() != null ? account.getId() : ""), (account.getEmail() != null ? account.getEmail() : ""));
//                    LoginRequestModel model = new LoginRequestModel(edt_pass.getText().toString().trim(), edt_uname.getText().toString().trim(), edt_email.getText().toString().trim(), edt_pass.getText().toString().trim());
//                    Log.e("TAGG", "Login Data " + model.user_email + " " + model.user_id + " " + model.user_name);
//                    if (KGlobal.isInternetAvailable(ImportImagesActivity.this)) {
//                        addUser(model, LOGIN_FROM_PAINTOLOGY);
//                    } else
//                        Toast.makeText(ImportImagesActivity.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            }
//        });
//        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        edt_uname.setFilters(new InputFilter[]{filter});
//        dialog.show();
//    }

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    Log.e("TAGG", "Source In loop " + source);
                    if (source.toString().contains(" "))
                        return "_";
                    else
                        return "";
                }
            }
            return null;
        }
    };
//    String _redirect_link;

    /*void getCategoryDataFromAPI() {
        Call<GetCategoryPostModel> call = apiInterface.getCategoryPostList(BASE_URL + "wp-json/wcra/v1/getTutorialCatPosts/?cat_id=483&post_type=tutorials");
        try {
            call.enqueue(new Callback<GetCategoryPostModel>() {
                @Override
                public void onResponse(Call<GetCategoryPostModel> call, retrofit2.Response<GetCategoryPostModel> response) {
                    try {
                        Log.e("TAGGG", "OnResponse Called in Main screen");
                        if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                            if (response.body().getPostList().size() > 0) {
                                if (response != null && response.body() != null && response.body().getPostList() != null) {
                                    ArrayList<GetCategoryPostModel.postData> list = response.body().getPostList();

                                    for (int i = 0; i < list.size(); i++) {
                                        if (list.get(i).getObjdata().getID().equalsIgnoreCase("14596")) {
                                            if (response.body().getPostList().size() > 0 && response.body().getPostList().get(i).getObjdata() != null && response.body().getPostList().get(i).getObjdata().getThumbImage() != null) {
                                                *//*Glide.with(ImportImagesActivity.this)
                                                        .load(response.body().getPostList().get(0).getResize())
                                                        .apply(new RequestOptions().placeholder(R.drawable.thumbnaildefault).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                                        .into(iv_own_adv);*//*

                                                Glide.with(ImportImagesActivity.this).load(response.body().getPostList().get(i).getObjdata().getThumbImage()).into(iv_own_adv).onLoadStarted(getResources().getDrawable(R.drawable.jumpdates_featured));
                                                Log.e("TAGGG", "Image Set");
                                            }
                                            if (list.get(i).getObjdata().getRedirect_url() != null && !list.get(i).getObjdata().getRedirect_url().isEmpty()) {
                                                _redirect_link = list.get(i).getObjdata().getRedirect_url();
                                                Log.e("TAGGG", "Image Set Redirect URL set " + _redirect_link);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at set counter " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(Call<GetCategoryPostModel> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            int permission = ActivityCompat.checkSelfPermission(ImportImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                Toast.makeText(ImportImagesActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.deny_storage_permission, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.deny_storage_permission);
                return;
            } else {
                Intent intent = new Intent(ImportImagesActivity.this, Gallery.class);
                intent.putExtra("title", "Select Images");
                intent.putExtra("mode", 1);
                intent.putExtra("maxSelection", 500);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ImportImagesActivity.this, constants.IMPORT_SCREEN_IMPORT_IMAGES, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(ImportImagesActivity.this, constants.IMPORT_SCREEN_IMPORT_IMAGES);
                startActivityForResult(intent, PICK_IMAGE_TO_IMPORT);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception on onRequestPermissionsResult " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ImportImagesActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, ImportImagesActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, ImportImagesActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called CategoryActivity 1176");
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
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ImportImagesActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, ImportImagesActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, ImportImagesActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called CategoryActivity 1198");
                    MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
//        if (kAdView != null) {
//            kAdView.destroy();
//        }
//        if (mInterstitialAd != null) {
//            mInterstitialAd.destroy();
//        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
