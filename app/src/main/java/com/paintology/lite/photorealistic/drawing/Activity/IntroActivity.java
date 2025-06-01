package com.paintology.lite.photorealistic.drawing.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.core.base.AsyncTaskExecutorService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayerView;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IntroActivity extends AppCompatActivity {

    int step = 0;
    StringConstants constants = new StringConstants();
    private final String fileName = "duck_image.png";

    boolean isFromHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (getIntent() != null && getIntent().hasExtra("isFromHome")) {
            isFromHome = true;
        }
        saveAndGetImagePath();

        Intent intent = getIntent();
        if (intent != null) {
            step = intent.getIntExtra("step", 0);
        }

        videoIntro();
    }

    private void saveAndGetImagePath() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.duck_image);
        new SaveImageTask(fileName).execute(bitmap);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (isFromHome) {
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.exit_app_msg))
                    .setPositiveButton(
                            getString(R.string.quit), (dialog, which) -> {
                                MyApplication.setAppUsedCountSeen(false);
                                finishAffinity();
                            }
                    )
                    .setNegativeButton(
                            getString(R.string.cancel), (dialog, which) -> {
                                startActivity(new Intent(IntroActivity.this, GalleryDashboard.class));
                                finish();
                            }
                    )
                    .show();
        }

    }

    TextView introText;
    ImageButton replay, btn_Next;
    RelativeLayout introButtons_Layout;
    LinearLayout intro_text_layout;
    YouTubePlayerView videoView;

    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView youTubePlayerView;

    public void videoIntro() {
        replay = findViewById(R.id.intro_reply);
        replay.setOnClickListener(v -> {
            playVideoFromYoutube();
        });
        btn_Next = findViewById(R.id.intro_next);
        btn_Next.setOnClickListener(v -> {
            int mPrefBackgroundColor = -1;
            Intent intent = new Intent();
            String str = "First Time Draw";
            if (step == 3) {
                str = "LoadWithoutTrace";
                intent.putExtra("path", fileName);
                intent.putExtra("isPaintIntro", true);
                intent.putExtra("drawingType", "TUTORAILS");
                intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this));
                String swatchesJson = "[{\"color_swatch\":\"#fff600\"},{\"color_swatch\":\"#ffb832\"},{\"color_swatch\":\"#4fbee9\"},{\"color_swatch\":\"#ffffff\"},{\"color_swatch\":\"#040404\"}]";
                intent.putExtra("swatches", swatchesJson);
                intent.putExtra("id", "8135");
            } else {
                constants.putInt("background_color", mPrefBackgroundColor, IntroActivity.this);
                intent.putExtra("background_color", mPrefBackgroundColor);
            }
            intent.setAction(str);
            intent.setClass(IntroActivity.this, PaintActivity.class);
            intent.putExtra("step", step);
            startActivity(intent);
            FirebaseUtils.logEvents(IntroActivity.this, constants.draw_screen_blank_canvas);
            finish();
        });
        // videoView=findViewById(R.id.video_intro);
//        videoView = findViewById(R.id.video_intro);

        youTubePlayerView = findViewById(R.id.video_intro);
        getLifecycle().addObserver(youTubePlayerView);

        // videoView.setVisibility(View.VISIBLE);
        introText = findViewById(R.id.intro_text);
        introButtons_Layout = findViewById(R.id.intro_buttons_layout);
        introButtons_Layout.setVisibility(View.GONE);
        intro_text_layout = findViewById(R.id.intro_text_layout);
        intro_text_layout.setVisibility(View.VISIBLE);


        // Set media controller
//        MediaController mediaController=new MediaController(this);
//        videoView.setMediaController(mediaController);
//        mediaController.setAnchorView(videoView);


//        getLifecycle().addObserver(videoView);
//        videoView.hideToggle();

        playVideoFromYoutube();


//        String path = "android.resource://" + getPackageName() + "/" + R.raw.draw_button_line;
//        if(step==0) {
//            // Set the path of the video file
//            path = "android.resource://" + getPackageName() + "/" + R.raw.draw_button_line;
//            introText.setText("The brush tool");
//        }else if(step==1){
//            path = "android.resource://" + getPackageName() + "/" + R.raw.color_picker;
//            introText.setText("Color picker tool");
//        } else if (step==2) {
//            path = "android.resource://" + getPackageName() + "/" + R.raw.color_bar;
//            introText.setText("Color bar");
//        }
//        // Set video URI
//        videoView.setVideoURI(Uri.parse(path));
//
//        // Start playing the video
//        videoView.start();

        // Listen for completion event to loop the video
