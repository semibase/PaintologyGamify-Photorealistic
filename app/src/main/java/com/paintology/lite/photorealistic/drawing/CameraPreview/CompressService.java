package com.paintology.lite.photorealistic.drawing.CameraPreview;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;

import java.io.File;
import java.io.FileInputStream;


public class CompressService extends IntentService {

    private static final String DEBUG_TAG = "TutListDownloaderService";
    private DownloadImageTask tutorialDownloader;
    //    ReceivedGroupMessage MessageObjects;
    boolean isImage = true;
    Integer FileType = 0;

    Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAGGG", "Copy Path from >onCreate> ");
        gson = new Gson();
    }

    public CompressService() {
        super(CompressService.class.getName());
    }

    /*
    public CompressService(String name, ReceivedGroupMessage MessageObjects) {
        super(name);
        this.MessageObjects = MessageObjects;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TBD

        try {
            final String receiver = intent.getStringExtra("messageobj");
            FileType = intent.getIntExtra("FileType", 0);
            Log.e("TAGGG", "Copy Path from >onStartCommand> " + receiver);

            Matrix Sample_matrix = CheckMatrix(receiver);

            tutorialDownloader = new DownloadImageTask(receiver, isImage, Sample_matrix);
            tutorialDownloader.execute();
        } catch (Exception e) {
            Log.e("TAG", "OnStartCommand Service " + e.getMessage());
        }
        return Service.START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.e("TAGGG", "Copy Path from >onHandleIntent> ");

    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        String MessageObjects;
        boolean isImage;
        Matrix Sample_matrix;

        public DownloadImageTask(String MessageObjects, boolean isImage, Matrix Sample_matrix) {
            this.MessageObjects = MessageObjects;
            this.isImage = isImage;
            this.Sample_matrix = Sample_matrix;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("TAGGG", "Copy Path from >onPreExecute> ");
        }

        protected Bitmap doInBackground(String... urls) {


            Bitmap mIcon11 = null;
            try {
                //InputStream in = new java.net.URL(MessageObjects).openStream();
                FileInputStream in = new FileInputStream(new File(MessageObjects));
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "Copy Path from >onStartCommand> " + e.getMessage(), e);
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                ImageManageModel LocalImageObj;
                if (Sample_matrix != null) {
                    Log.e("TAGGG", "Check Steps Here >< 121. ");
                    LocalImageObj = KGlobal.scaleAndSaveImageWithMatrix(result, result.getWidth(), result.getHeight(), 70, false, true, isImage, Sample_matrix);
                } else {
                    Log.e("TAGGG", "Check Steps Here >< 11. ");
                    LocalImageObj = KGlobal.scaleAndSaveImage(result, result.getWidth(), result.getHeight(), 70, false, true, isImage, "");
                }

                Log.e("TAGGG", "Copy Path from >Result> " + LocalImageObj.isDone() + " PATH " + LocalImageObj.getLocalImagePath());

                SandriosCamera._sample.DownloadCaptureCompleted(LocalImageObj.isDone(), LocalImageObj, FileType);

                stopSelf();
            } catch (Exception e) {
                Log.e("TAG", "Exception at onPost CompressService " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAGGG", "Copy Path from >onDestroy> ");
    }

    private Matrix CheckMatrix(String exifPath) {
        Matrix matrix = null;

        if (exifPath != null) {
            ExifInterface exif;
            try {
                exif = new ExifInterface(exifPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);

                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);

                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (Exception e) {
                Log.e("tmessages", "Errror Here ori>  " + e.getMessage());
            }
        }
        return matrix;
    }


}
