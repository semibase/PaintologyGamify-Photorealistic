package com.paintology.lite.photorealistic.drawing.minipaint;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Community.Community;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.SubCategoryActivity;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.TutorialDetail_Activity;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Youtube.player.AbstractYouTubePlayerListener;
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayer;
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayerView;
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item;
import com.paintology.lite.photorealistic.drawing.gallery.model_DownloadedTutorial;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class Play_YotubeVideo extends AppCompatActivity implements Interface_select_item {

    DisplayImageOptions mDisplayImageOptions;
    ImageLoaderConfiguration conf;

    ImageLoader mImageLoader;

    YouTubePlayerView youTubePlayerView;

    public static Interface_select_item objRedirect;
    String _youtube_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play__yotube_video);

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.abc_ab_share_pack_mtrl_alpha)
                /*.showImageOnLoading(R.drawable.loading_bg)
                .showImageOnLoading(R.drawable.loading_bg)*/
                .cacheInMemory(false)
                .cacheOnDisc(false)
                .build();

        conf = new ImageLoaderConfiguration.Builder(Play_YotubeVideo.this)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .writeDebugLogs()
                .threadPoolSize(5)
                .build();

        objRedirect = this;

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

        ImageView iv = (ImageView) findViewById(R.id.iv_featured);
        String url = "";

        Boolean isVideo = false;
        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
        }
        if (getIntent().hasExtra("isVideo")) {
            isVideo = getIntent().getBooleanExtra("isVideo", false);
        }

        if (!isVideo) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.thumbnaildefault);
            Log.e("TAGGG", "URL have to load  isVideo " + isVideo);
            mImageLoader.displayImage(url, iv);
        } else {

            _youtube_id = url.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
//            Log.e("TAGGG", "URL have to load on else " + _youtube_id + " isVideo " + isVideo);
//            playVideo(url);
            iv.setVisibility(View.GONE);
            youTubePlayerView = findViewById(R.id.yt_player);
            youTubePlayerView.setVisibility(View.VISIBLE);
            playVideo(_youtube_id);
        }

        if (getIntent().hasExtra("hideToggle")) {
            youTubePlayerView.hideToggle();
        }

    }

    void playVideo(String url) {
        initYouTubePlayerView(url, youTubePlayerView);
    }

    private void initYouTubePlayerView(String videoID, YouTubePlayerView youTubePlayerView) {

        youTubePlayerView.getPlayerUIController().showMenuButton(true);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(youTubePlayer -> {

            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    loadVideo(youTubePlayer, videoID);
                }
            });
        }, true);
    }

    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(Play_YotubeVideo.this, new StringConstants().switch_to_canvas, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(Play_YotubeVideo.this, new StringConstants().switch_to_canvas);
        if (TutorialDetail_Activity.obj_interface != null)
            TutorialDetail_Activity.obj_interface.switchtoCanvas(_youtube_id);
        else if (SubCategoryActivity.obj_interface != null)
            SubCategoryActivity.obj_interface.switchtoCanvas(_youtube_id);
        else if (Community.obj_interface != null)
            Community.obj_interface.switchtoCanvas(_youtube_id);

        finish();
    }

    @Override
    public void openTutorialDetail(String cat_id, String tut_id, int pos) {

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
