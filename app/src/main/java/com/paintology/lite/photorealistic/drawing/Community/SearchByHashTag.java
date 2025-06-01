package com.paintology.lite.photorealistic.drawing.Community;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Adapter.CommentListAdapter;
import com.paintology.lite.photorealistic.drawing.Adapter.CommunityPostAdapter;
import com.paintology.lite.photorealistic.drawing.Adapter.ShowAllHashTagAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Chat.MyUsersModel;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Data;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.MyResponse;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Sender;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Chat.RealTimeDBUtils;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.Model.GetUserProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.ResponseIncreaseCounter;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.bottomsheet.CommunityCommentsBottomsheet;
import com.paintology.lite.photorealistic.drawing.bus.UserLoginUpdateEvent;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ChatUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class SearchByHashTag extends AppCompatActivity implements home_fragment_operation, View.OnClickListener, PostOperation, GoogleApiClient.OnConnectionFailedListener {


    RecyclerView rv_community_detail;
    StringConstants constants;

    //    ShowCommunityListAdapter mAdapter;
    CommunityPostAdapter mAdapter;
    int pageNumber = 1;
    int totalItem = 0;

    ApiInterface apiInterface;
    CircleProgressBar item_progress_bar;

//    int p_num_base = 0;

    boolean isLoggedIn;
    GoogleSignInAccount account;
    String LoginInPaintology;


    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;

    List<CommunityPost> posts = new ArrayList<>();

    int RC_SIGN_IN = 7;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    ProgressDialog progressDialog = null;
    GoogleApiClient googleApiClient;

    LoginButton facebook_login_btn;
    SwipeRefreshLayout swipe_refresh;

    private GoogleSignInClient mGoogleSignInClient;

    String hashTag = "";
    boolean isLoading = false;
    int totalPost = 0;
//    LinearLayout ll_1, ll_2, ll_3, ll_4;

    GridLayoutManager mLayoutManager;

    boolean isAuthenticating = false;
    //    ImageView iv_post_image;
//    ImageView iv_profile;
    String isLoginInPaintology;

    //    TextView tv_title;
    String userID;
    //    ImageView iv_header_menu;
    FirebaseFirestore db_firebase;


    RelativeLayout fm_image;
    ImageView iv_enlarge_image;
    ImageView tv_back;

    HashMap<String, String> hashMap = new HashMap<>();
    OperationAfterLogin _operationLogin = null;

    ArrayList<MyUsersModel.data> lst_my_user = new ArrayList<>();

    RealTimeDBUtils realTimeDBUtils;
    ApiInterface apiService;


    TextView tv_empty_msg;
    private AlertDialog alertDialogViewModel = null;
    private MenuItem actionProfile;
    private List<CommunityPost> postList = new ArrayList<>();

    final int BATCH_SIZE = 10;

    private DocumentSnapshot lastVisibleDocument;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search_by_hash_tag);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        setTitle("Community");

        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        constants = new StringConstants();

        apiService = ApiClient.getClientNotification().create(ApiInterface.class);
        realTimeDBUtils = MyApplication.get_realTimeDbUtils(this);


        tv_empty_msg = (TextView) findViewById(R.id.tv_empty_msg);
        tv_empty_msg.setVisibility(View.GONE);

        FirebaseFirestore.setLoggingEnabled(true);
        db_firebase = FirebaseFirestore.getInstance();

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent);

        userID = constants.getString(constants.UserId, SearchByHashTag.this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        getAllUser();

//        tv_title = (TextView) findViewById(R.id.tv_hashtag_name);
//
//        tv_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showHashTag();
//            }
//        });


        if (BuildConfig.DEBUG) {

            Toast.makeText(SearchByHashTag.this, constants.open_post_detail, Toast.LENGTH_SHORT).show();
        }


        FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_post_detail);
        facebook_login_btn = (LoginButton) findViewById(R.id.login_button);
        facebook_login_btn.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        facebook_login_btn.registerCallback(callbackManager, callback);

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

        googleApiClient = new GoogleApiClient.Builder(SearchByHashTag.this)
                .enableAutoManage(SearchByHashTag.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SearchByHashTag.this, gso);
        rv_community_detail = (RecyclerView) findViewById(R.id.rv_community_detail);
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        item_progress_bar = findViewById(R.id.item_progress_bar);
        item_progress_bar.setVisibility(View.GONE);

        if (getIntent().hasExtra("hashtag")) {
            hashTag = getIntent().getStringExtra("hashtag");
            Log.d("CommunityPosts", "hashTag : " + hashTag);
            setTitle(hashTag);
            fetchPostsWithTag(hashTag);
        }

//        ll_1 = findViewById(R.id.ll_1);
//        ll_2 = findViewById(R.id.ll_2);
//        ll_3 = findViewById(R.id.ll_3);
//        ll_4 = findViewById(R.id.ll_4);
//
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
//
//        disableAllView(ll_4);
//        enableAllView(ll_1);

        rv_community_detail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = 0;
                if (layoutManager != null) {
                    visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= BATCH_SIZE) {
                            pageNumber++;
                            fetchPostsWithTag(hashTag); // LoaLoad more data
                        }
                    }
                }
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 1;
                isLastPage = false;
                lastVisibleDocument = null;
                swipe_refresh.setRefreshing(true);
                fetchPostsWithTag(hashTag);
            }
        });

//        iv_post_image = (ImageView) findViewById(R.id.iv_post_image);
//        iv_profile = findViewById(R.id.iv_profile_icon);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        isLoginInPaintology = constants.getString(constants.LoginInPaintology, this);

        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
            if (constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this) != null && !constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this).isEmpty()) {
                Log.e("TAGGG", "Profile Image Set from OnCreate");
//                Glide.with(SearchByHashTag.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
//                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(iv_profile);

                Glide.with(SearchByHashTag.this)
                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
                        .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_icon))
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
//        iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
//                    Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                isLoggedIn = accessToken != null && !accessToken.isExpired();
//                account = GoogleSignIn.getLastSignedInAccount(SearchByHashTag.this);
//                isLoginInPaintology = constants.getString(constants.LoginInPaintology, SearchByHashTag.this);
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                    startActivity(new Intent(SearchByHashTag.this, MyProfile.class));
////                    finish();
//                } else {
//                    if (BuildConfig.DEBUG){
//
//                        Toast.makeText(SearchByHashTag.this,constants.open_social_login_search_hashtag_profile_dialog, Toast.LENGTH_SHORT).show();
//                    }
//
//                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_social_login_search_hashtag_profile_dialog);
//                    showLoginDialog();
//                }
//            }
//        });
//
//        iv_post_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
//                    Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (BuildConfig.DEBUG){
//
//                    Toast.makeText(SearchByHashTag.this,constants.click_community_post, Toast.LENGTH_SHORT).show();
//                }
//
//                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_post);
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                    Intent intent = new Intent(SearchByHashTag.this, Gallery.class);
//                    intent.putExtra("title", "New Post");
//                    intent.putExtra("mode", 1);
//                    intent.putExtra("maxSelection", 500);
//                    intent.putExtra("isFromNewPost", true);
//                    startActivity(intent);
////                    finish();
//                } else {
//                    if (BuildConfig.DEBUG){
//
//                        Toast.makeText(SearchByHashTag.this,constants.open_social_login_search_hashtag_new_post_dialog, Toast.LENGTH_SHORT).show();
//                    }
//
//                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_social_login_search_hashtag_new_post_dialog);
//                    showLoginDialog();
//                }
//            }
//        });

