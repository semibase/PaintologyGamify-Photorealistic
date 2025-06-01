package com.paintology.lite.photorealistic.drawing.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ImageManageModel;
import com.paintology.lite.photorealistic.drawing.Model.SessionModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.brushpicker.CustomSeekBar;
import com.paintology.lite.photorealistic.drawing.component.TextViewKu;
import com.paintology.lite.photorealistic.drawing.gallery.BrushSettingTextView;
import com.paintology.lite.photorealistic.drawing.minipaint.Webview;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class KGlobal {

    //Azims Account token
//    public static String mixpanel_token = "16bb02f3c2c4708ebb5dbf3e30c1aa56";

    //Ankits Account token
//    public static String mixpanel_token = "1d02fabf943a14d4fba80f31c133d027";

    //For Testing without any token
    public static String mixpanel_token = "";

    //    Client account token
//    public static String mixpanel_token = "137f1ed0da0311316ab984385b313831";

    public static StringConstants _constant = new StringConstants();

    public static String getDefaultFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/Paintology Collection").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/Paintology Collection");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/Paintology Collection";
    }

    public static String getPatternFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/Pattern").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/Pattern");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/Pattern";
    }


    public static String getDownloadedFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/My Movies").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/My Movies");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/My Movies";
    }

    public static String getMyPaintingFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/My Paintings").toString();

