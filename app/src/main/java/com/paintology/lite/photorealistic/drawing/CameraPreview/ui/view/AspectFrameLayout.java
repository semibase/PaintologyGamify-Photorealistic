package com.paintology.lite.photorealistic.drawing.CameraPreview.ui.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Layout that adjusts to maintain a specific aspect ratio.
 */
public class AspectFrameLayout extends FrameLayout {

    private static final String TAG = "AspectFrameLayout";

    private double targetAspectRatio = -1.0;        // initially use default window size

    public AspectFrameLayout(Context context) {
        super(context);
    }

    public AspectFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException();
        }

        if (targetAspectRatio != aspectRatio) {
            targetAspectRatio = aspectRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.e("TAGGG", "View Height Width INIT > " + widthMeasureSpec + " H " + heightMeasureSpec);
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);


        if (targetAspectRatio > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            // padding
            int horizontalPadding = getPaddingLeft() + getPaddingRight();
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            initialWidth -= horizontalPadding;
            initialHeight -= verticalPadding;

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDifference = targetAspectRatio / viewAspectRatio - 1;
            Log.e("TAGGG", "View Height Width aspectDifference> " + aspectDifference + " target " + targetAspectRatio);

            if (Math.abs(aspectDifference) < 0.01) {
                //no changes
            } else {
                if (aspectDifference > 0) {
                    initialHeight = (int) (initialWidth / targetAspectRatio);
                } else {
                    initialWidth = (int) (initialHeight * targetAspectRatio);
                }
                initialWidth += horizontalPadding;
                initialHeight += verticalPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
                Log.e("TAGGG", "View Height Width > " + initialWidth + " H " + initialHeight);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