//        iv_header_menu = findViewById(R.id.iv_header_menu);
//        iv_header_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                final CharSequence[] items = {"View Mode - 4 X 2", "View Mode - 3 X 2", "View Mode - 2 X 2", "View Mode - normal", "Add new Post", "Share feedback", "Go to Google Playstore"};
//                final CharSequence[] items = {"View Mode", "Add new Post", "Share feedback", "Go to Google Playstore"};
//
//                if (BuildConfig.DEBUG){
//
//                    Toast.makeText(SearchByHashTag.this,constants.community_header_menu_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(SearchByHashTag.this, constants.community_header_menu_click);
//
//                // Initializing the popup menu and giving the reference as current context
//                PopupMenu popupMenu = new PopupMenu(SearchByHashTag.this, view);
//
//                // Inflating popup menu from popup_menu.xml file
//                popupMenu.getMenuInflater().inflate(R.menu.popup_community_menu, popupMenu.getMenu());
//                popupMenu.getMenu().findItem(R.id.share).setVisible(false);
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
//                                    ll_1.performClick();
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode2n.setOnClickListener(v -> {
//                                    ll_2.performClick();
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode3n.setOnClickListener(v -> {
//                                    ll_3.performClick();
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                viewMode4n.setOnClickListener(v -> {
//                                    ll_4.performClick();
//                                    if (alertDialogViewModel != null) {
//                                        alertDialogViewModel.dismiss();
//                                    }
//
//                                });
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchByHashTag.this);
//                                builder.setView(view);
//                                alertDialogViewModel = builder.create();
//
//                                alertDialogViewModel.show();
//                                break;
//                            case R.id.add_new_post:
//                                if (BuildConfig.DEBUG){
//
//                                    Toast.makeText(SearchByHashTag.this,constants.click_community_post_menu, Toast.LENGTH_SHORT).show();
//                                }
//
//                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_post_menu);
//                                if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
//                                    Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                                    break;
//                                }
//                                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                                    Intent intent = new Intent(SearchByHashTag.this, Gallery.class);
//                                    intent.putExtra("title", "New Post");
//                                    intent.putExtra("mode", 1);
//                                    intent.putExtra("maxSelection", 500);
//                                    intent.putExtra("isFromNewPost", true);
//                                    startActivity(intent);
//                                } else {
//                                    if (BuildConfig.DEBUG){
//
//                                        Toast.makeText(SearchByHashTag.this,constants.open_social_login_search_hashtag_new_post_dialog, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_social_login_search_hashtag_new_post_dialog);
//                                    showLoginDialog();
//                                }
//                                break;
//                            case R.id.feedback:
//                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_feedback);
//                                showFeedbackDialog();
//                                break;
//                            case R.id.playstore:
//                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_googleplay_click);
//                                try {
//                                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
//                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    startActivity(browserIntent);
//                                } catch (Exception e) {
//                                    Log.e("Community", e.getMessage());
//                                }
//                                break;
//                        }
//
//                        return true;
//                    }
//                });
//                // Showing the popup menu
//                popupMenu.show();
//
////                AlertDialog.Builder builder = new AlertDialog.Builder(SearchByHashTag.this);
////                builder.setItems(items, new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int item) {
////                        // Do something with the selection
////                        Log.e("TAGGG", "onClick " + item);
////                        switch (item) {
////                            case 0: {
////                                ll_4.performClick();
////                            }
////                            break;
////                            case 1: {
////                                ll_3.performClick();
////                            }
////                            break;
////                            case 2: {
////                                ll_2.performClick();
////                            }
////                            break;
////                            case 3: {
////                                ll_1.performClick();
////                            }
////                            break;
////                            case 4: {
////                                if (BuildConfig.DEBUG){
////
////                                    Toast.makeText(SearchByHashTag.this,constants.click_community_post_menu, Toast.LENGTH_SHORT).show();
////                                }
////
////                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_post_menu);
////                                if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
////                                    Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
////                                    return;
////                                }
////                                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
////                                    Intent intent = new Intent(SearchByHashTag.this, Gallery.class);
////                                    intent.putExtra("title", "New Post");
////                                    intent.putExtra("mode", 1);
////                                    intent.putExtra("maxSelection", 500);
////                                    intent.putExtra("isFromNewPost", true);
////                                    startActivity(intent);
////                                } else {
////                                    if (BuildConfig.DEBUG){
////
////                                        Toast.makeText(SearchByHashTag.this,constants.open_social_login_search_hashtag_new_post_dialog, Toast.LENGTH_SHORT).show();
////                                    }
////
////                                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_social_login_search_hashtag_new_post_dialog);
////                                    showLoginDialog();
////                                }
////                            }
////                            break;
////                            case 5: {
////                                if (BuildConfig.DEBUG){
////
////                                    Toast.makeText(SearchByHashTag.this,constants.click_community_menu_feedback, Toast.LENGTH_SHORT).show();
////                                }
////
////
////                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_feedback);
////                                showFeedbackDialog();
////                            }
////                            break;
////                            case 6: {
////                                if (BuildConfig.DEBUG){
////
////                                    Toast.makeText(SearchByHashTag.this,constants.click_community_menu_googleplay_click, Toast.LENGTH_SHORT).show();
////                                }
////
////                                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_googleplay_click);
////                                try {
////                                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
////                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                                    startActivity(browserIntent);
////                                } catch (ActivityNotFoundException nf) {
////
////                                } catch (Exception e) {
////
////                                }
////                            }
////                            break;
////                        }
////                    }
////                });
////                AlertDialog alert = builder.create();
////                alert.show();
//
//            }
//        });

        fm_image = (RelativeLayout) findViewById(R.id.fm_image);
        iv_enlarge_image = (ImageView) findViewById(R.id.iv_enlarge_image);

    }


    void showHashTag() {
        final Dialog dialog = new Dialog(SearchByHashTag.this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setContentView(R.layout.view_all_hashtag_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        StringTokenizer tokenizer = new StringTokenizer(hashTag, "|");
        ArrayList<String> lst = new ArrayList<>();
        do {
            lst.add(tokenizer.nextToken());

        } while (tokenizer.hasMoreTokens());

        RecyclerView rv = (RecyclerView) dialog.findViewById(R.id.rv_hashtag);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);

        ShowAllHashTagAdapter _adapter = new ShowAllHashTagAdapter(lst, SearchByHashTag.this);
        rv.setAdapter(_adapter);

        dialog.show();
    }

    @Override
    public void doOperationOnPost(int position, int operationType) {

    }

    @Override
    public void likeOperation(int pos, boolean isLike, boolean isFromSocialLogin) {


        String postID = String.valueOf(posts.get(pos).getPost_id());
        Map<String, Object> comments = new HashMap<>();
        comments.put("post_id", postID);

        FirebaseFunctions.getInstance().getHttpsCallable("communityPost-like").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SearchByHashTag.this, "Liked added successfully.", Toast.LENGTH_SHORT).show();
                    likeRewardPoint(posts.get(pos).getPost_id());
                    Log.d("CloudFunctions", "Liked added successfully");
                    if (posts.get(pos).getStatistic() != null) {
                        if (posts.get(pos).getStatistic().getLikes() == null) {
                            CommunityPost.Statistic statistic = posts.get(pos).getStatistic();
                            statistic.setLikes(1);
                            posts.get(pos).setStatistic(statistic);
                        } else {
                            CommunityPost.Statistic statistic = posts.get(pos).getStatistic();
                            int likesCount = posts.get(pos).getStatistic().getLikes();
                            statistic.setLikes(likesCount + 1);
                            posts.get(pos).setStatistic(statistic);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("CloudFunctions", "Error: " + e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("CloudFunctions", "Failure: " + e);
            }
        });
    }

    @Override
    public void viewOperation(int pos, int totalViews, boolean isFromSocialLogin) {
        if (hashMap.containsKey(posts.get(pos).getPost_id())) {
            return;
        } else {
            hashMap.put(posts.get(pos).getPost_id(), posts.get(pos).getPost_id());
        }

        String postID = String.valueOf(posts.get(pos).getPost_id());
        Map<String, Object> comments = new HashMap<>();
        comments.put("post_id", postID);

        FirebaseFunctions.getInstance().getHttpsCallable("communityPost-view").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    if (posts.get(pos).getStatistic() != null) {
                        CommunityPost.Statistic statistic = posts.get(pos).getStatistic();
                        int viewsCount = posts.get(pos).getStatistic().getViews();
                        statistic.setViews(viewsCount + 1);
                        posts.get(pos).setStatistic(statistic);
                        mAdapter.notifyItemChanged(pos);
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("CloudFunctions", "Error: " + e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("CloudFunctions", "Failure: " + e);
            }
        });


       /* UserPostList model = _userPostList.getObjData().getPost_list().get(pos);
        Log.e("TAGGG", "Like Operation isLiked " + model.isLiked() + " postId " + model.getPost_id());
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, SearchByHashTag.this));
        RequestBody post_id = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.getPost_id() != null) ? model.getPost_id() : "");
        RequestBody viewsCount = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(totalViews));

        _map.put("user_id", userId);
        _map.put("post_id", post_id);
        _map.put("views", viewsCount);

        Observable<ResponseBase> _observer = apiInterface.updateViewCount(ApiClient.SECRET_KEY, _map);

        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBase>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBase responseBase) {
                Log.e("TAGGG", "Operation onNext");
                if (responseBase != null) {
                    if (!responseBase.getCode().equals(200)) {

                        if (_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views() != null) {
                            int total = Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views());

                            total = total + 1;
                            _userPostList.getObjData().getPost_list().get(pos).getObjView().setTotal_views(total + "");

                        }
                        mAdapter.notifyItemChanged(pos);
                        if (responseBase.getStatus() != null)
                            Toast.makeText(SearchByHashTag.this, responseBase.getStatus() + "", Toast.LENGTH_SHORT).show();
                    } else {

                        MyUsersModel.data obj = new MyUsersModel.data();
                        obj.setType("like");
                        obj.setUser_id(model.getUserInfo().getUserId());
                        lst_my_user.add(obj);

                        if (isFromSocialLogin) {
                            int total = Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views());

                            total = total + 1;
                            _userPostList.getObjData().getPost_list().get(pos).getObjView().setTotal_views(total + "");

                        }
                    }
                    mAdapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGGG", "Operation Failed");
            }

            @Override
            public void onComplete() {
                Log.e("TAGGG", "Operation onComplete");
            }
        });*/
    }

    @Override
    public void addComment(int pos, String comment, ArrayList<Firebase_User> _user_list) {

        System.out.println("Add Comment In Search By HashTag Screen");

        if (AppUtils.isLoggedIn()) {

            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(SearchByHashTag.this, "Please add comment!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = constants.getString(constants.UserId, SearchByHashTag.this);
            String userName = constants.getString(constants.Username, SearchByHashTag.this);
            String userAvatar = constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this);
            String userCountry = constants.getString(constants.UserCountry, SearchByHashTag.this);
            String postID = String.valueOf(posts.get(pos).getPost_id());
            Log.d("Timestamp", "" + new Timestamp(new Date()));
            Log.d("userId", "" + userId);
            Map<String, Object> comments = new HashMap<>();
            comments.put("post_id", postID);
            comments.put("comment", comment);

            FirebaseFunctions.getInstance().getHttpsCallable("communityPost-comment").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SearchByHashTag.this, "Comment added successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("CloudFunctions", "Comment added successfully");
                        commentRewardPoint(posts.get(pos).getPost_id());
                        int commentsCount = posts.get(pos).getStatistic().getComments();
                        List<CommunityPost.Comment> commentList = posts.get(pos).getLastComments();
                        Map<String, Object> map = new HashMap<>();
                        map.put("comment", comment);
                        map.put("user_id", userId);
                        map.put("name", userName);
                        map.put("avatar", userAvatar);
                        map.put("country", userCountry);
                        map.put("created_at", "");
                        if (commentList.size() == 3) {
                            commentList.remove(0);
                        }
                        commentList.add(new CommunityPost.Comment(map));
                        posts.get(pos).setLastComments(commentList);
                        posts.get(pos).getStatistic().setComments(commentsCount + 1);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(SearchByHashTag.this, "Failed To Add Comment", Toast.LENGTH_SHORT).show();
                            Log.e("CloudFunctions", "Error: " + e);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("CloudFunctions", "Failure: " + e);
                }
            });

        }else{
            Intent intent = new Intent(SearchByHashTag.this, LoginActivity.class);
            startActivity(intent);
        }
//        String username = constants.getString(constants.Username, SearchByHashTag.this);
//        HashMap<String, RequestBody> _map = new HashMap<>();
//        RequestBody req_user_id = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, this));
//        RequestBody req_post_id = RequestBody.create(MediaType.parse("text/plain"), _userPostList.getObjData().getPost_list().get(pos).getPost_id() + "");
//        RequestBody req_comment = RequestBody.create(MediaType.parse("text/plain"), comment);
//        RequestBody req_username = RequestBody.create(MediaType.parse("text/plain"), username);
//        Log.e("TAGGG", "addComment Post Id " + _userPostList.getObjData().getPost_list().get(pos).getPost_id() + " Comment " + comment);
//        _map.put("user_id", req_user_id);
//        _map.put("post_id", req_post_id);
//        _map.put("comment", req_comment);
//        _map.put("username", req_username);
//
//        Observable<ResponseBase> _observer = apiInterface.add_post(ApiClient.SECRET_KEY, _map);
//
//        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBase>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(ResponseBase ResponseBase) {
//                try {
//                    if (ResponseBase != null) {
//                        if (ResponseBase.getCode() == 200) {
//                            int total = ((_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments() != null && !_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments().isEmpty()) ? Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments()) + 1 : 0);
//                            _userPostList.getObjData().getPost_list().get(pos).getCommentsList().setTotal_comments(total + "");
//
//                            if (_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getPost_comment_lists() != null) {
//                                ArrayList<post_comment_lists> lst = _userPostList.getObjData().getPost_list().get(pos).getCommentsList().getPost_comment_lists();
////                                Log.e("TAGGG", "Comment List size Before " + lst.size());
//                                post_comment_lists obj = new post_comment_lists();
//                                obj.setComment_content(comment);
//                                obj.setUsername(username);
//                                obj.setComment_date("Just Now");
//                                lst.add(0, obj);
//
//                                _userPostList.getObjData().getPost_list().get(pos).getCommentsList().setPost_comment_lists(lst);
////                                mAdapter.notifyItemChanged(pos);
////                                Log.e("TAGGG", "Comment List size After " + lst.size());
//                            }
//                            MyUsersModel.data obj = new MyUsersModel.data();
//                            obj.setType("like");
//                            obj.setUser_id(_userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserId());
//                            lst_my_user.add(obj);
//                            mAdapter.notifyItemChanged(pos);
//
//                            if (_user_list != null && _user_list.size() != 0)
//                                for (int i = 0; i < _user_list.size(); i++) {
//                                    sendMentionNotification(_user_list.get(i).getKey(), constants.getString(constants.Username, SearchByHashTag.this), constants.getString(constants.Username, SearchByHashTag.this) + " mentioned you in comment.", _userPostList.getObjData().getPost_list().get(pos).getPost_id() + "");
//                                }
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e("TAGG", "Exception at post comment " + e.getMessage(), e);
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e("TAGG", "Exception at post comment " + e.getMessage(), e);
//                Toast.makeText(SearchByHashTag.this, "Failed To Add Comment", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
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
    }

    private void sendMentionNotification(String receiver, String user_name, String msg, String post_id) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        Log.e("TAG", "sendNotification called msg " + msg);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    realTimeDBUtils = MyApplication.get_realTimeDbUtils(SearchByHashTag.this);

                    if (realTimeDBUtils.getCurrentUser() == null || realTimeDBUtils.getCurrentUser().getUid() == null)
                        return;

                    Data data = new Data(realTimeDBUtils.getCurrentUser().getUid(), R.mipmap.ic_launcher, msg, user_name, receiver, "true", post_id);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Log.e("TAGG", "onResponse called " + response.toString());
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("TAG", "Notification onFailure " + t.getMessage(), t);
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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


    /*This method will prompt confirm exit dialog, this method calles when user click on back arrow.*/
    @Override
    public boolean onKeyDown(int pInt, KeyEvent pKeyEvent) {

    /*    Log.e("TAGGG", "OnKeyDown pageNumber " + pageNumber + " p_num_base " + p_num_base + " " + _userPostList.getObjData().getPost_list().size());
        if (pageNumber > p_num_base) {
            Intent intent = new Intent();
            intent.putExtra("pageNumber", pageNumber);
            intent.putExtra("updatedList", _userPostList);
            setResult(RESULT_OK, intent);
            finish();
            return false;
        } else*/

        if (fm_image.getVisibility() == View.VISIBLE) {
            Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_animation);
            iv_enlarge_image.startAnimation(animZoomOut);
            if (BuildConfig.DEBUG) {

                Toast.makeText(SearchByHashTag.this, constants.comm_post_zoom_close, Toast.LENGTH_SHORT).show();
            }

            FirebaseUtils.logEvents(SearchByHashTag.this, constants.comm_post_zoom_close);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fm_image.setVisibility(View.GONE);
                    tv_back.setVisibility(View.GONE);
                }
            }, 500);
            return true;
        }

        finish();
        return false;
    }

    @Override
    public boolean isLoggedIn(OperationAfterLogin _operation) {
        if (constants.getBoolean(constants.IsGuestUser, this)) {
            FireUtils.openLoginScreen(this, true);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void downloadImage(int pos, boolean NeedToopenInCanvas) {
        try {

            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(this), fileName);
                if (imageFile.exists()) {
                    if (NeedToopenInCanvas) {
                        Intent intent = new Intent(this, PaintActivity.class);
                        intent.setAction("LoadWithoutTrace");
                        intent.putExtra("path", imageFile.getName());
                        intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                        intent.putExtra("isPickFromOverlaid", true);
                        startActivity(intent);
                    } else showDialog("File already saved", imageFile.getAbsolutePath());
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), removeQueryString(fileName), false, post_titel, NeedToopenInCanvas, pos, false, false).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    public static String removeQueryString(String url) {
        int index = url.indexOf('?');
        if (index != -1) {
            return url.substring(0, index);
        }
        return url; // Return original URL if '?' not found
    }

    @Override
    public void shareImage(int pos) {
        if (posts.get(pos).getImages().getContent() != null) {
            String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
            if (fileName.contains("?")) {
                String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                fileName = beforeQuestionMark.replace("%2F", "_");
            }
            String title = posts.get(pos).getTitle();
            File imageFile = new File(KGlobal.getDownloadPath(this), fileName);
            if (imageFile.exists()) {
                System.out.println("imageFile :" + imageFile.getPath());
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.authority), imageFile);
                doSocialShare(pos, photoURI, title);
            } else {
                Log.e("TAG", "shareImage: " + posts.get(pos).getImages().getContent());
                new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, true, title, false, pos, false, false).execute();
            }
        }
    }

    @Override
    public void copyImage(int pos) {
        if (posts.get(pos).getImages().getContent() != null) {

            ClipboardManager clipboard = (ClipboardManager) SearchByHashTag.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", posts.get(pos).getImages().getContent());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(SearchByHashTag.this, "Copied to Clipboard", Toast.LENGTH_LONG).show();


        }
    }

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType, GoogleSignInAccount... accounts) {

        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_name != null) ? model.user_name : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");
        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, SearchByHashTag.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, SearchByHashTag.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, SearchByHashTag.this));
        try {
            String _ip = constants.getString(constants.IpAddress, SearchByHashTag.this);
            String _country = constants.getString(constants.UserCountry, SearchByHashTag.this);
            String _city = constants.getString(constants.UserCity, SearchByHashTag.this);
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

//        Call<LoginResponseModel> call = apiInterface.addUserData(_map);

        ApiInterface apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
        Call<LoginResponseModel> call = apiInterface.addUserData(ApiClient.SECRET_KEY, _map);
        showProgress(getResources().getString(R.string.please_wait));
        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    isAuthenticating = false;
                    Log.e("TAGGG", "onResponse called");
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {
                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", SearchByHashTag.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", SearchByHashTag.this);

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", SearchByHashTag.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), SearchByHashTag.this);
                            Log.e("TAGGG", "Salt Value is " + response.body().getObjData().getSalt());

                            constants.putString(constants.Username, model.user_name, SearchByHashTag.this);
                            constants.putString(constants.Password, model.user_id, SearchByHashTag.this);
                            constants.putString(constants.Email, model.user_email, SearchByHashTag.this);
                            constants.putString(constants.LoginInPaintology, "true", SearchByHashTag.this);

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, SearchByHashTag.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {

                                        if (BuildConfig.DEBUG) {

                                            Toast.makeText(SearchByHashTag.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }

                                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {

                                            Toast.makeText(SearchByHashTag.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }

                                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    isLoggedIn = true;
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {

                                        if (BuildConfig.DEBUG) {

                                            Toast.makeText(SearchByHashTag.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }

                                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (accounts != null && accounts.length > 0)
                                        account = accounts[0];
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {

                                            Toast.makeText(SearchByHashTag.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }

                                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.GoogleRegistration);
                                    }
                                }
                            }

                            try {
                                if (_operationLogin != null && _operationLogin.getOperationType() != null) {
                                    if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeLike)) {
                                        likeOperation(_operationLogin.getPosition(), true, true);
                                    } else if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeComment)) {
                                        addComment(_operationLogin.getPosition(), _operationLogin.get_obj_comment_data().get_user_comment(), _operationLogin.get_obj_comment_data().get_user_list());
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("TAGG", "Exception " + e.getMessage());
                            }


                            String _user_id = constants.getString(constants.UserId, SearchByHashTag.this);
                            fetchProfileData();
                            MyApplication.get_realTimeDbUtils(SearchByHashTag.this).autoLoginRegister(response.body().getObjData().getStatus());
                            if (KGlobal.isInternetAvailable(SearchByHashTag.this) && _user_id != null && !_user_id.isEmpty()) {
                                startService(new Intent(SearchByHashTag.this, SendDeviceToken.class));
                            }
                        }

                    } else {
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

                                Toast.makeText(SearchByHashTag.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }

                            FirebaseUtils.logEvents(SearchByHashTag.this, constants.PaintologyLoginFailed);
                        }

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(SearchByHashTag.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.event_failed_to_adduser);
                        Toast.makeText(SearchByHashTag.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
                            LoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {

                                Toast.makeText(SearchByHashTag.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }

                            FirebaseUtils.logEvents(SearchByHashTag.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {

                            Toast.makeText(SearchByHashTag.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }

                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.event_failed_to_adduser);
                        Toast.makeText(SearchByHashTag.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "add user in Exception " + e.getMessage(), e);
            hideProgress();
        }
    }

    void showProgress(String msg) {
        try {
            progressDialog = new ProgressDialog(SearchByHashTag.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(msg);
//            progressDialog.setTitle(msg);
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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

                            if (BuildConfig.DEBUG) {

                                Toast.makeText(SearchByHashTag.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }

                            FirebaseUtils.logEvents(SearchByHashTag.this, constants.FacebookLoginSuccess);
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
//                                isLoggedIn = true;
//                                constants.putString(constants.Username, fnm, SearchByHashTag.this);
//                                constants.putString(constants.Password, fid, SearchByHashTag.this);
//                                constants.putString(constants.Email, mail, SearchByHashTag.this);

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

            if (BuildConfig.DEBUG) {

                Toast.makeText(SearchByHashTag.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }

            FirebaseUtils.logEvents(SearchByHashTag.this, constants.FacebookLoginFailed);
        }
    };

    /*This method will prompt social media login dialog when user click on upload zip file.*/
    private void showLoginDialog() {
        Intent intent = new Intent(SearchByHashTag.this, LoginActivity.class);
        startActivity(intent);
    }
//    public void showLoginDialog() {
//        final Dialog dialog = new Dialog(SearchByHashTag.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.social_media_login_layout);
//
//        Button btn_fb = (Button) dialog.findViewById(R.id.fb);
//
//        TextView tv_community_link = (TextView) dialog.findViewById(R.id.tv_community_link);
//        tv_community_link.setVisibility(View.GONE);
//        btn_fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (BuildConfig.DEBUG){
//
//                    Toast.makeText(SearchByHashTag.this,constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT).show();
//                }
//
//                //Perfome Action
//                FirebaseUtils.logEvents(SearchByHashTag.this, constants.FACEBOOK_LOGIN);
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
//        Button btn_paintology = (Button) dialog.findViewById(R.id.btn_paintology);
//        btn_paintology.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (BuildConfig.DEBUG){
//
//                    Toast.makeText(SearchByHashTag.this,constants.Social_Paintology_Login, Toast.LENGTH_SHORT).show();
//                }
//
//                FirebaseUtils.logEvents(SearchByHashTag.this, constants.Social_Paintology_Login);
//                showDialog();
//            }
//        });
//        dialog.show();
//    }

    /*User can do their google sign in using this method*/
    private void signIn() {
        if (BuildConfig.DEBUG) {

            Toast.makeText(SearchByHashTag.this, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT).show();
        }

        FirebaseUtils.logEvents(SearchByHashTag.this, constants.GOOGLE_LOGIN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                isAuthenticating = true;
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
//                constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), SearchByHashTag.this);
//                constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), SearchByHashTag.this);
//                constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), SearchByHashTag.this);
//
                LoginRequestModel model = new LoginRequestModel(
                        (account.getId() != null ? account.getId() : ""),
                        (account.getDisplayName() != null ? account.getDisplayName() : ""),
                        (account.getEmail() != null ? account.getEmail() : ""),
                        ""
                );

                if (BuildConfig.DEBUG) {

                    Toast.makeText(SearchByHashTag.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(SearchByHashTag.this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE, task.getResult(ApiException.class));
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
                if (BuildConfig.DEBUG) {

                    Toast.makeText(SearchByHashTag.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(SearchByHashTag.this, constants.GoogleLoginFailed);
            } catch (Exception e) {

            }
        } else {
            Log.e("TAGGG", "SignIn Result Called");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    ValueEventListener _listener;
    ArrayList<Firebase_User> _lst_firebase_user = new ArrayList<>();

    private void getAllUser() {
        Log.e("TAG", "getAllUser called");
        DatabaseReference _reference = realTimeDBUtils.getDbReferenceUserList();

        _listener = _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int i = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Firebase_User _user = postSnapshot.getValue(Firebase_User.class);
                        if (_user.getUser_name() != null && !_user.getUser_name().isEmpty()) {
                            String name = _user.getUser_name();
                            _user.setUser_name(name);
                            _lst_firebase_user.add(_user);
                        }
                    }
                    _reference.removeEventListener(this);
                    Log.e("TAG", "getAllUser list size " + _lst_firebase_user.size());
                } catch (Exception e) {
                    Log.e("TAGG", "getAllUser Exception " + e.getMessage(), e);
                }
                _reference.removeEventListener(_listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "getAllUser called on cancel " + databaseError.getMessage() + " " + databaseError.getDetails());
            }
        });
    }

//    public void showDialog() {
//
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//        final Dialog dialog = new Dialog(SearchByHashTag.this);
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
//                    LoginRequestModel model = new LoginRequestModel("", uname, edt_email.getText().toString().trim(), edt_pass.getText().toString().trim());
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

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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

    public void fetchProfileData() {
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Observable<GetUserProfileResponse> profileResponse = apiInterface.getUserProfileData(ApiClient.SECRET_KEY, constants.getString(constants.UserId, SearchByHashTag.this));
        profileResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GetUserProfileResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetUserProfileResponse getUserProfileResponse) {
                Log.e("TAGG", "GetUserProfileResponse OnNext Call ");
                if (getUserProfileResponse == null)
                    return;

                if (getUserProfileResponse.getResponse() == null)
                    return;

                int placeHolder;
                if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("male")) {
                    placeHolder = R.drawable.profile_icon;
                    constants.putString(constants.UserGender, constants.MALE, SearchByHashTag.this);
                } else if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("female")) {
                    placeHolder = R.drawable.profile_icon;
                    constants.putString(constants.UserGender, constants.FEMALE, SearchByHashTag.this);
                } else {
                    placeHolder = R.drawable.profile_icon;
                    constants.putString(constants.UserGender, constants.MALE, SearchByHashTag.this);
                }

                if (getUserProfileResponse.getResponse().getProfilePic() != null && !getUserProfileResponse.getResponse().getProfilePic().isEmpty()) {
                    Log.e("TAGGG", "Profile Image Set from 766");
//                    Glide.with(SearchByHashTag.this)
//                            .load(getUserProfileResponse.getResponse().getProfilePic())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                            .into(iv_profile);

                    Glide.with(SearchByHashTag.this)
                            .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    actionProfile.setIcon(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                    constants.putString(constants.ProfilePicsUrl, getUserProfileResponse.getResponse().getProfilePic(), SearchByHashTag.this);
                    MyApplication.get_realTimeDbUtils(SearchByHashTag.this).getDbReference().child(constants.firebase_user_list).child(constants.getString(constants.UserId, SearchByHashTag.this)).child("profile_pic").setValue(getUserProfileResponse.getResponse().getProfilePic());
                } else {
//                    iv_profile.setImageResource(R.drawable.profile_icon);
                    placeHolder = R.drawable.profile_icon;
                    Glide.with(SearchByHashTag.this)
                            .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
                            .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    actionProfile.setIcon(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }

//                swipe_refresh.setRefreshing(true);
//                pageNumber = 1;
//                fetchPostsWithTag(hashTag);
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


    class DownloadImageFromURL extends AsyncTask<Void, Void, String> {
        String imageURL, fileName, title;
        Boolean isFromShare = false, NeedToopenInCanvas, OpenInTraceCanvas, OpenInOverlayCanvas;
        int position;
        File imageFile;

        public DownloadImageFromURL(String imageURL, String fileName, Boolean isFromShare, String post_title, boolean NeedToopenInCanvas, int pos, boolean OpenInTraceCanvas, boolean OpenInOverlayCanvas) {
            this.imageURL = imageURL;
            this.fileName = fileName;
            this.isFromShare = isFromShare;
            title = post_title;
            this.NeedToopenInCanvas = NeedToopenInCanvas;
            this.OpenInTraceCanvas = OpenInTraceCanvas;
            this.OpenInOverlayCanvas = OpenInOverlayCanvas;
            Toast.makeText(SearchByHashTag.this, "downloading started!", Toast.LENGTH_SHORT).show();
            position = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SearchByHashTag.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                    cancel(true);
                    Log.e("TAGG", "onPreExecute cancel downloading ");
                }
            });
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            File file = new File(KGlobal.getDownloadPath(SearchByHashTag.this), fileName);
            if (file.exists()) {
                return file.getAbsolutePath();
            } else {
                URL url = null;
                try {
                    url = new URL(imageURL);
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
//                String downloadPath = KGlobal.getDownloadPath(requireContext());
                String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File path = new File(downloadPath); //Creates app specific folder
                if (!path.exists()) {
                    path.mkdirs();
                }
                imageFile = new File(path, fileName); // Imagename.png
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    if (bm != null) {
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress Image
                    }
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e);
                }
                if (isCancelled()) {
                    if (imageFile.exists()) {
                        imageFile.delete();
                        Log.e("TAGG", "onPreExecute cancel downloading delete");
                    }
                }
                return imageFile.getAbsolutePath();
            }

        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (isFromShare) {
                    System.out.println("imageFile :" + path);
                    Uri photoURI = FileProvider.getUriForFile(SearchByHashTag.this, getString(R.string.authority), new File(path));
                    doSocialShare(position, photoURI, title);
                } else if (NeedToopenInCanvas && OpenInOverlayCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent intent = new Intent(SearchByHashTag.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", file.getName());
                    intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else if (NeedToopenInCanvas && OpenInTraceCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent lIntent1 = new Intent(SearchByHashTag.this, PaintActivity.class);
                    lIntent1.setAction("Edit Paint");
                    lIntent1.putExtra("FromLocal", true);
                    lIntent1.putExtra("paint_name", file.getAbsolutePath());
                    lIntent1.putExtra("isOverraid", false);
                    startActivity(lIntent1);
                } else {
                    posts.get(position).setDownloaded(true);
                    mAdapter.notifyItemChanged(position);
//                    showDialog("Success", path);
                    if (imageFile.exists() && !isCancelled()) {
                        Toast.makeText(SearchByHashTag.this, "Saved in your Images folder", Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(SearchByHashTag.this, new String[]{imageFile.getAbsolutePath()}, null, null);
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    void showFeedbackDialog() {
        final Dialog dialog = new Dialog(SearchByHashTag.this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setContentView(R.layout.feedback_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Spinner _spn = (Spinner) dialog.findViewById(R.id.spn_feedback);

        String[] array = getResources().getStringArray(R.array.feedback_type);

        ImageView iv_icon_close = (ImageView) dialog.findViewById(R.id.iv_icon_close);
        iv_icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tv_exit = (TextView) dialog.findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.DEBUG) {

                    Toast.makeText(SearchByHashTag.this, constants.feedback_exit, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(SearchByHashTag.this, constants.feedback_exit);
                System.exit(0);
                finish();
            }
        });

        ImageView play_logo = (ImageView) dialog.findViewById(R.id.play_logo);
        play_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (BuildConfig.DEBUG) {

                        Toast.makeText(SearchByHashTag.this, constants.feedback_google_play_click, Toast.LENGTH_SHORT).show();
                    }

                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.feedback_google_play_click);
                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException nf) {

                } catch (Exception e) {

                }
            }
        });

        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_name_user);
        EditText edt_feedback = (EditText) dialog.findViewById(R.id.edt_feedback);
        TextView tv_send_feedback = (TextView) dialog.findViewById(R.id.tv_submit);
        me.zhanghai.android.materialratingbar.MaterialRatingBar _rating = dialog.findViewById(R.id.ratingBar);
        _rating.setRating(0);
        tv_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_name.getText().toString().isEmpty()) {
                    edt_name.setError(getResources().getString(R.string.required));
                } else if (edt_feedback.getText().toString().isEmpty()) {
                    edt_feedback.setError(getResources().getString(R.string.required));
                } else if (_rating.getRating() == 0) {
                    Toast.makeText(SearchByHashTag.this, "Please apply rating!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
                        Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        sendFeedback(edt_name.getText().toString().trim(), edt_feedback.getText().toString().trim(), _rating.getRating() + "", array[_spn.getSelectedItemPosition()]);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    void sendFeedback(String _name, String _feedback, String _rating, String app_behaviour) {
        HashMap<String, Object> _feedbackMap = new HashMap<>();
        _feedbackMap.put("name", _name);
        _feedbackMap.put("feedback", _feedback);
        _feedbackMap.put("rating", _rating);
        _feedbackMap.put("app_behaviour", app_behaviour);
        _feedbackMap.put("isFromCommunity", true);

        try {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateandTime = sdf.format(new Date());
                _feedbackMap.put("date", currentDateandTime);
            } catch (Exception e) {

            }
            String country = constants.getString(constants.UserCountry, SearchByHashTag.this);
            String city = constants.getString(constants.UserCity, SearchByHashTag.this);
            if (country != null && !country.isEmpty()) {
                _feedbackMap.put("location", country + "/" + city);
            } else {
                String country_code = constants.getString(constants.UserCountry, SearchByHashTag.this);
                _feedbackMap.put("location", country_code + "/" + city);
            }
        } catch (Exception e) {

        }
        try {
            showProgress(getResources().getString(R.string.please_wait));
            db_firebase.collection("feedback")
                    .add(_feedbackMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Log.e("TAGG", "DocumentSnapshot added with ID: " + documentReference.getId());

                            hideProgress();


                            if (BuildConfig.DEBUG) {

                                Toast.makeText(SearchByHashTag.this, constants.feedback_sent_community, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(SearchByHashTag.this, constants.feedback_sent_community);

                            AlertDialog.Builder builderInner = new AlertDialog.Builder(SearchByHashTag.this);
                            builderInner.setMessage("Thanks for your feedback!");
//                            builderInner.setMessage("This will help us to bring the best experience to Paintology. Thanks again.");
                            builderInner.setCancelable(false);

                            builderInner.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
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
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }

    void showDialog(String title, String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchByHashTag.this);

        builder1.setTitle(title);
        String _name = "File Stored In " + "<b>" + path + "</b>";
        builder1.setMessage(Html.fromHtml(_name));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void doSocialShare(int pos, Uri photoURI, String title) {
        try {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
//            Uri uri = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/google_play_with_paintology");
//
//            ArrayList<Uri> files = new ArrayList<Uri>();
//            files.add(photoURI);
//            files.add(uri);
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Paintology Community " + title);
//            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sent_post));
//            shareIntent.setType("*/*");
//            Intent receiver = new Intent(CommunityDetail.this, receiverForShare.class);
//            receiver.putExtra("test", "test");
//            PendingIntent pendingIntent;// = PendingIntent.getBroadcast(CommunityDetail.this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                pendingIntent = PendingIntent.getBroadcast(this,
//                        0,
//                        receiver,
//                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//            } else {
//                pendingIntent = PendingIntent.getBroadcast(this,
//                        0,
//                        receiver,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//
//            Intent chooser;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                chooser = Intent.createChooser(shareIntent, "Share To", pendingIntent.getIntentSender());
//            } else {
//                chooser = Intent.createChooser(shareIntent, "Share To");
//            }
//            startActivity(chooser);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title);
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, " Paintology community share");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this image that I thought you might like. It’s from the app " + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            startActivity(Intent.createChooser(shareIntent, "Share Image and Text"));

            shareRewardPoint(posts.get(pos).getPost_id());
        } catch (Exception e) {
            Log.e(PaintActivity.class.getName(), e.getMessage());
        }
    }

    public static class shareFromSearch extends BroadcastReceiver {

        public shareFromSearch() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGG", "OnReceived Called");
            try {
                for (String key : intent.getExtras().keySet()) {
                    Log.e(getClass().getSimpleName(), " " + intent.getExtras().get(key));
                    String _app_name = " " + intent.getExtras().get(key);
//                    Log.e("TAGGG", " " + intent.getExtras().get(key));
                    String shareFileVia = "";

                    if (_app_name.contains("skype"))
                        shareFileVia = "skype";
                    else if (_app_name.contains("apps.photos"))
                        shareFileVia = "photos";
                    else if (_app_name.contains("android.gm"))
                        shareFileVia = "gmail";
                    else if (_app_name.contains("apps.docs"))
                        shareFileVia = "drive";
                    else if (_app_name.contains("messaging"))
                        shareFileVia = "messages";
                    else if (_app_name.contains("android.talk"))
                        shareFileVia = "hangout";
                    else if (_app_name.contains("xender"))
                        shareFileVia = "xender";
                    else if (_app_name.contains("instagram"))
                        shareFileVia = "instagram";
                    else if (_app_name.contains("youtube"))
                        shareFileVia = "youtube";
                    else if (_app_name.contains("maps"))
                        shareFileVia = "maps";
                    else if (_app_name.contains("bluetooth"))
                        shareFileVia = "bluetooth";
                    else if (_app_name.contains("facebook"))
                        shareFileVia = "facebook";
                    else if (_app_name.contains("whatsapp"))
                        shareFileVia = "whatsapp";
                    else if (_app_name.contains("com.facebook.orca"))
                        shareFileVia = "facebook_messager";
                    else if (_app_name.contains("linkedin"))
                        shareFileVia = "linkedin";
                    else if (_app_name.contains("sketch"))
                        shareFileVia = "sketchapp";
                    else if (_app_name.contains("mail.compose")) {
                        shareFileVia = "email";
                    } else
                        shareFileVia = _app_name;

                    Log.e("TAGGG", "share_image_via " + shareFileVia + " ");
                    if (BuildConfig.DEBUG) {

                        Toast.makeText(context, "community_share_image_via_" + shareFileVia, Toast.LENGTH_SHORT).show();
                    }

                    FirebaseUtils.logEvents(context, "community_share_image_via_" + shareFileVia);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception while share image " + e.getMessage(), e);
            }
        }
    }


    @Override
    public void reportPost(int pos) {


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.report_icon);
        builderSingle.setTitle("Report this post");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Its Spam");
        arrayAdapter.add("Not Appropriate");


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sendSpamReport(pos, posts.get(pos).getPost_id(), arrayAdapter.getItem(which));
                } catch (Exception e) {
                    Log.e("TAG", "Exception " + e.getMessage());
                }
            }
        });
        builderSingle.show();
    }


    void sendSpamReport(int pos, String postId, String msg) {

        /*HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody post_id = RequestBody.create(MediaType.parse("text/plain"), postId);
        RequestBody req_msg = RequestBody.create(MediaType.parse("text/plain"), msg);
        _map.put("post_id", post_id);
        _map.put("reports", req_msg);

        Observable<ResponseBase> _observalbe = apiInterface.reportToPost(ApiClient.SECRET_KEY, _map);
        _observalbe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBase>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBase responseBase) {
                if (responseBase != null) {
                    if (responseBase.getData()) {
                        _userPostList.getObjData().getPost_list().remove(pos);
                        mAdapter.notifyItemRemoved(pos);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(SearchByHashTag.this);
                        builderInner.setTitle("Thanks for reporting this post");
                        builderInner.setMessage("Your feedback is important in helping us keep the Paintology community safe.");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });*/
    }

    @Override
    public void view_all_comment(int pos) {

//        final Dialog dialog = new Dialog(SearchByHashTag.this);
//        dialog.setContentView(R.layout.view_all_comment_layout);
//
//        ImageView iv_profile = (ImageView) dialog.findViewById(R.id.iv_post_user);
//        TextView tv_post_user = (TextView) dialog.findViewById(R.id.tv_post_username);
//        TextView tv_date_time = (TextView) dialog.findViewById(R.id.tv_date_time);
//
//        CircleProgressBar p_bar = (CircleProgressBar) dialog.findViewById(R.id.progress_bar);
//
//        RecyclerView rv_comment = (RecyclerView) dialog.findViewById(R.id.rv_comment_list);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
//        rv_comment.setLayoutManager(mLayoutManager);
//        try {
//            tv_post_user.setText(_userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserName());
//            tv_date_time.setText("Posted At " + _userPostList.getObjData().getPost_list().get(pos).getPost_date_time());
//
//            Glide.with(SearchByHashTag.this)
//                    .load(_userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserProfilePic())
//                    .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                    .into(iv_profile);
//        } catch (Exception e) {
//        }
//
//
//        String postId = postList.get(pos).getPost_id();
//        Log.e("TAGG", "Post ID for Comment " + postId);
//
//        ArrayList<AllCommentModel.data.all_comments> list = new ArrayList<AllCommentModel.data.all_comments>();
//        CommentListAdapter _adapter = new CommentListAdapter(SearchByHashTag.this, list, this, this);
//        rv_comment.setAdapter(_adapter);
//        commentPage = 1;
//        commentList(postId, commentPage + "", p_bar, _adapter, list);
//
//        rv_comment.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//            @Override
//            public void onLoadMore() {
//                Log.e("TAGGG", "OnLoad More Called");
//                if (list.size() < totalComment) {
//                    commentPage = commentPage + 1;
//                    commentList(postId, commentPage + "", p_bar, _adapter, list);
//                } else
//                    Log.e("TAGG", "OnLoad All Loaded");
//            }
//        });
//
////        AllCommentModel allCommentModel = commentList(postId, pageNumber, p_bar);
////
////        CommentListAdapter _adapter = new CommentListAdapter(SearchByHashTag.this, allCommentModel.getMainData().getComment_lists());
////        rv_comment.setAdapter(_adapter);
//        dialog.show();

        CommunityCommentsBottomsheet communityCommentsBottomsheet = CommunityCommentsBottomsheet.newInstance(posts.get(pos).getPost_id().toString());
        communityCommentsBottomsheet.show(getSupportFragmentManager(), "CommunityDetail");
    }


    int commentPage = 1;
    int totalComment;

    void commentList(String postId, String pageNumber, CircleProgressBar p_bar, final CommentListAdapter _adapter, ArrayList<AllCommentModel.data.all_comments> list) {


        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody post_id = RequestBody.create(MediaType.parse("text/plain"), postId);
        RequestBody req_page_number = RequestBody.create(MediaType.parse("text/plain"), pageNumber.trim());
        RequestBody req_page_size = RequestBody.create(MediaType.parse("text/plain"), 10 + "");

        _map.put("post_id", post_id);
        _map.put("pagenumber", req_page_number);
        _map.put("size", req_page_size);

        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Log.e("TAGGG", "commentList data postId " + postId + " pageNumber " + pageNumber);
        Observable<AllCommentModel> _observable = apiInterface.getAllComment(ApiClient.SECRET_KEY, _map);
        p_bar.setVisibility(View.VISIBLE);
        _observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<AllCommentModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AllCommentModel allCommentModel) {
                try {
                    totalComment = (Integer.parseInt(allCommentModel.getMainData().getTotal_comments()));
                    if (allCommentModel != null && allCommentModel.getMainData() != null) {
                        for (int i = 0; i < allCommentModel.getMainData().getComment_lists().size(); i++) {
                            list.add(allCommentModel.getMainData().getComment_lists().get(i));
                            _adapter.notifyItemInserted(i);
                        }
                    }
                    _adapter.notifyDataSetChanged();
                    p_bar.setVisibility(View.GONE);
                } catch (Exception e) {
                    p_bar.setVisibility(View.GONE);
                    Log.e("TAGG", "Exception at set comment " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {
                p_bar.setVisibility(View.GONE);
                Log.e("TAGGG", "OnError Called " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {
                p_bar.setVisibility(View.GONE);

            }
        });
    }


    @Override
    public void seachByHashTag(String tag) {
        mAdapter.removeAllData();
        mAdapter.notifyDataSetChanged();
        hashTag = tag;
        showProgress("Retrieving more results with " + hashTag);
        System.out.println("Retrieving more results with " + hashTag);
        pageNumber = 1;
        lastVisibleDocument = null;
//        getList(hashTag, true);
        posts.clear();
        fetchPostsWithTag(hashTag);
        StringTokenizer tokenizer = new StringTokenizer(hashTag, "|");
//        tv_title.setText(hashTag.replace("|", " "));
        if (tokenizer != null) {
            if (tokenizer.countTokens() > 1)
                setTitle(tokenizer.nextToken() + ",...");
            else
                setTitle(tokenizer.nextToken());
        }
    }

    @Override
    public void viewProfile(int pos) {
        if (AppUtils.isLoggedIn()) {
            FireUtils.openProfileScreen(this, posts.get(pos).getUser_id());
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void enlargeImageView(String _url) {
        if (BuildConfig.DEBUG) {

            Toast.makeText(SearchByHashTag.this, constants.double_tap_image_community, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(SearchByHashTag.this, constants.double_tap_image_community);

        try {
            ContextKt.showEnlargeImage(SearchByHashTag.this, _url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*fm_image.setVisibility(View.VISIBLE);
        tv_back.setVisibility(View.VISIBLE);
        Glide.with(SearchByHashTag.this)
                .load(_url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(iv_enlarge_image);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_animation);
        iv_enlarge_image.startAnimation(animation1);*/

        Toast.makeText(this, "pinch to zoom!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
//        disableAllView(ll_1);
//        disableAllView(ll_2);
//        disableAllView(ll_3);
//        disableAllView(ll_4);
//        switch (view.getId()) {
//            case R.id.ll_1: {
//                enableAllView(ll_1);
//                changeListFormat(1);
//            }
//            break;
//            case R.id.ll_2: {
//                enableAllView(ll_2);
//                changeListFormat(2);
//            }
//            break;
//            case R.id.ll_3: {
//                enableAllView(ll_3);
//                changeListFormat(3);
//            }
//            break;
//            case R.id.ll_4: {
//                enableAllView(ll_4);
//                changeListFormat(4);
//            }
//            break;
//        }

    }

    void disableAllView(LinearLayout view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            if (view.getChildAt(i) instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) view.getChildAt(i);
                {
                    for (int j = 0; j < ll.getChildCount(); j++) {
                        View view1 = (View) ll.getChildAt(j);
                        view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_square_radious));
                    }
                }
            } else {
                View view1 = (View) view.getChildAt(i);
                view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_square_radious));
            }
        }
    }

    void enableAllView(LinearLayout view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            if (view.getChildAt(i) instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) view.getChildAt(i);
                {
                    for (int j = 0; j < ll.getChildCount(); j++) {
                        View view1 = (View) ll.getChildAt(j);
                        view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_selected));
                    }
                }
            } else {
                View view1 = (View) view.getChildAt(i);
                view1.setBackground(getResources().getDrawable(R.drawable.rounded_white_selected));
            }
        }
    }

    public boolean changeListFormat(int rowCount) {

        if (mLayoutManager != null && rv_community_detail != null) {
            mLayoutManager = new GridLayoutManager(SearchByHashTag.this, rowCount, RecyclerView.VERTICAL, false);
            rv_community_detail.setLayoutManager(mLayoutManager);

            int currentPos = mAdapter.getLastPosition();
            mAdapter.notifyList(rowCount);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rv_community_detail.scrollToPosition(currentPos);
                }
            });
            return true;
        }
        return false;
    }


