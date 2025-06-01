package com.paintology.lite.photorealistic.drawing.CustomePicker;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.Model.ReserveHashTag;
import com.paintology.lite.photorealistic.drawing.Model.ResponseModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.PostInterface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostDialogActivity extends AppCompatActivity implements PostInterface {

    RecyclerView recyclerView;
    public PostInterface ObjpostImage;
    show_selected_items_adapter mAdapter;
    StringConstants constants = new StringConstants();
    ArrayList<AlbumImage> _list;
    ApiInterface apiInterface;
    boolean isFromCanvasMode = false;

    StringConstants _constants = new StringConstants();

    ArrayList<String> _lst_hashTag;

    String isPostGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_post_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);

//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setVisibility(View.GONE);
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);

        String data = _constants.getString(_constants.hashTagList, this);
        if (data == null || data.isEmpty()) {
            getReservedHashTag();
        } else {
            Gson gson = new Gson();
            _lst_hashTag = gson.fromJson(data, ArrayList.class);
        }

        Intent intent = getIntent();
        // Retrieve the string data from the Intent
        isPostGallery = intent.getStringExtra("isPostGallery");


        ObjpostImage = this;
        recyclerView = (RecyclerView) findViewById(R.id.rv_post_image);
        if (getIntent().hasExtra("result")) {
            final ArrayList<String> selectedImage = getIntent().getStringArrayListExtra("result");
            setupRecyclerView(selectedImage);
        }
        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase("from_canvas")) {
            isFromCanvasMode = true;
        }

    }

    void setupRecyclerView(ArrayList<String> mediaList) {
        try {
            _list = new ArrayList<>();
//            ArrayList<String> _lst_art_fav = new ArrayList<>();
//            _lst_art_fav.add(getResources().getStringArray(R.array.arr_art_fav)[0]);

            String userArtAbility = constants.getString(constants.userAbilityFromPref, this);
            String userArtMed = constants.getString(constants.userArtMedFromPref, this);
            String userArtFav = constants.getString(constants.userArtFavFromPref, this);

            Log.e("TAGGG", "Preference Value Ability " + userArtAbility);
            Log.e("TAGGG", "Preference Value userArtMed " + userArtMed);
            Log.e("TAGGG", "Preference Value userArtFav " + userArtFav);

            ArrayList<String> _lst_art_fav = new ArrayList<>();
            ArrayList<String> _lst_art_med = new ArrayList<>();
            try {
                /*if (userArtMed != null && !userArtMed.isEmpty()) {
                    StringTokenizer stringTokenizer = new StringTokenizer(userArtMed, "|");
                    Log.e("TAGGG", "stringTokenizer " + stringTokenizer != null ? stringTokenizer.countTokens() + " " : " null token");
                    if (stringTokenizer != null) {
                        do {
                            _lst_art_med.add(getResources().getStringArray(R.array.art_medium)[Integer.parseInt(stringTokenizer.nextToken())]);
                            Log.e("TAGG", "NextToken Added");
                        } while (stringTokenizer.hasMoreTokens());
                    }
                    Log.e("TAGGG", "Total size of the are medium list " + _lst_art_med.size());
                } else*/

                _lst_art_med.add(getResources().getStringArray(R.array.art_medium)[getResources().getStringArray(R.array.art_medium).length - 1]);

            } catch (Exception e) {

            }
            try {
               /* if (userArtFav != null && !userArtFav.isEmpty()) {
                    StringTokenizer stringTokenizer = new StringTokenizer(userArtFav, "|");
                    Log.e("TAGGG", "stringTokenizer " + stringTokenizer != null ? stringTokenizer.countTokens() + " " : " null token");
                    if (stringTokenizer != null) {
                        do {
                            _lst_art_fav.add(getResources().getStringArray(R.array.arr_art_fav)[Integer.parseInt(stringTokenizer.nextToken())]);
                            Log.e("TAGG", "NextToken Added");
                        } while (stringTokenizer.hasMoreTokens());
                    }
                    Log.e("TAGGG", "Total size of the are medium list " + _lst_art_med.size());
                } else*/

                _lst_art_fav.add(getResources().getStringArray(R.array.arr_art_fav)[getResources().getStringArray(R.array.arr_art_fav).length - 1]);
            } catch (Exception e) {

            }

            try {
                for (int i = 0; i < mediaList.size(); i++) {
                    AlbumImage obj = new AlbumImage();

                    obj.setFilePath(mediaList.get(i));
                    String filename = new File(mediaList.get(i)).getName();
                    obj.setFileName(filename);

                    obj.setStr_art_fav(_lst_art_fav.get(0));
                    obj.setArtFavList(_lst_art_fav);

                    obj.setStr_art_med(_lst_art_med.get(0));
                    obj.setArtMediumList(_lst_art_med);

                    /*if (userArtAbility != null && !userArtAbility.isEmpty()) {
                        obj.setArt_ability(userArtAbility);
                    } else
                        */

                    obj.setArt_ability(getResources().getStringArray(R.array.arr_art_ability)[getResources().getStringArray(R.array.arr_art_ability).length - 1]);

                    _list.add(obj);
                }
            } catch (Exception e) {

            }
            try {
                mAdapter = new show_selected_items_adapter(isPostGallery,_list, PostDialogActivity.this, this, true, _lst_hashTag);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, VERTICAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new MarginDecoration(getApplicationContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            } catch (Exception e) {

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void postImage(int position, String title, String description, String hashTag, String _youtube_url) {
        File file = new File(_list.get(position).getFilePath());
        Log.e("TAGGG", "HashTag Of Post Before " + hashTag);
        StringBuilder builder = new StringBuilder();
        builder.append("#Paintology|");
        builder.append(hashTag);
        Log.e("TAGGG", "HashTag Of Post " + builder.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        HashMap<String, RequestBody> map = new HashMap<>();
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserId, PostDialogActivity.this));
        RequestBody image_description = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody image_hashtag = RequestBody.create(MediaType.parse("text/plain"), builder.toString());
        RequestBody req__youtube_url = RequestBody.create(MediaType.parse("text/plain"), _youtube_url);
        map.put("youtube_url", req__youtube_url);
        map.put("user_id", user_id);
        map.put("image_description", image_description);
        map.put("image_name", image_name);
        map.put("image_hashtag", image_hashtag);

        if (BuildConfig.DEBUG) {
            Toast.makeText(PostDialogActivity.this, constants.post_image_click, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PostDialogActivity.this, constants.post_image_click);
        Observable<ResponseModel> observable = apiInterface.postNewImage(ApiClient.SECRET_KEY, fileToUpload, map);

        _list.get(position).setYoutube_url(_youtube_url);
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
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PostDialogActivity.this, constants.post_image_click_success, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PostDialogActivity.this, constants.post_image_click_success);
//                    Toast.makeText(PostDialogActivity.this, "Posted To Community Successfully!", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PostDialogActivity.this);
                    builder1.setMessage(R.string.your_drawing_has_been_posted_to_the_community)
                            .setPositiveButton(R.string.see_it_now, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PostDialogActivity.this, Community.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (isFromCanvasMode)
                                        finish();
                                }
                            })
                            .show();
//                    if (isFromCanvasMode)
//                        finish();
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PostDialogActivity.this, constants.post_image_click_failed, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PostDialogActivity.this, constants.post_image_click_failed);
                    mAdapter.updateStatus(position, 0);
                }
            }

            @Override
            public void onError(Throwable e) {
                mAdapter.updateStatus(position, 0);
                Log.e("TAGGG", "onError Called " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void cancelClick() {
        finish();
    }

    @Override
    public boolean isReserevedUsed(String _string) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(_string, "|");
            if (_lst_hashTag == null && _lst_hashTag.size() == 0)
                return false;

            StringBuilder str_builder = new StringBuilder();
            do {
                String _token = tokenizer.nextToken().replace("#", "").trim();
                for (int i = 0; i < _lst_hashTag.size(); i++) {
                    if (_token.equalsIgnoreCase(_lst_hashTag.get(i))) {
                        str_builder.append("#" + _token + " ");
                        break;
                    }
                }
            } while (tokenizer.hasMoreTokens());

            Log.e("TAGGG", "Reserved Used " + str_builder.toString());
            if (str_builder.toString().isEmpty())
                return false;
            else {
                showHashTagDialog(str_builder.toString());
                return true;
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at reservetag " + e.getMessage());
        }

        return false;
    }

    @Override
    public void deletePost(int pos) {

    }

    @Override
    public void pickPhotos(int pos) {

    }


    void showHashTagDialog(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PostDialogActivity.this);

        builder1.setTitle(getResources().getString(R.string.reserved_hashtag));
        String _name = "<b>" + msg + "</b>";
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

    public void getReservedHashTag() {
        Log.e("TAGG", "getReservedHashTag called");
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);

        Observable<ReserveHashTag> observable = apiInterface.getReservedHashTag(ApiClient.SECRET_KEY);
        showProgress();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ReserveHashTag>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReserveHashTag reserveHashTag) {

                if (reserveHashTag != null) {
                    if (reserveHashTag.get_lst_hashTag() != null) {
                        Log.e("TAGG", "onNext Called " + reserveHashTag.get_lst_hashTag().size());
                        Gson gson = new Gson();
                        _lst_hashTag = reserveHashTag.get_lst_hashTag();
                        String arrayData = gson.toJson(reserveHashTag.get_lst_hashTag());
                        StringConstants _constants = new StringConstants();
                        _constants.putString(_constants.hashTagList, arrayData, PostDialogActivity.this);
                    }
                }
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError Called " + e.getMessage(), e);
                hideProgress();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    ProgressDialog progressDialog;

    void showProgress() {
        try {
            progressDialog = new ProgressDialog(PostDialogActivity.this);
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
}
