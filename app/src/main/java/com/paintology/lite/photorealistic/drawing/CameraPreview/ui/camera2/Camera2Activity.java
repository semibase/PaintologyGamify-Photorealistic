package com.paintology.lite.photorealistic.drawing.CameraPreview.ui.camera2;

import android.annotation.TargetApi;
import android.media.CamcorderProfile;
import android.os.Build;

import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.CameraConfiguration;
import com.paintology.lite.photorealistic.drawing.CameraPreview.configuration.ConfigurationProvider;
import com.paintology.lite.photorealistic.drawing.CameraPreview.controller.CameraController;
import com.paintology.lite.photorealistic.drawing.CameraPreview.controller.impl.Camera2Controller;
import com.paintology.lite.photorealistic.drawing.CameraPreview.controller.impl.Camera2ControllerAPI24;
import com.paintology.lite.photorealistic.drawing.CameraPreview.controller.view.CameraView;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ui.BaseSandriosActivity;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ui.model.PhotoQualityOption;
import com.paintology.lite.photorealistic.drawing.CameraPreview.ui.model.VideoQualityOption;
import com.paintology.lite.photorealistic.drawing.CameraPreview.utils.CameraHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arpit Gandhi on 7/6/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends BaseSandriosActivity<String> {

    @Override
    public CameraController<String> createCameraController(CameraView cameraView, ConfigurationProvider configurationProvider) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return new Camera2ControllerAPI24(cameraView, configurationProvider);
        else
            return new Camera2Controller(cameraView, configurationProvider);
    }

    @Override
    protected CharSequence[] getVideoQualityOptions() {
        List<CharSequence> videoQualities = new ArrayList<>();

        if (getMinimumVideoDuration() > 0)
            videoQualities.add(new VideoQualityOption(CameraConfiguration.MEDIA_QUALITY_AUTO, CameraHelper.getCamcorderProfile(CameraConfiguration.MEDIA_QUALITY_AUTO, getCameraController().getCurrentCameraId()), getMinimumVideoDuration()));


        CamcorderProfile camcorderProfile = CameraHelper.getCamcorderProfile(CameraConfiguration.MEDIA_QUALITY_HIGH, getCameraController().getCurrentCameraId());
        double videoDuration = CameraHelper.calculateApproximateVideoDuration(camcorderProfile, getVideoFileSize());
        videoQualities.add(new VideoQualityOption(CameraConfiguration.MEDIA_QUALITY_HIGH, camcorderProfile, videoDuration));

        camcorderProfile = CameraHelper.getCamcorderProfile(CameraConfiguration.MEDIA_QUALITY_MEDIUM, getCameraController().getCurrentCameraId());
        videoDuration = CameraHelper.calculateApproximateVideoDuration(camcorderProfile, getVideoFileSize());
        videoQualities.add(new VideoQualityOption(CameraConfiguration.MEDIA_QUALITY_MEDIUM, camcorderProfile, videoDuration));

        camcorderProfile = CameraHelper.getCamcorderProfile(CameraConfiguration.MEDIA_QUALITY_LOW, getCameraController().getCurrentCameraId());
        videoDuration = CameraHelper.calculateApproximateVideoDuration(camcorderProfile, getVideoFileSize());
        videoQualities.add(new VideoQualityOption(CameraConfiguration.MEDIA_QUALITY_LOW, camcorderProfile, videoDuration));

        CharSequence[] array = new CharSequence[videoQualities.size()];
        videoQualities.toArray(array);

        return array;
    }

    @Override
    protected CharSequence[] getPhotoQualityOptions() {
        List<CharSequence> photoQualities = new ArrayList<>();
        photoQualities.add(new PhotoQualityOption(CameraConfiguration.MEDIA_QUALITY_HIGHEST, getCameraController().getCameraManager().getPhotoSizeForQuality(CameraConfiguration.MEDIA_QUALITY_HIGHEST)));
        photoQualities.add(new PhotoQualityOption(CameraConfiguration.MEDIA_QUALITY_HIGH, getCameraController().getCameraManager().getPhotoSizeForQuality(CameraConfiguration.MEDIA_QUALITY_HIGH)));
        photoQualities.add(new PhotoQualityOption(CameraConfiguration.MEDIA_QUALITY_MEDIUM, getCameraController().getCameraManager().getPhotoSizeForQuality(CameraConfiguration.MEDIA_QUALITY_MEDIUM)));
        photoQualities.add(new PhotoQualityOption(CameraConfiguration.MEDIA_QUALITY_LOWEST, getCameraController().getCameraManager().getPhotoSizeForQuality(CameraConfiguration.MEDIA_QUALITY_LOWEST)));

        CharSequence[] array = new CharSequence[photoQualities.size()];
        photoQualities.toArray(array);

        return array;
    }

}
