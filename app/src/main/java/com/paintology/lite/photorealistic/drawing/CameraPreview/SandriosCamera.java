package com.paintology.lite.photorealistic.drawing.CameraPreview;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.CameraConfiguration;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.CameraOutputModel;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ui.camera.Camera1Activity;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ui.camera2.Camera2Activity;
import com.paintology.lite.photorealistic.drawing.CameraPreview.utils.CameraHelper;
import com.paintology.lite.photorealistic.drawing.CameraPreview.utils.SandriosBus;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Sandrios Camera Builder Class
 * Created by Arpit Gandhi on 7/6/16.
 */
public class SandriosCamera implements Update_DownloadMedia {

    private static SandriosCamera mInstance = null;
    private static AppCompatActivity mActivity;
    private int mediaAction = CameraConfiguration.MEDIA_ACTION_BOTH;
    private boolean showPicker = true;
    private boolean autoRecord = false;
    private int type = 501;
    private boolean enableImageCrop = false;
    private long videoSize = -1;
    public static Update_DownloadMedia _sample;
    static CameraCallback cameraCallback;
    static CameraOutputModel outputModel;

    public static SandriosCamera with(AppCompatActivity activity) {
        if (mInstance == null) {
            mInstance = new SandriosCamera();
        }
        mActivity = activity;
        return mInstance;
    }

    public SandriosCamera setShowPickerType(int type) {
        this.type = type;
        return mInstance;
    }

    public SandriosCamera setShowPicker(boolean showPicker) {
        this.showPicker = showPicker;
        return mInstance;
    }

    public SandriosCamera setMediaAction(int mediaAction) {
        this.mediaAction = mediaAction;
        return mInstance;
    }

    public SandriosCamera enableImageCropping(boolean enableImageCrop) {
        this.enableImageCrop = enableImageCrop;
        return mInstance;
    }

    @SuppressWarnings("SameParameterValue")
    public SandriosCamera setVideoFileSize(int fileSize) {
        this.videoSize = fileSize;
        return mInstance;
    }

    /**
     * Only works if Media Action is set to Video
     */
    public SandriosCamera setAutoRecord() {
        autoRecord = true;
        return mInstance;
    }

    public void launchCamera(final CameraCallback cameraCallback) {
        _sample = this;
        Dexter.withActivity(mActivity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        launchIntent();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {

                    }

                }).check();

        SandriosBus.getBus()
                .toObserverable()
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof CameraOutputModel) {
                            CameraOutputModel outputModel = (CameraOutputModel) o;
                            if (cameraCallback != null) {
                                if (outputModel.getType() == 0) {
                                    File SourceFile = new File(outputModel.getPath());
                                    Intent intent = new Intent(MyApplication.getInstance().getApplicationContext(), CompressService.class);
                                    intent.putExtra("messageobj", SourceFile.getAbsolutePath());
                                    intent.putExtra("FileType", outputModel.getType());
                                    MyApplication.getInstance().getApplicationContext().startService(intent);
                                } else {
                                    cameraCallback.onComplete(outputModel);
                                }

//                                Intent intent = new Intent(mActivity, PreviewScreen.class);
//                                intent.putExtra("FilePath", outputModel.getPath());
//                                intent.putExtra("FileType", outputModel.getType());
//                                mActivity.startActivityForResult(intent, 102);
                                //cameraCallback.onComplete(outputModel);
                                SandriosCamera.outputModel = outputModel;
                                SandriosCamera.cameraCallback = cameraCallback;
                                mInstance = null;
                            }
                            SandriosBus.complete();
                        }
                    }
                });
    }

    public void launchIntent() {
        if (CameraHelper.hasCamera(mActivity)) {
            Intent cameraIntent;
            if (CameraHelper.hasCamera2(mActivity)) {
                cameraIntent = new Intent(mActivity, Camera2Activity.class);
            } else {
                cameraIntent = new Intent(mActivity, Camera1Activity.class);
            }
            cameraIntent.putExtra(CameraConfiguration.Arguments.SHOW_PICKER, showPicker);
            cameraIntent.putExtra(CameraConfiguration.Arguments.PICKER_TYPE, type);
            cameraIntent.putExtra(CameraConfiguration.Arguments.MEDIA_ACTION, mediaAction);
            cameraIntent.putExtra(CameraConfiguration.Arguments.ENABLE_CROP, enableImageCrop);
            cameraIntent.putExtra(CameraConfiguration.Arguments.AUTO_RECORD, autoRecord);

            if (videoSize > 0) {
                Log.e("TAGGGG", "cameraIntent.putExtra Redirect > " + videoSize);
                cameraIntent.putExtra(CameraConfiguration.Arguments.VIDEO_FILE_SIZE, videoSize * 1024 * 1024);
            }
            mActivity.startActivity(cameraIntent);
        }
    }


    public interface CameraCallback {
        void onComplete(CameraOutputModel cameraOutputModel);
    }

    public class MediaType {
        public static final int PHOTO = 0;
        public static final int VIDEO = 1;
    }


    @Override
    public void DownloadCompleted(boolean isDone, ImageManageModel LocalImageObj) {

    }

    @Override
    public void DownloadCaptureCompleted(boolean isDone, ImageManageModel LocalImageObj, Integer FileType) {
        Intent intent = new Intent();
        if (LocalImageObj.isDone()) {
            intent.putExtra("FilePath", LocalImageObj.getLocalImagePath());
            intent.putExtra("FileType", FileType);
            cameraCallback.onComplete(new CameraOutputModel(FileType, LocalImageObj.getLocalImagePath()));

        } else {
            intent.putExtra("FilePath", outputModel != null ? outputModel.getPath() : "");
            intent.putExtra("FileType", outputModel != null ? outputModel.getType() : 0);
            cameraCallback.onComplete(outputModel);
        }

        /*setResult(RESULT_OK, intent);
        finish();*/
    }


}