//        videoView.setOnCompletionListener(mp ->{
//                    replay.setVisibility(View.VISIBLE);
//                    btn_Next.setVisibility(View.VISIBLE);
//                    introText.setText("Try it!");
//                }
//                );

    }

    String video_1 = "", video_2 = "", video_3 = "", video_4 = "";
    String title_1 = "", title_2 = "", title_3 = "", title_4 = "";

    void playVideoFromYoutube() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create Remote Config setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600) // Fetch at least every 3600 seconds (1 hour).
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);


        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d("TAG", "Config params updated: " + updated);

                            // Retrieve the string value associated with "your_string_key"
                            String video_1_json = mFirebaseRemoteConfig.getString("learn_draw_1");
                            String video_2_json = mFirebaseRemoteConfig.getString("learn_draw_2");
                            String video_3_json = mFirebaseRemoteConfig.getString("learn_draw_3");
                            String video_4_json = mFirebaseRemoteConfig.getString("learn_draw_4");

                            try {
                                JSONArray jsonArray_1 = new JSONArray(video_1_json);
//                                JSONArray jsonArray_1 = new JSONArray(video_4_json);
                                for (int i = 0; i < jsonArray_1.length(); i++) {
                                    JSONObject jsonObject = jsonArray_1.getJSONObject(i);
                                    video_1 = jsonObject.getString("videoCode");
                                    title_1 = jsonObject.getString("videoTitle");

                                }
                                JSONArray jsonArray_2 = new JSONArray(video_2_json);
                                for (int i = 0; i < jsonArray_2.length(); i++) {
                                    JSONObject jsonObject = jsonArray_2.getJSONObject(i);
                                    video_2 = jsonObject.getString("videoCode");
                                    title_2 = jsonObject.getString("videoTitle");

                                }
                                JSONArray jsonArray_3 = new JSONArray(video_3_json);
                                for (int i = 0; i < jsonArray_3.length(); i++) {
                                    JSONObject jsonObject = jsonArray_3.getJSONObject(i);
                                    video_3 = jsonObject.getString("videoCode");
                                    title_3 = jsonObject.getString("videoTitle");
                                }
//                                JSONArray jsonArray_4 = new JSONArray(video_1_json);
                                JSONArray jsonArray_4 = new JSONArray(video_4_json);
                                for (int i = 0; i < jsonArray_4.length(); i++) {
                                    JSONObject jsonObject = jsonArray_4.getJSONObject(i);
                                    video_4 = jsonObject.getString("videoCode");
                                    title_4 = jsonObject.getString("videoTitle");
                                }
                                playVideo();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            Log.e("JSS", "Fetch=" + video_4 + " " + title_4);

                            // You can further process the retrieved string value as needed.
                        } else {
                            Log.e("TAG", "Fetch failed");
                        }
                    }
                });

    }

    void playVideo() {

        youTubePlayerView.addYouTubePlayerListener(new com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {

            @Override
            public void onStateChange(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                if (state == PlayerConstants.PlayerState.ENDED) {
                    intro_text_layout.setVisibility(View.GONE);
                    introButtons_Layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReady(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String videoId = video_1;
                if (step == 0) {
                    videoId = video_1;
                    introText.setText(title_1);
                } else if (step == 1) {
                    videoId = video_2;
                    introText.setText(title_2);
                } else if (step == 2) {
                    videoId = video_3;
                    introText.setText(title_3);
                } else if (step == 3) {
                    videoId = video_4;
                    introText.setText(title_4);
                }

                try {
                    youTubePlayer.loadVideo(videoId, 0f);
                } catch (IllegalStateException exception) {
                    Toast.makeText(IntroActivity.this, getString(R.string.s_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                Toast.makeText(IntroActivity.this, getString(R.string.s_wrong), Toast.LENGTH_SHORT).show();
            }
        });


        /*videoView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onStateChange(int state) {
                        super.onStateChange(state);
                        if (state == 0) {
                            intro_text_layout.setVisibility(View.GONE);
                            introButtons_Layout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onReady() {
                        Log.d("VideoStep", "onReady: " + step);
                        String videoId = video_1;
                        if (step == 0) {
                            videoId = video_1;
                            introText.setText(title_1);
                        } else if (step == 1) {
                            videoId = video_2;
                            introText.setText(title_2);
                        } else if (step == 2) {
                            videoId = video_3;
                            introText.setText(title_3);
                        } else if (step == 3) {
                            videoId = video_4;
                            introText.setText(title_4);
                        }
                        initializedYouTubePlayer.loadVideo(videoId, 0);
                    }
                });
            }

        }, true);*/
    }


    class SaveImageTask extends AsyncTaskExecutorService<Bitmap, Void, String> {
        private final String fileName;

        SaveImageTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(Bitmap bitmap) {
            // Return path if file already exists
            File file = new File(KGlobal.getTraceImageFolderPath(IntroActivity.this), fileName);
            if (file.exists()) {
                return file.getAbsolutePath();
            }

            //Create Path to save Image
            File path = new File(KGlobal.getTraceImageFolderPath(IntroActivity.this)); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }
            File imageFile = new File(path, fileName); // Imagename.png
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                Log.e("IntroException", "File Exception: " + e.getMessage());
            }
            if (out != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.e("IntroException", "Bitmap Exception: " + e.getMessage());
                }
            }
            return imageFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String filePath) {
            Log.e("IntroException", "Saved File Path: " + filePath);
        }
    }
}