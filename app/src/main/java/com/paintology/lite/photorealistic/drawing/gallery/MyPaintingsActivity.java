package com.paintology.lite.photorealistic.drawing.gallery;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Activity.MyConstantsKt;
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils;
import com.paintology.lite.photorealistic.drawing.Activity.video_intro.IntroVideoListActivity;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail;
import com.paintology.lite.photorealistic.drawing.CustomePicker.PostActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity;
import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.app.manifest.AppManifest;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.interfaces.MenuItemClickListener;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.painting.PaintItem;
import com.paintology.lite.photorealistic.drawing.painting.file.ImageManager;
import com.paintology.lite.photorealistic.drawing.photoeditor.DovCharney.PatrickCox;
import com.paintology.lite.photorealistic.drawing.photoeditor.PhotoEditorActivity;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.PaintingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.PublishDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedDrawingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedTutorialDao;
import com.paintology.lite.photorealistic.drawing.room.entities.PaintingEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.PublishEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPaintingsActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        MenuItemClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 7;
    int a = 10;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    GoogleApiClient googleApiClient;

    GoogleSignInAccount account;

    LoginButton facebook_login_btn;

    boolean isLoggedIn;

    ProgressDialog progressDialog = null;
    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String LoginInPaintology;


    HashMap<String, SavedDrawingEntity> entityHashMap = new HashMap<>();

    ImageAdapter mAdapter;

    RecyclerView mGridView;
    ImageManager mImageManager;
    int mImageViewWidth;
    int mImageViewHeight;
    SharedPreferences mPrefs;
    ArrayList<PaintItem> mThumbnailItems = new ArrayList<>();

    HashMap<String, PaintItem> mTraces = new HashMap<>();
    HashMap<String, PaintItem> mDrawings = new HashMap<>();


    StringConstants constants = new StringConstants();

    ImageView iv_default_img;
    boolean isShareClicked = false;


    public MyPaintingsActivity() {
        ArrayList lArrayList = new ArrayList();
        mThumbnailItems = lArrayList;
    }


    private void freeThumbnails() {
        Iterator lIterator = mThumbnailItems.iterator();
        while (true) {
            if (!lIterator.hasNext())
                return;
            ((PaintItem) lIterator.next()).freeThumbnail();
        }
    }

    private void loadThumbnails() {
        freeThumbnails();
        mThumbnailItems.clear();
        mImageManager.getAllPaints(this, mThumbnailItems);


        mThumbnailItems.sort(Comparator.comparingLong(PaintItem::getLastModifiedTime));
        ArrayList<PaintItem> paintItems = new ArrayList<>(mThumbnailItems);
        mThumbnailItems.clear();
        for (int i = 0; i < paintItems.size(); i++) {
            if (paintItems.get(i).getFileName().contains("_Trace") || paintItems.get(i).getFileName().contains("_Drawing")) {
                continue;
            }
            if (paintItems.get(i).get_drawing_type().toString().equalsIgnoreCase("None")) {
                mThumbnailItems.add(paintItems.get(i));
            } else if (paintItems.get(i).get_drawing_type().toString().equalsIgnoreCase("TraceDrawaing")) {
                PaintItem mPaintItem = paintItems.get(i);
                mThumbnailItems.add(mPaintItem);
                if (paintItems.get(i + 1).getFileName().contains("_Trace")) {
                    i++;
                    mTraces.put(mPaintItem.getFileName(), paintItems.get(i));
                    if (paintItems.get(i + 1).getFileName().contains("_Drawing")) {
                        i++;
                        mDrawings.put(mPaintItem.getFileName(), paintItems.get(i));
                    }
                } else if (paintItems.get(i + 1).getFileName().contains("_Drawing")) {
                    i++;
                    mDrawings.put(mPaintItem.getFileName(), paintItems.get(i));
                    if (paintItems.get(i + 1).getFileName().contains("_Trace")) {
                        i++;
                        mTraces.put(mPaintItem.getFileName(), paintItems.get(i));
                    }
                }
            } else if (paintItems.get(i).get_drawing_type().toString().equalsIgnoreCase("OverlayDrawing")) {
                PaintItem mPaintItem = paintItems.get(i);
                mThumbnailItems.add(mPaintItem);
                if (paintItems.get(i + 1).getFileName().contains("_Trace")) {
                    i++;
                    mTraces.put(mPaintItem.getFileName(), paintItems.get(i));
                }
            } else {
                mThumbnailItems.add(paintItems.get(i));
            }
        }
        Collections.reverse(mThumbnailItems);

        startWork();
    }


    private void setBackgrounds(Resources pResources) {
    }


    //    int prevSelectedpos = 0;
    String parentFolderPath = "";

    long time;
    boolean firstTouch = false;

    ImageView iv_post_icon, iv_play_youtube;
    ApiInterface apiInterface;

    boolean isMultiSelectEnable = false;
    private DownloadManager mDownloadManager;
    private long referenceID;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private boolean showExitDialog;

    public static MyPaintingsActivity instance;

    public static MyPaintingsActivity getInstance() {
        return instance;
    }

    boolean isFromGallery = false;

    public void onCreate(Bundle pBundle) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(pBundle);
        SharedPreferences lSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs = lSharedPreferences;
        setContentView(R.layout.activity_my_paintings);

        instance = this;

        MyConstantsKt.checkForIntroVideo(this, StringConstants.intro_paintings);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.my_painting));

        Intent intent = getIntent();
        showExitDialog = intent.getBooleanExtra("showExitDialog", false);

        // check if user is from galley screen or not
        isFromGallery = intent.getBooleanExtra("isFromGallery", false);

        facebook_login_btn = findViewById(R.id.login_button_dashboard);
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
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MyPaintingsActivity.this, gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        LoginInPaintology = constants.getString(constants.LoginInPaintology, this);

        apiInterface = ApiClient.getClient_1().create(ApiInterface.class);

        mGridView = (RecyclerView) findViewById(R.id.gridview_1);

        int orientation = getResources().getConfiguration().orientation;

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        if (orientation == 1) {
            if (isTablet)
                manager.setSpanCount(3);
            else
                manager.setSpanCount(2);
        } else {
            if (isTablet)
                manager.setSpanCount(4);
            else
                manager.setSpanCount(3);
        }

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    if (orientation == 1) {
                        if (isTablet)
                            return 3;
                        else
                            return 2;
                    } else {
                        if (isTablet)
                            return 4;
                        else
                            return 3;
                    }

                } else {
                    return 1;
                }
            }
        });

        mGridView.setLayoutManager(manager);
        parentFolderPath = KGlobal.getMyPaintingFolderPath(this);


        ImageManager lImageManager = new ImageManager(this, parentFolderPath);
        mImageManager = lImageManager;
        ImageAdapter lImageAdapter = new ImageAdapter(this, this, isFromGallery);
        mAdapter = lImageAdapter;
        mGridView.setAdapter(mAdapter);
        AppManifest appManifest = new AppManifest();
        mImageViewWidth = appManifest.computeBestThumbnailWidth(this);
        mImageViewHeight = appManifest.computeBestThumbnailHeight(this);

        iv_default_img = findViewById(R.id.iv_default_img);

        LoadData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LoadData();
    }

    public void onDestroy() {
        freeThumbnails();
        mImageManager.destroy();
        mImageManager = null;
        mThumbnailItems = null;
        mAdapter = null;
        System.gc();
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        if (isShareClicked) {
            isShareClicked = false;
            return;
        }

    }

    public void onStop() {
        int i = Log.i("GalleryPicker", "onStop");
        freeThumbnails();
        super.onStop();
        System.gc();
    }


    @SuppressLint("SetTextI18n")
    public void LoadData() {

        if (isShareClicked) {
            isShareClicked = false;
            return;
        }


        new Thread(() -> {
            List<SavedDrawingEntity> entities = MyApplication.getDb().savedDrawingDao().getAll();
            for (SavedDrawingEntity entity : entities) {
                File file = new File(entity.localPath);
                if (file.exists()) {
                    entityHashMap.put(file.getName(), entity);
                }
            }
        }).start();
        try {
            isMultiSelectEnable = false;
            loadThumbnails();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAGRR", e.toString());
            startWork();
        }


    }


    public void checkData() {
        ConstraintLayout noPaintings = findViewById(R.id.noPaintingsLayout);
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 1) {
                noPaintings.setVisibility(View.GONE);
            } else {
                noPaintings.setVisibility(View.VISIBLE);
            }
        }
    }

    public void startWork() {
        try {

            PaintItem item_1 = new PaintItem(null, "manuals.jpg", System.currentTimeMillis(), "", drawing_type.None, true);
            mThumbnailItems.add(0, item_1);
            setTitle(getString(R.string.my_painting_count, mThumbnailItems.size() - 1)); // -1 size for manually guide

            ConstraintLayout noPaintings = findViewById(R.id.noPaintingsLayout);
            Button drawButton = findViewById(R.id.buttonDraw);
            drawButton.setOnClickListener(v -> {

                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyPaintingsActivity.this, constants.draw_screen_blank_canvas, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.draw_screen_blank_canvas);
                Intent lIntent1 = new Intent(MyPaintingsActivity.this, CategoryActivity.class);
                startActivity(lIntent1);
                /*
                int mPrefBackgroundColor = -1;
                Intent lIntent1 = new Intent(MyPaintingsActivity.this, PaintActivity.class);
                String str = "New Paint";
                lIntent1.setAction(str);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyPaintingsActivity.this, constants.draw_screen_blank_canvas, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.draw_screen_blank_canvas);
                constants.putInt("background_color", mPrefBackgroundColor, MyPaintingsActivity.this);
                lIntent1.putExtra("background_color", mPrefBackgroundColor);
                startActivity(lIntent1);*/
            });

            if (mThumbnailItems.size() == 1) {
                if (isFromGallery) {
                    int mPrefBackgroundColor = -1;
                    ContextKt.setSharedNo(this);
                    Intent lIntent1 = new Intent(this, PaintActivity.class);
                    lIntent1.setAction("New Paint");
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MyPaintingsActivity.this, constants.draw_screen_blank_canvas, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.draw_screen_blank_canvas);
                    constants.putInt("background_color", mPrefBackgroundColor, MyPaintingsActivity.this);
                    lIntent1.putExtra("background_color", mPrefBackgroundColor);
                    startActivity(lIntent1);
                }
                mAdapter.setItemList(mThumbnailItems, true);
                noPaintings.setVisibility(View.VISIBLE);
            } else {
                constants.putString(constants.DisplayedMyPainting, "true", this);
                mAdapter.setItemList(mThumbnailItems, false);
                if (mThumbnailItems.size() > 1) {
                    noPaintings.setVisibility(View.GONE);
                } else {
                    noPaintings.setVisibility(View.VISIBLE);
                }
            }

            for (int i = 0; i < mThumbnailItems.size(); i++) {
                mThumbnailItems.get(i).setSelected(false);
            }
            if (mThumbnailItems != null && mThumbnailItems.size() > 0)
                mThumbnailItems.get(0).setSelected(true);
            mAdapter.notifyDataSetChanged();
            Resources lResources = getResources();
            setBackgrounds(lResources);
            getVideoGuideDataFromAPI();
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
        setupOnline();
    }


    @Override
    public void onClick(View v) {
        Log.e("TAGGG", "OnClick Called");
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    ArrayList<PaintItem> mItems = new ArrayList<>();


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void fetchConfig(Intent intent, String filePath) {
        try {
            String aBuffer = "";
            String name = AppUtils.getFileNameWithoutExtension(new File(filePath));
            File myFile = new File(KGlobal.getMyPaintingFolderPath(this) + "/" + name + ".txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow;
            }
            myReader.close();
            intent.putExtra("mConfigData", aBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void processMovie(String fileName) {
        try {
            //First check in painting folder.
            File _file = new File(KGlobal.getDownloadedFolderPath(this) + "/" + fileName);
            if (_file.exists()) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());
                if (appSharedPrefs.getString(fileName, null) != null) {
                    String fileNameWithOutExt = fileName.replaceFirst("[.][^.]+$", "");
                    Intent intent = new Intent(MyPaintingsActivity.this, PaintActivity.class);
                    intent.putExtra("TutorialPath", _file.getAbsolutePath());
                    intent.putExtra("EventFilePath", KGlobal.getDownloadedFolderPath(this) + "/EventData_" + fileNameWithOutExt + ".txt");
                    intent.putExtra("StrokeFilePath", KGlobal.getDownloadedFolderPath(this) + "/StrokeData_" + fileNameWithOutExt + ".txt");
                    intent.setAction("FromTutorialMode");
                    intent.putExtra("OverlaidImagePath", appSharedPrefs.getString(fileName, null));
                    FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                    }
                    fetchConfig(intent, KGlobal.getDownloadedFolderPath(this) + "/" + fileName);
                    startActivity(intent);
                    finish();
                } else {
                    String fileNameWithOutExt = fileName.replaceFirst("[.][^.]+$", "");
                    Intent intent = new Intent(MyPaintingsActivity.this, PaintActivity.class);
                    intent.putExtra("TutorialPath", _file.getAbsolutePath());
                    intent.putExtra("EventFilePath", KGlobal.getDownloadedFolderPath(this) + "/EventData_" + fileNameWithOutExt + ".txt");
                    intent.putExtra("StrokeFilePath", KGlobal.getDownloadedFolderPath(this) + "/StrokeData_" + fileNameWithOutExt + ".txt");
                    intent.setAction("FromTutorialMode");
                    FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                    }
                    fetchConfig(intent, KGlobal.getDownloadedFolderPath(this) + "/" + fileName);
                    startActivity(intent);
                    finish();
                }
                return;
            }
        } catch (Exception e) {

        }
    }

    class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private String TAG = "GalleryPickerAdapter";
        Context mContext;
        Boolean isFromDefaultList = false;
        private MenuItemClickListener mMenuItemClickListener;
        private int guideCount;

        public ImageAdapter(Context arg2, MenuItemClickListener listener, boolean isFromGallery) {
            mContext = (Context) arg2;
            mMenuItemClickListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView lImageView = null;
            ImageView lImageView2 = null;
            ImageView iv_movie_icon;
            TextView tv_trace_ref;
            TextView tv_trace_reff;
            TextView tv_file_name;
            ImageView more;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lImageView2 = itemView.findViewById(R.id.iv_trace);
                lImageView = itemView
                        .findViewById(R.id.iv_thumb);
                tv_trace_ref = itemView.findViewById(R.id.tv_trace_ref);
                tv_trace_reff = itemView.findViewById(R.id.tv_trace_reff);
                tv_file_name = itemView.findViewById(R.id.tv_file_name);
                iv_movie_icon = itemView.findViewById(R.id.iv_movie_icon);
                more = itemView.findViewById(R.id.iv_more);
            }
        }

        public class TitleHolder extends RecyclerView.ViewHolder {

            TextView tv_title;

            public TitleHolder(@NonNull View itemView) {
                super(itemView);

                tv_title = itemView.findViewById(R.id.tv_title);
            }
        }


        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mItems.get(position).getFileName().equalsIgnoreCase("manuals.jpg")) {
                return 0;
            } else return 1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1)
                return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.grid_layout_items, parent, false));
            else
                return new TitleHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_my_video_guides, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pInt) {

            if (viewHolder instanceof TitleHolder) {
                TitleHolder holder = (TitleHolder) viewHolder;
                holder.tv_title.setText(getResources().getString(R.string.quick_video_guide_countt));
                holder.itemView.setOnClickListener(v -> mMenuItemClickListener.onEditClick(mItems.get(pInt), pInt));
            } else {
                ViewHolder holder = (ViewHolder) viewHolder;

                File file = null;
                if (!isFromDefaultList) {
                    file = new File(parentFolderPath + "/" + mItems.get(pInt).getFileThumbName());
                } else {
                    String url = constants.getString(constants.mypaintings_youtube_thumb, MyPaintingsActivity.this);
                    Glide.with(MyPaintingsActivity.this)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.thumbnaildefault).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(holder.lImageView);
                }
                if (mItems.get(pInt).get_drawing_type().equals(drawing_type.Trace)) {
                    holder.tv_trace_ref.setText("T");
//                tv_trace_reff.setText("T");
                    holder.tv_trace_ref.setVisibility(View.VISIBLE);
//                tv_trace_ref.setBackgroundColor(getResources().getColor(R.color.background_color));
                    ViewCompat.setBackgroundTintList(holder.tv_trace_ref,
                            ColorStateList.valueOf(getResources().getColor(R.color.background_color)));
                    holder.iv_movie_icon.setVisibility(View.GONE);
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.TraceDrawaing)) {
                    holder.iv_movie_icon.setVisibility(View.GONE);
                    holder.tv_trace_ref.setVisibility(View.VISIBLE);
                    holder.tv_trace_ref.setText("D");
//                tv_trace_reff.setText("D");
//                tv_trace_ref.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(holder.tv_trace_ref,
                            ColorStateList.valueOf(getResources().getColor(R.color.background_color)));
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.Overlay)) {
                    holder.tv_trace_ref.setText("O");
//                tv_trace_reff.setText("O");
                    holder.tv_trace_ref.setVisibility(View.VISIBLE);
//                tv_trace_ref.setBackgroundColor(getResources().getColor(R.color.background_color));
                    ViewCompat.setBackgroundTintList(holder.tv_trace_ref,
                            ColorStateList.valueOf(getResources().getColor(R.color.background_color)));
                    holder.iv_movie_icon.setVisibility(View.GONE);
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.OverlayDrawing)) {
                    holder.iv_movie_icon.setVisibility(View.GONE);
                    holder.tv_trace_ref.setVisibility(View.VISIBLE);
                    holder.tv_trace_ref.setText("D");
//                tv_trace_reff.setText("D");
//                tv_trace_ref.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(holder.tv_trace_ref,
                            ColorStateList.valueOf(getResources().getColor(R.color.background_color)));
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.ImportImage)) {
                    holder.iv_movie_icon.setVisibility(View.GONE);
                    holder.tv_trace_ref.setVisibility(View.VISIBLE);
                    holder.tv_trace_ref.setText("I");
//                tv_trace_reff.setText("I");
//                tv_trace_ref.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(holder.tv_trace_ref,
                            ColorStateList.valueOf(getResources().getColor(R.color.background_color)));
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.Movie)) {
//                tv_trace_reff.setText("Movie");

                    holder.tv_trace_ref.setVisibility(View.GONE);
                    holder.iv_movie_icon.setVisibility(View.VISIBLE);
                } else if (mItems.get(pInt).get_drawing_type().equals(drawing_type.TraceCanvasDrawing)) {
//                tv_trace_reff.setText("TD");

                    holder.tv_trace_ref.setVisibility(View.GONE);
                    holder.iv_movie_icon.setVisibility(View.VISIBLE);
                } else {

//                tv_trace_reff.setText(mItems.get(pInt).get_drawing_type().toString());

                    holder.iv_movie_icon.setVisibility(View.GONE);
                    holder.tv_trace_ref.setVisibility(View.GONE);
                }

                holder.tv_trace_ref.setVisibility(View.GONE);

                if(new File(parentFolderPath + "/" + mItems.get(pInt).getFileThumbName().replace(".png", "_Drawing.png")).exists())
                {
                    try {
                        Picasso.get().load(new File(parentFolderPath + "/" + mItems.get(pInt).getFileThumbName().replace(".png", "_Trace.png")))
                                .into(holder.lImageView2);

                        Glide.with(MyPaintingsActivity.this)
                                .load(replaceColor(BitmapFactory.decodeFile(parentFolderPath + "/" + mItems.get(pInt).getFileThumbName().replace(".png", "_Drawing.png")))).into(holder.lImageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    if (file != null && file.exists()) {
                        try {

                            final String uri = Uri.fromFile(file).toString();
                            final String decoded = Uri.decode(uri);
                            Glide.with(MyPaintingsActivity.this)
                                    .load(uri)
                                    .apply(new RequestOptions().placeholder(R.drawable.thumbnaildefault).error(R.drawable.thumbnaildefault).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                    .into(holder.lImageView);

                        } catch (Exception e) {
                            Log.e("TAG", "Exception at load image " + e.getMessage());
                        }
                    }

                    if (mItems.get(pInt).getSelected()) {
                        holder.lImageView.setBackground(getResources().getDrawable(R.drawable.grid_small));
                    } else {
                        holder.lImageView.setBackgroundColor(getResources().getColor(R.color.gray_holo_light));
                    }
                }


                holder.tv_file_name.setText(mItems.get(pInt).getFileName());
                holder.iv_movie_icon.setImageResource(R.drawable.video_label_icon_outline);



                holder.itemView.setOnClickListener(view -> {
                    // is from gallery than go to post activity else show painting
                    if (isFromGallery) {
                        mMenuItemClickListener.onPostClick(mItems.get(pInt), pInt);
                    } else {

                        drawing_type drawingType = mItems.get(pInt).get_drawing_type();
                        if (drawingType.equals(drawing_type.Trace)) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_draw_trace, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_draw_trace);
                        } else if (drawingType.equals(drawing_type.Overlay)) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_draw_overlay, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_draw_overlay);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_draw_normal, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_draw_normal);
                        }

                        mMenuItemClickListener.onEditClick(mItems.get(pInt), pInt);
                    }


                });

                holder.more.setOnClickListener(view -> mMenuItemClickListener.onSubMenuClick(view, mItems.get(pInt), pInt));
                holder.tv_file_name.setOnClickListener(view -> mMenuItemClickListener.onSubMenuClick(holder.more, mItems.get(pInt), pInt));

            }
        }

        public Bitmap replaceColor(Bitmap src) {
            if (src == null)
                return null;
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixels = new int[width * height];
            src.getPixels(pixels, 0, 1 * width, 0, 0, width, height);
            for (int x = 0; x < pixels.length; ++x) {
                //    pixels[x] = ~(pixels[x] << 8 & 0xFF000000) & Color.BLACK;
                if(pixels[x] == Color.WHITE) pixels[x] = 0;
            }
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }

        public void setItemList(ArrayList<PaintItem> pArrayList, Boolean fromDefault) {

            ArrayList<PaintItem> tempList = new ArrayList<>();
            tempList.addAll(pArrayList);
            this.isFromDefaultList = fromDefault;
            for (int i = 0; i < tempList.size(); i++) {
                String extension = tempList.get(i).getFileName().substring(tempList.get(i).getFileName().toLowerCase().lastIndexOf("."));
                if (!(extension.toLowerCase().equalsIgnoreCase(".png") || extension.toLowerCase().equalsIgnoreCase(".jpg") || extension.toLowerCase().equalsIgnoreCase(".mp4"))) {
                    pArrayList.remove(tempList.get(i));
                }
            }
            mItems = pArrayList;
        }

        public void notifyRemove(int pos) {
            mItems.remove(mItems.get(pos));
            notifyDataSetChanged();
        }

        public void setGuideCount(int size) {
            guideCount = size;
            notifyDataSetChanged();
        }
    }

    private void confirmDialog(final String fileName, int position) {


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_save);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        androidx.appcompat.widget.AppCompatEditText input = dialog.findViewById(R.id.edtFileName);
        TextView header = dialog.findViewById(R.id.tvMessage);
        View FakeLine = dialog.findViewById(R.id.FakeLine);
        ImageView cross = dialog.findViewById(R.id.imgCross);


        FakeLine.setVisibility(View.INVISIBLE);

        input.setText(getString(R.string.are_you_sure));

        header.setText("Delete");

        input.setEnabled(false);
        input.setFocusable(false);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = null;
                    if (mItems.get(position).get_drawing_type().equals(drawing_type.Movie)) {
                        file = new File(KGlobal.getDownloadedFolderPath(MyPaintingsActivity.this) + "/" + mItems.get(position).getFileName());


                        String fileNameWithOutExt = mItems.get(position).getFileName().replaceFirst("[.][^.]+$", "");

                        fileNameWithOutExt = fileNameWithOutExt + ".png";
                        //delete thumb
                        File file_thumb = new File(parentFolderPath + "/" + fileNameWithOutExt);
                        if (file_thumb.exists()) {
                            if (file_thumb.delete()) {
                                ExecutorService myExecutor = Executors.newSingleThreadExecutor();
                                Handler myHandler = new Handler(Looper.getMainLooper());

                                myExecutor.execute(() -> {
                                    // Do something in background (back-end process)
                                    AppDatabase db = MyApplication.getDb();
                                    SavedTutorialDao savedTutorialDao = db.savedTutorialDao();
                                    SavedDrawingDao savedDrawingDao = db.savedDrawingDao();
                                    savedTutorialDao.deleteByPath(file_thumb.getPath());
                                    savedDrawingDao.deleteByPath(file_thumb.getPath());
                                    if (entityHashMap.containsKey(mItems.get(position).getFileName())) {
                                        entityHashMap.remove(mItems.get(position).getFileName());
                                    }
                                    // Create an interface to respond with the result after processing
                                    myHandler.post(() -> {
                                        // Update the UI here
                                        // Do something in UI (front-end process)

                                        if (mItems.size() > position) {
                                            mItems.remove(position);

                                            mAdapter.notifyRemove(position);
                                            checkData();
                                        }

                                        myExecutor.shutdown();
                                    });
                                });
                            }
                        }
                    } else {
                        file = new File(parentFolderPath + "/" + mItems.get(position).getFileName());
                    }
                    Log.e("TAG", "File Name at Delete " + file.getAbsolutePath());
                    if (file != null && file.exists()) {
                        boolean isDelete = file.delete();
                        if (isDelete) {

                            try {
                                new File(parentFolderPath + "/" + mItems.get(position).getFileName().replace(".png", "_Drawing.png")).delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                new File(parentFolderPath + "/" + mItems.get(position).getFileName().replace(".png", "_Trace.png")).delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                new File(parentFolderPath + "/" + mItems.get(position).getFileName().replace(".png", ".txt")).delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.DELETE_PAINTING_IMAGE_SUCCESS);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.DELETE_PAINTING_IMAGE_SUCCESS, Toast.LENGTH_SHORT).show();
                            }
                            mAdapter.notifyRemove(position);
                            checkData();
                            setTitle(getString(R.string.my_painting_count, mAdapter.getItemCount() - 1));
                        }
                    } else {
                        Toast.makeText(MyPaintingsActivity.this, "File Not Exist!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at delet file " + e.getMessage());
                }

                dialog.dismiss();
            }
        });

        dialog.show();


