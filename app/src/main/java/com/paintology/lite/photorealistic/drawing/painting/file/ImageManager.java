package com.paintology.lite.photorealistic.drawing.painting.file;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.app.manifest.AppManifest;
import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.painting.PaintItem;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.SaveDlg;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.PaintingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedDrawingDao;
import com.paintology.lite.photorealistic.drawing.room.daos.SavedTutorialDao;
import com.paintology.lite.photorealistic.drawing.room.entities.PaintingEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity;
import com.paintology.lite.photorealistic.drawing.room.entities.SavedTutorialEntity;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.TraceReference;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

public class ImageManager {

    public String BACKGOURNDIMAGE_FOLDER_NAME;
    public String PAINT_FOLDER_NAME;
    public String STROKE_FOLDER_NAME;
    public String THUMBNAIL_FOLDER_NAME;
    private boolean isProVersion = true;
    private Activity mContext;
    private String mFileToScan;
    private File mFolder;
    private boolean mHasStorage;
    private OnImageSavedListener mImageSavedListener;
    private Uri mImageUri;
    private MediaScannerConnection mMediaScannerConn = null;
    ProgressDialog mProgressDialog;
    private long mTimeOfPreviousSave = 0;

    StringConstants constants = new StringConstants();

    public ImageManager(Activity pActivity, String path) {
        mContext = pActivity;
        boolean bool1 = hasStorage(true);
        mHasStorage = bool1;
        String str1 = String.valueOf(Environment.getExternalStorageDirectory().toString());
//        String str2 = str1 + "/Paintology/";

//        String str2 = KGlobal.getDefaultFolderPath(this) + "/";
        String str2 = path;

        PAINT_FOLDER_NAME = str2;
        String str3 = String.valueOf(PAINT_FOLDER_NAME);
//        String str4 = str3 + "/.thumb/";
//        THUMBNAIL_FOLDER_NAME = str4;
        String str5 = String.valueOf(PAINT_FOLDER_NAME);
//        String str6 = str5 + "/.stk/";
//        STROKE_FOLDER_NAME = str6;
        String str7 = String.valueOf(PAINT_FOLDER_NAME);
//        String str8 = str7 + "/.bkg/";
//        BACKGOURNDIMAGE_FOLDER_NAME = str8;
//        if (mHasStorage) {
//            String str9 = PAINT_FOLDER_NAME;
//            File lFile1 = new File(str9);
//            mFolder = lFile1;
//            if (!mFolder.exists())
//                mFolder.mkdir();
//            String str10 = THUMBNAIL_FOLDER_NAME;
//            File lFile2 = new File(str10);
//            mFolder = lFile2;
//            if (!mFolder.exists())
//                mFolder.mkdir();
//            String str11 = STROKE_FOLDER_NAME;
//            File lFile3 = new File(str11);
//            mFolder = lFile3;
//            if (!mFolder.exists())
//                mFolder.mkdir();
//            String str12 = BACKGOURNDIMAGE_FOLDER_NAME;
//            File lFile4 = new File(str12);
//            mFolder = lFile4;
//            if (!mFolder.exists())
//                mFolder.mkdir();
//        }
        if (mMediaScannerConn == null) {
            PaintJoyScannerClient lPaintJoyScannerClient = new PaintJoyScannerClient();
            MediaScannerConnection lMediaScannerConnection = new MediaScannerConnection(pActivity, lPaintJoyScannerClient);
            mMediaScannerConn = lMediaScannerConnection;
        }
    }

    private void MyDbgLog(String pString1, String pString2) {
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

    private void dismissProgressDialog() {

        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        } catch (Exception e) {

        }
        Log.e("TAGG", "ProgressDialog dismiss called");
    }

    private String generateFileName() {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("MMddyy_HHmmss");
        long l = System.currentTimeMillis();
        Date lDate = new Date(l);
        return lSimpleDateFormat.format(lDate);
    }

    private Bitmap generateThumb(Bitmap pBitmap) {
        try {
            AppManifest manifest = new AppManifest();
            int i = manifest.computeBestThumbnailWidth(mContext);
            int j = manifest.computeBestThumbnailHeight(mContext);
            Bitmap.Config lConfig = Bitmap.Config.RGB_565;
            Bitmap lBitmap1 = Bitmap.createBitmap(i, j, lConfig);
            Canvas lCanvas = new Canvas(lBitmap1);
            int k = pBitmap.getWidth();
            int m = (pBitmap.getHeight() - k) / 2;
            int n = pBitmap.getWidth();
            int i1 = m + k;
            Rect lRect1 = new Rect(0, 0, pBitmap.getWidth(), pBitmap.getHeight());
            Rect lRect2 = new Rect(0, 0, i, j);
            Bitmap lBitmap2 = pBitmap;
            if (lBitmap2 != null && !lBitmap2.isRecycled())
                lCanvas.drawBitmap(lBitmap2, lRect1, lRect2, null);
            if (i > 100) ;
            for (int i2 = 1; ; i2 = 1) {
                Paint lPaint = new Paint();
                lPaint.setAntiAlias(true);
                Paint.Style lStyle = Paint.Style.STROKE;
                lPaint.setStyle(lStyle);
                float f = i2;
                lPaint.setStrokeWidth(f);
                int i3 = Color.rgb(100, 100, 100);
                lPaint.setColor(i3);
                lCanvas.drawRect(lRect2, lPaint);
                return lBitmap1;
            }
        } catch (Exception e) {

        }
        return pBitmap;

    }

