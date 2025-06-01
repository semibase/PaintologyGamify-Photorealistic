package com.paintology.lite.photorealistic.drawing.Community;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity;
import com.paintology.lite.photorealistic.drawing.Activity.MyConstantsKt;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.Fragment.MainCollectionFragment;
import com.paintology.lite.photorealistic.drawing.Model.GetUserProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.BannerCallBack;
import com.paintology.lite.photorealistic.drawing.ads.enums.CollapsiblePositionType;
import com.paintology.lite.photorealistic.drawing.bus.UserLoginUpdateEvent;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.CommunityInterface;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PostDetail_Main_Interface;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community extends BaseActivity implements View.OnClickListener, CommunityInterface, GoogleApiClient.OnConnectionFailedListener, PostDetail_Main_Interface, View.OnTouchListener {


    //    private TabLayout tabLayout;
//    private ViewPager viewPager;
//    TextView tv_popular, tv_art_fav, tv_art_medium, tv_art_ability;
    public static CommunityInterface obj_cmunity;
    //    Spinner spn_popular, spn_art_fav, spn_art_medium, spn_art_ablity;
    boolean isLoggedIn;
    StringConstants constants = new StringConstants();

//    ImageView iv_profile;

    GoogleSignInAccount account;
    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String isLoginInPaintology;

    public static String KeyFromCommunity = null;

    private GoogleSignInClient mGoogleSignInClient;

    int RC_SIGN_IN = 7;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    ProgressDialog progressDialog = null;
    GoogleApiClient googleApiClient;

    LoginButton facebook_login_btn;
    ApiInterface apiInterface;
    String LoginInPaintology;

//    ImageView iv_post_image;

    //    LinearLayout ll_1, ll_2, ll_3, ll_4;
    ArrayList<Integer> _indexes = new ArrayList<>();

    boolean isFromProfile = false;


    //    ImageView iv_header_menu;
    FirebaseFirestore db_firebase;

    RelativeLayout fm_image;
    ImageView iv_enlarge_image, iv_community_chat, iv_community_create_post;
    ImageView tv_back;

    OperationAfterLogin _operationLogin = null;

    public static PostDetail_Main_Interface obj_interface;
//    ImageView iv_direct;
//    FrameLayout fm_main;

    //    FloatingActionButton iv_fab, fab_left;
    FrameLayout fm_hashtag_dialog;
    ImageView iv_animation, iv_arrow, iv_close_gif/*, iv_search*/;
    LinearLayout ll_search_container;
//    TextView tv_toolbar_title;

    ImageView btn_search_header;
    EditText edt_hash_search;
    private AlertDialog alertDialogViewModel = null;
    private MenuItem actionProfile;
    private MenuItem actionChat;
    private boolean showExitDialog;

//    RealTimeDBUtils realTimeDBUtils;
//    private String logged_user_id;
//    ArrayList<DeletedUser> _lst_deleted = new ArrayList<>();
//    ArrayList<BlockedUsersModel> _lst_blocked = new ArrayList<>();
//    ArrayList<MyUsersModel.data> lst_my_user = new ArrayList<>();
//    List<Firebase_User> _user_list = new ArrayList<Firebase_User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_community);

        FrameLayout frameLayout = findViewById(R.id.ads_place_holder);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create a custom TextView for the title
        TextView titleTextView = new TextView(this);
        titleTextView.setText(getString(R.string.community));
        titleTextView.setTextSize(20);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        titleTextView.setGravity(Gravity.CENTER);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        toolbar.addView(titleTextView, layoutParams);


        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Intent intent = getIntent();
            showExitDialog = intent.getBooleanExtra("showExitDialog", false);

            KeyFromCommunity = intent.getStringExtra("community_post_id");


        } catch (Exception e) {

        }


        FirebaseFirestore.setLoggingEnabled(true);
        db_firebase = FirebaseFirestore.getInstance();
        obj_interface = this;
        iv_close_gif = findViewById(R.id.iv_close_gif);
        fm_hashtag_dialog = findViewById(R.id.fm_hashtag_dialog);
        fm_hashtag_dialog.setVisibility(View.GONE);
        iv_animation = findViewById(R.id.iv_animation);
        iv_arrow = findViewById(R.id.iv_arrow);
        iv_arrow.setVisibility(View.GONE);
//        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);

        btn_search_header = (ImageView) findViewById(R.id.btn_search_header);
        iv_community_create_post = (ImageView) findViewById(R.id.iv_community_create_post);
//        iv_search = (ImageView) findViewById(R.id.iv_search_icon);
        ll_search_container = (LinearLayout) findViewById(R.id.ll_search_container);
        edt_hash_search = (EditText) findViewById(R.id.edt_hash_search);

        Glide.with(Community.this)
                .load(R.drawable.gif_animated)
                .into(iv_animation);

//        fm_main = findViewById(R.id.fm_main);
//        iv_header_menu = findViewById(R.id.iv_header_menu);
//        iv_post_image = (ImageView) findViewById(R.id.iv_post_image);
//        iv_fab = (FloatingActionButton) findViewById(R.id.fab_add_post);
//        fab_left = (FloatingActionButton) findViewById(R.id.fab_left);


        MyConstantsKt.checkForIntroVideo(this, StringConstants.intro_community);


        obj_cmunity = this;
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_community);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        fm_image = (RelativeLayout) findViewById(R.id.fm_image);
        iv_community_chat = (ImageView) findViewById(R.id.iv_community_chat);
        iv_community_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_screen_chat_icon, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_screen_chat_icon);

                if (constants.getBoolean(constants.IsGuestUser, Community.this)) {
                    showLoginDialog();
                } else {
                    startActivity(new Intent(Community.this, ChatUserList.class));
                }

            }
        });

        iv_community_create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_screen_plus_icon, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_screen_plus_icon);
                if (!KGlobal.isInternetAvailable(Community.this)) {
                    Toast.makeText(Community.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                }

//                if (!PermissionUtils.checkReadStoragePermission(Community.this)) {
//                    // We don't have permission so prompt the user
//                    PermissionUtils.requestStoragePermission(Community.this, 1);
//                    break;
//                }
                System.out.println("isLoggedIn" + isLoggedIn);
                System.out.println("account" + account);
                System.out.println("isLoginInPaintology" + isLoginInPaintology);

                /*Intent intent = new Intent(Community.this, Gallery.class);
                intent.putExtra("title", "New Post");
                intent.putExtra("mode", 1);
                intent.putExtra("maxSelection", 500);
                intent.putExtra("isFromNewPost", true);
                startActivity(intent);*/


                if (constants.getBoolean(constants.IsGuestUser, Community.this)) {
                    showLoginDialog();
                } else {
                    Intent intent = new Intent(Community.this, Gallery.class);
                    intent.putExtra("title", "New Post");
                    intent.putExtra("mode", 1);
                    intent.putExtra("maxSelection", 500);
                    intent.putExtra("isFromNewPost", true);
                    startActivity(intent);
                }
            }
        });
        iv_enlarge_image = (ImageView) findViewById(R.id.iv_enlarge_image);

//        ll_1 = findViewById(R.id.ll_1);
//        ll_2 = findViewById(R.id.ll_2);
//        ll_3 = findViewById(R.id.ll_3);
//        ll_4 = findViewById(R.id.ll_4);
//
//        ll_1.setVisibility(View.GONE);
//        ll_2.setVisibility(View.GONE);
//        ll_3.setVisibility(View.GONE);
//        ll_4.setVisibility(View.GONE);
//
//        ll_1.setOnClickListener(this);
//        ll_2.setOnClickListener(this);
//        ll_3.setOnClickListener(this);
//        ll_4.setOnClickListener(this);

        if (getIntent().hasExtra("isFromProfile")) {
            isFromProfile = true;
        }

        currentType = (constants.getInt(constants.formatType, Community.this) == 0 ? 1 : constants.getInt(constants.formatType, Community.this));
        _indexes.add(currentType);
//        if (currentType == 1)
//            enableAllView(ll_1);
//        else if (currentType == 2)
//            enableAllView(ll_2);
//        else if (currentType == 3)
//            enableAllView(ll_3);
//        else if (currentType == 4)
//            enableAllView(ll_4);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
//        iv_profile = findViewById(R.id.iv_profile_icon);

