package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.paintology.lite.photorealistic.drawing.Activity.MyConstantsKt;
import com.paintology.lite.photorealistic.drawing.Activity.favourite.TutorialDbHelper;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils;
import com.paintology.lite.photorealistic.drawing.Adapter.SubCategoryAdapterAll;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type;
import com.paintology.lite.photorealistic.drawing.Model.BannerModel;
import com.paintology.lite.photorealistic.drawing.Model.Category;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialcategory;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialimages;
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBannerDrawBinding;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.Model.CategoryModel;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.Overlaid;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData;
import com.paintology.lite.photorealistic.drawing.Model.sizes;
import com.paintology.lite.photorealistic.drawing.Model.text_files;
import com.paintology.lite.photorealistic.drawing.Model.trace_image;
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.helpers.ShareBroadcast;
import com.paintology.lite.photorealistic.drawing.interfaces.SubCategoryItemClickListener;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.ColorSwatchDao;
import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSubCategoryActivity extends AppCompatActivity implements SubCategoryItemClickListener {

    private HorizontalScrollView mHorizontalScrollView;
    private ChipGroup cg_subcategory;
    private TextView tv_cate_name;
    private String cate_name;
    private int selectedChipID;
    private String cateId;
    private String level = "";
    private List<CategoryModel.categoryData> childList;
    private List<Category> childListNew;
    private ApiInterface apiInterface;
    private HashMap<Integer, List<GetCategoryPostModel.postData>> mListHashMap = new HashMap<>();
    private RecyclerView rvTutorialList;
    private static final String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    private AppDatabase db;
    private List<GetCategoryPostModel.postData> listAll;
    private ProgressDialog progressDialog;
    private int total_tutorials;
    private String subCatId;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String subCatTitle;

    String mainId = "";
    int pageNo = 1;
    private SubCategoryAdapterAll mSubCategoryAdapterAll;
    List<Tutorialdatum> _list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sub_category);

        rvTutorialList = findViewById(R.id.rv_tutorial_list);
        mHorizontalScrollView = findViewById(R.id.subcategory_list);
        cg_subcategory = findViewById(R.id.cg_subcategory);
        tv_cate_name = findViewById(R.id.tv_category_name);
        tv_cate_name.setAllCaps(false);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);
        // database
        db = MyApplication.getDb();


        if (getIntent().hasExtra("cate_id") && getIntent().getStringExtra("cate_id") != null)
            cateId = getIntent().getStringExtra("cate_id");

        if (getIntent().hasExtra("level") && getIntent().getStringExtra("level") != null) {
            level = getIntent().getStringExtra("level");
        }


        if (getIntent().hasExtra("cate_name") && getIntent().getStringExtra("cate_name") != null) {
            cate_name = getIntent().getStringExtra("cate_name");
            tv_cate_name.setText(Html.fromHtml(cate_name));
        }

        if (getIntent().hasExtra("sub_cat_id") && getIntent().getStringExtra("sub_cat_id") != null) {
            subCatId = getIntent().getStringExtra("sub_cat_id");
        } else {
            subCatId = null;
        }

        if (getIntent().hasExtra("childs") && getIntent().getStringExtra("childs") != null) {
            String childJson = getIntent().getStringExtra("childs");
            Gson gson = new Gson();
            childList = gson.fromJson(childJson, ArrayList.class);
            childListNew = gson.fromJson(childJson, ArrayList.class);

        }
        MyConstantsKt.checkForIntroVideo(this, StringConstants.intro_tutorials);
        ArrayList<BannerModel> data = AppUtils.getTutBanners(this);
        for (int i = 0; i < data.size(); i++) {
            LayoutBannerDrawBinding bannerBinding = LayoutBannerDrawBinding.inflate(getLayoutInflater());
            Picasso.get().load(Uri.parse(data.get(i).bannerImageUrl)).into(bannerBinding.ivOwnAdv);
            int finalI = i;
            bannerBinding.ivOwnAdv.setOnClickListener(v -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(NewSubCategoryActivity.this,
                                StringConstants.constants.ad_XX_tutorial_banner_click.replace("XX", String.valueOf(finalI)),
                                Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(
                            NewSubCategoryActivity.this,
                            StringConstants.constants.ad_XX_tutorial_banner_click.replace("XX", String.valueOf(finalI))
                    );
                    KGlobal.openInBrowser(NewSubCategoryActivity.this, data.get(finalI).bannerLInk);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            });

            ((LinearLayout) findViewById(R.id.llAds)).addView(bannerBinding.getRoot());
        }

        if (getIntent().hasExtra("total_tutorials")) {
            total_tutorials = getIntent().getIntExtra("total_tutorials", 0);
        }
        Log.e("total", total_tutorials + "");

        cg_subcategory.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                Chip chip = cg_subcategory.findViewById(checkedId);
                if (chip != null) {
                    selectedChipID = checkedId;
                    String title = chip.getText().toString();
                    String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");


                    if (cat.length() >= 17) {
                        cat = cat.substring(0, 16);
                    }

                    title = title.replaceAll("[^a-zA-Z0-9]", "_");

                    if (title.length() >= 17) {
                        title = title.substring(0, 16);
                    }

                    String eventName = cat + "_" + title;
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(NewSubCategoryActivity.this, eventName, Toast.LENGTH_SHORT).show();
                        FirebaseUtils.logEvents(NewSubCategoryActivity.this, eventName);
                    }

