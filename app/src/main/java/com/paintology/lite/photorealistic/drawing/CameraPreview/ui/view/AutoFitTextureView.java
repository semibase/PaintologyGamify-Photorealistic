package com.paintology.lite.photorealistic.drawing.CameraPreview.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.paintology.lite.photorealistic.drawing.CameraPreview.utils.Size;


/**
 * Created by Arpit Gandhi on 7/6/16.
 */
@SuppressLint("ViewConstructor")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AutoFitTextureView extends TextureView {

    private final static String TAG = "AutoFitTextureView";

    private int ratioWidth = 0;
    private int ratioHeight = 0;
    Context context;
    Activity activity;
    Size mPreviewSize;

    public AutoFitTextureView(Context context, SurfaceTextureListener surfaceTextureListener, Activity activity, Size previewSize) {
        super(context, null);
        this.context = context;
        this.activity = activity;
        this.mPreviewSize = previewSize;
        setSurfaceTextureListener(surfaceTextureListener);

    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated fromList the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        ratioWidth = width;
        ratioHeight = height;

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Log.e("TAGGG", "View Height Width Sample AUTOFIT> " + width + " height " + height + " R " + ratioWidth + " RH " + ratioHeight);
        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * (ratioWidth / (float) ratioHeight)) {
                setMeasuredDimension(width, (int) (width * (ratioWidth / (float) ratioHeight)));
            } else {
                setMeasuredDimension((int) (height * (ratioWidth / (float) ratioHeight)), height);
            }
        }
    }

    public void configureTransform(int viewWidth, int viewHeight) {

        try {

            /*if (null == mTextureView || null == mPreviewSize || null == activity) {
                return;
            }*/
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / mPreviewSize.getHeight(),
                        (float) viewWidth / mPreviewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            }
            Log.e("TAGGG", "Exception while configureTransform matrix " + matrix);
            setTransform(matrix);
        } catch (Exception e) {
            Log.e("TAGGG", "Exception while configureTransform " + e.toString() + " " + e.getMessage());
        }
    }
}
