package com.paintology.lite.photorealistic.drawing.minipaint;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.paintology.lite.photorealistic.drawing.Activity.IntroActivity;
import com.paintology.lite.photorealistic.drawing.Activity.shared_pref.SharedPref;
import com.paintology.lite.photorealistic.drawing.Adapter.ColorSpinnerAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.CustomePicker.PostActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity;
import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;
import com.paintology.lite.photorealistic.drawing.MainInterface;
import com.paintology.lite.photorealistic.drawing.Model.BrushType;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.PatternInfo;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.Tooltip.Tooltip;
import com.paintology.lite.photorealistic.drawing.Tooltip.TooltipAnimation;
import com.paintology.lite.photorealistic.drawing.Youtube.player.AbstractYouTubePlayerListener;
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayer;
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayerView;
import com.paintology.lite.photorealistic.drawing.ads.koin.DIComponent;
import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.brushpicker.BrushPickerActivity;
import com.paintology.lite.photorealistic.drawing.camerax.CameraActivity;
import com.paintology.lite.photorealistic.drawing.colorpicker.ColorPad;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.databinding.DialogRewardBinding;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.gallery.MyMoviesActivity;
import com.paintology.lite.photorealistic.drawing.market.TargetMarket;
import com.paintology.lite.photorealistic.drawing.onboarding.utils.Events;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.PaintingTemp;
import com.paintology.lite.photorealistic.drawing.painting.file.ImageManager;
import com.paintology.lite.photorealistic.drawing.photo.Gallery;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.PaintingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedDrawingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedTutorialDao;
import com.paintology.lite.photorealistic.drawing.room.entities.PaintingEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.SavedTutorialEntity;
import com.paintology.lite.photorealistic.drawing.screenshot.ScreenShotActivity;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.CountDownAnimation;
import com.paintology.lite.photorealistic.drawing.util.EventModel;
import com.paintology.lite.photorealistic.drawing.util.EventType;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.LoadingDialog;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.MyServiceForRecording;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils;
import com.paintology.lite.photorealistic.drawing.util.SeekbarWithIntervals;
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;
import com.paintology.lite.photorealistic.drawing.util.VerticalSeekBarWrapper;
import com.samsung.spen.lib.input.SPenEventLibrary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaintActivity extends BrushPickerActivity
        implements View.OnClickListener,
        View.OnKeyListener,
        MainInterface,
//        View.OnDragListener,
        View.OnLongClickListener,
        VideoRendererEventListener,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int NEW_CANVAS_TAG = 111;
    @SuppressWarnings("unused")

    boolean paintingIntro = false;

    public static PaintActivity mActivity;
    //Social Login Data
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 7;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private boolean isFromGallery = false;
    private boolean isGalleryPost = false;

    public int CREATE_PATTERN = 301;


    GoogleApiClient googleApiClient;

    GoogleSignInAccount account;

    String _ip, _country, _city;

    LoginButton facebook_login_btn;

    boolean isLoggedIn;

    ProgressDialog progressDialog = null;
    int LOGIN_FROM_FB = 0;
    int LOGIN_FROM_GOOGLE = 1;
    int LOGIN_FROM_PAINTOLOGY = 2;
    String LoginInPaintology;
    ApiInterface apiInterface;

    ImageView iv_arrow, iv_arrow_canvas, iv_arrow_stroke;
    FrameLayout frm_hint, frm_hint_canvas, frm_hint_stroke;

    public String EDIT_PAINT = "Edit Paint";
    public String NEW_PAINT = "New Paint";
    public String RELOAD_PAINTING = "Reload Painting";


    private int SELECT_PHOTO_REQUEST = 400;
    private String TAG = "Paintor Activity";
    private boolean isProVersion = false;

    private int mCompressionFormat;
    private Runnable mCreatePaintingRunnable;
    private boolean mDontShowSketchMoviePromote;

    private boolean mDontShwoGarden1Promote;

    private Handler mHandler;
    public boolean mHideAdsView;
    public ImageManager mImageManager;


    private Thread mMyAdsThread;
    public PaintView mPaintView;
    public Painting mPainting;


    public PaintViewTemp mPaintViewTemp;
    private PaintingTemp mPaintingTemp;


    //	public GridView mPaintingMenuBar;
    private int mPlayNumbers = 0;
    private int mPrefAlpha = 255;
    private int mPrefBackgroundColor = 0xFFFFFFFF;//-16777216;
    private int mPrefBrushColor = -65536;
    private int mPreviousPrefBrushColor = -65536;
    private int mPrefBrushMode = 33;
    private float mPrefBrushSize = 35.0F;
    private int mPrefBrushStyle = Brush.LineBrush;
    private int mPrefFlow = 255;
    public float mScaleDensity;

    public int mScreenHeight;
    public int mScreenWidth;
    float _screenScaleX = 1.0F, _screenScaleY = 1.0F;

    private boolean mSketchMovieDownloaded;


    public int status_block = 101;
    //    public int mStatus = 3;
    public int mStatus = 1;
    private long mTimeOfPreviousSave;

    public float mXDensity;

    private float mYDensity;

    private View m_viewCurColor, colorbar_bgcolor;
    private ImageView m_viewArrow, img_community;
    private ImageView m_viewColorPanel;
    //	private RelativeLayout m_viewContainer;
    private RelativeLayout m_viewColorContainer;
    private ColorPad viewSatVal;
    private ImageView viewTarget;

    private float[] currentColorHsv = new float[3];
//    public static RelativeLayout m_brushlayout;

    public boolean m_bInitFlag = false;

    public boolean g_bDestroyFlag = true;
    public String mStrBackground = null;

    String saveFileName = "";

    int CurrentMode = -1;
    // paint buttons
    ImageView mNewCanvasBtn;
    public static ImageView paintmenu_close;
    public static ImageView mBrushStyleBtn;
    ImageView mUndoBtn;
    ImageView mRedoBtn;

    ImageView mZoomBtn;
    ImageView iv_canvas_lock;
    ImageView mSavePicBtn;
    ImageView mPaintmenu_pen;
    ImageView iv_toggle_preview, iv_selected_image, iv_temp_traced, iv_toggle_preview_tooltip;
    ImageView iv_switch_to_player;

    ImageView iv_gps_icon, iv_cursor_icon;
    private final String IMAGE_VIEW_TAG = "LAUNCHER LOGO";

    private SPenEventLibrary mSPenEventLibrary;

    public static MainInterface obj_interface;

    Bitmap selected_bitmap = null;
    RelativeLayout viewContainer;

    FrameLayout trace_bar_container;
    int startingProgress = 0;
    SeekBar seekbar_1;
    VerticalSeekBarWrapper seekBarContainer4;
    View /*view1, view2,*/ view_mid;
    View view_trace_left, view_trace_right;
    private int traceSeekbarOldProgress;
    private boolean traceLeftClicked = false, traceRightClicked = false;

    LinearLayout ll_toggle;

    ImageView iv_start_recoring;

    TextView tv_recording_time;
    TextView tv_rec_time;
//    public final String DEVELOPER_KEY = "AIzaSyAj18CSQAzC020PM2n0HCenzkQmEkGULss";

    // YouTube video id
//    public static final String YOUTUBE_VIDEO_CODE = "https://youtu.be/z2uawv8Rw2Q?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz";

    boolean FromTutorialMode = false;


    int strokeCount = 0;
    boolean isFromEditImage;
    String selectedImagePath = "";
    String youtube_video_id;

    String backgroundImagePath = "";

    public final int SAVE_INTERVAL_TIME = 600000;

    public final int ONE_SECOND = 1000;

    private final int PERMISSION_CODE = 1;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private int DISPLAY_WIDTH = 480;
    private int DISPLAY_HEIGHT = 640;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;

    public ArrayList<EventModel> eventModelList = new ArrayList<>();

    Handler handler = new Handler();
    Handler recording_handler = new Handler();

    int second_indicator = 0;
    private YouTubePlayerView youTubePlayerView;
    Button btn_next_stroke;

    TextView tv_startTime, tv_endTime;
    SeekbarWithIntervals seekBar_timeLine;
    LinearLayout ll_bottom_bar;

    boolean isInZoomMode = false;
    Double alphaValue = 0.0;
    ImageView iv_play_pause;

    int currentStrokeIndex = 1;
    int index_stroke = 0;

    Boolean isPickFromOverlaid = false;
    ImageView iv_hide_exo_player;
    String recordingOutputFilePath = "";
    StringConstants constants = new StringConstants();
    ProgressBar progress;
    LinearLayout ll_rendering;
    LoadingDialog Loadingdialog;


    String PaintingType = "";

    ImageView iv_new_canvas, iv_new_ovarlay, iv_new_trace, iv_new_camera;
    CardView linear_background_color;

    FrameLayout colorlayout_2;
    View view_cross, view_cross2, view_zoom_indicator;
    boolean isBGSelected = false;
    RecyclerView pal_container;

    TextView tv_cancel, tv_ok;
    int bg_color_temp = 0;

    public int CAMERA_REQUEST = 150;

    private File output = null;
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";
    private final String PHOTOS = "photos";
    private final String FILENAME = System.currentTimeMillis() + "_img.jpeg";

    int orientation;
    boolean isTablet;

    LinearLayout ll_left_container;

    Handler _handler = null;
    Runnable _runnable = null;

    TextView tv_zoom_per;

    ImageView iv_plus_zoom;

    boolean isBGsetup = false;

    RelativeLayout relative_parent, rl_gray_scale;
    ImageView iv_gray_scale_indicator, iv_gray_scale_image;
    Bitmap selected_bitmap_gray_scale;

    int topLimit = 5;
    int bottomlimit = 0;

    int currentGrayScaleColor = Color.RED;
    public TextView tv_special_fun, tv_brush_percentage;
    private boolean videoPlayedOnce = false;

    ArrayList<axisAnDColor> _lst_colors_gray_scale = new ArrayList<>();

    ArrayList<axisAnDColor> _lst_colors_rgb = new ArrayList<>();
    private AppDatabase db;
    private String swatchesJson;
    private String colorPalette;
    private int post_id = -1;
    private static final int SAVE_TAG = 10;
    public static PopupWindow brushSettingsPopup;
    public static View brushDialogView;
    private RecyclerView brushList;
    private Timer touchTimer;

//    private static final int SCREEN_RECORD_REQUEST_CODE = 777;
//    private boolean screenCapturePermission;

    public static MutableLiveData<Boolean> _showBrushSettingsPopup = new MutableLiveData<>();
    private LiveData<Boolean> showBrushSettingsPopup = _showBrushSettingsPopup;

    public static ImageView banner;

    private boolean autoColorPickerActivated = false;
    private boolean pickNewColorMode = false;

    private static final int SCREEN_RECORD_REQUEST_CODE = 777;
    private boolean screenCapturePermission;
    private MediaProjectionManager mediaProjectionManager;
    private final String[] paintByNumberIntroRequireColors = {"#fff600", "#ffb832"};
    private int step = 0;
    private SharedPref sharedPref;

    private final ActivityResultLauncher<Intent> userLoginActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = new Intent(PaintActivity.this, GalleryDashboard.class);
                    startActivity(intent);
                }
                finishAffinity();
            });

    ActivityResultLauncher<Intent> startMediaProjection = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = (Intent) result.getData().clone();
                    AppUtils.setDataFromResultSS(data);
                    screenCapturePermission = true;
                } else {
                    screenCapturePermission = false;
                }
            }
    );

    private ActivityResultLauncher<Intent> loginActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
//                        Intent data = result.getData();
                        new saveImageInBackForShare(false).execute();
                    }
                }
            });

    public PaintActivity() {
        try {
            Log.e("TAG", "Paintor() called");
            mCreatePaintingRunnable = new Paintor1();
            mTimeOfPreviousSave = 0L;
            mDontShwoGarden1Promote = false;

            mSketchMovieDownloaded = false;
            mDontShowSketchMoviePromote = false;


            mHideAdsView = false;
        } catch (Exception e) {
            Log.e("TAG", "Exception Paintor " + e.getMessage(), e);
        }
    }

    private void MyDbgLog(String pString1, String pString2) {
    }

    private void bringPadToFront() {
        mPaintView.bringToFront();
    }

    public void clearPainting() {
        selectCanvasType();
    }

    private void clearPainting(boolean pBoolean) {
        if (pBoolean)
            startBackgroundColorPicker();
        else {
//            Gallery.startGallery(this, SELECT_PHOTO_REQUEST);
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO_REQUEST);
        }
        unhideAds();
        mPaintView.resetMatrix();
//        mStatus = 1;
    }

    private void clearPaintingWithSameCanvas() {
        unhideAds();
        mPaintView.resetMatrix();
//        mStatus = 1;
        mPainting.clearPainting();
        mPaintView.reDraw(null);
        deleteRecovery();
    }

    /**
     * =========================================== Exit dialog confirmation with different text======
     */

    private void confirmExit(boolean isCurrent) {
//        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(Paintor.this);
//        String str = "";
//
////        lBuilder1.setTitle(!isCurrent ? "Exit" : null);
//        if (isCurrent) {
//            int selected = (int) mNewCanvasBtn.getTag();
//            if (selected == 0) {
////                str = "Switch to New Paint?";
//                str = "Switch to Canvas Drawing ?";
//            } else if (selected == 1) {
////                str = "Switch to Overlay image?";
//                str = "Switch to Overlay Drawing ?";
//            } else if (selected == 2) {
////                str = "Switch to Trace mode?";
//                str = "Switch to Trace Drawing ?";
//            } else if (selected == 3) {
////                str = "Switch to Camera mode?";
//                str = "Switch to Camera Drawing ?";
//            }
//        } else {
//            str = "Exit Drawing ?";
//        }
//
//
//        lBuilder1.setMessage(str).setCancelable(true);
//
//        lBuilder1.setPositiveButton(!isCurrent ? "Ok" : "Yes", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, constants.getCLOSE_CANVAS(), Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(Paintor.this, constants.getCLOSE_CANVAS());
//                deleteTempFile();
////                iv_selected_image.setImageDrawable(null);
//                try {
//                    if (youTubePlayerView != null) {
//                        youTubePlayerView.release();
//                        youTubePlayerView.setVisibility(View.GONE);
//                        Log.e("TAGG", "Release YT Playe");
//                    }
//
//                    if (player != null) {
//                        player.stop();
//                        player.release();
//                        player = null;
//                        simpleExoPlayerView.setVisibility(View.GONE);
//                        Log.e("TAGG", "Release Exo player");
//                    }
//                    if (iv_switch_to_player != null)
//                        iv_switch_to_player.setVisibility(View.GONE);
//                    ll_bottom_bar.setVisibility(View.GONE);
//
//                } catch (Exception e) {
//                    handler.removeCallbacks(runnableCode);
//                }
//
//                if (isCurrent) {
//                    int selected = (int) mNewCanvasBtn.getTag();
//                    Log.e("TAGGG", "OnActivity Result Call Exit -> " + selected);
//                    if (selected == 0) {
//
//                       /* Intent lIntent1 = new Intent();
//                        lIntent1.setClass(Paintor.this, ColorPickerActivity.class);
//                        lIntent1.putExtra("for_brush", false);
//                        lIntent1.putExtra("current_color", constants.getInt("background_color", Paintor.this));
//                        startActivityForResult(lIntent1, SELECT_BACKGROUND_COLOR_REQUEST);*/
//
//                        Current_Mode = NEW_PAINT;
//
//                        constants.putString("pickfromresult", "", Paintor.this);
//                        constants.putString("isfromTrace", "", Paintor.this);
//                        constants.putString("isfromoverlay", "", Paintor.this);
//                        constants.putString("path", "", Paintor.this);
//                        constants.putString("parentFolder", "", Paintor.this);
//                        constants.putString("type", "", Paintor.this);
//
//                        constants.putString("action_name", "", Paintor.this);
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                        reflectCanvas();
//                        iv_selected_image.setImageBitmap(mPainting.getBitmap());
//                        iv_selected_image.setAlpha(0.1f);
//                        iv_selected_image.setVisibility(View.VISIBLE);
//                    } else if (selected == 1) {
//                        /*Intent _new_Intent = new Intent();
//                        _new_Intent.putExtra("actions", "LoadWithoutTrace");
//                        setResult(Activity.RESULT_OK, _new_Intent);
//                        finish();*/
//                        pickImageFromGallery(true);
//
//                    } else if (selected == 2) {
//                      /*  Intent _new_Intent = new Intent();
//                        _new_Intent.putExtra("actions", "Edit Paint");
//                        setResult(Activity.RESULT_OK, _new_Intent);
//                        finish();*/
//
//                        pickImageFromGallery(false);
//
//                    } else if (selected == 3) {
//                     /*   Intent _new_Intent = new Intent();
//                        _new_Intent.putExtra("actions", "LoadWithoutTraceFromCamera");
//                        setResult(Activity.RESULT_OK, _new_Intent);
//                        finish();*/
//                        captureImage();
//                    }
//                } else {
//                    exitFromAPP();
//                }
//
//                //finish();
//            }
//        });
//        lBuilder1.setNeutralButton(!isCurrent ? "Cancel" : "No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                dialog.cancel();
//            }
//        });
//        lBuilder1.create().show();

        String str = "";

//        lBuilder1.setTitle(!isCurrent ? "Exit" : null);
        if (isCurrent) {
            int selected = (int) mNewCanvasBtn.getTag();
            if (selected == 0) {
//                str = "Switch to New Paint?";
                str = "Switch to Canvas Drawing ?";
            } else if (selected == 1) {
//                str = "Switch to Overlay image?";
                str = "Switch to Overlay Drawing ?";
            } else if (selected == 2) {
//                str = "Switch to Trace mode?";
                str = "Switch to Trace Drawing ?";
            } else if (selected == 3) {
//                str = "Switch to Camera mode?";
                str = "Switch to Camera Drawing ?";
            }
        } else {
            str = "Exit Drawing ?";
        }

        final Dialog exitDialog = new Dialog(this, R.style.CustomDialog);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.dialog_switch_canvas_drawing);
        Button btnYes = exitDialog.findViewById(R.id.btnYes);
        Button btnNo = exitDialog.findViewById(R.id.btnNo);
        TextView tvMessage = exitDialog.findViewById(R.id.tvMessage);
        ImageView imgCross = exitDialog.findViewById(R.id.imgCross);

        tvMessage.setText(str);

        imgCross.setOnClickListener(view -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.getCLOSE_CANVAS(), Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.getCLOSE_CANVAS());
                deleteTempFile();
//                iv_selected_image.setImageDrawable(null);
                try {
                    if (youTubePlayerView != null) {
                        youTubePlayerView.release();
                        youTubePlayerView.setVisibility(View.GONE);
                        Log.e("TAGG", "Release YT Playe");
                    }

                    if (player != null) {
                        player.stop();
                        player.release();
                        player = null;
                        simpleExoPlayerView.setVisibility(View.GONE);
                        Log.e("TAGG", "Release Exo player");
                    }
                    if (iv_switch_to_player != null)
                        iv_switch_to_player.setVisibility(View.GONE);
                    ll_bottom_bar.setVisibility(View.GONE);

                } catch (Exception e) {
                    handler.removeCallbacks(runnableCode);
                }

                if (isCurrent) {
                    int selected = (int) mNewCanvasBtn.getTag();
                    Log.e("TAGGG", "OnActivity Result Call Exit -> " + selected);
                    if (selected == 0) {

                       /* Intent lIntent1 = new Intent();
                        lIntent1.setClass(Paintor.this, ColorPickerActivity.class);
                        lIntent1.putExtra("for_brush", false);
                        lIntent1.putExtra("current_color", constants.getInt("background_color", Paintor.this));
                        startActivityForResult(lIntent1, SELECT_BACKGROUND_COLOR_REQUEST);*/

                        Current_Mode = NEW_PAINT;

                        constants.putString("pickfromresult", "", PaintActivity.this);
                        constants.putString("isfromTrace", "", PaintActivity.this);
                        constants.putString("isfromoverlay", "", PaintActivity.this);
                        constants.putString("path", "", PaintActivity.this);
                        constants.putString("parentFolder", "", PaintActivity.this);
                        constants.putString("type", "", PaintActivity.this);

                        constants.putString("action_name", "", PaintActivity.this);
                        // new code
                        int orientation = this.getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        reflectCanvas();
                        iv_selected_image.setImageBitmap(mPainting.getBitmap());
                        iv_selected_image.setAlpha(0.1f);
                        iv_selected_image.setVisibility(View.VISIBLE);
                    } else if (selected == 1) {
                        /*Intent _new_Intent = new Intent();
                        _new_Intent.putExtra("actions", "LoadWithoutTrace");
                        setResult(Activity.RESULT_OK, _new_Intent);
                        finish();*/
                        pickImageFromGallery(true);

                    } else if (selected == 2) {
                      /*  Intent _new_Intent = new Intent();
                        _new_Intent.putExtra("actions", "Edit Paint");
                        setResult(Activity.RESULT_OK, _new_Intent);
                        finish();*/

                        pickImageFromGallery(false);

                    } else if (selected == 3) {
                     /*   Intent _new_Intent = new Intent();
                        _new_Intent.putExtra("actions", "LoadWithoutTraceFromCamera");
                        setResult(Activity.RESULT_OK, _new_Intent);
                        finish();*/
                        captureImage();
                    }
                } else {
                    exitFromAPP();
                }
            }
        });
        btnNo.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
        });

        if (!exitDialog.isShowing()) {
            exitDialog.show();
        }

    }

    private boolean createPainting(int pInt1, int pInt2) {

        try {
            if (mPainting == null)
                return false;
            boolean bRet = mPainting.createCanvas(pInt1, pInt2);
            if (bRet) {
                /*if (shallRecover()) {
                    mPainting.setBackgroundBitmap(getRecovery());
                }*/
                mPainting.clearPainting();
            }
            mPainting.syncComposeCanvas();
            mPaintView.reDraw(null);

            return bRet;
        } catch (Exception lOutOfMemoryError) {
            quitByOutOfMemory();
            Log.e("TAG", "Exception at createpainting " + lOutOfMemoryError.getMessage());
            return false;
        }
    }


    private boolean createPaintingTemp(int pInt1, int pInt2) {
        Log.i(TAG, "painting size: " + pInt1 + "," + pInt2);
        try {
            if (mPaintingTemp == null)
                return false;

            boolean bRet = mPaintingTemp.createCanvas(pInt1, pInt2);
            if (bRet) {
                if (shallRecover()) {
                    mPaintingTemp.setBackgroundBitmap(getRecovery());
                }
                mPaintingTemp.clearPainting();
            }
            mPainting.syncComposeCanvas();
            mPaintView.reDraw(null);

            mPaintingTemp.setBackgroundBitmap(null);
            mPaintingTemp.clearPainting();

            if (mPaintingTemp != null) {
                mPaintingTemp.syncComposeCanvas();
                mPaintingTemp.syncUndoCanvas();
            }
            mPaintViewTemp.reDraw(null);

            mPaintingTemp.setBackgroundColor(R.color.white);
            return bRet;
        } catch (OutOfMemoryError lOutOfMemoryError) {
            quitByOutOfMemory();
            return false;
        }
    }

    private void dumpColor(int pInt) {
        int i = Color.red(pInt);
        int j = Color.blue(pInt);
        int k = Color.green(pInt);
        int m = Color.alpha(pInt);
        Log.i(TAG, "bk color " + i + ", " + k + ", " + j + ", " + m);
    }


    private float getDisplayScaleDensity() {
        DisplayMetrics lDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(lDisplayMetrics);
        return lDisplayMetrics.scaledDensity;
    }

    public void getScreenSize() {
        Display lDisplay = getWindowManager().getDefaultDisplay();
        mScreenWidth = lDisplay.getWidth();
        mScreenHeight = lDisplay.getHeight();

        DISPLAY_WIDTH = mScreenWidth;
        DISPLAY_HEIGHT = mScreenHeight;

        constants.putInt(constants._scree_height, DISPLAY_HEIGHT, PaintActivity.this);
        constants.putInt(constants._scree_width, DISPLAY_WIDTH, PaintActivity.this);

        Log.e(TAG, "w, h pixels " + mScreenWidth + " " + mScreenHeight);
        Log.e(TAG, "lDisplayMetrics.scaledDensity " + getDisplayScaleDensity());
        MyDbgLog(TAG, getDisplayMetrics(this));
    }

    private void hideMoviePlayerMenuBar() {
    }


    private void hideZoomButton() {
    }


    private boolean isSupportMultiTouch() {
        if (Build.VERSION.SDK_INT >= 5)
            return true;

        return false;
    }

    private void loadPainting(String pString) {
        Log.i(TAG, "load painting " + pString);
        mPainting.setBackgroundBitmap(null);
        mPainting.clearPainting();
//        if (mPainting != null) {
//            Log.e("TAGGG", "Load Image Logs loadPainting called mPainting != null");
//            mPainting.syncComposeCanvas();
//            mPainting.syncUndoCanvas();
//        } else
//            Log.e("TAGGG", "Load Image Logs loadPainting called go to else");
    }


    public void setBrushStyleOnPress() {

        try {
            if (m_viewColorContainer.getVisibility() == View.VISIBLE)
                mStatus = 8;
            mPrefBrushStyle = mBrushStyle;
            mPainting.setBrushStyle(mPrefBrushStyle);

            if (mPrefBrushStyle == 112) {
                mPrefBrushSize = mBrushSize;
                mPainting.setBrushSize(mPrefBrushSize);
            } else {
                mPrefBrushSize = mBrushSize;
                mPrefAlpha = mBrushAlpha;
                mPrefFlow = mBrushFlow;
                Log.e("TAGGG", "Color Detector on setBrushStyle mPrefBrushColor " + mPrefBrushColor + " mBrushColor " + mBrushColor + " Size " + mBrushSize + " mBrushSize " + mBrushSize + " Style " + mBrushStyle);
                mPainting.setBrushSize(mPrefBrushSize);
                mPainting.setAlpha(mPrefAlpha);
                mPainting.mBrushFlow = mPrefFlow;
            }
        } catch (Exception e) {

        }

        try {

            savePaintingPreference();

            if (tv_recording_time.getTag().equals(recordingState.In_Resume) && lSharedPreferences.getInt("brush-style", Brush.LineBrush) != 112) {

                EventModel model = new EventModel();
//                model.setColorchange(false);
                model.setEventType(EventType.BRUSH_CHANGE + "");
                model.setTimeStamp(second_indicator + "");

                if (lSharedPreferences.getString("pref-saved", null) != null) {

                    model.objChangeData.setBrushStyle(lSharedPreferences.getInt("brush-style", Brush.LineBrush) + "");


                    model.objChangeData.setBrushColor(mPrefBrushColor + "");

                    model.objChangeData.setBrushSize(lSharedPreferences.getFloat("brush-size", 8.0F) + "");

                    model.objChangeData.setBrushFlow(lSharedPreferences.getInt("brush-flow", 65) + "");

                    model.objChangeData.setBrushAlpha(lSharedPreferences.getInt("brush-alpha", 255) + "");

                    model.objChangeData.setBrushHardness(lSharedPreferences.getInt("brush-pressure", 65) + "");

                    model.getObjChangeData().setBrushName(getStyleName(lSharedPreferences.getInt("brush-style", Brush.LineBrush)));
                }
                addEventInList(model);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at paintor " + e.getMessage());
        }


        try {
//                tv_brush_percentage.setText(mTxtBrushName.getText().toString() + "\n" + mTxtSize.getText().toString());
            setSize();
        } catch (Exception e) {

        }

    }


    public void setBrushStyle(boolean bOK) {
//        m_brushlayout.setVisibility(View.INVISIBLE);
        brushSettingsPopup.dismiss();
        setBrushListToTop();
        if (m_viewColorContainer.getVisibility() == View.VISIBLE)
            mStatus = 8;
        else {
//            mStatus = 1;
        }

        if (bOK) {
            mPrefBrushStyle = mBrushStyle;
            mPainting.setBrushStyle(mPrefBrushStyle);

            if (mPrefBrushStyle == 112) {
                mPrefBrushSize = mBrushSize;
                mPainting.setBrushSize(mPrefBrushSize);
            } else {
                /**intor=====*/
                if (mPrefBrushSize != mBrushSize) {
                    processSteps = "Brush_clicked";
                }


                Log.e("TAGGG", "Color Tracking 676 " + mBrushColor);
//                mPrefBrushColor = mBrushColor;
                mPrefBrushSize = mBrushSize;
                mPrefAlpha = mBrushAlpha;
                mPrefFlow = mBrushFlow;
//                mPainting.setBrushColor(mPrefBrushColor);
                Log.e("TAGGG", "Color Detector on setBrushStyle mPrefBrushColor " + mPrefBrushColor + " mBrushColor " + mBrushColor + " Size " + mBrushSize + " mBrushSize " + mBrushSize + " Style " + mBrushStyle);
                mPainting.setBrushSize(mPrefBrushSize);
                mPainting.setAlpha(mPrefAlpha);
                mPainting.mBrushFlow = mPrefFlow;
            }

            savePaintingPreference();

            if (tv_recording_time.getTag().equals(recordingState.In_Resume) && lSharedPreferences.getInt("brush-style", Brush.LineBrush) != 112) {

                EventModel model = new EventModel();
//                model.setColorchange(false);
                model.setEventType(EventType.BRUSH_CHANGE + "");
                model.setTimeStamp(second_indicator + "");

                if (lSharedPreferences.getString("pref-saved", null) != null) {

                    model.objChangeData.setBrushStyle(lSharedPreferences.getInt("brush-style", Brush.LineBrush) + "");


                    model.objChangeData.setBrushColor(mPrefBrushColor + "");

                    model.objChangeData.setBrushSize(lSharedPreferences.getFloat("brush-size", 8.0F) + "");

                    model.objChangeData.setBrushFlow(lSharedPreferences.getInt("brush-flow", 65) + "");

                    model.objChangeData.setBrushAlpha(lSharedPreferences.getInt("brush-alpha", 255) + "");

                    model.objChangeData.setBrushHardness(lSharedPreferences.getInt("brush-pressure", 65) + "");

                    model.getObjChangeData().setBrushName(getStyleName(lSharedPreferences.getInt("brush-style", Brush.LineBrush)));
                }
                addEventInList(model);
            }
            if (_list_adapter != null)
                _list_adapter.addBrushInRecent(mBrushStyle);

            try {
//                tv_brush_percentage.setText(mTxtBrushName.getText().toString() + "\n" + mTxtSize.getText().toString());
                setSize();
            } catch (Exception e) {

            }
        }
    }


    private void pickBackGroundColor() {
    }

    private void promoteSketchMovie() {
        if (!mSketchMovieDownloaded)
            showDialog(6);
    }

    private void redo() {
        Rect lRect = mPainting.redoStroke();
        if (lRect != null) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, StringConstants.canvas_redo_stroke, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_redo_stroke, post_id);
            mPaintView.reDraw(lRect);
        }
    }

    public void restorePaintingPreference() {
        //SharedPreferences lSharedPreferences = getPreferences(0);

        SharedPreferences lSharedPreferences = getSharedPreferences("brush", 0);

        if (getIntent().hasExtra("mConfigData")) {

            try {
                String data = getIntent().getStringExtra("mConfigData");
                JSONObject object = new JSONObject(data);

                if (object.getBoolean("singleTap")) {
                    switch_singleTap.setChecked(true);
                }
                if (object.getBoolean("line")) {
                    _switch_line.setChecked(true);
                }
                if (object.getBoolean("gray_scale")) {
                    switch_gray_scale.setChecked(true);
                }

                if (object.getBoolean("block_coloring")) {
                    switch_block_coloring.setChecked(true);
                }

                mPrefBackgroundColor = object.getInt("background-color");
                mPrefBrushStyle = object.getInt("brush-style");
                mPrefBrushColor = object.getInt("brush-color");
                mPrefBrushSize = (float) object.getDouble("brush-size");
                mPrefBrushMode = object.getInt("brush-mode");
                mPrefAlpha = object.getInt("brush-alpha");
                mPrefAlpha = object.getInt("brush-pressure");
                mPrefFlow = object.getInt("brush-flow");
            } catch (Exception e) {
                e.printStackTrace();
                Gson _gson = new Gson();
                String json = constants.getString(constants.recent_Brush, mContext);
                if (!json.isEmpty()) {
                    Type type = new TypeToken<ArrayList<Integer>>() {
                    }.getType();
                    ArrayList<Integer> recentList = _gson.fromJson(json, type);
                    mPrefBrushStyle = recentList.get(0);
                }
                mPrefBrushColor = -65536;
                mPrefBrushSize = 35.0F;
                mPrefFlow = 255;
                mPrefAlpha = 255;
                PushPickcolor(-65536);
            }
        } else if (lSharedPreferences.getString("pref-saved", null) != null && lSharedPreferences.getString("pref-saved", "").equalsIgnoreCase("yes")) {
            mPrefBackgroundColor = lSharedPreferences.getInt("background-color", 0xFFFFFFFF);
            mPrefBrushStyle = lSharedPreferences.getInt("brush-style", Brush.LineBrush);
            mPrefBrushColor = lSharedPreferences.getInt("brush-color", -65536);
            mPrefBrushSize = lSharedPreferences.getFloat("brush-size", 35.0F);
            mPrefBrushMode = lSharedPreferences.getInt("brush-mode", 33);
            mPrefAlpha = lSharedPreferences.getInt("brush-alpha", 255);
            mPrefAlpha = lSharedPreferences.getInt("brush-pressure", 65);
            mPrefFlow = lSharedPreferences.getInt("brush-flow", 255);
        } else {
            Gson _gson = new Gson();
            String json = constants.getString(constants.recent_Brush, mContext);
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                ArrayList<Integer> recentList = _gson.fromJson(json, type);
                mPrefBrushStyle = recentList.get(0);
            }
            mPrefBrushColor = -65536;
//            mPrefBrushStyle = 576;
//            mPrefBrushSize = 9.0f;
            mPrefBrushSize = 35.0F;
            mPrefFlow = 255;
            mPrefAlpha = 255;
            PushPickcolor(-65536);
        }
        Gson _gson = new Gson();
        String json = constants.getString(constants.recent_Brush, mContext);
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            ArrayList<Integer> recentList = _gson.fromJson(json, type);
            setSelectedBrush(recentList.get(0));
            if (_list_adapter != null) {
                _list_adapter.setSelected(recentList.get(0));
                _list_adapter.addBrushInRecent(recentList.get(0));
            }
        } else {
            setSelectedBrush(mPrefBrushStyle);
            if (_list_adapter != null) {
                _list_adapter.setSelected(mPrefBrushStyle);
                _list_adapter.addBrushInRecent(mPrefBrushStyle);
            }
        }

        Log.e("TAGGG", "mPrefBackgroundColor From Selection In Painting " + mPrefBackgroundColor + " mPrefBrushStyle " + mPrefBrushStyle);
//        mPrefBackgroundColor = getResources().getColor(android.R.color.white);

        dumpColor(mPrefBackgroundColor);

        mPainting.setBackgroundColor(mPrefBackgroundColor);
        mPainting.setBrushStyle(mPrefBrushStyle);
        mPaintingTemp.setBackgroundColor(mPrefBackgroundColor);
        mPaintingTemp.setBrushStyle(mPrefBrushStyle);

        m_viewCurColor.setBackgroundColor(mPrefBrushColor);
        setHSVColor(mPrefBrushColor);
//        moveCursor();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            moveCursor();
        } else {
            moveCursorVeritcal();
        }

        if (mPrefBrushStyle == 112) {
            mPainting.setBrushColor(mPrefBackgroundColor);
        } else {
            mPainting.setBrushColor(mPrefBrushColor);
        }


        try {

            if (getIntent() != null && getIntent().hasExtra("drawingType") && getIntent().getStringExtra("drawingType").equalsIgnoreCase("TUTORAILS")) {
                if (lSharedPreferences.getString("pref-saved-tutorials", "").equalsIgnoreCase("yes")) {
                    SharedPreferences.Editor lEditor1 = getSharedPreferences("brush", 0).edit();
                    lEditor1.putString("pref-saved-tutorials", "");
                    lEditor1.commit();
                    float f1 = mBrush.mBrushMaxSize;
                    float f2 = mBrush.mBrushMinSize;
                    //mPrefBrushSize = (f1 * mPrefBrushSize) / 100;
                    mPrefBrushSize = (mPrefBrushSize * (f1 - f2) / 100) + f2;
                }
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        }


        // setSize();

        mBrushSize = mPrefBrushSize;
        mPainting.setBrushSize(mPrefBrushSize);
        mPainting.setBrushMode(mPrefBrushMode);
        mPainting.setAlpha(mPrefAlpha);
        mPainting.mBrushFlow = mPrefFlow;
    }

    private void savePaintingPreference() {
        // SharedPreferences.Editor lEditor1 = getPreferences(0).edit();
        SharedPreferences.Editor lEditor1 = getSharedPreferences("brush", 0).edit();

        lEditor1.putString("pref-saved", "yes");
        lEditor1.putInt("background-color", mPrefBackgroundColor);
        lEditor1.putInt("brush-style", mPrefBrushStyle);
        lEditor1.putFloat("brush-size", mPrefBrushSize);
        lEditor1.putInt("brush-color", mPrefBrushColor);
        lEditor1.putInt("brush-mode", mPrefBrushMode);
        lEditor1.putInt("brush-alpha", mPrefAlpha);
        lEditor1.putInt("brush-pressure", mPrefAlpha);
        lEditor1.putInt("brush-flow", mPrefFlow);
        lEditor1.commit();
    }

    private void saveState(Bundle pBundle) {
    }

    private void selectCanvasType() {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setTitle("New Canvas");
        lBuilder1.setMessage("Select canvas type to paint").setCancelable(true);

        lBuilder1.setNeutralButton("New Canvas", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                clearPainting(true);
                restartTimer();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.getPICK_NEW_CANVAS(), Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.getPICK_NEW_CANVAS());
            }
        });

        lBuilder1.setPositiveButton("Same Canvas", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                restartTimer();
                clearPaintingWithSameCanvas();
            }
        });

        lBuilder1.setNegativeButton("Photo", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                clearPainting(false);
            }
        });

        lBuilder1.create().show();
    }

    void restartTimer() {
        try {
            if (BuildConfig.DEBUG) {
                KGlobal.appendLog(this, "restartTimer");
            }
            Log.e("TAGG", "restartTimer called");
            handler.removeCallbacks(runnableCode);
            File file = new File(KGlobal.getMyPaintingFolderPath(PaintActivity.this) + "/temp.png");
            if (file.exists())
                file.delete();

            handler.postDelayed(runnableCode, SAVE_INTERVAL_TIME);
        } catch (Exception e) {

        }
    }

    void deleteTempFile() {
        try {

            handler.removeCallbacks(runnableCode);
            /*File file = new File(KGlobal.getMyPaintingFolderPath(this) + "/temp.png");
            if (file.exists())
                file.delete();*/
        } catch (Exception e) {

        }
    }

    private void sharePainting() {
        ImageManager lImageManager = mImageManager;
        lImageManager.setImageSavedListener(new ImageManager.OnImageSavedListener() {

            public void onImageSaved() {
                Uri lUri = mImageManager.getUri();

                Intent lIntent1 = new Intent();
                Intent lIntent2 = lIntent1.setAction("android.intent.action.SEND");
                String str3;

                if (mCompressionFormat == 1)
                    str3 = "image/jpeg";
                else
                    str3 = "image/png";

                Intent lIntent3 = lIntent1.setType(str3);
                Intent lIntent4 = lIntent1.putExtra("android.intent.extra.STREAM", lUri);
                Intent lIntent5 = lIntent1.putExtra("android.intent.extra.TEXT", "\n\nI made this painting with 'Paint Joy' on my Android phone:-)");
                Intent lIntent6 = Intent.createChooser(lIntent1, "Share Painting");
                startActivity(lIntent6);
                return;
            }
        });

        Bitmap lBitmap;

        if ((!isProVersion) && (!mHideAdsView)) {
            lBitmap = mPainting.getPainting();
        } else
            lBitmap = mPainting.getPainting();

        if (mCompressionFormat != 1)
            mImageManager.saveShareImageAsPng(lBitmap, String.valueOf(System.currentTimeMillis()) + ".png");
        else
            mImageManager.saveShareImageAsJpeg(lBitmap);

    }

    private void startBackgroundColorPicker() {
        /*Intent lIntent1 = new Intent();

        lIntent1.setClass(this, ColorPickerActivity.class);
        lIntent1.putExtra("for_brush", false);
        lIntent1.putExtra("current_color", mPrefBackgroundColor);

        startActivityForResult(lIntent1, SELECT_BACKGROUND_COLOR_REQUEST);*/

        reflectCanvas();
    }

    /*double newWidth;
    double newHeigth;*/


    LinearLayout.LayoutParams Params = null;


    boolean hasTraceImage = false;
    Boolean needToSetupBGColor = true;

    private void startPainting() {
        Intent lIntent = getIntent();
        String str1 = lIntent.getAction();

        String pickfromresult = constants.getString("pickfromresult", PaintActivity.this);
        Log.e("TAG", "canvas_mode pickfromresult " + pickfromresult);
        if (!str1.isEmpty())
            constants.putString("action_name", str1, PaintActivity.this);
        else if (!constants.getString("action_name", PaintActivity.this).isEmpty()) {
            str1 = constants.getString("action_name", PaintActivity.this);
            init_configure();
        } else if (!pickfromresult.isEmpty()) {
            setImageWhilePickFromResult();
            setBackgroundImageTouch();
            return;
        }
        Log.e("TAGGG", "canvas_mode Current Action 1023> " + str1);
        try {
            if (str1.equalsIgnoreCase("YOUTUBE_TUTORIAL_WITH_FILE")) {
                String youTubeLink = "";
                if (lIntent.hasExtra("youtube_video_id")) {
                    youTubeLink = lIntent.getStringExtra("youtube_video_id");
                    if (!youTubeLink.equalsIgnoreCase("")) {
                        youTubePlayerView = findViewById(R.id.youtube_player_view);

//                    initYouTubePlayerView(youTubeLink, false);
                        if (!videoPlayedOnce) {
                            youTubePlayerView.setVisibility(View.VISIBLE);
                            initYouTubePlayerView(youTubeLink, true);
                        } else {
                            showStrokeHintDialog();
                            hideCanvasHintDialog();
                            initYouTubePlayerView(youTubeLink, false);
                        }
                    }


                }

                if (lIntent.hasExtra("id")) {
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }
                }

                String eventFilePath = lIntent.getStringExtra("EventFilePath");
                String strokeFilePath = lIntent.getStringExtra("StrokeFilePath");
//                readFilesAndStartVideo(eventFilePath, strokeFilePath, "", true, true);

                mPainting.setBackgroundColor(mPrefBackgroundColor);
                mPaintView.invalidate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "Called PostDelayed");
                        if (lIntent.hasExtra("youtube_video_id") && !videoPlayedOnce) {
                            String youTubeLinkHere = lIntent.getStringExtra("youtube_video_id");
                            if (youTubeLinkHere.equalsIgnoreCase("")) {
                                readFilesAndStartVideo(eventFilePath, strokeFilePath, "", false, true);

                            } else {
                                readFilesAndStartVideo(eventFilePath, strokeFilePath, "", true, true);

                            }

                        } else {
                            readFilesAndStartVideo(eventFilePath, strokeFilePath, "", false, true);
                        }

                        Loadingdialog.DismissDialog();
//                        ll_rendering.setVisibility(View.INVISIBLE);
//

                    }
                }, 1000);

                FromTutorialMode = true;
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();

                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                mPaintView.reDraw(null);
                alphaValue = 5.0;
                if (!videoPlayedOnce) {
                    current_mode = canvas_mode.canvas_front;
                } else {
                    current_mode = canvas_mode.canvas_back;
                }
                Log.e("TAGGG", "canvas_mode Current 1> " + current_mode);
//                iv_toggle_preview.setImageResource(R.drawable.toggle_draw);
                ll_toggle.setVisibility(View.VISIBLE);
                seekbar_1.setVisibility(View.VISIBLE);
                seekBarContainer4.setVisibility(View.VISIBLE);
                hideShowSeekbarView(true);
            } else if (str1.equalsIgnoreCase("YOUTUBE_TUTORIAL_WITH_OVERLAID")) {
                String youTubeLink = "";
                if (lIntent.hasExtra("youtube_video_id")) {
                    youTubeLink = lIntent.getStringExtra("youtube_video_id");
                    youTubePlayerView = findViewById(R.id.youtube_player_view);

                    if (!videoPlayedOnce) {
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        initYouTubePlayerView(youTubeLink, false);
                    } else {
                        showStrokeHintDialog();
                        hideCanvasHintDialog();
                        initYouTubePlayerView(youTubeLink, false);
                    }
                }

                if (lIntent.hasExtra("id")) {
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }
                }

                String eventFilePath = lIntent.getStringExtra("EventFilePath");
                String strokeFilePath = lIntent.getStringExtra("StrokeFilePath");

                FromTutorialMode = true;
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();

                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                mPaintView.reDraw(null);
                if (!videoPlayedOnce) {
                    current_mode = canvas_mode.canvas_front;
                } else {
                    current_mode = canvas_mode.canvas_back;
                }
                Log.e("TAGGG", "canvas_mode Current 2> " + current_mode);
//                iv_toggle_preview.setImageResource(R.drawable.toggle_draw);
                ll_toggle.setVisibility(View.VISIBLE);
                seekbar_1.setVisibility(View.VISIBLE);
                seekBarContainer4.setVisibility(View.VISIBLE);
                hideShowSeekbarView(false);


                if (lIntent.hasExtra("OverlaidImagePath")) {
                    File file = new File(lIntent.getStringExtra("OverlaidImagePath"));
                    selectedImagePath = file.getName();
                    String parentFolder = file.getParentFile().getAbsolutePath();
                    Log.e("TAGG", "OverlaidImagePath selectedImagePath " + selectedImagePath + " parentFolder " + parentFolder);
                    mPainting.setBackgroundBitmap(null);
                    mPainting.clearPainting();
                    System.gc();
//                    mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);
                    loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
                    if (mPainting != null) {
                        mPainting.syncComposeCanvas();
                        mPainting.syncUndoCanvas();
                    }
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = getBitmap(selectedImagePath, BitmapFactory.decodeFile(selectedImagePath, bmOptions));
                    setupBitmap(bitmap);
                    iv_selected_image.setVisibility(View.GONE);
//                    isFromOverlaid = true;
                    mPaintView.reDraw(null);
                    seekbar_1.setVisibility(View.VISIBLE);
                    seekBarContainer4.setVisibility(View.VISIBLE);
                    hideShowSeekbarView(false);
                }
//                readFilesAndStartVideo(eventFilePath, strokeFilePath, "", true, false);

                mPainting.setBackgroundColor(mPrefBackgroundColor);
                mPaintView.invalidate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "Called PostDelayed");
                        readFilesAndStartVideo(eventFilePath, strokeFilePath, "", true, false);
//                        ll_rendering.setVisibility(View.INVISIBLE);
                        Loadingdialog.DismissDialog();
                    }
                }, 1000);

            } else if (str1.equalsIgnoreCase("YOUTUBE_TUTORIAL")) {

                if (lIntent.hasExtra("type")) {
                    String type = lIntent.getStringExtra("type");
                    if (type.equalsIgnoreCase("overlay")) {
                        Current_Mode = "LoadWithoutTrace";
                    } else if (type.equalsIgnoreCase("trace")) {
                        Current_Mode = "Edit Paint";
                    }
                }

                if (iv_switch_to_player != null) {
                    iv_switch_to_player.setVisibility(View.VISIBLE);
                }
                String youTubeLink = lIntent.getStringExtra("youtube_video_id");
                isFromEditImage = true;

                if (lIntent.hasExtra("id")) {
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }
                }

                if (lIntent.hasExtra("youtube_video_id")) {

                    youTubePlayerView = findViewById(R.id.youtube_player_view);

                    videoPlayedOnce = false;
                    if (!videoPlayedOnce) {
                        if (iv_switch_to_player != null) {
                            String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                            if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                                //                        showStrokeHintDialog();
                                showCanvasHintDialog();
                            }
                            iv_switch_to_player.setVisibility(View.VISIBLE);
                        }

                        youTubePlayerView.setVisibility(View.VISIBLE);
                        initYouTubePlayerView(youTubeLink, true);
                    } else {
                        showStrokeHintDialog();
                        hideCanvasHintDialog();
                        initYouTubePlayerView(youTubeLink, false);
                    }

                }

//                current_mode = canvas_mode.canvas_back;
                if (!videoPlayedOnce) {
                    current_mode = canvas_mode.canvas_front;
                } else {
                    current_mode = canvas_mode.canvas_back;
                }

                Log.e("TAGGG", "canvas_mode Current 3> " + current_mode);
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();

                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                mPaintView.reDraw(null);
                if (lIntent.hasExtra("paint_name")) {
                    hasTraceImage = true;
                    String trace_img_link = lIntent.getStringExtra("trace_link");
                    Log.e("TAGG", "Trace Links " + trace_img_link);
                    selectedImagePath = lIntent.getStringExtra("paint_name");
                    File file = new File(selectedImagePath);

                    backgroundImagePath = file.getAbsolutePath();

                    PaintingType = "save_trace_canvas_drawing";
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = getBitmap(file.getAbsolutePath(), BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions));
                    //Checked Bitmap
                    setupBitmap(bitmap);
                    iv_selected_image.setVisibility(View.VISIBLE);
                    Log.e("TAGGG", "Oncreate startPainting");
                    isFromEditImage = true;
                    alphaValue = 5.0;


                    if (lIntent.hasExtra("canvas_color")) {
                        mPainting.setBackgroundBitmap(null);
//                mPainting.setBackgroundColor(Color.TRANSPARENT);
                        Log.e("TAGG", "Selected Color mPrefBackgroundColor " + lIntent.getStringExtra("canvas_color"));
                        int color = -1;
                        try {
                            color = Color.parseColor(lIntent.getStringExtra("canvas_color"));
                        } catch (Exception e) {
                        }
                        mPainting.setBackgroundColor(color);
                        mPainting.clearPainting();
                        mPaintView.invalidate();
                        savePaintingPreference();
                    }
                    if (lIntent.hasExtra("swatches")) {
                        swatchesJson = lIntent.getStringExtra("swatches");
                        if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                            post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                        }
                        setupSwatches(swatchesJson);
                    }
                } else {
                    iv_start_recoring.setVisibility(View.GONE);
                    tv_recording_time.setVisibility(View.GONE);
//                    showInfoDialog();
                }
            } else if (str1.equalsIgnoreCase("FromTutorialMode")) {
                String eventFilePath = lIntent.getStringExtra("EventFilePath");
                String strokeFilePath = lIntent.getStringExtra("StrokeFilePath");
                String tutorialPath = lIntent.getStringExtra("TutorialPath");

                FromTutorialMode = true;
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();

                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                mPaintView.reDraw(null);
                /* Boolean needToSetupBGColor = true;*/
                if (lIntent.hasExtra("OverlaidImagePath")) {
                    File file = new File(lIntent.getStringExtra("OverlaidImagePath"));
                    selectedImagePath = file.getName();
                    String parentFolder = file.getParentFile().getAbsolutePath();
                    Log.e("TAGG", "OverlaidImagePath selectedImagePath " + selectedImagePath + " parentFolder " + parentFolder);
                    mPainting.setBackgroundBitmap(null);
                    mPainting.clearPainting();
                    System.gc();
                    mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);
                    if (mPainting != null) {
                        mPainting.syncComposeCanvas();
                        mPainting.syncUndoCanvas();
                    }
                    needToSetupBGColor = false;
//                    isFromOverlaid = true;
                    mPaintView.reDraw(null);
                    seekbar_1.setVisibility(View.VISIBLE);
                    seekBarContainer4.setVisibility(View.VISIBLE);
                    hideShowSeekbarView(false);

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(parentFolder + "/" + selectedImagePath, bmOptions);
                    Bitmap b1 = Bitmap.createScaledBitmap(bitmap, mScreenWidth, mScreenHeight, true);
                    iv_selected_image.setImageBitmap(getBitmap(parentFolder + "/" + selectedImagePath, b1));
                    iv_selected_image.setVisibility(View.GONE);
                }

                alphaValue = 5.0;
                current_mode = canvas_mode.canvas_front;
                Log.e("TAGGG", "canvas_mode Current 4> " + current_mode);
                iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
                ll_toggle.setVisibility(View.VISIBLE);
                seekbar_1.setVisibility(View.VISIBLE);
                seekBarContainer4.setVisibility(View.VISIBLE);
                hideShowSeekbarView(false);


                if (!Loadingdialog.IsDialogShowing()) {
                    Loadingdialog.ShowPleaseWaitDialog(getString(R.string.rendering));
                    progress.setVisibility(View.VISIBLE);

                }

//                if (ll_rendering.getVisibility() != View.VISIBLE) {
//                    ll_rendering.setVisibility(View.VISIBLE);
//                    progress.setVisibility(View.VISIBLE);
//                }

                mPainting.setBackgroundColor(mPrefBackgroundColor);
                mPaintView.invalidate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "Called PostDelayed");
                        readFilesAndStartVideo(eventFilePath, strokeFilePath, tutorialPath, false, needToSetupBGColor);
//                        ll_rendering.setVisibility(View.INVISIBLE);
                        Loadingdialog.DismissDialog();

                    }
                }, 1000);
//                readFilesAndStartVideo(eventFilePath, strokeFilePath, tutorialPath, false, needToSetupBGColor);

                if (lIntent.hasExtra("colorPalette")) {
                    colorPalette = lIntent.getStringExtra("colorPalette");
                    setupColorPalette(colorPalette);
                }


                if (lIntent.hasExtra("swatches")) {
                    swatchesJson = lIntent.getStringExtra("swatches");
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }
                    setupSwatches(swatchesJson);
                } else {

                    ExecutorService myExecutor = Executors.newSingleThreadExecutor();
                    Handler myHandler = new Handler(Looper.getMainLooper());

                    // Create an interface to respond with the result after processing
                    final OnProcessedListener listener = new OnProcessedListener() {
                        @Override
                        public void onProcessed(SavedTutorialEntity result) {
                            // Use the handler so we're not trying to update the UI from the bg thread
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Update the UI here
                                    // Do something in UI (front-end process)
                                    boolean finished = false;
                                    if (!TextUtils.isEmpty(swatchesJson)) {
                                        setupSwatches(swatchesJson);
                                        setupColorPalette(colorPalette);
                                        finished = true;
                                    } else {
                                        Toast.makeText(PaintActivity.this, "Swatches Missing", Toast.LENGTH_SHORT).show();
                                    }

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
                    };
                    myExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Do something in background (back-end process)
                            SavedTutorialDao colorSwatchDao = db.savedTutorialDao();
                            List<SavedTutorialEntity> allList = colorSwatchDao.getAll();
                            List<SavedTutorialEntity> list = colorSwatchDao.getSwatchByPath(selectedImagePath);

                            if (list.size() > 0) {
                                SavedTutorialEntity swatchEntity = list.get(0);
                                swatchesJson = swatchEntity.swatches;
                                colorPalette = swatchEntity.colorPalette;
                                post_id = swatchEntity.postId;

                                listener.onProcessed(swatchEntity);
                            }
                        }
                    });

                }

            } else if (str1.equalsIgnoreCase("LoadWithoutTrace")) {
                selectedImagePath = lIntent.getStringExtra("path");
                String parentFolder = lIntent.getStringExtra("ParentFolderPath");
                boolean isPaintIntro = lIntent.getBooleanExtra("isPaintIntro", false);
                if (isPaintIntro) { //It will be true if user come from IntroScreen
                    step = lIntent.getIntExtra("step", 0);
                    paintingIntro = true;
                    videoIntro();
                }
                /*if (!selectedImagePath.isEmpty()) {
                    constants.putString("path", selectedImagePath, Paintor.this);
                    constants.putString("parentFolder", parentFolder, Paintor.this);
                } else {
                    selectedImagePath = constants.getString("path", Paintor.this);
                    parentFolder = constants.getString("parentFolder", Paintor.this);
                }*/
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();
                System.gc();
                PaintingType = "save_overlay_canvas_drawing";
//                mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);
                Log.e("TAG", "Image Path at LoadWithoutTrace getBitmap " + (parentFolder + "/" + selectedImagePath));

                hideShowCross(false);
                loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Log.e("TAG", "Image Path at Trace getBitmap " + backgroundImagePath);
                Bitmap bitmap = getBitmap("/storage/emulated/0/Paintology/My Paintings/IMG-20200725-WA0009.png", BitmapFactory.decodeFile("/storage/emulated/0/Paintology/My Paintings/IMG-20200725-WA0009.png", bmOptions));
                //Checked
                setupBitmap(bitmap);*/

                iv_selected_image.setImageBitmap(mPainting.getBitmap());
                iv_selected_image.setVisibility(View.GONE);
                iv_selected_image.setAlpha(0.1f);

                mPrefBackgroundColor = -1;
                mPainting.setBackgroundColor(mPrefBackgroundColor);
                savePaintingPreference();
                mPaintView.reDraw(null);
                mPaintView.invalidate();
/*                if (lIntent.hasExtra("youtube_video_id")) {


                    String youTubeLink = lIntent.getStringExtra("youtube_video_id");
                    youTubePlayerView = findViewById(R.id.youtube_player_view);

                    iv_switch_to_player.setVisibility(View.VISIBLE);

                    Log.e("TAGGG", "canvas_mode Current 6> " + current_mode);
                    if (!videoPlayedOnce) {
                        ll_toggle.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.VISIBLE);
                        seekBarContainer4.setVisibility(View.VISIBLE);
                        hideShowSeekbarView(false);

                        youTubePlayerView.setVisibility(View.VISIBLE);
//                    current_mode = canvas_mode.canvas_back;
                        current_mode = canvas_mode.canvas_front;

                        String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                        if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
//                        showStrokeHintDialog();
                            showCanvasHintDialog();
                        }

                        initYouTubePlayerView(youTubeLink, true);
                    } else {
                        showStrokeHintDialog();
                        hideCanvasHintDialog();
                        current_mode = canvas_mode.canvas_back;
                        initYouTubePlayerView(youTubeLink, false);
                    }*/

                if (iv_switch_to_player != null) {
                    iv_switch_to_player.setVisibility(View.VISIBLE);
                }

                if (lIntent.hasExtra("youtube_video_id")) {
                    String youTubeLink = lIntent.getStringExtra("youtube_video_id");
                    youTubePlayerView = findViewById(R.id.youtube_player_view);

                    videoPlayedOnce = false;
                    if (!videoPlayedOnce) {
                        if (iv_switch_to_player != null) {
                            String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                            if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                                //                        showStrokeHintDialog();
                                showCanvasHintDialog();
                            }
                            iv_switch_to_player.setVisibility(View.VISIBLE);
                        }

                        youTubePlayerView.setVisibility(View.VISIBLE);
                        initYouTubePlayerView(youTubeLink, false);
                    } else {
                        showStrokeHintDialog();
                        hideCanvasHintDialog();
                        initYouTubePlayerView(youTubeLink, false);
                    }

                    if (!videoPlayedOnce) {
                        current_mode = canvas_mode.canvas_front;
                    } else {
                        current_mode = canvas_mode.canvas_back;
                    }

                } else {
                    ll_toggle.setVisibility(View.GONE);
                    seekbar_1.setVisibility(View.GONE);
                    seekBarContainer4.setVisibility(View.GONE);
                    hideShowSeekbarView(true);
                }

                ll_toggle.setVisibility(View.GONE);
                seekbar_1.setVisibility(View.GONE);
                seekBarContainer4.setVisibility(View.GONE);
                hideShowSeekbarView(true);

                selectedImagePath = parentFolder + "/" + selectedImagePath;

                /*ll_toggle.setVisibility(View.GONE);
                seekbar_1.setVisibility(View.GONE);
                seekBarContainer4.setVisibility(View.GONE);
                hideShowSeekbarView(true);
*/
                if (lIntent.hasExtra("isPickFromOverlaid")) {
                    isPickFromOverlaid = true;
                }

                if (lIntent.hasExtra("colorPalette")) {
                    colorPalette = lIntent.getStringExtra("colorPalette");
                    setupColorPalette(colorPalette);
                }

                if (lIntent.hasExtra("swatches")) {
                    swatchesJson = lIntent.getStringExtra("swatches");
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }
                    setupSwatches(swatchesJson);
                } else {


                    ExecutorService myExecutor = Executors.newSingleThreadExecutor();
                    Handler myHandler = new Handler(Looper.getMainLooper());

                    // Create an interface to respond with the result after processing
                    final OnProcessedListener listener = new OnProcessedListener() {
                        @Override
                        public void onProcessed(SavedTutorialEntity result) {
                            // Use the handler so we're not trying to update the UI from the bg thread
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Update the UI here
                                    // Do something in UI (front-end process)
                                    boolean finished = false;
                                    if (!TextUtils.isEmpty(swatchesJson)) {
                                        setupSwatches(swatchesJson);
                                        setupColorPalette(colorPalette);
                                        finished = true;
                                    } else {
                                        Toast.makeText(PaintActivity.this, "Swatches Missing", Toast.LENGTH_SHORT).show();
                                    }

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
                    };
                    myExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Do something in background (back-end process)
                            SavedTutorialDao colorSwatchDao = db.savedTutorialDao();
                            List<SavedTutorialEntity> allList = colorSwatchDao.getAll();
                            List<SavedTutorialEntity> list = colorSwatchDao.getSwatchByPath(selectedImagePath);

                            if (list.size() > 0) {
                                SavedTutorialEntity swatchEntity = list.get(0);
                                swatchesJson = swatchEntity.swatches;
                                colorPalette = swatchEntity.colorPalette;
                                post_id = swatchEntity.postId;

                                listener.onProcessed(swatchEntity);
                            }

                        }
                    });
                }

            } else if (str1.equalsIgnoreCase("DO_DRAWING_OVERLAY")) {
                selectedImagePath = lIntent.getStringExtra("path");
                String parentFolder = lIntent.getStringExtra("ParentFolderPath");
                selectedImagePath = parentFolder + "/" + selectedImagePath;
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();
                System.gc();
//                mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);
                loadPaintingFromFile(selectedImagePath);
                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }
                mPaintView.reDraw(null);

                ll_toggle.setVisibility(View.GONE);


                seekbar_1.setVisibility(View.GONE);
                seekBarContainer4.setVisibility(View.GONE);
                hideShowSeekbarView(true);
            } else if (str1.equalsIgnoreCase(RELOAD_PAINTING)) {
                try {
                    mPaintView.reDraw(null);
                    boolean isInTutorialMode = lIntent.getBooleanExtra("isTutorialmode", false);


                    if (isInTutorialMode) {

                        selectedImagePath = lIntent.getStringExtra("path");
                        File file = new File(lIntent.getStringExtra("path"));
                        backgroundImagePath = file.getAbsolutePath();

                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Log.e("TAG", "Image Path at Trace getBitmap " + backgroundImagePath);
                        Bitmap bitmap = getBitmap(backgroundImagePath, BitmapFactory.decodeFile(backgroundImagePath, bmOptions));
                        //Checked
                        setupBitmap(bitmap);
                        Log.e("TAGGG", "canvas_mode Current 211> " + current_mode);
                        iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
                        ll_toggle.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.VISIBLE);
                        seekBarContainer4.setVisibility(View.VISIBLE);
                        hideShowSeekbarView(false);
                        Log.e("TAGGG", "Oncreate startPainting");
                        isFromEditImage = true;
                        alphaValue = 5.0;
//                        bitmap.recycle();
                    } else {
                        if (BuildConfig.DEBUG)
                            Toast.makeText(getApplicationContext(), "Reload-el", Toast.LENGTH_SHORT).show();

                        ll_toggle.setVisibility(View.GONE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);
                    }


                    selectedImagePath = lIntent.getStringExtra("drawingPath");
                    String parentFolder = lIntent.getStringExtra("ParentFolderPath");
                    mPainting.setBackgroundBitmap(null);
                    mPainting.clearPainting();
//                    mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);
                    Log.e("TAG", "Image Path at load getBitmap " + (parentFolder + "/" + selectedImagePath));
                    loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
                    if (mPainting != null) {
                        mPainting.syncComposeCanvas();
                        mPainting.syncUndoCanvas();
                    }


                    try {

                        if (lIntent.hasExtra("isGrayScale")) {
                            if (lIntent.getBooleanExtra("isGrayScale", false)) {
                                Log.e("TAG", "Gray Scale True!");
                                defaultSwitchGrayScaleStatus = true;
                                switch_gray_scale.setChecked(true);
                            } else {
                                Log.e("TAG", "Gray Scale False!");
                            }
                        } else {
                            Log.e("TAG", "Gray Scale Not found!");
                        }

                    } catch (Exception e) {
                        Log.e("TAG", "Gray Scale Exception at Reload Painting " + e.toString());
                    }


                } catch (Exception e) {
                    Log.e("TAGG", "Exception while load image " + e.getMessage());
                }

            } else if (str1.equalsIgnoreCase(EDIT_PAINT)) {
                try {
                    PaintingType = "save_trace_canvas_drawing";
                    selectedImagePath = lIntent.getStringExtra("paint_name");
                    loadPainting(lIntent.getStringExtra("paint_name"));
                    if (!TextUtils.isEmpty(lIntent.getStringExtra("id"))) {
                        post_id = Integer.parseInt(lIntent.getStringExtra("id"));
                    }

                    ExecutorService myExecutor = Executors.newSingleThreadExecutor();
                    Handler myHandler = new Handler(Looper.getMainLooper());

                    // Create an interface to respond with the result after processing
                    final OnProcessedListener listener = new OnProcessedListener() {
                        @Override
                        public void onProcessed(SavedTutorialEntity result) {
                            // Use the handler so we're not trying to update the UI from the bg thread
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Update the UI here
                                    // Do something in UI (front-end process)
                                    boolean finished = false;
                                    if (!TextUtils.isEmpty(swatchesJson)) {
                                        setupSwatches(swatchesJson);
                                        setupColorPalette(colorPalette);
                                        finished = true;
                                    } else {
                                        Toast.makeText(PaintActivity.this, "Swatches Missing", Toast.LENGTH_SHORT).show();
                                    }

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
                    };
                    myExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Do something in background (back-end process)
                            SavedTutorialDao colorSwatchDao = db.savedTutorialDao();
                            List<SavedTutorialEntity> allList = colorSwatchDao.getAll();
                            List<SavedTutorialEntity> list = colorSwatchDao.getSwatchByPath(selectedImagePath);

                            if (list.size() > 0) {
                                SavedTutorialEntity swatchEntity = list.get(0);
                                swatchesJson = swatchEntity.swatches;
                                colorPalette = swatchEntity.colorPalette;
                                post_id = swatchEntity.postId;

                                listener.onProcessed(swatchEntity);
                            }
                        }
                    });
                    mPaintView.reDraw(null);
                    File file;
                    if (lIntent.hasExtra("FromLocal")) {
                        file = new File(selectedImagePath);
                    } else
                        file = new File(Environment.getExternalStorageDirectory(), dirName + selectedImagePath);

                    Log.e("TAGGG", "Oncreate startPainting file path " + file.getAbsolutePath());
                    backgroundImagePath = file.getAbsolutePath();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                    Bitmap bitmap = getBitmap(file.getAbsolutePath(), BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions));
                    //Checked Bitmap
                    setupBitmap(bitmap);
                    iv_selected_image.setVisibility(View.VISIBLE);

                    isFromEditImage = true;
                    alphaValue = 5.0;
                    Log.e("TAGGG", "canvas_mode Current 212> " + current_mode);
                    ll_toggle.setVisibility(View.VISIBLE);
                    seekbar_1.setVisibility(View.VISIBLE);
                    seekBarContainer4.setVisibility(View.VISIBLE);
                    hideShowSeekbarView(false);

                    if (lIntent.hasExtra("youtube_video_id")) {

                        String youTubeLink = lIntent.getStringExtra("youtube_video_id");

                        youTubePlayerView = findViewById(R.id.youtube_player_view);

                        videoPlayedOnce = false;
                        if (!videoPlayedOnce) {
                            if (iv_switch_to_player != null) {
                                String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                                if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                                    //                        showStrokeHintDialog();
                                    showCanvasHintDialog();
                                }
                                iv_switch_to_player.setVisibility(View.VISIBLE);
                            }

                            youTubePlayerView.setVisibility(View.VISIBLE);
                            initYouTubePlayerView(youTubeLink, false);
                        } else {
                            showStrokeHintDialog();
                            hideCanvasHintDialog();
                            initYouTubePlayerView(youTubeLink, false);
                        }

                        if (!videoPlayedOnce) {
                            current_mode = canvas_mode.canvas_front;
                        } else {
                            current_mode = canvas_mode.canvas_back;
                        }

                    } else {
                     /*   ll_toggle.setVisibility(View.GONE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);*/
                    }


                    if (lIntent.hasExtra("canvas_color")) {
                        mPainting.setBackgroundBitmap(null);
//                mPainting.setBackgroundColor(Color.TRANSPARENT);
                        Log.e("TAGG", "Selected Color mPrefBackgroundColor " + lIntent.getStringExtra("canvas_color"));
                        int color = -1;
                        try {
                            color = Color.parseColor(lIntent.getStringExtra("canvas_color"));
                        } catch (Exception e) {
                        }
                        mPainting.setBackgroundColor(color);
                        mPainting.clearPainting();
                        mPaintView.invalidate();
                        savePaintingPreference();
                    } else if (lIntent.hasExtra("SetBlackColor")) {
                        mPainting.setBackgroundBitmap(null);
                        Log.e("TAGG", "Selected Color mPrefBackgroundColor " + mPrefBackgroundColor);
                        if (mPrefBackgroundColor == 0)
                            mPrefBackgroundColor = -1;
                        mPainting.setBackgroundColor(-16777216);
                        mPainting.clearPainting();
                        mPaintView.invalidate();
                        savePaintingPreference();
                    } else {
                        mPainting.setBackgroundColor(-1);
                        mPainting.clearPainting();
                        mPaintView.reDraw(null);
                        mPaintView.invalidate();
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at set image " + e.getMessage() + " " + e.getStackTrace());
                }
            } else if (str1.equalsIgnoreCase(NEW_PAINT)) {
                try {
                    /*int deepColor = Color.parseColor("#00ff0000");
                    Log.e("TAG", "Paintor DeepColor " + deepColor);
                    mPainting.setBackgroundColor(deepColor);
                    mPainting.clearPainting();
                    mPaintView.invalidate();*/

                    Params = new LinearLayout.LayoutParams((int) mScreenWidth, (int) (mScreenHeight));
                    rl_canvas.setLayoutParams(Params);
                    mPaintView.reDraw(null);
                    ll_toggle.setVisibility(View.GONE);
                    seekbar_1.setVisibility(View.GONE);
                    seekBarContainer4.setVisibility(View.GONE);
                    hideShowSeekbarView(true);

                    PaintingType = "save_blank_canvas_drawing";
                    displayHintToggleBG(colorbar_bgcolor);
                    iv_selected_image.setImageBitmap(mPainting.getBitmap());
                    iv_selected_image.setAlpha(0.1f);


                    if (lIntent.hasExtra("youtube_video_id")) {

                        String youTubeLink = lIntent.getStringExtra("youtube_video_id");

                        youTubePlayerView = findViewById(R.id.youtube_player_view);

                        videoPlayedOnce = false;
                        if (!videoPlayedOnce) {
                            if (iv_switch_to_player != null) {
                                String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                                if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                                    //                        showStrokeHintDialog();
                                    showCanvasHintDialog();
                                }
                                iv_switch_to_player.setVisibility(View.VISIBLE);
                            }

                            youTubePlayerView.setVisibility(View.VISIBLE);
                            initYouTubePlayerView(youTubeLink, false);
                        } else {
                            showStrokeHintDialog();
                            hideCanvasHintDialog();
                            initYouTubePlayerView(youTubeLink, false);
                        }

                        if (!videoPlayedOnce) {
                            current_mode = canvas_mode.canvas_front;
                        } else {
                            current_mode = canvas_mode.canvas_back;
                        }

                    } else {
                        ll_toggle.setVisibility(View.GONE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);
                    }

                    ll_toggle.setVisibility(View.GONE);
                    seekbar_1.setVisibility(View.GONE);
                    seekBarContainer4.setVisibility(View.GONE);
                    hideShowSeekbarView(true);

                } catch (Exception e) {
                    Log.e("TAG", "Exception NEW PAINT " + e.getMessage());
                }
            } else if (str1.equalsIgnoreCase("LoadWithoutTraceFromCamera")) {
                selectedImagePath = lIntent.getStringExtra("path");
                String parentFolder = lIntent.getStringExtra("ParentFolderPath");
                mPainting.setBackgroundBitmap(null);
                mPainting.clearPainting();
                System.gc();
                PaintingType = "save_camera_canvas_drawing";
//                mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);

                loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
                if (mPainting != null) {
                    mPainting.syncComposeCanvas();
                    mPainting.syncUndoCanvas();
                }

                iv_selected_image.setImageBitmap(mPainting.getBitmap());
                iv_selected_image.setVisibility(View.VISIBLE);
                iv_selected_image.setAlpha(0.1f);

                mPrefBackgroundColor = -1;
                mPainting.setBackgroundColor(mPrefBackgroundColor);
                savePaintingPreference();

                mPaintView.reDraw(null);
                mPaintView.invalidate();
                if (lIntent.hasExtra("youtube_video_id")) {
                    String youTubeLink = lIntent.getStringExtra("youtube_video_id");
                    youTubePlayerView = findViewById(R.id.youtube_player_view);
                    youTubePlayerView.setVisibility(View.GONE);

                    Log.e("TAGGG", "canvas_mode Current 204> " + current_mode);
                    if (!videoPlayedOnce) {
                        ll_toggle.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.VISIBLE);
                        seekBarContainer4.setVisibility(View.VISIBLE);
                        hideShowSeekbarView(false);
                        current_mode = canvas_mode.canvas_back;
                        initYouTubePlayerView(youTubeLink, true);
                    } else {
                        showStrokeHintDialog();
                        hideCanvasHintDialog();
                        initYouTubePlayerView(youTubeLink, false);
                    }

                    current_mode = canvas_mode.canvas_back;

                } else {
                    ll_toggle.setVisibility(View.GONE);
                    seekbar_1.setVisibility(View.GONE);
                    seekBarContainer4.setVisibility(View.GONE);
                    hideShowSeekbarView(true);
                }
                selectedImagePath = parentFolder + "/" + selectedImagePath;
                seekbar_1.setVisibility(View.GONE);
                seekBarContainer4.setVisibility(View.GONE);
                hideShowSeekbarView(true);
                if (lIntent.hasExtra("isPickFromOverlaid")) {
                    isPickFromOverlaid = true;
                }
            } else {
                mPaintView.reDraw(null);
                ll_toggle.setVisibility(View.GONE);
                seekbar_1.setVisibility(View.GONE);
                seekBarContainer4.setVisibility(View.GONE);
                hideShowSeekbarView(true);
            }
            lIntent.setAction("");
            setBackgroundImageTouch();

            System.gc();
        } catch (OutOfMemoryError error) {
            quitByOutOfMemory();
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at start painting " + e.getMessage());
        }
    }

    private void setPickColorPalette(String colorPalette) {
        if (!TextUtils.isEmpty(colorPalette)) {
            String key = "top_pick_color";
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(key, ""); // Clear previous colors
            editor.putString(key, colorPalette); // Add color palette according selected image
            editor.apply();
        }
    }

    private void setupColorPalette(String colorPalette) {
        setPickColorPalette(colorPalette);
    }

    private void setupSwatches(String swatchesJson) {
        Gson gson = new Gson();

        List<ColorSwatch> swatches = gson.fromJson(swatchesJson, new TypeToken<List<ColorSwatch>>() {
        }.getType());

        FrameLayout hsv_swatches = findViewById(R.id.hsv_swatches);

        LinearLayout swatch_container = findViewById(R.id.swatch_container);

        if (swatches != null) {
            if (!swatches.isEmpty()) {
                hsv_swatches.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                layoutParams.leftMargin = 10;
                layoutParams.rightMargin = 10;
                layoutParams.topMargin = 5;
                layoutParams.bottomMargin = 5;

                View.OnClickListener mOnClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, "Color: " + view.getTag(), Toast.LENGTH_SHORT).show();
                        }

                        String viewTag = (String) view.getTag();
                        int color = Color.parseColor(viewTag);

                        setColorInBox(color);

                        mPrefBrushColor = color;
                        mBrushColor = color;
                        setHSVColor(color);
                        mPainting.setBrushColor(getColor());

                        if (viewTag.equals(paintByNumberIntroRequireColors[0])) {
                            processSteps = "Pbn_color_one_picked";
                        } else if (viewTag.equals(paintByNumberIntroRequireColors[1])) {
                            if (processSteps.equals("Pbn_color_one_chosen_and_drawn")) {
                                isPbnColorOneDrawn = true;
                                processSteps = "Pbn_color_two_picked";
                            }
                        } else {
                            if (processSteps.equals("Pbn_color_one_chosen_and_drawn")) {
                                isPbnColorOneDrawn = true;
                            }
                            processSteps = "Pbn_invalid_color_selected";
                        }
                    }
                };

                for (int i = 0; i < swatches.size(); i++) {

                    ColorSwatch swatch = swatches.get(i);

                    if (!TextUtils.isEmpty(swatch.getColor_swatch())) {
                        View view = getLayoutInflater().inflate(R.layout.view_swatch_item, null); //new View(mContext);

                        CardView swatch_card = view.findViewById(R.id.swatch_card);
                        TextView tv_position = view.findViewById(R.id.tv_position);
                        tv_position.setText(String.valueOf(i + 1));

                        view.setLayoutParams(layoutParams);
                        view.setTag(swatch.getColor_swatch()); // For retrieving colors when clicking or long-clicking.
                        view.setOnClickListener(mOnClickListener);
                        swatch_card.setCardBackgroundColor(Color.parseColor(swatch.getColor_swatch()));

                        swatch_container.addView(view);
                    }

                }

                swatch_container.postInvalidate();
                hsv_swatches.postInvalidate();

            } else {
                hsv_swatches.setVisibility(View.GONE);
            }
        } else {
            hsv_swatches.setVisibility(View.GONE);
        }
    }

    private void stopMyAdsThread() {
        if (mMyAdsThread == null)
            return;
        int i = 1;
        while (true) {
            if (i == 0) {
                mMyAdsThread = null;
                break;
            }
            try {
                mMyAdsThread.join();
                i = 0;
            } catch (InterruptedException lInterruptedException) {
            }
        }
    }


    /**
     * =========== Undo Method ================
     */
    private void undo() {
        Rect lRect = mPainting.undoStroke(PaintActivity.this);
        try {
            if (youTubePlayerView != null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_tsstrokes_undo_strokes, Toast.LENGTH_SHORT).show();
                }
                Log.e("TAGGG", "Undo Logs stroke size " + mPaintingTemp.mDeletedStrokes.size() + " " + mPaintingTemp.mCachedUndoStrokeList.size());
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_tsstrokes_undo_strokes, post_id);
            } else if (simpleExoPlayerView != null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_strokes_undo_strokes, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_strokes_undo_strokes, post_id);
            } else {
                // if no undo requred
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_undo_stroke, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_undo_stroke, post_id);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
        if (lRect != null) {
            mPaintView.reDraw(lRect);
        }
    }

    private void unhideAds() {
    }

    public void deleteRecovery() {
    }

    public String getDisplayMetrics(Context pContext) {
        DisplayMetrics lDisplayMetrics2 = pContext.getApplicationContext().getResources().getDisplayMetrics();
        mXDensity = lDisplayMetrics2.xdpi;
        mYDensity = lDisplayMetrics2.ydpi;
        mScaleDensity = lDisplayMetrics2.density;

        return "";
    }

    public Bitmap getRecovery() {
        if (mStrBackground != null) {
            try {
                return Gallery.getGalleryCropScaledPhoto(mStrBackground, mPainting.mPaintingWidth, mPainting.mPaintingHeight);
            } catch (OutOfMemoryError lOutOfMemoryError) {
                quitByOutOfMemory();
            } catch (NullPointerException lNullPointerException) {
            }
        }
        return null;
    }

    public void hideToolbars() {
        if (!TargetMarket.isForAmazon()) {
        }
    }

    public boolean inMovie() {
        if (mStatus == 2)
            return true;

        return false;
    }


    public int getMaxSize() {
        int screenWidth = constants.getInt(constants._scree_width, MyApplication.getInstance());
        int screenHeight = constants.getInt(constants._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;

        int mBrushMaxSize = 125;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 125;
            } else if (screenHeight > 1024 && screenHeight <= 2000) {
                mBrushMaxSize = 225;
            } else {
                mBrushMaxSize = 300;
            }
        } else {
            // code for landscape mode
            if (screenWidth <= 1024) {
                mBrushMaxSize = 125;
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 225;
            } else {
                mBrushMaxSize = 300;
            }
        }
        return mBrushMaxSize;
    }


    private String saveImageToStorage(Bitmap bitmap) {
        File directory = getApplicationContext().getFilesDir();
        File imagePath = new File(directory, "image.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePath.getAbsolutePath();
    }

    private static String getParentFolderPath(String imagePath) {
        File imageFile = new File(imagePath);
        File parentFolder = imageFile.getParentFile();
        if (parentFolder != null) {
            return parentFolder.getAbsolutePath();
        } else {
            return null; // Image path doesn't have a parent folder
        }
    }

    private static String getImageName(String imagePath) {
        File imageFile = new File(imagePath);
        return imageFile.getName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAGGG", "OnActivity Result Call PAINTOR > " + requestCode + " pInt2 " + resultCode);


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String imagePath = saveImageToStorage(photo);
            File _file = new File(imagePath);

            Log.e("HH=", "=" + imagePath);
            resetCanvas();
            restartTimer();
            if (CAMERA_OPERATION == 1) {
                Log.e("HH=", "F=" + _file);
                opneCameraPicInOverlay(_file);
            } else if (CAMERA_OPERATION == 2) {
                openCameraPicInTraceMode(_file);
            }
        }


        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: Permission Granted");
                AppUtils.setDataFromResultSS((Intent) data.clone());
                screenCapturePermission = true;
            } else {
                Log.d(TAG, "onActivityResult: Permission Deined");
                screenCapturePermission = false;
            }
        } else if (requestCode == PICK_IMAGE_CAMERA) {
//            Uri selectedImage = data.getData();
            if (CAMERA_OPERATION == 1) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.DIALOG_PICK_CAMERA_OVERLAY);

                try {

                    Uri savedUri = data.getData();
                    File destination = new File(savedUri.getPath());

                    opneCameraPicInOverlay(destination);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (CAMERA_OPERATION == 2) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.DIALOG_PICK_CAMERA_TRACE, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.DIALOG_PICK_CAMERA_TRACE);

                try {

                    Uri savedUri = data.getData();
                    File destination = new File(savedUri.getPath());

                    openCameraPicInTraceMode(destination);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (resultCode == RESULT_OK && requestCode == CREATE_PATTERN) {
            try {
                String path = data.getStringExtra("_pattern_image_path");
                String _brush_name = data.getStringExtra("_pattern_name");
                Log.e("TAG", "Pattern Image Path " + path);
                File f = new File(path);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                int patternNumber = 0;
                int num = constants.getInt(constants.last_patttern_number, PaintActivity.this);
                if (num == 0) {
                    patternNumber = 564;
                } else {
                    patternNumber = num + 1;
                }

                //Check id in existing list.

                for (int i = 0; i < mPatternManager.mPatternInfoList.size(); i++) {
                    if (patternNumber == mPatternManager.mPatternInfoList.get(i).style) {
                        patternNumber = patternNumber + 1;
                    }
                }
                Log.e("TAg", "mBrushStyleList patternNumber " + patternNumber);
                constants.putInt(constants.last_patttern_number, patternNumber, PaintActivity.this);

                PatternInfo customepattern = new PatternInfo(patternNumber, getMaxSize(), 1, 5, 5, _brush_name, false, 0, path, BrushType.CustomeBrush, null, true, 0);
                mBrushSortList.add(mBrushSortList.size() - 1);
                for (int i = 0; i < mBrushStyleList.size(); i++) {
                    if (mBrushStyleList.get(i) == 112) {
                        mBrushStyleList.add(i, patternNumber);
                        break;
                    }
                }

                notifyLists(customepattern);
                try {
                    Gson gson = new Gson();
                    String json = constants.getString(constants.manual_Brush, PaintActivity.this);
                    Type type = new TypeToken<ArrayList<PatternInfo>>() {
                    }.getType();
                    ArrayList<PatternInfo> customeList = gson.fromJson(json, type);
                    if (customeList == null) {
                        customeList = new ArrayList<>();
                    }
                    customeList.add(0, customepattern);
                    Log.e("TAG", "New Json From List" + gson.toJson(customeList));
                    constants.putString(constants.manual_Brush, gson.toJson(customeList), PaintActivity.this);

                    mPrefBrushStyle = customepattern.style;
                    savePaintingPreference();

                    mStatus = 9;
                    setBruchSetting(mPrefBrushStyle, mPrefBrushColor, mPrefBrushSize,
                            false, mPrefAlpha, mPrefFlow);

                    setBrushStyle(true);
                    setSelectedBrush(mPrefBrushStyle);
                    /*m_brushlayout.setVisibility(View.VISIBLE);
                    m_brushlayout.bringToFront();*/
//                    m_brushlayout.invalidate();

                } catch (Exception e) {
                    Log.e("TAG", "Exception at LN 1685 " + e.getMessage());
                }
                Toast.makeText(this, _brush_name + " Brush added successfully in existing list.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("TAG", "Exception at result " + e.getMessage());
            }
        } else if (requestCode == PERMISSION_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this,
                        "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
                iv_start_recoring.setEnabled(true);
                try {
                    if (!recordingOutputFilePath.isEmpty()) {
                        File file = new File(recordingOutputFilePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                } catch (Exception e) {
                }
                return;
            }

            try {

                startRecordingService();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                        mVirtualDisplay = createVirtualDisplay();

                        startCountDown();
                    }
                }, 2000);

            } catch (Exception e) {
                Log.e("TAG", "Exception at startCount " + e.getMessage());
            }
        } else if (requestCode == SELECT_PHOTO_REQUEST) {
            MyDbgLog(TAG, "get background from album ");

            if (resultCode == -1) {
                try {
                    if (data == null || data.getData() == null)
                        return;

                    Uri imageUri = data.getData();
                    String str14 = getPath(imageUri);
                    mStrBackground = str14;

                    if (mPainting.mPaintingWidth != 0) {
                        if (str14 == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PaintActivity.this);
                            builder.setTitle("Can't Load");
                            builder.setMessage("Selected image is not on your local storage, please download first to your local storage and then try.");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return;
                        }


                        if (imageUri != null) {
                            resetCanvas();
                            restartTimer();
//                            iv_selected_image.setImageURI(imageUri);
                            backgroundImagePath = str14;
                            selectedImagePath = str14;
                            if (iv_selected_image.getVisibility() != View.VISIBLE) {
                                iv_selected_image.setVisibility(View.VISIBLE);
                            }
                            mPainting.clearPainting();
                            mPaintView.reDraw(null);
                            deleteRecovery();
                            if (ll_toggle.getVisibility() != View.VISIBLE) {
                                ll_toggle.setVisibility(View.VISIBLE);
                                seekbar_1.setVisibility(View.VISIBLE);
                                seekBarContainer4.setVisibility(View.VISIBLE);
                                hideShowSeekbarView(false);
                            }
                            /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = getBitmap(selectedImagePath, BitmapFactory.decodeFile(selectedImagePath, bmOptions));
                            setupBitmap(bitmap);*/
                            isFromEditImage = true;
                            alphaValue = 5.0;
                            seekbar_1.setProgress(127);
                            iv_selected_image.setAlpha(0.5f);
                            iv_selected_image.invalidate();
                            switch_singleTap.setChecked(false);
                            _switch_line.setChecked(false);
                            defaultSwitchLineStatus = false;
                            defaultSwitchSingleTapStatus = false;
                            Current_Mode = "Edit Paint";
                            mNewCanvasBtn.setImageResource(R.drawable.trace_icon_white_canvas);
                            mNewCanvasBtn.setTag(2);
                            CurrentMode = 2;
                            current_mode = canvas_mode.canvas_back;

                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = getBitmap(selectedImagePath, BitmapFactory.decodeFile(selectedImagePath, bmOptions));
                            setupBitmap(bitmap);

                            Log.e("TAGGG", "canvas_mode Current 222> " + current_mode);
                            if (iv_start_recoring.getVisibility() != View.VISIBLE) {
                                iv_start_recoring.setVisibility(View.VISIBLE);
                            }

                            if (tv_recording_time.getVisibility() != View.VISIBLE) {
                                tv_recording_time.setVisibility(View.VISIBLE);
                            }

                            mPaintingTemp.setBackgroundBitmap(null);
                            mPaintingTemp.clearPainting();

                            if (mPaintingTemp != null) {
                                mPaintingTemp.syncComposeCanvas();
                                mPaintingTemp.syncUndoCanvas();
                            }
                            mPaintViewTemp.reDraw(null);

                            mPaintingTemp.setBackgroundColor(R.color.white);
                            mPaintViewTemp.setVisibility(View.GONE);

                            view_cross.setVisibility(View.GONE);
                            mPaintView.resetCanvas();
                            constants.putString("pickfromresult", "yes", PaintActivity.this);
                            constants.putString("isfromTrace", "yes", PaintActivity.this);
                            constants.putString("isfromoverlay", "", PaintActivity.this);
                            constants.putString("path", selectedImagePath, PaintActivity.this);
                            constants.putString("type", "", PaintActivity.this);
                            constants.putString("action_name", "", PaintActivity.this);
                            // new code
                            int orientation = this.getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            } else {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            }
                            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        }
                    }
                } catch (OutOfMemoryError lOutOfMemoryError) {
                    quitByOutOfMemory();
                } catch (NullPointerException lNullPointerException) {
                    Log.e("TAGGG", "lNullPointerException " + lNullPointerException.getMessage());
                }
            }
        } else if (requestCode == 120 && data != null && data.getData() != null) {

            try {
                view_cross.setVisibility(View.GONE);
                mPaintView.resetCanvas();
                resetCanvas();

                Uri imageUri = data.getData();
                String str14 = getPath(imageUri);

                File file = new File(str14);
                Current_Mode = "LoadWithoutTrace";
                mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
                mNewCanvasBtn.setTag(1);
                CurrentMode = 1;
                hideShowCross(false);

                if (iv_start_recoring.getVisibility() != View.VISIBLE) {
                    iv_start_recoring.setVisibility(View.VISIBLE);
                }
                if (tv_recording_time.getVisibility() != View.VISIBLE) {
                    tv_recording_time.setVisibility(View.VISIBLE);
                }
                isFromEditImage = false;
//                current_mode = canvas_mode.canvas_front;
                FromTutorialMode = false;
                switch_singleTap.setChecked(false);
                _switch_line.setChecked(false);
                defaultSwitchLineStatus = false;
                defaultSwitchSingleTapStatus = false;
                LoadWithoutTrace(file.getName(), file.getParentFile().getAbsolutePath(), true);

                iv_selected_image.setImageBitmap(mPainting.getBitmap());
                iv_selected_image.setVisibility(View.VISIBLE);
                iv_selected_image.setAlpha(0.1f);

            } catch (Exception e) {
                Log.e("TAG", "Exception at paintor " + e.getMessage());
            }
        } else if (requestCode == CAMERA_REQUEST) {
            try {

                if (output == null) {
                    return;
                }

//                File _file = new File(output);
                if (CAMERA_OPERATION == 1) {
                    opneCameraPicInOverlay(output);
                } else if (CAMERA_OPERATION == 2) {
                    openCameraPicInTraceMode(output);
                }

            } catch (Exception e) {
                Log.e("TAGG", "Exception at onResult " + e.getMessage());
            }
        } else if (requestCode == RC_SIGN_IN) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                account = task.getResult(ApiException.class);

                Log.e("TAGG", "signInResult Logged in success " + account.getDisplayName() + " " + account.getEmail() + " Id " + account.getId());
                constants.putString(constants.Username, (account.getDisplayName() != null ? account.getDisplayName() : ""), PaintActivity.this);
                constants.putString(constants.Password, (account.getId() != null ? account.getId() : ""), PaintActivity.this);
                constants.putString(constants.Email, (account.getEmail() != null ? account.getEmail() : ""), PaintActivity.this);
                LoginRequestModel model = new LoginRequestModel(
                        (account.getId() != null ? account.getId() : ""),
                        (account.getDisplayName() != null ? account.getDisplayName() : ""),
                        (account.getEmail() != null ? account.getEmail() : ""),
                        ""
                );
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.GoogleLoginSuccess, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess);
                addUser(model, LOGIN_FROM_GOOGLE);
            } catch (ApiException e) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.GoogleLoginFailed, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.GoogleLoginFailed);
                // The ApiException status code indicates the detailed failure reason.
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Button on click --------------------
     *
     * @param pView
     */
    public void onClick(View pView) {
//        Log.e("TAG", "OnClick Logs Current_Mode " + Current_Mode + " CurrentMode " + CurrentMode);
        // paint buttons
        if (pView != mPaintmenu_pen) {
            disableColorPickerMode();
            try {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    moveCursor();
                else
                    moveCursorVeritcal();
            } catch (Exception e) {
            }
        }
        if (pView != mBrushStyleBtn) {
//            m_brushlayout.setVisibility(View.INVISIBLE);
            brushSettingsPopup.dismiss();
        }
        hideHintDialog();
        hideCanvasHintDialog();
        hidePopupWindowIfOpen();
        if (pView == mNewCanvasBtn) {
            Log.e("HH=", "canvas");
            if (isCanvasTooltipShown) {
                isCanvasTooltipShown = false;
                mPopupWindow_canvas.dismiss();
            } else {
                if (mPopupWindow_canvas != null) {
                    isCanvasTooltipShown = false;
                    mPopupWindow_canvas.dismiss();
                    mPopupWindow_canvas = null;
                    return;
                }
                displayPopupWindowNewCanvas(mNewCanvasBtn);
            }
        } else if (pView == mBrushStyleBtn) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_select_brush, Toast.LENGTH_SHORT).show();
            }
            processSteps = "brush_clicked";
            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_select_brush);
            try {
                if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                    iv_gps_icon.setVisibility(View.GONE);
                    selected_bitmap = null;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
                }

                if (brushSettingsPopup.isShowing()) {
//                    m_brushlayout.setVisibility(View.INVISIBLE);
                    brushSettingsPopup.dismiss();

                    if (m_viewColorContainer.getVisibility() == View.VISIBLE)
                        mStatus = 8;
                    else {
//                    mStatus = 1;
                    }
                    if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
                        iv_cursor_icon.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (iv_cursor_icon.getVisibility() == View.VISIBLE) {
                        iv_cursor_icon.setVisibility(View.GONE);
                    }

                    if (mStatus != 1) {
                        hideZoomButton();
//                    mStatus = 1;
                        mPaintView.reDraw(null);
                    }
                    mStatus = 9;
                    setBruchSetting(mPrefBrushStyle, mPrefBrushColor, mPrefBrushSize,
                            false, mPrefAlpha, mPrefFlow);
//                    m_brushlayout.setVisibility(View.VISIBLE);
//                    m_brushlayout.bringToFront();
//                    brushSettingsPopup.showAsDropDown(pView);

                    checkCreateButton();
                    isClickedCancel = false;
                    isClickedCreateBrush = false;
                    if (autoColorPickerActivated) {
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                    }
                    switch_block_coloring.setChecked(autoColorPickerActivated);

                    if (getResources().getBoolean(R.bool.is_tablet)) {
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            Toast.makeText(this, "if 3155", Toast.LENGTH_SHORT).show();
                            brushSettingsPopup.showAsDropDown(pView, (brushSettingsPopup.getWidth() - 15), (brushSettingsPopup.getHeight() + 20));
                        } else {
                            Toast.makeText(this, "else 3160", Toast.LENGTH_SHORT).show();
                            brushSettingsPopup.showAsDropDown(paintmenu_close, (paintmenu_close.getWidth() - brushSettingsPopup.getWidth() + 20), -(paintmenu_close.getHeight() - brushSettingsPopup.getHeight() + (20)));

                        }

                    } else {
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            brushSettingsPopup.showAsDropDown(pView, (brushSettingsPopup.getWidth() - 30), (brushSettingsPopup.getHeight() + 25));
                            Toast.makeText(this, "3168", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "else 3171", Toast.LENGTH_SHORT).show();
                            brushSettingsPopup.showAsDropDown(paintmenu_close, (paintmenu_close.getWidth() - brushSettingsPopup.getWidth() + 20), -(paintmenu_close.getHeight() - brushSettingsPopup.getHeight() + 40));

                        }
                    }
                }
            } catch (Exception e) {
                brushSettingsPopup.showAsDropDown(paintmenu_close, (paintmenu_close.getWidth() - brushSettingsPopup.getWidth() + 20), -(paintmenu_close.getHeight() - brushSettingsPopup.getHeight() + (20)));

                Log.e("TAG", "Exception at set Brush " + e.getMessage());
            }
        } else if (pView == mUndoBtn) {
            undo();
        } else if (pView == mRedoBtn) {
            redo();
        } else if (pView == img_community) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_banner_ad_click, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_banner_ad_click);
            startActivity(new Intent(PaintActivity.this, Community.class));

        } else if (pView == mSavePicBtn) {
            if (issaveTooltipShown) {
                issaveTooltipShown = false;
                mPopupWindow_save.dismiss();
                if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                    mPopupWindow_post.dismiss();
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), StringConstants.canvas_save_press, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_save_press);

                displayPopupWindowSave(mSavePicBtn);
            }
        } else if (pView == mPaintmenu_pen) {
            /*TODO Change pickup color*/
            if (iv_cursor_icon.getVisibility() == View.VISIBLE) {
                iv_cursor_icon.setVisibility(View.GONE);
            }

//            if (mColorPopupWindow != null && mColorPopupWindow.isShowing()) {
//                mColorPopupWindow.dismiss();
//                iv_gps_icon.setVisibility(View.GONE);
//                selected_bitmap = null;
//                mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
////                mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
//                if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
//                    iv_cursor_icon.setVisibility(View.VISIBLE);
//                }
//                return;
//            }

            if (iv_gps_icon.getVisibility() != View.VISIBLE) {
                if (!isFromEditImage) {
//                    if (mColorPopupWindow != null) {
//                        mColorPopupWindow.dismiss();
//                        mColorPopupWindow = null;
//                        return;
//                    }
                    if (FromTutorialMode && (current_mode == canvas_mode.canvas_back)) {
//                        if (alphaValue == 0.0 || alphaValue == 10.0) {
                        displayPopupWindowTopPickColor(mPaintmenu_pen);
                        iv_gps_icon.setVisibility(View.VISIBLE);
//                        mPaintView.setOnDragListener(this);
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas_selected);
//                            mPaintmenu_pen.setImageResource(R.drawable.pen_icon_new);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.getPICK_COLOR_PICKER(), Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.getPICK_COLOR_PICKER());
//                        } else
//                            Toast.makeText(Paintor.this, "Not Allowed! Please Set Opacity to Low Or High.", Toast.LENGTH_LONG).show();
                    } else {
                        displayPopupWindowTopPickColor(mPaintmenu_pen);
                        iv_gps_icon.setVisibility(View.VISIBLE);
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas_selected);
//                        mPaintView.setOnDragListener(this);
//                        mPaintmenu_pen.setImageResource(R.drawable.pen_icon_new);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.getPICK_COLOR_PICKER(), Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.getPICK_COLOR_PICKER());

                    }
                } else {
//                    if (alphaValue == 0.0 || alphaValue == 10.0) {
                    displayPopupWindowTopPickColor(mPaintmenu_pen);
                    iv_gps_icon.setVisibility(View.VISIBLE);
//                    mPaintView.setOnDragListener(this);
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas_selected);
//                        mPaintmenu_pen.setImageResource(R.drawable.pen_icon_new);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.getPICK_COLOR_PICKER(), Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.getPICK_COLOR_PICKER());
//                    } else {
//                        if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
//                            iv_cursor_icon.setVisibility(View.VISIBLE);
//                        }
//                        Toast.makeText(Paintor.this, "Not Allowed! Please Set Opacity to Low Or High.", Toast.LENGTH_LONG).show();
//                    }
                }

                processSteps = "Color_picker_clicked";

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    mPaintView.cancelDragAndDrop();
                iv_gps_icon.setVisibility(View.GONE);
                selected_bitmap = null;
                mColorPopupWindow = null;
                if (autoColorPickerActivated) {
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                } else {
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
                }
//                mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
                if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
                    iv_cursor_icon.setVisibility(View.VISIBLE);
                }

                Log.e("TAG", "Hue Value on Click " + getHue());
                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    moveCursor();
                else {
                    moveCursorVeritcal();
                }
            }
/**            //Home button============================================*/
        } else if (pView == paintmenu_close && !getIntent().hasExtra("step")) {


            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_back_home, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_back_home);
            paintmenu_close.setTag(0);

            if (!mPainting.isEmpty() && !mPainting.isSaved())
                saveWorkBeforeContinuingDialog();
            else {
                Log.e("TAGG", "confirmExit called LN 1874");
                finish();
            }


        } else if (pView == iv_toggle_preview) {
            Log.e("TAG", "OnClick Called");
            changeToggle();
        } else if (pView == iv_start_recoring) {
//            FirebaseUtils.logEvents(Paintor.this, constants.canvas_record_click);
            try {
                if (!AppUtils.getPurchasedProducts().contains("record")) {
                    FireUtils.getStoreDetails(PaintActivity.this, "record", (productId, productName) -> {
                        FireUtils.showProgressDialog(PaintActivity.this, getResources().getString(R.string.please_wait));
                        FirebaseFirestoreApi.redeemProduct(productId)
                                .addOnCompleteListener(task -> {
                                    FireUtils.hideProgressDialog();
                                    if (task.isSuccessful()) {
                                        if (!productName.equalsIgnoreCase("")) {
                                            AppUtils.getPurchasedProducts().add(productName);
                                        } else {
                                            AppUtils.getPurchasedProducts().add("record");
                                        }
                                        iv_start_recoring.setAlpha(1f);
                                        tv_recording_time.setAlpha(1f);
                                        ContextKt.showToast(PaintActivity.this, "Redeem Success");
                                    } else {
                                        try {
                                            if (task.getException() != null) {
                                                if (task.getException().toString().contains("Insufficient points")) {
                                                    FireUtils.showStoreError(PaintActivity.this, "feature");
                                                } else {
                                                    ContextKt.showToast(PaintActivity.this, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                                }
                                                Log.e("TAGRR", task.getException().toString());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    });
                } else {
                    if (mPopupWindow != null) {
                        if (mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                            mPopupWindow = null;
                        } else {
                            mPopupWindow = null;
                            displayPopupWindowRecording(iv_start_recoring);
                        }

                    } else {
                        displayPopupWindowRecording(iv_start_recoring);
                    }
                }

            } catch (Exception e) {
                Log.e("TAG", "Exception at start record " + e.getMessage());
            }
        } else if (pView == tv_recording_time) {
            if (mMediaRecorder != null) {
                if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                    try {
                        mMediaRecorder.pause();
                    } catch (IllegalStateException e) {

                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.PAUSE_RECORDING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.PAUSE_RECORDING);
//                    iv_start_recoring.setImageResource(R.drawable.record_play);
                    tv_recording_time.setTag(recordingState.In_Pause);
                    isManuallyPauseRecording = true;
                    Toast.makeText(this, "Pause Recording", Toast.LENGTH_SHORT).show();
                } else if (tv_recording_time.getTag().equals(recordingState.In_Pause)) {
                    try {
                        mMediaRecorder.resume();
                    } catch (IllegalStateException e) {

                    }
                    isManuallyPauseRecording = false;
//                    iv_start_recoring.setImageResource(R.drawable.pause_recording);
                    tv_recording_time.setTag(recordingState.In_Resume);
                    Toast.makeText(this, "Resume Recording", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (pView == btn_next_stroke) {
            try {

                if (mPaintingTemp.mDeletedStrokes.size() > 0) {
                    Rect lRect = mPaintingTemp.redoStroke();
                    if (lRect != null) {
                        mPaintViewTemp.reDraw(lRect);
                    }
                    configureBrush(false, false);
                    currentStrokeIndex = currentStrokeIndex + 1;
                    Log.e("TAGGG", "currentStrokeIndex Logs 1940 " + currentStrokeIndex);
                    seekBar_timeLine.setProgress(seekBar_timeLine.getProgress() + 1);
                    reflectStroke();
                } else {
                    new drawSroke(false).execute();
                }
            } catch (Exception e) {
                Log.e("TAGG", "Exception at draw stroke " + e.getMessage());
            }
        } else if (pView == iv_play_pause) {
            hideHintDialog();
            hideCanvasHintDialog();
            if (iv_play_pause.getTag().equals("0")) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.resume_stroke_playing, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.resume_stroke_playing);
                if (currentStrokeIndex == strokeList.size())
                    return;

                iv_play_pause.setTag("1");
                iv_play_pause.setImageResource(R.drawable.pause_icon);
//                redoStrokes();

                try {
                    if (mPaintingTemp.mDeletedStrokes.size() > 0) {
                        redoStrokes(true);
                    } else
                        new drawSroke(true).execute();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception " + e.getMessage());
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.pause_stroke_playing, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.pause_stroke_playing);
                iv_play_pause.setTag("0");
                iv_play_pause.setImageResource(R.drawable.play_icon);
            }
        } else if (pView == iv_hide_exo_player) {
            changeToggle();
        } else if (pView == iv_switch_to_player) {
//            FirebaseUtils.logEvents(Paintor.this, constants.canvas_youtube_toggle);
            hideHintDialog();
            hideCanvasHintDialog();
            changeToggle();
        }
        /*else if (pView == ll_toggle) {
            traceBarTooltip(iv_toggle_preview);
        }*/
    }


    Handler handler_redo = new Handler();

    public void redoStrokes(Boolean drawOnTemp) {
        handler_redo.postDelayed(new Runnable() {
            @Override
            public void run() {
                int availableStrokes = 0;
                if (drawOnTemp) {
                    Rect lRect = mPaintingTemp.redoStroke();
                    if (lRect != null) {
                        mPaintViewTemp.reDraw(lRect);
                    }
                    reflectStroke();
                    availableStrokes = mPaintingTemp.mDeletedStrokes.size();
                } else {
                    availableStrokes = mPainting.mDeletedStrokes.size();
                    Rect lRect = mPainting.redoStroke();
                    if (lRect != null) {
                        mPaintView.reDraw(lRect);
                    }
                }
                seekBar_timeLine.setProgress(seekBar_timeLine.getProgress() + 1);
                currentStrokeIndex = currentStrokeIndex + 1;
                Log.e("TAGGG", "currentStrokeIndex Logs 2008 " + currentStrokeIndex);
                Log.e("TAGGG", "Seekbar Logs onStopTrackingTouch currentStrokeIndex after redo " + currentStrokeIndex);
                if (availableStrokes == 0) {
                    iv_play_pause.setImageResource(R.drawable.play_icon);
                    iv_play_pause.setTag("0");
                } else if (iv_play_pause.getTag().equals("0")) {
                    configureBrush(false, true);
                    handler_redo.removeCallbacks(this);
                } else
                    handler_redo.postDelayed(this, 250);
            }
        }, 500);
    }


    /**
     * ========================================================== Exit Dialog
     */
    private void confirmExitPainting() {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);

//        lBuilder1.setTitle("Exit Canvas");
        lBuilder1.setMessage("Save work before continuing?").setCancelable(true);

        lBuilder1.setPositiveButton("Save", (dialog, which) -> saveAndExitpainting());


        lBuilder1.setNeutralButton("DISCARD", (dialog, which) -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.getCLOSE_CANVAS(), Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.getCLOSE_CANVAS());
            mPrefBackgroundColor = -1;
            try {
                if (mMediaRecorder != null) {
//                        Toast.makeText(Paintor.this, "Stopping...", Toast.LENGTH_SHORT).show();
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.STOP_RECORDING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.STOP_RECORDING);
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    iv_start_recoring.setTag(0);
                    Log.v(TAG, "Recording Stopped");
                    stopScreenSharing();
                }
                Runtime.getRuntime().gc();
            } catch (RuntimeException e) {
            } catch (Exception e) {
            }

            if (selected_bitmap != null) {
                selected_bitmap.recycle();
                selected_bitmap = null;
            }

            if (selected_bitmap_gray_scale != null) {
                selected_bitmap_gray_scale.recycle();
                selected_bitmap_gray_scale = null;
            }

            deleteTempFile();
//                finish();
            exitFromAPP();
        });

        lBuilder1.setNegativeButton("Cancel", null);
        lBuilder1.create().show();

        saveWorkBeforeContinuingDialog();

    }

    private void saveWorkBeforeContinuingDialog() {
        final Dialog exitDialog = new Dialog(this, R.style.CustomDialog);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.dialog_save_work_before_countinuing);
        Button btnSave = exitDialog.findViewById(R.id.btnSave);
        Button btnDiscard = exitDialog.findViewById(R.id.btnDiscard);
        ImageView imgCross = exitDialog.findViewById(R.id.imgCross);

        imgCross.setOnClickListener(view -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
        });
        btnSave.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
                saveAndExitpainting();
            }
        });
        btnDiscard.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.getCLOSE_CANVAS(), Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.getCLOSE_CANVAS());
                mPrefBackgroundColor = -1;
                try {
                    if (mMediaRecorder != null) {
//                        Toast.makeText(Paintor.this, "Stopping...", Toast.LENGTH_SHORT).show();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.STOP_RECORDING, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.STOP_RECORDING);
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        iv_start_recoring.setTag(0);
                        Log.v(TAG, "Recording Stopped");
                        stopScreenSharing();
                    }
                    Runtime.getRuntime().gc();
                } catch (RuntimeException e) {
                } catch (Exception e) {
                }

                if (selected_bitmap != null) {
                    selected_bitmap.recycle();
                    selected_bitmap = null;
                }

                if (selected_bitmap_gray_scale != null) {
                    selected_bitmap_gray_scale.recycle();
                    selected_bitmap_gray_scale = null;
                }

                deleteTempFile();

                exitFromAPP();
            }
        });

        if (!exitDialog.isShowing()) {
            exitDialog.show();
        }
    }

    public void saveAndExitpainting() {
        String drawingType1 = getIntent().getStringExtra("drawingType");

        if (drawingType1 == null) {
            drawingType1 = "freehand";
        }

        savePaintingPreference();
        Log.e("drawingType", "saveAndExitpainting: " + drawingType1);

        if (!mPainting.isEmpty() || mPainting.getBackgroundColor() != -1 || !mPainting.isEmpty() || isFromEditImage || isPickFromOverlaid) {
            if (System.currentTimeMillis() - mTimeOfPreviousSave > 3000L) {
                mTimeOfPreviousSave = System.currentTimeMillis();
                if (isProVersion) {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, true, PaintActivity.this,
                            true, -1, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                } else {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, true, PaintActivity.this,
                            true, -1, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                }
            }
        }
    }

    boolean isXLargeScreen = false;
    int _width, _height;

    String Current_Mode = "";
    SharedPreferences lSharedPreferences;

    Toast _toast;

    SwitchCompat switch_gray_scale;
    SwitchCompat switch_block_coloring;

    LinearLayout ll_brush;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;
    SharedPreferences painterDbPref;
    SharedPreferences.Editor painterDbEditor;
    String drawingType1 = "";


    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(Bundle pBundle) {
        super.onCreate(pBundle);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseUtils.resetHashMap();

        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);

        mActivity = this;
        Loadingdialog = new LoadingDialog(this);

        getScreenSize();
        _toast = Toast.makeText(PaintActivity.this, "Pick colors by moving slider bars to top (image) or bottom (canvas)", Toast.LENGTH_LONG);
        orientation = getResources().getConfiguration().orientation;
        isTablet = getResources().getBoolean(R.bool.isTablet);
        //lSharedPreferences = getPreferences(0);
        lSharedPreferences = getSharedPreferences("brush", 0);
        obj_interface = this;
        float ratio_phone, ratio_small, ratio_large;

        db = MyApplication.getDb();

        mPrefs = getSharedPreferences(StringConstants.PREF, Context.MODE_PRIVATE);
        editor = mPrefs.edit();

        painterDbPref = getSharedPreferences("PaintologyDB", Context.MODE_PRIVATE);
        painterDbEditor = painterDbPref.edit();

        videoPlayedOnce = mPrefs.getBoolean("videoPlayedOnce", false);

        // landscape mode ( _width > _height )
        Display display = getWindowManager().getDefaultDisplay();

        _width = display.getWidth();
        _height = display.getHeight();

        ratio_phone = 1.0F * _width / _height;
        ratio_small = 1.0F * 800 / 480;
        ratio_large = 1.0F * 1024 / 800;

        if (Math.abs(ratio_phone - ratio_small) < Math.abs(ratio_phone - ratio_large)) {
            _screenScaleX = 1.0F * _width / 800;
            _screenScaleY = 1.0F * _height / 480;
        } else {
            isXLargeScreen = true;
            _screenScaleX = 1.0F * _width / 1024;
            _screenScaleY = 1.0F * _height / 800;
        }
        try {
            sharedPref = new SharedPref(this);
            setContentView(R.layout.main_lite_pad);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setContent " + e.getMessage());
        }


        if (getIntent().hasExtra("isFromGallery")) {
            isFromGallery = getIntent().getBooleanExtra("isFromGallery", false);
        }
        if (getIntent().hasExtra("id")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
                post_id = Integer.parseInt(getIntent().getStringExtra("id"));
            }
        }

        if (getIntent().hasExtra("youtube_video_id")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("youtube_video_id"))) {
                youtube_video_id = getIntent().getStringExtra("youtube_video_id");
            }
        }


        drawingType1 = getIntent().getStringExtra("drawingType");

        if (drawingType1 == null) {
            drawingType1 = "freehand";
        }

        if (drawingType1.equalsIgnoreCase("TUTORAILS") && post_id != -1) {
            FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.scribble_on_your_canvas, String.valueOf(post_id));
        }

        try {
            banner = findViewById(R.id.banner);
            ll_brush = findViewById(R.id.ll_brush);
            rl_gray_scale = findViewById(R.id.rl_gray_scale);
            iv_gray_scale_image = findViewById(R.id.iv_grayscale);
            iv_gray_scale_indicator = findViewById(R.id.view_gray_scale_indicator);
            iv_gray_scale_image.setOnTouchListener(_touch_listener);

            m_viewColorPanel = findViewById(R.id.colorbar_colorpanel);

            FrameLayout fm_color_bar = findViewById(R.id.fm_color_bar);
            ViewTreeObserver vto_rl = fm_color_bar.getViewTreeObserver();
            vto_rl.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    fm_color_bar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    Log.e("TAG", "FrameLayout height on tree Liste " + fm_color_bar.getMeasuredWidth() + " Heig " + iv_gray_scale_image.getMeasuredHeight());
                    Bitmap temp = ((BitmapDrawable) iv_gray_scale_image.getDrawable()).getBitmap();

                    Log.e("TAG", "width: " + temp.getWidth() + "height: " + temp.getHeight());

                    if (temp.getWidth() <= 0 || temp.getHeight() <= 0) {
                        return;
                    }

                    if (fm_color_bar.getMeasuredWidth() <= 0 || fm_color_bar.getMeasuredHeight() <= 0) {
                        return;
                    }

                    Log.e("TAG", "fm_width: " + fm_color_bar.getMeasuredWidth() + "fm_height: " + fm_color_bar.getMeasuredHeight());

                    selected_bitmap_gray_scale = Bitmap.createScaledBitmap(temp, fm_color_bar.getMeasuredWidth(), fm_color_bar.getMeasuredHeight(), true);

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        bottomlimit = fm_color_bar.getMeasuredHeight() - 7;
                    } else {
                        bottomlimit = fm_color_bar.getMeasuredWidth() - 10;
                    }


                    int width = selected_bitmap_gray_scale.getWidth();
                    int height = selected_bitmap_gray_scale.getHeight();

                    Runnable _runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < height; i++) {
                                    for (int j = 0; j < width; j++) {
                                        //scan through each pixel
                                        int pixel = selected_bitmap_gray_scale.getPixel(j, i);
                                        int redValue = Color.red(pixel);
                                        int greenValue = Color.green(pixel);
                                        int blueValue = Color.blue(pixel);
                                        int alphaValue = Color.alpha(pixel);
                                        int colorValue = Color.argb(alphaValue, redValue, greenValue, blueValue);
//                                        Log.e("TAG", "Color Value of All Image colorValue " + colorValue + " X " + i + "  Y " + j);
                                        boolean isAded = false;
                                        for (int k = 0; k < _lst_colors_gray_scale.size(); k++) {
                                            if (colorValue == _lst_colors_gray_scale.get(k).getColorCode()) {
                                                isAded = true;
                                                break;
                                            }
                                        }
                                        if (!isAded) {
                                            axisAnDColor _object = new axisAnDColor(colorValue, j, i);
                                            _lst_colors_gray_scale.add(_object);
                                        }
                                    }
                                }

                                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    _lst_colors_gray_scale.remove(_lst_colors_gray_scale.size() - 1);
                                    _lst_colors_gray_scale.remove(_lst_colors_gray_scale.size() - 1);
                                } else {
                                    _lst_colors_gray_scale.remove(0);
                                    _lst_colors_gray_scale.remove(0);
                                }

                                try {
                                    axisAnDColor _object = getAxisFromColor(_lst_colors_gray_scale.get(_lst_colors_gray_scale.size() / 2).getColorCode());
                                    setIndicatorPos(_object);
//                                currentGrayScaleColor = _lst_colors_gray_scale.get(0).getColorCode();
                                    currentGrayScaleColor = _object.getColorCode();
                                    Log.e("TAG", "Color List Size " + _lst_colors_gray_scale.size() + " last x " + _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getX() + " last y " + _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getY());
                                } catch (Exception e) {
                                    Log.e("TAG", "Exception " + e.getMessage());
                                }
                            } catch (Exception e) {
                                Log.e("TAG", "Exception at getColor " + e.getMessage());
                            }
                        }
                    };
                    new Handler().postDelayed(_runnable, 1000);
                }
            });

            tv_special_fun = findViewById(R.id.tv_special_fun);
            tv_special_fun.setVisibility(View.GONE);

            tv_brush_percentage = findViewById(R.id.tv_brush_percentage);
            relative_parent = findViewById(R.id.relative_parent);

            iv_plus_zoom = findViewById(R.id.iv_plus_zoom);

            rl_canvas = findViewById(R.id.rl_canvas);
            iv_arrow = findViewById(R.id.iv_arrow);
            iv_arrow_canvas = findViewById(R.id.iv_arrow_canvas);
            iv_arrow_stroke = findViewById(R.id.iv_arrow_stroke);
            frm_hint = findViewById(R.id.frm_hint);
            frm_hint_canvas = findViewById(R.id.frm_hint_canvas);
            frm_hint_stroke = findViewById(R.id.frm_hint_stroke);
            linear_background_color = findViewById(R.id.linear_background_color);
            tv_cancel = findViewById(R.id.tv_cancel);
            tv_ok = findViewById(R.id.tv_ok);
            tv_zoom_per = findViewById(R.id.tv_zoom_per);

            linear_background_color.setOnClickListener(view -> {
            });
            tv_ok.setOnClickListener(view -> {
                linear_background_color.setVisibility(View.GONE);

                if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                    try {
                        m_viewColorContainer.setVisibility(View.INVISIBLE);

                        linear_background_color.setVisibility(View.GONE);
                        colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                        m_viewColorContainer.invalidate();
                        viewContainer.invalidate();
                        Integer oldColor = mPrefBrushColor;

                        if (brushSettingsPopup.isShowing())
                            returnWithSelectedBrush();

                        Log.e("TAGGG", "Color Tracking 2282 " + mBrushColor);

                        mPrefBrushColor = mPrefBackgroundColor;
//                            setColor();
                        Integer newColor = getColor();

//                            bg_color_temp = mPrefBackgroundColor;
                        mPrefBackgroundColor = newColor;

                        setHSVColor(newColor);
                        viewSatVal.setHue(getHue());
                        mPainting.setBrushColor(getColor());

                        if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                            addColorChangeEvent();
                        }
                        Log.e("TAGGG", "Tracking oldColor " + oldColor + "  newColor " + newColor + " OldSize " + mPrefBrushSize + " New Size " + mBrushSize + " BrushName " + mPrefBrushStyle + " " + mBrushStyle + " mPrefFlow " + mPrefFlow + " mBrushFlow " + mBrushFlow + " mPrefAlpha " + mPrefAlpha + " mBrushAlpha " + mBrushAlpha);
                        savePaintingPreference();
//                            view_cross.setVisibility(View.VISIBLE);
                        Log.e("TAGGG", "m_viewColorPanel ACTION_MOVE mPrefBackgroundColor OK " + mPrefBackgroundColor);

                        isBGSelected = false;
                        mPrefBrushColor = oldColor;
                        mPainting.setBrushColor(mPrefBrushColor);
                        m_viewCurColor.setBackgroundColor(mPrefBrushColor);
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception while hide color pallete");
                    }
                }
            });
            tv_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

//                m_viewColorContainer.setVisibility(View.INVISIBLE);
                    linear_background_color.setVisibility(View.GONE);
                    colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
//                m_viewColorContainer.invalidate();
                    viewContainer.invalidate();

                    mPainting.setBackgroundColor(bg_color_temp);
                    mPainting.clearAndUpdateBGPainting();
                    mPaintView.invalidate();

                    mPrefBackgroundColor = bg_color_temp;
                    Integer newColor1 = mPrefBrushColor;
                    setColorInBox(newColor1);
                    mBrushColor = newColor1;
                    setHSVColor(newColor1);
                    viewSatVal.setHue(getHue());
                    mPainting.setBrushColor(getColor());
                    moveCursorVeritcal();
                    savePaintingPreference();

                    linear_background_color.setVisibility(View.GONE);
                    colorbar_bgcolor.setBackgroundColor(mPrefBackgroundColor);

                    mPaintView.reDraw(null);
                    mPaintView.invalidate();
                    if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                        try {
                            m_viewColorContainer.setVisibility(View.INVISIBLE);

                            linear_background_color.setVisibility(View.GONE);
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                            m_viewColorContainer.invalidate();
                            viewContainer.invalidate();
                            Integer oldColor = mPrefBrushColor;

                            if (brushSettingsPopup.isShowing())
                                returnWithSelectedBrush();

                            Log.e("TAGGG", "Color Tracking 2282 " + mBrushColor);
                            setColor();
                            Integer newColor = getColor();
                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                addColorChangeEvent();
                            }
                            Log.e("TAGGG", "Tracking oldColor " + oldColor + "  newColor " + newColor + " OldSize " + mPrefBrushSize + " New Size " + mBrushSize + " BrushName " + mPrefBrushStyle + " " + mBrushStyle + " mPrefFlow " + mPrefFlow + " mBrushFlow " + mBrushFlow + " mPrefAlpha " + mPrefAlpha + " mBrushAlpha " + mBrushAlpha);
                            savePaintingPreference();

                            isBGSelected = false;

                        } catch (Exception e) {
                            Log.e("TAGG", "Exception while hide color pallete");
                        }
                    }
                }
            });

            ll_left_container = (LinearLayout) findViewById(R.id.ll_left_container);
            ll_left_container.setOnClickListener(this);
            //Social media login.
            facebook_login_btn = (LoginButton) findViewById(R.id.login_button_dashboard);
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

            mGoogleSignInClient = GoogleSignIn.getClient(PaintActivity.this, gso);

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(this);
            LoginInPaintology = constants.getString(constants.LoginInPaintology, this);


            windowwidth = getWindowManager().getDefaultDisplay().getWidth();
            windowheight = getWindowManager().getDefaultDisplay().getHeight();

            mPaintView = (PaintView) findViewById(R.id.my_canvas);

//            mPaintView.setOnDragListener(new View.OnDragListener() {
//                @Override
//                public boolean onDrag(View v, DragEvent event) {
//                    Log.e("TAG", "onDrag Called in paintview " + event.getAction());
//                    return true;
//                }
//            });

            mPaintingTemp = new PaintingTemp(this);
            mPaintingTemp.setDensity(mXDensity, mYDensity);
            mPaintViewTemp = (PaintViewTemp) findViewById(R.id.my_canvas_temp);
            mPaintViewTemp.mDensity = mScaleDensity;
            mPaintViewTemp.setPainting(mPaintingTemp);
            mPaintViewTemp.setTag(0);
            progress = (ProgressBar) findViewById(R.id.progress_indicator);
            progress.setVisibility(View.INVISIBLE);

            ll_rendering = (LinearLayout) findViewById(R.id.ll_rendering);
            ll_rendering.setVisibility(View.INVISIBLE);
            Loadingdialog.DismissDialog();

            iv_hide_exo_player = (ImageView) findViewById(R.id.iv_hide_player);
            iv_hide_exo_player.setOnClickListener(this);
            iv_hide_exo_player.setVisibility(View.GONE);
            trace_bar_container = findViewById(R.id.trace_bar_container); // for landscape mode
            seekbar_1 = findViewById(R.id.seekbar_1);
            seekBarContainer4 = findViewById(R.id.seekBarContainer4);
//            view1 = (View) findViewById(R.id.view_1);
//            view2 = (View) findViewById(R.id.view_2);
            view_trace_left = (View) findViewById(R.id.view_trace_left);
            view_trace_right = (View) findViewById(R.id.view_trace_right);
            view_mid = (View) findViewById(R.id.view_mid);
            ll_toggle = findViewById(R.id.ll_toggle);
            ll_toggle.setOnClickListener(this);

            mPainting = new Painting(this);

            mPainting.setDensity(mXDensity, mYDensity);
            mPaintView.mDensity = mScaleDensity;

            mPaintView.setPainting(mPainting);
            mPaintView.setOnKeyListener(this);


            // paint buttons
            paintmenu_close = findViewById(R.id.paintmenu_close);
            paintmenu_close.setOnClickListener(this);

            colorlayout_2 = findViewById(R.id.colorlayout_2);
            view_cross = findViewById(R.id.view_cross);
            view_cross.setVisibility(View.GONE);

            view_cross2 = findViewById(R.id.view_cross2);
            view_cross2.setVisibility(View.GONE);

            view_zoom_indicator = findViewById(R.id.view_zoom_indicator);
            view_zoom_indicator.setVisibility(View.GONE);

            mNewCanvasBtn = findViewById(R.id.paintmenu_newcanvas);
            mNewCanvasBtn.setOnClickListener(this);

            mBrushStyleBtn = findViewById(R.id.paintmenu_brush);
            mBrushStyleBtn.setOnClickListener(this);

            ll_brush.setOnClickListener(view -> mBrushStyleBtn.performClick());


            mUndoBtn = findViewById(R.id.paintmenu_undo);
            mUndoBtn.setOnClickListener(this);
            mRedoBtn = findViewById(R.id.paintmenu_redo);
            mRedoBtn.setOnClickListener(this);
            mZoomBtn = findViewById(R.id.paintmenu_zoom);

            iv_canvas_lock = findViewById(R.id.iv_canvas_lock);
            iv_canvas_lock.setVisibility(View.GONE);

            FrameLayout fl_zoom = findViewById(R.id.fl_zoom);

            fl_zoom.setOnClickListener(v -> {

                if (mPaintView.currentScale() <= 10) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PaintActivity.this);
                    builder1.setTitle(getString(R.string.zoom_help_title))
                            .setMessage(getString(R.string.pinch_zoom_msg))
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }

                Log.e("TAG", "Onclick called ");
                if (System.currentTimeMillis() - doubleClickLastTime < 500) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_zoom_menuitem_dbl_tap, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_zoom_menuitem_dbl_tap);
                    doubleClickLastTime = 0;
                    if (_handler != null) {
                        _handler.removeCallbacks(_runnable);
                    }
                    Log.e("TAG", "Doublbe clicks ");
                    if (mStatus != status_block) {
                        iv_canvas_lock.setVisibility(View.VISIBLE);
                        mZoomBtn.setVisibility(View.INVISIBLE);
                        Toast.makeText(PaintActivity.this, "Zoom locked, press again to unlock.", Toast.LENGTH_SHORT).show();
                        mStatus = status_block;
                    }
                    return;
                } else {
                    doubleClickLastTime = System.currentTimeMillis();
                }

                _handler = new Handler();
                _runnable = new Runnable() {
                    @Override
                    public void run() {
//                            Toast.makeText(Paintor.this, getResources().getString(R.string.zoom_msg), Toast.LENGTH_LONG).show();

                        if (mStatus != status_block) {
                            if (mPaintView.currentScale() > 10) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(PaintActivity.this, constants.canvas_zoom_menuitem_reset_click, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_zoom_menuitem_reset_click);
                                mPaintView.resetCanvas();
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(PaintActivity.this, constants.canvas_zoom_menuitem_click, Toast.LENGTH_SHORT).show();
                                }
                                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_zoom_menuitem_click);
                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(PaintActivity.this, constants.canvas_zoom_unlock, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_zoom_unlock);
                            iv_canvas_lock.setVisibility(View.INVISIBLE);
                            mZoomBtn.setVisibility(View.VISIBLE);
//                                Toast.makeText(Paintor.this, "canvas unlock", Toast.LENGTH_SHORT).show();
                            mStatus = 3;
                        }
                    }
                };
                _handler.postDelayed(_runnable, 500);

            });

            fl_zoom.setOnLongClickListener(v -> {
                if (mStatus != status_block) {
                    isLongPressed = true;
                    mPaintView.isStopAutoZoom = false;
                    mPaintView.startAutoZoom();
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_zoom_menuitem_long_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_zoom_menuitem_long_click);
                }
                return true;
            });

            fl_zoom.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("TAG", "F1 zoom Action " + event.getAction());
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // We're only interested in anything if our speak button is currently pressed.
                        if (isLongPressed) {
                            // Do something when the button is released.
                            isLongPressed = false;
                            mPaintView.stopAutoZoom();
                        }
                    }
                    return false;
                }
            });

            mSavePicBtn = findViewById(R.id.paintmenu_savepic);
            mSavePicBtn.setOnClickListener(this);

            mPaintmenu_pen = findViewById(R.id.iv_color_picker);
            mPaintmenu_pen.setOnClickListener(this);
            mPaintmenu_pen.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (autoColorPickerActivated) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, "activate the auto picker!", Toast.LENGTH_SHORT).show();

                            Toast.makeText(PaintActivity.this, constants.auto_color_picker_deactivated, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_deactivated);
                        autoColorPickerActivated = false;
                        pickNewColorMode = false;
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

                        iv_gps_icon.setVisibility(View.GONE);
                        iv_cursor_icon.setVisibility(View.VISIBLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            mPaintView.cancelDragAndDrop();

                        selected_bitmap = null;
                        mColorPopupWindow = null;

                        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                            moveCursor();
                        else {
                            moveCursorVeritcal();
                        }

                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.auto_color_picker_activated, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_activated);
                        autoColorPickerActivated = true;
                        pickNewColorMode = true;
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                        iv_cursor_icon.setVisibility(View.GONE);
                        iv_gps_icon.setVisibility(View.VISIBLE);
                    }

                    return true;
                }
            });

            iv_toggle_preview = findViewById(R.id.iv_toggle_preview);
            iv_toggle_preview.setOnClickListener(this);

            iv_switch_to_player = findViewById(R.id.iv_switch_to_player);
            if (iv_switch_to_player != null) {
                iv_switch_to_player.setOnClickListener(this);
                iv_switch_to_player.setVisibility(View.GONE);
            }

            iv_selected_image = findViewById(R.id.iv_selected_image);
            iv_selected_image.setVisibility(View.VISIBLE);

            iv_play_pause = findViewById(R.id.iv_play_pause);
            iv_play_pause.setOnClickListener(this);
            iv_play_pause.setVisibility(View.GONE);

            iv_play_pause.setImageResource(R.drawable.play_icon);
            iv_play_pause.setTag("0");

            iv_temp_traced = findViewById(R.id.iv_temp_traced);
            iv_temp_traced.setVisibility(View.GONE);

            iv_gps_icon = findViewById(R.id.iv_gps_icon);
            iv_gps_icon.setVisibility(View.GONE);
            mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

//            iv_gps_icon.setOnLongClickListener(this);

            iv_gps_icon.setTag(IMAGE_VIEW_TAG);

            iv_cursor_icon = findViewById(R.id.iv_pencil_icon);

            iv_cursor_icon.bringToFront();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mPaintView.cancelDragAndDrop();

            // color bar
            setupColorBar();
            setupBrushColor();

            onInit();

            viewContainer = findViewById(R.id.paintor_containter);

            ((ViewGroup.MarginLayoutParams) (m_viewColorContainer.getLayoutParams())).leftMargin = ((ViewGroup.MarginLayoutParams) (mPaintView.getLayoutParams())).leftMargin;

//            ((LinearLayout) findViewById(R.id.LayoutOKCancel)).setGravity(Gravity.TOP);    // center_horizontal | center_vertical

            mImageManager = new ImageManager(PaintActivity.this, KGlobal.getMyPaintingFolderPath(PaintActivity.this));

            if (pBundle != null) {
                String str = TAG;
                MyDbgLog(str, "try to restore status");
            }

            if (!isProVersion) {
                SharedPreferences lSharedPreferences = getPreferences(0);
                mPlayNumbers = lSharedPreferences.getInt("play_number", 0);
                mPlayNumbers = mPlayNumbers + 1;
                mDontShwoGarden1Promote = lSharedPreferences.getBoolean("dont_show_garden2_ads", false);

                if (!mDontShwoGarden1Promote)
                    mDontShowSketchMoviePromote = lSharedPreferences.getBoolean("dont_show_sketchmovie_ads", false);

                mSketchMovieDownloaded = lSharedPreferences.getBoolean("promote_sketchmovie_downloaded", false);

                if ((!mDontShowSketchMoviePromote) && (!mSketchMovieDownloaded) && (mPlayNumbers >= 3))
                    promoteSketchMovie();

                mHandler = new Paintor6();

                mHandler.postDelayed(mCreatePaintingRunnable, 500L);
                /*if (mScreenWidth > mScreenHeight) {
                }*/
            }

            mSPenEventLibrary = new SPenEventLibrary();

            btn_next_stroke = findViewById(R.id.btn_next_stroke);
            btn_next_stroke.setOnClickListener(this);

            tv_startTime = findViewById(R.id.tv_start_time);
            tv_endTime = findViewById(R.id.tv_end_time);
            seekBar_timeLine = findViewById(R.id.seekbar_timeLine);
            seekBar_timeLine.setProgress(0);

            ll_bottom_bar = findViewById(R.id.ll_bottom_bar);
            ll_bottom_bar.setVisibility(View.GONE);


            mPaintView.setOnClickListener(v -> {
            });
            mPaintView.setOnTouchListener((view, event) -> {
                if (paintingIntro) {
                    Log.d("SwatchColor", "onTouchFinger: " + event.getAction());
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (processSteps.equals("Pbn_color_one_picked")) {
                            processSteps = "Pbn_color_one_chosen_and_drawn";
                        } else if (processSteps.equals("Pbn_color_two_picked")) {
                            processSteps = "Pbn_color_two_chosen_and_drawn";
                        }
                        drawing_activities();
                        if (processSteps.equals("Color_picker_clicked"))
                            processSteps = "Color_chosen_and_drawn";
                    }
                } else {
                    Log.d("SwatchColor", "onTouchFinger: Else" + event.getAction());
                }

                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if (autoColorPickerActivated) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        pickNewColorMode = true;
                        if (event.getAction() == MotionEvent.ACTION_UP && view_cross.getVisibility() != View.VISIBLE) {
                            view_cross.setVisibility(View.VISIBLE);
                        }


                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        gpsIconRelatedWork(event, x, y, view);
                        iv_gps_icon.setVisibility(View.GONE);
                        pickNewColorMode = false;
                    }
                }

                // Old way of coloring
                try {
//                        Log.e("TAG", "onTouchFinger called " + event.getAction());
                    if (event.getAction() == MotionEvent.ACTION_UP && view_cross.getVisibility() != View.VISIBLE) {
                        view_cross.setVisibility(View.VISIBLE);
                    }
                    hideHintDialog();
                    hideCanvasHintDialog();
                    hidePopupWindowIfOpen();
                    issaveTooltipShown = false;
                    isCanvasTooltipShown = false;
                    if (linear_background_color.getVisibility() == View.VISIBLE)
                        return true;

//                            final int x = (int) event.getX();
//                            final int y = (int) event.getY();

                    if (iv_gps_icon != null && iv_gps_icon.getVisibility() == View.VISIBLE) {
                        gpsIconRelatedWork(event, x, y, view);
                    } else {
                        if (isInZoomMode == false && iv_cursor_icon.getVisibility() != View.VISIBLE) {
                            iv_cursor_icon.setVisibility(View.VISIBLE);
                        }
                        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_cursor_icon.getLayoutParams();
                        LayoutParams.leftMargin = (int) ((mPaintView.getLeft() + x - Math.floor(iv_cursor_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft()) + dpToPx(0));
                        LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_cursor_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
                        iv_cursor_icon.bringToFront();
                        iv_cursor_icon.setLayoutParams(LayoutParams);
                    }
                    if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                        try {
                            m_viewColorContainer.setVisibility(View.INVISIBLE);
                            linear_background_color.setVisibility(View.GONE);
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                            m_viewColorContainer.invalidate();
                            viewContainer.invalidate();
                            Integer oldColor = mPrefBrushColor;

                            if (brushSettingsPopup.isShowing())
                                returnWithSelectedBrush();

                            Log.e("TAGGG", "Color Tracking 2282 " + mBrushColor);
                            setColor();
                            Integer newColor = getColor();
                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                addColorChangeEvent();
                            }

                            /*if (!oldColor.equals(newColor)) {
                                Log.e("TAGG", "New Color Added " + newColor);
                            }*/
                            setColorInBox(newColor);
                            Log.e("TAGGG", "Tracking oldColor " + oldColor + "  newColor " + newColor + " OldSize " + mPrefBrushSize + " New Size " + mBrushSize + " BrushName " + mPrefBrushStyle + " " + mBrushStyle + " mPrefFlow " + mPrefFlow + " mBrushFlow " + mBrushFlow + " mPrefAlpha " + mPrefAlpha + " mBrushAlpha " + mBrushAlpha);
                            savePaintingPreference();
                        } catch (Exception e) {
                            Log.e("TAGG", "Exception while hide color pallete");
                        }
                    }

                    mPrefBrushSize = mBrushSize;
//                        mPrefBrushSize = getsize();

                    mPainting.setBrushSize(mPrefBrushSize);
//                        mPainting.setBrushSize(mPrefBrushSize);
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while Pick Color 1 " + e.getMessage());
                }
                return view.onTouchEvent(event);
            });

            mPaintView.invalidate();

            seekbar_1.setProgress(153);


            seekbar_1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    try {
                        if (progress > 0) {
                            alphaValue = ((double) progress / 255) * 10;
                            Log.e("TAG", "Alfa Value set " + ((float) progress / 255));
                            iv_selected_image.setAlpha((float) progress / 255);
                            iv_temp_traced.setAlpha((float) progress / 255);
                            mPainting.backgroundPaint.setAlpha((int) progress / 255);
                        } else {
                            alphaValue = 0.0;
                            iv_selected_image.setAlpha(0.0f);
                            iv_temp_traced.setAlpha(0.0f);
                            mPainting.backgroundPaint.setAlpha(0);
                        }

                        if (!isBGsetup) {
//                            view1.setBackground(getResources().getDrawable(R.drawable.bkg_b_w));
//                            view2.setBackground(getResources().getDrawable(R.drawable.bkg_b_w));
                            isBGsetup = true;
                            Log.e("TAG", "Current Progress Goto set color");
                        }

                        if (progress == 0) {
                            isBGsetup = false;
//                            view1.setBackground(getResources().getDrawable(R.drawable.bkg_yellow));
                        } else if (progress == 255) {
                            isBGsetup = false;
//                            view2.setBackground(getResources().getDrawable(R.drawable.bkg_yellow));
                        }
                        Log.e("TAG", "Current Progress " + progress);

                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at seekbar change " + e.getMessage());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    startingProgress = seekBar.getProgress();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        if (youTubePlayerView != null) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(PaintActivity.this, constants.canvas_Tstrokes_trace_slider_function, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_Tstrokes_trace_slider_function);
                        } else if (simpleExoPlayerView != null) {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(PaintActivity.this, constants.canvas_strokes_trace_slider_function, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_strokes_trace_slider_function);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(PaintActivity.this, constants.canvas_trace_slider_function, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_trace_slider_function);
                        }

                        traceSeekbarOldProgress = seekbar_1.getProgress();

                        traceLeftClicked = false;
                        traceRightClicked = false;

                    } catch (Exception e) {
                        Log.e("TAGG", "Exception " + e.getMessage());
                    }

                    if (selected_bitmap != null) {
                        selected_bitmap = null;
                    }
                    //removed *&& alphaValue != 0.0 && alphaValue != 10.0* from condition

                    if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                        if (startingProgress > 127 && seekBar.getProgress() < 127) {
                            disableColorPickerMode();
                        } else if (startingProgress < 127 && seekBar.getProgress() > 127) {
                            disableColorPickerMode();
                        }

                    }
                }
            });

            view_trace_left.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (traceLeftClicked) {
                        traceLeftClicked = false;
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.draw_trace_left_toggle2, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.draw_trace_left_toggle2);
                        seekbar_1.setProgress(traceSeekbarOldProgress);
                        Log.d("TAG", "left-undo");
                    } else {
                        traceLeftClicked = true;
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.draw_trace_left_toggle1, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.draw_trace_left_toggle1);
                        traceSeekbarOldProgress = seekbar_1.getProgress();
                        seekbar_1.setProgress(0);

                        Log.d("TAG", "left-redo");
                    }


                    Log.d("TAG", "left");


                }
            });

            view_trace_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (traceRightClicked) {
                        traceRightClicked = false;
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.draw_trace_right_toggle2, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.draw_trace_right_toggle2);
                        seekbar_1.setProgress(traceSeekbarOldProgress);
                    } else {
                        traceRightClicked = true;
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.draw_trace_right_toggle1, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.draw_trace_right_toggle1);
                        traceSeekbarOldProgress = seekbar_1.getProgress();
                        seekbar_1.setProgress(255);
                    }
                }
            });


            if (getIntent().hasExtra("background_color")) {
                mPrefBackgroundColor = getIntent().getIntExtra("background_color", 0);
                dumpColor(mPrefBackgroundColor);
                mPainting.setBackgroundColor(mPrefBackgroundColor);
                colorbar_bgcolor.setBackgroundColor(mPrefBackgroundColor);
                Log.e("TAGGG", "BackGround Color at oncreate 2446 " + mPrefBackgroundColor);
            }


            mPaintingTemp.setBackgroundColor(-1);
            mPaintingTemp.setBackgroundBitmap(null);
            mPaintingTemp.clearPainting();

            if (mPaintingTemp != null) {
                mPaintingTemp.syncComposeCanvas();
                mPaintingTemp.syncUndoCanvas();
            }
            mPaintViewTemp.reDraw(null);

            if (mPainting != null)
                mPainting.syncComposeCanvas();

            if (mPaintingTemp != null)
                mPaintingTemp.syncComposeCanvas();


            iv_start_recoring = findViewById(R.id.iv_start_recoring);
            iv_start_recoring.setOnClickListener(this);
            iv_start_recoring.setTag(0);


            tv_recording_time = (TextView) findViewById(R.id.tv_recording_time);

            tv_recording_time.setTag(recordingState.In_Idle);
            tv_recording_time.setVisibility(View.VISIBLE);
            tv_recording_time.setText(getString(R.string.record));
            iv_start_recoring.setImageResource(R.drawable.recording_icon_canvas);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = metrics.densityDpi;

            seekBar_timeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    try {
                        tv_startTime.setText(convertToTime(Integer.parseInt(strokeList.get(seekBar.getProgress()).getTimeStamp())));
                    } catch (Exception e) {

                    }
//                showEventData(strokeList.get(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    startTrackProgress = seekBar.getProgress();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.e("TAGGG", "Seekbar Logs onStopTrackingTouch " + seekBar.getProgress() + " currentStrokeIndex " + currentStrokeIndex);
                    try {
                        if (seekBar.getProgress() > startTrackProgress) {
                            int diff = seekBar.getProgress() - startTrackProgress;

                            if (mPaintingTemp.mDeletedStrokes.size() == 0) {
                                Toast.makeText(PaintActivity.this, "No More Stroke In Cache!", Toast.LENGTH_SHORT).show();
                                seekBar_timeLine.setProgress(startTrackProgress);
                                return;
                            }
                            new undoRedo(false, diff).execute();
                            seekBar_timeLine.setProgress(startTrackProgress);
                        } else {
                            int diff = startTrackProgress - seekBar.getProgress();
                            new undoRedo(true, diff).execute();
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at stop tracking " + e.getMessage());
                    }
                }
            });

            try {
                init_configure();
            } catch (Exception e) {
                Log.e("TAG", "Exception init_configure " + e.getMessage());
            }
//            int permission = ActivityCompat.checkSelfPermission(Paintor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                // We don't have permission so prompt the user
//                ActivityCompat.requestPermissions(
//                        Paintor.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        555
//                );
//                return;
//            }

//            if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
//                // We don't have permission so prompt the user
//                PermissionUtils.requestStoragePermission(Paintor.this, 555);
//                return;
//            }
        } catch (Exception ex) {
            Log.e("TAG", "Exception at oncreate " + ex.getMessage());
        }

        String hue = constants.getString("hueValue", PaintActivity.this);
        if (!hue.toString().isEmpty())
            viewSatVal.setHue(Float.parseFloat(constants.getString("hueValue", PaintActivity.this)));

        currentColorHsv[0] = viewSatVal.color[0];

        setColorInBox(getColor());


        fetchBrush();
        setColorInBox(mPrefBrushColor);


        orientation = this.getResources().getConfiguration().orientation;

        setBruchSetting(mPrefBrushStyle, mPrefBrushColor, mPrefBrushSize,
                false, mPrefAlpha, mPrefFlow);

        showBrushSettingsPopup.observe(this, aBoolean -> {
            if (aBoolean) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(mContext, "Observe called", Toast.LENGTH_SHORT).show();
                }
                PaintActivity._showBrushSettingsPopup.setValue(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onClick(mBrushStyleBtn);
                    }
                }, 500);

            }
        });
        checkRecording();
    }

    public void checkRecording() {


        if (!AppUtils.getStoreProducts().containsKey("record")) {
            iv_start_recoring.setAlpha(0.5f);
            tv_recording_time.setAlpha(0.5f);
        }

        if (!AppUtils.getPurchasedProducts().contains("record")) {
            iv_start_recoring.setAlpha(0.5f);
            tv_recording_time.setAlpha(0.5f);
        } else {
            iv_start_recoring.setAlpha(1f);
            tv_recording_time.setAlpha(1f);
        }
    }

    public void fetchBrush() {
        //On Resume Code
        restorePaintingPreference();

        try {
            float f1 = mBrush.mBrushMaxSize;
            float f2 = mBrush.mBrushMinSize;
            float m = (float) ((f1) * 10.0F);
            float f3 = mBrush.mBrushSize;
            float f4 = mBrush.mBrushMinSize;

            float n = ((f3) * 10.0F);
            if (m < n)
                n = m;

            float percent = (n * 100 / m);

            String strPercent = AppUtils.getValueToTwoDecimal(percent) + "%";
            mTxtSize.setText(strPercent);
            setSize();

        } catch (Exception e) {
            Log.e("TAG", "Exception at e " + e.getMessage());
        }
    }

    private PopupWindow createBrushSettingsPopup() {

        brushDialogView = getLayoutInflater().inflate(R.layout.brush_dialog, null);

        brushList = brushDialogView.findViewById(R.id.brush_list);
//        setUpRecyclerView(recyclerView);


        PopupWindow popupWindow = new PopupWindow(brushDialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(brushDialogView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!isClickedCancel && !isClickedCreateBrush) {

//                    try {
//                        setBrushListToTop();
//                        disableColorPenMode();
//                        showCursor();
//                        returnWithSelectedBrush();
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at onClick LN 1510 " + e.getMessage());
//                    }
                    BrushPickerActivity.saveDataOnClickOutside();


                    if (autoColorPickerActivated) {
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);

                    } else {
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

                    }

                }

            }
        });
        return popupWindow;
    }

    private void gpsIconRelatedWork(MotionEvent event, int x, int y, View view) {
        try {
            if (mPaintView.currentScale() > 10 && iv_selected_image.getVisibility() != View.VISIBLE) {
                iv_selected_image.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at select " + e.getMessage());
        }

        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_gps_icon.getLayoutParams();
//                            LayoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gps_icon.getMeasuredWidth() / 2));
//                            LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gps_icon.getMeasuredHeight() / 2));
//                            iv_gps_icon.setLayoutParams(LayoutParams);
        LayoutParams.leftMargin = (int) ((mPaintView.getLeft() + x - Math.floor(iv_cursor_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft()) + dpToPx(0));
        LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_cursor_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        iv_gps_icon.bringToFront();
        iv_gps_icon.setLayoutParams(LayoutParams);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", "setSPenTouchListener called event " + event.getAction());


                int touchedRGB = 0;
                float[] touchPoint = new float[]{event.getX(), event.getY()};
//                                    int xCoord = (int) touchPoint[0];
//                                    int yCoord = (int) touchPoint[1];

                try {
                    if (FromTutorialMode) {
                        if (alphaValue >= 5 && iv_temp_traced != null) {
                            Bitmap temp = ((BitmapDrawable) iv_temp_traced.getDrawable()).getBitmap();
                            if (temp != null) {
                                selected_bitmap = Bitmap.createScaledBitmap(temp, view.getWidth(), view.getHeight(), true);
//                                                        touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);

                                touchedRGB = getTouchedRGB(touchPoint, iv_selected_image, selected_bitmap);
                            }
                        } else {
                            if (mPainting != null && mPainting.getBitmap() != null) {
                                touchedRGB = getTouchedRGB(touchPoint, mPainting.getBitmap());
//                                                    touchedRGB = mPainting.getBitmap().getPixel(xCoord, yCoord);
//                                                        touchedRGB = mPainting.getBitmap().getPixel((int) event.getRawX(), (int) event.getY());
                            }
                        }
                    } else if (isFromEditImage) {
                        if (selected_bitmap == null) {
                            Bitmap temp = ((BitmapDrawable) iv_selected_image.getDrawable()).getBitmap();
                            selected_bitmap = Bitmap.createScaledBitmap(temp, view.getWidth(), view.getHeight(), true);
                        }
//                                                touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);
                        touchedRGB = getTouchedRGB(touchPoint, iv_selected_image, selected_bitmap);
                    } else {
                        if (mPainting.getBitmap() != null) {

                            Log.e("TAG", "Set Color From  LN 2922");
//                                                xCoord = Integer.valueOf((int) touchPoint[0]);
//                                                yCoord = Integer.valueOf((int) touchPoint[1]);
//                                                touchedRGB = mPainting.getBitmap().getPixel(xCoord, yCoord);

                            touchedRGB = getTouchedRGB(touchPoint, mPainting.getBitmap());
                        }
                    }

                    //then do what you want with the pixel data, e.g
                    int redValue = Color.red(touchedRGB);
                    int greenValue = Color.green(touchedRGB);
                    int blueValue = Color.blue(touchedRGB);
                    int alphaValue = Color.alpha(touchedRGB);
                    colorValue = Color.argb(alphaValue, redValue, greenValue, blueValue);

                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        Log.e("TAG", "Color Picker Return from LN 3209");
                        m_viewCurColor.setBackgroundColor(colorValue);
//                        return;
                    }
                    setColorInBox(colorValue);
                    mPrefBrushColor = colorValue;
                    mBrushColor = colorValue;
                    setHSVColor(colorValue);
                    mPainting.setBrushColor(getColor());
//                                mPainting.setBrushColor(mPrefBrushColor);
                    hexCode = toHex(alphaValue, redValue, greenValue, blueValue);

                    savePaintingPreference();
                    if (!autoColorPickerActivated) {
                        onClick(mPaintmenu_pen);
                    }
//                    onClick(mPaintmenu_pen);
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while Pick Color");
                }
            }
        });
    }

    private int getTouchedRGB(float[] touchPoint, ImageView imageView, Bitmap selected_bitmap) {
        int touchedRGB;
        Matrix inverse = new Matrix();
        imageView.getImageMatrix().invert(inverse);
        inverse.mapPoints(touchPoint);
        touchedRGB = getTouchedRGB(touchPoint, selected_bitmap);
        return touchedRGB;
    }

    private int getTouchedRGB(float[] touchPoint, Bitmap selected_bitmap) {
        int xCoord;
        int yCoord;
        int touchedRGB;
        xCoord = (int) touchPoint[0];
        yCoord = (int) touchPoint[1];
        touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);
        return touchedRGB;
    }

    void hidePopupWindowIfOpen() {
        try {
            if (mPopupWindow_canvas != null && mPopupWindow_canvas.isShowing()) {
                mPopupWindow_canvas.dismiss();
            }

            if (mPopupWindow_save != null && mPopupWindow_save.isShowing()) {
                mPopupWindow_save.dismiss();
            }

            if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                mPopupWindow_post.dismiss();

            if (mColorPopupWindow != null && mColorPopupWindow.isShowing()) {
                mColorPopupWindow.dismiss();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.Canvas_color_picker_close, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.Canvas_color_picker_close);
            }
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at hide " + e.getMessage());
        }
    }

    void disableColorPickerMode() {
        Log.e("TAG", "disableColorPickerMode called");
        iv_gps_icon.setVisibility(View.GONE);
        selected_bitmap = null;
        iv_cursor_icon.setVisibility(View.VISIBLE);
        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
    }


    private long doubleClickLastTime = 0L;
    boolean isLongPressed = false;
    int randomSize = 1;

    public float getsize() {

        if (randomSize >= 25) {
            randomSize = -1;
        } else if (randomSize <= 0) {
            randomSize += 1;
        } else {
            randomSize++;
        }

        Log.e("TAG", "Random Size " + randomSize);

        return randomSize;
    }

    /**
     * ==================================================== get Data by Intent ====================
     */


    private void init_configure() {
        Intent lIntent = getIntent();
        Current_Mode = lIntent.getAction();
        Log.e("TAGGG", "canvas_mode Current Action 2904> " + Current_Mode);
        if (Current_Mode.isEmpty()) {
            Current_Mode = constants.getString("action_name", PaintActivity.this);
        }
        if (Current_Mode.equals(NEW_PAINT)) {
            mNewCanvasBtn.setImageResource(R.drawable.blank_canvas_white_canvas);
            mNewCanvasBtn.setTag(0);
            CurrentMode = 0;
        } else if (Current_Mode.equalsIgnoreCase("LoadWithoutTrace")) {
            mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
            mNewCanvasBtn.setTag(1);
            CurrentMode = 1;
            hideShowCross(false);
        } else if (Current_Mode.equals("Edit Paint") || Current_Mode.equals(RELOAD_PAINTING)) {
            mNewCanvasBtn.setImageResource(R.drawable.trace_icon_white_canvas);
            mNewCanvasBtn.setTag(2);
            CurrentMode = 2;
        } else if (Current_Mode.equals("LoadWithoutTraceFromCamera")) {
            mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
            mNewCanvasBtn.setTag(3);
            CurrentMode = 3;
            hideShowCross(false);
        } else if (Current_Mode.equals("First Time Draw")) {
            mNewCanvasBtn.setImageResource(R.drawable.blank_canvas_white_canvas);
            mNewCanvasBtn.setTag(0);
            Current_Mode = NEW_PAINT;
            CurrentMode = 0;
            paintingIntro = true;

            if (lIntent != null) {
                step = lIntent.getIntExtra("step", 0);
            }

            videoIntro();
        }


    }


    public class undoRedo extends AsyncTask<Void, Void, Void> {

        boolean isUndo = false;
        int diff = 0;

        public undoRedo(boolean isUndo, int diff) {
            this.isUndo = isUndo;
            this.diff = diff;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgress();
            progress.setVisibility(View.VISIBLE);
//            ll_rendering.setVisibility(View.VISIBLE);
            Loadingdialog.ShowPleaseWaitDialog(getString(R.string.rendering));

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isUndo) {
                for (int i = 0; i < diff; i++) {
                    Rect lRect = mPaintingTemp.undoStroke();
                    if (lRect != null) {
                        mPaintViewTemp.reDraw(lRect);
                    }
                    currentStrokeIndex = currentStrokeIndex - 1;
                }
//                currentStrokeIndex = currentStrokeIndex - diff;
                Log.e("TAGGG", "currentStrokeIndex Logs 2715 " + currentStrokeIndex);
                Log.e("TAGGG", "Seekbar Logs onStopTrackingTouch currentStrokeIndex after undo " + currentStrokeIndex);
                configureBrush(true, false);
            } else {
                int avaiableStroke = mPaintingTemp.mDeletedStrokes.size();
                for (int i = 0; i < diff; i++) {
                    if (i < avaiableStroke) {
                        Rect lRect = mPaintingTemp.redoStroke();
                        if (lRect != null) {
                            mPaintViewTemp.reDraw(lRect);
                        }
                        currentStrokeIndex = currentStrokeIndex + 1;
                        seekBar_timeLine.setProgress(seekBar_timeLine.getProgress() + 1);
                    }
                }
//                currentStrokeIndex = currentStrokeIndex + diff;
                Log.e("TAGGG", "currentStrokeIndex Logs 2731 " + currentStrokeIndex);
                configureBrush(false, true);
                Log.e("TAGGG", "Seekbar Logs onStopTrackingTouch currentStrokeIndex after redo " + currentStrokeIndex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("TAGGG", "OnPost called");
//            hideProgress();
            progress.setVisibility(View.INVISIBLE);
//            ll_rendering.setVisibility(View.INVISIBLE);
            Loadingdialog.DismissDialog();
            reflectStroke();
//            if (!isFromOverlaid) {
//            }
        }
    }


    int startTrackProgress = 0;

    private void addColorChangeEvent() {
        EventModel model = new EventModel();
//        model.setColorchange(true);
        model.setEventType(EventType.COLOR_CHANGE + "");
        model.setTimeStamp(second_indicator + "");
        model.setBrushColor(mPrefBrushColor + "");

        // SharedPreferences lSharedPreferences = getPreferences(0);
        SharedPreferences lSharedPreferences = getSharedPreferences("brush", 0);

        if (lSharedPreferences.getString("pref-saved", null) != null) {


            model.objChangeData.setBrushStyle(lSharedPreferences.getInt("brush-style", Brush.LineBrush) + "");

            model.objChangeData.setBrushColor(mPrefBrushColor + "");

            model.objChangeData.setBrushSize(lSharedPreferences.getFloat("brush-size", 8.0F) + "");

            model.objChangeData.setBrushFlow(lSharedPreferences.getInt("brush-flow", 65) + "");

            model.objChangeData.setBrushAlpha(lSharedPreferences.getInt("brush-alpha", 255) + "");

            model.objChangeData.setBrushHardness(lSharedPreferences.getInt("brush-pressure", 65) + "");

            model.getObjChangeData().setBrushName(getStyleName(lSharedPreferences.getInt("brush-style", Brush.LineBrush)));
        }

        addEventInList(model);
    }

    // To keep track of activity's window focus
    boolean currentFocus;
    // To keep track of activity's foreground/background status
    boolean isPaused;
    boolean isManuallyPauseRecording = false;

    Handler collapseNotificationHandler;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            currentFocus = hasFocus;

            if (!hasFocus) {

                // Method that handles loss of window focus
                collapseNow();
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at collapse " + e.toString());
        }
    }

    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }
        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;
                    try {
                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`
                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager.getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager.getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }


                    try {
                        if (collapseStatusBar != null) {
                            collapseStatusBar.setAccessible(true);
                            collapseStatusBar.invoke(statusBarService);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }

                }
            }, 50L);
        }
    }

    boolean isRecovered = false;

    private void initRecorder() {
        try {
            mMediaRecorder = new MediaRecorder();
//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

            if (mic_state == 1) {
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mMediaRecorder.setAudioEncodingBitRate(128000);
                mMediaRecorder.setAudioSamplingRate(44100);
            }

            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

//            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mMediaRecorder.setVideoEncodingBitRate(512 * 1500);
//            mMediaRecorder.setVideoFrameRate(30);

            //mMediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);
            mMediaRecorder.setVideoSize(2048, 1024);
            mMediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
            //mMediaRecorder.setVideoFrameRate(30);

            try {
                mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            } catch (Exception e) {
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.SaveMovieFile, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.SaveMovieFile);
            recordingOutputFilePath = getFilePath();

            File _recordingFile = new File(recordingOutputFilePath);
            mMediaRecorder.setOutputFile(_recordingFile.getAbsolutePath());

            if (!_recordingFile.exists()) {
                _recordingFile.createNewFile();
            }
            Log.e("TAGGG", "initRecorder called outputFilePath " + recordingOutputFilePath + " " + getResources().getConfiguration().orientation + " FileExist " + _recordingFile.exists());
            try {
                mMediaRecorder.prepare();
//                writeException("MediaRecorder Works FilePath " + recordingOutputFilePath + " W*H " + DISPLAY_WIDTH + "*" + DISPLAY_HEIGHT + " Orienation " + getResources().getConfiguration().orientation + " FileExist " + _recordingFile.exists());
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Toast.makeText(this, "IllegalStateException " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                writeException("MediaRecorder IllegalStateException " + e.getMessage() + " " + e.toString() + " " + e.getCause() + " FilePath " + recordingOutputFilePath);
                stopRecordingService();
            } catch (FileNotFoundException fnf) {
                Log.e("TAG", "File Not Found Exception " + fnf.getMessage() + " " + fnf.getCause() + " " + fnf.toString());
//                writeException("MediaRecorder FileNotFoundException " + fnf.getMessage() + " " + fnf.toString() + " " + fnf.getCause() + " FilePath " + recordingOutputFilePath);
                stopRecordingService();
            } catch (IOException e) {
//                writeException("MediaRecorder IOException " + e.getMessage() + " " + e.toString() + " " + e.getCause() + " FilePath " + recordingOutputFilePath + " e " + e + " W*H " + DISPLAY_WIDTH + "*" + DISPLAY_HEIGHT + " Orienation " + getResources().getConfiguration().orientation + " FileExist " + _recordingFile.exists());
//                Toast.makeText(this, "IOException " + e.getMessage(), Toast.LENGTH_SHORT).show();
                stopRecordingService();
                if (!isRecovered) {
                    if (mMediaRecorder != null)
                        mMediaRecorder.release();
                    isRecovered = true;
                    initRecorder();
//                    startRecordingService();
                }

            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at initRecorder " + e.getMessage() + " " + e.toString() + " " + e.getStackTrace().toString());
        }
    }


    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            try {
//                iv_start_recoring.setImageResource(R.drawable.start_recording);
                mMediaRecorder.stop();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.STOP_RECORDING, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.STOP_RECORDING);
                mMediaRecorder.reset();
                iv_start_recoring.setTag(0);
                Log.v(TAG, "Recording Stopped");
                mMediaProjection = null;
                stopScreenSharing();
                Log.i(TAG, "MediaProjection Stopped");

            } catch (IllegalStateException e) {

            } catch (Exception e) {

            }
        }
    }

    private void stopScreenSharing() {
        if (everySecondRunnable != null)
            recording_handler.removeCallbacks(everySecondRunnable);

        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release();
//        iv_start_recoring.setImageResource(R.drawable.start_recording);
        iv_start_recoring.setTag(0);

        if (eventModelList.size() != 0) {
            prepareJsonFromEventList();
        }

        second_indicator = 0;
        tv_recording_time.setTag(recordingState.In_Idle);
        tv_recording_time.setText(convertToTime(second_indicator));
        tv_recording_time.setVisibility(View.VISIBLE);
        tv_recording_time.setText(getString(R.string.record));
        iv_start_recoring.setImageResource(R.drawable.recording_icon_canvas);

        Log.e("TAGGG", "saveToLocal logs recordedFileName " + recordedFileName + " isPickFromOverlaid " + isPickFromOverlaid + " stopScreenSharing ");
        if (!recordedFileName.isEmpty() && isPickFromOverlaid) {
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            prefsEditor.putString(recordedFileName + ".mp4", selectedImagePath);
            prefsEditor.commit();

            Log.e("TAGGG", "saveToLocal logs File Key SaveToLocal" + selectedImagePath + " RecordingName " + recordedFileName);
            recordedFileName = "";
        }
        stopRecordingService();
        new saveImageForRecording().execute();
    }


    void stopRecordingService() {
        try {
            stopService(new Intent(PaintActivity.this, MyServiceForRecording.class));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                stopService(new Intent(Paintor.this, MyServiceForRecording.class));
//            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at stop ser " + e.getMessage());
        }
    }

    void startRecordingService() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForegroundService(new Intent(PaintActivity.this, MyServiceForRecording.class));
            } else {
                startService(new Intent(PaintActivity.this, MyServiceForRecording.class));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at start ser " + e.getMessage());
        }
    }


    private void shareScreen() {
        try {
            startRecordingService();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMediaProjection == null || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
                        return;
                    }

                    mMediaProjection.registerCallback(mMediaProjectionCallback, null);

                    mVirtualDisplay = createVirtualDisplay();
                    startCountDown();
                }
            }, 2000);
        } catch (Exception e) {
            Log.e("TAG", "Exception at shareScreen " + e.getMessage());
        }
    }


    public void prepareJsonFromEventList() {

        JSONObject parentObject = new JSONObject();
        JSONArray mainArrayObject = new JSONArray();
        try {
            JSONObject objFrequencyData = new JSONObject();
            Integer ColorChange = 0;
            Integer BrushChange = 0;

            for (int i = 0; i < eventModelList.size(); i++) {
                if (eventModelList.get(i).getEventType().equalsIgnoreCase(EventType.COLOR_CHANGE + ""))
                    ColorChange++;
                else
                    BrushChange++;
            }

            objFrequencyData.put(constants.getTOTAL_EVENTS(), eventModelList.size());
            objFrequencyData.put(constants.getTOTAL_COLOR_CHANGE(), ColorChange);
            objFrequencyData.put(constants.getTOTAL_Brush_CHANGE(), BrushChange);
            objFrequencyData.put(constants.getCanvas_Background_Color(), mPrefBackgroundColor);

            Log.e("TAGGG", "BackGround Color at oncreate 2903 " + mPrefBackgroundColor);
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.SaveStrokeFile, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.SaveStrokeFile);

            for (int i = 0; i < eventModelList.size(); i++) {
                JSONObject childObject = new JSONObject();
                childObject.put(constants.getTIME_STAMP(), eventModelList.get(i).getTimeStamp());
                childObject.put(constants.getBRUSH_COLOR(), eventModelList.get(i).getBrushColor());

                childObject.put(constants.getIS_COLOR_CHANGE(), eventModelList.get(i).getEventType());

                childObject.put(constants.getEVENT_NOTES(), eventModelList.get(i).getNotes());
                JSONObject childObject_1 = new JSONObject();
                childObject_1.put(constants.getBRUSH_FLOW(), eventModelList.get(i).getObjChangeData().getBrushFlow());
                childObject_1.put(constants.getBRUSH_ALPHA(), eventModelList.get(i).getObjChangeData().getBrushAlpha());
                childObject_1.put(constants.getBRUSH_SIZE(), eventModelList.get(i).getObjChangeData().getBrushSize());
                childObject_1.put(constants.getBRUSH_HARDNESS(), eventModelList.get(i).getObjChangeData().getBrushHardness());
                childObject_1.put(constants.getBRUSH_STYLE(), eventModelList.get(i).getObjChangeData().getBrushStyle());
                childObject_1.put(constants.getBRUSH_NAME(), eventModelList.get(i).getObjChangeData().getBrushName());
                childObject_1.put(constants.getBRUSH_COLOR(), eventModelList.get(i).getObjChangeData().getBrushColor());
                childObject.put(constants.getBRUSH_EVENT_OBJECT(), childObject_1);
                mainArrayObject.put(childObject);
            }

            parentObject.put(constants.getFREQUENCY_DATA(), objFrequencyData);
            parentObject.put(constants.getEVENTS_DATA(), mainArrayObject);
            String jsonStr = parentObject.toString();
            writeToFile(jsonStr, true);

            eventModelList.clear();

            //Store Stroke data in file.


            JSONArray array = new JSONArray();

            for (int i = 0; i < strokeList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put(constants.getTIME_STAMP(), strokeList.get(i).getTimeStamp());
                object.put(constants.getSTROKE(), strokeList.get(i).getStrokeAxis());
                JSONObject childObject_1 = new JSONObject();
                childObject_1.put(constants.getBRUSH_FLOW(), strokeList.get(i).getObjChangeData().getBrushFlow());
                childObject_1.put(constants.getBRUSH_ALPHA(), strokeList.get(i).getObjChangeData().getBrushAlpha());
                childObject_1.put(constants.getBRUSH_SIZE(), strokeList.get(i).getObjChangeData().getBrushSize());
                childObject_1.put(constants.getBRUSH_HARDNESS(), strokeList.get(i).getObjChangeData().getBrushHardness());
                childObject_1.put(constants.getBRUSH_STYLE(), strokeList.get(i).getObjChangeData().getBrushStyle());
                childObject_1.put(constants.getBRUSH_NAME(), strokeList.get(i).getObjChangeData().getBrushName());
                childObject_1.put(constants.getBRUSH_COLOR(), strokeList.get(i).getObjChangeData().getBrushColor());
                object.put(constants.getBRUSH_EVENT_OBJECT(), childObject_1);
                array.put(object);
            }

            writeToFile(array + "", false);
            strokeList.clear();
        } catch (Exception e) {
            Log.e("TAGG", "Exception while prepareJson " + e.toString());
        }
    }

    public void writeToFile(String data, boolean isFromEvent) {
        // Get the directory for the user's public pictures directory.
//        final File path = new File(Environment.getExternalStorageDirectory() + dirName + "Recordings");
        final File path = new File(KGlobal.getDownloadedFolderPath(this));
        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file;
        if (isFromEvent)
            file = new File(path, "EventData_" + recordedFileName + ".txt");
        else
            file = new File(path, "StrokeData_" + recordedFileName + ".txt");
        // Save your stream, don't forget to flush() it before closing it.

        try {
            if (!file.exists())
                file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public void writeException(String _log) {
        File f = new File(Environment.getExternalStorageDirectory(),
                "Crash_Reports");
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Crash_Reports/MediaRecorder_Error.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                Log.e("TAG", "writeLogsInFile Created file path " + file.getAbsolutePath());
            }
            try {
                OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, true));
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);


                buffered_writer.write(_log + "\n");

                buffered_writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "writeLogsInFile IOException at writeLogs " + e.getMessage());
                Toast.makeText(this, "Exception to write in file " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception to create file " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TAG", "writeLogsInFile Exception at writeLogs " + e.getMessage());
        }
    }

   /* public void writeException(String data) {
        // Get the directory for the user's public pictures directory.
//        final File path = new File(Environment.getExternalStorageDirectory() + dirName + "Recordings");

        try {

            File path = new File(Environment.getExternalStorageDirectory(),
                    "Crash_Reports");
            if (!path.exists()) {
                path.mkdir();
            }

            // Make sure the path directory exists.
            if (!path.exists()) {
                // Make it, if it doesn't exit
                path.mkdirs();
            }

            final File file = new File(path, "Exeption_MediaRecorder.txt");
            // Save your stream, don't forget to flush() it before closing it.

            try {
                if (!file.exists())
                    file.createNewFile();

                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);
                myOutWriter.close();
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at writeException " + e.getMessage());
        }
    }*/

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("Paintor",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null /*Handler*/);
    }

    String recordedFileName = "";

    public String getFilePath() {
//        final String directory = Environment.getExternalStorageDirectory() + dirName + "Recordings";
        try {

            final String directory = KGlobal.getDownloadedFolderPath(this);
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Toast.makeText(this, "Failed to get External Storage", Toast.LENGTH_SHORT).show();
                return null;
            }
            final File folder = new File(directory);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            String filePath;
            if (success) {
                recordedFileName = generateFileName();
                String videoName = (recordedFileName + ".mp4");
                filePath = directory + File.separator + videoName;

                File _file = new File(filePath);
                if (!_file.exists()) {
                    _file.createNewFile();
                }
            } else {
                Toast.makeText(this, "Failed to create Recordings directory", Toast.LENGTH_SHORT).show();
                return null;
            }
            Log.e("TAGG", "getFilePath called " + filePath);
            return filePath;
        } catch (Exception e) {
        }
        return "";
    }

    public String getCurSysDate() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                // Do something here on the main thread
                Log.e("Handlers", "Called on main thread");
                if (!mPainting.isEmpty())
                    new saveImageInBack().execute();
                handler.postDelayed(this, SAVE_INTERVAL_TIME);
            } catch (Exception e) {

            }
        }
    };

    public Runnable everySecondRunnable = new Runnable() {
        @Override
        public void run() {
            if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                second_indicator++;
                /*if (tv_recording_time.getVisibility() != View.VISIBLE)
                    tv_recording_time.setVisibility(View.VISIBLE);*/
                tv_recording_time.setText(convertToTime(second_indicator));
                if (tv_rec_time != null) {
                    tv_rec_time.setText(convertToTime(second_indicator) + " Min");
                }
                recording_handler.postDelayed(this, ONE_SECOND);
            } else {
                /*if (tv_recording_time.getVisibility() == View.VISIBLE)
                    tv_recording_time.setVisibility(View.INVISIBLE);
                else
                    tv_recording_time.setVisibility(View.VISIBLE);*/

                if (tv_rec_time != null) {
                    if (mPopupWindow.isShowing()) {
                        if (tv_rec_time.getVisibility() == View.VISIBLE)
                            tv_rec_time.setVisibility(View.INVISIBLE);
                        else
                            tv_rec_time.setVisibility(View.VISIBLE);
                    } else {
                        if (tv_recording_time.getVisibility() == View.VISIBLE)
                            tv_recording_time.setVisibility(View.INVISIBLE);
                        else
                            tv_recording_time.setVisibility(View.VISIBLE);
                    }

                }
                recording_handler.postDelayed(this, 500);
            }
        }
    };

    YouTubePlayer YTPlayer;

    private void initYouTubePlayerView(String videoID, boolean needToPause) {
        initPlayerMenu();
        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is paused
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(youTubePlayer -> {
            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    YTPlayer = youTubePlayer;
//                    youTubePlayer.setVolume(0);
                    loadVideo(youTubePlayer, videoID, needToPause);
                }

                /*@Override
                public void onStateChange(int state) {
                    super.onStateChange(state);
                }*/
            });
        }, true);
    }


    private void loadVideo(YouTubePlayer youTubePlayer, String videoId, boolean needToPause) {
//        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
//            youTubePlayer.loadVideo(videoId, 0);
//        else
//            youTubePlayer.cueVideo(videoId, 0);

        youTubePlayer.cueVideo(videoId, 0);

        Log.e("TAGG", "loadVideo needToPause " + needToPause);
        if (YTPlayer != null && needToPause) {
//            YTPlayer.pause();
            if (!videoPlayedOnce) {
                videoPlayedOnce = true;
                editor.putBoolean("videoPlayedOnce", videoPlayedOnce).apply();
                YTPlayer.play();
            } else {
                YTPlayer.pause();
            }
        }
    }

    private void initPlayerMenu() {
        youTubePlayerView.getPlayerUIController().showMenuButton(true);
     /*   youTubePlayerView.getPlayerUIController().getMenu().addItem(
                new com.pierfrancescosoffritti.youtubeplayer.ui.menu.MenuItem("example", R.drawable.icon, (view) -> Toast.makeText(this, "item clicked", Toast.LENGTH_SHORT).show())
        );*/
    }

    void setBackgroundImageTouch() {

        iv_selected_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                Log.e("TAGGG", "onTouch called " + current_mode);
//                if (current_mode == canvas_mode.canvas_back) {

                final int x = (int) event.getX();
                final int y = (int) event.getY();
                if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                    if (linear_background_color.getVisibility() == View.VISIBLE) {
                        return false;
                    }
//                    setHSVColor(mPrefBrushColor);
                    try {
                        m_viewColorContainer.setVisibility(View.INVISIBLE);
                        isBGSelected = false;
                        linear_background_color.setVisibility(View.GONE);
                        colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                        m_viewColorContainer.invalidate();
                        viewContainer.invalidate();
                        setColor();

                        Integer newColor = getColor();
                        Integer oldColor = mPrefBrushColor;

                        /*if (!oldColor.equals(newColor)) {
                            Log.e("TAGG", "New Color Added " + newColor);
                        }*/
                        setColorInBox(newColor);
                        Log.e("TAGG", "SetColor Called LN 3199");
                    } catch (Exception e) {

                    }
                }

                try {
                    if (mPopupWindow_canvas != null && mPopupWindow_canvas.isShowing()) {
                        mPopupWindow_canvas.dismiss();
                    }

                    if (mPopupWindow_save != null && mPopupWindow_save.isShowing()) {
                        mPopupWindow_save.dismiss();
                    }

                    if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                        mPopupWindow_post.dismiss();

                    if (mColorPopupWindow != null && mColorPopupWindow.isShowing()) {
                        mColorPopupWindow.dismiss();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.Canvas_color_picker_close, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.Canvas_color_picker_close);
                    }
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception " + e.getMessage());
                }

                if (iv_gps_icon.getVisibility() == View.VISIBLE) {

                    RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_gps_icon.getLayoutParams();
//                    LayoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gps_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft() + dpToPx(20));
                    LayoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gps_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
                    LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gps_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());

                    iv_gps_icon.setLayoutParams(LayoutParams);
                    /*if (alphaValue < 5 && alphaValue != 0) {
                        if (!_toast.getView().isShown()) {
                            _toast.show();
                        }

                    }*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                                /*RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_gps_icon.getLayoutParams();
                                LayoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gps_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
                                LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gps_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
                                iv_gps_icon.setLayoutParams(LayoutParams);*/

                            if (alphaValue >= 5 && selected_bitmap == null) {
//                                    selected_bitmap = ((BitmapDrawable) iv_selected_image.getDrawable()).getBitmap();
                                Bitmap temp = ((BitmapDrawable) iv_selected_image.getDrawable()).getBitmap();
                                selected_bitmap = Bitmap.createScaledBitmap(temp, iv_selected_image.getWidth(), iv_selected_image.getHeight(), true);
                            } else if (alphaValue < 5 && selected_bitmap == null) {
                                if (mPainting != null && mPainting.getBitmap() != null) {
                                    selected_bitmap = mPainting.getBitmap();
                                }
                            }

                            if (selected_bitmap != null) {
                                float[] touchPoint = new float[]{event.getX(), event.getY()};
//                                int xCoord = (int) touchPoint[0];
//                                int yCoord = (int) touchPoint[1];
                                try {

                                    Matrix inverse = new Matrix();
                                    iv_selected_image.getImageMatrix().invert(inverse);
                                    inverse.mapPoints(touchPoint);
                                    int xCoord = Integer.valueOf((int) touchPoint[0]);
                                    int yCoord = Integer.valueOf((int) touchPoint[1]);
                                    int touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);
                                    //then do what you want with the pixel data, e.g
                                    int redValue = Color.red(touchedRGB);
                                    int greenValue = Color.green(touchedRGB);
                                    int blueValue = Color.blue(touchedRGB);
                                    int alphaValue = Color.alpha(touchedRGB);
                                    colorValue = Color.argb(alphaValue, redValue, greenValue, blueValue);

//                                    if (event.getAction() != MotionEvent.ACTION_UP) {
//                                        Log.e("TAG", "Color Picker Return from LN 4026 colorValue " + colorValue);
//                                        m_viewCurColor.setBackgroundColor(colorValue);
//                                        return;
//                                    }

                                    m_viewCurColor.setBackgroundColor(colorValue);

                                    setColorInBox(colorValue);

                                    updateDensity(255, 100f);

                                    mPrefBrushColor = colorValue;
                                    mPrefBackgroundColor = colorValue;
                                    mBrushColor = colorValue;
                                    setHSVColor(colorValue);
                                    mPainting.setBrushColor(getColor());
                                    hexCode = toHex(alphaValue, redValue, greenValue, blueValue);
                                    savePaintingPreference();
                                    Log.e("TAGGG", " HEX CODE" + toHex(alphaValue, redValue, greenValue, blueValue));
//                                    releasePicker();
                                    onClick(mPaintmenu_pen);

                                    if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                                        try {
                                            m_viewColorContainer.setVisibility(View.INVISIBLE);
                                            linear_background_color.setVisibility(View.GONE);
                                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                                            m_viewColorContainer.invalidate();
                                            viewContainer.invalidate();
                                            Integer oldColor = mPrefBrushColor;

                                            if (brushSettingsPopup.isShowing())
                                                returnWithSelectedBrush();

                                            Log.e("TAGGG", "Color Tracking 2282 " + mBrushColor);
                                            setColor();
                                            Integer newColor = getColor();
                                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                                addColorChangeEvent();
                                            }

                                /*if (!oldColor.equals(newColor)) {
                                    Log.e("TAGG", "New Color Added " + newColor);
                                }*/
                                            setColorInBox(newColor);
                                            Log.e("TAGGG", "Tracking oldColor " + oldColor + "  newColor " + newColor + " OldSize " + mPrefBrushSize + " New Size " + mBrushSize + " BrushName " + mPrefBrushStyle + " " + mBrushStyle + " mPrefFlow " + mPrefFlow + " mBrushFlow " + mBrushFlow + " mPrefAlpha " + mPrefAlpha + " mBrushAlpha " + mBrushAlpha);
                                            savePaintingPreference();
                                        } catch (Exception e) {
                                            Log.e("TAGG", "Exception while hide color pallete");
                                        }
                                    }

                                } catch (Exception e) {
                                    Log.e("TAGGG", "Exception while get color " + e.getMessage());
//                                    Log.e("TAGGG", "Exception while get color " + e.getMessage() + " xCoord " + xCoord + " bitmap width " + selected_bitmap.getWidth());
                                }
                            }
                        }
                    });
//                    }
                    return false;
                } else {
                    if (isInZoomMode == false && iv_cursor_icon.getVisibility() != View.VISIBLE) {
                        iv_cursor_icon.setVisibility(View.VISIBLE);
                    }
                    RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_cursor_icon.getLayoutParams();
                    LayoutParams.leftMargin = (int) ((mPaintView.getLeft() + x - Math.floor(iv_cursor_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft()) + dpToPx(0));
                    LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_cursor_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
                    iv_cursor_icon.bringToFront();
                    iv_cursor_icon.setLayoutParams(LayoutParams);

                    if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                        try {
                            m_viewColorContainer.setVisibility(View.INVISIBLE);
                            linear_background_color.setVisibility(View.GONE);
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
                            m_viewColorContainer.invalidate();
                            viewContainer.invalidate();
                            Integer oldColor = mPrefBrushColor;

                            if (brushSettingsPopup.isShowing())
                                returnWithSelectedBrush();

                            Log.e("TAGGG", "Color Tracking 2282 " + mBrushColor);
                            setColor();
                            Integer newColor = getColor();
                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                addColorChangeEvent();
                            }

                                /*if (!oldColor.equals(newColor)) {
                                    Log.e("TAGG", "New Color Added " + newColor);
                                }*/
                            setColorInBox(newColor);
                            Log.e("TAGGG", "Tracking oldColor " + oldColor + "  newColor " + newColor + " OldSize " + mPrefBrushSize + " New Size " + mBrushSize + " BrushName " + mPrefBrushStyle + " " + mBrushStyle + " mPrefFlow " + mPrefFlow + " mBrushFlow " + mBrushFlow + " mPrefAlpha " + mPrefAlpha + " mBrushAlpha " + mBrushAlpha);
                            savePaintingPreference();
                        } catch (Exception e) {
                            Log.e("TAGG", "Exception while hide color pallete");
                        }
                    }

                    mPrefBrushSize = mBrushSize;
//                        mPrefBrushSize = getsize();
                    mPainting.setBrushSize(mPrefBrushSize);
                    return false;
                }
            }
        });

    }

   /* private AdView mAdView;

    void loadAd() {
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }*/

    public static String toHex(int a, int r, int g, int b) {
        return "#" + toBrowserHexValue(a) + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    protected void setupBrushColor() {
//        m_brushlayout = (RelativeLayout) findViewById(R.id.brushlayout);
        brushSettingsPopup = createBrushSettingsPopup();

        SharedPreferences sharedPref = getSharedPreferences("brush", 0);
        boolean grayScaleChecked = sharedPref.getBoolean("gray_scale", false);
        Log.e("greyScal", grayScaleChecked + "");
        switch_gray_scale = brushDialogView.findViewById(R.id.switch_gray_scale);


        switch_gray_scale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //  SharedPreferences sharedPref = getPreferences(0);
                SharedPreferences sharedPref = getSharedPreferences("brush", 0);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("gray_scale", isChecked);
                editor.apply();

                if (isChecked) {
                    Log.e("grayScaleMain1", grayScaleChecked + "");
                    rl_gray_scale.setVisibility(View.VISIBLE);
                    m_viewColorPanel.setVisibility(View.GONE);
                    m_viewArrow.setVisibility(View.GONE);

                    mPreviousPrefBrushColor = mPrefBrushColor;
                    mPrefBrushColor = currentGrayScaleColor;
                    mBrushColor = currentGrayScaleColor;
                    setHSVColor(currentGrayScaleColor);
                    mPainting.setBrushColor(getColor());

                    setColorInBox(currentGrayScaleColor);

                    if (CurrentMode == 2 || Current_Mode.equalsIgnoreCase("Reload Painting")) {
                        iv_selected_image.setImageBitmap(_grayScaleForTrace);
                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_gray_scale_on, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_gray_scale_on);
                } else {
                    Log.e("grayScaleMain2", grayScaleChecked + "");
                    rl_gray_scale.setVisibility(View.GONE);
                    m_viewColorPanel.setVisibility(View.VISIBLE);
                    m_viewArrow.setVisibility(View.VISIBLE);

                    view_cross2.setVisibility(View.GONE);

//                    mPrefBrushColor = mPreviousPrefBrushColor;
//                    mBrushColor = mPreviousPrefBrushColor;
//                    setHSVColor(mPreviousPrefBrushColor);
//                    mPainting.setBrushColor(getColor());
//
//                    setColorInBox(mPreviousPrefBrushColor);

                    if (CurrentMode == 2 || Current_Mode.equalsIgnoreCase("Reload Painting")) {
                        iv_selected_image.setImageBitmap(_coloredBitmapFortrace);
                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_gray_scale_off, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_gray_scale_off);
                }
                setSpecialFunctionState(switch_gray_scale);
            }
        });
        Log.e("grayScaleMain", grayScaleChecked + "");
        switch_gray_scale.setChecked(grayScaleChecked);
        defaultSwitchGrayScaleStatus = grayScaleChecked;
        setSpecialFunctionState(switch_gray_scale);

        boolean blockColoringChecked = sharedPref.getBoolean("block_coloring", false);
        Log.e("block_coloring", grayScaleChecked + "");
        switch_block_coloring = brushDialogView.findViewById(R.id.switch_block_coloring);

//        switch_block_coloring.setChecked(true);
        switch_block_coloring.setChecked(blockColoringChecked);

//        Toast.makeText(this, String.valueOf(autoColorPickerActivated), Toast.LENGTH_SHORT).show();
//

        switch_block_coloring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = getSharedPreferences("brush", 0);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("block_coloring", b);
                editor.apply();

/*
                if (autoColorPickerActivated) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.auto_color_picker_deactivated, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_deactivated);
                    autoColorPickerActivated = false;
                    pickNewColorMode = false;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

                    iv_gps_icon.setVisibility(View.GONE);
                    iv_cursor_icon.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        mPaintView.cancelDragAndDrop();

                    selected_bitmap = null;
                    mColorPopupWindow = null;

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                        moveCursor();
                    else {
                        moveCursorVeritcal();
                    }

                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.auto_color_picker_activated, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_activated);
                    autoColorPickerActivated = true;
                    pickNewColorMode = true;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                    iv_cursor_icon.setVisibility(View.GONE);
                    iv_gps_icon.setVisibility(View.VISIBLE);
                }
*/


                if (b) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, "constants.block_coloring_on", Toast.LENGTH_SHORT).show();
                        Toast.makeText(PaintActivity.this, constants.auto_color_picker_activated, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_activated);
                    autoColorPickerActivated = true;
                    pickNewColorMode = true;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                    iv_cursor_icon.setVisibility(View.GONE);
                    iv_gps_icon.setVisibility(View.VISIBLE);
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.auto_color_picker_deactivated, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_deactivated);
                    autoColorPickerActivated = false;
                    pickNewColorMode = false;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

                    iv_gps_icon.setVisibility(View.GONE);
                    iv_cursor_icon.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        mPaintView.cancelDragAndDrop();

                    selected_bitmap = null;
                    mColorPopupWindow = null;

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                        moveCursor();
                    else {
                        moveCursorVeritcal();
                    }

                }
            }
        });
    }


    protected void setupColorBar() {
        try {
            m_viewCurColor = (View) findViewById(R.id.colorbar_curcolor); // Right side Brush color box
            img_community = findViewById(R.id.img_community);
            colorbar_bgcolor = (View) findViewById(R.id.colorbar_bgcolor); // Left side Background color box
            m_viewArrow = findViewById(R.id.colorbar_arrow);

            m_viewColorContainer = (RelativeLayout) findViewById(R.id.colorlayout);
            viewSatVal = (ColorPad) findViewById(R.id.colorpicker_viewSatBri); // Small Color pad box
            viewTarget = findViewById(R.id.colorpicker_target); // Color picker icon on Color pad box

            img_community.setOnClickListener(this);
            img_community.setVisibility(View.GONE);
            m_viewCurColor.setBackgroundColor(mPrefBrushColor);

            setHSVColor(mPrefBrushColor);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                moveCursor();
            } else {
                moveCursorVeritcal();
            }

            // rainbow color bar touch event
            m_viewColorPanel.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    try {
                        if (brushSettingsPopup.isShowing())
                            returnWithSelectedBrush();
                    } catch (Exception e) {
                    }

                    if (event.getAction() == MotionEvent.ACTION_MOVE
                            || event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                mPaintView.cancelDragAndDrop();
                            iv_gps_icon.setVisibility(View.GONE);
                            selected_bitmap = null;
                            if (autoColorPickerActivated) {
                                mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                            } else {
                                mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
                            }
//                        mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
                        }

                        ViewGroup.MarginLayoutParams LayoutParams = (ViewGroup.MarginLayoutParams) m_viewColorContainer.getLayoutParams();
                        float hue = 0.0f;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                            try {

                                // code for portrait mode
                                float y = event.getY();
                                if (y < 0.f) y = 0.f;
                                if (y > m_viewColorPanel.getMeasuredHeight())
                                    y = m_viewColorPanel.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
                                hue = 360.f / m_viewColorPanel.getMeasuredHeight() * y;
                                if (hue == 360.f) hue = 0.f;
                                setHue(hue);
                                moveCursor();
                                LayoutParams.leftMargin = ((ViewGroup.MarginLayoutParams) (mPaintView.getLayoutParams())).leftMargin + (dpToPx(isXLargeScreen ? 60 : 40));
                                LayoutParams.topMargin = (int) (y - (LayoutParams.height));

                                if (LayoutParams.topMargin < m_viewCurColor.getMeasuredHeight())
                                    LayoutParams.topMargin = m_viewCurColor.getMeasuredHeight();
                                else if (LayoutParams.topMargin > mScreenWidth + LayoutParams.height)
                                    LayoutParams.topMargin = mScreenWidth - LayoutParams.height;

                                Log.e("TAG", "Margins Left " + LayoutParams.leftMargin + " y " + y + " Top " + LayoutParams.topMargin + " ColorPanelHeight " + m_viewColorPanel.getMeasuredHeight());
                            } catch (Exception ex) {
                                Log.e("TAG", "Exception at set color " + ex.getMessage());
                            }
                        } else {
                            try {
                                float x = event.getX();
                                Log.e("TAG", "X value " + x + " y " + event.getY() + " width " + m_viewColorPanel.getWidth());
                                if (x <= 0) {
                                    Log.e("TAG", "X value Return from set");
                                    return true;
                                }
                                if (x < 0.f) x = 0.f;
                                if (x > m_viewColorPanel.getMeasuredWidth())
                                    x = m_viewColorPanel.getMeasuredWidth() - 0.001f; // to avoid looping from end to start.

                                hue = 360.f / m_viewColorPanel.getMeasuredWidth() * x;
                                if (hue == 360.f) hue = 0.f;
                                setHue(hue);

                               /* hue = 360.f - 360.f / m_viewColorPanel.getMeasuredWidth() * x;
                                if (hue == 360.f) hue = 0.f;
                                setHue(hue);*/

//                                float y = (getHue() * m_viewColorPanel.getMeasuredWidth() / 360.f);
                                float y = x + 10;

                                moveCursorVeritcal();
//                                if (x >= m_viewColorContainer.getWidth() / 2)
//                                    LayoutParams.leftMargin = (int) m_viewArrow.getLeft() - (m_viewColorContainer.getWidth() / 2);
//
//                                Log.e("TAG", "Margin Left Before " + LayoutParams.leftMargin);
//                                if (LayoutParams.leftMargin <= 100)
//                                    LayoutParams.leftMargin = 100;

                                LayoutParams.leftMargin = (int) (y - (LayoutParams.width));

                                if (LayoutParams.leftMargin < m_viewCurColor.getMeasuredHeight())
                                    LayoutParams.leftMargin = m_viewCurColor.getMeasuredHeight();
                                else if (LayoutParams.leftMargin > mScreenWidth + LayoutParams.height)
                                    LayoutParams.leftMargin = mScreenWidth - LayoutParams.width;

                                Log.e("TAG", "Margin Left " + LayoutParams.leftMargin + " Right " + LayoutParams.rightMargin);
                            } catch (Exception e) {
                                Log.e("TAG", "Exception at set params " + e.getMessage());
                            }
                        }


                        // update view
                        viewSatVal.setHue(getHue());
                        moveTarget();
                        mPaintView.reDraw(null);
                        mStatus = 8;
                        hideZoomButton();

                        m_viewColorContainer.setVisibility(View.VISIBLE);


                      /*  if (LayoutParams.topMargin < 0)
                            LayoutParams.topMargin = 0;
                        else if (LayoutParams.topMargin > mScreenHeight - LayoutParams.height)
                            LayoutParams.topMargin = mScreenHeight - LayoutParams.height;
*/

                        m_viewColorContainer.setLayoutParams(LayoutParams);
                        m_viewColorContainer.bringToFront();
                        viewSatVal.setHue(hue);

                        if (isBGSelected) {

                            colorbar_bgcolor.setBackgroundColor(getColor());
                            colorbar_bgcolor.invalidate();

                            //mPainting.setBackgroundColor(getColor());
                            mPainting.setBackgroundColor(getColor());
                            mPainting.clearAndUpdateBGPainting();
                            mPaintView.invalidate();
                            savePaintingPreference();

                            mPrefBackgroundColor = getColor();
                            Log.e("TAGGG", "m_viewColorPanel ACTION_MOVE mPrefBackgroundColor " + mPrefBackgroundColor);
                        } else {
                            Log.e("TAGGG", "m_viewColorPanel ACTION_MOVE mPrefBrushColor " + mPrefBrushColor + " getColor " + getColor());
                            m_viewCurColor.setBackgroundColor(getColor());
                            m_viewCurColor.invalidate();
                        }
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.e("TAGGG", "m_viewColorPanel ACTION_UP " + getColor());
                        Log.e("TAGGG", "m_viewColorPanel bg_color_temp " + bg_color_temp);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, StringConstants.canvas_colorbar_select, Toast.LENGTH_SHORT).show();
                        }
                        processSteps = "ColorBar_clicked";
                        FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_colorbar_select, post_id);
                    }
                    return false;
                }
            });


            // Color pad for background or Brush color pick
            viewSatVal.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    final int x1 = (int) event.getRawX();
                    final int y1 = (int) event.getRawY();


                    if (event.getAction() == MotionEvent.ACTION_MOVE
                            || (event.getAction() == MotionEvent.ACTION_UP)
                            || event.getAction() == MotionEvent.ACTION_DOWN) {

                        float x = event.getX(); // touch event are in dp units.
                        float y = event.getY();

                        if (x < 0.f) x = 0.f;
                        if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                        if (y < 0.f) y = 0.f;
                        if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                        setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                        setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                        /*if (isBGSelected) {
                            mPrefBackgroundColor = getColor();
                            colorbar_bgcolor.setBackgroundColor(getColor());
                            colorbar_bgcolor.invalidate();
                            //mPainting.setBackgroundColor(getColor());
                            mPainting.setBackgroundColor(getColor());
                            mPainting.clearAndUpdateBGPainting();
                            mPaintView.invalidate();
                            savePaintingPreference();
                        } else {
                            m_viewCurColor.setBackgroundColor(getColor());
                            m_viewCurColor.invalidate();
                        }*/

                        if (isBGSelected) {

                            colorbar_bgcolor.setBackgroundColor(getColor());
                            colorbar_bgcolor.invalidate();

                            //mPainting.setBackgroundColor(getColor());
                            mPainting.setBackgroundColor(getColor());
                            mPainting.clearAndUpdateBGPainting();
                            mPaintView.reDraw(null);
                            mPaintView.invalidate();
                            savePaintingPreference();

                            mPrefBackgroundColor = getColor();
                            Log.e("TAGGG", "m_viewColorPanel ACTION_MOVE mPrefBackgroundColor " + mPrefBackgroundColor);
                        } else {
                            Log.e("TAGGG", "m_viewColorPanel ACTION_MOVE mPrefBrushColor " + mPrefBrushColor + " getColor " + getColor());
                            m_viewCurColor.setBackgroundColor(getColor());
                            m_viewCurColor.invalidate();
                        }


                        // update view
                        moveTarget();

                  /*  Log.e("TAGGG", "Values For Edge x1 " + x1 + " y1 " + y1 + " xDelta " + xDelta + " yDelta " + yDelta);
                    RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) viewTarget
                            .getLayoutParams();
                    LayoutParams.leftMargin = x1 - xDelta;
                    LayoutParams.topMargin = y1 - yDelta;
                    LayoutParams.rightMargin = 0;
                    LayoutParams.bottomMargin = 0;


                    viewTarget.setLayoutParams(LayoutParams);*/

                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {

                  /*  RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                            viewTarget.getLayoutParams();
                    xDelta = x1 - lParams.leftMargin;
                    yDelta = y1 - lParams.topMargin;*/

                        return true;
                    }
                    m_viewColorContainer.invalidate();
                    return false;
                }
            });

            m_viewCurColor.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.e("TAGGG", "m_viewCurColor OnClick Called");
                    try {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.canvas_color_box_select, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_color_box_select);

                        if (switch_gray_scale.isChecked())
                            return;
                        if (mStatus != 1) {
                            hideZoomButton();
//                            mStatus = 1;
                            mPaintView.reDraw(null);
                        }

                        if (m_viewColorContainer.getVisibility() == View.VISIBLE) {

                            Integer oldColor = getColor();
                            m_viewColorContainer.setVisibility(View.INVISIBLE);

                            isBGSelected = false;
                            linear_background_color.setVisibility(View.GONE);
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));

                            if (brushSettingsPopup.isShowing())
                                mStatus = 9;
                            else {
//                                mStatus = 1;
                            }

                            Integer newColor = getColor();
                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                addColorChangeEvent();
                            }
                            setColor();

                        } else {
                            if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    mPaintView.cancelDragAndDrop();
                                iv_gps_icon.setVisibility(View.GONE);
                                selected_bitmap = null;
                                mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
                            }

                            if (current_mode != null && !current_mode.toString().equalsIgnoreCase(canvas_mode.canvas_back.toString()) && iv_cursor_icon.getVisibility() != View.VISIBLE) {
                                iv_cursor_icon.setVisibility(View.VISIBLE);
                            }

                            Color.colorToHSV(mPrefBrushColor, currentColorHsv);

                            // update view
//                            moveCursor();
                            moveTarget();

                            mPaintView.reDraw(null);
                            mStatus = 8;

                            hideZoomButton();
                            m_viewColorContainer.setVisibility(View.VISIBLE);
                            Log.e("TAG", "viewSatVal.setHue from color " + viewSatVal.color[0] + " currentColorHsv " + currentColorHsv[0]);
                            viewSatVal.setHue(viewSatVal.color[0]);


                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                try {
                                    int max_height = (m_viewColorPanel.getMeasuredHeight() - 10);
                                    moveCursor();
                                    viewSatVal.setHue(getHue());
                                    float y = (getHue() * m_viewColorPanel.getMeasuredHeight() / 360.f);
                                    if (y == m_viewColorPanel.getMeasuredHeight()) y = 0.f;
                                    RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) m_viewColorContainer.getLayoutParams();
                                    if (y <= 15) {
                                        LayoutParams.topMargin = 05;
                                    } else if (y >= max_height) {
                                        LayoutParams.topMargin = (max_height - (m_viewColorContainer.getMeasuredHeight() / 2));
                                    } else
                                        LayoutParams.topMargin = (int) (m_viewColorPanel.getTop() + y - m_viewColorContainer.getMeasuredHeight() / 2);

                                    if (LayoutParams.topMargin < 0) {
                                        Log.e("TAG", "Top Params for recover");
                                        LayoutParams.topMargin = 05;
                                    }
                                    Log.e("TAG", "Top Params for container " + LayoutParams.topMargin + " " + getHue() + " y " + y + " max_height " + max_height);
                                    m_viewColorContainer.setLayoutParams(LayoutParams);

                                } catch (Exception e) {
                                    Log.e("TAG", "Exception at set Color box " + e.getMessage());
                                }
                            } else {

                                int max_width = (m_viewColorPanel.getMeasuredWidth() - 10);
                                moveCursorVeritcal();

                                viewSatVal.setHue(getHue());
                                float y = (getHue() * m_viewColorPanel.getMeasuredWidth() / 360.f);
                                if (y == m_viewColorPanel.getMeasuredWidth()) y = 0.f;
                                RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) m_viewColorContainer.getLayoutParams();
                                LayoutParams.leftMargin = (int) (m_viewColorPanel.getLeft() + y - m_viewColorContainer.getMeasuredHeight() / 2);
                                if (y <= 15) {
                                    LayoutParams.leftMargin = 05;
                                } else if (y >= max_width) {
                                    LayoutParams.leftMargin = (max_width - (m_viewColorContainer.getMeasuredHeight() / 2));
                                } else
                                    LayoutParams.leftMargin = (int) (m_viewColorPanel.getTop() + y - m_viewColorContainer.getMeasuredHeight() / 2);

                                if (LayoutParams.leftMargin < 0) {
                                    Log.e("TAG", "Top Params for recover");
                                    LayoutParams.leftMargin = 05;
                                }
                                Log.e("TAG", "Left Params for container " + LayoutParams.topMargin + " " + getHue() + " y " + y + " max_height " + max_width);
                                m_viewColorContainer.setLayoutParams(LayoutParams);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception while draw " + e.getMessage() + " " + e.toString());
                    }
//                moveCursor();
                }
            });

            /*BG color*/
            colorbar_bgcolor.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (linear_background_color.getVisibility() == View.VISIBLE) {
                        return;
                    }

                    disableColorPenMode();

                    if (CurrentMode == 1 || CurrentMode == 3) {
                        Toast.makeText(PaintActivity.this, "Background color cannot be set for Overlay & Camera Drawing", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.e("TAGGG", "m_viewCurColor OnClick Called");
                    try {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.canvas_background_box_select, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_background_box_select);
                        if (mStatus != 1) {
                            hideZoomButton();
//                            mStatus = 1;
                            mPaintView.reDraw(null);
                        }

                        Log.e("TAGGGG", "onClick Select event > " + isBGSelected);

                        if (m_viewColorContainer.getVisibility() == View.VISIBLE) {

                            Integer oldColor = getColor();
//                    setHSVColor(mPrefBrushColor);
                            m_viewColorContainer.setVisibility(View.INVISIBLE);

                            isBGSelected = false;
                            linear_background_color.setVisibility(View.GONE);
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));

                            if (brushSettingsPopup.isShowing())
                                mStatus = 9;
                            else {
//                                mStatus = 1;
                            }
                            isBGSelected = false;
                            colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
//                        mPainting.setBrushColor(getColor());

                            Integer newColor = getColor();
                            Log.e("TAGGG", " oldColor " + oldColor + " newColor " + newColor + " Match " + oldColor.equals(newColor));
                            if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                                addColorChangeEvent();
                            }
                        } else {
                            if (!mPainting.isEmpty()) {
                                showPlus_tooltip(colorbar_bgcolor);
//                                confirmChangeBGColor();
                            } else {
                                colorbar_bgcolor.setBackgroundColor(getColor());
                                colorbar_bgcolor.invalidate();

                                //mPainting.setBackgroundColor(getColor());
                                mPainting.setBackgroundColor(getColor());
                                mPainting.clearAndUpdateBGPainting();
                                mPaintView.reDraw(null);
                                mPaintView.invalidate();
                                savePaintingPreference();

//                                mPrefBackgroundColor = getColor();
                                startBGColor();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception while draw " + e.getMessage() + " " + e.toString());
                    }
//                moveCursor();
                }
            });


            final View view = getWindow().getDecorView();
            ViewTreeObserver vto = view.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        moveCursor();
                    } else {
                        moveCursorVeritcal();
                    }
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        } catch (Exception e) {

        }
    }


    boolean isIndicatorSet = false;

    View.OnTouchListener _touch_listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view1, MotionEvent event) {

//                if (current_mode == canvas_mode.canvas_back) {

            if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                iv_gps_icon.setVisibility(View.GONE);
                mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
            }
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            Log.e("TAGGG", "onTouch called " + current_mode + " x " + x + " y " + y);
            float[] touchPoint = new float[]{event.getX(), event.getY()};
            int xCoord = (int) touchPoint[0];
            int yCoord = (int) touchPoint[1];
            if (xCoord < 0 || yCoord < 0) {
                return true;
            }
            try {
                /*if (yCoord > _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getY()) {
                    yCoord = _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 3).getY();
                    Log.e("TAG", "Color Picker Return set Last " + xCoord + " " + yCoord);
                }*/
                Log.e("TAG", "Color Picker Return Co-ord " + xCoord + " " + yCoord);
                Log.d("currentGrayColor", "currentGrayColor: height:  " + selected_bitmap_gray_scale.getHeight() + " y: " + yCoord);

                int touchedRGB = selected_bitmap_gray_scale.getPixel(xCoord, yCoord);
                //then do what you want with the pixel data, e.g
                int redValue = Color.red(touchedRGB);
                int greenValue = Color.green(touchedRGB);
                int blueValue = Color.blue(touchedRGB);
                int alphaValue = Color.alpha(touchedRGB);
                currentGrayScaleColor = Color.argb(alphaValue, redValue, greenValue, blueValue);

                /*if (yCoord < selected_bitmap_gray_scale.getHeight()) {
                    Log.d(TAG, "currentGrayColor: $currentGrayScaleColor if:  " + currentGrayScaleColor);
                } else {
                    currentGrayScaleColor = _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getColorCode();
                    Log.d(TAG, "currentGrayColor: $currentGrayScaleColor else:  " + currentGrayScaleColor);
                }*/

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("TAG", "setColorInBox Color Picker Return from ACTION_UP color " + currentGrayScaleColor);
                } else {
                    Log.e("TAG", "Color Picker Return from LN Else Action " + event.getAction() + " y " + yCoord + " bitmap Height " + selected_bitmap_gray_scale.getHeight());
                }

                setColorInBox(currentGrayScaleColor);
                mPrefBrushColor = currentGrayScaleColor;
                mBrushColor = currentGrayScaleColor;
                setHSVColor(currentGrayScaleColor);
//                    mPainting.setBrushColor(getColor());
                mPainting.setBrushColor(currentGrayScaleColor);
                m_viewCurColor.setBackgroundColor(currentGrayScaleColor);

            } catch (Exception e) {
                Log.e("grayScaleTouch", "Color Picker Return Exception at _touch_listener " + e.getMessage());
            }

            if (yCoord < selected_bitmap_gray_scale.getHeight()) {
                try {
                    iv_gray_scale_indicator.bringToFront();
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_gray_scale_indicator.getLayoutParams();
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        layoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gray_scale_indicator.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
                        Log.e("tag", "Left Margin Set portrait " + layoutParams.leftMargin + " bottomlimit " + bottomlimit + " topLimit " + topLimit);
                        if (layoutParams.leftMargin >= topLimit && layoutParams.leftMargin <= bottomlimit) {
                            Log.e("potraitMode", "Left Margin Set portrait If " + layoutParams.leftMargin + " " + bottomlimit + " Color " + currentGrayScaleColor);
                            iv_gray_scale_indicator.setLayoutParams(layoutParams);
//                        isIndicatorSet to prevent set image all time.
                            if (!isIndicatorSet) {
                                iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_yellow);
                                isIndicatorSet = true;
                            }
                        } else {
                            if (layoutParams.leftMargin <= 0) {
                                layoutParams.leftMargin = 1;
                                isIndicatorSet = false;
                                iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_red);

                                currentGrayScaleColor = _lst_colors_gray_scale.get(0).getColorCode();
                                setColorInBox(currentGrayScaleColor);
                                mPrefBrushColor = currentGrayScaleColor;
                                mBrushColor = currentGrayScaleColor;
                                setHSVColor(currentGrayScaleColor);
                                mPainting.setBrushColor(currentGrayScaleColor);

                            } else if (layoutParams.leftMargin >= bottomlimit) {

                                currentGrayScaleColor = _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getColorCode();
                                setColorInBox(currentGrayScaleColor);
                                mPrefBrushColor = currentGrayScaleColor;
                                mBrushColor = currentGrayScaleColor;
                                setHSVColor(currentGrayScaleColor);
                                mPainting.setBrushColor(currentGrayScaleColor);

                                layoutParams.leftMargin = bottomlimit;
                                isIndicatorSet = false;
                                iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_red);
                            }
                            iv_gray_scale_indicator.setLayoutParams(layoutParams);
                            Log.e("potraitMode", "Left Margin Set portrait else " + layoutParams.topMargin + " Left Margin " + layoutParams.leftMargin);
                        }
                    } else {
                        layoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gray_scale_indicator.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
                        if (layoutParams.topMargin >= topLimit && layoutParams.topMargin <= bottomlimit) {
                            Log.e("TAG", "Top Margin Set " + layoutParams.topMargin + " " + bottomlimit);
                            iv_gray_scale_indicator.setLayoutParams(layoutParams);
                            if (!isIndicatorSet) {
                                isIndicatorSet = true;
                                iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_yellow);
                            }
                        } else {
                            if (layoutParams.topMargin <= 0) {
                                currentGrayScaleColor = _lst_colors_gray_scale.get(0).getColorCode();
                                setColorInBox(currentGrayScaleColor);
                                mPrefBrushColor = currentGrayScaleColor;
                                mBrushColor = currentGrayScaleColor;
                                setHSVColor(currentGrayScaleColor);
                                mPainting.setBrushColor(currentGrayScaleColor);
                                layoutParams.topMargin = 1;
                                isIndicatorSet = false;
                                iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_red);
                            } else if (layoutParams.topMargin >= bottomlimit) {
                                currentGrayScaleColor = _lst_colors_gray_scale.get(_lst_colors_gray_scale.size() - 1).getColorCode();
                                setColorInBox(currentGrayScaleColor);
                                mPrefBrushColor = currentGrayScaleColor;
                                mBrushColor = currentGrayScaleColor;
                                setHSVColor(currentGrayScaleColor);
                                mPainting.setBrushColor(currentGrayScaleColor);
                                layoutParams.topMargin = bottomlimit;
                                isIndicatorSet = false;
                                iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_red);
                            }
                            iv_gray_scale_indicator.setLayoutParams(layoutParams);
                            Log.e("TAG", "Top Margin Set else " + layoutParams.topMargin + " Left Margin " + layoutParams.leftMargin + " ACTION " + event.getAction());
                        }
                    }
                } catch (Exception e) {
                    Log.e("grayScaleTouch", "Exception at _touch_listener 1 " + e.getMessage());
                }
            }

            return true;
        }
    };


    public int dpToPx(int dp) {
        Log.e("TAG", "dpToPx " + ((int) (dp * Resources.getSystem().getDisplayMetrics().density)));
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    protected void moveCursor() {
        try {
            float y = (getHue() * m_viewColorPanel.getMeasuredHeight() / 360.f);
            if (y == m_viewColorPanel.getMeasuredHeight()) y = 0.f;
            RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) m_viewArrow.getLayoutParams();
            LayoutParams.topMargin = (int) (m_viewColorPanel.getTop() + y - m_viewArrow.getMeasuredHeight() / 2);
            m_viewArrow.setLayoutParams(LayoutParams);
            Log.e("TAG", "Top Margin from the click " + LayoutParams.topMargin + " " + y + " " + getHue());
        } catch (Exception e) {

        }
    }

    protected void moveCursorVeritcal() {
        try {
            float y = (getHue() * m_viewColorPanel.getMeasuredWidth() / 360.f);
            if (y == m_viewColorPanel.getMeasuredWidth()) y = 0.f;
            RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) m_viewArrow.getLayoutParams();
            LayoutParams.leftMargin = (int) (m_viewColorPanel.getLeft() + y - m_viewArrow.getMeasuredHeight() / 2);
            if (LayoutParams.leftMargin <= 0)
                LayoutParams.leftMargin = 1;
            m_viewArrow.setLayoutParams(LayoutParams);
            Log.e("TAG", "moveCursorVeritcal Left Margin " + LayoutParams.leftMargin);
        } catch (Exception e) {

        }
    }


    protected void moveTarget() {
        float x = ((getSat() * viewSatVal.getMeasuredWidth()));
        float y = (((1.f - getVal()) * viewSatVal.getMeasuredHeight()));

        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        LayoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - m_viewColorContainer.getPaddingLeft());
        LayoutParams.topMargin = (int) ((viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - m_viewColorContainer.getPaddingTop()));
        LayoutParams.rightMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - m_viewColorContainer.getPaddingRight());
        LayoutParams.bottomMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - m_viewColorContainer.getPaddingBottom());

        viewTarget.setLayoutParams(LayoutParams);
    }

    public void setColor() {
        Log.e("TAGGG", "setColor Called mPrefBrushColor " + mPrefBrushColor + " new color " + getColor() + " isBGSelected " + isBGSelected);
//        mStatus = 1;
        m_viewColorContainer.setVisibility(View.INVISIBLE);
//        m_brushlayout.setVisibility(View.INVISIBLE);
        brushSettingsPopup.dismiss();
        if (!isBGSelected) {
            mPrefBrushColor = getColor();
            mPainting.setBrushColor(mPrefBrushColor);
            m_viewCurColor.setBackgroundColor(mPrefBrushColor);
        } else {
//            isBGSelected = false;
//            setHSVColor(mPrefBackgroundColor);
        }

        m_viewCurColor.invalidate();
    }

    private void setHSVColor(int color) {
        Color.colorToHSV(color, currentColorHsv);
    }

    private int getColor() {

        return Color.HSVToColor(currentColorHsv);
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }

    protected Dialog onCreateDialog(int pInt) {
        return null;
    }

    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);

        pMenu.add(0, 50, 0, "share").setIcon(R.drawable.icon_share);

        return true;
    }

    public void onDestroy() {
        Log.e("OnDestroy", "OnDestroy");
        try {

            g_bDestroyFlag = false;

            if (selected_bitmap != null) {
                selected_bitmap.recycle();
                selected_bitmap = null;
            }

            if (selected_bitmap_gray_scale != null) {
                selected_bitmap_gray_scale.recycle();
                selected_bitmap_gray_scale = null;
            }
            SharedPreferences.Editor lEditor1 = getPreferences(0).edit();

            if (mPainting != null)
                mPainting.deinit();

            lEditor1.putInt("play_number", mPlayNumbers);
            lEditor1.commit();
            System.gc();

            mSPenEventLibrary.unregisterSPenDetachmentListener(PaintActivity.this);

            mActivity = null;
            super.onDestroy();

            g_bDestroyFlag = true;

            handler.removeCallbacks(runnableCode);

            if (mMediaProjection != null) {
                mMediaProjection.stop();
                mMediaProjection = null;
            }
            if (mMediaRecorder != null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.STOP_RECORDING, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.STOP_RECORDING);
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }

            iv_start_recoring.setTag(0);
            Log.v(TAG, "Recording Stopped");
            stopScreenSharing();

            try {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                    Log.e("TAGG", "Release Exo player");
                }


                if (youTubePlayerView != null) {
                    youTubePlayerView.release();
                    Log.e("TAGG", "Release YT Playe");
                }
            } catch (Exception e) {
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            handler.removeCallbacks(runnableCode);
        }
    }

    public static void finishWork() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences("brush", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        // set Single Tab
        editor.putBoolean("singleTap", false);
        // set line
        editor.putBoolean("line", false);
        // set gray scale
        editor.putBoolean("gray_scale", false);
        editor.putBoolean("block_coloring", false);
        editor.apply();
    }

    public boolean onKey(View pView, int pInt, KeyEvent pKeyEvent) {
        return false;
    }

    public boolean onKeyDown(int pInt, KeyEvent pKeyEvent) {
        Log.e(TAG, "key down FromTutorialMode " + FromTutorialMode + " current_mode " + current_mode + " isPickFromOverlaid " + isPickFromOverlaid + " ");
//        Log.e("TAGG", "onKeyDown called " + current_mode.toString());

        if (pKeyEvent.getKeyCode() != KeyEvent.KEYCODE_BACK)
            return false;

        if ((current_mode != null && current_mode == canvas_mode.canvas_front)) {
            changeToggle();
            return true;
        }

        if ((pInt == 82) && (pKeyEvent.getAction() == 0)) {
            MyDbgLog(TAG, "menu key down");

            if (inMovie())
                return false;
        } else if ((pInt == 4) && (pKeyEvent.getAction() == 0)) {
            MyDbgLog(TAG, "back key down");

            if (inMovie()) {
                MyDbgLog(TAG, "back key close movie");
                stopReplayPaint();
            } else {


                /** back press =========================================================== ========================================================================*/
                Log.e("TAGG", "confirmExit called LN 3729");
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_backpress, Toast.LENGTH_SHORT).show();
                }

                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_backpress);
                paintmenu_close.setTag(1);

                if (!mPainting.isEmpty() && !mPainting.isSaved())
                    saveWorkBeforeContinuingDialog();
                else {
                    Log.e("TAGG", "confirmExit called LN 1874");
                    finishWork();
                    finish();
                }


            }
            return true;
        }
        return super.onKeyDown(pInt, pKeyEvent);
    }


    protected void onSaveInstanceState(Bundle pBundle) {
        super.onSaveInstanceState(pBundle);
        saveState(pBundle);
        MyDbgLog(TAG, "onSaveInstanceState");
    }

    public void onStart() {
        super.onStart();

        MyDbgLog(TAG, "onStart");
        MyDbgLog(TAG, "Flurry start");
    }

    public void onStop() {
        super.onStop();
        MyDbgLog(TAG, "onStop");
        stopMyAdsThread();
    }

    public void quitByOutOfMemory() {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        AlertDialog.Builder lBuilder2 = lBuilder1.setTitle("Sorry!").setMessage("Ops, Paintology has to quit as system free memory is not enough. Please try to close a few app to free some memory, then try again.").setCancelable(false);
        lBuilder2.setNegativeButton("OK", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
                finish();
            }
        });
        lBuilder1.create().show();
    }

    boolean isFromSavePainting = false;
    boolean isFromRecording = false;

    public void savePainting(Integer Tag) {
//        int permission = ActivityCompat.checkSelfPermission(Paintor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            isFromSavePainting = true;
//            ActivityCompat.requestPermissions(
//                    Paintor.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    Tag
//            );
//        }
//        if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
//            // We don't have permission so prompt the user
//            PermissionUtils.requestStoragePermission(Paintor.this, Tag);
//        }
//        else {
//            savePainting(true, Tag);
//        }

        savePainting(true, Tag);

    }

    public void savePainting(boolean bSave, Integer Tag) {
        String drawingType1 = getIntent().getStringExtra("drawingType");

        if (drawingType1 == null) {
            drawingType1 = "freehand";
        }

        savePaintingPreference();

        Log.e("drawingType", "saveAndExitpainting: " + drawingType1);

        if (mPainting.getBackgroundColor() != -1 || !mPainting.isEmpty() || isFromEditImage || isPickFromOverlaid) {
            if (System.currentTimeMillis() - mTimeOfPreviousSave > 3000L) {
                mTimeOfPreviousSave = System.currentTimeMillis();
                if (isProVersion) {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, bSave, PaintActivity.this,
                            false, Tag, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                } else {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, bSave, PaintActivity.this,
                            false, Tag, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                }
            }
        } else {
            if (Tag != -1) {
                /*if (CurrentMode == 0) {
                    confirmExit(true);
                } else {*/
                mTimeOfPreviousSave = System.currentTimeMillis();

                if (isProVersion) {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, bSave, PaintActivity.this,
                            false, Tag, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                } else {
                    mImageManager.savePaintingToFile(strokeCount, youtube_video_id, selectedImagePath, mPainting, bSave, PaintActivity.this,
                            false, Tag, post_id, swatchesJson, colorPalette, isPickFromOverlaid, drawingType1);
                }
                //}
            }
        }
    }

    public boolean shallRecover() {
        return true;
    }

    //    public void startRecoveryDaemon() {
////        mHandler.postDelayed(mRecoveryDaemon, 30000L);
////    }
    public void stopReplayPaint() {
        if (inMovie()) {
            mPainting.stopReplayPainting();
//            mStatus = 1;
        }

        hideMoviePlayerMenuBar();

    }

    public void stoptRecoveryDaemon() {
    }

    @Override
    public void setColorInBox(int code) {
        Log.e("TAGGGGG", "setColorInBox " + code);
        m_viewCurColor.setBackgroundColor(code);
        PushPickcolor(code);
        try {
            if (code != -1 && switch_gray_scale.isChecked()) {
                view_cross2.setVisibility(View.VISIBLE);
                Log.e("TAGRR", "yess 1" + code);
                axisAnDColor _object = getAxisFromColor(code);
                setIndicatorPos(_object);
            } else {
                view_cross2.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at setColorInBox " + e.getMessage());
        }
    }

    private void releasePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mPaintView.cancelDragAndDrop();
        iv_gps_icon.setVisibility(View.GONE);
        selected_bitmap = null;
        mColorPopupWindow = null;
        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
//                mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
        if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
            iv_cursor_icon.setVisibility(View.VISIBLE);
        }

        Log.e("TAG", "Hue Value on Click " + getHue());
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            moveCursor();
        else {
            moveCursorVeritcal();
        }
    }


    void setIndicatorPos(axisAnDColor _object) {
        try {

            final int x = _object.getX();
            final int y = _object.getY();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_gray_scale_indicator.getLayoutParams();
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gray_scale_indicator.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
                Log.e("TAG", "Left Margin Set portrait " + layoutParams.leftMargin + " bottomlimit " + bottomlimit + " topLimit " + topLimit);
                if (layoutParams.leftMargin >= topLimit && layoutParams.leftMargin <= bottomlimit) {
                    Log.e("TAG", "Left Margin Set portrait If " + layoutParams.leftMargin + " " + bottomlimit + " Color " + currentGrayScaleColor);
                    iv_gray_scale_indicator.setLayoutParams(layoutParams);
//                        isIndicatorSet to prevent set image all time.
                    if (!isIndicatorSet) {
                        iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_yellow);
                        isIndicatorSet = true;
                    }
                } else {
                    if (layoutParams.leftMargin <= 0) {
                        layoutParams.leftMargin = 1;
                        isIndicatorSet = false;
                        iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_red);
                    } else if (layoutParams.leftMargin >= bottomlimit) {
                        layoutParams.leftMargin = bottomlimit;
                        isIndicatorSet = false;
                        iv_gray_scale_indicator.setImageResource(R.drawable.indicatore_red);
                    }
                    iv_gray_scale_indicator.setLayoutParams(layoutParams);
                    Log.e("TAG", "Left Margin Set portrait else " + layoutParams.topMargin + " Left Margin " + layoutParams.leftMargin);
                }
            } else {
                layoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gray_scale_indicator.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
                if (layoutParams.topMargin >= topLimit && layoutParams.topMargin <= bottomlimit) {
                    Log.e("TAG", "Top Margin Set " + layoutParams.topMargin + " " + bottomlimit);
                    iv_gray_scale_indicator.setLayoutParams(layoutParams);
                    if (!isIndicatorSet) {
                        isIndicatorSet = true;
                        iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_yellow);
                    }
                } else {
                    if (layoutParams.topMargin <= 0) {
                        layoutParams.topMargin = 1;
                        isIndicatorSet = false;
                        iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_red);
                    } else if (layoutParams.topMargin >= bottomlimit) {
                        layoutParams.topMargin = bottomlimit;
                        isIndicatorSet = false;
                        iv_gray_scale_indicator.setImageResource(R.drawable.hor_indicator_red);
                    }
                    iv_gray_scale_indicator.setLayoutParams(layoutParams);
                    Log.e("TAG", "Top Margin Set else " + layoutParams.topMargin + " Left Margin " + layoutParams.leftMargin);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at setindicator " + e.getMessage());
        }
    }

    @Override
    public boolean isIndicatorVisible() {
        return iv_gps_icon.getVisibility() == View.VISIBLE ? true : false;
    }

    @Override
    public void clearPaintingAndSetNew() {
        clearPainting();
    }

    @Override
    public void disableColorPenMode() {
        if (iv_gps_icon.getVisibility() == View.VISIBLE) {
            iv_gps_icon.setVisibility(View.GONE);
            selected_bitmap = null;
            mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
//            mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
        }
    }


    /**
     * Exit Activity ============================
     */

    @Override
    public void exitFromAPP() {
        try {

            finishWork();
            int _tag = (int) paintmenu_close.getTag();
            Log.e("TAGGG", "exitFromAPP Called _tag " + _tag);
            if (_tag == 0) {
                Intent intent = new Intent(PaintActivity.this, GalleryDashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                finish();
            }

        } catch (Exception e) {
            Log.e("TAGG", "Exception at exit");
        }


    }

    @Override
    public void showCursor() {
        if (iv_cursor_icon.getVisibility() != View.VISIBLE) {
            iv_cursor_icon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isInTutorialMode() {
        return isFromEditImage;
    }

    @Override
    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    @Override
    public void resetTimer() {
        restartTimer();
    }

    ArrayList<EventModel> strokeList = new ArrayList<>();
    ArrayList<EventModel> TempListForRedo = new ArrayList<>();

    @Override
    public void addStroke(String strokeData) {
        EventModel model = new EventModel();
        model.setStrokeAxis(strokeData);
        model.setTimeStamp(second_indicator + "");
        //  SharedPreferences lSharedPreferences = getPreferences(0);
        SharedPreferences lSharedPreferences = getSharedPreferences("brush", 0);

        try {
            if (mPainting.getStrokeList() != null && mPainting.getStrokeList().size() == 1) {
                // old code
                orientation = this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                Log.e("TAG", "addStroke called");
            }
        } catch (Exception e) {
            Log.e("TAG", "addStroke called on exception " + e.getMessage());
        }


        if (post_id != -1) {
            strokeCount++;
        }
        if (drawingType1.equalsIgnoreCase("TUTORAILS") && post_id != -1) {
            FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.draw_strokes, String.valueOf(post_id));
        }

        try {
            if (youTubePlayerView != null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_tsstrokes_draw_strokes, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_tsstrokes_draw_strokes, post_id);
            } else if (simpleExoPlayerView != null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_strokes_draw_strokes, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_strokes_draw_strokes, post_id);
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, StringConstants.canvas_draw_stroke, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, StringConstants.canvas_draw_stroke, post_id);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }

// && lSharedPreferences.getInt("brush-style", Brush.LineBrush) != 112
        if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {

            if (lSharedPreferences.getString("pref-saved", null) != null) {

                model.objChangeData.setBrushStyle(lSharedPreferences.getInt("brush-style", Brush.LineBrush) + "");

                model.objChangeData.setBrushColor(mPrefBrushColor + "");

//                model.objChangeData.setBrushSize(lSharedPreferences.getFloat("brush-size", 8.0F) + "");
                model.objChangeData.setBrushSize(mPrefBrushSize + "");

                model.objChangeData.setBrushFlow(lSharedPreferences.getInt("brush-flow", 65) + "");

                model.objChangeData.setBrushAlpha(lSharedPreferences.getInt("brush-alpha", 255) + "");

                model.objChangeData.setBrushHardness(lSharedPreferences.getInt("brush-pressure", 65) + "");

                model.getObjChangeData().setBrushName(getStyleName(lSharedPreferences.getInt("brush-style", Brush.LineBrush)));
            }
            strokeList.add(model);
            model.setEventType(EventType.NEW_STROKE + "");
            addEventInList(model);
        }
//        Log.e("TAGGG", "addStroke Logs stroke size " + model.getObjChangeData().getBrushSize() + "From variable " + mPrefBrushSize + " Style From Preference " + lSharedPreferences.getInt("brush-style", Brush.LineBrush));
    }

    @Override
    public void addRemoveStrokeInRedoList(boolean isAdd) {
        try {
            if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                if (isAdd) {
                    if (strokeList.size() > 0) {
                        Log.e("TAGGG", "addRemoveStrokeInRedoList isAdd " + isAdd + " List Size " + strokeList.size() + " TempListForRedo size " + TempListForRedo.size());
                        EventModel obj = strokeList.get(strokeList.size() - 1);
                        TempListForRedo.add(obj);

                        strokeList.remove(strokeList.size() - 1);

                        Log.e("TAGGG", "addRemoveStrokeInRedoList isAdd " + isAdd + " List Size " + strokeList.size() + " TempListForRedo size " + TempListForRedo.size());
                    }
                } else {
                    Log.e("TAGGG", "addRemoveStrokeInRedoList isAdd " + isAdd + " List Size " + strokeList.size() + " TempListForRedo size " + TempListForRedo.size());
                    EventModel obj = TempListForRedo.get(TempListForRedo.size() - 1);
                    strokeList.add(obj);
                    TempListForRedo.remove(TempListForRedo.size() - 1);
                    Log.e("TAGGG", "addRemoveStrokeInRedoList isAdd " + isAdd + " List Size " + strokeList.size() + " TempListForRedo size " + TempListForRedo.size());
                }
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception while addRemoveStrokeInRedoList " + e.getMessage());
        }
    }

    @Override
    public void clearAddRemoveStrokeInRedoList() {
        try {
            if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                if (TempListForRedo != null && TempListForRedo.size() != 0) {
                    TempListForRedo.clear();
                }
            }
        } catch (Exception e) {

        }
    }


    int windowwidth;
    int windowheight;


//    @Override
//    public boolean onDrag(final View view, final DragEvent event) {
//        int action = event.getAction();
//
//        Log.e("TAGGG", "DragEvent " + event.getX() + " " + event.getY() + " action " + action);
//        // Handles each of the expected events
//        final int x = (int) event.getX();
//        final int y = (int) event.getY();
//
//
//        switch (action) {
//            case DragEvent.ACTION_DRAG_STARTED:
//
//                Log.e("TAGG", "onDrag Logs ACTION_DRAG_STARTED " + action);
//                // Determines if this View can accept the dragged data
//                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                    return true;
//                }
//
//                return false;
//
//            case DragEvent.ACTION_DRAG_ENTERED:
//                // Invalidate the view to force a redraw in the new tint
//                view.invalidate();
//
//                return true;
//            case DragEvent.ACTION_DRAG_LOCATION:
//                // Ignore the event
//
//                Log.e("TAGGGG", "onDrag called ACTION_HOVER_MOVE " + mPaintView.mScale);
//
//
//               /* runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (x > 0 && y > 0) {
//                            RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) iv_gps_icon.getLayoutParams();
//                            LayoutParams.leftMargin = (int) (mPaintView.getLeft() + x - Math.floor(iv_gps_icon.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft() + dpToPx(45));
//                            LayoutParams.topMargin = (int) (mPaintView.getTop() + y - Math.floor(iv_gps_icon.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
//
//                            iv_gps_icon.setLayoutParams(LayoutParams);
//
//                            if (mPainting.getBitmap() != null) {
//
//                                float[] touchPoint = new float[]{event.getX(), event.getY()};
//
//                                int xCoord = (int) touchPoint[0];
//                                int yCoord = (int) touchPoint[1];
//
//                                try {
//
//                                    int touchedRGB = 0;
//
//                                    if (current_mode != null && isFromEditImage && alphaValue == 10 && current_mode == canvas_mode.canvas_mid) {
//                                        if (selected_bitmap == null) {
//                                            Bitmap temp = ((BitmapDrawable) iv_selected_image.getDrawable()).getBitmap();
//                                            selected_bitmap = Bitmap.createScaledBitmap(temp, view.getWidth(), view.getHeight(), true);
//                                        }
//                                        touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);
//                                    } else {
//                                        if (mPainting.getBitmap() != null) {
//                                            touchedRGB = mPainting.getBitmap().getPixel(xCoord, yCoord);
//                                        }
//                                    }
//
////                                    int touchedRGB = mPainting.getBitmap().getPixel(xCoord, yCoord);
//
//                                    //then do what you want with the pixel data, e.g
//                                    int redValue = Color.red(touchedRGB);
//                                    int greenValue = Color.green(touchedRGB);
//                                    int blueValue = Color.blue(touchedRGB);
//                                    int alphaValue = Color.alpha(touchedRGB);
//
//
//                                    colorValue = Color.argb(alphaValue, redValue, greenValue, blueValue);
//                                    hexCode = toHex(alphaValue, redValue, greenValue, blueValue);
//                                    Log.e("TAGGG", "colorValue from argb " + colorValue);
//
//                                    setColorInBox(colorValue);
//                                    mPrefBrushColor = colorValue;
//                                    mPainting.setBrushColor(mPrefBrushColor);
//
//                                    Log.e("TAGGG", "HEX CODE " + toHex(alphaValue, redValue, greenValue, blueValue));
//                                } catch (Exception e) {
//
//                                }
//                            }
//                        }
//                    }
//                });*/
//                return true;
//            case DragEvent.ACTION_DRAG_EXITED:
//                view.invalidate();
//                return true;
//            case DragEvent.ACTION_DROP:
//                // Gets the item containing the dragged data
//                ClipData.Item item = event.getClipData().getItemAt(0);
//
//                // Gets the text data from the item.
//                String dragData = item.getText().toString();
//
//
//                view.invalidate();
//             /*   View v = (View) event.getLocalState();
//                ViewGroup owner = (ViewGroup) v.getParent();
//                owner.removeView(v);//remove the dragged view
//                REl container = (LinearLayout) view;//caste the view into LinearLayout as our drag acceptable layout is LinearLayout
//                container.addView(v);//Add the dragged view*/
////                view.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE
//
//                return true;
//            case DragEvent.ACTION_DRAG_ENDED:
//
//                view.invalidate();
//
//               /* // Does a getResult(), and displays what happened.
//                if (event.getResult())
//                    Toast.makeText(this, "The drop was handled.", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_SHORT).show();
//                */
//
////                iv_gps_icon.setVisibility(View.VISIBLE);
//
//                // returns true; the value is ignored.
//                return false;
//
//            // An unknown action type was received.
//            default:
//                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
//                break;
//        }
//        return false;
//    }

    @Override
    public boolean onLongClick(View view) {
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        try {
//            Toast.makeText(this, "Drag Cursor Now!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);

        // Instantiates the drag shadow builder.
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

        // Starts the drag
        view.startDrag(data//data to be dragged
                , shadowBuilder //drag shadow
                , view//local data about the drag and drop operation
                , 0//no needed flags
        );

        //Set view visibility to INVISIBLE as we are going to drag the view
//        view.setVisibility(View.INVISIBLE);
        return true;
    }

    class Paintor6 extends Handler {
        public void handleMessage(Message pMessage) {
            PaintActivity lPaintActivity = PaintActivity.this;
        }
    }

    class Paintor1 implements Runnable {
        public void run() {
            Log.e("TAG", "Paintor1 called");
            try {
                Log.e("run", "getScreenSize");
                getScreenSize();
                mScreenWidth = mPaintView.getWidth();
                mScreenHeight = mPaintView.getHeight();
                int i = mScreenWidth;
                int j = mScreenHeight;

                System.gc();
                createPaintingTemp(i, j);
                Log.e("TAGGG", "canvas_mode Current 11> " + ((i == 0) || (j == 0)));
                if ((i == 0) || (j == 0)) {
                    mHandler.postDelayed(mCreatePaintingRunnable, 50L);
                } else {
                    Log.e("TAGGG", "canvas_mode Current 12> " + (g_bDestroyFlag));
                    if (g_bDestroyFlag) {
                        /*moveCursor();*/
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            moveCursor();
                        } else {
                            moveCursorVeritcal();
                        }
//                    if (!getIntent().hasExtra("isPickFromOverlaid"))
                        createPainting(i, j);
                        startPainting();
                        m_bInitFlag = true;
                        mPaintView.reDraw(null);
                        mPaintViewTemp.reDraw(null);
                    } else {
                        mHandler.postDelayed(mCreatePaintingRunnable, 50L);
                    }
                }
                Log.e("TAG", "Paintor1 called end");
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at setup Paintor " + e.getMessage());
            }
        }
    }


    private String getPath(Uri uri) {
        String[] projection;
        Cursor cursor = null;
        int column_index = 0;
        try {
            projection = new String[]{MediaStore.Images.Media.DATA};
            if (projection != null) {
                cursor = managedQuery(uri, projection, null, null, null);
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        cursor.moveToFirst();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at getpath " + e.getMessage());
        }
        return (cursor != null ? cursor.getString(column_index) : "");
    }

    int colorValue;
    String hexCode;

    canvas_mode current_mode;

    String dirName = "/Paintology/Paintology Collection/";


    void changeToggle() {
        Log.e("TAGGG", "current_mode select " + current_mode);
        try {
            if (!FromTutorialMode) {
                if (current_mode == canvas_mode.canvas_back) {
//                    currentBitmap = mPainting.getBitmap();
                    current_mode = canvas_mode.canvas_front;
                    iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
                    if (iv_toggle_preview_tooltip != null) {
                        iv_toggle_preview_tooltip.setImageResource(R.drawable.toggle_vid_new);
                    }
                    mPaintView.setVisibility(View.VISIBLE);

                    iv_selected_image.setVisibility(View.VISIBLE);
                    mPaintView.invalidate();
                    if (iv_cursor_icon.getVisibility() != View.VISIBLE)
                        iv_cursor_icon.setVisibility(View.VISIBLE);

                    if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            mPaintView.cancelDragAndDrop();
                        iv_gps_icon.setVisibility(View.GONE);
                        selected_bitmap = null;
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
//                        mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
                    }
                    if (youTubePlayerView != null && youTubePlayerView.getVisibility() != View.VISIBLE) {
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);
                        YTPlayer.play();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_pcollection_canvas_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_pcollection_canvas_click);
                    }


                } else if (current_mode == canvas_mode.canvas_front) {
                    if (iv_cursor_icon.getVisibility() == View.VISIBLE)
                        iv_cursor_icon.setVisibility(View.GONE);


                    current_mode = canvas_mode.canvas_back;
                    iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);

                    if (iv_toggle_preview_tooltip != null) {
                        iv_toggle_preview_tooltip.setImageResource(R.drawable.toggle_vid_new);
                    }

                    iv_selected_image.setVisibility(View.VISIBLE);
                    if (youTubePlayerView != null && youTubePlayerView.getVisibility() == View.VISIBLE) {
                        youTubePlayerView.setVisibility(View.GONE);
                        mPaintView.setVisibility(View.VISIBLE);

                        if (PaintingType == "save_trace_canvas_drawing") {
                            seekbar_1.setVisibility(View.VISIBLE);
                            seekBarContainer4.setVisibility(View.VISIBLE);
                            hideShowSeekbarView(false);
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_pcollection_video_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_pcollection_video_click);
                        YTPlayer.pause();
                    } else
                        mPaintView.setVisibility(View.GONE);

                    String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                    if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                        if (!isHintShowed) {
                            isHintShowed = true;
                            showStrokeHintDialog();
                            hideCanvasHintDialog();
                        }
                    }
                }
            } else {
                if (current_mode == canvas_mode.canvas_front) {

                    if (iv_switch_to_player != null)
                        iv_switch_to_player.setVisibility(View.VISIBLE);
                    if (iv_cursor_icon.getVisibility() == View.VISIBLE)
                        iv_cursor_icon.setVisibility(View.GONE);
                    current_mode = canvas_mode.canvas_back;
                    iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
                    if (iv_toggle_preview_tooltip != null) {
                        iv_toggle_preview_tooltip.setImageResource(R.drawable.toggle_vid_new);
                    }
                    mPaintView.setVisibility(View.VISIBLE);
                    seekbar_1.setVisibility(View.VISIBLE);
                    seekBarContainer4.setVisibility(View.VISIBLE);
                    hideShowSeekbarView(false);

                    if (youTubePlayerView != null && youTubePlayerView.getVisibility() == View.VISIBLE) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_pcollection_video_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_pcollection_video_click);
                        youTubePlayerView.setVisibility(View.GONE);

                        if (YTPlayer != null)
                            YTPlayer.pause();
                    }
                    if (simpleExoPlayerView != null && simpleExoPlayerView.getVisibility() == View.VISIBLE) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_user_video_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_user_video_click);
                        simpleExoPlayerView.setVisibility(View.GONE);
                        iv_hide_exo_player.setVisibility(View.GONE);
                        if (player != null) {
                            player.setPlayWhenReady(false);
                            player.getPlaybackState();
                        }
                    }
                    iv_temp_traced.setVisibility(View.VISIBLE);
                    ll_bottom_bar.setVisibility(View.VISIBLE);
                    img_community.setVisibility(View.GONE);
                    iv_play_pause.setVisibility(View.VISIBLE);

                    String need_to_show_selected = constants.getString(constants.is_dont_show_selected, PaintActivity.this);
                    if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                        if (!isHintShowed) {
                            isHintShowed = true;
                            showStrokeHintDialog();
                            hideCanvasHintDialog();
                        }
                    }
                } else if (current_mode == canvas_mode.canvas_back) {
                    if (iv_switch_to_player != null)
                        iv_switch_to_player.setVisibility(View.GONE);
                    ll_bottom_bar.setVisibility(View.GONE);
                    iv_play_pause.setVisibility(View.GONE);
                    current_mode = canvas_mode.canvas_front;
                    iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
                    if (iv_toggle_preview_tooltip != null) {
                        iv_toggle_preview_tooltip.setImageResource(R.drawable.toggle_vid_new);
                    }
                    mPaintView.setVisibility(View.GONE);
                    iv_temp_traced.setVisibility(View.GONE);
                    iv_selected_image.setVisibility(View.GONE);
                    mPaintView.invalidate();
                    mPaintViewTemp.invalidate();
                    if (iv_cursor_icon.getVisibility() != View.VISIBLE)
                        iv_cursor_icon.setVisibility(View.VISIBLE);
                    if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            mPaintView.cancelDragAndDrop();
                        iv_gps_icon.setVisibility(View.GONE);
                        selected_bitmap = null;
                        mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
//                        mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
                    }
                    if (youTubePlayerView != null && youTubePlayerView.getVisibility() != View.VISIBLE) {
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_pcollection_canvas_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_pcollection_canvas_click);
                        if (YTPlayer != null)
                            YTPlayer.play();
                    }
                    if (simpleExoPlayerView != null && simpleExoPlayerView.getVisibility() != View.VISIBLE) {
                        simpleExoPlayerView.setVisibility(View.VISIBLE);
                        iv_hide_exo_player.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.GONE);
                        seekBarContainer4.setVisibility(View.GONE);
                        hideShowSeekbarView(true);

                        if (player != null) {
                            player.setPlayWhenReady(true);
                            player.getPlaybackState();
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.toggle_user_canvas_click, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.toggle_user_canvas_click);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at change toggle " + e.getMessage() + " " + e.toString());
        }
    }

    enum canvas_mode {
        canvas_front, canvas_mid, canvas_back;
    }


    protected void onPause() {
        super.onPause();
        try {
            Log.i(TAG, "onPause - store preference");
            savePaintingPreference();
            stoptRecoveryDaemon();
            isPaused = true;
            if (!FromTutorialMode)
                handler.removeCallbacks(runnableCode);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (mMediaRecorder != null & tv_recording_time.getTag().equals(recordingState.In_Resume) && !tv_recording_time.getTag().equals(recordingState.In_Pause)) {
                    try {
                        mMediaRecorder.pause();
                    } catch (IllegalStateException e) {

                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.PAUSE_RECORDING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, constants.PAUSE_RECORDING);
                    tv_recording_time.setTag(recordingState.In_Pause);
                    Toast.makeText(this, "Media Recorder Pause", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception on pause 5317 " + e.getMessage());
        }

        try {
            if (player != null) {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            }
        } catch (Exception e) {

        }

        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(PaintActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, PaintActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, PaintActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called Paintor 4796");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }

    public void onResume() {
        super.onResume();
        isPaused = false;
        Log.e(TAG, "onResume - restore preference");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (mMediaRecorder != null & tv_recording_time.getTag().equals(recordingState.In_Pause) && !isManuallyPauseRecording) {
                    mMediaRecorder.resume();
                    tv_recording_time.setTag(recordingState.In_Resume);
                    Toast.makeText(this, "Media Recorder Resume", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IllegalStateException ise) {
        } catch (Exception e) {
        }

        if (!FromTutorialMode)
            handler.postDelayed(runnableCode, SAVE_INTERVAL_TIME);

//        try {
//            if (player != null) {
//                player.setPlayWhenReady(true);
//                player.getPlaybackState();
//            }
//        } catch (Exception e) {
//
//        }

        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            isLoggedIn = accessToken != null && !accessToken.isExpired();
            account = GoogleSignIn.getLastSignedInAccount(PaintActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, PaintActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, PaintActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called Paintor 4838");
                    MyApplication.get_realTimeDbUtils(this).setOnline(_user_id);
                }
            }
        } catch (Exception e) {

        }

    }


    public class saveImageInBack extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String str7 = "";
            if (mPainting != null) {
                Bitmap lBitmap = mPainting.getPainting();
                str7 = saveImage(lBitmap, "temp");
            }
            return str7;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.isEmpty(s)) {
//                Toast.makeText(Paintor.this, "Failed to save painting. Please check if SD card is avaialbe OR Storage Permission.", Toast.LENGTH_LONG).show();
                return;
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.auto_save_drawing_10mins, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.auto_save_drawing_10mins);
            Log.e("TAGG", "File Saved In " + s);
            Toast.makeText(PaintActivity.this, "auto saved drawing to My Paintings", Toast.LENGTH_LONG).show();
        }
    }

    private void saveTypeInfo(Context context, String imagePath, String type) {
        String metadataPath = imagePath.replace(".png", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataPath))) {
            try {
                SharedPreferences sharedPref = context.getSharedPreferences("brush", 0);
                JSONObject object = new JSONObject();
                object.put("singleTap", switch_singleTap.isActivated());
                object.put("line", _switch_line.isActivated());
                object.put("gray_scale", switch_gray_scale.isActivated());
                object.put("block_coloring", switch_block_coloring.isActivated());
                object.put("background-color", mPrefBackgroundColor);
                object.put("brush-style", mPrefBrushStyle);
                object.put("brush-size", mPrefBrushSize);
                object.put("brush-color", mPrefBrushColor);
                object.put("brush-alpha", mPrefAlpha);
                object.put("brush-pressure", mPrefAlpha);
                object.put("brush-flow", mPrefFlow);
                object.put("brush-mode", mPrefBrushMode);
                object.put("type", type);
                writer.write(object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("ImageManager", "Error saving type info", e);
        }
    }


    public String saveImage(Bitmap pBitmap, String pString) {
        if (pBitmap == null)
            return null;

        if (!hasStorage(true)) {
            int i = Log.v("ImageManager", "no storage, savePaintingToFile");
            try {
//                Toast.makeText(Paintor.this, "Failed to save painting. Please check if SD card is avaialbe.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
            return null;
        }
        if (pString == null)
            pString = "temp";

        String fileName = pString;
        String fileNameWithExt = fileName + ".png";

        if (!pString.equals("temp")) {
            runOnUiThread(() -> {
                if (isPickFromOverlaid) {
                    storeInOverlayList(fileName);
                } else {
                    storeInTraceList(fileName);
                }
            });

        }
        String rootFolderPath = KGlobal.getMyPaintingFolderPath(PaintActivity.this);
        String filePath = rootFolderPath + "/" + fileNameWithExt;

        OutputStream imageOutStream = null;

        File lFile = new File(filePath);
        try {
            imageOutStream = new FileOutputStream(lFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String type = getIntent().getStringExtra("drawingType");

        if (type == null) {
            type = "freehand";
        }

        saveTypeInfo(this, filePath, type);

        AppDatabase appDatabase = MyApplication.getDb();
        PaintingDao paintingDao = appDatabase.paintingDao();
        SavedDrawingDao savedDrawingDao = db.savedDrawingDao();

        // Inserting a painting
        String finalType = type;
        new Thread(() -> {
            PaintingEntity painting = new PaintingEntity(0, fileNameWithExt, finalType, false);
            paintingDao.insertPainting(painting);

            if (post_id != -1) {

                SavedDrawingEntity savedDrawingEntity = new SavedDrawingEntity();
                savedDrawingEntity.postId = post_id;
                savedDrawingEntity.modifiedDate = System.currentTimeMillis();
                savedDrawingEntity.youtubeVideoId = youtube_video_id;

                if (!TextUtils.isEmpty(swatchesJson)) {
                    savedDrawingEntity.swatches = swatchesJson;
                }

                if (!TextUtils.isEmpty(colorPalette)) {
                    savedDrawingEntity.colorPalette = colorPalette;
                }

                savedDrawingEntity.localPath = filePath;
                savedDrawingEntity.strokeCount = strokeCount;
                savedDrawingEntity.originPath = selectedImagePath;

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        savedDrawingDao.insertAll(savedDrawingEntity);
                    }
                });
            }

        }).start();

        try {
            if (imageOutStream != null) {
                BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(imageOutStream);
                Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
                boolean bool = pBitmap.compress(lCompressFormat, 90, lBufferedOutputStream);
                lBufferedOutputStream.flush();
                lBufferedOutputStream.close();
                String str5 = rootFolderPath;
                String str6 = str5 + "/" + fileNameWithExt;
                String str7 = "saved to sdcard/Paintology/ as " + fileNameWithExt;
                return str7;
            }

        } catch (FileNotFoundException lFileNotFoundException) {
            Log.e("TAGGG", "");
            return null;
        } catch (IOException lIOException) {
            return null;
        }
        return null;
    }

    public boolean hasStorage(boolean pBoolean) {
        String str = Environment.getExternalStorageState();
        if ("mounted".equals(str)) {
            if (!pBoolean)
                return checkFsWritable();
        } else if ((!pBoolean) && ("mounted_ro".equals(str))) {
            return false;
        }
        return true;
    }

    private boolean checkFsWritable() {
        int i = 0;
        String str = Environment.getExternalStorageDirectory().toString();
        File lFile = new File(str, ".probe");
        try {
            if (lFile.exists())
                lFile.delete();
            if (!lFile.createNewFile()) {
                return false;
            } else {
                lFile.delete();
                return true;
            }
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
            return false;
        }
    }

    public enum recordingState {
        In_Pause, In_Resume, In_Idle;
    }

    void addEventInList(EventModel model) {
        eventModelList.add(model);
       /* for (int i = 0; i < eventModelList.size(); i++) {
            Log.e("TAGG", "Event Data From List Time Stamp " + eventModelList.get(i).getTimeStamp());
            Log.e("TAGG", "Event Data From List Brush Color " + eventModelList.get(i).getBrushColor());
            Log.e("TAGG", "Event Data From List is Color Change " + eventModelList.get(i).isColorchange);
            Log.e("TAGG", "Event Data From List Brush Flow " + eventModelList.get(i).getObjChangeData().getBrushFlow());
            Log.e("TAGG", "Event Data From List Brush Alpha " + eventModelList.get(i).getObjChangeData().getBrushAlpha());
            Log.e("TAGG", "Event Data From List Brush Size " + eventModelList.get(i).getObjChangeData().getBrushSize());
            Log.e("TAGG", "Event Data From List Brush Color " + eventModelList.get(i).getObjChangeData().getBrushColor());
            Log.e("TAGG", "Event Data From List Brush Hardness " + eventModelList.get(i).getObjChangeData().getBrushHardness());
            Log.e("TAGG", "Event Data From List Brush Style " + eventModelList.get(i).getObjChangeData().getBrushStyle());
            Log.e("TAGG", "Event Data From List Brush Name " + eventModelList.get(i).getObjChangeData().getBrushName());

            Log.e("TAGG", "Event Data From List *************************************************");
        }*/
    }

    String getStyleName(int style) {
        switch (style) {
            case 624:
                return constants.getPICK_GRASS_BRUSH();//"grass";
            case 96:
                return constants.getPICK_EMBOSS_BRUSH();//"emboss";
            case 642:
                return constants.getPICK_HAZE_DARK_BRUSH();//"haze dark";
            case 81:
                return constants.getPICK_LINE_BRUSH();//"line";
            case 784:
                return constants.getPICK_MIST_BRUSH();//"mist";
            case 640:
                return constants.getPICK_HAZE_LIGHT_BRUSH();//"haze light";
            case 608:
                return constants.getPICK_LAND_PATCH_BRUSH();//"land-patch";
            case 656:
                return constants.getPICK_MEADOW_BRUSH();//"meadow";
            case 768:
                return constants.getPICK_INDUSTRY_BRUSH();//"industry";
            case 512:
                return constants.getPICK_CHALK_BRUSH();//"chalk";
            case 576:
                return constants.getPICK_CHARCOAL_BRUSH();//"charcoal";
            case 528:
                return constants.getPICK_STICKS_BRUSH();//"sticks";
            case 592:
                return constants.getPICK_FLOWER_BRUSH();//"flower";
            case 560:
                return constants.getPICK_WAVE_BRUSH();//"wave";
            case 112:
                return constants.getPICK_ERASER_BRUSH();//"eraser";
            case 80:
                return constants.getPICK_SHADE_BRUSH();//"shade";
            case 55:
                return constants.getPICK_WATERCOLOR_BRUSH();//"watercolor";
            case 272:
                return constants.getPICK_SKETCH_OVAL_BRUSH();//"sketch oval";
            case 256:
                return constants.getPICK_SKETCH_FILL_BRUSH();//"sketch fill";
            case 64:
                return constants.getPICK_SKETCH_PEN_BRUSH();//"sketch pen";
            case 257:
                return constants.getPICK_SKETCH_WIRE_BRUSH(); //"sketch wire";
        }
        return "";
    }

    @Override
    public void storeInTraceList(String drawingFileName) {
        Log.e("TAGG", "storeInTraceList logs " + PaintingType + " drawingFileName " + drawingFileName);
        if (isFromEditImage) {
            String mainFilePath = selectedImagePath;
            if (!selectedImagePath.contains("/")) {
                mainFilePath = KGlobal.getMyPaintingFolderPath(PaintActivity.this) + "/" + mainFilePath.replaceFirst("[.][^.]+$", "") + "_Trace.png";
            }

            File sourceFile = new File(mainFilePath);
            File myPaintingFolder = new File(KGlobal.getMyPaintingFolderPath(PaintActivity.this));
            if (!myPaintingFolder.exists()) {
                myPaintingFolder.mkdirs();
            }
            try {
                int i = sourceFile.getName().lastIndexOf('.');
                String name = System.currentTimeMillis() + "";
                // String name = sourceFile.getName().substring(0, i);
                String new_name = drawingFileName + "_Trace" + ".png";
                File _traceImage = new File(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                Log.e("TAG", "storeInTraceList _traceImage " + _traceImage.getAbsolutePath() + " isExist " + _traceImage.exists() + " " + mainFilePath);
                if (!_traceImage.exists()) {
                    InputStream in = new FileInputStream(sourceFile.getAbsolutePath());
                    OutputStream out = new FileOutputStream(myPaintingFolder.getAbsolutePath() + "/" + new_name);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
                TraceReference traceReference = new TraceReference();
                try {
                    traceReference.setUserPaintingName(drawingFileName + ".png");
                    traceReference.setTraceImageName(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                    Log.e("TAGGG", "storeInTraceList New Name In TraceList " + myPaintingFolder.getAbsolutePath() + "/" + new_name);
                } catch (Exception e) {
                }
                File file = new File(mainFilePath);
                if (file.exists()) {
                    traceReference.setFromPaintologyFolder(false);
                } else {
                    traceReference.setFromPaintologyFolder(true);
                }
                traceReference.set_drawing_type(drawing_type.TraceDrawaing);
                traceReference.setGrayScale(switch_gray_scale.isChecked());
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());

                Gson gson = new Gson();
                String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");

                Log.e("TAG", "storeInTraceList json " + json);
                Type type = new TypeToken<ArrayList<TraceReference>>() {
                }.getType();
                ArrayList<TraceReference> traceList = gson.fromJson(json, type);

                if (traceList == null)
                    traceList = new ArrayList<TraceReference>();
                else
                    Log.e("TAG", "storeInTraceList size " + traceList.size() + " drawingFileName " + drawingFileName + " selectedImagePath " + selectedImagePath);

                traceList.add(traceReference);


                boolean isAdded = false;
                for (int j = 0; j < traceList.size(); j++) {
                    if (traceList.get(j).get_drawing_type() == drawing_type.Trace && traceList.get(j).getUserPaintingName().equalsIgnoreCase(new_name)) {
                        isAdded = true;
                        break;
                    }
                }
                Log.e("TAG", "storeInTraceList isAdded in trace list " + isAdded);
                if (!isAdded) {
                    TraceReference trace_image = new TraceReference();
                    try {
                        trace_image.setUserPaintingName(new_name);
                        trace_image.set_drawing_type(drawing_type.Trace);
//                        trace_image.setTraceImageName(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                        Log.e("TAG", "New Name In TraceList " + myPaintingFolder.getAbsolutePath() + "/" + new_name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    traceList.add(trace_image);
                }


                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                String json_1 = gson.toJson(traceList);
                Log.e("Akshits", json_1);
                prefsEditor.putString(constants.getTraceList_Gson_Key(), json_1);
                prefsEditor.commit();
                File _file = new File(myPaintingFolder.getAbsolutePath() + "/" + drawingFileName + ".png");
                new Handler().postDelayed(() -> copyFiles(_file, _traceImage, drawingFileName), 250);
                Log.v(TAG, "Copy file successful.");
            } catch (FileNotFoundException e) {
                Log.e("TAG111", "FileNotFound Excel " + e.getMessage());
            } catch (Exception e) {
                Log.e("TAG222", "Exception Excel " + e.getMessage());
            }
        }
    }

    @Override
    public void storeInOverlayList(String drawingFileName) {
        Log.e("TAG", "storeInTraceList logs " + PaintingType + " drawingFileName " + drawingFileName);
        if (isPickFromOverlaid) {
            String mainFilePath = selectedImagePath;
            if (!selectedImagePath.contains("/")) {
                mainFilePath = KGlobal.getMyPaintingFolderPath(PaintActivity.this) + "/" + mainFilePath.replaceFirst("[.][^.]+$", "") + "_Trace.png";
            }

            File sourceFile = new File(mainFilePath);
            File myPaintingFolder = new File(KGlobal.getMyPaintingFolderPath(PaintActivity.this));
            if (!myPaintingFolder.exists()) {
                myPaintingFolder.mkdirs();
            }
            try {
                int i = sourceFile.getName().lastIndexOf('.');
                String name = sourceFile.getName().substring(0, i);
                String new_name = drawingFileName + "_Trace" + ".png";
                File _traceImage = new File(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                Log.e("TAG", "storeInTraceList _traceImage " + _traceImage.getAbsolutePath() + " isExist " + _traceImage.exists() + " " + selectedImagePath);
                if (!_traceImage.exists()) {
                    InputStream in = new FileInputStream(sourceFile.getAbsolutePath());
                    OutputStream out = new FileOutputStream(myPaintingFolder.getAbsolutePath() + "/" + new_name);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
                TraceReference traceReference = new TraceReference();
                try {
                    traceReference.setUserPaintingName(drawingFileName + ".png");
                    traceReference.setTraceImageName(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                    Log.e("TAGGG", "storeInTraceList New Name In TraceList " + myPaintingFolder.getAbsolutePath() + "/" + new_name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = new File(mainFilePath);
                if (file.exists()) {
                    traceReference.setFromPaintologyFolder(false);
                } else {
                    traceReference.setFromPaintologyFolder(true);
                }
                traceReference.set_drawing_type(drawing_type.OverlayDrawing);
                traceReference.setGrayScale(switch_gray_scale.isChecked());
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());

                Gson gson = new Gson();
                String json = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");

                Log.e("TAG", "storeInTraceList json " + json);
                Type type = new TypeToken<ArrayList<TraceReference>>() {
                }.getType();
                ArrayList<TraceReference> traceList = gson.fromJson(json, type);

                if (traceList == null)
                    traceList = new ArrayList<TraceReference>();
                else
                    Log.e("TAGGG", "storeInTraceList size " + traceList.size() + " drawingFileName " + drawingFileName + " selectedImagePath " + selectedImagePath);

                traceList.add(traceReference);

                boolean isAdded = false;
                for (int j = 0; j < traceList.size(); j++) {
                    if (traceList.get(j).get_drawing_type() == drawing_type.Overlay && traceList.get(j).getUserPaintingName().equalsIgnoreCase(new_name)) {
                        isAdded = true;
                        break;
                    }
                }
                Log.e("TAG", "storeInTraceList isAdded in tracelist " + isAdded);
                if (!isAdded) {
                    TraceReference trace_image = new TraceReference();
                    try {
                        trace_image.setUserPaintingName(new_name);
                        trace_image.set_drawing_type(drawing_type.Overlay);
//                        trace_image.setTraceImageName(myPaintingFolder.getAbsolutePath() + "/" + new_name);
                        Log.e("TAGGG", "New Name In TraceList " + myPaintingFolder.getAbsolutePath() + "/" + new_name);
                    } catch (Exception e) {

                    }
                    traceList.add(trace_image);
                }


                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                String json_1 = gson.toJson(traceList);
                prefsEditor.putString(constants.getOverlayList_Gson_Key(), json_1);
                prefsEditor.commit();
                //                    File _file = new File(myPaintingFolder.getAbsolutePath() + "/" + drawingFileName + ".png");
//                    copyOverlayFiles(_file, _traceImage);
                Log.v(TAG, "Copy file successful.");
            } catch (FileNotFoundException e) {
                Log.e("TAGGG", "FileNotFound Exce " + e.getMessage());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception Exce " + e.getMessage());
            }
        }
    }

    void copyOverlayFiles(File sourcefile, File traceFilename) {
        InputStream in = null;
        OutputStream out = null;
        File outFile = null;
        try {
            in = new FileInputStream(sourcefile.getAbsolutePath());

            File dirsketchdata = new File(KGlobal.getMyPaintingFolderPath(this));

            outFile = new File(dirsketchdata, System.currentTimeMillis() + ".png");

            Log.e("TAG", "CopyFile Logs sourcefile " + sourcefile.getAbsolutePath());
            Log.e("TAG", "CopyFile Logs destFile " + outFile.getAbsolutePath());


            if (!outFile.exists()) {
                outFile.createNewFile();
            } else
                return;

            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            if (outFile != null && outFile.exists()) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());
                Gson gson = new Gson();
                String json = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");

                Log.e("TAG", "storeInOverlayList json " + json);
                Type type = new TypeToken<ArrayList<TraceReference>>() {
                }.getType();
                ArrayList<TraceReference> traceList = gson.fromJson(json, type);

                if (traceList == null)
                    traceList = new ArrayList<TraceReference>();


                TraceReference trace_image = new TraceReference();
                try {
                    trace_image.setUserPaintingName(outFile.getAbsolutePath());
                    trace_image.set_drawing_type(drawing_type.Normal);
                    trace_image.setTraceImageName(traceFilename.getAbsolutePath());
                } catch (Exception e) {

                }
                traceList.add(trace_image);
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                String json_1 = gson.toJson(traceList);
                prefsEditor.putString(constants.getOverlayList_Gson_Key(), json_1);
                prefsEditor.commit();

                json = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");

                Log.e("TAG", "storeInTraceList json Updated " + json);

                Log.e("TAGG", "File Copy successfully " + outFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e("tag", "Exception copyAssets Failed to copy asset file: " + sourcefile + e.toString() + " " + e.getMessage());
        }
    }

    void copyFiles(File sourcefile, File traceFilename, String FileName) {
        InputStream in = null;
        OutputStream out = null;
        File outFile = null;
        try {
            in = new FileInputStream(sourcefile.getAbsolutePath());

            File dirsketchdata = new File(KGlobal.getMyPaintingFolderPath(this));

            outFile = new File(dirsketchdata, FileName + "_Drawing" + ".png");

            Log.e("TAG", "CopyFile Logs sourcefile " + sourcefile.getAbsolutePath());
            Log.e("TAG", "CopyFile Logs destFile " + outFile.getAbsolutePath());


            if (!outFile.exists()) {
                outFile.createNewFile();
            } else
                return;

            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            if (outFile != null && outFile.exists()) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext());
                Gson gson = new Gson();
                String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");

                Log.e("TAG", "storeInTraceList json " + json);
                Type type = new TypeToken<ArrayList<TraceReference>>() {
                }.getType();
                ArrayList<TraceReference> traceList = gson.fromJson(json, type);

                if (traceList == null)
                    traceList = new ArrayList<TraceReference>();


                TraceReference trace_image = new TraceReference();
                try {
                    trace_image.setUserPaintingName(outFile.getAbsolutePath());
                    trace_image.set_drawing_type(drawing_type.Normal);
                    trace_image.setTraceImageName(traceFilename.getAbsolutePath());
                } catch (Exception e) {

                }
                traceList.add(trace_image);
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                String json_1 = gson.toJson(traceList);
                prefsEditor.putString(constants.getTraceList_Gson_Key(), json_1);
                prefsEditor.commit();

                json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");

                Log.e("TAG", "storeInTraceList json Updated " + json);

                Log.e("TAGG", "File Copy successfully " + outFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e("tag", "Exception copyAssets Failed to copy asset file: " + sourcefile + e.toString() + " " + e.getMessage());
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    public void hidePlayer() {
        changeToggle();
    }

    @Override
    public void saveToLocal(String fileName, int Tag) {
        if (Tag == NEW_CANVAS_TAG) {
//            reflectCanvas();
//            displayHintToggleBG(colorbar_bgcolor);
            startActivity(new Intent(this, DrawNowActivity.class));
            finish();
        } else if (Tag != SAVE_TAG) {
            Log.e("c", "confirmExit called LN 4678");
            confirmExit(true);
        }
        Log.e("TAGGG", "Painting Type at save " + PaintingType);
        if (!PaintingType.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, PaintingType, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, PaintingType);
        }
    }


    void reflectCanvas() {
        try {
            iv_selected_image.setImageDrawable(null);
            view_cross.setVisibility(View.GONE);
            try {
                if (youTubePlayerView != null) {
                    youTubePlayerView.release();
                    youTubePlayerView.setVisibility(View.GONE);
                    Log.e("TAGG", "Release YT Playe");
                }
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                    simpleExoPlayerView.setVisibility(View.GONE);
                    Log.e("TAGG", "Release Exo player");
                }
                if (iv_switch_to_player != null)
                    iv_switch_to_player.setVisibility(View.GONE);
                ll_bottom_bar.setVisibility(View.GONE);
                FromTutorialMode = false;
                isFromEditImage = false;
                isPickFromOverlaid = false;
//                current_mode = canvas_mode.canvas_front;

                mPaintView.resetCanvas();

            } catch (Exception e) {
                Log.e("TAG", "Exception at new canvas " + e.getMessage());
                handler.removeCallbacks(runnableCode);
            }
            switch_singleTap.setChecked(false);
            _switch_line.setChecked(false);
            defaultSwitchLineStatus = false;
            defaultSwitchSingleTapStatus = false;
            mStrBackground = null;
            mPainting.setBackgroundBitmap(null);
            iv_selected_image.setVisibility(View.GONE);
            iv_selected_image.setImageBitmap(null);
            iv_temp_traced.setImageBitmap(null);
            iv_temp_traced.setVisibility(View.GONE);
            Log.e("TAGG", "Selected Color mPrefBackgroundColor " + mPrefBackgroundColor);
//            mPainting.setBackgroundColor(mPrefBackgroundColor);
            mPainting.setBackgroundColor(-1);
            mPrefBackgroundColor = -1;
            mPainting.clearPainting();
            mPaintView.invalidate();
            iv_temp_traced.invalidate();
            iv_selected_image.invalidate();

            mNewCanvasBtn.setImageResource(R.drawable.blank_canvas_white_canvas);
            mNewCanvasBtn.setTag(0);
            CurrentMode = 0;

            seekbar_1.setVisibility(View.GONE);
            seekBarContainer4.setVisibility(View.GONE);
            hideShowSeekbarView(true);

            ll_toggle.setVisibility(View.GONE);
            colorbar_bgcolor.setBackgroundColor(mPrefBackgroundColor);


            constants.putString("pickfromresult", "", PaintActivity.this);
            constants.putString("isfromTrace", "", PaintActivity.this);
            constants.putString("isfromoverlay", "", PaintActivity.this);
            constants.putString("path", "", PaintActivity.this);
            constants.putString("parentFolder", "", PaintActivity.this);
            constants.putString("type", "", PaintActivity.this);

            constants.putString("action_name", "", PaintActivity.this);
            resetCanvas();
            // new code
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } catch (Exception e) {
            Log.e("TAGG", "Exception at refelct " + e.getMessage());
        }
    }


    void resetCanvas() {
        try {
            if (BuildConfig.DEBUG) {
                KGlobal.appendLog(this, "resetCanvas");
            }
            LinearLayout.LayoutParams _param = new LinearLayout.LayoutParams(mScreenWidth, mScreenHeight);
            rl_canvas.setLayoutParams(_param);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(_param.width, _param.height, conf);
            Log.e("TAG", "Width And Height " + _param.width + " " + _param.height);
            mPainting.setBitmap(bmp);
            mPainting.setBackgroundColor(-1);
            mPaintView.reDraw(null);
            mPainting.clearPainting();
            mPaintView.invalidate();

            PaintingType = "";
            bmp.recycle();
            bmp = null;
            System.gc();
            switch_singleTap.setChecked(false);
            _switch_line.setChecked(false);
            defaultSwitchLineStatus = false;
            defaultSwitchSingleTapStatus = false;

        } catch (Exception e) {
            Log.e("TAG", "Reset canvas Exception " + e.getMessage());
            KGlobal.appendLog(this, "resetCanvas ex: " + e.getMessage());
        }
    }

    private String generateFileName() {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("MMddyy_HHmmss");
        long l = System.currentTimeMillis();
        Date lDate = new Date(l);
        return lSimpleDateFormat.format(lDate);
    }

    void readFilesAndStartVideo(String eventFilePath, String strokeFilePath, String videoFilePath, boolean needToShowYTPlayer, boolean needToSetBGColor) {

        File file;

        StringBuilder text = new StringBuilder();
        BufferedReader br;
        try {
            Log.e("TAGGG", "readFiles Called -1 ");
            file = new File(eventFilePath);
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                Log.e("TAGGG", "readFiles Called ");
                JSONObject obj = new JSONObject(text.toString());

                if (obj.has("FrequencyData")) {
                    JSONObject frequencyObject = obj.getJSONObject("FrequencyData");
                    int totalEvent = frequencyObject.getInt("TotalEvents");
                    int totalColorChange = frequencyObject.getInt("TotalColorChange");
                    int totalBrushChange = frequencyObject.getInt("TotalBrushChange");
                    if (needToSetBGColor) {
                        int background_clr = frequencyObject.has(constants.getCanvas_Background_Color()) ? frequencyObject.getInt(constants.getCanvas_Background_Color()) : 0;
                        mStrBackground = null;
                        mPrefBackgroundColor = background_clr;
                        mPainting.setBackgroundBitmap(null);
//                mPainting.setBackgroundColor(Color.TRANSPARENT);
                        Log.e("TAGG", "Selected Color mPrefBackgroundColor " + mPrefBackgroundColor);
                        if (mPrefBackgroundColor == 0 || mPrefBackgroundColor == -16777216)
                            mPrefBackgroundColor = -1;

                        mPainting.setBackgroundColor(mPrefBackgroundColor);
                        mPainting.clearPainting();
                        mPaintView.invalidate();
                        savePaintingPreference();

                        mPaintingTemp.setBackgroundBitmap(null);
                        mPaintingTemp.clearPainting();

                        mPaintingTemp.setBackgroundColor(mPrefBackgroundColor);
                        mPaintingTemp.setBackgroundBitmap(null);
                        mPaintingTemp.clearPainting();

                        if (mPaintingTemp != null) {
                            mPaintingTemp.syncComposeCanvas();
                            mPaintingTemp.syncUndoCanvas();
                        }
                        mPaintViewTemp.reDraw(null);
                    }
                    showEffortDialog(videoFilePath, totalEvent + "", totalBrushChange + "", totalColorChange + "", needToShowYTPlayer);
                }

                Log.e("TAGGG", "readFiles Called AFTER");
                if (obj.has("EventsData")) {

                    JSONArray array = obj.getJSONArray("EventsData");
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject obj_child = array.getJSONObject(i);
                        EventModel _model = new EventModel();
                        _model.setTimeStamp(obj_child.getString("TimeStamp"));
                        _model.setBrushColor(obj_child.getString("BrushColor"));

//                        _model.setEventType(obj_child.getString(KGlobal.IS_COLOR_CHANGE));
                        _model.setEventType(obj_child.getString(constants.getIS_COLOR_CHANGE()));

                        _model.setNotes(obj_child.getString("EventNotes"));
                        JSONObject obj_child_2 = obj_child.getJSONObject("ChangeBrushEventData");
                        _model.objChangeData.setBrushFlow(obj_child_2.getString("BrushFlow"));
                        _model.objChangeData.setBrushAlpha(obj_child_2.getString("BrushAlpha"));
                        _model.objChangeData.setBrushSize(obj_child_2.getString("BrushSize"));
                        _model.objChangeData.setBrushHardness(obj_child_2.getString("BrushHardness"));
                        _model.objChangeData.setBrushStyle(obj_child_2.getString("BrushStyle"));
                        _model.objChangeData.setBrushName(obj_child_2.getString("BrushName"));
                        _model.objChangeData.setBrushColor(obj_child_2.getString("BrushColor"));
                        eventModelList.add(_model);
                    }
                }
            }

            Log.e("TAGGG", "readFiles Called AFTER 1");
            text = new StringBuilder();
            file = new File(strokeFilePath);
            Log.e("TAGGG", "readFiles Called AFTER 2");
            if (file.exists()) {

                br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            Log.e("TAGGG", "readFiles Called AFTER 3");
            JSONArray main_array = new JSONArray(text.toString());

            EventModel model = new EventModel();
            model.setTimeStamp("0");
            strokeList.add(model);

            for (int i = 0; i < main_array.length(); i++) {
                JSONObject obj_child = main_array.getJSONObject(i);
                EventModel _model = new EventModel();
                _model.setTimeStamp(obj_child.getString("TimeStamp"));
                _model.setStrokeAxis(obj_child.getString("Stroke"));
                JSONObject obj_child_2 = obj_child.getJSONObject("ChangeBrushEventData");
                _model.objChangeData.setBrushFlow(obj_child_2.getString("BrushFlow"));
                _model.objChangeData.setBrushAlpha(obj_child_2.getString("BrushAlpha"));
                _model.objChangeData.setBrushSize(obj_child_2.getString("BrushSize"));
                _model.objChangeData.setBrushHardness(obj_child_2.getString("BrushHardness"));
                _model.objChangeData.setBrushStyle(obj_child_2.getString("BrushStyle"));
                _model.objChangeData.setBrushName(obj_child_2.getString("BrushName"));
                _model.objChangeData.setBrushColor(obj_child_2.getString("BrushColor"));
                strokeList.add(_model);
            }
            Log.e("TAGGG", "readFiles Called AFTER 4");
            tv_startTime.setText(convertToTime(0));

            if (eventModelList != null && eventModelList.size() != 0) {
//                seekBar_timeLine.setMax(Integer.parseInt(eventModelList.get(eventModelList.size() - 1).getTimeStamp()));
                tv_endTime.setText(getDurationFromPath(videoFilePath));
            }
            seekBar_timeLine.setIntervals(strokeList);

            showCanvasStrokeDialog();
        } catch (Exception e) {
            //You'll need to add proper error handling here
            Log.e("TaGG", "Exception at parse files " + e.getMessage() + " " + e.toString());
        }
    }

    public void displayHintToggle(View view) {
        int SPLASH_TIME_OUT = 2000;

        LinearLayout linear_hint = findViewById(R.id.linear_hint_toggle);
        linear_hint.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                linear_hint.setVisibility(View.GONE);
                handler.removeCallbacks(this);
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    public void displayHintToggleBG(View view) {
        int SPLASH_TIME_OUT = 3000;

        LinearLayout linear_hint = findViewById(R.id.linear_hint);
        linear_hint.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                linear_hint.setVisibility(View.GONE);
                handler.removeCallbacks(this);
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }


    String getDurationFromPath(String videoFilepath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        File f = new File(videoFilepath);
        if (f.exists()) {
            retriever.setDataSource(this, Uri.fromFile(f));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long millis = Long.parseLong(time);

            String convertedTime = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return convertedTime;
        }
        return "";
    }

    String convertToTime(int timeSec) {
        int hours = (int) timeSec / 3600;
        int temp = (int) timeSec - hours * 3600;
        int mins = temp / 60;
        temp = temp - mins * 60;
        int secs = temp;

        String hr = "", min = "", sec = "";
        if (hours == 0) {
            hr = "00";
        } else if (hours < 10) {
            hr = "0" + hours;
        } else
            hr = hours + "";

        if (mins == 0) {
            min = "00";
        } else if (mins < 10) {
            min = "0" + mins;
        } else
            min = mins + "";

        if (secs == 0) {
            sec = "00";
        } else if (secs < 10) {
            sec = "0" + secs;
        } else
            sec = secs + "";


        String requiredFormat = hr + ":" + min + ":" + sec;
        return requiredFormat;
    }

    public void showEffortDialog(String videoFilepath, String totalEvents, String totalBrushChange, String totalColorChange, boolean needToShowYTPlayer) {

        /*String need_to_show_selected = constants.getString(constants.is_dont_show_selected, Paintor.this);
        if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
            showStrokeHintDialog();
        }*/

        if (needToShowYTPlayer) {
            current_mode = canvas_mode.canvas_front;
//            openCanvasInstedOfPlayer();

            if (youTubePlayerView != null && youTubePlayerView.getVisibility() != View.VISIBLE) {
//                youTubePlayerView.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.VISIBLE);
                videoPlayedOnce = true;
                editor.putBoolean("videoPlayedOnce", videoPlayedOnce).apply();
                if (YTPlayer != null)
                    YTPlayer.play();

                String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                    showCanvasHintDialog();
                }
            }
        } else {
            if (videoFilepath.equalsIgnoreCase("")) {
//                youTubePlayerView.setVisibility(View.GONE);
//                simpleExoPlayerView.setVisibility(View.GONE);

                openCanvasInstedOfPlayer();
            } else {
                String need_to_show_selected = constants.getString(constants.is_dont_show_selected_canvas, PaintActivity.this);
                if (need_to_show_selected.isEmpty() || need_to_show_selected.equalsIgnoreCase("false")) {
                    showCanvasHintDialog();
                }
                playVideo(videoFilepath);
            }


        }
    }

    void openCanvasInstedOfPlayer() {
        if (iv_switch_to_player != null)
            iv_switch_to_player.setVisibility(View.VISIBLE);
        if (iv_cursor_icon.getVisibility() == View.VISIBLE)
            iv_cursor_icon.setVisibility(View.GONE);
        current_mode = canvas_mode.canvas_back;
        iv_toggle_preview.setImageResource(R.drawable.toggle_vid_new);
        if (iv_toggle_preview_tooltip != null) {
            iv_toggle_preview_tooltip.setImageResource(R.drawable.toggle_vid_new);
        }
        mPaintView.setVisibility(View.VISIBLE);
        if (youTubePlayerView != null && youTubePlayerView.getVisibility() == View.VISIBLE) {
            youTubePlayerView.setVisibility(View.GONE);
            if (YTPlayer != null)
                YTPlayer.pause();
        }
        if (simpleExoPlayerView != null && simpleExoPlayerView.getVisibility() == View.VISIBLE) {
            simpleExoPlayerView.setVisibility(View.GONE);
            iv_hide_exo_player.setVisibility(View.GONE);
            if (player != null) {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            }
        }
        iv_temp_traced.setVisibility(View.VISIBLE);
        ll_bottom_bar.setVisibility(View.VISIBLE);
        img_community.setVisibility(View.GONE);
        iv_play_pause.setVisibility(View.VISIBLE);
    }

    void startCountDown() {

        TextView count_town_text = findViewById(R.id.count_town_text);
        count_town_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        CountDownAnimation countDownAnimation = new CountDownAnimation(count_town_text, 5);
        // Use a set of animations
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);
// Customizable start count
        countDownAnimation.setStartCount(4);

        countDownAnimation.start();

        countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                startRecording();
            }
        });
    }

    void startRecording() {

        try {

            iv_start_recoring.setEnabled(true);
            mMediaRecorder.start();

            iv_start_recoring.setTag(1);
//        iv_start_recoring.setImageResource(R.drawable.pause_recording);
            recording_handler.postDelayed(everySecondRunnable, ONE_SECOND);

        } catch (IllegalStateException exs) {
            Log.e("TAG", "IllegalStateException at startRecording " + exs.getMessage() + " " + exs);

            stopRecordingService();
//            writeException("startRecording IllegalStateException " + exs.getMessage() + " " + exs.toString() + " " + exs.getCause() + " FilePath " + recordingOutputFilePath);
        } catch (Exception e) {
            stopRecordingService();
            Log.e("TAG", "Exception at startRecording " + e.getMessage());
//            writeException("startRecording Exception " + e.getMessage() + " " + e.toString() + " " + e.getCause() + " FilePath " + recordingOutputFilePath);
        }


        EventModel model = new EventModel();
//        model.setColorchange(false);
        model.setEventType(EventType.BRUSH_CHANGE + "");
        model.setTimeStamp(second_indicator + "");
        //    SharedPreferences lSharedPreferences = getPreferences(0);
        SharedPreferences lSharedPreferences = getSharedPreferences("brush", 0);
        if (lSharedPreferences.getString("pref-saved", null) != null) {

            model.objChangeData.setBrushStyle(lSharedPreferences.getInt("brush-style", Brush.LineBrush) + "");

            model.objChangeData.setBrushColor(mPrefBrushColor + "");

            model.objChangeData.setBrushSize(lSharedPreferences.getFloat("brush-size", 8.0F) + "");

            model.objChangeData.setBrushFlow(lSharedPreferences.getInt("brush-flow", 65) + "");

            model.objChangeData.setBrushAlpha(lSharedPreferences.getInt("brush-alpha", 255) + "");

            model.objChangeData.setBrushHardness(lSharedPreferences.getInt("brush-pressure", 65) + "");

            model.getObjChangeData().setBrushName(getStyleName(lSharedPreferences.getInt("brush-style", Brush.LineBrush)));
        }
        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.START_RECORDING, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.START_RECORDING);
        addEventInList(model);
        tv_recording_time.setTag(recordingState.In_Resume);
        tv_recording_time.setVisibility(View.VISIBLE);
        iv_start_recoring.setImageResource(R.drawable.rec_started_icon);
    }

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    void playVideo(String filePath) {
        mPaintView.setVisibility(View.GONE);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

        simpleExoPlayerView.setVisibility(View.VISIBLE);

        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//Set media controller
        simpleExoPlayerView.setControllerShowTimeoutMs(3600000);
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        player.setRepeatMode(Player.REPEAT_MODE_OFF);
// Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        Uri mp4VideoUri = Uri.parse(filePath);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
            mp4VideoUri = Uri.fromFile(new File(filePath)); //FileProvider.getUriForFile(this, "com.paintology.lite" + ".provider", new File(filePath));
        }

        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource, 1);

        player.prepare(loopingSource);

        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...isLoading:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }

            @Override
            public void onSeekProcessed() {

            }
        });


//        iv_hide_exo_player.setVisibility(View.GONE);
        iv_hide_exo_player.setVisibility(View.VISIBLE);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this);

        current_mode = canvas_mode.canvas_front;

//        openCanvasInstedOfPlayer();
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }


    void drawNextStroke() {
        try {
            if (currentStrokeIndex < strokeList.size()) {
                Log.e("TAGG", "currentStrokeIndex Logs at drawNextStroke " + currentStrokeIndex);
                EventModel model = strokeList.get(currentStrokeIndex);

                mPaintingTemp.setBrushColor(model.getObjChangeData().getBrushColor() != "" ? Integer.parseInt(model.getObjChangeData().getBrushColor()) : -65536);
                mPaintingTemp.setBrushSize(model.getObjChangeData().getBrushSize() != "" ? Float.parseFloat(model.getObjChangeData().getBrushSize()) : 8.0F);
                mPaintingTemp.setBrushMode(model.getObjChangeData().getBrushMode() != "" ? Integer.parseInt(model.getObjChangeData().getBrushMode()) : 33);
                mPaintingTemp.setAlpha(model.getObjChangeData().getBrushAlpha() != "" ? Integer.parseInt(model.getObjChangeData().getBrushAlpha()) : 255);
                mPaintingTemp.setBrushStyle(model.getObjChangeData().getBrushStyle() != "" ? Integer.parseInt(model.getObjChangeData().getBrushStyle()) : Brush.LineBrush);
                mPaintingTemp.mBrushFlow = Integer.parseInt(model.getObjChangeData().getBrushFlow());

                String strokeData = model.getStrokeAxis();
                StringTokenizer tokens = new StringTokenizer(strokeData, "|");
                ArrayList<String> lst_strokeAxis = new ArrayList<>();

                do {
                    lst_strokeAxis.add(tokens.nextToken());
                } while (tokens.hasMoreTokens());

                String[] sep = lst_strokeAxis.get(0).split(",");
                mPaintViewTemp.onTouchEventForMultiTouchTemp((Float.parseFloat(sep[0])), Float.parseFloat(sep[1]), 0);
                for (index_stroke = 0; index_stroke < lst_strokeAxis.size(); index_stroke++) {
                    String[] separated = lst_strokeAxis.get(index_stroke).split(",");
                    mPaintViewTemp.onTouchEventForMultiTouchTemp(Float.parseFloat(separated[0]), Float.parseFloat(separated[1]), 2);
                }
                String[] separated = lst_strokeAxis.get(lst_strokeAxis.size() - 1).split(",");
                mPaintViewTemp.onTouchEventForMultiTouchTemp(Float.parseFloat(separated[0]), Float.parseFloat(separated[1]), 1);

                currentStrokeIndex = currentStrokeIndex + 1;
                Log.e("TAGGG", "currentStrokeIndex Logs 5453 " + currentStrokeIndex + " list size " + strokeList.size());
                System.gc();
                mPainting.setBrushColor(model.getObjChangeData().getBrushColor() != "" ? Integer.parseInt(model.getObjChangeData().getBrushColor()) : -65536);
                mPainting.setBrushMode(model.getObjChangeData().getBrushMode() != "" ? Integer.parseInt(model.getObjChangeData().getBrushMode()) : 33);
                if (model.getObjChangeData().getBrushSize() != "") {
                    mBrushSize = Float.parseFloat(model.getObjChangeData().getBrushSize());
                    mPainting.setBrushSize(Float.parseFloat(model.getObjChangeData().getBrushSize()));
                } else {
                    mBrushSize = 8.0F;
                    mPainting.setBrushSize(8.0F);
                }
                mPainting.setAlpha(model.getObjChangeData().getBrushAlpha() != "" ? Integer.parseInt(model.getObjChangeData().getBrushAlpha()) : 255);
                mPainting.setBrushStyle(model.getObjChangeData().getBrushStyle() != "" ? Integer.parseInt(model.getObjChangeData().getBrushStyle()) : Brush.LineBrush);
                mPrefBrushStyle = (model.getObjChangeData().getBrushStyle() != "" ? Integer.parseInt(model.getObjChangeData().getBrushStyle()) : Brush.LineBrush);

                mPrefBrushColor = (model.getObjChangeData().getBrushColor() != "" ? Integer.parseInt(model.getObjChangeData().getBrushColor()) : -65536);
                mPrefBrushSize = mBrushSize;
                mPrefAlpha = (model.getObjChangeData().getBrushAlpha() != "" ? Integer.parseInt(model.getObjChangeData().getBrushAlpha()) : 255);
                mPrefFlow = Integer.parseInt(model.getObjChangeData().getBrushFlow());
                mPainting.mBrushFlow = mPrefFlow;
                savePaintingPreference();
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception while draw Stroke " + e.getMessage() + " " + e.toString());
        }
    }


    void configureBrush(boolean isFromUndo, boolean isFromRedo) {
        try {
            EventModel model;
            if (isFromUndo || isFromRedo)
                if (currentStrokeIndex == 1)
                    model = strokeList.get(currentStrokeIndex);
                else
                    model = strokeList.get(currentStrokeIndex - 1);
            else
                model = strokeList.get(currentStrokeIndex);
            mPainting.setBrushColor(model.getObjChangeData().getBrushColor() != "" ? Integer.parseInt(model.getObjChangeData().getBrushColor()) : -65536);
            mPainting.setBrushMode(model.getObjChangeData().getBrushMode() != "" ? Integer.parseInt(model.getObjChangeData().getBrushMode()) : 33);
            if (model.getObjChangeData().getBrushSize() != "") {
                mBrushSize = Float.parseFloat(model.getObjChangeData().getBrushSize());
                mPainting.setBrushSize(Float.parseFloat(model.getObjChangeData().getBrushSize()));
            } else {
                mBrushSize = 8.0F;
                mPainting.setBrushSize(8.0F);
            }
            mPainting.setAlpha(model.getObjChangeData().getBrushAlpha() != "" ? Integer.parseInt(model.getObjChangeData().getBrushAlpha()) : 255);
            mPainting.setBrushStyle(model.getObjChangeData().getBrushStyle() != "" ? Integer.parseInt(model.getObjChangeData().getBrushStyle()) : Brush.LineBrush);
            mPrefBrushStyle = (model.getObjChangeData().getBrushStyle() != "" ? Integer.parseInt(model.getObjChangeData().getBrushStyle()) : Brush.LineBrush);

            mPrefBrushColor = (model.getObjChangeData().getBrushColor() != "" ? Integer.parseInt(model.getObjChangeData().getBrushColor()) : -65536);
            mPrefBrushSize = mBrushSize;
            mPrefAlpha = (model.getObjChangeData().getBrushAlpha() != "" ? Integer.parseInt(model.getObjChangeData().getBrushAlpha()) : 255);
            mPrefFlow = Integer.parseInt(model.getObjChangeData().getBrushFlow());
            mPainting.mBrushFlow = mPrefFlow;
            savePaintingPreference();
            setColorInBox(mPrefBrushColor);
        } catch (Exception e) {
            Log.e("TAGG", "Exception at configure brush " + e.getMessage());
        }
    }

    Bitmap b;

    void reflectStroke() {
        b = mPaintingTemp.getBitmap();
        iv_temp_traced.setImageBitmap(b);
        if (iv_temp_traced.getVisibility() != View.VISIBLE)
            iv_temp_traced.setVisibility(View.VISIBLE);
        iv_temp_traced.invalidate();
    }

    void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PaintActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();
        Log.e("TAG", "progressDialog Logs show");
    }

    void hideProgress() {

        Log.e("TAG", "progressDialog Logs Hide");
        try {
            if (progressDialog != null && progressDialog.isShowing() && !PaintActivity.this.isDestroyed()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }


    public class drawSroke extends AsyncTask<Void, Void, Void> {

        boolean isFromPlaybutton = false;

        public drawSroke(boolean isFromPlay) {
            this.isFromPlaybutton = isFromPlay;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!Loadingdialog.IsDialogShowing()) {
                Loadingdialog.ShowPleaseWaitDialog(getString(R.string.rendering));
                progress.setVisibility(View.VISIBLE);

            }

//            if (ll_rendering.getVisibility() != View.VISIBLE) {
//                ll_rendering.setVisibility(View.VISIBLE);
//                progress.setVisibility(View.VISIBLE);
//            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
//            if (isFromOverlaid)
//                drawNextStrokeForOverraid();
//            else
            drawNextStroke();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            hideProgress();

            seekBar_timeLine.setProgress(seekBar_timeLine.getProgress() + 1);
            reflectStroke();
//            if (!isFromOverlaid) {
//            }
            setColorInBox(mPrefBrushColor);
            try {
                if (isFromPlaybutton) {
                    Log.e("TAGGG", "Draw Next Stroke " + currentStrokeIndex + " Size " + strokeList.size());
                    if (currentStrokeIndex < strokeList.size() && iv_play_pause.getTag().equals("1")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new drawSroke(isFromPlaybutton).execute();
                            }
                        }, 1000);
                    } else {
                        iv_play_pause.setTag("0");
                        iv_play_pause.setImageResource(R.drawable.play_icon);
                        progress.setVisibility(View.INVISIBLE);
                        Loadingdialog.DismissDialog();
//                        ll_rendering.setVisibility(View.INVISIBLE);
                    }
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    Loadingdialog.DismissDialog();
//                    ll_rendering.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                Log.e("TAGG", "Exception at play stroke " + e.getMessage());
            }
        }
    }


    public void undo(View v) {
        try {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.previous_stroke, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.previous_stroke);
            Log.e("TAGGG", "Undo Logs stroke size " + mPaintingTemp.mDeletedStrokes.size() + " " + mPaintingTemp.mCachedUndoStrokeList.size());
            if (mPaintingTemp.mCachedUndoStrokeList.size() > 0) {
                Rect lRect = mPaintingTemp.undoStroke();
                if (lRect != null) {
                    mPaintViewTemp.reDraw(lRect);
                }
                currentStrokeIndex = currentStrokeIndex - 1;
                Log.e("TAGGG", "currentStrokeIndex Logs 5645 " + currentStrokeIndex);
                seekBar_timeLine.setProgress(seekBar_timeLine.getProgress() - 1);
                reflectStroke();
            } else {
                Toast.makeText(this, "No Stroke To Undo!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at undo " + e.getMessage());
        }
    }

    public void redo(View v) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.next_stroke, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PaintActivity.this, constants.next_stroke);
        btn_next_stroke.performClick();
    }

    PopupWindow mPopupWindow_save;
    PopupWindow mPopupWindow_post;
    PopupWindow mPopupWindow_canvas;

    boolean issaveTooltipShown = false;
    boolean ispostTooltipShown = false;
    boolean isCanvasTooltipShown = false;

    public void displayPostWindow(View view) {

        ispostTooltipShown = true;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.post_layout_canvas, null);
        mPopupWindow_post = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow_post.setOutsideTouchable(true);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_post.showAsDropDown(view, (mPopupWindow_post.getWidth() - 15), (mPopupWindow_post.getHeight() + 20));
            else
                mPopupWindow_post.showAsDropDown(view, (view.getWidth() - mPopupWindow_post.getWidth() + 20), -(view.getHeight() - mPopupWindow_post.getHeight() + (20)));
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_post.showAsDropDown(view, (mPopupWindow_post.getWidth() - 30), (mPopupWindow_post.getHeight() + 25));
            else
                mPopupWindow_post.showAsDropDown(view, (view.getWidth() - mPopupWindow_post.getWidth() + 20), -(view.getHeight() - mPopupWindow_post.getHeight() + 40));
        }


        LinearLayout post_community = customView.findViewById(R.id.post_community);
        LinearLayout post_gallery = customView.findViewById(R.id.post_gallery);

        post_community.setOnClickListener(v -> {
            mPopupWindow_save.dismiss();
            mPopupWindow_post.dismiss();
            if (mPainting != null && mPainting.getStrokeCount() == 0) {
                if (getIntent().getExtras().getString("isImport") != null) {
                    if (!getIntent().getExtras().getString("isImport").isEmpty()) {
                        if (getIntent().getExtras().getString("isImport").equalsIgnoreCase("ImportImage")) {
                            if (isFromGallery) {
                                ContextKt.sendUserEvent("gallery_canvas_save_menu_post");
                            }
                            isGalleryPost = false;
                            new saveImageInBackForShare(false).execute();
                        }
                    }
                } else {
                    Toast.makeText(PaintActivity.this, "Canvas is empty, please do an art and then share on community!", Toast.LENGTH_SHORT).show();

                }
            } else {
                if (isFromGallery) {
                    ContextKt.sendUserEvent("gallery_canvas_save_menu_post");
                }
                isGalleryPost = false;
                new saveImageInBackForShare(false).execute();
            }
        });

        post_gallery.setOnClickListener(v -> {
            mPopupWindow_save.dismiss();
            mPopupWindow_post.dismiss();
            if (mPainting != null && mPainting.getStrokeCount() == 0) {
                if (getIntent().getExtras().getString("isImport") != null) {
                    if (!getIntent().getExtras().getString("isImport").isEmpty()) {
                        if (getIntent().getExtras().getString("isImport").equalsIgnoreCase("ImportImage")) {
                            if (isFromGallery) {
                                ContextKt.sendUserEvent("gallery_canvas_save_menu_post");
                            }
                            isGalleryPost = true;
                            new saveImageInBackForShare(false).execute();
                        }
                    }
                } else {
                    Toast.makeText(PaintActivity.this, "Canvas is empty, please do an art and then share on community!", Toast.LENGTH_SHORT).show();

                }
            } else {
                if (isFromGallery) {
                    ContextKt.sendUserEvent("gallery_canvas_save_menu_post");
                }
                isGalleryPost = true;
                new saveImageInBackForShare(false).execute();
            }
        });
    }

    public void displayPopupWindowSave(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.save_layout_canvas, null);
        issaveTooltipShown = true;
        mPopupWindow_save = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow_save.setOutsideTouchable(true);
        //mPopupWindow.showAsDropDown(view, 50, -40, Gravity.LEFT);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_save.showAsDropDown(view, (mPopupWindow_save.getWidth() - 15), (mPopupWindow_save.getHeight() + 20));
            else
                mPopupWindow_save.showAsDropDown(view, (view.getWidth() - mPopupWindow_save.getWidth() + 20), -(view.getHeight() - mPopupWindow_save.getHeight() + (20)));
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_save.showAsDropDown(view, (mPopupWindow_save.getWidth() - 30), (mPopupWindow_save.getHeight() + 25));
            else
                mPopupWindow_save.showAsDropDown(view, (view.getWidth() - mPopupWindow_save.getWidth() + 20), -(view.getHeight() - mPopupWindow_save.getHeight() + 40));
        }

        LinearLayout iv_save = customView.findViewById(R.id.save_root);
        LinearLayout iv_share = customView.findViewById(R.id.share_root);
        LinearLayout iv_community = customView.findViewById(R.id.community_root);
        LinearLayout iv_new_canvas = customView.findViewById(R.id.new_canvas_root);
        LinearLayout iv_sshot = customView.findViewById(R.id.sshot_root);


        iv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {

                    if (getIntent().getExtras().getString("isImport") != null) {
                        Toast.makeText(PaintActivity.this, "not null key", Toast.LENGTH_SHORT).show();
                        if (!getIntent().getExtras().getString("isImport").isEmpty()) {
                            if (getIntent().getExtras().getString("isImport").equalsIgnoreCase("ImportImage")) {
                                mPopupWindow_save.dismiss();
                                disableColorPenMode();
                                savePainting(SAVE_TAG);
                                return;
                            }
                        }
                    } else {
                        Toast.makeText(PaintActivity.this, "Canvas is empty, please do an art!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_save_painting, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_save_painting);
                if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                    mPopupWindow_post.dismiss();
                mPopupWindow_save.dismiss();
                disableColorPenMode();
                savePainting(SAVE_TAG);
            }
        });

        iv_community.setOnClickListener(view1 -> {

            if (!getIntent().hasExtra("step")) {

                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_community_click, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_community_click);

                if (ispostTooltipShown) {
                    ispostTooltipShown = false;
                    mPopupWindow_post.dismiss();
                } else {
                    displayPostWindow(iv_community);
                }

            }
        });

        iv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_share_click, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_share_click);

//                    int permission = ActivityCompat.checkSelfPermission(Paintor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    if (permission != PackageManager.PERMISSION_GRANTED) {
//                        // We don't have permission so prompt the user
//                        ActivityCompat.requestPermissions(
//                                Paintor.this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                1
//                        );
//                        return;
//                    }

//                    if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
//                        // We don't have permission so prompt the user
//                        PermissionUtils.requestStoragePermission(Paintor.this, 1);
//                        return;
//                    }
                    /*if (mPainting != null && mPainting.getStrokeCount() == 0) {
                        Toast.makeText(Paintor.this, "Canvas is empty, please do an art and then share!", Toast.LENGTH_SHORT).show();
                        return;
                    }*/

//                    if (!mPainting.isEmpty()) {
//                        new saveImageInBackForShare(true).execute();
//                    } else {
//                        if (PaintingType.equalsIgnoreCase("save_overlay_canvas_drawing")) {
//                            new saveImageInBackForShare(true).execute();
//                        } else
//                            Toast.makeText(Paintor.this, "Canvas is empty, please do an art and then share!", Toast.LENGTH_SHORT).show();
//                    }

                    new saveImageInBackForShare(true).execute();
                    if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                        mPopupWindow_post.dismiss();
                    mPopupWindow_save.dismiss();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                }
            }
        });

        iv_new_canvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_save_new_click, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_save_new_click);
                mPopupWindow_save.dismiss();
                if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                    mPopupWindow_post.dismiss();

                if (isPickFromOverlaid || isFromEditImage || (mPainting != null && mPainting.getStrokeCount() != 0)) {
                    displayAlartForNewCanvas(NEW_CANVAS_TAG);
                } else {
//                    displayHintToggleBG(colorbar_bgcolor);
//                    reflectCanvas();
                    startActivity(new Intent(PaintActivity.this, DrawNowActivity.class));
                    finish();
                }
//                mPopupWindow_save.dismiss();
            }
        });

        iv_sshot.setOnClickListener(view3 -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_screenshot_click, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_screenshot_click);
            mPopupWindow_save.dismiss();
            if (mPopupWindow_post != null && mPopupWindow_post.isShowing())
                mPopupWindow_post.dismiss();

            if (AppUtils.getDataFromResultSS() == null) {
                takeScreenCapturePermission();
            } else {
                PaintActivity.banner.setVisibility(View.VISIBLE);
                try {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION},
                                0);
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        this.startForegroundService(new Intent(PaintActivity.this, MyServiceForRecording.class));

                    } else {
                        startService(new Intent(PaintActivity.this, MyServiceForRecording.class));
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at start ser " + e.getMessage());
                }
                takeScreenshot();
            }
        });
    }

    private void takeScreenshot() {
        Intent intent = new Intent(PaintActivity.this, ScreenShotActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void displayPopupWindowNewCanvas(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.new_canvas_from_panel_item, null);

        iv_new_canvas = customView.findViewById(R.id.iv_new_canvas);
        iv_new_ovarlay = customView.findViewById(R.id.iv_new_ovarlay);
        iv_new_trace = customView.findViewById(R.id.iv_new_trace);
        iv_new_camera = customView.findViewById(R.id.iv_new_camera);

        View selection_blank = customView.findViewById(R.id.selection_blank);
        View selection_overlay = customView.findViewById(R.id.selection_overlay);
        View selection_trace = customView.findViewById(R.id.selection_trace);
        View selection_camera = customView.findViewById(R.id.selection_camera);

        selection_blank.setVisibility(View.GONE);
        selection_overlay.setVisibility(View.GONE);
        selection_trace.setVisibility(View.GONE);
        selection_camera.setVisibility(View.GONE);

//        isCanvasTooltipShown = true;
        if (Current_Mode.equals(NEW_PAINT)) {
//            iv_new_canvas.setBackground(getResources().getDrawable(R.drawable.action_item_btn));
            selection_blank.setVisibility(View.VISIBLE);
        } else if (Current_Mode.equalsIgnoreCase("LoadWithoutTrace")) {
//            iv_new_ovarlay.setBackground(getResources().getDrawable(R.drawable.action_item_btn));
            selection_overlay.setVisibility(View.VISIBLE);
        } else if (Current_Mode.equals("Edit Paint")) {
//            iv_new_trace.setBackground(getResources().getDrawable(R.drawable.action_item_btn));
            selection_trace.setVisibility(View.VISIBLE);
        } else if (Current_Mode.equals("LoadWithoutTraceFromCamera")) {
//            iv_new_camera.setBackground(getResources().getDrawable(R.drawable.action_item_btn));
            selection_camera.setVisibility(View.GONE);
            selection_overlay.setVisibility(View.VISIBLE);
        }

        mPopupWindow_canvas = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        iv_new_canvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(0);
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_blank, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_blank);
                    if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                        confirmExit(true);
                    } else {
                        displayAlartForSwitchDrawMode(0);
                    }
                    mPopupWindow_canvas.dismiss();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                }
            }
        });
        iv_new_ovarlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(1);

                boolean isStoragePassed = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_overlay, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_overlay);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else {
                                            //TODO always ask user to save image/
                                            displayAlartForSwitchDrawMode(1);
                                        }
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                } else if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 11 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_overlay, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_overlay);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else {
                                            //TODO always ask user to save image/
                                            displayAlartForSwitchDrawMode(1);
                                        }
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                } else {
                    // Android 6 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_trace, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_trace);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else
                                            displayAlartForSwitchDrawMode(2);
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                }


                /*   mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(1);

                boolean isStoragePassed = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // android 13 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // android 11 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
                    // We don't have permission so prompt the user
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }

                if (!isStoragePassed) {
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }

                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(Paintor.this, constants.canvas_switch_mode_overlay, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(Paintor.this, constants.canvas_switch_mode_overlay);
                    if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                        confirmExit(true);
                    } else {
                        *//*TODO always ask user to save image*//*
                        displayAlartForSwitchDrawMode(1);
                    }
                    mPopupWindow_canvas.dismiss();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                }*/
            }
        });
        iv_new_trace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_trace, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_trace);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else
                                            displayAlartForSwitchDrawMode(2);
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 11 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_trace, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_trace);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else
                                            displayAlartForSwitchDrawMode(2);
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                } else {
                    // Android 6 and above
                    Dexter.withContext(PaintActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    try {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_trace, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_trace);
                                        if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                                            confirmExit(true);
                                        } else
                                            displayAlartForSwitchDrawMode(2);
                                        mPopupWindow_canvas.dismiss();
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // Handle denied permission case
                                    Toast.makeText(PaintActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    // Handle permission rationale
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                }


               /* mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(2);

                boolean isStoragePassed = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // android 13 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // android 11 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
                    // We don't have permission so prompt the user
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }

                if (!isStoragePassed) {
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }

                try {
                    *//*TODO always ask user to save image*//*
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(Paintor.this, constants.canvas_switch_mode_trace, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(Paintor.this, constants.canvas_switch_mode_trace);
                    if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                        confirmExit(true);
                    } else
                        displayAlartForSwitchDrawMode(2);
                    mPopupWindow_canvas.dismiss();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                }*/
            }
        });
        iv_new_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow_canvas.dismiss();
                mNewCanvasBtn.setTag(3);
                try {
                    int permission_camera = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.CAMERA);
                    if (permission_camera != PackageManager.PERMISSION_GRANTED) {
                        try {
                            ActivityCompat.requestPermissions(
                                    PaintActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    100);
                            return;
                        } catch (Exception e) {

                        }
                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_camera, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_camera);
                    /*TODO always ask user to save image*/
                    if (mPainting.getBackgroundColor() == -1 && !isPickFromOverlaid && !isFromEditImage && mPainting != null && mPainting.getStrokeCount() == 0) {
                        confirmExit(true);
                    } else
                        displayAlartForSwitchDrawMode(3);

                    mPopupWindow_canvas.dismiss();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception while share " + e.getMessage() + " " + e.toString());
                }
            }
        });

        mPopupWindow_canvas.setOutsideTouchable(true);
//        mPopupWindow_canvas.showAsDropDown(view);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_canvas.showAsDropDown(view, (mPopupWindow_canvas.getWidth() - 15), (mPopupWindow_canvas.getHeight() + 20));
            else
                mPopupWindow_canvas.showAsDropDown(view, (view.getWidth() - mPopupWindow_canvas.getWidth() + 20), -(view.getHeight() - mPopupWindow_canvas.getHeight() + (80)));
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow_canvas.showAsDropDown(view, (mPopupWindow_canvas.getWidth() - 30), (mPopupWindow_canvas.getHeight() + 20));
            else
                mPopupWindow_canvas.showAsDropDown(view, (view.getWidth() - mPopupWindow_canvas.getWidth() + 20), -(view.getHeight() - mPopupWindow_canvas.getHeight() + 80));
        }
    }

    PopupWindow mPopupWindow;

    int mic_state = 0;

    public void displayPopupWindowRecording(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.starte_recording_panel_layout, null);
        mPopupWindow = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow.setOutsideTouchable(true);


        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow.showAsDropDown(view, (mPopupWindow.getWidth() - 15), (mPopupWindow.getHeight() + 20));
            else
                mPopupWindow.showAsDropDown(view, (view.getWidth() - mPopupWindow.getWidth() + 20), -(view.getHeight() - mPopupWindow.getHeight() + (20)));
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                mPopupWindow.showAsDropDown(view, (mPopupWindow.getWidth() - 60), (mPopupWindow.getHeight() + 40));
            else
                mPopupWindow.showAsDropDown(view, (view.getWidth() - mPopupWindow.getWidth() + 20), -(view.getHeight() - mPopupWindow.getHeight() + 40));
        }

        tv_rec_time = customView.findViewById(R.id.tv_rec_time_record);
        TextView tv_record = customView.findViewById(R.id.tv_record);
        TextView tv_pause = customView.findViewById(R.id.tv_pause);
        ImageView iv_rec_start = customView.findViewById(R.id.iv_start_recording);
        ImageView iv_rec_pause = customView.findViewById(R.id.iv_pause_recording);
        ImageView iv_rec_stop = customView.findViewById(R.id.iv_stop_recording);
        ImageView iv_mic_icn = customView.findViewById(R.id.iv_mic_icn);
        LinearLayout pauseContainer = customView.findViewById(R.id.pause_container);
        LinearLayout stopContainer = customView.findViewById(R.id.stop_container);

        if (mic_state == 0) {
            iv_mic_icn.setImageResource(R.drawable.mic_off);
        } else {
            iv_mic_icn.setImageResource(R.drawable.mic_on);
        }

        iv_mic_icn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mic_state == 0) {
                    mic_state = 1;
                    iv_mic_icn.setImageResource(R.drawable.mic_on);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_record_mic_on, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_record_mic_on);
                } else {
                    mic_state = 0;
                    iv_mic_icn.setImageResource(R.drawable.mic_off);
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_record_mic_of, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_record_mic_of);
                }
            }
        });

        if ((Integer) iv_start_recoring.getTag() == 0) {
            iv_rec_start.setVisibility(View.VISIBLE);
            iv_rec_stop.setVisibility(View.GONE);
            iv_rec_pause.setVisibility(View.GONE);
            tv_record.setText(getString(R.string.record));
            tv_pause.setText(getString(R.string.pause));
            pauseContainer.setVisibility(View.GONE);
            stopContainer.setVisibility(View.GONE);
            tv_rec_time.setVisibility(View.GONE);
            tv_rec_time.setText(getResources().getString(R.string.start_recording));
        } else if (tv_recording_time.getTag().equals(recordingState.In_Pause)) {
            iv_rec_start.setVisibility(View.VISIBLE);
            iv_rec_stop.setVisibility(View.VISIBLE);
            pauseContainer.setVisibility(View.VISIBLE);
            stopContainer.setVisibility(View.VISIBLE);
            tv_record.setText(getString(R.string.resume));
            tv_pause.setText(getString(R.string.paused));
            tv_rec_time = customView.findViewById(R.id.tv_rec_time_pause);
            tv_rec_time.setVisibility(View.VISIBLE);
            tv_rec_time.setText(tv_recording_time.getText().toString() + " Min");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                iv_rec_pause.setVisibility(View.GONE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                tv_recording_time.setOnClickListener(this);
                pauseContainer.setVisibility(View.VISIBLE);
                iv_rec_pause.setVisibility(View.VISIBLE);
            } else {
                pauseContainer.setVisibility(View.GONE);
                iv_rec_pause.setVisibility(View.GONE);
            }

            stopContainer.setVisibility(View.VISIBLE);
            iv_rec_start.setVisibility(View.GONE);
            iv_rec_stop.setVisibility(View.VISIBLE);

            tv_rec_time = customView.findViewById(R.id.tv_rec_time_record);
            tv_rec_time.setVisibility(View.VISIBLE);
            tv_rec_time.setText(tv_recording_time.getText().toString() + " Min");

            tv_record.setText(getString(R.string.recording));
            tv_pause.setText(getString(R.string.pause));
        }
        iv_rec_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                int permission_record_audio = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.RECORD_AUDIO);
                int is_storage_granted = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


                if (permission_record_audio != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            PaintActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            100
                    );

                    mPopupWindow.dismiss();
                    return;
                }
//                else if (is_storage_granted != PackageManager.PERMISSION_GRANTED) {
//                    // We don't have permission so prompt the user
//                    isFromRecording = true;
//                    ActivityCompat.requestPermissions(
//                            Paintor.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1
//                    );
//                    mPopupWindow.dismiss();
//                    return;
//                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isFromRecording = true;
                    PermissionUtils.requestStoragePermission(PaintActivity.this, 1);
                    mPopupWindow.dismiss();
//                    if (!PermissionUtils.checkReadStoragePermission(Paintor.this)) {
//                        // We don't have permission so prompt the user
//                        isFromRecording = true;
//                        PermissionUtils.requestStoragePermission(Paintor.this, 1);
//                        mPopupWindow.dismiss();
//                        return;
//                    }
                } else if (!PermissionUtils.checkStoragePermission(PaintActivity.this)) {
                    // We don't have permission so prompt the user
                    isFromRecording = true;
                    PermissionUtils.requestStoragePermission(PaintActivity.this, 1);
                    mPopupWindow.dismiss();
                    return;
                }

                if ((Integer) iv_start_recoring.getTag() == 0) {
                    try {
                        iv_start_recoring.setEnabled(false);
                        initRecorder();
                        mProjectionManager = (MediaProjectionManager) getSystemService
                                (Context.MEDIA_PROJECTION_SERVICE);

                        mMediaProjectionCallback = new MediaProjectionCallback();
                        shareScreen();
                        mPopupWindow.dismiss();
                    } catch (Exception e) {
                        Log.d(TAG, "onClick: " + e.getMessage());
                    } catch (NoClassDefFoundError er) {
                        Log.d(TAG, "onClick: " + er.getMessage());

                    }

                } else {
                    try {
                        if (tv_recording_time.getTag().equals(recordingState.In_Pause)) {
                            if (mMediaRecorder != null) {
                                mMediaRecorder.resume();
                            }
//                        iv_start_recoring.setImageResource(R.drawable.pause_recording);
                            tv_recording_time.setTag(recordingState.In_Resume);
                            isManuallyPauseRecording = false;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                pauseContainer.setVisibility(View.VISIBLE);
                                customView.findViewById(R.id.tv_rec_time_pause)
                                        .setVisibility(View.GONE);
                                iv_rec_pause.setVisibility(View.VISIBLE);
                            } else {
                                pauseContainer.setVisibility(View.GONE);
                                iv_rec_pause.setVisibility(View.GONE);
                            }

                            tv_record.setText(getString(R.string.recording));
                            tv_pause.setText(getString(R.string.pause));

                            iv_rec_start.setVisibility(View.GONE);
                            tv_rec_time = customView.findViewById(R.id.tv_rec_time_record);
                            tv_rec_time.setVisibility(View.VISIBLE);
                            tv_recording_time.setVisibility(View.VISIBLE);
                        }
                    } catch (IllegalStateException e) {
                        Log.e("TAGG", "IllegalStateException at resume " + e.getMessage());
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at resume " + e.getMessage());
                    }
                }
            }
        });

        iv_rec_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                    try {
                        mMediaRecorder.pause();
                    } catch (IllegalStateException e) {

                    }
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.PAUSE_RECORDING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.PAUSE_RECORDING);
//                    iv_start_recoring.setImageResource(R.drawable.record_play);
                    tv_recording_time.setTag(recordingState.In_Pause);
                    isManuallyPauseRecording = true;

                    stopContainer.setVisibility(View.VISIBLE);
                    iv_rec_start.setVisibility(View.VISIBLE);
                    iv_rec_pause.setVisibility(View.GONE);
                    tv_rec_time.setVisibility(View.GONE);

                    tv_record.setText(getString(R.string.resume));
                    tv_pause.setText(getString(R.string.paused));

                    String text = tv_rec_time.getText().toString();
                    tv_rec_time = customView.findViewById(R.id.tv_rec_time_pause);
                    tv_rec_time.setVisibility(View.VISIBLE);
                    tv_rec_time.setText(tv_recording_time.getText().toString() + " Min");

                    Toast.makeText(PaintActivity.this, "Pause Recording", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_rec_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaintActivity.this, "Recorded file saved in " + recordingOutputFilePath, Toast.LENGTH_SHORT).show();
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.STOP_RECORDING, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.STOP_RECORDING);


                    Log.v(TAG, "Recording Stopped");
                    stopScreenSharing();
                    mPopupWindow.dismiss();

                    try {
                        if (mMediaRecorder != null) {
                            mMediaRecorder.stop();
                            mMediaRecorder.reset();
                            mMediaRecorder = null;
                        }
                        iv_start_recoring.setTag(0);
                        pauseContainer.setVisibility(View.GONE);
                        stopContainer.setVisibility(View.GONE);
                        tv_rec_time = customView.findViewById(R.id.tv_rec_time_record);
                        tv_rec_time.setVisibility(View.GONE);
                        iv_rec_start.setVisibility(View.VISIBLE);

                        tv_record.setText(getString(R.string.record));
                        tv_pause.setText(getString(R.string.pause));

                        AlertDialog.Builder builder = new AlertDialog.Builder(PaintActivity.this);
                        builder.setMessage(getString(R.string.recording_made_successfully))
                                .setPositiveButton(R.string.goto_my_movies, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(PaintActivity.this, MyMoviesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    } catch (Exception e) {

                        Log.e("save dialog movie", "save dialog movie: " + e.getMessage());
                    }
                } catch (RuntimeException e) {
                    stopScreenSharing();
                    mPopupWindow.dismiss();
                } catch (Exception e) {
                    stopScreenSharing();
                    mPopupWindow.dismiss();
                }
            }
        });

        if (tv_recording_time.getVisibility() != View.VISIBLE)
            tv_recording_time.setVisibility(View.VISIBLE);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (tv_recording_time.getVisibility() != View.VISIBLE)
                    tv_recording_time.setVisibility(View.VISIBLE);
            }
        });

//        tv_take_snap.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                takeScreenshot();
//            }
//        });
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

        String FileFormate = detectFileFormatFromUri(String.valueOf(photoURI));

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
//            Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID +"/drawable/google_play_with_paintology");

            String text = getResources().getString(R.string.default_msg_while_share);
            text = text + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

            ArrayList<Uri> files = new ArrayList<Uri>();
            files.add(photoURI);
//            files.add(uri);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.setType(FileFormate + "/*");
            Intent receiver = new Intent(this, BroadcastShareFromCanvas.class);
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
            Log.e(PaintActivity.class.getName(), e.getMessage());
        }
    }

    public static class BroadcastShareFromCanvas extends BroadcastReceiver {

        public BroadcastShareFromCanvas() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGG", "OnReceived Called");
            try {
                for (String key : intent.getExtras().keySet()) {
                    Log.e(getClass().getSimpleName(), " " + intent.getExtras().get(key));
                    String _app_name = " " + intent.getExtras().get(key);
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
                        Toast.makeText(context, "canvas_share_image_via_" + shareFileVia, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(context, "canvas_share_image_via_" + shareFileVia);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception while share image " + e.getMessage(), e);
            }
        }
    }

    public String getFileName() {
        if (saveFileName.equalsIgnoreCase(""))
            saveFileName = System.currentTimeMillis() + "";

        return saveFileName;
    }

    public class saveImageInBackForShare extends AsyncTask<Void, Void, String> {

        Boolean isFromShare = false;

        public saveImageInBackForShare(Boolean isFromShare) {
            this.isFromShare = isFromShare;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String fileName = System.currentTimeMillis() + "";

            File myPaintingFolder = new File(KGlobal.getMyPaintingFolderPath(PaintActivity.this));
            if (!myPaintingFolder.exists()) {
                myPaintingFolder.mkdirs();
            }

            if (mPainting != null) {
                Bitmap lBitmap = mPainting.getPainting();
                saveImage(lBitmap, fileName);

            }
            return fileName + ".png";
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (fileName == "") {
                return;
            }
            if (fileName == null) {
//                Toast.makeText(Paintor.this, "Failed to save painting. Please check if SD card is avaialbe OR Storage Permission.", Toast.LENGTH_LONG).show();
                return;
            }

            File photoFile = new File(KGlobal.getMyPaintingFolderPath(PaintActivity.this) + "/" + fileName);
            if (photoFile.exists()) {
                Log.e("TAGG", "File Exist " + photoFile.getAbsolutePath());

                if (isFromShare) {
                    Uri photoURI = FileProvider.getUriForFile(PaintActivity.this,
                            getString(R.string.authority),
                            photoFile);

                    doSocialShare(photoURI);

                } else {

                    String drawingType1 = getIntent().getStringExtra("drawingType");
                    String youTubeLink = getIntent().getStringExtra("youtube_video_id");
                    String selectedImagePath = getIntent().getStringExtra("path");
                    String parentFolder = getIntent().getStringExtra("ParentFolderPath");
                    String referenceId = getIntent().getStringExtra("id");

                    Log.d("youTubeLink", "drawingType: " + drawingType1 + "\nyoutube link " + youTubeLink + "\nselectedImagePath "
                            + selectedImagePath + "\nparentFolder " + parentFolder);


                    if (drawingType1 == null) {
                        drawingType1 = "freehand";
                    }
                    if (youTubeLink == null) {
                        youTubeLink = "null";
                    }

                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.canvas_save_painting_success, Toast.LENGTH_SHORT).show();
                    }

                    String filePath = photoFile.getAbsolutePath();
                    ArrayList<String> mImageList = new ArrayList<>();
                    mImageList.add(filePath);
                    Intent returnIntent = new Intent(PaintActivity.this, PostActivity.class);
                    returnIntent.setAction("from_canvas");
                    returnIntent.putStringArrayListExtra("result", mImageList);
                    if (isGalleryPost) {
                        returnIntent.putExtra("isPostGallery", "post_gallery");
                    } else {
                        returnIntent.putExtra("isPostGallery", "post_community");
                    }
                    returnIntent.putExtra("isFromDrawing", true);

                    if (referenceId != null && !referenceId.equalsIgnoreCase("")) {
                        returnIntent.putExtra("referenceId", referenceId);
                        returnIntent.putExtra("drawingType", "tutorials");
                        returnIntent.putExtra("youtube_video_id", youTubeLink);
                        returnIntent.putExtra("path", selectedImagePath);
                        returnIntent.putExtra("ParentFolderPath", parentFolder);
                    } else {
                        returnIntent.putExtra("youtube_video_id", "");
                        returnIntent.putExtra("drawingType", "freehand");
                        returnIntent.putExtra("path", "");
                        returnIntent.putExtra("ParentFolderPath", "");
                    }
                    //showPostDialog();
                    startActivity(returnIntent);

                }
            } else {
                Toast.makeText(PaintActivity.this, "Failed To Save Painting", Toast.LENGTH_SHORT).show();
            }
            Log.e("TAGG", "File not exist");
        }
    }

    PopupWindow mColorPopupWindow;

    public void displayPopupWindowTopPickColor(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.new_canvas_from_pick_color, null);

        pal_container = customView.findViewById(R.id.pal_container);


        TextView tv_clear = customView.findViewById(R.id.tv_clear);


        ImageView iv_close_tooltip = customView.findViewById(R.id.iv_close_tooltip);
//        String[] Colors = GetPickcolor();
//        Log.e("TAGGG", "m_viewColorPanel FINAL > " + Colors.length);

        ArrayList<String> list = GetPickcolor();
        Collections.reverse(list);
//        Colors = (String[]) list.toArray();

        ColorSpinnerAdapter _adapter = new ColorSpinnerAdapter(list, PaintActivity.this, this);
        pal_container.setLayoutManager(new GridLayoutManager(this, 5));
        pal_container.setAdapter(_adapter);

        tv_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.Canvas_color_picker_clear, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.Canvas_color_picker_clear);
                String key = "top_pick_color";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(key, "-65536");
                editor.commit();
                if (mColorPopupWindow != null && mColorPopupWindow.isShowing()) {
                    mColorPopupWindow.dismiss();
                    displayPopupWindowTopPickColor(view);
                }
            }
        });

        mColorPopupWindow = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

//        mColorPopupWindow.setOutsideTouchable(true);
        if (list.size() != 0) {
            /*if (getResources().getBoolean(R.bool.is_tablet))
                mColorPopupWindow.showAsDropDown(view, (view.getWidth() - mColorPopupWindow.getWidth() + 20), -(view.getHeight() - mColorPopupWindow.getHeight() + (-10)));
            else
                mColorPopupWindow.showAsDropDown(view, (view.getWidth() - mColorPopupWindow.getWidth() + 20), -(view.getHeight() - mColorPopupWindow.getHeight() + 40));*/

            if (getResources().getBoolean(R.bool.is_tablet)) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    mColorPopupWindow.showAsDropDown(view, (mColorPopupWindow.getWidth() - 15), (mColorPopupWindow.getHeight() + 20));
                else
                    mColorPopupWindow.showAsDropDown(view, (view.getWidth() - mColorPopupWindow.getWidth() + 20), -(view.getHeight() - mColorPopupWindow.getHeight() + (-10)));
            } else {
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    mColorPopupWindow.showAsDropDown(view, (mColorPopupWindow.getWidth() - 30), (mColorPopupWindow.getHeight() + 25));
                else
                    mColorPopupWindow.showAsDropDown(view, (view.getWidth() - mColorPopupWindow.getWidth() + 20), -(view.getHeight() - mColorPopupWindow.getHeight() + 40));
            }
        }

        iv_close_tooltip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.Canvas_color_picker_close, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.Canvas_color_picker_close);
                    mColorPopupWindow.dismiss();
                } catch (Exception e) {
                }
            }
        });
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

    private void PushPickcolor(int value) {
        String key = "top_pick_color";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        String lastColor = preferences.getString(key, "");
        if (lastColor.equals("")) {
            editor.putString(key, Integer.toString(value));
        } else {
            String[] list_color = lastColor.split(",");
            Log.e("TAGGG", "m_viewColorPanel PROCESS > " + list_color.length + " <CANDY> " + (list_color.length < 20));
            Log.e("PushPickcolor", "From <> Color value " + value);

            boolean isFound = false;
            for (String _clr : list_color) {
                if (_clr.equalsIgnoreCase(value + "")) {
                    isFound = true;
                    break;
                }
            }
            Log.e("PushPickcolor", "isFound " + isFound);
            if (!isFound)
                if (list_color.length < 20) {
                    editor.putString(key, lastColor + "," + Integer.toString(value));
                } else {
                    StringBuilder _sb = new StringBuilder();
                    for (int i = 1; i < list_color.length; i++) {
                        _sb.append(list_color[i] + ",");
//                    Log.e("TAGGG", "m_viewColorPanel COLOR > " + i + " COLOR " + list_color[i]);
                    }
                    editor.putString(key, _sb.toString() + Integer.toString(value));
                }
        }
        editor.commit();

        String lastColorPalette = preferences.getString(key, "");
        colorPalette = lastColorPalette;
    }


    private ArrayList<String> GetPickcolor() {
        String key = "top_pick_color";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this);
        String _colors = preferences.getString(key, "");

        ArrayList<String> _lst_color = new ArrayList<>();
        if (!_colors.equals("")) {
            String[] list_color = _colors.split(",");
            for (String _str : list_color) {
                Log.e("TAG", "Color Code While Show " + _str);
                _lst_color.add(_str);
            }
            return _lst_color;
        } else {
            String[] list_color = new String[0];
            for (String _str : list_color) {
                Log.e("TAG", "Color Code While Show " + _str);
                _lst_color.add(_str);
            }
            return _lst_color;
        }
    }

    @Override
    public void GetCatchColor(String path) {
        try {
            if (mColorPopupWindow != null && mColorPopupWindow.isShowing()) {
                mColorPopupWindow.dismiss();
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.Canvas_color_picker_pick_color, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.Canvas_color_picker_pick_color);
            m_viewCurColor.setBackgroundColor(Integer.parseInt(path));
            /*Set current stroke color*/
            setColorInBox(Integer.parseInt(path));
            mPrefBrushColor = Integer.parseInt(path);
            mPainting.setBrushColor(mPrefBrushColor);
            /*End set stroke color*/

            if (m_viewColorContainer.getVisibility() == View.VISIBLE) {
                m_viewColorContainer.setVisibility(View.GONE);
            }
            setHSVColor(Integer.parseInt(path));
            setHue(getHue());
            onClick(mPaintmenu_pen);
        } catch (Exception ex) {
        }
    }


    @Override
    public void hideShowCross(boolean isHide) {
        Log.e("TAG", "hideShowCross called isHide " + isHide);
        if (isHide) {
            if (CurrentMode != 1 && CurrentMode != 3)
                view_cross.setVisibility(View.GONE);
        } else {
            view_cross.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setSize() {
        try {
//            tv_brush_percentage.setText(mTxtBrushName.getText().toString() + "\n" + mTxtSize.getText().toString());
            tv_brush_percentage.setText(mTxtSize.getText().toString());
            tv_brush_percentage.invalidate();
        } catch (Exception e) {

        }
    }

    @Override
    public void setSpecialFunctionState(SwitchCompat selectedSwitch) {
        try {
            if (switch_singleTap.isChecked()) {
                tv_special_fun.setText("Single\nTap");
            } else if (_switch_line.isChecked()) {
                tv_special_fun.setText("Straight\nLine");
            } else if (switch_gray_scale.isChecked()) {
                tv_special_fun.setText("Gray\nScale");
             /*   rl_gray_scale.setVisibility(View.VISIBLE);
                m_viewColorPanel.setVisibility(View.GONE);
                m_viewArrow.setVisibility(View.GONE);

                mPreviousPrefBrushColor = mPrefBrushColor;
                mPrefBrushColor = currentGrayScaleColor;
                mBrushColor = currentGrayScaleColor;
                setHSVColor(currentGrayScaleColor);
                mPainting.setBrushColor(getColor());

                setColorInBox(currentGrayScaleColor);

                if (CurrentMode == 2 || Current_Mode.equalsIgnoreCase("Reload Painting")) {
                    iv_selected_image.setImageBitmap(_grayScaleForTrace);
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_gray_scale_on, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_gray_scale_on);*/
            }

            if (switch_gray_scale.isChecked()) {
                rl_gray_scale.setVisibility(View.VISIBLE);
                m_viewColorPanel.setVisibility(View.GONE);
                m_viewArrow.setVisibility(View.GONE);

                mPreviousPrefBrushColor = mPrefBrushColor;
                mPrefBrushColor = currentGrayScaleColor;
                mBrushColor = currentGrayScaleColor;
                setHSVColor(currentGrayScaleColor);
                mPainting.setBrushColor(getColor());

                setColorInBox(currentGrayScaleColor);

                if (CurrentMode == 2 || Current_Mode.equalsIgnoreCase("Reload Painting")) {
                    iv_selected_image.setImageBitmap(_grayScaleForTrace);
                }
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.canvas_gray_scale_on, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_gray_scale_on);
            }

            if (switch_block_coloring.isChecked()) {

                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.auto_color_picker_activated, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_activated);
                autoColorPickerActivated = true;
                pickNewColorMode = true;
                mPaintmenu_pen.setImageResource(R.drawable.color_picker_activated);
                iv_cursor_icon.setVisibility(View.GONE);
                iv_gps_icon.setVisibility(View.VISIBLE);
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.auto_color_picker_deactivated, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.auto_color_picker_deactivated);
                autoColorPickerActivated = false;
                pickNewColorMode = false;
                mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);

                iv_gps_icon.setVisibility(View.GONE);
                iv_cursor_icon.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    mPaintView.cancelDragAndDrop();

                selected_bitmap = null;
                mColorPopupWindow = null;

                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    moveCursor();
                else {
                    moveCursorVeritcal();
                }

            }


            tv_special_fun.setVisibility(View.VISIBLE);
            if (!switch_singleTap.isChecked() && !_switch_line.isChecked() && !switch_gray_scale.isChecked()) {
                tv_special_fun.setVisibility(View.GONE);
                return;
            }

            if (switch_singleTap.isChecked() && _switch_line.isChecked() && switch_gray_scale.isChecked()) {
                if (selectedSwitch.equals(switch_singleTap))
                    tv_special_fun.setText("Single\nTap");
                else if (selectedSwitch.equals(_switch_line))
                    tv_special_fun.setText("Straight\nLine");
                else if (selectedSwitch.equals(switch_gray_scale))
                    tv_special_fun.setText("Gray\nScale");
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at setSPecialFunction " + e.getMessage());
        }
    }

    public void pickImageFromGallery(boolean is_overlay) {
//        int permission = ActivityCompat.checkSelfPermission(Paintor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    Paintor.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    1
//            );
//            return;
//        }
//        if (!PermissionUtils.checkReadStoragePermission(Paintor.this)) {
//            // We don't have permission so prompt the user
//            PermissionUtils.requestStoragePermission(Paintor.this, 1);
//            return;
//        }
        if (is_overlay) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.PICK_IMAGE_FOR_OVERLAY, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.PICK_IMAGE_FOR_OVERLAY);
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, 120);
        } else {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.PICK_IMAGE_FOR_TRACE, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(this, constants.PICK_IMAGE_FOR_TRACE);
            SELECT_PHOTO_REQUEST = 400;

            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO_REQUEST);
        }
    }

    public void captureImage() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, "captureImage", Toast.LENGTH_SHORT).show();
        }
        KGlobal.appendLog(this, "captureImage");

        int permission_camera = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.CAMERA);
        if (permission_camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        } else {
            showDialogForCamera();
        }
    }

    public int CAMERA_OPERATION = 0;
    public int PICK_IMAGE_CAMERA = 151;
    public int REQUEST_IMAGE_CAMERA = 2;

//    public void addCamera() {
//        if (BuildConfig.DEBUG) {
//            Toast.makeText(Paintor.this, constants.Click_Camera_Selection, Toast.LENGTH_SHORT).show();
//        }
//        FirebaseUtils.logEvents(this, constants.Click_Camera_Selection);
//
//        if (BuildConfig.DEBUG) {
//            KGlobal.appendLog(this, constants.Click_Camera_Selection);
//        }
//
//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//
//            if (BuildConfig.DEBUG) {
//                KGlobal.appendLog(this, "> LOLLIPOP_MR1");
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if (!PermissionUtils.checkCameraPermission(Paintor.this)) {
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(Paintor.this, ">= Q & dont has camera permission", Toast.LENGTH_SHORT).show();
//                        KGlobal.appendLog(this, ">= Q & dont has camera permission");
//                    }
//
//                    // We don't have permission so prompt the user
//                    PermissionUtils.requestCameraPermission(Paintor.this, REQUEST_IMAGE_CAMERA);
//                    return;
//                }
//
////                if (PermissionUtils.checkReadStoragePermission(this)) {
////                    if (BuildConfig.DEBUG) {
////                        Toast.makeText(Paintor.this, ">= Q & has camera & Read Storage permission", Toast.LENGTH_SHORT).show();
////                    }
////                    KGlobal.appendLog(this, ">= Q & has camera & Read Storage permission");
////                    startActivityForResult(new Intent(Paintor.this, CameraActivity.class), PICK_IMAGE_CAMERA);
////                }
//
//
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, ">= Q & has camera permission", Toast.LENGTH_SHORT).show();
//                    KGlobal.appendLog(this, ">= Q & has camera permission");
//                }
//
//                startActivityForResult(new Intent(Paintor.this, CameraActivity.class), PICK_IMAGE_CAMERA);
//
//            } else {
//                if (BuildConfig.DEBUG) {
//                    KGlobal.appendLog(this, "< Q");
//                }
//                SandriosCamera
//                        .with(this)
//                        .setShowPicker(false)
//                        .setVideoFileSize(40)®
//                        .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
//                        .enableImageCropping(true)
//                        .launchCamera(new SandriosCamera.CameraCallback() {
//                            @Override
//                            public void onComplete(CameraOutputModel model) {
//                                if (BuildConfig.DEBUG) {
//                                    Toast.makeText(Paintor.this, "SandriosCamera onComplete", Toast.LENGTH_SHORT).show();
//                                    KGlobal.appendLog(Paintor.this, "SandriosCamera onComplete");
//                                }
//
//
//                                File _file = new File(model.getPath());
//                                Log.e("TAG", "File Logs At OnComplete " + model.getPath() + " FileName " + _file.getName() + " Parent Name " + _file.getParentFile().getAbsolutePath());
//                                Log.e("Type", "" + model.getType() + " CAMERA_OPERATION " + CAMERA_OPERATION);
//                                try {
//
//                                    resetCanvas();
//                                    restartTimer();
//                                    if (CAMERA_OPERATION == 1) {
//                                        opneCameraPicInOverlay(_file);
//                                    } else if (CAMERA_OPERATION == 2) {
//                                        openCameraPicInTraceMode(_file);
//                                    }
//                                } catch (Exception e) {
//                                    Log.e("TAGG", "Exception at onResult " + e.getMessage());
//                                }
//
//                            }
//                        });
//            }
//        } else {
//            if (BuildConfig.DEBUG) {
//                Toast.makeText(Paintor.this, "<= LOLLIPOP_MR1", Toast.LENGTH_SHORT).show();
//                KGlobal.appendLog(this, "<= LOLLIPOP_MR1");
//            }
//
//            output = new File(new File(getFilesDir(), PHOTOS), FILENAME);
//
//            if (output.exists()) {
//                output.delete();
//            } else {
//                output.getParentFile().mkdirs();
//            }
//
//            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);
//
//            i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                ClipData clip =
//                        ClipData.newUri(getContentResolver(), "A photo", outputUri);
//                i.setClipData(clip);
//                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            } else {
//                List<ResolveInfo> resInfoList =
//                        getPackageManager()
//                                .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
//
//                for (ResolveInfo resolveInfo : resInfoList) {
//                    String packageName = resolveInfo.activityInfo.packageName;
//                    grantUriPermission(packageName, outputUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }
//            }
//            try {
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, "starting CAMERA_REQUEST", Toast.LENGTH_SHORT).show();
//                    KGlobal.appendLog(this, "starting CAMERA_REQUEST");
//                }
//
//                startActivityForResult(i, CAMERA_REQUEST);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }
//    }


    public void addCamera() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.Click_Camera_Selection, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.Click_Camera_Selection);


        // Check permission for both storage and camera on Android Q and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(PaintActivity.this,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(PaintActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                // Request both storage and camera permissions if not granted
                String[] permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(PaintActivity.this, permissions, 1);
            }
        } else {
            // Check permissions on older versions
            if (!PermissionUtils.checkStoragePermission(PaintActivity.this) ||
                    !PermissionUtils.checkCameraPermission(PaintActivity.this)) {
                // Request permissions if not granted
                PermissionUtils.requestStorageAndCameraPermissions(PaintActivity.this, 1);
            }
        }

        // Launch camera intent based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(PaintActivity.this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(PaintActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                Log.e("HH=", "paintor-in");
                // Request both storage and camera permissions if not granted
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

        } else {
            try {

                // old code
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                output = new File(new File(getFilesDir(), PHOTOS), FILENAME);

                if (output.exists()) {
                    output.delete();
                } else {
                    output.getParentFile().mkdirs();
                }

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);

                i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, orientation);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = ClipData.newUri(getContentResolver(), "A photo", outputUri);
                    i.setClipData(clip);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, outputUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }

                startActivityForResult(i, CAMERA_REQUEST);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e) {
                Log.e("TAG", "Exception to open camera " + e.toString());
            }
        }
    }

    private void LoadWithoutTrace(String path, String ParentFolderPath, boolean isPickFromOverlaid) {
        selectedImagePath = path;
        String parentFolder = ParentFolderPath;
        mPainting.setBackgroundBitmap(null);
        mPainting.clearPainting();
        System.gc();
        PaintingType = "save_overlay_canvas_drawing";
        loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
        if (mPainting != null) {
            mPainting.syncComposeCanvas();
            mPainting.syncUndoCanvas();
        }
        mPrefBackgroundColor = -1;
        mPainting.setBackgroundColor(mPrefBackgroundColor);
        savePaintingPreference();

        mPaintView.reDraw(null);
        mPaintView.invalidate();

        ll_toggle.setVisibility(View.GONE);
        seekbar_1.setVisibility(View.GONE);
        seekBarContainer4.setVisibility(View.GONE);
        hideShowSeekbarView(true);


        selectedImagePath = parentFolder + "/" + selectedImagePath;


        //if (lIntent.hasExtra("isPickFromOverlaid")) {
        this.isPickFromOverlaid = isPickFromOverlaid;
        //}

        mPaintingTemp.setBackgroundBitmap(null);
        mPaintingTemp.clearPainting();

        if (mPaintingTemp != null) {
            mPaintingTemp.syncComposeCanvas();
            mPaintingTemp.syncUndoCanvas();
        }
        mPaintViewTemp.reDraw(null);


        constants.putString("pickfromresult", "yes", PaintActivity.this);
        constants.putString("isfromTrace", "", PaintActivity.this);
        constants.putString("isfromoverlay", "yes", PaintActivity.this);
        constants.putString("path", selectedImagePath, PaintActivity.this);
        constants.putString("parentFolder", parentFolder, PaintActivity.this);
        constants.putString("type", "", PaintActivity.this);

        mPaintingTemp.setBackgroundColor(R.color.white);
        mPaintViewTemp.setVisibility(View.GONE);
        constants.putString("action_name", "", PaintActivity.this);
        // new code
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void captureImageSet(String path, String ParentFolderPath, boolean isPickFromOverlaid) {
        selectedImagePath = path;
        String parentFolder = ParentFolderPath;
        mPainting.setBackgroundBitmap(null);
        mPainting.clearPainting();
        System.gc();
        PaintingType = "save_camera_canvas_drawing";
//        mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);


        Log.e("HH=", "pth=" + path);
        Log.e("HH=", "prnt=" + parentFolder);

        loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
        if (mPainting != null) {
            mPainting.syncComposeCanvas();
            mPainting.syncUndoCanvas();
        }

        iv_selected_image.setImageBitmap(mPainting.getBitmap());
        iv_selected_image.setVisibility(View.VISIBLE);
        iv_selected_image.setAlpha(0.1f);

//        mPrefBackgroundColor = -1;
//        mPainting.setBackgroundColor(mPrefBackgroundColor);
        savePaintingPreference();

        mPaintView.reDraw(null);
        mPaintView.invalidate();

        ll_toggle.setVisibility(View.GONE);
        seekbar_1.setVisibility(View.GONE);
        seekBarContainer4.setVisibility(View.GONE);
        hideShowSeekbarView(true);

        selectedImagePath = parentFolder + "/" + selectedImagePath;

        iv_selected_image.setAlpha(0.0f);
        iv_temp_traced.setAlpha(0.0f);
        mPainting.backgroundPaint.setAlpha(0);

        mPainting.syncComposeCanvas();
        mPaintView.reDraw(null);

        mPaintingTemp.setBackgroundBitmap(null);
        mPaintingTemp.clearPainting();

        if (mPaintingTemp != null) {
            mPaintingTemp.syncComposeCanvas();
            mPaintingTemp.syncUndoCanvas();
        }
        mPaintViewTemp.reDraw(null);

        mPaintingTemp.setBackgroundColor(R.color.white);
        mPaintViewTemp.setVisibility(View.GONE);

        this.isPickFromOverlaid = isPickFromOverlaid;


        constants.putString("pickfromresult", "yes", PaintActivity.this);
        constants.putString("isfromTrace", "", PaintActivity.this);
        constants.putString("isfromoverlay", "yes", PaintActivity.this);
        constants.putString("path", selectedImagePath, PaintActivity.this);
        constants.putString("type", "camera", PaintActivity.this);

        constants.putString("action_name", "", PaintActivity.this);
        // new code
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }


    public void displayAlartForNewCanvas(int Tag) {

        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(PaintActivity.this);
        String str;
        str = getString(R.string.save_drawing);

        lBuilder1.setMessage(str).setCancelable(true);

        lBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                savePainting(Tag);
                //finish();
            }
        });
        lBuilder1.setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                reflectCanvas();
                dialog.dismiss();
                displayHintToggleBG(colorbar_bgcolor);
            }
        });
        Dialog dialog = lBuilder1.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void displayAlartForSwitchDrawMode(int Tag) {

        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(PaintActivity.this);
        String str;
        str = getString(R.string.save_drawing);

        lBuilder1.setMessage(str).setCancelable(true);

        lBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                savePainting(Tag);
                //finish();
            }
        });
        lBuilder1.setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.e("TAGG", "confirmExit called LN 6405");
                confirmExit(true);
                dialog.cancel();
            }
        });
        Dialog dialog = lBuilder1.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();

    }

    public void startBGColor() {
        try {
            if (mStatus != 1) {
                hideZoomButton();
//                mStatus = 1;
                mPaintView.reDraw(null);
            }

            Log.e("TAGGGG", "onClick Select event > " + isBGSelected);
            bg_color_temp = mPrefBackgroundColor;
            if (m_viewColorContainer.getVisibility() == View.VISIBLE) {

                Integer oldColor = getColor();
//                    setHSVColor(mPrefBrushColor);
                m_viewColorContainer.setVisibility(View.INVISIBLE);

                isBGSelected = false;

                linear_background_color.setVisibility(View.GONE);
                colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));

                if (brushSettingsPopup.isShowing())
                    mStatus = 9;
                else {
//                    mStatus = 1;
                }
                isBGSelected = false;
                colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn));
//                mPainting.setBrushColor(getColor());

                Integer newColor = getColor();
                Log.e("TAGGG", " oldColor " + oldColor + " newColor " + newColor + " Match " + oldColor.equals(newColor));
                if (!oldColor.equals(newColor) && tv_recording_time.getTag().equals(recordingState.In_Resume)) {
                    addColorChangeEvent();
                }
            } else {

                if (iv_gps_icon.getVisibility() == View.VISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        mPaintView.cancelDragAndDrop();
                    iv_gps_icon.setVisibility(View.GONE);
                    selected_bitmap = null;
                    mPaintmenu_pen.setImageResource(R.drawable.color_picker_canvas);
//                    mPaintmenu_pen.setImageResource(R.drawable.pen_icon_disable);
                }

                if (current_mode != null && !current_mode.toString().equalsIgnoreCase(canvas_mode.canvas_back.toString()) && iv_cursor_icon.getVisibility() != View.VISIBLE) {
                    iv_cursor_icon.setVisibility(View.VISIBLE);
                }

//                bg_color_temp = mPrefBackgroundColor;
//                Color.colorToHSV(mPrefBrushColor, currentColorHsv);

                // update view
                moveCursor();
                moveTarget();

                mPaintView.reDraw(null);
                mStatus = 8;

                hideZoomButton();

                isBGSelected = true;
                colorlayout_2.setBackground(getResources().getDrawable(R.drawable.set_action_item_btn_color));
                linear_background_color.setVisibility(View.VISIBLE);

                m_viewColorContainer.setVisibility(View.VISIBLE);
                m_viewColorContainer.bringToFront();

                viewSatVal.setHue(viewSatVal.color[0]);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception while draw " + e.getMessage() + " " + e.toString());
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*This method will prompt social media login dialog when user click on upload zip file.*/
    private void showLoginDialog() {
        Intent intent = new Intent(PaintActivity.this, LoginActivity.class);
        intent.putExtra("title", getString(R.string.login_heading_community));
        intent.putExtra("fromPaintor", true);
        loginActivityLauncher.launch(intent);
    }
//    public void showLoginDialog() {
//        final Dialog dialog = new Dialog(Paintor.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.social_media_login_layout);
//        if (BuildConfig.DEBUG) {
//            Toast.makeText(Paintor.this, constants.open_social_login_canvas_dialog, Toast.LENGTH_SHORT).show();
//        }
//        FirebaseUtils.logEvents(Paintor.this, constants.open_social_login_canvas_dialog);
//        Button btn_fb = (Button) dialog.findViewById(R.id.fb);
//
//        TextView tv_community_link = (TextView) dialog.findViewById(R.id.tv_community_link);
//        tv_community_link.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, constants.open_social_login_canvas_dialog_link_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(Paintor.this, constants.open_social_login_canvas_dialog_link_click);
//                startActivity(new Intent(Paintor.this, Community.class));
//                dialog.dismiss();
//
//            }
//        });
//
//        btn_fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Perfome Action
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(Paintor.this, constants.FACEBOOK_LOGIN);
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
//                if (BuildConfig.DEBUG) {
//                    Toast.makeText(Paintor.this, constants.Social_Paintology_Login, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(Paintor.this, constants.Social_Paintology_Login);
//                showDialog();
//            }
//        });
//        dialog.show();
//    }

    boolean isHintShowed = false;

    void showStrokeHintDialog() {


//        String value = mPrefs.getString(StringConstants.watch_video, "false");
//        if (value.equalsIgnoreCase("true")) {
//
//            return;
//        }


        String _local = constants.getString(constants.selected_language, PaintActivity.this);
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.strokes_dialog_message, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PaintActivity.this, constants.strokes_dialog_message);

        iv_arrow.setVisibility(View.VISIBLE);
        frm_hint.setVisibility(View.VISIBLE);

       /* iv_arrow.setVisibility(View.GONE);
        frm_hint.setVisibility(View.GONE);*/


        AppCompatCheckBox cb_1 = (AppCompatCheckBox) findViewById(R.id.cb_1);
        cb_1.setVisibility(View.GONE);

        TextView btn_close = (TextView) findViewById(R.id.btn_close);

        cb_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                constants.putString(constants.is_dont_show_selected, cb_1.isChecked() ? "true" : "false", PaintActivity.this);


            }
        });

        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_arrow.setVisibility(View.GONE);
                frm_hint.setVisibility(View.GONE);

//                editor.putString(StringConstants.watch_video, "true");
//                editor.commit();

                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.strokes_dialog_message_close, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.strokes_dialog_message_close);
                if (cb_1.isChecked()) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.strokes_dialog_message_chkbox, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.strokes_dialog_message_chkbox);
                }
                constants.putString(constants.is_dont_show_selected, cb_1.isChecked() ? "true" : "false", PaintActivity.this);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (btn_close != null) {
                    btn_close.performClick();
                }
            }
        }, 2000);
    }

    void showCanvasHintDialog() {

        String value = mPrefs.getString(StringConstants.switch_to_tutorial, "false");
        if (value.equalsIgnoreCase("true")) {
            return;
        }


        String _local = constants.getString(constants.selected_language, PaintActivity.this);
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.dialog_canvas_message, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PaintActivity.this, constants.dialog_canvas_message);

        iv_arrow_canvas.setVisibility(View.VISIBLE);
        frm_hint_canvas.setVisibility(View.VISIBLE);

        AppCompatCheckBox cb_1_canavs = (AppCompatCheckBox) findViewById(R.id.cb_1_canvas);
        cb_1_canavs.setVisibility(View.GONE);

        TextView btn_close_canvas = (TextView) findViewById(R.id.btn_close_canvas);

        cb_1_canavs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                constants.putString(constants.is_dont_show_selected_canvas, cb_1_canavs.isChecked() ? "true" : "false", PaintActivity.this);
            }
        });

        btn_close_canvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_arrow_canvas.setVisibility(View.GONE);
                frm_hint_canvas.setVisibility(View.GONE);

                editor.putString(StringConstants.switch_to_tutorial, "true");
                editor.commit();

                if (BuildConfig.DEBUG) {
                    Toast.makeText(PaintActivity.this, constants.dialog_canvas_message_close, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(PaintActivity.this, constants.dialog_canvas_message_close);
                if (cb_1_canavs.isChecked()) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(PaintActivity.this, constants.dialog_canvas_message_chkbox, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(PaintActivity.this, constants.dialog_canvas_message_chkbox);
                }
                constants.putString(constants.is_dont_show_selected_canvas, cb_1_canavs.isChecked() ? "true" : "false", PaintActivity.this);
            }
        });
    }

    void showCanvasStrokeDialog() {

        String value = mPrefs.getString(StringConstants.switch_to_strokes, "false");
        if (value.equalsIgnoreCase("true")) {
            return;
        }

        String _local = constants.getString(constants.selected_language, PaintActivity.this);
        Locale myLocale = new Locale(_local);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = myLocale;
        res.updateConfiguration(config, dm);

        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, constants.dialog_canvas_message, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(PaintActivity.this, constants.dialog_canvas_message);

        iv_arrow_stroke.setVisibility(View.VISIBLE);
        frm_hint_stroke.setVisibility(View.VISIBLE);

        TextView btn_close_canvas = (TextView) findViewById(R.id.btn_close_stroke);
        btn_close_canvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_arrow_stroke.setVisibility(View.GONE);
                frm_hint_stroke.setVisibility(View.GONE);

                editor.putString(StringConstants.switch_to_tutorial, "true");
                editor.commit();
            }
        });
    }


    void hideCanvasHintDialog() {
        try {
            if (iv_arrow_canvas.getVisibility() == View.VISIBLE)
                iv_arrow_canvas.setVisibility(View.GONE);
            if (frm_hint_canvas.getVisibility() == View.VISIBLE)
                frm_hint_canvas.setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    void hideHintDialog() {
        try {
            if (iv_arrow.getVisibility() == View.VISIBLE)
                iv_arrow.setVisibility(View.GONE);
            if (frm_hint.getVisibility() == View.VISIBLE)
                frm_hint.setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

//    public void showDialog() {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        final Dialog dialog = new Dialog(Paintor.this);
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
//                    LoginRequestModel model = new LoginRequestModel(
//                            edt_pass.getText().toString().trim(),
//                            edt_uname.getText().toString().trim(),
//                            edt_email.getText().toString().trim(),
//                            edt_pass.getText().toString().trim()
//                    );
//                    Log.e("TAGG", "Login Data " + model.user_email + " " + model.user_id + " " + model.user_name);
//                    if (KGlobal.isInternetAvailable(Paintor.this)) {
//                        addUser(model, LOGIN_FROM_PAINTOLOGY);
//                    } else
//                        Toast.makeText(Paintor.this, getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PaintActivity.this, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.GOOGLE_LOGIN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                                Toast.makeText(PaintActivity.this, constants.FacebookLoginSuccess, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.FacebookLoginSuccess);
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
                                constants.putString(constants.Username, fnm, PaintActivity.this);
                                constants.putString(constants.Password, fid, PaintActivity.this);
                                constants.putString(constants.Email, mail, PaintActivity.this);
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
                Toast.makeText(PaintActivity.this, constants.FacebookLoginFailed, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.FacebookLoginFailed);
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
        RequestBody req_ip_address = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.IpAddress, PaintActivity.this));
        RequestBody req_ip_country = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCountry, PaintActivity.this));
        RequestBody req_ip_city = RequestBody.create(MediaType.parse("text/plain"), constants.getString(constants.UserCity, PaintActivity.this));


        try {
            _ip = constants.getString(constants.IpAddress, PaintActivity.this);
            _country = constants.getString(constants.UserCountry, PaintActivity.this);
            _city = constants.getString(constants.UserCity, PaintActivity.this);

            SharedPref sharedPref = new SharedPref(this);


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
        showProgress(false);
        try {
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getObjData() != null && response.body().getObjData().getUser_id() != null) {

                            if (response.body().getObjData().isZipUploaded.equalsIgnoreCase("true")) {
                                constants.putString(constants.IsFileUploaded, "true", PaintActivity.this);
                            } else
                                constants.putString(constants.IsFileUploaded, "false", PaintActivity.this);

                            constants.putString(constants.UserId, response.body().getObjData().getUser_id() + "", PaintActivity.this);
                            constants.putString(constants.Salt, (response.body().getObjData().getSalt() != null ? response.body().getObjData().getSalt() : ""), PaintActivity.this);
                            Log.e("TAGGG", "Salt Value is " + response.body().getObjData().getSalt());

                            new saveImageInBackForShare(false).execute();

                            if (loginType == LOGIN_FROM_PAINTOLOGY) {
                                constants.putString(constants.Username, model.user_name, PaintActivity.this);
                                constants.putString(constants.Password, model.user_id, PaintActivity.this);
                                constants.putString(constants.Email, model.user_email, PaintActivity.this);
                                constants.putString(constants.LoginInPaintology, "true", PaintActivity.this);
                                LoginInPaintology = constants.getString(constants.LoginInPaintology, PaintActivity.this);
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().toLowerCase().contains("user already exists")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.PaintologyLoginSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.PaintologyLoginSuccess);
                                    } else if (response.body().getObjData().getStatus().toLowerCase().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.PaintologyRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.PaintologyRegistration);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_FB) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.FacebookRegister, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.FacebookRegister);
                                    }
                                }
                            } else if (loginType == LOGIN_FROM_GOOGLE) {
                                if (response.body().getObjData().getStatus() != null && !response.body().getObjData().getStatus().isEmpty()) {
                                    Log.e("TAGGG", "Login Status " + response.body().getObjData().getStatus());
                                    if (response.body().getObjData().getStatus().contains("user inserted")) {
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(PaintActivity.this, constants.GoogleRegistration, Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseUtils.logEvents(PaintActivity.this, constants.GoogleRegistration);
                                    }
                                }
                            }
                            String _user_id = constants.getString(constants.UserId, PaintActivity.this);
                            MyApplication.get_realTimeDbUtils(PaintActivity.this).autoLoginRegister(response.body().getObjData().getStatus());
                            if (KGlobal.isInternetAvailable(PaintActivity.this) && _user_id != null && !_user_id.isEmpty()) {
                                startService(new Intent(PaintActivity.this, SendDeviceToken.class));
                            }
                        }
                    } else {
                        if (loginType == LOGIN_FROM_FB)
                            LoginManager.getInstance().logOut();
                        else if (loginType == LOGIN_FROM_GOOGLE) {
                            Auth.GoogleSignInApi.signOut(googleApiClient);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(PaintActivity.this, constants.PaintologyLoginFailed, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(PaintActivity.this, constants.PaintologyLoginFailed);
                        }

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.event_failed_to_adduser, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.event_failed_to_adduser);
                        Toast.makeText(PaintActivity.this, "Failed To Add User", Toast.LENGTH_SHORT).show();
                    }
//                    new SaveTask(model).execute();
                    hideProgress();
                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    Log.e("TAGG", "add user in failure " + t.getMessage(), t);
                    hideProgress();
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "add user in Exception " + e.getMessage(), e);
            hideProgress();
        }
    }

    void showProgress(Boolean isFromUpload) {
        try {
            progressDialog = new ProgressDialog(PaintActivity.this);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {

            boolean STORAGE = false, CAMERA = false, RECORDING = false;
            if (requestCode == REQUEST_IMAGE_CAMERA) {
                startActivityForResult(new Intent(PaintActivity.this, CameraActivity.class), PICK_IMAGE_CAMERA);
//                if (PermissionUtils.checkReadStoragePermission(this)) {
//                    startActivityForResult(new Intent(Paintor.this, CameraActivity.class), PICK_IMAGE_CAMERA);
//                }
            } else if (permissions != null && permissions.length > 0) {

                int permission = 0;
                System.out.println("permissions :" + permissions);
                if (permissions[0].equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    STORAGE = true;
                    permission = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permission = PackageManager.PERMISSION_GRANTED;
                    }
                } else if (permissions[0].equalsIgnoreCase("android.permission.READ_MEDIA_IMAGES")) {
                    STORAGE = true;
                    permission = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.READ_MEDIA_IMAGES);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permission = PackageManager.PERMISSION_GRANTED;
                    }
                } else if (permissions[0].equalsIgnoreCase("android.permission.CAMERA")) {

                    CAMERA = true;
                    permission = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.CAMERA);
                } else if (permissions[0].equalsIgnoreCase("android.permission.RECORD_AUDIO")) {
                    RECORDING = true;
                    permission = ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.RECORD_AUDIO);

                }
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user


                    if (STORAGE) {
                        Toast.makeText(PaintActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.deny_storage_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.deny_storage_permission);
                    } else if (CAMERA) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.deny_camera_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.deny_camera_permission);
                        Toast.makeText(PaintActivity.this, getResources().getString(R.string.camera_permission_msg), Toast.LENGTH_LONG).show();
                    } else if (RECORDING) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.deny_recording_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.deny_recording_permission);
                        Toast.makeText(PaintActivity.this, getResources().getString(R.string.recording_permission_msg), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (STORAGE) {
                        Log.e("TAGG", "onRequestPermissionsResult isFromSavePainting " + isFromSavePainting + " requestCode " + requestCode);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.allow_storage_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.allow_storage_permission);
                        if (requestCode == 155) {
                            saveAndExitpainting();
                        } else if (isFromSavePainting) {
                            isFromSavePainting = false;
                            savePainting(true, requestCode);
                        } else if (isFromRecording) {
                            isFromRecording = false;
                            iv_start_recoring.setEnabled(false);
                            initRecorder();
//                            prepareRecorder();
                            mProjectionManager = (MediaProjectionManager) getSystemService
                                    (Context.MEDIA_PROJECTION_SERVICE);
                            mMediaProjectionCallback = new MediaProjectionCallback();
                            shareScreen();
                        }
                    } else if (CAMERA) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.allow_camera_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.allow_camera_permission);
//                        addCamera();
                        showDialogForCamera();
                    } else if (RECORDING) {
                        try {

//                            int is_storage_granted = ActivityCompat.checkSelfPermission(Paintor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            isFromRecording = true;
//                            if (is_storage_granted != PackageManager.PERMISSION_GRANTED) {
//                                // We don't have permission so prompt the user
//                                ActivityCompat.requestPermissions(
//                                        Paintor.this,
//                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                        1
//                                );
//                                return;
//                            }
//                            if (!PermissionUtils.checkReadStoragePermission(Paintor.this)) {
//                                // We don't have permission so prompt the user
//                                PermissionUtils.requestStoragePermission(Paintor.this, 1);
//                                return;
//                            }

                            iv_start_recoring.setEnabled(false);
                            initRecorder();
//                            prepareRecorder();
                            mProjectionManager = (MediaProjectionManager) getSystemService
                                    (Context.MEDIA_PROJECTION_SERVICE);
                            mMediaProjectionCallback = new MediaProjectionCallback();
                            shareScreen();
                        } catch (Exception e) {

                        } catch (NoClassDefFoundError er) {

                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(PaintActivity.this, constants.allow_recording_permission, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(PaintActivity.this, constants.allow_recording_permission);
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    public class saveImageForRecording extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("TAGG", "saveImageForRecording onPreExecute ");
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str7 = "";
            if (mPainting != null) {
                Bitmap lBitmap = mPainting.getPainting();
                str7 = saveImage(lBitmap, recordedFileName);

                try {

                    SharedPreferences appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(PaintActivity.this);

                    Gson gson = new Gson();
                    String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");
                    Type type = new TypeToken<ArrayList<TraceReference>>() {
                    }.getType();
                    ArrayList<TraceReference> traceList = gson.fromJson(json, type);
                    if (traceList == null)
                        traceList = new ArrayList<TraceReference>();
                    else
                        Log.e("TAGGG", "storeInTraceList size " + traceList.size() + " drawingFileName " + recordedFileName + " selectedImagePath " + selectedImagePath);

                    TraceReference traceReference = new TraceReference();
                    try {
                        traceReference.setUserPaintingName(recordedFileName + ".png");
                    } catch (Exception e) {

                    }
                    traceReference.set_drawing_type(drawing_type.Movie);
                    traceList.add(traceReference);
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    String json_1 = gson.toJson(traceList);
                    prefsEditor.putString(constants.getTraceList_Gson_Key(), json_1);
                    prefsEditor.commit();
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            } else {
                Log.e("TAGG", "saveImageForRecording doInBackground onelse ");
            }
            return str7;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == "") {
                return;
            }
            if (s == null) {
                Toast.makeText(PaintActivity.this, "Failed to save painting. Please check if SD card is avaialbe OR Storage Permission.", Toast.LENGTH_LONG).show();
                return;
            }
            Log.e("TAGGG", "OnPost Execute " + s);
//            Toast.makeText(Paintor.this, s, Toast.LENGTH_LONG).show();
        }
    }


    private int tipSizeSmall;
    private int tipSizeRegular;
    private int tipRadius;
    private int tooltipColor;

    Tooltip tooltip;

    private void showPlus_tooltip(@NonNull View anchor) {
        ViewGroup content = (ViewGroup) getLayoutInflater().inflate(R.layout.plus_icon_tooltip_layout, null);

        TextView tv_title = (TextView) content.findViewById(R.id.tv_title);
        tv_title.setText(Html.fromHtml(getResources().getString(R.string.bg_color_tooltip_text)));

        tipSizeSmall = getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipSizeRegular = getResources().getDimensionPixelSize(R.dimen.tip_dimen_regular);
        tipRadius = getResources().getDimensionPixelOffset(R.dimen.tip_radius);
        tooltipColor = ContextCompat.getColor(this, R.color.background_color);
        try {
            tooltip = new Tooltip.Builder(this)
                    .anchor(anchor, (orientation == Configuration.ORIENTATION_PORTRAIT ? Tooltip.RIGHT : Tooltip.LEFT))
                    .animate(new TooltipAnimation(TooltipAnimation.REVEAL, 300))
                    .autoAdjust(true)
                    .cancelable(false)
                    .content(content)
                    .withTip(new Tooltip.Tip(tipSizeRegular, tipSizeRegular, tooltipColor))
                    .withPadding(getResources().getDimensionPixelOffset(R.dimen.menu_tooltip_padding))
                    .into(viewContainer)
                    .autoCancel(4000)
                    .show();

        } catch (Exception e) {
            Log.e("TAGGG", "Exception " + e.getMessage());
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    void setImageWhilePickFromResult() {
        Log.e("TAG", "setImageWhilePickFromResult called");
        resetCanvas();
        if (!constants.getString("isfromTrace", PaintActivity.this).isEmpty()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            String filePath = constants.getString("path", PaintActivity.this);
            Bitmap bitmap = getBitmap(filePath, BitmapFactory.decodeFile(filePath, bmOptions));
            setupBitmap(bitmap);
            iv_selected_image.setVisibility(View.VISIBLE);
            isFromEditImage = true;
            alphaValue = 5.0;
            seekbar_1.setProgress(127);
            iv_selected_image.setAlpha(0.5f);

            Current_Mode = "Edit Paint";
            mNewCanvasBtn.setImageResource(R.drawable.trace_icon_white_canvas);
            mNewCanvasBtn.setTag(2);
            CurrentMode = 2;
            current_mode = canvas_mode.canvas_back;
            Log.e("TAGGG", "canvas_mode Current 222> " + current_mode);
            if (iv_start_recoring.getVisibility() != View.VISIBLE) {
                iv_start_recoring.setVisibility(View.VISIBLE);
            }

            if (tv_recording_time.getVisibility() != View.VISIBLE) {
                tv_recording_time.setVisibility(View.VISIBLE);
            }

            hideShowSeekbarView(false);
        } else if (!constants.getString("isfromoverlay", PaintActivity.this).isEmpty()) {

            hideShowSeekbarView(true);
            if (constants.getString("type", PaintActivity.this).equalsIgnoreCase("camera")) {
                Current_Mode = "LoadWithoutTraceFromCamera";
                mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
                mNewCanvasBtn.setTag(3);
                CurrentMode = 3;
                hideShowCross(false);
            } else {
                Current_Mode = "LoadWithoutTrace";
                mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
                mNewCanvasBtn.setTag(1);
                CurrentMode = 1;
                hideShowCross(false);
            }
            isFromEditImage = false;
            FromTutorialMode = false;
            seekbar_1.setVisibility(View.GONE);

            selectedImagePath = constants.getString("path", PaintActivity.this);
            String parentFolder = constants.getString("ParentFolderPath", PaintActivity.this);
            mPainting.setBackgroundBitmap(null);
            mPainting.clearPainting();
            System.gc();
            PaintingType = "save_overlay_canvas_drawing";
//            mImageManager.loadPaintingFromFile(mPainting, selectedImagePath, mScreenWidth, mScreenHeight, parentFolder);

            loadPaintingFromFile(parentFolder + "/" + selectedImagePath);
            if (mPainting != null) {
                mPainting.syncComposeCanvas();
                mPainting.syncUndoCanvas();
            }
            mPrefBackgroundColor = -1;
            mPainting.setBackgroundColor(mPrefBackgroundColor);
            savePaintingPreference();

            mPaintView.reDraw(null);
            mPaintView.invalidate();

            //if (lIntent.hasExtra("isPickFromOverlaid")) {
            this.isPickFromOverlaid = isPickFromOverlaid;
            //}

        }
    }


    public Bitmap getBitmap(String photoPath, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        Log.e("TAG", "getBitmap photoPath " + photoPath);
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (Exception e) {
            Log.e("TAG", "getBitmap Exception " + e.getMessage());
        }
        return rotatedBitmap;
    }


    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    RelativeLayout rl_canvas;


    void hideShowSeekbarView(boolean _hide) {
        Log.e("TAG", "hideShowSeekbarView " + _hide);
//        view2.setVisibility((_hide ? View.GONE : View.VISIBLE));
//        view1.setVisibility((_hide ? View.GONE : View.VISIBLE));
        view_mid.setVisibility((_hide ? View.GONE : View.VISIBLE));
        seekBarContainer4.setVisibility((_hide ? View.GONE : View.VISIBLE));
        if (trace_bar_container != null) {
            trace_bar_container.setVisibility((_hide ? View.GONE : View.VISIBLE));
        }
    }


    Bitmap _coloredBitmapFortrace;
    Bitmap _grayScaleForTrace;

    void setupBitmap(Bitmap bitmap) {
        try {
            Log.e("TAG", "Bitmap Logs setupBitmap called");
            double canvas_width = mScreenWidth;
            double canvas_height = mScreenHeight;
            double canvas_aspect_ratio = (canvas_width / canvas_height);
            double image_width = bitmap.getWidth();
            double image_height = bitmap.getHeight();
            double image_aspect_ratio = (image_width / image_height);
            double new_width = 0;
            double new_height = 0;
            if (image_aspect_ratio >= canvas_aspect_ratio) {
                new_width = canvas_width;
                new_height = (int) (canvas_width / image_aspect_ratio);
            } else {
                new_height = canvas_height;
                new_width = (canvas_height * image_aspect_ratio);
            }
           /* Log.e("TAG", "Calculated logs canvas_width " + canvas_width + " canvas_height " + canvas_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " rl_canvas " + rl_canvas.getWidth() + " " + rl_canvas.getHeight());
            Log.e("TAG", "Calculated logs image_width " + image_width + " image_height " + image_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " image_aspect_ratio " + image_aspect_ratio);
            Log.e("TAG", "Calculated logs final new_width " + new_width + " new_height " + new_height);
*/
            Bitmap b1 = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);
//            if (switch_gray_scale.isChecked() && (CurrentMode == 3 || CurrentMode == 2)) {
            _coloredBitmapFortrace = b1;
            _grayScaleForTrace = toGrayscale(b1);
            if (switch_gray_scale.isChecked()) {
                iv_selected_image.setImageBitmap(_grayScaleForTrace);
            } else {
                iv_selected_image.setImageBitmap(b1);
            }
            Params = new LinearLayout.LayoutParams((int) (new_width), (int) new_height);
            rl_canvas.setLayoutParams(Params);
            mPaintView.invalidate();
            createPainting((int) (new_width), (int) new_height);
            Log.e("TAG", "Trace Logs Overlaid  new W_H " + b1.getWidth() + "*" + b1.getHeight() + " Current_Mode " + Current_Mode + " current_mode " + current_mode + " CurrentMode " + CurrentMode);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupBitmap " + e.getMessage());
        }
    }


    /*  void loadPaintingFromFile(String path) {
          Log.e("TAG", "Bitmap Logs loadPaintingFromFile called");
          BitmapFactory.Options bmOptions = new BitmapFactory.Options();
          Bitmap bitmap = getBitmap(path, BitmapFactory.decodeFile(path, bmOptions));

          try {
              double canvas_width = mScreenWidth;
              double canvas_height = mScreenHeight;
              double canvas_aspect_ratio = (canvas_width / canvas_height);

              double image_width = bitmap.getWidth();
              double image_height = bitmap.getHeight();

              double image_aspect_ratio = (image_width / image_height);
              double new_width = 0;
              double new_height = 0;
              if (image_aspect_ratio >= canvas_aspect_ratio) {
                  new_width = canvas_width;
                  new_height = (int) (canvas_width / image_aspect_ratio);
              } else {
                  new_height = canvas_height;
                  new_width = (canvas_height * image_aspect_ratio);
              }
              Bitmap b1 = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);

  //            if (switch_gray_scale.isChecked() && (CurrentMode == 3 || CurrentMode == 1)) {
              if (switch_gray_scale.isChecked()) {
                  Bitmap _grayScale = toGrayscale(b1);
                  mPainting.setBitmap(_grayScale);
              } else {
                  mPainting.setBitmap(b1);
              }

              Params = new LinearLayout.LayoutParams((int) new_width, (int) new_height);
              rl_canvas.setLayoutParams(Params);
              Log.e("TAG", "Overlaid Logs Overlaid  new W_H " + b1.getWidth() + "*" + b1.getHeight() + " Current_Mode " + Current_Mode + " current_mode " + current_mode + " CurrentMode " + CurrentMode);

              try {
                  if (bitmap != null && !bitmap.isRecycled()) {
                      bitmap.recycle();
                      bitmap = null;
                  }
                  if (b1 != null && !b1.isRecycled()) {
                      b1.recycle();
                      b1 = null;
                  }

              } catch (Exception e) {
              }
              System.gc();
              mPaintView.invalidate();

              reloadBitmap(path);
          } catch (OutOfMemoryError error) {
              quitByOutOfMemory();
          } catch (Exception e) {
              Log.e("TAG", "Exception at set Params " + e.getMessage(), e);
          }
      }*/
    void loadPaintingFromFile(String path) {
        Log.e("TAG", "Bitmap Logs loadPaintingFromFile called");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = getBitmap(path, BitmapFactory.decodeFile(path, bmOptions));

        if (bitmap == null) {
            Log.e("TAG", "Bitmap is null. Could not decode file at path: " + path);
            return;
        }

        try {
            double canvas_width = mScreenWidth;
            double canvas_height = mScreenHeight;
            double canvas_aspect_ratio = (canvas_width / canvas_height);

            double image_width = bitmap.getWidth();
            double image_height = bitmap.getHeight();

            double image_aspect_ratio = (image_width / image_height);
            double new_width = 0;
            double new_height = 0;
            if (image_aspect_ratio >= canvas_aspect_ratio) {
                new_width = canvas_width;
                new_height = (int) (canvas_width / image_aspect_ratio);
            } else {
                new_height = canvas_height;
                new_width = (canvas_height * image_aspect_ratio);
            }
            Bitmap b1 = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);

            // if (switch_gray_scale.isChecked() && (CurrentMode == 3 || CurrentMode == 1)) {
            if (switch_gray_scale.isChecked()) {
                Bitmap _grayScale = toGrayscale(b1);
                mPainting.setBitmap(_grayScale);
            } else {
                mPainting.setBitmap(b1);
            }

            Params = new LinearLayout.LayoutParams((int) new_width, (int) new_height);
            rl_canvas.setLayoutParams(Params);
            Log.e("TAG", "Overlaid Logs Overlaid  new W_H " + b1.getWidth() + "*" + b1.getHeight() + " Current_Mode " + Current_Mode + " current_mode " + current_mode + " CurrentMode " + CurrentMode);

            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
                if (b1 != null && !b1.isRecycled()) {
                    b1.recycle();
                    b1 = null;
                }

            } catch (Exception e) {
                Log.e("TAG", "Exception during bitmap recycling: " + e.getMessage(), e);
            }
            System.gc();
            mPaintView.invalidate();

            reloadBitmap(path);
        } catch (OutOfMemoryError error) {
            quitByOutOfMemory();
        } catch (Exception e) {
            Log.e("TAG", "Exception at set Params " + e.getMessage(), e);
        }
    }


    void reloadBitmap(String drawingPath) {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");

        Log.e("TAG", "storeInTraceList json " + json);
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();
        ArrayList<TraceReference> traceList = gson.fromJson(json, type);

        Bitmap bitmap = null;
        try {
            if (traceList != null)
                for (int i = 0; i < traceList.size(); i++) {
                    if (traceList.get(i).get_drawing_type() == drawing_type.Normal && drawingPath.equalsIgnoreCase(traceList.get(i).getUserPaintingName())) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Log.e("TAG", "Image Path at Trace getBitmap Found " + traceList.get(i).getTraceImageName());
                        bitmap = getBitmap(traceList.get(i).getTraceImageName(), BitmapFactory.decodeFile(traceList.get(i).getTraceImageName(), bmOptions));
                        break;
                    }
                }
        } catch (Exception e) {
            Log.e("TAG", "Exception at reloadBitmap " + e.getMessage());
        }

        try {
            if (bitmap == null) {
                return;
            }
            Log.e("TAG", "Bitmap Logs reloadBitmap called");
            double canvas_width = mScreenWidth;
            double canvas_height = mScreenHeight;
            double canvas_aspect_ratio = (canvas_width / canvas_height);
            double image_width = bitmap.getWidth();
            double image_height = bitmap.getHeight();
            double image_aspect_ratio = (image_width / image_height);
            double new_width = 0;
            double new_height = 0;
            if (image_aspect_ratio >= canvas_aspect_ratio) {
                new_width = canvas_width;
                new_height = (int) (canvas_width / image_aspect_ratio);
            } else {
                new_height = canvas_height;
                new_width = (canvas_height * image_aspect_ratio);
            }
           /* Log.e("TAG", "Calculated logs canvas_width " + canvas_width + " canvas_height " + canvas_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " rl_canvas " + rl_canvas.getWidth() + " " + rl_canvas.getHeight());
            Log.e("TAG", "Calculated logs image_width " + image_width + " image_height " + image_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " image_aspect_ratio " + image_aspect_ratio);
            Log.e("TAG", "Calculated logs final new_width " + new_width + " new_height " + new_height);*/
            Params = new LinearLayout.LayoutParams((int) (new_width), (int) new_height);
            rl_canvas.setLayoutParams(Params);

            mPaintView.invalidate();
        } catch (Exception e) {
            Log.e("TAG", "Exception at reloadBitmap " + e.getMessage());
        }
    }

    public void showDialogForCamera() {

        if (BuildConfig.DEBUG) {
            Toast.makeText(PaintActivity.this, "showDialogForCamera", Toast.LENGTH_SHORT).show();
            KGlobal.appendLog(this, "showDialogForCamera");
        }

        final Dialog dialog = new Dialog(PaintActivity.this);
        dialog.setContentView(R.layout.camera_image_op_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        TextView tv_image_path = dialog.findViewById(R.id.tv_image_path);
        LinearLayout tv_use_overlay = dialog.findViewById(R.id.ll_overlay);
        LinearLayout tv_use_trace = dialog.findViewById(R.id.ll_trace);

//        File _file = new File(_path);
//        tv_image_path.setText(_path);

        tv_use_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  opneCameraPicInOverlay(_file);
                dialog.dismiss();
                boolean isStoragePassed = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // android 13 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // android 11 and above
                    if (ContextCompat.checkSelfPermission(
                            Paintor.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED
                    ) {
                        isStoragePassed = true;
                    }
                } else if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
                    // We don't have permission so prompt the user
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }

                if (!isStoragePassed) {
                    PermissionUtils.requestStoragePermission(Paintor.this, 1);
                    return;
                }*/

                CAMERA_OPERATION = 1;
                addCamera();
                dialog.dismiss();
            }
        });
        tv_use_trace.setOnClickListener(view -> {
            /*openCameraPicInTraceMode(_file);
            dialog.dismiss();
           boolean isStoragePassed = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                if (ContextCompat.checkSelfPermission(
                        Paintor.this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // android 11 and above
                if (ContextCompat.checkSelfPermission(
                        Paintor.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePassed = true;
                }
            } else if (!PermissionUtils.checkStoragePermission(Paintor.this)) {
                // We don't have permission so prompt the user
                PermissionUtils.requestStoragePermission(Paintor.this, 1);
                return;
            }

            if (!isStoragePassed) {
                PermissionUtils.requestStoragePermission(Paintor.this, 1);
                return;
            }*/

            CAMERA_OPERATION = 2;
            addCamera();
            dialog.dismiss();
        });
        dialog.show();
    }

    void opneCameraPicInOverlay(File _file) {
        try {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_camere_overlay, Toast.LENGTH_SHORT).show();
                KGlobal.appendLog(this, constants.canvas_switch_mode_camere_overlay);
            }


            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_camere_overlay);
            view_cross.setVisibility(View.GONE);
            mPaintView.resetCanvas();
            Current_Mode = "LoadWithoutTraceFromCamera";
            mNewCanvasBtn.setImageResource(R.drawable.overlay_image_white_canvas);
            mNewCanvasBtn.setTag(3);
            CurrentMode = 3;
            hideShowCross(false);


//            if (iv_start_recoring.getVisibility() != View.VISIBLE) {
//                iv_start_recoring.setVisibility(View.VISIBLE);
//            }
//            if (tv_recording_time.getVisibility() != View.VISIBLE) {
//                tv_recording_time.setVisibility(View.VISIBLE);
//            }


            switch_singleTap.setChecked(false);
            _switch_line.setChecked(false);
            defaultSwitchLineStatus = false;
            defaultSwitchSingleTapStatus = false;


            resetCanvas();

            captureImageSet(_file.getName(), _file.getParentFile().getAbsolutePath(), true);

        } catch (Exception e) {
            Log.e("TAG", "Exception at paintor " + e.getMessage());
            if (BuildConfig.DEBUG) {
                KGlobal.appendLog(this, "opneCameraPicInOverlay Ex: " + e.getMessage());
            }
        }
    }

    void openCameraPicInTraceMode(File _file) {
        try {
            if (BuildConfig.DEBUG) {
                Toast.makeText(PaintActivity.this, constants.canvas_switch_mode_camera_trace, Toast.LENGTH_SHORT).show();
                KGlobal.appendLog(this, constants.canvas_switch_mode_camera_trace);
            }
            FirebaseUtils.logEvents(PaintActivity.this, constants.canvas_switch_mode_camera_trace);
            mStrBackground = _file.getAbsolutePath();

            if (mPainting.mPaintingWidth != 0) {
                if (_file.getAbsolutePath() == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaintActivity.this);
                    builder.setTitle("Can't Load");
                    builder.setMessage("Selected image is not on your local storage, please download first to your local storage and then try.");
                    builder.setNegativeButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (_file.getAbsolutePath() != null) {
                    resetCanvas();
                    restartTimer();
//                            iv_selected_image.setImageURI(imageUri);
                    backgroundImagePath = _file.getAbsolutePath();
                    selectedImagePath = _file.getAbsolutePath();
                    if (iv_selected_image.getVisibility() != View.VISIBLE) {
                        iv_selected_image.setVisibility(View.VISIBLE);
                    }
                    mPainting.clearPainting();
                    mPaintView.reDraw(null);
                    deleteRecovery();
                    if (ll_toggle.getVisibility() != View.VISIBLE) {
                        ll_toggle.setVisibility(View.VISIBLE);
                        seekbar_1.setVisibility(View.VISIBLE);
                        seekBarContainer4.setVisibility(View.VISIBLE);
                        hideShowSeekbarView(false);
                    }
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = getBitmap(selectedImagePath, BitmapFactory.decodeFile(selectedImagePath, bmOptions));
                    setupBitmap(bitmap);
                    isFromEditImage = true;
                    alphaValue = 5.0;
                    seekbar_1.setProgress(127);
                    iv_selected_image.setAlpha(0.5f);
                    iv_selected_image.invalidate();
                    switch_singleTap.setChecked(false);
                    _switch_line.setChecked(false);
                    defaultSwitchLineStatus = false;
                    defaultSwitchSingleTapStatus = false;
                    Current_Mode = "Edit Paint";
                    mNewCanvasBtn.setImageResource(R.drawable.trace_icon_white_canvas);
                    mNewCanvasBtn.setTag(2);
                    CurrentMode = 2;
                    current_mode = canvas_mode.canvas_back;
                    Log.e("TAGGG", "canvas_mode Current 222> " + current_mode);
                    if (iv_start_recoring.getVisibility() != View.VISIBLE) {
                        iv_start_recoring.setVisibility(View.VISIBLE);
                    }

                    if (tv_recording_time.getVisibility() != View.VISIBLE) {
                        tv_recording_time.setVisibility(View.VISIBLE);
                    }

                    mPaintingTemp.setBackgroundBitmap(null);
                    mPaintingTemp.clearPainting();

                    if (mPaintingTemp != null) {
                        mPaintingTemp.syncComposeCanvas();
                        mPaintingTemp.syncUndoCanvas();
                    }
                    mPaintViewTemp.reDraw(null);

                    mPaintingTemp.setBackgroundColor(R.color.white);
                    mPaintViewTemp.setVisibility(View.GONE);

                    view_cross.setVisibility(View.GONE);
                    mPaintView.resetCanvas();
                    constants.putString("pickfromresult", "yes", PaintActivity.this);
                    constants.putString("isfromTrace", "yes", PaintActivity.this);
                    constants.putString("isfromoverlay", "", PaintActivity.this);
                    constants.putString("path", selectedImagePath, PaintActivity.this);
                    constants.putString("type", "", PaintActivity.this);
                    constants.putString("action_name", "", PaintActivity.this);
                    // new code
                    int orientation = this.getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    if (BuildConfig.DEBUG) {
                        KGlobal.appendLog(this, "opneCameraPicInTraceMode completing");
                    }
                    if (ll_bottom_bar.getVisibility() == View.VISIBLE)
                        ll_bottom_bar.setVisibility(View.GONE);
                    if (iv_switch_to_player != null)
                        iv_switch_to_player.setVisibility(View.GONE);
                }
            }
        } catch (OutOfMemoryError lOutOfMemoryError) {
            if (BuildConfig.DEBUG) {
                KGlobal.appendLog(this, "opneCameraPicInTraceMode Ex: " + lOutOfMemoryError.getMessage());
            }
            quitByOutOfMemory();
        } catch (NullPointerException lNullPointerException) {
            Log.e("TAGGG", "lNullPointerException " + lNullPointerException.getMessage());
            if (BuildConfig.DEBUG) {
                KGlobal.appendLog(this, "opneCameraPicInTraceMode Ex: " + lNullPointerException.getMessage());
            }
        }
    }

    axisAnDColor getAxisFromColor(int colorCode) {
        try {
            Log.e("TAG", "Color Code For Gray Scale " + colorCode);
           /* for (int i = 0; i < _lst_colors_gray_scale.size(); i++) {
                Log.e("TAG", "Color Code From List " + _lst_colors_gray_scale.get(i).getColorCode() + " X " + _lst_colors_gray_scale.get(i).getX() + " Y " + _lst_colors_gray_scale.get(i).getY());
            }*/

            for (int i = 0; i < _lst_colors_gray_scale.size(); i++) {
                if (colorCode == _lst_colors_gray_scale.get(i).getColorCode()) {
                    Log.e("TAG", "Color Code Match x " + _lst_colors_gray_scale.get(i).getX() + " " + _lst_colors_gray_scale.get(i).getY() + " " + _lst_colors_gray_scale.get(i).getColorCode());
                    return _lst_colors_gray_scale.get(i);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at getAxisFromColor " + e.getMessage());
        }
//        int myNumber = 490;
        int distance = Math.abs(_lst_colors_gray_scale.get(0).getColorCode() - colorCode);
        int idx = 0;
        for (int c = 1; c < _lst_colors_gray_scale.size(); c++) {
            int cdistance = Math.abs(_lst_colors_gray_scale.get(c).getColorCode() - colorCode);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        int theNumber = _lst_colors_gray_scale.get(idx).getColorCode();
        Log.e("TAG", "Closest Number Required " + colorCode + " theNumber " + theNumber);

//        Toast.makeText(this, "Set From Closest", Toast.LENGTH_SHORT).show();
        return _lst_colors_gray_scale.get(idx);
    }


    public class axisAnDColor {
        public int colorCode, X, Y;

        public axisAnDColor(int colorCode, int x, int y) {
            this.colorCode = colorCode;
            X = x;
            Y = y;
        }

        public int getColorCode() {
            return colorCode;
        }

        public void setColorCode(int colorCode) {
            this.colorCode = colorCode;
        }

        public int getX() {
            return X;
        }

        public void setX(int x) {
            X = x;
        }

        public int getY() {
            return Y;
        }

        public void setY(int y) {
            Y = y;
        }
    }


    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        try {
            int width, height;
            height = bmpOriginal.getHeight();
            width = bmpOriginal.getWidth();

            Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmpGrayscale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bmpOriginal, 0, 0, paint);
            return bmpGrayscale;
        } catch (Exception e) {
            Log.e("TAG", "Exception at convertGray Scale " + e.getMessage() + " " + e.toString());
        }
        return null;
    }

    public interface OnProcessedListener {
        public void onProcessed(SavedTutorialEntity result);
    }

    @Override
    public void cancelBrushDialogListener() {
        // reset gray scale
        if (defaultSwitchGrayScaleStatus != switch_gray_scale.isChecked()) {
            switch_gray_scale.setChecked(defaultSwitchGrayScaleStatus);
            //  SharedPreferences sharedPref = getPreferences(0);
            SharedPreferences sharedPref = getSharedPreferences("brush", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            // reset gray scale
            editor.putBoolean("gray_scale", switch_gray_scale.isChecked());
            editor.apply();

            if (!switch_gray_scale.isChecked()) {
                mPrefBrushColor = mPreviousPrefBrushColor;

                Integer newColor = mPrefBrushColor; //getColor();
                setColorInBox(newColor);
                mBrushColor = newColor;
                setHSVColor(newColor);
                mPainting.setBrushColor(getColor());
            }
        }
        // end reset gray scale

        // reset brush
        mPrefBrushSize = oldDefaultSizeBarProgress;
        mPrefAlpha = oldDefaultDensityBarAlpha;
        mPrefFlow = oldDefaultHardnessBarFlow;

        savePaintingPreference();

    }

    @Override
    public void brushSetting() {
        // restorePaintingPreference();
    }

//    protected void takeScreenCapturePermission() {
//        final MediaProjectionManager mediaProjectionManager;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            mediaProjectionManager = getSystemService(MediaProjectionManager.class);
//            ActivityResultLauncher<Intent> startMediaProjection = registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    result -> {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            AppUtils.setDataFromResultSS((Intent) result.getData().clone());
//                            screenCapturePermission = true;
//                        } else {
//                            screenCapturePermission = false;
//                        }
//                    }
//            );
//            startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent());
//        } else {
//            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_RECORD_REQUEST_CODE);
//        }
//    }

    protected void takeScreenCapturePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mediaProjectionManager = getSystemService(MediaProjectionManager.class);
            AppUtils.setMediaProjectionManager(mediaProjectionManager);
            startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent());
        } else {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            AppUtils.setMediaProjectionManager(mediaProjectionManager);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_RECORD_REQUEST_CODE);
        }
    }

    //intor=====================
    public static String processSteps = "";
    private boolean isPbnColorOneDrawn = false;
    int userDrawCount = 0;

    void drawing_activities() {
        userDrawCount++;
        Log.d("SwatchColor", "processSteps: " + processSteps);
        if (step == 0 && processSteps.equals("Brush_clicked")) {
            introView.setVisibility(View.VISIBLE);
            btn_Next_v.setVisibility(View.VISIBLE);
            button_previous_v.setVisibility(View.VISIBLE);
            introText.setVisibility(View.INVISIBLE);
            introText2.setVisibility(View.VISIBLE);
            introText2.setClickable(false);
            introText2.setText("Success!");
        } else if (step == 1 && processSteps.equals("ColorBar_clicked")) {
            introView.setVisibility(View.VISIBLE);
            btn_Next_v.setVisibility(View.VISIBLE);
            button_previous_v.setVisibility(View.VISIBLE);
            introText.setVisibility(View.INVISIBLE);
            introText2.setVisibility(View.VISIBLE);
            introText2.setClickable(false);
            introText2.setText("Success!");
        } else if (step == 2 && processSteps.equals("Color_chosen_and_drawn")) {
            introView.setVisibility(View.VISIBLE);
            btn_Next_v.setVisibility(View.VISIBLE);
            button_previous_v.setVisibility(View.VISIBLE);
            introText.setVisibility(View.INVISIBLE);
            introText2.setVisibility(View.VISIBLE);
            introText2.setClickable(false);
            introText2.setText("Success!");
        } else if (step == 3) {
            introView.setVisibility(View.VISIBLE);
            button_previous_v.setVisibility(View.VISIBLE);
            introText.setVisibility(View.INVISIBLE);
            introText2.setVisibility(View.VISIBLE);
            introText2.setClickable(false);
            if (isPbnColorOneDrawn && processSteps.equals("Pbn_invalid_color_selected")) {
                introText2.setText("Try again");
            } else if (processSteps.equals("Pbn_color_two_chosen_and_drawn")) {
                introText2.setText("Success!");
                btn_Next_v.setVisibility(View.VISIBLE);
                introText2.setVisibility(View.VISIBLE);
            } else {
                btn_Next_v.setVisibility(View.INVISIBLE);
                introText2.setVisibility(View.GONE);
            }
        } else {
            btn_Next_v.setVisibility(View.INVISIBLE);
            button_previous_v.setVisibility(View.VISIBLE);
            introText.setVisibility(View.INVISIBLE);
            introText2.setVisibility(View.VISIBLE);
            introText2.setClickable(true);
            introText2.setText("Try agan!");
        }

        Log.e("intro=", "S=" + processSteps + "=" + step);
    }

    RelativeLayout introView;
    ImageButton button_previous_v;
    RelativeLayout btn_Next_v;
    TextView introText, introText2;
    VideoView videoView;

    public void videoIntro() {


        introView = findViewById(R.id.intro_view);
        introText = findViewById(R.id.intro_text);
        introText2 = findViewById(R.id.intro_txt2);
        introText2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < userDrawCount; i++) {
                    try {
                        undo();
                    } catch (Exception e) {
                    }

                }
            }
        });
        button_previous_v = findViewById(R.id.intro_previous_v);
        button_previous_v.setOnClickListener(v -> {
            Intent intent = new Intent(PaintActivity.this, IntroActivity.class);
            intent.putExtra("step", step);
            startActivity(intent);
            finish();
        });
        btn_Next_v = findViewById(R.id.intro_next_v);
        btn_Next_v.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                if (step == 0) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, "" + 1, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, Events.EVENT_VID1);
                } else if (step == 1) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, "" + 2, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, Events.EVENT_VID2);
                } else if (step == 2) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, "" + 3, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, Events.EVENT_VID3);
                } else if (step == 3) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(this, "" + 4, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(this, Events.EVENT_VID4);
                }
            }
            if (step < 3) {
                Intent intent = new Intent(PaintActivity.this, IntroActivity.class);
                intent.putExtra("step", step + 1);
                startActivity(intent);
                finish();
            } else {
                if (sharedPref != null) {
                    boolean shouldCloseScreen = painterDbPref.getBoolean("shouldShowIntro", false);
                    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                    if (auth == null || !shouldCloseScreen) {
                        showRewardPointDialog();
                    } else {
                        finish();
                    }
                    painterDbEditor.putBoolean("shouldShowIntro", true);
                } else {
                    finish();
                }
            }
        });
        introView.setVisibility(View.VISIBLE);
        introText.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> introText.setVisibility(View.GONE), 3000);
    }

    private void showRewardPointDialog() {
        final Dialog exitDialog = new Dialog(this, R.style.my_dialog);
        final DialogRewardBinding dialogBinding = DialogRewardBinding.inflate(getLayoutInflater());
        exitDialog.setContentView(dialogBinding.getRoot());

        String title = "Reward Points!";
        String content = "Congratulations, you have earned 50 points!";
        dialogBinding.tvDialogTitle.setText(title);
        dialogBinding.tvDialogContent.setText(content);
        dialogBinding.btnDoAward.setText(Html.fromHtml("<b>Continue</b>"));

        dialogBinding.tvDialogContent.setTextSize(14);
        dialogBinding.btnDoAward.setTextSize(14);

        dialogBinding.btnDoAward.setBackgroundResource(0);
        dialogBinding.btnDoAward.setTextColor(Color.parseColor("#4C6FE7"));

        dialogBinding.btnDoAward.setOnClickListener(v -> {
            if (exitDialog.isShowing()) {
                FirebaseUtils.logEvents(this, Events.EVENT_BONUS);

                new DIComponent().getSharedPreferenceUtils().setFourStepCompleted(true);

                painterDbEditor.putBoolean("isFirstTimeLogin", false).apply();
                painterDbEditor.putBoolean("shouldGiveReward", true).apply();
                exitDialog.dismiss();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("fromPaintor", false);
                intent.putExtra("startDashboardScreen", true);
                startActivity(intent);
                finish();
            }
        });

        exitDialog.show();
    }

}