//                        List<GetCategoryPostModel.postData> list = mListHashMap.get(checkedId);
                    title = chip.getText().toString();
                    // reloadData(checkedId, title);

                    if (checkedId == getString(R.string.key_all).hashCode()) {
                        isLastPage = false;
                        pageNo = 1;
                        _list.clear();
                        callTutorialsFirebase(cateId);
                    } else {
                        isLastPage = false;
                        pageNo = 1;
                        _list.clear();
                        Object item = this.childListNew.get(checkedId);
                        LinkedTreeMap<Object, Object> t = (LinkedTreeMap) item;
                        String id = t.get("id").toString();
                        Log.e("child id", id);
                        callTutorialsFirebase(id);
                    }
                }

            }


        });


        mSubCategoryAdapterAll = new SubCategoryAdapterAll(_list, NewSubCategoryActivity.this, NewSubCategoryActivity.this, true, cate_name);
        rvTutorialList.setAdapter(mSubCategoryAdapterAll);
        rvTutorialList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition =
                        layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                    ) {
                        pageNo++;
                        callTutorialsFirebase(mainId);
                    }
                }
            }
        });

        progressDialog = new ProgressDialog(NewSubCategoryActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setMessage("Loading Tutorials...");
        progressDialog.setCanceledOnTouchOutside(false);

        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
        chip.setId(getString(R.string.key_all).hashCode());
        String textAll = getString(R.string.key_all) + " " + String.valueOf(total_tutorials);
        chip.setText(textAll);
        cg_subcategory.addView(chip);
        mListHashMap.put(getString(R.string.key_all).hashCode(), new ArrayList<>());
//        chip.setChecked(true);

        if (KGlobal.isInternetAvailable(NewSubCategoryActivity.this)) {

            _list.clear();
            // getCategoryDataFromAPI(false);
            callTutorialsFirebase(cateId);

        } else showSnackBar(getString(R.string.no_internet_msg));

        for (int i = 0; i < childListNew.size(); i++) {
            chip = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
            chip.setId(i);

            Object item = this.childListNew.get(i);
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) item;

            chip.setText(t.get("name").toString());
            cg_subcategory.addView(chip);
        }


    }


    private void callTutorialsFirebase(String categoryId) {

        isLoading = true;
        mainId = categoryId;

        try {
            if (pageNo == 1) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //  FirebaseFirestoreApi.fetchTutorialsList("categories.id:="+cateId+" && level:="+level,page).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
        FirebaseFirestoreApi.fetchTutorialsList("categories.id:=" + categoryId + " && level:=" + level, pageNo).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {

            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {

                isLoading = false;
                if (task.isSuccessful()) {

                    Log.e("TAGRR", task.getResult().getData().toString());
                    HashMap<String, Object> list_from_response1 = (HashMap<String, Object>) task.getResult().getData();

                    List<HashMap<String, Object>> mlist = (List<HashMap<String, Object>>) list_from_response1.get("data");
                    ArrayList<Tutorialdatum> mTutorialdata = new ArrayList<>();


                    for (int i = 0; i < mlist.size(); i++) {
                        Tutorialdatum mTutorialdatum = new Tutorialdatum();

                        mTutorialdatum.setTitle(mlist.get(i).get("title").toString());
                        mTutorialdatum.setContent(mlist.get(i).get("content").toString());
                        mTutorialdatum.setCreatedAt(mlist.get(i).get("created_at").toString());
                        mTutorialdatum.setId(mlist.get(i).get("id").toString());
                        mTutorialdatum.setLevel(mlist.get(i).get("level").toString());
                        mTutorialdatum.setStatus(mlist.get(i).get("status").toString());
                        mTutorialdatum.setType(mlist.get(i).get("type").toString());
                        mTutorialdatum.setVisibility(mlist.get(i).get("visibility").toString());

                        // Extract the YouTube link
                        HashMap<String, Object> links = (HashMap<String, Object>) mlist.get(i).get("links");
                        if (links != null && links.get("youtube") != null) {
                            mTutorialdatum.setYoutube_link(links.get("youtube").toString());
                        } else {
                            mTutorialdatum.setYoutube_link(""); // Set empty string if links or YouTube link is null
                        }

                        if (links != null && links.get("redirect") != null) {
                            mTutorialdatum.setRedirect(links.get("redirect").toString());
                        } else {
                            mTutorialdatum.setRedirect(""); // Set empty string if links or YouTube link is null
                        }

                        if (links != null && links.get("external") != null) {
                            mTutorialdatum.setExternal(links.get("external").toString());
                        } else {
                            mTutorialdatum.setExternal(""); // Set empty string if links or YouTube link is null
                        }


                        List<HashMap<String, Object>> mlistCategory = (List<HashMap<String, Object>>) mlist.get(i).get("categories");
                        ArrayList<Tutorialcategory> mTutorialcategories = new ArrayList<>();
                        for (int j = 0; j < mlistCategory.size(); j++) {
                            Tutorialcategory mTutorialcategory = new Tutorialcategory();
                            mTutorialcategory.setThumbnail(mlistCategory.get(j).get("thumbnail").toString());
                            mTutorialcategory.setId(mlistCategory.get(j).get("id").toString());
                            mTutorialcategory.setName(mlistCategory.get(j).get("name").toString());
                            mTutorialcategories.add(mTutorialcategory);
                        }

                        mTutorialdatum.setTutorialcategories(mTutorialcategories);

                        HashMap<String, Object> images = (HashMap<String, Object>) mlist.get(i).get("images");
                        Tutorialimages mTutorialimages = new Tutorialimages();
                        //  if(images.containsKey("thumbnail") && images.get("thumbnail").toString()!=null){
                        if (images.containsKey("thumbnail")) {
                            mTutorialimages.setThumbnail(images.get("thumbnail").toString());
                            Log.e("imagelinks", "added");
                        }

                        if (images.containsKey("thumbnail_resized") && images.get("thumbnail_resized").toString() != null) {
                            mTutorialimages.setThumbnailResized(images.get("thumbnail_resized").toString());
                        }

                        mTutorialdatum.setTutorialimages(mTutorialimages);

                        List<Object> mlistTags = (List<Object>) mlist.get(i).get("tags");
                        ArrayList<String> mStrings = new ArrayList<>();
                        for (int k = 0; k < mlistTags.size(); k++) {
                            mStrings.add(mlistTags.get(k).toString());
                        }
                        mTutorialdatum.setTags(mStrings);
                        mTutorialdata.add(mTutorialdatum);
                    }

                    if (mTutorialdata.isEmpty()) {
                        isLastPage = true;
                    } else {
                        _list.addAll(mTutorialdata);
                        mSubCategoryAdapterAll.notifyDataSetChanged();
                    }

                }

                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void setupSubCategoryHeader(ArrayList<GetCategoryPostModel.postData> list) {

        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
        chip.setId(getString(R.string.featured).hashCode());
        chip.setText(getString(R.string.featured));
        cg_subcategory.addView(chip);
        mListHashMap.put(getString(R.string.featured).hashCode(), list);

        setupChildren();
    }

    private void setupChildren() {
        for (int i = 0; i < childList.size(); i++) {

            Object item = this.childList.get(i);
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) item;

            Object objData = t.get("Data");
            LinkedTreeMap<Object, Object> dataMap = (LinkedTreeMap) objData;

            String term_id = dataMap.get("term_id").toString();
            String title = dataMap.get("name").toString();//.getCategoryName();

            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
            chip.setId(title.hashCode());
            chip.setText(title);
            cg_subcategory.addView(chip);

            if (term_id.equals(subCatId)) {
                subCatTitle = title;
            }

            getSubCategoryDataFromAPI(term_id, title);

        }


        if (childList.size() == 0) {
            if (mListHashMap.size() == childList.size() + 2) {
                Chip chip = cg_subcategory.findViewById(getString(R.string.key_all).hashCode());
                chip.setChecked(true);
            }
        }

        mHorizontalScrollView.setSmoothScrollingEnabled(true);
        mHorizontalScrollView.postDelayed(() -> {
            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

            mHorizontalScrollView.postDelayed(new Runnable() {
                public void run() {
                    mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                }
            }, 3000);
        }, 3000);
    }

    void getSubCategoryDataFromAPI(String term_id, String title) {

        Log.e(NewSubCategoryActivity.class.getName(), "subCateId: " + term_id + " childList: " + childList.size());

        Call<GetCategoryPostModel> call = apiInterface.getCategoryPostList(ApiClient.SECRET_KEY, term_id);

        try {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<GetCategoryPostModel> call, Response<GetCategoryPostModel> response) {
                    try {
                        if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                            if (response.body().getPostList().size() > 0) {

                                ArrayList<GetCategoryPostModel.postData> list = response.body().getPostList();
                                mListHashMap.put(title.hashCode(), list);

                                if (mListHashMap.size() == childList.size() + 2) {
                                    if (TextUtils.isEmpty(subCatTitle)) {
                                        Chip chip = cg_subcategory.findViewById(getString(R.string.key_all).hashCode());
                                        chip.setChecked(true);
                                    } else {
                                        Chip chip = cg_subcategory.findViewById(subCatTitle.hashCode());
                                        chip.setChecked(true);
                                    }
                                }
                            } else {
                                mListHashMap.put(title.hashCode(), new ArrayList<>());
                                if (mListHashMap.size() == childList.size() + 2) {
                                    Chip chip = cg_subcategory.findViewById(getString(R.string.key_all).hashCode());
                                    chip.setChecked(true);
                                }
                            }
                        } else {
                            Toast.makeText(NewSubCategoryActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(NewSubCategoryActivity.class.getName(), e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<GetCategoryPostModel> call, Throwable t) {
                    showSnackBar("Failed To Retrieve Content!");
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }


    // Helper class implementing Comparator
    // from the Comparable interface
    class sortItemsDescending implements Comparator<GetCategoryPostModel.postData> {

        // Method of this class
        // @Override
        public int compare(GetCategoryPostModel.postData a, GetCategoryPostModel.postData b) {

            // Returning the value after comparing the objects
            // this will sort the data in Descending order
            return b.getObjdata().getPost_date().compareTo(a.getObjdata().getPost_date());
        }
    }

    class sortItemsAscending implements Comparator<GetCategoryPostModel.postData> {

        // Method of this class
        // @Override
        public int compare(GetCategoryPostModel.postData a, GetCategoryPostModel.postData b) {

            // Returning the value after comparing the objects
            // this will sort the data in Descending order
//            return b.getObjdata().getPost_date().compareTo(a.getObjdata().getPost_date());
            // Ascending order
            return a.getObjdata().getPost_date().compareTo(b.getObjdata().getPost_date());
        }
    }

    void showSnackBar(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        Log.d("SubCategory", "selectItem: " + pos);
    }

    @Override
    public void selectChildItem(GetCategoryPostModel.postData item, String subCategoryName) {
        selectChild(NewSubCategoryActivity.this, item, subCategoryName);
    }


    public void selectChild(Activity activity, GetCategoryPostModel.postData item, String subCategoryName) {
        if (KGlobal.isInternetAvailable(activity)) {

            if (item.getObjdata().getRedirect_url() != null && !item.getObjdata().getRedirect_url().isEmpty() && !item.getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {

                Chip chip = cg_subcategory.findViewById(selectedChipID);

                if (chip != null) {
                    String title = chip.getText().toString();
                    String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");

                    if (cat.length() >= 17) {
                        cat = cat.substring(0, 16);
                    }

                    title = title.replaceAll("[^a-zA-Z0-9]", "_");

                    if (title.length() >= 17) {
                        title = title.substring(0, 16);
                    }

                    String eventName = cat + "_" + title + "_" + item.getObjdata().getID();


                    eventName = eventName.replace("___", "_");
                    eventName = eventName.replace("__", "_");

                    FirebaseUtils.logEvents(this, eventName);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(activity, eventName, Toast.LENGTH_SHORT).show();
                    }
                }

                try {
                    String url = item.getObjdata().getRedirect_url();
                    KGlobal.openInBrowser(activity, url);
                } catch (ActivityNotFoundException ex) {

                } catch (Exception e) {

                }
                return;
            } else if (item.getObjdata().getRedirect_url() != null && !item.getObjdata().getRedirect_url().isEmpty() && item.getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {
                Chip chip = cg_subcategory.findViewById(selectedChipID);

                if (chip != null) {
                    String title = chip.getText().toString();
                    String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");


                    if (cat.length() >= 17) {
                        cat = cat.substring(0, 16);
                    }

                    title = title.replaceAll("[^a-zA-Z0-9]", "_");

                    if (title.length() >= 17) {
                        title = title.substring(0, 16);
                    }

                    String eventName = cat + "_" + title + "_" + item.getObjdata().getID();

                    eventName = eventName.replace("___", "_");
                    eventName = eventName.replace("__", "_");

                    FirebaseUtils.logEvents(activity, eventName);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(activity, eventName, Toast.LENGTH_SHORT).show();
                    }

                }

                getCategoryDetailFromAPI(cateId, item.getObjdata().getID(), activity);
            } else {

                Chip chip = cg_subcategory.findViewById(selectedChipID);

                if (chip != null) {
                    String title = chip.getText().toString();
                    String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");

                    Intent intent = new Intent(activity, TutorialDetail_Activity.class);
                    intent.putExtra("catID", cateId);
                    intent.putExtra("postID", item.getObjdata().getID());

                    if (cat.length() >= 17) {
                        cat = cat.substring(0, 16);
                    }

                    title = title.replaceAll("[^a-zA-Z0-9]", "_");

                    if (title.length() >= 17) {
                        title = title.substring(0, 16);
                    }

                    String eventName = cat + "_" + title + "_" + item.getObjdata().getID();

                    eventName = eventName.replace("___", "_");
                    eventName = eventName.replace("__", "_");

                    FirebaseUtils.logEvents(activity, eventName);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(activity, eventName, Toast.LENGTH_SHORT).show();
                    }

                    activity.startActivity(intent);
                }
            }
        } else {
            showSnackBar(activity.getString(R.string.no_internet_msg));
        }
    }

    void getCategoryDetailFromAPI(String catID, String postID, Activity activity) {
        apiInterface = ApiClient.getRetroClient().create(ApiInterface.class);

        Call<String> call = apiInterface.getPostDetail(ApiClient.SECRET_KEY, catID, postID);

        if (!isDestroyed() || !isFinishing()) {
            progressDialog = new ProgressDialog(NewSubCategoryActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing() && !isDestroyed()) {
                            progressDialog.dismiss();
                        }
                        if (response != null && response.body() != null) {
                            Log.e("TAGGG", "Response Data " + response.body());
                            parseResponseManually(response.body(), activity);
                        } else {
                            if (isDestroyed()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            showSnackBar("Failed To Load");
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (progressDialog != null && progressDialog.isShowing() && !isDestroyed())
                        progressDialog.dismiss();
                    showSnackBar("Failed To Retrieve Content!");
                }
            });
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing() && !isDestroyed())
                progressDialog.dismiss();

            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    PostDetailModel _object;
    Tutorial_Type tutorial_type;

    void parseResponseManually(String response, Activity activity) {
        try {
            JSONArray mainArray = new JSONArray(response);
            if (mainArray.length() > 0) {
                ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
                JSONObject objectFirst = mainArray.getJSONObject(0);
                _object = new PostDetailModel();
                _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
                _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
                _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
                _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
                _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
                _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
                _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
                _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
                _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
                _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
                _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
                _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
                _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
                _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");

                if (objectFirst.has("color_swatch") && !objectFirst.isNull("color_swatch")) {
                    JSONArray swatchesArray = objectFirst.getJSONArray("color_swatch");
                    ArrayList<ColorSwatch> swatches = new ArrayList<>();

                    if (swatchesArray != null && swatchesArray.length() > 0) {
                        for (int i = 0; i < swatchesArray.length(); i++) {

                            String swatch = swatchesArray.getJSONObject(i).getString("color_swatch");

                            ColorSwatch colorSwatch = new ColorSwatch();
                            colorSwatch.setColor_swatch(swatch);
                            swatches.add(colorSwatch);
                        }

                    }

                    _object.setSwatches(swatches);

                    // save swatches into database
                    ColorSwatchDao colorSwatchDao = db.colorSwatchDao();

                    ColorSwatchEntity colorSwatchEntity = new ColorSwatchEntity();
                    colorSwatchEntity.postId = Integer.parseInt(_object.getID());
                    colorSwatchEntity.swatches = new Gson().toJson(_object.getSwatches());
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            colorSwatchDao.insertAll(colorSwatchEntity);
                        }
                    });

                }

                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
                    _object.setResizeImage(objectFirst.getString("ResizeImage"));
                }
                if (objectFirst.has("RelatedPostsData")) {
                    JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
                    ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
                    if (related_list_json != null && related_list_json.length() > 0) {
                        for (int i = 0; i < related_list_json.length(); i++) {
                            RelatedPostsData obj_related = new RelatedPostsData();
                            JSONObject obj = related_list_json.getJSONObject(i);
                            if (obj.has("ID")) {
                                obj_related.setID(obj.getInt("ID"));
                            }
                            if (obj.has("post_title") && obj.getString("post_title") != null) {
                                obj_related.setPost_title(obj.getString("post_title"));
                            }
                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
                                obj_related.setThumbImage(obj.getString("thumbImage"));
                            }
                            related_List.add(obj_related);
                        }
                        _object.setList_related_post(related_List);
                    }
                }
                ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
                ContentSectionModel obj_content = new ContentSectionModel();
                obj_content.setUrl(_object.getThumb_url());
                obj_content.setCaption("Featured");
                obj_content.setVideoContent(false);
                contentSectionList.add(obj_content);

                if (objectFirst.has("EmbededData")) {
                    JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
                    for (int i = 0; i < embededVideoList.length(); i++) {
                        obj_content = new ContentSectionModel();
                        JSONObject obj = embededVideoList.getJSONObject(i);
                        obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
                        obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");

                        if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {

                            if (obj_content.getUrl().contains("youtu.be")) {
                                obj_content.setVideoContent(true);
                                String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
                            }
                        }
                        contentSectionList.add(obj_content);
                    }
                }

                try {
                    if (objectFirst.has("EmbededImage")) {
                        JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
                        for (int i = 0; i < embededImageList.length(); i++) {
                            JSONObject object = embededImageList.getJSONObject(i);
                            obj_content = new ContentSectionModel();
                            obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
                            obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
                            obj_content.setVideoContent(false);
                            contentSectionList.add(obj_content);
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
                }
                _object.setFeaturedImage(contentSectionList);
                if (objectFirst.has("videos_and_files")) {

                    JSONArray videoArray = null;
                    try {
                        videoArray = objectFirst.getJSONArray("videos_and_files");
                    } catch (Exception e) {

                    }
                    if (videoArray != null) for (int i = 0; i < videoArray.length(); i++) {
                        JSONObject obj = videoArray.getJSONObject(i);
                        videos_and_files videos_and_files = new videos_and_files();
                        if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
                            text_files obj_text_file = new text_files();
                            JSONObject obj_text = obj.getJSONObject("text_file");
                            obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
                            obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
                            obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
                            obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
                            obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
                            videos_and_files.setObj_text_files(obj_text_file);
                        } else videos_and_files.setObj_text_files(null);

                        try {
                            if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
                                trace_image obj_trace = new trace_image();
                                JSONObject obj_trace_object = obj.getJSONObject("trace_image");
                                obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
                                obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
                                obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
                                obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
                                obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
                                if (obj_trace_object.has("sizes")) {
                                    JSONObject objSize = obj_trace_object.getJSONObject("sizes");
                                    sizes obj_size = new sizes();
                                    obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
                                    obj_trace.setObj_sizes(obj_size);
                                } else {
                                    obj_trace.setObj_sizes(null);
                                }
                                videos_and_files.setObj_trace_image(obj_trace);
                            } else videos_and_files.setObj_trace_image(null);

                        } catch (Exception e) {
                            Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
                        }
                        try {
                            if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
                                Overlaid overlaid = new Overlaid();
                                JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
                                if (obj_overlaid_object != null) {
                                    overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
                                    overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
                                    overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
                                }
                                videos_and_files.setObj_overlaid(overlaid);
                            } else videos_and_files.setObj_overlaid(null);

                        } catch (Exception e) {
                            Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
                        }
                        _lst_video_file.add(videos_and_files);
                    }

                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
                        _object.setVideo_and_file_list(_lst_video_file);
                } else _object.setVideo_and_file_list(null);

            }

            if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() >= 2 && (_object.getVideo_and_file_list().get(0).getObj_text_files() != null && _object.getVideo_and_file_list().get(1).getObj_text_files() != null) && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
                if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null || _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;

                } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
                    tutorial_type = Tutorial_Type.Strokes_Window;

                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window;

                }
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {

                tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {

                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
            } else if (_object.getExternal_link() != null && !_object.getExternal_link().isEmpty()) {
                if (_object.getExternal_link().contains("youtu.be")) {

                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link;
                } else {

                    tutorial_type = Tutorial_Type.Read_Post;
                }
            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty()) {

                tutorial_type = Tutorial_Type.See_Video;
            } else {

                tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
            }

            progressDialog.dismiss();

            processTutorial(activity);
        } catch (Exception e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
        }
    }

    void processTutorial(Activity activity) {


        if (tutorial_type == Tutorial_Type.See_Video) {
            String eventName = "watch_video_";
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(activity, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getYoutube_link_list());
            intent.putExtra("isVideo", true);
            activity.startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
            String eventName = "read_post_";

            try {
                Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(activity, _object.getExternal_link().replace("htttps://", "https://").trim());
            } catch (ActivityNotFoundException ex) {
            } catch (Exception e) {
            }
            return;
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
            String eventName = "watch_video_from_external_link_";

            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(activity, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getExternal_link());
            intent.putExtra("isVideo", true);
            Log.e("TAGGG", "URL " + _object.getExternal_link());
            activity.startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            String eventName = "video_tutorial_overlaid_";


            String fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename();
            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
            String youtubeLink = _object.getYoutube_link_list();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                if (!file.exists()) {
                    new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl(), false, _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename(), activity).execute(_object.getVideo_and_file_list().get(0).obj_overlaid.getUrl());
                    return;
                } else {
//                    if (_object.getPost_title() != null)
//                        FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                    openTutorialsRewardPoint(_object.getID(), activity);


                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 1");
                    Intent intent = new Intent(activity, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("isPickFromOverlaid", true);
                    intent.putExtra("path", fileName);
                    intent.putExtra("youtube_video_id", _youtube_id);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("id", _object.getID());
                    activity.startActivity(intent);
                    return;
                }
            } else {
                Toast.makeText(activity, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            String eventName = "do_drawing_overlay_";

            String fileName = _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, false, activity).execute();
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {

            String fileName = _object.getVideo_and_file_list().get(0).getObj_trace_image().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_trace_image().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, true, activity).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            new DownloadsTextFiles(_object, activity).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {

            String OverLayName = "", OverLayUrl = "";

            if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
                OverLayName = (_object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            } else {
                OverLayName = (_object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl();
            }


            new DownloadOverlayImage(OverLayUrl, OverLayName, activity).execute();

        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
            try {
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    if (_object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().get(0).obj_trace_image != null && _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes() != null) {
                        if (_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge() != null) {
                            String fileName = _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().substring(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().lastIndexOf('/') + 1);
                            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
                            if (!file.exists())
                                new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge(), true, "", activity).execute(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge());
                            else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                                openTutorialsRewardPoint(_object.getID(), activity);
                                Log.e("PaintActivity", "Paint Flow 3");
                                StringConstants.IsFromDetailPage = false;
                                Intent intent = new Intent(activity, PaintActivity.class);
                                intent.putExtra("youtube_video_id", _youtube_id);
                                intent.setAction("YOUTUBE_TUTORIAL");
                                intent.putExtra("drawingType", "TUTORAILS");
                                intent.putExtra("paint_name", file.getAbsolutePath());
                                if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvas_color());
                                }
                                intent.putExtra("id", _object.getID());
                                activity.startActivity(intent);
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                        openTutorialsRewardPoint(_object.getID(), activity);
                        Log.e("PaintActivity", "Paint Flow 6");
                        StringConstants.IsFromDetailPage = false;
                        Intent intent = new Intent(activity, PaintActivity.class);
                        intent.putExtra("youtube_video_id", _youtube_id);
                        intent.setAction("YOUTUBE_TUTORIAL");
                        intent.putExtra("drawingType", "TUTORAILS");
                        if (!_object.getCanvas_color().isEmpty()) {
                            intent.putExtra("canvas_color", _object.getCanvas_color());
                        }
                        intent.putExtra("id", _object.getID());
                        activity.startActivity(intent);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(activity, "Failed To Load!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            try {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(activity, defaultLink.trim());
            } catch (ActivityNotFoundException anf) {

            } catch (Exception e) {

            }
        }
    }

    private void openTutorialsRewardPoint(String mId, Activity activity) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.open_tutorial, mId);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(activity);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue("opening_tutorials", rewardSetup.getOpening_tutorials() == null ? 0 : rewardSetup.getOpening_tutorials(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }*/
    }

    public class DownloadsImage extends AsyncTask<String, Void, String> {
        String youtubeLink, traceImageLink, fileName;
        Boolean isFromTrace = false;
        Activity activity;

        public DownloadsImage(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName, Activity activity) {
            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.fileName = fileName;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
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
            File path = new File(KGlobal.getTraceImageFolderPath(activity)); //Creates app specific folder

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
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                openTutorialsRewardPoint(_object.getID(), activity);

                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 7");
                    Intent intent = new Intent(activity, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("paint_name", path);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                } else {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 8");
                    Intent intent = new Intent(activity, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("isPickFromOverlaid", true);
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);
                    Log.e("swatch", swatchesJson.toString());
                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    public class DownloadsImageFirebase extends AsyncTask<String, Void, String> {

        String youtubeLink, traceImageLink, canvas, fileName, id, type;

        Boolean isFromTrace = false;

        ArrayList<ColorSwatch> swatches;

        Activity activity;


        public DownloadsImageFirebase(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName, String canvas, String id, ArrayList<ColorSwatch> swatches, String type, Activity activity) {

            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.canvas = canvas;
            this.id = id;
            this.fileName = fileName;
            this.swatches = swatches;
            this.type = type;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
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
            File path = new File(KGlobal.getTraceImageFolderPath(activity)); //Creates app specific folder

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
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                Log.e("newfilename", path);

                openTutorialsRewardPoint(id, activity);

            /*    if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 7");
                    Intent intent = new Intent(NewSubCategoryActivity.this, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("paint_name", path);
                   // if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", canvas);
                    //}
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                else {
                    StringConstants.IsFromDetailPage = false;
                    Log.e("PaintActivity", "Paint Flow 8");
                    Intent intent = new Intent(NewSubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(NewSubCategoryActivity.this));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", canvas);
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                   *//* Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", id);*//*
                    startActivity(intent);
                }*/
                Intent intent = new Intent(activity, PaintActivity.class);

               /* if(!youtubeLink.isEmpty()) {
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    intent.putExtra("tutorial_type", type);
                }else{*/
                if (type.equalsIgnoreCase("trace")) {

                    Log.e("Tag visit", "trace");

                    intent.setAction("Edit Paint");
                    // intent.setAction("LoadWithoutTrace");
                } else if (type.equalsIgnoreCase("blank")) {
                    intent.setAction("New Paint");
                    //intent.setAction("LoadWithoutTrace");
                } else {
                    intent.setAction("LoadWithoutTrace");
                }
                if (type.equalsIgnoreCase("trace")) {
                    intent.setAction("Edit Paint");
                    intent.putExtra("paint_name", path);
                    intent.putExtra("FromLocal", true);
                    // intent.setAction("LoadWithoutTrace");
                } else if (type.equalsIgnoreCase("blank")) {
                    intent.setAction("New Paint");
                    intent.putExtra("isPickFromOverlaid", true);
                    //intent.setAction("LoadWithoutTrace");
                } else {
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("isPickFromOverlaid", true);
                }

                if (!youtubeLink.isEmpty()) {
                    intent.putExtra("youtube_video_id", youtubeLink);
                }

                intent.putExtra("path", fileName);
                intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
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

                Log.e("Tag visit", "start acticity");

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    class DownloadsTextFiles extends AsyncTask<Void, Void, ArrayList<String>> {
        PostDetailModel _objects;
        Activity activity;

        public DownloadsTextFiles(PostDetailModel _objects, Activity activity) {
            this._objects = _objects;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(activity));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _objects.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _objects.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

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

                openTutorialsRewardPoint(_object.getID(), activity);

                StringConstants.IsFromDetailPage = false;
                Log.e("PaintActivity", "Paint Flow 15");
                Intent intent = new Intent(activity, PaintActivity.class);

                intent.putExtra("drawingType", "TUTORAILS");
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color());
                }
                String youtubeLink = _objects.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
                if (list.size() == 2) {
                    intent.putExtra("StrokeFilePath", list.get(0));
                    intent.putExtra("EventFilePath", list.get(1));
                } else
                    Toast.makeText(activity, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                intent.putExtra("id", _object.getID());

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }


    class DownloadsTextFilesFirebase extends AsyncTask<Void, Void, ArrayList<String>> {


        String youtubeLink, canvas, textFileName, textUrl, textfileName2, texturl2, id;

        ArrayList<ColorSwatch> swatches;
        Activity activity;

        public DownloadsTextFilesFirebase(String youtubeLink, String canvas, String id, ArrayList<ColorSwatch> swatches, String textFileName, String textUrl, String textfileName2, String texturl2, Activity activity) {
            this.youtubeLink = youtubeLink;
            this.canvas = canvas;
            this.id = id;
            this.swatches = swatches;
            this.textFileName = textFileName;
            this.textfileName2 = textfileName2;
            this.textUrl = textUrl;
            this.texturl2 = texturl2;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(activity));
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

                openTutorialsRewardPoint(id, activity);

                StringConstants.IsFromDetailPage = false;
                Log.e("PaintActivity", "Paint Flow 9");
                Intent intent = new Intent(activity, PaintActivity.class);

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
                    Toast.makeText(activity, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                intent.putExtra("id", id);

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception " + e.getMessage());
            }
        }
    }

    class DownloadOverlayImage extends AsyncTask<Void, Void, ArrayList<String>> {
        String traceImageLink, fileName;

        Activity activity;

        public DownloadOverlayImage(String traceImageLink, String fileName, Activity activity) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> filesList = downloadTextFiles();

            File file = new File(KGlobal.getTraceImageFolderPath(activity), fileName);

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
                File path = new File(KGlobal.getTraceImageFolderPath(activity)); //Creates app specific folder

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
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                openTutorialsRewardPoint(_object.getID(), activity);

                StringConstants.IsFromDetailPage = false;
                Log.e("PaintActivity", "Paint Flow 10");
                Intent intent = new Intent(activity, PaintActivity.class);
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color());
                }
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
                intent.putExtra("OverlaidImagePath", new File(KGlobal.getTraceImageFolderPath(activity), fileName).getAbsolutePath());
                intent.putExtra("id", _object.getID());
                intent.putExtra("drawingType", "TUTORAILS");
                activity.startActivity(intent);
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }


        public ArrayList<String> downloadTextFiles() {
            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(activity));
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
        Activity activity;

        public DownloadOverlayFromDoDrawing(String traceImageLink, String fileName, Boolean isFromTrace, Activity activity) {
            this.traceImageLink = traceImageLink;
            Log.d("ObjectID", "DownloadOverlayFromDoDrawing fileName: " + fileName);
            this.fileName = fileName;
            this.isFromTrace = isFromTrace;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {

            File file = new File(KGlobal.getTraceImageFolderPath(activity), fileName);
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
                File path = new File(KGlobal.getTraceImageFolderPath(activity)); //Creates app specific folder

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
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));

                openTutorialsRewardPoint(_object.getID(), activity);

                StringConstants.IsFromDetailPage = false;
                if (isFromTrace) {
                    Log.e("PaintActivity", "Paint Flow 11");
                    Intent intent = new Intent(activity, PaintActivity.class);
                    intent.setAction("Edit Paint");
                    intent.putExtra("FromLocal", true);
                    intent.putExtra("drawingType", "TUTORAILS");
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
                    Log.e("PaintActivity", "Paint Flow 12");
                    Intent intent = new Intent(activity, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("drawingType", "TUTORAILS");
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                    if (!_object.getCanvas_color().isEmpty()) {
                        Log.d("ObjectID", "getCanvas_color: " + _object.getCanvas_color());
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    Log.d("ObjectID", "fileName: " + fileName);
                    Log.d("ObjectID", "path: " + path);
                    Log.d("ObjectID", "getID: " + _object.getID());
                    Log.d("ObjectID", "swatchesJson: " + swatchesJson);
                    intent.putExtra("id", _object.getID());

                    activity.startActivity(intent);
                }
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + path);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    public void back(View v) {
        finish();
    }

    public void gotoUrl(String url) {
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(url));
            startActivity(viewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubMenuClickAll(@Nullable View view, @Nullable Tutorialdatum item, int position) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Child Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        StringConstants constants = new StringConstants();
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, view);


        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.subcategory_item_menu, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.action_open_link).setVisible(false);
        if (item != null && item.getExternal() != null) {
            if (!item.getExternal().equalsIgnoreCase("")) {
                popupMenu.getMenu().findItem(R.id.action_open_link).setVisible(true);
            }
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.action_share:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_share, bundle);
                    }
                    onShareClickAll(item, position, NewSubCategoryActivity.this);
                    break;
                case R.id.action_open_link:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_open_link, bundle);
                    }
                    gotoUrl(item.getExternal().toString());
                    break;
                case R.id.action_open_tutorial:
                    selectChildItemAll(item, cate_name);
                    break;
                case R.id.action_rating:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_rating, bundle);
                    }
                    if (AppUtils.isLoggedIn()) {
                        openRatingDialogAll(item, NewSubCategoryActivity.this);
                    } else {
                        Intent intent = new Intent(NewSubCategoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    break;

                case R.id.addFav:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_add_favorite, bundle);
                    }
                    TutorialDbHelper dbHelper = new TutorialDbHelper(NewSubCategoryActivity.this);
                    dbHelper.addTutorial(item, cate_name, NewSubCategoryActivity.this);

                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();

    }

    @Override
    public void selectChildItemAll(@Nullable Tutorialdatum item, @Nullable String subCategoryName) {
        SelectItemsAll(this, item, subCategoryName);
    }

    public void SelectItemsAll(Context activity, @Nullable Tutorialdatum item, @Nullable String subCategoryName) {
            /*  String title = "All";
        String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");

        Intent intent = new Intent(this, TutorialDetail_Activity.class);
        intent.putExtra("catID", cateId);
        intent.putExtra("postID", item.getId());

        if (cat.length() >= 17) {
            cat = cat.substring(0, 16);
        }

        title = title.replaceAll("[^a-zA-Z0-9]", "_");
`
        if (title.length() >= 17) {
            title = title.substring(0, 16);
        }

        String eventName = cat + "_" + title + "_" + item.getId();

        eventName = eventName.replace("___", "_");
        eventName = eventName.replace("__", "_");

        FirebaseUtils.logEvents(this, eventName);
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
        }

        startActivity(intent);*/

        // Fetch previous rating before showing dialog.
        if (item != null && item.getId() != null) {
            Log.e("tutorialId", item.getId());
            FireUtils.showProgressDialog(
                    activity,
                    activity.getResources().getString(R.string.please_wait)
            );
            new TutorialUtils(activity).parseTutorial(item.getId());
        }
        /*FirebaseFirestoreApi.getTutorialDetail(item.getId()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                String canvas_color = ds.get("canvas_color").toString();
                String type = ds.get("type").toString();

                Map<String, Object> data = ds.getData();

                Map<String, Object> links = (Map<String, Object>) data.get("links");
                String redirect = "";
                if (links.get("redirect") != null) {
                    redirect = links.get("redirect").toString();
                }

                Map<String, Object> files = (Map<String, Object>) data.get("files");
                if (!type.isEmpty() || !redirect.isEmpty()) {


                    String textFileName = "";
                    String textFile = "";
                    String textFileName2 = "";
                    String textFile2 = "";
                    if (files.get("text_file_1") != null) {
                        Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_1");
                        textFileName = text1.get("name").toString();
                        textFile = text1.get("url").toString();
                    }

                    if (files.get("text_file_2") != null) {
                        Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_2");
                        textFileName2 = text1.get("name").toString();
                        textFile2 = text1.get("url").toString();
                    }


                    String youtubeLink = "";
                    if (links.get("youtube") != null) {
                        youtubeLink = links.get("youtube").toString();
                    }

                    Map<String, Object> images = (Map<String, Object>) data.get("images");
                    String image = "";
                    if (images.get("content") != null) {
                        image = images.get("content").toString();
                    }


                    Map<String, Object> options = (Map<String, Object>) data.get("options");
                    boolean singleTap = (boolean) options.get("single_tap");
                    boolean line = (boolean) options.get("straight_lines");
                    boolean greyScal = (boolean) options.get("grayscale");

                    boolean block_colorin = false;
                    if (options.containsKey("block_coloring")) {
                        block_colorin = (boolean) options.get("block_coloring");
                    }


                    SharedPreferences sharedPref = activity.getSharedPreferences("brush", 0);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    Log.e("greyScal", greyScal + "");
                    editor.putBoolean("singleTap", singleTap);
                    editor.putBoolean("line", line);
                    editor.putBoolean("gray_scale", greyScal);
                    editor.putBoolean("block_coloring", block_colorin);
                    editor.commit();
                    editor.apply();


                    Map<String, Object> brush = (Map<String, Object>) options.get("brush");
                    Float mPrefBrushSize = Float.valueOf(brush.get("size").toString());

                    String brushcolor = brush.get("color").toString();
                    Long mPrefAlpha1 = (Long) brush.get("density");
                    int mPrefAlpha = Integer.valueOf(mPrefAlpha1.toString());
                    mPrefAlpha = (255 * mPrefAlpha) / 100;
                    Long mPrefFlow1 = (Long) brush.get("hardness");
                    int mPrefFlow = Integer.valueOf(mPrefFlow1.toString());
                    mPrefFlow = (255 * mPrefFlow) / 100;
                    SharedPreferences.Editor lEditor1 = activity.getSharedPreferences("brush", 0).edit();

                    String brushMode = brush.get("type").toString();
                    int mPrefBrushStyle = getBrushMode(brushMode);
                    lEditor1.putString("pref-saved", "yes");
                    // lEditor1.putInt("background-color", mPrefBackgroundColor);
                    lEditor1.putInt("brush-style", mPrefBrushStyle);
                    lEditor1.putFloat("brush-size", mPrefBrushSize);
                    lEditor1.putInt("brush-color", Color.parseColor(brushcolor));
                    //   lEditor1.putInt("brush-mode", mPrefBrushMode);
                    lEditor1.putInt("brush-alpha", mPrefAlpha);
                    lEditor1.putInt("brush-pressure", mPrefAlpha);
                    lEditor1.putInt("brush-flow", mPrefFlow);
                    lEditor1.commit();

                    ArrayList<String> colorSwatch = (ArrayList<String>) data.get("color_swatch");
                    Log.e("swatchesArray size", colorSwatch.size() + "");
                    ArrayList<String> swatchesArray = new ArrayList<>();
                    for (int i = 0; i < colorSwatch.size(); i++) {
                        //   String index = String.valueOf(i);
                        if (colorSwatch.get(i) != null) {
                            swatchesArray.add(colorSwatch.get(i).toString());
                            Log.e("swatchesArray ", i + " : " + swatchesArray.get(i));
                        } else {
                            break;
                        }

                    }

                    ArrayList<ColorSwatch> swatches = new ArrayList<>();

                    if (swatchesArray != null && swatchesArray.size() > 0) {
                        for (int i = 0; i < swatchesArray.size(); i++) {

                            String swatch = swatchesArray.get(i);
                            ColorSwatch colorSwatchItem = new ColorSwatch();
                            colorSwatchItem.setColor_swatch(swatch);
                            swatches.add(colorSwatchItem);
                        }

                    }


                    if (!textFileName.isEmpty()) {
                        String _youtube_id = "";
                        if (!youtubeLink.isEmpty()) {
                            _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                        }
                        new DownloadsTextFilesFirebase(youtubeLink, canvas_color, item.getId(), swatches, textFileName, textFile, textFileName2, textFile2, activity).execute();

                    } else {

                        if (type.equalsIgnoreCase("trace") || type.equalsIgnoreCase("overlay") || type.equalsIgnoreCase("blank")) {
                            try {

                                Boolean isTrace = false;
                                if (type.equalsIgnoreCase("trace")) {
                                    isTrace = false;
                                }
                                String _youtube_id = "";
                                if (!youtubeLink.isEmpty()) {
                                    _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                }


                                String fileName = image.substring(image.lastIndexOf('/') + 1);
                                Log.e("newfilename", fileName);
                                if (fileName.contains("token")) {
                                    //  String temp = image.substring(image.lastIndexOf('/') + 1);
                                    fileName = fileName.substring(0, fileName.indexOf("?"));

                                    *//* fileName = fileName.split(".")[0];*//*
                                    //fileName+=".jpg";
                                    Log.e("newfilename", fileName);
                                }
                                File file = new File(KGlobal.getTraceImageFolderPath(activity) + "/" + fileName);
                                if (!file.exists())
                                    new DownloadsImageFirebase(_youtube_id, image, isTrace, fileName, canvas_color, item.getId(), swatches, type, activity).execute(image);
                                else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                                    openTutorialsRewardPoint(item.getId(), activity);
                                    Log.e("PaintActivity", "Paint Flow 3");
                                    StringConstants.IsFromDetailPage = false;
                                    Intent intent = new Intent(activity, PaintActivity.class);
                                       *//* if (!_youtube_id.isEmpty()) {
                                            intent.putExtra("youtube_video_id", _youtube_id);
                                            intent.setAction("YOUTUBE_TUTORIAL");
                                            intent.putExtra("paint_name", file.getAbsolutePath());
                                            intent.putExtra("tutorial_type", type);
                                        } else {*//*
                                    if (type.equalsIgnoreCase("trace")) {
                                        intent.setAction("Edit Paint");
                                        intent.putExtra("paint_name", file.getAbsolutePath());
                                        intent.putExtra("FromLocal", true);
                                        //intent.setAction("LoadWithoutTrace");
                                    } else if (type.equalsIgnoreCase("blank")) {
                                        intent.setAction("New Paint");
                                        //intent.setAction("LoadWithoutTrace");
                                        intent.putExtra("isPickFromOverlaid", true);
                                    } else {
                                        intent.setAction("LoadWithoutTrace");
                                        intent.putExtra("isPickFromOverlaid", true);
                                    }
                                    if (!_youtube_id.isEmpty()) {
                                        intent.putExtra("youtube_video_id", _youtube_id);
                                    }
                                    intent.putExtra("path", fileName);
                                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                                    //}
                                    intent.putExtra("drawingType", "TUTORAILS");
                                    if (swatches.size() > 0) {
                                        Gson gson = new Gson();
                                        String swatchesJson = gson.toJson(swatches);

                                        intent.putExtra("swatches", swatchesJson);
                                    }
                                    // if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", canvas_color);
                                    //}
                                    intent.putExtra("id", item.getId());
                                    activity.startActivity(intent);
                                }


                            } catch (Exception e) {
                                Toast.makeText(NewSubCategoryActivity.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {

                    // sendEventToFirebase(eventName);
                    // Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
            *//*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
            startActivity(browserIntent);*//*

                    if (links.get("external") != null) {
                        String ref = links.get("external").toString();
                        KGlobal.openInBrowser(NewSubCategoryActivity.this, ref);
                    } else {
                        String ref = ds.get("ref").toString();
                        KGlobal.openInBrowser(NewSubCategoryActivity.this, ref);
                    }



                           *//* String title = "All";
                            String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");

                            Intent intent = new Intent(this, TutorialDetail_Activity.class);
                            intent.putExtra("catID", cateId);
                            intent.putExtra("postID", item.getId());

                            if (cat.length() >= 17) {
                                cat = cat.substring(0, 16);
                            }

                            title = title.replaceAll("[^a-zA-Z0-9]", "_");

                            if (title.length() >= 17) {
                                title = title.substring(0, 16);
                            }

                            String eventName = cat + "_" + title + "_" + item.getId();

                            eventName = eventName.replace("___", "_");
                            eventName = eventName.replace("__", "_");

                            FirebaseUtils.logEvents(this, eventName);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                            }

                            startActivity(intent);*//*
                }

            }

        }).addOnFailureListener(e -> {


        });*/
    }


    public void SelectItemsAll(Activity activity, @Nullable String item, @Nullable String subCategoryName) {

        Toast.makeText(activity, "function called", Toast.LENGTH_SHORT).show();

        FirebaseFirestoreApi.getTutorialDetail(item).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                String canvas_color = ds.get("canvas_color").toString();
                String type = ds.get("type").toString();

                Map<String, Object> data = ds.getData();

                Map<String, Object> links = (Map<String, Object>) data.get("links");
                String redirect = "";
                if (links.get("redirect") != null) {
                    redirect = links.get("redirect").toString();
                }

                Map<String, Object> files = (Map<String, Object>) data.get("files");
                if (!type.isEmpty() || !redirect.isEmpty()) {


                    String textFileName = "";
                    String textFile = "";
                    String textFileName2 = "";
                    String textFile2 = "";
                    if (files.get("text_file_1") != null) {
                        Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_1");
                        textFileName = text1.get("name").toString();
                        textFile = text1.get("url").toString();
                    }

                    if (files.get("text_file_2") != null) {
                        Map<String, Object> text1 = (Map<String, Object>) files.get("text_file_2");
                        textFileName2 = text1.get("name").toString();
                        textFile2 = text1.get("url").toString();
                    }


                    String youtubeLink = "";
                    if (links.get("youtube") != null) {
                        youtubeLink = links.get("youtube").toString();
                    }

                    Map<String, Object> images = (Map<String, Object>) data.get("images");
                    String image = "";
                    if (images.get("content") != null) {
                        image = images.get("content").toString();
                    }


                    Map<String, Object> options = (Map<String, Object>) data.get("options");
                    boolean singleTap = (boolean) options.get("single_tap");
                    boolean line = (boolean) options.get("straight_lines");
                    boolean greyScal = (boolean) options.get("grayscale");

                    boolean block_colorin = false;
                    if (options.containsKey("block_coloring")) {
                        block_colorin = (boolean) options.get("block_coloring");
                    }


                    SharedPreferences sharedPref = activity.getSharedPreferences("brush", 0);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    Log.e("greyScal", greyScal + "");
                    editor.putBoolean("singleTap", singleTap);
                    editor.putBoolean("line", line);
                    editor.putBoolean("gray_scale", greyScal);
                    editor.putBoolean("block_coloring", block_colorin);
                    editor.commit();
                    editor.apply();


                    Map<String, Object> brush = (Map<String, Object>) options.get("brush");
                    Float mPrefBrushSize = Float.valueOf(brush.get("size").toString());

                    String brushcolor = brush.get("color").toString();
                    Long mPrefAlpha1 = (Long) brush.get("density");
                    int mPrefAlpha = Integer.valueOf(mPrefAlpha1.toString());
                    mPrefAlpha = (255 * mPrefAlpha) / 100;
                    Long mPrefFlow1 = (Long) brush.get("hardness");
                    int mPrefFlow = Integer.valueOf(mPrefFlow1.toString());
                    mPrefFlow = (255 * mPrefFlow) / 100;
                    SharedPreferences.Editor lEditor1 = activity.getSharedPreferences("brush", 0).edit();

                    String brushMode = brush.get("type").toString();
                    int mPrefBrushStyle = getBrushMode(brushMode);
                    lEditor1.putString("pref-saved", "yes");
                    // lEditor1.putInt("background-color", mPrefBackgroundColor);
                    lEditor1.putInt("brush-style", mPrefBrushStyle);
                    lEditor1.putFloat("brush-size", mPrefBrushSize);
                    lEditor1.putInt("brush-color", Color.parseColor(brushcolor));
                    //   lEditor1.putInt("brush-mode", mPrefBrushMode);
                    lEditor1.putInt("brush-alpha", mPrefAlpha);
                    lEditor1.putInt("brush-pressure", mPrefAlpha);
                    lEditor1.putInt("brush-flow", mPrefFlow);
                    lEditor1.commit();

                    ArrayList<String> colorSwatch = (ArrayList<String>) data.get("color_swatch");
                    Log.e("swatchesArray size", colorSwatch.size() + "");
                    ArrayList<String> swatchesArray = new ArrayList<>();
                    for (int i = 0; i < colorSwatch.size(); i++) {
                        //   String index = String.valueOf(i);
                        if (colorSwatch.get(i) != null) {
                            swatchesArray.add(colorSwatch.get(i).toString());
                            Log.e("swatchesArray ", i + " : " + swatchesArray.get(i));
                        } else {
                            break;
                        }

                    }

                    ArrayList<ColorSwatch> swatches = new ArrayList<>();

                    if (swatchesArray != null && swatchesArray.size() > 0) {
                        for (int i = 0; i < swatchesArray.size(); i++) {

                            String swatch = swatchesArray.get(i);
                            ColorSwatch colorSwatchItem = new ColorSwatch();
                            colorSwatchItem.setColor_swatch(swatch);
                            swatches.add(colorSwatchItem);
                        }

                    }


                    if (!textFileName.isEmpty()) {
                        String _youtube_id = "";
                        if (!youtubeLink.isEmpty()) {
                            _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                        }
                        new DownloadsTextFilesFirebase(youtubeLink, canvas_color, item, swatches, textFileName, textFile, textFileName2, textFile2, activity).execute();

                    } else {

                        if (type.equalsIgnoreCase("trace") || type.equalsIgnoreCase("overlay") || type.equalsIgnoreCase("blank")) {
                            try {

                                Boolean isTrace = false;
                                if (type.equalsIgnoreCase("trace")) {
                                    isTrace = false;
                                }
                                String _youtube_id = "";
                                if (!youtubeLink.isEmpty()) {
                                    _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                }


                                String fileName = image.substring(image.lastIndexOf('/') + 1);
                                Log.e("newfilename", fileName);
                                if (fileName.contains("token")) {
                                    //  String temp = image.substring(image.lastIndexOf('/') + 1);
                                    fileName = fileName.substring(0, fileName.indexOf("?"));

                                    /* fileName = fileName.split(".")[0];*/
                                    //fileName+=".jpg";
                                    Log.e("newfilename", fileName);
                                }
                                File file = new File(KGlobal.getTraceImageFolderPath(activity) + "/" + fileName);
                                if (!file.exists())
                                    new DownloadsImageFirebase(_youtube_id, image, isTrace, fileName, canvas_color, item, swatches, type, activity).execute(image);
                                else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(NewSubCategoryActivity.this, "Try " + _object.getPost_title());

                                    openTutorialsRewardPoint(item, activity);
                                    Log.e("PaintActivity", "Paint Flow 3");
                                    StringConstants.IsFromDetailPage = false;
                                    Intent intent = new Intent(activity, PaintActivity.class);
                                       /* if (!_youtube_id.isEmpty()) {
                                            intent.putExtra("youtube_video_id", _youtube_id);
                                            intent.setAction("YOUTUBE_TUTORIAL");
                                            intent.putExtra("paint_name", file.getAbsolutePath());
                                            intent.putExtra("tutorial_type", type);
                                        } else {*/
                                    if (type.equalsIgnoreCase("trace")) {
                                        intent.setAction("Edit Paint");
                                        intent.putExtra("paint_name", file.getAbsolutePath());
                                        intent.putExtra("FromLocal", true);
                                        //intent.setAction("LoadWithoutTrace");
                                    } else if (type.equalsIgnoreCase("blank")) {
                                        intent.setAction("New Paint");
                                        //intent.setAction("LoadWithoutTrace");
                                        intent.putExtra("isPickFromOverlaid", true);
                                    } else {
                                        intent.setAction("LoadWithoutTrace");
                                        intent.putExtra("isPickFromOverlaid", true);
                                    }
                                    if (!_youtube_id.isEmpty()) {
                                        intent.putExtra("youtube_video_id", _youtube_id);
                                    }
                                    intent.putExtra("path", fileName);
                                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(activity));
                                    //}
                                    intent.putExtra("drawingType", "TUTORAILS");
                                    if (swatches.size() > 0) {
                                        Gson gson = new Gson();
                                        String swatchesJson = gson.toJson(swatches);

                                        intent.putExtra("swatches", swatchesJson);
                                    }
                                    // if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", canvas_color);
                                    //}
                                    intent.putExtra("id", item);
                                    activity.startActivity(intent);
                                }


                            } catch (Exception e) {
                                Toast.makeText(NewSubCategoryActivity.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {

                    // sendEventToFirebase(eventName);
                    // Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
            startActivity(browserIntent);*/
                    String ref = ds.get("ref").toString();

                    KGlobal.openInBrowser(NewSubCategoryActivity.this, ref);

                           /* String title = "All";
                            String cat = cate_name.replaceAll("[^a-zA-Z0-9]", "_");

                            Intent intent = new Intent(this, TutorialDetail_Activity.class);
                            intent.putExtra("catID", cateId);
                            intent.putExtra("postID", item.getId());

                            if (cat.length() >= 17) {
                                cat = cat.substring(0, 16);
                            }

                            title = title.replaceAll("[^a-zA-Z0-9]", "_");

                            if (title.length() >= 17) {
                                title = title.substring(0, 16);
                            }

                            String eventName = cat + "_" + title + "_" + item.getId();

                            eventName = eventName.replace("___", "_");
                            eventName = eventName.replace("__", "_");

                            FirebaseUtils.logEvents(this, eventName);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                            }

                            startActivity(intent);*/
                }

            }

        }).addOnFailureListener(e -> {


        });
    }

    private int getBrushMode(String name) {
        int mode = 0;

        switch (name) {
            case "sticks": {
                mode = 528;
                break;
            }

            case "meadow": {
                mode = 656;
                break;
            }

            case "haze light": {
                mode = 640;
                break;
            }

            case "haze dark": {
                mode = 642;
                break;
            }

            case "line": {
                mode = 81;
                break;
            }

            case "mist": {
                mode = 784;
                break;
            }

            case "land patch": {
                mode = 608;
                break;
            }

            case "grass": {
                mode = 624;
                break;
            }
            case "industry": {
                mode = 768;
                break;
            }

            case "chalk": {
                mode = 512;
                break;
            }

            case "charcoal": {
                mode = 576;
                break;
            }

            case "flower": {
                mode = 592;
                break;
            }

            case "wave": {
                mode = 560;
                break;
            }

            case "eraser": {
                mode = 112;
                break;
            }


            case "shade": {
                mode = 80;
                break;
            }


            case "watercolor": {
                mode = 55;
                break;
            }

            case "sketch oval": {
                mode = 272;
                break;
            }


            case "sketch fill": {
                mode = 256;
                break;
            }

            case "sketch pen": {
                mode = 264;
                break;
            }

            case "sketch wire": {
                mode = 257;
                break;
            }

            case "emboss": {
                mode = 96;
                break;
            }

            case "rainbow": {
                mode = 39;
                break;
            }

            case "inkpen": {
                mode = 56;
                break;
            }

            case "fountain": {
                mode = 561;
                break;
            }

            case "lane": {
                mode = 559;
                break;
            }

            case "streak": {
                mode = 562;
                break;
            }


            case "foliage": {
                mode = 563;
                break;
            }

            case "felt": {
                mode = 45;
                break;
            }

            case "halo": {
                mode = 46;
                break;
            }

            case "outline": {
                mode = 47;
                break;
            }

            case "cube line": {
                mode = 54;
                break;
            }

            case "dash line": {
                mode = 48;
                break;
            }
        }

        Log.e("brushsize mode", mode + "");

        return mode;
    }

    @Override
    public void onSubMenuClick(View view, GetCategoryPostModel.postData item, int position) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Child Item: " + item.getObjdata().post_title, Toast.LENGTH_SHORT).show();
        }

        StringConstants constants = new StringConstants();
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.subcategory_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.action_share:
                    onShareClick(item, position, this);
                    break;
                case R.id.action_open_tutorial:
                    selectChildItem(item, cate_name);
                    break;
                case R.id.action_rating:
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, constants.TUTORIAL_MENU_RATING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.TUTORIAL_MENU_RATING);
                    if (AppUtils.isLoggedIn()) {
                        openRatingDialog(item);
                    } else {
                        Intent intent = new Intent(NewSubCategoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();

    }

    private void openRatingDialog(GetCategoryPostModel.postData item) {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);
        final RatingBar rating = new RatingBar(this);

        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.topMargin = 20;
        rating.setLayoutParams(lp);
        rating.setNumStars(5);
        rating.setStepSize(1);

        //add ratingBar to linearLayout
        linearLayout.addView(rating);

        popDialog.setTitle(getString(R.string.rating));

        //add linearLayout to dailog
        popDialog.setView(linearLayout);

        rating.setOnRatingBarChangeListener((ratingBar, v, b) -> System.out.println("Rated val:" + v));

        // Button OK
        popDialog.setPositiveButton(R.string.done, (dialog, which) -> {
                    Toast.makeText(NewSubCategoryActivity.this, String.valueOf(rating.getProgress()), Toast.LENGTH_SHORT).show();
                    HashMap<String, Integer> map = new HashMap<String, Integer>();
                    map.put("rating", rating.getProgress());
                    FirebaseFirestoreApi.setRating(item.objdata.getID(), map, FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("RatingResult", "" + task.isSuccessful());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("RatingResult", e.getMessage());
                        }
                    });
                    dialog.dismiss();
                })

                // Button Cancel
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        // Fetch previous rating before showing dialog.
        FirebaseFirestoreApi.getRating(item.objdata.getID(), FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                Object value = ds.get("rating");

                if (value != null) {
                    rating.setRating(Float.valueOf(String.valueOf(value)));
                }
            }

            popDialog.create();
            popDialog.show();
        }).addOnFailureListener(e -> {
            popDialog.create();
            popDialog.show();
        });

    }

    public void openRatingDialogAll(Tutorialdatum item, Activity activity) {


        Dialog dialog = new Dialog(activity);

        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_rate_tut);

        dialog.setCancelable(false);

        me.zhanghai.android.materialratingbar.MaterialRatingBar rating = dialog.findViewById(R.id.pdfRatingBar);

        androidx.appcompat.widget.AppCompatButton btnSubmit = dialog.findViewById(R.id.btnYes);

        androidx.appcompat.widget.AppCompatButton btnCancel = dialog.findViewById(R.id.btnNo);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, String.valueOf(rating.getProgress()), Toast.LENGTH_SHORT).show();
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                map.put("rating", rating.getProgress());
                FirebaseFirestoreApi.setRating(item.getId(), map, StringConstants.constants.getString(StringConstants.constants.UserId,activity)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("RatingResult", "" + task.isSuccessful());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RatingResult", e.getMessage());
                    }
                });
                dialog.dismiss();
            }
        });