    public boolean hasStorage(boolean pBoolean) {
        boolean bool = true;
        String str = Environment.getExternalStorageState();
        if ("mounted".equals(str)) {
            if (!pBoolean)
                return checkFsWritable();
        } else if ((!pBoolean) && ("mounted_ro".equals(str))) {
            return false;
        }
        return true;
    }

    private void pushToAndroidGallery() {
        mImageUri = null;
        mMediaScannerConn.connect();
    }

    private void showProgressDialog() {
//        mProgressDialog = ProgressDialog.show(mContext, "", "Saving. Please wait...", true);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Saving. Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Log.e("TAGG", "ProgressDialog showProgressDialog called");
    }

    private void sortPainting(ArrayList<PaintItem> pArrayList) {
        ComparatorValues lComparatorValues = new ComparatorValues();

        Collections.sort(pArrayList, lComparatorValues);
    }

    public void deletePainting(String pString) {
        try {
            String str1 = String.valueOf(THUMBNAIL_FOLDER_NAME);
            String str2 = str1 + pString + ".tmb";
            File lFile1 = new File(str2);
            String str3 = String.valueOf(STROKE_FOLDER_NAME);
            String str4 = str3 + pString + ".stk";
            File lFile2 = new File(str4);
            String str5 = String.valueOf(PAINT_FOLDER_NAME);
            String str6 = str5 + "/" + pString;
            File lFile3 = new File(str6);
            String str7 = String.valueOf(BACKGOURNDIMAGE_FOLDER_NAME);
            String str8 = str7 + pString + ".bkg";
            File lFile4 = new File(str8);
            boolean bool1 = lFile1.delete();
            boolean bool2 = lFile3.delete();
            boolean bool3 = lFile2.delete();
            boolean bool4 = lFile4.delete();
        } catch (Exception lException) {
        }
    }

    public void destroy() {
        mContext = null;
        mMediaScannerConn = null;
    }


