package com.paintology.lite.photorealistic.drawing.brushpicker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Adapter.BrushListAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.DrawingApp.CreateBrushActivity;
import com.paintology.lite.photorealistic.drawing.Model.BrushDemoItem;
import com.paintology.lite.photorealistic.drawing.Model.PatternInfo;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.brushsetting.BrushSetting;
import com.paintology.lite.photorealistic.drawing.brushsetting.BrushSettingManager;
import com.paintology.lite.photorealistic.drawing.colorpicker.RandomColorPicker;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.gallery.BrushSettingTextView;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util._inteface_brushpicker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;


public class BrushPickerActivity extends AppCompatActivity implements _inteface_brushpicker {
    float[] path_FurBrush;

    float[] path_SketchyBrush = {1115422720, 1116209152, 1115947008, 1116209152, 1117126656, 1116209152, 1118306304, 1115947008, 1120403456, 1113849856, 1121976320, 1110441984, 1123418112, 1106771968, 1124270080, 1102053376, 1124532224, 1104674816, 1124466688, 1109393408, 1124204544, 1114112000, 1124073472, 1115947008, 1124073472, 1116340224, 1124728832, 1115684864, 1125384192, 1113325568, 1126170624, 1109655552, 1126694912, 1107296256, 1126957056, 1106247680, 1127219200, 1107820544, 1127415808, 1109655552, 1127481344, 1110704128, 1127546880, 1114112000, 1127743488, 1114636288, 1128398848, 1113587712, 1129054208, 1111752704, 1129906176, 1107820544, 1130364928, 1104674816, 1130823680, 1104150528, 1131020288, 1105723392, 1131085824, 1108082688, 1131151360, 1110441984, 1131085824, 1113325568, 1131085824, 1114112000, 1132068864, 1112276992, 1132560384, 1110179840, 1132920832, 1106771968, 1133117440, 1104674816, 1133248512, 1104150528, 1133346816, 1106771968, 1133346816, 1109393408, 1133346816, 1110966272, 1133379584, 1111752704, 1133576192, 1112276992, 1133707264, 1111752704, 1133903872, 1110704128, 1134198784, 1108344832, 1134329856, 1107296256, 1134460928, 1106771968, 1134624768, 1109655552, 1134690304, 1110966272, 1134723072, 1112014848, 1134821376, 1112539136, 1134952448, 1112276992, 1135312896, 1110966272, 1135575040, 1109131264, 1135837184, 1107558400, 1136099328, 1104150528, 1136230400, 1103626240, 1136328704, 1104150528, 1136361472, 1105723392, 1136427008, 1107558400, 1136427008, 1108869120, 1136525312, 1109393408, 1136689152, 1108344832, 1137082368, 1104674816, 1137278976, 1103101952, 1137442816, 1103101952, 1137508352, 1104674816, 1137573888, 1106771968, 1137573888, 1107820544, 1137541120, 1108606976, 1137442816, 1110179840, 1137147904, 1112539136, 1137147904, 1112539136, 1137147904, 1112539136};

    private String TAG = "BrushPickerActivity";
    public Brush mBrush;
    public int mBrushAlpha;
    public int mBrushColor;
    public final BrushDemoItem[] mBrushDemoItems;
    public int mBrushFlow;
    boolean mBrushKidMode;
    //    ListView mBrushListView;
    RecyclerView mBrushListView;
    public BrushListAdapter _list_adapter;
    BrushSettingManager mBrushSettingManager = null;
    public static float mBrushSize;
    public int mBrushStyle;
    Button mCancelButton;
    public Context mContext;
    private boolean mFirstShowUp = true;
    //    View mFlowPanel;
    ConstraintLayout sizeContainer;
    ConstraintLayout densityContainer;
    ConstraintLayout hardnessContainer;
    CustomSeekBar mHardnessPicker;
    private LayoutInflater mInflater;
    static Button mOkButton;
    //    View mOpacityPanel;
    private Painting mPainting;
    public TextView mTxtBrushName;
    //    private PatternIconAdapter mPatternIconAdapter;
    public PatternManager mPatternManager;
    public CustomSeekBar mOpacityPicker;
    private RandomColorPicker mRandomDarkColorPicker;
    SelectedBrushView mSelectedBrushView;
    CustomSeekBar mSizePicker;
    /*private final float[] path_CircleChasisBrush;
    private final float[] path_RibbonBrush;
    public final float[] path_ShadowBrush;*/
    private final float[] path_SketchyLineBrush;

    public TextView mTxtSize;
    private TextView mTxtFlow;
    public TextView mTxtOpacity;

    //    public BrushListAdapter _adapter;
//    ImageView iv_plus, iv_minus;

    public BrushSettingTextView mCustomSize;
    private BrushSettingTextView mCustomFlow;
    private BrushSettingTextView mCustomOpacity;
    public int CREATE_PATTERN = 301;
    public Handler mHandler;


    _inteface_brushpicker _obj_interface;
    //    public int[] mBrushSortList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37};
//    public int[] mBrushSortList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
    public ArrayList<Integer> mBrushSortList = new ArrayList<>();
//    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};


    /*
    public int[] mBrushStyleList = {642, 640, 784, 608, 624, 656, 768, 512, 576, 528, 592, 560, 559, 561, 562, 563,
            112, 80, 81, 55, 272, 256, 64, 257, 96, 39, 56, 45, 46, 47, 48, 54};*/

    public ArrayList<Integer> mBrushStyleList = new ArrayList<>();
    StringConstants _constant = new StringConstants();

    StringConstants constants = new StringConstants();
    int _selectedPid = 0;
    int defaultSelectedPid = 0;
    Gson _gson = new Gson();
    private static final int HARDNESS_MAX_VALUE = 255;
    public static boolean isClickedCancel;
    public static boolean isClickedCreateBrush;
    private PopupWindow mBrushListPopupWindow;
    private int brushSelectedFrameWidth;


    public BrushPickerActivity() {

        path_FurBrush = new float[]{1109393408, 1109393408, 1110179840, 1109655552, 1112539136, 1108869120, 1114636288, 1107820544, 1118044160, 1105723392, 1121583104, 1099956224, 1124728832, 1082130432, 1125384192, 1065353216, 1125580800, 1082130432, 1125449728, 1095761920, 1124466688, 1103101952, 1123549184, 1106247680, 1122893824, 1108606976, 1124139008, 1108869120, 1125056512, 1108082688, 1125908480, 1106247680, 1127743488, 1103101952, 1128857600, 1103101952, 1129381888, 1103626240, 1129512960, 1105723392, 1129185280, 1107558400, 1128595456, 1108344832, 1127481344, 1108082688, 1126039552, 1106247680, 1124597760, 1103626240, 1121058816, 1096810496, 1117650944, 1094713344, 1114112000, 1099431936, 1105199104, 1104674816, 1090519040, 1108869120, 1090519040, 1110966272, 1101529088, 1112014848, 1109393408, 1111228416, 1116995584, 1109131264, 1123418112, 1107820544, 1125908480, 1106771968, 1129185280, 1104150528, 1131085824, 1103626240, 1132462080, 1103626240, 1133314048, 1104150528, 1133805568, 1104674816, 1134100480, 1105723392, 1133871104, 1106247680, 1133051904, 1105723392, 1132527616, 1104150528, 1130233856, 1095761920, 1129578496, -1065353216, 1129185280, -1049624576, 1130299392, -1046478848, 1131872256, -1046478848, 1132756992, -1047003136, 1133805568, -1052770304, 1134231552, -1063256064, 1134428160, 0, 1134624768, 1082130432, 1135017984, 1090519040, 1135542272, 1090519040, 1135968256, 1092616192, 1136295936, 1093664768, 1137147904, 1095761920, 1137737728, 1096810496, 1137999872, 1096810496, 1138524160, 1096810496, 1138917376, 1098907648, 1139212288, 1100480512, 1139146752, 1103626240, 1138753536, 1105199104, 1138130944, 1106771968, 1137606656, 1106771968, 1136164864, 1105199104, 1135083520, 1103626240, 1134329856, 1101529088, 1133281280, 1096810496, 1132855296, 1093664768, 1132625920, 1092616192, 1132986368, 1096810496, 1134264320, 1097859072, 1135673344, 1093664768, 1136459776, 1073741824, 1137410048, -1056964608, 1138196480, -1049624576, 1138655232, -1048576000, 1138982912, -1049624576, 1138556928, -1055916032, 1138196480, -1073741824, 1137770496, 1091567616, 1136590848, 1102053376, 1136001024, 1105199104, 1135017984, 1108606976, 1134624768, 1111490560, 1134329856, 1114898432, 1134297088, 1115684864, 1134460928, 1115422720, 1134755840, 1114636288, 1135411200, 1112801280, 1135771648, 1110704128, 1136066560, 1108344832, 1136099328, 1107558400, 1135968256, 1107296256, 1135149056, 1107558400, 1134460928, 1107820544, 1133019136, 1108869120, 1131544576, 1109917696, 1129906176, 1110704128, 1128464384, 1112276992, 1128267776, 1112539136, 1129381888, 1110441984, 1131806720, 1108082688, 1133838336, 1104150528, 1134624768, 1099956224, 1135575040, 1082130432, 1135706112, 0, 1135247360, 1065353216, 1133969408, 1090519040, 1132756992, 1096810496, 1128923136, 1099431936, 1126432768, 1099431936, 1123811328, 1096810496, 1117913088, 1094713344, 1113325568, 1099431936, 1111228416, 1100480512, 1111228416, 1100480512, 1111228416, 1100480512, 1115947008, 1116471296, 1118044160, 1116078080, 1121714176, 1115815936, 1124925440, 1116340224, 1128267776, 1116995584, 1130627072, 1116995584, 1132986368, 1116471296, 1134002176, 1115160576, 1134690304, 1112801280, 1134690304, 1112801280, 1134690304, 1112801280};
        _obj_interface = this::_onclick;
        for (int i = 0; i < 32; i++) {
            mBrushSortList.add(i);
        }

        mBrushStyleList.add(642);
        mBrushStyleList.add(640);
        mBrushStyleList.add(784);
        mBrushStyleList.add(608);
        mBrushStyleList.add(624);
        mBrushStyleList.add(656);
        mBrushStyleList.add(768);
        mBrushStyleList.add(512);
        mBrushStyleList.add(576);
        mBrushStyleList.add(528);
        mBrushStyleList.add(592);
        mBrushStyleList.add(560);
        mBrushStyleList.add(559);
        mBrushStyleList.add(561);
        mBrushStyleList.add(562);
        mBrushStyleList.add(563);


        try {
            String json = _constant.getString(_constant.manual_Brush, MyApplication.getInstance().getApplicationContext());
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<PatternInfo>>() {
                }.getType();
                ArrayList<PatternInfo> customeList = _gson.fromJson(json, type);
                if (customeList != null) {
                    Log.e("TAG", "Size of customeList " + customeList.size());
                    for (int i = 0; i < customeList.size(); i++) {
                        mBrushStyleList.add(customeList.get(i).style);
                        mBrushSortList.add(mBrushSortList.size() - 1);
                    }
                }
            }
        } catch (Exception e) {

        }

        mBrushStyleList.add(112);
        mBrushStyleList.add(80);
        mBrushStyleList.add(81);
        mBrushStyleList.add(55);
        mBrushStyleList.add(272);
        mBrushStyleList.add(256);
        mBrushStyleList.add(64);
        mBrushStyleList.add(257);
        mBrushStyleList.add(96);
        mBrushStyleList.add(39);
        mBrushStyleList.add(56);
        mBrushStyleList.add(45);
        mBrushStyleList.add(46);
        mBrushStyleList.add(47);
        mBrushStyleList.add(48);
        mBrushStyleList.add(54);


