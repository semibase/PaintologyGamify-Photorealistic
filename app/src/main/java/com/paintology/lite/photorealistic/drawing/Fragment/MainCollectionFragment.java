package com.paintology.lite.photorealistic.drawing.Fragment;

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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DatabaseHelperForCommunity;
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity;
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
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail;
import com.paintology.lite.photorealistic.drawing.Community.PostOperation;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;
import com.paintology.lite.photorealistic.drawing.Model.OperationAfterLogin;
import com.paintology.lite.photorealistic.drawing.Model.ResponseIncreaseCounter;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.Tooltip.Tooltip;
import com.paintology.lite.photorealistic.drawing.bottomsheet.CommunityCommentsBottomsheet;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
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
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.events.RefreshFavoriteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


public class MainCollectionFragment extends Fragment implements home_fragment_operation, PostOperation {

    View view;

    public static MainCollectionFragment fragment;

    ApiInterface apiInterface;
    StringConstants constants = new StringConstants();
    RecyclerView rv_1;
    String userID;
    GridLayoutManager mLayoutManager;
    CommunityPostAdapter mAdapter;
    CircleProgressBar item_progress_bar;

    public static home_fragment_operation objHomeInterface;
    ProgressDialog progressDialog = null;
    Button btn_try_agin;

    int pageNumber = 1;
    int totalItem = 0;

    SwipeRefreshLayout swipe_refresh;
    boolean isLoading = false;

    public static PostOperation _post_operation;

    LinearLayout ll_temp;
    FrameLayout frm_main;
    int lastPosition = 0;
    public static Boolean isTrack = false;


    RealTimeDBUtils realTimeDBUtils;
    ApiInterface apiService;


    FirebaseFirestore db_firebase;
    private DocumentSnapshot lastVisibleDocument;
    private boolean isLastPage = false;

    List<CommunityPost> posts = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    final int BATCH_SIZE = 10;

    private TextView HeadingTxt;
    private TextView DescriptionTxt;
    private ConstraintLayout layoutNoData;
    private ImageView EmptyDataImg;

    public boolean IsFromCommunityFavCalled;


    public MainCollectionFragment(boolean isFromCommunityFavCalled) {
        IsFromCommunityFavCalled = isFromCommunityFavCalled;
    }

    public MainCollectionFragment() {
        IsFromCommunityFavCalled = false;
    }

    public static MainCollectionFragment newInstance() {
        MainCollectionFragment fragment = new MainCollectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_collection, container, false);
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objHomeInterface = this;
        _post_operation = this;

        apiService = ApiClient.getClientNotification().create(ApiInterface.class);
        realTimeDBUtils = MyApplication.get_realTimeDbUtils(requireContext());

        ll_temp = view.findViewById(R.id.ll_temp);
        frm_main = view.findViewById(R.id.frm_main);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent);
        btn_try_agin = view.findViewById(R.id.btn_try_agin);
        rv_1 = view.findViewById(R.id.recycler_view);
        rv_1.addItemDecoration(new MarginDecoration(getActivity()));
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);

        HeadingTxt = view.findViewById(R.id.HeadingTxt);
        DescriptionTxt = view.findViewById(R.id.DescriptionTxt);
        layoutNoData = view.findViewById(R.id.layoutNoData);
        EmptyDataImg = view.findViewById(R.id.EmptyDataImg);

        item_progress_bar = view.findViewById(R.id.item_progress_bar);
        userID = constants.getString(constants.UserId, getActivity());
