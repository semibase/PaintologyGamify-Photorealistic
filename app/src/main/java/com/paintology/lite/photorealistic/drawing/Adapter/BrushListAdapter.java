package com.paintology.lite.photorealistic.drawing.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Model.BrushDemoItem;
import com.paintology.lite.photorealistic.drawing.Model.BrushType;
import com.paintology.lite.photorealistic.drawing.Model.PatternInfo;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.brushsetting.BrushSettingManager;
import com.paintology.lite.photorealistic.drawing.colorpicker.RandomColorPicker;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util._inteface_brushpicker;

import java.util.ArrayList;
import java.util.Objects;

public class BrushListAdapter extends RecyclerView.Adapter<BrushListAdapter.MyViewHolder> {


    ArrayList<String> brushList = new ArrayList<>();
    Context _context;
    StringConstants constants;
    //    PatternManager mPatternManager;
    BrushSettingManager mBrushSettingManager;
    public ArrayList<Integer> mBrushSortList;
    BrushDemoItem lBrushDemoItem;
    int _selectedPid = 0;
    BrushDemoItem[] mBrushDemoItems;
    RandomColorPicker mRandomDarkColorPicker;
    Painting mPainting;

    ArrayList<PatternInfo> _patternInfoList;
    _inteface_brushpicker _obj_interface;

