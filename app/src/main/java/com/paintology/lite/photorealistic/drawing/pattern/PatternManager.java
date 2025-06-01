package com.paintology.lite.photorealistic.drawing.pattern;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Model.BrushType;
import com.paintology.lite.photorealistic.drawing.Model.PatternInfo;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class PatternManager {
    //    public static final int CHALK = 1;
    private String TAG;
    private Context mContext;
    public int mCurrentColor;
    public int mCurrentID;
    public Bitmap mCurrentPattern;
    public int mCurrentSize;
    public int mHeight;
//    PatternInfo[] mPatternInfoList;

    public ArrayList<PatternInfo> mPatternInfoList;
    Gson gson = new Gson();
    public int[] mPixels;
    public ArrayList<PatternItem> mRawPatternList;
    public int mWidth;

    private final float[] path_SketchyBrush = {1115422720, 1116209152, 1115947008, 1116209152, 1117126656, 1116209152, 1118306304, 1115947008, 1120403456, 1113849856, 1121976320, 1110441984, 1123418112, 1106771968, 1124270080, 1102053376, 1124532224, 1104674816, 1124466688, 1109393408, 1124204544, 1114112000, 1124073472, 1115947008, 1124073472, 1116340224, 1124728832, 1115684864, 1125384192, 1113325568, 1126170624, 1109655552, 1126694912, 1107296256, 1126957056, 1106247680, 1127219200, 1107820544, 1127415808, 1109655552, 1127481344, 1110704128, 1127546880, 1114112000, 1127743488, 1114636288, 1128398848, 1113587712, 1129054208, 1111752704, 1129906176, 1107820544, 1130364928, 1104674816, 1130823680, 1104150528, 1131020288, 1105723392, 1131085824, 1108082688, 1131151360, 1110441984, 1131085824, 1113325568, 1131085824, 1114112000, 1132068864, 1112276992, 1132560384, 1110179840, 1132920832, 1106771968, 1133117440, 1104674816, 1133248512, 1104150528, 1133346816, 1106771968, 1133346816, 1109393408, 1133346816, 1110966272, 1133379584, 1111752704, 1133576192, 1112276992, 1133707264, 1111752704, 1133903872, 1110704128, 1134198784, 1108344832, 1134329856, 1107296256, 1134460928, 1106771968, 1134624768, 1109655552, 1134690304, 1110966272, 1134723072, 1112014848, 1134821376, 1112539136, 1134952448, 1112276992, 1135312896, 1110966272, 1135575040, 1109131264, 1135837184, 1107558400, 1136099328, 1104150528, 1136230400, 1103626240, 1136328704, 1104150528, 1136361472, 1105723392, 1136427008, 1107558400, 1136427008, 1108869120, 1136525312, 1109393408, 1136689152, 1108344832, 1137082368, 1104674816, 1137278976, 1103101952, 1137442816, 1103101952, 1137508352, 1104674816, 1137573888, 1106771968, 1137573888, 1107820544, 1137541120, 1108606976, 1137442816, 1110179840, 1137147904, 1112539136, 1137147904, 1112539136, 1137147904, 1112539136};

    float[] path_SketchyLineBrush = {1104150528, 1116864512, 1106247680, 1116864512, 1108869120, 1116733440, 1110966272, 1116209152, 1112276992, 1115422720, 1113849856, 1113325568, 1115160576, 1111752704, 1117782016, 1108344832, 1119223808, 1104674816, 1120665600, 1101004800, 1121189888, 1099956224, 1121976320, 1100480512, 1123811328, 1102053376, 1124728832, 1104674816, 1125515264, 1108606976, 1125908480, 1109917696, 1126367232, 1110179840, 1126694912, 1109655552, 1126957056, 1108606976, 1127546880, 1105199104, 1128005632, 1102577664, 1128660992, 1100480512, 1129054208, 1100480512, 1129644032, 1101529088, 1130037248, 1101529088, 1130430464, 1101529088, 1131085824, 1100480512, 1131610112, 1098907648, 1132396544, 1094713344, 1132658688, 1092616192, 1132953600, 1094713344, 1133248512, 1098907648, 1133608960, 1099956224, 1134034944, 1101004800, 1134231552, 1099956224, 1134493696, 1096810496, 1134690304, 1095761920, 1135149056, 1094713344, 1135509504, 1095761920, 1135968256, 1095761920, 1136164864, 1099431936, 1136295936, 1101004800, 1136427008, 1102053376};
    StringConstants _constant = new StringConstants();


    float[] path_FurBrush = new float[]{1109393408, 1109393408, 1110179840, 1109655552, 1112539136, 1108869120, 1114636288, 1107820544, 1118044160, 1105723392, 1121583104, 1099956224, 1124728832, 1082130432, 1125384192, 1065353216, 1125580800, 1082130432, 1125449728, 1095761920, 1124466688, 1103101952, 1123549184, 1106247680, 1122893824, 1108606976, 1124139008, 1108869120, 1125056512, 1108082688, 1125908480, 1106247680, 1127743488, 1103101952, 1128857600, 1103101952, 1129381888, 1103626240, 1129512960, 1105723392, 1129185280, 1107558400, 1128595456, 1108344832, 1127481344, 1108082688, 1126039552, 1106247680, 1124597760, 1103626240, 1121058816, 1096810496, 1117650944, 1094713344, 1114112000, 1099431936, 1105199104, 1104674816, 1090519040, 1108869120, 1090519040, 1110966272, 1101529088, 1112014848, 1109393408, 1111228416, 1116995584, 1109131264, 1123418112, 1107820544, 1125908480, 1106771968, 1129185280, 1104150528, 1131085824, 1103626240, 1132462080, 1103626240, 1133314048, 1104150528, 1133805568, 1104674816, 1134100480, 1105723392, 1133871104, 1106247680, 1133051904, 1105723392, 1132527616, 1104150528, 1130233856, 1095761920, 1129578496, -1065353216, 1129185280, -1049624576, 1130299392, -1046478848, 1131872256, -1046478848, 1132756992, -1047003136, 1133805568, -1052770304, 1134231552, -1063256064, 1134428160, 0, 1134624768, 1082130432, 1135017984, 1090519040, 1135542272, 1090519040, 1135968256, 1092616192, 1136295936, 1093664768, 1137147904, 1095761920, 1137737728, 1096810496, 1137999872, 1096810496, 1138524160, 1096810496, 1138917376, 1098907648, 1139212288, 1100480512, 1139146752, 1103626240, 1138753536, 1105199104, 1138130944, 1106771968, 1137606656, 1106771968, 1136164864, 1105199104, 1135083520, 1103626240, 1134329856, 1101529088, 1133281280, 1096810496, 1132855296, 1093664768, 1132625920, 1092616192, 1132986368, 1096810496, 1134264320, 1097859072, 1135673344, 1093664768, 1136459776, 1073741824, 1137410048, -1056964608, 1138196480, -1049624576, 1138655232, -1048576000, 1138982912, -1049624576, 1138556928, -1055916032, 1138196480, -1073741824, 1137770496, 1091567616, 1136590848, 1102053376, 1136001024, 1105199104, 1135017984, 1108606976, 1134624768, 1111490560, 1134329856, 1114898432, 1134297088, 1115684864, 1134460928, 1115422720, 1134755840, 1114636288, 1135411200, 1112801280, 1135771648, 1110704128, 1136066560, 1108344832, 1136099328, 1107558400, 1135968256, 1107296256, 1135149056, 1107558400, 1134460928, 1107820544, 1133019136, 1108869120, 1131544576, 1109917696, 1129906176, 1110704128, 1128464384, 1112276992, 1128267776, 1112539136, 1129381888, 1110441984, 1131806720, 1108082688, 1133838336, 1104150528, 1134624768, 1099956224, 1135575040, 1082130432, 1135706112, 0, 1135247360, 1065353216, 1133969408, 1090519040, 1132756992, 1096810496, 1128923136, 1099431936, 1126432768, 1099431936, 1123811328, 1096810496, 1117913088, 1094713344, 1113325568, 1099431936, 1111228416, 1100480512, 1111228416, 1100480512, 1111228416, 1100480512, 1115947008, 1116471296, 1118044160, 1116078080, 1121714176, 1115815936, 1124925440, 1116340224, 1128267776, 1116995584, 1130627072, 1116995584, 1132986368, 1116471296, 1134002176, 1115160576, 1134690304, 1112801280, 1134690304, 1112801280, 1134690304, 1112801280};

    public PatternManager(Context pContext) {
        mContext = pContext;

        mPatternInfoList = new ArrayList<>();
        initBrush();

        mRawPatternList = new ArrayList();
        TAG = "PatternManager";
//        mPixels = new int[10000];
        mPixels = new int[100000];
//        Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.yuanxing_yingmao_smooth);
        Log.e("PatternManager", "PatternManager");
    }

    public void initBrush() {
        try {
            int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
            int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
            int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;

            int mBrushMaxSize = 0;
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


            Log.e("TAG", "mBrushMaxSize at PatternManager " + mBrushMaxSize);

            PatternInfo _obj_paitology = new PatternInfo(1, mBrushMaxSize, 1, 5, 1.0F, "Paintology Brushes (32)", false, 0, "", BrushType.HeadPaintology, null, false, 0);
            PatternInfo mPatternInfoList_0 = new PatternInfo(642, mBrushMaxSize, 1, 5, 1.0F, "haze dark", false, R.drawable.yuanxing_yingmao_smooth, "", BrushType.PaintologyBrush, null, true, 1);
            PatternInfo mPatternInfoList_1 = new PatternInfo(640, mBrushMaxSize, 1, 5, 1.2F, "haze light", false, R.drawable.yuanxing_yingmao, "", BrushType.PaintologyBrush, null, true, 2);
            PatternInfo mPatternInfoList_2 = new PatternInfo(784, mBrushMaxSize, 1, 5, 1.2F, "mist", false, R.drawable.labi, "", BrushType.PaintologyBrush, null, true, 3);
            PatternInfo mPatternInfoList_3 = new PatternInfo(608, mBrushMaxSize, 1, 5, 2.5F, "land patch", false, R.drawable.flat_wet_water, "", BrushType.PaintologyBrush, null, true, 4);
            PatternInfo mPatternInfoList_4 = new PatternInfo(624, mBrushMaxSize, 1, 5, 1.2F, "grass", false, R.drawable.star_spray, "", BrushType.PaintologyBrush, null, true, 5);
            PatternInfo mPatternInfoList_5 = new PatternInfo(656, mBrushMaxSize, 1, 4, 1.2F, "meadow", false, R.drawable.bianping_yingmao, "", BrushType.PaintologyBrush, null, true, 6);
            PatternInfo mPatternInfoList_6 = new PatternInfo(768, mBrushMaxSize, 1, 5, 1.4F, "industry", false, R.drawable.tansu_2, "", BrushType.PaintologyBrush, null, true, 7);
            PatternInfo mPatternInfoList_7 = new PatternInfo(512, mBrushMaxSize, 1, 5, 1.5F, "chalk", false, R.drawable.chalk, "", BrushType.PaintologyBrush, null, true, 8);
            PatternInfo mPatternInfoList_8 = new PatternInfo(576, mBrushMaxSize, 1, 5, 2.5F, "charcoal", false, R.drawable.charcoal, "", BrushType.PaintologyBrush, null, true, 9);
            PatternInfo mPatternInfoList_9 = new PatternInfo(528, mBrushMaxSize, 1, 5, 1.8F, "sticks", false, R.drawable.dry_oil, "", BrushType.PaintologyBrush, null, true, 10);
            PatternInfo mPatternInfoList_10 = new PatternInfo(592, mBrushMaxSize, 1, 5, 1.3F, "flower", false, R.drawable.colored_wax, "", BrushType.PaintologyBrush, null, true, 11);
            PatternInfo mPatternInfoList_11 = new PatternInfo(560, mBrushMaxSize, 1, 5, 2.5F, "wave", false, R.drawable.oil_wax, "", BrushType.PaintologyBrush, null, true, 12);
            PatternInfo mPatternInfoList_12 = new PatternInfo(559, mBrushMaxSize, 1, 5, 3, "lane", false, R.drawable.new_stroke, "", BrushType.PaintologyBrush, null, true, 13);
//            PatternInfo mPatternInfoList_13 = new PatternInfo(561, 100, 1, 5, 4, "fountain", false, R.drawable.circle_shape, "", BrushType.PaintologyBrush, null, true, 14);
            PatternInfo mPatternInfoList_13 = new PatternInfo(561, mBrushMaxSize, 1, 5, 4, "fountain", false, R.drawable.circle_shape, "", BrushType.PaintologyBrush, null, true, 14);
            PatternInfo mPatternInfoList_14 = new PatternInfo(562, mBrushMaxSize, 1, 5, 2, "streak", false, R.drawable.splatter_pattern, "", BrushType.PaintologyBrush, null, true, 15);
            PatternInfo mPatternInfoList_15 = new PatternInfo(563, mBrushMaxSize, 1, 5, 5, "foliage", false, R.drawable.custom_pattern, "", BrushType.PaintologyBrush, null, true, 16);


//            mBrushDemoItems[0] = new BrushDemoItem(112, R.string.brush_rubber, R.drawable.icon, null, null);
            PatternInfo _obj_rubber = new PatternInfo(112, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_rubber), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 17);

//            mBrushDemoItems[1] = new BrushDemoItem(80, R.string.brush_spray, R.drawable.icon, null, null);
            PatternInfo _obj_spray = new PatternInfo(80, 0, 1, 0, 1.0F, mContext.getResources().getString(R.string.brush_spray), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 18);

//            mBrushDemoItems[2] = new BrushDemoItem(81, R.string.brush_line, R.drawable.icon, null, null);
            PatternInfo _obj_line = new PatternInfo(81, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_line), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 19);

//            mBrushDemoItems[3] = new BrushDemoItem(55, R.string.brush_watercolor, R.drawable.icon, null, null);
            PatternInfo _obj_watercolor = new PatternInfo(55, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_watercolor), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 20);

