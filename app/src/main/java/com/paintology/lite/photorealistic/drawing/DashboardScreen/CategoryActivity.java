package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity;
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel;
import com.paintology.lite.photorealistic.drawing.Adapter.ShowCategoryAdapter;
import com.paintology.lite.photorealistic.drawing.Adapter.ShowCategoryAdapterNew;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.Category;
import com.paintology.lite.photorealistic.drawing.Model.CategoryModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.ads.callbacks.BannerCallBack;
import com.paintology.lite.photorealistic.drawing.ads.enums.NativeType;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item;
import com.paintology.lite.photorealistic.drawing.gallery.model_DownloadedTutorial;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration;
import com.paintology.lite.photorealistic.drawing.util.SpecingDecorationHorizontal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.videoguide.VideoGuideActivity;
import com.rey.material.app.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class CategoryActivity extends BaseActivity implements Interface_select_item {
    ProgressDialog progressDialog;
    RecyclerView rv_category_list;
    RecyclerView rv_level_list;
    ApiInterface apiInterface;

    LinkedHashMap<String, String> mlist = new LinkedHashMap<>();

    private String cateId;
    ArrayList<YourRankingModel> rankingList = new ArrayList<>();
    ArrayList<CategoryModel.categoryData> list_from_response = new ArrayList<>();

    ArrayList<Category> mCategories = new ArrayList<>();
    ArrayList<Category> mCategoriesSub = new ArrayList<>();
    ShowCategoryAdapter adapter;
    ShowCategoryAdapterNew adapterNew;
    Interface_select_item _obj_interface;

    LinearLayout mLinearNoTutorial;

    AppCompatButton mAppCompatButton;

    String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    private MenuItem actionProfile;
    private boolean showExitDialog;

    TextView mTextviewTitle;

    String level = "";

    int selectedlevel = 0;

    ImageView imageViewLevel;

    LevelRankingAdapter mYourRankingAdapter;

    BottomSheetDialog bottomSheetDialog;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        level = sharedPref.getString(StringConstants.user_level, StringConstants.beginner).toString();
        Bundle bundle = new Bundle();
        bundle.putString("level", level);
        bundle.putString("source", "default");
        ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_post_filter, bundle);

        if (getIntent().hasExtra("cate_id") && getIntent().getStringExtra("cate_id") != null)
            cateId = getIntent().getStringExtra("cate_id");
        else cateId = StringConstants.CATE_ID;


        setLevelList(true);

        frameLayout = findViewById(R.id.ads_place_holders);
        if (BuildConfig.DEBUG) {
            getDiComponent().getAdmobNativeAds().loadNativeAds(this,
                    frameLayout,
                    "ca-app-pub-3940256099942544/2247696110",
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeCommunity(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    NativeType.CUSTOM_DOWN,
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
            getDiComponent().getAdmobNativeAds().loadNativeAds(this,
                    frameLayout,
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeID(),
                    getDiComponent().getSharedPreferenceUtils().getRcvNativeCommunity(),
                    getDiComponent().getSharedPreferenceUtils().isAppPurchased(),
                    getDiComponent().getInternetManager().isInternetConnected(),
                    NativeType.CUSTOM_DOWN,
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
        String title = getString(R.string.default_collection) + " - " + level;
        //  setTitle(R.string.default_collection);
        //  setTitle(title);
        mTextviewTitle = toolbar.findViewById(R.id.toolbar_title);
        mTextviewTitle.setText(title);
        mCategories = new ArrayList<>();


        Intent intent = getIntent();
        showExitDialog = intent.getBooleanExtra("showExitDialog", false);

        mlist = new LinkedHashMap<>();

        _obj_interface = this;
        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);

        rv_category_list = findViewById(R.id.rv_cate_list);
        rv_level_list = findViewById(R.id.rv_level_list);

        mLinearNoTutorial = findViewById(R.id.linear_no);

        mAppCompatButton = findViewById(R.id.btn_paintology);
        mAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent1 = new Intent(CategoryActivity.this, VideoGuideActivity.class);
                startActivity(mIntent1);
            }
        });

        imageViewLevel = toolbar.findViewById(R.id.img_user_level);
        imageViewLevel.setOnClickListener(view -> {
            /*Intent intent1 = new Intent(CategoryActivity.this, YourRankingActivity.class);
            startActivity(intent1);*/
            mLinearNoTutorial.setVisibility(View.GONE);
            /*rv_category_list.setVisibility(View.GONE);
            rv_level_list.setVisibility(View.VISIBLE);*/
            openBottomSheetForLevel();
        });

        setLevel(level);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            if (isTablet)
                rv_category_list.setLayoutManager(new GridLayoutManager(this, 3));
            else
                rv_category_list.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            if (isTablet)
                rv_category_list.setLayoutManager(new GridLayoutManager(this, 4));
            else
                rv_category_list.setLayoutManager(new GridLayoutManager(this, 3));
        }
        //  getCategoryDataFromAPI();


        callFirebaseCloud(level);

        getTotalCategoryDataFromAPI();

    }

    void getTotalCategoryDataFromAPI() {

        FirebaseFirestoreApi.fetchTutorialsListCount("").addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {

                if (task.isSuccessful()) {
                    mlist = new LinkedHashMap<>();
                    HashMap<String, Object> list_from_response1 = (HashMap<String, Object>) task.getResult().getData();
                    // Log.e("result22", list_from_response1.toString());
                    HashMap<String, Object> page = (HashMap<String, Object>) list_from_response1.get("page");
                    List<HashMap<String, Object>> levelListCount = (List<HashMap<String, Object>>) list_from_response1.get("facet_counts");

                    Log.e("levelcount ", "main : " + levelListCount.size());

                    for (int i = 0; i < levelListCount.size(); i++) {
                        List<HashMap<String, Object>> counts = (List<HashMap<String, Object>>) levelListCount.get(i).get("counts");
                        Log.e("levelcount ", "counts : " + counts.size());

                        for (int j = 0; j < counts.size(); j++) {
                            mlist.put(counts.get(j).get("value").toString(), counts.get(j).get("count").toString());
                        }

                        Log.e("levelcount ", "counts new: " + mlist.size());
                        Log.e("levelcount ", "counts : " + mlist.get("Beginner 1"));

                    }


                    setLevelList(false);

                }


            }
        });

        /*Call<CategoryModel> call = apiInterface.getCategoryList(ApiClient.SECRET_KEY);

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

    private void openBottomSheetForLevel() {
        bottomSheetDialog = new BottomSheetDialog(
                CategoryActivity.this);
        View bottomSheetView = getLayoutInflater()
                .inflate(R.layout.bottomsheet_level, null);

        bottomSheetDialog.setContentView(bottomSheetView);

        BottomSheetBehavior sheetBehavior;

/*
        CoordinatorLayout mCoordinatorLayout = bottomSheetView.findViewById(R.id.bottomSheet);

        sheetBehavior = BottomSheetBehavior.from(bottomSheetView.getRootView());

        sheetBehavior.setHalfExpandedRatio(0.6f);
*/

