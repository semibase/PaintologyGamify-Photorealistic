package com.paintology.lite.photorealistic.drawing.DrawingApp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.BrushType;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.interface_drawing_view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreateBrushActivity extends AppCompatActivity implements View.OnClickListener, interface_drawing_view {

    protected DrawingView mDrawingView;


    SeekBar seekbar;
    TextView tv_size;

    Button tv_save_pattern;
    Button btnCancel;
    ImageView iv_undo, iv_redo;
    public static interface_drawing_view _obj_interface;

    EditText edt_brush_name;
    //    ImageView iv_minus_brush_size, iv_plus_brush_size;
//    LinearLayout ll_shape;
//    TextView tv_shapes;
//    TextView tv_freehand, tv_circle, tv_square, tv_rect, /*tv_triangle,*/ tv_line;
    PatternManager _manager;
    StringConstants _constant = new StringConstants();


//    View view_current;

    LinearLayout view_freehand, view_square, view_line, view_circle, view_rectangle, view_triangle;
    RelativeLayout view_freehand_image_container;
    RelativeLayout view_line_image_container;
    RelativeLayout view_square_image_container;
    RelativeLayout view_rectangle_image_container;
    RelativeLayout view_circle_image_container;
    RelativeLayout view_triangle_image_container;

//    LinearLayout ll_shape_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_create_brush);

        setTitle(null);

        mDrawingView = (DrawingView) findViewById(R.id.drawingview);

        _manager = new PatternManager(CreateBrushActivity.this);
        Log.e("TAG", "List Size oncreate " + _manager.mPatternInfoList.size());
        _obj_interface = this;
//        tv_shapes = findViewById(R.id.tv_shapes);
//        ll_shape = findViewById(R.id.ll_shape);
        seekbar = findViewById(R.id.seekbar);
        tv_size = findViewById(R.id.tv_brush_size);
        tv_size.setText("50");
        seekbar.setMax(100);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_size.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDrawingView.setBrushSize(seekBar.getProgress());
            }

        });
        seekbar.setProgress(50);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_save_pattern = findViewById(R.id.btn_save);
        tv_save_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Bitmap emptyBitmap = Bitmap.createBitmap(mDrawingView.getWidth(), mDrawingView.getHeight(), Bitmap.Config.ARGB_8888);
//                mDrawingView.setEraser();
                    if (edt_brush_name.getText().toString().trim().isEmpty()) {
//                    edt_brush_name.setError("brush name required!");
                        Toast.makeText(CreateBrushActivity.this, "brush name required!", Toast.LENGTH_SHORT).show();
                    } else if (mDrawingView.getBitmap().sameAs(emptyBitmap)) {
                        Toast.makeText(CreateBrushActivity.this, "canvas empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isFound = false;
                        for (int i = 0; i < _manager.mPatternInfoList.size(); i++) {
                            if (_manager.mPatternInfoList.get(i)._brushType != BrushType.RecentBrush) {
                                if (edt_brush_name.getText().toString().equalsIgnoreCase(_manager.mPatternInfoList.get(i).strName)) {
                                    isFound = true;
                                    break;
                                }
                            }
                        }
                        Log.e("TAG", "Name from List isFound " + isFound);
                        int _total_found = 0;
                        if (isFound) {
                            for (int i = 0; i < _manager.mPatternInfoList.size(); i++) {
                                if (_manager.mPatternInfoList.get(i)._brushType != BrushType.RecentBrush) {
                                    String splited[] = _manager.mPatternInfoList.get(i).strName.toString().split("_");
                                    String splite_0 = splited[0];
                                    if (edt_brush_name.getText().toString().equalsIgnoreCase(splite_0)) {
                                        Log.e("TAG", "Name from List from splite_0 " + splite_0 + "<>" + edt_brush_name.getText().toString());
                                        _total_found++;
                                    }
                                }
                            }
                            if (_total_found == 0)
                                saveFile(edt_brush_name.getText().toString().trim() + "_1");
                            else {
                                saveFile(edt_brush_name.getText().toString().trim() + "_" + _total_found);
                            }
                        } else {
                            saveFile(edt_brush_name.getText().toString().trim());
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at save " + e.getMessage());
                }
            }
        });

        edt_brush_name = findViewById(R.id.edt_brush_name);