        float[] arrayOfFloat1 = {1104150528, 1116864512, 1106247680, 1116864512, 1108869120, 1116733440, 1110966272, 1116209152, 1112276992, 1115422720, 1113849856, 1113325568, 1115160576, 1111752704, 1117782016, 1108344832, 1119223808, 1104674816, 1120665600, 1101004800, 1121189888, 1099956224, 1121976320, 1100480512, 1123811328, 1102053376, 1124728832, 1104674816, 1125515264, 1108606976, 1125908480, 1109917696, 1126367232, 1110179840, 1126694912, 1109655552, 1126957056, 1108606976, 1127546880, 1105199104, 1128005632, 1102577664, 1128660992, 1100480512, 1129054208, 1100480512, 1129644032, 1101529088, 1130037248, 1101529088, 1130430464, 1101529088, 1131085824, 1100480512, 1131610112, 1098907648, 1132396544, 1094713344, 1132658688, 1092616192, 1132953600, 1094713344, 1133248512, 1098907648, 1133608960, 1099956224, 1134034944, 1101004800, 1134231552, 1099956224, 1134493696, 1096810496, 1134690304, 1095761920, 1135149056, 1094713344, 1135509504, 1095761920, 1135968256, 1095761920, 1136164864, 1099431936, 1136295936, 1101004800, 1136427008, 1102053376};
        path_SketchyLineBrush = arrayOfFloat1;
        /*float[] arrayOfFloat2 = {1110704128, 1110966272, 1111752704, 1110441984, 1115684864, 1109917696, 1118306304, 1109917696, 1120927744, 1109131264, 1124990976, 1107820544, 1127284736, 1106247680, 1128857600, 1104674816, 1130364928, 1104150528, 1131872256, 1104150528, 1133084672, 1105199104, 1134067712, 1105723392, 1134559232, 1105723392, 1135476736, 1105723392, 1136001024, 1105723392, 1136459776, 1105723392, 1137016832, 1105199104, 1137770496, 1104674816, 1138130944, 1104150528, 1138753536, 1103626240, 1138753536, 1103626240, 1138753536, 1103626240};
        path_CircleChasisBrush = arrayOfFloat2;
        float[] arrayOfFloat3 = {1106771968, 1117782016, 1107820544, 1117782016, 1111228416, 1117913088, 1113587712, 1117650944, 1117126656, 1116995584, 1119485952, 1115160576, 1121976320, 1109131264, 1124204544, 1103101952, 1124597760, 1097859072, 1124335616, 1090519040, 1122107392, 1090519040, 1120141312, 1097859072, 1115947008, 1105199104, 1113587712, 1108869120, 1113063424, 1114112000, 1114374144, 1116471296, 1116995584, 1117519872, 1120403456, 1118044160, 1122369536, 1117782016, 1124663296, 1117257728, 1125908480, 1116078080, 1126367232, 1115422720, 1126760448, 1115815936, 1126957056, 1116471296, 1126957056, 1116471296, 1126957056, 1116471296, 1111490560, 1118044160, 1110179840, 1118175232, 1106771968, 1118044160, 1103101952, 1117519872, 1107296256, 1116602368, 1110704128, 1113849856, 1117126656, 1108082688, 1121320960, 1103626240, 1123811328, 1103626240, 1124728832, 1105199104, 1124990976, 1108869120, 1124401152, 1112539136, 1121845248, 1116602368, 1120403456, 1117388800, 1119223808, 1117388800, 1118961664, 1116733440, 1120010240, 1114636288, 1122893824, 1108606976, 1124859904, 1105199104, 1126236160, 1103626240, 1126825984, 1105723392, 1127022592, 1109917696, 1126760448, 1114112000, 1126367232, 1116471296, 1125646336, 1117519872, 1125187584, 1117519872, 1124859904, 1116602368, 1125253120, 1113325568, 1126498304, 1107558400, 1127809024, 1103101952, 1128923136, 1101004800, 1129906176, 1103101952, 1130102784, 1105723392, 1129709568, 1110179840, 1129054208, 1113587712, 1128398848, 1115422720, 1127940096, 1115815936, 1128136704, 1114374144, 1129381888, 1109131264, 1130496000, 1106771968, 1132265472, 1103626240, 1132658688, 1103626240, 1132789760, 1106247680, 1132691456, 1108082688, 1132068864, 1110966272, 1131282432, 1112276992, 1130692608, 1112801280, 1130496000, 1112014848, 1130823680, 1109917696, 1132199936, 1105723392, 1132789760, 1102053376, 1133346816, 1100480512, 1133936640, 1102053376, 1134034944, 1104150528, 1133805568, 1107558400, 1133445120, 1108869120, 1133215744, 1109393408, 1133576192, 1107296256, 1134100480, 1104150528, 1134919680, 1099956224, 1135575040, 1100480512, 1135935488, 1102577664, 1136001024, 1106247680, 1135837184, 1108082688, 1135575040, 1109131264, 1135378432, 1109393408, 1135706112, 1107558400, 1136099328, 1104674816, 1136656384, 1101529088, 1137573888, 1101004800, 1137868800, 1103101952, 1137967104, 1105723392, 1137901568, 1108344832, 1137803264, 1110966272, 1137639424, 1112539136, 1137278976, 1113849856, 1136623616, 1115160576, 1136197632, 1115684864, 1135673344, 1115684864, 1134690304, 1114636288, 1134034944, 1113587712, 1133674496, 1112801280, 1133477888, 1112276992, 1134362624, 1111752704, 1135247360, 1111752704, 1136066560, 1110704128, 1137442816, 1108606976, 1138360320, 1106247680, 1138982912, 1104674816, 1139376128, 1103626240, 1139507200, 1102577664, 1138884608, 1101004800, 1137934336, 1099431936, 1136656384, 1097859072, 1135869952, 1097859072, 1135050752, 1096810496, 1134231552, 1099431936, 1133740032, 1101004800, 1133445120, 1103626240, 1134100480, 1105723392, 1134788608, 1106771968, 1135673344, 1107296256, 1136754688, 1107820544, 1137475584, 1107820544, 1137934336, 1107820544, 1138032640, 1108344832, 1137967104, 1109655552, 1137836032, 1111752704, 1137180672, 1115422720, 1136525312, 1116340224, 1135902720, 1116471296, 1134788608, 1116209152, 1134231552, 1115947008, 1133576192, 1115947008, 1133379584, 1116078080, 1134657536, 1116995584, 1135640576, 1117257728, 1136361472, 1117388800, 1137246208, 1117126656, 1137410048, 1116864512, 1137049600, 1116733440, 1136394240, 1116864512, 1134854144, 1117126656, 1133608960, 1116995584, 1132331008, 1116864512, 1129316352, 1116602368, 1126891520, 1116209152, 1125580800, 1115947008, 1123024896, 1115947008, 1120796672, 1115947008, 1118961664, 1116078080, 1116209152, 1116078080, 1113849856, 1115947008, 1111752704, 1115815936, 1110704128, 1115422720, 1110179840, 1114636288, 1109131264, 1113587712, 1107820544, 1112276992, 1105723392, 1111228416, 1106771968, 1109917696, 1109655552, 1108606976, 1114112000, 1106247680, 1120403456, 1101529088, 1124401152, 1094713344, 1126367232, 1082130432, 1129054208, 1073741824, 1131085824, 1090519040, 1132003328, 1092616192, 1132265472, 1094713344, 1132003328, 1094713344, 1130299392, 1099431936, 1127415808, 1101529088, 1125711872, 1102577664, 1123680256, 1102053376, 1122762752, 1101529088, 1124990976, 1101004800, 1127940096, 1101529088, 1129709568, 1102577664, 1132494848, 1102577664, 1133477888, 1101529088, 1134297088, 1101004800, 1135411200, 1101004800, 1136033792, 1101529088, 1136951296, 1102577664, 1137508352, 1103626240, 1137836032, 1103626240, 1138229248, 1104150528, 1138491392, 1104150528, 1138851840, 1104674816, 1138819072, 1106247680, 1138753536, 1107558400, 1138524160, 1109393408, 1138229248, 1112801280, 1138032640, 1114898432, 1137836032, 1115947008, 1137082368, 1116471296, 1136132096, 1116602368, 1135312896, 1116340224, 1134559232, 1115815936, 1133641728, 1114636288, 1132134400, 1112539136, 1130430464, 1112014848, 1128529920, 1113063424, 1127677952, 1113849856, 1127088128, 1114898432, 1128071168, 1115160576, 1129316352, 1114374144, 1131806720, 1112539136, 1133117440, 1111228416, 1134559232, 1110179840, 1135607808, 1109917696, 1136951296, 1109131264, 1137672192, 1108344832, 1138065408, 1107820544, 1138458624, 1107296256, 1138229248, 1105199104, 1137737728, 1103626240, 1136361472, 1099431936, 1135378432, 1096810496, 1134460928, 1097859072, 1134297088, 1099431936, 1134395392, 1099956224, 1135411200, 1099956224, 1136328704, 1099956224, 1137639424, 1093664768, 1138032640, 1088421888, 1138130944, 1090519040, 1137868800, 1098907648, 1137082368, 1104150528, 1135640576, 1111228416, 1135050752, 1113849856, 1135640576, 1111490560, 1136918528, 1106771968, 1137737728, 1100480512, 1138262016, 1092616192, 1138524160, 1091567616, 1138458624, 1094713344, 1137410048, 1107558400, 1136754688, 1110966272, 1136525312, 1113063424, 1136721920, 1112801280, 1137344512, 1110441984, 1138032640, 1107558400, 1138229248, 1106247680, 1138130944, 1106771968, 1137770496, 1109655552, 1137246208, 1114112000, 1136885760, 1116471296, 1137475584, 1115160576, 1137770496, 1113063424, 1136328704, 1117126656, 1135738880, 1117782016, 1135640576, 1117782016, 1135935488, 1116602368, 1136263168, 1112276992, 1136525312, 1107558400, 1136328704, 1106771968, 1134985216, 1110179840, 1133805568, 1115422720, 1132494848, 1117913088, 1131806720, 1118437376, 1132789760, 1116471296, 1133445120, 1112276992, 1133740032, 1107820544, 1133379584, 1106771968, 1131872256, 1108082688, 1129906176, 1110441984, 1128071168, 1114112000, 1127809024, 1114374144, 1128202240, 1113063424, 1129644032, 1108606976, 1130430464, 1105723392, 1130496000, 1104150528, 1129644032, 1106247680, 1127022592, 1111752704, 1125711872, 1115160576, 1125187584, 1115947008, 1125384192, 1115815936, 1126694912, 1112539136, 1128857600, 1105723392, 1129185280, 1103101952, 1128857600, 1103101952, 1126498304, 1108082688, 1124204544, 1112801280, 1120272384, 1116602368, 1119748096, 1116995584, 1120534528, 1116471296, 1124270080, 1111228416, 1125646336, 1104674816, 1125384192, 1104150528, 1119092736, 1114112000, 1115684864, 1116995584, 1121976320, 1109131264, 1124728832, 1099431936, 1124794368, 1095761920, 1123549184, 1099431936, 1118175232, 1108082688, 1113849856, 1112539136, 1110179840, 1115684864, 1110966272, 1115160576, 1117257728, 1110179840, 1120403456, 1105723392, 1122631680, 1099431936, 1123155968, 1093664768, 1119748096, 1097859072, 1113587712, 1102577664, 1105199104, 1105199104, 1099956224, 1105723392, 1102577664, 1105199104, 1112539136, 1102053376, 1118175232, 1096810496, 1121189888, 1088421888, 1122369536, 1092616192, 1121058816, 1101004800, 1117257728, 1109917696, 1116864512, 1111490560, 1122893824, 1109655552, 1126694912, 1107820544, 1131020288, 1103626240, 1132494848, 1101004800, 1132691456, 1100480512, 1131544576, 1104150528, 1129381888, 1108606976, 1126760448, 1113325568, 1123155968, 1116995584, 1121976320, 1117519872, 1122762752, 1117388800, 1126105088, 1115815936, 1130627072, 1109393408, 1133051904, 1106247680, 1134100480, 1103626240, 1134592000, 1102577664, 1134460928, 1104150528, 1133248512, 1108344832, 1131675648, 1112801280, 1129709568, 1116078080, 1129512960, 1116471296, 1129840640, 1116471296, 1132462080, 1115160576, 1133903872, 1112014848, 1135542272, 1109393408, 1136066560, 1108082688, 1136295936, 1107820544};
        path_ShadowBrush = arrayOfFloat3;
        float[] arrayOfFloat4 = {1110966272, 1116864512, 1110179840, 1117126656, 1110179840, 1116602368, 1110704128, 1116078080, 1112276992, 1114898432, 1113587712, 1113587712, 1116209152, 1110704128, 1117782016, 1108344832, 1119223808, 1105199104, 1121058816, 1101004800, 1122500608, 1098907648, 1124597760, 1093664768, 1125318656, 1092616192, 1126105088, 1093664768, 1126629376, 1096810496, 1127219200, 1100480512, 1128202240, 1106771968, 1128792064, 1109393408, 1129644032, 1112539136, 1130233856, 1113849856, 1131282432, 1115422720, 1132003328, 1115947008, 1132658688, 1116209152, 1132953600, 1116340224, 1133314048, 1116471296, 1133871104, 1116340224, 1134198784, 1116209152, 1134690304, 1115684864, 1135083520, 1114636288, 1135673344, 1112539136, 1136001024, 1110441984, 1136295936, 1108344832, 1136721920, 1103626240, 1136918528, 1101529088, 1137278976, 1099431936, 1137442816, 1096810496, 1137606656, 1093664768, 1137704960, 1091567616, 1137836032, 1091567616, 1137934336, 1092616192};
        path_RibbonBrush = arrayOfFloat4;*/
        mBrushDemoItems = new BrushDemoItem[16];
        mBrushDemoItems[0] = new BrushDemoItem(112, R.string.brush_rubber, R.drawable.icon, null, null);
        mBrushDemoItems[1] = new BrushDemoItem(80, R.string.brush_spray, R.drawable.icon, null, null);
        mBrushDemoItems[2] = new BrushDemoItem(81, R.string.brush_line, R.drawable.icon, null, null);
        mBrushDemoItems[3] = new BrushDemoItem(55, R.string.brush_watercolor, R.drawable.icon, null, null);
        mBrushDemoItems[4] = new BrushDemoItem(272, R.string.brush_sketchsingle, R.drawable.icon, null, path_SketchyBrush);
        mBrushDemoItems[5] = new BrushDemoItem(256, R.string.brush_sketch, R.drawable.icon, null, path_SketchyBrush);
        mBrushDemoItems[6] = new BrushDemoItem(64, R.string.brush_sketchline, R.drawable.icon, null, path_SketchyLineBrush);
        mBrushDemoItems[7] = new BrushDemoItem(257, R.string.brush_sketchfur, R.drawable.icon, null, path_FurBrush);
        mBrushDemoItems[8] = new BrushDemoItem(96, R.string.brush_emboss, R.drawable.icon, null, null);
        mBrushDemoItems[9] = new BrushDemoItem(39, R.string.brush_rainbow, R.drawable.icon, null, null);
        mBrushDemoItems[10] = new BrushDemoItem(56, R.string.brush_inkpen, R.drawable.icon, null, null);
        mBrushDemoItems[11] = new BrushDemoItem(45, R.string.brush_felt, R.drawable.icon, null, null);
        mBrushDemoItems[12] = new BrushDemoItem(46, R.string.brush_halo, R.drawable.icon, null, null);
        mBrushDemoItems[13] = new BrushDemoItem(47, R.string.brush_outline, R.drawable.icon, null, null);
        mBrushDemoItems[14] = new BrushDemoItem(48, R.string.brush_dash_line, R.drawable.icon, null, null);
        mBrushDemoItems[15] = new BrushDemoItem(54, R.string.brush_cube_line, R.drawable.icon, null, null);

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
            lBrush2.setPatternManager(mPatternManager);
            lBrush2.prepareBrush();
            lBrush2.setSize(10);
            lBrush2.getPaint().setColor(0xFF000000);

