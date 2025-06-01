package com.paintology.lite.photorealistic.drawing.gallery;

import static android.content.Intent.ACTION_VIEW;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.common.reflect.TypeToken;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.Any;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.AboutActivity;
import com.paintology.lite.photorealistic.drawing.Activity.AwardActivity;
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity;
import com.paintology.lite.photorealistic.drawing.Activity.IntroActivity;
import com.paintology.lite.photorealistic.drawing.Activity.big_points.BigPointActivity;
import com.paintology.lite.photorealistic.drawing.Activity.country.SelectCountryActivity;
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserProfile;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity;
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity;
import com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities.NotificationActivity;
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewActivity;
import com.paintology.lite.photorealistic.drawing.Activity.settings.SettingActivity;
import com.paintology.lite.photorealistic.drawing.Activity.shared_pref.SharedPref;
import com.paintology.lite.photorealistic.drawing.Activity.support.SupportActivity;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.ProgressActivity;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.UserPointActivity;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils;
import com.paintology.lite.photorealistic.drawing.Activity.video_intro.IntroVideoListActivity;
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingActivity;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity;
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.NewSubCategoryActivity;
import com.paintology.lite.photorealistic.drawing.Enums.DialogType;
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type;
import com.paintology.lite.photorealistic.drawing.LanguageActivity;
import com.paintology.lite.photorealistic.drawing.LinksForYouActivity;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.LanguageModel;
import com.paintology.lite.photorealistic.drawing.Model.LocalityData;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailResponse;
import com.paintology.lite.photorealistic.drawing.Model.SearchResponse;
import com.paintology.lite.photorealistic.drawing.Model.Tutorial;
import com.paintology.lite.photorealistic.drawing.Model.VideosAndFile;
import com.paintology.lite.photorealistic.drawing.Model.firebase.Brush;
import com.paintology.lite.photorealistic.drawing.Model.firebase.FirebaseTutorial;
import com.paintology.lite.photorealistic.drawing.Model.firebase.SearchContentResponse;
import com.paintology.lite.photorealistic.drawing.Model.firebase.SearchNumberResponse;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.Tooltip.Tooltip;
import com.paintology.lite.photorealistic.drawing.Tooltip.TooltipAnimation;
import com.paintology.lite.photorealistic.drawing.Youtube.utils.Utils;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.BannerCallBack;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnLoadCallBack;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnShowCallBack;
import com.paintology.lite.photorealistic.drawing.ads.enums.CollapsiblePositionType;
import com.paintology.lite.photorealistic.drawing.bus.UserLoginUpdateEvent;
import com.paintology.lite.photorealistic.drawing.challenge.view.ChallengeActivity;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.databinding.ActivityEditTutorialBinding;
import com.paintology.lite.photorealistic.drawing.databinding.DialogRewardBinding;
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBannerResourcesBinding;
import com.paintology.lite.photorealistic.drawing.databinding.LayoutUpdateChangeBinding;
import com.paintology.lite.photorealistic.drawing.databinding.VersionDialogLayoutBinding;
import com.paintology.lite.photorealistic.drawing.findAbility.FindYourAbilityActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.MyResources;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.onboarding.OnboardingExample1Activity;
import com.paintology.lite.photorealistic.drawing.policy.PrivacyPolicyActivity;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.CircleImageView;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.CustomizedExceptionHandler;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.GetIpAddress;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.LoginUtils;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.MyServiceForRecording;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils;
import com.paintology.lite.photorealistic.drawing.util.RoundRectCornerImageView;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;
import com.paintology.lite.photorealistic.drawing.util.transforms.CircleTransform;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;


public class GalleryDashboard extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private ActivityEditTutorialBinding binding;

    private static final String TAG = "Dashboard";
    private GoogleSignInClient mGoogleSignInClient;
    Call<GetCategoryPostModel> call;
    Call<LocalityData> Localitycall;
    boolean isLoggedIn;
    ProgressDialog progressDialog = null;
    String LoginInPaintology;
    File defaultFolder, downloadedFolder, myPaintingFolder;
    Intent mlIntent;
    private int SELECT_PHOTO_REQUEST = 400;
    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;
    ImageLoader mImageLoader;
    StringConstants constants = new StringConstants();
    private int SELECT_BACKGROUND_COLOR_REQUEST = 300;
    ApiInterface apiInterface;
    FirebaseFirestore db_firebase;
    ImageView iv_begin_doodle;
    public boolean isCollectionImageSet = false;
    ImageView iv_chat_icon;
    String current_lan = "";
    ArrayList<LanguageModel> _lst = new ArrayList<>();
    ImageView iv_profile_icon;
    ProgressBar pbar;
    DialogType dialogType_selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private int appUsedCount;
    private ReviewManager reviewManager;
    private LinearLayout ll_search_container;
    private ConstraintLayout btn_leader_boaard;
    ImageView btn_search_header;
    EditText edt_hash_search;
    TextView tv_total_amount, tv_level;
    ImageView btn_search;
    String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    private AppDatabase db;

    Boolean isUserDataSend = false;

    List<BannerModel> data = new ArrayList<>();

    RoundRectCornerImageView MyPainting;
    RoundRectCornerImageView ll_community;
    RoundRectCornerImageView btnBigPoints;
    RoundRectCornerImageView GalleryBtn;

    LinearLayout ll_import_image;
    LinearLayout ll_tutorials;

    public static GalleryDashboard mActivity;

    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> loginActivityLauncher;
    private DrawerLayout drawerLayout;
    private int totalTutorialCount;

    private String levelList = "";
    private String searchedSubCatId;

    private boolean isNumberSearch = false;

    SharedPref sharedPref;


    boolean islogiFromFB = false;
    boolean islogiFromGoogle = false;

    private List<Class> randomClass = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
//                    PermissionUtils.requestStoragePermission(GalleryDashboard.this, 12);
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    FirebaseFunctions mFunctions;


    private DatabaseReference presenceRef;
    private DatabaseReference connectedRef;
    private FirebaseAuth auth;
    private ImageView ivFavorite;
    private FrameLayout frameLayout;

  /*  private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable adsRunner = new Runnable() {
        @Override
        public void run() {
            checkAdvertisement();
        }
    };*/

    private void fetchData() {

        data = AppUtils.getBanners(this);
        for (int i = 0; i < data.size(); i++) {
            LayoutBannerResourcesBinding bannerBinding = LayoutBannerResourcesBinding.inflate(getLayoutInflater());
            Picasso.get().load(Uri.parse(data.get(i).bannerImageUrl)).into(bannerBinding.ivOwnAdv);
            int finalI = i;
            bannerBinding.cvAdv.setOnClickListener(v -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this,
                                constants.ad_XX_dashboard_banner_click.replace("XX", String.valueOf(finalI)),
                                Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(
                            GalleryDashboard.this,
                            constants.ad_XX_dashboard_banner_click.replace("XX", String.valueOf(finalI))
                    );
                    KGlobal.openInBrowser(GalleryDashboard.this, data.get(finalI).bannerLInk);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            });

            ((LinearLayout) findViewById(R.id.llAds)).addView(bannerBinding.getRoot());
        }
    }


    public void checkONline() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference presenceRef = database.getReference("users")
                    .child(currentUser.getUid())
                    .child("status");

            connectedRef = database.getReference(".info/connected");

            // Set up presence listener
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);

                    if (connected) {
                        Log.e(TAG, "Firebase connected: true");
                        // User is connected
                        presenceRef.setValue("online");
                        presenceRef.onDisconnect().setValue("offline");
                        Log.e(TAG, "User presence set to true");
                    } else {
                        Log.e(TAG, "Firebase connected: false");
                        // User is disconnected
                        presenceRef.setValue("offline");
                        Log.e(TAG, "User presence set to false");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase onCancelled: " + error.getMessage());
                    // Handle potential errors
                }
            });
        } else {
            Log.e(TAG, "Current user is null. User not authenticated.");
            // Handle the case where the current user is not authenticated
        }
    }

    private int mCounter = 0;
    private boolean isInterstitialLoadOrFailed = false;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        try {
            if (getIntent() != null && getIntent().hasExtra("target_type")) {
                if (getIntent().getStringExtra("target_type").equalsIgnoreCase("chat")) {
                    if (getIntent().getStringExtra("target_id") != null && !getIntent().getStringExtra("target_id").equalsIgnoreCase("")) {
                        Intent intent = new Intent(GalleryDashboard.this, ChatActivity.class);
                        intent.putExtra("room_id", getIntent().getStringExtra("target_id"));
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(this, ChatUserList.class));
                    }
                } else {
                    if (!getIntent().getStringExtra("target_name").equalsIgnoreCase("welcome")) {
                        Intent intent = new Intent(GalleryDashboard.this, NotificationActivity.class);
                        intent.putExtra("notification_id", getIntent().getStringExtra("notification_id"));
                        intent.putExtra("target_type", getIntent().getStringExtra("target_type"));
                        intent.putExtra("target_name", getIntent().getStringExtra("target_name"));
                        intent.putExtra("target_id", getIntent().getStringExtra("target_id"));
                        startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* SharedPreferences sf = getSharedPreferences("shareprefrence", Context.MODE_PRIVATE);
        String checkNotification = sf.getString("target_type", "");
        String target_id = sf.getString("target_id", "");
        if (!checkNotification.isEmpty()) {
            if (checkNotification.equalsIgnoreCase("chat")) {
                String requiredPart = target_id.split("_")[1];
                Intent _intent = new Intent(this, ChatActivity.class);
                _intent.putExtra("userUid", requiredPart);
                startActivity(_intent);
            } else {
                Intent notificationIntent = new Intent(this, NotificationActivity.class);
                notificationIntent.putExtra("isTarget", true);
                startActivity(notificationIntent);
            }
        }*/

        mFunctions = FirebaseFunctions.getInstance();

        levelList = "";
        auth = FirebaseAuth.getInstance();


// Check if the current user is authenticated
        try {
            checkONline();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String _local = constants.getString(constants.selected_language, GalleryDashboard.this);
        current_lan = _local;
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        db = MyApplication.getDb();

        sharedPreferences = getApplicationContext().getSharedPreferences("PaintologyDB", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        reviewManager = ReviewManagerFactory.create(this);

        FacebookSdk.sdkInitialize(this);
        binding = ActivityEditTutorialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.e("TAGG", "GalleryDashboard called");


        fetchData();
        int profileId = R.drawable.paintology_logo;
        if (constants.getBoolean(constants.IsGuestUser, this)) {
            profileId = R.drawable.img_default_avatar;
        }

        try {
            binding.includedHeader.navHeaderTitle.setText(constants.getString(constants.Username, this));
            Picasso.get().load(Uri.parse(constants.getString(constants.ProfilePicsUrl, GalleryDashboard.this)))
                    .transform(new CircleTransform())
                    .placeholder(profileId)
                    .error(profileId)
                    .into(binding.includedHeader.navHeaderPic);
            CircleImageView profilePic = findViewById(R.id.iv_profile);
            TextView profileName = findViewById(R.id.tv_name);
            profileName.setText(constants.getString(constants.Username, this));
            Picasso.get().load(Uri.parse(constants.getString(constants.ProfilePicsUrl, GalleryDashboard.this)))
                    .transform(new CircleTransform())
                    .placeholder(profileId)
                    .error(profileId)
                    .into(profilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frameLayout = findViewById(R.id.ads_banner_place_holder);
        if (BuildConfig.DEBUG) {
            getDiComponent().getAdmobBannerAds().loadBannerAds(this,
                    frameLayout,
                    "ca-app-pub-3940256099942544/2014213617",
                    getDiComponent().getSharedPreferenceUtils().getRcvBannerDashBoard(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    CollapsiblePositionType.NONE,
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
            getDiComponent().getAdmobBannerAds().loadBannerAds(this,
                    frameLayout,
                    getDiComponent().getSharedPreferenceUtils().getRcvBannerID(),
                    getDiComponent().getSharedPreferenceUtils().getRcvBannerDashBoard(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    CollapsiblePositionType.NONE,
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
        setTitle(R.string.app_name);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(GalleryDashboard.this);

        if (isLoggedIn) {
            islogiFromFB = true;
            islogiFromGoogle = false;
        } else if (account != null) {
            islogiFromFB = false;
            islogiFromGoogle = true;
        }

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("TAGGG", "Device ID android_id " + android_id);

        FirebaseFirestore.setLoggingEnabled(true);
        db_firebase = FirebaseFirestore.getInstance();
        checkCurrentVersion();
        pbar = findViewById(R.id.pbar);
        root = findViewById(R.id.container);
        btn_search_header = findViewById(R.id.btn_search_header);
        ll_search_container = findViewById(R.id.ll_search_container);
        btn_leader_boaard = findViewById(R.id.btn_leader_boaard);
        edt_hash_search = findViewById(R.id.edt_hash_search);
        ivFavorite = findViewById(R.id.ivFavorite);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_level = findViewById(R.id.tv_level);
        AppCompatImageButton btn_notification = findViewById(R.id.btn_notification);
        MaterialTextView tvCounter = findViewById(R.id.materialTextViewCounter);
        AppCompatImageButton btn_settings = findViewById(R.id.btn_settings);
        View point = findViewById(R.id.point);
        View diamond = findViewById(R.id.diamond);
        ImageView ivStore = findViewById(R.id.ivStore);


        MyPainting = findViewById(R.id.MyPainting);
        ll_import_image = findViewById(R.id.ll_import_image);
        ll_community = findViewById(R.id.ll_community);
        ll_tutorials = findViewById(R.id.ll_tutorials);
        btnBigPoints = findViewById(R.id.btnBigPoints);
        GalleryBtn = findViewById(R.id.GalleryBtn);


        MyPainting.setOnClickListener(v -> {
            RedirectToGaller();
            DisableAllBtn();
        });

        ll_tutorials.setOnClickListener(v -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }
            gotogallery();
            DisableAllBtn();
        });

        btnBigPoints.setOnClickListener(v -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_BIG_POINTS, Toast.LENGTH_SHORT).show();
            }

            FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_BIG_POINTS);
            startActivity(new Intent(GalleryDashboard.this, BigPointActivity.class));
            DisableAllBtn();
        });


        ll_import_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_MY_RESOURCES, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_MY_RESOURCES);
                startActivity(new Intent(GalleryDashboard.this, MyResources.class));
                DisableAllBtn();
            }
        });

        ll_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                    KGlobal.showNetworkError(GalleryDashboard.this);
                    return;
                }

                if (!PermissionUtils.checkStoragePermission(GalleryDashboard.this)) {
//                    // We don't have permission so prompt the user
//                    PermissionUtils.requestStoragePermission(GalleryDashboard.this, REQ_COMMUNITY);
//                    return;
//                }
//                int permission = ActivityCompat.checkSelfPermission(GalleryDashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    // We don't have permission so prompt the user
//                    ActivityCompat.requestPermissions(
//                            GalleryDashboard.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQ_COMMUNITY
//                    );
//                    return;
//                }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_COMMUNITY, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_COMMUNITY);
                    startActivity(new Intent(GalleryDashboard.this, Community.class));
                    DisableAllBtn();
                }
            }
        });


        GalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextKt.sendUserEvent(StringConstants.home_button_gallery);
                hidetooltip();
                Intent intent = new Intent(GalleryDashboard.this, GalleryActivity.class);
                startActivity(intent);
                DisableAllBtn();
            }
        });


        ivStore.setOnClickListener(v -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            ContextKt.sendUserEvent(StringConstants.home_top_store);
            ContextKt.showStoreDialog(this);
        });

        ivFavorite.setOnClickListener(v -> {

            ContextKt.sendUserEvent(StringConstants.home_top_favorite);
            startActivity(new Intent(GalleryDashboard.this, FavActivity.class));
        });

        FirebaseUser uId = FirebaseAuth.getInstance().getCurrentUser();
        if (uId != null) {
            getNotificationsViewModel().loadNotifications(uId.getUid());
            getNotificationsViewModel().loadNotificationsForCount(uId.getUid());
        } else {
            // Handle the case where the user is not signed in
            Log.e("Error", "User not signed in");
            // You can redirect to a sign-in screen or show an error message
        }
        getNotificationsViewModel().getNotificationsCount().observe(this, list -> {
            if (list.isEmpty()) {
                tvCounter.setVisibility(View.INVISIBLE);
            } else {
                tvCounter.setText(list.size() + "");
                tvCounter.setVisibility(View.VISIBLE);
            }
        });

        btn_notification.setOnClickListener(v -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            binding.appBarMain.adLoading.getRoot().setVisibility(View.VISIBLE);
            loadInterRewardAd();
            // mHandler.post(adsRunner);
        });

        try {
            btn_leader_boaard.setOnClickListener(view -> {
                ContextKt.sendUserEvent(StringConstants.home_top_leaderboard);
                startActivity(new Intent(GalleryDashboard.this, LeaderBoardActivity.class));
            });
        } catch (Exception e) {

        }


        btn_settings.setOnClickListener(v -> {
            ContextKt.sendUserEvent(StringConstants.home_top_settings);
            startActivity(new Intent(GalleryDashboard.this, SettingActivity.class));
        });

        point.setOnClickListener(v -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            ContextKt.sendUserEvent(StringConstants.home_top_points_progress);
            Intent intent = new Intent(GalleryDashboard.this, UserPointActivity.class);
            startActivity(intent);
        });

        diamond.setOnClickListener(v -> {

        });

        btn_search_header.setOnClickListener(v -> {
            FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.search_header_textentry_query);
            if (!edt_hash_search.getText().toString().isEmpty()) {
                searchFromEditText();
            }
        });

        tv_total_amount.setOnClickListener(view -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            ContextKt.sendUserEvent(StringConstants.home_top_drawing_activity);
            Intent intent = new Intent(GalleryDashboard.this, ProgressActivity.class);
            startActivity(intent);
        });

        tv_level.setOnClickListener(view -> {

            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
            ContextKt.sendUserEvent(StringConstants.home_top_levels);
            Intent intent = new Intent(GalleryDashboard.this, YourRankingActivity.class);
            startActivity(intent);
        });

        edt_hash_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                if (!edt_hash_search.getText().toString().isEmpty()) {
                    searchFromEditText();
                }
                return true;
            }
            return false;
        });


       /* new Handler().postDelayed(() -> {
            MyConstantsKt.checkForIntroVideo(this, StringConstants.intro_home);
        }, 2000);*/


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.client_id))
                .build();


        FireUtils.setPoints(this, findViewById(R.id.tv_points), findViewById(R.id.tv_total_amount));
        String json = constants.getString(constants.recent_Brush, GalleryDashboard.this);

        if (json.isEmpty()) {
            ArrayList<Integer> _recent_list = new ArrayList<>();
            _recent_list.add(81);
            _recent_list.add(576);
            _recent_list.add(80);
            _recent_list.add(112);
            Gson gson = new Gson();
            constants.putString(constants.recent_Brush, gson.toJson(_recent_list), GalleryDashboard.this);
        }


        mGoogleSignInClient = GoogleSignIn.getClient(GalleryDashboard.this, gso);

        defaultFolder = new File(KGlobal.getDefaultFolderPath(this));
        downloadedFolder = new File(KGlobal.getDownloadedFolderPath(this));


        myPaintingFolder = new File(KGlobal.getMyPaintingFolderPath(this));
        if (!myPaintingFolder.exists()) {
            myPaintingFolder.mkdirs();
        }

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
                /*.showImageOnLoading(R.drawable.loading_bg)
                .showImageOnLoading(R.drawable.loading_bg)*/
                .cacheInMemory(false)
                .cacheOnDisc(false)
                .build();

        conf = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .writeDebugLogs()
                .threadPoolSize(5)
                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
        setup();

        getTotalCategoryDataFromAPI();

        try {
            if (Utils.isOnline(this)) {
                startService(new Intent(this, GetIpAddress.class));
            }
        } catch (Exception e) {
        }

        constants.putInt("size", 0, GalleryDashboard.this);

        String policyStatus = sharedPreferences.getString("policyStatus", null);

        if (policyStatus == null) {
            startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class));
        }

      /*  if (!MyApplication.isAppUsedCountSeen()) {
            MyApplication.setAppUsedCountSeen(true);

            appUsedCount = constants.getInt("app_used_count", this);

            appUsedCount++;

            constants.putInt("app_used_count", appUsedCount, this);

            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "appUsedCount: " + appUsedCount, Toast.LENGTH_SHORT).show();
            }
            if (appUsedCount <= 20) {
                //  FirebaseUtils.logEvents(GalleryDashboard.this, "appUsedCount: " + appUsedCount);
                FirebaseUtils.logEvents(GalleryDashboard.this, "appUsedCount");
            }
        }*/

        loginActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                    }
                });

        drawerLayout = findViewById(R.id.drawer_layout);
       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        if (constants.getBoolean(constants.IsGuestUser, this)) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_profile:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_profile, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_profile);
                    if (AppUtils.isLoggedIn()) {
                        FireUtils.openProfileScreen(GalleryDashboard.this, null);
                    } else {
                        Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.nav_draw:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_draw, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_draw);
                    startActivity(new Intent(GalleryDashboard.this, DrawNowActivity.class));
                    DisableAllBtn();
                    break;
                case R.id.nav_quick_draw:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_quick_draw, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_quick_draw);
                    beginDoodle();
                    break;
                case R.id.nav_my_drawing:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_my_drawings, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_my_drawings);
                    Intent lIntent1 = new Intent(GalleryDashboard.this, MyPaintingsActivity.class);
                    lIntent1.putExtra("IsFromDefault", false);
                    startActivity(lIntent1);
                    break;

                case R.id.nav_tutorials:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_tutorials, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_tutorials);
                    Intent _intent = new Intent(this, CategoryActivity.class);
                    _intent.putExtra("levelCount", levelList);
                    startActivity(_intent);
                    break;

                case R.id.nav_gallery:
                    hidetooltip();
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_gallery);
                    Intent intent = new Intent(GalleryDashboard.this, GalleryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.nav_community:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_community, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_community);
                    startActivity(new Intent(GalleryDashboard.this, Community.class));
                    break;
                case R.id.nav_notification:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_notifications, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_notifications);
                    moveToNotification();
                    break;
                case R.id.nav_chat:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_chat, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_chat);
                    startActivity(new Intent(GalleryDashboard.this, ChatUserList.class));
                    break;

                case R.id.navv_store:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_store, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_store);
                    ContextKt.showStoreDialog(this);
                    break;

                case R.id.navv_favorite:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_fav, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_fav);
                    startActivity(new Intent(GalleryDashboard.this, FavActivity.class));


                    break;

                case R.id.nav_drawing_activity:
                    ContextKt.sendUserEvent(StringConstants.home_top_drawing_activity);
                    Intent intent1 = new Intent(GalleryDashboard.this, ProgressActivity.class);
                    intent1.putExtra("level", tv_level.getText().toString());
                    startActivity(intent1);
                    break;

                case R.id.nav_rate:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_rate, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_rate);

                    rateApp();
                    break;
                case R.id.nav_share:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_share, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_share);
                    shareAppLink();
                    break;

                case R.id.nav_support:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_support, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_support);
                    startActivity(new Intent(GalleryDashboard.this, SupportActivity.class));
                    break;
