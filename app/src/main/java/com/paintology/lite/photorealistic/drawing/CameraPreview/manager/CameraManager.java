package com.paintology.lite.photorealistic.drawing.CameraPreview.manager;

import android.content.Context;

import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.CameraConfiguration;
import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.ConfigurationProvider;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.listener.CameraCloseListener;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.listener.CameraOpenListener;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.listener.CameraPhotoListener;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.listener.CameraVideoListener;
import com.paintology.lite.photorealistic.drawing.CameraPreview.utils.Size;

import java.io.File;


/**
 * Created by Arpit Gandhi on 8/14/16.
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@CameraConfiguration.MediaQuality int mediaQuality);

    void setFlashMode(@CameraConfiguration.FlashMode int flashMode);

    void stopVideoRecord();

    void releaseCameraManager();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();
}