            if (lBrush2.mBrushStyle < 512) {
                lPath.moveTo(5, 20);
                lPath.quadTo(55, 1.0F, 110, 20);
                lPath.quadTo(165, 39, 215, 20);
                lBrush2.drawStroke(lCanvas, lPath);
            } else {
                Point lPoint1 = new Point(5, 20);
                Point lPoint2 = new Point(55, 10);
                Point lPoint3 = new Point(110, 20);
//                Log.e("TAG", "drawStroke called 239");
                lBrush2.drawStroke(lCanvas, lPoint1, lPoint2, lPoint3);
                Point lPoint4 = new Point(110, 20);
                Point lPoint5 = new Point(165, 30);
                Point lPoint6 = new Point(215, 20);
                lBrush2.drawStroke(lCanvas, lPoint4, lPoint5, lPoint6);
            }

            lBrush2.endStroke();

            pBrushDemoItem.brushDemoImage = lBitmap;
        } catch (Exception e) {
            Log.e("TAG", "Exception at generateNonSketchyBrushDemoImage " + e.getMessage());
        }
    }


    private BrushDemoItem getBrushDemoItem(int pInt) {


        for (int i = 0; i < mBrushDemoItems.length; i++) {
            Log.e("TAG", "Brush Name at getBrushDemoItem " + mBrushDemoItems[i].brushName);
        }

        for (int i = 0; i < mBrushDemoItems.length; i++) {
            if (mBrushDemoItems[i].brushType == pInt)
                return mBrushDemoItems[i];
        }

        return null;
    }

    private int getPositionOfBrush(int pInt) {
        for (int i = 0; i < mBrushDemoItems.length; i++) {

            if (mBrushDemoItems[i].brushType == pInt)
                return i;
        }
        return -1;
    }

    private void releaseBrushDemoImage() {
        for (int i = 0; i < mBrushDemoItems.length; i++) {
            if ((mBrushDemoItems[i].brushDemoImage != null) && (!mBrushDemoItems[i].brushDemoImage.isRecycled())) {
                mBrushDemoItems[i].brushDemoImage.recycle();
                mBrushDemoItems[i].brushDemoImage = null;
            }
        }
    }


    public void returnWithSelectedBrushRecover() {
        int i, j;

        try {

            saveBrushSetting(mBrush.mBrushStyle);

            for (i = 0; i < mBrushStyleList.size(); i++) {
                if (mBrush.mBrushStyle == mBrushStyleList.get(i)) // .mBrush
                {
                    SharedPreferences sharedPref = getPreferences(0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String colorKeyStr;

                    int style = mBrushStyleList.get(i);
                    int index = mBrushSortList.get(i);

                    for (j = i; j > 0; j--) {
                        mBrushStyleList.set(j, mBrushStyleList.get(j - 1));
//                    mBrushSortList[j] = mBrushSortList[j - 1];
                        mBrushSortList.set(j, mBrushSortList.get(j - 1));
                        colorKeyStr = String.format("brushstyle_%d", j);
                        editor.putInt(colorKeyStr, sharedPref.getInt(String.format("brushstyle_%d", j - 1), mBrushStyleList.get(j - 1)));
                        colorKeyStr = String.format("brushsort_%d", j);
                        editor.putInt(colorKeyStr, sharedPref.getInt(String.format("brushsort_%d", j - 1), mBrushSortList.get(j - 1)));
                    }

                    mBrushStyleList.set(0, style);
                    mBrushSortList.set(0, index);
                    colorKeyStr = String.format("brushstyle_%d", 0);
                    editor.putInt(colorKeyStr, style);
                    colorKeyStr = String.format("brushsort_%d", 0);
                    editor.putInt(colorKeyStr, index);
                    editor.commit();
                    break;
                }
            }
            ((PaintActivity) (BrushPickerActivity.this)).setBrushStyleOnPress();
        } catch (Exception e) {
            Log.e("TAG", "Exceptin at returnbrush " + e.getCause());
        }
        /*
         * Add brush in recent list
         * */


        ///
    }

    public void returnWithSelectedBrush() {
        int i, j;

        try {

            saveBrushSetting(mBrush.mBrushStyle);

            for (i = 0; i < mBrushStyleList.size(); i++) {
                if (mBrush.mBrushStyle == mBrushStyleList.get(i)) // .mBrush
                {
                    SharedPreferences sharedPref = getPreferences(0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String colorKeyStr;

                    int style = mBrushStyleList.get(i);
                    int index = mBrushSortList.get(i);

                    for (j = i; j > 0; j--) {
                        mBrushStyleList.set(j, mBrushStyleList.get(j - 1));
//                    mBrushSortList[j] = mBrushSortList[j - 1];
                        mBrushSortList.set(j, mBrushSortList.get(j - 1));
                        colorKeyStr = String.format("brushstyle_%d", j);
                        editor.putInt(colorKeyStr, sharedPref.getInt(String.format("brushstyle_%d", j - 1), mBrushStyleList.get(j - 1)));
                        colorKeyStr = String.format("brushsort_%d", j);
                        editor.putInt(colorKeyStr, sharedPref.getInt(String.format("brushsort_%d", j - 1), mBrushSortList.get(j - 1)));
                    }

                    mBrushStyleList.set(0, style);
//                mBrushSortList[0] = index;
                    mBrushSortList.set(0, index);
                    colorKeyStr = String.format("brushstyle_%d", 0);
                    editor.putInt(colorKeyStr, style);
                    colorKeyStr = String.format("brushsort_%d", 0);
                    editor.putInt(colorKeyStr, index);
                    editor.commit();
                    break;
                }
            }
            sendBrushEvent();
            ((PaintActivity) (BrushPickerActivity.this)).setBrushStyle(true);
        } catch (Exception e) {
            Log.e("TAG", "Exceptin at returnbrush " + e.getCause());
        }
        /*
         * Add brush in recent list
         * */


        ///
    }


    void sendBrushEvent() {
        int nPatternNum = mPatternManager.getPatternNumber();
        int k = 0;
        String selected_brush = "";
        for (k = 0; k < nPatternNum; k++) {
            if (mPatternManager.getPatternStyle(k) == mBrush.mBrushStyle) {
                mTxtBrushName.setText(mPatternManager.getPatternName(k));
                selected_brush = mPatternManager.getPatternName(k);
                break;
            }
        }

        if (selected_brush.isEmpty()) {
            if (mBrush.mBrushStyle == 55) {
                selected_brush = "watercolor";
            } else if (mBrush.mBrushStyle == 81) {
                selected_brush = "line";
            } else if (mBrush.mBrushStyle == 112) {
                selected_brush = "eraser";
            } else if (mBrush.mBrushStyle == 80) {
                selected_brush = "shade";
            } else if (mBrush.mBrushStyle == 272) {
                selected_brush = "sketch oval";
            } else if (mBrush.mBrushStyle == 256) {
                selected_brush = "sketch fill";
            } else if (mBrush.mBrushStyle == 64) {
                selected_brush = "sketch pen";
            } else if (mBrush.mBrushStyle == 257) {
                selected_brush = "sketch wire";
            } else if (mBrush.mBrushStyle == 96) {
                selected_brush = "emboss";
            } else if (mBrush.mBrushStyle == 39) {
                selected_brush = "rainbow";
            } else if (mBrush.mBrushStyle == 56) {
                selected_brush = "inkpen";
            } else if (mBrush.mBrushStyle == 45) {
                selected_brush = "felt";
            } else if (mBrush.mBrushStyle == 46) {
                selected_brush = "halo";
            } else if (mBrush.mBrushStyle == 47) {
                selected_brush = "outline";
            } else if (mBrush.mBrushStyle == 54) {
                selected_brush = "cube line";
            } else if (mBrush.mBrushStyle == 48) {
                selected_brush = "dash line";
            }
        }

        switch (selected_brush) {
            case "sticks": {
                sendEvent(constants.getPICK_STICKS_BRUSH());
            }
            break;
            case "meadow": {
                sendEvent(constants.getPICK_MEADOW_BRUSH());
            }
            break;
            case "haze light": {
                sendEvent(constants.getPICK_HAZE_LIGHT_BRUSH());
            }
            break;
            case "haze dark": {
                sendEvent(constants.getPICK_HAZE_DARK_BRUSH());
            }
            break;
            case "line": {
                sendEvent(constants.getPICK_LINE_BRUSH());
            }
            break;
            case "mist": {
                sendEvent(constants.getPICK_MIST_BRUSH());
            }
            break;
            case "land patch": {
                sendEvent(constants.getPICK_LAND_PATCH_BRUSH());
            }
            break;
            case "grass": {
                sendEvent(constants.getPICK_GRASS_BRUSH());
            }
            break;
            case "industry": {
                sendEvent(constants.getPICK_INDUSTRY_BRUSH());
            }
            break;
            case "chalk": {
                sendEvent(constants.getPICK_CHALK_BRUSH());
            }
            break;
            case "charcoal": {
                sendEvent(constants.getPICK_CHARCOAL_BRUSH());
            }
            break;

            case "flower": {
                sendEvent(constants.getPICK_FLOWER_BRUSH());
            }
            break;

            case "wave": {
                sendEvent(constants.getPICK_WAVE_BRUSH());
            }
            break;

            case "eraser": {
                sendEvent(constants.getPICK_ERASER_BRUSH());
            }
            break;

            case "shade": {
                sendEvent(constants.getPICK_SHADE_BRUSH());
            }
            break;

            case "watercolor": {
                sendEvent(constants.getPICK_WATERCOLOR_BRUSH());
            }
            break;

            case "sketch oval": {
                sendEvent(constants.getPICK_SKETCH_OVAL_BRUSH());
            }
            break;

            case "sketch fill": {
                sendEvent(constants.getPICK_SKETCH_FILL_BRUSH());
            }
            break;
            case "sketch pen": {
                sendEvent(constants.getPICK_SKETCH_PEN_BRUSH());
            }
            break;
            case "sketch wire": {
                sendEvent(constants.getPICK_SKETCH_WIRE_BRUSH());
            }
            break;
            case "emboss": {
                sendEvent(constants.getPICK_EMBOSS_BRUSH());
            }
            break;
            case "rainbow": {
                sendEvent(constants.BrushRainbow);
            }
            break;
            case "inkpen": {
                sendEvent(constants.BrushInkPen);
            }
            break;
            case "fountain": {
                sendEvent(constants.brush_fountain);
            }
            break;

            case "lane": {
                sendEvent(constants.brush_lane);
            }
            break;
            case "streak": {
                sendEvent(constants.brush_streak);
            }
            break;

            case "foliage": {
                sendEvent(constants.brush_foliage);
            }
            break;
            case "felt": {
                sendEvent(constants.brush_felt);
            }
            break;
            case "halo": {
                sendEvent(constants.brush_halo);
            }
            break;
            case "outline": {
                sendEvent(constants.brush_outline);
            }
            break;
            case "cube line": {
                sendEvent(constants.brush_cube_line);
            }
            break;
            case "dash line": {
                sendEvent(constants.brush_dash_line);
            }
            break;
        }
    }

    void sendEvent(String eventName) {
//        Toast.makeText(this, eventName, Toast.LENGTH_SHORT).show();
        if (BuildConfig.DEBUG) {
            Toast.makeText(BrushPickerActivity.this, eventName, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(BrushPickerActivity.this, eventName);
    }

    private void saveBrushSetting(int pInt) {
        mBrushSettingManager.setBrushOpacity(pInt, mBrushAlpha);
        mBrushSettingManager.setBrushFlow(pInt, mBrushFlow);
        mBrushSettingManager.setBrushSize(pInt, mBrushSize);
        Log.e("TAG", "setSelectedBrush called saveBrushSetting called ");
    }

    public void setSelectedBrush(int pInt) {
        if (mBrush != null) {
            mBrush.release();
            mBrush = null;
        }
        mBrushStyle = pInt;
        mBrush = Brush.createBrush(mBrushStyle);
        mBrush.setRandomColorPicker(mRandomDarkColorPicker);
        mBrush.setPatternManager(mPatternManager);
        mBrush.setColor(mBrushColor);

        //  PaintActivity.obj_interface.brushSetting();

        BrushSetting lBrushSetting = mBrushSettingManager.getSetting(pInt);
        if (lBrushSetting == null) {
//            if (pInt == 561 && mBrushSize > 95)
//                mBrushSize = 96;
            mBrush.setAlpha(mBrushAlpha);
            if (mBrushStyle == 80) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(BrushPickerActivity.this);
                boolean isFirstLaunch = pref.getBoolean("isFirstLaunch", true);
                if (isFirstLaunch) {
                    mBrushFlow = HARDNESS_MAX_VALUE;
                }
            }
            mBrush.mBrushFlow = mBrushFlow;
            mBrush.setSize(mBrushSize);
            saveBrushSetting(pInt);
//            Log.e("TAG", "setSelectedBrush Brush Size if " + mBrushSize);
        } else {
//            if (pInt == 561 && lBrushSetting.size > 95)
//                lBrushSetting.size = 96;
            mBrushAlpha = lBrushSetting.opacity;
            mBrushFlow = lBrushSetting.flow;
            if (mBrushStyle == 80) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(BrushPickerActivity.this);
                boolean isFirstLaunch = pref.getBoolean("isFirstLaunch", true);
                if (isFirstLaunch) {
                    mBrushFlow = HARDNESS_MAX_VALUE;
                }
            }
            mBrushSize = lBrushSetting.size;
            mBrush.setAlpha(lBrushSetting.opacity);
            mBrush.mBrushFlow = lBrushSetting.flow;
            mBrush.setSize(lBrushSetting.size);
//            Log.e("TAG", "setSelectedBrush Brush Size Else " + lBrushSetting.size);
        }
        mBrush.setMode(33);
        mSelectedBrushView.setBrush(mBrush);
        int nPatternNum = BrushPickerActivity.this.mPatternManager.getPatternNumber();
        int i = 0;

        for (i = 0; i < nPatternNum; i++) {
            if (BrushPickerActivity.this.mPatternManager.getPatternStyle(i) == pInt) {
                mTxtBrushName.setText(BrushPickerActivity.this.mPatternManager.getPatternName(i));
                break;
            }
        }

        if (i == nPatternNum) {
            for (int j = 0; j < mBrushDemoItems.length; j++) {
                BrushDemoItem lBrushDemoItem = BrushPickerActivity.this.getBrushDemoItem(pInt);

                if (lBrushDemoItem != null) {
                    mTxtBrushName.setText(lBrushDemoItem.brushName);
                    break;
                }
            }
        }

        if ((mBrush.mBrushStyle >= 256) && (mBrush.mBrushStyle <= 511)) {
            int k = getPositionOfBrush(pInt);
            float[] arrayOfFloat = mBrushDemoItems[k].points;
            mSelectedBrushView.setStrokePoints(arrayOfFloat);
        }

        if (!mBrush.mSupportOpacity) {
            densityContainer.setVisibility(View.GONE);
        } else {
            densityContainer.setVisibility(View.VISIBLE);
        }

        if (!mBrush.mSupportFlow) {
            hardnessContainer.setVisibility(View.GONE);
        } else {
            hardnessContainer.setVisibility(View.VISIBLE);
        }
        setSizeSeekBar();
    }

    public void setSizeSeekBar() {

        setupOpacityPercent();
        setupFlowPercent();
        setupSizePercent();

    }

    protected void resetSeekBars() {

        // Reset Density bar
//        setupOpacityPercent();
//
//        mBrushAlpha = defaultDensityBarAlpha;
//        mOpacityPicker.setProgress(mBrushAlpha);
//        mOpacityPicker.invalidate();
//
//        int percentDensity = defaultDensityBarPercent;
//        String strPercentDensity = percentDensity + "%";
//
//        mTxtOpacity.setText(strPercentDensity);

//        defaultDensityBarSet = false;

        if (oldDefaultDensityBarAlpha == 0 && oldDefaultDensityBarPercent == 0.0) {
            oldDefaultDensityBarAlpha = defaultDensityBarAlpha;
            oldDefaultDensityBarPercent = defaultDensityBarPercent;
        }

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = 3;
            msg.arg1 = (int) oldDefaultDensityBarAlpha;
            msg.obj = oldDefaultDensityBarPercent;
            mHandler.sendMessage(msg);
        }
        // End Reset Density bar

        // Reset Hardness bar
//        setupFlowPercent();
//
//        mBrushFlow = defaultHardnessBarFlow;
//        mHardnessPicker.setProgress(mBrushFlow);
//        mHardnessPicker.invalidate();
//
//        int percent = defaultHardnessBarPercent;
//        String strPercentHardness = percent + "%";
//
//        mTxtFlow.setText(strPercentHardness);

//        defaultHardnessBarSet = false;

        if (oldDefaultHardnessBarFlow == 0 && oldDefaultHardnessBarPercent == 0.0) {
            oldDefaultHardnessBarFlow = defaultHardnessBarFlow;
            oldDefaultHardnessBarPercent = defaultHardnessBarPercent;
        }

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = 2;
            msg.arg1 = (int) oldDefaultHardnessBarFlow;
            msg.obj = oldDefaultHardnessBarPercent;
            mHandler.sendMessage(msg);
        }
        // End Reset Hardness bar

        // reset Size bar
//        setupSizePercent();

//        mSizePicker.setProgress(defaultSizeBarProgress);
//        mSizePicker.invalidate();
//
//        String strPercent = AppUtils.getValueToTwoDecimal(defaultSizeBarPercent) + "%";
//
//        mTxtSize.setText(strPercent);
//        Paintor.obj_interface.setSize();
//
//        defaultSizeBarSet = false;

        if (oldDefaultSizeBarProgress == 0 && oldDefaultSizeBarPercent == 0.0) {
            oldDefaultSizeBarProgress = defaultSizeBarProgress;
            oldDefaultSizeBarPercent = defaultSizeBarPercent;
        }

//        mSizePicker.setProgress(oldDefaultSizeBarProgress);
//        mSizePicker.invalidate();
//
//        String strPercent = AppUtils.getValueToTwoDecimal(oldDefaultSizeBarPercent) + "%";
//
//        mTxtSize.setText(strPercent);
//        Paintor.obj_interface.setSize();

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = 1;
            msg.arg1 = (int) oldDefaultSizeBarProgress;
            msg.obj = oldDefaultSizeBarPercent;
            mHandler.sendMessage(msg);
        }