//                case R.id.nav_share:
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(GalleryDashboard.this, constants.slidepop_share, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_share);
//                    shareAppLink();
//                    break;
                case R.id.nav_help_intro:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_help_intro, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_help_intro);
                    startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class).putExtra("dashboard", true));
                    break;
                case R.id.nav_video_guides:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_video_guides, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_video_guides);
                    startActivity(new Intent(GalleryDashboard.this, IntroVideoListActivity.class));
                    break;
                case R.id.nav_help_guide:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_settings, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_settings);
                    startActivity(new Intent(GalleryDashboard.this, SettingActivity.class));


//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(GalleryDashboard.this, constants.slidepop_help, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_help);
//                    startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class));
                    break;
                case R.id.nav_language:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_lang, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_lang);
                    startActivity(new Intent(GalleryDashboard.this, LanguageActivity.class));
                    break;
                case R.id.nav_about:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_about, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_about);
                    startActivity(new Intent(GalleryDashboard.this, AboutActivity.class));
                    break;
                case R.id.nav_login:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_login, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_login);

                    FireUtils.openLoginScreen(this, false);
                    //  startActivity(new Intent(GalleryDashboard.this, LoginActivity.class));
                    break;
                case R.id.nav_logout:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_logout, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_logout);

                    new LoginUtils(GalleryDashboard.this, mGoogleSignInClient, sharedPref).confirmLogout(
                            islogiFromFB,
                            islogiFromGoogle
                    );
                    break;

                case R.id.nav_exit:


                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_exit, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_exit);
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GalleryDashboard.this);
                    builder.setMessage("Exit App?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();

//                        if (isLoggedIn) {
//                            signOut();
//                        } else {
//                            startActivity(new Intent(GalleryDashboard.this, LoginActivity.class));
//                        }
                    break;
                case R.id.nav_post:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.slidepop_community_post, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_community_post);
                    if (!KGlobal.isInternetAvailable(GalleryDashboard.this)) {
                        Toast.makeText(GalleryDashboard.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (AppUtils.isLoggedIn()) {
                        Intent intentGallery = new Intent(GalleryDashboard.this, Gallery.class);
                        intentGallery.putExtra("title", "New Post");
                        intentGallery.putExtra("mode", 1);
                        intentGallery.putExtra("maxSelection", 500);
                        intentGallery.putExtra("isFromNewPost", true);
                        startActivity(intentGallery);
                    } else {
                        Intent intent11 = new Intent(GalleryDashboard.this, LoginActivity.class);
                        startActivity(intent11);
                    }
                    break;

                case R.id.nav_intro:
                    Intent intent2 = new Intent(GalleryDashboard.this, IntroActivity.class);
                    intent2.putExtra("step", 0);
                    startActivity(intent2);
                    break;
            }

//                drawerLayout.close();

            return false;
        });*/

        randomClass.add(Community.class);
        randomClass.add(MyResources.class);
        randomClass.add(MyPaintingsActivity.class);
        randomClass.add(DrawNowActivity.class);
        randomClass.add(CategoryActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Snackbar.make(
                        drawerLayout,
                        "Notification blocked",
                        Snackbar.LENGTH_LONG
                ).setAction("Settings", v -> {
                    // Responds to click on the action
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }).show();
            } else {
                requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }
        }

        AppUtils.hideKeyboard(this);

        findViewById(R.id.diamond_amount_root).setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "gg", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GalleryDashboard.this, AwardActivity.class);
            startActivity(intent);
        });

        // get data from sharedPrefs
        sharedPref = new SharedPref(this);

        isUserDataSend = sharedPref.getBoolean("isUserDataSend", false);


        // showRateApp();

        if (constants.getBoolean(constants.IsGuestUser, this)) {
            binding.includedHeader.navLogin.setVisibility(View.GONE);
            binding.includedHeader.navLogout.setVisibility(View.GONE);
        } else {
            binding.includedHeader.navLogin.setVisibility(View.GONE);
            binding.includedHeader.navLogout.setVisibility(View.GONE);
        }

        for (int i = 0; i < binding.includedHeader.llMain.getChildCount(); i++) {
            binding.includedHeader.llMain.getChildAt(i).setOnClickListener(v -> {
                onClick(v.getId());
            });
        }

        ContextKt.showBanners(this, binding);
    }

    public void onClick(int id) {

        switch (id) {
            case R.id.nav_profile:

                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }

                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_profile, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_profile);
                if (AppUtils.isLoggedIn()) {
                    FireUtils.openProfileScreen(GalleryDashboard.this, null);
                } else {
                    Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_draw:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_draw, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_draw);
                startActivity(new Intent(GalleryDashboard.this, DrawNowActivity.class));
                DisableAllBtn();
                break;
            case R.id.nav_quick_draw:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_quick_draw, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_quick_draw);
                beginDoodle();
                break;
            case R.id.nav_my_drawing:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_my_drawings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_my_drawings);
                Intent lIntent1 = new Intent(GalleryDashboard.this, MyPaintingsActivity.class);
                lIntent1.putExtra("IsFromDefault", false);
                startActivity(lIntent1);
                break;

            case R.id.nav_big_points:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_big_points, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_big_points);
                startActivity(new Intent(GalleryDashboard.this, BigPointActivity.class));
                break;

            case R.id.nav_tutorials:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_tutorials, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_tutorials);
                Intent _intent = new Intent(this, CategoryActivity.class);
                _intent.putExtra("levelCount", levelList);
                _intent.putExtra("cate_id", StringConstants.CATE_ID);
                startActivity(_intent);
                break;

            case R.id.nav_levels:
                ContextKt.sendUserEvent(StringConstants.slidepop_levels);
                startActivity(new Intent(GalleryDashboard.this, YourRankingActivity.class));
                break;

            case R.id.nav_leaderboard:
                ContextKt.sendUserEvent(StringConstants.slidepop_leaderboard);
                startActivity(new Intent(GalleryDashboard.this, LeaderBoardActivity.class));
                break;
            case R.id.nav_gallery:

                hidetooltip();
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_gallery);
                Intent intent = new Intent(GalleryDashboard.this, GalleryActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_community:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_community, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_community);
                startActivity(new Intent(GalleryDashboard.this, Community.class));
                break;
            case R.id.nav_notification:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_notifications, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_notifications);
                moveToNotification();
                break;
            case R.id.nav_chat:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_chat, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_chat);
                startActivity(new Intent(GalleryDashboard.this, ChatUserList.class));
                break;

            case R.id.navv_store:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_store, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_store);
                ContextKt.showStoreDialog(this);
                break;

            case R.id.navv_favorite:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_fav, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_fav);
                startActivity(new Intent(GalleryDashboard.this, FavActivity.class));
                break;

            case R.id.nav_drawing_activity:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                ContextKt.sendUserEvent(StringConstants.home_top_drawing_activity);
                Intent intent1 = new Intent(GalleryDashboard.this, ProgressActivity.class);
                intent1.putExtra("level", tv_level.getText().toString());
                startActivity(intent1);
                break;

            case R.id.nav_rate:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_rate, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_rate);

                rateApp();
                break;
            case R.id.nav_share:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_share, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_share);
                shareAppLink();
                break;

            case R.id.nav_support:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_support, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_support);
                startActivity(new Intent(GalleryDashboard.this, SupportActivity.class));
                break;
//                case R.id.nav_share:
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(GalleryDashboard.this, constants.slidepop_share, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_share);
//                    shareAppLink();
//                    break;
            case R.id.nav_help_intro:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_help_intro, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_help_intro);
                startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class).putExtra("dashboard", true));
                break;
            case R.id.nav_video_guides:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_video_guides, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_video_guides);
                startActivity(new Intent(GalleryDashboard.this, IntroVideoListActivity.class));
                break;
            case R.id.nav_help_guide:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_settings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_settings);
                startActivity(new Intent(GalleryDashboard.this, SettingActivity.class));


//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(GalleryDashboard.this, constants.slidepop_help, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_help);
//                    startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class));
                break;
            case R.id.nav_language:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_lang, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_lang);
                startActivity(new Intent(GalleryDashboard.this, LanguageActivity.class));
                break;
            case R.id.nav_about:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_about, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_about);
                startActivity(new Intent(GalleryDashboard.this, AboutActivity.class));
                break;
            case R.id.nav_login:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_login, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_login);

                FireUtils.openLoginScreen(this, false);
                //  startActivity(new Intent(GalleryDashboard.this, LoginActivity.class));
                break;
            case R.id.nav_logout:
                if (!KGlobal.checkInternet(this)) {
                    KGlobal.showNetworkError(this);
                    return;
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_logout, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_logout);

                new LoginUtils(GalleryDashboard.this, mGoogleSignInClient, sharedPref).confirmLogout(
                        islogiFromFB,
                        islogiFromGoogle
                );
                break;

            case R.id.nav_exit:


                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, StringConstants.slidepop_exit, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, StringConstants.slidepop_exit);
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GalleryDashboard.this);
                builder.setMessage(getString(R.string.exit_app_msg));
                builder.setPositiveButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyApplication.setAppUsedCountSeen(false);
                        finishAffinity();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

