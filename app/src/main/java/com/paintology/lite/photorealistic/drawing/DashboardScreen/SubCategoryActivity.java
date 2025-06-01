package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.paintology.lite.photorealistic.drawing.Adapter.ShowSubCategoryAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type;
import com.paintology.lite.photorealistic.drawing.Model.CategoryModel;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.Overlaid;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum;
import com.paintology.lite.photorealistic.drawing.Model.sizes;
import com.paintology.lite.photorealistic.drawing.Model.text_files;
import com.paintology.lite.photorealistic.drawing.Model.trace_image;
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.interfaces.SubCategoryItemClickListener;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.ColorSwatchDao;
import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PostDetail_Main_Interface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

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
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity implements SubCategoryItemClickListener, PostDetail_Main_Interface {

    ArrayList<GetCategoryPostModel.postData> list;
    //    RecyclerView rv_tutorial_list;
    ShowSubCategoryAdapter adapter;
    SubCategoryItemClickListener _obj_interface;

    ApiInterface apiInterface;
    //    ProgressDialog progressDialog;
    String cateId;

    TextView tv_cate_name;
    String cate_name;
    String defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";
    public static PostDetail_Main_Interface obj_interface;
    private ArrayList<CategoryModel.categoryData> childList;
    //    private ArrayList<GetCategoryPostModel.postData> childList;
    private LinearLayout list_root;
    private int orientation;
    private boolean isTablet;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_online_tutorial);
        list_root = (LinearLayout) findViewById(R.id.list_root);
//        rv_tutorial_list = (RecyclerView) findViewById(R.id.rv_tutorial_list);
        tv_cate_name = (TextView) findViewById(R.id.tv_category_name);
        obj_interface = this;
        tv_cate_name.setAllCaps(false);


        orientation = getResources().getConfiguration().orientation;

        isTablet = getResources().getBoolean(R.bool.isTablet);