    public BrushListAdapter(Context _context, ArrayList<Integer> mBrushSortList, BrushDemoItem[] mBrushDemoItems, _inteface_brushpicker _obj_interface, ArrayList<PatternInfo> _patternInfoList) {
        this._context = _context;
        constants = new StringConstants();
        mBrushSettingManager = new BrushSettingManager(_context);
//        mPatternManager = new PatternManager(_context);
        this.mBrushSortList = mBrushSortList;
        this.mBrushDemoItems = mBrushDemoItems;
        mRandomDarkColorPicker = new RandomColorPicker(32, RandomColorPicker.ColorPref.DARK_COLOR);
        mPainting = new Painting(null);
        mPainting.createCanvas(480, 60);
        mPainting.mBrushDemoMode = true;

        this._patternInfoList = _patternInfoList;
        this._obj_interface = _obj_interface;
        brushList.add("line");
        brushList.add("charcoal");
        brushList.add("shade");
        brushList.add("eraser");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brush_picker_list_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder lAppViewHolder, int pInt) {
        try {
            int pos = lAppViewHolder.getAdapterPosition();
            int style = _patternInfoList.get(pInt).style;
            lAppViewHolder.llRedeem.setVisibility(View.GONE);
            try {
                if (_patternInfoList.get(pInt)._brushType == BrushType.HeadRecent || _patternInfoList.get(pInt)._brushType == BrushType.HeadPaintology || _patternInfoList.get(pInt)._brushType == BrushType.HeadCustom) {
                    lAppViewHolder.ll_brush_head.setVisibility(View.VISIBLE);
//                    lAppViewHolder.ll_index.setVisibility(View.GONE);
                    lAppViewHolder.parent_linear.setVisibility(View.GONE);
                    lAppViewHolder.tv_head_name.setText(_patternInfoList.get(pInt).strName);
                    lAppViewHolder.tv_index.setVisibility(View.GONE);
                    return;
                } else {
                    lAppViewHolder.tv_index.setVisibility(View.VISIBLE);
//                    lAppViewHolder.ll_index.setVisibility(View.VISIBLE);
                    lAppViewHolder.tv_index.setText(_patternInfoList.get(pInt)._index + ".");
                    lAppViewHolder.parent_linear.setVisibility(View.VISIBLE);
                    lAppViewHolder.ll_brush_head.setVisibility(View.GONE);
                }

                if (_patternInfoList.get(pInt).isselect) {
                    lAppViewHolder.parent_linear.setBackgroundColor(_context.getResources().getColor(R.color.header_color));
                } else {
                    lAppViewHolder.parent_linear.setBackgroundColor(_context.getResources().getColor(R.color.white));
                }

                if (_patternInfoList.get(pInt)._brushType == BrushType.CustomeBrush)
                    lAppViewHolder.iv_del_icon.setVisibility(View.VISIBLE);
                else
                    lAppViewHolder.iv_del_icon.setVisibility(View.GONE);
            } catch (Exception ex) {
                Log.e("TAG", "Exception at setbackground color " + ex.getMessage());
            }


            if (_patternInfoList.get(pos)._brushType == BrushType.PaintologyBrush || _patternInfoList.get(pos)._brushType == BrushType.RecentBrush) {
                try {
                    String key = _patternInfoList.get(pos).strName.toLowerCase().replace(" ", "_");
                    if (brushList.contains(key)) {
                        lAppViewHolder.llRedeem.setVisibility(View.GONE);
                    } else if (!AppUtils.getPurchasedBrushes().contains(key)) {
                        lAppViewHolder.llRedeem.setVisibility(View.VISIBLE);
                        lAppViewHolder.llRedeem.setOnClickListener(v -> {
                            FireUtils.getStoreDetails(_context, key, (productId, productName) -> {
                                FireUtils.showProgressDialog(_context, _context.getResources().getString(R.string.please_wait));
                                FirebaseFirestoreApi.redeemProduct(productId)
                                        .addOnCompleteListener(task -> {
                                            FireUtils.hideProgressDialog();
                                            if (task.isSuccessful()) {
                                                if (!productName.equalsIgnoreCase("")) {
                                                    AppUtils.getPurchasedBrushes().add(productName);
                                                } else {
                                                    AppUtils.getPurchasedBrushes().add(key);
                                                }
                                                lAppViewHolder.llRedeem.setVisibility(View.GONE);
                                                ContextKt.showToast(_context, "Redeem Success");
                                            } else {
                                                try {
                                                    if (task.getException() != null) {
                                                        if (task.getException().toString().contains("Insufficient points")) {
                                                            FireUtils.showStoreError(_context, "brush");
                                                        } else {
                                                            ContextKt.showToast(_context, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                                        }
                                                        Log.e("TAGRR", task.getException().toString());
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            });
                        });
                    } else {
                        lAppViewHolder.llRedeem.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                lAppViewHolder.llRedeem.setVisibility(View.GONE);
            }

            lAppViewHolder.brushName.setText(_patternInfoList.get(pInt).strName);

            lBrushDemoItem = new BrushDemoItem(style, -1, -1, null, null);
            if (_selectedPid == _patternInfoList.get(pInt).style) {
                lAppViewHolder.parent_linear.setBackground(_context.getResources().getDrawable(R.drawable.pressed));
            } else {
                lAppViewHolder.parent_linear.setBackground(_context.getResources().getDrawable(R.drawable.normal));
            }
            try {
                if (_patternInfoList.get(pInt).getBrushDemoImage() == null) {
                    if ((lBrushDemoItem.brushType < 256) || (lBrushDemoItem.brushType > 511)) {
                        generateNonSketchyBrushDemoImage(lBrushDemoItem);
                    } else {
                        generateSketchyBrushDemoImage(lBrushDemoItem);
                    }
                    _patternInfoList.get(pInt).setBrushDemoImage(lBrushDemoItem.brushDemoImage);
                    lAppViewHolder.brushDemo.setImageBitmap(lBrushDemoItem.brushDemoImage);
                } else {
                    lAppViewHolder.brushDemo.setImageBitmap(_patternInfoList.get(pInt).getBrushDemoImage());
                }
            } catch (OutOfMemoryError e) {

            } catch (Exception ex) {
                Log.e("TAG", "Exception at createPAttern " + ex.getMessage());
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at brushPicker " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        try {
            return _patternInfoList.size();
        } catch (Exception lException) {
            return 0;
        }
    }

    private BrushDemoItem getBrushDemoItem(int pInt) {

        /*for (int i = 0; i < mBrushDemoItems.length; i++) {
            Log.e("TAG", "Brush Name at getBrushDemoItem " + mBrushDemoItems[i].brushName);
        }*/

        for (int i = 0; i < mBrushDemoItems.length; i++) {
            if (mBrushDemoItems[i].brushType == pInt)
                return mBrushDemoItems[i];
        }
        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout llRedeem;
        ImageView brushDemo;
        ImageView iv_del_icon;
        TextView brushName, tv_head_name, tv_index;
        LinearLayout parent_linear, ll_brush_head, ll_index;

        public MyViewHolder(View view) {
            super(view);
            llRedeem = view.findViewById(R.id.llRedeem);
            tv_index = (TextView) view.findViewById(R.id.tv_index);
            brushName = (TextView) view.findViewById(R.id.brush_name);
            tv_head_name = (TextView) view.findViewById(R.id.tv_head_name);
            brushDemo = (ImageView) view.findViewById(R.id.brush_demo);
            parent_linear = (LinearLayout) view.findViewById(R.id.parent_linear);
            ll_brush_head = (LinearLayout) view.findViewById(R.id.ll_brush_head);
            iv_del_icon = (ImageView) view.findViewById(R.id.iv_del_brush);
            ll_index = (LinearLayout) view.findViewById(R.id.ll_index);
            parent_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", "OnItemClickListener called ");
//                    v.setTag(_patternInfoList.get(getAdapterPosition()).style);
                    try {
                        _obj_interface._onclick(_patternInfoList.get(getAdapterPosition()));
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at onclick " + e.getMessage());
                    }
                }
            });

            iv_del_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert(getAdapterPosition());
                }
            });
        }
    }


    void showAlert(int pos) {
        new AlertDialog.Builder(_context).setMessage("Delete custom brush " + _patternInfoList.get(pos).strName + " ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int style = _patternInfoList.get(pos).style;
                        boolean isFoundInRecent = false;
                        for (int i = 0; i < _patternInfoList.size(); i++) {
                            if (_patternInfoList.get(i).style == style) {
                                if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                                    isFoundInRecent = true;
                                }
                                _patternInfoList.remove(i);
                                notifyItemRemoved(i);
                            }
                        }

                        int _custom_head_pos = 0;
                        int index = 0;
                        //Reindexing recent brush
                        for (int i = 0; i < _patternInfoList.size(); i++) {
                            if (_patternInfoList.get(i)._brushType == BrushType.CustomeBrush) {
                                index = index + 1;
                                _patternInfoList.get(i)._index = index;
                            }

                            if (_patternInfoList.get(i)._brushType == BrushType.HeadCustom) {
                                _custom_head_pos = i;
                            }
                        }

                        if (index == 0) {
                            _patternInfoList.remove(_custom_head_pos);
                            notifyItemRemoved(_custom_head_pos);
//                            Toast.makeText(_context, "Header Removed", Toast.LENGTH_SHORT).show();
                        }


                        int _index = 0;
                        if (isFoundInRecent) {
                            //Reindexing
                            for (int i = 0; i < _patternInfoList.size(); i++) {
                                if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                                    _index = _index + 1;
                                    _patternInfoList.get(i)._index = _index;
                                }
                            }
                            _patternInfoList.get(0).setStrName("Recent Brushes (" + _index + ")");
                        }
                        try {
                            if (_index >= 1) {
                                //set default from the recent list
                                _obj_interface._onclick(_patternInfoList.get(1));
                            } else {
                                //set default from the paintology brush

                                PatternInfo _info_carcoal = null;
                                for (int i = 0; i < _patternInfoList.size(); i++) {
                                    if (_patternInfoList.get(i).style == 576) {
                                        _info_carcoal = _patternInfoList.get(i);
                                        break;
                                    }
                                }
                                if (_info_carcoal != null) {
                                    _obj_interface._onclick(_info_carcoal);
                                    addBrushInRecent(_info_carcoal.style);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "Exception at set in recent " + e.toString());
                        }


                        int pos_of_paintology_head = 0;
                        int total_custom = 0;
                        ArrayList<PatternInfo> _latestList = new ArrayList<>();
                        for (int i = 0; i < _patternInfoList.size(); i++) {
                            if (_patternInfoList.get(i)._brushType == BrushType.HeadCustom) {
                                pos_of_paintology_head = i;
                            }
                            if (_patternInfoList.get(i)._brushType == BrushType.CustomeBrush) {
                                total_custom = total_custom + 1;

                                PatternInfo o1 = _patternInfoList.get(i);
                                PatternInfo object = new PatternInfo(o1.style, o1.maxSize, o1.minSize, o1.spacing, o1.alphaScale, o1.strName, o1.isselect, o1.resID, o1._filePath, o1._brushType, o1.points, o1.isPatternBrush, o1._index);
                                _latestList.add(object);
                            }
                        }


                        addInPreference(_latestList);
                        if (total_custom != 0)
                            _patternInfoList.get(pos_of_paintology_head).setStrName(_context.getResources().getString(R.string.custom_brush) + " (" + total_custom + ")");
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    public void setSelected(int _value) {
        _selectedPid = _value;
        Log.e("TAG", "Selected Style Id " + _selectedPid);
        notifyDataSetChanged();
    }

    public void addItemInList(PatternInfo _objPattern) {
        Log.e("TAG", "addItemInList Called _objPattern name " + _objPattern.strName);
        int _recentLastIndex = 0;
        int _custome_brush_size = 0;

        /*for (int i = 0; i < _patternInfoList.size(); i++) {
            if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                _recentLastIndex = _recentLastIndex + 1;
            }
            if (_patternInfoList.get(i)._brushType == BrushType.CustomeBrush) {
                _custome_brush_size = _custome_brush_size + 1;
            }
        }*/


        int total_custom = 0;
        int pos_of_paintology_head = -1;
        for (int i = 0; i < _patternInfoList.size(); i++) {

            if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                _recentLastIndex = _recentLastIndex + 1;
            }

            if (_patternInfoList.get(i)._brushType == BrushType.HeadCustom) {
                pos_of_paintology_head = i;
            }

            if (_patternInfoList.get(i)._brushType == BrushType.CustomeBrush) {
                total_custom = total_custom + 1;
            }
        }

        if (pos_of_paintology_head == -1) {
            PatternInfo _obj_customebrush = new PatternInfo(2, 100, 1, 5, 1.0F, _context.getResources().getString(R.string.custom_brush) + " (1)", false, R.drawable.icon, "", BrushType.HeadCustom, null, false, 2);
            _recentLastIndex = _recentLastIndex + 1;
            _patternInfoList.add(_recentLastIndex, _obj_customebrush);
            notifyItemInserted(_recentLastIndex);
            _recentLastIndex = _recentLastIndex + 1;
            _patternInfoList.add(_recentLastIndex, _objPattern);
            notifyItemInserted(_recentLastIndex);
            _patternInfoList.get(_recentLastIndex)._index = 1;
        } else {
            total_custom = total_custom + 1;
            _patternInfoList.get(pos_of_paintology_head).setStrName(_context.getResources().getString(R.string.custom_brush) + " (" + total_custom + ")");
            pos_of_paintology_head = pos_of_paintology_head + 1;
            _patternInfoList.add(pos_of_paintology_head, _objPattern);
            notifyItemInserted(pos_of_paintology_head);
//            _patternInfoList.get(pos_of_paintology_head)._index = total_custom;

            //Re-Indexing
            int _index = 0;
            for (int i = 0; i < _patternInfoList.size(); i++) {
                if (_patternInfoList.get(i)._brushType == BrushType.CustomeBrush) {
                    _index = _index + 1;
                    _patternInfoList.get(i)._index = _index;
//                    if (_index < 5)
                }
            }
        }
        notifyDataSetChanged();
    }

    private void generateNonSketchyBrushDemoImage(BrushDemoItem pBrushDemoItem) {
        if ((pBrushDemoItem.brushDemoImage != null) && (!pBrushDemoItem.brushDemoImage.isRecycled()))
            return;

        try {
            Canvas lCanvas = null;
            Path lPath;
            int i = 220;
            int j = 40;
            Bitmap lBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
            if (lBitmap != null && !lBitmap.isRecycled())
                lCanvas = new Canvas(lBitmap);
            else
                return;
            lPath = new Path();

            if (pBrushDemoItem.brushType == 39) {
                Brush lBrush1 = Brush.createBrush(pBrushDemoItem.brushType);
                lBrush1.setColor(-65536);
                mRandomDarkColorPicker.resetPicker();
                lBrush1.setRandomColorPicker(mRandomDarkColorPicker);
//            generateRainbowBrushDemo(lBrush1, lCanvas, i, j);
            }

            Brush lBrush2;

            lBrush2 = Brush.createBrush(pBrushDemoItem.brushType);

            lBrush2.setColor(0xFF000000);
            lBrush2.setPatternManager(new PatternManager(_context));
            lBrush2.prepareBrush();
            lBrush2.setSize(10);
            lBrush2.getPaint().setColor(0xFF000000);

            if (lBrush2.mBrushStyle < 512) {
                lPath.moveTo(5, 20);
                lPath.quadTo(55, 1.0F, 110, 20);
                lPath.quadTo(165, 39, 215, 20);
                lBrush2.drawStroke(lCanvas, lPath);
            } else {
                try {
                    Point lPoint1 = new Point(5, 20);
                    Point lPoint2 = new Point(55, 10);
                    Point lPoint3 = new Point(110, 20);
//                    Log.e("TAG", "drawStroke called 239");
                    lBrush2.drawStroke(lCanvas, lPoint1, lPoint2, lPoint3);
                    Point lPoint4 = new Point(110, 20);
                    Point lPoint5 = new Point(165, 30);
                    Point lPoint6 = new Point(215, 20);
                    lBrush2.drawStroke(lCanvas, lPoint4, lPoint5, lPoint6);
                } catch (Exception e) {
                    Log.e("TAG", "Exception at generate NonSketchyBrushDemoImage " + e.getMessage());
                }
            }
            lBrush2.endStroke();
            pBrushDemoItem.brushDemoImage = lBitmap;
        } catch (Exception e) {
            Log.e("TAG", "Exception at generateNonSketchyBrushDemoImage " + e.getMessage());
        }
    }

    private void generateSketchyBrushDemoImage(BrushDemoItem pBrushDemoItem) {
        try {

            Painting lPainting = mPainting;
            lPainting.setBackgroundColor(0xFFFF0000);

            if ((pBrushDemoItem.brushDemoImage != null) && (!pBrushDemoItem.brushDemoImage.isRecycled()))
                return;

            if ((pBrushDemoItem.brushType == 256)) {
                pBrushDemoItem.brushDemoImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(_context.getResources(), R.drawable.stretchdemo), 220, 40, true);
                return;
            } else if ((pBrushDemoItem.brushType == 257)) {
                pBrushDemoItem.brushDemoImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(_context.getResources(), R.drawable.stretchfurdemo), 220, 40, true);
                return;
            } else if ((pBrushDemoItem.brushType == 272)) {
                pBrushDemoItem.brushDemoImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(_context.getResources(), R.drawable.stretchovaldemo), 220, 40, true);
                return;
            }

            lPainting.setBrushStyle(pBrushDemoItem.brushType);
            lPainting.setBrushColor(0xFF000000);
            lPainting.setBrushSize(1.0F);
            lPainting.mBrushKidOrArtistMode = 33;
            lPainting.clearPainting();

            float[] arrayOfFloat = pBrushDemoItem.points;
            int j = (int) arrayOfFloat[0];
            int m = (int) arrayOfFloat[1];

            int i1;

            if ((pBrushDemoItem.brushType != 266) && (pBrushDemoItem.brushType != 267)) {
                lPainting.strokeFrom(j, m);

                for (i1 = 1; i1 < arrayOfFloat.length / 2; i1++) {
                    int k = (int) arrayOfFloat[i1 * 2];
                    int i4 = i1 * 2 + 1;
                    int n = (int) arrayOfFloat[i4];
                    lPainting.strokeTo(k, n);
                }
            }

            lPainting.strokeEnd(j, m);
            Bitmap lBitmap = Bitmap.createScaledBitmap(lPainting.getBitmap(), 220, 40, true);
            pBrushDemoItem.brushDemoImage = lBitmap;
            lPainting.clearPainting();
        } catch (Exception e) {
            Log.e("TAG", "Exception at generateSketchyBrushDemoImage " + e.getMessage());
        }
    }

