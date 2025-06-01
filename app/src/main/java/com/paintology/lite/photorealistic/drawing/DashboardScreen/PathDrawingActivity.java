package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CameraPreview.SandriosCamera;
import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.CameraConfiguration;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.CameraOutputModel;
import com.paintology.lite.photorealistic.drawing.DrawingApp.ColorPicker.ColorPickerDialog;
import com.paintology.lite.photorealistic.drawing.DrawingApp.DrawingView;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.VerticalSeekBarWrapper;
import com.paintology.lite.photorealistic.drawing.util.interface_drawing_view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PathDrawingActivity extends AppCompatActivity implements View.OnClickListener, interface_drawing_view {

    PathDrawingView mDrawingView;
    LinearLayout ll_drawing_container;

    SeekBar seekbar;
    TextView tv_size;

    TextView tv_save_pattern;
    ImageView iv_undo, iv_redo;
    public static interface_drawing_view _obj_interface;

    ImageView iv_minus_brush_size, iv_plus_brush_size, iv_gray_scale;
    RelativeLayout ll_shape;
    TextView tv_shapes;
    TextView tv_freehand, tv_circle, tv_square, tv_rect, tv_triangle, tv_line;
    StringConstants _constant = new StringConstants();
    TextView tv_pen_brush, tv_color, tv_shade_tool, tv_image_trace, tv_image_camera;
    int currentColor = Color.RED;
    public int SELECT_PHOTO_REQUEST = 100;
    public int SELECT_Camera_REQUEST = 101;
    public int PERMISSION_PHOTO = 102;
    public static ImageView _iv_trace_image;


    RelativeLayout rl_container;
    int CANVAS_WIDTH = 0;
    int CANVAS_HEIGHT = 0;

    VerticalSeekBarWrapper seekBarContainer4;
    View view1, view2, view_mid;
    ImageView view_gray_scale_indicator;
    FrameLayout fm_tracebar;
    int startingProgress = 0;
    SeekBar seekbar_1;
    Double alphaValue = 0.0;
    boolean isBGsetup = false;

    StringConstants constants = new StringConstants();
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";


    View view_gray_scale;


    public enum DrawingMode {
        Blank, Trace, Camera, Exit
    }

    RelativeLayout rl_gray_scale;
    FrameLayout fm_main_container;
    Bitmap selected_bitmap;

    int topLimit = 5;
    int bottomlimit = 0;
    int orientation;


    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;

    String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_drawing);
        mDrawingView = new PathDrawingView(this);
        mDrawingView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ll_drawing_container = findViewById(R.id.ll_drawing_container);
        ll_drawing_container.addView(mDrawingView);
        Log.e("TAG", "OnCreate Logs " + ll_drawing_container.getWidth() + " " + ll_drawing_container.getHeight());
        rl_container = findViewById(R.id.rl_container);
        initialize();
        Log.e("TAG", "OnCreate Size Logs Seekbar " + seekbar.getProgress() + " Text " + tv_size.getText().toString());


        rl_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("TAG", "OnTouch Of Container called");


                return true;
            }
        });
    }

    void initialize() {
        _obj_interface = this;
        _iv_trace_image = findViewById(R.id.iv_trace_image);
        tv_shapes = findViewById(R.id.tv_shapes);
        ll_shape = findViewById(R.id.ll_shape);

        seekbar = findViewById(R.id.seekbar);
        tv_size = findViewById(R.id.tv_brush_size);
        tv_size.setText("10");
        seekbar.setMax(100);

        seekbar.setProgress(10);
        mDrawingView.setBrushSize(constants.getInt("size", PathDrawingActivity.this) == 0 ? 10 : constants.getInt("size", PathDrawingActivity.this));
        fm_main_container = findViewById(R.id.fm_main_container);
        view_gray_scale = findViewById(R.id.view_gray_scale);
        view_gray_scale.setOnClickListener(this::onClick);

        rl_gray_scale = findViewById(R.id.rl_gray_scale);
        rl_gray_scale.setOnClickListener(this::onClick);

        tv_image_camera = findViewById(R.id.tv_image_camera);
        tv_image_camera.setOnClickListener(this::onClick);
        fm_tracebar = findViewById(R.id.fm_tracebar);
        view1 = (View) findViewById(R.id.view_1);
        view2 = (View) findViewById(R.id.view_2);
        view_mid = (View) findViewById(R.id.view_mid);
        view_gray_scale_indicator = (ImageView) findViewById(R.id.view_gray_scale_indicator);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() >= 1)
                    tv_size.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() >= 1) {
                    constants.putInt("size", seekBar.getProgress(), PathDrawingActivity.this);
                    mDrawingView.setBrushSize(seekBar.getProgress());
                } else if (seekBar.getProgress() == 0) {
                    constants.putInt("size", 1, PathDrawingActivity.this);
                    seekbar.setProgress(1);
                    mDrawingView.setBrushSize(1);
                }
            }

        });

        seekbar_1 = findViewById(R.id.seekbar_1);
        seekBarContainer4 = findViewById(R.id.seekBarContainer4);
        tv_save_pattern = findViewById(R.id.tv_save_pattern);
        tv_save_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap emptyBitmap = Bitmap.createBitmap(mDrawingView.getWidth(), mDrawingView.getHeight(), Bitmap.Config.ARGB_8888);
                    if (mDrawingView.getBitmap().sameAs(emptyBitmap)) {
                        Toast.makeText(PathDrawingActivity.this, "canvas empty!", Toast.LENGTH_SHORT).show();
                    } else {
//                        showDialog();
//                        int permission = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        if (permission != PackageManager.PERMISSION_GRANTED) {
//                            // We don't have permission so prompt the user
//                            ActivityCompat.requestPermissions(
//                                    PathDrawingActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                    SELECT_PHOTO_REQUEST
//                            );
//                            return;
//                        }
                        if (!PermissionUtils.checkStoragePermission(PathDrawingActivity.this)) {
                            // We don't have permission so prompt the user
                            PermissionUtils.requestStoragePermission(PathDrawingActivity.this, SELECT_PHOTO_REQUEST);
                            return;
                        }
                        showDialog(DrawingMode.Blank);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at save " + e.getMessage());
                }
            }
        });

        tv_pen_brush = findViewById(R.id.tv_pen_brush);
        tv_pen_brush.setTextColor(getResources().getColor(R.color.yellow));
        tv_color = findViewById(R.id.tv_color);
        tv_shade_tool = findViewById(R.id.tv_shade);
        tv_image_trace = findViewById(R.id.tv_image_trace);

        tv_color.setTextColor(currentColor);
        tv_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(PathDrawingActivity.this, currentColor,
                        new ColorPickerDialog.OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int color) {
                                currentColor = color;
                                tv_color.setTextColor(currentColor);
                                mDrawingView.setBrushColor(color);
                            }
                        });
                colorPickerDialog.show();
            }
        });
        iv_undo = findViewById(R.id.iv_undo);
        iv_redo = findViewById(R.id.iv_redo);
        iv_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.undo();
            }
        });
        iv_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.redo();
            }
        });

        iv_minus_brush_size = (ImageView) findViewById(R.id.iv_minus_brush_size);
        iv_plus_brush_size = (ImageView) findViewById(R.id.iv_plus_brush_size);
        iv_minus_brush_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekbar.getProgress() > 1) {
                    int size = seekbar.getProgress() - 1;
                    seekbar.setProgress(size);
                    tv_size.setText(size + "");
                    mDrawingView.setBrushSize(size);
                    constants.putInt("size", seekbar.getProgress(), PathDrawingActivity.this);
                }

            }
        });

        iv_plus_brush_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekbar.getProgress() < 100) {
                    int size = seekbar.getProgress() + 1;
                    seekbar.setProgress(size);
                    tv_size.setText(size + "");
                    mDrawingView.setBrushSize(size);
                    constants.putInt("size", seekbar.getProgress(), PathDrawingActivity.this);
                }
            }
        });

        tv_shapes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_shape.getVisibility() == View.VISIBLE)
                    ll_shape.setVisibility(View.GONE);
                else
                    ll_shape.setVisibility(View.VISIBLE);
            }
        });

        iv_gray_scale = findViewById(R.id.iv_grayscale);
        iv_gray_scale.setOnTouchListener(_touch_listener);

        tv_image_trace.setOnClickListener(this::onClick);
        tv_shade_tool.setOnClickListener(this::onClick);
        tv_pen_brush.setOnClickListener(this::onClick);
        tv_shade_tool.setOnClickListener(this::onClick);

        tv_freehand = findViewById(R.id.tv_free_hand);
        tv_line = findViewById(R.id.tv_line);
        tv_square = findViewById(R.id.tv_square);
        tv_rect = findViewById(R.id.tv_rectangle);
        tv_circle = findViewById(R.id.tv_circle);
        tv_triangle = findViewById(R.id.tv_triangle);

        tv_freehand.setOnClickListener(this);
        tv_line.setOnClickListener(this);
        tv_square.setOnClickListener(this);
        tv_rect.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
        tv_triangle.setOnClickListener(this);
        setupSeekbar();
        String isFromResult = constants.getString("pickfromresult", PathDrawingActivity.this);


        ViewTreeObserver vto = ll_drawing_container.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_drawing_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_drawing_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                CANVAS_WIDTH = ll_drawing_container.getMeasuredWidth();
                CANVAS_HEIGHT = ll_drawing_container.getMeasuredHeight();

                Log.e("TAG", "addOnGlobalLayoutListener " + CANVAS_WIDTH + " * " + CANVAS_HEIGHT + " " + rl_gray_scale.getMeasuredWidth() + "*" + rl_gray_scale.getMeasuredHeight());