//        if (orientation == 1) {
//            if (isTablet)
//            {
//                rv_tutorial_list.setLayoutManager(new GridLayoutManager(this, 3));
//            }
//            else
//            {
////                rv_tutorial_list.setLayoutManager(new GridLayoutManager(this, 2));
//                rv_tutorial_list.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//
//            }
//        } else {
//            if (isTablet)
//            {
//                rv_tutorial_list.setLayoutManager(new GridLayoutManager(this, 4));
//            }
//            else
//            {
//                rv_tutorial_list.setLayoutManager(new GridLayoutManager(this, 3));
//            }
//        }

        _obj_interface = this;
        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);

        // database
        db = MyApplication.getDb();

        SharedPreferences lSharedPreferences = getPreferences(0);
        int mPrefBackgroundColor = lSharedPreferences.getInt("background-color", 0);
        Log.e("TAGGG", "BeginDoodle Logs Background Color " + mPrefBackgroundColor);
        if (getIntent().hasExtra("cate_id") && getIntent().getStringExtra("cate_id") != null)
            cateId = getIntent().getStringExtra("cate_id");

        if (getIntent().hasExtra("cate_name") && getIntent().getStringExtra("cate_name") != null) {
            cate_name = getIntent().getStringExtra("cate_name");
            tv_cate_name.setText(Html.fromHtml(cate_name));
        }

        if (getIntent().hasExtra("childs") && getIntent().getStringExtra("childs") != null) {
            String childJson = getIntent().getStringExtra("childs");
            Gson gson = new Gson();
            childList = gson.fromJson(childJson, ArrayList.class);
        }

        if (KGlobal.isInternetAvailable(SubCategoryActivity.this)) {
            getCategoryDataFromAPI(false);
        } else
            showSnackBar(getString(R.string.no_internet_msg));
    }

    public void closeApp(View v) {
        finish();
    }

    @Override
    public void selectItem(int pos, boolean b) {
        if (KGlobal.isInternetAvailable(SubCategoryActivity.this)) {

            String featured = getString(R.string.featured).toLowerCase();

            String cat = cate_name.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
            if (cat.contentEquals("paint_by_numbers")) {
                cat = "pbyno";
            }

            String evnt = "subcat_" + cat + "_" + featured.replaceAll("[^a-zA-Z0-9]", "_") + "_click";
            FirebaseUtils.logEvents(this, evnt);
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, evnt, Toast.LENGTH_SHORT).show();
            }

            featured = featured.substring(0, 4);

            if (list.get(pos).getObjdata().getRedirect_url() != null && !list.get(pos).getObjdata().getRedirect_url().isEmpty() && !list.get(pos).getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {

                String eventName = "subcat_" + list.get(pos).getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");

                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, featured);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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
                    String url = list.get(pos).getObjdata().getRedirect_url();
                    /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);*/
                    KGlobal.openInBrowser(SubCategoryActivity.this, url);
                } catch (ActivityNotFoundException ex) {

                } catch (Exception e) {

                }
                return;
            } else if (list.get(pos).getObjdata().getRedirect_url() != null && !list.get(pos).getObjdata().getRedirect_url().isEmpty() && list.get(pos).getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {
                String eventName = "subcat_" + list.get(pos).getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, featured);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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


                getCategoryDetailFromAPI(cateId, list.get(pos).getObjdata().getID());
            } else {
                Intent intent = new Intent(this, TutorialDetail_Activity.class);
                intent.putExtra("catID", cateId);
                intent.putExtra("postID", list.get(pos).getObjdata().getID());

                String eventName = "click_" + list.get(pos).getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, featured);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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
        } else
            showSnackBar(getString(R.string.no_internet_msg));
    }

    @Override
    public void selectChildItem(GetCategoryPostModel.postData item, String subCategoryName) {
        if (KGlobal.isInternetAvailable(SubCategoryActivity.this)) {
            System.out.println("OkHttpClient : selectChildItem");
            subCategoryName = subCategoryName.toLowerCase();

            String cat = cate_name.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
            if (cat.contentEquals("paint_by_numbers")) {
                cat = "pbyno";
            }

            String evnt = "subcat_" + cat + "_" + subCategoryName.replaceAll("[^a-zA-Z0-9]", "_") + "_click";
            FirebaseUtils.logEvents(this, evnt);
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, evnt, Toast.LENGTH_SHORT).show();
            }

            subCategoryName = subCategoryName.substring(0, 4);

            if (item.getObjdata().getRedirect_url() != null && !item.getObjdata().getRedirect_url().isEmpty() && !item.getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {

                String eventName = "subcat_" + item.getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, subCategoryName);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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
                    String url = item.getObjdata().getRedirect_url();
                    /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);*/
                    KGlobal.openInBrowser(SubCategoryActivity.this, url);
                } catch (ActivityNotFoundException ex) {

                } catch (Exception e) {

                }
                return;
            } else if (item.getObjdata().getRedirect_url() != null && !item.getObjdata().getRedirect_url().isEmpty() && item.getObjdata().getRedirect_url().equalsIgnoreCase("canvas")) {
                String eventName = "subcat_" + item.getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, subCategoryName);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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


                getCategoryDetailFromAPI(cateId, item.getObjdata().getID());
            } else {
                Intent intent = new Intent(this, TutorialDetail_Activity.class);
                intent.putExtra("catID", cateId);
                intent.putExtra("postID", item.getObjdata().getID());

                String eventName = "click_" + item.getObjdata().getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
                eventName = FirebaseUtils.getShortCategoryNameForEvent(eventName, subCategoryName);
                eventName = eventName.replace("___", "_");
                eventName = eventName.replace("__", "_");

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
        } else {
            showSnackBar(getString(R.string.no_internet_msg));
        }
    }

    void getCategoryDataFromAPI(Boolean isFromRefresh) {

        Call<GetCategoryPostModel> call = apiInterface.getCategoryPostList(ApiClient.SECRET_KEY, cateId);
        ProgressDialog progressDialog = new ProgressDialog(SubCategoryActivity.this);
        if (!isFromRefresh) {
            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        try {
            call.enqueue(new Callback<GetCategoryPostModel>() {
                @Override
                public void onResponse(Call<GetCategoryPostModel> call, Response<GetCategoryPostModel> response) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing() && !isFromRefresh) {
                            progressDialog.dismiss();
                        }

                        if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                            if (response.body().getPostList().size() > 0) {

                                list = response.body().getPostList();
                                adapter = new ShowSubCategoryAdapter(list, SubCategoryActivity.this, _obj_interface, false, null);
//                                rv_tutorial_list.setAdapter(adapter);

                                setupRecyclerView(adapter);
                            }
                        } else {
                            Toast.makeText(SubCategoryActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(SubCategoryActivity.class.getName(), e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<GetCategoryPostModel> call, Throwable t) {
                    showSnackBar("Failed To Retrieve Content!");
                    if (!isFromRefresh && progressDialog != null && progressDialog.isShowing() && !SubCategoryActivity.this.isDestroyed())
                        progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            if (!isFromRefresh && progressDialog != null && progressDialog.isShowing() && !SubCategoryActivity.this.isDestroyed())
                progressDialog.dismiss();

            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    void getSubCategoryDataFromAPI(RecyclerView recyclerView, String subCateId, TextView heading, String title) {

        Call<GetCategoryPostModel> call = apiInterface.getCategoryPostList(ApiClient.SECRET_KEY, subCateId);

        try {
            call.enqueue(new Callback<GetCategoryPostModel>() {
                @Override
                public void onResponse(Call<GetCategoryPostModel> call, Response<GetCategoryPostModel> response) {
                    try {

                        if (response != null && response.body() != null && (response.body().getCode() == 200)) {
                            if (response.body().getPostList().size() > 0) {

                                ArrayList<GetCategoryPostModel.postData> list = response.body().getPostList();
                                ShowSubCategoryAdapter adapter = new ShowSubCategoryAdapter(list,
                                        SubCategoryActivity.this, _obj_interface, true, title);

                                heading.setText(getString(R.string.heading_brackets, title, list.size()));

                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(SubCategoryActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(SubCategoryActivity.class.getName(), e.getMessage());
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

    private void setupRecyclerView(ShowSubCategoryAdapter adapter) {
        View view = getLayoutInflater().inflate(R.layout.subcategory_vertical_items, null);

        TextView heading = view.findViewById(R.id.tv_heading);
        RecyclerView recyclerView = view.findViewById(R.id.rv_list);

//        heading.setText(getString(R.string.heading_brackets,  Html.fromHtml(cate_name) , list.size()));
        heading.setText(getString(R.string.heading_brackets, getString(R.string.featured), list.size()));

//        if (orientation == 1) {
//            if (isTablet) {
////                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//                recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//            } else {
////                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//                recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//            }
//        } else {
//            if (isTablet) {
////                recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//                recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//            } else {
////                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//                recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//            }
//        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(adapter);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        view.setLayoutParams(layoutParams2);

        list_root.addView(view);

        setupChildren();

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.animationView);
        LottieDrawable drawable = new LottieDrawable();

        LottieComposition.Factory.fromRawFile(this, R.raw.swipe_right_arrows, (composition ->
        {
            drawable.setComposition(composition);
            drawable.playAnimation();
            drawable.setScale(0.3f);
            drawable.loop(true);
            lottieAnimationView.setImageDrawable(drawable);

        }));

        new Handler().postDelayed(() -> lottieAnimationView.setVisibility(View.GONE), 10000);
    }

    private void setupChildren() {
        for (int i = 0; i < childList.size(); i++) {

            Object item = this.childList.get(i);
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) item;

            Object objData = t.get("Data");
            LinkedTreeMap<Object, Object> dataMap = (LinkedTreeMap) objData;
//            CategoryModel.Data data = (CategoryModel.Data) t.get("Data");

            String term_id = dataMap.get("term_id").toString();
            String title = dataMap.get("name").toString();//.getCategoryName();

            String Resize = t.get("Resize").toString();


            Object objChild = t.get("Childs");
            ArrayList<CategoryModel.categoryData> childList = (ArrayList<CategoryModel.categoryData>) objChild;
//            ArrayList<CategoryModel.categoryData> Childs = (ArrayList<CategoryModel.categoryData>) t.get("Childs");


            View view = getLayoutInflater().inflate(R.layout.subcategory_vertical_items, null);

            LottieAnimationView lottieAnimationView = view.findViewById(R.id.animationView);
            lottieAnimationView.setVisibility(View.GONE);
            TextView heading = view.findViewById(R.id.tv_heading);
            RecyclerView recyclerView = view.findViewById(R.id.rv_list);

//            heading.setText("Tutorial: " + Html.fromHtml(cate_name) + " > " +title);
            heading.setText(getString(R.string.heading_brackets, title, 0));

//            if (orientation == 1) {
//                if (isTablet) {
////                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//                } else {
////                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//                }
//            } else {
//                if (isTablet) {
////                    recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//                } else {
////                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//                }
//            }

            recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));

//            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
//            view.setLayoutParams(layoutParams2);

            list_root.addView(view);

            getSubCategoryDataFromAPI(recyclerView, term_id, heading, title);

        }

        View ownAdView = getLayoutInflater().inflate(R.layout.subcategory_bottom_ads, null);
        ownAdView.findViewById(R.id.iv_udemy_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url_1 = "https://www.udemy.com/courses/search/?p=2&q=paintology";
                    KGlobal.openInBrowser(SubCategoryActivity.this, url_1);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(SubCategoryActivity.this, StringConstants.subcat_banner_udemy, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(SubCategoryActivity.this, StringConstants.subcat_banner_udemy);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
        });

        ownAdView.findViewById(R.id.iv_own_adv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(SubCategoryActivity.this, "subcat_banner_ferdouse_youtube", Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(SubCategoryActivity.this, "subcat_banner_ferdouse_youtube");

                    String url_1 = "https://www.youtube.com/c/Ferdouse";
                    KGlobal.openInBrowser(SubCategoryActivity.this, url_1);

                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
        });

        ownAdView.findViewById(R.id.iv_own_adv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(SubCategoryActivity.this, "subcat_banner_paintology_youtube", Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(SubCategoryActivity.this, "subcat_banner_paintology_youtube");

                    String url_1 = "https://www.youtube.com/channel/UCrR1Ya_KHuHyudP48FiR99A";
                    KGlobal.openInBrowser(SubCategoryActivity.this, url_1);

                } catch (ActivityNotFoundException e) {
                    Log.e("TAGGG", "Exception at view " + e.getMessage());
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }
        });

        list_root.addView(ownAdView);

    }

    void showSnackBar(String msg) {
//        Snackbar snackbar = Snackbar
//                .make(mSwipeRefreshLayout, msg, Snackbar.LENGTH_LONG);
//        snackbar.show();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void back(View v) {
        finish();
    }

    ProgressDialog progressDialog;

    void getCategoryDetailFromAPI(String catID, String postID) {
        apiInterface = ApiClient.getRetroClient().create(ApiInterface.class);

        Call<String> call = apiInterface.getPostDetail(ApiClient.SECRET_KEY, catID, postID);

        progressDialog = new ProgressDialog(SubCategoryActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing() && !SubCategoryActivity.this.isDestroyed()) {
                            progressDialog.dismiss();
                        }
                        if (response != null && response.body() != null) {
                            Log.e("TAGGG", "Response Data " + response.body());
                            parseResponseManually(response.body());
                        } else {
                            if (SubCategoryActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
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
                    showSnackBar("Failed To Retrieve Content!");
                }
            });
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing() && !SubCategoryActivity.this.isDestroyed())
                progressDialog.dismiss();

            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    PostDetailModel _object;
    Tutorial_Type tutorial_type;

    void parseResponseManually(String response) {
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
                    if (videoArray != null)
                        for (int i = 0; i < videoArray.length(); i++) {
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
                            } else
                                videos_and_files.setObj_text_files(null);

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
                                } else
                                    videos_and_files.setObj_trace_image(null);

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
                                } else
                                    videos_and_files.setObj_overlaid(null);

                            } catch (Exception e) {
                                Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
                            }
                            _lst_video_file.add(videos_and_files);
                        }

                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
                        _object.setVideo_and_file_list(_lst_video_file);
                } else
                    _object.setVideo_and_file_list(null);

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

            processTutorial();
        } catch (Exception e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
        }
    }

    @Override
    public void switchtoCanvas(String youtubeID) {
        StringConstants.IsFromDetailPage = false;
        Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
        intent.putExtra("youtube_video_id", youtubeID);
        intent.setAction("YOUTUBE_TUTORIAL");
        if (!_object.getCanvas_color().isEmpty()) {
            intent.putExtra("canvas_color", _object.getCanvas_color());
        }
        intent.putExtra("id", _object.getID());
        startActivity(intent);
    }

    void processTutorial() {

        if (tutorial_type == Tutorial_Type.See_Video) {
            String eventName = "watch_video_";
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(SubCategoryActivity.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getYoutube_link_list());
            intent.putExtra("isVideo", true);
            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
            String eventName = "read_post_";

            try {
                Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(SubCategoryActivity.this, _object.getExternal_link().replace("htttps://", "https://").trim());
            } catch (ActivityNotFoundException ex) {
            } catch (Exception e) {
            }
            return;
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
            String eventName = "watch_video_from_external_link_";

            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(SubCategoryActivity.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getExternal_link());
            intent.putExtra("isVideo", true);
            Log.e("TAGGG", "URL " + _object.getExternal_link());
            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            String eventName = "video_tutorial_overlaid_";


            String fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename();
            File file = new File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName);
            String youtubeLink = _object.getYoutube_link_list();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                if (!file.exists()) {
                    new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl(), false, _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename()).execute(_object.getVideo_and_file_list().get(0).obj_overlaid.getUrl());
                    return;
                } else {
//                    if (_object.getPost_title() != null)
//                        FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());

                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("youtube_video_id", _youtube_id);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this));
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }

                    List<ColorSwatch> swatches = _object.getSwatches();

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                    return;
                }
            } else {
                Toast.makeText(SubCategoryActivity.this, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            String eventName = "do_drawing_overlay_";

            String fileName = _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, false).execute();
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {

            String fileName = _object.getVideo_and_file_list().get(0).getObj_trace_image().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_trace_image().getUrl();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, true).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            new DownloadsTextFiles(_object).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {

            String OverLayName = "", OverLayUrl = "";

            if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
                OverLayName = (_object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            } else {
                OverLayName = (_object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl();
            }


            new DownloadOverlayImage(OverLayUrl, OverLayName).execute();

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
                                new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge(), true, "").execute(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge());
                            else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());

                                StringConstants.IsFromDetailPage = false;
                                Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                                intent.putExtra("youtube_video_id", _youtube_id);
                                intent.setAction("YOUTUBE_TUTORIAL");
                                intent.putExtra("paint_name", file.getAbsolutePath());
                                if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvas_color());
                                }
                                intent.putExtra("id", _object.getID());
                                startActivity(intent);
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                        StringConstants.IsFromDetailPage = false;
                        Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                        intent.putExtra("youtube_video_id", _youtube_id);
                        intent.setAction("YOUTUBE_TUTORIAL");
                        if (!_object.getCanvas_color().isEmpty()) {
                            intent.putExtra("canvas_color", _object.getCanvas_color());
                        }
                        intent.putExtra("id", _object.getID());
                        startActivity(intent);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(SubCategoryActivity.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            try {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(SubCategoryActivity.this, defaultLink.trim());
            } catch (ActivityNotFoundException anf) {

            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onSubMenuClickAll(@Nullable View view, @Nullable Tutorialdatum item, int position) {

    }

    @Override
    public void selectChildItemAll(@Nullable Tutorialdatum item, @Nullable String subCategoryName) {

    }

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
            progressDialog = new ProgressDialog(SubCategoryActivity.this);
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
            File path = new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this)); //Creates app specific folder

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
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());

                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }
                    intent.putExtra("id", _object.getID());
                    startActivity(intent);
                } else {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(SubCategoryActivity.this));
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
        PostDetailModel _objects;

        public DownloadsTextFiles(PostDetailModel _objects) {
            this._objects = _objects;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubCategoryActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(SubCategoryActivity.this));
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

                if (SubCategoryActivity.this.isDestroyed()) {
                    return;
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
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
                    Toast.makeText(SubCategoryActivity.this, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());

                intent.putExtra("id", _object.getID());

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
            progressDialog = new ProgressDialog(SubCategoryActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> filesList = downloadTextFiles();

            File file = new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this), fileName);

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
                File path = new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this)); //Creates app specific folder

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
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
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
                intent.putExtra("OverlaidImagePath", new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this), fileName).getAbsolutePath());
                intent.putExtra("id", _object.getID());
                startActivity(intent);
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }


        public ArrayList<String> downloadTextFiles() {
            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(SubCategoryActivity.this));
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

        public DownloadOverlayFromDoDrawing(String traceImageLink, String fileName, Boolean isFromTrace) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
            this.isFromTrace = isFromTrace;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubCategoryActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {

            File file = new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this), fileName);
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
                File path = new File(KGlobal.getTraceImageFolderPath(SubCategoryActivity.this)); //Creates app specific folder

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
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));

                StringConstants.IsFromDetailPage = false;
                if (isFromTrace) {
                    Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("Edit Paint");
                    intent.putExtra("FromLocal", true);
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
                    Intent intent = new Intent(SubCategoryActivity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(SubCategoryActivity.this));
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
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + path);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    @Override
    public void onSubMenuClick(View view, GetCategoryPostModel.postData item, int position) {

    }
}