//            mBrushDemoItems[4] = new BrushDemoItem(272, R.string.brush_sketchsingle, R.drawable.icon, null, path_SketchyBrush);
            PatternInfo _obj_sketchLine = new PatternInfo(272, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_sketchsingle), false, R.drawable.icon, "", BrushType.PaintologyBrush, path_SketchyBrush, false, 21);

//            mBrushDemoItems[5] = new BrushDemoItem(256, R.string.brush_sketch, R.drawable.icon, null, path_SketchyBrush);
            PatternInfo _obj_sketch = new PatternInfo(256, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_sketch), false, R.drawable.icon, "", BrushType.PaintologyBrush, path_SketchyBrush, false, 22);

//            mBrushDemoItems[6] = new BrushDemoItem(64, R.string.brush_sketchline, R.drawable.icon, null, path_SketchyLineBrush);
            PatternInfo _obj_sketch_line = new PatternInfo(64, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_sketchline), false, R.drawable.icon, "", BrushType.PaintologyBrush, path_SketchyLineBrush, false, 23);

//            mBrushDemoItems[7] = new BrushDemoItem(257, R.string.brush_sketchfur, R.drawable.icon, null, path_FurBrush);
            PatternInfo _obj_sketch_fur = new PatternInfo(257, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_sketchfur), false, R.drawable.icon, "", BrushType.PaintologyBrush, path_FurBrush, false, 24);