//        ll_shape_indicator = findViewById(R.id.ll_shape_indicator);

        iv_undo = findViewById(R.id.iv_undo);
        iv_redo = findViewById(R.id.iv_redo);
        iv_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_stroke_undo, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_stroke_undo);
                mDrawingView.undo();
            }
        });
        iv_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_stroke_redo, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_stroke_redo);
                mDrawingView.redo();
            }
        });


//        iv_minus_brush_size = (ImageView) findViewById(R.id.iv_minus_brush_size);
//        iv_plus_brush_size = (ImageView) findViewById(R.id.iv_plus_brush_size);
//        iv_minus_brush_size.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int size = seekbar.getProgress() - 1;
//                seekbar.setProgress(size);
//                tv_size.setText(size + "");
//                mDrawingView.setBrushSize(size);
//            }
//        });
//
//        iv_plus_brush_size.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int size = seekbar.getProgress() + 1;
//                seekbar.setProgress(size);
//                tv_size.setText(size + "");
//                mDrawingView.setBrushSize(size);
//            }
//        });

//        ll_shape_indicator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ll_shape.getVisibility() == View.VISIBLE)
//                    ll_shape.setVisibility(View.GONE);
//                else
//                    ll_shape.setVisibility(View.VISIBLE);
//            }
//        });


//        tv_freehand = findViewById(R.id.tv_free_hand);
//        tv_line = findViewById(R.id.tv_line);
//        tv_square = findViewById(R.id.tv_square);
//        tv_rect = findViewById(R.id.tv_rectangle);
//        tv_circle = findViewById(R.id.tv_circle);
//        tv_triangle = findViewById(R.id.tv_triangle);


/*

        tv_freehand.setOnClickListener(this);
        tv_line.setOnClickListener(this);
        tv_square.setOnClickListener(this);
        tv_rect.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
        tv_triangle.setOnClickListener(this);
*/


//        view_current = findViewById(R.id.view_current_shape);
        view_freehand = findViewById(R.id.ll_freehand);
        view_square = findViewById(R.id.ll_square);
        view_circle = findViewById(R.id.ll_circle);
        view_line = findViewById(R.id.ll_line);
        view_rectangle = findViewById(R.id.ll_rectangle);
        view_triangle = findViewById(R.id.ll_triangle);

        view_freehand_image_container = findViewById(R.id.freehand_image_container);
        view_line_image_container = findViewById(R.id.line_image_container);
        view_square_image_container = findViewById(R.id.square_image_container);
        view_rectangle_image_container = findViewById(R.id.rectangle_image_container);
        view_circle_image_container = findViewById(R.id.circle_image_container);
        view_triangle_image_container = findViewById(R.id.triangle_image_container);

        view_freehand.setOnClickListener(this);
        view_square.setOnClickListener(this);
        view_line.setOnClickListener(this);
        view_triangle.setOnClickListener(this);
        view_circle.setOnClickListener(this);
        view_rectangle.setOnClickListener(this);

        mDrawingView.mCurrentShape = DrawingView.SMOOTHLINE;

        resetTypeSelection();
        setTypeSelection(view_freehand_image_container);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_freehand:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_freehand, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_freehand);
//                tv_shapes.setText(tv_freehand.getText().toString());
//                view_current.setBackground(getResources().getDrawable(R.drawable.freehand_shape));

                mDrawingView.mCurrentShape = DrawingView.SMOOTHLINE;
                mDrawingView.reset();

                resetTypeSelection();
                setTypeSelection(view_freehand_image_container);

