package com.paintology.lite.photorealistic.drawing.gallery;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.paintology.lite.photorealistic.drawing.Adapter.DownloadedAdapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.CustomePicker.PostActivity;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.interfaces.MyMoviesMenuItemClickListener;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MyMoviesActivity extends AppCompatActivity implements Interface_select_item,
        MyMoviesMenuItemClickListener {

    private static final int PERMISSION_REQUEST_CODE = 10;
    GridView gridView;
    File downloadedFolder;
    ArrayList<model_DownloadedTutorial> lst_main = new ArrayList<>();
    DownloadedAdapter adapter;
    ImageView iv_default_img/*, iv_edit_btn*/;
    boolean isDisplayDefault_Image = false;

    Interface_select_item objInterface;
    StringConstants constants = new StringConstants();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_movies);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Movies");

        objInterface = this;
        try {
            fillData();
        } catch (Exception e) {
            Log.e("TAG", "Exception at fill data " + e.getMessage());
        }
//        if (constants.getString(constants.DisplayedMyMovies, this).isEmpty()) {
//            constants.putString(constants.DisplayedMyMovies, "true", this);
//            showHintDialog();
//        }
    }

    public void close(View v) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(MyMoviesActivity.this, constants.canvas_close_movies, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.canvas_close_movies);
        finish();
    }

    boolean firstTouch = false;
    long time;

    void fillData() {
        gridView = findViewById(R.id.grid_view);
        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (orientation == 1) {
            if (isTablet)
                gridView.setNumColumns(3);
            else
                gridView.setNumColumns(2);
        } else {
            if (isTablet)
                gridView.setNumColumns(4);
            else
                gridView.setNumColumns(3);
        }

        downloadedFolder = new File(KGlobal.getDownloadedFolderPath(this));

        iv_default_img = (ImageView) findViewById(R.id.iv_default_img);
//        iv_edit_btn = (ImageView) findViewById(R.id.iv_edit_btn);

        if (lst_main != null) {
            lst_main.clear();
        }
        if (downloadedFolder.exists()) {
            File childfile[] = downloadedFolder.listFiles();
            if (childfile != null)
                for (int i = 0; i < childfile.length; i++) {
                    if (childfile[i].isFile()) {
                        if (childfile[i].getAbsolutePath().toLowerCase().endsWith(".mp4")) {
                            model_DownloadedTutorial obj = new model_DownloadedTutorial();
                            obj.setDownloadedFileName(childfile[i].getName());
                            obj.setDownloadedFilePath(childfile[i].getAbsolutePath());
                            lst_main.add(obj);
                        }
                    }
                }

            if (lst_main.size() > 0) {
                for (int i = 0; i < lst_main.size(); i++) {
                    for (int j = 0; j < childfile.length; j++) {
                        if (lst_main.get(i).getDownloadedFileName().indexOf(".") > 0) {
                            String name = lst_main.get(i).getDownloadedFileName().substring(0, lst_main.get(i).getDownloadedFileName().lastIndexOf("."));
                            String fileName = childfile[j].getName().substring(0, childfile[j].getName().lastIndexOf("."));
                            if (fileName.startsWith("EventData") && fileName.endsWith(name))
                                lst_main.get(i).setDownloadedEevntFilePath(childfile[j].getAbsolutePath());
                            else if (fileName.startsWith("StrokeData") && fileName.endsWith(name))
                                lst_main.get(i).setDownloadedStrokeFilePath(childfile[j].getAbsolutePath());
                        }
                    }
                }
                if (lst_main != null && lst_main.size() > 0)
                    lst_main.get(0).setSelected(true);
                Collections.reverse(lst_main);
            } else {
                model_DownloadedTutorial obj_1 = new model_DownloadedTutorial();
                obj_1.setDownloadedFilePath(KGlobal.getDefaultFolderPath(this) + "/manuals.jpg");
                obj_1.setDownloadedFileName("Get Started");
                lst_main.add(obj_1);
                isDisplayDefault_Image = true;
            }
            for (int i = 0; i < lst_main.size(); i++) {
                lst_main.get(i).setSelected(false);
            }
            lst_main.get(0).setSelected(true);
            adapter = new DownloadedAdapter(this, lst_main, this);
            gridView.setAdapter(adapter);
        } else {
            model_DownloadedTutorial obj_1 = new model_DownloadedTutorial();
            obj_1.setDownloadedFilePath(KGlobal.getDefaultFolderPath(this) + "/manuals.jpg");
            obj_1.setDownloadedFileName("manuals");

            lst_main.add(obj_1);
            isDisplayDefault_Image = true;
            for (int i = 0; i < lst_main.size(); i++) {
                lst_main.get(i).setSelected(false);
            }
            lst_main.get(0).setSelected(true);
            adapter = new DownloadedAdapter(this, lst_main, this);
            gridView.setAdapter(adapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                objInterface.selectItem(position, false);

//                if (firstTouch && (Calendar.getInstance().getTimeInMillis() - time) <= 500) {
//                    //do stuff here for double tap
//                    Log.e("** DOUBLE TAP**", " second tap ");
//                    firstTouch = false;
////                    objInterface.selectItem(prevSelectedpos, false);
//                    objInterface.selectItem(position, false);
//                    return;
//                } else {
//                    firstTouch = true;
//                    time = Calendar.getInstance().getTimeInMillis();
//                    Log.e("** SINGLE  TAP**", " First Tap time  " + time);
//                }
//                /*if (prevSelectedpos == position) {
//                    lst_main.get(prevSelectedpos).setSelected(false);
////                    prevSelectedpos = -1;
//                } else {
////                    if (prevSelectedpos != -1)
//                    lst_main.get(prevSelectedpos).setSelected(false);
//                    prevSelectedpos = position;
//                    lst_main.get(position).setSelected(!lst_main.get(position).getSelected());
//                }*/
//
//                for (int i = 0; i < lst_main.size(); i++) {
//                    if (i == position) {
////                        lst_main.get(position).setSelected(!lst_main.get(position).getSelected());
//                        lst_main.get(position).setSelected(true);
//                    } else
//                        lst_main.get(i).setSelected(false);
//                }
//                adapter.notifyDataSetChanged();
            }
        });
    }

