package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.Adapter.Videos_and_file_adapter;
import com.paintology.lite.photorealistic.drawing.Adapter.Youtube_video_adapter;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.Model.Overlaid;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData;
import com.paintology.lite.photorealistic.drawing.Model.sizes;
import com.paintology.lite.photorealistic.drawing.Model.text_files;
import com.paintology.lite.photorealistic.drawing.Model.trace_image;
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item;
import com.paintology.lite.photorealistic.drawing.gallery.model_DownloadedTutorial;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo;
import com.paintology.lite.photorealistic.drawing.room.AppDatabase;
import com.paintology.lite.photorealistic.drawing.room.daos.ColorSwatchDao;
import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.PostDetail_Main_Interface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorialDetail_Activity extends AppCompatActivity implements Interface_select_item, PostDetail_Main_Interface {


    TextView tv_tutorial_title, tv_created_date, tv_plan, tv_tutorial_content, tv_gui_link, tv_video_file_head, tv_youtube_video_head, tv_more_detail, tv_try_it;

    TextView tv_visit_post_head;
    RecyclerView rv_video_files, rv_youtube_video;
    Interface_select_item _obj;
    Youtube_video_adapter adapter;
    Videos_and_file_adapter adapter_video_file;
    RatingBar rating;

    ApiInterface apiInterface;

    String postID, catID;
    PostDetailModel _object;
    Tutorial_Type tutorial_type;
    public static PostDetail_Main_Interface obj_interface;

    ImageView iv_button_back;
    String defaultLink = "https://www.paintology.com/Tutorials/left-hand-drawing-challenge-for-30-days/";

    boolean isFromDynamicLink = false;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_detail);
        _obj = this;

        db = MyApplication.getDb();

        apiInterface = ApiClient.getRetroClient().create(ApiInterface.class);
        if (getIntent().hasExtra("catID"))
            catID = getIntent().getStringExtra("catID");
        if (getIntent().hasExtra("postID"))
            postID = getIntent().getStringExtra("postID");

        if (getIntent().hasExtra("isFromDynamicLink")) {
            isFromDynamicLink = true;
        }

        Log.e("TAGGG", "Post Detail Data catID " + catID + " postID " + postID);
        obj_interface = this;
        try {
            setup();
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at oncreate " + e.getMessage() + "" + e.toString());
        }

        if (isFromDynamicLink) {
            processTutorial();
        }
    }

    void setup() {
        rv_video_files = (RecyclerView) findViewById(R.id.rv_videos_and_files);
        rv_youtube_video = (RecyclerView) findViewById(R.id.rv_youtube_video);
        tv_more_detail = (TextView) findViewById(R.id.tv_more_detail);
        tv_try_it = (TextView) findViewById(R.id.tv_try_it);
        tv_try_it.setText("");

        iv_button_back = (ImageView) findViewById(R.id.iv_button_back);
        iv_button_back.setVisibility(View.GONE);

        tv_video_file_head = (TextView) findViewById(R.id.tv_video_file_head);
        tv_youtube_video_head = (TextView) findViewById(R.id.tv_youtube_video_head);


        tv_visit_post_head = (TextView) findViewById(R.id.visitpost_head);
        tv_visit_post_head.setVisibility(View.GONE);

//        tv_youtube_video_head.setText(getString(R.string.youtube_video) + " [ " + (_object.getYoutube_video_list() != null ? _object.getYoutube_video_list().size() : 0) + " ]");
        tv_tutorial_title = (TextView) findViewById(R.id.tv_tutorial_title);
        tv_created_date = (TextView) findViewById(R.id.tv_created_date);
        tv_plan = (TextView) findViewById(R.id.tv_membership_plan);
        tv_tutorial_content = (TextView) findViewById(R.id.tv_tutorial_content);
        tv_gui_link = (TextView) findViewById(R.id.tv_gui_link);

        tv_plan.setVisibility(View.GONE);

        tv_gui_link.setPaintFlags(tv_gui_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tv_gui_link.setHorizontallyScrolling(true);
        tv_gui_link.setSelected(true);

        rating = (RatingBar) findViewById(R.id.rating);
        rating.setVisibility(View.GONE);

        getCategoryDataFromAPI(false);

        tv_try_it.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    processTutorial();
                } catch (Exception e) {
                    Log.e("TAGG", "exception at process tutorial " + e.getMessage());
                }
            }
        });
    }

    ProgressDialog progressDialog;

    void processTutorial() {

        if (tutorial_type == Tutorial_Type.See_Video) {
            String eventName = "watch_video_";
            sendEventToFirebase(eventName);
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(TutorialDetail_Activity.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getYoutube_link_list());
            intent.putExtra("isVideo", true);
            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
            String eventName = "read_post_";
            sendEventToFirebase(eventName);
            Log.e("TAGGG", "ExtLinks " + _object.getExternal_link());
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
            startActivity(browserIntent);*/
            KGlobal.openInBrowser(TutorialDetail_Activity.this, _object.getExternal_link().replace("htttps://", "https://").trim());
            return;
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
            String eventName = "watch_video_from_external_link_";
            sendEventToFirebase(eventName);

            Intent intent = new Intent(TutorialDetail_Activity.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getExternal_link());
            intent.putExtra("isVideo", true);
            Log.e("TAGGG", "URL " + _object.getExternal_link());
            startActivity(intent);
            return;
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            String eventName = "video_tutorial_overlaid_";
            sendEventToFirebase(eventName);

            String fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename();
            File file = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this) + "/" + fileName);
            String youtubeLink = _object.getYoutube_link_list();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                if (!file.exists()) {
                    new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl(), false, _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename()).execute(_object.getVideo_and_file_list().get(0).obj_overlaid.getUrl());
                    return;
                } else {
                   /* if (_object.getPost_title() != null)
                        FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());
*/
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("youtube_video_id", _youtube_id);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this));
                    startActivity(intent);
                    return;
                }
            } else {
                Toast.makeText(TutorialDetail_Activity.this, "Youtube Link Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            String eventName = "do_drawing_overlay_";
            sendEventToFirebase(eventName);
            String fileName = _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            List<ColorSwatch> swatches = _object.getSwatches();
            String pid = _object.getID();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, false, swatches, pid).execute();
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {
            String eventName = "do_drawing_trace_";
            sendEventToFirebase(eventName);
            String fileName = _object.getVideo_and_file_list().get(0).getObj_trace_image().getFilename();
            String fileURL = _object.getVideo_and_file_list().get(0).getObj_trace_image().getUrl();
            List<ColorSwatch> swatches = _object.getSwatches();
            String pid = _object.getID();
            new DownloadOverlayFromDoDrawing(fileURL, fileName, true, swatches, pid).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            String eventName = "strokes_window_";
            sendEventToFirebase(eventName);
            new DownloadsTextFiles(_object).execute();
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {

            String OverLayName = "", OverLayUrl = "";

            if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
                OverLayName = (_object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(0).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl();
            } else {
                OverLayName = (_object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() != null ? _object.getVideo_and_file_list().get(1).getObj_overlaid().getFilename() : "overLaid.jpg");
                OverLayUrl = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl();
            }

            String eventName = "strokes_overlaid_window_";
            sendEventToFirebase(eventName);

            new DownloadOverlayImage(OverLayUrl, OverLayName).execute();

        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
            try {
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    if (_object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().get(0).obj_trace_image != null && _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes() != null) {
                        if (_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge() != null) {
                            String fileName = _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().substring(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge().lastIndexOf('/') + 1);
                            File file = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this) + "/" + fileName);
                            if (!file.exists())
                                new DownloadsImage(_youtube_id, _object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge(), true, "").execute(_object.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes().getLarge());
                            else {
                               /* if (_object.getPost_title() != null)
                                    FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());
*/
                                StringConstants.IsFromDetailPage = false;
                                Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                                intent.putExtra("youtube_video_id", _youtube_id);
                                intent.setAction("YOUTUBE_TUTORIAL");
                                intent.putExtra("paint_name", file.getAbsolutePath());
                                if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvas_color());
                                }
                                startActivity(intent);
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());
                        StringConstants.IsFromDetailPage = false;
                        Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                        intent.putExtra("youtube_video_id", _youtube_id);
                        intent.setAction("YOUTUBE_TUTORIAL");
                        startActivity(intent);
                    }
                }
                String eventName = "video_tutorial_trace_";
                sendEventToFirebase(eventName);
            } catch (Exception e) {
                Toast.makeText(TutorialDetail_Activity.this, "Failed To Load!", Toast.LENGTH_SHORT).show();
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            String eventName = "read_post_default_";
            sendEventToFirebase(eventName);
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
            startActivity(browserIntent);*/
            KGlobal.openInBrowser(TutorialDetail_Activity.this, defaultLink.trim());
        }
    }

    @Override
    public void switchtoCanvas(String youtubeID) {
        StringConstants.IsFromDetailPage = false;
        Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
        intent.putExtra("youtube_video_id", youtubeID);
        intent.setAction("YOUTUBE_TUTORIAL");
        startActivity(intent);
    }

    class DownloadsImage extends AsyncTask<String, Void, String> {
        String youtubeLink, traceImageLink, fileName;
        Boolean isFromTrace = false;

        public DownloadsImage(String youtubeLink, String traceImageLink, Boolean isFromTrace, String fileName) {
            this.youtubeLink = youtubeLink;
            this.traceImageLink = traceImageLink;
            this.isFromTrace = isFromTrace;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                progressDialog = new ProgressDialog(TutorialDetail_Activity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            File path = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            File imageFile = new File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)); // Imagename.png
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
            } catch (Exception e) {
                Log.e("TAGG", "Exception at download " + e.getMessage());
            }
            return imageFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                try {

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());
*/
                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                    intent.putExtra("youtube_video_id", youtubeLink);
                    intent.setAction("YOUTUBE_TUTORIAL");
                    intent.putExtra("paint_name", path);
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color());
                    }
                    startActivity(intent);
                } else {
                    StringConstants.IsFromDetailPage = false;
                    Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this));
                    intent.putExtra("youtube_video_id", youtubeLink);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }


    class DownloadsTextFiles extends AsyncTask<Void, Void, ArrayList<String>> {
        PostDetailModel _objects;

        public DownloadsTextFiles(PostDetailModel _objects) {
            this._objects = _objects;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                progressDialog = new ProgressDialog(TutorialDetail_Activity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(TutorialDetail_Activity.this));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _objects.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _objects.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            try {


                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringConstants.IsFromDetailPage = false;
            Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
            String youtubeLink = _objects.getYoutube_link_list();
            if (youtubeLink != null) {
                String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                intent.putExtra("youtube_video_id", _youtube_id);
            }
            intent.setAction("YOUTUBE_TUTORIAL_WITH_FILE");
            if (list.size() == 2) {
                intent.putExtra("StrokeFilePath", list.get(0));
                intent.putExtra("EventFilePath", list.get(1));
            } else
                Toast.makeText(TutorialDetail_Activity.this, "Stroke Event File Not Downloaded Properly", Toast.LENGTH_SHORT).show();

//            if (_object.getPost_title() != null)
//                FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());

            startActivity(intent);
        }
    }

    public void openBrowser(View v) {
        if (tv_gui_link.getText().toString().trim().length() > 0) {
            String eventName = "click_gui_link_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
            FirebaseUtils.logEvents(TutorialDetail_Activity.this, eventName);
            if (BuildConfig.DEBUG) {
                Toast.makeText(TutorialDetail_Activity.this, eventName, Toast.LENGTH_SHORT).show();
            }
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tv_gui_link.getText().toString().trim()));
            startActivity(browserIntent);*/
            KGlobal.openInBrowser(TutorialDetail_Activity.this, tv_gui_link.getText().toString().trim());
        }
    }

    StringConstants constants = new StringConstants();

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        if (isFromRelatedPost) {

            if (BuildConfig.DEBUG) {
                Toast.makeText(TutorialDetail_Activity.this, constants.tutorial_intermediary_thumb_clicks, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(TutorialDetail_Activity.this, constants.tutorial_intermediary_thumb_clicks);

            AlertDialog.Builder dialog = new AlertDialog.Builder(TutorialDetail_Activity.this);
            dialog.setTitle("Related Post");
            dialog.setMessage("Want to load " + _object.getList_related_post().get(pos).getPost_title() + " ? ");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        dialogInterface.dismiss();
                        if (_object != null &&
                                _object.getList_related_post() != null
                                && _object.getList_related_post().size() > 0) {
                            postID = _object.getList_related_post().get(pos).getID() + "";
                            clearData();
                            getCategoryDataFromAPI(false);
                        }
                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at selectItem ");
                    }
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        } else {
            StringConstants.IsFromDetailPage = true;
            Intent intent = new Intent(TutorialDetail_Activity.this, Play_YotubeVideo.class);
            intent.putExtra("url", _object.getFeaturedImage().get(pos).getUrl());
            intent.putExtra("isVideo", _object.getFeaturedImage().get(pos).getVideoContent());
            Log.e("TAGGG", "URL " + _object.getFeaturedImage().get(pos).getUrl() + " Is Image " + _object.getFeaturedImage().get(pos).getVideoContent());
            startActivity(intent);
        }
    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {

    }

    void clearData() {
        adapter_video_file.clearList();
        adapter.clearList();
        tv_visit_post_head.setVisibility(View.GONE);
        tv_gui_link.setText("");
        tv_tutorial_content.setText("");
        tv_more_detail.setText("");
        tv_tutorial_title.setText("");
        tv_created_date.setText("");
        rating.setVisibility(View.GONE);
        tv_try_it.setText("");
        iv_button_back.setVisibility(View.GONE);
    }

    public void back(View v) {
        if (isFromDynamicLink) {
            Intent intent = new Intent(this, GalleryDashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    void getCategoryDataFromAPI(Boolean isFromRefresh) {
        Call<String> call = apiInterface.getPostDetail(ApiClient.SECRET_KEY, catID, postID);
        if (!isFromRefresh) {
            try {

                progressDialog = new ProgressDialog(TutorialDetail_Activity.this);
                progressDialog.setTitle(getResources().getString(R.string.please_wait));
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response != null && response.body() != null) {
                        parseResponseManually(response.body(), isFromRefresh);
                    } else {
                        if (TutorialDetail_Activity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                            return;
                        }
                        try {

                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showSnackBar("Failed To Load");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showSnackBar("Failed To Retrieve Content!");
                    Log.e("TAGGG", "Exception at callAPI " + t.getMessage() + " " + t.toString());
                    if (!isFromRefresh) {
                        if (TutorialDetail_Activity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                            return;
                        }
                        try {

                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            if (!isFromRefresh)
                if (TutorialDetail_Activity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                    return;
                }
            try {

                progressDialog.dismiss();
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    void showSnackBar(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void parseResponseManually(String response, boolean isFromRefresh) {
        try {
            JSONArray mainArray = new JSONArray(response);
            if (mainArray.length() > 0) {
                ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
                JSONObject objectFirst = mainArray.getJSONObject(0);
                _object = new PostDetailModel();
                _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
                _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
                _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
                _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
                _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
                _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
                _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
                _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
                _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
                _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
                _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
                _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
                _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
                _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");


                if (objectFirst.has("color_swatch")) {
                    JSONArray swatchesArray = objectFirst.getJSONArray("color_swatch");
                    ArrayList<ColorSwatch> swatches = new ArrayList<>();

                    if (swatchesArray != null && swatchesArray.length() > 0) {
                        for (int i = 0; i < swatchesArray.length(); i++) {

                            String swatch = String.valueOf(swatchesArray.getJSONObject(i).getString("color_swatch"));

                            ColorSwatch colorSwatch = new ColorSwatch();
                            colorSwatch.setColor_swatch(swatch);
                            swatches.add(colorSwatch);
                        }

                    }

                    _object.setSwatches(swatches);

                    // save swatches into database
                    ColorSwatchDao colorSwatchDao = db.colorSwatchDao();

                    ColorSwatchEntity colorSwatchEntity = new ColorSwatchEntity();
                    colorSwatchEntity.postId = Integer.parseInt(_object.getID());
                    colorSwatchEntity.swatches = new Gson().toJson(_object.getSwatches());
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            colorSwatchDao.insertAll(colorSwatchEntity);
                        }
                    });
                }

//                ArrayList<String> swatches = new ArrayList<>();
//                swatches.add("#FF0000");
//                swatches.add("#00FF00");
//                swatches.add("#0000FF");
//
//                _object.setSwatches(swatches);

                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
                    _object.setResizeImage(objectFirst.getString("ResizeImage"));
                }
                if (objectFirst.has("RelatedPostsData")) {
                    JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
                    ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
                    if (related_list_json != null && related_list_json.length() > 0) {
                        for (int i = 0; i < related_list_json.length(); i++) {
                            RelatedPostsData obj_related = new RelatedPostsData();
                            JSONObject obj = related_list_json.getJSONObject(i);
                            if (obj.has("ID")) {
                                obj_related.setID(obj.getInt("ID"));
                            }
                            if (obj.has("post_title") && obj.getString("post_title") != null) {
                                obj_related.setPost_title(obj.getString("post_title"));
                            }
                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
                                obj_related.setThumbImage(obj.getString("thumbImage"));
                            }
                            related_List.add(obj_related);
                        }
                        _object.setList_related_post(related_List);
                    }
                }
                ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
                ContentSectionModel obj_content = new ContentSectionModel();
                obj_content.setUrl(_object.getThumb_url());
                obj_content.setCaption("Featured");
                obj_content.setVideoContent(false);
                contentSectionList.add(obj_content);

                if (objectFirst.has("EmbededData")) {
                    JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
                    for (int i = 0; i < embededVideoList.length(); i++) {
                        obj_content = new ContentSectionModel();
                        JSONObject obj = embededVideoList.getJSONObject(i);
                        obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
                        obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");

//                        http://img.youtube.com/vi/GDFUdMvacI0/0.jpg
                        if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {

                            if (obj_content.getUrl().contains("youtu.be")) {
                                obj_content.setVideoContent(true);
                                String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
                            }
//                            else
//                                obj_content.setVideoContent(false);
                        }
                        contentSectionList.add(obj_content);
                    }
                }

                try {
                    if (objectFirst.has("EmbededImage")) {
                        JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
                        for (int i = 0; i < embededImageList.length(); i++) {
                            JSONObject object = embededImageList.getJSONObject(i);
                            obj_content = new ContentSectionModel();
                            obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
                            obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
                            obj_content.setVideoContent(false);
                            contentSectionList.add(obj_content);
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
                }
                _object.setFeaturedImage(contentSectionList);
                if (objectFirst.has("videos_and_files")) {

                    JSONArray videoArray = null;
                    try {
                        videoArray = objectFirst.getJSONArray("videos_and_files");
                    } catch (Exception e) {

                    }
                    if (videoArray != null)
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject obj = videoArray.getJSONObject(i);
                            videos_and_files videos_and_files = new videos_and_files();
                            if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
                                text_files obj_text_file = new text_files();
                                JSONObject obj_text = obj.getJSONObject("text_file");
                                obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
                                obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
                                obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
                                obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
                                obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
                                videos_and_files.setObj_text_files(obj_text_file);
                            } else
                                videos_and_files.setObj_text_files(null);

                            try {
                                if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
                                    trace_image obj_trace = new trace_image();
                                    JSONObject obj_trace_object = obj.getJSONObject("trace_image");
                                    obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
                                    obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
                                    obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
                                    obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
                                    obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
                                    if (obj_trace_object.has("sizes")) {
                                        JSONObject objSize = obj_trace_object.getJSONObject("sizes");
                                        sizes obj_size = new sizes();
                                        obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
                                        obj_trace.setObj_sizes(obj_size);
                                    } else {
                                        obj_trace.setObj_sizes(null);
                                    }
                                    videos_and_files.setObj_trace_image(obj_trace);
                                } else
                                    videos_and_files.setObj_trace_image(null);

                            } catch (Exception e) {
                                Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
                            }
                            try {
                                if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
                                    Overlaid overlaid = new Overlaid();
                                    JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
                                    if (obj_overlaid_object != null) {
                                        overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
                                        overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
                                        overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
                                    }
                                    videos_and_files.setObj_overlaid(overlaid);
                                } else
                                    videos_and_files.setObj_overlaid(null);

                            } catch (Exception e) {
                                Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
                            }
                            _lst_video_file.add(videos_and_files);
                        }

                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
                        _object.setVideo_and_file_list(_lst_video_file);
                } else
                    _object.setVideo_and_file_list(null);

                if (_object != null) {
                    tv_created_date.setText("Created At " + _object.getPost_date());
                    tv_tutorial_title.setText(_object.getPost_title());
                    tv_plan.setText((_object.getMembership_plan() != null ? _object.getMembership_plan() : ""));

                    String content = _object.getPost_content().replace("<!-- wp:paragraph -->\n", "").replace("<!-- /wp:paragraph -->", "").replace("\n", "");

//                    tv_tutorial_content.setText(Html.fromHtml(Html.fromHtml(_object.getPost_content()).toString()));
                    tv_tutorial_content.setText(Html.fromHtml(content));

                    if (_object.getVisitPage() != null && !_object.getVisitPage().isEmpty()) {
                        tv_gui_link.setText(_object.getVisitPage());
                        tv_gui_link.setVisibility(View.VISIBLE);
                        tv_visit_post_head.setVisibility(View.VISIBLE);
                    } else {
                        tv_gui_link.setVisibility(View.GONE);
                        tv_visit_post_head.setVisibility(View.GONE);
                    }

                    Log.d("rateVal", _object.getRating() + "");

                    if (_object.getRating() != null && _object.getRating().length() > 0 &&
                            !_object.getRating().equalsIgnoreCase("null")

                    ) {
                        rating.setRating(Integer.parseInt(_object.getRating()));
                        rating.setVisibility(View.VISIBLE);
                    }


                    if (_object.getList_related_post() != null) {
                        RecyclerView.LayoutManager mLayoutManager_1 = new GridLayoutManager(TutorialDetail_Activity.this, 1,
                                GridLayoutManager.HORIZONTAL, false);
                        rv_video_files.setLayoutManager(mLayoutManager_1);
                        tv_video_file_head.setText(getResources().getString(R.string.no_any_reference));
                        if (_object != null && _object.getList_related_post() != null) {
//                            tv_video_file_head.setText(getString(R.string.related_post) + " [ " + (_object.getList_related_post() != null ? _object.getList_related_post().size() : 0) + " ]");
                            tv_video_file_head.setText(getString(R.string.related_post));
                            adapter_video_file = new Videos_and_file_adapter(_object.getList_related_post(), TutorialDetail_Activity.this, _obj, true);
                        }

//                        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
//                                getResources().getDisplayMetrics()); // calculated
//                        rv_video_files.addItemDecoration(new SpecingDecoration(1,space));
                        rv_video_files.setAdapter(adapter_video_file);


                    } else {
                        tv_video_file_head.setText(getResources().getString(R.string.no_any_reference));
                    }
                    if (_object.getFeaturedImage() != null) {
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1,
                                GridLayoutManager.HORIZONTAL, false);
                        rv_youtube_video.setLayoutManager(mLayoutManager);
                        adapter = new Youtube_video_adapter(_object.getFeaturedImage(), this, _obj);
//                        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
//                                getResources().getDisplayMetrics()); // calculated
//                        rv_video_files.addItemDecoration(new SpecingDecoration(1,space));
                        rv_video_files.setAdapter(adapter_video_file);
                    }
                    tv_more_detail.setText(Html.fromHtml(Html.fromHtml(_object.getText_descriptions() != null ? _object.getText_descriptions() : "N/A").toString()));
//                    tv_more_detail.setText(Html.fromHtml(_object.getText_descriptions() != null ? _object.getText_descriptions() : "N/A"));
                }
            }

            if (_object != null && _object.getVideo_and_file_list() != null &&
                    _object.getVideo_and_file_list().size() >= 2 &&
                    (_object.getVideo_and_file_list().get(0).getObj_text_files() != null
                            && _object.getVideo_and_file_list().get(1).getObj_text_files() != null) &&
                    (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {

                if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null || _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;
                    tv_try_it.setText("DO OVERLAY TUTORIAL (STROKES)");
                } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                    tv_try_it.setText("DO TRACE TUTORIAL (STROKES)");
                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                    tv_try_it.setText("DO STROKES TUTORIAL");
                }
            } else if (_object != null && _object.getVideo_and_file_list() != null
                    && _object.getVideo_and_file_list().size() >= 2 &&
                    (_object.getVideo_and_file_list().get(0).getObj_text_files() != null
                            && _object.getVideo_and_file_list().get(1).getObj_text_files() != null)
                    && (_object.getYoutube_link_list() == null
                    || _object.getYoutube_link_list().isEmpty())) {
                if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null ||
                        _object.getVideo_and_file_list().get(1).getObj_overlaid() != null) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window;
                    tv_try_it.setText("DO OVERLAY TUTORIAL (STROKES)");
                } else if (_object.getVideo_and_file_list().get(0).getObj_trace_image() == null || _object.getVideo_and_file_list().get(1).getObj_trace_image() == null) {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                    tv_try_it.setText("DO TRACE TUTORIAL (STROKES)");
                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window;
                    tv_try_it.setText("DO STROKES TUTORIAL");
                }
            } else if (_object != null && _object.getVideo_and_file_list() != null &&
                    _object.getVideo_and_file_list().size() > 0
                    && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null &&
                    (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
                tv_try_it.setText("DO TRACE TUTORIAL");
                tutorial_type = Tutorial_Type.Video_Tutorial_Trace;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && (_object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty())) {
                tv_try_it.setText("DO OVERLAY TUTORIAL");
                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()) {
                tv_try_it.setText("DO DRAWING!");
                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY;
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size() > 0 && _object.getVideo_and_file_list().get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()) {
                tv_try_it.setText("DO DRAWING!");
                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE;
            } else if (_object.getExternal_link() != null && !_object.getExternal_link().isEmpty()) {
                if (_object.getExternal_link().contains("youtu.be")) {
                    tv_try_it.setText("WATCH VIDEO");
                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link;
                } else {
                    tv_try_it.setText("READ POST");
                    tutorial_type = Tutorial_Type.Read_Post;
                }
            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list().isEmpty()) {

                tv_try_it.setText("WATCH VIDEO");
                tutorial_type = Tutorial_Type.See_Video;

            } else {
                tv_try_it.setText("READ POST");
                tutorial_type = Tutorial_Type.READ_POST_DEFAULT;
            }
            iv_button_back.setVisibility(View.VISIBLE);


            if (!isFromRefresh && progressDialog != null) {
                progressDialog.dismiss();
            }

        } catch (Exception e) {
            try {
                if (!isFromRefresh && progressDialog != null) {
                    progressDialog.dismiss();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            Log.e("TAGGG", "Exception at parse " + e.getMessage() + " " + e.getStackTrace().toString());
        }
    }

    enum Tutorial_Type {
        Read_Post, SeeVideo_From_External_Link, See_Video, Video_Tutorial_Trace, Video_Tutorial_Overraid, Strokes_Window, Strokes_Overlaid_Window, DO_DRAWING_OVERLAY, DO_DRAWING_TRACE, READ_POST_DEFAULT;
    }

    class DownloadOverlayImage extends AsyncTask<Void, Void, ArrayList<String>> {
        String traceImageLink, fileName;

        public DownloadOverlayImage(String traceImageLink, String fileName) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                progressDialog = new ProgressDialog(TutorialDetail_Activity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Void... strings) {

            ArrayList<String> filesList = downloadTextFiles();

            File file = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this), fileName);

            if (file.exists()) {
                return filesList;
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Create Path to save Image
                File path = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
                File imageFile = new File(path, fileName); // Imagename.png
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e.getMessage());
                }
                return filesList;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> lst_main) {
            super.onPostExecute(lst_main);
            try {

                try {
                    progressDialog.dismiss();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try " + _object.getPost_title());

                StringConstants.IsFromDetailPage = false;
                Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                String youtubeLink = _object.getYoutube_link_list();
                if (youtubeLink != null) {
                    String _youtube_id = youtubeLink.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                    intent.putExtra("youtube_video_id", _youtube_id);
                }
                intent.setAction("YOUTUBE_TUTORIAL_WITH_OVERLAID");
                if (lst_main.size() == 2) {
                    intent.putExtra("StrokeFilePath", lst_main.get(0));
                    intent.putExtra("EventFilePath", lst_main.get(1));
                }
                intent.putExtra("OverlaidImagePath", new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this), fileName).getAbsolutePath());
                startActivity(intent);
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }


        public ArrayList<String> downloadTextFiles() {
            ArrayList<String> lst_fileNames = new ArrayList<>();
            File file1 = new File(KGlobal.getStrokeEventFolderPath(TutorialDetail_Activity.this));
            if (!file1.exists()) {
                file1.mkdirs();
            }
            for (int i = 0; i < 2; i++) {
                String textFileLink = _object.getVideo_and_file_list().get(i).getObj_text_files().getUrl();
                String fileName = _object.getVideo_and_file_list().get(i).getObj_text_files().getFilename();

                File file = new File(file1, fileName);
                if (file.exists()) {
                    lst_fileNames.add(file.getAbsolutePath());
                } else {
                    try {
                        URL url = new URL(textFileLink);
                        URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(50000);
                        ucon.setConnectTimeout(100000);

                        InputStream is = ucon.getInputStream();
                        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                        if (file.exists()) {
                            lst_fileNames.add(file.getAbsolutePath());
                            break;
                        }
                        FileOutputStream outStream = new FileOutputStream(file);
                        byte[] buff = new byte[5 * 1024];

                        int len;
                        while ((len = inStream.read(buff)) != -1) {
                            outStream.write(buff, 0, len);
                        }
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        lst_fileNames.add(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return lst_fileNames;
        }
    }


    class DownloadOverlayFromDoDrawing extends AsyncTask<Void, Void, String> {
        String traceImageLink, fileName;
        Boolean isFromTrace = false;
        private List<ColorSwatch> swatches;
        private String id;

        public DownloadOverlayFromDoDrawing(String traceImageLink, String fileName,
                                            Boolean isFromTrace,
                                            List<ColorSwatch> swatches, String pid) {
            this.traceImageLink = traceImageLink;
            this.fileName = fileName;
            this.isFromTrace = isFromTrace;
            this.swatches = swatches;
            this.id = pid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                progressDialog = new ProgressDialog(TutorialDetail_Activity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... strings) {

            File file = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this), fileName);
            if (file.exists()) {
                return file.getAbsolutePath();
            } else {
                URL url = null;
                try {
                    url = new URL(traceImageLink);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Create Path to save Image
                File path = new File(KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this)); //Creates app specific folder

                if (!path.exists()) {
                    path.mkdirs();
                }
                File imageFile = new File(path, fileName); // Imagename.png
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at download " + e.getMessage());
                }
                return imageFile.getAbsolutePath();
            }
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            try {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* if (_object.getPost_title() != null)
                    FirebaseUtils.logEvents(TutorialDetail_Activity.this, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));
*/
                StringConstants.IsFromDetailPage = false;
                if (isFromTrace) {
                    Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                    intent.setAction("Edit Paint");
                    intent.putExtra("FromLocal", true);
                    intent.putExtra("paint_name", path);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TutorialDetail_Activity.this, PaintActivity.class);
                    intent.setAction("LoadWithoutTrace");
                    intent.putExtra("path", fileName);
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(TutorialDetail_Activity.this));

                    Gson gson = new Gson();
                    String swatchesJson = gson.toJson(swatches);

                    intent.putExtra("swatches", swatchesJson);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                Log.e("TAGGG", "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + path);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at post " + e.toString());
            }
        }
    }

    void sendEventToFirebase(String eventType) {
        try {
            String eventName = eventType + ((_object != null && _object.getPost_title() != null) ? _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") : "");

            if (eventName.length() >= 35) {
                String upToNCharacters = eventName.substring(0, Math.min(eventName.length(), 35));
                FirebaseUtils.logEvents(this, upToNCharacters);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(TutorialDetail_Activity.this, upToNCharacters, Toast.LENGTH_SHORT).show();
                }
            } else {
                FirebaseUtils.logEvents(this, eventName);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(TutorialDetail_Activity.this, eventName, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at send event " + e.getMessage());
        }
    }

    @Override
    public void onSubMenuClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onMovieIconClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onEditClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onDeleteClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onShareClick(View view, model_DownloadedTutorial item, int position) {

    }

    @Override
    public void onPostClick(View view, model_DownloadedTutorial item, int position) {

    }
}