//        setSelectedBrush(mBrushStyle);

        // end reset Size bar
    }

    private void setupOpacityPercent() {
        if (mBrush.mBrushMaxAlpha < mBrushAlpha)
            mBrushAlpha = mBrush.mBrushMaxAlpha;

//        if (255 < mBrushFlow)
//            mBrushFlow = 255;

//        if (mBrushStyle == 80) {
//            mBrushFlow = HARDNESS_MAX_VALUE / 2;
//        }

        mOpacityPicker.setMax(mBrush.mBrushMaxAlpha);
        mOpacityPicker.setProgress(mBrushAlpha);
        mOpacityPicker.invalidate();

        int percent = mBrushAlpha * 100 / mBrush.mBrushMaxAlpha;
        String strPercent = String.valueOf(percent) + "%";
//        mTxtOpacity.refitText(strPercent, -1, -1);
        mTxtOpacity.setText(strPercent);

        if (!defaultDensityBarSet) {
            defaultDensityBarSet = true;
            defaultDensityBarPercent = percent;
            defaultDensityBarAlpha = mBrushAlpha;

            oldDefaultDensityBarAlpha = defaultDensityBarAlpha;
            oldDefaultDensityBarPercent = defaultDensityBarPercent;
        }
    }

    private void setupFlowPercent() {
        mHardnessPicker.setMax(HARDNESS_MAX_VALUE);
        mHardnessPicker.setProgress(mBrushFlow);
        mHardnessPicker.invalidate();

        int percent = mBrushFlow * 100 / HARDNESS_MAX_VALUE;
        String strPercent = String.valueOf(percent) + "%";
//        mTxtFlow.refitText(strPercent, -1, -1);
        mTxtFlow.setText(strPercent);

        if (!defaultHardnessBarSet) {
            defaultHardnessBarSet = true;
            defaultHardnessBarPercent = percent;
            defaultHardnessBarFlow = mBrushFlow;

            oldDefaultHardnessBarFlow = defaultHardnessBarFlow;
            oldDefaultHardnessBarPercent = defaultHardnessBarPercent;
        }
    }

    private void setupSizePercent() {
        float f1 = 0;
//        if (mBrush.mBrushStyle == 561)
//            f1 = 96;
//        else
        f1 = mBrush.mBrushMaxSize;
        float f2 = mBrush.mBrushMinSize;
        //  int m = (int) ((f1 - f2) * 10.0F);
        int m = (int) ((f1) * 10.0F);

//        m = 970;
        mSizePicker.setMax(m);
        float f3 = mBrush.mBrushSize;
        //float f3 = (mBrush.mBrushSize*m)/100;
        float f4 = mBrush.mBrushMinSize;

        //  float n = (float) ((f3 - f4) * 10.0F);
        float n = (float) ((f3) * 10.0F);
        if (m < n)
            n = m;

        //  float n = f3;

        Log.e("brushsize", "SetMax at Picker " + " f1 " + f1 + " m " + m + " n " + n + " f3 " + f3 + " f4 " + f4);
        mSizePicker.setProgress((int) n);
        mSizePicker.invalidate();

        float percent = (n * 100 / m);
        if (percent == 0.00) {
            percent = (mBrush.mBrushMinSize / mBrush.mBrushMaxSize) * 100;
        }

        if (!defaultSizeBarSet) {
            defaultSizeBarSet = true;
            defaultSizeBarPercent = percent;
            defaultSizeBarProgress = (int) n;

            oldDefaultSizeBarProgress = defaultSizeBarProgress;
            oldDefaultSizeBarPercent = defaultSizeBarPercent;

        }
//        String strPercent = String.format("%.1f", Math.floor(percent * 100) / 100) + "%";
//        String strPercent = Math.floor(percent * 100) / 100 + "%";

        String strPercent = AppUtils.getValueToTwoDecimal(percent) + "%";
//        String strPercent = String.valueOf(n * 100 / m) + "%";
//        mTxtSize.refitText(strPercent, -1, -1);
        Log.e("brushsize", "Get Size SeekBar Progress N " + n + " strPercent " + strPercent);
        try {
            mTxtSize.setText(strPercent);
            PaintActivity.obj_interface.setSize();
        } catch (Exception e) {

        }
    }

    private void setupPatternGrid() {
    }

    int _progress = 0;
    float _percent = 0;


    public SwitchCompat _switch_line, switch_singleTap;
    public boolean defaultSwitchLineStatus;
    public boolean defaultSwitchSingleTapStatus;
    public boolean defaultSwitchGrayScaleStatus;

    public boolean defaultSizeBarSet;
    public float defaultSizeBarProgress;
    public float defaultSizeBarPercent;
    public float oldDefaultSizeBarProgress = 0;
    public float oldDefaultSizeBarPercent = 0.0f;

    public boolean defaultHardnessBarSet;
    public int defaultHardnessBarPercent;
    public int defaultHardnessBarFlow;
    public int oldDefaultHardnessBarPercent = 0;
    public int oldDefaultHardnessBarFlow = 0;

    public boolean defaultDensityBarSet;
    public int defaultDensityBarPercent;
    public int defaultDensityBarAlpha;
    public int oldDefaultDensityBarPercent = 0;
    public int oldDefaultDensityBarAlpha = 0;