    public PaintItem getPaintByPath(Context context, String localPath) {

        if (localPath == null) {
            Log.e("ImageManager", "empty paint item list");
            return null;
        }

        if (!mHasStorage) {
            Log.v("ImageManager", "no storage, getAllPaints");
            return null;
        }
        String str1 = PAINT_FOLDER_NAME;
        File lFile = new File(localPath);

        if (!lFile.exists())
            return null;

        if (lFile.isFile()) {
            String str2 = lFile.getName();

            if (!(str2.equalsIgnoreCase("manuals.jpg")) && !str2.toLowerCase().contains("thumb") && !str2.toLowerCase().contains("how_to_draw_canvas")) {
                try {
                    drawing_type type = isTraceDrawing(str2);
                    PaintItem lPaintItem;
                    String fileNameWithOutExt;

                    String extension = str2.substring(str2.toLowerCase().lastIndexOf("."));
                    //prevent .mp4 file include in list.
                    if ((extension.toLowerCase().equalsIgnoreCase(".png") || extension.toLowerCase().equalsIgnoreCase(".jpg"))) {
                        if (type.equals(drawing_type.Movie)) {
                            fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");

                            PaintItem lPaintItem_1 = new PaintItem(null, fileNameWithOutExt + ".png", lFile.lastModified(), str2, drawing_type.None, false);
                            // pArrayList.add(lPaintItem_1);
                            fileNameWithOutExt = fileNameWithOutExt + ".mp4";
                            lPaintItem = new PaintItem(null, fileNameWithOutExt, lFile.lastModified(), str2, type, false);

                            Log.e("ImageManager 319", type.toString());

                        } else {
                            fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
                            boolean isExisted = isExistedonMyMovie(context, fileNameWithOutExt);
                            if (isExisted) {
                                Log.e("ImageManager 326", "Movie");

                                lPaintItem = new PaintItem(null, fileNameWithOutExt + ".mp4", lFile.lastModified(), str2, drawing_type.Movie, false);
                            } else {
                                Log.e("ImageManager 326", type.toString());

                                lPaintItem = new PaintItem(null, str2, lFile.lastModified(), str2, type, false);
                            }
                        }
                        return lPaintItem;
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at add to lost thumbName " + str2 + e.getMessage());
                }
            }
        }
        return null;
    }

    public void getAllPaints(Context context, ArrayList<PaintItem> pArrayList) {
        if (pArrayList == null) {
            Log.e("ImageManager", "empty paint item list");
            return;
        }

        if (!mHasStorage) {
            Log.v("ImageManager", "no storage, getAllPaints");
            return;
        }
        String str1 = PAINT_FOLDER_NAME;
        File[] arrayOfFile = new File(str1).listFiles();

        if (arrayOfFile == null)
            return;

        for (int k = 0; k < arrayOfFile.length; k++) {
            File lFile = arrayOfFile[k];
            if (lFile.isFile()) {
                String str2 = lFile.getName();

                if (!(str2.equalsIgnoreCase("manuals.jpg")) && !str2.toLowerCase().contains("thumb") && !str2.toLowerCase().contains("how_to_draw_canvas")) {
                    try {
                        drawing_type type = isTraceDrawing(str2);
                        PaintItem lPaintItem;
                        String fileNameWithOutExt;

                        String extension = str2.substring(str2.toLowerCase().lastIndexOf("."));
                        //prevent .mp4 file include in list.
                        if ((extension.toLowerCase().equalsIgnoreCase(".png") || extension.toLowerCase().equalsIgnoreCase(".jpg"))) {
                            if (type.equals(drawing_type.Movie)) {
                                fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
                                Log.e("ImageManager 377", "None");

                                PaintItem lPaintItem_1 = new PaintItem(null, fileNameWithOutExt + ".png", lFile.lastModified(), str2, drawing_type.None, false);
                                pArrayList.add(lPaintItem_1);
                                fileNameWithOutExt = fileNameWithOutExt + ".mp4";
                                lPaintItem = new PaintItem(null, fileNameWithOutExt, lFile.lastModified(), str2, type, false);

                            } else {
                                fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
                                boolean isExisted = isExistedonMyMovie(context, fileNameWithOutExt);
                                if (isExisted) {
                                    Log.e("ImageManager 388", "Movie");

                                    lPaintItem = new PaintItem(null, fileNameWithOutExt + ".mp4", lFile.lastModified(), str2, drawing_type.Movie, false);
                                } else {
                                    Log.e("ImageManager 392", type.toString());

                                    lPaintItem = new PaintItem(null, str2, lFile.lastModified(), str2, type, false);

                                }
                            }
                            pArrayList.add(lPaintItem);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at add to lost thumbName " + str2 + e.getMessage());
                    }
                }
            }
        }

        File[] arrayOfMyMovies = new File(KGlobal.getDownloadedFolderPath(context)).listFiles();

        if (arrayOfMyMovies == null)
            return;

        for (int k = 0; k < arrayOfMyMovies.length; k++) {
            File lFile = arrayOfMyMovies[k];
            if (lFile.isFile()) {
                String str2 = lFile.getName();

                Log.e("TAGGG", "File Name at Load " + str2);
                try {
//                    drawing_type type = isTraceDrawing(str2);
                    PaintItem lPaintItem;
                    String fileNameWithOutExt;

                    String extension = str2.substring(str2.toLowerCase().lastIndexOf("."));
                    //prevent .mp4 file include in list.
                    if ((extension.toLowerCase().equalsIgnoreCase(".mp4"))) {
                        fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
                        fileNameWithOutExt = fileNameWithOutExt + ".mp4";
                        lPaintItem = new PaintItem(null, fileNameWithOutExt, lFile.lastModified(), fileNameWithOutExt + ".png", drawing_type.Movie, false);
                        Log.e("ImageManager 428", "Movie");

                        lPaintItem = new PaintItem(null, fileNameWithOutExt, lFile.lastModified(), fileNameWithOutExt + ".png", drawing_type.Movie, false);

                        pArrayList.add(lPaintItem);
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at add to lost thumbName " + str2 + e.getMessage());
                }
            }
        }

        try {
            Collections.sort(pArrayList, lastModifiedComparator);
        } catch (Exception e) {
        }
        //        for (int i = 0; i < pArrayList.size(); i++) {
//            Log.e("TAGG", "sorting in imagemanager After " + pArrayList.get(i).getFileName() + " Time " + pArrayList.get(i).getLastModifiedTime());
//        }
//        sortPainting(pArrayList);
    }

//    public void getAllPaints(Context context, ArrayList<PaintItem> pArrayList) {
//        if (pArrayList == null) {
//            Log.e("ImageManager", "empty paint item list");
//            return;
//        }
//
//        if (!mHasStorage) {
//            Log.e("ImageManager", "no storage, getAllPaints");
//            return;
//        }
//        String str1 = PAINT_FOLDER_NAME;
//        File[] arrayOfFile = new File(str1).listFiles();
//
//        if (arrayOfFile == null)
//            return;
//
//        for (File lFile : arrayOfFile) {
//            if (lFile.isFile()) {
//                String str2 = lFile.getName();
//
//                if (!(str2.equalsIgnoreCase("manuals.jpg")) && !str2.toLowerCase().contains("thumb") && !str2.toLowerCase().contains("how_to_draw_canvas")) {
//                    try {
//                        drawing_type drawingType = isTraceDrawing(str2);
//                        PaintItem lPaintItem;
//                        String fileNameWithOutExt;
//
//                        String extension = str2.substring(str2.toLowerCase().lastIndexOf("."));
//                        // Prevent .mp4 file include in list.
//                        if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")) {
//                            if (drawingType == drawing_type.Movie) {
//                                fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
//                                fileNameWithOutExt = fileNameWithOutExt + ".mp4";
//                                lPaintItem = new PaintItem(null, fileNameWithOutExt, lFile.lastModified(), str2, drawingType, false, null);
//                            } else {
//                                fileNameWithOutExt = str2.replaceFirst("[.][^.]+$", "");
//                                boolean isExisted = isExistedonMyMovie(context, fileNameWithOutExt);
//                                if (isExisted)
//                                    lPaintItem = new PaintItem(null, fileNameWithOutExt + ".mp4", lFile.lastModified(), str2, drawing_type.Movie, false, null);
//                                else
//                                    lPaintItem = new PaintItem(null, str2, lFile.lastModified(), str2, drawingType, false, null);
//                            }
//
//                            // Retrieve the type from metadata file
//                            String paintingType = getTypeFromMetadata(lFile.getAbsolutePath());
//                            if (paintingType != null) {
//                                lPaintItem.setType(paintingType);
//                                Log.e("ImageManager", "File: " + str2 + " | Type: " + paintingType);
//                            } else {
//                                Log.e("ImageManager", "File: " + str2 + " | Type: not found");
//                            }
//
//                            pArrayList.add(lPaintItem);
//                        }
//                    } catch (Exception e) {
//                        Log.e("TAGG", "Exception at add to list thumbName " + str2 + ": " + e.getMessage());
//                    }
//                }
//            }
//        }
//
//        try {
//            Collections.sort(pArrayList, lastModifiedComparator);
//        } catch (Exception e) {
//            Log.e("TAGG", "Exception during sorting: " + e.getMessage());
//        }
//    }

    public String getTypeFromMetadata(String imagePath) {
        String metadataPath = imagePath.replace(".png", ".txt");
        StringBuilder type = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(metadataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                type.append(line);
            }
        } catch (IOException e) {
            Log.e("ImageManager", "Error reading type info", e);
            return null;
        }

        return type.toString();
    }


    public boolean isExistedonMyMovie(Context context, String name) {
        File[] arrayOfMyMovies = new File(KGlobal.getDownloadedFolderPath(context)).listFiles();
        for (int k = 0; k < arrayOfMyMovies.length; k++) {
            if (arrayOfMyMovies[k].getName().contains(name)) {
                return true;
            }
        }
        return false;
    }


    public drawing_type isTraceDrawing(String fileName) {
        try {
            ArrayList<TraceReference> traceList = listFromPreference();
            if (traceList != null) {
                for (int i = 0; i < traceList.size(); i++) {
//                Log.e("TAGG", "From Name " + fileName + " From Pref " + traceList.get(i).getUserPaintingName());
//                    if(fileName.equalsIgnoreCase(traceList.get(i).get_drawing_type()))
                    if (traceList.get(i).get_drawing_type() != null) {
                        if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.Trace)) {
                            return drawing_type.Trace;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.TraceDrawaing)) {
                            return drawing_type.TraceDrawaing;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.Overlay)) {
                            return drawing_type.Overlay;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.OverlayDrawing)) {
                            return drawing_type.OverlayDrawing;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.ImportImage)) {
                            return drawing_type.ImportImage;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.Movie)) {
                            return drawing_type.Movie;
                        } else if (fileName.equalsIgnoreCase(traceList.get(i).getUserPaintingName()) && traceList.get(i).get_drawing_type().equals(drawing_type.TraceCanvasDrawing)) {
                            return drawing_type.TraceCanvasDrawing;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at isTraceDrawing fileName " + e.getMessage());
        }
        return drawing_type.None;
    }


    public ArrayList<TraceReference> listFromPreference() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(constants.getTraceList_Gson_Key(), "");
        String overlayJson = appSharedPrefs.getString(constants.getOverlayList_Gson_Key(), "");
        Type type = new TypeToken<ArrayList<TraceReference>>() {
        }.getType();
        ArrayList<TraceReference> traceList = gson.fromJson(json, type);

        if (traceList == null) {
            traceList = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(overlayJson)) {
            traceList.addAll(gson.fromJson(overlayJson, type));
        }

        return traceList;
    }

    Comparator<PaintItem> lastModifiedComparator = new Comparator<PaintItem>() {
        @Override
        public int compare(PaintItem jc1, PaintItem jc2) {
            return (jc2.lastModifiedTime < jc1.lastModifiedTime ? -1 :
                    (jc2.lastModifiedTime == jc1.lastModifiedTime ? 0 : 1));
        }
    };


    public Bitmap getThumb(String pString, int width, int height) {
        pString = pString.toLowerCase();
        Log.e("TAGGG", "getThumb extension " + pString + " ");
        if (pString.contains(".png") || pString.contains(".jpg")) {
            String str = String.valueOf(PAINT_FOLDER_NAME);
            return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(str + pString), width, height, true);
        }
        return null;
        /*else {
            String str = String.valueOf(THUMBNAIL_FOLDER_NAME);
            Log.e("str", str + pString + ".tmb");
            return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(str + pString + ".tmb"), width, height, true);
        }*/
    }

    public Bitmap getThumb(int nId, int width, int height) {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), nId), width, height, true);
    }

    public Uri getUri() {
        return mImageUri;
    }

    public Bitmap loadBitmap(String pString) {
        String str = String.valueOf(PAINT_FOLDER_NAME);
//        if (pString.contains(".png"))
//            return BitmapFactory.decodeFile(str + "/" + pString);
//        else
//            return BitmapFactory.decodeFile(str + "/" + pString + ".png");
//
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(str + "/" + pString, bmOptions);
    }

    public void loadPaintBackground(Painting pPainting, String pString) {
        String str = String.valueOf(BACKGOURNDIMAGE_FOLDER_NAME);
        Log.e("TAGGG", "Load Image Logs loadPainting called loadPaintingFromFile goto in if goto in if  called loadPaintBackground str " + str);
        if (pString.contains(".png") || pString.contains(".jpg") || pString.contains(".JPG") || pString.contains(".jpeg") || pString.contains(".JPEG")) {
        } else {
            Bitmap lBitmap = BitmapFactory.decodeFile(str + pString + ".bkg");
            pPainting.setBackgroundBitmap(lBitmap);
        }
    }

    public void loadPaintBitmap(Painting pPainting, String pString, int width,
                                int height, String parentFolder) {
//        String str = String.valueOf(PAINT_FOLDER_NAME);
        String str = parentFolder;
        try {
            System.gc();
            if (pString.contains(".png") || pString.contains(".jpg") || pString.contains(".JPG") || pString.contains(".jpeg") || pString.contains(".JPEG")) {
                Bitmap lBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(str + "/" + pString), width, height, true);

                if (lBitmap != null)
                    pPainting.setBitmap(getRotatedBitmap(str + "/" + pString, lBitmap));
                else
                    pPainting.setBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
            } else {
                Bitmap lBitmap = BitmapFactory.decodeFile(str + pString + ".png");
                pPainting.setBitmap(getRotatedBitmap(str + pString + ".png", lBitmap));
            }
        } catch (Exception e) {
            Log.e("TAGGG", "loadPaintBitmap exception " + e.getMessage());
        }
    }

    public Bitmap getRotatedBitmap(String photoPath, Bitmap bitmap) {
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

        Display lDisplay = mContext.getWindowManager().getDefaultDisplay();

        Log.e("TAG", "Rotet Image From Bitmp width " + source.getWidth() + " height " + source.getHeight());
        Log.e("TAG", "Rotet Image From scree width " + lDisplay.getWidth() + " height " + lDisplay.getHeight());

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void loadPaintingFromFile(Painting pPainting, String pString, int width, int height, String ParentFolderPath) {

        try {
            if (pString.contains(".png") || pString.contains(".jpg") || pString.contains(".JPG") || pString.contains(".jpeg") || pString.contains(".JPEG")) {
                String str1 = String.valueOf(PAINT_FOLDER_NAME) + "/" + String.valueOf(pString);

                File lFile = new File(str1);
                loadPaintBitmap(pPainting, pString, width, height, ParentFolderPath);
                if (pPainting.isUseBackgroundBitmap()) {
                    loadPaintBackground(pPainting, pString);
                }
            }
        } catch (OutOfMemoryError er) {

        } catch (Exception e) {

        }
    }

    public String saveImage(Bitmap pBitmap, String pString, String type) {
        Uri lUri = null;
        if (pBitmap == null)
            return null;

        if (!mHasStorage) {
            int i = Log.v("ImageManager", "no storage, savePaintingToFile");
            /*try {
                Toast.makeText(mContext, "Failed to save painting. Please check if SD card is avaialbe.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }*/
            return null;
        }
        if (pString == null)
            pString = generateFileName();

        String fileName = pString;
        String fileNameWithExt = fileName + ".png";


        // Accessing Room database instance
        AppDatabase appDatabase = MyApplication.getDb();
        PaintingDao paintingDao = appDatabase.paintingDao();

        // Inserting a painting
        new Thread(() -> {
            PaintingEntity painting = new PaintingEntity(0, fileNameWithExt, type, false);
            paintingDao.insertPainting(painting);
        }).start();

        try {
            String rootFolderPath = String.valueOf(PAINT_FOLDER_NAME);
            File directory = new File(rootFolderPath);

            if (!directory.exists())
                directory.mkdirs();

            String filePath = rootFolderPath + "/" + fileNameWithExt;

            OutputStream imageOutStream;
            File lFile = new File(filePath);
            imageOutStream = new FileOutputStream(lFile);

            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(imageOutStream);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
            pBitmap.compress(lCompressFormat, 0, lBufferedOutputStream);
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();
//            String str5 = String.valueOf(PAINT_FOLDER_NAME);
//            String str6 = str5 + fileNameWithExt;
//            mFileToScan = str6;
            mFileToScan = filePath;
            Activity lActivity = mContext;

            Log.e("TAG", "Bitmap Logs While save " + pBitmap.getWidth() + " * " + pBitmap.getHeight());
//            String str7 = "saved to " + PAINT_FOLDER_NAME + " as " + fileNameWithExt;


            // Save the type information in a metadata file
            saveTypeInfo(mContext, filePath, type);

            pushToAndroidGallery();
            lUri = mImageUri;
            dismissProgressDialog();

            return filePath;
        } catch (FileNotFoundException lFileNotFoundException) {

            String str8 = String.valueOf(pString);
            String str9 = str8 + " not found";
            int j = Log.e("ImageManager", str9);
            return null;
        } catch (IOException lIOException) {
            String str10 = String.valueOf(pString);
            String str11 = str10 + " io error";
            int k = Log.e("ImageManager", str11);
            return null;
        }
    }

    private void saveTypeInfo(Context context, String imagePath, String type) {
        String metadataPath = imagePath.replace(".png", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataPath))) {
            try {
                SharedPreferences sharedPref = context.getSharedPreferences("brush", 0);
                JsonObject object = new JsonObject();
                object.addProperty("singleTap", sharedPref.getBoolean("singleTap", false));
                object.addProperty("line", sharedPref.getBoolean("line", false));
                object.addProperty("gray_scale", sharedPref.getBoolean("gray_scale", false));
                object.addProperty("block_coloring", sharedPref.getBoolean("block_coloring", false));
                object.addProperty("background-color", sharedPref.getInt("background-color", 0xFFFFFFFF));
                object.addProperty("brush-style", sharedPref.getInt("brush-style", Brush.LineBrush));
                object.addProperty("brush-size", sharedPref.getFloat("brush-size", 35.0F));
                object.addProperty("brush-color", sharedPref.getInt("brush-color", -65536));
                object.addProperty("brush-alpha", sharedPref.getInt("brush-alpha", 255));
                object.addProperty("brush-pressure", sharedPref.getInt("brush-pressure", 65));
                object.addProperty("brush-flow", sharedPref.getInt("brush-flow", 255));
                object.addProperty("brush-mode", sharedPref.getInt("brush-mode", 33));
                object.addProperty("type", type);

                writer.write(object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("ImageManager", "Error saving type info", e);
        }
    }

    public void savePaintBackgroundImageToFile(Painting pPainting, String pString) {
        String str1 = String.valueOf(pString);
        String str2 = str1 + ".bkg";
        String str3 = String.valueOf(BACKGOURNDIMAGE_FOLDER_NAME);
        String str4 = str3 + str2;
        File lFile = new File(str4);
        Bitmap lBitmap = pPainting.getBackgroundBitmap();
        if (lBitmap == null)
            return;

        try {
            FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
            boolean bool = lBitmap.compress(lCompressFormat, 95, lBufferedOutputStream);
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();

            Log.e("TAG", "SavePainting mPainting" + pPainting.mPaintingWidth + " * " + pPainting.mPaintingWidth);
            Log.e("TAG", "SavePainting Bitmap " + lBitmap.getWidth() + " * " + lBitmap.getHeight());
        } catch (FileNotFoundException lFileNotFoundException) {
            lFileNotFoundException.printStackTrace();
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
        }
    }

    public String savePaintingToFile(int strokeCount, String youtube_video_id, String selectedImagePath, Painting pPainting, boolean bSave, final Activity context,
                                     final boolean needToExit, Integer Tag, int post_id,
                                     String swatchesJson, String colorPalette, Boolean isPickFromOverlaid, String type) {
        String randName = generateFileName();

        mContext = context;
        SaveDlg dlg = new SaveDlg(mContext, pPainting, randName, bSave, new SaveDlg.OnProjectNameListener() {
            public void onOk(SaveDlg dialog, Painting pPainting, boolean bSave, String strProjectName) {

                boolean isFileFound = false;
                String fileNameFromDirectory = "";

                if (getAllfiles() != null)
                    for (int i = 0; i < getAllfiles().length; i++) {

                        if (getAllfiles()[i].getName().indexOf(".") > 0)
                            fileNameFromDirectory = getAllfiles()[i].getName().substring(0, getAllfiles()[i].getName().lastIndexOf("."));

                        if (fileNameFromDirectory.equalsIgnoreCase(strProjectName)) {
                            isFileFound = true;
                            break;
                        }
                    }
                if (!isFileFound) {
                    dialog.cancel();
                    new saveImageInBack(strokeCount, youtube_video_id, selectedImagePath, pPainting, bSave, strProjectName, mContext, needToExit, Tag, post_id, swatchesJson, colorPalette, isPickFromOverlaid, type).execute();
                } else {
                    confirmOverwritesPainting(strokeCount, youtube_video_id, selectedImagePath, dialog, pPainting, bSave, strProjectName, context, needToExit, post_id, swatchesJson, colorPalette, isPickFromOverlaid, type);
                }
            }

            @Override
            public void onCancel() {
                // Paintor.obj_interface.saveToLocal("", Tag);
            }
        });

        dlg.show();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return randName;
    }

    private void confirmOverwritesPainting(int strokeCount, String youtube_video_id, String selectedImagePath, final SaveDlg saveDlg, final Painting pPainting,
                                           final boolean bSave, final String fileName,
                                           final Context context, final boolean needToExit,
                                           int post_id, String swatchesJson, String colorPalette,
                                           Boolean isPickFromOverlaid, String type) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(context);

        lBuilder1.setTitle("File exist!");
        lBuilder1.setMessage("\'" + fileName + "\'" + " is already exist, Do you want to overwrites it now?").setCancelable(true);

        lBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (saveDlg != null && saveDlg.isShowing()) {
                    saveDlg.cancel();
                }

                new saveImageInBack(strokeCount, youtube_video_id, selectedImagePath, pPainting, bSave, fileName, mContext, needToExit, -1, post_id, swatchesJson, colorPalette, isPickFromOverlaid, type).execute();
            }
        });

        lBuilder1.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        lBuilder1.create().show();
    }

    File[] getAllfiles() {
//        String path = Environment.getExternalStorageDirectory().toString() + "/Paintology";

        String path = PAINT_FOLDER_NAME;
        File directory = new File(path);
        File[] files = directory.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            Log.e("Files", "FileName:" + files[i].getName());
//        }
        return files;
    }

    public class saveImageInBack extends AsyncTask<Void, Void, String> {

        Painting pPainting;
        boolean bSave;
        String selectedImagePath;
        String strProjectName;
        Activity context;
        private ProgressDialog dialog;
        boolean needToExit = false;
        Integer Tag = -1;
        private int post_id;
        private String swatchesJson;
        private String colorPalette;

        private String type;

        String youtube_video_id;
        private int strokeCount;
        private boolean isPickFromOverlaid = false;

        public saveImageInBack(int strokeCount, String youtube_video_id, String selectedImagePath, Painting pPainting, boolean bSave, String strProjectName,
                               Activity context, boolean exit, Integer Tag, int post_id,
                               String swatchesJson, String colorPalette, Boolean isPickFromOverlaid, String type) {
            this.pPainting = pPainting;
            this.bSave = bSave;
            this.Tag = Tag;
            this.strokeCount = strokeCount;
            this.youtube_video_id = youtube_video_id;
            this.strProjectName = strProjectName;
            this.context = context;
            this.selectedImagePath = selectedImagePath;
            dialog = new ProgressDialog(context);
            needToExit = exit;
            this.post_id = post_id;
            this.swatchesJson = swatchesJson;
            this.colorPalette = colorPalette;
            this.isPickFromOverlaid = isPickFromOverlaid;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Saving. Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str7 = "";
            if (System.currentTimeMillis() - ImageManager.this.mTimeOfPreviousSave > 3000L) {
                ImageManager.this.mTimeOfPreviousSave = System.currentTimeMillis();

                setImageSavedListener(null);
                Bitmap lBitmap = pPainting.getPainting();

                Log.e("Looking For Tag", type);

                str7 = saveImage(lBitmap, strProjectName, type);
                Log.e("TAG", "ImageManager Bitmap Size " + lBitmap.getWidth() + " " + lBitmap.getHeight());
                if (ImageManager.this.isProVersion) {
                    saveThumbnailToFile(pPainting, strProjectName);
                    if (pPainting.isUseBackgroundBitmap())
                        savePaintBackgroundImageToFile(pPainting, strProjectName);

                    if (pPainting.getStrokeCount() > 0) { // Check if there are strokes
                        Log.d("getStrokeCount", "pPainting.getStrokeCount" + pPainting.getStrokeCount());
                        saveStrokeToFile(pPainting, strProjectName);
                    }
//                    saveStrokeToFile(pPainting, strProjectName);
                }
                pPainting.hasSaved();

                if (post_id != -1) {
                    String str8 = str7;
                    AppDatabase db = MyApplication.getDb();
                    SavedTutorialDao savedTutorialDao = db.savedTutorialDao();
                    SavedDrawingDao savedDrawingDao = db.savedDrawingDao();
                    SavedTutorialEntity savedTutorialEntity = new SavedTutorialEntity();
                    SavedDrawingEntity savedDrawingEntity = new SavedDrawingEntity();
                    savedTutorialEntity.postId = post_id;
                    savedDrawingEntity.postId = post_id;
                    savedDrawingEntity.modifiedDate = System.currentTimeMillis();
                    savedDrawingEntity.youtubeVideoId = youtube_video_id;

                    if (!TextUtils.isEmpty(swatchesJson)) {
                        savedTutorialEntity.swatches = swatchesJson;
                        savedDrawingEntity.swatches = swatchesJson;

                    }

                    if (!TextUtils.isEmpty(colorPalette)) {
                        savedTutorialEntity.colorPalette = colorPalette;
                        savedDrawingEntity.colorPalette = colorPalette;
                    }

                    savedTutorialEntity.localPath = str8;
                    savedDrawingEntity.localPath = str8;
                    savedDrawingEntity.strokeCount = strokeCount;
                    savedDrawingEntity.originPath = selectedImagePath;
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            savedTutorialDao.insertAll(savedTutorialEntity);
                            savedDrawingDao.insertAll(savedDrawingEntity);
//                            savedTutorialDao.updateLocalPath(str8, post_id);
                        }
                    });
                }
            }
            return str7;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Log.e("TAGRR",strProjectName+" HELL");


            Toast.makeText(context, "isPickFromOverlaid: " + isPickFromOverlaid, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                if (isPickFromOverlaid) {
                    PaintActivity.obj_interface.storeInOverlayList(strProjectName);
                } else {
                    PaintActivity.obj_interface.storeInTraceList(strProjectName);
                }
            }, 250);


            if (s == null) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, constants.canvas_save_painting_fail, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(context, constants.canvas_save_painting_fail);
                Toast.makeText(mContext, "Failed to save painting. Please check if SD card is available OR Storage Permission.", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, constants.canvas_save_painting_success, Toast.LENGTH_SHORT).show();
                }

                /************   After Saving Image  */
                try {
                    MyPaintingsActivity.getInstance().LoadData();
                } catch (Exception e) {
                }

                try {
                    if (post_id > 0) {
                        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.save_drawing, String.valueOf(post_id));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                FirebaseUtils.logEvents(context, constants.canvas_save_painting_success);
                if (needToExit) {
                    Intent intent = new Intent("RefreshHome");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    PaintActivity.obj_interface.exitFromAPP();
                    return;
                }
            }

            PaintActivity.obj_interface.saveToLocal(strProjectName, Tag);
            if (!bSave) {
//                ((Paintor) mContext).clearPainting();
                PaintActivity.obj_interface.clearPaintingAndSetNew();
                PaintActivity.obj_interface.resetTimer();
            }
            Toast.makeText(context, context.getResources().getString(R.string.saved_to_my_painting), Toast.LENGTH_LONG).show();
            Intent intent = new Intent("RefreshHome");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        }
    }

    public Uri saveShareImageAsJpeg(Bitmap pBitmap) {
        Uri lUri = null;
        if (pBitmap == null)
            return null;

        String str1 = "temp";
        if (!mHasStorage) {
            int i = Log.v("ImageManager", "no storage, savePaintingToFile");

            return null;
        }
        String str2 = String.valueOf(str1);
        String str3 = str2 + ".jpg";
        showProgressDialog();
        try {

            String str4 = String.valueOf(PAINT_FOLDER_NAME);
            String str5 = str4 + str3;
            File lFile = new File(str5);
            FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.JPEG;
            boolean bool = pBitmap.compress(lCompressFormat, 95, lBufferedOutputStream);
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();
            String str6 = String.valueOf(PAINT_FOLDER_NAME);
            String str7 = str6 + str3;
            mFileToScan = str7;
            pushToAndroidGallery();
            lUri = mImageUri;
            dismissProgressDialog();

            return lUri;
        } catch (FileNotFoundException lFileNotFoundException) {

            String str8 = String.valueOf(str1);
            String str9 = str8 + " not found";
            int j = Log.e("ImageManager", str9);
            dismissProgressDialog();
            return null;
        } catch (IOException lIOException) {
            String str10 = String.valueOf(str1);
            String str11 = str10 + " io error";
            int k = Log.e("ImageManager", str11);
            dismissProgressDialog();
            return null;
        }
    }

    public Uri saveShareImageAsPng(Bitmap pBitmap, String str) {
        Uri lUri = null;
        if (pBitmap == null)
            return null;
        String str1 = "temp";
        if (!mHasStorage) {
            int i = Log.v("ImageManager", "no storage, savePaintingToFile");
          /*  try {
                Toast.makeText(mContext, "Failed to save painting. Please check if SD card is avaialbe.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }*/
            return null;
        }

        String str3 = "paintology_" + str;
        showProgressDialog();
        try {
            String str4 = String.valueOf(PAINT_FOLDER_NAME);
            String str5 = str4 + str3;
            File lFile = new File(str5);
            FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
            boolean bool = pBitmap.compress(lCompressFormat, 95, lBufferedOutputStream);
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();
            String str6 = String.valueOf(PAINT_FOLDER_NAME);
            String str7 = str6 + str3;
            mFileToScan = str7;
            pushToAndroidGallery();
            lUri = mImageUri;

            return lUri;
        } catch (FileNotFoundException lFileNotFoundException) {

            /*try {
                Toast.makeText(mContext, "Fail to save image for share. Please check SD card.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }*/
            String str8 = String.valueOf(str1);
            String str9 = str8 + " not found";
            int j = Log.e("ImageManager", str9);
            dismissProgressDialog();

            return null;
        } catch (IOException lIOException) {
            String str10 = String.valueOf(str1);
            String str11 = str10 + " io error";
            int k = Log.e("ImageManager", str11);
            dismissProgressDialog();

            return null;
        }
    }

    public String saveStrokeToFile(Painting pPainting, String pString) {
        PaintingFile lPaintingFile = new PaintingFile();
        String str1 = String.valueOf(pString);
        String str2 = str1 + ".stk";
        String str3 = String.valueOf(STROKE_FOLDER_NAME);
        String str4 = str3 + str2;
        File lFile = new File(str4);
        try {
            FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
            BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
            GZIPOutputStream lGZIPOutputStream = new GZIPOutputStream(lBufferedOutputStream);
            DataOutputStream lDataOutputStream = new DataOutputStream(lGZIPOutputStream);
            lPaintingFile.storePaintingToStream(pPainting, lDataOutputStream);
            lDataOutputStream.flush();
            lDataOutputStream.close();
            lGZIPOutputStream.flush();
            lGZIPOutputStream.close();
            lBufferedOutputStream.flush();
            lBufferedOutputStream.close();
            return str2;
        } catch (FileNotFoundException lFileNotFoundException) {
            lFileNotFoundException.printStackTrace();
            return null;
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
            return null;
        }
    }

    public void saveThumbnailToFile(Painting pPainting, String pString) {
        String strFileName = pString + ".tmb";
        String strFilePath = THUMBNAIL_FOLDER_NAME + strFileName;
        File imgFile = new File(strFilePath);

        Bitmap lBitmap1 = pPainting.getPainting();
        Bitmap lBitmap2 = generateThumb(lBitmap1);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imgFile));
            lBitmap2.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return;
        } catch (FileNotFoundException lFileNotFoundException) {
            lFileNotFoundException.printStackTrace();
            if (lBitmap2 != null)
                lBitmap2.recycle();
            return;
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
            if (lBitmap2 != null)
                lBitmap2.recycle();
            return;
        } finally {
            if (lBitmap1 != null)
                lBitmap2.recycle();
        }
    }

    public void setImageSavedListener(OnImageSavedListener pOnImageSavedListener) {
        mImageSavedListener = pOnImageSavedListener;
    }

    final class ComparatorValues
            implements Comparator<PaintItem> {
        public int compare(PaintItem pPaintItem1, PaintItem pPaintItem2) {
            File file1 = new File(PAINT_FOLDER_NAME + pPaintItem1.getFileName());
            File file2 = new File(PAINT_FOLDER_NAME + pPaintItem2.getFileName());
            return file1.lastModified() > file2.lastModified() ? -1 : 1;
        }
    }

    public abstract interface OnImageSavedListener {
        public abstract void onImageSaved();
    }

    class PaintJoyScannerClient
            implements MediaScannerConnection.MediaScannerConnectionClient {
        private PaintJoyScannerClient() {
        }

        public void onMediaScannerConnected() {
            ImageManager lImageManager = ImageManager.this;
            StringBuilder lStringBuilder = new StringBuilder("Scan paintjoy folder ");
            String str1 = ImageManager.this.mFileToScan;
            String str2 = str1;
            lImageManager.MyDbgLog("ImageManager", str2);
            MediaScannerConnection lMediaScannerConnection = ImageManager.this.mMediaScannerConn;
            String str3 = ImageManager.this.mFileToScan;
            if (!TextUtils.isEmpty(str3)) {
                lMediaScannerConnection.scanFile(str3, null);
            }
        }

        public void onScanCompleted(String pString, Uri pUri) {
            ImageManager lImageManager = ImageManager.this;
            String str = "Scan completed, path " + pString;
            lImageManager.MyDbgLog("ImageManager", str);
            ImageManager.this.mImageUri = pUri;
            ImageManager.this.mMediaScannerConn.disconnect();
            ImageManager.this.dismissProgressDialog();
            if (ImageManager.this.mImageSavedListener != null)
                ImageManager.this.mImageSavedListener.onImageSaved();
        }
    }
}