//        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Paintology/My Paintings"); // working in Android 11
////
//////        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/My Paintings");
////
//        if (!f.exists()) {
//            f.mkdirs();
//        }
////
//////        return Environment.getExternalStorageDirectory() + "/Paintology/My Paintings";
//        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Paintology/My Paintings";
    }


    public static String getTraceImageFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/TraceImages").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/TraceImages");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/TraceImages";
    }

    public static String getTraceImageFolderPathForSpecificUser(Context context) {
        return context.getExternalFilesDir("/Paintology/TraceImages/UsersTraceImages").toString();
    }

    public static String getStrokeEventFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/Stroke And Events").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/Stroke And Events");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/Stroke And Events";
    }

    public static String getDownloadPath(Context context) {

        return context.getExternalFilesDir("/Paintology/Download").toString();

//        File f = new File(Environment.getExternalStorageDirectory() + "/Paintology/Download");
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return Environment.getExternalStorageDirectory() + "/Paintology/Download";
    }

    public static String getDrawImageFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/DrawImages").toString();

    }

    public static String getProfileFolderPath(Context context) {

        return context.getExternalFilesDir("/Paintology/Profile").toString();

    }


    private static void checkLayoutParams(View v) {
        if (v.getLayoutParams() == null)
            v.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
    }

    public static int get_width(View v) {
        checkLayoutParams(v);
        return ((ViewGroup.LayoutParams) v.getLayoutParams()).width;
    }

    public static int get_height(View v) {
        checkLayoutParams(v);
        return ((ViewGroup.LayoutParams) v.getLayoutParams()).height;
    }

    void set_height(View v, int nHeight) {
        checkLayoutParams(v);
        ((ViewGroup.LayoutParams) v.getLayoutParams()).height = nHeight;
    }

    public static float minVal(float val_1st, float val_2nd) {
        if (val_1st < val_2nd)
            return val_1st;

        return val_2nd;
    }

    public static void resizeView(ViewGroup container, float scaleX, float scaleY) {
        int nChildCount = container.getChildCount();

        for (int i = 0; i < container.getChildCount(); ++i) {
            View childview = container.getChildAt(i);
            ViewGroup.MarginLayoutParams layps = (ViewGroup.MarginLayoutParams) childview.getLayoutParams();

            if (layps == null)
                continue;

            if (layps.width > 0) layps.width *= scaleX;
            if (layps.height > 0) layps.height *= scaleY;
            if (layps.leftMargin > 0) layps.leftMargin *= scaleX;
            if (layps.topMargin > 0) layps.topMargin *= scaleY;

            if (childview.getClass() == RelativeLayout.class || childview.getClass() == LinearLayout.class
                    || childview.getClass() == ScrollView.class) {
                Log.e("TAGG", "Control Header ViewGroup");
                resizeView((ViewGroup) childview, scaleX, scaleY);
            } else if (childview.getClass() == TextView.class) {
                TextView txtView = (TextView) childview;
//                txtView.setTextSize(1.0F * txtView.getTextSize() * minVal(scaleX, scaleY));
                txtView.setTextSize(20);
                Log.e("TAGG", "Control Header TextView");
            } else if (childview.getClass() == BrushSettingTextView.class) {
                BrushSettingTextView txtView = (BrushSettingTextView) childview;
                txtView.setSize((int) (1.0F * txtView.getSize() * minVal(scaleX, scaleY)));
            } else if (childview.getClass() == CustomSeekBar.class) {
                CustomSeekBar seekBar = (CustomSeekBar) childview;
                seekBar.m_nMargin = (int) (seekBar.m_nMargin * scaleX);
                seekBar.m_nThumbHeight = (int) (seekBar.m_nThumbHeight * scaleY);
                seekBar.m_scaleX = scaleX;
                seekBar.m_scaleY = scaleY;
            } else if (childview.getClass() == TextViewKu.class) {
                TextViewKu tmp = (TextViewKu) childview;
                tmp.nGap *= scaleY;
            }
        }
    }

    public static void showNetworkError(Context context) {
        Toast.makeText(context, context.getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
    }

    public static boolean checkInternet(Context activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                if (cm.getActiveNetworkInfo() == null) {
                    int count = _constant.getInt("no_internet", context);
                    count = count + 1;
                    _constant.putInt("no_internet", count, context);
                    Log.e("TAG", "Internet Not Available! " + (_constant.getInt("no_internet", context)));
                } else {
                    sendNoInternetEvent(context);
                }
            } catch (Exception e) {
                Log.e("TAG", "Exception at no internet " + e.getMessage());
            }
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendNoInternetEvent(Context _context) {
        try {
            int count = _constant.getInt("no_internet", _context);

            for (int i = 0; i < count; i++) {
                FirebaseUtils.logEvents(_context, "no_internet");
                if (BuildConfig.DEBUG) {
                    Toast.makeText(_context, "no internet", Toast.LENGTH_SHORT).show();
                }
            }
            _constant.putInt("no_internet", 0, _context);
        } catch (Exception e) {
        }
    }

    public static SessionModel _session_model = new SessionModel();

    public static SessionModel get_session_model() {
        return _session_model;
    }

    public static void set_session_model(SessionModel _session_model) {
        KGlobal._session_model = _session_model;
    }

    public static void openInBrowser(Context context, String _url) {
        try {

            Log.e("youtubeLink", _url);

            if (KGlobal.isInternetAvailable(context)) {
                Intent _intent = new Intent(context, Webview.class);
                _intent.putExtra("_url", _url);
                context.startActivity(_intent);
            } else
                Toast.makeText(context, context.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();

           /* if (KGlobal.isInternetAvailable(context)) {

                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(_url));
                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
                String packageName = resolveInfo.activityInfo.packageName;
                browserIntent.setPackage(packageName);
                context.startActivity(browserIntent);
            } else
                Toast.makeText(context, context.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
*/
        } catch (Exception e) {
            Log.e("TAGG", "Exception at openBrowser " + e.getMessage(), e);
        }
    }

    public static int externalCacheNotAvailableState = 0;

    public static File getCameraCacheDir(boolean isSend, boolean isTemp) {
        if (externalCacheNotAvailableState == 1 || externalCacheNotAvailableState == 0 && Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            externalCacheNotAvailableState = 1;
            return GetFolderInformation(1, isSend, isTemp);
        }
        externalCacheNotAvailableState = 2;
        //return ApplicationLoader.applicationContext.getCacheDir();
        return GetFolderInformation(1, isSend, isTemp);
    }


    public static File GetFolderInformation(int FileType, boolean isSend, boolean isTemp) {
        File mydir = null;
        switch (FileType) {
            case 1:
                if (isSend) {
                    if (!isTemp) {
//                        mydir = new File(Environment.getExternalStorageDirectory() + "/CEASE/image/send");
                        mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/image/camera");
                    } else {
                        mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/image/.temp");
                    }
                } else {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/image");
                }

                //File mydir = ApplicationLoader.applicationContext.getDir("CEASE", Context.MODE_PRIVATE); //Creating an internal dir;
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                break;
            case 2:
                if (isSend) {
                    if (!isTemp) {
                        mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/video/camera");
                    } else {
                        mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/video/.temp");
                    }
                } else {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/video");
                }
                //File mydir = ApplicationLoader.applicationContext.getDir("CEASE", Context.MODE_PRIVATE); //Creating an internal dir;
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                break;
            case 3:
                if (isSend) {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/map/camera");
                } else {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/map");
                }
                //File mydir = ApplicationLoader.applicationContext.getDir("CEASE", Context.MODE_PRIVATE); //Creating an internal dir;
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                break;
            case 4:
                if (isSend) {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/audio/camera");
                } else {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/audio");
                }
                //File mydir = ApplicationLoader.applicationContext.getDir("CEASE", Context.MODE_PRIVATE); //Creating an internal dir;
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                break;
            case 5:
                if (isSend) {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/doc/camera");
                } else {
                    mydir = new File(Environment.getExternalStorageDirectory() + "/Paintology/doc");
                }
                //File mydir = ApplicationLoader.applicationContext.getDir("CEASE", Context.MODE_PRIVATE); //Creating an internal dir;
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                break;
        }
        return mydir;
    }


    public static String getPath(final Uri uri, Activity context) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
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
                return getDataColumn(context, contentUri, null, null);
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

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

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


    public static ImageManageModel scaleAndSaveImageWithMatrix(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, boolean isSend, boolean isImage, Matrix matrix) {
        ImageManageModel _localManage = new ImageManageModel();
        if (bitmap == null) {
            return _localManage;
        }
        float photoW = bitmap.getWidth();
        float photoH = bitmap.getHeight();
        if (photoW == 0 || photoH == 0) {
            return _localManage;
        }
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);

        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (photoW / scaleFactor), (int) (photoH / scaleFactor), matrix, true);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) (photoW / scaleFactor), (int) (photoH / scaleFactor), matrix, true);

        try {
            if (!cache) {
                String fileName = "img" + "_" + (new Date()).getTime() + ".jpg";
                final File cacheFile;
                if (isImage) {
                    cacheFile = new File(getCameraCacheDir(isSend, false), fileName);
                } else {
                    cacheFile = new File(getVideoCacheDir(isSend, false), fileName);
                }
                FileOutputStream stream = new FileOutputStream(cacheFile);
                Log.e("TAGGG", "Copy Path from >> " + stream + " PATH > " + cacheFile.getAbsolutePath());
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

                _localManage.setDone(true);
                _localManage.setLocalImagePath(cacheFile.getAbsolutePath());
                // size.size = (int) stream.getChannel().size();
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);


               /* size.bytes = stream.toByteArray();
                size.size = size.bytes.length;*/
            }
            if (Build.VERSION.SDK_INT < 11) {
                if (scaledBitmap != bitmap) {
                    scaledBitmap.recycle();
                }
            }
            return _localManage;
        } catch (Exception e) {
            return _localManage;
        }
    }

    public static File getVideoCacheDir(boolean isSend, boolean isTemp) {
        if (externalCacheNotAvailableState == 1 || externalCacheNotAvailableState == 0 && Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            externalCacheNotAvailableState = 1;
            return GetFolderInformation(2, isSend, isTemp);
        }
        externalCacheNotAvailableState = 2;
        //return ApplicationLoader.applicationContext.getCacheDir();
        return GetFolderInformation(2, isSend, isTemp);
    }


    public static ImageManageModel scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, boolean isSend, boolean isImage, String FileName) {
        //String fileNameWithOutExt = FileName.replaceFirst("[.][^.]+$", "");
        ImageManageModel _localManage = new ImageManageModel();
        if (bitmap == null) {
            return _localManage;
        }
        float photoW = bitmap.getWidth();
        float photoH = bitmap.getHeight();
        if (photoW == 0 || photoH == 0) {
            return _localManage;
        }
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (photoW / scaleFactor), (int) (photoH / scaleFactor), true);

        try {
            if (!cache) {
                Log.e("TAGGG", "Check Steps Here >< 44. " + FileName);
                if (FileName.equals("")) {
                    FileName = "img" + "_" + (new Date()).getTime() + ".jpg";
                }

                final File cacheFile;
                if (isImage) {
                    cacheFile = new File(getCameraCacheDir(isSend, false), FileName);
                } else {
                    cacheFile = new File(getVideoCacheDir(isSend, false), FileName);
                }
                FileOutputStream stream = new FileOutputStream(cacheFile);
                Log.e("TAGGG", "Copy Path from >> " + stream + " PATH > " + cacheFile.getAbsolutePath());
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

                _localManage.setDone(true);
                _localManage.setLocalImagePath(cacheFile.getAbsolutePath());
                // size.size = (int) stream.getChannel().size();
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);


               /* size.bytes = stream.toByteArray();
                size.size = size.bytes.length;*/
            }
            if (Build.VERSION.SDK_INT < 11) {
                if (scaledBitmap != bitmap) {
                    scaledBitmap.recycle();
                }
            }
            return _localManage;
        } catch (Exception e) {
            return _localManage;
        }
    }

    public static String getLogFileFolderPath(Context context) {
        return context.getExternalFilesDir("/Paintology/Log").toString();
    }

    public static void appendLog(Context context, String text) {
        String parentPath = getLogFileFolderPath(context);

        File logFile = new File(parentPath + "/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}