//        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
//        lBuilder1.setTitle("Delete").setMessage(getResources().getString(R.string.are_you_sure)).setCancelable(true);
//        lBuilder1.setPositiveButton("Yes", (dialog, which) -> {
//
//        });
//        lBuilder1.setNegativeButton("Cancel", (dialog, which) -> {
//            dialog.dismiss();
//        });
//        lBuilder1.create().show();
    }

    private void startDoodle(int pInt, boolean pBoolean) {
        Intent lIntent1 = new Intent();
        Intent lIntent2 = lIntent1.setClass(this, PaintActivity.class);
        String str1 = "Edit Paint";
        Intent lIntent3 = lIntent1.setAction(str1);
        String str2 = ((PaintItem) mItems.get(pInt)).getFileName();
        lIntent1.putExtra("paint_name", str2);

        if (str2.contains("egypt_trace") || str2.contains("collie_trace"))
            lIntent1.putExtra("SetBlackColor", true);
        startActivity(lIntent1);
        finish();
//        prevSelectedpos = -1;
//        finish();
    }

    public ArrayList<TraceReference> listFromPreference() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");
        String overlayJson = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");
        String importJson = appSharedPrefs.getString(constants.getImportImageList_Gson_Key(), "");
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();
        ArrayList<TraceReference> traceList = gson.fromJson(json, type);
        if (traceList == null) {
            traceList = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(overlayJson)) {
            traceList.addAll(gson.fromJson(overlayJson, type));
        }

        if (!TextUtils.isEmpty(importJson)) {
            traceList.addAll(gson.fromJson(importJson, type));
        }
        return traceList;
    }

    private void showAlertDialog(String drawingName, String traceImagePath) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setMessage("You already have a drawing with this traced image, do you want to continue ?").setCancelable(true);
        lBuilder1.setPositiveButton("Yes Resume it.", (dialog, which) -> {
            // TODO Auto-generated method stub
            if (BuildConfig.DEBUG) {
                Toast.makeText(MyPaintingsActivity.this, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.Pick_Image_My_Paintings);
            Intent intent = new Intent(MyPaintingsActivity.this, PaintActivity.class);
            intent.setAction("Reload Painting");
            intent.putExtra("isTutorialmode", true);
            intent.putExtra("path", traceImagePath);
            intent.putExtra("drawingPath", drawingName);
            intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this));
            startActivity(intent);
            finish();
        });
        lBuilder1.setNegativeButton("Start As Fresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyPaintingsActivity.this, constants.CLICK_COLLECTION + " " + drawingName, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.CLICK_COLLECTION + " " + drawingName);
                startDoodle(getSelectedPos(), true);
            }
        });
        lBuilder1.create().show();
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
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
                                startActivity(new Intent(MyPaintingsActivity.this, GalleryDashboard.class));
                                finish();
                            }
                    )
                    .show();
        } else {
            if (isMultiSelectEnable) {
                for (int i = 0; i < mItems.size(); i++) {
                    mItems.get(i).setSelected(false);
                }
                isMultiSelectEnable = false;
                mAdapter.notifyDataSetChanged();
                return;
            }
            if (iv_default_img.getVisibility() == View.VISIBLE) {
                iv_default_img.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        }
    }

    public void openExternalBrowser(String _url) {
        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
        startActivity(browserIntent);*/
        KGlobal.openInBrowser(MyPaintingsActivity.this, _url);
    }

    void showHintDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyPaintingsActivity.this);
        // set title
        alertDialogBuilder.setTitle(R.string.dialog_title_your_saved_drawings);
        String msg = "";
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            msg = getResources().getString(R.string.selection_msg_portrait);
        } else {
            msg = getResources().getString(R.string.selection_msg);
        }
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String detectFileFormatFromUri(String fileUri) {
        try {
            URI uri = new URI(fileUri);
            String filePath = uri.getPath();
            return detectFileFormat(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return empty string if unable to parse URI or detect file format
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String detectFileFormat(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase(); // Return the file extension in lowercase
        }

        return ""; // Return empty string if no file extension found
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void doSocialShare(Uri photoURI) {

        String formate = detectFileFormatFromUri(String.valueOf(photoURI));

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            String text = getResources().getString(R.string.default_msg_while_share);
            text = text + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

            ArrayList<Uri> files = new ArrayList<Uri>();
            files.add(photoURI);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.setType(formate + "/*");

            Intent receiver = new Intent(this, BroadcastTest.class);
//        receiver.putExtra("test", "test");
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0,
                        receiver,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0,
                        receiver,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            Intent chooser;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                chooser = Intent.createChooser(shareIntent, "Share To", pendingIntent.getIntentSender());
            } else {
                chooser = Intent.createChooser(shareIntent, "Share To");
            }

            startActivity(chooser);
        } catch (Exception e) {
            Log.e("TAGRR", e.getMessage());
        }
    }

    public static class BroadcastTest extends BroadcastReceiver {

        public BroadcastTest() {
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
                        Toast.makeText(context, "share_image_via_" + shareFileVia, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(context, "share_image_via_" + shareFileVia);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception while share image " + e.getMessage(), e);
            }
        }
    }

    public int getSelectedPos() {

        try {
            if (mItems == null)
                return -1;
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getSelected()) {
                    return i;
                }
            }
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                account = task.getResult(ApiException.class);

                Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
                constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), MyPaintingsActivity.this);
                constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), MyPaintingsActivity.this);
                constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), MyPaintingsActivity.this);
                LoginRequestModel model = new LoginRequestModel((account.getId() != null ? account.getId() : ""), (account.getDisplayName() != null ? account.getDisplayName() : ""), (account.getEmail() != null ? account.getEmail() : ""), "");
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyPaintingsActivity.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE);
            } catch (ApiException e) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyPaintingsActivity.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginFailed);
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
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
                                Toast.makeText(MyPaintingsActivity.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.FacebookLoginSuccess);
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
                                isLoggedIn = true;
                                constants.putString(constants.Username, fnm, MyPaintingsActivity.this);
                                constants.putString(constants.Password, fid, MyPaintingsActivity.this);
                                constants.putString(constants.Email, mail, MyPaintingsActivity.this);
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
            if (BuildConfig.DEBUG) {
                Toast.makeText(MyPaintingsActivity.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.FacebookLoginFailed);
            Log.e("TAGGG", "Facebook Event OnError Called " + error.getMessage(), error);
        }
    };

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    public void addUser(LoginRequestModel model, int loginType) {

        HashMap<String, RequestBody> _map = new HashMap<>();
        String username = "";

        if ((model != null && model.user_name != null)) {
            username = model.user_name.replace(" ", "_");
        }
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_id != null) ? model.user_id : "");
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), (username != null) ? username : "");
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), (model != null && model.user_email != null) ? model.user_email : "");

        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, MyPaintingsActivity.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, MyPaintingsActivity.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, MyPaintingsActivity.this));

        try {
            String _ip = constants.getString(constants.IpAddress, MyPaintingsActivity.this);
            String _country = constants.getString(constants.UserCountry, MyPaintingsActivity.this);
            String _city = constants.getString(constants.UserCity, MyPaintingsActivity.this);
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

        Call<LoginResponseModel> call = apiInterface.addUserData(ApiClient.SECRET_KEY, _map);
        showProgress(false);
        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {

                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", MyPaintingsActivity.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", MyPaintingsActivity.this);

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", MyPaintingsActivity.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), MyPaintingsActivity.this);
                            Log.e("TAGGG", "Salt Value is " + response.body().getObjData().getSalt());

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                constants.putString(constants.Username, model.user_name, MyPaintingsActivity.this);
                                constants.putString(constants.Password, model.user_id, MyPaintingsActivity.this);
                                constants.putString(constants.Email, model.user_email, MyPaintingsActivity.this);
                                constants.putString(constants.LoginInPaintology, "true", MyPaintingsActivity.this);
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, MyPaintingsActivity.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.PaintologyLoginSuccess);
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(MyPaintingsActivity.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.PaintologyRegistration);
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(MyPaintingsActivity.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.FacebookRegister);
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(MyPaintingsActivity.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.GoogleRegistration);
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(MyPaintingsActivity.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            String _user_id = constants.getString(constants.UserId, MyPaintingsActivity.this);
                            MyApplication.get_realTimeDbUtils(MyPaintingsActivity.this).autoLoginRegister(response.body().getObjData().getStatus());
                            try {
                                if (KGlobal.isInternetAvailable(MyPaintingsActivity.this) && _user_id != null && !_user_id.isEmpty()) {
                                    startService(new Intent(MyPaintingsActivity.this, SendDeviceToken.class));
                                }
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        if (loginType == LOGIN_FROM_FB)
                            LoginManager.getInstance().logOut();
                        else if (loginType == LOGIN_FROM_GOOGLE) {
                            Auth.GoogleSignInApi.signOut(googleApiClient);
                        } else {
                            LoginInPaintology = "false";
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.PaintologyLoginFailed);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.event_failed_to_adduser);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MyPaintingsActivity.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
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
                            LoginInPaintology = "false";
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.PaintologyLoginFailed);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.event_failed_to_adduser);
                        Toast.makeText(MyPaintingsActivity.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "add user in Exception " + e.getMessage(), e);
            hideProgress();
        }
    }


    void showProgress(Boolean isFromUpload) {
        try {
            progressDialog = new ProgressDialog(MyPaintingsActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            if (isFromUpload) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.setMessage("Uploading File");
            }
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    void hideProgress() {
        try {
            if (progressDialog != null && progressDialog.isShowing() && !MyPaintingsActivity.this.isDestroyed()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }


    /*This method will prompt social media login dialog when user click on upload zip file.*/
//    public void showLoginDialog() {
//        final Dialog dialog = new Dialog(MyPaintingsActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.social_media_login_layout);
//
//        if (BuildConfig.DEBUG){
//            Toast.makeText(MyPaintingsActivity.this, constants.open_social_login_mypainting_dialog, Toast.LENGTH_SHORT).show();
//        }
//        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.open_social_login_mypainting_dialog);
//
//        TextView tv_community_link = (TextView) dialog.findViewById(R.id.tv_community_link);
//        tv_community_link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(MyPaintingsActivity.this, constants.open_social_login_mypainting_dialog_link_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.open_social_login_mypainting_dialog_link_click);
//                startActivity(new Intent(MyPaintingsActivity.this, Community.class));
//                dialog.dismiss();
//            }
//        });
//
//        Button btn_fb = (Button) dialog.findViewById(R.id.fb);
//        btn_fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Perfome Action
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(MyPaintingsActivity.this, constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.FACEBOOK_LOGIN);
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
//
//        Button btn_paintology = (Button) dialog.findViewById(R.id.btn_paintology);
//        btn_paintology.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(MyPaintingsActivity.this, constants.Social_Paintology_Login, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.Social_Paintology_Login);
//                showDialog();
//            }
//        });
//        dialog.show();
//    }

//    public void showDialog() {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        final Dialog dialog = new Dialog(MyPaintingsActivity.this);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        dialog.setContentView(R.layout.login_to_paintology_layout);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        EditText edt_uname = dialog.findViewById(R.id.edt_uname);
//        EditText edt_email = dialog.findViewById(R.id.edt_email);
//        EditText edt_pass = dialog.findViewById(R.id.edt_pass);
//        Button btn_proceed = dialog.findViewById(R.id.btn_proceed);
//        ImageView iv_close_dialog = dialog.findViewById(R.id.iv_close_dialog);
//
//        AppCompatCheckBox cb_viewpass = (AppCompatCheckBox) dialog.findViewById(R.id.cb_viewpass);
//
//        cb_viewpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked) {
//
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
//                if (edt_email.getText().toString().isEmpty()) {
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
//                } else if (edt_uname.getText().toString().isEmpty()) {
//                    edt_uname.setError(getResources().getString(R.string.required));
//                    edt_uname.requestFocus();
//                } else if (edt_uname.getText().toString().trim().contains(" ")) {
//                    edt_uname.setError("Blank space not allowed");
//                    edt_uname.requestFocus();
//                } else {
////                    LoginRequestModel model = new LoginRequestModel((account.getDisplayName() != null ? account.getDisplayName() : ""), (account.getId() != null ? account.getId() : ""), (account.getEmail() != null ? account.getEmail() : ""));
//                    LoginRequestModel model = new LoginRequestModel(edt_pass.getText().toString().trim(), edt_uname.getText().toString().trim(), edt_email.getText().toString().trim(), edt_pass.getText().toString().trim());
//                    Log.e("TAGG", "Login Data " + model.user_email + " " + model.user_id + " " + model.user_name);
//                    if (KGlobal.isInternetAvailable(MyPaintingsActivity.this)) {
//                        addUser(model, LOGIN_FROM_PAINTOLOGY);
//                    } else
//                        Toast.makeText(MyPaintingsActivity.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            }
//        });
//        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
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

    /*User can do their google sign in using this method*/
    private void signIn() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(MyPaintingsActivity.this, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.GOOGLE_LOGIN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(MyPaintingsActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, MyPaintingsActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, MyPaintingsActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called Thumbnail 1558");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }


    protected void setupOnline() {
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(this);
            LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
            try {
                if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
                    String _user_id = constants.getString(constants.UserId, MyPaintingsActivity.this);
                    if (_user_id != null && !_user_id.isEmpty()) {
                        Log.e("TAG", "setOnline called Thumbnail 1578");
                        MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                    }
                }
            } catch (Exception e) {

            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at onResume " + e.getMessage(), e);
        }
        System.gc();
    }

    public boolean isMultiSelected() {
        int selected = 0;
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getSelected()) {
                selected++;
            }
        }
        if (selected == 1)
            return false;
        else
            return true;
    }

    @Override
    public void onEditClick(@Nullable PaintItem item, int position) {
        edit(item, position, false);
    }

    @Override
    public void onDeleteClick(@Nullable PaintItem item, int position) {
        delete(item, position);
    }

    @Override
    public void onShareClick(@Nullable PaintItem item, int position) {
        share(item, position);
    }

    @Override
    public void onPostClick(@Nullable PaintItem item, int position) {
        post(item, position, "post_gallery");
    }

    private void edit(PaintItem item, int position, boolean openInTrace) {
        if (mItems == null && mItems.size() > 0) {
            return;
        }

        String str2 = item.getFileName();
        if (item.isDefaultImageLoaded()) {

            if (BuildConfig.DEBUG) {
                Toast.makeText(this, constants.mypaintings_open_video_guides, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.mypaintings_open_video_guides);

            Intent intent = new Intent(MyPaintingsActivity.this, IntroVideoListActivity.class);
            startActivity(intent);

//            try {
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(this, constants.mypaintings_open_video, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(this, constants.mypaintings_open_video);
//                StringConstants.IsFromDetailPage = true;
//                Intent intent = new Intent(MyPaintingsActivity.this, Play_YotubeVideo.class);
//                String url = constants.getString(constants.mypaintings_youtube_url, MyPaintingsActivity.this);
//                if (url != null && !url.isEmpty())
//                    intent.putExtra("url", url);
//                else
//                    intent.putExtra("url", "https://youtu.be/vPl5mUgDQOY");
//
//                intent.putExtra("isVideo", true);
//                intent.putExtra("hideToggle", "hideToggle");
//                startActivity(intent);
//            } catch (Exception e) {
//                Log.e("TAGGG", "exception at set image " + e.getMessage());
//            }
            return;
        }

        boolean ismultiSelected = isMultiSelected();
        if (ismultiSelected) {
            Toast.makeText(this, getResources().getString(R.string.one_item_allowd), Toast.LENGTH_SHORT).show();
            return;
        }
        if (item.get_drawing_type().equals(drawing_type.Movie)) {
            processMovie(str2);
        } else {


            ArrayList<TraceReference> traceList = listFromPreference();
            if (traceList != null) {
                for (int i = 0; i < traceList.size(); i++) {
                    drawing_type type = traceList.get(i).get_drawing_type();
                    File _file_trace = new File(traceList.get(i).getTraceImageName());
                    if (str2.equalsIgnoreCase(traceList.get(i).getUserPaintingName())) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(this, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(this, constants.Pick_Image_My_Paintings);


                        if (type.equals(drawing_type.TraceDrawaing)) {
                            Intent intent = new Intent(this, PaintActivity.class);
                            intent.setAction("Reload Painting");
                            if (type.equals(drawing_type.TraceDrawaing))
                                intent.putExtra("isTutorialmode", true);
                            else
                                intent.putExtra("isTutorialmode", false);
                            File file;
                            if (traceList.get(i).isFromPaintologyFolder()) {
//                                file = new File(Environment.getExternalStorageDirectory(), "/Paintology/" + traceList.get(i).getTraceImageName());
                                file = new File(KGlobal.getDefaultFolderPath(this) + "/" + traceList.get(i).getTraceImageName());
                            } else
                                file = new File(traceList.get(i).getTraceImageName());

                            intent.putExtra("path", file.getAbsolutePath());
                            try {
                                if (entityHashMap.containsKey(str2)) {
                                    intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            intent.putExtra("drawingPath", str2);
                            intent.putExtra("isImport", type.toString());
                            intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                            intent.putExtra("isGrayScale", traceList.get(i).isGrayScale());
                            fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                            startActivity(intent);
                            finish();
                        } else if (type.equals(drawing_type.OverlayDrawing)) {

                            String path = KGlobal.getMyPaintingFolderPath(this) + "/" + str2;

                            Intent intent = new Intent(this, PaintActivity.class);

                            intent.setAction("LoadWithoutTrace");
                            intent.putExtra("path", str2);
                            intent.putExtra("isImport", type.toString());
                            intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));

                            try {
                                if (entityHashMap.containsKey(str2)) {
                                    intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            intent.setAction("Edit Paint");
//                            intent.putExtra("FromLocal", true);
//                            intent.putExtra("paint_name", path);
//                            intent.putExtra("isOverraid", true);
                            fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                            startActivity(intent);
                            finish();
                        } else if (type.equals(drawing_type.ImportImage)) {
                            if (openInTrace) {
                                String path = KGlobal.getMyPaintingFolderPath(this) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(this, PaintActivity.class);
//        lIntent1.setAction(Paintor.EDIT_PAINT);
                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("isImport", type.toString());
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                lIntent1.putExtra("isOverraid", false);
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        lIntent1.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                fetchConfig(lIntent1, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(lIntent1);
                                finish();
                            } else {
                                Intent intent = new Intent(this, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("isImport", type.toString());
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                                fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(intent);
                                finish();
                            }
                        }
                        return;
                    } else if (str2.equalsIgnoreCase(_file_trace.getName())) {

                        if (type.equals(drawing_type.TraceDrawaing)) {
                            try {
                                String path = KGlobal.getMyPaintingFolderPath(this) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(this, PaintActivity.class);
//        lIntent1.setAction(Paintor.EDIT_PAINT);

                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("isImport", type.toString());
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                lIntent1.putExtra("isOverraid", false);
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        lIntent1.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("TAGGG", "startDoodle paint_name " + path);
                                fetchConfig(lIntent1, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(lIntent1);
                                finish();

                                return;
                            } catch (Exception e) {
                                Log.e("MyPaintingsActivity", e.getMessage());
                            }
                            return;
                        } else if (type.equals(drawing_type.OverlayDrawing)) {
                            try {
                                Intent intent = new Intent(this, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("isImport", type.toString());
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                                fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(intent);
                                finish();
                                return;
                            } catch (Exception e) {
                                Log.e("MyPaintingsActivity", e.getMessage());
                            }
                            return;
                        } else if (type.equals(drawing_type.ImportImage)) {
                            if (openInTrace) {
                                String path = KGlobal.getMyPaintingFolderPath(this) + "/" + str2;
                                Intent lIntent1 = new Intent();
                                lIntent1.setClass(this, PaintActivity.class);
//        lIntent1.setAction(Paintor.EDIT_PAINT);

                                lIntent1.setAction("Edit Paint");
                                lIntent1.putExtra("isImport", type.toString());
                                lIntent1.putExtra("FromLocal", true);
                                lIntent1.putExtra("paint_name", path);
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        lIntent1.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                lIntent1.putExtra("isOverraid", false);
                                fetchConfig(lIntent1, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(lIntent1);
                                finish();
                            } else {
                                Intent intent = new Intent(this, PaintActivity.class);
                                intent.setAction("LoadWithoutTrace");
                                intent.putExtra("path", str2);
                                intent.putExtra("isImport", type.toString());
                                try {
                                    if (entityHashMap.containsKey(str2)) {
                                        intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                                fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                                startActivity(intent);
                                finish();
                            }
                            return;
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.Pick_Image_My_Paintings);
                Intent intent = new Intent(this, PaintActivity.class);
                intent.setAction("LoadWithoutTrace");
                intent.putExtra("path", str2);
                intent.putExtra("isImport", "LoadWithoutTrace");
                try {
                    if (entityHashMap.containsKey(str2)) {
                        intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                startActivity(intent);
                finish();
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, constants.Pick_Image_My_Paintings, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.Pick_Image_My_Paintings);
                Intent intent = new Intent(this, PaintActivity.class);
                intent.setAction("LoadWithoutTrace");
                intent.putExtra("hey", "hey");
                try {
                    if (entityHashMap.containsKey(str2)) {
                        intent.putExtra("id", String.valueOf(entityHashMap.get(str2).postId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra("path", str2);
                intent.putExtra("isImport", "LoadWithoutTrace");
                intent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(this));
                fetchConfig(intent, KGlobal.getMyPaintingFolderPath(this) + "/" + str2);
                startActivity(intent);
                finish();
            }
        }
    }

    private void delete(PaintItem item, int position) {
        if (item.isDefaultImageLoaded()) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, constants.DELETE_PAINTING_IMAGE, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.DELETE_PAINTING_IMAGE);
        confirmDialog(item.getFileName(), position);
    }

    private void share(PaintItem item, int position) {
        if (item.isDefaultImageLoaded()) {
            return;
        }

        try {
            boolean ismultiSelected = isMultiSelected();
            if (ismultiSelected) {
                Toast.makeText(this, getResources().getString(R.string.one_item_allowd), Toast.LENGTH_SHORT).show();
                return;
            }

            isShareClicked = true;

            if (item.get_drawing_type().equals(drawing_type.Movie)) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, constants.SHARE_VIDEO, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.SHARE_VIDEO);
                Uri videoURI;
                File photoFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + item.getFileName());
                videoURI = FileProvider.getUriForFile(MyPaintingsActivity.this, getString(R.string.authority), photoFile);

                shareMovieRewardPoint();

                doSocialShare(videoURI);
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, constants.getSHARE_IMAGE(), Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.getSHARE_IMAGE());
                Uri photoURI;
                File photoFile = new File(parentFolderPath + "/" + item.getFileName());
                photoURI = FileProvider.getUriForFile(MyPaintingsActivity.this, getString(R.string.authority), photoFile);
                shareRewardPoint();

                doSocialShare(photoURI);
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
        }
    }

    private void post(PaintItem item, int position, String postGallery) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(this);
        LoginInPaintology = constants.getString(constants.LoginInPaintology, this);

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, constants.my_paintings_community_post_click, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_community_post_click);
//        if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
        ArrayList<String> mImageList = new ArrayList<>();

//            if (item.getSelected() && !item.get_drawing_type().equals(drawing_type.Movie)) {
        if (!item.get_drawing_type().equals(drawing_type.Movie)) {
            File photoFile = new File(parentFolderPath + "/" + item.getFileName());
            String filePath = photoFile.getAbsolutePath();
            mImageList.add(filePath);
        } else if (item.getSelected() && item.get_drawing_type().equals(drawing_type.Movie)) {
            Toast.makeText(this, ".mp4 file not allowed to post", Toast.LENGTH_SHORT).show();
        }

        AppDatabase db = MyApplication.getDb();
        PaintingDao paintingDao = db.paintingDao();
        PublishDao publishDao = db.publishDao();

        // Fetch a PaintingEntity by fileName
        new Thread(() -> {

            String fileName = item.getFileName();
            Log.e("TAG", "FileName: " + item.getFileName());

            PaintingEntity painting = paintingDao.getPaintingByFileName(fileName);
            if (painting != null) {

                PublishEntity entity = publishDao.getPaintingByFileName(fileName);
                if (entity == null) {
                    publishDao.insertPainting(new PublishEntity(painting.getId(), painting.getFileName(), painting.getType(), false, false));
                }

                PublishEntity entity1 = publishDao.getPaintingByFileName(fileName);
                // Print all data of the retrieved PaintingEntity
                Log.e("TAG", "ID: " + entity1.getId() +
                        ", File Name: " + entity1.getFileName() +
                        ", Type: " + entity1.getType() +
                        ", Is Uploaded Gallery: " + entity1.isUploadedGallery() +
                        ", Is Uploaded Community: " + entity1.isUploadedCommunity());

                // Check if the painting is already uploaded
                if (postGallery.equalsIgnoreCase("post_gallery") && entity1.isUploadedGallery()) {
                    runOnUiThread(() -> showPreventMultiplePostingDialog(painting.getType(), postGallery));
                } else if (!postGallery.equalsIgnoreCase("post_gallery") && entity1.isUploadedCommunity()) {
                    runOnUiThread(() -> showPreventMultiplePostingDialog(painting.getType(), postGallery));
                } else {
                    // Prepare intent for PostActivity

                    Intent returnIntent = new Intent(MyPaintingsActivity.this, PostActivity.class);
                    returnIntent.putStringArrayListExtra("result", mImageList);
                    returnIntent.putExtra("isPostGallery", postGallery);

                    // Determine drawingType based on painting.getType()
                    String drawingType = painting.getType().equals("freehand") ? "freehand" : "tutorials";
                    returnIntent.putExtra("isFromMyPainting", true);
                    returnIntent.putExtra("drawingType", drawingType);

                    try {
                        if (entityHashMap.containsKey(item.getFileName())) {
                            returnIntent.putExtra("drawingType", "tutorials");
                            returnIntent.putExtra("referenceId", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                            returnIntent.putExtra("youtube_video_id", entityHashMap.get(item.getFileName()).youtubeVideoId);
                            returnIntent.putExtra("path", painting.getFileName());
                            returnIntent.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    returnIntent.putExtra("fileName", painting.getFileName());
                    returnIntent.putExtra("isUploaded", painting.isUploaded());
                    // Start PostActivity
                    startActivity(returnIntent);
                }
            } else {
                // Handle case where no painting is found
                Log.e("TAG", "No painting found with fileName: " + fileName);
                runOnUiThread(() -> Toast.makeText(MyPaintingsActivity.this, "No painting found with fileName: " + fileName, Toast.LENGTH_SHORT).show());
            }

           /* if (painting != null) {
                // Printing all data of the retrieved PaintingEntity
                Log.e("TAG", "post: ID" + painting.getId() +
                        "File Name: " + painting.getFileName() +
                        "Type: " + painting.getType() +
                        "Is Uploaded: " + painting.isUploaded()
                );
                if (painting.isUploaded()) {
                    Toast.makeText(instance, "Drawing Already uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    if (painting.getType().equals("freehand")) {
                        Intent returnIntent = new Intent(this, PostActivity.class);
                        returnIntent.putStringArrayListExtra("result", mImageList);
                        returnIntent.putExtra("isPostGallery", postGallery);
                        returnIntent.putExtra("drawingType", "freehand");
                        returnIntent.putExtra("fileName", painting.getFileName());
                        returnIntent.putExtra("isUploaded", painting.isUploaded());
                        startActivity(returnIntent);
                    } else {
                        Intent returnIntent = new Intent(this, PostActivity.class);
                        returnIntent.putStringArrayListExtra("result", mImageList);
                        returnIntent.putExtra("isPostGallery", postGallery);
                        returnIntent.putExtra("drawingType", "tutorials");
                        returnIntent.putExtra("fileName", painting.getFileName());
                        returnIntent.putExtra("isUploaded", painting.isUploaded());
                        startActivity(returnIntent);
                    }
                }
            } else {
                Log.e("TAG", "No painting found with fileName: " + fileName);
            }*/
        }).start();

       /* if (item.getType().equals("freehand")) {
            Intent returnIntent = new Intent(this, PostActivity.class);
            returnIntent.putStringArrayListExtra("result", mImageList);
            returnIntent.putExtra("isPostGallery", postGallery);
            returnIntent.putExtra("drawingType", "freehand");
            startActivity(returnIntent);
        } else {
            Intent returnIntent = new Intent(this, PostActivity.class);
            returnIntent.putStringArrayListExtra("result", mImageList);
            returnIntent.putExtra("isPostGallery", postGallery);
            returnIntent.putExtra("drawingType", "tutorials");
            startActivity(returnIntent);
        }*/
//        }
//        else {
//            Intent intent = new Intent(MyPaintingsActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
    }

    private void showPreventMultiplePostingDialog(String type, String postGallery) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_prevent_multiple_posting, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);

        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        TextView btnSeePost = dialogView.findViewById(R.id.btnSeePost);
        if (postGallery.equalsIgnoreCase("post_gallery")) {
            tvDialogMessage.setText("You already have this drawing in Gallery");
        } else {
            tvDialogMessage.setText("You already have this drawing in Commmunity");
        }
        // Set up the "See your post" button click listener
        btnSeePost.setOnClickListener(v -> {
            // Implement the action to take the user to their post in the Gallery

            if (postGallery.equalsIgnoreCase("post_gallery")) {
                Intent intent = new Intent(MyPaintingsActivity.this, GalleryActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyPaintingsActivity.this, CommunityDetail.class);
                intent.putExtra("user_id", constants.getString(constants.UserId, MyPaintingsActivity.this));
                intent.putExtra("user_name", constants.getString(constants.Username, MyPaintingsActivity.this));
                startActivity(intent);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onSubMenuClick(View view, @Nullable PaintItem item, int position) {


        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.my_painting_item_menu, popupMenu.getMenu());

        MenuItem menuItem = popupMenu.getMenu().findItem(R.id.action_open_trace);
        menuItem.setVisible(item.getFileName() != null && mTraces.containsKey(item.getFileName()));

        MenuItem menuItem2 = popupMenu.getMenu().findItem(R.id.action_open_drawing);
        menuItem2.setVisible(item.getFileName() != null && mDrawings.containsKey(item.getFileName()));

        MenuItem menuItem3 = popupMenu.getMenu().findItem(R.id.action_open_in_trace);
        menuItem3.setVisible((item.getFileName() != null && !mDrawings.containsKey(item.getFileName()) && !mTraces.containsKey(item.getFileName())));

        MenuItem menuItem4 = popupMenu.getMenu().findItem(R.id.action_open_in_overlay);
        menuItem4.setVisible(item.getFileName() != null && item._drawing_type.equals(drawing_type.TraceDrawaing) || mTraces.containsKey(item.getFileName()));

        MenuItem menuItem5 = popupMenu.getMenu().findItem(R.id.action_open_tutorial);
        menuItem5.setVisible(item.getFileName() != null && entityHashMap.containsKey(item.getFileName()));

        if (menuItem5.isVisible()) {
            menuItem5.setTitle(getResources().getString(R.string.open_tutorial) + ": " + entityHashMap.get(item.getFileName()).postId);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_post:
                        if (constants.getBoolean(constants.IsGuestUser, MyPaintingsActivity.this)) {
                            FireUtils.openLoginScreen(MyPaintingsActivity.this, true);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_icon_community, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_icon_community);
                            post(item, position, "post_community");
                        }
                        break;

                    case R.id.action_post_gallery:

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_icon_community, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_icon_community);

                        // will check here if this post is already avialible in firebase or not

                        post(item, position, "post_gallery");
                        break;


                    case R.id.action_open:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_open, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open);
                        edit(item, position, false);
                        break;

                    case R.id.action_rename:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_rename, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_rename);
                        renameFileDialog(position, item);
                        break;
                    case R.id.action_open_trace:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_trace, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_trace);
                        try {
                            String path = KGlobal.getMyPaintingFolderPath(getApplicationContext()) + "/" + mTraces.get(item.getFileName()).getFileName();
                            Intent lIntent1 = new Intent();
                            lIntent1.setClass(getApplicationContext(), PaintActivity.class);
                            lIntent1.setAction("Edit Paint");
                            try {
                                if (entityHashMap.containsKey(mTraces.get(item.getFileName()).getFileName())) {
                                    lIntent1.putExtra("id", String.valueOf(entityHashMap.get(mTraces.get(item.getFileName()).getFileName()).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            lIntent1.putExtra("isImport", mTraces.get(item.getFileName()).get_drawing_type());
                            lIntent1.putExtra("FromLocal", true);
                            lIntent1.putExtra("paint_name", path);
                            lIntent1.putExtra("isOverraid", false);
                            fetchConfig(lIntent1, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                            startActivity(lIntent1);
                            finish();
                        } catch (Exception e) {
                            Log.e("MyPaintingsActivity", e.getMessage());
                        }
                        break;
                    case R.id.action_open_drawing:
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_overlay);
                        edit(mDrawings.get(item.getFileName()), 0, false);
                        break;

                    case R.id.action_open_in_trace:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_trace, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_trace);
                        try {
                            String path = KGlobal.getMyPaintingFolderPath(getApplicationContext()) + "/" + item.getFileName();
                            Intent lIntent1 = new Intent();
                            lIntent1.setClass(getApplicationContext(), PaintActivity.class);
                            lIntent1.setAction("Edit Paint");
                            lIntent1.putExtra("FromLocal", true);
                            lIntent1.putExtra("paint_name", path);
                            try {
                                if (entityHashMap.containsKey(item.getFileName())) {
                                    lIntent1.putExtra("id", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            lIntent1.putExtra("isOverraid", false);
                            fetchConfig(lIntent1, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                            startActivity(lIntent1);
                            finish();
                        } catch (Exception e) {
                            Log.e("MyPaintingsActivity", e.getMessage());
                        }
                        break;
                    case R.id.action_open_in_overlay:
                        if (mTraces.containsKey(item.getFileName())) {
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_overlay);
                            Intent intentt = new Intent(getApplicationContext(), PaintActivity.class);
                            intentt.setAction("LoadWithoutTrace");
                            try {
                                if (entityHashMap.containsKey(item.getFileName())) {
                                    intentt.putExtra("id", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            intentt.putExtra("path", mTraces.get(item.getFileName()).getFileName());
                            intentt.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(getApplicationContext()));
                            fetchConfig(intentt, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                            startActivity(intentt);
                            finish();
                        } else if (entityHashMap.containsKey(item.getFileName())) {
                            File file = new File(entityHashMap.get(item.getFileName()).originPath);
                            if (file.exists()) {
                                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_overlay);
                                Intent intentt = new Intent(getApplicationContext(), PaintActivity.class);
                                intentt.setAction("LoadWithoutTrace");
                                try {
                                    if (entityHashMap.containsKey(item.getFileName())) {
                                        intentt.putExtra("id", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                intentt.putExtra("path", file.getName());
                                intentt.putExtra("ParentFolderPath", file.getParent());
                                fetchConfig(intentt, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                                startActivity(intentt);
                                finish();
                            } else {
                                FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_overlay);
                                Intent intentt = new Intent(getApplicationContext(), PaintActivity.class);
                                intentt.setAction("LoadWithoutTrace");
                                try {
                                    if (entityHashMap.containsKey(item.getFileName())) {
                                        intentt.putExtra("id", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                intentt.putExtra("path", item.getFileName());
                                intentt.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(getApplicationContext()));
                                fetchConfig(intentt, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                                startActivity(intentt);
                                finish();
                            }
                        } else {
                            FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_open_in_overlay);
                            Intent intentt = new Intent(getApplicationContext(), PaintActivity.class);
                            intentt.setAction("LoadWithoutTrace");
                            try {
                                if (entityHashMap.containsKey(item.getFileName())) {
                                    intentt.putExtra("id", String.valueOf(entityHashMap.get(item.getFileName()).postId));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            intentt.putExtra("path", item.getFileName());
                            intentt.putExtra("ParentFolderPath", KGlobal.getMyPaintingFolderPath(getApplicationContext()));
                            fetchConfig(intentt, KGlobal.getMyPaintingFolderPath(MyPaintingsActivity.this) + "/" + item.getFileName());
                            startActivity(intentt);
                            finish();
                        }
                        break;
                    case R.id.action_open_tutorial:
                        FireUtils.showProgressDialog(
                                MyPaintingsActivity.this,
                                getString(R.string.please_wait)
                        );
                        new TutorialUtils(MyPaintingsActivity.this).parseTutorial(String.valueOf(entityHashMap.get(item.getFileName()).postId));
                        finish();
                        break;
                    case R.id.action_add_text:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_add_text, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_add_text);
                        addText(position, item);
                        break;
                    case R.id.action_share:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_share, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_share);
                        share(item, position);
                        break;
                    case R.id.action_download:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_download, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_download);
                        download(item);
                        break;
                    case R.id.action_delete:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_delete, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_delete);
                        delete(item, position);
                        break;
                    case R.id.action_cancel:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyPaintingsActivity.this, constants.my_paintings_menuitem_cancel, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyPaintingsActivity.this, constants.my_paintings_menuitem_cancel);
                        popupMenu.dismiss();
                        break;

                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }

    private void addText(int position, PaintItem item) {
        String path = parentFolderPath + "/" + item.getFileName();
        String extension = FilenameUtils.getExtension(path);

        File oldFile = null;
        if (!TextUtils.isEmpty(path)) {
            if (item.get_drawing_type().equals(drawing_type.Movie)) {
                showToast("Please select image file");
            } else {
                oldFile = new File(path);
            }
        }

        if (oldFile != null) {
            try {
                Uri fileUri = Uri.fromFile(oldFile);
                PatrickCox.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                Intent intent = new Intent(this, PhotoEditorActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void renameFileDialog(final int position, PaintItem item) {

        String path = parentFolderPath + "/" + item.getFileName();
        String extension = FilenameUtils.getExtension(path);

        File oldFile = null;
        if (!TextUtils.isEmpty(path)) {
            if (item.get_drawing_type().equals(drawing_type.Movie)) {
//            if (extension.equalsIgnoreCase("mp4")) {
                oldFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + item.getFileName());
//                oldFile = new File(path);
            } else {
                oldFile = new File(path);
            }
        }


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_save);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        androidx.appcompat.widget.AppCompatEditText input = dialog.findViewById(R.id.edtFileName);
        TextView header = dialog.findViewById(R.id.tvMessage);
        ImageView cross = dialog.findViewById(R.id.imgCross);


//        input.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.typing), PorterDuff.Mode.SRC_ATOP);

//        FilenameUtils.EXTENSION_SEPARATOR + extension

        String name = AppUtils.getFileNameWithoutExtension(oldFile);
        input.setText(name);
        input.setSelection(0, name.length());
        input.setSelectAllOnFocus(true);
        input.selectAll();

//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


        showSoftKeyboard(MyPaintingsActivity.this, input);

        input.requestFocus();


        header.setText(getString(R.string.dialog_title_rename));


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!TextUtils.isEmpty(input.getText().toString())) {
                        String value = input.getText().toString().trim();
                        applyNewName(item, value, position, extension);
                    }
                } catch (Exception e) {
                    Log.e("LOG_TAG", "exception", e);
                }

                dialog.dismiss();
            }
        });

        dialog.show();


        // File rename dialog
//        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(this);
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.dialog_rename_file, null);

//        final EditText input = view.findViewById(R.id.new_name);
//        final TextView textView = view.findViewById(R.id.textView);

//        textView.setText();


//        renameFileBuilder.setTitle(getString(R.string.dialog_title_rename));
//        renameFileBuilder.setCancelable(true);
//        renameFileBuilder.setPositiveButton(getString(R.string.dialog_action_ok),
//                new DialogInterface.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    public void onClick(DialogInterface dialog, int id) {
//                        try {
//                            if (!TextUtils.isEmpty(input.getText().toString())) {
//                                String value = input.getText().toString().trim();
//                                applyNewName(item, value, position, extension);
//                            }
//                        } catch (Exception e) {
//                            Log.e("LOG_TAG", "exception", e);
//                        }
//
//                        dialog.cancel();
//                    }
//                });
//        renameFileBuilder.setNegativeButton(getString(R.string.dialog_action_cancel),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        renameFileBuilder.setView(view);
//        AlertDialog alert = renameFileBuilder.create();
//        alert.show();
    }

//    private void renameFile(PaintItem item, int position) {
//
//        final EditText etRename = new EditText(this);
//        etRename.setText(item.getFileThumbName().substring(0, item.getFileThumbName().lastIndexOf(".")));
//        etRename.setHint("Enter new name");
////        etRename.setSelection();
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setCancelable(false)
//                .setTitle("Rename")
//                .setView(etRename)
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String name = etRename.getText().toString();
//                        if (!TextUtils.isEmpty(name)) {
//                            applyNewName(item, name, position);
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .create();
//        dialog.show();
//
//    }


    public static void showSoftKeyboard(final Context context, final EditText editText) {
        try {
            editText.requestFocus();
            editText.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(editText, 0);
                        }
                    }
                    , 200);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void applyNewName(PaintItem item, String newFilenameWithoutExt, int position, String extension) {
        File oldFile;
        File oldFileMovieThumbnail = null;
        File newFileMovieThumbnail = null;

        String newFilenameWithExt = newFilenameWithoutExt + "." + extension;

        File newFile;
        if (extension.equalsIgnoreCase("mp4")) {
//            oldFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + item.getFileName());
//            newFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + newFilenameWithExt);
            oldFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + item.getFileName());
            newFile = new File(KGlobal.getDownloadedFolderPath(this) + "/" + newFilenameWithExt);

            oldFileMovieThumbnail = new File(parentFolderPath + "/" + item.getFileThumbName());
            newFileMovieThumbnail = new File(parentFolderPath + "/" + newFilenameWithoutExt + "." + "png");
        } else {
            oldFile = new File(parentFolderPath + "/" + item.getFileThumbName());
            newFile = new File(parentFolderPath + "/" + newFilenameWithExt);
        }

        ExecutorService myExecutor = Executors.newSingleThreadExecutor();
        Handler myHandler = new Handler(Looper.getMainLooper());

        File finalNewFile = newFile;
        File finalOldFile = oldFile;

        File finalOldFileMovieThumbnail = oldFileMovieThumbnail;
        File finalNewFileMovieThumbnail = newFileMovieThumbnail;
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Do something in background (back-end process)
                if (extension.equalsIgnoreCase("mp4")) {
                    // Rename mp4 file's thumbnail
                    if (finalOldFileMovieThumbnail != null && finalNewFileMovieThumbnail != null) {
                        finalOldFileMovieThumbnail.renameTo(finalNewFileMovieThumbnail);
                    }

                    // Rename mp4 file
                    finalOldFile.renameTo(finalNewFile);

                } else {
                    // Rename image file's thumbnail
                    if (finalOldFile.renameTo(finalNewFile)) {
                        AppDatabase db = MyApplication.getDb();
                        SavedTutorialDao savedTutorialDao = db.savedTutorialDao();
                        SavedDrawingDao savedDrawingDao = db.savedDrawingDao();
                        savedTutorialDao.updateLocalPathByOldPath(finalNewFile.getPath(), finalOldFile.getPath());
                        savedDrawingDao.updateLocalPathByOldPath(finalNewFile.getPath(), finalOldFile.getPath());


                        String newTraceFilenameWithExt = newFilenameWithoutExt + "_Trace." + extension;
                        String newDrawingFilenameWithExt = newFilenameWithoutExt + "_Drawing." + extension;

                        String oldTraceName = "";
                        String oldDrawingName = "";

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (mTraces.containsKey(item.getFileName())) {
                            PaintItem paintItem = mTraces.get(item.getFileName());
                            if (paintItem != null) {
                                if (new File(parentFolderPath + "/" + paintItem.getFileThumbName()).renameTo(new File(parentFolderPath + "/" + newTraceFilenameWithExt))) {
                                    if (new File(parentFolderPath + "/" + newTraceFilenameWithExt).exists()) {
                                        if (new File(parentFolderPath + "/" + paintItem.getFileThumbName()).exists()) {
                                            new File(parentFolderPath + "/" + paintItem.getFileThumbName()).delete();
                                        }
                                    }
                                    oldTraceName = paintItem.getFileName();
                                    paintItem.setFileThumbName(newTraceFilenameWithExt);
                                    paintItem.mFileName = newTraceFilenameWithExt;
                                    mTraces.put(newFilenameWithExt, paintItem);
                                    mTraces.remove(item.getFileName());
                                }
                            }
                        }

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if (mDrawings.containsKey(item.getFileName())) {
                            PaintItem paintItem = mDrawings.get(item.getFileName());
                            if (paintItem != null) {
                                if (new File(parentFolderPath + "/" + paintItem.getFileThumbName()).renameTo(new File(parentFolderPath + "/" + newDrawingFilenameWithExt))) {
                                    if (new File(parentFolderPath + "/" + newTraceFilenameWithExt).exists()) {
                                        if (new File(parentFolderPath + "/" + paintItem.getFileThumbName()).exists()) {
                                            new File(parentFolderPath + "/" + paintItem.getFileThumbName()).delete();
                                        }
                                    }
                                    oldDrawingName = paintItem.getFileName();
                                    paintItem.setFileThumbName(newDrawingFilenameWithExt);
                                    paintItem.mFileName = newDrawingFilenameWithExt;
                                    mDrawings.put(newFilenameWithExt, paintItem);
                                    mDrawings.remove(item.getFileName());
                                }
                            }
                        }

                        if (mTraces.containsKey(newFilenameWithExt) || mDrawings.containsKey(newFilenameWithExt)) {
                            if (mDrawings.containsKey(newFilenameWithExt)) {
                                updatePathinPrefs(constants.getTraceList_Gson_Key(), item.getFileName(), newFilenameWithExt, newTraceFilenameWithExt);
                                if (!oldTraceName.equalsIgnoreCase(""))
                                    updatePathinPrefs(constants.getTraceList_Gson_Key(), oldTraceName, newTraceFilenameWithExt, null);
                                if (!oldDrawingName.equalsIgnoreCase(""))
                                    updatePathinPrefs(constants.getTraceList_Gson_Key(), oldDrawingName, newDrawingFilenameWithExt, null);
                            } else {
                                updatePathinPrefs(constants.getOverlayList_Gson_Key(), item.getFileName(), newFilenameWithExt, newTraceFilenameWithExt);
                                if (!oldTraceName.equalsIgnoreCase(""))
                                    updatePathinPrefs(constants.getOverlayList_Gson_Key(), oldTraceName, newTraceFilenameWithExt, null);
                            }
                        }
                    }
                }

                // Create an interface to respond with the result after processing
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI here
                        // Do something in UI (front-end process)

                        if (entityHashMap.containsKey(mItems.get(position).getFileName())) {
                            entityHashMap.put(newFilenameWithExt, entityHashMap.get(mItems.get(position).getFileName()));
                            entityHashMap.remove(mItems.get(position).getFileName());
                        }

                        item.setFileThumbName(newFilenameWithExt);
                        item.mFileName = newFilenameWithExt;


                        mItems.set(position, item);

                        mAdapter.notifyDataSetChanged();

                        // Rename mp4 file's event and stroke files too
                        if (extension.equalsIgnoreCase("mp4")) {
                            String newFileNameWithoutExt = newFilenameWithExt.substring(0, newFilenameWithExt.lastIndexOf("."));
                            String oldFileNameWithoutExt = finalOldFile.getName().substring(0, finalOldFile.getName().lastIndexOf("."));
                            renameStrokeAndEventFiles(newFileNameWithoutExt, oldFileNameWithoutExt, position);
                        }


                        boolean finished = false;
//                        if (!TextUtils.isEmpty(swatchesJson)) {
//                            setupSwatches(swatchesJson);
                        finished = true;
//                        } else {
//                            Toast.makeText(Paintor.this, "Swatches Missing", Toast.LENGTH_SHORT).show();
//                        }

                        // If we're done with the ExecutorService, shut it down.
                        // (If you want to re-use the ExecutorService,
                        // make sure to shut it down whenever everything's completed
                        // and you don't need it any more.)
                        if (finished) {
                            myExecutor.shutdown();
                        }
                    }
                });
            }
        });
    }

    public void updatePathinPrefs(String mPrefs, String oldName, String newName, String traceImageName) {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(MyPaintingsActivity.this);

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(mPrefs, "");
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();
        ArrayList<TraceReference> traceList = gson.fromJson(json, type);

        if (traceList == null)
            traceList = new ArrayList<TraceReference>();

        for (int j = 0; j < traceList.size(); j++) {
            if (traceList.get(j).getUserPaintingName().equalsIgnoreCase(oldName)) {
                traceList.get(j).setUserPaintingName(newName);
                if (traceImageName != null)
                    traceList.get(j).setTraceImageName(parentFolderPath + "/" + traceImageName);
            }
        }

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        String json_1 = gson.toJson(traceList);
        prefsEditor.putString(mPrefs, json_1);
        prefsEditor.commit();
    }

    // Rename mp4 file's stroke and event files
    private void renameStrokeAndEventFiles(String newFileNameWithoutExt, String oldFileNameWithoutExt, int position) {

        String oldEventPath = KGlobal.getDownloadedFolderPath(this) + "/EventData_" + oldFileNameWithoutExt + ".txt";
        String oldStrokePath = KGlobal.getDownloadedFolderPath(this) + "/StrokeData_" + oldFileNameWithoutExt + ".txt";

        File oldStrokeFile = new File(oldStrokePath);
        File oldEventFile = new File(oldEventPath);

        String newEventFilePath = KGlobal.getDownloadedFolderPath(this) + "/EventData_" + newFileNameWithoutExt + ".txt";
        String newStrokeFilePath = KGlobal.getDownloadedFolderPath(this) + "/StrokeData_" + newFileNameWithoutExt + ".txt";

        File newStrokeFile = new File(newStrokeFilePath);
        File newEventFile = new File(newEventFilePath);

        oldStrokeFile.renameTo(newStrokeFile);
        oldEventFile.renameTo(newEventFile);

        mAdapter.notifyDataSetChanged();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MyPaintingsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MyPaintingsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void download(PaintItem item) {

        String path = parentFolderPath + "/" + item.getFileName();
        String extension = FilenameUtils.getExtension(path);

        if (!TextUtils.isEmpty(path)) {
            if (extension.equalsIgnoreCase("mp4")) {
                downloadMovie(item);
            } else {
                downloadImage(item);
            }
        }
    }

    private void downloadImage(PaintItem item) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (!checkPermission()) {
                    requestPermission();
                    return;
                }
            }

        }

        File file = new File(parentFolderPath + "/" + item.getFileThumbName());

        String filenameWithExtention = item.getFileThumbName();
        String extStorageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString();

        if (!new File(extStorageDirectory).exists()) {
            new File(extStorageDirectory).mkdirs();
        }

        File pictureFile = new File(extStorageDirectory, filenameWithExtention);
        if (pictureFile == null) {
            Log.d("MyPaintingActivity",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }

        try {
            AppUtils.copyFile(file, pictureFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pictureFile.exists()) {
                Toast.makeText(this, "Drawing is  in your Images folder", Toast.LENGTH_SHORT).show();
                MediaScannerConnection.scanFile(this, new String[]{pictureFile.getAbsolutePath()}, null, null);
            }
        }

    }

    private void downloadMovie(PaintItem item) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (!checkPermission()) {
                    requestPermission();
                    return;
                }
            }

        }

        File file = new File(KGlobal.getDownloadedFolderPath(this) + "/" + item.getFileName());

        String filenameWithExtension = item.getFileName();
        String extStorageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                        .toString();

        if (!new File(extStorageDirectory).exists()) {
            new File(extStorageDirectory).mkdirs();
        }

        File pictureFile = new File(extStorageDirectory, filenameWithExtension);
        if (pictureFile == null) {
            Log.d("MyPaintingActivity",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }

        try {
            AppUtils.copyFile(file, pictureFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pictureFile.exists()) {
                Toast.makeText(this, "Video is in your Videos folder", Toast.LENGTH_SHORT).show();
                MediaScannerConnection.scanFile(this, new String[]{pictureFile.getAbsolutePath()}, null, null);
            }
        }

    }

    void getVideoGuideDataFromAPI() {
        Call<GetCategoryPostModel> call = apiInterface.getCategoryPostList(ApiClient.SECRET_KEY, "32");
        call.enqueue(new Callback<GetCategoryPostModel>() {
            @Override
            public void onResponse(Call<GetCategoryPostModel> call, retrofit2.Response<GetCategoryPostModel> response) {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                if (response.body() != null) {
                    ArrayList<GetCategoryPostModel.postData> list = response.body().getPostList();
                    if (mAdapter != null && list != null) {
                        mAdapter.setGuideCount(list.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCategoryPostModel> call, Throwable t) {

            }
        });
    }


    private void shareRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.my_paintings_share, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(MyPaintingsActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "my_paintings_share",
                        rewardSetup.getMy_paintings_share() == null ? 0 : rewardSetup.getMy_paintings_share(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }


    private void shareMovieRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.movies_share, null);
       /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(MyPaintingsActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "movies_share",
                        rewardSetup.getMovies_share() == null ? 0 : rewardSetup.getMovies_share(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MyConstantsKt.commonMenuClick(this, item, StringConstants.intro_paintings);
    }

}