//        followUser("");


        fragment = this;


        if (IsFromCommunityFavCalled) {
            item_progress_bar.setVisibility(View.VISIBLE);

            showCommunityFavOfflinePosts();
            btn_try_agin.setVisibility(View.GONE);


        } else {


            db_firebase = FirebaseFirestore.getInstance();
            getAllUser();
            if (KGlobal.isInternetAvailable(getActivity())) {

                Toast.makeText(requireContext(), "255 ", Toast.LENGTH_SHORT).show();

                item_progress_bar.setVisibility(View.VISIBLE);

                fetchCommunityPosts();
                btn_try_agin.setVisibility(View.GONE);
            } else {

                btn_try_agin.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
            }


            if (Community.KeyFromCommunity == null) {
                rv_1.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            // Do something
                            Log.e("TAG", "RV States Scroll");
                            Community.obj_cmunity.hideSearchBar();
                        }
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
                                    fetchCommunityPosts(); // Load more data
                                }
                            }
                        }
                    }
                });

                btn_try_agin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (KGlobal.isInternetAvailable(getActivity())) {

                            fetchCommunityPosts();
                            btn_try_agin.setVisibility(View.GONE);
                        } else {
                            btn_try_agin.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
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
                        fetchCommunityPosts();
                    }
                });
            }


        }


        return view;
    }

    public void RemoveItem(int currentPos) {
        mAdapter.ItemRemoved(currentPos);
    }

    int currentPos;

    @Override
    public boolean changeListFormat(int rowCount) {

        rv_1.setLayoutManager(rowCount == 1 ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), rowCount, RecyclerView.VERTICAL, false));
        mAdapter.notifyList(rowCount);


       /* try {
            if (mLayoutManager != null && rv_1 != null) {

                currentPos = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (currentPos == -1) currentPos = mLayoutManager.findFirstVisibleItemPosition();

                mLayoutManager = new GridLayoutManager(getContext(), rowCount, RecyclerView.VERTICAL, false);
                rv_1.setLayoutManager(mLayoutManager);
                mAdapter.notifyList(rowCount);
                getActivity().runOnUiThread(() -> {
                    if (detailViewPosition != 0) {
                        rv_1.scrollToPosition(detailViewPosition);
                        detailViewPosition = 0;
                    } else rv_1.scrollToPosition(currentPos);
                });

                return true;
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }*/
        return true;
    }

    int detailViewPosition = 0;

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
/*
        if (BuildConfig.DEBUG) {
            Toast.makeText(getActivity(), constants.click_community_user_image, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(getActivity(), constants.click_community_user_image);
        Community.obj_cmunity.DisableAllView(1);
        changeListFormat(1);
        getActivity().runOnUiThread(() -> rv_1.scrollToPosition(pos));
        detailViewPosition = pos;
        increaseCounter(posts.get(pos).getPost_id());*/


        if (posts.get(pos).getPost_id() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("post_id", posts.get(pos).getPost_id());
            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
            ContextKt.sendUserEventWithParam(requireContext(), StringConstants.community_post_open, bundle);
        }
        Intent _intent = new Intent(requireContext(), CommunityDetail.class);
        _intent.putExtra("post_id", posts.get(pos).getPost_id());
        requireContext().startActivity(_intent);

    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {
       /* Intent intent = new Intent(getActivity(), CommunityDetail.class);
//        intent.putParcelableArrayListExtra("_list", sortedList);
        intent.putExtra("_list", _userPostList);
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
        try {
            ContextKt.showEnlargeImage(getContext(), _url);
        } catch (Exception e) {
            e.printStackTrace();
        }

     /*   if (IsFromCommunityFavCalled) {

            FavActivity.obj_cmunity.enlargeImageView(_url);

        } else {
            Community.obj_cmunity.enlargeImageView(_url);
        }
*/
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
       /* try {
            if (visible) {
                // ...
                obj_cmunity.ReflectColor(getResources().getColor(R.color.white));
            } else {
                obj_cmunity.ReflectColor(getResources().getColor(R.color.gray_color));
                Log.e("TAGG", "Invisible Main Collection Frag");
            }
        } catch (Exception e) {

        }*/
    }