//                Bitmap temp = ((BitmapDrawable) getResources().getDrawable(R.drawable.gray_scale)).getBitmap();
//                selected_bitmap = Bitmap.createScaledBitmap(temp, rl_gray_scale.getMeasuredWidth(), rl_gray_scale.getMeasuredHeight(), true);

                if (!isFromResult.isEmpty() && isFromResult.length() > 0) {
                    setupBitmapToCanvas(constants.getString("path", PathDrawingActivity.this));
                }
            }
        });
        ViewTreeObserver vto_rl = rl_gray_scale.getViewTreeObserver();
        vto_rl.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rl_gray_scale.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    rl_gray_scale.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                Log.e("TAG", "addOnGlobalLayoutListener " + CANVAS_WIDTH + " * " + CANVAS_HEIGHT + " " + rl_gray_scale.getMeasuredWidth() + "*" + rl_gray_scale.getMeasuredHeight());
                Bitmap temp = ((BitmapDrawable) iv_gray_scale.getDrawable()).getBitmap();
//                selected_bitmap = Bitmap.createScaledBitmap(temp, iv_gray_scale.getMeasuredWidth(), iv_gray_scale.getMeasuredHeight(), true);
                selected_bitmap = Bitmap.createScaledBitmap(temp, iv_gray_scale.getMeasuredWidth(), iv_gray_scale.getMeasuredHeight(), true);

                bottomlimit = iv_gray_scale.getMeasuredHeight() - 10;
            }
        });

        orientation = getResources().getConfiguration().orientation;
    }

    int xDelta = 0;
    int yDelta = 0;

    View.OnTouchListener _touch_listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view1, MotionEvent event) {

            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            Log.e("TAG", "Bot Axis at touch x " + x + " y " + y);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                            view_gray_scale_indicator.getLayoutParams();

                    if (x >= 0 && y >= 0) {
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    break;

                case MotionEvent.ACTION_MOVE:
                    try {
                        if (x < 0 || y < 0)
                            return true;

                        float[] touchPoint = new float[]{event.getX(), event.getY()};
                        int xCoord = Integer.valueOf((int) touchPoint[0]);
                        int yCoord = Integer.valueOf((int) touchPoint[1]);
                        if (xCoord < 0 || yCoord < 0)
                            return true;
                        int touchedRGB = selected_bitmap.getPixel(xCoord, yCoord);
                        //then do what you want with the pixel data, e.g
                        int redValue = Color.red(touchedRGB);
                        int greenValue = Color.green(touchedRGB);
                        int blueValue = Color.blue(touchedRGB);
                        int alphaValue = Color.alpha(touchedRGB);
                        currentColor = Color.argb(alphaValue, redValue, greenValue, blueValue);
                        view_gray_scale.setBackgroundColor(currentColor);
                        tv_color.setTextColor(currentColor);
                        Log.e("TAG", "ColorValue at Move :- " + currentColor);
                        if (Integer.toString(currentColor).length() <= 9)
                            mDrawingView.setBrushColor(currentColor);
                        view_gray_scale_indicator.bringToFront();

                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view_gray_scale_indicator
                                .getLayoutParams();
                        layoutParams.width = rl_gray_scale.getMeasuredWidth();


                        layoutParams.topMargin = y - yDelta;
                        /*if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            // code for portrait mode
                        } else {
                            // code for landscape mode
                            layoutParams.leftMargin = x - xDelta;
                        }*/

                        if (layoutParams.topMargin >= topLimit && layoutParams.topMargin <= bottomlimit) {
                            Log.e("TAG", "Top Margin Set " + layoutParams.topMargin + " " + bottomlimit);
                            view_gray_scale_indicator.setLayoutParams(layoutParams);
                        } else {

                            if (layoutParams.topMargin <= 0)
                                layoutParams.topMargin = 1;
                            else if (layoutParams.topMargin >= bottomlimit)
                                layoutParams.topMargin = bottomlimit;

                            view_gray_scale_indicator.setLayoutParams(layoutParams);
                            Log.e("TAG", "Top Margin Set else " + layoutParams.topMargin + " Left Margin " + layoutParams.leftMargin);
                        }

                    } catch (Exception e) {
                        Log.e("TAG", "Exception at e " + e.getMessage());
                    }
                    break;
            }
            view_gray_scale_indicator.invalidate();
            return true;
        }
    };


    void setupSeekbar() {
        seekbar_1.setProgress(160);
        seekbar_1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                try {
                    if (progress > 0) {
                        alphaValue = ((double) progress / 255) * 10;
                        Log.e("TAG", "Alfa Value set " + ((float) progress / 255));
                        _iv_trace_image.setAlpha((float) progress / 255);
                    } else {
                        alphaValue = 0.0;
                        _iv_trace_image.setAlpha(0.0f);
                    }
                    if (!isBGsetup) {
                        view1.setBackground(getResources().getDrawable(R.drawable.bkg_b_w));
                        view2.setBackground(getResources().getDrawable(R.drawable.bkg_b_w));
                        isBGsetup = true;
                        Log.e("TAG", "Current Progress Goto set color");
                    }
                    if (progress == 0) {
                        isBGsetup = false;
                        view1.setBackground(getResources().getDrawable(R.drawable.bkg_yellow));
                    } else if (progress == 255) {
                        isBGsetup = false;
                        view2.setBackground(getResources().getDrawable(R.drawable.bkg_yellow));
                    }
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
                //removed *&& alphaValue != 0.0 && alphaValue != 10.0* from condition

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_shade:
                tv_shade_tool.setTextColor(getResources().getColor(R.color.yellow));
                tv_pen_brush.setTextColor(getResources().getColor(R.color.white));
                mDrawingView.changeBrush(1);
                Toast.makeText(this, "Shade Tool Activated", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_pen_brush:
                tv_shade_tool.setTextColor(getResources().getColor(R.color.white));
                tv_pen_brush.setTextColor(getResources().getColor(R.color.yellow));
                mDrawingView.changeBrush(0);
                Toast.makeText(this, "Line Tool Activated", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_free_hand:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_freehand, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_freehand);
                tv_shapes.setText(tv_freehand.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.SMOOTHLINE;
                mDrawingView.reset();
                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.tv_line:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_line, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_line);
                tv_shapes.setText(tv_line.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.LINE;
                mDrawingView.reset();
                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.tv_square:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_square, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_square);
                tv_shapes.setText(tv_square.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.SQUARE;
                mDrawingView.reset();
                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.tv_rectangle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_rectangle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_rectangle);
                tv_shapes.setText(tv_rect.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.RECTANGLE;
                mDrawingView.reset();
                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.tv_circle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_circle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_circle);
                tv_shapes.setText(tv_circle.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.CIRCLE;
                mDrawingView.reset();
                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.tv_triangle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_triangle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_triangle);
                tv_shapes.setText(tv_triangle.getText().toString());
                ll_shape.setVisibility(View.GONE);
                mDrawingView.mCurrentShape = DrawingView.TRIANGLE;
                mDrawingView.reset();
                break;

            case R.id.tv_image_trace:

//                int permission_photo = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if (permission_photo != PackageManager.PERMISSION_GRANTED) {
//                    try {
//                        ActivityCompat.requestPermissions(
//                                PathDrawingActivity.this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                PERMISSION_PHOTO);
//                        return;
//                    } catch (Exception e) {
//
//                    }
//                }
                if (!PermissionUtils.checkStoragePermission(PathDrawingActivity.this)) {
                    // We don't have permission so prompt the user
                    PermissionUtils.requestStoragePermission(PathDrawingActivity.this, PERMISSION_PHOTO);
                    return;
                }
                showDialogForPickPhoto();
                break;
            case R.id.tv_image_camera:
                int permission_camera = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.CAMERA);
                if (permission_camera != PackageManager.PERMISSION_GRANTED) {
                    try {
                        ActivityCompat.requestPermissions(
                                PathDrawingActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                SELECT_Camera_REQUEST);
                        return;
                    } catch (Exception e) {

                    }
                }

//                if (!PermissionUtils.checkStoragePermission(PathDrawingActivity.this) &&
//                        !PermissionUtils.checkCameraPermission(PathDrawingActivity.this)) {
//                    // We don't have permission so prompt the user
//                    requestStoragePermissionWithCamera(SELECT_Camera_REQUEST);
//                    return;
//                }
                showDialogForCamera();
                break;

            case R.id.view_gray_scale:
                if (rl_gray_scale.getVisibility() == View.VISIBLE)
                    rl_gray_scale.setVisibility(View.GONE);
                else
                    rl_gray_scale.setVisibility(View.VISIBLE);
                break;
        }
    }


    void showDialogForPickPhoto() {
        Bitmap emptyBitmap = Bitmap.createBitmap(mDrawingView.getWidth(), mDrawingView.getHeight(), Bitmap.Config.ARGB_8888);
        if (mDrawingView.getBitmap().sameAs(emptyBitmap)) {
          /*  Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO_REQUEST);*/

            new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Switch to Trace Mode ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PHOTO_REQUEST);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } else {
            new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Do you want to save painting ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    showDialog(DrawingMode.Trace);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PHOTO_REQUEST);
                }
            }).setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    void showDialogForCamera() {
        Log.e("TAG", "Camera showDialogForCamera");
        Bitmap emptyBitmap = Bitmap.createBitmap(mDrawingView.getWidth(), mDrawingView.getHeight(), Bitmap.Config.ARGB_8888);
        if (mDrawingView.getBitmap().sameAs(emptyBitmap)) {
            /*  addCamera();*/
            new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Switch to Trace Mode ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addCamera();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } else {
            new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Do you want to save painting ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Log.e("TAG", "Camera showDialog Camera");
                    showDialog(DrawingMode.Camera);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    /*addCamera();*/
                    new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Switch to Trace Mode ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addCamera();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }).setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    @Override
    public void hideLayout() {
        if (ll_shape.getVisibility() == View.VISIBLE)
            ll_shape.setVisibility(View.GONE);

        if (rl_gray_scale.getVisibility() == View.VISIBLE)
            rl_gray_scale.setVisibility(View.GONE);
    }


    public void showDialog(DrawingMode mode) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.savedlg);

        Button btn_save = (Button) dialog.findViewById(R.id.btn_projectnamesave);

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        EditText edt_file_name = (EditText) dialog.findViewById(R.id.txtprojectname);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_file_name.getText().toString().isEmpty())
                    edt_file_name.setError(getResources().getString(R.string.required));
                else {
                    dialog.dismiss();
//                    saveFile(edt_file_name.getText().toString());
                    mDrawingView.setBackgroundColor(Color.WHITE);
                    saveImage(mDrawingView.getBitmap(), edt_file_name.getText().toString(), mode);
                }
            }
        });
        dialog.show();
    }

    public String saveImage(Bitmap pBitmap, String pString, DrawingMode mode) {
        if (pBitmap == null)
            return null;

        if (pString == null)
            pString = generateFileName();
        String str1 = String.valueOf(pString);
        String str2 = str1 + ".png";

        try {
            String str3 = String.valueOf(KGlobal.getMyPaintingFolderPath(this));
            File directory = new File(str3);

            if (!directory.exists())
                directory.mkdirs();

            String str4 = str3 + "/" + str2;
            File lFile = new File(str4);
            FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
            pBitmap.compress(lCompressFormat, 0, lBufferedOutputStream);
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();
            Log.e("TAG", "Bitmap Logs While save " + pBitmap.getWidth() + " * " + pBitmap.getHeight());
            String str7 = "File saved in " + lFile.getAbsolutePath();
            Toast.makeText(this, str7 + "", Toast.LENGTH_SHORT).show();

            if (mode == DrawingMode.Trace) {
                new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Switch to Trace Mode ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, SELECT_PHOTO_REQUEST);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            } else if (mode == DrawingMode.Camera) {
                new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Switch to Trace Mode ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCamera();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            } else if (mode == DrawingMode.Exit) {
                finish();
            }
            return str7;
        } catch (FileNotFoundException lFileNotFoundException) {
            return null;
        } catch (IOException lIOException) {
            return null;
        }
    }


    private String generateFileName() {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("MMddyy_HHmmss");
        long l = System.currentTimeMillis();
        Date lDate = new Date(l);
        return lSimpleDateFormat.format(lDate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("TAG", "Camera onRequestPermissionsResult requestCode " + requestCode);
        if (requestCode == SELECT_PHOTO_REQUEST) {
            int permission = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                Toast.makeText(PathDrawingActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                return;
            } else {
                showDialog(DrawingMode.Blank);
            }
        } else if (requestCode == PERMISSION_PHOTO) {
            int permission = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                Toast.makeText(PathDrawingActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                return;
            } else {
                showDialogForPickPhoto();
            }
        } else if (requestCode == SELECT_Camera_REQUEST) {
            int storage_permission = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camera_permission = ActivityCompat.checkSelfPermission(PathDrawingActivity.this, Manifest.permission.CAMERA);

            if (camera_permission == PackageManager.PERMISSION_GRANTED &&
                    storage_permission != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(PathDrawingActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                PermissionUtils.requestStoragePermission(PathDrawingActivity.this, SELECT_Camera_REQUEST);
                return;
            }

            if (storage_permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PathDrawingActivity.this, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                return;
            }

            if (camera_permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PathDrawingActivity.this, getResources().getString(R.string.camera_permission_msg), Toast.LENGTH_LONG).show();
                return;
            }
            Log.e("TAG", "Camera onRequestPermissionsResult");
            showDialogForCamera();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PHOTO_REQUEST) {
                if (data == null)
                    return;

                Uri imageUri = data.getData();
                String str14 = getPath(imageUri);
                if (str14 == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PathDrawingActivity.this);
                    builder.setTitle("Can't Load");
                    builder.setMessage("Selected image is not on your local storage, please download and pick image from there.");
                    builder.setNegativeButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                constants.putString("path", str14, PathDrawingActivity.this);
                constants.putString("pickfromresult", "yes", PathDrawingActivity.this);


                Log.e("TAG", "OnActivity Result File Path " + str14 + " " + mDrawingView.getWidth() + " " + mDrawingView.getHeight() + " CANVAS_WIDTH " + CANVAS_WIDTH + " * " + CANVAS_HEIGHT);
                setupBitmapToCanvas(str14);
            } else if (requestCode == SELECT_Camera_REQUEST) {
                try {
                    if (output == null)
                        return;
                    Uri selectedImage = FileProvider.getUriForFile(this, AUTHORITY, output);
                    String path = getRealPath(selectedImage);
                    Log.e("TAGG", "Path of CameraImage " + path + " selectedImage " + selectedImage);

                    constants.putString("path", path, PathDrawingActivity.this);
                    constants.putString("pickfromresult", "yes", PathDrawingActivity.this);


                    setupBitmapToCanvas(path);
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at onResult " + e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        return path;
    }

    void setupBitmapToCanvas(String selectedImagePath) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (fm_tracebar.getVisibility() != View.VISIBLE)
            fm_tracebar.setVisibility(View.VISIBLE);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = getBitmap(selectedImagePath, BitmapFactory.decodeFile(selectedImagePath, bmOptions));
        setupBitmap(bitmap);


    }

    void setupBitmap(Bitmap bitmap) {
        try {
           /* double canvas_width = mDrawingView.mCanvas.getWidth();
            double canvas_height = mDrawingView.mCanvas.getHeight();
*/
            double canvas_width = CANVAS_WIDTH;
            double canvas_height = CANVAS_HEIGHT;
            Log.e("TAG", "Bitmap Logs setupBitmap called " + canvas_width + " " + canvas_height + " ");

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
            Log.e("TAG", "Calculated logs canvas_width " + canvas_width + " canvas_height " + canvas_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " mDrawingView " + mDrawingView.getWidth() + " " + mDrawingView.getHeight());
            Log.e("TAG", "Calculated logs image_width " + image_width + " image_height " + image_height + " canvas_aspect_ratio " + canvas_aspect_ratio + " image_aspect_ratio " + image_aspect_ratio);
            Log.e("TAG", "Calculated logs final new_width " + new_width + " new_height " + new_height);

            Bitmap b1 = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);
            _iv_trace_image.setImageBitmap(b1);

            LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams((int) (new_width), (int) new_height);
            mDrawingView.setLayoutParams(Params);
            mDrawingView.clearPainting();
            mDrawingView.invalidate();

        } catch (Exception e) {
            Log.e("TAG", "Exception at setupBitmap " + e.getMessage());
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


    @Override
    public void fixOrientation() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            // code for landscape mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void setRotation(float degree) {
       /* _iv_trace_image.setRotation(degree);
        _iv_trace_image.invalidate();

        fm_main_container.setRotation(degree);*/
        rl_container.animate().rotationBy(degree).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        /*rl_container.animate().scaleXBy(scale).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        rl_container.animate().scaleYBy(scale).setDuration(0).setInterpolator(new LinearInterpolator()).start();*/
    }


    @Override
    public void setScale(float degree) {
       /* _iv_trace_image.setRotation(degree);
        _iv_trace_image.invalidate();

        fm_main_container.setRotation(degree);*/

        ll_drawing_container.animate().scaleX(degree).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        ll_drawing_container.animate().scaleY(degree).setDuration(0).setInterpolator(new LinearInterpolator()).start();


        /*rl_container.animate().scaleXBy(scale).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        rl_container.animate().scaleYBy(scale).setDuration(0).setInterpolator(new LinearInterpolator()).start();*/
    }

    @Override
    public void releaseOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }


    private File output = null;
    private final String PHOTOS = "photos";
    private final String FILENAME = "CameraContentDemo.jpeg";

    public void addCamera() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            SandriosCamera
                    .with(this)
                    .setShowPicker(false)
                    .setVideoFileSize(40)
                    .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                    .enableImageCropping(true)
                    .launchCamera(new SandriosCamera.CameraCallback() {
                        @Override
                        public void onComplete(CameraOutputModel model) {
                            File _file = new File(model.getPath());
                            Log.e("TAG", "File Logs At OnComplete " + model.getPath() + " FileName " + _file.getName() + " Parent Name " + _file.getParentFile().getAbsolutePath());
                            Log.e("Type", "" + model.getType());
                            constants.putString("path", model.getPath(), PathDrawingActivity.this);
                            constants.putString("pickfromresult", "yes", PathDrawingActivity.this);

                            setupBitmapToCanvas(model.getPath());
                        }
                    });
        } else {
            output = new File(new File(getFilesDir(), PHOTOS), FILENAME);

            if (output.exists()) {
                output.delete();
            } else {
                output.getParentFile().mkdirs();
            }

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);

            i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(getContentResolver(), "A photo", outputUri);
                i.setClipData(clip);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList =
                        getPackageManager()
                                .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, outputUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            try {
                startActivityForResult(i, SELECT_Camera_REQUEST);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public String getRealPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(PathDrawingActivity.this, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(PathDrawingActivity.this, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(PathDrawingActivity.this, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(PathDrawingActivity.this, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /*This method will check there are external storage is available for their device OR not*/
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /*This method will check there is document pick or not*/
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /*This method verify the selected URI is valid or not once pick file from the local */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*This method will query to find specific file path in device through the cursor.*/
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e("tmessages", "Exception " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Bitmap emptyBitmap = Bitmap.createBitmap(mDrawingView.getWidth(), mDrawingView.getHeight(), Bitmap.Config.ARGB_8888);
            if (mDrawingView.getBitmap().sameAs(emptyBitmap)) {

                new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Exit drawing ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).show();
            } else {
                new AlertDialog.Builder(PathDrawingActivity.this).setMessage("Save painting before exit ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showDialog(DrawingMode.Exit);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}