//    public boolean defaultSwitchSmoothStatus;
//    public LinearLayout smooth_container;
//    public SwitchCompat _switch_smooth;
//    SeekBar _seekbar_smooth_1, _seekbar_smooth_2, seek_smoothing;
//    TextView _tv_smooth, tv_smooth_2, tv_smoothing_progress;

    Button ll_create_brush, view_create_brush;


//    EditText edt_1, edt_2, edt_smoothing;

    public float TOUCH_TOLERANCE = 0.1f;
    public float TOUCH_TOLERANCE_1 = 0.5f;
//    public float smoothing = 0f;

    public void onInit() {
        mContext = this;

        mHandler = new Handler() {
            public void handleMessage(Message pMessage) {
                try {
                    int progress = pMessage.arg1;
//                    int percent = pMessage.arg2;
                    float percent = Float.valueOf(pMessage.obj.toString());
                    _progress = progress;
                    _percent = percent;
                    Log.e("TAG", " OnInit progress " + progress + " percent " + percent + " pMessage.what " + pMessage.what);
                    String strPercent = "";

                    switch (pMessage.what) {
                        case 1:
                            defaultSizeBarPercent = _percent;
                            defaultSizeBarProgress = _progress;

                            if (mBrush.mBrushStyle != 51) {
                                if (_percent <= 0.0) {
                                    mBrushSize = mBrush.mBrushMinSize * 10.0F;
                                } else {
                                    mBrushSize = (progress * 1.0F / 10.0F) +
                                            mBrush.mBrushMinSize;
                                }
                                Log.e("TAG", "Brush Size at onInit " + mBrushSize + " _percent " + _percent + " progress " + progress);
                                mBrush.setSize(mBrushSize);
                            } else {
                                mBrush.setSize(mBrushSize);
                            }
//                            saveBrushSetting(mBrush.mBrushStyle);
                            strPercent = AppUtils.getValueToTwoDecimal(_percent) + "%";
//                        mTxtSize.refitText(strPercent, -1, -1);
                            mTxtSize.setText(strPercent);
                            PaintActivity.obj_interface.setSize();
                            mSelectedBrushView.setBrush(mBrush);
                            mSelectedBrushView.invalidate();

//                            } else {
//                                Log.e("TAG", "Brush null in handler case 1 ");
//                            }
                            break;
                        case 2:
                            defaultHardnessBarPercent = (int) _percent;
                            defaultHardnessBarFlow = _progress;

                            if (mBrush.mBrushStyle != 112) {
                                strPercent = AppUtils.getValueToTwoDecimal(_percent) + "%";
//                            mTxtFlow.refitText(strPercent, -1, -1);
                                mTxtFlow.setText(strPercent);

                                mBrushFlow = progress;
                                mBrush.mBrushFlow = progress;
                                mSelectedBrushView.setBrush(mBrush);
                                mSelectedBrushView.invalidate();
                            }

                            if (mBrushStyle == 80) {
                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(BrushPickerActivity.this);
                                boolean isFirstLaunch = pref.getBoolean("isFirstLaunch", true);
                                if (isFirstLaunch) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("isFirstLaunch", false);
                                    editor.apply();
                                }
                            }

//                            saveBrushSetting(mBrush.mBrushStyle);

                            break;
                        case 3:
                            defaultDensityBarPercent = (int) _percent;
                            defaultDensityBarAlpha = _progress;

                            if (mBrush.mBrushStyle != 112) {
                                strPercent = AppUtils.getValueToTwoDecimal(_percent) + "%";
//                            mTxtOpacity.refitText(strPercent, -1, -1);
                                mTxtOpacity.setText(strPercent);
                                mBrushAlpha = progress;
                                mBrush.mBrushAlphaValue = progress;
                                mSelectedBrushView.setBrush(mBrush);
                                mSelectedBrushView.invalidate();
                                Log.e("TAG", "BrushAlpha Value at Handler " + mBrushAlpha);
                            }
//                            saveBrushSetting(mBrush.mBrushStyle);

                            break;
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at onHandler " + e.getMessage());
                }
                super.handleMessage(pMessage);
            }
        };

        LayoutInflater lLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mInflater = lLayoutInflater;
        RandomColorPicker.ColorPref lColorPref = RandomColorPicker.ColorPref.DARK_COLOR;
        RandomColorPicker lRandomColorPicker = new RandomColorPicker(32, lColorPref);
        mRandomDarkColorPicker = lRandomColorPicker;
        PatternManager lPatternManager = new PatternManager(BrushPickerActivity.this);
        mPatternManager = lPatternManager;
        mOkButton = (Button) PaintActivity.brushDialogView.findViewById(R.id.btn_ok);
        mCancelButton = (Button) PaintActivity.brushDialogView.findViewById(R.id.btn_cancel);
        SelectedBrushView lSelectedBrushView1 = (SelectedBrushView) PaintActivity.brushDialogView.findViewById(R.id.brush_selected);
        mSelectedBrushView = lSelectedBrushView1;

        View brushSelectedFrame = PaintActivity.brushDialogView.findViewById(R.id.brush_selected_frame);
        brushSelectedFrame.setOnClickListener(view -> {
            PaintActivity.brushSettingsPopup.dismiss();
            showBrushList(PaintActivity.mBrushStyleBtn);
        });

        brushSelectedFrame.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        brushSelectedFrameWidth = brushSelectedFrame.getMeasuredWidth();
                    }
                });

        PaintActivity.brushDialogView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        brushSelectedFrameWidth = PaintActivity.brushDialogView.getMeasuredWidth();
                    }
                });

        TextView tvSelectBrush = PaintActivity.brushDialogView.findViewById(R.id.tv_select_brush);
        tvSelectBrush.setOnClickListener(view -> {
            PaintActivity.brushSettingsPopup.dismiss();
            showBrushList(PaintActivity.mBrushStyleBtn);
//            showBrushList(brushSelectedFrame);
        });

        mOpacityPicker = PaintActivity.brushDialogView.findViewById(R.id.opacity_picker);
        mOpacityPicker.setMax(100);
        mOpacityPicker.setHandler(mHandler, 3);
//        mOpacityPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mBrush.mBrushStyle != 112) {
////                    String strPercent = String.valueOf(progress) + "%";
//////                            mTxtOpacity.refitText(strPercent, -1, -1);
////                    mTxtOpacity.setText(strPercent);
//                    mBrushAlpha = progress;
//                    mBrush.mBrushAlphaValue = progress;
//                    mSelectedBrushView.setBrush(mBrush);
//                    mSelectedBrushView.invalidate();
//                    Log.e("TAG", "BrushAlpha Value at Handler " + mBrushAlpha);
//
//                    setupOpacityPercent();
//                }
//                saveBrushSetting(mBrush.mBrushStyle);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        densityContainer = PaintActivity.brushDialogView.findViewById(R.id.density_container);
        hardnessContainer = PaintActivity.brushDialogView.findViewById(R.id.hardness_container);
        mHardnessPicker = PaintActivity.brushDialogView.findViewById(R.id.hardness_seeker);
        mHardnessPicker.setMax(100);
        mHardnessPicker.setHandler(mHandler, 2);
//        mHardnessPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                if (mHandler != null) {
////                    Message msg = mHandler.obtainMessage();
////
////                    msg.what = 2;
////                    msg.arg1 = progress;
////                    msg.arg2 = (progress * 100 / HARDNESS_MAX_VALUE);
////                    mHandler.sendMessage(msg);
////                }
//
//                if (mBrushStyle == 80) {
//                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(BrushPickerActivity.this);
//                    boolean isFirstLaunch = pref.getBoolean("isFirstLaunch", true);
//                    if (isFirstLaunch) {
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putBoolean("isFirstLaunch", false);
//                        editor.apply();
//                    }
//                }
//
//                if (mBrush.mBrushStyle != 112) {
////                    String strPercent = String.valueOf(progress) + "%";
//////                            mTxtFlow.refitText(strPercent, -1, -1);
////                    mTxtFlow.setText(strPercent);
//
//                    mBrushFlow = progress;
//                    mBrush.mBrushFlow = progress;
//                    mSelectedBrushView.setBrush(mBrush);
//                    mSelectedBrushView.invalidate();
//
//                    setupFlowPercent();
//                }
//                saveBrushSetting(mBrush.mBrushStyle);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        mSizePicker = PaintActivity.brushDialogView.findViewById(R.id.size_picker);
        mSizePicker.setMax(100);
        mSizePicker.setHandler(mHandler, 1);
//        mSizePicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mBrush.mBrushStyle != 51) {
//                    mBrushSize = (progress * 1.0F / 10.0F) +
//                            mBrush.mBrushMinSize;
//                    Log.e("TAG", "Brush Size at onInit " + mBrushSize + " _percent " + _percent + " progress " + progress);
//                }
//                mBrush.setSize(mBrushSize);
//                saveBrushSetting(mBrush.mBrushStyle);
//
////                String strPercent = String.format("%.2f", mBrush.mBrushSize);
////                String strPercent = String.valueOf(progress) + "%";
//////                        mTxtSize.refitText(strPercent, -1, -1);
////                mTxtSize.setText(strPercent);
//
//                setupSizePercent();
//
//                Paintor.obj_interface.setSize();
//                mSelectedBrushView.setBrush(mBrush);
//                mSelectedBrushView.invalidate();
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        TextView plus_size = (TextView) PaintActivity.brushDialogView.findViewById(R.id.plus_size);
        TextView minus_size = (TextView) PaintActivity.brushDialogView.findViewById(R.id.minus_size);

        plus_size.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Log.e("TAG", "Plus Icon Click mBrush.mBrushSize Before " + mBrush.mBrushSize + " " + _progress);
                    mBrush.mBrushSize++;
                    if (mBrush.mBrushStyle != 51) {
                        mBrushSize = mBrush.mBrushSize;
                        mBrush.setSize(mBrushSize);
                    } else {
                        mBrush.setSize(mBrushSize);
                    }
                    saveBrushSetting(mBrush.mBrushStyle);

//                    String strPercent = String.format("%.2f", mBrush.mBrushSize);
//                    String strPercent = String.valueOf((int) mBrush.mBrushSize) + "%";
//                        mTxtSize.refitText(strPercent, -1, -1);
//                    mTxtSize.setText(strPercent);

                    setupSizePercent();

                    PaintActivity.obj_interface.setSize();
                    mSelectedBrushView.setBrush(mBrush);
                    mSelectedBrushView.invalidate();

//                    setSizeSeekBar();
                    Log.e("TAG", "Plus Icon Click mBrush.mBrushSize After " + mBrush.mBrushSize);