/*
    public boolean checkUserExist(String _userID) {
        if (_user_list != null) {
            for (int i = 0; i < _user_list.size(); i++) {
                Log.e("TAG", "checkUserExist _userID " + _userID + " " + _user_list.get(i).getUser_id());
                if (_userID == _user_list.get(i).getUser_id()) {
                    return true;
                }
            }
        }

        return false;
    }*/

    @Override
    public void doOperationOnPost(int position, int operationType) {

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

        Log.e("TAGRR", posts.get(pos).getPost_id() + " " + totalViews);
        FirebaseFunctions.getInstance().getHttpsCallable("communityPost-view").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    if (posts.get(pos).getStatistic() != null) {
                        CommunityPost.Statistic statistic = posts.get(pos).getStatistic();
                        statistic.setViews(totalViews);
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

       /* UserPostList model = _userPostList.getObjData().getPost_list().get(pos);
        Log.e("TAGGG", "Like Operation isLiked " + model.isLiked() + " postId " + model.getPost_id());
        HashMap<String, RequestBody> _map = new HashMap<>();
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, getActivity()));
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
                            Toast.makeText(getActivity(), responseBase.getStatus() + "", Toast.LENGTH_SHORT).show();
                    } else {

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
    public void likeOperation(int pos, boolean isLike, boolean isFromSocialLogin) {


        String postID = String.valueOf(posts.get(pos).getPost_id());
        Map<String, Object> comments = new HashMap<>();
        comments.put("post_id", postID);

        if (AppUtils.isLoggedIn()) {
            FirebaseFunctions.getInstance().getHttpsCallable("communityPost-like").call(comments).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Liked added successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("CloudFunctions", "Liked added successfully");
                        if (posts.get(pos).getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", posts.get(pos).getPost_id());
                            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(requireContext(), StringConstants.community_post_like, bundle);
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
            if (BuildConfig.DEBUG) {
                Toast.makeText(requireContext(), constants.open_social_login_community, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), constants.open_social_login_community, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(requireContext(), constants.open_social_login_community_comment_dialog);
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void addComment(int pos, String comment, ArrayList<Firebase_User> _user_list) {
        System.out.println("Add Comment In MainFragment Screen");
        if (AppUtils.isLoggedIn()) {
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(getActivity(), "Please add comment!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = constants.getString(constants.UserId, requireContext());
            String userName = constants.getString(constants.Username, requireContext());
            String userAvatar = constants.getString(constants.ProfilePicsUrl, requireContext());
            String userCountry = constants.getString(constants.UserCountry, requireContext());
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
                        Toast.makeText(getActivity(), "Comment added successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("CloudFunctions", "Comment added successfully");
                        if (posts.get(pos).getPost_id() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", posts.get(pos).getPost_id());
                            bundle.putString("user_id", posts.get(pos).getAuthor().getUser_id());
                            ContextKt.sendUserEventWithParam(requireContext(), StringConstants.community_post_comment, bundle);
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
                            Toast.makeText(getActivity(), "Failed To Add Comment", Toast.LENGTH_SHORT).show();
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
            if (BuildConfig.DEBUG) {
                Toast.makeText(requireContext(), constants.open_social_login_community, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), constants.open_social_login_community, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(requireContext(), constants.open_social_login_community_comment_dialog);
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        }
    }


    private void sendMentionNotification(String receiver, String user_name, String msg, String post_id) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        Log.e("TAG", "sendNotification called msg " + msg);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
//                        if (realTimeDBUtils == null) {
//                        }
                        realTimeDBUtils = MyApplication.get_realTimeDbUtils(requireContext());

                        if (realTimeDBUtils.getCurrentUser() == null || realTimeDBUtils.getCurrentUser().getUid() == null)
                            return;
                        Data data = new Data(realTimeDBUtils.getCurrentUser().getUid(), R.mipmap.ic_launcher, msg, user_name, receiver, "true", post_id);

                        Sender sender = new Sender(data, token.getToken());
                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                Log.e("TAGG", "onResponse called " + response.toString());
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(getActivity(), "Notification Success: " + response.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e("TAG", "Notification onFailure " + t.getMessage(), t);
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(getActivity(), "Notification onFailure " + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at send notif " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean isLoggedIn(OperationAfterLogin _loginOperationModel) {
        if (constants.getBoolean(constants.IsGuestUser, getContext())) {
            FireUtils.openLoginScreen(getContext(), true);
            return false;
        } else {
            return true;
        }
       /* try {
            return Community.obj_cmunity.isLoggedIn(_loginOperationModel);
        } catch (Exception e) {
            return false;
        }*/
    }

    @Override
    public void downloadImage(int pos, boolean NeedToopenInCanvas) {
        try {

            if (posts.get(pos).getImages().getContent() != null) {

                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(getActivity()), fileName);
                if (imageFile.exists()) {
                    if (NeedToopenInCanvas) {
                        Intent intent = new Intent(getActivity(), PaintActivity.class);
                        intent.setAction("LoadWithoutTrace");
                        intent.putExtra("path", imageFile.getName());
                        intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                        intent.putExtra("isPickFromOverlaid", true);
                        startActivity(intent);
                    } else showDialog("File already saved", imageFile.getAbsolutePath());
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), removeQueryString(fileName), false, post_titel, null, NeedToopenInCanvas, pos, false, false).execute();
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
            String videoUrl = posts.get(pos).getLinks().getYoutube();

            String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
            if (fileName.contains("?")) {
                String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                fileName = beforeQuestionMark.replace("%2F", "_");
            }
            String title = posts.get(pos).getTitle();
            File imageFile = new File(KGlobal.getDownloadPath(getActivity()), fileName);
            if (imageFile.exists()) {
                System.out.println("imageFile :" + imageFile.getPath());
                Uri photoURI = FileProvider.getUriForFile(getActivity(), getString(R.string.authority), imageFile);
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

            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", posts.get(pos).getImages().getContent());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void reportPost(int pos) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.report_icon);
        builderSingle.setTitle("Report this post");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
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

    int commentPage = 1;
    int totalComment;

    @Override
    public void view_all_comment(int pos) {
        CommunityCommentsBottomsheet communityCommentsBottomsheet = CommunityCommentsBottomsheet.newInstance(posts.get(pos).getPost_id().toString());
        communityCommentsBottomsheet.show(requireFragmentManager(), "CommunityDetail");
    }

    @Override
    public void seachByHashTag(String tag) {
        Intent _intent = new Intent(getActivity(), CommunityDetail.class);
        _intent.putExtra("hashtag", tag);
        startActivity(_intent);
    }


    class MarginDecoration extends RecyclerView.ItemDecoration {
        private int margin;

        public MarginDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin_feed);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }
    }

    class DownloadImageFromURL extends AsyncTask<Void, Void, String> {
        String imageURL, fileName, title, videoUrl;
        Boolean isFromShare = false, NeedToopenInCanvas, OpenInTraceCanvas, OpenInOverlayCanvas;
        int position;
        File imageFile;

        public DownloadImageFromURL(String imageURL, String fileName, Boolean isFromShare, String post_title, String videoUrl, boolean NeedToopenInCanvas, int pos, boolean OpenInTraceCanvas, boolean OpenInOverlayCanvas) {
            this.imageURL = imageURL;
            this.fileName = fileName;
            this.isFromShare = isFromShare;
            this.videoUrl = videoUrl;
            title = post_title;
            this.NeedToopenInCanvas = NeedToopenInCanvas;
            this.OpenInTraceCanvas = OpenInTraceCanvas;
            this.OpenInOverlayCanvas = OpenInOverlayCanvas;
            Toast.makeText(getActivity(), "downloading started!", Toast.LENGTH_SHORT).show();
            position = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
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
            File file = new File(KGlobal.getDownloadPath(getActivity()), fileName);
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
                    Uri photoURI = FileProvider.getUriForFile(getActivity(), getString(R.string.authority), new File(path));
                    doSocialShare(position, photoURI, title, videoUrl);
                } else if (NeedToopenInCanvas && OpenInOverlayCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent intent = new Intent(requireContext(), PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", file.getName());
                    intent.putExtra("ParentFolderPath", file.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else if (NeedToopenInCanvas && OpenInTraceCanvas) {
                    Log.e("TAGGG", "receivedType path " + path);
                    File file = new File(path);
                    Intent lIntent1 = new Intent(requireContext(), PaintActivity.class);
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
                        Toast.makeText(requireContext(), "Saved in your Images folder", Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(requireContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    void showDialog(String title, String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

        builder1.setTitle(title);
        String _name = "File Stored In " + "<b>" + path + "</b>";
        builder1.setMessage(Html.fromHtml(_name));
        builder1.setCancelable(true);

        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
////            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
////            Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID +"/drawable/google_play_with_paintology");
//            ArrayList<Uri> files = new ArrayList<Uri>();
//            files.add(photoURI);
////            files.add(uri);
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Paintology Community " + title);
//            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sent_post));
//            shareIntent.setType("*/*");
//            Intent receiver = new Intent(getActivity(), BroadcastShareFromCommunity.class);
////        receiver.putExtra("test", "test");
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent pendingIntent;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, receiver, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//            } else {
//                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            Intent chooser;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                chooser = Intent.createChooser(shareIntent, "Share To", pendingIntent.getIntentSender());
//            } else {
//                chooser = Intent.createChooser(shareIntent, "Share To");
//            }
////            shareRewardPoint();
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

    public static class BroadcastShareFromCommunity extends BroadcastReceiver {

        public BroadcastShareFromCommunity() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGG", "OnReceived Called");
            try {
                Log.e("TAGG", "Key " + intent.getExtras().keySet());

                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            String _app_name = " " + extras.get(key);
                            String shareFileVia = "";
                            Log.d("BroadcastReceiver", String.format("%s %s (%s)", key, _app_name.toString(), _app_name.getClass().getName()));

                            if (_app_name.contains("skype")) shareFileVia = "skype";
                            else if (_app_name.contains("apps.photos")) shareFileVia = "photos";
                            else if (_app_name.contains("android.gm")) shareFileVia = "gmail";
                            else if (_app_name.contains("apps.docs")) shareFileVia = "drive";
                            else if (_app_name.contains("messaging")) shareFileVia = "messages";
                            else if (_app_name.contains("android.talk")) shareFileVia = "hangout";
                            else if (_app_name.contains("xender")) shareFileVia = "xender";
                            else if (_app_name.contains("instagram")) shareFileVia = "instagram";
                            else if (_app_name.contains("youtube")) shareFileVia = "youtube";
                            else if (_app_name.contains("maps")) shareFileVia = "maps";
                            else if (_app_name.contains("bluetooth")) shareFileVia = "bluetooth";
                            else if (_app_name.contains("facebook")) shareFileVia = "facebook";
                            else if (_app_name.contains("whatsapp")) shareFileVia = "whatsapp";
                            else if (_app_name.contains("com.facebook.orca"))
                                shareFileVia = "facebook_messager";
                            else if (_app_name.contains("linkedin")) shareFileVia = "linkedin";
                            else if (_app_name.contains("sketch")) shareFileVia = "sketchapp";
                            else if (_app_name.contains("mail.compose")) {
                                shareFileVia = "email";
                            } else shareFileVia = _app_name;

                            Log.e("TAGGG", "share_image_via " + shareFileVia + " ");
                            FirebaseUtils.logEvents(context, "community_share_image_via_" + shareFileVia);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(context, "community_share_image_via_" + shareFileVia, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.d("BroadcastReceiver", "Extras are null");
                    }
                } else {
                    Log.d("BroadcastReceiver", "Intent is null");
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception while share image " + e.getMessage(), e);
            }
        }
    }

    void sendSpamReport(int pos, String postId, String msg) {

       /* HashMap<String, RequestBody> _map = new HashMap<>();
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
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
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
            ContextKt.sendUserEventWithParam(requireContext(), StringConstants.community_post_open_author, bundle);
            FireUtils.openProfileScreen(getActivity(), posts.get(pos).getUser_id());
        } else {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        userID = constants.getString(constants.UserId, getActivity());
    }

    private int tipSizeSmall;
    private int tipSizeRegular;
    private int tipRadius;
    private int tooltipColor;


    Tooltip tooltip;

    private void showPlus_tooltip(@NonNull View anchor, ViewGroup parent) {
        /*
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.plus_icon_tooltip_layout, null);

        TextView tv = (TextView) content.findViewById(R.id.tv_title);
        tv.setText("Search Hashtags");
        Log.e("TAGGG", "Content is " + (content == null) + " view is " + (content == null));
        tipSizeSmall = getActivity().getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipSizeRegular = getActivity().getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipRadius = getActivity().getResources().getDimensionPixelOffset(R.dimen.tip_radius);
        tooltipColor = ContextCompat.getColor(getActivity(), R.color.background_color);
        try {
            tooltip = new Tooltip.Builder(getActivity())
                    .anchor(anchor, Tooltip.TOP)
                    .animate(new TooltipAnimation(TooltipAnimation.REVEAL, 300))
                    .autoAdjust(true)
                    .cancelable(false)
                    .content(content)
                    .withTip(new Tooltip.Tip(tipSizeRegular, tipSizeRegular, tooltipColor))
                    .withPadding(getActivity().getResources().getDimensionPixelOffset(R.dimen.menu_tooltip_padding))
                    .into(parent)
                    .autoCancel(3000)
                    .show();

        } catch (Exception e) {
            Log.e("TAGGG", "Exception " + e.getMessage());
        }*/
    }

    @Override
    public void showHideFab(boolean needToShown) {
        Community.obj_cmunity.showHideFab(needToShown);
    }


    boolean is_found_in_delete = false;
    boolean is_found_in_block = false;

    @Override
    public void openChatsScreen() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(getContext(), constants.user_chats, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(getContext(), constants.user_chats);
        Intent intent = new Intent(getContext(), ChatUserList.class);
        startActivity(intent);
    }

    @Override
    public void openUsersPostsListScreen(int pos) {
        System.out.println("openUsersPostsListScreen" + posts.get(pos).getUser_id());
//        db_firebase.collection("users").document(posts.get(pos).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        String quora = document.getString("external_id");
//                        System.out.println("external_id: " + quora);
//
//                        Intent _intent = new Intent(requireContext(), CommunityDetail.class);
//                        _intent.setAction("isFromProfile");
//
//                        String user_ID = "";
//                        Log.i("CommunityDetail", "quora: "+quora);
//                        if (quora == null) {
//                            user_ID = posts.get(pos).getUser_id();
//                        }
//                        else {
//                            user_ID = quora;
//                        }
//                    } else {
//                        System.out.println("No such document!");
//                    }
//                } else {
//                    System.out.println("Get failed with " + task.getException());
//                }
//            }
//        });

        Intent _intent = new Intent(requireContext(), CommunityDetail.class);
//                        _intent.setAction("isFromProfile");

        Log.i("CommunityDetail", "user_ID: " + posts.get(pos).getUser_id());
        Log.i("CommunityDetail", "Post_id: " + posts.get(pos).getPost_id());
        _intent.putExtra("user_id", posts.get(pos).getUser_id());
        _intent.putExtra("user_name", posts.get(pos).getAuthor().getName());
        requireContext().startActivity(_intent);
    }

    @Override
    public void openChatScreen(String key, String user_id, int position) {
        new ChatUtils(getContext()).openChatScreen(key, user_id);
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

    private void fetchCommunityPosts() {
        System.out.println("fetchCommunityPosts"); // Number of posts to load in each batch

        // If it's the first page, start from the beginning
        com.google.firebase.firestore.Query query;
        if (lastVisibleDocument == null) {
            query = db_firebase.collection("community_posts")
                    .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(BATCH_SIZE);
        } else { // Otherwise, start from the last visible document
            query = db_firebase.collection("community_posts")
                    .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING).startAfter(lastVisibleDocument).limit(BATCH_SIZE);
        }
//        com.google.firebase.firestore.Query query;
//        if (lastVisibleDocument == null) {
//            query = db_firebase.collection("community_posts_staging")
//                    .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(BATCH_SIZE);
//        } else { // Otherwise, start from the last visible document
//            query = db_firebase.collection("community_posts_staging")
//                    .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING).startAfter(lastVisibleDocument).limit(BATCH_SIZE);
//        }
//        isLoading = true;
//        List<CommunityPost> postsList = new ArrayList<>();
//
//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if (!queryDocumentSnapshots.isEmpty()) {
//                    lastVisibleDocument = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
//
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//                        CommunityPost post = document.toObject(CommunityPost.class);
//
//                        if (postsList != null) {
//                            System.out.println("document.getId() :" + document.getId());
//                            System.out.println("document :" + new Gson().toJson(post));
//                            post.setPost_id(document.getId());
//                            postsList.add(post);
//                        }
//                    }
//
//                    if (queryDocumentSnapshots.size() < BATCH_SIZE) {
//                        isLastPage = true; // Reached the last page
//                    }
//                } else {
//                    isLastPage = true; // Reached the last page
//                }
//
//                isLoading = false;
//                item_progress_bar.setVisibility(View.GONE);
//                if (!swipe_refresh.isRefreshing()) {
//                    if (posts != null && !posts.isEmpty()) {
//                        mAdapter.addNewData(postsList);
//                    } else {
//                        posts.addAll(postsList);
//                        fillRecyclerView(posts);
//                        swipe_refresh.setRefreshing(false);
//                    }
//                } else {
//                    swipe_refresh.setRefreshing(false);
//                    posts.clear();
//                    posts.addAll(postsList);
//                    fillRecyclerView(posts);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                isLoading = false;
//                System.out.println("Error getting documents: " + e);
//            }
//        });

        fetchCommunityPostsFromFirebaseFunctions();

    }

    void showCommunityFavOfflinePosts() {
        DatabaseHelperForCommunity db = new DatabaseHelperForCommunity(requireContext());
        List<CommunityPost> listOfPosts = new ArrayList<>();
        listOfPosts = db.getAllCommunityPosts();

        if (listOfPosts.isEmpty()) {
            btn_try_agin.setVisibility(View.INVISIBLE);
            rv_1.setVisibility(View.INVISIBLE);
            swipe_refresh.setVisibility(View.INVISIBLE);
            item_progress_bar.setVisibility(View.INVISIBLE);
            ll_temp.setVisibility(View.INVISIBLE);
            layoutNoData.setVisibility(View.VISIBLE);
            HeadingTxt.setText(getString(R.string.no_favorites_added));
            EmptyDataImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_post));
            DescriptionTxt.setText(getResources().getString(R.string.you_don_t_have_any_favorite_posts_yet_start_exploring_and_adding_your_top_picks));
        } else {
            MainCollectionFragment.this.posts = listOfPosts;
            fillRecyclerView(listOfPosts);
            item_progress_bar.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFavoriteEvent event) {
        if (IsFromCommunityFavCalled && event.getPage() == 3) {
            refreshFavOfflinePOsts();
        }
    }

    public void refreshFavOfflinePOsts() {
        DatabaseHelperForCommunity db = new DatabaseHelperForCommunity(requireContext());
        List<CommunityPost> listOfPosts = new ArrayList<>();
        listOfPosts = db.getAllCommunityPosts();

        if (listOfPosts.isEmpty()) {
            if (IsFromCommunityFavCalled) {
                Objects.requireNonNull(FavActivity.favActivity.binding.tabLayout.getTabAt(3)).setText("Community");
            }
            btn_try_agin.setVisibility(View.INVISIBLE);
            rv_1.setVisibility(View.INVISIBLE);
            swipe_refresh.setVisibility(View.INVISIBLE);
            item_progress_bar.setVisibility(View.INVISIBLE);
            ll_temp.setVisibility(View.INVISIBLE);
            layoutNoData.setVisibility(View.VISIBLE);
            HeadingTxt.setText(getString(R.string.no_favorites_added));
            EmptyDataImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_post));
            DescriptionTxt.setText(getResources().getString(R.string.you_don_t_have_any_favorite_posts_yet_start_exploring_and_adding_your_top_picks));
        } else {
            MainCollectionFragment.this.posts = listOfPosts;
            fillRecyclerView(listOfPosts);
            item_progress_bar.setVisibility(View.GONE);
        }
    }

    void fillRecyclerView(List<CommunityPost> lst) {

        if (IsFromCommunityFavCalled) {
            FavActivity.favActivity.binding.tabLayout.getTabAt(3).setText("Community" + " (" + lst.size() + ")");
//            FavActivity.favActivity.binding.tabLayout.getTabAt(3).getOrCreateBadge().setBadgeTextColor(getContext().getColor(R.color.white));
        }

        try {
            for (int i = 0; i < lst.size(); i++) {
                if (lst.get(i).getTags() != null && !lst.get(i).getTags().isEmpty()) {
                    try {
                        String fileName = lst.get(i).getImages().getContent().substring(lst.get(i).getImages().getContent().lastIndexOf('/') + 1);
                        File imageFile = new File(KGlobal.getDownloadPath(getActivity()), fileName);
                        if (imageFile.exists()) {
                            posts.get(i).setDownloaded(true);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (mAdapter == null) {
//            mAdapter = new ShowCommunityListAdapter(lst, getActivity(), this, this);
                mAdapter = new CommunityPostAdapter(lst, getActivity(), this, this, IsFromCommunityFavCalled);
                int type = 1;
                try {
                    type = (constants.getInt(constants.formatType, getActivity()) == 0 ? 1 : constants.getInt(constants.formatType, getActivity()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mLayoutManager = new GridLayoutManager(getContext(), type, RecyclerView.VERTICAL, false);
                rv_1.setLayoutManager(mLayoutManager);
                rv_1.addItemDecoration(new MarginDecoration(getActivity()));
                //    rv_1.setHasFixedSize(true);
                rv_1.setAdapter(mAdapter);
                rv_1.setItemViewCacheSize(100);
                mAdapter.notifyList(type);

                if (type == 1)
                    Toast.makeText(getActivity(), "double tap to enlarge!", Toast.LENGTH_SHORT).show();
//            mAdapter.notifyDataSetChanged();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv_1.scrollToPosition(0);
                    }
                });
//            showPlus_tooltip(ll_temp, frm_main);

            } else {
                mAdapter.refresh(lst);
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at fill data " + e.getMessage(), e);
        }
    }


    void fetchCommunityPostsFromFirebaseFunctions() {
        Log.d("CommunityPosts", "page : " + pageNumber);
        Map<String, Object> data = new HashMap<>();


        if (Community.KeyFromCommunity != null) {

            data.put("filter_by", "id:[" + Community.KeyFromCommunity + "]");

        } else {
            data.put("page", pageNumber);
            data.put("q", "");
            data.put("per_page", 20);
        }

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

                        Log.d("CommunityPostsCommunity", "Post Data By One By One: " + posts);


                        if (posts != null) {
                            List<CommunityPost> listOfPosts = new ArrayList<>();
                            for (Map<String, Object> post : posts) {
                                Gson gson = new Gson();
                                Log.d("CommunityPosts", "Post Data By One By One: " + gson.toJson(post));
                                if (post != null) {
                                    listOfPosts.add(new CommunityPost(post));
                                }
                                Log.d("CommunityPosts", "Post Data By List: " + gson.toJson(listOfPosts));
                                if (!posts.isEmpty() && post.equals(posts.get(posts.size() - 1))) {
                                    Log.d("CommunityPosts", "This is the last added post");
                                }
                            }
                            isLoading = false;
                            if (pageNumber == 1) {
                                MainCollectionFragment.this.posts = listOfPosts;
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
                    // Handle the error
                    Log.e("CommunityPosts", "Error fetching community posts", e);
                    try {
                        Toast.makeText(requireContext(), "Error fetching community posts", Toast.LENGTH_SHORT).show();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public ArrayList<Firebase_User> getFirebaseUserList() {

        return _lst_firebase_user;
    }

    @Override
    public void downloadImageOpenInOverlayCanvas(int pos) {
        try {

            boolean isStoragePassed = false;
            isTrack = false;
            if (pos == 0) {
                pos = lastPosition;
            } else {
                lastPosition = pos;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(getContext())) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(getActivity(), 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(getActivity(), 1);
                return;
            }


            if (posts.get(pos).getImages().getContent() != null) {
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(requireContext()), fileName);
                System.out.println("imageFile.exists() " + imageFile.exists());
                if (imageFile.exists()) {
                    Intent intent = new Intent(getActivity(), PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", imageFile.getName());
                    intent.putExtra("ParentFolderPath", imageFile.getParentFile().getAbsolutePath());
                    intent.putExtra("isPickFromOverlaid", true);
                    startActivity(intent);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel, null, true, pos, false, true).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    @Override
    public void downloadImageOpenInTraceCanvas(int pos) {
        try {

            isTrack = true;
            if (pos == 0) {
                pos = lastPosition;
            } else {
                lastPosition = pos;
            }
            boolean isStoragePassed = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(getContext())) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(getActivity(), 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(getActivity(), 1);
                return;
            }

            if (posts.get(pos).getImages().getContent() != null) {
                System.out.println("Content Image Url" + posts.get(pos).getImages().getContent());
                String fileName = posts.get(pos).getImages().getContent().substring(posts.get(pos).getImages().getContent().lastIndexOf('/') + 1);
                if (fileName.contains("?")) {
                    String beforeQuestionMark = fileName.substring(0, fileName.indexOf('?'));
                    fileName = beforeQuestionMark.replace("%2F", "_");
                }
                String post_titel = posts.get(pos).getTitle();
                File imageFile = new File(KGlobal.getDownloadPath(requireContext()), fileName);
                if (imageFile.exists()) {
                    Intent lIntent1 = new Intent();
                    lIntent1.setClass(requireContext(), PaintActivity.class);
                    lIntent1.setAction("Edit Paint");
                    lIntent1.putExtra("FromLocal", true);
                    lIntent1.putExtra("paint_name", imageFile.getName());
                    lIntent1.putExtra("isOverraid", false);
                    startActivity(lIntent1);
                } else {
                    new DownloadImageFromURL(posts.get(pos).getImages().getContent(), fileName, false, post_titel, null, true, pos, true, false).execute();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    private void commentRewardPoint(String postId) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_comment, postId);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(requireContext());
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_comment", rewardSetup.getCommunity_post_comment() == null ? 0 : rewardSetup.getCommunity_post_comment(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void likeRewardPoint(String postId) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_like, postId);
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(requireContext());
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_like", rewardSetup.getCommunity_post_like() == null ? 0 : rewardSetup.getCommunity_post_like(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    private void shareRewardPoint(String postId) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.community_post_share, postId);
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(requireContext());
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("community_post_share", rewardSetup.getCommunity_post_share() == null ? 0 : rewardSetup.getCommunity_post_share(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }
}