//            mBrushDemoItems[8] = new BrushDemoItem(96, R.string.brush_emboss, R.drawable.icon, null, null);
            PatternInfo _obj_sketch_emboss = new PatternInfo(96, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_emboss), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 25);

//            mBrushDemoItems[9] = new BrushDemoItem(39, R.string.brush_rainbow, R.drawable.icon, null, null);
            PatternInfo _obj_brush_rainbow = new PatternInfo(39, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_rainbow), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 26);

//            mBrushDemoItems[10] = new BrushDemoItem(56, R.string.brush_inkpen, R.drawable.icon, null, null);
            PatternInfo _obj_brush_inkpen = new PatternInfo(56, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_inkpen), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 27);

//            mBrushDemoItems[11] = new BrushDemoItem(45, R.string.brush_felt, R.drawable.icon, null, null);
            PatternInfo _obj_brush_felt = new PatternInfo(45, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_felt), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 28);

//            mBrushDemoItems[12] = new BrushDemoItem(46, R.string.brush_halo, R.drawable.icon, null, null);
            PatternInfo _obj_brush_halo = new PatternInfo(46, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_halo), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 29);

//            mBrushDemoItems[13] = new BrushDemoItem(47, R.string.brush_outline, R.drawable.icon, null, null);
            PatternInfo _obj_brush_outline = new PatternInfo(47, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_outline), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 30);