/*
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mCoordinatorLayout.getLayoutParams();
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
*/

       /* int desiredHeight = (int) Math.round(height * 0.55);
        layoutParams.height = desiredHeight;
        mCoordinatorLayout.setLayoutParams(layoutParams);*/
        RecyclerView mRecyclerView = bottomSheetView.findViewById(R.id.recyclerView_level);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int space = getResources().getDimensionPixelSize(R.dimen._80sdp);
        SpecingDecoration spacingDecoration = new SpecingDecoration(0, space);
        mRecyclerView.addItemDecoration(spacingDecoration);


        mYourRankingAdapter = new LevelRankingAdapter(rankingList, _obj_interface, selectedlevel);
        mRecyclerView.setAdapter(mYourRankingAdapter);


        bottomSheetDialog.show();

    }

    private void callFirebaseCloud(String levels) {
        mLinearNoTutorial.setVisibility(View.GONE);

        try {
            progressDialog = new ProgressDialog(CategoryActivity.this);

            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setMessage("Loading Tutorials...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> levelsList = new ArrayList<>();
        levelsList.add(levels);
        Log.e("level", levels);

        FirebaseFirestoreApi.fetchCategoryStagingList(levelsList).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {

                Category mSelectedCategory = null;

                if (task.isSuccessful()) {

                    mCategories = new ArrayList<>();
                    mCategoriesSub = new ArrayList<>();
                    List<Map<String, Object>> list_from_response1 = (List<Map<String, Object>>) task.getResult().getData();
                    Log.e("result", list_from_response1.toString());
                    for (int i = 0; i < list_from_response1.size(); i++) {
                        Category mCategory = new Category();
                        String id = (String) list_from_response1.get(i).get("id");
                        String name = (String) list_from_response1.get(i).get("name");
                        String sorting_number = list_from_response1.get(i).get("sorting_number").toString();
                        String level = list_from_response1.get(i).get("level").toString();
                        String parent_id = (String) list_from_response1.get(i).get("parent_id");
                        String total_tutorials = list_from_response1.get(i).get("total_tutorials").toString();
                        String thumbnail = (String) list_from_response1.get(i).get("thumbnail");


                        if (list_from_response1.get(i).containsKey("levels")) {
                            ArrayList<String> levels = (ArrayList<String>) list_from_response1.get(i).get("levels");
                            if (levels.size() > 0) {
                                mCategory.setLevels(levels);
                            }
                        } else {
                            mCategory.setLevels(new ArrayList<>());
                        }

                        if (list_from_response1.get(i).containsKey("statistic")) {
                            Map<String, Object> statisticMap = (Map<String, Object>) list_from_response1.get(i).get("statistic");
                            Category.Statistic statistic = new Category.Statistic();
                            String views = statisticMap.containsKey("views") ? statisticMap.get("views").toString() : "0";
                            statistic.setViews(views);
                            mCategory.setStatistic(statistic);
                        } else {
                            Category.Statistic statistic = new Category.Statistic();
                            statistic.setViews("0");
                            mCategory.setStatistic(statistic);
                        }

                        mCategory.setId(id);
                        mCategory.setName(name);
                        mCategory.setSorting_number(sorting_number);
                        mCategory.setLevel(level);
                        mCategory.setParent_id(parent_id);
                        mCategory.setTotal_tutorials(total_tutorials);
                        mCategory.setThumbnail(thumbnail);

                        List<Category> mChild = new ArrayList<>();
                        if (list_from_response1.get(i).containsKey("childs")) {
                            List<Map<String, Object>> list_from_response2 = (List<Map<String, Object>>) list_from_response1.get(i).get("childs");

                            for (int j = 0; j < list_from_response2.size(); j++) {
                                Category mCategoryChild = new Category();
                                String idChild = (String) list_from_response2.get(j).get("id");
                                String nameChild = (String) list_from_response2.get(j).get("name");
                                String sorting_numberChild = list_from_response2.get(j).get("sorting_number").toString();
                                String levelChild = list_from_response2.get(j).get("level").toString();
                                String parent_idChild = (String) list_from_response2.get(j).get("parent_id");
                                String total_tutorialsChild = list_from_response2.get(j).get("total_tutorials").toString();
                                String thumbnailChild = (String) list_from_response2.get(j).get("thumbnail");

                                mCategoryChild.setId(idChild);
                                mCategoryChild.setName(nameChild);
                                mCategoryChild.setSorting_number(sorting_numberChild);
                                mCategoryChild.setLevel(levelChild);
                                mCategoryChild.setParent_id(parent_idChild);
                                mCategoryChild.setTotal_tutorials(total_tutorialsChild);
                                mCategoryChild.setThumbnail(thumbnailChild);

                                if (list_from_response2.get(j).containsKey("levels")) {
                                    ArrayList<String> levels = (ArrayList<String>) list_from_response2.get(j).get("levels");
                                    if (levels.size() > 0) {
                                        mCategoryChild.setLevels(levels);
                                    }
                                }

                                mChild.add(mCategoryChild);
                            }
                        }
                        mCategory.setChilds(mChild);
                        mCategories.add(mCategory);

                        if (cateId != null && cateId.equals(id)) {
                            mSelectedCategory = mCategory;
                        }

                        Log.e("category", levels);
                        Log.e("category", mCategory.getLevels().toString());

                        if (mCategory.getLevels().contains(levels)) {
                            mCategoriesSub.add(mCategory);
                        }
                    }

                    if (mCategoriesSub.size() == 0) {
                        mLinearNoTutorial.setVisibility(View.VISIBLE);
                    } else {
                        mLinearNoTutorial.setVisibility(View.GONE);
                    }

                   /* adapterNew = new ShowCategoryAdapterNew(mCategoriesSub, CategoryActivity.this, _obj_interface);
                    int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                            getResources().getDisplayMetrics());
                    rv_category_list.setAdapter(adapterNew);*/


                }

                try {
                    progressDialog.dismiss();

                    if (mSelectedCategory != null) {
                        Intent intent = new Intent(CategoryActivity.this, NewSubCategoryActivity.class);
                        intent.putExtra("cate_id", mSelectedCategory.getId());
                        intent.putExtra("level", level);
                        intent.putExtra("childs", new Gson().toJson(mSelectedCategory.getChilds()));
                        intent.putExtra("total_tutorials", Integer.valueOf(mSelectedCategory.getTotal_tutorials()));
                        intent.putExtra("cate_name", mSelectedCategory.getName());
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void setLevelList(boolean isCheck) {

        rankingList.clear();


        String count = "0";
        if (mlist.containsKey("Beginner 1")) {
            count = mlist.get("Beginner 1").toString();
        }


        rankingList.add(new YourRankingModel(
                1,
                R.drawable.img_beginner_1,
                "Beginner 1",
                count
        ));

        count = "0";
        if (mlist.containsKey("Beginner 2")) {
            count = mlist.get("Beginner 2").toString();
        }

        rankingList.add(new YourRankingModel(
                2,
                R.drawable.img_beginner_2,
                "Beginner 2",
                count
        ));

        count = "0";
        if (mlist.containsKey("Beginner 3")) {
            count = mlist.get("Beginner 3").toString();
        }

        rankingList.add(new YourRankingModel(
                3,
                R.drawable.img_beginner_3,
                "Beginner 3",
                count
        ));

        count = "0";
        if (mlist.containsKey("Intermediate 1")) {
            count = mlist.get("Intermediate 1").toString();
        }

        rankingList.add(new YourRankingModel(
                4,
                R.drawable.img_intermidiate_1,
                "Intermediate 1",
                count
        ));

        count = "0";
        if (mlist.containsKey("Intermediate 2")) {
            count = mlist.get("Intermediate 2").toString();
        }

        rankingList.add(new YourRankingModel(
                5,
                R.drawable.img_intermidiate_2,
                "Intermediate 2",
                count
        ));


        count = "0";
        if (mlist.containsKey("Intermediate 3")) {
            count = mlist.get("Intermediate 3").toString();
        }

        rankingList.add(new YourRankingModel(
                6,
                R.drawable.img_intermidiate_3,
                "Intermediate 3",
                count
        ));

        count = "0";
        if (mlist.containsKey("Advanced 1")) {
            count = mlist.get("Advanced 1").toString();
        }

        rankingList.add(new YourRankingModel(
                7,
                R.drawable.img_advance_1,
                "Advanced 1",
                count
        ));

        count = "0";
        if (mlist.containsKey("Advanced 2")) {
            count = mlist.get("Advanced 2").toString();
        }


        rankingList.add(new YourRankingModel(
                8,
                R.drawable.img_advance_2,
                "Advanced 2",
                count
        ));

        count = "0";
        if (mlist.containsKey("Advanced 3")) {
            count = mlist.get("Advanced 3").toString();
        }


        rankingList.add(new YourRankingModel(
                9,
                R.drawable.img_advance_3,
                "Advanced 3",
                count
        ));

        count = "0";
        if (mlist.containsKey("Expert")) {
            count = mlist.get("Expert").toString();
        }


        rankingList.add(new YourRankingModel(
                10,
                R.drawable.img_expert,
                "Expert",
                count
        ));

        if (isCheck) {
            try {
                for (int i = 0; i < rankingList.size(); i++) {
                    if (rankingList.get(i).getTvRankLevel().equalsIgnoreCase(level)) {
                        selectedlevel = i;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*rv_level_list.setLayoutManager(new LinearLayoutManager(this));

        int space = getResources().getDimensionPixelSize(R.dimen._80sdp);
        SpecingDecoration spacingDecoration = new SpecingDecoration(0, space);
        rv_level_list.addItemDecoration(spacingDecoration);

         mYourRankingAdapter = new LevelRankingAdapter(rankingList,_obj_interface);
        rv_level_list.setAdapter(mYourRankingAdapter);*/

    }

    private void setupActionBarContent() {
        Glide.with(CategoryActivity.this)
                .load(constants.getString(constants.ProfilePicsUrl, CategoryActivity.this))
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


    void getCategoryDataFromAPI() {

        Call<CategoryModel> call = apiInterface.getCategoryList(ApiClient.SECRET_KEY);
        progressDialog = new ProgressDialog(CategoryActivity.this);

        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setMessage("Loading Tutorials...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

//        try {
        call.enqueue(new Callback<CategoryModel>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onResponse(Call<CategoryModel> call, retrofit2.Response<CategoryModel> response) {

                try {
                    if (progressDialog != null && progressDialog.isShowing() && !CategoryActivity.this.isDestroyed())
                        progressDialog.dismiss();
                } catch (Exception e) {
                    Log.e(CategoryActivity.class.getName(), e.getMessage());
                }

                longLog(new Gson().toJson(response.body()));

                if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                    if (response.body().getCategoryList() != null && response.body().getCategoryList().size() > 0) {
                        list_from_response = response.body().getCategoryList();
                            /*adapter = new ShowCategoryAdapter(list_from_response, CategoryActivity.this, _obj_interface);
                            rv_category_list.setAdapter(adapter);*/
                    } else {
                        Log.e("TAGG", getResources().getString(R.string.empty_list));
                    }
                } else {
                    Log.e("TAGG", (response != null && response.body() != null) ? response.body().getResponse() : "Failed");
                }
//                getSubCategortData(ApiClient.CAT_ID);

                adapter = new ShowCategoryAdapter(list_from_response, CategoryActivity.this, _obj_interface);
                int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()); // calculated
                rv_category_list.addItemDecoration(new SpecingDecorationHorizontal(1, space));
                rv_category_list.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                try {

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        } catch (Exception e) {
//            progressDialog.dismiss();
//            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
//        }
    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("Tag111", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("Tag111", str);
    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        if (KGlobal.isInternetAvailable(CategoryActivity.this)) {
            // if (list_from_response.get(pos).isTypeCategory()) {
            Gson gson = new Gson();


            Bundle bundle = new Bundle();
            bundle.putString("category_id", mCategories.get(pos).getId());
            bundle.putString("category_name", mCategories.get(pos).getName());
            ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_open_category, bundle);

            FirebaseFirestoreApi.incrementCategoryViewsCount(mCategories.get(pos).getId());

            Intent intent = new Intent(this, NewSubCategoryActivity.class);
            intent.putExtra("cate_id", mCategories.get(pos).getId());
            intent.putExtra("level", level);
            intent.putExtra("childs", gson.toJson(mCategories.get(pos).getChilds()));
            intent.putExtra("total_tutorials", Integer.valueOf(mCategories.get(pos).getTotal_tutorials()));
            intent.putExtra("cate_name", mCategories.get(pos).getName());
            Log.e("total", mCategories.get(pos).getTotal_tutorials());
            try {
                String eventName = "cat_" + (mCategories.get(pos).getName());
                if (eventName.length() >= 39) {
                    String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 39));
                    FirebaseUtils.logEvents(this, upToNCharacters);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    FirebaseUtils.logEvents(this, eventName);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("TAGG", "Exception at send event " + e.getMessage());
            }
            startActivity(intent);
            //   }
           /* else {
//                getCategoryDetailFromAPI(categoryID, list_from_response.get(pos).getObj_data().getCate_id());
                if (list_from_response.get(pos).getObj_data().getRedirect_url() != null &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().isEmpty() &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().equalsIgnoreCase("canvas")) {

                    String eventName = "cr_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }
                    try {
                        String url = list_from_response.get(pos).getObj_data().getRedirect_url();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        KGlobal.openInBrowser(CategoryActivity.this, url);
                    } catch (ActivityNotFoundException ex) {

                    } catch (Exception e) {

                    }
                    return;
                } else if (list_from_response.get(pos).getObj_data().getRedirect_url() != null &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().isEmpty() &&
                        list_from_response.get(pos).getObj_data().getRedirect_url().equalsIgnoreCase("canvas")) {
                    String eventName = "cr_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }

                    getCategoryDetailFromAPI(ApiClient.CAT_ID, list_from_response.get(pos).getObj_data().getCate_id());
                } else {
                    Intent intent = new Intent(this, TutorialDetail_Activity.class);
                    intent.putExtra("catID", ApiClient.CAT_ID);
                    intent.putExtra("postID", list_from_response.get(pos).getObj_data().getCate_id());

                    String eventName = "click_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }
                    startActivity(intent);
                }
            }*/
        } else {
            Toast.makeText(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
        }
    }

/*    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        if (KGlobal.isInternetAvailable(CategoryActivity.this)) {
            if (list_from_response.get(pos).isTypeCategory()) {
                Gson gson = new Gson();

                Intent intent = new Intent(this, NewSubCategoryActivity.class);
                intent.putExtra("cate_id", list_from_response.get(pos).getObj_data().getCate_id());
                intent.putExtra("childs", gson.toJson(list_from_response.get(pos).getChilds()));
                intent.putExtra("total_tutorials", list_from_response.get(pos).getObj_data().getTotalTutorials());
                intent.putExtra("cate_name", (list_from_response.get(pos).getObj_data().getCategoryName() != null ? list_from_response.get(pos).getObj_data().getCategoryName() : ""));
                try {
                    String eventName = "cat_" + (list_from_response.get(pos).getObj_data().getCategoryName() != null ? list_from_response.get(pos).getObj_data().getCategoryName().replaceAll("[^a-zA-Z0-9]", "_") : "");
                    if (eventName.length() >= 39) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 39));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at send event " + e.getMessage());
                }
                startActivity(intent);
            }
            else {
//                getCategoryDetailFromAPI(categoryID, list_from_response.get(pos).getObj_data().getCate_id());
                if (list_from_response.get(pos).getObj_data().getRedirect_url() != null &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().isEmpty() &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().equalsIgnoreCase("canvas")) {

                    String eventName = "cr_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }
                    try {
                        String url = list_from_response.get(pos).getObj_data().getRedirect_url();
Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);

                        KGlobal.openInBrowser(CategoryActivity.this, url);
                    } catch (ActivityNotFoundException ex) {

                    } catch (Exception e) {

                    }
                    return;
                } else if (list_from_response.get(pos).getObj_data().getRedirect_url() != null &&
                        !list_from_response.get(pos).getObj_data().getRedirect_url().isEmpty() &&
                        list_from_response.get(pos).getObj_data().getRedirect_url().equalsIgnoreCase("canvas")) {
                    String eventName = "cr_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }

                    getCategoryDetailFromAPI(ApiClient.CAT_ID, list_from_response.get(pos).getObj_data().getCate_id());
                } else {
                    Intent intent = new Intent(this, TutorialDetail_Activity.class);
                    intent.putExtra("catID", ApiClient.CAT_ID);
                    intent.putExtra("postID", list_from_response.get(pos).getObj_data().getCate_id());

                    String eventName = "click_" + list_from_response.get(pos).getObj_data().getCategoryName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                    if (eventName.length() >= 35) {
                        String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                        FirebaseUtils.logEvents(this, upToNCharacters);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, upToNCharacters, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        FirebaseUtils.logEvents(this, eventName);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
                        }
                    }
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
        }
    }*/


    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {

    }

    public void back(View view) {
        finish();
    }


    StringConstants constants = new StringConstants();

    @Override
    protected void onPause() {
        super.onPause();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(CategoryActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, CategoryActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, CategoryActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called CategoryActivity 1176");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(CategoryActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, CategoryActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, CategoryActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called CategoryActivity 1198");
                    MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                }
            }
        } catch (Exception e) {
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MyConstantsKt.commonMenuClick(this, item, StringConstants.intro_tutorials);
    }*/

    public void beginDoodle() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(CategoryActivity.this, constants.CLICK_DRAW_ICON, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(CategoryActivity.this, constants.CLICK_DRAW_ICON);
        ContextKt.setSharedNo(this);
        constants.putInt("background_color", -1, CategoryActivity.this);
        Intent _intent = new Intent(CategoryActivity.this, PaintActivity.class);
        _intent.setAction("New Paint");
        _intent.putExtra("background_color", -1);
        startActivity(_intent);
    }

    @Override
    public void onSubMenuClick(View view, model_DownloadedTutorial item, int position) {
        if (rankingList != null && rankingList.size() > position) {
             /*   mCategoriesSub = new ArrayList<>();
                for(int i=0;i<mCategories.size();i++){
                    if(mCategories.get(i).getLevels().contains(rankingList.get(position).getTvRankLevel())){
                        mCategoriesSub.add(mCategories.get(i));
                    }
                }
                adapterNew = new ShowCategoryAdapterNew(mCategoriesSub, CategoryActivity.this, _obj_interface);
                int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()); // calculated
                rv_category_list.addItemDecoration(new SpecingDecorationHorizontal(1,space));
                rv_category_list.setAdapter(adapterNew);

*/
            selectedlevel = position;
            bottomSheetDialog.dismiss();
            rv_category_list.setVisibility(View.VISIBLE);
            rv_level_list.setVisibility(View.GONE);
            level = rankingList.get(position).getTvRankLevel();
            setLevel(level);
            if (level != null) {
                Bundle bundle = new Bundle();
                bundle.putString("level", level);
                bundle.putString("source", "dialog");
                ContextKt.sendUserEventWithParam(this, StringConstants.tutorials_post_filter, bundle);
            }
            callFirebaseCloud(rankingList.get(position).getTvRankLevel());
            mTextviewTitle.setText(getString(R.string.default_collection) + " - " + rankingList.get(position).getTvRankLevel());
        }
    }

    public void setLevel(String level) {
        if (level.equalsIgnoreCase("Beginner 1")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_beginner_1);
        } else if (level.equalsIgnoreCase("Beginner 2")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_beginner_2);
        } else if (level.equalsIgnoreCase("Beginner 3")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_beginner_3);
        } else if (level.equalsIgnoreCase("Intermediate 1")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_intermediate_1);
        } else if (level.equalsIgnoreCase("Intermediate 2")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_intermediate_2);
        } else if (level.equalsIgnoreCase("Intermediate 3")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_intermediate_3);
        } else if (level.equalsIgnoreCase("Advanced 1")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_advanced_1);
        } else if (level.equalsIgnoreCase("Advanced 2")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_advanced_2);
        } else if (level.equalsIgnoreCase("Advanced 3")) {
            imageViewLevel.setImageResource(R.drawable.img_challenge_advanced_3);
        } else if (level.equalsIgnoreCase("Expert")) {
            imageViewLevel.setImageResource(R.drawable.img_expert);
        }
    }

    @Override
    public void onMovieIconClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onEditClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onDeleteClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onShareClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onPostClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onBackPressed() {
        if (rv_level_list.getVisibility() == View.VISIBLE) {
            rv_level_list.setVisibility(View.GONE);
            rv_category_list.setVisibility(View.VISIBLE);
        } else if (mLinearNoTutorial.getVisibility() == View.VISIBLE || !level.equalsIgnoreCase(sharedPref.getString(StringConstants.user_level, StringConstants.beginner))) {
            rv_level_list.setVisibility(View.GONE);
            mLinearNoTutorial.setVisibility(View.GONE);
            rv_category_list.setVisibility(View.VISIBLE);
            //mYourRankingAdapter.setSelectedPos(0);
            selectedlevel = 0;
            level = sharedPref.getString(StringConstants.user_level, StringConstants.beginner);
            try {
                for (int i = 0; i < rankingList.size(); i++) {
                    if (rankingList.get(i).getTvRankLevel().equalsIgnoreCase(level)) {
                        selectedlevel = i;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            setLevel(level);
            mTextviewTitle.setText(getString(R.string.default_collection) + " - " + level);
            callFirebaseCloud(level);
        } else if (showExitDialog) {
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
                                startActivity(new Intent(CategoryActivity.this, GalleryDashboard.class));
                                finish();
                            }
                    )
                    .show();
        } else {
            super.onBackPressed();
        }
    }


}
