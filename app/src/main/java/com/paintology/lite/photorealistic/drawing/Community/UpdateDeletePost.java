package com.paintology.lite.photorealistic.drawing.Community;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Adapter.update_post_items_adapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.Model.ResponseDeletePost;
import com.paintology.lite.photorealistic.drawing.Model.ResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.UserPostFromApi;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.PostInterface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateDeletePost extends AppCompatActivity implements PostInterface {


    RecyclerView rv_post_list;
    StringConstants constants = new StringConstants();
    ApiInterface apiInterface;
    CircleProgressBar item_progress_bar;

    //    ProgressDialog progressDialog = null;
    boolean isLoading = false;
    UserPostFromApi _userPostList;

    String user_id = "0";
    int pageNumber = 1;
    int totalItem = 0;
    update_post_items_adapter mAdapter;

    int SELECT_PHOTO_REQUEST = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Posts");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num_of_delet == 0) {
                    finish();
                } else {
                    Intent _intent = new Intent();
                    _intent.putExtra("num_of_delet", num_of_delet);
                    setResult(Activity.RESULT_OK, _intent);
                    finish();
                }
            }
        });
        rv_post_list = (RecyclerView) findViewById(R.id.rv_my_posts);
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        item_progress_bar = findViewById(R.id.item_progress_bar);
        user_id = getIntent().getStringExtra("user_id");
        getList();

        rv_post_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.e("TAGGG", "isLoading " + isLoading + " Lst Pos " + mAdapter.getLastPosition() + " List Size " + _userPostList.getObjData().getPost_list().size() + " TotlaPost from list " + totalPost);
                if (!isLoading) {
                    if (mAdapter.getLastPosition() == _userPostList.getObjData().getPost_list().size() - 1 && _userPostList.getObjData().getPost_list().size() < totalItem) {
                        isLoading = true;
                        Log.e("TAGGG", "onScrolled Called goto getList ");
                        getList();
                    }
                }
            }
        });

        AppUtils.hideKeyboard(this);
    }

    public void getList() {
        try {
            HashMap<String, RequestBody> _map = new HashMap<>();
            RequestBody req_user_id = RequestBody.create(MediaType.parse("text/plain"), user_id);
            RequestBody req_page_number = RequestBody.create(MediaType.parse("text/plain"), pageNumber + "");
            RequestBody req_page_size = RequestBody.create(MediaType.parse("text/plain"), 10 + "");

            _map.put("user_id", req_user_id);
            _map.put("pagenumber", req_page_number);
            _map.put("size", req_page_size);

            Observable<UserPostFromApi> _observer;
            _observer = apiInterface.getMyPost(ApiClient.SECRET_KEY, _map);

//        _observer = apiInterface.getAllUsersPost(_map);
            item_progress_bar.setVisibility(View.VISIBLE);

            _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<UserPostFromApi>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(UserPostFromApi userPostFromApi) {

                    try {
                        item_progress_bar.setVisibility(View.GONE);
                        if (userPostFromApi == null || userPostFromApi.getCode() != 200 || userPostFromApi.getObjData() == null)
                            return;

                        if (userPostFromApi.getObjData().getPost_list().size() == 0) {
                            Toast.makeText(UpdateDeletePost.this, "Posts not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (_userPostList == null) {
                            try {
                                for (int i = 0; i < userPostFromApi.getObjData().getPost_list().size(); i++) {
                                    if (userPostFromApi.getObjData().getPost_list().get(i).getImage_hashtag() != null) {
                                        String hashtag = userPostFromApi.getObjData().getPost_list().get(i).getImage_hashtag().replace("|", " ");
                                        userPostFromApi.getObjData().getPost_list().get(i).setImage_hashtag(hashtag);
                                    }
                                }
                            } catch (Exception e) {

                            }

                            _userPostList = userPostFromApi;
                            totalItem = Integer.parseInt(_userPostList.getObjData().getTotal_posts());
                            pageNumber++;
                            fillRecyclerView(_userPostList);
                        } else {
                            pageNumber++;
                            for (int i = 0; i < userPostFromApi.getObjData().getPost_list().size(); i++) {
                                if (userPostFromApi.getObjData().getPost_list().get(i).getImage_hashtag() != null) {
                                    String hashtag = userPostFromApi.getObjData().getPost_list().get(i).getImage_hashtag().replace("|", " ");
                                    userPostFromApi.getObjData().getPost_list().get(i).setImage_hashtag(hashtag);
                                }
                            }
                            mAdapter.addNewData(_getList(userPostFromApi));
                        }
                        isLoading = false;

                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at getList " + e.getMessage(), e);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    isLoading = false;
                    Log.e("TAGGG", "onError " + e.getMessage(), e);
                    item_progress_bar.setVisibility(View.GONE);
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {

        }
    }

    List<AlbumImage> fileList = new ArrayList<>();


    public ArrayList<AlbumImage> _getList(UserPostFromApi _list) {
        ArrayList<AlbumImage> tempList = new ArrayList<>();

        String _lst_art_med[] = getResources().getStringArray(R.array.art_medium);
        String _lst_art_fav[] = getResources().getStringArray(R.array.arr_art_fav);
        String _lst_art_ability[] = getResources().getStringArray(R.array.arr_art_ability);
        try {
            if (_list != null && _list.getObjData() != null) {
                for (int i = 0; i < _list.getObjData().getPost_list().size(); i++) {
                    AlbumImage _object = new AlbumImage();
                    try {
                        if (_list.getObjData().getPost_list().get(i).getImage_Url() != null)
                            _object.setFilePath(_list.getObjData().getPost_list().get(i).getImage_Url());
                        if (_list.getObjData().getPost_list().get(i).getImage_title() != null) {
                            _object.setIv_caption(_list.getObjData().getPost_list().get(i).getImage_title());
                        }
                        if (_list.getObjData().getPost_list().get(i).getImage_description() != null) {
                            _object.setIv_description(_list.getObjData().getPost_list().get(i).getImage_description());
                        }
                        ArrayList<String> _art_ability = new ArrayList<>();
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int j = 0; j < _lst_art_med.length; j++) {
                                    if (token.equalsIgnoreCase(_lst_art_med[j])) {
                                        _art_ability.add(token);
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());
                            if (_art_ability.size() == 0) {
                                _art_ability.add(_lst_art_med[_lst_art_med.length - 1]);
                                _object.setStr_art_med(_art_ability.get(0));
                                _object.setArtMediumList(_art_ability);
                            } else {
                                _object.setArtMediumList(_art_ability);
                                _object.setStr_art_med(_art_ability.get(0));
                            }
                        } else {
                            _art_ability.add(_lst_art_med[_lst_art_med.length - 1]);
                            _object.setStr_art_med(_art_ability.get(0));
                            _object.setArtMediumList(_art_ability);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception " + e.getMessage());
                    }
                    try {
                        ArrayList<String> _art_fav = new ArrayList<>();
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int j = 0; j < _lst_art_fav.length; j++) {
                                    if (token.equalsIgnoreCase(_lst_art_fav[j])) {
                                        _art_fav.add(token);
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());
                            if (_art_fav.size() == 0) {
                                _art_fav.add(_lst_art_fav[_lst_art_fav.length - 1]);
                                _object.setStr_art_fav(_art_fav.get(0));
                                _object.setArtFavList(_art_fav);
                            } else {
                                _object.setArtFavList(_art_fav);
                                _object.setStr_art_fav(_art_fav.get(0));
                            }
                        } else {
                            _art_fav.add(_lst_art_fav[_lst_art_fav.length - 1]);
                            _object.setStr_art_fav(_art_fav.get(0));
                            _object.setArtFavList(_art_fav);
                        }
                        _object.setArt_ability(getResources().getStringArray(R.array.arr_art_ability)[getResources().getStringArray(R.array.arr_art_ability).length - 1]);

                    } catch (Exception e) {
                    }

                    try {
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");

                            boolean isFound = false;
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int k = 0; k < _lst_art_ability.length; k++) {
                                    if (token.equalsIgnoreCase(_lst_art_ability[k])) {
                                        _object.setArt_ability(token);
                                        isFound = true;
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());

                            if (!isFound) {
                                _object.setArt_ability(_lst_art_ability[_lst_art_ability.length - 1]);
                            }
                        } else {
                            _object.setArt_ability(_lst_art_ability[_lst_art_ability.length - 1]);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at setArt Ability " + e.getMessage());
                    }
                    tempList.add(_object);
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
        return tempList;
    }

    void fillRecyclerView(UserPostFromApi _list) {
        String _lst_art_med[] = getResources().getStringArray(R.array.art_medium);
        String _lst_art_fav[] = getResources().getStringArray(R.array.arr_art_fav);

        String _lst_art_ability[] = getResources().getStringArray(R.array.arr_art_ability);
        try {
            if (_list != null && _list.getObjData() != null) {
                for (int i = 0; i < _list.getObjData().getPost_list().size(); i++) {
                    AlbumImage _object = new AlbumImage();

                    _object.setPost_id(_list.getObjData().getPost_list().get(i).post_id);
                    try {
                        if (_list.getObjData().getPost_list().get(i).getImage_Url() != null)
                            _object.setFilePath(_list.getObjData().getPost_list().get(i).getImage_Url());
                        if (_list.getObjData().getPost_list().get(i).getImage_title() != null) {
                            _object.setIv_caption(_list.getObjData().getPost_list().get(i).getImage_title());
                        }
                        if (_list.getObjData().getPost_list().get(i).getImage_description() != null) {
                            _object.setIv_description(_list.getObjData().getPost_list().get(i).getImage_description());
                        }

                        if (_list.getObjData().getPost_list().get(i).getYoutube_url() != null && !_list.getObjData().getPost_list().get(i).getYoutube_url().isEmpty()) {
                            _object.setYoutube_url(_list.getObjData().getPost_list().get(i).getYoutube_url());
                        }

                        ArrayList<String> _art_ability = new ArrayList<>();
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int j = 0; j < _lst_art_med.length; j++) {
                                    if (token.equalsIgnoreCase(_lst_art_med[j])) {
                                        _art_ability.add(token);
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());
                            if (_art_ability.size() == 0) {
                                _art_ability.add(_lst_art_med[_lst_art_med.length - 1]);
                                _object.setStr_art_med(_art_ability.get(0));
                                _object.setArtMediumList(_art_ability);
                            } else {
                                _object.setArtMediumList(_art_ability);
                                _object.setStr_art_med(_art_ability.get(0));
                            }
                        } else {
                            _art_ability.add(_lst_art_med[_lst_art_med.length - 1]);
                            _object.setStr_art_med(_art_ability.get(0));
                            _object.setArtMediumList(_art_ability);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception " + e.getMessage());
                    }
                    try {
                        ArrayList<String> _art_fav = new ArrayList<>();
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int j = 0; j < _lst_art_fav.length; j++) {
                                    if (token.equalsIgnoreCase(_lst_art_fav[j])) {
                                        _art_fav.add(token);
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());
                            if (_art_fav.size() == 0) {
                                _art_fav.add(_lst_art_fav[_lst_art_fav.length - 1]);
                                _object.setStr_art_fav(_art_fav.get(0));
                                _object.setArtFavList(_art_fav);
                            } else {
                                _object.setArtFavList(_art_fav);
                                _object.setStr_art_fav(_art_fav.get(0));
                            }
                        } else {
                            _art_fav.add(_lst_art_fav[_lst_art_fav.length - 1]);
                            _object.setStr_art_fav(_art_fav.get(0));
                            _object.setArtFavList(_art_fav);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at parse " + e.getMessage());
                    }

                    try {
                        if (_list.getObjData().getPost_list().get(i).getImage_hashtag() != null && !_list.getObjData().getPost_list().get(i).getImage_hashtag().isEmpty()) {
                            StringTokenizer _token = new StringTokenizer(_list.getObjData().getPost_list().get(i).getImage_hashtag(), " ");
                            boolean isFound = false;
                            do {
                                String token = _token.nextToken().replace("#", "").trim();
                                for (int k = 0; k < _lst_art_ability.length; k++) {
                                    if (token.equalsIgnoreCase(_lst_art_ability[k])) {
                                        _object.setArt_ability(token);
                                        isFound = true;
                                        break;
                                    }
                                }
                            } while (_token.hasMoreTokens());

                            if (!isFound) {
                                _object.setArt_ability(_lst_art_ability[_lst_art_ability.length - 1]);
                            }
                        } else {
                            _object.setArt_ability(_lst_art_ability[_lst_art_ability.length - 1]);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at setArt Ability " + e.getMessage());
                    }

                    fileList.add(_object);
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }


        mAdapter = new update_post_items_adapter(fileList, UpdateDeletePost.this, this);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, VERTICAL, false);
        rv_post_list.setLayoutManager(mLayoutManager);
        rv_post_list.addItemDecoration(new MarginDecoration(UpdateDeletePost.this));
        rv_post_list.setHasFixedSize(true);
        rv_post_list.setAdapter(mAdapter);
    }

    @Override
    public void postImage(int position, String title, String description, String hashTag, String _youtube_url) {

        try {
            MultipartBody.Part fileToUpload = null;
            if (fileList.get(position).isLocalPath()) {
                File file = new File(fileList.get(position).getFilePath());
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            }

            HashMap<String, RequestBody> map = new HashMap<>();
            String post_id = _userPostList.getObjData().getPost_list().get(position).getPost_id();
            RequestBody req_post_id = RequestBody.create(MediaType.parse("text/plain"), post_id);
            RequestBody image_description = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody image_hashtag = RequestBody.create(MediaType.parse("text/plain"), hashTag);
            RequestBody req__youtube_url = RequestBody.create(MediaType.parse("text/plain"), _youtube_url);
            map.put("youtube_url", req__youtube_url);
            map.put("post_id", req_post_id);
            map.put("image_description", image_description);
            map.put("image_name", image_name);
            map.put("image_hashtag", image_hashtag);

            if (BuildConfig.DEBUG) {
                Toast.makeText(UpdateDeletePost.this, constants.community_post_edit_click, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_edit_click);
            Observable<ResponseModel> observable = apiInterface.updatePost(ApiClient.SECRET_KEY, fileToUpload, map);
            fileList.get(position).setYoutube_url(_youtube_url);
            mAdapter.notifyItemChanged(position);
            mAdapter.updateStatus(position, 1);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseModel>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(ResponseModel responseModel) {
                    Log.e("TAGGG", "OnNext Called " + responseModel.getStatus() + " responseModel.getData() " + responseModel.getDataAsString());
                    if (responseModel == null)
                        return;
                    if (responseModel.getDataAsString().equalsIgnoreCase("Success")) {
                        mAdapter.updateStatus(position, 2);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.updateStatus(position, 0);
                            }
                        }, 1000);

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(UpdateDeletePost.this, constants.community_post_update_success, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_update_success);
//                        Toast.makeText(UpdateDeletePost.this, "Posted To Community Successfully!", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdateDeletePost.this);
                        builder1.setMessage(R.string.your_drawing_has_been_posted_to_the_community)
                                .setPositiveButton(R.string.see_it_now, (dialog, which) -> {
//                                    Intent intent = new Intent(UpdateDeletePost.this, Community.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("post_id", fileList.get(position).getPost_id());

                                    Intent intent = new Intent(
                                            UpdateDeletePost.this,
                                            ShowPostFromNotification.class
                                    );
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                })
                                .setNegativeButton(R.string.ok_label, (dialog, which) -> {
                                    mAdapter.updateStatus(position, 0);
                                    dialog.dismiss();
                                })
                                .setOnDismissListener(dialog -> {

                                })
                                .show();
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(UpdateDeletePost.this, constants.community_post_update_failed, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_update_failed);
                        mAdapter.updateStatus(position, 0);
                        Toast.makeText(UpdateDeletePost.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mAdapter.updateStatus(position, 0);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(UpdateDeletePost.this, constants.community_post_update_failed, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_update_failed);
                    Log.e("TAGGG", "onError Called " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {
                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public void cancelClick() {

    }

    @Override
    public boolean isReserevedUsed(String _string) {
        return false;
    }

    int num_of_delet = 0;

    @Override
    public void deletePost(int pos) {

        HashMap<String, RequestBody> _map = new HashMap<>();
        String post_id = _userPostList.getObjData().getPost_list().get(pos).getPost_id();
        RequestBody req_post_id = RequestBody.create(MediaType.parse("text/plain"), post_id);

        _map.put("post_id", req_post_id);

        Observable<ResponseDeletePost> _observer = apiInterface.deletePost(ApiClient.SECRET_KEY, _map);
        showProgress();
        _observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseDeletePost>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseDeletePost _ResponseBase) {
                try {
                    if (_ResponseBase != null) {
                        if (_ResponseBase.getStatus().equalsIgnoreCase("OK") && _ResponseBase.getCode() == 200) {
                            if (_ResponseBase.get_objData() != null) {
                                Toast.makeText(UpdateDeletePost.this, _ResponseBase.get_objData().getStatus() + "", Toast.LENGTH_SHORT).show();
                            }
                            fileList.remove(pos);
                            mAdapter.notifyItemRemoved(pos);
                            num_of_delet = num_of_delet + 1;
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(UpdateDeletePost.this, constants.community_post_delete_success, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_delete_success);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(UpdateDeletePost.this, constants.community_post_delete_failed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_delete_failed);
                            Toast.makeText(UpdateDeletePost.this, _ResponseBase.getResponse() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                    hideProgress();
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at delet " + e.getMessage());
                    hideProgress();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(UpdateDeletePost.this, constants.community_post_delete_failed, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(UpdateDeletePost.this, constants.community_post_delete_failed);
                Log.e("TAGG", "OnError Called " + e.getMessage());
                hideProgress();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    int pickPhotoPosition;

    @Override
    public void pickPhotos(int pos) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickPhotoPosition = pos;
        startActivityForResult(intent, SELECT_PHOTO_REQUEST);
    }

    ProgressDialog progressDialog;

    void showProgress() {
        try {
            progressDialog = new ProgressDialog(UpdateDeletePost.this);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (num_of_delet == 0) {
            finish();
        } else {
            Intent _intent = new Intent();
            _intent.putExtra("num_of_delet", num_of_delet);
            setResult(Activity.RESULT_OK, _intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == SELECT_PHOTO_REQUEST) {
                if (data == null)
                    return;

                Uri imageUri = data.getData();
                String str14 = getPath(imageUri);

                fileList.get(pickPhotoPosition).setFilePath(str14);
                fileList.get(pickPhotoPosition).setLocalPath(true);
                mAdapter.notifyItemChanged(pickPhotoPosition);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
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

}