//    private void fetchPostsWithTag(String tag) {
//        CollectionReference postsRef = db_firebase.collection("community_posts");
//
//        postsRef.whereArrayContains("tags", tag)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("SearchByHashTag", "Query successful, number of documents: " + task.getResult().size());
////                            postList.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                CommunityPost post = document.toObject(CommunityPost.class);
////                                postList.add(post);
//                                Log.d("SearchByHashTag", "Post added: " + post.getTitle());
//                            }
////                            postAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d("SearchByHashTag", "Error getting documents: ", task.getException());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("SearchByHashTag", "Error getting documents: ", e);
//                    }
//                });
//
//
//    }

    private void fetchPostsWithTag(String tag) {
        final String TAG_FILTER = tag;
        String tagName = tag.replace("#", "");
        tagName = tagName.replace(" ", "");
        fetchCommunityPostsFromFirebaseFunctions(tagName);

    }

    void fetchCommunityPostsFromFirebaseFunctions(String tag) {
        Log.d("CommunityPosts", "page : " + pageNumber);
        Log.d("CommunityPosts", "tag :" + tag);
        Map<String, Object> data = new HashMap<>();
        data.put("page", pageNumber);
        data.put("per_page", 20);
        data.put("q", tag);

        isLoading = true;
        FirebaseFunctions.getInstance().getHttpsCallable("communityPost-list").call(data).continueWith(new Continuation<HttpsCallableResult, Map<String, Object>>() {
            @Override
            public Map<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Get the result as a Map
                Log.d("CommunityPosts", "Post Data: " + task.getResult().getData());
                return (Map<String, Object>) task.getResult().getData();
            }
        }).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> result = task.getResult();
                    // Log the raw data
                    if (result != null && result.containsKey("data")) {
                        List<Map<String, Object>> posts = (List<Map<String, Object>>) result.get("data");
                        if (posts != null) {
                            List<CommunityPost> listOfPosts = new ArrayList<>();
                            for (Map<String, Object> post : posts) {
                                Gson gson = new Gson();
                                Log.d("CommunityPosts", "Post Data By One By One: " + gson.toJson(post));
                                if (post != null) {
                                    listOfPosts.add(new CommunityPost(post));
                                }
                                Log.d("CommunityPosts", "Post Data By One By One: " + gson.toJson(listOfPosts));
                                if (!posts.isEmpty() && post.equals(posts.get(posts.size() - 1))) {
                                    Log.d("CommunityPosts", "This is the last added post");
                                }
                            }
                            isLoading = false;
                            if (pageNumber == 1) {
                                SearchByHashTag.this.posts = listOfPosts;
                                fillRecyclerView(listOfPosts);
                            } else {
                                mAdapter.addNewData(listOfPosts);
                            }
                            item_progress_bar.setVisibility(View.GONE);
                            if (swipe_refresh.isRefreshing()) {
                                swipe_refresh.setRefreshing(false);
                            }
                        }
                    } else {
                        isLoading = false;
                        Log.e("CommunityPosts", "No data found in the response");
                    }
                } else {
                    isLoading = false;
                    Exception e = task.getException();
                    hideProgress();

                    // Handle the error
                    Log.e("CommunityPosts", "Error fetching community posts", e);
                    Toast.makeText(SearchByHashTag.this, "Error fetching community posts", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void fillRecyclerView(List<CommunityPost> lst) {
        try {

            try {
                if (lst.isEmpty()) {
                    tv_empty_msg.setVisibility(View.VISIBLE);
                } else
                    tv_empty_msg.setVisibility(View.GONE);
            } catch (Exception e) {

            }

            try {
                for (int i = 0; i < lst.size(); i++) {
                    if (lst.get(i).getTags() != null && !lst.get(i).getTags().isEmpty()) {
                        try {
                            String fileName = lst.get(i).getImages().getContent().substring(lst.get(i).getImages().getContent().lastIndexOf('/') + 1);
                            File imageFile = new File(KGlobal.getDownloadPath(SearchByHashTag.this), fileName);
                            if (imageFile.exists()) {
                                posts.get(i).setDownloaded(true);
                            }
                        } catch (Exception e) {
                        }
                    }
                }


//            mAdapter = new ShowCommunityListAdapter(lst, SearchByHashTag.this, this, this);
                mAdapter = new CommunityPostAdapter(lst, SearchByHashTag.this, this, this, false);
                int type = 1;
                try {
                    type = (constants.getInt(constants.formatType, SearchByHashTag.this) == 0 ? 1 : constants.getInt(constants.formatType, SearchByHashTag.this));
                } catch (Exception e) {

                }


//                mAdapter = new CommunityPostAdapter(lst, SearchByHashTag.this, this, this);
                mLayoutManager = new GridLayoutManager(SearchByHashTag.this, 4, RecyclerView.VERTICAL, false);
                rv_community_detail.setLayoutManager(mLayoutManager);
                rv_community_detail.addItemDecoration(new MarginDecoration(SearchByHashTag.this));
//                rv_community_detail.setHasFixedSize(true);
                rv_community_detail.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                changeListFormat(1);

                if (type == 1)
                    Toast.makeText(SearchByHashTag.this, "double tap to enlarge!", Toast.LENGTH_SHORT).show();
//            mAdapter.notifyDataSetChanged();

                runOnUiThread(() -> rv_community_detail.scrollToPosition(0));
//                showPlus_tooltip(ll_temp, frm_main);

            } catch (Exception e) {
                Log.e("TAGGG", "Exception at fill data " + e.getMessage(), e);
            }


        } catch (Exception e) {
            Log.e("TAGGG", "Exception at fillrecyclerview " + e.getMessage(), e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginUpdateEvent(UserLoginUpdateEvent event) {
        Log.e("SearchByHashTag", "onUserLoginUpdateEvent");
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAuthenticating)
            return;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        isLoginInPaintology = constants.getString(constants.LoginInPaintology, this);
        Log.e("TAGG", "Profile Url From Prefe " + constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this));
        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
            if (constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this).isEmpty()) {
                int placeHolder;
                if (constants.getString(constants.UserGender, SearchByHashTag.this).equalsIgnoreCase(constants.MALE)) {
                    placeHolder = R.drawable.profile_icon;
                } else if (constants.getString(constants.UserGender, SearchByHashTag.this).equalsIgnoreCase(constants.FEMALE)) {
                    placeHolder = R.drawable.profile_icon;
                } else {
                    placeHolder = R.drawable.profile_icon;
                }

                if (actionProfile != null) {
                    actionProfile.setIcon(placeHolder);
                }
                Glide.with(SearchByHashTag.this)
                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
                        .apply(RequestOptions.circleCropTransform().placeholder(placeHolder))
                        .error(placeHolder)
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                actionProfile.setIcon(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

            } else {
                int placeHolder;
                if (constants.getString(constants.UserGender, SearchByHashTag.this).equalsIgnoreCase(constants.MALE)) {
                    placeHolder = R.drawable.profile_icon;
                } else if (constants.getString(constants.UserGender, SearchByHashTag.this).equalsIgnoreCase(constants.FEMALE)) {
                    placeHolder = R.drawable.profile_icon;
                } else {
                    placeHolder = R.drawable.profile_icon;
                }
//                Glide.with(SearchByHashTag.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
//                        .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(iv_profile);
                if (actionProfile != null) {
                    actionProfile.setIcon(placeHolder);
                }

                Glide.with(SearchByHashTag.this)
                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
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
            int placeHolder = R.drawable.profile_icon;
            if (actionProfile != null) {
                actionProfile.setIcon(placeHolder);
            }
            Glide.with(SearchByHashTag.this)
                    .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
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

        fetchProfileData();
    }


    int detailViewPosition = 0;

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        /*if (BuildConfig.DEBUG) {
            Toast.makeText(SearchByHashTag.this, constants.click_community_user_image, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_user_image);
        Community.obj_cmunity.DisableAllView(1);
        changeListFormat(1);
        runOnUiThread(() -> rv_community_detail.scrollToPosition(pos));
        detailViewPosition = pos;
        increaseCounter(posts.get(pos).getPost_id());*/
        Intent _intent = new Intent(this, CommunityDetail.class);
        _intent.putExtra("post_id", posts.get(pos).getPost_id());
        startActivity(_intent);
    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {

    }

    @Override
    public void setUserID(String _id) {

    }

    @Override
    public void showHashTagHint() {

    }


    void increaseCounter(String postId) {

        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody post_id = RequestBody.create(MediaType.parse("text/plain"), postId);
        _map.put("post_id", post_id);

        Observable<ResponseIncreaseCounter> _observalbe = apiInterface.increaseViewCounter(ApiClient.SECRET_KEY, _map);

        _observalbe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseIncreaseCounter>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseIncreaseCounter responseIncreaseCounter) {
                Log.e("TAGGG", "OnNext " + responseIncreaseCounter.getResponse());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGGG", "onError " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void showHideFab(boolean needToShown) {

    }

    @Override
    public void openChatsScreen() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(
                    SearchByHashTag.this,
                    constants.user_chats,
                    Toast.LENGTH_SHORT
            ).show();
        }
        FirebaseUtils.logEvents(SearchByHashTag.this, constants.user_chats);
        Intent intent = new Intent(
                SearchByHashTag.this,
                ChatUserList.class
        );
        startActivity(intent);
    }

    @Override
    public void openUsersPostsListScreen(int pos) {
        Intent _intent = new Intent(SearchByHashTag.this, CommunityDetail.class);
        _intent.putExtra("user_id", posts.get(pos).getUser_id());
        _intent.putExtra("user_name", posts.get(pos).getAuthor().getName());
        startActivity(_intent);
    }

    @Override
    public void openChatScreen(String key, String user_id, int position) {
        new ChatUtils(this).openChatScreen(key, user_id);
    }


    @Override
    public ArrayList<Firebase_User> getFirebaseUserList() {

        return _lst_firebase_user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.community_menu, menu);
//        actionProfile = menu.findItem(R.id.action_profile);
//        menu.findItem(R.id.action_chat).setVisible(false);
//        menu.findItem(R.id.action_share_paintology).setVisible(false);
//        setupProfileIcon();
        return super.onCreateOptionsMenu(menu);
    }

    private void setupProfileIcon() {

        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
            if (constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this) != null && !constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this).isEmpty()) {
                Log.e("TAGGG", "Profile Image Set from OnCreate");
//                Glide.with(SearchByHashTag.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
//                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(iv_profile);

                Glide.with(SearchByHashTag.this)
                        .load(constants.getString(constants.ProfilePicsUrl, SearchByHashTag.this))
                        .apply(RequestOptions.circleCropTransform())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                actionProfile.setIcon(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }

    }

    private void commentRewardPoint(String postId) {
         /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_comment", rewardSetup.getCommunity_post_comment() == null ? 0 : rewardSetup.getCommunity_post_comment(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void likeRewardPoint(String postId) {

       /*  if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_like", rewardSetup.getCommunity_post_like() == null ? 0 : rewardSetup.getCommunity_post_like(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void shareRewardPoint(String postId) {
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_share", rewardSetup.getCommunity_post_share() == null ? 0 : rewardSetup.getCommunity_post_share(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_profile:
                if (AppUtils.isLoggedIn()) {
                    FireUtils.openProfileScreen(this, null);
                } else {
                    Intent intent = new Intent(SearchByHashTag.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
//            case R.id.action_chat:
//                FirebaseUtils.logEvents(SearchByHashTag.this, constants.chat_community_header_click);
//
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                isLoggedIn = accessToken != null && !accessToken.isExpired();
//                account = GoogleSignIn.getLastSignedInAccount(SearchByHashTag.this);
//                isLoginInPaintology = constants.getString(constants.LoginInPaintology, SearchByHashTag.this);
//
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true")))
//                    startActivity(new Intent(SearchByHashTag.this, ChatUserList.class));
//                else {
//                    showLoginDialog(DialogType.CHAT);
//                }
//                return true;
            case R.id.action_add_post:
                if (BuildConfig.DEBUG) {

                    Toast.makeText(SearchByHashTag.this, constants.click_community_post_menu, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_post_menu);
                if (!KGlobal.isInternetAvailable(SearchByHashTag.this)) {
                    Toast.makeText(SearchByHashTag.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                    Intent intent = new Intent(SearchByHashTag.this, Gallery.class);
                    intent.putExtra("title", "New Post");
                    intent.putExtra("mode", 1);
                    intent.putExtra("maxSelection", 500);
                    intent.putExtra("isFromNewPost", true);
                    startActivity(intent);
                } else {
                    if (BuildConfig.DEBUG) {

                        Toast.makeText(SearchByHashTag.this, constants.open_social_login_search_hashtag_new_post_dialog, Toast.LENGTH_SHORT).show();
                    }

                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.open_social_login_search_hashtag_new_post_dialog);
                    showLoginDialog();
                }
                return true;
            case R.id.action_view_mode:
                View view = getLayoutInflater().inflate(R.layout.view_mode_dialog, null, false);

                LinearLayout viewModeNormal = view.findViewById(R.id.view_mode_normal);
                LinearLayout viewMode2n = view.findViewById(R.id.view_mode_2n);
                LinearLayout viewMode3n = view.findViewById(R.id.view_mode_3n);
                LinearLayout viewMode4n = view.findViewById(R.id.view_mode_4n);

                viewModeNormal.setOnClickListener(v -> {
//                    ll_1.performClick();

//                    enableAllView(ll_1);
                    changeListFormat(1);
//
//                    if (MainCollectionFragment.objHomeInterface.changeListFormat(1)) {
//                        if (currentType != 1 && currentType != 5) {
//                            _indexes.add(1);
//                            currentType = 1;
////                                            disableAllView();
////                                            enableAllView(ll_1);
//                        }
//                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.community_1x1_selection);
//                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode2n.setOnClickListener(v -> {
//                    ll_2.performClick();

//                    enableAllView(ll_2);
                    changeListFormat(2);

//                    if (MainCollectionFragment.objHomeInterface.changeListFormat(2)) {
//                        if (currentType != 2) {
//                            _indexes.add(2);
//                            currentType = 2;
////                                            disableAllView();
////                                            enableAllView(ll_2);
//                        }
//                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.community_2x2_selection);
//                    }

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode3n.setOnClickListener(v -> {
//                    ll_3.performClick();

//                    if (MainCollectionFragment.objHomeInterface.changeListFormat(3)) {
//
//                        if (currentType != 3) {
//                            _indexes.add(3);
//                            currentType = 3;
////                                            disableAllView();
////                                            enableAllView(ll_3);
//                        }
//                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.community_3x2_selection);
//                    }

//                    enableAllView(ll_3);
                    changeListFormat(3);

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                viewMode4n.setOnClickListener(v -> {
//                    ll_4.performClick();

//                    if (MainCollectionFragment.objHomeInterface.changeListFormat(4)) {
//                        if (currentType != 4) {
//                            _indexes.add(4);
//                            currentType = 4;
////                                            disableAllView();
////                                            enableAllView(ll_4);
//                        }
//                        FirebaseUtils.logEvents(SearchByHashTag.this, constants.community_4x2_selection);
//                    }

//                    enableAllView(ll_4);
                    changeListFormat(4);

                    if (alertDialogViewModel != null) {
                        alertDialogViewModel.dismiss();
                    }

                });

                AlertDialog.Builder builder = new AlertDialog.Builder(SearchByHashTag.this);
                builder.setView(view);
                alertDialogViewModel = builder.create();

                alertDialogViewModel.show();
                return true;
//            case R.id.action_share_paintology:
//                try {
//                    FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_share_paintology);
//                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                    share.setType("text/plain");
//                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//
//                    // Add data to the intent, the receiving app will decide
//                    // what to do with it.
//                    share.putExtra(Intent.EXTRA_SUBJECT, "Paintology - great little app you should check out");
//                    share.putExtra(Intent.EXTRA_TEXT, "I found this free app called Paintology, I think you will love it, check it out!\n\nhttps://play.google.com/store/apps/details?id=com.paintology.lite");
//
//                    startActivity(Intent.createChooser(share, "Share link!"));
//                } catch (Exception e) {
//                    Log.e("TAG", "Exception at share " + e.getMessage());
//                }
//                return true;
            case R.id.action_feedback:
                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_feedback);
                showFeedbackDialog();
                return true;
            case R.id.action_goto_playstore:
                FirebaseUtils.logEvents(SearchByHashTag.this, constants.click_community_menu_googleplay_click);
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

    @Override
    public void downloadImageOpenInOverlayCanvas(int pos) {
        try {

            boolean isStoragePassed = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(
                        SearchByHashTag.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(
                        SearchByHashTag.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(SearchByHashTag.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(SearchByHashTag.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(SearchByHashTag.this, 1);
                return;
            }

            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(SearchByHashTag.this), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(SearchByHashTag.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel, true, pos, false, true).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    @Override
    public void downloadImageOpenInTraceCanvas(int pos) {
        try {

            boolean isStoragePassed = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(
                        SearchByHashTag.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(
                        SearchByHashTag.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(SearchByHashTag.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(SearchByHashTag.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(SearchByHashTag.this, 1);
                return;
            }

            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(SearchByHashTag.this), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(SearchByHashTag.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel, true, pos, false, true).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }
}