//                    if (!isLongClicked)
//                    {
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(getApplicationContext(), constants.canvas_brush_plus_click, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_plus_click);
//                    }
                } catch (Exception e) {
                    Log.e("Increase bush size", e.getMessage());
                }
            }
        });

        minus_size.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e("TAG", "Plus Icon Click mBrush.mBrushSize Before " + mBrush.mBrushSize);
                    if (mBrush.mBrushSize > 1) {
                        mBrush.mBrushSize--;
                        if (mBrush.mBrushStyle != 51) {
                            mBrushSize = mBrush.mBrushSize;
                            mBrush.setSize(mBrushSize);
                        } else {
                            mBrush.setSize(mBrushSize);
                        }
                        saveBrushSetting(mBrush.mBrushStyle);

//                        String strPercent = String.format("%.2f", mBrush.mBrushSize)+ "%";
//                        String strPercent = String.valueOf((int) mBrush.mBrushSize) + "%";
//                        mTxtSize.refitText(strPercent, -1, -1);
//                        mTxtSize.setText(strPercent);

                        setupSizePercent();

                        PaintActivity.obj_interface.setSize();
                        mSelectedBrushView.setBrush(mBrush);
                        mSelectedBrushView.invalidate();
//                        setSizeSeekBar();
                    }
//                    if (!isLongClicked)
//                    {
//                        if (BuildConfig.DEBUG){
//                            Toast.makeText(getApplicationContext(), constants.canvas_brush_minus_click, Toast.LENGTH_SHORT).show();
//                        }
//                        FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_minus_click);
//                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at iv_minus " + e.getMessage());
                }
            }
        });

        mTxtFlow = (TextView) PaintActivity.brushDialogView.findViewById(R.id.txtFlow);
        mTxtOpacity = (TextView) PaintActivity.brushDialogView.findViewById(R.id.txtOpacity);

        TextView plus_density = (TextView) PaintActivity.brushDialogView.findViewById(R.id.plus_pressure);
        TextView minus_density = (TextView) PaintActivity.brushDialogView.findViewById(R.id.minus_pressure);

        plus_density.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBrush.mBrushStyle != 112) {
                    mBrushAlpha += 1;
                    mBrush.mBrushAlphaValue += 1;
                    mSelectedBrushView.setBrush(mBrush);
                    mSelectedBrushView.invalidate();
                    Log.e("TAG", "BrushAlpha Value at Handler " + mBrushAlpha);

                    setupOpacityPercent();
                }
                saveBrushSetting(mBrush.mBrushStyle);
            }
        });

        minus_density.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBrush.mBrushStyle != 112) {
                    mBrushAlpha -= 1;
                    mBrush.mBrushAlphaValue -= 1;
                    mSelectedBrushView.setBrush(mBrush);
                    mSelectedBrushView.invalidate();
                    Log.e("TAG", "BrushAlpha Value at Handler " + mBrushAlpha);

                    setupOpacityPercent();
                }
                saveBrushSetting(mBrush.mBrushStyle);
            }
        });

        TextView plus_hardness = (TextView) PaintActivity.brushDialogView.findViewById(R.id.plus_hardness);
        TextView minus_hardness = (TextView) PaintActivity.brushDialogView.findViewById(R.id.minus_hardness);

        plus_hardness.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBrush.mBrushStyle != 112) {
                    mBrushFlow += 1;
                    mBrush.mBrushFlow += 1;
                    mSelectedBrushView.setBrush(mBrush);
                    mSelectedBrushView.invalidate();

                    setupFlowPercent();
                }
                saveBrushSetting(mBrush.mBrushStyle);
            }
        });

        minus_hardness.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBrush.mBrushStyle != 112) {
                    mBrushFlow -= 1;
                    mBrush.mBrushFlow -= 1;
                    mSelectedBrushView.setBrush(mBrush);
                    mSelectedBrushView.invalidate();

                    setupFlowPercent();
                }
                saveBrushSetting(mBrush.mBrushStyle);
            }
        });

        try {

            SharedPreferences sharedPref = getSharedPreferences("brush", 0);
            boolean singleTapChecked = sharedPref.getBoolean("singleTap", false);

            switch_singleTap = (SwitchCompat) PaintActivity.brushDialogView.findViewById(R.id.switch_singleTap);
            switch_singleTap.setChecked(singleTapChecked);
            defaultSwitchSingleTapStatus = singleTapChecked;
            PaintActivity.obj_interface.setSpecialFunctionState(switch_singleTap);

            switch_singleTap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences sharedPref = getSharedPreferences("brush", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putBoolean("singleTap", isChecked);
                    editor.apply();

                    if (isChecked) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), constants.canvas_single_tap_on, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_single_tap_on);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), constants.canvas_single_tap_off, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_single_tap_off);
                    }
                    PaintActivity.obj_interface.setSpecialFunctionState(switch_singleTap);
                }
            });

            boolean lineChecked = sharedPref.getBoolean("line", false);

            _switch_line = (SwitchCompat) PaintActivity.brushDialogView.findViewById(R.id.switch_line);
            _switch_line.setChecked(lineChecked);
            defaultSwitchLineStatus = lineChecked;
            PaintActivity.obj_interface.setSpecialFunctionState(_switch_line);


//            edt_1 = (EditText) Paintor.brushDialogView.findViewById(R.id.edt_1);
//            edt_2 = (EditText) Paintor.brushDialogView.findViewById(R.id.edt_2);
//            edt_smoothing = (EditText) Paintor.brushDialogView.findViewById(R.id.edt_smooth);
//
//            smooth_container = Paintor.brushDialogView.findViewById(R.id.smooth_container);
//            _switch_smooth = (SwitchCompat) Paintor.brushDialogView.findViewById(R.id.switch_smooth);
//            seek_smoothing = (SeekBar) Paintor.brushDialogView.findViewById(R.id.seek_s);
//            _seekbar_smooth_1 = (SeekBar) Paintor.brushDialogView.findViewById(R.id.seekbar_smooth_1);
//            _seekbar_smooth_2 = (SeekBar) Paintor.brushDialogView.findViewById(R.id.seekbar_smooth_2);
//            _switch_smooth.setChecked(false);
//            defaultSwitchSmoothStatus = false;

//            smooth_container.setVisibility(View.GONE);

//            _seekbar_smooth_1.setMax(Integer.parseInt(edt_1.getText().toString()) * 100);
//            _seekbar_smooth_2.setMax(Integer.parseInt(edt_2.getText().toString()) * 100);
//            seek_smoothing.setMax(Integer.parseInt(edt_2.getText().toString()) * 100);

//            _tv_smooth = (TextView) Paintor.brushDialogView.findViewById(R.id.tv_progress);
//            tv_smooth_2 = (TextView) Paintor.brushDialogView.findViewById(R.id.tv_progress_2);
//            tv_smoothing_progress = (TextView) Paintor.brushDialogView.findViewById(R.id.tv_p);

//            _seekbar_smooth_1.setProgress(0);
//            _seekbar_smooth_2.setProgress(0);
//            seek_smoothing.setProgress(0);

//            tv_smoothing_progress.setText(0 + "");
//            _tv_smooth.setText(0 + "");
//            tv_smooth_2.setText(0 + "");


            ll_create_brush = (Button) PaintActivity.brushDialogView.findViewById(R.id.btn_create);
            view_create_brush = PaintActivity.brushDialogView.findViewById(R.id.view_btn_create);
            ll_create_brush.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(getApplicationContext(), constants.brush_dialog_create_brush_open, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(getApplicationContext(), constants.brush_dialog_create_brush_open);
//                    Paintor.m_brushlayout.setVisibility(View.GONE);

                    isClickedCancel = false;
                    isClickedCreateBrush = true;
                    PaintActivity.brushSettingsPopup.dismiss();
                    Intent _intent_pattern = new Intent(BrushPickerActivity.this, CreateBrushActivity.class);
                    startActivityForResult(_intent_pattern, CREATE_PATTERN);
                }
            });

            mTxtSize = (TextView) PaintActivity.brushDialogView.findViewById(R.id.txtSize);
//            iv_plus = (ImageView) findViewById(R.id.iv_plus);
//            iv_minus = (ImageView) findViewById(R.id.iv_minus);


//            edt_1.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//                        if (s.toString().isEmpty())
//                            return;
//                        Log.e("TAG", "Set Max " + Integer.parseInt(s.toString()) * 100);
//                        _seekbar_smooth_1.setMax(Integer.parseInt(s.toString()) * 100);
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at afterTextChanged " + e.getMessage());
//                    }
//                }
//            });
//
//            edt_2.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//                        if (s.toString().isEmpty())
//                            return;
//                        Log.e("TAG", "Set Max " + Integer.parseInt(s.toString()) * 100);
//                        _seekbar_smooth_2.setMax(Integer.parseInt(s.toString()) * 100);
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at afterTextChanged " + e.getMessage());
//                    }
//                }
//            });
//            edt_smoothing.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//                        if (s.toString().isEmpty())
//                            return;
//                        Log.e("TAG", "Set Max " + Integer.parseInt(s.toString()) * 100);
//                        seek_smoothing.setMax(Integer.parseInt(s.toString()) * 100);
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at afterTextChanged " + e.getMessage());
//                    }
//                }
//            });

//            seek_smoothing.setVisibility(View.GONE);
//            tv_smoothing_progress.setVisibility(View.GONE);
//            edt_smoothing.setVisibility(View.GONE);


        /*    _seekbar_smooth_1.setVisibility(View.GONE);
            _tv_smooth.setVisibility(View.GONE);
            edt_1.setVisibility(View.GONE);

            _seekbar_smooth_2.setVisibility(View.GONE);
            tv_smooth_2.setVisibility(View.GONE);
            edt_2.setVisibility(View.GONE);*/

//            _switch_smooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        smooth_container.setVisibility(View.VISIBLE);
//                        seek_smoothing.setVisibility(View.VISIBLE);
//                        tv_smoothing_progress.setVisibility(View.VISIBLE);
//                        edt_smoothing.setVisibility(View.VISIBLE);
//                    } else {
//                        smooth_container.setVisibility(View.GONE);
//                        seek_smoothing.setVisibility(View.GONE);
//                        tv_smoothing_progress.setVisibility(View.GONE);
//                        edt_smoothing.setVisibility(View.GONE);
//                    }
//                }
//            });
//
//            seek_smoothing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                    tv_smoothing_progress.setText(progress / (float) 100 + "");
//
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    smoothing = ((float) seekBar.getProgress() / (float) 100);
//                }
//            });


            _switch_line.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences sharedPref = getSharedPreferences("brush", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putBoolean("line", isChecked);
                    editor.apply();

                    if (isChecked) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), constants.brush_dialog_line_on, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(getApplicationContext(), constants.brush_dialog_line_on);
//                        _seekbar_smooth_1.setVisibility(View.VISIBLE);
//                        _tv_smooth.setVisibility(View.VISIBLE);
//                        _seekbar_smooth_2.setVisibility(View.VISIBLE);
//                        tv_smooth_2.setVisibility(View.VISIBLE);
//                        edt_1.setVisibility(View.VISIBLE);
//                        edt_2.setVisibility(View.VISIBLE);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), constants.brush_dialog_line_off, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(getApplicationContext(), constants.brush_dialog_line_off);
//                        _seekbar_smooth_1.setVisibility(View.GONE);
//                        _tv_smooth.setVisibility(View.GONE);
//
//                        _seekbar_smooth_2.setVisibility(View.GONE);
//                        tv_smooth_2.setVisibility(View.GONE);
//
//                        edt_1.setVisibility(View.GONE);
//                        edt_2.setVisibility(View.GONE);
                    }
                    PaintActivity.obj_interface.setSpecialFunctionState(_switch_line);
                }
            });

//            _seekbar_smooth_1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    _tv_smooth.setText(progress / (float) 100 + "");
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    TOUCH_TOLERANCE = ((float) seekBar.getProgress() / (float) 100);
//                    /*if (seekBar.getProgress() >= 1)
//                    else if (seekBar.getProgress() == 0) {
//                        TOUCH_TOLERANCE = (float) 0.1;
//                        _seekbar_smooth_1.setProgress(1);
//                    }*/
//                }
//            });
//
//            _seekbar_smooth_2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                    tv_smooth_2.setText(progress / (float) 100 + "");
//
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    TOUCH_TOLERANCE_1 = ((float) seekBar.getProgress() / (float) 100);
//                    /*if (seekBar.getProgress() >= 1)
//                    else if (seekBar.getProgress() == 0) {
//                        TOUCH_TOLERANCE_1 = 1;
//                        _seekbar_smooth_2.setProgress(1);
//                    }*/
//                }
//            });