//            mBrushDemoItems[14] = new BrushDemoItem(48, R.string.brush_dash_line, R.drawable.icon, null, null);
            PatternInfo _obj_brush_dashline = new PatternInfo(48, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_dash_line), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 31);

//            mBrushDemoItems[15] = new BrushDemoItem(54, R.string.brush_cube_line, R.drawable.icon, null, null);
            PatternInfo _obj_brush_cube_line = new PatternInfo(54, 0, 0, 0, 0, mContext.getResources().getString(R.string.brush_cube_line), false, R.drawable.icon, "", BrushType.PaintologyBrush, null, false, 32);

            String json = "";
            Gson gson = new Gson();
            try {
                json = _constant.getString(_constant.manual_Brush, mContext);
                if (!json.isEmpty()) {
                    PatternInfo _obj_customebrush = new PatternInfo(2, 100, 1, 5, 1.0F, mContext.getResources().getString(R.string.custom_brush) + " (0)", false, R.drawable.icon, "", BrushType.HeadCustom, null, false, 2);
                    Type type = new com.google.gson.reflect.TypeToken<ArrayList<PatternInfo>>() {
                    }.getType();
                    ArrayList<PatternInfo> customeList = gson.fromJson(json, type);
                    if (customeList != null && customeList.size() > 0) {
                        _obj_customebrush.setStrName(mContext.getResources().getString(R.string.custom_brush) + " (" + customeList.size() + ")");
                        mPatternInfoList.add(_obj_customebrush);
                        int index = 1;
                        for (int i = 0; i < customeList.size(); i++) {
                            customeList.get(i)._index = index;
                            customeList.get(i).maxSize = mBrushMaxSize;
                            mPatternInfoList.add(customeList.get(i));
                            index++;
                        }
                    }
                } else {
                    Log.e("TAG", "Custome Brush Not Found");
                }
            } catch (Exception e) {
                Log.e("TAG", "Exception at initBrush " + e.getMessage());
            }

            mPatternInfoList.add(_obj_paitology);
            mPatternInfoList.add(mPatternInfoList_0);
            mPatternInfoList.add(mPatternInfoList_1);
            mPatternInfoList.add(mPatternInfoList_2);
            mPatternInfoList.add(mPatternInfoList_3);
            mPatternInfoList.add(mPatternInfoList_4);
            mPatternInfoList.add(mPatternInfoList_5);
            mPatternInfoList.add(mPatternInfoList_6);
            mPatternInfoList.add(mPatternInfoList_7);
            mPatternInfoList.add(mPatternInfoList_8);
            mPatternInfoList.add(mPatternInfoList_9);
            mPatternInfoList.add(mPatternInfoList_10);
            mPatternInfoList.add(mPatternInfoList_11);
            mPatternInfoList.add(mPatternInfoList_12);
            mPatternInfoList.add(mPatternInfoList_13);
            mPatternInfoList.add(mPatternInfoList_14);
            mPatternInfoList.add(mPatternInfoList_15);
            mPatternInfoList.add(_obj_rubber);
            mPatternInfoList.add(_obj_spray);
            mPatternInfoList.add(_obj_line);
            mPatternInfoList.add(_obj_watercolor);
            mPatternInfoList.add(_obj_sketchLine);
            mPatternInfoList.add(_obj_sketch);
            mPatternInfoList.add(_obj_sketch_line);
            mPatternInfoList.add(_obj_sketch_fur);
            mPatternInfoList.add(_obj_sketch_emboss);
            mPatternInfoList.add(_obj_brush_rainbow);
            mPatternInfoList.add(_obj_brush_inkpen);
            mPatternInfoList.add(_obj_brush_felt);
            mPatternInfoList.add(_obj_brush_halo);
            mPatternInfoList.add(_obj_brush_outline);
            mPatternInfoList.add(_obj_brush_dashline);
            mPatternInfoList.add(_obj_brush_cube_line);


            //setup recent brushesh
            try {
                json = _constant.getString(_constant.recent_Brush, mContext);
                if (!json.isEmpty()) {
                    Type type = new TypeToken<ArrayList<Integer>>() {
                    }.getType();
                    ArrayList<Integer> recentList = gson.fromJson(json, type);
                    int _main_index = 0;
                    ArrayList<PatternInfo> _temp = new ArrayList<>();
                    for (int _recent_index = 0; _recent_index < recentList.size(); _recent_index++) {
                        for (int _list_index = 0; _list_index < mPatternInfoList.size(); _list_index++) {
                            if (recentList.get(_recent_index) == mPatternInfoList.get(_list_index).style) {
                                _main_index = _main_index + 1;
                                PatternInfo _info = mPatternInfoList.get(_list_index);
                                PatternInfo _object = new PatternInfo(_info.style, _info.maxSize, _info.minSize, _info.spacing, _info.alphaScale, _info.strName, _info.isselect, _info.resID, _info._filePath, BrushType.RecentBrush, _info.points, _info.isPatternBrush, _main_index);
                                _object.setBrushDemoImage(null);
                                _temp.add(_object);
                            }
                        }
                    }
                    PatternInfo _obj_recent = new PatternInfo(1, 100, 1, 5, 1.0F, "Recent Brushes (" + _temp.size() + ")", false, R.drawable.icon, "", BrushType.HeadRecent, null, false, 1);
                    mPatternInfoList.add(0, _obj_recent);

                    int recentIndex = 0;
                    for (int i = 0; i < _temp.size(); i++) {
                        recentIndex = recentIndex + 1;
                        mPatternInfoList.add(recentIndex, _temp.get(i));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "Exception at parse " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at BrushPickerActivity " + e.getMessage());
        }
    }


    private void changeToColor(int pInt1, int pInt2, int pInt3) {
        int i = 0;
        int j = Color.red(pInt1);
        int k = Color.green(pInt1);
        int m = Color.blue(pInt1);

        try {
            for (int n = 0; n < pInt3; n++) {
                for (int i1 = 0; i1 < pInt2; i1++) {
                    int i2 = Color.alpha(mPixels[i]);
                    if (i2 != 0) {
                        mPixels[i] = Color.argb(i2, j, k, m);
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Issue at change color " + e.getMessage());
        }
    }

    private PatternInfo getPatternInfo(int pInt) {

//        Log.e("TAG", "getPatternInfo called " + pInt);
        for (int i = 0; i < mPatternInfoList.size(); i++) {
            if (mPatternInfoList.get(i).style == pInt) {
//                writeLogsInFile("Requested Brush ID " + pInt, " Return Brush ID " + mPatternInfoList.get(i).style, "");
//                Log.e("TAG", "getPatternInfo return brush " + mPatternInfoList.get(i).strName + " <> " + mPatternInfoList.get(i).style);
                return mPatternInfoList.get(i);
            }
        }
        String json = _constant.getString(_constant.manual_Brush, mContext);
        Type type = new TypeToken<ArrayList<PatternInfo>>() {
        }.getType();
        ArrayList<PatternInfo> customeList = gson.fromJson(json, type);
        if (customeList != null) {
//            Log.e("TAG", "Size of customeList " + customeList.size());
            for (int i = 0; i < customeList.size(); i++) {
                if (pInt == customeList.get(i).style) {
//                    Log.e("TAG", "getPatternInfo return brush Custom " + mPatternInfoList.get(i).strName + " <> " + mPatternInfoList.get(i).style);
                    return customeList.get(i);
                }
            }
        }

//        writeLogsInFile("Requested Brush ID " + pInt + " Style not found");
        String str1 = TAG;
        String str2 = "style not found " + pInt;
        Log.e(str1, str2);

        return null;
    }

    private Bitmap getRawPattern(int pInt) {
        PatternItem lPatternItem = null;
//        int j = 0;
        Bitmap lBitmap = null;

       /* for (int k = 0; k < mRawPatternList.size(); k++) {
            lPatternItem = (PatternItem) mRawPatternList.get(k);
            if (lPatternItem.id == pInt) {
                return lPatternItem.pattern;
//                j = 1;
            }
        }*/

//        if (j == 0) {
        try {
            PatternInfo pattern_info = getPatternInfo(pInt);
            if (pattern_info != null) {
                if (pattern_info._brushType == BrushType.PaintologyBrush) {
                    lBitmap = BitmapFactory.decodeResource(mContext.getResources(), pattern_info.resID);
                } else {
                    if (pattern_info._filePath != null && !pattern_info._filePath.isEmpty()) {
                        try {
                            File f = new File(pattern_info._filePath);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            lBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                        } catch (Exception e) {
                            Log.e("TAG", "Exception at getRawPattern " + e.getMessage());
//                            writeLogsInFile("GetRaw Pattern Called Style " + pInt + " Exception " + e.toString() + " " + e.getMessage());
                        }
                    } else {
                        lBitmap = BitmapFactory.decodeResource(mContext.getResources(), pattern_info.resID);
                    }
                }
            } else {
                Log.e("TAG", "getPatternInfo found null");
//                writeLogsInFile("GetRaw Pattern Called Style " + pInt + " goto else patternInfo null");
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at get RawPattern " + e.getMessage());
//            writeLogsInFile("GetRaw Pattern Called Style 485 " + pInt + " Exception " + e.toString() + " " + e.getMessage());
        }
//        mRawPatternList.add(new PatternItem(lBitmap, pInt));
//        if (getPatternInfo(pInt)._brushType == BrushType.PaintologyBrush)
//        else if(getPatternInfo(pInt)._brushType == BrushType.CustomeBrush)
//            lBitmap = BitmapFactory.decodeResource(mContext.getResources(), getPatternInfo(pInt).resID);
//        Log.e("TAG", "Image Added in raw patterm " + pInt + " isRecycled " + lBitmap.isRecycled());

//        }
        return lBitmap;
    }

    protected void MyDbgLog(String pString1, String pString2) {
    }

    public void finish() {
        mContext = null;
        mPixels = null;
        Iterator lIterator = mRawPatternList.iterator();
        while (true) {
            if (!lIterator.hasNext()) {
                mRawPatternList.clear();
                mRawPatternList = null;
                mPatternInfoList = null;
                Log.e("TAG", "mPatternInfoList set to null LN194");
                mCurrentPattern = null;
                return;
            }
            PatternItem lPatternItem = (PatternItem) lIterator.next();
            if (lPatternItem.pattern != null) {
                lPatternItem.pattern.recycle();
                lPatternItem.pattern = null;
            }
        }
    }

    public float getAlphaScale(int pInt) {
//        Log.e("TAG", "getPatternInfo called LN 184 " + mPatternInfoList.size());
        return (getPatternInfo(pInt) == null ? 5 : getPatternInfo(pInt).alphaScale);
    }

    public int getMaxSize(int pInt) {
//        Log.e("TAG", "getPatternInfo called LN 189 " + mPatternInfoList.size());
        return (getPatternInfo(pInt) == null ? 125 : getPatternInfo(pInt).maxSize);
    }

    public int getMinSize(int pInt) {
//        Log.e("TAG", "getPatternInfo called LN 194 " + mPatternInfoList.size());
        return (getPatternInfo(pInt) == null ? 10 : getPatternInfo(pInt).minSize);
    }

    public Bitmap getPattern(int pInt1, int pInt2, int pInt3) {

        /*if (mHeight > 100)
            return null;*/

        Bitmap lBitmap1 = null;
        Log.e("TAG", "GetPattern Called Style " + pInt1);
        if (pInt1 == mCurrentID) {
            if (pInt2 == mCurrentSize) {
                if (pInt3 == mCurrentColor) {
                    lBitmap1 = mCurrentPattern;
                    return lBitmap1;
                }
            }
        }

        if (mCurrentPattern != null) {
            mCurrentPattern.recycle();
            mCurrentPattern = null;
        }

        if (pInt1 == mCurrentID) {
            if (pInt2 == mCurrentSize) {
                changeToColor(pInt3, mWidth, mHeight);
                Bitmap lBitmap2 = Bitmap.createBitmap(mPixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);
                mCurrentColor = pInt3;
                mCurrentPattern = lBitmap2;
                lBitmap1 = mCurrentPattern;
                return lBitmap1;
            }
        }
        Bitmap lBitmap3 = getRawPattern(pInt1);
        mWidth = pInt2;
//        try {
        if (lBitmap3 != null && !lBitmap3.isRecycled()) {
            mHeight = lBitmap3.getHeight() * pInt2 / lBitmap3.getWidth();
            Bitmap lBitmap4 = Bitmap.createScaledBitmap(lBitmap3, mWidth, mHeight, true);
            lBitmap4.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
            lBitmap4.recycle();
        }
        Log.e("TAG", "Brush Logs pInt3 " + pInt3 + " mWidth " + mWidth + " mHeight " + mHeight);
        changeToColor(pInt3, mWidth, mHeight);
        Bitmap lBitmap5 = Bitmap.createBitmap(mPixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCurrentColor = pInt3;
        mCurrentPattern = lBitmap5;
        mCurrentID = pInt1;
        mCurrentSize = pInt2;
        lBitmap1 = mCurrentPattern;
        return lBitmap1;
    }

    public int getPatternId(int pInt) {
        return mPatternInfoList.get(pInt).resID;
    }

    public String getPatternName(int pInt) {
        return mPatternInfoList.get(pInt).strName;
    }

    public boolean getCheck(int pInt) {
        return mPatternInfoList.get(pInt).isselect;
    }

    public boolean SetCheck(int pInt, boolean value) {
        return mPatternInfoList.get(pInt).isselect = true;
    }

    public Bitmap getPatternDemo(int pInt) {
        Log.e("TAG", "GetPattern demo called pInt " + pInt + " Name " + mPatternInfoList.get(pInt).strName);
        return mPatternInfoList.get(pInt).brushDemoImage;
    }


    public void setPatternDemo(int pInt, Bitmap bmp) {
        mPatternInfoList.get(pInt).brushDemoImage = bmp;
    }

    public int getPatternNumber() {
        Log.e("TAG", "getPatternNumber called " + mPatternInfoList.size());
        return mPatternInfoList.size();
    }

    public int getPatternStyle(int pInt) {
        return mPatternInfoList.get(pInt).style;
    }

    public int getSpacing(int pInt1, int pInt2) {
//        Log.e("TAG", "getPatternInfo called LN 281");
        return (getPatternInfo(pInt1) == null ? 5 : getPatternInfo(pInt1).spacing);
    }


    class PatternItem {
        public int id;
        public Bitmap pattern;

        public PatternItem(Bitmap pInt, int arg3) {
            pattern = pInt;
            id = arg3;
        }
    }

    public void writeLogsInFile(String brushLogs) {
        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/Logs");
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Paintology/Logs/_brush_selection.txt");
        try {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    Log.e("TAG", "writeLogsInFile Created file path " + file.getAbsolutePath());
                }
                OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, true));
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);
                String Log = getDateTime() + " " + brushLogs;
                buffered_writer.write(Log + "\n");
                buffered_writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "writeLogsInFile IOException at writeLogs " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e("TAG", "writeLogsInFile Exception at writeLogs " + e.getMessage());
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