//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        tabLayout = (TabLayout) findViewById(R.id.tabs);

//        tabLayout.addTab(tabLayout.newTab().setText("My Art"));
//        tabLayout.addTab(tabLayout.newTab().setText("My Feed"));
//        tabLayout.addTab(tabLayout.newTab().setText("Popular"));

//        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
//        layoutParams.weight = 0.2f; // e.g. 0.5f
//        layout.setLayoutParams(layoutParams);
//
//        LinearLayout layout_1 = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
//        LinearLayout.LayoutParams layoutParams_1 = (LinearLayout.LayoutParams) layout_1.getLayoutParams();
//        layoutParams_1.weight = 0.2f; // e.g. 0.5f
//        layout_1.setLayoutParams(layoutParams_1);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        viewPager.setOffscreenPageLimit(3);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.spinner_layout, null, false);
//        LinearLayout linearLayoutOne = (LinearLayout) headerView.findViewById(R.id.ll);
//        tabLayout.getTabAt(0).setCustomView(linearLayoutOne);

//        tv_popular = (TextView) findViewById(R.id.tv_popular);
//        tv_art_fav = (TextView) findViewById(R.id.tv_art_fav);
//        tv_art_medium = (TextView) findViewById(R.id.tv_art_medium);
//        tv_art_ability = (TextView) findViewById(R.id.tv_art_ability);

//        spn_popular = (Spinner) linearLayoutOne.findViewById(R.id.spn_popular);
//        spn_art_fav = (Spinner) linearLayoutOne.findViewById(R.id.spn_art_fav);
//        spn_art_medium = (Spinner) linearLayoutOne.findViewById(R.id.spn_art_medium);
//        spn_art_ablity = (Spinner) linearLayoutOne.findViewById(R.id.spn_art_ability);
//
//        spn_art_medium.setSelection(1);
//        spn_popular.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                Toast.makeText(Community.this, i + "", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(0);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        facebook_login_btn = (LoginButton) findViewById(R.id.login_button);

        facebook_login_btn.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        facebook_login_btn.registerCallback(callbackManager, callback);

        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        isLoginInPaintology = constants.getString(constants.LoginInPaintology, this);

//        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//            if (constants.getString(constants.ProfilePicsUrl, Community.this) != null && !constants.getString(constants.ProfilePicsUrl, Community.this).isEmpty()) {
//                Log.e("TAGGG", "Profile Image Set from OnCreate");
////                Glide.with(Community.this)
////                        .load(constants.getString(constants.ProfilePicsUrl, Community.this))
////                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
////                        .into(iv_profile);
//
//                Glide.with(Community.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, Community.this))
//                        .apply(RequestOptions.circleCropTransform())
////                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(new CustomTarget<Drawable>() {
//                            @Override
//                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                actionProfile.setIcon(resource);
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                            }
//                        });
//            }
//        }

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


        googleApiClient = new GoogleApiClient.Builder(Community.this)
                .enableAutoManage(Community.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Community.this, gso);

//        iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!KGlobal.isInternetAvailable(Community.this)) {
//                    Toast.makeText(Community.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                isLoggedIn = accessToken != null && !accessToken.isExpired();
//                account = GoogleSignIn.getLastSignedInAccount(Community.this);
//                isLoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);
//
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                    startActivity(new Intent(Community.this, MyProfile.class));
////                    finish();
//                } else {
//                    FirebaseUtils.logEvents(Community.this, constants.open_social_login_community_profile_dialog);
//                    showLoginDialog(DialogType.Profile);
//                }
//            }
//        });

//        iv_post_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadNewImage(false);
//            }
//        });
        FirebaseUtils.logEvents(Community.this, constants.view_communty_page);

//        iv_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadNewImage(true);
//            }
//        });
//
//        fab_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (fm_hashtag_dialog.getVisibility() == View.VISIBLE) {
//                    iv_arrow.setVisibility(View.GONE);
//                    fm_hashtag_dialog.setVisibility(View.GONE);
//                } else {
//                    FirebaseUtils.logEvents(Community.this, constants.click_community_search_Mag);
//                    iv_arrow.setVisibility(View.VISIBLE);
//                    fm_hashtag_dialog.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        iv_close_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fm_hashtag_dialog.getVisibility() == View.VISIBLE) {
                    iv_arrow.setVisibility(View.GONE);
                    fm_hashtag_dialog.setVisibility(View.GONE);
                    FirebaseUtils.logEvents(Community.this, constants.click_community_search_Mag_close);
                } else {
                    iv_arrow.setVisibility(View.VISIBLE);
                    fm_hashtag_dialog.setVisibility(View.VISIBLE);
                }
            }
        });

