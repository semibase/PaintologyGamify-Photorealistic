package com.paintology.lite.photorealistic.drawing.Community;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Adapter.CommentListAdapter;
import com.paintology.lite.photorealistic.drawing.Adapter.CommunityPostAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Data;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.MyResponse;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Sender;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Chat.RealTimeDBUtils;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.bottomsheet.CommunityCommentsBottomsheet;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.Model.GetUserProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.ResponseIncreaseCounter;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


public class CommunityDetail extends AppCompatActivity implements home_fragment_operation, PostOperation, GoogleApiClient.OnConnectionFailedListener {


    ApiInterface apiInterface;
    StringConstants constants = new StringConstants();
    RecyclerView rv_community_detail;
    String userID;
    GridLayoutManager mLayoutManager;
    CommunityPostAdapter mAdapter;
    CircleProgressBar item_progress_bar;

    public static home_fragment_operation objHomeInterface;
    ProgressDialog progressDialog = null;

    int pageNumber = 1;
    int totalItem = 0;

    SwipeRefreshLayout swipe_refresh;
    boolean isLoading = false;

    public static PostOperation _post_operation;

    LinearLayout ll_temp;

    HashMap<String, String> hashMap = new HashMap<>();
    FrameLayout frm_main;
    FirebaseFirestore db_firebase;
    private DocumentSnapshot lastVisibleDocument;
    final int BATCH_SIZE = 10;
    List<CommunityPost> posts = new ArrayList<>();
    private boolean isLastPage = false;
    String _user_id = "";
    String username = "";
    String post_id = "";
    String hashTag = "";

//    ImageView iv_profile, iv_plus, iv_menu;

    RelativeLayout fm_image;
    ImageView iv_enlarge_image;
    ImageView tv_back;


    //    TextView tv_name;
    boolean isLoggedIn;
    GoogleSignInAccount account;
    String LoginInPaintology;
    OperationAfterLogin _operationLogin = null;
    LoginButton facebook_login_btn;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 7;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    GoogleApiClient googleApiClient;

    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String isLoginInPaintology;