    public void addBrushInRecent(int brushStyle) {
        try {
            boolean isRecentAdded = false;
            int total_recent = 0;
            for (int i = 0; i < _patternInfoList.size(); i++) {
                if (_patternInfoList.get(i)._brushType == BrushType.HeadRecent) {
                    isRecentAdded = true;
                }
                if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                    total_recent += 1;
                }
            }

            if (!isRecentAdded) {
                PatternInfo _obj_paitology = new PatternInfo(1, 100, 1, 5, 1.0F, "Recent Brushes (1)", false, 0, "", BrushType.HeadRecent, null, false, 1);
                _patternInfoList.add(0, _obj_paitology);
                notifyItemInserted(0);
            } else {
                boolean isRemoved = false;
                //Remove existing if same exist
                for (int i = 0; i < _patternInfoList.size(); i++) {
                    if (_patternInfoList.get(i).style == brushStyle && _patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                        _patternInfoList.remove(i);
                        notifyItemRemoved(i);
                        isRemoved = true;
                        break;
                    }
                }
                if (!isRemoved)
                    total_recent = total_recent + 1;
                _patternInfoList.get(0).setStrName("Recent Brushes (" + total_recent + ")");
            }

            for (int i = 0; i < _patternInfoList.size(); i++) {
                if (_patternInfoList.get(i).style == brushStyle) {
                    PatternInfo _info = _patternInfoList.get(i);
                    PatternInfo _object = new PatternInfo(_info.style, _info.maxSize, _info.minSize, _info.spacing, _info.alphaScale, _info.strName, _info.isselect, 0, _info._filePath, BrushType.RecentBrush, _info.points, _info.isPatternBrush, 1);
                    _object.setBrushDemoImage(_info.getBrushDemoImage());
                    _patternInfoList.add(1, _object);
                    notifyItemInserted(1);
                    break;
                }
            }


            int _index = 1;
            //Reindexing
            for (int i = 0; i < _patternInfoList.size(); i++) {
                if (_patternInfoList.get(i).style != brushStyle && _patternInfoList.get(i)._brushType == BrushType.RecentBrush) {
                    _index = _index + 1;
                    _patternInfoList.get(i)._index = _index;
//                    if (_index < 5)
                }
            }


            if (_index > 5) {
                _patternInfoList.remove(6);
                notifyItemRemoved(6);
                _patternInfoList.get(0).setStrName("Recent Brushes (" + 5 + ")");
            }
            notifyDataSetChanged();

            ArrayList<Integer> _recent_list = new ArrayList<>();
            for (int i = 0; i < _patternInfoList.size(); i++) {
                if (_patternInfoList.get(i)._brushType == BrushType.RecentBrush)
                    _recent_list.add(_patternInfoList.get(i).style);
            }
            Gson gson = new Gson();
            Log.e("TAG", "New Json From List Recent " + gson.toJson(_recent_list));

            if (_recent_list.size() == 0)
                constants.putString(constants.recent_Brush, "", _context);
            else
                constants.putString(constants.recent_Brush, gson.toJson(_recent_list), _context);

        } catch (Exception e) {
            Log.e("TAG", "Exception at setBrush " + e.getMessage());
        }
    }

    public void addInPreference(ArrayList<PatternInfo> _customList) {
        try {
            Gson gson = new Gson();
            Log.e("TAG", "New Json From List" + gson.toJson(_customList));
            if (_customList.size() == 0)
                constants.putString(constants.manual_Brush, "", _context);
            else
                constants.putString(constants.manual_Brush, gson.toJson(_customList), _context);
        } catch (Exception e) {
            Log.e("TAG", "Exception at LN 1685 " + e.getMessage());
        }
    }


}