//        iv_header_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                final CharSequence[] items = {"View Mode - 4 X 2", "View Mode - 3 X 2", "View Mode - 2 X 2", "View Mode - normal", "Add new Post", "Share feedback", "Go to Google Playstore"};
////                final CharSequence[] items = {"View Mode - 4 X 2", "View Mode - 3 X 2", "View Mode - 2 X 2", "View Mode - normal", "Add new Post", "Share Paintology with Others", "Share feedback", "Go to Google Playstore"};
//                final CharSequence[] items = {"View Mode", "Add new Post", "Share Paintology with Others", "Share feedback", "Go to Google Playstore"};
//
//                FirebaseUtils.logEvents(Community.this, constants.community_header_menu_click);
//
//                // Initializing the popup menu and giving the reference as current context
//                PopupMenu popupMenu = new PopupMenu(Community.this, view);
//
//                // Inflating popup menu from popup_menu.xml file
//                popupMenu.getMenuInflater().inflate(R.menu.popup_community_menu, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        int id = menuItem.getItemId();
//                        switch (id) {
//                            case R.id.view_mode:
//                                View view = getLayoutInflater().inflate(R.layout.view_mode_dialog, null, false);
//
//                                LinearLayout viewModeNormal = view.findViewById(R.id.view_mode_normal);
//                                LinearLayout viewMode2n = view.findViewById(R.id.view_mode_2n);
//                                LinearLayout viewMode3n = view.findViewById(R.id.view_mode_3n);
//                                LinearLayout viewMode4n = view.findViewById(R.id.view_mode_4n);
//
//                                viewModeNormal.setOnClickListener(v -> {
////                                    ll_1.performClick();
//
//                                    if (MainCollectionFragment.objHomeInterface.changeListFormat(1)) {
//                                        if (currentType != 1 && currentType != 5) {
//                                            _indexes.add(1);
//                                            currentType = 1;
////                                            disableAllView();
////                                            enableAllView(ll_1);
//                                        }
//                                        FirebaseUtils.logEvents(Community.this, constants.community_1x1_selection);
//                                    }
//
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode2n.setOnClickListener(v -> {
////                                    ll_2.performClick();
//                                    if (MainCollectionFragment.objHomeInterface.changeListFormat(2)) {
//                                        if (currentType != 2) {
//                                            _indexes.add(2);
//                                            currentType = 2;
////                                            disableAllView();
////                                            enableAllView(ll_2);
//                                        }
//                                        FirebaseUtils.logEvents(Community.this, constants.community_2x2_selection);
//                                    }
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode3n.setOnClickListener(v -> {
////                                    ll_3.performClick();
//
//                                    if (MainCollectionFragment.objHomeInterface.changeListFormat(3)) {
//
//                                        if (currentType != 3) {
//                                            _indexes.add(3);
//                                            currentType = 3;
////                                            disableAllView();
////                                            enableAllView(ll_3);
//                                        }
//                                        FirebaseUtils.logEvents(Community.this, constants.community_3x2_selection);
//                                    }
//
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode4n.setOnClickListener(v -> {
////                                    ll_4.performClick();
//
//                                    if (MainCollectionFragment.objHomeInterface.changeListFormat(4)) {
//                                        if (currentType != 4) {
//                                            _indexes.add(4);
//                                            currentType = 4;
////                                            disableAllView();
////                                            enableAllView(ll_4);
//                                        }
//                                        FirebaseUtils.logEvents(Community.this, constants.community_4x2_selection);
//                                    }
//
//                                    position = _indexes.size();
//
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(Community.this);
//                                builder.setView(view);
//                                alertDialogViewModel = builder.create();
//
//                                alertDialogViewModel.show();
//                                break;
//                            case R.id.add_new_post:
//                                FirebaseUtils.logEvents(Community.this, constants.click_community_post_menu);
//                                if (!KGlobal.isInternetAvailable(Community.this)) {
//                                    Toast.makeText(Community.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                                    break;
//                                }
//
//                                if (!PermissionUtils.checkReadStoragePermission(Community.this)) {
//                                    // We don't have permission so prompt the user
//                                    PermissionUtils.requestStoragePermission(Community.this, 1);
//                                    break;
//                                }
//
//                                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                                    Intent intent = new Intent(Community.this, Gallery.class);
//                                    intent.putExtra("title", "New Post");
//                                    intent.putExtra("mode", 1);
//                                    intent.putExtra("maxSelection", 500);
//                                    intent.putExtra("isFromNewPost", true);
//                                    startActivity(intent);
//                                } else {
//                                    FirebaseUtils.logEvents(Community.this, constants.open_social_login_community_new_post_dialog);
//                                    showLoginDialog(DialogType.Post);
//                                }
//                                break;
//                            case R.id.share:
//                                try {
//                                    FirebaseUtils.logEvents(Community.this, constants.click_community_menu_share_paintology);
//                                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                                    share.setType("text/plain");
//                                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//
//                                    // Add data to the intent, the receiving app will decide
//                                    // what to do with it.
//                                    share.putExtra(Intent.EXTRA_SUBJECT, "Paintology - great little app you should check out");
//                                    share.putExtra(Intent.EXTRA_TEXT, "I found this free app called Paintology, I think you will love it, check it out!\n\nhttps://play.google.com/store/apps/details?id=com.paintology.lite");
//
//                                    startActivity(Intent.createChooser(share, "Share link!"));
//                                } catch (Exception e) {
//                                    Log.e("TAG", "Exception at share " + e.getMessage());
//                                }
//                                break;
//                            case R.id.feedback:
//                                FirebaseUtils.logEvents(Community.this, constants.click_community_menu_feedback);
//                                showFeedbackDialog();
//                                break;
//                            case R.id.playstore:
//                                FirebaseUtils.logEvents(Community.this, constants.click_community_menu_googleplay_click);
//                                try {
//                                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
//                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    startActivity(browserIntent);
//                                } catch (Exception e) {
//                                    Log.e("Community", e.getMessage());
//                                }
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                // Showing the popup menu
//                popupMenu.show();
//
//            }
//        });

//        iv_direct = (ImageView) findViewById(R.id.iv_direct);
//        iv_direct.setVisibility(View.VISIBLE);
//
//        iv_direct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseUtils.logEvents(Community.this, constants.chat_community_header_click);
//
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                isLoggedIn = accessToken != null && !accessToken.isExpired();
//                account = GoogleSignIn.getLastSignedInAccount(Community.this);
//                isLoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);
//
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true")))
//                    startActivity(new Intent(Community.this, ChatUserList.class));
//                else {
//                    showLoginDialog(DialogType.CHAT);
//                }
//            }
//        });
//
//        iv_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (ll_search_container.getVisibility() == View.GONE) {
//                    ll_search_container.setVisibility(View.VISIBLE);
//                    iv_search.setImageResource(R.drawable.ic_search_disabled);
//                    Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//                    ll_search_container.startAnimation(animSlideDown);
//                } else {
//                    Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//                    ll_search_container.startAnimation(animSlideDown);
//                    iv_search.setImageResource(R.drawable.ic_search_enabled);
////                    ll_search_container.setVisibility(View.GONE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            edt_hash_search.setText("");
//                            ll_search_container.setVisibility(View.GONE);
//                        }
//                    }, 500);
//                }
//            }
//        });

        btn_search_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtils.logEvents(Community.this, constants.search_header_textentry_query);
                if (!edt_hash_search.getText().toString().isEmpty()) {
                    searchFromEditText();
                }
            }
        });

        edt_hash_search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    if (!edt_hash_search.getText().toString().isEmpty()) {

                        FirebaseUtils.logEvents(Community.this, constants.search_communty_page + edt_hash_search.getText().toString().replace(" ", "_") + ">");


                        searchFromEditText();
                    }
                    return true;
                }
                return false;
            }
        });

