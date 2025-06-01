package com.paintology.lite.photorealistic.drawing.CameraPreview.controller;

import android.os.Bundle;

import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.CameraConfiguration;
import com.paintology.lite.photorealistic.drawing.CameraPreview.manager.CameraManager;

import java.io.File;


/**
 * Created by Arpit Gandhi on 7/6/16.
 */
public interface CameraController<CameraId> {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void takePhoto();

    void startVideoRecord();

    void stopVideoRecord();

    boolean isVideoRecording();

    void switchCamera(@CameraConfiguration.CameraFace int cameraFace);

    void switchQuality();

    int getNumberOfCameras();

    @CameraConfiguration.MediaAction
    int getMediaAction();

    CameraId getCurrentCameraId();

    File getOutputFile();

    CameraManager getCameraManager();

    void setFlashMode(@CameraConfiguration.FlashMode int flashMode);

}