//        final AlertDialog.Builder popDialog = new AlertDialog.Builder(activity);

//        LinearLayout linearLayout = new LinearLayout(activity);
//        final RatingBar rating = new RatingBar(activity);

//        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        lp.topMargin = 20;
//        rating.setLayoutParams(lp);
//        rating.setNumStars(5);
//        rating.setStepSize(1);
//
//        //add ratingBar to linearLayout
//        linearLayout.addView(rating);
//
//        popDialog.setTitle(activity.getString(R.string.rating));
//
//        //add linearLayout to dailog
//        popDialog.setView(linearLayout);

//        rating.setOnRatingBarChangeListener((ratingBar, v, b) -> System.out.println("Rated val:" + v));

//        // Button OK
//        popDialog.setPositiveButton(R.string.done, (dialog, which) -> {
//
//                })

        // Button Cancel
//                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        // Fetch previous rating before showing dialog.
        FirebaseFirestoreApi.getRating(item.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                Object value = ds.get("rating");

                if (value != null) {
                    rating.setRating(Float.valueOf(String.valueOf(value)));
                }
            }

            dialog.show();

//            popDialog.create();
//            popDialog.show();
        }).addOnFailureListener(e -> {
//            popDialog.create();
//            popDialog.show();


            dialog.show();

        });

    }


    public void onShareClickAll(Tutorialdatum item, int position, Activity activity) {

        String imageUrl = item.getTutorialimages().getThumbnail();

        Glide.with(activity).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), resource, "", null);
                if (!TextUtils.isEmpty(path)) {
                    Uri uri = Uri.parse(path);
                    doSocialShare(uri, item.getTitle(), activity);
                }

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

    }


    private void onShareClick(GetCategoryPostModel.postData item, int position, Activity activity) {

        String imageUrl = item.getResize();

        Glide.with(this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), resource, "", null);
                if (!TextUtils.isEmpty(path)) {
                    Uri uri = Uri.parse(path);
                    doSocialShare(uri, item.getObjdata().getPost_title(), activity);
                }

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

    }

    public void doSocialShare(Uri photoURI, String title, Activity activity) {
//        try {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        String appStoreUrl = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        String text = activity.getResources().getString(R.string.subcategory_share_msg, title, appStoreUrl);

        ArrayList<Uri> files = new ArrayList<>();
        files.add(photoURI);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.subcategory_share_subject, cate_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("*/*");
        Intent receiver = new Intent(activity, ShareBroadcast.class);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(activity, 0, receiver, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(activity, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent chooser;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            chooser = Intent.createChooser(shareIntent, "Share To", pendingIntent.getIntentSender());
        } else {
            chooser = Intent.createChooser(shareIntent, "Share To");
        }
        activity.startActivity(chooser);
//        } catch (Exception e) {
//            Log.e("PaintActivity", "Paint Flow 13");
//            Log.e(PaintActivity.class.getName(), e.getMessage());
//        }
    }
}