//        realTimeDBUtils = MyApplication.get_realTimeDbUtils(this);
//
//        if (AppUtils.isLoggedIn()) {
//            getData();
//        }

    }


    void searchFromEditText() {

        try {
            AppUtils.hideKeyboard(this);

            StringBuilder stringBuilder = new StringBuilder();
            try {
                StringTokenizer tokenizer = new StringTokenizer(edt_hash_search.getText().toString(), " ");
                if (tokenizer.countTokens() != 0) {
                    do {
                        String nextElem = tokenizer.nextToken().toString();
                        if (nextElem.startsWith("#") && nextElem.length() > 1)
                            stringBuilder.append(nextElem + "|");
                        else
                            stringBuilder.append("#" + nextElem + "|");
                    } while (tokenizer.hasMoreTokens());
                }
            } catch (Exception e) {
                Log.e("TAGG", "Exception on find tag " + e.getMessage(), e);
            }
            String hasTag = stringBuilder.toString();
            if (hasTag != null && hasTag.length() > 0 && hasTag.charAt(hasTag.length() - 1) == '|') {
                hasTag = hasTag.substring(0, hasTag.length() - 1);
            }

            MainCollectionFragment._post_operation.seachByHashTag(hasTag);
        } catch (Exception e) {

        }
    }


    void uploadNewImage(boolean isFromFab) {
        if (!KGlobal.isInternetAvailable(Community.this)) {
            Toast.makeText(Community.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
            return;
        }

//        if (!PermissionUtils.checkReadStoragePermission(Community.this)) {
//            // We don't have permission so prompt the user
//            PermissionUtils.requestStoragePermission(Community.this, 1);
//            return;
//        }


        if (constants.getBoolean(constants.IsGuestUser, Community.this)) {
            showLoginDialog();
        } else {
            if (isFromFab)
                FirebaseUtils.logEvents(Community.this, constants.click_community_post_Bigplus);
            else
                FirebaseUtils.logEvents(Community.this, constants.click_community_post);

            Intent intent = new Intent(Community.this, Gallery.class);
            intent.putExtra("title", "New Post");
            intent.putExtra("mode", 1);
            intent.putExtra("maxSelection", 500);
            intent.putExtra("isFromNewPost", true);
            startActivity(intent);
        }
    }


    /*This is the method where user can get confirmation about app permission, this method give the result of permission dialog and says that accepted or not*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            int permission = 0;
            System.out.println("permissions :" + permissions[0]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                permission = ActivityCompat.checkSelfPermission(Community.this, Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                // android 11 and above
                permission = ActivityCompat.checkSelfPermission(Community.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                Toast.makeText(Community.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                FirebaseUtils.logEvents(Community.this, constants.deny_storage_permission);
                return;
            } else {
                if (MainCollectionFragment.isTrack) {
                    MainCollectionFragment._post_operation.downloadImageOpenInTraceCanvas(0);
                } else {
                    MainCollectionFragment._post_operation.downloadImageOpenInOverlayCanvas(0);
                }
                FirebaseUtils.logEvents(Community.this, constants.allow_storage_permission);
            }


        } catch (Exception e) {

        }
    }

    @Override
    public void ReflectColor(int code) {
//        tv_popular.setTextColor(code);
//        tv_art_fav.setTextColor(code);
//        tv_art_medium.setTextColor(code);
//        tv_art_ability.setTextColor(code);
    }

    @Override
    public void ShowProfileIcon() {
//        iv_profile.setVisibility(View.VISIBLE);
        Log.e("TAGGG", "Profile Image Set from 331");
//        Glide.with(Community.this)
//                .load(constants.getString(constants.ProfilePicsUrl, Community.this))
//                .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                .into(iv_profile);

        Glide.with(Community.this)
                .load(constants.getString(constants.ProfilePicsUrl, Community.this))
                .apply(RequestOptions.circleCropTransform())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (actionProfile != null) {
                            actionProfile.setIcon(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();

        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(Community.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, Community.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called Community 814");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginUpdateEvent(UserLoginUpdateEvent event) {
        Log.e("Community", "onUserLoginUpdateEvent");
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            if (isAuthenticating)
                return;
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(this);
            isLoginInPaintology = constants.getString(constants.LoginInPaintology, this);
            Log.e("TAGG", "Profile Url From Prefe " + constants.getString(constants.ProfilePicsUrl, Community.this));

            int placeHolder;
            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                if (constants.getString(constants.ProfilePicsUrl, Community.this).isEmpty()) {
                    if (constants.getString(constants.UserGender, Community.this).equalsIgnoreCase(constants.MALE))
                        placeHolder = R.drawable.profile_icon;
                    else if (constants.getString(constants.UserGender, Community.this).equalsIgnoreCase(constants.FEMALE))
                        placeHolder = R.drawable.profile_icon;
                    else
                        placeHolder = R.drawable.profile_icon;


//                    Glide.with(Community.this)
//                            .load(constants.getString(constants.ProfilePicsUrl, Community.this))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                            .into(iv_profile);
                    if (actionProfile != null) {
                        actionProfile.setIcon(placeHolder);
                    }

                    Glide.with(Community.this)
                            .load(constants.getString(constants.ProfilePicsUrl, Community.this))
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
                            .error(placeHolder)
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (actionProfile != null) {
                                        actionProfile.setIcon(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                } else {

//                    if (constants.getString(constants.UserGender, Community.this).equalsIgnoreCase(constants.MALE)) {
//                        placeHolder = R.drawable.profile_icon_male;
//                    } else if (constants.getString(constants.UserGender, Community.this).equalsIgnoreCase(constants.FEMALE)) {
//                        placeHolder = R.drawable.profile_icon_female;
//                    } else {
//                        placeHolder = R.drawable.profile_icon_male;
//                    }

                    placeHolder = R.drawable.profile_icon;

//                    Glide.with(Community.this)
//                            .load(constants.getString(constants.ProfilePicsUrl, Community.this))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                            .into(iv_profile);
                    if (actionProfile != null) {
                        actionProfile.setIcon(placeHolder);
                    }
                    Glide.with(Community.this)
                            .load(constants.getString(constants.ProfilePicsUrl, Community.this))
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
                            .error(placeHolder)
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (actionProfile != null) {
                                        actionProfile.setIcon(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
            } else {
//                iv_profile.setImageResource(R.drawable.profile_icon);
                placeHolder = R.drawable.profile_icon;
                if (actionProfile != null) {
                    actionProfile.setIcon(placeHolder);
                }
                Glide.with(Community.this)
                        .load(constants.getString(constants.ProfilePicsUrl, Community.this))
                        .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (actionProfile != null) {
                                    actionProfile.setIcon(resource);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
            try {
                accessToken = AccessToken.getCurrentAccessToken();
                isLoggedIn = accessToken != null && !accessToken.isExpired();
                account = GoogleSignIn.getLastSignedInAccount(Community.this);
                String isLoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);

                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                    String _user_id = constants.getString(constants.UserId, Community.this);
                    if (_user_id != null && !_user_id.isEmpty()) {
                        Log.e("TAG", "setOnline called Community 849");
                        MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
            }

           // fetchProfileData();

        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    int currentType = 0;

    @Override
    public void onClick(View view) {

//        switch (view.getId()) {
//            case R.id.ll_1: {
////                ll_1.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
//                if (MainCollectionFragment.objHomeInterface.changeListFormat(1)) {
//                    if (currentType != 1 && currentType != 5) {
//                        _indexes.add(1);
//                        currentType = 1;
//                        disableAllView();
//                        enableAllView(ll_1);
//                    }
//                    FirebaseUtils.logEvents(Community.this, constants.community_1x1_selection);
//                }
//            }
//            break;
//            case R.id.ll_2: {
////                ll_2.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
//                if (MainCollectionFragment.objHomeInterface.changeListFormat(2)) {
//                    if (currentType != 2) {
//                        _indexes.add(2);
//                        currentType = 2;
//                        disableAllView();
//                        enableAllView(ll_2);
//                    }
//                    FirebaseUtils.logEvents(Community.this, constants.community_2x2_selection);
//                }
//            }
//            break;
//            case R.id.ll_3: {
//
////                ll_3.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
//                if (MainCollectionFragment.objHomeInterface.changeListFormat(3)) {
//
//                    if (currentType != 3) {
//                        _indexes.add(3);
//                        currentType = 3;
//                        disableAllView();
//                        enableAllView(ll_3);
//                    }
//                    FirebaseUtils.logEvents(Community.this, constants.community_3x2_selection);
//                }
//            }
//            break;
//            case R.id.ll_4: {
////                ll_4.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
//                if (MainCollectionFragment.objHomeInterface.changeListFormat(4)) {
//                    if (currentType != 4) {
//                        _indexes.add(4);
//                        currentType = 4;
//                        disableAllView();
//                        enableAllView(ll_4);
//                    }
//                    FirebaseUtils.logEvents(Community.this, constants.community_4x2_selection);
//                }
//            }
//            break;
//        }
//        position = _indexes.size();
    }


//    void disableAllView() {
//        disableAllView(ll_1);
//        disableAllView(ll_2);
//        disableAllView(ll_3);
//        disableAllView(ll_4);
//    }
//
//    void disableAllView(LinearLayout view) {
//        for (int i = 0; i < view.getChildCount(); i++) {
//            if (view.getChildAt(i) instanceof LinearLayout) {
//                LinearLayout ll = (LinearLayout) view.getChildAt(i);
//                {
//                    for (int j = 0; j < ll.getChildCount(); j++) {
//                        View view1 = (View) ll.getChildAt(j);
//                        view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_square_radious));
//                    }
//                }
//            } else {
//                View view1 = (View) view.getChildAt(i);
//                view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_square_radious));
//            }
//        }
//    }
//
//    void enableAllView(LinearLayout view) {
//        for (int i = 0; i < view.getChildCount(); i++) {
//            if (view.getChildAt(i) instanceof LinearLayout) {
//                LinearLayout ll = (LinearLayout) view.getChildAt(i);
//                {
//                    for (int j = 0; j < ll.getChildCount(); j++) {
//                        View view1 = (View) ll.getChildAt(j);
//                        view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_selected));
//                    }
//                }
//            } else {
//                View view1 = (View) view.getChildAt(i);
//                view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_selected));
//            }
//        }
//    }

    @Override
    public void switchtoCanvas(String youtubeID) {
        StringConstants.IsFromDetailPage = false;
        Intent intent = new Intent(Community.this, PaintActivity.class);
        intent.putExtra("youtube_video_id", youtubeID);
        intent.setAction("YOUTUBE_TUTORIAL");
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Toast.makeText(this, "Click Outside", Toast.LENGTH_SHORT).show();
        iv_arrow.setVisibility(View.GONE);
        fm_hashtag_dialog.setVisibility(View.GONE);
        return true;
    }



    /*@Override
    public void onDoubleTap() {
        Toast.makeText(this, "You have Double Tapped.", Toast.LENGTH_SHORT)
                .show();
    }*/


    class MyAdapter extends FragmentPagerAdapter {

        int totalTabs;

        public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            this.totalTabs = totalTabs;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            Log.e("TAGGG", "Position " + position);
            switch (position) {
                case 0:
                   /* MyArtFragment myArtFragment = new MyArtFragment().newInstance();
                    return myArtFragment;*/
                    MainCollectionFragment frg_filtered = new MainCollectionFragment().newInstance();

                    return frg_filtered;
               /* case 1:
                    MyFeedFragment feedFragment = new MyFeedFragment();
                    return feedFragment;*/
                default:
                    MainCollectionFragment frg_main = new MainCollectionFragment().newInstance();
                    return frg_main;
            }
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }

    /*This method will prompt social media login dialog when user click on upload zip file.*/
    private void showLoginDialog() {
        /*Intent intent = new Intent(Community.this, LoginActivity.class);
        startActivity(intent);*/
        FireUtils.openLoginScreen(this, true);
    }
//    public void showLoginDialog(DialogType dialogType) {
//        try {
//            final Dialog dialog = new Dialog(Community.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setCancelable(true);
//            dialog.setContentView(R.layout.social_media_login_layout);
//
//            Button btn_fb = (Button) dialog.findViewById(R.id.fb);
//
//            TextView tv_community_link = (TextView) dialog.findViewById(R.id.tv_community_link);
//            tv_community_link.setVisibility(View.GONE);
//
//            TextView txt_dialog_title = (TextView) dialog.findViewById(R.id.txt_dialog_title);
//            if (dialogType.equals(DialogType.Post)) {
//                txt_dialog_title.setText(getString(R.string.str_post));
//            } else if (dialogType.equals(DialogType.Profile)) {
//                txt_dialog_title.setText(getString(R.string.str_profile));
//            }
//
//            btn_fb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Perfome Action
//                    FirebaseUtils.logEvents(Community.this, constants.FACEBOOK_LOGIN);
//                    facebook_login_btn.performClick();
//                    dialog.dismiss();
//                }
//            });
//            Button btn_google = (Button) dialog.findViewById(R.id.google);
//            btn_google.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Perfome Action
//                    signIn();
//                    dialog.dismiss();
//
//                }
//            });
//            Button btn_paintology = (Button) dialog.findViewById(R.id.btn_paintology);
//            btn_paintology.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//
//                        if (Community.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
//                            return;
//                        }
//                        dialog.dismiss();
//                        FirebaseUtils.logEvents(Community.this, constants.Social_Paintology_Login);
//                        showDialog();
//                    } catch (Exception e) {
//
//                    }
//                }
//            });
//            dialog.show();
//        } catch (Exception e) {
//            Log.e("TAGG", "Exception " + e.getMessage());
//        }
//    }

    /*User can do their google sign in using this method*/
    private void signIn() {
        FirebaseUtils.logEvents(Community.this, constants.GOOGLE_LOGIN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            try {
                isAuthenticating = true;
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
//                constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), Community.this);
//                constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), Community.this);
//                constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), Community.this);
//
                LoginRequestModel model = new LoginRequestModel(
                        (account.getId() != null ? account.getId() : ""),
                        (account.getDisplayName() != null ? account.getDisplayName() : ""),
                        (account.getEmail() != null ? account.getEmail() : ""),
                        ""
                );

                FirebaseUtils.logEvents(Community.this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE, task.getResult(ApiException.class));

//                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
                FirebaseUtils.logEvents(Community.this, constants.GoogleLoginFailed);
            } catch (Exception e) {

            }
        } else {
            isAuthenticating = true;
            Log.e("TAGGG", "SignIn Result Called");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

//    public void showDialog() {
//
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//        final Dialog dialog = new Dialog(Community.this);
//        dialog.setContentView(R.layout.login_to_paintology_layout);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        EditText edt_email = dialog.findViewById(R.id.edt_email);
//        EditText edt_uname = dialog.findViewById(R.id.edt_uname);
//        EditText edt_pass = dialog.findViewById(R.id.edt_pass);
//        Button btn_proceed = dialog.findViewById(R.id.btn_proceed);
//        ImageView iv_close_dialog = dialog.findViewById(R.id.iv_close_dialog);
//        AppCompatCheckBox cb_viewpass = (AppCompatCheckBox) dialog.findViewById(R.id.cb_viewpass);
//
//        cb_viewpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked) {
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
//                if (edt_uname.getText().toString().isEmpty()) {
//
//                } else if (edt_email.getText().toString().isEmpty()) {
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
//                } else {
//                    String uname = "";
//                    try {
//                        String[] separated = edt_email.getText().toString().trim().split("@");
//                        if (separated != null && separated.length > 0)
//                            uname = separated[0];
//                    } catch (Exception e) {
//
//                    }
//                    LoginRequestModel model = new LoginRequestModel(uname, edt_pass.getText().toString().trim(), edt_email.getText().toString().trim());
//                    Log.e("TAGG", "Login Data " + model.user_email + " " + model.user_id + " " + model.user_name);
//
////                    FirebaseUtils.logEvents(ImportImagesActivity.this,constants.PaintologyLoginSuccess);
//                    addUser(model, LOGIN_FROM_PAINTOLOGY);
//                    dialog.dismiss();
//
//                }
//            }
//        });
//        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        edt_uname.setFilters(new InputFilter[]{filter});
//        dialog.show();
//    }
//
//    public boolean isValidEmail(CharSequence target) {
//        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
//    }

//    InputFilter filter = new InputFilter() {
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end,
//                                   Spanned dest, int dstart, int dend) {
//            for (int i = start; i < end; i++) {
//                if (!Character.isLetterOrDigit(source.charAt(i))) {
//                    Log.e("TAGG", "Source In loop " + source);
//                    if (source.toString().contains(" "))
//                        return "_";
//                    else
//                        return "";
//                }
//            }
//            return null;
//        }
//    };

    boolean isAuthenticating = false;

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType, GoogleSignInAccount... accounts) {

        Log.e("TAGGG", "Add User Data userID " + model.user_id + " email " + model.user_email + " username " + model.user_name);
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_name != null) ? model.user_name : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");
        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, Community.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, Community.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, Community.this));

        try {
            String _ip = constants.getString(constants.IpAddress, Community.this);
            String _country = constants.getString(constants.UserCountry, Community.this);
            String _city = constants.getString(constants.UserCity, Community.this);

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
        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
        Call<LoginResponseModel> call = apiInterface.addUserData(ApiClient.SECRET_KEY, _map);
        showProgress();

        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    isAuthenticating = false;
                    Log.e("TAGGG", "onResponse called " + response.toString());
                    if (response != null && response.isSuccessful()) {

                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {
                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", Community.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", Community.this);

                            MainCollectionFragment.objHomeInterface.setUserID(response.body().getObjData().getUser_id() + "");

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", Community.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), Community.this);

                            constants.putString(constants.Username, model.user_name, Community.this);
                            constants.putString(constants.Password, model.user_id, Community.this);
                            constants.putString(constants.Email, model.user_email, Community.this);
                            constants.putString(constants.LoginInPaintology, "true", Community.this);

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        FirebaseUtils.logEvents(Community.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        FirebaseUtils.logEvents(Community.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    isLoggedIn = true;
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        FirebaseUtils.logEvents(Community.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (accounts != null && accounts.length > 0)
                                        account = accounts[0];
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        FirebaseUtils.logEvents(Community.this, constants.GoogleRegistration);
                                    }
                                }
                            }

                            try {
                                if (_operationLogin != null && _operationLogin.getOperationType() != null) {
                                    if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeLike)) {
                                        MainCollectionFragment._post_operation.likeOperation(_operationLogin.getPosition(), true, true);
                                    } else if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeComment)) {
                                        MainCollectionFragment._post_operation.addComment(_operationLogin.getPosition(), _operationLogin.get_obj_comment_data().get_user_comment(), _operationLogin.get_obj_comment_data().get_user_list());
                                    } else if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeView)) {
                                        MainCollectionFragment._post_operation.viewOperation(_operationLogin.getPosition(), Integer.parseInt(_operationLogin.get_obj_view_data().getTotalViews()), true);
                                    }
                                }

                                String _user_id = constants.getString(constants.UserId, Community.this);
                                try {
                                    if (KGlobal.isInternetAvailable(Community.this) && _user_id != null && !_user_id.isEmpty()) {
                                        startService(new Intent(Community.this, SendDeviceToken.class));
                                    }
                                } catch (Exception e) {
                                }
                                fetchProfileData();
                                MyApplication.get_realTimeDbUtils(Community.this).autoLoginRegister(response.body().getObjData().getStatus());
                            } catch (Exception e) {
                                Log.e("TAGG", "Exception at add user " + e.getMessage());
                            }
                        } else {
                            Log.e("TAGGG", "onResponse called goto else loginType " + loginType);
                            if (loginType == LOGIN_FROM_FB) {
                                LoginManager.getInstance().logOut();
                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                isLoggedIn = accessToken != null && !accessToken.isExpired();
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                account = null;
                                Auth.GoogleSignInApi.signOut(googleApiClient);
                            } else {
                                isLoginInPaintology = "false";
                                FirebaseUtils.logEvents(Community.this, constants.PaintologyLoginFailed);
                            }
                            FirebaseUtils.logEvents(Community.this, constants.event_failed_to_adduser);
                            Toast.makeText(Community.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        try {
                            Log.e("TAGGG", "onResponse called goto else loginType " + loginType);
                            if (loginType == LOGIN_FROM_FB) {
                                LoginManager.getInstance().logOut();
                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                isLoggedIn = accessToken != null && !accessToken.isExpired();
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                account = null;
                                Auth.GoogleSignInApi.signOut(googleApiClient);
                            } else {
                                isLoginInPaintology = "false";
                                FirebaseUtils.logEvents(Community.this, constants.PaintologyLoginFailed);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FirebaseUtils.logEvents(Community.this, constants.event_failed_to_adduser);
                        Toast.makeText(Community.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    }
//                    new SaveTask(model).execute();
                    hideProgress();
                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    Log.e("TAGG", "add user in failure " + t.getMessage(), t);
                    isAuthenticating = false;
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
                            isLoginInPaintology = "false";
                            FirebaseUtils.logEvents(Community.this, constants.PaintologyLoginFailed);
                        }
                        FirebaseUtils.logEvents(Community.this, constants.event_failed_to_adduser);
                        Toast.makeText(Community.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "add user in Exception " + e.getMessage(), e);
            hideProgress();
        }
    }

    void showProgress() {
        try {
            progressDialog = new ProgressDialog(Community.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        try {
            if (Community.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

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

                            FirebaseUtils.logEvents(Community.this, constants.FacebookLoginSuccess);
                            // Application code
                            try {
//                                handleFacebookAccessToken(loginResult.getAccessToken());
//                                Log.d("tttttt", object.toString());
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
//                                isLoggedIn = true;
//                                constants.putString(constants.Username, fnm, Community.this);
//                                constants.putString(constants.Password, fid, Community.this);
//                                constants.putString(constants.Email, mail, Community.this);
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
            Log.e("TAGGG", "Facebook Event OnError Called " + error.getMessage(), error);
            FirebaseUtils.logEvents(Community.this, constants.FacebookLoginFailed);
        }
    };

    public void fetchProfileData() {
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Observable<GetUserProfileResponse> profileResponse = apiInterface.getUserProfileData(ApiClient.SECRET_KEY, constants.getString(constants.UserId, Community.this));
        profileResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GetUserProfileResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetUserProfileResponse getUserProfileResponse) {
                Log.e("TAGG", "GetUserProfileResponse OnNext Call ");
                if (isDestroyed() || isFinishing())
                    return;
                if (getUserProfileResponse == null)
                    return;

                if (getUserProfileResponse.getResponse() == null)
                    return;

                int placeHolder;
                if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("male")) {
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);
                    placeHolder = R.drawable.profile_icon_male;
                    constants.putString(constants.UserGender, constants.MALE, Community.this);
                } else if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("female")) {
//                    iv_profile.setImageResource(R.drawable.profile_icon_female);
                    placeHolder = R.drawable.profile_icon_female;
                    constants.putString(constants.UserGender, constants.FEMALE, Community.this);
                } else {
                    placeHolder = R.drawable.profile_icon_male;
                    constants.putString(constants.UserGender, constants.MALE, Community.this);
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);
                }

                if (getUserProfileResponse.getResponse().getProfilePic() != null && !getUserProfileResponse.getResponse().getProfilePic().isEmpty()) {
                    Log.e("TAGGG", "Profile Image Set from 766");
//                    Glide.with(Community.this)
//                            .load(getUserProfileResponse.getResponse().getProfilePic())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                            .into(iv_profile);

                    Glide.with(Community.this)
                            .load(getUserProfileResponse.getResponse().getProfilePic())
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (actionProfile != null) {
                                        actionProfile.setIcon(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                    constants.putString(constants.ProfilePicsUrl, getUserProfileResponse.getResponse().getProfilePic(), Community.this);

                    MyApplication.get_realTimeDbUtils(Community.this).getDbReference().child(constants.firebase_user_list).child(constants.getString(constants.UserId, Community.this)).child("profile_pic").setValue(getUserProfileResponse.getResponse().getProfilePic());
                } else {
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);

                    Glide.with(Community.this)
                            .load(getUserProfileResponse.getResponse().getProfilePic())
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (actionProfile != null) {
                                        actionProfile.setIcon(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "GetUserProfileResponse OnError e " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {
                Log.e("TAGG", "GetUserProfileResponse OnComplete Call");
            }
        });
    }


    @Override
    public boolean isLoggedIn(OperationAfterLogin _operationAfterLogin) {
        if (constants.getBoolean(constants.IsGuestUser, this)) {
            FireUtils.openLoginScreen(this, true);
            return false;
        } else {
            return true;
        }
       /* AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
        _operationLogin = _operationAfterLogin;
        if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
            return true;
        } else {
//            showLoginDialog(DialogType.NONE);
            showLoginDialog();
            return false;
        }*/
    }

    @Override
    public void showToolTip() {
    }


    @Override
    public void DisableAllView(int typeToEnable) {
        currentType = 1;
//        disableAllView();
//        enableAllView(ll_1);
        position = _indexes.size();
        constants.putInt(constants.formatType, currentType, Community.this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // your code.
//        Toast.makeText(this, "onBackPressed Called", Toast.LENGTH_SHORT).show();

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
                                startActivity(new Intent(Community.this, GalleryDashboard.class));
                                finish();
                            }
                    )
                    .show();
        } else {
            constants.putInt(constants.formatType, currentType, Community.this);
            FirebaseUtils.logEvents(Community.this, constants.community_back_selection);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        constants.putInt(constants.formatType, currentType, Community.this);
    }


    @Override
    public void enlargeImageView(String _url) {
        try {
            FirebaseUtils.logEvents(Community.this, constants.double_tap_image_community);

            try {
                ContextKt.showEnlargeImage(MyApplication.getInstance().getApplicationContext(), _url);
            } catch (Exception e) {
                e.printStackTrace();
            }

          /*  fm_image.setVisibility(View.VISIBLE);
            tv_back.setVisibility(View.VISIBLE);
            if (!Community.this.isDestroyed()) {
                Glide.with(Community.this)
                        .load(_url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(iv_enlarge_image);

                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_animation);
                iv_enlarge_image.startAnimation(animation1);
            }*/
        } catch (Exception e) {

        }
//        Toast.makeText(this, "pinch to zoom!", Toast.LENGTH_SHORT).show();
    }

    int position = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {

                if (currentType == 1) {
                    onBackPressed();
                } else if (_indexes != null && _indexes.size() > 0) {
                    int viewType = _indexes.get(_indexes.size() - 1);
                    if (currentType == viewType) {
                        _indexes.remove(_indexes.size() - 1);
                        if (_indexes != null && _indexes.size() > 0) {
                            viewType = _indexes.get(_indexes.size() - 1);
                            currentType = viewType;
                        }
                    } else {
                        currentType = viewType;
                    }
//                    disableAllView();

                    MainCollectionFragment.objHomeInterface.changeListFormat(viewType);
//                    if (viewType == 1)
//                        enableAllView(ll_1);
//                    else if (viewType == 2)
//                        enableAllView(ll_2);
//                    else if (viewType == 3)
//                        enableAllView(ll_3);
//                    else if (viewType == 4)
//                        enableAllView(ll_4);

                    if (_indexes.size() > 0) {
                        constants.putInt(constants.formatType, currentType, Community.this);
                        _indexes.remove(_indexes.size() - 1);
                    }

                } else {
                    /*if (currentType != 4 && _indexes.size() == 0) {
                        disableAllView();
                        enableAllView(ll_4);
                        currentType = 4;
                        constants.putInt(constants.formatType, currentType, Community.this);
                        MainCollectionFragment.objHomeInterface.changeListFormat(4);
                    } else {
                    }*/
                  onBackPressed();
                }
            } catch (IllegalStateException es) {

            } catch (Exception e) {
                Log.e("TAGGG", "Exception at changeView " + e.getMessage(), e);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

   /* private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAGG", "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAGGG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TAGGG", "signInWithCredential", task.getException());
                            Toast.makeText(Community.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Community.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/

  /*  private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            Firebase_User user = mFirebaseAuth.getCurrentUser();
//                            updateUI(user);
                            Toast.makeText(Community.this, "FB Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Community.this, "FB Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }*/

    @Override
    public void showHideFab(boolean needToShown) {
        try {
            iv_arrow.setVisibility(View.GONE);
            fm_hashtag_dialog.setVisibility(View.GONE);

//            if (needToShown) {
//                iv_fab.show();
//                fab_left.show();
//            } else {
//                iv_fab.hide();
//                fab_left.hide();
//            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    @Override
    public void hideSearchBar() {
        Log.e("TAG", "hideSearchBar called ");
//        if (ll_search_container.getVisibility() == View.VISIBLE) {
//
//            Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//            ll_search_container.startAnimation(animSlideDown);
//            iv_search.setImageResource(R.drawable.search);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    edt_hash_search.setText("");
//                    ll_search_container.setVisibility(View.GONE);
//                }
//            }, 500);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.community_menu, menu);
        actionProfile = menu.findItem(R.id.action_profile);
        actionChat = menu.findItem(R.id.action_chat);


//        setChatNotificationIcon();
//        setupProfileIcon();
        return super.onCreateOptionsMenu(menu);
    }

    public void setChatNotificationIcon() {
        if (AppUtils.hasUnreadChat(this)) {
            actionChat.setIcon(R.drawable.ic_chat_badge);
        } else {
            actionChat.setIcon(R.drawable.chat_icon);
        }
    }

    private void setupProfileIcon() {

        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
            if (constants.getString(constants.ProfilePicsUrl, Community.this) != null && !constants.getString(constants.ProfilePicsUrl, Community.this).isEmpty()) {
                Log.e("TAGGG", "Profile Image Set from OnCreate");
//                Glide.with(Community.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, Community.this))
//                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(iv_profile);

                Glide.with(Community.this)
                        .load(constants.getString(constants.ProfilePicsUrl, Community.this))
                        .apply(RequestOptions.circleCropTransform())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (actionProfile != null) {
                                    actionProfile.setIcon(resource);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_profile:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_screen_profile_icon, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_screen_profile_icon);
                if (AppUtils.isLoggedIn()) {
                    FireUtils.openProfileScreen(Community.this, null);
                } else {
                    Intent intent = new Intent(Community.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_chat:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_screen_chat_icon, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_screen_chat_icon);

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                isLoggedIn = accessToken != null && !accessToken.isExpired();
                account = GoogleSignIn.getLastSignedInAccount(Community.this);
                isLoginInPaintology = constants.getString(constants.LoginInPaintology, Community.this);


                if (constants.getBoolean(constants.IsGuestUser, Community.this)) {
                    showLoginDialog();
                } else {
                    startActivity(new Intent(Community.this, ChatUserList.class));
                }

                return true;
            case R.id.action_add_post:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_screen_plus_icon, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_screen_plus_icon);
                if (!KGlobal.isInternetAvailable(Community.this)) {
                    Toast.makeText(Community.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    break;
                }

//                if (!PermissionUtils.checkReadStoragePermission(Community.this)) {
//                    // We don't have permission so prompt the user
//                    PermissionUtils.requestStoragePermission(Community.this, 1);
//                    break;
//                }

                if (constants.getBoolean(constants.IsGuestUser, Community.this)) {
                    showLoginDialog();
                } else {
                    Intent intent = new Intent(Community.this, Gallery.class);
                    intent.putExtra("title", "New Post");
                    intent.putExtra("mode", 1);
                    intent.putExtra("maxSelection", 500);
                    intent.putExtra("isFromNewPost", true);
                    startActivity(intent);
                }
                return true;
            case R.id.action_view_mode:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_menuitem_view_mode, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_menuitem_view_mode);
                View view = getLayoutInflater().inflate(R.layout.view_mode_dialog, null, false);

                LinearLayout viewModeNormal = view.findViewById(R.id.view_mode_normal);
                LinearLayout viewMode2n = view.findViewById(R.id.view_mode_2n);
                LinearLayout viewMode3n = view.findViewById(R.id.view_mode_3n);
                LinearLayout viewMode4n = view.findViewById(R.id.view_mode_4n);

                viewModeNormal.setOnClickListener(v -> {
//                    ll_1.performClick();

                    if (MainCollectionFragment.objHomeInterface.changeListFormat(1)) {
                        if (currentType != 1 && currentType != 5) {
                            _indexes.add(1);
                            currentType = 1;
//                                            disableAllView();
//                                            enableAllView(ll_1);
                        }
                        FirebaseUtils.logEvents(Community.this, constants.community_1x1_selection);
                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode2n.setOnClickListener(v -> {
//                    ll_2.performClick();

                    if (MainCollectionFragment.objHomeInterface.changeListFormat(2)) {
                        if (currentType != 2) {
                            _indexes.add(2);
                            currentType = 2;
//                                            disableAllView();
//                                            enableAllView(ll_2);
                        }
                        FirebaseUtils.logEvents(Community.this, constants.community_2x2_selection);
                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode3n.setOnClickListener(v -> {
//                    ll_3.performClick();

                    if (MainCollectionFragment.objHomeInterface.changeListFormat(3)) {

                        if (currentType != 3) {
                            _indexes.add(3);
                            currentType = 3;
//                                            disableAllView();
//                                            enableAllView(ll_3);
                        }
                        FirebaseUtils.logEvents(Community.this, constants.community_3x2_selection);
                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode4n.setOnClickListener(v -> {
//                    ll_4.performClick();

                    if (MainCollectionFragment.objHomeInterface.changeListFormat(4)) {
                        if (currentType != 4) {
                            _indexes.add(4);
                            currentType = 4;
//                                            disableAllView();
//                                            enableAllView(ll_4);
                        }
                        FirebaseUtils.logEvents(Community.this, constants.community_4x2_selection);
                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                AlertDialog.Builder builder = new AlertDialog.Builder(Community.this);
                builder.setView(view);
                alertDialogViewModel = builder.create();

                alertDialogViewModel.show();
                return true;
            case R.id.action_share_paintology:
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(Community.this, constants.comm_menuitem_share_paintology, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(Community.this, constants.comm_menuitem_share_paintology);
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, "Paintology - great little app you should check out");
                    share.putExtra(Intent.EXTRA_TEXT, "I found this free app called Paintology, I think you will love it, check it out!\n\nhttps://play.google.com/store/apps/details?id=com.paintology.lite");

                    startActivity(Intent.createChooser(share, "Share link!"));
                } catch (Exception e) {
                    Log.e("TAG", "Exception at share " + e.getMessage());
                }
                return true;
            case R.id.action_feedback:
                Bundle bundle = new Bundle();
                bundle.putString("video_id", StringConstants.intro_community);
                bundle.putString("screen", "feedback");
                ContextKt.sendUserEventWithParam(Community.this, StringConstants.intro_video_watch, bundle);
                FireUtils.showFeedbackDialog(this);
                return true;
            case R.id.action_watch_intro_video:
                Bundle bundle1 = new Bundle();
                bundle1.putString("video_id", StringConstants.intro_community);
                bundle1.putString("screen", "video");
                ContextKt.sendUserEventWithParam(Community.this, StringConstants.intro_video_watch, bundle1);
                FireUtils.openIntroVideoScreen(this, StringConstants.intro_community, "");
                return true;
            case R.id.action_goto_playstore:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(Community.this, constants.comm_menuitem_google_playstore, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(Community.this, constants.comm_menuitem_google_playstore);
                try {
                    String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Log.e("Community", e.getMessage());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void getData() {
//        if (realTimeDBUtils.getCurrentUser() != null) {
//            logged_user_id = constants.getString(constants.UserId, Community.this);
//
//            realTimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(constants.firebase_deleted_user).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.e("TAGG", "OnDataChange Called Get Deleted");
//                    _lst_deleted.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        DeletedUser _deleted = postSnapshot.getValue(DeletedUser.class);
//                        _lst_deleted.add(_deleted);
//                    }
//                    blockedUser();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
//
//    void blockedUser() {
//
//        realTimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(constants.firebase_blocked_user).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("TAGG", "OnDataChange Called Get Deleted");
//                _lst_blocked.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    BlockedUsersModel _deleted = postSnapshot.getValue(BlockedUsersModel.class);
//                    _lst_blocked.add(_deleted);
//                }
//                getUserFromApi();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
//
//    public void getUserFromApi() {
//        HashMap<String, RequestBody> _map = new HashMap<>();
//        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), logged_user_id);
//        _map.put("user_id", userId);
//
//        Observable<MyUsersModel> _observer = apiInterface.getMyUser(ApiClient.SECRET_KEY, _map);
//        Log.e("TAG", "getUserFromApi " + logged_user_id);
//        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<MyUsersModel>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(MyUsersModel _response) {
//                Log.e("TAGG", "OnNext Call ");
//                try {
//                    if (_response != null && _response.getCode() == 200) {
//                        lst_my_user = _response.get_user_list();
//                        if (_response.get_user_list() != null && _response.get_user_list().size() > 0) {
//                            Log.e("TAGG", "getUserFromApi OnNext userlist size " + _response.get_user_list().size());
//                            getAllUser(_response.get_user_list());
//                        }
//                    } else {
//                        Toast.makeText(Community.this, "failed", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Log.e("TAG", "Exception at onNext " + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//    }
//
//    private void getAllUser(ArrayList<MyUsersModel.data> my_user_list) {
//        Log.e("TAG", "getUserFromApi getAllUser called");
//
//        ValueEventListener _ref_listener = realTimeDBUtils.getDbReferenceUserList().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                _user_list.clear();
//                try {
//                    int i = 0;
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        Firebase_User _user = postSnapshot.getValue(Firebase_User.class);
//
////                        setPending(_user);
//
//                        if (!isallreadyAdded(_user.getUser_id()) && !isFoundInDeleted(_user.getUser_id()) && !_user.getUser_id().equalsIgnoreCase(logged_user_id)) {
//                            for (int j = 0; j < my_user_list.size(); j++) {
//                                if (_user.getUser_id().equalsIgnoreCase(my_user_list.get(j).getUser_id())) {
//                                    if (isFoundInBlocked(_user.getUser_id())) {
//                                        _user.setBlocked(true);
//                                    } else
//                                        _user.setBlocked(false);
//                                    _user_list.add(_user);
//                                    break;
//                                }
//                            }
//                        }
//                        i++;
//                    }
//                    try {
//                        Collections.sort(_user_list, new Comparator<Firebase_User>() {
//                            @Override
//                            public int compare(Firebase_User o1, Firebase_User o2) {
//                                try {
//                                    boolean b1 = Boolean.parseBoolean(o1.getIs_online());
//                                    boolean b2 = Boolean.parseBoolean(o2.getIs_online());
//                                    return Boolean.compare(b2, b1);
//                                } catch (Exception e) {
//                                    Log.e("TAGGG", "Exception at sort 1 " + e.getMessage());
//                                }
//                                return 0;
//                            }
//                        });
//                    } catch (Exception e) {
//                        Log.e("TAGGG", "Exception at sort " + e.getMessage());
//                    }
//
//                    try {
//                        Collections.sort(_user_list, new Comparator<Firebase_User>() {
//                            @Override
//                            public int compare(Firebase_User o1, Firebase_User o2) {
//                                try {
//                                    boolean b1 = o1.isBlocked();
//                                    boolean b2 = o2.isBlocked();
//                                    return Boolean.compare(b1, b2);
//                                } catch (Exception e) {
//                                    Log.e("TAGGG", "Exception at sort 1 " + e.getMessage());
//                                }
//                                return 0;
//                            }
//                        });
//                    } catch (Exception e) {
//                        Log.e("TAGGG", "Exception at sort " + e.getMessage());
//                    }
//
//
//
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception " + e.getMessage(), e);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
//
//    public boolean isFoundInDeleted(String _id) {
//        for (int i = 0; i < _lst_deleted.size(); i++) {
//            if (_id.equalsIgnoreCase(_lst_deleted.get(i).getUser_id()))
//                return true;
//        }
//        return false;
//    }
//
//    public boolean isFoundInBlocked(String _id) {
//        for (int i = 0; i < _lst_blocked.size(); i++) {
//            if (_id.equalsIgnoreCase(_lst_blocked.get(i).getUser_id()))
//                return true;
//        }
//        return false;
//    }
//
//    public boolean isallreadyAdded(String user_id) {
//        for (int i = 0; i < _user_list.size(); i++) {
//            if (_user_list.get(i).getUser_id().equalsIgnoreCase(user_id))
//                return true;
//        }
//        return false;
//
//    }
//
//    void setPending(Firebase_User user) {
//        if (user.getKey().isEmpty() || TextUtils.isEmpty(user.getUser_id()))
//            return;
//
//        String my_user_key = realTimeDBUtils.getCurrentUser().getUid();
//
//        String _node_name = getChatNode(Integer.parseInt(user.getUser_id()), Integer.parseInt(logged_user_id));
//        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference(constants.firebase_chat_module).child(_node_name).child("Msg");
//        _reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("TAGG", "Chat Found in node " + _node_name);
//                boolean isreceived_msg = false;
//                boolean issended_msg = false;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getSender().equalsIgnoreCase(user.getKey())) {
//                        isreceived_msg = true;
//                    } else if (chat.getSender().equalsIgnoreCase(my_user_key)) {
//                        issended_msg = true;
//                    }
//                }
//                Log.e("TAGG", "Both flas isreceived_msg " + isreceived_msg + " issended_msg " + issended_msg);
//                if (isreceived_msg && !issended_msg && !isallreadyAdded(user.getUser_id()) && !isFoundInDeleted(user.getUser_id())) {
//                    Log.e("TAGG", "UserAdded in list " + user.getUser_id());
//                    if (isFoundInBlocked(user.getUser_id())) {
//                        user.setBlocked(true);
//                    } else {
//                        user.setBlocked(false);
//                        user.setPending(true);
//                    }
//
//                    _user_list.add(user);
//
//                } else if (isreceived_msg && issended_msg && !isallreadyAdded(user.getUser_id()) && !isFoundInDeleted(user.getUser_id())) {
//                    if (isFoundInBlocked(user.getUser_id())) {
//                        user.setBlocked(true);
//                    } else
//                        user.setBlocked(false);
//
//                    _user_list.add(user);
//                    checkCount(_user_list);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
//
//    private void checkCount(List<Firebase_User> user_list) {
//        for (Firebase_User user :
//                user_list) {
//            getUnreadCounter(user.getKey(), user.getUser_id(), new CountListener() {
//                @Override
//                public void onCount(int count) {
//                    if (count > 0) {
//                        AppUtils.saveHasUnreadChat(Community.this, true);
//                        setChatNotificationIcon();
//                    }
//                }
//            });
//        }
//    }
//
//    private String getChatNode(int uid1, int uid2) {
//        if (uid1 < uid2) {
//            Log.e("TAGG", "Node Return " + uid1 + "_" + uid2);
//            return uid1 + "_" + uid2;
//        } else {
//            Log.e("TAGG", "Node Return " + uid2 + "_" + uid1);
//            return uid2 + "_" + uid1;
//        }
//    }
//
//    public void getUnreadCounter(String senderKey, String senderID, CountListener listener) {
//
//
//        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference(constants.firebase_chat_module).child(getChatNode(Integer.parseInt(_user_id), Integer.parseInt(senderID))).child("Msg");
//        _reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getSender().equalsIgnoreCase(senderKey) && chat.getIsMsgseen().equalsIgnoreCase("false")) {
//                        counter = counter + 1;
//                    }
//                }
//
//                listener.onCount(counter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    interface CountListener {
//        void onCount(int count);
//    }

}