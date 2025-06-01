package com.paintology.lite.photorealistic.drawing.minipaint;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.paintology.lite.photorealistic.drawing.Activity.IntroActivity;
import com.paintology.lite.photorealistic.drawing.Activity.shared_pref.SharedPref;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.Model.LocalityData;
import com.paintology.lite.photorealistic.drawing.Model.SlideInfo;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.InterstitialOnLoadCallBack;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.InterstitialOnShowCallBack;
import com.paintology.lite.photorealistic.drawing.ads.koin.DIComponent;
import com.paintology.lite.photorealistic.drawing.databinding.ActivityFirstScreenBinding;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.onboarding.utils.Events;
import com.paintology.lite.photorealistic.drawing.ui.login.GuestUtils;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;

public class FirstScreen extends AppCompatActivity {

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    long cacheExpiration = 43200;
    StringConstants constants = new StringConstants();

    boolean isLangSelected = false;

    FirebaseFirestore db_firebase;
    String ses_id = "";
    String city;
    String _ip;
    String country = "";

    SharedPref sharedPref;
    SharedPreferences painterDbPref;

    DIComponent diComponent;

 /*   private final ActivityResultLauncher<Intent> loginActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (getIntent() != null && getIntent().hasExtra("target_type")) {
                        Intent intent = new Intent(FirstScreen.this, GalleryDashboard.class);
                        if (getIntent().hasExtra("notification_id")) {
                            intent.putExtra("notification_id", getIntent().getStringExtra("notification_id"));
                        }
                        intent.putExtra("target_type", getIntent().getStringExtra("target_type"));
                        intent.putExtra("target_name", getIntent().getStringExtra("target_name"));
                        intent.putExtra("target_id", getIntent().getStringExtra("target_id"));
                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(FirstScreen.this, GalleryDashboard.class));
                        finish();
                    }
                }
            });*/

    public static FirstScreen mActivity;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable adsRunner = new Runnable() {
        @Override
        public void run() {
            checkAdvertisement();
        }
    };

    private int mCounter = 0;
    private boolean isNativeLoadedOrFailed = false;
    private boolean isScreenShown = true;
    private ActivityFirstScreenBinding binding;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        diComponent = new DIComponent();
        mActivity = this;
        sharedPref = new SharedPref(FirstScreen.this);
        painterDbPref = getSharedPreferences("PaintologyDB", Context.MODE_PRIVATE);

        if (BuildConfig.DEBUG) {
            cacheExpiration = 0;
        } else {
            cacheExpiration = 43200L; // 12 hours same as the default value
        }

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .build();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        // [END get_remote_config_instance]
        FirebaseFirestore.setLoggingEnabled(true);
        db_firebase = FirebaseFirestore.getInstance();

        KGlobal._session_model.get_event_list().clear();
        KGlobal._session_model.setDoc_name("");
        KGlobal._session_model.set_index(0);
        KGlobal._session_model.get_map().clear();

        sendStartEvent();