//            iv_plus.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//
//                        Log.e("TAG", "Plus Icon Click mBrush.mBrushSize Before " + mBrush.mBrushSize + " " + _progress);
//                        mBrush.mBrushSize++;
//                        if (mBrush.mBrushStyle != 51) {
//                            mBrushSize = mBrush.mBrushSize;
//                            mBrush.setSize(mBrushSize);
//                        } else {
//                            mBrush.setSize(mBrushSize);
//                        }
//                        saveBrushSetting(mBrush.mBrushStyle);
//
//                        String strPercent = String.valueOf((int) mBrush.mBrushSize) + "%";
////                        mTxtSize.refitText(strPercent, -1, -1);
//                        mTxtSize.setText(strPercent);
//                        Paintor.obj_interface.setSize();
//                        mSelectedBrushView.setBrush(mBrush);
//                        mSelectedBrushView.invalidate();
//
//                        setSizeSeekBar();
//                        Log.e("TAG", "Plus Icon Click mBrush.mBrushSize After " + mBrush.mBrushSize);
//                        if (!isLongClicked)
//                        {
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(getApplicationContext(), constants.canvas_brush_plus_click, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_plus_click);
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//            });
//
//            iv_minus.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Log.e("TAG", "Plus Icon Click mBrush.mBrushSize Before " + mBrush.mBrushSize);
//                        if (mBrush.mBrushSize > 1) {
//                            mBrush.mBrushSize--;
//                            if (mBrush.mBrushStyle != 51) {
//                                mBrushSize = mBrush.mBrushSize;
//                                mBrush.setSize(mBrushSize);
//                            } else {
//                                mBrush.setSize(mBrushSize);
//                            }
//                            saveBrushSetting(mBrush.mBrushStyle);
//
//                            String strPercent = String.valueOf((int) mBrush.mBrushSize) + "%";
////                        mTxtSize.refitText(strPercent, -1, -1);
//                            mTxtSize.setText(strPercent);
//                            Paintor.obj_interface.setSize();
//                            mSelectedBrushView.setBrush(mBrush);
//                            mSelectedBrushView.invalidate();
//                            setSizeSeekBar();
//                        }
//                        if (!isLongClicked)
//                        {
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(getApplicationContext(), constants.canvas_brush_minus_click, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_minus_click);
//                        }
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at iv_minus " + e.getMessage());
//                    }
//                }
//            });
//
//            iv_plus.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    isLongClicked = true;
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(getApplicationContext(), constants.canvas_brush_plus_long_click, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_plus_long_click);
//                    increaseDecrease(true);
//                    return false;
//                }
//            });
//
//            iv_minus.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    isLongClicked = true;
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(getApplicationContext(), constants.canvas_brush_minus_long_click, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(getApplicationContext(), constants.canvas_brush_minus_long_click);
//                    increaseDecrease(false);
//                    return false;
//                }
//            });
//
//
//            iv_plus.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Log.e("TAG", "Iv Plus Action " + event.getAction());
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            break;
//
//                        case MotionEvent.ACTION_UP:
//                            try {
//                                Log.e("TAG", "CallBack Removed");
//                                _incrementalHandler.removeCallbacks(_runnalbe);
//                                isLongClicked = false;
//                            } catch (Exception e) {
//
//                            }
//                            break;
//                    }
//                    return false;
//                }
//            });
//
//            iv_minus.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    Log.e("TAG", "Iv Plus Action " + event.getAction());
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            break;
//
//                        case MotionEvent.ACTION_UP:
//                            try {
//                                Log.e("TAG", "CallBack Removed");
//                                _incrementalHandler.removeCallbacks(_runnalbe);
//                            } catch (Exception e) {
//
//                            }
//                            break;
//                    }
//                    return false;
//                }
//            });

        } catch (Exception e) {
            Log.e("TAG", "Exception at LN 1220 " + e.getMessage());
        }

        mCustomSize = (BrushSettingTextView) PaintActivity.brushDialogView.findViewById(R.id.tv_size_lbl);
        mCustomSize.setColor(0xFF000000);
//        mCustomSize.setSize(20);
        mCustomSize.setText("Size");
        mCustomOpacity = (BrushSettingTextView) PaintActivity.brushDialogView.findViewById(R.id.tv_density_lbl);
        mCustomOpacity.setColor(0xFF000000);
//        mCustomOpacity.setSize(20);
        mCustomOpacity.setText("Density");
        mCustomFlow = (BrushSettingTextView) PaintActivity.brushDialogView.findViewById(R.id.tv_hardness_lbl);
        mCustomFlow.setColor(0xFF000000);
//        mCustomFlow.setSize(20);
        mCustomFlow.setText("Hardness");

//        ListView lListView1 = (ListView) findViewById(R.id.brush_list);
//        mBrushListView = (RecyclerView) Paintor.brushDialogView.findViewById(R.id.brush_list);
        mBrushListPopupWindow = createBrushListPopup();

//        View lView1 = findViewById(R.id.opacityLinearLayout);
//        mOpacityPanel = lView1;
//        View lView2 = findViewById(R.id.flowLinearLayout);
//        mFlowPanel = lView2;
        mBrushSettingManager = new BrushSettingManager(this);

        mTxtBrushName = (TextView) PaintActivity.brushDialogView.findViewById(R.id.txtBrushName);
        mTxtBrushName.setOnClickListener(view -> {
            PaintActivity.brushSettingsPopup.dismiss();
            showBrushList(PaintActivity.mBrushStyleBtn);
        });
        setupPatternGrid();
        Intent lIntent = getIntent();
//        mBrushStyle = lIntent.getIntExtra("Brush Style", 16);
        mBrushStyle = lIntent.getIntExtra("Brush Style", 576);
        mBrushColor = lIntent.getIntExtra("Brush Color", -65536);
        mBrushSize = lIntent.getFloatExtra("Brush Size", 10.0F);
        mBrushKidMode = lIntent.getBooleanExtra("Brush Kid Mode", false);
        mBrushAlpha = lIntent.getIntExtra("Brush Pressure", 255);
        mBrushFlow = lIntent.getIntExtra("Brush Flow", 255);

        if (mBrushStyle == 80) {
            mBrushFlow = HARDNESS_MAX_VALUE;
        }

        Painting lPainting = new Painting(null);
        mPainting = lPainting;
        mPainting.createCanvas(480, 60);
        mPainting.mBrushDemoMode = true;
        SelectedBrushView lSelectedBrushView2 = mSelectedBrushView;
        lSelectedBrushView2.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
//			access$5(this$0);
            }
        });
        if (mOkButton != null)
            mOkButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//                    sendBrushEvent();
                    try {
                        setBrushListToTop();
                        PaintActivity.obj_interface.disableColorPenMode();
                        PaintActivity.obj_interface.showCursor();
                        handleOKBrushDialog();
                        returnWithSelectedBrush();
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at onClick LN 1510 " + e.getMessage());
                    }
                }
            });
        if (mCancelButton != null)
            mCancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    isClickedCancel = true;
                    isClickedCreateBrush = false;

                    ((PaintActivity) (BrushPickerActivity.this)).setBrushStyle(false);
                    PaintActivity.obj_interface.showCursor();
                    PaintActivity.obj_interface.cancelBrushDialogListener(); // this line is specially for gray scale
                    handleCancelBrushDialog();
                }
            });
        Log.e("TAG", "setSelectedBrush called LN 1500 " + mBrushStyle);
        setSelectedBrush(mBrushStyle);

        mBrushListView.setBackgroundColor(0xFFFFFFFF);

//        mBrushListView.setAdapter(new BrushListAdapter());

//        _adapter = new BrushListAdapter();

//        mBrushListView.setAdapter(_adapter);

        mBrushListView.setLayoutManager(new LinearLayoutManager(this));

        _list_adapter = new BrushListAdapter(this, mBrushSortList, mBrushDemoItems, this::_onclick, mPatternManager.mPatternInfoList);
        _selectedPid = mBrushStyleList.get(0);
        mBrushListView.setAdapter(_list_adapter);
        mBrushListView.setHasFixedSize(true);
        mBrushListView.setItemViewCacheSize(100);
        defaultSelectedPid = _selectedPid;


        // kgb code
        // bottom layout