//                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.ll_line:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_line, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_line);
//                tv_shapes.setText(tv_line.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.LINE;
//                view_current.setBackground(getResources().getDrawable(R.drawable.shape_line));
                mDrawingView.reset();
                resetTypeSelection();
                setTypeSelection(view_line_image_container);

//                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.ll_square:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_square, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_square);
//                tv_shapes.setText(tv_square.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.SQUARE;
//                view_current.setBackground(getResources().getDrawable(R.drawable.shape_square));
                mDrawingView.reset();
                resetTypeSelection();
                setTypeSelection(view_square_image_container);

//                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.ll_rectangle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_rectangle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_rectangle);
//                tv_shapes.setText(tv_rect.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.RECTANGLE;
                mDrawingView.reset();
                resetTypeSelection();
                setTypeSelection(view_rectangle_image_container);

//                view_current.setBackground(getResources().getDrawable(R.drawable.shape_rectangle));
//                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.ll_circle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_circle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_circle);
//                tv_shapes.setText(tv_circle.getText().toString());
                mDrawingView.mCurrentShape = DrawingView.CIRCLE;
                mDrawingView.reset();
                resetTypeSelection();
                setTypeSelection(view_circle_image_container);

//                view_current.setBackground(getResources().getDrawable(R.drawable.shape_circle));
//                ll_shape.setVisibility(View.GONE);
                break;
            case R.id.ll_triangle:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_triangle, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_triangle);
//                tv_shapes.setText(tv_triangle.getText().toString());
//                ll_shape.setVisibility(View.GONE);
                mDrawingView.mCurrentShape = DrawingView.TRIANGLE;
//                view_current.setBackground(getResources().getDrawable(R.drawable.shape_triangle));
                mDrawingView.reset();
                resetTypeSelection();
                setTypeSelection(view_triangle_image_container);
                break;
        }
    }

    private void setTypeSelection(RelativeLayout view) {
        view.setBackground(getResources().getDrawable(R.drawable.border));
    }

    private void resetTypeSelection() {
        Drawable grayBorder = getResources().getDrawable(R.drawable.gray_border);
        view_freehand_image_container.setBackground(grayBorder);
        view_line_image_container.setBackground(grayBorder);
        view_square_image_container.setBackground(grayBorder);
        view_rectangle_image_container.setBackground(grayBorder);
        view_circle_image_container.setBackground(grayBorder);
        view_triangle_image_container.setBackground(grayBorder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    void saveFile(String filename) {
        File file = new File(KGlobal.getPatternFolderPath(this) + "/" + filename.toLowerCase().trim() + ".png");
        Log.e("TAG", "File Path " + file.getAbsolutePath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(file);

            System.out.println(ostream);
            Bitmap well = mDrawingView.getBitmap();
            Bitmap save = Bitmap.createBitmap(well.getWidth(), well.getWidth(), Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.TRANSPARENT);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, well.getWidth(), well.getWidth()), paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, well.getWidth(), well.getWidth()), null);
            // Canvas now = new Canvas(save);
            //myDrawView.layout(0, 0, 100, 100);
            //myDrawView.draw(now);
            if (save == null) {
                System.out.println("NULL bitmap save\n");
            }
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

            if (BuildConfig.DEBUG) {
                Toast.makeText(getApplicationContext(), _constant.custom_brush_menu_save, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(getApplicationContext(), _constant.custom_brush_menu_save);

            Intent _intent = new Intent();
            _intent.putExtra("_pattern_name", filename);
            _intent.putExtra("_pattern_image_path", file.getAbsolutePath());
            setResult(RESULT_OK, _intent);
            finish();

        } catch (NullPointerException e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void hideLayout() {
//        if (ll_shape.getVisibility() == View.VISIBLE)
//            ll_shape.setVisibility(View.GONE);
    }

    @Override
    public void fixOrientation() {

    }

    @Override
    public void setRotation(float degree) {

    }

    @Override
    public void setScale(float degree) {

    }

    @Override
    public void releaseOrientation() {

    }

}