        binding.btnRetry.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.cvError.setVisibility(View.GONE);
            doLogin();
        });
        String session_name = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ses_id = session_name + "_" + String.valueOf(System.currentTimeMillis());
        constants.putString(constants._android_device_id, ses_id, FirstScreen.this);
        Log.e("TAG", "Android ID from If Splafsh " + session_name);

        if (constants.getString(constants.isLanguageSelected, FirstScreen.this).equalsIgnoreCase("true")) {

            isLangSelected = true;
        }

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. Also use Remote Config
        // Setting to set the minimum fetch interval.
        // [START enable_dev_mode]
        try {
            Intent receivedIntent = getIntent();
            String receivedAction = receivedIntent.getAction();
            //find out what we are dealing with
            String receivedType = receivedIntent.getType();
            try {
                if (receivedAction.equals(Intent.ACTION_SEND)) {
                    //content is being shared
                    if (receivedType.startsWith("image/")) {
                        //handle sent image
                        //get the uri of the received image
                        Uri receivedUri = (Uri) receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                        Log.e("TAG", "receivedType receivedUri " + receivedUri + " uri2 " + receivedIntent.getData());

                        if (receivedUri != null) {
                            //set the picture
                            String _path = getFilePath(this, receivedUri);
                            Log.e("TAG", "receivedType receivedUri " + receivedUri + " _path " + _path);
                            File file = new File(_path);
                            Intent intent = new Intent(this, PaintActivity.class);
                            intent.setAction("LoadWithoutTrace");
                            intent.putExtra("path", file.getName());
                            intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                            intent.putExtra("isPickFromOverlaid", true);
                            startActivityForResult(intent, 111);
                            finish();
                            return;
                        } else if (receivedIntent != null && receivedIntent.getData() != null) {
                            String _path = getFilePath(this, (Uri) receivedIntent.getData());
                            Log.e("TAG", "receivedType receivedUri " + receivedUri + " _path " + receivedIntent.getData().getPath());
                            File file = new File(_path);
                            Intent intent = new Intent(this, PaintActivity.class);
                            intent.setAction("LoadWithoutTrace");
                            intent.putExtra("path", file.getName());
                            intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                            intent.putExtra("isPickFromOverlaid", true);
                            startActivityForResult(intent, 111);
                            finish();
                            return;
                        }
                    } else {
                        Log.e("TAG", "Goto Else ");
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "Exception at receive " + e.getMessage(), e);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }


        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                try {
                    String userId = constants.getString(constants.UserId, FirstScreen.this);
                    if (!userId.equalsIgnoreCase("") && FireUtils.isInteger(userId)) {
                        constants.putString(constants.UserId, FirebaseAuth.getInstance().getCurrentUser().getUid(), FirstScreen.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_configue_default);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Splashscreen", "Exception: " + e.getMessage());
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activate();
                        } else {
                            Log.d("", "");
                        }

                        boolean isFacebookLoginSupport = mFirebaseRemoteConfig.getBoolean("is_facebook_login_support");
                        AppUtils.saveFacebookLoginSupport(FirstScreen.this, isFacebookLoginSupport);

                        String policyLink = mFirebaseRemoteConfig.getString("main_policy_link");
                        String termsLink = mFirebaseRemoteConfig.getString("main_terms_link");

                        AppUtils.saveLink(FirstScreen.this, AppUtils.POLICY_LINK, policyLink);
                        AppUtils.saveLink(FirstScreen.this, AppUtils.TERMS_LINK, termsLink);

                        constants.putString(StringConstants.apps_by_paintology, mFirebaseRemoteConfig.getString("apps_by_paintology"), FirstScreen.this);
                        constants.putString(StringConstants.daily_blog, mFirebaseRemoteConfig.getString("daily_blog"), FirstScreen.this);
                        constants.putString(StringConstants.learn_drawing_painting, mFirebaseRemoteConfig.getString("learn_drawing_painting"), FirstScreen.this);
                        constants.putString(StringConstants.online_tutorials, mFirebaseRemoteConfig.getString("online_tutorials"), FirstScreen.this);
                        constants.putString(StringConstants.paintology_website, mFirebaseRemoteConfig.getString("paintology_website"), FirstScreen.this);
                        constants.putString(StringConstants.youtube_paintology, mFirebaseRemoteConfig.getString("youtube_paintology"), FirstScreen.this);


                        // get Onboarding Slider info
                        int totalBanners = (int) mFirebaseRemoteConfig.getLong("total_pr_banners");
                        List<BannerModel> resBanners = new ArrayList<>();

                        for (int i = 1; i <= totalBanners; i++) {
                            String bannerImageUrl = mFirebaseRemoteConfig.getString("ad_pr_banner" + i + "_image").trim();
                            String bannerLInk = mFirebaseRemoteConfig.getString("ad_pr_banner" + i + "_link").trim();
                            resBanners.add(new BannerModel(bannerImageUrl, bannerLInk));
                        }

                        AppUtils.saveTotalBanners(FirstScreen.this, totalBanners);
                        AppUtils.saveBanners(FirstScreen.this, resBanners);

                        // get Onboarding Slider info
                        int totalDrawBanners = (int) mFirebaseRemoteConfig.getLong("draw_pr_total_banners");
                        List<BannerModel> drawBanners = new ArrayList<>();

                        for (int i = 1; i <= totalDrawBanners; i++) {
                            String bannerImageUrl = mFirebaseRemoteConfig.getString("draw_pr_banner" + i + "_image").trim();
                            String bannerLInk = mFirebaseRemoteConfig.getString("draw_pr_banner" + i + "_link").trim();
                            drawBanners.add(new BannerModel(bannerImageUrl, bannerLInk));
                        }

                        AppUtils.saveTotalDrawBanners(FirstScreen.this, totalDrawBanners);
                        AppUtils.saveDrawBanners(FirstScreen.this, drawBanners);

                        int totalTutBanners = (int) mFirebaseRemoteConfig.getLong("tut_pr_total_banners");
                        List<BannerModel> tutBanners = new ArrayList<>();

                        for (int i = 1; i <= totalTutBanners; i++) {
                            String bannerImageUrl = mFirebaseRemoteConfig.getString("tut_pr_banner" + i + "_image").trim();
                            String bannerLInk = mFirebaseRemoteConfig.getString("tut_pr_banner" + i + "_link").trim();
                            tutBanners.add(new BannerModel(bannerImageUrl, bannerLInk));
                        }

                        AppUtils.saveTotalTutBanners(FirstScreen.this, totalTutBanners);
                        AppUtils.saveTutBanners(FirstScreen.this, tutBanners);


                        // get Onboarding Slider info
                        int totalSlides = (int) mFirebaseRemoteConfig.getLong("Gam_total_slides");

                        List<SlideInfo> slideInfos = new ArrayList<>();

                        for (int i = 1; i <= totalSlides; i++) {
                            String slideImageUrl = mFirebaseRemoteConfig.getString("Gam_slide_" + i + "_image_url").trim();
                            String slideTitle = mFirebaseRemoteConfig.getString("Gam_slide_" + i + "_title").trim();
                            String slideDescription = mFirebaseRemoteConfig.getString("Gam_slide_" + i + "_description").trim();

                            SlideInfo slideInfo = new SlideInfo();
                            slideInfo.setSlideTitle(slideTitle);
                            slideInfo.setSlideDescription(slideDescription);
                            slideInfo.setSlideImageUrl(slideImageUrl);

                            slideInfos.add(slideInfo);
                        }

                        Log.w("TAGslideInfos", "onComplete: " + slideInfos);
                        AppUtils.saveTotalSlides(FirstScreen.this, totalSlides);
                        AppUtils.saveSliders(FirstScreen.this, slideInfos);

                        // get Home Slider info
                        int totalHomeSlides = (int) mFirebaseRemoteConfig.getLong("total_home_slides");
                        int slideInterval = (int) mFirebaseRemoteConfig.getLong("home_slide_interval");

                        List<SlideInfo> homeSlideInfos = new ArrayList<>();

                        for (int i = 1; i <= totalHomeSlides; i++) {
                            String slideImageUrl = mFirebaseRemoteConfig.getString("home_slide_" + i + "_image_url").trim();
                            String slideName = mFirebaseRemoteConfig.getString("home_slide_" + i + "_name").trim();
                            String slideUrl = mFirebaseRemoteConfig.getString("home_slide_" + i + "_url").trim();

                            SlideInfo slideInfo = new SlideInfo();
                            slideInfo.setSlideName(slideName);
                            slideInfo.setSlideImageUrl(slideImageUrl);
                            slideInfo.setSlideUrl(slideUrl);

                            homeSlideInfos.add(slideInfo);
                        }

                        AppUtils.saveTotalHomeSlides(FirstScreen.this, totalHomeSlides);
                        AppUtils.saveHomeSliders(FirstScreen.this, homeSlideInfos);
                        AppUtils.saveHomeSlideInterval(FirstScreen.this, slideInterval);

                        int minimumLaunchCountForRating = (int) mFirebaseRemoteConfig.getLong("minimum_launch_count_for_rating");
                        constants.putInt("minimum_launch_count_for_rating", minimumLaunchCountForRating, FirstScreen.this);

                        String Dating_Image_Url = mFirebaseRemoteConfig.getString("Dating_Image_Url").trim();
                        String Dating_Image_RecirectLink = mFirebaseRemoteConfig.getString("Dating_Image_RecirectLink").trim();
                        String community_snap = mFirebaseRemoteConfig.getString("community_snap").trim();
                        constants.putString("community_snap", community_snap, FirstScreen.this);

                        Log.e("TAG", "community_snap at splash " + community_snap);
                        String Ferdouse_Image_Url = mFirebaseRemoteConfig.getString("Ferdouse_Image_Url").trim();
                        String Ferdouse_Image_RecirectLink = mFirebaseRemoteConfig.getString("Ferdouse_Image_RecirectLink").trim();
                        constants.putString("dating_image_url", Dating_Image_Url, FirstScreen.this);
                        constants.putString("dating_image_redirect_link", Dating_Image_RecirectLink, FirstScreen.this);
                        constants.putString("ferdouse_image_url", Ferdouse_Image_Url, FirstScreen.this);
                        constants.putString("ferdouse_image_redirect_link", Ferdouse_Image_RecirectLink, FirstScreen.this);

                        String mypaintings_youtube_url = mFirebaseRemoteConfig.getString("mypaintings_youtube_url").trim();
                        String mypaintings_youtube_thumb = mFirebaseRemoteConfig.getString("mypaintings_youtube_thumb").trim();

                        constants.putString(constants.mypaintings_youtube_url, mypaintings_youtube_url, FirstScreen.this);
                        constants.putString(constants.mypaintings_youtube_thumb, mypaintings_youtube_thumb, FirstScreen.this);

                        String mymovies_youtube_url = mFirebaseRemoteConfig.getString("mymovies_youtube_url").trim();
                        String mymovies_youtube_thumb = mFirebaseRemoteConfig.getString("mymovies_youtube_thumb").trim();

                        constants.putString(constants.mymovies_youtube_url, mymovies_youtube_url, FirstScreen.this);
                        constants.putString(constants.mymovies_youtube_thumb, mymovies_youtube_thumb, FirstScreen.this);

                        String links_for_you_website = mFirebaseRemoteConfig.getString("links_for_you_website").trim();
                        String links_for_you_youtube = mFirebaseRemoteConfig.getString("links_for_you_youtube").trim();
                        String links_for_you_learn_drawing = mFirebaseRemoteConfig.getString("links_for_you_learn_drawing").trim();
                        String links_for_you_apps = mFirebaseRemoteConfig.getString("links_for_you_apps").trim();

                        constants.putString(constants.links_for_you_website, links_for_you_website, FirstScreen.this);
                        constants.putString(constants.links_for_you_youtube, links_for_you_youtube, FirstScreen.this);
                        constants.putString(constants.links_for_you_learn_drawing, links_for_you_learn_drawing, FirstScreen.this);
                        constants.putString(constants.links_for_you_apps, links_for_you_apps, FirstScreen.this);

                        displayCanvasScreen = mFirebaseRemoteConfig.getString("main_entry_canvas_screen").trim();
                        int pref_clr = constants.getInt("background_color", FirstScreen.this);

                       /* String interval_time = mFirebaseRemoteConfig.getString("clouddb_send_interval").trim();
                        long millis = Long.parseLong(interval_time) * 60 * 1000;

                        KGlobal.get_session_model().setInterval_time(millis);
                        try {
                            startService(new Intent(FirstScreen.this, SendSessionEvent.class));
                        } catch (Exception e) {
                            Log.e("TAG", "Exception at startService " + e.getMessage());
                        }*/


                        if (FirebaseAuth.getInstance().getCurrentUser() != null || diComponent.getSharedPreferenceUtils().isFourStepCompleted()) {
                            binding.progressBar.setVisibility(View.VISIBLE);
                            binding.content.setVisibility(View.VISIBLE);
                            fetchRemoteConfiguration();
                        } else {
                            getLocation();
                        }
                    }
                });
        binding.nextLL.setOnClickListener(v -> {
            if (diComponent.getSharedPreferenceUtils().isFourStep() && !diComponent.getSharedPreferenceUtils().isFourStepCompleted()) {
                FirebaseUtils.logEvents(this, Events.EVENT_SPLASH);
                startActivity(new Intent(FirstScreen.this, IntroActivity.class));
                finish();
            } else {
                Intent intent = new Intent(FirstScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

      /*  city = constants.getString(constants.UserCity, FirstScreen.this);
        _ip = constants.getString(constants.IpAddress, FirstScreen.this);
        country = constants.getString(constants.UserCountryCode, FirstScreen.this);

        if (city.isEmpty() || _ip.isEmpty() || country.isEmpty()) {
            getLocalityDataAPI();
        }*/

    }

    void getLocation() {
        getLocalityDataAPI();
    }

    private void moveNext() {
        if (diComponent.getAdmobInterstitialAds().isInterstitialLoaded() && diComponent.getSharedPreferenceUtils().getRcvInterSplash() != 0) {
            diComponent.getAdmobInterstitialAds().showInterstitialAd(this,
                    new InterstitialOnShowCallBack() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            goToNextScreen();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent() {
                            goToNextScreen();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {

                        }

                        @Override
                        public void onAdImpression() {

                        }
                    });
        } else {
            goToNextScreen();
        }
    }

    private void goToNextScreen() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            delayAndLaunchNextScreen();
        } else {
            delayAndLaunchNextScreen();
        }
    }

    private void fetchRemoteConfiguration() {
        diComponent.getRemoteConfiguration().checkRemoteConfig(success -> {
            if (success) {
                mCounter = 0;
                passAppID();
                startHandler();
                loadInter();
                diComponent.getAdmobOpenApp().fetchAd();
            } else {
                mHandler.removeCallbacks(adsRunner);
                moveNext();
            }
            return null;
        });
    }

    private void passAppID() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (BuildConfig.DEBUG) {
                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", "ca-app-pub-3940256099942544~3347511713");
            } else {
                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", diComponent.getSharedPreferenceUtils().getRcvAdmobAppID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    private void loadInter() {
        if (diComponent.getSharedPreferenceUtils().getRcvInterSplash() == 0) {
            isNativeLoadedOrFailed = true;
        } else {
            if (BuildConfig.DEBUG) {
                diComponent.getAdmobInterstitialAds().loadInterstitialAd(this,
                        "ca-app-pub-3940256099942544/1033173712",
                        diComponent.getSharedPreferenceUtils().getRcvInterSplash(),
                        diComponent.getSharedPreferenceUtils().isAppPurchased(),
                        diComponent.getInternetManager().isInternetConnected(),
                        new InterstitialOnLoadCallBack() {
                            @Override
                            public void onAdFailedToLoad(@NonNull String adError) {
                                isNativeLoadedOrFailed = true;
                            }

                            @Override
                            public void onAdLoaded() {
                                isNativeLoadedOrFailed = true;
                            }

                            @Override
                            public void onPreloaded() {

                            }
                        });
            } else {
                diComponent.getAdmobInterstitialAds().loadInterstitialAd(this,
                        diComponent.getSharedPreferenceUtils().getRcvInterID(),
                        diComponent.getSharedPreferenceUtils().getRcvInterSplash(),
                        diComponent.getSharedPreferenceUtils().isAppPurchased(),
                        diComponent.getInternetManager().isInternetConnected(),
                        new InterstitialOnLoadCallBack() {
                            @Override
                            public void onAdFailedToLoad(@NonNull String adError) {
                                isNativeLoadedOrFailed = true;
                            }

                            @Override
                            public void onAdLoaded() {
                                isNativeLoadedOrFailed = true;
                            }

                            @Override
                            public void onPreloaded() {

                            }
                        });
            }
        }
    }

    private void startHandler() {
        mHandler.post(adsRunner);
    }

    private void stopHandler() {
        mCounter = 0;
        mHandler.removeCallbacks(adsRunner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(adsRunner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(adsRunner);
    }

    private void checkAdvertisement() {
        if (mCounter < 16) {
            try {
                mCounter++;
                if (isNativeLoadedOrFailed) {
                    mHandler.removeCallbacks(adsRunner);
                    if (isScreenShown) {
                        isScreenShown = false;
                        moveNext();
                    }
                } else {
                    mHandler.removeCallbacks(adsRunner);
                    mHandler.postDelayed(adsRunner, 1000);
                }
            } catch (Exception e) {
                Log.e("AdsInformation", e.getMessage());
            }
        } else {
            isNativeLoadedOrFailed = true;
            mHandler.removeCallbacks(adsRunner);
            if (isScreenShown) {
                isScreenShown = false;
                moveNext();
            }
        }
    }


    private void delayAndLaunchNextScreen() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            if (diComponent.getSharedPreferenceUtils().isFourStep() && !diComponent.getSharedPreferenceUtils().isFourStepCompleted()) {
                startActivity(new Intent(FirstScreen.this, IntroActivity.class));
                finish();
            } else {
                Intent intent = new Intent(FirstScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            if (getIntent() != null && getIntent().hasExtra("target_type")) {
                Intent intent = new Intent(FirstScreen.this, GalleryDashboard.class);
                if (getIntent().hasExtra("notification_id")) {
                    intent.putExtra("notification_id", getIntent().getStringExtra("notification_id"));
                }
                intent.putExtra("target_type", getIntent().getStringExtra("target_type"));
                intent.putExtra("target_name", getIntent().getStringExtra("target_name"));
                intent.putExtra("target_id", getIntent().getStringExtra("target_id"));
                startActivity(intent);
                finish();
            } else {
                startActivity(new Intent(FirstScreen.this, GalleryDashboard.class));
                finish();
            }
        }
    }


    private void saveSessionOnFirebaseDB() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            KGlobal.get_session_model().set_start_time(currentDateandTime);

            KGlobal.get_session_model().get_map().put(constants.document_name, KGlobal.get_session_model().get_event_list());
            KGlobal.get_session_model().setDoc_name(constants.document_name);
            Log.e("TAG", "Session Data Doc Name " + constants.document_name + " currentDateandTime " + currentDateandTime);
            db_firebase.collection(constants.collection_name).document(ses_id).set(KGlobal.get_session_model().get_map(), SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("TAG", "OnSuccess called");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "EXception on failure " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }


    /*This method will call when app start and send start app event to mixpanel dashboard.*/
    void sendStartEvent() {

        /* *getDeviceId() returns the unique device ID.
         * For example, the IMEI for GSM and the MEID or ESN for CDMA phones.*/
        try {
            // We also identify the current user with a distinct ID, and
            // register ourselves for push notifications from Mixpanel.
            FirebaseUtils.SetInstance(FirstScreen.this);
            if (BuildConfig.DEBUG) {
                Toast.makeText(FirstScreen.this, constants.getAPP_START(), Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.getAPP_START());
        } catch (SecurityException e) {
            Log.e("TAG", "SecurityException while track " + e.getMessage());
        } catch (Exception e) {
            Log.e("TAG", "Exception while track " + e.getMessage());
        }
    }

    String displayCanvasScreen = "false";

    public String getFilePath(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;

        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }


        if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGoogleDriveUri(uri)) {
                return RetriveDriveImage(FirstScreen.this, uri);
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /*TODO Mine code*/

    private String RetriveDriveImage(final Context context, final Uri uri) {
        Uri returnUri = uri;
        if (returnUri == null)
            return "";
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("TAG", "Check image present Size" + "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("TAG", "Check image present Path" + "Path " + file.getPath());
        } catch (Exception e) {
            Log.e("Exception", Objects.requireNonNull(e.getMessage()));
        }
        Log.e("TAG", "Check image present Size" + "Size " + file.length());
        return file.getPath();
    }

    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    ApiInterface apiInterface;
    Call<LocalityData> Localitycall;

    void getLocalityDataAPI() {
        if (Localitycall != null) {
            Localitycall.cancel();
        }
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Localitycall = apiInterface.getLocalityData("https://us-central1-even-scheduler-265110.cloudfunctions.net/geolocation");
        try {
            Localitycall.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<LocalityData> call, retrofit2.Response<LocalityData> response) {
                    try {
                        if (response != null) {
                            if (response.body() != null) {

                                Log.e("TAGRR", response.body().toString());
                                if (response.body().getCity() != null) {
                                    constants.putString(constants.UserCity, response.body().getCity(), FirstScreen.this);
                                    sharedPref.putString("userCity", response.body().getCity());
                                }

                                if (response.body().getUserIP() != null) {
                                    constants.putString(constants.IpAddress, response.body().getUserIP(), FirstScreen.this);
                                    sharedPref.putString("userIp", response.body().getUserIP());
                                }

                                if (response.body().getCountry() != null && !response.body().getCountry().isEmpty()) {
                                    constants.putString(constants.UserCountryCode, response.body().getCountry(), FirstScreen.this);
                                    sharedPref.putString("userCountryCode", response.body().getCountry());
                                }

                              /*  if (response.body().getCityData() != null && response.body().getCityData().size() > 0) {
                                    if (response.body().getCityData().get(0).getCountry() != null)
                                        constants.putString(constants.UserCountry, response.body().getCityData().get(0).getCountry(), FirstScreen.this);
                                    sharedPref.putString("userCountry", response.body().getCityData().get(0).getCountry());

                                } else {
                                    if (response.body().getCountry() != null && !response.body().getCountry().isEmpty())
                                        constants.putString(constants.UserCountry, response.body().getCountry(), FirstScreen.this);
                                    sharedPref.putString("userCountry", response.body().getCountry());
                                }*/
                            }
                        }

                        if (BuildConfig.DEBUG) {
                            Log.e("TAG", "LocalityData response country " + response.body().getCountry());
                        }


                    } catch (Exception e) {
                        Log.e("TAG", "Exception at set counter locality data " + e.getMessage(), e);
                    }

                    doLogin();

                }

                @Override
                public void onFailure(Call<LocalityData> call, Throwable t) {
                    try {
                        Log.e("TAG", "OnFailure on IpAddress" + t.getMessage());
                    } catch (Exception e) {
                        Log.e("TAG", "Exception onFailure " + e.getMessage());
                    }
                    doLogin();
                }
            });
        } catch (Exception e) {
            doLogin();
            Log.e("TAG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    void doLogin() {
        new GuestUtils().login(FirstScreen.this, constants.getString(constants.UserCountryCode, FirstScreen.this), new Function2<Boolean, String, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean, String s) {
                if (aBoolean) {
                    startActivity(new Intent(FirstScreen.this, GalleryDashboard.class));
                    finish();
                } else {
                    binding.message.setText(s + "");
                    binding.progressBar.setVisibility(View.GONE);
                    binding.cvError.setVisibility(View.VISIBLE);
                }
                return null;
            }
        });
    }


}