//        LinearLayout layout1st = (LinearLayout) findViewById(R.id.LinearLayout12);
//        int nHeightTmp = KGlobal.get_height(layout1st);
//        int a = nHeightTmp;
    }

    private void handleOKBrushDialog() {

        // set Switches
        defaultSwitchSingleTapStatus = switch_singleTap.isChecked();
        defaultSwitchLineStatus = _switch_line.isChecked();
        SwitchCompat switch_gray_scale = PaintActivity.brushDialogView.findViewById(R.id.switch_gray_scale);
        defaultSwitchGrayScaleStatus = switch_gray_scale.isChecked();

        SharedPreferences sharedPref = getSharedPreferences("brush", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        // set Single Tab
        editor.putBoolean("singleTap", switch_singleTap.isChecked());
        // set line
        editor.putBoolean("line", _switch_line.isChecked());
        // set gray scale
        editor.putBoolean("gray_scale", switch_gray_scale.isChecked());
        editor.apply();
        // end set Switches

        // set Seekbars
        defaultSizeBarSet = false;
        defaultDensityBarSet = false;
        defaultHardnessBarSet = false;

        oldDefaultSizeBarProgress = defaultSizeBarProgress;
        oldDefaultSizeBarPercent = defaultSizeBarPercent;
        // end set Seekbars

        saveBrushSetting(mBrush.mBrushStyle);

    }

    private void handleCancelBrushDialog() {
        // reset Selected Brush
        PatternInfo patternInfo = getPatternInfoByStyleId(defaultSelectedPid);
//        _onclick(patternInfo);
        _selectedPid = patternInfo.style;
        Log.e("TAG", "setSelectedBrush called LN 1706 " + _selectedPid);
        if (patternInfo.isPatternBrush) {
            setSelectedBrush(_selectedPid);
            mSelectedBrushView.invalidate();
        } else {
            BrushDemoItem lBrushDemoItem = BrushPickerActivity.this.getBrushDemoItem(_selectedPid);
            setSelectedBrush(_selectedPid);
            mSelectedBrushView.setBrush(BrushPickerActivity.this.mBrush);
            mSelectedBrushView.setStrokePoints(lBrushDemoItem.points);
            mSelectedBrushView.invalidate();
        }
        _list_adapter.setSelected(_selectedPid);

        if (mBrushListPopupWindow.isShowing()) {
            mBrushListPopupWindow.dismiss();
        }

        try {
            returnWithSelectedBrushRecover();
        } catch (Exception e) {
            Log.e("TAG", "Exception at onClick " + e.getMessage());
        }

        switch_singleTap.setChecked(defaultSwitchSingleTapStatus);
        _switch_line.setChecked(defaultSwitchLineStatus);

        SharedPreferences sharedPref = getPreferences(0);
        SharedPreferences.Editor editor = sharedPref.edit();
        // reset Single Tab
        editor.putBoolean("singleTap", defaultSwitchSingleTapStatus);
        // reset line
        editor.putBoolean("line", defaultSwitchLineStatus);
        editor.apply();

        // reset Seekbars
        resetSeekBars();
    }

    private PatternInfo getPatternInfoByStyleId(int defaultSelectedPid) {
        PatternInfo info = null;
        for (PatternInfo patternInfo : mPatternManager.mPatternInfoList) {
            if (patternInfo.style == defaultSelectedPid) {
                info = patternInfo;
                break;
            }
        }

        return info;
    }

    private void showBrushList(View view) {
        if (mBrushListPopupWindow.isShowing()) {
            mBrushListPopupWindow.dismiss();
        } else {
//            mBrushListPopupWindow.showAsDropDown(view);
            int orientation = getResources().getConfiguration().orientation;
            if (getResources().getBoolean(R.bool.is_tablet)) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    mBrushListPopupWindow.showAsDropDown(view, (mBrushListPopupWindow.getWidth() - 15), (mBrushListPopupWindow.getHeight() + 20));
                else
                    mBrushListPopupWindow.showAsDropDown(PaintActivity.paintmenu_close, (PaintActivity.paintmenu_close.getWidth() - mBrushListPopupWindow.getWidth() + 20), -(PaintActivity.paintmenu_close.getHeight() - mBrushListPopupWindow.getHeight() + (20)));
            } else {
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    mBrushListPopupWindow.showAsDropDown(view, (mBrushListPopupWindow.getWidth() - 30), (mBrushListPopupWindow.getHeight() + 25));
                else
                    mBrushListPopupWindow.showAsDropDown(PaintActivity.paintmenu_close, (PaintActivity.paintmenu_close.getWidth() - mBrushListPopupWindow.getWidth() + 20), -(PaintActivity.paintmenu_close.getHeight() - mBrushListPopupWindow.getHeight() + 40));
            }
        }
    }

    private PopupWindow createBrushListPopup() {

        View brushDialogView = getLayoutInflater().inflate(R.layout.brush_list_dialog, null);

        mBrushListView = brushDialogView.findViewById(R.id.brush_list);
        MaterialButton btnBack = brushDialogView.findViewById(R.id.btn_back);

//        MaterialButton btnCreate = brushDialogView.findViewById(R.id.btn_create);
//        MaterialButton btnCancel = brushDialogView.findViewById(R.id.btn_cancel);
//
//        btnCreate.setOnClickListener(view -> {
//            if (mBrushListPopupWindow != null) {
//                if (mBrushListPopupWindow.isShowing()) {
//                    mBrushListPopupWindow.dismiss();
//                }
//            }
//            ll_create_brush.performClick();
//        });

        btnBack.setOnClickListener(view -> {
            if (mBrushListPopupWindow != null) {
                if (mBrushListPopupWindow.isShowing()) {
                    mBrushListPopupWindow.dismiss();

                    if (!PaintActivity.brushSettingsPopup.isShowing()) {
                        PaintActivity._showBrushSettingsPopup.setValue(true);
                    }
                }
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            PopupWindow popupWindow = new PopupWindow(brushDialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(brushDialogView);
            return popupWindow;
        } else {
            PopupWindow popupWindow = new PopupWindow(brushDialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(brushDialogView);
            return popupWindow;
        }

//        PopupWindow popupWindow = new PopupWindow(brushDialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
//        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        popupWindow.setContentView(brushDialogView);
//        return popupWindow;
    }

    public static void saveDataOnClickOutside() {
        if (mOkButton != null) {
            mOkButton.performClick();
        }
    }


    Handler _incrementalHandler = new Handler();
    Runnable _runnalbe;
//    boolean isLongClicked = false;
//
//    void increaseDecrease(boolean isIncrease) {
//        _runnalbe = new Runnable() {
//            @Override
//            public void run() {
//                Log.e("TAG", "increaseDecrease called " + isIncrease);
//                if (isIncrease)
//                    iv_plus.performClick();
//                else
//                    iv_minus.performClick();
//
//                _incrementalHandler.postDelayed(_runnalbe, 100);
//            }
//        };
//        _incrementalHandler.postDelayed(_runnalbe, 100);
//    }


    public void checkCreateButton() {

     /*   if (!AppUtils.getStoreProducts().containsKey("create-brush")) {
            view_create_brush.setVisibility(View.VISIBLE);
            ll_create_brush.setEnabled(false);
            return;
        }
*/
        if (!AppUtils.getPurchasedProducts().contains("create-brush")) {
            view_create_brush.setVisibility(View.VISIBLE);
            ll_create_brush.setEnabled(false);
            view_create_brush.setOnClickListener(v -> {
                FireUtils.getStoreDetails(BrushPickerActivity.this, "create-brush", (productId, productName) -> {
                    FireUtils.showProgressDialog(BrushPickerActivity.this, getResources().getString(R.string.please_wait));
                    FirebaseFirestoreApi.redeemProduct(productId)
                            .addOnCompleteListener(task -> {
                                FireUtils.hideProgressDialog();
                                if (task.isSuccessful()) {
                                    if (!productName.equalsIgnoreCase("")) {
                                        AppUtils.getPurchasedProducts().add(productName);
                                    } else {
                                        AppUtils.getPurchasedProducts().add("create-brush");
                                    }
                                    ll_create_brush.setEnabled(true);
                                    view_create_brush.setVisibility(View.GONE);
                                    ContextKt.showToast(BrushPickerActivity.this, "Redeem Success");
                                } else {
                                    try {
                                        if (task.getException() != null) {
                                            if (task.getException().toString().contains("Insufficient points")) {
                                                FireUtils.showStoreError(BrushPickerActivity.this, "feature");
                                            } else {
                                                ContextKt.showToast(BrushPickerActivity.this, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
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
            view_create_brush.setVisibility(View.GONE);
        }
    }

    public void setBruchSetting(int style, int color, float size, boolean mode, int alpha, int flow) {
        try {

            defaultSizeBarSet = false;
            defaultDensityBarSet = false;
            defaultHardnessBarSet = false;

            defaultSizeBarProgress = size;
            defaultHardnessBarFlow = flow;
            defaultDensityBarAlpha = alpha;

            oldDefaultSizeBarProgress = defaultSizeBarProgress;
            oldDefaultDensityBarAlpha = defaultDensityBarAlpha;
            oldDefaultHardnessBarFlow = defaultHardnessBarFlow;


            mBrushStyle = style;
            mBrushColor = color;
            mBrushSize = size;
            mBrushKidMode = mode;
            mBrushAlpha = alpha;
            mBrushFlow = flow;

//            if (mBrushStyle == 80) {
//                mBrushFlow = HARDNESS_MAX_VALUE / 2;
//            }

            if (mBrushSettingManager != null) {
                mBrushSettingManager.onDestroy();
            }

            mBrushSettingManager = new BrushSettingManager(this);
            Log.e("TAG", "setSelectedBrush called LN 1645 " + mBrushStyle);
            setSelectedBrush(mBrushStyle);

            SharedPreferences sharedPref = getPreferences(0);

            for (int i = 0; i < mBrushSortList.size(); i++) {
                String colorKeyStr = String.format("brushsort_%d", i);
//            mBrushSortList[i] = sharedPref.getInt(colorKeyStr, mBrushSortList[i]);
                mBrushSortList.set(i, sharedPref.getInt(colorKeyStr, mBrushSortList.get(i)));
                colorKeyStr = String.format("brushstyle_%d", i);
                mBrushStyleList.set(i, sharedPref.getInt(colorKeyStr, mBrushStyleList.get(i)));
            }
            if (mBrushStyleList != null && mBrushStyleList.size() > 0) {
                _selectedPid = mBrushStyleList.get(0);
                defaultSelectedPid = _selectedPid;
            }
//            ((BaseAdapter) mBrushListView.getAdapter()).notifyDataSetChanged();
//            mBrushListView.smoothScrollToPosition(0);
//            mBrushListView.setSelection(0);
//            mBrushListView.setSelection(0);
//            mBrushListView.setSelectionFromTop(0, 0);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setBruchSetting " + e.getMessage() + " " + e.toString());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        int i = Log.e(TAG, "destroy");
        mPatternManager.finish();
//        mPatternIconAdapter = null;
        mPatternManager = null;
        mPainting.deinit();
        mPainting = null;

        mSelectedBrushView.finish();
        mSelectedBrushView = null;
        mOkButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
        mBrushListView.setAdapter(null);
        mOkButton = null;
        mCancelButton = null;
        mSelectedBrushView = null;
        mBrushListView = null;
        mOpacityPicker = null;
        mSizePicker = null;
        mRandomDarkColorPicker = null;
        if (mBrush != null) {
            mBrush.release();
            mBrush = null;
        }
        releaseBrushDemoImage();
        mBrushSettingManager.onDestroy();
        mContext = null;
        System.gc();
    }

    @Override
    public void _onclick(PatternInfo _object) {
        /*_selectedPid = (int) style;
        _list_adapter.setSelected(_selectedPid);
        Log.e("TAG", "_selectedPid at onClick " + _selectedPid);
        Log.e("TAG", "setSelectedBrush called LN 1706 " + _selectedPid);
        setSelectedBrush(_selectedPid);
        mSelectedBrushView.invalidate();*/
        /*int nPatternNum = mPatternManager.getPatternNumber();
        Log.e("TAG", "OnItemClickListener called nPatternNum " + nPatternNum + " selected " + _object.style);
        if (_object.isPatternBrush) {
            _selectedPid = _object.style;
            _list_adapter.setSelected(_selectedPid);
            Log.e("TAG", "_selectedPid " + _selectedPid);
            setSelectedBrush(_selectedPid);
            mSelectedBrushView.invalidate();
        } else {
            try {
//                int j = mBrushSettingManager.getBrushStyleAtOrder(mBrushSortList.get(style) - nPatternNum);
                _selectedPid = _object.style;
                _list_adapter.setSelected(_selectedPid);
                BrushDemoItem lBrushDemoItem = getBrushDemoItem(_object.style);
                setSelectedBrush(_selectedPid);
                mSelectedBrushView.setBrush(mBrush);
                mSelectedBrushView.setStrokePoints(lBrushDemoItem.points);
                mSelectedBrushView.invalidate();
            } catch (Exception e) {
                Log.e("TAG", "Exception at else " + e.getMessage());
            }
        }*/
        /*try {

            mPatternManager.writeLogsInFile("Brush Before Click<>" + mBrushStyle + " " + mTxtBrushName.getText().toString() + "" + " Requested Brush " + _object.getStrName() + " " + _object.style + " " + _object._brushType.toString());
        } catch (Exception e) {

        }*/
        _selectedPid = _object.style;
        Log.e("TAG", "setSelectedBrush called LN 1706 " + _selectedPid);
        if (_object.isPatternBrush) {
            setSelectedBrush(_selectedPid);
            mSelectedBrushView.invalidate();
        } else {
            BrushDemoItem lBrushDemoItem = BrushPickerActivity.this.getBrushDemoItem(_selectedPid);
            setSelectedBrush(_selectedPid);
            mSelectedBrushView.setBrush(BrushPickerActivity.this.mBrush);
            mSelectedBrushView.setStrokePoints(lBrushDemoItem.points);
            mSelectedBrushView.invalidate();
        }
        _list_adapter.setSelected(_selectedPid);

        if (mBrushListPopupWindow.isShowing()) {
            mBrushListPopupWindow.dismiss();
        }

        if (!PaintActivity.brushSettingsPopup.isShowing()) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(mContext, "Check Brush State called", Toast.LENGTH_SHORT).show();
            }
            PaintActivity._showBrushSettingsPopup.setValue(true);
        }

        try {
            returnWithSelectedBrushRecover();
        } catch (Exception e) {
            Log.e("TAG", "Exception at onClick " + e.getMessage());
        }
       /* try {
            mPatternManager.writeLogsInFile("Brush Afters Click<>" + _selectedPid + " " + mTxtBrushName.getText().toString());
        } catch (Exception e) {

        }*/
    }

    class Shadow extends View {
        public Shadow(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        private ListView mBrushListView;

        public Shadow(Context context, AttributeSet arg2) {
            super(context, arg2);
        }

        public void draw(Canvas pCanvas) {
            if (mBrushListView != null) {
                if (mBrushListView.getScrollY() <= getHeight())
                    invalidate();
            } else {
                super.draw(pCanvas);
            }
            invalidate();
        }

        public void setListView(ListView pListView) {
            mBrushListView = pListView;
        }
    }

    public static float getmBrushSize() {
        return mBrushSize;
    }

    public void notifyLists(PatternInfo patternObject) {
        try {
            _list_adapter.addItemInList(patternObject);
            _list_adapter.addBrushInRecent(patternObject.style);
            _list_adapter.setSelected(patternObject.style);
        } catch (Exception e) {
            Log.e("TAG", "Exception at notify list " + e.getMessage());
        }
    }

    public void setBrushListToTop() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBrushListView.scrollToPosition(0);
                    _list_adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "Exception at setBrushTo top " + e.getMessage());
        }
    }

    public void updateDensity(int progress, float percent) {
//        mOpacityPicker.onUpdateProgress(progress, mHandler, 3);
//        setupOpacityPercent();

        mBrushAlpha = progress;
        mOpacityPicker.setProgress(mBrushAlpha);
        mOpacityPicker.invalidate();

        int percentDensity = (int) percent;
        String strPercentDensity = percentDensity + "%";

        mTxtOpacity.setText(strPercentDensity);

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = 3;
            msg.arg1 = (int) progress;
            msg.obj = percent;
            mHandler.sendMessage(msg);
        }

        setupOpacityPercent();

    }
}