//                        if (isLoggedIn) {
//                            signOut();
//                        } else {
//                            startActivity(new Intent(GalleryDashboard.this, LoginActivity.class));
//                        }
                break;
            case R.id.nav_post:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, constants.slidepop_community_post, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, constants.slidepop_community_post);
                if (!KGlobal.isInternetAvailable(GalleryDashboard.this)) {
                    Toast.makeText(GalleryDashboard.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (AppUtils.isLoggedIn()) {
                    Intent intentGallery = new Intent(GalleryDashboard.this, Gallery.class);
                    intentGallery.putExtra("title", "New Post");
                    intentGallery.putExtra("mode", 1);
                    intentGallery.putExtra("maxSelection", 500);
                    intentGallery.putExtra("isFromNewPost", true);
                    startActivity(intentGallery);
                } else {
                    Intent intent11 = new Intent(GalleryDashboard.this, LoginActivity.class);
                    startActivity(intent11);
                }
                break;

            case R.id.nav_intro:
                Intent intent2 = new Intent(GalleryDashboard.this, IntroActivity.class);
                intent2.putExtra("isFromHome", true);
                intent2.putExtra("step", 0);
                startActivity(intent2);
                break;
        }
    }

    private void loadInterRewardAd() {
        if (getDiComponent().getSharedPreferenceUtils().getRcvInterRewardNotification() == 0) {
            isInterstitialLoadOrFailed = true;
            checkAdLoad();
        } else {
            if (BuildConfig.DEBUG) {
                getDiComponent().getAdmobRewardedAds().loadRewardedAd(this,
                        "ca-app-pub-3940256099942544/5354046379",
                        getDiComponent().getSharedPreferenceUtils().getRcvInterRewardNotification(),
                        getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                        getDiComponent().getInternetManager().isInternetConnected(),
                        new RewardedOnLoadCallBack() {
                            @Override
                            public void onAdFailedToLoad(@NonNull String adError) {
                                isInterstitialLoadOrFailed = true;
                                checkAdLoad();
                            }

                            @Override
                            public void onAdLoaded() {
                                checkAdLoad();
                            }

                            @Override
                            public void onPreloaded() {
                                isInterstitialLoadOrFailed = true;
                            }
                        });
            } else {
                getDiComponent().getAdmobRewardedAds().loadRewardedAd(this,
                        getDiComponent().getSharedPreferenceUtils().getRcvInterRewardID(),
                        getDiComponent().getSharedPreferenceUtils().getRcvInterRewardNotification(),
                        getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                        getDiComponent().getInternetManager().isInternetConnected(),
                        new RewardedOnLoadCallBack() {
                            @Override
                            public void onAdFailedToLoad(@NonNull String adError) {
                                isInterstitialLoadOrFailed = true;
                                checkAdLoad();
                            }

                            @Override
                            public void onAdLoaded() {
                                checkAdLoad();
                            }

                            @Override
                            public void onPreloaded() {
                                isInterstitialLoadOrFailed = true;
                            }
                        });
            }
        }
    }

    private void moveToNotification() {
        ContextKt.sendUserEvent(StringConstants.home_top_notifications);
        startActivity(new Intent(GalleryDashboard.this, NotificationActivity.class));
    }

   /* private void checkAdvertisement() {
        if (mCounter < 16) {
            try {
                mCounter++;
                if (isInterstitialLoadOrFailed) {
                    mHandler.removeCallbacks(adsRunner);
                    checkAdLoad();
                } else {
                    mHandler.removeCallbacks(adsRunner);
                    mHandler.postDelayed(adsRunner, 1000);
                }
            } catch (Exception e) {
                Log.e("AdsInformation", e.getMessage());
            }
        } else {
            isInterstitialLoadOrFailed = true;
            mHandler.removeCallbacks(adsRunner);
        }
    }*/

    private void checkAdLoad() {
        if (getDiComponent().getAdmobRewardedAds().isRewardedLoaded()) {
            getDiComponent().getAdmobRewardedAds().showRewardedAd(this, new RewardedOnShowCallBack() {
                @Override
                public void onAdClicked() {

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    moveToNotification();
                }

                @Override
                public void onAdFailedToShowFullScreenContent() {
                    binding.appBarMain.adLoading.getRoot().setVisibility(View.GONE);
                    moveToNotification();
                }

                @Override
                public void onAdImpression() {

                }

                @Override
                public void onAdShowedFullScreenContent() {
                    binding.appBarMain.adLoading.getRoot().setVisibility(View.GONE);
                }

                @Override
                public void onUserEarnedReward() {

                }
            });
        } else {
            binding.appBarMain.adLoading.getRoot().setVisibility(View.GONE);
            moveToNotification();
        }
    }

    private void searchFromEditText() {
        String tutorialID = edt_hash_search.getText().toString();
        //condition check tutorialID is full number

        if (!TextUtils.isEmpty(tutorialID)) {

//            toggleSearchHeader();
            searchedSubCatId = null;
            AppUtils.setSearchResponse(null);
            AppUtils.hideKeyboard(this);

            isNumberSearch = tutorialID.matches("\\d+");
            getCategoryDetailFromAPI(BuildConfig.CAT_ID, tutorialID);
        }
    }


    private void DisableAllBtn() {
        MyPainting.setEnabled(false);
        ll_import_image.setEnabled(false);
        ll_community.setEnabled(false);
        ll_tutorials.setEnabled(false);
        btnBigPoints.setEnabled(false);
        GalleryBtn.setEnabled(false);
    }


    private void EnableAllBtn() {
        MyPainting.setEnabled(true);
        ll_import_image.setEnabled(true);
        ll_community.setEnabled(true);
        ll_tutorials.setEnabled(true);
        btnBigPoints.setEnabled(true);
        GalleryBtn.setEnabled(true);
    }


    private int tipSizeSmall;
    private int tipSizeRegular;
    private int tipRadius;
    private int tooltipColor;

    private ViewGroup root;
    Tooltip tooltip;


    private void showMenuTooltip(@NonNull View anchor) {
        ViewGroup content = (ViewGroup) getLayoutInflater().inflate(R.layout.language_select_tooltip, null);
        RecyclerView rv_notification_popup = (RecyclerView) content.findViewById(R.id.rv_lang_list);
        tipSizeSmall = getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipSizeRegular = getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipRadius = getResources().getDimensionPixelOffset(R.dimen.tip_radius);
        tooltipColor = ContextCompat.getColor(this, R.color.dull_white);
        try {
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.lang_select_header_click, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_click);
            tooltip = new Tooltip.Builder(this)
                    .anchor(anchor, Tooltip.BOTTOM)
                    .animate(new TooltipAnimation(TooltipAnimation.REVEAL, 300))
                    .autoAdjust(true)
                    .cancelable(false)
                    .content(content)
                    .withTip(new Tooltip.Tip(tipSizeRegular, tipSizeRegular, tooltipColor))
                    .withPadding(getResources().getDimensionPixelOffset(R.dimen.menu_tooltip_padding))
                    .into(root)
                    .show();

            View.OnClickListener _listener = view -> {

                if (current_lan.equalsIgnoreCase(_lst.get((int) view.getTag()).getLang_pref())) {
                    return;
                }

                String msg = "Switch to the <b>" + _lst.get((int) view.getTag()).getLang_name() + "</b> language ?";
                new AlertDialog.Builder(GalleryDashboard.this)
                        .setMessage(Html.fromHtml(msg))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                constants.putString(constants.selected_language, _lst.get((int) view.getTag()).getLang_pref(), GalleryDashboard.this);

                                switch ((int) view.getTag()) {
                                    case 0:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "english", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "english");
                                        break;
                                    case 1: {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "hindi", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "hindi");
                                    }
                                    break;
                                    case 2: {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "bangla", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "bangla");
                                    }
                                    break;
                                    case 3: {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "urdu", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "urdu");
                                    }
                                    break;
                                    case 5: {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "filipino", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "filipino");
                                    }
                                    break;
                                    case 4:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "egypt", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "egypt");
                                        break;
                                    case 6:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "chinese", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_ + "chinese");
                                        break;
                                    case 7:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "spanish", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_start_ + "spanish");
                                        break;
                                    case 8:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "french", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_start_ + "french");
                                        break;
                                    case 9:
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.lang_select_header_ + "portuguese", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_start_ + "portuguese");
                                        break;

                                }
                                tooltip.dismiss();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, whichButton) -> {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, constants.lang_select_header_cancel, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(GalleryDashboard.this, constants.lang_select_header_cancel);
                            dialog.dismiss();
                        }).show();
            };
            rv_notification_popup.setLayoutManager(new LinearLayoutManager(this));
            language_adapter _adapter = new language_adapter(GalleryDashboard.this, _lst, _listener);
            rv_notification_popup.setAdapter(_adapter);

        } catch (Exception e) {
            Log.e("TAGGG", "Exception " + e.getMessage());
        }
    }


    void getFcmToken() {
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        // request token that will be used by the server to send push notifications
        String DeviceID = instanceID.getToken();
        constants.putString(constants.DeviceToken, DeviceID, GalleryDashboard.this);
        String _user_id = constants.getString(constants.UserId, GalleryDashboard.this);
        Log.e("TAGGGG", "FCM Registration Token: " + DeviceID);
        Log.e("TAGGGG", "FCM Registration _user_id " + _user_id);

        // Create a map to hold the token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("android", DeviceID);

        // user id
        FirebaseUser uId = FirebaseAuth.getInstance().getCurrentUser();
        if (uId != null) {
            // put in database
            db_firebase.collection("fcm_tokens").document(uId.getUid())
                    .set(tokenMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("fcm_tokens", "FCM token successfully written! ");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("fcm_tokens", "getFcmToken: " + e);
                    });
        } else {
            // Handle the case where the user is not signed in
            Log.e("Error", "User not signed in");
            // You can redirect to a sign-in screen or show an error message
        }


        if (KGlobal.isInternetAvailable(GalleryDashboard.this) && _user_id != null && !_user_id.isEmpty()) {
            startService(new Intent(GalleryDashboard.this, SendDeviceToken.class));
        }
    }

    /*This method will call when user click on close button from upper right button.
     * It will prompt confirm dialog to user and ask for want to exit or not. Once
     * user do selection from that dialog, app perform operation accordigly.
     * */
    public void exitFromApplication(View view) {
        try {
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.click_home_minus, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(GalleryDashboard.this, constants.click_home_minus);

            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

            if (drawerLayout.isOpen()) {
                drawerLayout.close();
            } else {
                drawerLayout.open();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at exitFromApplication " + e.getMessage());
        }
    }

    /*Display confirm dialog for user, that confirm really want to exir or cancel.*/
    private void confirmExit() {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setMessage("Close App");
        lBuilder1.setPositiveButton("Ok", (dialog, which) -> {
            // TODO Auto-generated method stub
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.exit_app, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(GalleryDashboard.this, constants.exit_app);
//                System.exit(0);
            finish();
        });
        lBuilder1.setNeutralButton("Cancel", (dialog, which) -> {
            // TODO Auto-generated method stub
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.exit_cancel, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(GalleryDashboard.this, constants.exit_cancel);
            dialog.cancel();
        });
        lBuilder1.create().show();
    }


    //Allows user to pick image from device local storage.
    public void pickImageFromGallery(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO_REQUEST);
    }

    /*This method allows to setup some basic configuration for the app.
    such as prompt permission dialog to user, copy all paintoloy collection
    images from app to local storage and send app start event in mixpanel.
    */
    void setup() {
        File mydir = new File(Environment.getExternalStorageDirectory(),
                "Crash_Reports");
        if (!mydir.exists()) {
            mydir.mkdir();
        }

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(
                mydir.getAbsolutePath(), GalleryDashboard.this));
        if (getIntent() != null && getIntent().hasExtra("crash")) {
            Intent lIntent1 = new Intent();
            boolean isTutorialMode = getIntent().getBooleanExtra("isTutorialmode", false);
            String selectedPath = getIntent().getStringExtra("path");
            lIntent1.putExtra("isTutorialmode", isTutorialMode);
            lIntent1.putExtra("path", selectedPath);
            lIntent1.putExtra("drawingPath", "temp.png");
            lIntent1.putExtra("crash", true);
            lIntent1.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
            String str = "Reload Painting";
            Intent lIntent2 = lIntent1.setAction(str);
            mlIntent = lIntent1.setClass(this, PaintActivity.class);
            startActivity(lIntent1);
        }
        new Handler().postDelayed(() -> getFcmToken(), 2000);
    }

    /*This method will redirect to my movies screen when user click on my movies icon from dashbord  screen*/
    public void DisplayAllTutorial(View v) {
        hidetooltip();

        int permission = ActivityCompat.checkSelfPermission(GalleryDashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    GalleryDashboard.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQ_MY_MOVIE
            );
            return;
        } else {
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.getOPEN_My_Moviews(), Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.getOPEN_My_Moviews());
            startActivity(new Intent(this, MyMoviesActivity.class));
        }
    }

    public void openDrawer() {
        try {
            if (!drawerLayout.isOpen()) {
                drawerLayout.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*This method allows us to redicrect in Paintology collection screen, when user click on Paintology Collection icon
     * from dashboard screen then app will redirect to the default collection screen*/
    public void gotogallery() {

        hidetooltip();
        if (KGlobal.isInternetAvailable(GalleryDashboard.this)) {

            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.getHOME_BUTTON_NEW_DRAW(), Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.getHOME_BUTTON_NEW_DRAW());
            Intent _intent = new Intent(this, CategoryActivity.class);
            _intent.putExtra("levelCount", levelList);
            _intent.putExtra("cate_id", StringConstants.CATE_ID);
            startActivity(_intent);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    openDrawer();
                }
            }, 1000);
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
        }
    }


    void hidetooltip() {
        try {
            if (tooltip != null) {
                tooltip.dismiss(true);
                return;
            }
        } catch (Exception e) {

        }
    }


    public final int REQ_MY_PAINTING = 10;
    public final int REQ_MY_MOVIE = 11;
    public final int REQ_PLUS_ICON = 12;
    public final int REQ_P_COLLECTION = 13;
    public final int REQ_COMMUNITY = 14;


    /*This method allows to redirect to my painting screen, where user can see their paintings Or imported from local storage*/
    public void RedirectToGaller() {

        if (BuildConfig.DEBUG) {
            Toast.makeText(GalleryDashboard.this, constants.getHOME_BUTTON_MY_PAINTINGS(), Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.getHOME_BUTTON_MY_PAINTINGS());
        Intent lIntent1 = new Intent(this, MyPaintingsActivity.class);
        lIntent1.putExtra("IsFromDefault", false);
        startActivity(lIntent1);

    }

    /*This is the native method, that will call when screen start/wakeup,
     this method will count all the files under the Paintology Collection,My Movies and My Painting folder.
     this will set total of each folder respectively.
     */


    public void showCountryDialog() {
        try {
            if (constants.getString(constants.UserCountryCode, this).equalsIgnoreCase("")) {
                FireUtils.showCustomDialog(this, getString(R.string.app_title), getString(R.string.ss_country_missing), new FireUtils.onCloseListener() {
                    @Override
                    public void onDismiss() {
                        startActivity(new Intent(GalleryDashboard.this, SelectCountryActivity.class));
                    }
                });
            } else {
                FirebaseFirestoreApi.updateCountry(constants.getString(constants.UserCountryCode, this))
                        .addOnSuccessListener(httpsCallableResult -> constants.putString(constants.UserCountry, constants.getString(constants.UserCountryCode, GalleryDashboard.this), GalleryDashboard.this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            FirebaseFirestore.getInstance().collection("users").document(constants.getString(constants.UserId, this)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> value = task.getResult().getData();
                        if (value != null) {
                            if (value.containsKey("country")) {
                                String val = (String) value.getOrDefault("country", "");
                                if (val != null && val.equalsIgnoreCase("")) {
                                    showCountryDialog();
                                } else if (val == null) {
                                    showCountryDialog();
                                }
                            } else {
                                showCountryDialog();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (constants.getBoolean(StringConstants.isMenuShow, this)) {
                constants.putBoolean(StringConstants.isMenuShow, false, this);
                if (!drawerLayout.isOpen()) {
                    drawerLayout.open();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            EnableAllBtn();

        } catch (Exception e) {

        }

        String _local = constants.getString(constants.selected_language, GalleryDashboard.this);
        if (!current_lan.equals(_local)) {
            recreate();
            return;
        }

        setDrawerNotificationIcon();

        try {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();//mAuth.getCurrentUser();
            updateUI(currentUser);


        } catch (IllegalStateException ise) {
            Log.e("TAG", "IllegalStateException " + ise.getMessage());
        } catch (Exception e) {
            Log.e("TAGG", "Exception at onResume " + e.getMessage(), e);
        }
        constants.putString("pickfromresult", "", GalleryDashboard.this);
        constants.putString("isfromTrace", "", GalleryDashboard.this);
        constants.putString("isfromoverlay", "", GalleryDashboard.this);
        constants.putString("path", "", GalleryDashboard.this);
        constants.putString("parentFolder", "", GalleryDashboard.this);
        constants.putString("type", "", GalleryDashboard.this);

        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                stopService(new Intent(GalleryDashboard.this, MyServiceForRecording.class));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at stop ser " + e.getMessage());
        }
        System.gc();

    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            fetchProfileData();
        } else {
            isLoggedIn = false;
            resetInitialUI();
        }

    }

    private void resetInitialUI() {


        CircleImageView profilePic = findViewById(R.id.iv_profile);
        TextView profileName = findViewById(R.id.tv_name);
        TextView tvLevel = findViewById(R.id.tv_level);

        profileName.setText(constants.getString(constants.Username, this));
        tvLevel.setText(sharedPref.getString(StringConstants.user_level, StringConstants.beginner));

        Picasso.get().load(R.drawable.img_default_avatar)
                .transform(new CircleTransform())
                .placeholder(R.drawable.img_default_avatar)
                .error(R.drawable.img_default_avatar)
                .into(profilePic);

        Picasso.get().load(R.drawable.img_default_avatar)
                .placeholder(R.drawable.img_default_avatar)
                .error(R.drawable.img_default_avatar)
                .transform(new CircleTransform())
                .into(binding.includedHeader.navHeaderPic);

        drawerLayout = findViewById(R.id.drawer_layout);
        binding.includedHeader.imgBack.setOnClickListener(view -> {
            if (drawerLayout.isOpen()) {
                drawerLayout.close();
            }
        });

        binding.includedHeader.navHeaderPic.setOnClickListener(view -> {
            if (!KGlobal.checkInternet(this)) {
                KGlobal.showNetworkError(this);
                return;
            }
            if (AppUtils.isLoggedIn()) {
                FireUtils.openProfileScreen(GalleryDashboard.this, null);
            } else {
                Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.includedHeader.navHeaderTitle.setText(constants.getString(constants.Username, this));

    }


    /*This method handle the click event of each buttons*/
    @Override
    public void onClick(View view) {

        hidetooltip();
        switch (view.getId()) {
            case R.id.ll_find_ability:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_FIND_ABILITY, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_FIND_ABILITY);
                startActivity(new Intent(this, FindYourAbilityActivity.class));
                break;
            case R.id.ll_challenge:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_CHALLENGE, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_CHALLENGE);
                startActivity(new Intent(this, ChallengeActivity.class));
                break;


            case R.id.ll_video_tuto: {
                try {

                    startActivity(new Intent(this, LinksForYouActivity.class));

//                    if (KGlobal.isInternetAvailable(GalleryDashboard.this)) {
//                        String url = "https://www.paintology.com/";
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(GalleryDashboard.this, constants.click_website_button, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.click_website_button);
//                        KGlobal.openInBrowser(GalleryDashboard.this, url);
//                    } else
//                        Toast.makeText(this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();

                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
            break;
//            case R.id.iv_own_adv:
//                try {
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(GalleryDashboard.this, constants.homepage_promo_postweb_button, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.homepage_promo_postweb_button);
//                    String url_1 = "https://www.amazon.com/gp/product/B084GRP9CR";
//                    KGlobal.openInBrowser(GalleryDashboard.this, url_1);
//                } catch (ActivityNotFoundException e) {
//                    Log.e("TAGGG", "Exception at view " + e.getMessage());
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception " + e.getMessage());
//                }
//                break;
//            case R.id.iv_own_adv_:
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(GalleryDashboard.this, constants.homepage_promo_community_button, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(GalleryDashboard.this, constants.homepage_promo_community_button);
//                startActivity(new Intent(GalleryDashboard.this, Community.class));
//                break;
//            case R.id.iv_hor_banner:
//                try {
//                    String url_1 = "https://www.udemy.com/courses/search/?p=2&q=paintology";
//                    KGlobal.openInBrowser(GalleryDashboard.this, url_1);
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(GalleryDashboard.this, constants.homepage_promo_udemy_button, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.homepage_promo_udemy_button);
//                } catch (ActivityNotFoundException e) {
//                    Log.e("TAGGG", "Exception at view " + e.getMessage());
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception " + e.getMessage());
//                }
//                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*This class is comparable class, that sort the files based on last modified date of image*/
    class Pair implements Comparable {
        public long t;
        public File f;

        public Pair(File file) {
            f = file;
            t = file.lastModified();
        }

        public int compareTo(Object o) {
            long u = ((Pair) o).t;
            return t < u ? -1 : t == u ? 0 : 1;
        }
    }


    /*This method will redirect user to the canvas screen where user can draw their arts.
     * This method will called when user click on square image in dashboard screen.*/
    public void beginDoodle() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(GalleryDashboard.this, constants.HOME_DRAW_ICON, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_DRAW_ICON);
        ContextKt.setSharedNo(this);
        constants.putInt("background_color", -1, GalleryDashboard.this);
        Intent _intent = new Intent(GalleryDashboard.this, PaintActivity.class);
        _intent.setAction("New Paint");
        _intent.putExtra("background_color", -1);
        startActivity(_intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginUpdateEvent(UserLoginUpdateEvent event) {
        onResume();
    }

    ProgressDialog pdialog;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyApplication.setAppUsedCountSeen(true);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mActivity = null;
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
        }
        if (GalleryDashboard.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
            return;
        }
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }

    /*This method will call once user pick image from their local storgae, when user pick color from color picker,call once user logged in via
     google and facebook and the result accordingle.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("TAGGG", "OnActivity Result Call test > " + requestCode);
            boolean isFromOverraid = false;

//            if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
//                if (resultCode == Activity.RESULT_OK) {
//                    Log.d(TAG, "onActivityResult: Permission Granted");
//                    AppUtils.setDataFromResultSS((Intent) data.clone());
//                    screenCapturePermission = true;
//                } else {
//                    Log.d(TAG, "onActivityResult: Permission Deined");
//                    screenCapturePermission = false;
//                }
//            } else
            if (requestCode == 100)
                isFromOverraid = true;
            try {
                if (requestCode == 400) {
                    if (data == null)
                        return;

                    Uri imageUri = data.getData();
                    String str14 = getPath(imageUri);
                    if (str14 == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryDashboard.this);
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
                } else if (requestCode == SELECT_BACKGROUND_COLOR_REQUEST) {
                    try {
                        int mPrefBackgroundColor = data.getIntExtra("color-selected", -65536);
                        Intent lIntent1 = new Intent();
                        String str = "New Paint";
                        lIntent1.setAction(str);

                        constants.putInt("background_color", mPrefBackgroundColor, GalleryDashboard.this);

                        lIntent1.putExtra("background_color", mPrefBackgroundColor);
                        mlIntent = lIntent1.setClass(this, PaintActivity.class);
//                    FirebaseUtils.logEvents(this,constants.getPICK_NEW_CANVAS());
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(GalleryDashboard.this, constants.getPICK_NEW_CANVAS(), Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(this, constants.getPICK_NEW_CANVAS());

                        startActivity(lIntent1);
                    } catch (Exception e) {

                    }
                } else if (requestCode == 100) {
                    try {
                        Uri imageUri = data.getData();
                        String str14 = getPath(imageUri);

                        File file = new File(str14);
                        Intent intent = new Intent(this, PaintActivity.class);
                        intent.setAction("LoadWithoutTrace");
                        intent.putExtra("path", file.getName());
                        intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                        intent.putExtra("isPickFromOverlaid", true);
                        if (!_object.getCanvas_color().isEmpty()) {
                            intent.putExtra("canvas_color", _object.getCanvas_color());
                        }

                        List<ColorSwatch> swatches = _object.getSwatches();

                        Gson gson = new Gson();
                        String swatchesJson = gson.toJson(swatches);

                        intent.putExtra("swatches", swatchesJson);
                        intent.putExtra("id", _object.getID());
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                }
//                else if (requestCode == RC_SIGN_IN) {
//                    try {
//                        // The Task returned from this call is always completed, no need to attach
//                        // a listener.
//                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                        account = task.getResult(ApiException.class);
//
//                        Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
//                        constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), GalleryDashboard.this);
//                        constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), GalleryDashboard.this);
//                        constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), GalleryDashboard.this);
//                        LoginRequestModel model = new LoginRequestModel((account.getDisplayName() != null ? account.getDisplayName() : ""), (account.getId() != null ? account.getId() : ""), (account.getEmail() != null ? account.getEmail() : ""));
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(GalleryDashboard.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess);
//                        addUser(model, LOGIN_FROM_GOOGLE);
//                    } catch (ApiException e) {
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(GalleryDashboard.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(this, constants.GoogleLoginFailed);
//                        // The ApiException status code indicates the detailed failure reason.
//                        Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
//                    }
//                } else {
//                    callbackManager.onActivityResult(requestCode, resultCode, data);
//                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at on result " + e.getMessage(), e);
            }
        } catch (Exception e) {

        }
    }


    /*This method will redirect user to canvas screen that allows user other feature and draw their art.*/
    private void startDoodle(String paint_name, boolean isOverraid) {
        Intent lIntent1 = new Intent();
        lIntent1.setClass(this, PaintActivity.class);
//        lIntent1.setAction(Paintor.EDIT_PAINT);
        lIntent1.setAction("Edit Paint");
        lIntent1.putExtra("FromLocal", true);
        lIntent1.putExtra("paint_name", paint_name);
        lIntent1.putExtra("isOverraid", isOverraid);
        Log.e("TAGGG", "startDoodle paint_name " + paint_name);
//        FirebaseUtils.logEvents(GalleryDashboard.this,constants.getPICK_IMAGE_FROM_ALBUM());
//        FirebaseUtils.logEvents(this, constants.getPICK_IMAGE_FROM_ALBUM());
        startActivity(lIntent1);
    }

    /*This method prompt the dialog to user when there are alredy trace image for specific image that user has selected.*/
    private void showAlertDialog(String drawingName, String traceImagePath) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setMessage("You already have a drawing with this traced image, do you want to continue ?").setCancelable(true);
        lBuilder1.setPositiveButton("Yes Resume it.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

//                FirebaseUtils.logEvents(GalleryDashboard.this,constants.getPICK_IMAGE_FROM_ALBUM());
//                FirebaseUtils.logEvents(GalleryDashboard.this, constants.getPICK_IMAGE_FROM_ALBUM());
                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                intent.setAction("Reload Painting");
                intent.putExtra("isTutorialmode", true);
                intent.putExtra("path", traceImagePath);
                intent.putExtra("drawingPath", drawingName);
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(GalleryDashboard.this));
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color());
                }

                List<ColorSwatch> swatches = _object.getSwatches();

                Gson gson = new Gson();
                String swatchesJson = gson.toJson(swatches);

                intent.putExtra("swatches", swatchesJson);
                intent.putExtra("id", _object.getID());
                startActivity(intent);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        try {

            if (tooltip != null && tooltip.isShown()) {
                tooltip.dismiss(true);
                return;
            }

            showFeedbackDialog();

            MyApplication.get_realTimeDbUtils(this).setOffline(constants.getString(constants.UserId, GalleryDashboard.this));
            //  stopService(new Intent(GalleryDashboard.this, SendSessionEvent.class));
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, constants.click_exit_app, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(GalleryDashboard.this, constants.click_exit_app);
            Log.e("TAGG", "onBackPressed called");
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at onBack " + e.getMessage());
        }
    }

    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    Log.d("rattingAlert", "showRateApp: " + "success");
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                Log.d("rattingAlert", "showRateApp: " + "failed");
                showRateAppFallbackDialog();
            }
        });
    }

    private void showRateAppFallbackDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> redirectToPlayStore())
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                            super.onBackPressed();
                            // take action when pressed not now
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                            // take action when pressed remind me later
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
//        new MaterialAlertDialogBuilder(this)
//                .setTitle(R.string.rate_app_title)
//                .setMessage(R.string.rate_app_message)
//                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> redirectToPlayStore())
//                .setNegativeButton(R.string.rate_btn_neg,
//                        (dialog, which) -> {
//                            super.onBackPressed();
//                            // take action when pressed not now
//                        })
//                .setNeutralButton(R.string.rate_btn_nut,
//                        (dialog, which) -> {
//                            // take action when pressed remind me later
//                        })
//                .setOnDismissListener(dialog -> {
//                })
//                .show();
    }

    // redirecting user to PlayStore
    public void redirectToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
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

    void getLocalityDataAPI() {
        Log.e("TAGG", "getLocalityDataAPI called");
        if (Localitycall != null) {
            Localitycall.cancel();
            Log.e("TAGG", "getLocalityDataAPI onCancel");
        }
        Localitycall = apiInterface.getLocalityData("https://us-central1-even-scheduler-265110.cloudfunctions.net/geolocation");
        try {
            Localitycall.enqueue(new Callback<LocalityData>() {
                @Override
                public void onResponse(Call<LocalityData> call, retrofit2.Response<LocalityData> response) {
                    try {
                        Log.e("TAGGG", "LocalityData response");
                        String city = response.body().getCity();
                        Log.e("city ===", city);
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at set counter locality data " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(Call<LocalityData> call, Throwable t) {

                    Log.e("TAGGG", t.getMessage());
                }


            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    void getTotalCategoryDataFromAPI() {

        FirebaseFirestoreApi.fetchTutorialsListCount("categories.id:=" + StringConstants.CATE_ID).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {

                if (task.isSuccessful()) {

                    HashMap<String, Object> list_from_response1 = (HashMap<String, Object>) task.getResult().getData();
                    // Log.e("result22", list_from_response1.toString());
                    HashMap<String, Object> page = (HashMap<String, Object>) list_from_response1.get("page");
                    List<HashMap<String, Object>> levelListCount = (List<HashMap<String, Object>>) list_from_response1.get("facet_counts");
                    Log.e("levelListCount", levelListCount.get(0).toString() + "");

                    levelList = levelListCount.toString();

                    totalTutorialCount = Integer.valueOf(page.get("total_elements").toString());
                    TextView tv_tutorials = findViewById(R.id.tvTotalPaint);
                    tv_tutorials.setVisibility(View.VISIBLE);
                    tv_tutorials.setText(totalTutorialCount + "");
                }


            }
        });

        /*Call<CategoryModel> call = apiInterface.geftCategoryList(ApiClient.SECRET_KEY);

        call.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, retrofit2.Response<CategoryModel> response) {

                Log.d("onResponseHere", "onResponse: " + new Gson().toJson(response.body()));
                TextView tv_tutorials = findViewById(R.id.tv_tutorials);
                if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                    if (response.body().getCategoryList() != null && response.body().getCategoryList().size() > 0) {
                        ArrayList<CategoryModel.categoryData> list_from_response = response.body().getCategoryList();
                        for (CategoryModel.categoryData categoryData : list_from_response) {
                            totalTutorialCount += categoryData.getObj_data().totalTutorials;
                        }
                        tv_tutorials.setText(getString(R.string.default_collection) + " (" + totalTutorialCount + ")");
                    } else {
                        Log.e("TAGG", getResources().getString(R.string.empty_list));
                    }
                } else {
                    Log.e("TAGG", (response != null && response.body() != null) ? response.body().getResponse() : "Failed");
                }
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {

            }
        });*/
    }


    void showProgress(Boolean isFromUpload) {
        try {
            progressDialog = new ProgressDialog(GalleryDashboard.this);
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
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


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


    void showFeedbackDialog() {
        Random random = new Random();
        int index = random.nextInt(randomClass.size() - 1);
        Intent intent = new Intent(GalleryDashboard.this, randomClass.get(index));
        intent.putExtra("showExitDialog", true);
        startActivity(intent);
        finish();
    }


    private void shareAppLink() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Paintology Drawing App");
        String app_url = " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        String text = "Check out this drawing made with the Paintology drawing app from the Google play store.\n" +
                "\n" +
                "A new and easy way to draw on your phone!\n\n";

        text = text + app_url;
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        String packageName = getPackageName();

        Log.d("packageName", "rateIntentForUrl: " + packageName);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, packageName)));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    void checkCurrentVersion() {

        DocumentReference _app_version_data = db_firebase.collection("VariantApps").document("Apps")
                .collection(StringConstants.APP_NAME).document("app_version");

        _app_version_data.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                String _currentVersion = doc.get("version") + "";
                String new_feature = doc.get("new_feature") + "";
                int total_lines = 0;
                try {
                    if (doc.contains("total_lines")) {
                        total_lines = Integer.parseInt(doc.get("total_lines").toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    total_lines = 0;
                }

//                    new_feature = "A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.A Bug Fixes And all over the improvement of the application.";
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    Log.e("TAGGGG", "Version From FB " + _currentVersion);
                    Log.e("TAGGGG", "Version From App " + version);

                    if (version.contains("-")) {
                        version = version.split("-")[0];
                    }

                    double _fb_version = Double.parseDouble(_currentVersion);
                    double _app_version = Double.parseDouble(version);

                    if (_app_version == _fb_version) {
                        constants.putString(constants.dont_show_dialog, "false", GalleryDashboard.this);
                    }

                    if (_app_version < _fb_version) {
                        String is_dont_show = constants.getString(constants.dont_show_dialog, GalleryDashboard.this);
                        Log.e("TAGGG", "is_dont_show " + is_dont_show);
                    }

                    if (_app_version < _fb_version) {
                        String is_dont_show = constants.getString(constants.dont_show_dialog, GalleryDashboard.this);
                        Log.e("TAGGG", "is_dont_show " + is_dont_show);
                        if (is_dont_show.isEmpty() || !is_dont_show.equalsIgnoreCase("true")) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, constants.version_dialog_open, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(GalleryDashboard.this, constants.version_dialog_open);

                            Dialog _dialog = new Dialog(GalleryDashboard.this);
                            VersionDialogLayoutBinding binding1 = VersionDialogLayoutBinding.inflate(getLayoutInflater());
                            _dialog.setContentView(binding1.getRoot());

                            if (_dialog.getWindow() != null) {
                                _dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                _dialog.getWindow().setLayout(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                            }
                            if (total_lines == 0) {
                                binding1.tvLine2.setVisibility(View.INVISIBLE);
                            }

                            try {
                                for (int i = 1; i <= total_lines; i++) {
                                    if (doc.contains("line_" + i)) {
                                        LayoutUpdateChangeBinding binding2 = LayoutUpdateChangeBinding.inflate(getLayoutInflater());
                                        binding2.tvLine.setText(doc.get("line_" + i).toString());
                                        binding1.llChanges.addView(binding2.getRoot());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String _msg = "New version <b>" + _fb_version + "</b> available.";
//                                String _msg_1 = "<b>WHAT'S NEWS </b>" + " New brushes, single tap feature, optimized for 2k, minor bug fixes.";
                            String _msg_1 = "<b>WHAT'S NEW : </b>" + new_feature;
                            binding1.tvLine1.setText(Html.fromHtml(_msg));

                            binding1.imgCross.setOnClickListener(v -> {
                                constants.putString(constants.dont_show_dialog, "true", GalleryDashboard.this);
                                _dialog.dismiss();
                            });

                            binding1.tvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _dialog.dismiss();
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(GalleryDashboard.this, constants.version_dialog_cancel, Toast.LENGTH_SHORT).show();
                                    }
                                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.version_dialog_cancel);
                                    constants.putString(constants.dont_show_dialog, "true", GalleryDashboard.this);
                                }
                            });

                            binding1.tvPlayStore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _dialog.dismiss();
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(GalleryDashboard.this, constants.version_dialog_go_to_playstore, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(GalleryDashboard.this, constants.version_dialog_go_to_playstore);
                                        String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
                                        Intent browserIntent = new Intent(ACTION_VIEW, Uri.parse(url));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            _dialog.show();
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    void sendFeedback(String _name, String _feedback, String _rating, String app_behaviour, String app_favorite) {
        HashMap<String, Object> _feedbackMap = new HashMap<>();
        _feedbackMap.put("name", _name);
        _feedbackMap.put("feedback", _feedback);
        _feedbackMap.put("rating", _rating);
        _feedbackMap.put("isFromCommunity", false);
        _feedbackMap.put("user_favorite_app", app_favorite);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String logdate = sdf.format(new Date());
        _feedbackMap.put("log_date", logdate);

        try {
            try {
                SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                String currentDateandTime = sdf_.format(new Date());
                _feedbackMap.put("date", currentDateandTime);
            } catch (Exception e) {

            }
            String country = constants.getString(constants.UserCountry, GalleryDashboard.this);
            String city = constants.getString(constants.UserCity, GalleryDashboard.this);
            if (country != null && !country.isEmpty()) {
                _feedbackMap.put("location", country + "/" + city);
            } else {
                String country_code = constants.getString(constants.UserCountry, GalleryDashboard.this);
                _feedbackMap.put("location", country_code + "/" + city);
            }
        } catch (Exception e) {

        }
        try {
            showProgress(false);
            db_firebase.collection("feedback")
                    .add(_feedbackMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, constants.feedback_sent, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(GalleryDashboard.this, constants.feedback_sent);
                            constants.putString(constants.IsFeedbackSent, "true", GalleryDashboard.this);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(GalleryDashboard.this);
                            builderInner.setMessage("Thanks for your feedback!");
                            builderInner.setCancelable(false);
                            builderInner.setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideProgress();
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builderInner.show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgress();
                            Log.w("TAGG", "Error adding document", e);
                        }
                    });
        } catch (Exception e) {
            hideProgress();
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }

    class language_adapter extends RecyclerView.Adapter<language_adapter.MyViewHolder> {

        Context _context;
        ArrayList<LanguageModel> _lst_model;
        View.OnClickListener _listener;

        public language_adapter(Context _context, ArrayList<LanguageModel> _lst_model, View.OnClickListener _listener) {
            this._context = _context;
            this._lst_model = _lst_model;
            this._listener = _listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_item_layout, viewGroup, false);
            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

            myViewHolder.lang_name.setText(_lst_model.get(i).getLang_name());
            myViewHolder.iv_lang_icon.setImageResource(_lst_model.get(i).getFlag_img());
            if (_lst_model.get(i).isSelected())
                myViewHolder.iv_tick_icon.setVisibility(View.VISIBLE);
            else
                myViewHolder.iv_tick_icon.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return (_lst_model != null ? _lst_model.size() : 0);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_lang_icon, iv_tick_icon;
            TextView lang_name;
            LinearLayout ll_main;

            public MyViewHolder(View view) {
                super(view);
                iv_lang_icon = (ImageView) view.findViewById(R.id.iv_flag);
                ll_main = view.findViewById(R.id.ll_main);

                lang_name = view.findViewById(R.id.tv_lang_name);
                iv_tick_icon = view.findViewById(R.id.iv_tick_icon);
                ll_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setTag(getAdapterPosition());
                        _listener.onClick(view);
                    }
                });
            }
        }
    }


    public String getInstalledDate() {
        String date_time = "";
        try {
            PackageManager pm = getPackageManager();
//            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long installed = new File(appFile).lastModified();
            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            date_time = formatter.format(new Date(installed));
            Log.e("TAGG", "Information installed " + installed + " time " + date_time);

        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
        return date_time;
    }

    public String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return "-"; // not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:     // api< 8: replace by 11
                case TelephonyManager.NETWORK_TYPE_GSM:      // api<25: replace by 16
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:   // api< 9: replace by 12
                case TelephonyManager.NETWORK_TYPE_EHRPD:    // api<11: replace by 14
                case TelephonyManager.NETWORK_TYPE_HSPAP:    // api<13: replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA: // api<25: replace by 17
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:      // api<11: replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:    // api<25: replace by 18
                case 19: // LTE_CA
                    return "4G";
                default:
                    return "?";
            }
        }
        return "?";
    }


    /*This is the method where user can get confirmation about app permission, this method give the result of permission dialog and says that accepted or not*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                int permission = ActivityCompat.checkSelfPermission(GalleryDashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    Toast.makeText(GalleryDashboard.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.deny_storage_permission, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.deny_storage_permission);
                    return;
                }
            } else {
                if (requestCode == REQ_MY_PAINTING) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.getHOME_BUTTON_MY_PAINTINGS(), Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.getHOME_BUTTON_MY_PAINTINGS());
                    Intent lIntent1 = new Intent(this, MyPaintingsActivity.class);
                    lIntent1.putExtra("IsFromDefault", false);
                    startActivity(lIntent1);
                } else if (requestCode == REQ_MY_MOVIE) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.getOPEN_My_Moviews(), Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.getOPEN_My_Moviews());
                    startActivity(new Intent(this, MyMoviesActivity.class));
                } else if (requestCode == REQ_PLUS_ICON) {
                    beginDoodle();
                } else if (requestCode == REQ_P_COLLECTION) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.getHOME_BUTTON_NEW_DRAW(), Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.getHOME_BUTTON_NEW_DRAW());
                    Intent _intent = new Intent(this, CategoryActivity.class);
                    _intent.putExtra("levelCount", levelList);
                    startActivity(_intent);
                } else if (requestCode == REQ_COMMUNITY) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.HOME_BUTTON_COMMUNITY, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_BUTTON_COMMUNITY);
                    startActivity(new Intent(this, Community.class));
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, constants.allow_storage_permission, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(GalleryDashboard.this, constants.allow_storage_permission);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception on onRequestPermissionsResult " + e.getMessage());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            AccessToken accessToken = AccessToken.getCurrentAccessToken();
//            isLoggedIn = accessToken != null && !accessToken.isExpired();
//            account = GoogleSignIn.getLastSignedInAccount(GalleryDashboard.this);
//            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, GalleryDashboard.this);
//
//            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                String _user_id = constants.getString(constants.UserId, GalleryDashboard.this);
//                if (_user_id != null && !_user_id.isEmpty()) {
//                    Log.e("TAG", "setOffline called GalleryDashboard 2661");
//                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
//                }
//            }
//        } catch (Exception e) {
//
//        }
    }

    public void setDrawerNotificationIcon() {
     /*   NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem navNotification = navigationView.getMenu().findItem(R.id.nav_notification);
*/
        if (AppUtils.hasUnreadNotifications(this)) {
            binding.includedHeader.ivNotification.setImageResource(R.drawable.ic_menu_notification_badge);
        } else {
            binding.includedHeader.ivNotification.setImageResource(R.drawable.drawer_notification);
        }
    }

    @SuppressLint("SetTextI18n")
    public void fetchProfileData() {


        CircleImageView profilePic = findViewById(R.id.iv_profile);
        TextView profileName = findViewById(R.id.tv_name);
        TextView tvLevel = findViewById(R.id.tv_level);
        TextView tvPoints = findViewById(R.id.tv_total_amount);
        TextView tv_points = findViewById(R.id.tv_points);


        binding.includedHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isOpen()) {
                    drawerLayout.close();
                }
            }
        });
        binding.includedHeader.navHeaderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                    KGlobal.showNetworkError(GalleryDashboard.this);
                    return;
                }
                if (AppUtils.isLoggedIn()) {
                    FireUtils.openProfileScreen(GalleryDashboard.this, null);
                } else {
                    Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        profilePic.setOnClickListener(view -> {
            if (!KGlobal.checkInternet(GalleryDashboard.this)) {
                KGlobal.showNetworkError(GalleryDashboard.this);
                return;
            }

            if (AppUtils.isLoggedIn()) {
                ContextKt.sendUserEvent(StringConstants.home_top_profile);
                FireUtils.openProfileScreen(GalleryDashboard.this, null);
            } else {
                Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                startActivity(intent);
            }

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            profilePic.setEnabled(true);
                        }
                    });
                }
            }, 2000, 1000);
        });

        String userId = constants.getString(constants.UserId, GalleryDashboard.this);

        if (!userId.isEmpty()) {
            MyApplication.get_realTimeDbUtils(this).setOnline(userId);
        }

        FirebaseFirestoreApi.userProfileFunction(constants.getString(constants.UserId, this))
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        hideProgress();

                        Map<String, Any> data = (Map<String, Any>) task.getResult().getData();

                        if (data == null)
                            return;

                        UserProfile userProfile = ContextKt.parseUserProfile(data);

                        isLoggedIn = true;
                        //   AppUtils.setLoggedIn(true);

                        if (constants.getBoolean(constants.IsGuestUser, this)) {
                            binding.includedHeader.navLogin.setVisibility(View.GONE);
                            binding.includedHeader.navLogout.setVisibility(View.GONE);
                        } else {
                            binding.includedHeader.navLogin.setVisibility(View.GONE);
                            binding.includedHeader.navLogout.setVisibility(View.GONE);
                        }

                        String name = userProfile.getName();
                        if (!TextUtils.isEmpty(name)) {
                            binding.includedHeader.navHeaderTitle.setText(name);
                            profileName.setText(name);
                            constants.putString(constants.Username, name, GalleryDashboard.this);
                        }

                        constants.putString(constants.Username, userProfile.getName(), GalleryDashboard.this);

                        try {
                            ((TextView) findViewById(R.id.tv_gallery_progress_value)).setText(userProfile.getProgress().getGallery() + "%");
                            ((TextView) findViewById(R.id.tv_big_points_progress_value)).setText(userProfile.getProgress().getBig_points() + "%");
                            ((TextView) findViewById(R.id.tv_community_progress_value)).setText(userProfile.getProgress().getCommunity() + "%");
                            ((TextView) findViewById(R.id.tv_tutorials_progress_value)).setText(userProfile.getProgress().getTutorial() + "%");
                            ((TextView) findViewById(R.id.tv_mypaintings_progress_value)).setText(userProfile.getProgress().getPainting() + "%");
                            ((TextView) findViewById(R.id.tv_myresources_progress_value)).setText(userProfile.getProgress().getResource() + "%");

                            ((ProgressBar) findViewById(R.id.GalleryProgressBar)).setProgress(userProfile.getProgress().getGallery());
                            ((ProgressBar) findViewById(R.id.BigPointsProgressBar)).setProgress(userProfile.getProgress().getBig_points());
                            ((ProgressBar) findViewById(R.id.CommunityProgressBar)).setProgress(userProfile.getProgress().getCommunity());
                            ((ProgressBar) findViewById(R.id.TutorialsProgressBar)).setProgress(userProfile.getProgress().getTutorial());
                            ((ProgressBar) findViewById(R.id.MyPaintingsProgressBar)).setProgress(userProfile.getProgress().getPainting());
                            ((ProgressBar) findViewById(R.id.MyResourcesProgressBar)).setProgress(userProfile.getProgress().getResource());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String level = userProfile.getLevel();
                        if (!level.equalsIgnoreCase("")) {
                            tvLevel.setText(level);
                            sharedPref.putString(StringConstants.user_level, level);
                        } else {
                            tvLevel.setText("Beginner 1");
                            sharedPref.putString(StringConstants.user_level, "Beginner 1");
                        }

                        tv_points.setText(userProfile.getPoints() + "");
                        tvPoints.setText(userProfile.getPoints() + "");

                        SharedPref sharedPref = new SharedPref(this);
                        sharedPref.putString("total_points", String.valueOf(userProfile.getPoints()));

                        constants.putString(constants.Email, userProfile.getEmail(), GalleryDashboard.this);


                        if (userProfile.getGender().equalsIgnoreCase("male")) {
                            constants.putString(constants.UserGender, constants.MALE, GalleryDashboard.this);
                        } else if (userProfile.getGender().equalsIgnoreCase("female")) {
                            constants.putString(constants.UserGender, constants.FEMALE, GalleryDashboard.this);
                        } else {
                            constants.putString(constants.UserGender, constants.MALE, GalleryDashboard.this);
                        }

                        int profileId = R.drawable.paintology_logo;
                        if (userProfile.getAuth_provider().equalsIgnoreCase("guest")) {
                            profileId = R.drawable.img_default_avatar;
                        }
                        if (!userProfile.getAvatar().isEmpty()) {
                            constants.putString(constants.ProfilePicsUrl, userProfile.getAvatar(), GalleryDashboard.this);
                            MyApplication.get_realTimeDbUtils(this).getDbReference().child(constants.firebase_user_list).child(constants.getString(constants.UserId, GalleryDashboard.this)).child("profile_pic").setValue(userProfile.getAvatar());
                            Picasso.get().load(Uri.parse(constants.getString(constants.ProfilePicsUrl, GalleryDashboard.this)))
                                    .transform(new CircleTransform())
                                    .placeholder(profileId)
                                    .error(profileId)
                                    .into(binding.includedHeader.navHeaderPic);
                            Picasso.get().load(Uri.parse(constants.getString(constants.ProfilePicsUrl, GalleryDashboard.this)))
                                    .placeholder(profileId)
                                    .error(profileId)
                                    .transform(new CircleTransform())
                                    .into(profilePic);
                        } else {
                            Picasso.get().load(profileId)
                                    .transform(new CircleTransform())
                                    .placeholder(profileId)
                                    .error(profileId)
                                    .into(profilePic);
                            Picasso.get().load(profileId)
                                    .transform(new CircleTransform())
                                    .placeholder(profileId)
                                    .error(profileId)
                                    .into(binding.includedHeader.navHeaderPic);
                        }

                        AppUtils.savePurchasedProducts(userProfile.getFeatures());
                        AppUtils.savePurchasedBrushes(userProfile.getBrushes());

                        if (dialogType_selected != null) {
                            if (dialogType_selected.equals(DialogType.CHAT))
                                startActivity(new Intent(GalleryDashboard.this, ChatUserList.class));
                        }

                    } else {
                        hideProgress();
                        isLoggedIn = false;
                        //  AppUtils.setLoggedIn(false);
                        resetInitialUI();
                    }
                });

    }


    private void updateToken(String token) {
        try {
            Log.e("TAG", "Authenticate Update Token Called");
            if (MyApplication.get_realTimeDbUtils(this).getCurrentUser() != null) {
                DatabaseReference reference = MyApplication.get_realTimeDbUtils(this).getDbReference().child("Tokens");
                Token token1 = new Token(token);
                reference.child(MyApplication.get_realTimeDbUtils(this).getCurrentUser().getUid()).setValue(token1);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at update token " + e.getMessage());
        }
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_privacy, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(false);

        TextView body_tv = (TextView) dialogView.findViewById(R.id.body_tv);

//        String text = "We've embraced a few changes in our Privacy Policy to make it even easier to understand what data we collect, how it's processed and the controls you have. You can find more details about our updates for new European data protection laws (GDPR) by reading our Privacy Policy and Terms of Service. By continuing, you are confirming that you are over the age of 16 or under guidance of the holder of your parental responsibility and agree to our revisions.";
        String text = "We have updated our Privacy Policy to make it easier for you to understand. You can find more details from the Privacy Policy and Terms of Service. By continuing, you are confirming that you agree to these requirements. Thank You.";


        TextView dialogButton = (TextView) dialogView.findViewById(R.id.continue_tv);

        // spanable string begin

        SpannableString SpanString = new SpannableString(text);

        ClickableSpan teremsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                Intent intent = new Intent(GalleryDashboard.this, PrivacyPolicyActivity.class);
                intent.putExtra("value", "terms");
                startActivity(intent);


            }
        };

        // Character starting from 32 - 45 is Terms and condition.
        // Character starting from 49 - 63 is privacy policy.

        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                Intent intent = new Intent(GalleryDashboard.this, PrivacyPolicyActivity.class);
                intent.putExtra("value", "privacy");
                startActivity(intent);

            }
        };

        SpanString.setSpan(teremsAndCondition, 130, 146, 0);
        SpanString.setSpan(privacy, 111, 125, 0);
        SpanString.setSpan(new ForegroundColorSpan(Color.BLUE), 130, 146, 0);
        SpanString.setSpan(new ForegroundColorSpan(Color.BLUE), 111, 125, 0);
        SpanString.setSpan(new UnderlineSpan(), 130, 146, 0);
        SpanString.setSpan(new UnderlineSpan(), 111, 125, 0);

        body_tv.setHighlightColor(getResources().getColor(android.R.color.transparent));
        body_tv.setMovementMethod(LinkMovementMethod.getInstance());
        body_tv.setText(SpanString, TextView.BufferType.SPANNABLE);
        body_tv.setSelected(false);

        //spanable string end


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("policyStatus", "1");
                editor.apply();
                alertDialog.dismiss();

                startActivity(new Intent(GalleryDashboard.this, OnboardingExample1Activity.class));
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("Tag111", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("Tag111", str);
    }

    void getCategoryDetailFromAPI(String catID, String search) {
        apiInterface = ApiClient.getRetroClient().create(ApiInterface.class);


        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "API calling", Toast.LENGTH_SHORT).show();
        }
        Bundle bundle = new Bundle();
        bundle.putString("keyword", search);
        FirebaseAnalytics.getInstance(this).logEvent("home_search", bundle);

        try {
            String endPoint;
            Map<String, Object> data = new HashMap<>();
            if (isNumberSearch) {
                FireUtils.showProgressDialog(this, getResources().getString(R.string.ss_loading_please_wait));

                NewSubCategoryActivity activity = new NewSubCategoryActivity();
                //   activity.SelectItemsAll(this,search,"");

                new TutorialUtils(GalleryDashboard.this).parseTutorial(search);

//                FirebaseFirestoreApi
//                        .getTutorialDetail(search)
//                        .addOnCompleteListener(
//                                task -> {
//                                    if (task.isSuccessful()) {
//                                        try {
//
//                                            Map<String, Object> responseData = task.getResult().getData();
//
//                                            Log.e("print data bro",responseData.toString());
//
//
//                                            Gson gson = new GsonBuilder().create();
//                                            String json = gson.toJson(responseData);
//
//                                            FirebaseTutorial tutorial = gson.fromJson(json, FirebaseTutorial.class);
//
//                                            processTutorial(tutorial, tutorial.getId());
//
//                                            Toast.makeText(this, "my tut id " + tutorial.getId(), Toast.LENGTH_SHORT).show();
//
////                                            NewSubCategoryActivity activity = new NewSubCategoryActivity();
////                                            activity.SelectItemsAll(this,tutorial.getId(),tutorial.getCategories().get(0).getId());
//
//                                        } catch (Exception e) {
//                                            Toast.makeText(this, "No Tutorial Found", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        Toast.makeText(this, "No Tutorial Found", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                        );
                return;
            } else {

                progressDialog = new ProgressDialog(GalleryDashboard.this);
                progressDialog.setTitle(getResources().getString(R.string.please_wait));
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                data.put("q", search);
                data.put("sort_by", "created_at:desc");
                data.put("page", 1);
                data.put("per_page", 5);

                endPoint = "search-contents";
            }


            FirebaseFunctions.getInstance()
                    .getHttpsCallable(endPoint)
                    .call(data)
                    .addOnCompleteListener(task -> {
                        try {

                            if (progressDialog != null && progressDialog.isShowing() && !GalleryDashboard.this.isDestroyed()) {
                                progressDialog.dismiss();
                            }


                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                FirebaseFirestoreApi.getSearchedContent(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addOnSuccessListener(documentSnapshot -> {
                                            List<String> list = (List<String>) documentSnapshot.get("content");
                                            if (list != null) {
                                                if (!list.contains(search)) {
                                                    list.add(search);
                                                    saveSearchContent(list);
                                                }
                                            } else {
                                                list = new ArrayList<>();
                                                list.add(search);
                                                saveSearchContent(list);
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("FirestoreError", e.getMessage()));
                            }

                            if (task.isSuccessful()) {
                                HttpsCallableResult result = task.getResult();
                                if (result != null) {
                                    Map<String, Object> responseData = (Map<String, Object>) result.getData();

                                    Gson gson = new GsonBuilder().create();
                                    String json = gson.toJson(responseData);
                                    longLog(json);
                                    if (isNumberSearch) {
                                        SearchNumberResponse response = gson.fromJson(json, SearchNumberResponse.class);

                                        goToTutorialsPaintActivity(response);
                                    } else {
                                        SearchContentResponse response = gson.fromJson(json, SearchContentResponse.class);
                                        goToSearchViewActivity(response, search);
                                    }
                                }
                            } else {
                                if (GalleryDashboard.this.isDestroyed()) {
                                    return;
                                }
                                showSnackBar("Failed To Load");
                            }
                        } catch (Exception e) {
                            Log.e("TAGG", "Exception " + e.getMessage());
                            e.printStackTrace();
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, "Inner Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(e -> Log.e("CloudFunctions", "Failure: " + e));
//
//            call.enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, Response<String> response) {
//                    try {
//                        if (progressDialog != null && progressDialog.isShowing() && !GalleryDashboard.this.isDestroyed()) {
//                            progressDialog.dismiss();
//                        }
//                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//                            FirebaseFirestoreApi.getSearchedContent(
//                                            FirebaseAuth.getInstance().getCurrentUser().getUid()
//                                    )
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                            List<String> list = (List<String>) documentSnapshot.get("content");
//                                            if (list != null) {
//                                                if (!list.contains(search)) {
//                                                    list.add(search);
//                                                    HashMap<String, List<String>> map = new HashMap();
//                                                    map.put("content", list);
//
//                                                    FirebaseFirestoreApi.saveSearchedContent(
//                                                                    map,
//                                                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
//                                                            )
//                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void unused) {
//                                                                    Log.e("SaveSearch", "onSuccess");
//                                                                }
//                                                            })
//                                                            .addOnFailureListener(new OnFailureListener() {
//                                                                @Override
//                                                                public void onFailure(@NonNull Exception e) {
//                                                                    Log.e("SaveSearch", e.getMessage());
//                                                                }
//                                                            });
//                                                }
//                                            } else {
//                                                list = new ArrayList<>();
//                                                list.add(search);
//                                                HashMap<String, List<String>> map = new HashMap();
//                                                map.put("content", list);
//                                                FirebaseFirestoreApi.saveSearchedContent(
//                                                                map,
//                                                                FirebaseAuth.getInstance().getCurrentUser().getUid()
//                                                        )
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void unused) {
//                                                                Log.e("SaveSearch", "onSuccess");
//                                                            }
//                                                        })
//                                                        .addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                Log.e("SaveSearch", e.getMessage());
//                                                            }
//                                                        });
//                                            }
//
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//
//                                        }
//                                    });
//
//                        }
//
//                        if (response != null && response.body() != null) {
//                            Log.e("TAGGG", "Response Data " + response.body());
//                            parseResponseManually(search, response.body());
//                        } else {
//                            if (GalleryDashboard.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
//                                return;
//                            }
//                            showSnackBar("Failed To Load");
//                        }
//                    } catch (Exception e) {
//                        Log.e("TAGG", "Exception " + e.getMessage());
//                        if (BuildConfig.DEBUG) {
//                            Toast.makeText(GalleryDashboard.this, "Inner Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//                    showSnackBar("Failed To Retrieve Content!");
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(GalleryDashboard.this, "API fail Block: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing() && !GalleryDashboard.this.isDestroyed())
                progressDialog.dismiss();

            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "Main Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    private void saveSearchContent(List<String> list) {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("content", list);

        FirebaseFirestoreApi.saveSearchedContent(map, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnSuccessListener(unused -> Log.e("SaveSearch", "onSuccess"))
                .addOnFailureListener(e -> Log.e("SaveSearch", e.getMessage()));
    }

    void showSnackBar(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    PostDetailModel _object;
    Tutorial_Type tutorial_type;

    private void processTutorial(FirebaseTutorial tutorial, String id) {


//        Toast.makeText(this, "id tut = " + tutorial.getRef(), Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = this.getSharedPreferences("brush", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d("loggggg", tutorial.toString());

        if (!tutorial.getType().isEmpty() || (tutorial.getLinks().getRedirect() != null && !tutorial.getLinks().getRedirect().isEmpty())) {
            editor.putBoolean("singleTap", tutorial.getOptions().getSingleTap());
            editor.putBoolean("line", tutorial.getOptions().getStraightLines());
            editor.putBoolean("gray_scale", tutorial.getOptions().getGrayscale());
            editor.putBoolean("block_coloring", tutorial.getOptions().getBlockColoring());
            editor.apply();


            Brush brush = tutorial.getOptions().getBrush();
            float mPrefBrushSize = brush.getSize();

            String brushColor = brush.getColor();
            int mPrefAlpha = brush.getDensity();
            mPrefAlpha = (255 * mPrefAlpha) / 100;
            int mPrefFlow = brush.getHardness();
            mPrefFlow = (255 * mPrefFlow) / 100;

            String brushMode = brush.getType();
            int mPrefBrushStyle = getBrushMode(brushMode);
            editor.putString("pref-saved", "yes");

            editor.putInt("brush-style", mPrefBrushStyle);
            editor.putFloat("brush-size", mPrefBrushSize);
            editor.putInt("brush-color", Color.parseColor(brushColor));
            editor.putInt("brush-alpha", mPrefAlpha);
            editor.putInt("brush-pressure", mPrefAlpha);
            editor.putInt("brush-flow", mPrefFlow);
            editor.apply();

            if (tutorial.getFiles().getTextFile1() != null && !tutorial.getFiles().getTextFile1().getName().isEmpty()) {


                new DownloadsTextFilesFirebase(
                        tutorial.getLinks().getYoutube(),
                        tutorial.getCanvasColor(),
                        id,
                        tutorial.getArrayColorSwatch(),
                        tutorial.getFiles().getTextFile1().getName(),
                        tutorial.getFiles().getTextFile1().getUrl(),
                        tutorial.getFiles().getTextFile2() != null ? tutorial.getFiles().getTextFile2().getName() : "",
                        tutorial.getFiles().getTextFile2() != null ? tutorial.getFiles().getTextFile2().getUrl() : ""
                ).execute();
            } else {

                if (tutorial.getType().equals("trace") || tutorial.getType().equals("overlay") || tutorial.getType().equals("blank")) {
                    try {

                        Toast.makeText(this, "3293 try equal b t o", Toast.LENGTH_SHORT).show();

                        boolean isTrace = tutorial.getType().equals("trace");

                        String youtubeId = tutorial.getLinks().getYoutube();
                        if (youtubeId != null) {
                            youtubeId = youtubeId.replace("https://youtu.be/", "")
                                    .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                        }

                        String fileName = tutorial.getImages().getContent().substring(tutorial.getImages().getContent().lastIndexOf('/') + 1);
                        if (fileName.contains("token")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }

                        File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
                        if (!file.exists()) {
                            new DownloadsImageFirebase(
                                    youtubeId != null ? youtubeId : "",
                                    fileName,
                                    isTrace,
                                    fileName,
                                    tutorial.getCanvasColor(),
                                    id,
                                    tutorial.getArrayColorSwatch(),
                                    tutorial.getType()
                            ).execute(tutorial.getImages().getContent());
                        } else {
                            openTutorialsRewardPoint(id);

                            StringConstants.IsFromDetailPage = false;

                            Intent intent = new Intent(this, PaintActivity.class);

                            if (tutorial.getType().equals("trace")) {
                                intent.setAction("Edit Paint");
                            } else if (tutorial.getType().equalsIgnoreCase("blank")) {
                                intent.setAction("New Paint");
                            } else {
                                intent.setAction("LoadWithoutTrace");
                            }

                            if (youtubeId != null && !youtubeId.isEmpty()) {
                                intent.putExtra("youtube_video_id", youtubeId);
                            }
                            intent.putExtra("path", fileName);
                            intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this));
                            intent.putExtra("drawingType", "TUTORIALS");

                            if (!tutorial.getColorSwatch().isEmpty()) {
                                Gson gson = new Gson();
                                ArrayList<String> swatches = new ArrayList<>(tutorial.getColorSwatch());
                                String swatchesJson = gson.toJson(swatches);
                                intent.putExtra("swatches", swatchesJson);
                            }
                            intent.putExtra("canvas_color", tutorial.getCanvasColor());
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            String ref = tutorial.getRef();
            KGlobal.openInBrowser(this, ref);
        }
    }

    private int getBrushMode(String brushMode) {
        // Implement this method as per your requirements
        return 0;
    }

    class DownloadsTextFilesFirebase extends AsyncTask<Void, Void, ArrayList<String>> {
        String youtubeLink, canvas, textFileName, textUrl, textfileName2, texturl2, id;

        ArrayList<ColorSwatch> swatches;

        public DownloadsTextFilesFirebase(String youtubeLink, String canvas, String id, ArrayList<ColorSwatch> swatches, String textFileName, String textUrl, String textfileName2, String texturl2) {
            this.youtubeLink = youtubeLink;
            this.canvas = canvas;
            this.id = id;
            this.swatches = swatches;
            this.textFileName = textFileName;
            this.textfileName2 = textfileName2;
            this.textUrl = textUrl;
            this.texturl2 = texturl2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(GalleryDashboard.this));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = "";

                String fileName = "";


                if (i == 0) {
                    textFileLink = textUrl;
                    fileName = textFileName;
                } else {
                    textFileLink = texturl2;
                    fileName = textfileName2;
                }

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            try {

                if (isDestroyed()) {
                    return;
                }
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

                openTutorialsRewardPoint(id);

                StringConstants.IsFromDetailPage = false;
                Log.e("PaintActivity", "Paint Flow 9");
                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);

                intent.putExtra("drawingType", "TUTORAILS");
                if (!canvas.isEmpty()) {
                    intent.putExtra("canvas_color", canvas);
                }
                // String youtubeLink = yout;
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
                if (list.size() == 2) {
                    intent.putExtra("StrokeFilePath", list.get(0));
                    intent.putExtra("EventFilePath", list.get(1));
                } else
                    Toast.makeText(GalleryDashboard.this, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                intent.putExtra("id", id);

                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }

    public class DownloadsImageFirebase extends AsyncTask<String, Void, String> {

        String youtubeLink, traceImageLink, canvas, fileName, id, type;

        Boolean isFromTrace = false;

        ArrayList<ColorSwatch> swatches;


        public DownloadsImageFirebase(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName, String canvas, String id, ArrayList<ColorSwatch> swatches, String type) {

            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.canvas = canvas;
            this.id = id;
            this.fileName = fileName;
            this.swatches = swatches;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            File path = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            //File imageFile = new File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)); // Imagename.png
            File imageFile = new File(path, fileName); // Imagename.png
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
            } catch (Exception e) {
                Log.e("TAGG", "Exception at download " + e.getMessage());
            }
            return imageFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                progressDialog.dismiss();

                Log.e("newfilename", path);
                openTutorialsRewardPoint(id);

                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);

                if (type.equalsIgnoreCase("trace")) {
                    intent.setAction("Edit Paint");
                    // intent.setAction("LoadWithoutTrace");
                } else if (type.equalsIgnoreCase("blank")) {
                    intent.setAction("New Paint");
                    //intent.setAction("LoadWithoutTrace");
                } else {
                    intent.setAction("LoadWithoutTrace");
                }

                if (!youtubeLink.isEmpty()) {
                    intent.putExtra("youtube_video_id", youtubeLink);
                }

                intent.putExtra("path", fileName);
                intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(GalleryDashboard.this));
                //}
                intent.putExtra("drawingType", "TUTORAILS");

                // if (!_object.getCanvas_color().isEmpty()) {
                intent.putExtra("canvas_color", canvas);
                //}

                if (swatches.size() > 0) {
                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                }

                intent.putExtra("id", id);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    private void openTutorialsRewardPoint(String mId) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.open_tutorial, mId);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(GalleryDashboard.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("opening_tutorials", rewardSetup.getOpening_tutorials() == null ? 0 : rewardSetup.getOpening_tutorials(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }


    void goToTutorialsPaintActivity(SearchNumberResponse response) {
        Log.d("SearchNumberResponse", response.toString());

        new DownloadOverlayFromDoDrawing(
                response.getSearchResults().getId(),
                "#ffffff",
                response.getSearchResults().getArrayColorSwatch(),
                response.getSearchResults().getImages().getContent(),
                response.getSearchResults().getId(),
                false
        ).execute();
    }

    void goToSearchViewActivity(SearchContentResponse response, String searchKeyword) {
        progressDialog.dismiss();
        Gson gson = new Gson();
        String responseJsonString = gson.toJson(response);
        AppUtils.setSearchResponse(responseJsonString);

        Intent intent = new Intent(GalleryDashboard.this, SearchViewActivity.class);
        intent.putExtra("has_search_response", true);
        intent.putExtra("search", responseJsonString);
        intent.putExtra("searchKeyword", searchKeyword);
        edt_hash_search.setText("");
        startActivity(intent);
    }

    void parseResponseManually(String search, String response) {
        try {
            try {
                if (!TextUtils.isEmpty(response)) {
                    if (response.contains("\"data\":{\"status\"")) {
                        Gson gson = new Gson();
                        PostDetailResponse postDetailResponse = gson.fromJson(response, PostDetailResponse.class);

                        if (postDetailResponse != null) {
                            String status = postDetailResponse.getData().getStatus();
                            if (status.equalsIgnoreCase("Authentication failed. Post Id is missing.") ||
                                    status.equalsIgnoreCase("No Tutorial exists with this Post Id")) {

                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GalleryDashboard.this);
                                builder.setMessage("Tutorial not found. Please try again")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                                return;
                            }
                        }
                    } else {
                        Log.e("API Response", "Response" + response);
                    }
                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, "Json Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, "Parse enter", Toast.LENGTH_SHORT).show();
            }
            SearchResponse searchResponse = null;
            try {
                searchResponse = new Gson().fromJson(response, SearchResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, "GSON Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            Log.e("API Response", "searchResponse" + response);

            if (searchResponse != null) {
                boolean isNumberSearch = searchResponse.getData().isNumberSearch();
                String searchType = searchResponse.getData().getSearchedNumberIs();
                String searchStatus = searchResponse.getData().getSearchResponse();

                if (BuildConfig.DEBUG) {
                    Toast.makeText(GalleryDashboard.this, "isNumberSearch: " + isNumberSearch, Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(GalleryDashboard.this, "" + searchStatus, Toast.LENGTH_SHORT).show();

                if (isNumberSearch) {
                    progressDialog.dismiss();

                    Gson gson = new Gson();

                    switch (searchType) {
                        case "tutorial":
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, "Tutorial searched by id.", Toast.LENGTH_SHORT).show();
                            }
                            List<Tutorial> list = searchResponse.getData().getTutorials();
                            Tutorial tutorial = list.get(0);

                            Toast.makeText(this,
                                    "We found the tutorial #" + tutorial.getId() +
//                                    "\nCategory "+ "" +
                                            "\n" + tutorial.getPostTitle(), Toast.LENGTH_SHORT).show();
//                    _object.setCanvas_color(tutorial.getCanvasColor());
//                    _object.setCategoryName(tutorial.getC);
//                    _object.setCategoryURL(tutorial.getCategoryURL());
//                    _object.setExternal_link(tutorial.getE);
//                    _object.setID(tutorial.getId().toString());
//                    _object.setFeaturedImage(tutorial.get);
//                    _object.setList_related_post(tutorial.getRelatedPostsData());

                            handleNumberSearch(tutorial);
                            break;
                        case "category":
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, "Category searched by id.", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(this, NewSubCategoryActivity.class);
                            intent.putExtra("sub_cat_id", searchedSubCatId);
                            intent.putExtra("cate_id", search);
                            intent.putExtra("level", tv_level.getText().toString());
                            intent.putExtra("childs", gson.toJson(searchResponse.getData().getChilds()));
                            intent.putExtra("total_tutorials", searchResponse.getData().getCount());
                            intent.putExtra("cate_name", searchResponse.getData().getName());
                            startActivity(intent);
                            break;
                        case "subcategory":
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(GalleryDashboard.this, "Subcategory searched by id.", Toast.LENGTH_SHORT).show();
                            }

                            searchedSubCatId = search;
                            getCategoryDetailFromAPI(BuildConfig.CAT_ID, String.valueOf(searchResponse.getData().getParentCategoryId()));

//                            Intent intent2 = new Intent(this, NewSubCategoryActivity.class);
//                            intent2.putExtra("sub_cat_id", search);
//                            intent2.putExtra("cate_id", searchResponse.getData().getParentCategoryId());
//                            intent2.putExtra("childs", gson.toJson(searchResponse.getData().getChilds()));
//                            intent2.putExtra("total_tutorials", searchResponse.getData().getCount());
//                            intent2.putExtra("cate_name", searchResponse.getData().getParentName());
//                            startActivity(intent2);
                            break;
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, "You searched text.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    Gson gson = new Gson();
                    response = gson.toJson(searchResponse.getData());
                    AppUtils.setSearchResponse(response);
                    Intent intent = new Intent(GalleryDashboard.this, SearchViewActivity.class);
//                    intent.putExtra("search_response", response);
                    intent.putExtra("has_search_response", true);
                    intent.putExtra("search", search);
                    edt_hash_search.setText("");
                    startActivity(intent);
                }

            }

//            if (response.startsWith("{\"tutorials\"")) {
//
//                JSONObject jsonObject = new JSONObject(response);
//                if (jsonObject.length() > 0) {
//                    boolean isNumberSearch = jsonObject.getBoolean("isNumberSearch");
//                    String tutorials = jsonObject.get("tutorials").toString();
//                    Gson gson = new Gson();
//                    Type itemListType = new TypeToken<ArrayList<Tutorial>>(){}.getType();
//
//                    List<Tutorial> list = gson.fromJson(response, itemListType);
//
//                    if (isNumberSearch && list.size() > 0) {
//                        handleNumberSearch(list.get(0));
//                    } else {
//                        if (BuildConfig.DEBUG) {
//                            Toast.makeText(GalleryDashboard.this, "You searched text.", Toast.LENGTH_SHORT).show();
//                        }
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(GalleryDashboard.this, SearchActivity.class);
//                        intent.putExtra("search_response", response);
//                        intent.putExtra("search", search);
//                        startActivity(intent);
//                    }
//                }
//
//            } else {
//                JSONArray mainArray = new JSONArray(response);
//                if (mainArray.length() > 0) {
//                    ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
//                    JSONObject objectFirst = mainArray.getJSONObject(0);
//                    _object = new PostDetailModel();
//                    _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
//                    _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
//                    _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
//                    _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
//                    _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
//                    _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
//                    _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
//                    _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
//                    _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
//                    _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
//                    _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
//                    _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
//                    _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
//                    _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");
//
//                    if (objectFirst.has("color_swatch") && !objectFirst.isNull("color_swatch")) {
//                        JSONArray swatchesArray = objectFirst.getJSONArray("color_swatch");
//                        ArrayList<ColorSwatch> swatches = new ArrayList<>();
//
//                        if (swatchesArray != null && swatchesArray.length() > 0) {
//                            for (int i = 0; i < swatchesArray.length(); i++) {
//
//                                String swatch = swatchesArray.getJSONObject(i).getString("color_swatch");
//
//                                ColorSwatch colorSwatch = new ColorSwatch();
//                                colorSwatch.setColor_swatch(swatch);
//                                swatches.add(colorSwatch);
//                            }
//
//                        }
//
//                        _object.setSwatches(swatches);
//
//                        // save swatches into database
//                        ColorSwatchDao colorSwatchDao = db.colorSwatchDao();
//
//                        ColorSwatchEntity colorSwatchEntity = new ColorSwatchEntity();
//                        colorSwatchEntity.postId = Integer.parseInt(_object.getID());
//                        colorSwatchEntity.swatches = new Gson().toJson(_object.getSwatches());
//                        Executors.newSingleThreadExecutor().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                colorSwatchDao.insertAll(colorSwatchEntity);
//                            }
//                        });
//
//                    }
//
//                    if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
//                        _object.setResizeImage(objectFirst.getString("ResizeImage"));
//                    }
//                    if (objectFirst.has("RelatedPostsData")) {
//                        JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
//                        ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
//                        if (related_list_json != null && related_list_json.length() > 0) {
//                            for (int i = 0; i < related_list_json.length(); i++) {
//                                RelatedPostsData obj_related = new RelatedPostsData();
//                                JSONObject obj = related_list_json.getJSONObject(i);
//                                if (obj.has("ID")) {
//                                    obj_related.setID(obj.getInt("ID"));
//                                }
//                                if (obj.has("post_title") && obj.getString("post_title") != null) {
//                                    obj_related.setPost_title(obj.getString("post_title"));
//                                }
//                                if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
//                                    obj_related.setThumbImage(obj.getString("thumbImage"));
//                                }
//                                related_List.add(obj_related);
//                            }
//                            _object.setList_related_post(related_List);
//                        }
//                    }
//                    ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
//                    ContentSectionModel obj_content = new ContentSectionModel();
//                    obj_content.setUrl(_object.getThumb_url());
//                    obj_content.setCaption("Featured");
//                    obj_content.setVideoContent(false);
//                    contentSectionList.add(obj_content);
//
//                    if (objectFirst.has("EmbededData")) {
//                        JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
//                        for (int i = 0; i < embededVideoList.length(); i++) {
//                            obj_content = new ContentSectionModel();
//                            JSONObject obj = embededVideoList.getJSONObject(i);
//                            obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
//                            obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");
//
//                            if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {
//
//                                if (obj_content.getUrl().contains("youtu.be")) {
//                                    obj_content.setVideoContent(true);
//                                    String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
//                                    obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
//                                }
//                            }
//                            contentSectionList.add(obj_content);
//                        }
//                    }
//
//                    try {
//                        if (objectFirst.has("EmbededImage")) {
//                            JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
//                            for (int i = 0; i < embededImageList.length(); i++) {
//                                JSONObject object = embededImageList.getJSONObject(i);
//                                obj_content = new ContentSectionModel();
//                                obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
//                                obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
//                                obj_content.setVideoContent(false);
//                                contentSectionList.add(obj_content);
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
//                    }
//                    _object.setFeaturedImage(contentSectionList);
//                    if (objectFirst.has("videos_and_files")) {
//
//                        JSONArray videoArray = null;
//                        try {
//                            videoArray = objectFirst.getJSONArray("videos_and_files");
//                        } catch (Exception e) {
//
//                        }
//                        if (videoArray != null)
//                            for (int i = 0; i < videoArray.length(); i++) {
//                                JSONObject obj = videoArray.getJSONObject(i);
//                                videos_and_files videos_and_files = new videos_and_files();
//                                if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
//                                    text_files obj_text_file = new text_files();
//                                    JSONObject obj_text = obj.getJSONObject("text_file");
//                                    obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
//                                    obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
//                                    obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
//                                    obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
//                                    obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
//                                    videos_and_files.setObj_text_files(obj_text_file);
//                                } else
//                                    videos_and_files.setObj_text_files(null);
//
//                                try {
//                                    if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
//                                        trace_image obj_trace = new trace_image();
//                                        JSONObject obj_trace_object = obj.getJSONObject("trace_image");
//                                        obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
//                                        obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
//                                        obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
//                                        obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
//                                        obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
//                                        if (obj_trace_object.has("sizes")) {
//                                            JSONObject objSize = obj_trace_object.getJSONObject("sizes");
//                                            sizes obj_size = new sizes();
//                                            obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
//                                            obj_trace.setObj_sizes(obj_size);
//                                        } else {
//                                            obj_trace.setObj_sizes(null);
//                                        }
//                                        videos_and_files.setObj_trace_image(obj_trace);
//                                    } else
//                                        videos_and_files.setObj_trace_image(null);
//
//                                } catch (Exception e) {
//                                    Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
//                                }
//                                try {
//                                    if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
//                                        Overlaid overlaid = new Overlaid();
//                                        JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
//                                        if (obj_overlaid_object != null) {
//                                            overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
//                                            overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
//                                            overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
//                                        }
//                                        videos_and_files.setObj_overlaid(overlaid);
//                                    } else
//                                        videos_and_files.setObj_overlaid(null);
//
//                                } catch (Exception e) {
//                                    Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
//                                }
//                                _lst_video_file.add(videos_and_files);
//                            }
//
//                        if (_lst_video_file != null && !_lst_video_file.isEmpty())
//                            _object.setVideo_and_file_list(_lst_video_file);
//                    } else
//                        _object.setVideo_and_file_list(null);
//
//                }
//
//                if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() >= 2 && (_object.getVideo_and_file_list().get(0).getObj_text_files() != null && _object.getVideo_and_file_list().get(1).getObj_text_files() != null) && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//                    if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null || _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
//                        tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;
//
//                    } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
//                        tutorial_type = Tutorial_Type.Strokes_Window;
//
//                    } else {
//                        tutorial_type = Tutorial_Type.Strokes_Window;
//
//                    }
//                } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//
//                    tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
//                } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
//
//                    tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
//                } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()) {
//
//                    tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
//                } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()) {
//
//                    tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
//                } else if (_object.getExternal_link() != null && !_object.getExternal_link().isEmpty()) {
//                    if (_object.getExternal_link().contains("youtu.be")) {
//
//                        tutorial_type = Tutorial_Type.SeeVideo_From_External_Link;
//                    } else {
//
//                        tutorial_type = Tutorial_Type.Read_Post;
//                    }
//                } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty()) {
//
//                    tutorial_type = Tutorial_Type.See_Video;
//                } else {
//
//                    tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
//                }
//
//
//                progressDialog.dismiss();
//
//                processTutorial();
//            }

        } catch (Exception e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(GalleryDashboard.this, "Parse Main Catch Block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
        }
    }

    private void handleNumberSearch(Tutorial tutorial) {
        Tutorial _object = tutorial;
//        GalleryDashboard.Tutorial_Type tutorial_type;

        if (_object != null) {
            if (_object.getVideosAndFiles() != null && _object.getVideosAndFiles().size() >= 2 && _object.getVideosAndFiles()
                    .get(0).getTextFile() != null && _object.getVideosAndFiles()
                    .get(1)
                    .getTextFile() != null && _object.getYoutubeLink() != null && !_object.getYoutubeLink().isEmpty()
            ) {
                if (_object.getVideosAndFiles().get(0)
                        .getOverlayImage() != null || _object.getVideosAndFiles().get(1)
                        .getOverlayImage() != null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;
                } else if (_object.getVideosAndFiles().get(0)
                        .getTraceImage() == null || _object.getVideosAndFiles().get(1)
                        .getTraceImage() == null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                }
            } else if (_object.getVideosAndFiles() != null && !_object.getVideosAndFiles().isEmpty() && _object.getVideosAndFiles().get(0)
                    .getTraceImage() != null && _object.getYoutubeLink() != null && !_object.getYoutubeLink().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
            } else if (_object.getVideosAndFiles() != null && !_object.getVideosAndFiles().isEmpty() && _object.getVideosAndFiles().get(0)
                    .getOverlayImage() != null && _object.getYoutubeLink() != null && !_object.getYoutubeLink()
                    .isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
            } else if (_object.getVideosAndFiles() != null && !_object.getVideosAndFiles().isEmpty() &&
                    _object.getVideosAndFiles().get(0).getOverlayImage() != null && _object.getYoutubeLink().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
            } else if (_object.getVideosAndFiles() != null && !_object.getVideosAndFiles().isEmpty() &&
                    _object.getVideosAndFiles().get(0).getTraceImage() != null && _object.getYoutubeLink().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
            } else if (_object.getYoutubeLink() != null && !_object.getYoutubeLink().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.See_Video;
            } else {
                tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
            }

            processTutorial(tutorial_type, _object);
        }

    }

    private void processTutorial(Tutorial_Type tutorial_type, Tutorial _object) {
        if (tutorial_type == Tutorial_Type.See_Video) {
            String eventName = "watch_video_";
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(GalleryDashboard.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getYoutubeLink());
            intent.putExtra("isVideo", true);
            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
//            String eventName = "read_post_";
//
//            try {
//                Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
//                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
//                startActivity(browserIntent);*/
//                KGlobal.openInBrowser(GalleryDashboard.this, _object.getExternal_link().replace("htttps://", "https://").trim());
//            } catch (ActivityNotFoundException ex) {
//            } catch (Exception e) {
//            }
            return;
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
//            String eventName = "watch_video_from_external_link_";
//
//            StringConstants.IsFromDetailPage = true;
//            Intent intent = new Intent(GalleryDashboard.this, Play_YotubeVideo.class);
//            intent.putExtra("url", _object.getExternal_link());
//            intent.putExtra("isVideo", true);
//            Log.e("TAGGG", "URL " + _object.getExternal_link());
//            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            String eventName = "video_tutorial_overlaid_";

            String fileName = _object.getVideosAndFiles().get(0).getOverlayImage().getFilename();
            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
            String youtubeLink = _object.getYoutubeLink();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                if (!file.exists()) {
                    new DownloadsImage(_youtube_id, _object.getVideosAndFiles().get(0).getOverlayImage().getUrl(), false, _object.getVideosAndFiles().get(0).getOverlayImage().getFilename()).execute(_object.getVideosAndFiles().get(0).getOverlayImage().getUrl());
                    return;
                } else {
//                    if (_object.getPost_title() != null)
//                        FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());

                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("youtube_video_id", _youtube_id);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this));
                    if (!_object.getCanvasColor().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvasColor());
                    }

                    List<ColorSwatch> swatches = _object.getColorSwatch();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", _object.getId());
                    startActivity(intent);
                    return;
                }
            } else {
                Toast.makeText(GalleryDashboard.this, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            String eventName = "do_drawing_overlay_";

            String fileName = _object.getVideosAndFiles().get(0).getOverlayImage().getFilename();
            String fileURL = _object.getVideosAndFiles().get(0).getOverlayImage().getUrl();
            String id = _object.getId().toString();
            String canvasColor = _object.getCanvasColor();
            List<ColorSwatch> swatches = _object.getColorSwatch();
            new DownloadOverlayFromDoDrawing(id, canvasColor, swatches, fileURL, fileName, false).execute();
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {

            String fileName = _object.getVideosAndFiles().get(0).getTraceImage().getFilename();
            String fileURL = _object.getVideosAndFiles().get(0).getTraceImage().getUrl();
            String id = _object.getId().toString();
            String canvasColor = _object.getCanvasColor();
            List<ColorSwatch> swatches = _object.getColorSwatch();
            new DownloadOverlayFromDoDrawing(id, canvasColor, swatches, fileURL, fileName, true).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            new DownloadsTextFiles(_object.getVideosAndFiles(), _object.getYoutubeLink()).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {

            String OverLayName = "", OverLayUrl = "";

            if (_object.getVideosAndFiles().get(0).getOverlayImage() != null) {
                OverLayName = (_object.getVideosAndFiles().get(0).getOverlayImage().getFilename() != null ? _object.getVideosAndFiles().get(0).getOverlayImage().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideosAndFiles().get(0).getOverlayImage().getUrl();
            } else {
                OverLayName = (_object.getVideosAndFiles().get(1).getOverlayImage().getFilename() != null ? _object.getVideosAndFiles().get(1).getOverlayImage().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideosAndFiles().get(1).getOverlayImage().getUrl();
            }


            new DownloadOverlayImage(OverLayUrl, OverLayName).execute();

        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
            try {
                String youtubeLink = _object.getYoutubeLink();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    if (_object.getVideosAndFiles() != null && _object.getVideosAndFiles().get(0).getTraceImage() != null && _object.getVideosAndFiles().get(0).getTraceImage().getSizes() != null) {
                        if (_object.getVideosAndFiles().get(0).getTraceImage().getSizes().getLarge() != null) {
                            String fileName = _object.getVideosAndFiles().get(0).getTraceImage().getSizes().getLarge().substring(_object.getVideosAndFiles().get(0).getTraceImage().getSizes().getLarge().lastIndexOf('/') + 1);
                            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
                            if (!file.exists())
                                new DownloadsImage(_youtube_id, _object.getVideosAndFiles().get(0).getTraceImage().getSizes().getLarge(), true, "").execute(_object.getVideosAndFiles().get(0).getTraceImage().getSizes().getLarge());
                            else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());

                                StringConstants.IsFromDetailPage = false;
                                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                                intent.putExtra("youtube_video_id", _youtube_id);
                                intent.setAction("YOUTUBE_TUTORIAL");
                                intent.putExtra("paint_name", file.getAbsolutePath());
                                if (!_object.getCanvasColor().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvasColor());
                                }

                                List<ColorSwatch> swatches = _object.getColorSwatch();

                                Gson gson = new Gson();
                                String swatchesJson = gson.toJson(swatches);

                                intent.putExtra("swatches", swatchesJson);
                                intent.putExtra("id", _object.getId());
                                startActivity(intent);
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                        StringConstants.IsFromDetailPage = false;
                        Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                        intent.putExtra("youtube_video_id", _youtube_id);
                        intent.setAction("YOUTUBE_TUTORIAL");
                        if (!_object.getCanvasColor().isEmpty()) {
                            intent.putExtra("canvas_color", _object.getCanvasColor());
                        }

                        List<ColorSwatch> swatches = _object.getColorSwatch();

                        Gson gson = new Gson();
                        String swatchesJson = gson.toJson(swatches);

                        intent.putExtra("swatches", swatchesJson);
                        intent.putExtra("id", _object.getId());
                        startActivity(intent);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(GalleryDashboard.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            try {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(GalleryDashboard.this, defaultLink.trim());
            } catch (ActivityNotFoundException anf) {

            } catch (Exception e) {

            }
        }
    }

//    void processTutorial() {
//
//        if (tutorial_type == Tutorial_Type.See_Video) {
//            String eventName = "watch_video_";
//            StringConstants.IsFromDetailPage = true;
//            Intent intent = new Intent(GalleryDashboard.this, Play_YotubeVideo.class);
//            intent.putExtra("url", _object.getYoutube_link_list());
//            intent.putExtra("isVideo", true);
//            startActivity(intent);
//            return;
//        } else if (tutorial_type == Tutorial_Type.Read_Post) {
//            String eventName = "read_post_";
//
//            try {
//                Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
//                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
//                startActivity(browserIntent);*/
//                KGlobal.openInBrowser(GalleryDashboard.this, _object.getExternal_link().replace("htttps://", "https://").trim());
//            } catch (ActivityNotFoundException ex) {
//            } catch (Exception e) {
//            }
//            return;
//        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
//            String eventName = "watch_video_from_external_link_";
//
//            StringConstants.IsFromDetailPage = true;
//            Intent intent = new Intent(GalleryDashboard.this, Play_YotubeVideo.class);
//            intent.putExtra("url", _object.getExternal_link());
//            intent.putExtra("isVideo", true);
//            Log.e("TAGGG", "URL " + _object.getExternal_link());
//            startActivity(intent);
//            return;
//        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
//            String eventName = "video_tutorial_overlaid_";
//
//
//            String fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename();
//            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
//            String youtubeLink = _object.getYoutube_link_list();
//            if (youtubeLink != null) {
//                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
//                if (!file.exists()) {
//                    new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl(), false, _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename()).execute(_object.getVideo_and_file_list().get(0).obj_overlaid.getUrl());
//                    return;
//                } else {
////                    if (_object.getPost_title() != null)
////                        FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
//
//                    StringConstants.IsFromDetailPage = false;
//                    Intent intent = new Intent(GalleryDashboard.this, Paintor.class);
//                    intent.setAction("LoadWithoutTrace");
//                    intent.putExtra("path", fileName);
//                    intent.putExtra("youtube_video_id", _youtube_id);
//                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this));
//                    if (!_object.getCanvas_color().isEmpty()) {
//                        intent.putExtra("canvas_color", _object.getCanvas_color());
//                    }
//
//                    List<ColorSwatch> swatches = _object.getSwatches();
//
//                    Gson gson = new Gson();
//                    String swatchesJson = gson.toJson(swatches);
//
//                    intent.putExtra("swatches", swatchesJson);
//                    intent.putExtra("id", _object.getID());
//                    startActivity(intent);
//                    return;
//                }
//            } else {
//                Toast.makeText(GalleryDashboard.this, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
//            }
//        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
//            String eventName = "do_drawing_overlay_";
//
//            String fileName = _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename();
//            String fileURL = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
//            new DownloadOverlayFromDoDrawing(fileURL, fileName, false).execute();
//        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {
//
//            String fileName = _object.getVideo_and_file_list().get(0).getObj_trace_image().getFilename();
//            String fileURL = _object.getVideo_and_file_list().get(0).getObj_trace_image().getUrl();
//            new DownloadOverlayFromDoDrawing(fileURL, fileName, true).execute();
//        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
//            new DownloadsTextFiles(_object).execute();
//        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {
//
//            String OverLayName = "", OverLayUrl = "";
//
//            if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
//                OverLayName = (_object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() : "overLaid.jpg");
//                OverLayUrl = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
//            } else {
//                OverLayName = (_object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() : "overLaid.jpg");
//                OverLayUrl = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl();
//            }
//
//
//            new DownloadOverlayImage(OverLayUrl, OverLayName).execute();
//
//        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
//            try {
//                String youtubeLink = _object.getYoutube_link_list();
//                if (youtubeLink != null) {
//                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
//                    if (_object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().get(0).obj_trace_image != null && _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes() != null) {
//                        if (_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge() != null) {
//                            String fileName = _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().substring(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().lastIndexOf('/') + 1);
//                            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
//                            if (!file.exists())
//                                new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge(), true, "").execute(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge());
//                            else {
////                                if (_object.getPost_title() != null)
////                                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
//
//                                StringConstants.IsFromDetailPage = false;
//                                Intent intent = new Intent(GalleryDashboard.this, Paintor.class);
//                                intent.putExtra("youtube_video_id", _youtube_id);
//                                intent.setAction("YOUTUBE_TUTORIAL");
//                                intent.putExtra("paint_name", file.getAbsolutePath());
//                                if (!_object.getCanvas_color().isEmpty()) {
//                                    intent.putExtra("canvas_color", _object.getCanvas_color());
//                                }
//
//                                List<ColorSwatch> swatches = _object.getSwatches();
//
//                                Gson gson = new Gson();
//                                String swatchesJson = gson.toJson(swatches);
//
//                                intent.putExtra("swatches", swatchesJson);
//                                intent.putExtra("id", _object.getID());
//                                startActivity(intent);
//                            }
//                        }
//                    } else {
////                        if (_object.getPost_title() != null)
////                            FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
//                        StringConstants.IsFromDetailPage = false;
//                        Intent intent = new Intent(GalleryDashboard.this, Paintor.class);
//                        intent.putExtra("youtube_video_id", _youtube_id);
//                        intent.setAction("YOUTUBE_TUTORIAL");
//                        if (!_object.getCanvas_color().isEmpty()) {
//                            intent.putExtra("canvas_color", _object.getCanvas_color());
//                        }
//
//                        List<ColorSwatch> swatches = _object.getSwatches();
//
//                        Gson gson = new Gson();
//                        String swatchesJson = gson.toJson(swatches);
//
//                        intent.putExtra("swatches", swatchesJson);
//                        intent.putExtra("id", _object.getID());
//                        startActivity(intent);
//                    }
//                }
//
//            } catch (Exception e) {
//                Toast.makeText(GalleryDashboard.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
//            }
//        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
//            try {
//                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
//                startActivity(browserIntent);*/
//                KGlobal.openInBrowser(GalleryDashboard.this, defaultLink.trim());
//            } catch (ActivityNotFoundException anf) {
//
//            } catch (Exception e) {
//
//            }
//        }
//    }

    class DownloadsImage extends AsyncTask<String, Void, String> {
        String youtubeLink, traceImageLink, fileName;
        Boolean isFromTrace = false;

        public DownloadsImage(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName) {
            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GalleryDashboard.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            File path = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            File imageFile = new File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)); // Imagename.png
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
            } catch (Exception e) {
                Log.e("TAGG", "Exception at download " + e.getMessage());
            }
            return imageFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                progressDialog.dismiss();
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());

                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                } else {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(GalleryDashboard.this));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    class DownloadsTextFiles extends AsyncTask<Void, Void, ArrayList<String>> {
        List<VideosAndFile> videosAndFiles;
        String youtubeLink;

        public DownloadsTextFiles(List<VideosAndFile> videosAndFiles, String youtubeLink) {
            this.videosAndFiles = videosAndFiles;
            this.youtubeLink = youtubeLink;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GalleryDashboard.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(GalleryDashboard.this));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = videosAndFiles.get(i).getTextFile().getUrl();
                String fileName = videosAndFiles.get(i).getTextFile().getFilename();

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            try {

                if (GalleryDashboard.this.isDestroyed()) {
                    return;
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color());
                }

                List<ColorSwatch> swatches = _object.getSwatches();

                Gson gson = new Gson();
                String swatchesJson = gson.toJson(swatches);

                intent.putExtra("swatches", swatchesJson);
                intent.putExtra("id", _object.getID());

                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
                if (list.size() == 2) {
                    intent.putExtra("StrokeFilePath", list.get(0));
                    intent.putExtra("EventFilePath", list.get(1));
                } else
                    Toast.makeText(GalleryDashboard.this, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());

                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }

    class DownloadOverlayImage extends AsyncTask<Void, Void, ArrayList<String>> {
        String traceImageLink, fileName;

        public DownloadOverlayImage(String traceImageLink, String fileName) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GalleryDashboard.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> filesList = downloadTextFiles();

            File file = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this), fileName);

            if (file.exists()) {
                return filesList;
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Create Path to save Image
                File path = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
                File imageFile = new File(path, fileName); // Imagename.png
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e.getMessage());
                }
                return filesList;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> lst_main) {
            super.onPostExecute(lst_main);
            try {
                progressDialog.dismiss();
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color());
                }

                List<ColorSwatch> swatches = _object.getSwatches();

                Gson gson = new Gson();
                String swatchesJson = gson.toJson(swatches);

                intent.putExtra("swatches", swatchesJson);
                intent.putExtra("id", _object.getID());
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_OVERLAID");
                if (lst_main.size() == 2) {
                    intent.putExtra("StrokeFilePath", lst_main.get(0));
                    intent.putExtra("EventFilePath", lst_main.get(1));
                }
                intent.putExtra("OverlaidImagePath", new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this), fileName).getAbsolutePath());
                startActivity(intent);
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }


        public ArrayList<String> downloadTextFiles() {
            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(GalleryDashboard.this));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _object.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _object.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);

                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }
    }

    class DownloadOverlayFromDoDrawing extends AsyncTask<Void, Void, String> {
        String traceImageLink, fileName;
        Boolean isFromTrace = false;
        String id;
        String canvasColor;
        List<ColorSwatch> swatches = new ArrayList<>();

        public DownloadOverlayFromDoDrawing(String id, String canvasColor, List<ColorSwatch> swatches,
                                            String traceImageLink, String fileName, Boolean isFromTrace) {
            this.id = id;
            this.canvasColor = canvasColor;
            this.swatches = swatches;
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
            this.isFromTrace = isFromTrace;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GalleryDashboard.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            Log.d("doInBackground", "doInBackground");
            File file = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this), fileName);
            if (file.exists()) {
                return file.getAbsolutePath();
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Create Path to save Image
                File path = new File(KGlobal.getTraceImageFolderPath(GalleryDashboard.this)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
                File imageFile = new File(path, fileName); // Imagename.png
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e.getMessage());
                }
                return imageFile.getAbsolutePath();
            }
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                progressDialog.dismiss();
                StringConstants.IsFromDetailPage = false;
                if (isFromTrace) {
                    Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                    intent.setAction("Edit Paint");
                    intent.putExtra("FromLocal", true);
                    intent.putExtra("paint_name", path);
                    if (!canvasColor.isEmpty()) {
                        intent.putExtra("canvas_color", canvasColor);
                    }

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", id);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(GalleryDashboard.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(GalleryDashboard.this));
                    if (!canvasColor.isEmpty()) {
                        intent.putExtra("canvas_color", canvasColor);
                    }

//                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + path);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    private void showRewardPointDialog() {
        final Dialog exitDialog = new Dialog(this, R.style.my_dialog);
        final DialogRewardBinding dialogBinding = DialogRewardBinding.inflate(getLayoutInflater());
        exitDialog.setContentView(dialogBinding.getRoot());

        String title = "Reward Points!";
        String content = "Congratulations, you have earned 50 points!";
        dialogBinding.tvDialogTitle.setText(title);
        dialogBinding.tvDialogContent.setText(content);
        dialogBinding.btnDoAward.setText(Html.fromHtml("<b>Continue</b>"));

        dialogBinding.tvDialogContent.setTextSize(14);
        dialogBinding.btnDoAward.setTextSize(14);

        dialogBinding.btnDoAward.setBackgroundResource(0);
        dialogBinding.btnDoAward.setTextColor(Color.parseColor("#4C6FE7"));

        dialogBinding.btnDoAward.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                editor.putBoolean("isFirstTimeLogin", false).apply();
                exitDialog.dismiss();
            }
        });

        exitDialog.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        actionProfile = menu.findItem(R.id.action_profile);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_profile:
                if (AppUtils.isLoggedIn()) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(GalleryDashboard.this, constants.HOME_MY_PROFILE_VIEW, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(GalleryDashboard.this, constants.HOME_MY_PROFILE_VIEW);
                    FireUtils.openProfileScreen(this, null);
                } else {
                    Intent intent = new Intent(GalleryDashboard.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_quick_draw:
                beginDoodle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
