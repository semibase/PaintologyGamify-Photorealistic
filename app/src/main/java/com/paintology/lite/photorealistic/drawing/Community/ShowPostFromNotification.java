package com.paintology.lite.photorealistic.drawing.Community;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paintology.lite.photorealistic.drawing.Adapter.CommentListAdapter;
import com.paintology.lite.photorealistic.drawing.Adapter.ShowCommunitySinglePostAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Data;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.MyResponse;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Sender;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Chat.RealTimeDBUtils;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.Fragment.MainCollectionFragment;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.Model.GetUserProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.ResponseBase;
import com.paintology.lite.photorealistic.drawing.Model.UserPostList;
import com.paintology.lite.photorealistic.drawing.Model.UserSinglePostFromApi;
import com.paintology.lite.photorealistic.drawing.Model.post_comment_lists;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.gallery.home_fragment_operation;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.EndlessRecyclerOnScrollListener;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
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
import java.util.HashMap;

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

public class ShowPostFromNotification extends AppCompatActivity implements home_fragment_operation, PostOperation {

    RecyclerView recycler_view;
    ShowCommunitySinglePostAdapter mAdapter;
    CircleProgressBar item_progress_bar;
    String _post_id = "";
    UserSinglePostFromApi _userPostList;
    ApiInterface apiInterface;
    StringConstants constants = new StringConstants();
    String userID;
    ProgressDialog progressDialog = null;
    boolean isLoggedIn;
    GoogleSignInAccount account;
    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String isLoginInPaintology;

    private GoogleSignInClient mGoogleSignInClient;

    int RC_SIGN_IN = 7;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    GoogleApiClient googleApiClient;
    String LoginInPaintology;

    LoginButton facebook_login_btn;

    RealTimeDBUtils realTimeDBUtils;
    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post_from_notification);

        setupToolbar();
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        userID = constants.getString(constants.UserId, ShowPostFromNotification.this);
        recycler_view = findViewById(R.id.recycler_view);
//        GridLayoutManager mLayoutManager = new GridLayoutManager(ShowPostFromNotification.this, 1, RecyclerView.VERTICAL, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(
                ShowPostFromNotification.this,
                LinearLayoutManager.VERTICAL,
                false);
        recycler_view.setLayoutManager(mLayoutManager);
        item_progress_bar = findViewById(R.id.item_progress_bar);
        try {
            if (getIntent() != null && getIntent().hasExtra("post_id")) {
                getPostDetail(getIntent().getStringExtra("post_id"));
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }


        apiService = ApiClient.getClientNotification().create(ApiInterface.class);
        realTimeDBUtils = MyApplication.get_realTimeDbUtils(this);

        getAllUser();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        facebook_login_btn = (LoginButton) findViewById(R.id.login_button);

        facebook_login_btn.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        facebook_login_btn.registerCallback(callbackManager, callback);

        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        isLoginInPaintology = constants.getString(constants.LoginInPaintology, this);


    }

    void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.post_detail));
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void getPostDetail(String post_id) {
        Log.e("TAGGG", "GetList Called post_id " + post_id);
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody req_post_id = RequestBody.create(MediaType.parse("text/plain"), post_id);

        _map.put("post_id", req_post_id);

        Observable<UserSinglePostFromApi> _observer = apiInterface.getPostDetail(ApiClient.SECRET_KEY, _map);

        item_progress_bar.setVisibility(View.VISIBLE);

        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<UserSinglePostFromApi>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(UserSinglePostFromApi userPostFromApi) {
                try {
                    Log.e("TAGG", "onNext Called null " + (userPostFromApi == null));
                    if (userPostFromApi == null || userPostFromApi.getCode() != 200)
                        return;
                    item_progress_bar.setVisibility(View.GONE);
                    if (_userPostList == null) {
                        _userPostList = userPostFromApi;
                        fillRecyclerView(_userPostList);
                    }
                } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                try {
                    Log.e("TAGGG", "onError " + e.getMessage(), e);
                    item_progress_bar.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(ShowPostFromNotification.this, constants.event_service_un_available, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.event_service_un_available);
                    Toast.makeText(ShowPostFromNotification.this, "Service temporarily unavailable, please try later.", Toast.LENGTH_SHORT).show();
                } catch (Exception ec) {
                    Log.e("TAG", e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                item_progress_bar.setVisibility(View.GONE);
            }
        });
    }

    void fillRecyclerView(UserSinglePostFromApi lst) {
        try {
            for (int i = 0; i < lst.getObjData().getPost_list().size(); i++) {
                if (lst.getObjData().getPost_list().get(i).getImage_hashtag() != null) {
                    String hashtag = lst.getObjData().getPost_list().get(i).getImage_hashtag().replace("|", " ");
                    lst.getObjData().getPost_list().get(i).setImage_hashtag(hashtag);
                }
            }
            mAdapter = new ShowCommunitySinglePostAdapter(lst, ShowPostFromNotification.this, this, this);

            recycler_view.addItemDecoration(new MarginDecoration(ShowPostFromNotification.this));
         //   recycler_view.setHasFixedSize(true);
            recycler_view.setAdapter(mAdapter);
            mAdapter.notifyList(1);
//            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at fill data " + e.getMessage(), e);
        }
    }

    @Override
    public void doOperationOnPost(int position, int operationType) {

    }

    @Override
    public void viewOperation(int pos, int totalViews, boolean isFromSocialLogin) {
        UserPostList model = _userPostList.getObjData().getPost_list().get(pos);
        Log.e("TAGGG", "Like Operation isLiked " + model.isLiked() + " postId " + model.getPost_id());
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, ShowPostFromNotification.this));
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
                            Toast.makeText(ShowPostFromNotification.this, responseBase.getStatus() + "", Toast.LENGTH_SHORT).show();
                    }