    RealTimeDBUtils realTimeDBUtils;
    ApiInterface apiService;
    private AlertDialog alertDialogViewModel = null;
    private MenuItem actionProfile;
    String TAG = "CommunityDetail";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);
        objHomeInterface = this;
        _post_operation = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);


        apiService = ApiClient.getClientNotification().create(ApiInterface.class);
        realTimeDBUtils = MyApplication.get_realTimeDbUtils(this);

        facebook_login_btn = (LoginButton) findViewById(R.id.login_button);

        db_firebase = FirebaseFirestore.getInstance();
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


        googleApiClient = new GoogleApiClient.Builder(CommunityDetail.this)
                .enableAutoManage(CommunityDetail.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(CommunityDetail.this, gso);

        iv_enlarge_image = (ImageView) findViewById(R.id.iv_enlarge_image);
        fm_image = (RelativeLayout) findViewById(R.id.fm_image);

//        iv_profile = (ImageView) findViewById(R.id.iv_profile_icon);
//        iv_plus = (ImageView) findViewById(R.id.iv_post_image);
//        iv_menu = (ImageView) findViewById(R.id.iv_header_menu);
//
//        iv_profile.setVisibility(View.GONE);
//        iv_plus.setVisibility(View.GONE);
//        iv_menu.setVisibility(View.GONE);

//        ll_temp = findViewById(R.id.ll_temp);
//        frm_main = findViewById(R.id.frm_main);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent);

        rv_community_detail = findViewById(R.id.rv_community_detail);
        rv_community_detail.addItemDecoration(new MarginDecoration(CommunityDetail.this));


        item_progress_bar = findViewById(R.id.item_progress_bar);
        userID = constants.getString(constants.UserId, CommunityDetail.this);
//        followUser("");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        getAllUser();


        if (getIntent() != null && getIntent().hasExtra("hashtag")) {
            hashTag = getIntent().getStringExtra("hashtag");
            Log.d("CommunityPosts", "hashTag : " + hashTag);
            setTitle(hashTag);
            fetchPostsWithTag(hashTag);
        } else if (getIntent() != null && getIntent().hasExtra("post_id")) {
            setTitle("Post Detail");
            post_id = getIntent().getStringExtra("post_id");
            swipe_refresh.setEnabled(false);
            startPostDetails();
        } else {
            setTitle("Posts");
            if (getIntent() != null && getIntent().hasExtra("user_name")) {
                username = getIntent().getStringExtra("user_name");
                Log.i(TAG, "username: " + username);
                setTitle("Posts - " + username);
            }
            if (getIntent() != null && getIntent().hasExtra("user_id")) {
                _user_id = getIntent().getStringExtra("user_id");
                Log.i(TAG, "_user_id: " + _user_id);
                fetchPostsWithUser(_user_id);
            }
            startWork();
        }

    }

    public void setTitle(int count) {
        if (!username.equalsIgnoreCase(""))
            setTitle(getResources().getString(R.string.user_post_title, count, username));
        else if (!hashTag.equalsIgnoreCase(""))
            setTitle(getResources().getString(R.string.user_post_title, count, hashTag));
    }

    public void startPostDetails() {
        HashMap<String, String> filters = new HashMap<>();
        filters.put("filter_by", "id:=" + post_id);

        HashMap<String, String> sorts = new HashMap<>();
        sorts.put("sort_by", "created_at:desc");

        FirebaseFirestoreApi.fetchCommunityList(1, 1, filters, sorts).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getData();

                if (data != null && data.containsKey("data") && data.get("data") != null) {
                    List<Map<String, Object>> posts = (List<Map<String, Object>>) data.get("data");
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

                        hideProgress();
                        item_progress_bar.setVisibility(View.GONE);
                        isLoading = false;
                        if (pageNumber == 1) {
                            CommunityDetail.this.posts = listOfPosts;
                            fillRecyclerView(listOfPosts);
                        } else {
                            mAdapter.addNewData(listOfPosts);
                        }
                        if (swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        return;
                    }
                }
                hideProgress();
                item_progress_bar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("CommunityPosts", "No data found in the response");
            } else {
                isLoading = false;
                Exception e = task.getException();
                hideProgress();
                item_progress_bar.setVisibility(View.GONE);
                // Handle the error
                Log.e("CommunityPosts", "Error fetching community posts", e);
                Toast.makeText(CommunityDetail.this, "Error fetching community posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startWork() {

        if (KGlobal.isInternetAvailable(CommunityDetail.this)) {
            item_progress_bar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(CommunityDetail.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
        }

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
                            fetchPostsWithUser(_user_id);
                        }
                    }
                }

            }
        });
        swipe_refresh.setOnRefreshListener(() -> {
            pageNumber = 1;
            lastVisibleDocument = null;
            swipe_refresh.setRefreshing(true);
            fetchPostsWithUser(_user_id);
        });
    }


    int currentPos;

    @Override
    public boolean changeListFormat(int rowCount) {
        if (mLayoutManager != null && rv_community_detail != null) {

            currentPos = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (currentPos == -1)
                currentPos = mLayoutManager.findFirstVisibleItemPosition();

            mLayoutManager = new GridLayoutManager(CommunityDetail.this, rowCount, RecyclerView.VERTICAL, false);
            rv_community_detail.setLayoutManager(mLayoutManager);
            mAdapter.notifyList(rowCount);
            runOnUiThread(() -> {
                if (detailViewPosition != 0) {
                    rv_community_detail.scrollToPosition(detailViewPosition);
                    detailViewPosition = 0;
                } else
                    rv_community_detail.scrollToPosition(currentPos);
            });
            return true;
        }
        return false;
    }

    private void fetchPostsWithUser(String tag) {
        final String TAG_FILTER = tag;
        String tagName = tag.replace("#", "");
        fetchCommunityPostsFromFirebaseFunctions(tagName);
    }

    private void fetchPostsWithTag(String tag) {
        String tagName = tag.replace("#", "");
        tagName = tagName.replace(" ", "");
        fetchCommunityPostsFromFirebaseFunctions(tagName);
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
                        Firebase_User _user;
                        try {
                            _user = postSnapshot.getValue(Firebase_User.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;

                        }
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

    void fetchCommunityPostsFromFirebaseFunctions(String tag) {
        Log.d("CommunityPosts", "page : " + pageNumber);
        Log.d("CommunityPosts", "name : " + tag);
        Map<String, Object> data = new HashMap<>();
        data.put("page", pageNumber);
        data.put("per_page", 10);
        if (hashTag.equalsIgnoreCase("")) {
            data.put("q", "");
            data.put("filter_by", "author.user_id:" + tag);
        } else {
            data.put("q", tag);
        }

        Log.d("CommunityPosts", "data : " + new Gson().toJson(data));
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
                    if (result != null && result.containsKey("page")) {
                        Map<String, Object> page = (Map<String, Object>) result.get("page");
                        if (page != null && page.containsKey("total_elements")) {
                            Integer count = (Integer) page.get("total_elements");
                            if (count != null)
                                setTitle(count);
                        }
                    }

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
                            hideProgress();
                            item_progress_bar.setVisibility(View.GONE);
                            isLoading = false;
                            if (pageNumber == 1) {
                                CommunityDetail.this.posts = listOfPosts;
                                fillRecyclerView(listOfPosts);
                            } else {
                                mAdapter.addNewData(listOfPosts);
                            }
                            if (swipe_refresh.isRefreshing()) {
                                swipe_refresh.setRefreshing(false);
                            }
                        }
                    } else {
                        hideProgress();
                        item_progress_bar.setVisibility(View.GONE);
                        isLoading = false;
                        Log.e("CommunityPosts", "No data found in the response");
                    }
                } else {
                    isLoading = false;
                    Exception e = task.getException();
                    hideProgress();
                    item_progress_bar.setVisibility(View.GONE);

                    // Handle the error
                    Log.e("CommunityPosts", "Error fetching community posts", e);
                    Toast.makeText(CommunityDetail.this, "Error fetching community posts", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    int detailViewPosition = 0;

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {

     /*   if (BuildConfig.DEBUG) {
            Toast.makeText(CommunityDetail.this, constants.click_community_user_image, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(CommunityDetail.this, constants.click_community_user_image);
        Community.obj_cmunity.DisableAllView(1);
        changeListFormat(1);
        runOnUiThread(() -> rv_community_detail.scrollToPosition(pos));
        detailViewPosition = pos;
        increaseCounter(posts.get(pos).getPost_id());*/

        if (posts.get(pos).getPost_id() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("post_id", posts.get(pos).getPost_id());
            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
            ContextKt.sendUserEventWithParam(this, StringConstants.community_post_open, bundle);
        }
        Intent _intent = new Intent(this, CommunityDetail.class);
        _intent.putExtra("post_id", posts.get(pos).getPost_id());
        this.startActivity(_intent);
    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {
       /* Intent intent = new Intent(CommunityDetail.this, CommunityDetail.class);
//        intent.putParcelableArrayListExtra("_list", sortedList);
        intent.putExtra("_list", posts);
        intent.putExtra("_index", pos);
        intent.putExtra("totalItem", totalItem);
        intent.putExtra("pageNumber", pageNumber);
//        intent.putExtra("catID", cat_id);
//        intent.putExtra("postID", tut_id);
        startActivity(intent);*/
    }

    @Override
    public void setUserID(String _id) {
        userID = _id;
    }

    @Override
    public void showHashTagHint() {

    }

    @Override
    public void enlargeImageView(String _url) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(CommunityDetail.this, constants.double_tap_image_community, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(CommunityDetail.this, constants.double_tap_image_community);

        try {
            ContextKt.showEnlargeImage(CommunityDetail.this, _url);
        } catch (Exception e) {
            e.printStackTrace();
        }
   /*     fm_image.setVisibility(View.VISIBLE);
        tv_back.setVisibility(View.VISIBLE);
        Glide.with(CommunityDetail.this)
                .load(_url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(iv_enlarge_image);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_animation);
        iv_enlarge_image.startAnimation(animation1);*/

    }


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
                if (data.hasExtra("updatedList")) {
                    UserPostFromApi receivedObject = (UserPostFromApi) data.getExtras().getParcelable("updatedList");
                    Log.e("TAGGG", "Received Data list size " + receivedObject.getObjData().getPost_list().size() + "size Before  " + _userPostList.getObjData().getPost_list().size());
                    int startInd = _userPostList.getObjData().getPost_list().size();
                    for (int i = startInd; i < receivedObject.getObjData().getPost_list().size(); i++) {
                        _userPostList.getObjData().getPost_list().add(receivedObject.getObjData().getPost_list().get(i));
                        mAdapter.notifyItemInserted(i);
                    }
                    Log.e("TAGGG", "Received Data list size " + receivedObject.getObjData().getPost_list().size() + "size After  " + _userPostList.getObjData().getPost_list().size());
                }

                if (data.hasExtra("pageNumber")) {
                    pageNumber = data.getIntExtra("pageNumber", pageNumber);
                    Log.e("TAGGG", "Received Data pageNumber " + pageNumber);
                }

                for (int i = 0; i < _userPostList.getObjData().getPost_list().size(); i++) {
                    Log.e("TAGG", "POST ID AFTER ADD " + _userPostList.getObjData().getPost_list().get(i).getPost_id());
                }
            }
        } catch (Exception e) {

        }
    }*/


    private void updateTitle(int count) {
        setTitle(getString(R.string.user_post_title, count, username));
    }

    void fillRecyclerView(List<CommunityPost> lst) {
        try {
            for (int i = 0; i < lst.size(); i++) {
                if (lst.get(i).getTags() != null && !lst.get(i).getTags().isEmpty()) {
                    try {
                        String fileName = lst.get(i).getImages().getContent().substring(lst.get(i).getImages().getContent().lastIndexOf('/') + 1);
                        File imageFile = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
                        if (imageFile.exists()) {
                            posts.get(i).setDownloaded(true);
                        }
                    } catch (Exception e) {
                    }
                }
            }


//            mAdapter = new ShowCommunityListAdapter(lst, CommunityDetail.this, this, this);
            mAdapter = new CommunityPostAdapter(lst, CommunityDetail.this, this, this, false);
            int type = 1;
            try {
                type = (constants.getInt(constants.formatType, CommunityDetail.this) == 0 ? 1 : constants.getInt(constants.formatType, CommunityDetail.this));
            } catch (Exception e) {
                e.printStackTrace();

            }


            mLayoutManager = new GridLayoutManager(CommunityDetail.this, type, RecyclerView.VERTICAL, false);
            rv_community_detail.setLayoutManager(mLayoutManager);
            rv_community_detail.addItemDecoration(new MarginDecoration(CommunityDetail.this));
            rv_community_detail.setHasFixedSize(true);
            rv_community_detail.setAdapter(mAdapter);
            mAdapter.notifyList(type);

            if (type == 1)
                Toast.makeText(CommunityDetail.this, "double tap to enlarge!", Toast.LENGTH_SHORT).show();
//            mAdapter.notifyDataSetChanged();

            CommunityDetail.this.runOnUiThread(() -> rv_community_detail.scrollToPosition(0));
//            showPlus_tooltip(ll_temp, frm_main);

        } catch (Exception e) {
            Log.e("TAGGG", "Exception at fill data " + e.getMessage(), e);
        }
    }


    @Override
    public void doOperationOnPost(int position, int operationType) {

    }

    @Override
    public void likeOperation(int pos, boolean isLike, boolean isFromSocialLogin) {


        System.out.println("Add Like In CommunityDetails Screen");
        String postID = String.valueOf(posts.get(pos).getPost_id());
        Map<String, Object> comments = new HashMap<>();
        comments.put("post_id", postID);

        if (AppUtils.isLoggedIn()) {
            FirebaseFunctions.getInstance().getHttpsCallable("communityPost-like").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CommunityDetail.this, "Liked added successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("CloudFunctions", "Liked added successfully");
                        if (posts.get(pos).getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", posts.get(pos).getPost_id());
                            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(CommunityDetail.this, StringConstants.community_post_like, bundle);
                        }
                        likeRewardPoint(posts.get(pos).getPost_id());
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
        } else {
            Intent intent = new Intent(CommunityDetail.this, LoginActivity.class);
            startActivity(intent);
        }
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
                        //  mAdapter.notifyItemChanged(pos);
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

        /*UserPostList model = _userPostList.getObjData().getPost_list().get(pos);
        Log.e("TAGGG", "Like Operation isLiked " + model.isLiked() + " postId " + model.getPost_id());
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, CommunityDetail.this));
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
//                        _userPostList.getObjData().getPost_list().get(pos).getObjView().setTotal_views(_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views());

                        if (_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views() != null) {
                            int total = Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views());

                            total = total + 1;
                            _userPostList.getObjData().getPost_list().get(pos).getObjView().setTotal_views(total + "");

//                            if (isLike) {
//                                total = total + 1;
//                                _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
//                            } else {
//                                if (total > 0) {
//                                    total = total - 1;
//                                    _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
//                                }
//                            }
//                            mAdapter.notifyItemChanged(pos);
                        }
                        mAdapter.notifyItemChanged(pos);
                        if (responseBase.getStatus() != null)
                            Toast.makeText(CommunityDetail.this, responseBase.getStatus() + "", Toast.LENGTH_SHORT).show();
                    } else {

                        if (isFromSocialLogin) {
//                            boolean _operation_liked = true;
                            int total = Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getObjView().getTotal_views());

                            total = total + 1;
                            _userPostList.getObjData().getPost_list().get(pos).getObjView().setTotal_views(total + "");

//                            if (_operation_liked) {
//                                total = total + 1;
//                                _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
//                            } else {
//                                if (total > 0) {
//                                    total = total - 1;
//                                    _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
//                                }
//                            }
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
        System.out.println("Add Comment In CommunityDetails Screen");
        if (AppUtils.isLoggedIn()) {
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(CommunityDetail.this, "Please add comment!", Toast.LENGTH_SHORT).show();
                return;
            }


            String userId = constants.getString(constants.UserId, CommunityDetail.this);
            String userName = constants.getString(constants.Username, CommunityDetail.this);
            String userAvatar = constants.getString(constants.ProfilePicsUrl, CommunityDetail.this);
            String userCountry = constants.getString(constants.UserCountry, CommunityDetail.this);
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
                        Toast.makeText(CommunityDetail.this, "Comment added successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("CloudFunctions", "Comment added successfully");

                        if (posts.get(pos).getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", posts.get(pos).getPost_id());
                            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(CommunityDetail.this, StringConstants.community_post_comment, bundle);
                        }
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
                            Toast.makeText(CommunityDetail.this, "Failed To Add Comment", Toast.LENGTH_SHORT).show();
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
        } else {
            Intent intent = new Intent(CommunityDetail.this, LoginActivity.class);
            startActivity(intent);
        }
    }


    private void sendMentionNotification(String receiver, String user_name, String msg, String
            post_id) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        Log.e("TAG", "sendNotification called msg " + msg);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    realTimeDBUtils = MyApplication.get_realTimeDbUtils(CommunityDetail.this);

                    if (realTimeDBUtils.getCurrentUser() == null || realTimeDBUtils.getCurrentUser().getUid() == null)
                        return;
                    Data data = new Data(realTimeDBUtils.getCurrentUser().getUid(), R.mipmap.ic_launcher, msg, user_name, receiver, "true", post_id);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Log.e("TAGG", "onResponse called " + response.toString());
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(CommunityDetail.this,
                                                "onResponse called " + response.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("TAG", "Notification onFailure " + t.getMessage(), t);
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(CommunityDetail.this,
                                                "Notification onFailure " + t.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
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
    public boolean isLoggedIn(OperationAfterLogin _operationAfterLogin) {
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

            boolean isStoragePassed = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(CommunityDetail.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(CommunityDetail.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(CommunityDetail.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }


            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(CommunityDetail.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel,null, true, pos, false, true).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    @Override
    public void downloadImageOpenInOverlayCanvas(int pos) {
        try {

            boolean isStoragePassed = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(
                        CommunityDetail.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(
                        CommunityDetail.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(CommunityDetail.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }

            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(CommunityDetail.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel,null, true, pos, false, true).execute();
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
                        CommunityDetail.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(
                        CommunityDetail.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(CommunityDetail.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(CommunityDetail.this, 1);
                return;
            }

            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(CommunityDetail.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel,null, true, pos, false, true).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    @Override
    public void shareImage(int pos) {
        if (posts.get(pos).getImages().getContent() != null) {
            String videoUrl = posts.get(pos).getLinks().getYoutube();
            String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
            if (fileName.contains("?")) {
                String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                fileName = beforeQuestionMark.replace("%2F", "_");
            }
            String title = posts.get(pos).getTitle();
            File imageFile = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
            if (imageFile.exists()) {
                Uri photoURI = FileProvider.getUriForFile(CommunityDetail.this, getString(R.string.authority), imageFile);
                doSocialShare(pos, photoURI, title, videoUrl);
            } else {
                Log.e("TAG", "shareImage: " + posts.get(pos).getImages().getContent());
                new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, true, title, videoUrl, false, pos, false, false).execute();
            }
        }
    }

    @Override
    public void copyImage(int pos) {
        if (posts.get(pos).getImages().getContent() != null) {


            ClipboardManager clipboard = (ClipboardManager) CommunityDetail.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", posts.get(pos).getImages().getContent());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(CommunityDetail.this, "Copied to Clipboard", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void reportPost(int pos) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CommunityDetail.this);
        builderSingle.setIcon(R.drawable.report_icon);
        builderSingle.setTitle("Report this post");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommunityDetail.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Its Spam");
        arrayAdapter.add("Not Appropriate");


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendSpamReport(pos, posts.get(pos).getPost_id(), arrayAdapter.getItem(which));
            }
        });
        builderSingle.show();
    }

    int commentPage = 1;
    int totalComment;

    @Override
    public void view_all_comment(int pos) {
        CommunityCommentsBottomsheet communityCommentsBottomsheet = CommunityCommentsBottomsheet.newInstance(posts.get(pos).getPost_id().toString());
        communityCommentsBottomsheet.show(getSupportFragmentManager(), "CommunityDetail");
    }

    @Override
    public void seachByHashTag(String tag) {
        Intent _intent = new Intent(CommunityDetail.this, CommunityDetail.class);
        _intent.putExtra("hashtag", tag);
        startActivity(_intent);
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

    class DownloadImageFromURL extends AsyncTask<Void, Void, String> {
        String imageURL, fileName, title, videoUrl;
        Boolean isFromShare = false, NeedToopenInCanvas, OpenInTraceCanvas, OpenInOverlayCanvas;
        int position;
        File imageFile;

        public DownloadImageFromURL(String imageURL, String fileName, Boolean isFromShare,
                                    String post_title,
                                    String videoUrl,
                                    boolean NeedToopenInCanvas, int pos,
                                    boolean OpenInTraceCanvas,
                                    boolean OpenInOverlayCanvas) {
            this.imageURL = imageURL;
            this.fileName = fileName;
            this.isFromShare = isFromShare;
            title = post_title;
            this.videoUrl = videoUrl;
            this.NeedToopenInCanvas = NeedToopenInCanvas;
            this.OpenInTraceCanvas = OpenInTraceCanvas;
            this.OpenInOverlayCanvas = OpenInOverlayCanvas;
            Toast.makeText(CommunityDetail.this, "downloading started!", Toast.LENGTH_SHORT).show();
            position = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CommunityDetail.this);
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
            File file = new File(KGlobal.getDownloadPath(CommunityDetail.this), fileName);
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
//                String downloadPath = KGlobal.getDownloadPath(CommunityDetail.this);
                String downloadPath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .toString();
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
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e.getMessage());
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
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (isFromShare) {
                    Uri photoURI = FileProvider.getUriForFile(CommunityDetail.this,
                            getString(R.string.authority),
                            new File(path));
                    doSocialShare(position, photoURI, title,videoUrl);
                } else if (NeedToopenInCanvas && OpenInOverlayCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent intent = new Intent(CommunityDetail.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", file.getName());
                    intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else if (NeedToopenInCanvas && OpenInTraceCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);

                    Intent lIntent1 = new Intent(CommunityDetail.this, PaintActivity.class);
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
                        Toast.makeText(CommunityDetail.this, "Saved in your Images folder", Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(CommunityDetail.this, new String[]{imageFile.getAbsolutePath()}, null, null);
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    void showDialog(String title, String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CommunityDetail.this);

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

    public void doSocialShare(int pos, Uri photoURI, String title, String videoUrl) {
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

            String message = "";
            if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.endsWith("null")) {
                message += "Watch video : " + "\n";
                message += videoUrl + "\n\n";
            }

            message += "Checkout this image that I thought you might like. It’s from the app " + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share Image and Text"));

            shareRewardPoint(posts.get(pos).getPost_id());
        } catch (Exception e) {
            Log.e(PaintActivity.class.getName(), e.getMessage());
        }
    }

    public static class receiverForShare extends BroadcastReceiver {

        public receiverForShare() {
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

    void sendSpamReport(int pos, String postId, String msg) {

      /*  HashMap<String, RequestBody> _map = new HashMap<>();
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
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(CommunityDetail.this);
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

    void commentList(String postId, String pageNumber, CircleProgressBar p_bar,
                     final CommentListAdapter _adapter, ArrayList<AllCommentModel.data.all_comments> list) {


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
//                Log.e("TAGGG", "OnNext " + responseIncreaseCounter.getResponse());
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
    public void viewProfile(int pos) {
        if (AppUtils.isLoggedIn()) {
            Bundle bundle = new Bundle();
            bundle.putString("post_id", posts.get(pos).getPost_id());
            bundle.putString("user_id", posts.get(pos).getUser_id());
            ContextKt.sendUserEventWithParam(this, StringConstants.community_post_open_author, bundle);
            FireUtils.openProfileScreen(this, posts.get(pos).getUser_id());
        } else {
            Intent intent = new Intent(CommunityDetail.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userID = constants.getString(constants.UserId, CommunityDetail.this);
    }


    @Override
    public void showHideFab(boolean needToShown) {
        try {
            Community.obj_cmunity.showHideFab(needToShown);
        } catch (Exception e) {
            Log.e("TAG", "exception " + e.getMessage());
        }
    }


    boolean is_found_in_delete = false;
    boolean is_found_in_block = false;

    @Override
    public void openChatsScreen() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(
                    CommunityDetail.this,
                    constants.user_chats,
                    Toast.LENGTH_SHORT
            ).show();
        }
        FirebaseUtils.logEvents(CommunityDetail.this, constants.user_chats);
        Intent intent = new Intent(
                CommunityDetail.this,
                ChatUserList.class
        );
        startActivity(intent);
    }

    @Override
    public void openUsersPostsListScreen(int pos) {

    }

    @Override
    public void openChatScreen(String key, String user_id, int position) {
        new ChatUtils(this).openChatScreen(key, user_id);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    /*This method will prompt social media login dialog when user click on upload zip file.*/
    private void showLoginDialog() {
        Intent intent = new Intent(CommunityDetail.this, LoginActivity.class);
        startActivity(intent);
    }
//    public void showLoginDialog(DialogType dialogType) {
//        try {
//            final Dialog dialog = new Dialog(CommunityDetail.this);
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
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(CommunityDetail.this, constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(CommunityDetail.this, constants.FACEBOOK_LOGIN);
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
//                        if (CommunityDetail.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
//                            return;
//                        }
//                        dialog.dismiss();
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(CommunityDetail.this, constants.Social_Paintology_Login, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(CommunityDetail.this, constants.Social_Paintology_Login);
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

//    public void showDialog() {
//
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//        final Dialog dialog = new Dialog(CommunityDetail.this);
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
//                    LoginRequestModel model = new LoginRequestModel(
//                            "",
//                            uname,
//                            edt_email.getText().toString().trim(),
//                            edt_pass.getText().toString().trim()
//                    );
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
                                Toast.makeText(CommunityDetail.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(CommunityDetail.this, constants.FacebookLoginSuccess);
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
                Toast.makeText(CommunityDetail.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(CommunityDetail.this, constants.FacebookLoginFailed);
        }
    };

    /*User can do their google sign in using this method*/
    private void signIn() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(CommunityDetail.this, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(CommunityDetail.this, constants.GOOGLE_LOGIN);
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

                if (BuildConfig.DEBUG) {
                    Toast.makeText(CommunityDetail.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(CommunityDetail.this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE, task.getResult(ApiException.class));

//                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(CommunityDetail.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(CommunityDetail.this, constants.GoogleLoginFailed);
            } catch (Exception e) {

            }
        } else {
            Log.e("TAGGG", "SignIn Result Called");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType, GoogleSignInAccount... accounts) {

        Log.e("TAGGG", "Add User Data userID " + model.user_id + " email " + model.user_email + " username " + model.user_name);
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_name != null) ? model.user_name : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");
        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, CommunityDetail.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, CommunityDetail.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, CommunityDetail.this));

        try {
            String _ip = constants.getString(constants.IpAddress, CommunityDetail.this);
            String _country = constants.getString(constants.UserCountry, CommunityDetail.this);
            String _city = constants.getString(constants.UserCity, CommunityDetail.this);

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
                    Log.e("TAGGG", "onResponse called " + response.toString());
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {
                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", CommunityDetail.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", CommunityDetail.this);

                            setUserID(response.body().getObjData().getUser_id() + "");

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", CommunityDetail.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), CommunityDetail.this);

                            constants.putString(constants.Username, model.user_name, CommunityDetail.this);
                            constants.putString(constants.Password, model.user_id, CommunityDetail.this);
                            constants.putString(constants.Email, model.user_email, CommunityDetail.this);
                            constants.putString(constants.LoginInPaintology, "true", CommunityDetail.this);

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, CommunityDetail.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(CommunityDetail.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(CommunityDetail.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(CommunityDetail.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(CommunityDetail.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    isLoggedIn = true;
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(CommunityDetail.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(CommunityDetail.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (accounts != null && accounts.length > 0)
                                        account = accounts[0];
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(CommunityDetail.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(CommunityDetail.this, constants.GoogleRegistration);
                                    }
                                }
                            }

                            try {
                                if (_operationLogin != null && _operationLogin.getOperationType() != null) {
                                    if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeLike)) {
                                        _post_operation.likeOperation(_operationLogin.getPosition(), true, true);
                                    } else if (_operationLogin.getOperationType().equalsIgnoreCase(constants.OperationTypeComment)) {
                                        _post_operation.addComment(_operationLogin.getPosition(), _operationLogin.get_obj_comment_data().get_user_comment(), _operationLogin.get_obj_comment_data().get_user_list());
                                    }
                                }

                                String _user_id = constants.getString(constants.UserId, CommunityDetail.this);
                                try {
                                    if (KGlobal.isInternetAvailable(CommunityDetail.this) && _user_id != null && !_user_id.isEmpty()) {
                                        startService(new Intent(CommunityDetail.this, SendDeviceToken.class));
                                    }
                                } catch (Exception e) {
                                }
                                fetchProfileData();
                                MyApplication.get_realTimeDbUtils(CommunityDetail.this).autoLoginRegister(response.body().getObjData().getStatus());

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
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(CommunityDetail.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(CommunityDetail.this, constants.PaintologyLoginFailed);
                            }
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(CommunityDetail.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(CommunityDetail.this, constants.event_failed_to_adduser);
                            Toast.makeText(CommunityDetail.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(CommunityDetail.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(CommunityDetail.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(CommunityDetail.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(CommunityDetail.this, constants.event_failed_to_adduser);
                        Toast.makeText(CommunityDetail.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
                            isLoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(CommunityDetail.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(CommunityDetail.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(CommunityDetail.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(CommunityDetail.this, constants.event_failed_to_adduser);
                        Toast.makeText(CommunityDetail.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(CommunityDetail.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        try {
            if (CommunityDetail.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    public void fetchProfileData() {
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Observable<GetUserProfileResponse> profileResponse = apiInterface.getUserProfileData(ApiClient.SECRET_KEY, constants.getString(constants.UserId, CommunityDetail.this));
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
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);
                    placeHolder = R.drawable.profile_icon_male;
                    constants.putString(constants.UserGender, constants.MALE, CommunityDetail.this);
                } else if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("female")) {
//                    iv_profile.setImageResource(R.drawable.profile_icon_female);
                    placeHolder = R.drawable.profile_icon_female;
                    constants.putString(constants.UserGender, constants.FEMALE, CommunityDetail.this);
                } else {
                    placeHolder = R.drawable.profile_icon_male;
                    constants.putString(constants.UserGender, constants.MALE, CommunityDetail.this);
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);
                }

                if (getUserProfileResponse.getResponse().getProfilePic() != null && !getUserProfileResponse.getResponse().getProfilePic().isEmpty()) {
                    Log.e("TAGGG", "Profile Image Set from 766");
//                    Glide.with(CommunityDetail.this)
//                            .load(getUserProfileResponse.getResponse().getProfilePic())
//                            .apply(new RequestOptions().placeholder(placeHolder).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                            .into(iv_profile);

                    Glide.with(CommunityDetail.this)
                            .load(getUserProfileResponse.getResponse().getProfilePic())
                            .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_icon))
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

                    constants.putString(constants.ProfilePicsUrl, getUserProfileResponse.getResponse().getProfilePic(), CommunityDetail.this);

                    MyApplication.get_realTimeDbUtils(CommunityDetail.this).getDbReference().child(constants.firebase_user_list).child(constants.getString(constants.UserId, CommunityDetail.this)).child("profile_pic").setValue(getUserProfileResponse.getResponse().getProfilePic());
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
    public ArrayList<Firebase_User> getFirebaseUserList() {

        return _lst_firebase_user;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.community_menu, menu);
//        actionProfile = menu.findItem(R.id.action_profile);
//        menu.findItem(R.id.action_chat).setVisible(false);
//        menu.findItem(R.id.action_share_paintology).setVisible(false);
//        setupProfileIcon();
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void setupProfileIcon() {
//
//        if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//            if (constants.getString(constants.ProfilePicsUrl, CommunityDetail.this) != null && !constants.getString(constants.ProfilePicsUrl, CommunityDetail.this).isEmpty()) {
//                Log.e("TAGGG", "Profile Image Set from OnCreate");
////                Glide.with(CommunityDetail.this)
////                        .load(constants.getString(constants.ProfilePicsUrl, CommunityDetail.this))
////                        .apply(new RequestOptions().placeholder(R.drawable.profile_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
////                        .into(iv_profile);
//
//                Glide.with(CommunityDetail.this)
//                        .load(constants.getString(constants.ProfilePicsUrl, CommunityDetail.this))
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
//
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.action_profile:
//                if (AppUtils.isLoggedIn()) {
//                    startActivity(new Intent(CommunityDetail.this, MyProfile.class));
//                } else {
//                    Intent intent = new Intent(CommunityDetail.this, LoginActivity.class);
//                    startActivity(intent);
//                }
//                return true;
////            case R.id.action_chat:
////                FirebaseUtils.logEvents(CommunityDetail.this, constants.chat_community_header_click);
////
////                AccessToken accessToken = AccessToken.getCurrentAccessToken();
////                isLoggedIn = accessToken != null && !accessToken.isExpired();
////                account = GoogleSignIn.getLastSignedInAccount(CommunityDetail.this);
////                isLoginInPaintology = constants.getString(constants.LoginInPaintology, CommunityDetail.this);
////
////                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true")))
////                    startActivity(new Intent(CommunityDetail.this, ChatUserList.class));
////                else {
////                    showLoginDialog(DialogType.CHAT);
////                }
////                return true;
//            case R.id.action_add_post:
//                if (BuildConfig.DEBUG){
//
//                    Toast.makeText(CommunityDetail.this,constants.click_community_post_menu, Toast.LENGTH_SHORT).show();
//                }
//
//                FirebaseUtils.logEvents(CommunityDetail.this, constants.click_community_post_menu);
//                if (!KGlobal.isInternetAvailable(CommunityDetail.this)) {
//                    Toast.makeText(CommunityDetail.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//                if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
//                    Intent intent = new Intent(CommunityDetail.this, Gallery.class);
//                    intent.putExtra("title", "New Post");
//                    intent.putExtra("mode", 1);
//                    intent.putExtra("maxSelection", 500);
//                    intent.putExtra("isFromNewPost", true);
//                    startActivity(intent);
//                } else {
//                    if (BuildConfig.DEBUG){
//
//                        Toast.makeText(CommunityDetail.this,constants.open_social_login_search_hashtag_new_post_dialog, Toast.LENGTH_SHORT).show();
//                    }
//
//                    FirebaseUtils.logEvents(CommunityDetail.this, constants.open_social_login_search_hashtag_new_post_dialog);
//                    showLoginDialog();
//                }
//                return true;
//            case R.id.action_view_mode:
//                View view = getLayoutInflater().inflate(R.layout.view_mode_dialog, null, false);
//
//                LinearLayout viewModeNormal = view.findViewById(R.id.view_mode_normal);
//                LinearLayout viewMode2n = view.findViewById(R.id.view_mode_2n);
//                LinearLayout viewMode3n = view.findViewById(R.id.view_mode_3n);
//                LinearLayout viewMode4n = view.findViewById(R.id.view_mode_4n);
//
//                viewModeNormal.setOnClickListener(v -> {
////                    ll_1.performClick();
//
////                    enableAllView(ll_1);
//                    changeListFormat(1);
////
////                    if (MainCollectionFragment.objHomeInterface.changeListFormat(1)) {
////                        if (currentType != 1 && currentType != 5) {
////                            _indexes.add(1);
////                            currentType = 1;
//////                                            disableAllView();
//////                                            enableAllView(ll_1);
////                        }
////                        FirebaseUtils.logEvents(CommunityDetail.this, constants.community_1x1_selection);
////                    }
//
//                    if (alertDialogViewModel != null) {
//                        alertDialogViewModel.dismiss();
//                    }
//
//                });
//
//                viewMode2n.setOnClickListener(v -> {
////                    ll_2.performClick();
//
////                    enableAllView(ll_2);
//                    changeListFormat(2);
//
////                    if (MainCollectionFragment.objHomeInterface.changeListFormat(2)) {
////                        if (currentType != 2) {
////                            _indexes.add(2);
////                            currentType = 2;
//////                                            disableAllView();
//////                                            enableAllView(ll_2);
////                        }
////                        FirebaseUtils.logEvents(CommunityDetail.this, constants.community_2x2_selection);
////                    }
//
//                    if (alertDialogViewModel != null) {
//                        alertDialogViewModel.dismiss();
//                    }
//
//                });
//
//                viewMode3n.setOnClickListener(v -> {
////                    ll_3.performClick();
//
////                    if (MainCollectionFragment.objHomeInterface.changeListFormat(3)) {
////
////                        if (currentType != 3) {
////                            _indexes.add(3);
////                            currentType = 3;
//////                                            disableAllView();
//////                                            enableAllView(ll_3);
////                        }
////                        FirebaseUtils.logEvents(CommunityDetail.this, constants.community_3x2_selection);
////                    }
//
////                    enableAllView(ll_3);
//                    changeListFormat(3);
//
//                    if (alertDialogViewModel != null) {
//                        alertDialogViewModel.dismiss();
//                    }
//
//                });
//
//                viewMode4n.setOnClickListener(v -> {
////                    ll_4.performClick();
//
////                    if (MainCollectionFragment.objHomeInterface.changeListFormat(4)) {
////                        if (currentType != 4) {
////                            _indexes.add(4);
////                            currentType = 4;
//////                                            disableAllView();
//////                                            enableAllView(ll_4);
////                        }
////                        FirebaseUtils.logEvents(CommunityDetail.this, constants.community_4x2_selection);
////                    }
//
////                    enableAllView(ll_4);
//                    changeListFormat(4);
//
//                    if (alertDialogViewModel != null) {
//                        alertDialogViewModel.dismiss();
//                    }
//
//                });
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityDetail.this);
//                builder.setView(view);
//                alertDialogViewModel = builder.create();
//
//                alertDialogViewModel.show();
//                return true;
////            case R.id.action_share_paintology:
////                try {
////                    FirebaseUtils.logEvents(CommunityDetail.this, constants.click_community_menu_share_paintology);
////                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
////                    share.setType("text/plain");
////                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
////
////                    // Add data to the intent, the receiving app will decide
////                    // what to do with it.
////                    share.putExtra(Intent.EXTRA_SUBJECT, "Paintology - great little app you should check out");
////                    share.putExtra(Intent.EXTRA_TEXT, "I found this free app called Paintology, I think you will love it, check it out!\n\nhttps://play.google.com/store/apps/details?id=com.paintology.lite");
////
////                    startActivity(Intent.createChooser(share, "Share link!"));
////                } catch (Exception e) {
////                    Log.e("TAG", "Exception at share " + e.getMessage());
////                }
////                return true;
//            case R.id.action_feedback:
//                FirebaseUtils.logEvents(CommunityDetail.this, constants.click_community_menu_feedback);
//                showFeedbackDialog();
//                return true;
//            case R.id.action_goto_playstore:
//                FirebaseUtils.logEvents(CommunityDetail.this, constants.click_community_menu_googleplay_click);
//                try {
//                    String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(browserIntent);
//                } catch (Exception e) {
//                    Log.e("Community", e.getMessage());
//                }
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void commentRewardPoint(String postId) {

        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_comment, postId);
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_comment", rewardSetup.getCommunity_post_comment() == null ? 0 : rewardSetup.getCommunity_post_comment(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void likeRewardPoint(String postId) {


        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_like, postId);
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_like", rewardSetup.getCommunity_post_like() == null ? 0 : rewardSetup.getCommunity_post_like(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void shareRewardPoint(String postId) {

        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_share, postId);
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_share", rewardSetup.getCommunity_post_share() == null ? 0 : rewardSetup.getCommunity_post_share(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }
}