//    public void onclick(View v) {
//        try {
//            if (adapter == null)
//                return;
//
//            if (v.getId() == R.id.iv_play_youtube) {
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(MyMoviesActivity.this, constants.mymovie_video_icon_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(this, constants.mymovie_video_icon_click);
//
//                StringConstants.IsFromDetailPage = true;
//                Intent intent = new Intent(MyMoviesActivity.this, Play_YotubeVideo.class);
//                String url = constants.getString(constants.mymovies_youtube_url, MyMoviesActivity.this);
//                if (url != null && !url.isEmpty())
//                    intent.putExtra("url", url);
//                else
//                    intent.putExtra("url", "https://youtu.be/vPl5mUgDQOY");
//
//                intent.putExtra("isVideo", true);
//                intent.putExtra("hideToggle", "hideToggle");
//                startActivity(intent);
//            }
//
//            int position = adapter.getSelectedPos();
//            if (position == -1 && !isDisplayDefault_Image) {
//                Toast.makeText(this, "Nothing Selected!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            switch (v.getId()) {
//                case R.id.iv_post_icon: {
//                    if (constants.getString(constants.Password, this).isEmpty()) {
//                        Toast.makeText(this, getResources().getString(R.string.login_via_dialog), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    File photoFile = new File(lst_main.get(position).getDownloadedFilePath());
//                    String filePath = photoFile.getAbsolutePath();
//                    ArrayList<String> mImageList = new ArrayList<>();
//                    mImageList.add(filePath);
//                    Intent returnIntent = new Intent(this, PostActivity.class);
//                    returnIntent.putStringArrayListExtra("result", mImageList);
//                    startActivity(returnIntent);
//                    finish();
//                }
//                break;
//                case R.id.iv_edit_btn: {
//                    if (isDisplayDefault_Image) {
//                        try {
//                            /*if (iv_default_img.getVisibility() != View.VISIBLE) {
//                                iv_default_img.setVisibility(View.VISIBLE);
//                                gridView.setVisibility(View.GONE);
//                                InputStream is = getResources().getAssets().open(lst_main.get(position).getDownloadedFileName() + ".jpg");
//                                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                                iv_default_img.setImageBitmap(bitmap);
//                            }*/
//
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(MyMoviesActivity.this, constants.mymovies_open_video, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(this, constants.mymovies_open_video);
//
//                            StringConstants.IsFromDetailPage = true;
//                            Intent intent = new Intent(MyMoviesActivity.this, Play_YotubeVideo.class);
//                            String url = constants.getString(constants.mymovies_youtube_url, MyMoviesActivity.this);
//                            if (url != null && !url.isEmpty())
//                                intent.putExtra("url", url);
//                            else
//                                intent.putExtra("url", "https://youtu.be/vPl5mUgDQOY");
//
//                            intent.putExtra("isVideo", true);
//                            intent.putExtra("hideToggle", "hideToggle");
//                            startActivity(intent);
//                        } catch (Exception e) {
//                            Log.e("TAGGG", "exception at set image " + e.getMessage());
//                        }
//                    } else {
//                        SharedPreferences appSharedPrefs = PreferenceManager
//                                .getDefaultSharedPreferences(this.getApplicationContext());
//                        if (appSharedPrefs.getString(lst_main.get(position).getDownloadedFileName(), null) != null) {
//                            Intent intent = new Intent(MyMoviesActivity.this, Paintor.class);
//                            intent.putExtra("TutorialPath", lst_main.get(position).getDownloadedFilePath());
//                            intent.putExtra("EventFilePath", lst_main.get(position).getDownloadedEevntFilePath());
//                            intent.putExtra("StrokeFilePath", lst_main.get(position).getDownloadedStrokeFilePath());
//                            intent.setAction("FromTutorialMode");
//                            intent.putExtra("OverlaidImagePath", appSharedPrefs.getString(lst_main.get(position).getDownloadedFileName(), null));
////                            FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(MyMoviesActivity.this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(MyMoviesActivity.this, Paintor.class);
//                            intent.putExtra("TutorialPath", lst_main.get(position).getDownloadedFilePath());
//                            intent.putExtra("EventFilePath", lst_main.get(position).getDownloadedEevntFilePath());
//                            intent.putExtra("StrokeFilePath", lst_main.get(position).getDownloadedStrokeFilePath());
//                            intent.setAction("FromTutorialMode");
////                            FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(MyMoviesActivity.this, constants.Pick_Image_My_Movies, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
//                            if (BuildConfig.DEBUG){
//                                Toast.makeText(MyMoviesActivity.this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
//                            }
//                            FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
//                            startActivity(intent);
//                        }
//                    }
//                }
//                break;
//                case R.id.iv_delete_icon: {
//                    if (isDisplayDefault_Image)
//                        return;
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(MyMoviesActivity.this, constants.DELETE_MOVIE, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(MyMoviesActivity.this, constants.DELETE_MOVIE);
//                    confirmDialog(position);
//                }
//                break;
//                case R.id.iv_share_icon: {
//                    if (isDisplayDefault_Image)
//                        return;
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(MyMoviesActivity.this, constants.share_movie_click, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(MyMoviesActivity.this, constants.share_movie_click);
//                    File photoFile = new File(lst_main.get(position).getDownloadedFilePath());
//                    Uri photoURI = FileProvider.getUriForFile(MyMoviesActivity.this,
//                            getString(R.string.authority),
//                            photoFile);
//
//                    doSocialShare(photoURI);
////                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
////                    shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
////                    shareIntent.setType("*/*");
////                    startActivity(Intent.createChooser(shareIntent, "Share To"));
//                }
//                break;
//            }
//        } catch (
//                Exception e) {
//            Log.e("TAGG", "Exception at selction " + e.getMessage() + " " + e.toString());
//        }
//
//    }

    public void closeApp(View view) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(MyMoviesActivity.this, constants.canvas_close_movies, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(this, constants.canvas_close_movies);
        if (iv_default_img.getVisibility() == View.VISIBLE) {
            iv_default_img.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        } else
            finish();
    }

    private void confirmDialog(int position) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(this);
        lBuilder1.setMessage(getResources().getString(R.string.are_you_sure)).setCancelable(true);

        lBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                File file = new File(Environment.getExternalStorageDirectory(), "Paintology/" + fileName);

                // Delete mp4 file's thumbnail
                String parentThumbnailFolderPath = KGlobal.getMyPaintingFolderPath(MyMoviesActivity.this);
                String newFilenameWithoutExt = AppUtils.getFileNameWithoutExtension(new File(lst_main.get(position).getDownloadedFilePath()));
                File fileMovieThumbnail = new File(parentThumbnailFolderPath + "/" + newFilenameWithoutExt + "." + "png");

                // Delete mp4 file
                File file = new File(lst_main.get(position).getDownloadedFilePath());
                if (file.exists()) {
                    boolean isDelete = file.delete();
                    if (isDelete) {
                        fileMovieThumbnail.delete();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.DELETE_MOVIE_SUCCES, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.DELETE_MOVIE_SUCCES);
                        Toast.makeText(MyMoviesActivity.this, "Delete Success!", Toast.LENGTH_SHORT).show();
                        lst_main.remove(lst_main.get(position));
                        adapter.notifyDataSetChanged();
                        adapter.resetSelection();
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.DELETE_MOVIE_FAIL, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.DELETE_MOVIE_FAIL);
                    }
                } else {
                    Toast.makeText(MyMoviesActivity.this, "File Not Exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        lBuilder1.create().show();
    }

//    public class createGIF extends AsyncTask {
//        String[] cmd;
//        Context context;
//        String path;
//
//        public createGIF(String[] cmd, Context context, String path) {
//            this.cmd = cmd;
//            this.context = context;
//            this.path = path;
//        }
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            return null;
//        }
//    }

//    public static void conversion(String[] cmd, Context context, String path) {
//        FFmpeg ffmpeg = FFmpeg.getInstance(context);
//
//        try {
//
//            Log.e("TAGG", "conversion called path " + path);
//
//            // to execute "ffmpeg -version" command you just need to pass "-version"
//            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    Log.e("TAGG", "conversion onStart");
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Log.e("TAGG", "conversion onProgress message " + message);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    Log.e("TAGG", "conversion onFailure message " + message);
//                }
//
//                @Override
//                public void onSuccess(String message) {
//                    Log.e("TAGG", "conversion onSuccess message " + message);
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.e("TAGG", "conversion onFinish  ");
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // Handle if FFmpeg is already running
//            e.printStackTrace();
//            Log.e("TAGG", "conversion FFmpegCommandAlreadyRunningException  " + e.getMessage());
//        } catch (Exception e) {
//            Log.e("TAGG", "conversion Exception  " + e.getMessage() + " " + e.toString());
//        }
//    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (iv_default_img.getVisibility() == View.VISIBLE) {
            iv_default_img.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        } else
            finish();
    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
//        onclick(iv_edit_btn);
        onEditClick(lst_main.get(pos), pos);
    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {

    }

    void showHintDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Select Item");

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


    public void doSocialShare(Uri photoURI) {
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
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.setType("*/*");
            Intent receiver = new Intent(this, PaintActivity.BroadcastShareFromCanvas.class);
//        receiver.putExtra("test", "test");
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);

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

            shareRewardPoint();

            startActivity(chooser);
        } catch (Exception e) {
            Log.e(PaintActivity.class.getName(), e.getMessage());
        }
    }

    public static class BroadcastForMovies extends BroadcastReceiver {

        public BroadcastForMovies() {
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
                    else
                        shareFileVia = _app_name;

                    Log.e("TAGGG", "share_movie_via " + shareFileVia + " ");
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(context, "share_movies_via_" + shareFileVia, Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUtils.logEvents(context, "share_movies_via_" + shareFileVia);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception while share image " + e.getMessage(), e);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MyMoviesActivity.this);
            String isLoginInPaintology = constants.getString(constants.LoginInPaintology, MyMoviesActivity.this);

            if (isLoggedIn || account != null || (isLoginInPaintology != null && isLoginInPaintology.trim().equalsIgnoreCase("true"))) {
                String _user_id = constants.getString(constants.UserId, MyMoviesActivity.this);
                if (_user_id != null && !_user_id.isEmpty()) {
                    Log.e("TAG", "setOffline called MyMovies 1558");
                    MyApplication.get_realTimeDbUtils(this).setOffline(_user_id);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            String LoginInPaintology = constants.getString(constants.LoginInPaintology, this);
            try {
                if (isLoggedIn || account != null || (LoginInPaintology != null && LoginInPaintology.trim().equalsIgnoreCase("true"))) {
                    String _user_id = constants.getString(constants.UserId, MyMoviesActivity.this);
                    if (_user_id != null && !_user_id.isEmpty()) {
                        Log.e("TAG", "setOnline called MyMovies 1578");
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

    @Override
    public void onEditClick(@Nullable model_DownloadedTutorial item, int position) {
        if (isDisplayDefault_Image) {
            try {
                            /*if (iv_default_img.getVisibility() != View.VISIBLE) {
                                iv_default_img.setVisibility(View.VISIBLE);
                                gridView.setVisibility(View.GONE);
                                InputStream is = getResources().getAssets().open(lst_main.get(position).getDownloadedFileName() + ".jpg");
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                iv_default_img.setImageBitmap(bitmap);
                            }*/

                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyMoviesActivity.this, constants.mymovies_open_video, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.mymovies_open_video);

                StringConstants.IsFromDetailPage = true;
                Intent intent = new Intent(MyMoviesActivity.this, Play_YotubeVideo.class);
                String url = constants.getString(constants.mymovies_youtube_url, MyMoviesActivity.this);
                if (url != null && !url.isEmpty())
                    intent.putExtra("url", url);
                else
                    intent.putExtra("url", "https://youtu.be/LomXFP8zKw8");


//                   intent.putExtra("url", "https://youtu.be/vPl5mUgDQOY");

                intent.putExtra("isVideo", true);
                intent.putExtra("hideToggle", "hideToggle");
                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAGGG", "exception at set image " + e.getMessage());
            }
        } else {
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());
            if (appSharedPrefs.getString(item.getDownloadedFileName(), null) != null) {
                Intent intent = new Intent(MyMoviesActivity.this, PaintActivity.class);
                intent.putExtra("TutorialPath", item.getDownloadedFilePath());
                intent.putExtra("EventFilePath", item.getDownloadedEevntFilePath());
                intent.putExtra("StrokeFilePath", item.getDownloadedStrokeFilePath());
                intent.setAction("FromTutorialMode");
                intent.putExtra("OverlaidImagePath", appSharedPrefs.getString(item.getDownloadedFileName(), null));
//                            FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyMoviesActivity.this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyMoviesActivity.this, PaintActivity.class);
                intent.putExtra("TutorialPath", item.getDownloadedFilePath());
                intent.putExtra("EventFilePath", item.getDownloadedEevntFilePath());
                intent.putExtra("StrokeFilePath", item.getDownloadedStrokeFilePath());
                intent.setAction("FromTutorialMode");
//                            FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyMoviesActivity.this, constants.Pick_Image_My_Movies, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.Pick_Image_My_Movies);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MyMoviesActivity.this, constants.LoadStrokeFile, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(this, constants.LoadStrokeFile);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDeleteClick(@Nullable model_DownloadedTutorial item, int position) {
        if (isDisplayDefault_Image)
            return;
        if (BuildConfig.DEBUG) {
            Toast.makeText(MyMoviesActivity.this, constants.DELETE_MOVIE, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.DELETE_MOVIE);
        confirmDialog(position);
    }

    @Override
    public void onShareClick(@Nullable model_DownloadedTutorial item, int position) {
        if (isDisplayDefault_Image)
            return;
        if (BuildConfig.DEBUG) {
            Toast.makeText(MyMoviesActivity.this, constants.share_movie_click, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.share_movie_click);
        File photoFile = new File(item.getDownloadedFilePath());
        Uri photoURI = FileProvider.getUriForFile(MyMoviesActivity.this,
                getString(R.string.authority),
                photoFile);

        doSocialShare(photoURI);
    }

    @Override
    public void onPostClick(@Nullable model_DownloadedTutorial item, int position) {
        if (constants.getString(constants.UserId, this).isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.login_via_dialog), Toast.LENGTH_SHORT).show();
            return;
        }
        File photoFile = new File(item.getDownloadedFilePath());
        String filePath = photoFile.getAbsolutePath();
        ArrayList<String> mImageList = new ArrayList<>();
        mImageList.add(filePath);
        Intent returnIntent = new Intent(this, PostActivity.class);
        returnIntent.putStringArrayListExtra("result", mImageList);
        startActivity(returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubMenuClick(View view, model_DownloadedTutorial item, int position) {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.my_movies_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_open:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_open, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_open);
                        objInterface.selectItem(position, false);
                        break;
                    case R.id.action_rename:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_rename, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_rename);
                        renameFileDialog(position, item.getDownloadedFilePath(), item);
                        break;
                    case R.id.action_share:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_share, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_share);
                        onShareClick(item, position);
                        break;
                    case R.id.action_download:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_download, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_download);
                        downloadMovie(item);
                        break;
                    case R.id.action_delete:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_delete, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_delete);
                        onDeleteClick(item, position);
                        break;
                    case R.id.action_cancel:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MyMoviesActivity.this, constants.my_movies_menuitem_cancel, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(MyMoviesActivity.this, constants.my_movies_menuitem_cancel);
                        popupMenu.dismiss();
                        break;

                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }

    @Override
    public void onMovieIconClick(View view, model_DownloadedTutorial item, int position) {
        objInterface.selectItem(position, false);
    }

    @Override
    public void onEditClick(View view, model_DownloadedTutorial item, int position) {
        objInterface.selectItem(position, false);
    }

    @Override
    public void onDeleteClick(View view, model_DownloadedTutorial item, int position) {
        onDeleteClick(item, position);
    }

    @Override
    public void onShareClick(View view, model_DownloadedTutorial item, int position) {
        onShareClick(item, position);
    }

    @Override
    public void onPostClick(View view, model_DownloadedTutorial item, int position) {

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MyMoviesActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MyMoviesActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void downloadMovie(model_DownloadedTutorial item) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (!checkPermission()) {
                    requestPermission();
                    return;
                }
            }

        }

        String parentFolderPath = KGlobal.getDownloadedFolderPath(this);

        File file = new File(parentFolderPath + "/" + item.getDownloadedFileName());

        String filenameWithExtention = item.getDownloadedFileName();
        String extStorageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
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
                Toast.makeText(this, "Movie is in your Videos folder", Toast.LENGTH_SHORT).show();
                MediaScannerConnection.scanFile(this, new String[]{pictureFile.getAbsolutePath()}, null, null);
            }
        }

    }

    public void renameFileDialog(final int position, final String oldFilePath, model_DownloadedTutorial movie) {
        // File rename dialog
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);
        final TextView textView = view.findViewById(R.id.textView);
        textView.setText(".mp4");

        String name = AppUtils.getFileNameWithoutExtension(new File(oldFilePath));
        input.setText(name);
        input.setSelection(0, name.length());
        input.setSelectAllOnFocus(true);
//        input.selectAll();
        input.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        renameFileBuilder.setTitle(getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".mp4";
                            rename(position, value, oldFilePath, movie);

                        } catch (Exception e) {
                            Log.e("LOG_TAG", "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    public void rename(int position, String newFileNameWithExt, String oldFilePath, model_DownloadedTutorial item) {
        //rename a file

        // Rename mp4 file's thumbnail
        String parentThumbnailFolderPath = KGlobal.getMyPaintingFolderPath(this);

        String newFilenameWithoutExt = newFileNameWithExt.substring(0, newFileNameWithExt.lastIndexOf("."));
        String oldFilenameThumbWithoutExt = item.getDownloadedFileName().substring(0, item.getDownloadedFileName().lastIndexOf("."));

        File oldFileMovieThumbnail = new File(parentThumbnailFolderPath + "/" + oldFilenameThumbWithoutExt + "." + "png");
        File newFileMovieThumbnail = new File(parentThumbnailFolderPath + "/" + newFilenameWithoutExt + "." + "png");

        // Rename mp4 file
        String parentFolderPath = KGlobal.getDownloadedFolderPath(this);
        String mFilePath = parentFolderPath + "/" + newFileNameWithExt;
        File f = new File(mFilePath);

        // Description file
//        String descFileName = name.replaceFirst("[.][^.]+$", "");
//        descFileName = descFileName + ".txt";
//        String mDescFilePath = AppUtils.getDescriptionFolderPath(this) + "/" + descFileName;
//        File mDescFile = new File(mDescFilePath);

        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot rename file.
            Toast.makeText(this,
                    String.format(getString(R.string.toast_file_exists), newFileNameWithExt),
                    Toast.LENGTH_SHORT).show();

        } else {
            //file name is unique, rename file
            // Rename mp4 file's thumbnail
            oldFileMovieThumbnail.renameTo(newFileMovieThumbnail);
            // Rename mp4 file
            File oldFile = new File(oldFilePath);
            if (oldFile.renameTo(f)) {
                item.setDownloadedFileName(newFileNameWithExt);
                item.setDownloadedFilePath(mFilePath);

                renameStrokeAndEventFiles(newFileNameWithExt, oldFilePath, item, position);

                addMedia(this, f);

            }

        }
    }

    private void renameStrokeAndEventFiles(String newFileNameWithExt, String oldFilePath,
                                           model_DownloadedTutorial item, int position) {
        String oldStrokePath = item.getDownloadedStrokeFilePath();
        String oldEventPath = item.getDownloadedEevntFilePath();

        File oldStrokeFile = new File(oldStrokePath);
        File oldEventFile = new File(oldEventPath);

        String oldStrokeFileName = oldStrokeFile.getName();
        String oldEventFileName = oldEventFile.getName();

        String newFileNameWithoutExt = newFileNameWithExt.substring(0, newFileNameWithExt.lastIndexOf("."));

        String newStrokeFileNameWithExt = "StrokeData_" + newFileNameWithoutExt + ".txt";
        String newEventFileNameWithExt = "EventData_" + newFileNameWithoutExt + ".txt";

        String newStrokePath = oldStrokePath.replace(oldStrokeFileName, newStrokeFileNameWithExt);
        String newEventPath = oldEventPath.replace(oldEventFileName, newEventFileNameWithExt);

        File newStrokeFile = new File(newStrokePath);
        File newEventFile = new File(newEventPath);

        oldStrokeFile.renameTo(newStrokeFile);
        oldEventFile.renameTo(newEventFile);

        item.setDownloadedStrokeFilePath(newStrokePath);
        item.setDownloadedEevntFilePath(newEventPath);

        lst_main.set(position, item);

        adapter.notifyDataSetChanged();
    }

    public void addMedia(Context c, File f) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(f));
        c.sendBroadcast(intent);
    }

    private void shareRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.movies_share, null);
      /*  if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            RewardSetup rewardSetup = AppUtils.getRewardSetup(MyMoviesActivity.this);
            if (rewardSetup != null) {
                FirebaseFirestoreApi.updateIncreasableRewardValue(
                        "movies_share",
                        rewardSetup.getMovies_share() == null ? 0 : rewardSetup.getMovies_share(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
            }
        }*/
    }


}