//                    mAdapter.notifyItemChanged(pos);
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
        });
    }

    @Override
    public void likeOperation(int pos, boolean isLike, boolean isFromSocialLogin) {

        UserPostList model = _userPostList.getObjData().getPost_list().get(pos);
        Log.e("TAGGG", "Like Operation isLiked " + model.isLiked() + " postId " + model.getPost_id());
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, ShowPostFromNotification.this));
        RequestBody post_id = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.getPost_id() != null) ? model.getPost_id() : "");
//        RequestBody like = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.isLiked()) ? "0" : "1");
        RequestBody like = RequestBody.create(MediaType.parse("text/plain"), isLike ? "1" : "0");

        _map.put("user_id", userId);
        _map.put("post_id", post_id);
        _map.put("like", like);

        Observable<ResponseBase> _observer = apiInterface.doLikeUnlike(ApiClient.SECRET_KEY, _map);

        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBase>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBase responseBase) {
                Log.e("TAGGG", "Operation onNext");
                if (responseBase != null) {
                    if (!responseBase.getCode().equals(200)) {
                        _userPostList.getObjData().getPost_list().get(pos).setLiked(!_userPostList.getObjData().getPost_list().get(pos).isLiked());

                        if (_userPostList.getObjData().getPost_list().get(pos).getObjLikes().getTotal_likes() != null) {
                            int total = Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getObjLikes().getTotal_likes());
                            if (isLike) {
                                total = total + 1;
                                _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
                            } else {
                                if (total > 0) {
                                    total = total - 1;
                                    _userPostList.getObjData().getPost_list().get(pos).getObjLikes().setTotal_likes(total + "");
                                }
                            }
//                            mAdapter.notifyItemChanged(pos);
                        }
                        mAdapter.notifyItemChanged(pos);
                        if (responseBase.getStatus() != null)
                            Toast.makeText(ShowPostFromNotification.this, responseBase.getStatus() + "", Toast.LENGTH_SHORT).show();
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
        });
    }

    @Override
    public void addComment(int pos, String comment, ArrayList<Firebase_User> _user_list) {

        String username = constants.getString(constants.Username, ShowPostFromNotification.this);
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody req_user_id = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, this));
        RequestBody req_post_id = RequestBody.create(MediaType.parse("text/plain"), _userPostList.getObjData().getPost_list().get(pos).getPost_id() + "");
        RequestBody req_comment = RequestBody.create(MediaType.parse("text/plain"), comment);
        RequestBody req_username = RequestBody.create(MediaType.parse("text/plain"), username);
        Log.e("TAGGG", "addComment Post Id " + _userPostList.getObjData().getPost_list().get(pos).getPost_id() + " Comment " + comment);
        _map.put("user_id", req_user_id);
        _map.put("post_id", req_post_id);
        _map.put("comment", req_comment);
        _map.put("username", req_username);

        Observable<ResponseBase> _observer = apiInterface.add_post(ApiClient.SECRET_KEY, _map);

        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBase>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBase ResponseBase) {
                try {
                    if (ResponseBase != null) {
                        if (ResponseBase.getCode() == 200) {
                            int total = ((_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments() != null && !_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments().isEmpty()) ? Integer.parseInt(_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getTotal_comments()) + 1 : 0);
                            _userPostList.getObjData().getPost_list().get(pos).getCommentsList().setTotal_comments(total + "");

                            if (_userPostList.getObjData().getPost_list().get(pos).getCommentsList().getPost_comment_lists() != null) {
                                ArrayList<post_comment_lists> lst = _userPostList.getObjData().getPost_list().get(pos).getCommentsList().getPost_comment_lists();
//                                Log.e("TAGGG", "Comment List size Before " + lst.size());
                                post_comment_lists obj = new post_comment_lists();
                                obj.setComment_content(comment);
                                obj.setUsername(username);
                                obj.setComment_date("Just Now");
                                lst.add(0, obj);

                                _userPostList.getObjData().getPost_list().get(pos).getCommentsList().setPost_comment_lists(lst);
//                                mAdapter.notifyItemChanged(pos);
//                                Log.e("TAGGG", "Comment List size After " + lst.size());
                            }
                            mAdapter.notifyItemChanged(pos);

//                            if (_user_list != null && _user_list.size() != 0)
//                                for (int i = 0; i < _user_list.size(); i++) {
//                                    sendMentionNotification(_user_list.get(i).getKey(), constants.getString(constants.Username, ShowPostFromNotification.this), constants.getString(constants.Username, ShowPostFromNotification.this) + " mentioned you in comment.", _userPostList.getObjData().getPost_list().get(pos).getPost_id() + "");
//                                }

                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at post comment " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "Exception at post comment " + e.getMessage(), e);
                Toast.makeText(ShowPostFromNotification.this, "Failed To Add Comment", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
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
                    realTimeDBUtils = MyApplication.get_realTimeDbUtils(ShowPostFromNotification.this);

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
    public boolean isLoggedIn(OperationAfterLogin _operationLogin) {
        if (constants.getBoolean(constants.IsGuestUser, this)) {
            FireUtils.openLoginScreen(this,true);
            return false;
        } else {
            return true;
        }
       /* AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
        if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
            return true;
        } else {
//            showLoginDialog();
            return false;
        }*/
    }

    @Override
    public void downloadImage(int pos, boolean NeedToopenInCanvas) {
        if (_userPostList.getObjData().getPost_list().get(pos).getImage_Url() != null) {
            String fileName = _userPostList.getObjData().getPost_list().get(pos).getImage_Url().substring(_userPostList.getObjData().getPost_list().get(pos).getImage_Url().lastIndexOf('/') + 1);
            String post_titel = _userPostList.getObjData().getPost_list().get(pos).getImage_title();
            File imageFile = new File(KGlobal.getDownloadPath(this), fileName);
            if (imageFile.exists()) {
                if (NeedToopenInCanvas) {
                    Intent intent = new Intent(ShowPostFromNotification.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else
                    showDialog("File Exist", imageFile.getAbsolutePath());
            } else {
                new DownloadImageFromURL(_userPostList.getObjData().getPost_list().get(pos).getImage_Url(), fileName, false, post_titel, NeedToopenInCanvas).execute();
            }
        }
    }

    @Override
    public void shareImage(int pos) {

        if (_userPostList.getObjData().getPost_list().get(pos).getImage_Url() != null) {
            String fileName = _userPostList.getObjData().getPost_list().get(pos).getImage_Url().substring(_userPostList.getObjData().getPost_list().get(pos).getImage_Url().lastIndexOf('/') + 1);
            String title = _userPostList.getObjData().getPost_list().get(pos).getImage_title();
            File imageFile = new File(KGlobal.getDownloadPath(this), fileName);
            if (imageFile.exists()) {
                Uri photoURI = FileProvider.getUriForFile(ShowPostFromNotification.this,
                        getString(R.string.authority),
                        imageFile);
                doSocialShare(photoURI, title);
            } else {
                new DownloadImageFromURL(_userPostList.getObjData().getPost_list().get(pos).getImage_Url(), fileName, true, title, false).execute();
            }
        }
    }

    @Override
    public void copyImage(int pos) {

    }

    @Override
    public void reportPost(int pos) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ShowPostFromNotification.this);
        builderSingle.setIcon(R.drawable.report_icon);
        builderSingle.setTitle("Report this post");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowPostFromNotification.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Its Spam");
        arrayAdapter.add("Not Appropriate");


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendSpamReport(pos, _userPostList.getObjData().getPost_list().get(pos).getPost_id(), arrayAdapter.getItem(which));
            }
        });
        builderSingle.show();

    }

    int commentPage = 1;
    int totalComment;


    @Override
    public void view_all_comment(int pos) {
        final Dialog dialog = new Dialog(ShowPostFromNotification.this);
        dialog.setContentView(R.layout.view_all_comment_layout);

        ImageView iv_profile = (ImageView) dialog.findViewById(R.id.iv_post_user);
        TextView tv_post_user = (TextView) dialog.findViewById(R.id.tv_post_username);
        TextView tv_date_time = (TextView) dialog.findViewById(R.id.tv_date_time);
        TextView tv_post_title = (TextView) dialog.findViewById(R.id.tv_post_title);

        CircleProgressBar p_bar = (CircleProgressBar) dialog.findViewById(R.id.progress_bar);

        RecyclerView rv_comment = (RecyclerView) dialog.findViewById(R.id.rv_comment_list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ShowPostFromNotification.this, 1, RecyclerView.VERTICAL, false);
        rv_comment.setLayoutManager(mLayoutManager);
        try {
            tv_post_user.setText(_userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserName());
            tv_post_title.setText(_userPostList.getObjData().getPost_list().get(pos).getImage_title());

            tv_date_time.setText("Posted " + _userPostList.getObjData().getPost_list().get(pos).getPost_date_time());

            Glide.with(ShowPostFromNotification.this)
                    .load(_userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserProfilePic())
                    .apply(new RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(iv_profile);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }


        String postId = _userPostList.getObjData().getPost_list().get(pos).getPost_id();
        Log.e("TAGG", "Post ID for Comment " + postId);

        ArrayList<AllCommentModel.data.all_comments> list = new ArrayList<AllCommentModel.data.all_comments>();
        CommentListAdapter _adapter = new CommentListAdapter(ShowPostFromNotification.this, list, this, this);
        rv_comment.setAdapter(_adapter);
        commentPage = 1;
        commentList(postId, commentPage + "", p_bar, _adapter, list);

        rv_comment.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.e("TAGGG", "OnLoad More Called");
                if (list.size() < totalComment) {
                    commentPage = commentPage + 1;
                    commentList(postId, commentPage + "", p_bar, _adapter, list);
                } else
                    Log.e("TAGG", "OnLoad All Loaded");
            }
        });

        dialog.show();
    }

    @Override
    public void seachByHashTag(String tag) {
        Intent _intent = new Intent(ShowPostFromNotification.this, CommunityDetail.class);
        _intent.putExtra("hashtag", tag);
        startActivity(_intent);
    }

    @Override
    public void viewProfile(int pos) {
        if (AppUtils.isLoggedIn()) {
            FireUtils.openProfileScreen(this, _userPostList.getObjData().getPost_list().get(pos).getUserInfo().getUserId());
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void enlargeImageView(String _url) {

    }

    @Override
    public boolean changeListFormat(int rowCount) {
        return false;
    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {

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

    void sendSpamReport(int pos, String postId, String msg) {

        HashMap<String, RequestBody> _map = new HashMap<>();
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
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(ShowPostFromNotification.this);
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
        });
    }

    class DownloadImageFromURL extends AsyncTask<Void, Void, String> {
        String imageURL, fileName, title;
        Boolean isFromShare = false, NeedToopenInCanvas;


        public DownloadImageFromURL(String imageURL, String fileName, Boolean isFromShare, String post_title, boolean NeedToopenInCanvas) {
            this.imageURL = imageURL;
            this.fileName = fileName;
            this.isFromShare = isFromShare;
            title = post_title;
            this.NeedToopenInCanvas = NeedToopenInCanvas;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowPostFromNotification.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            File file = new File(KGlobal.getDownloadPath(ShowPostFromNotification.this), fileName);
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
                File path = new File(KGlobal.getDownloadPath(ShowPostFromNotification.this)); //Creates app specific folder

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
                if (isFromShare) {
                    Uri photoURI = FileProvider.getUriForFile(ShowPostFromNotification.this,
                            getString(R.string.authority),
                            new File(path));
                    doSocialShare(photoURI, title);
                } else if (NeedToopenInCanvas) {

                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent intent = new Intent(ShowPostFromNotification.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", file.getName());
                    intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);

                } else {
                    showDialog("Success", path);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    void showDialog(String title, String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ShowPostFromNotification.this);

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

    public void doSocialShare(Uri photoURI, String title) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
//            Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID +"/drawable/google_play_with_paintology");

            ArrayList<Uri> files = new ArrayList<Uri>();
            files.add(photoURI);
//            files.add(uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Paintology Community " + title);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sent_post));
            shareIntent.setType("*/*");
            Intent receiver = new Intent(ShowPostFromNotification.this, BroadcastShareFromPostDetail.class);
//        receiver.putExtra("test", "test");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ShowPostFromNotification.this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent chooser = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                chooser = Intent.createChooser(shareIntent, "Share To", pendingIntent.getIntentSender());
            } else {
                chooser = Intent.createChooser(shareIntent, "Share To");
            }
            startActivity(chooser);
        } catch (Exception e) {
            Log.e(PaintActivity.class.getName(), e.getMessage());
        }
    }


    public static class BroadcastShareFromPostDetail extends BroadcastReceiver {

        public BroadcastShareFromPostDetail() {
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


    boolean isAuthenticating = false;

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType, GoogleSignInAccount... accounts) {

        Log.e("TAGGG", "Add User Data userID " + model.user_id + " email " + model.user_email + " username " + model.user_name);
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_name != null) ? model.user_name : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");
        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, ShowPostFromNotification.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, ShowPostFromNotification.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, ShowPostFromNotification.this));

        try {

            String _ip = constants.getString(constants.IpAddress, ShowPostFromNotification.this);
            String _country = constants.getString(constants.UserCountry, ShowPostFromNotification.this);
            String _city = constants.getString(constants.UserCity, ShowPostFromNotification.this);

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

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
        Call<LoginResponseModel> call = apiInterface.addUserData(ApiClient.SECRET_KEY, _map);
        showProgress();
        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    isAuthenticating = false;
                    Log.e("TAGGG", "onResponse called");
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {
                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", ShowPostFromNotification.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", ShowPostFromNotification.this);

                            MainCollectionFragment.objHomeInterface.setUserID(response.body().getObjData().getUser_id() + "");

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", ShowPostFromNotification.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), ShowPostFromNotification.this);

                            constants.putString(constants.Username, model.user_name, ShowPostFromNotification.this);
                            constants.putString(constants.Password, model.user_id, ShowPostFromNotification.this);
                            constants.putString(constants.Email, model.user_email, ShowPostFromNotification.this);
                            constants.putString(constants.LoginInPaintology, "true", ShowPostFromNotification.this);

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, ShowPostFromNotification.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ShowPostFromNotification.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ShowPostFromNotification.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    isLoggedIn = true;
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ShowPostFromNotification.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (accounts != null && accounts.length > 0)
                                        account = accounts[0];
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(ShowPostFromNotification.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.GoogleRegistration);
                                    }
                                }
                            }
                        }

                        String _user_id = constants.getString(constants.UserId, ShowPostFromNotification.this);
                        fetchProfileData();
                        MyApplication.get_realTimeDbUtils(ShowPostFromNotification.this).autoLoginRegister(response.body().getObjData().getStatus());
                        if (KGlobal.isInternetAvailable(ShowPostFromNotification.this) && _user_id != null && !_user_id.isEmpty()) {
                            startService(new Intent(ShowPostFromNotification.this, SendDeviceToken.class));
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
                            isLoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ShowPostFromNotification.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ShowPostFromNotification.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.event_failed_to_adduser);
                        Toast.makeText(ShowPostFromNotification.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ShowPostFromNotification.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ShowPostFromNotification.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.event_failed_to_adduser);
                        Toast.makeText(ShowPostFromNotification.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(ShowPostFromNotification.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

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
                                Toast.makeText(ShowPostFromNotification.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.FacebookLoginSuccess);
                            // Application code
                            try {
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
            if (BuildConfig.DEBUG) {
                Toast.makeText(ShowPostFromNotification.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.FacebookLoginFailed);
        }
    };

    public void fetchProfileData() {
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        Observable<GetUserProfileResponse> profileResponse = apiInterface.getUserProfileData(ApiClient.SECRET_KEY, constants.getString(constants.UserId, ShowPostFromNotification.this));
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
                    constants.putString(constants.UserGender, constants.MALE, ShowPostFromNotification.this);
                } else if (getUserProfileResponse.getResponse().getGender() != null && getUserProfileResponse.getResponse().getGender().equalsIgnoreCase("female")) {
//                    iv_profile.setImageResource(R.drawable.profile_icon_female);
                    placeHolder = R.drawable.profile_icon_female;
                    constants.putString(constants.UserGender, constants.FEMALE, ShowPostFromNotification.this);
                } else {
                    placeHolder = R.drawable.profile_icon_male;
                    constants.putString(constants.UserGender, constants.MALE, ShowPostFromNotification.this);
//                    iv_profile.setImageResource(R.drawable.profile_icon_male);
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
    public void showHideFab(boolean needToShown) {

    }

    @Override
    public void openChatScreen(String key, String user_id, int pos) {

    }

    @Override
    public void openChatsScreen() {

    }

    @Override
    public void openUsersPostsListScreen(int pos) {

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


    @Override
    public ArrayList<Firebase_User> getFirebaseUserList() {

        return _lst_firebase_user;
    }

    @Override
    public void downloadImageOpenInOverlayCanvas(int pos) {

    }

    @Override
    public void downloadImageOpenInTraceCanvas(int pos) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_post_from_notify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_community:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(ShowPostFromNotification.this, constants.CLICK_COMMUNITY, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(ShowPostFromNotification.this, constants.CLICK_COMMUNITY);
                Intent intent = new Intent(ShowPostFromNotification.this, Community.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
