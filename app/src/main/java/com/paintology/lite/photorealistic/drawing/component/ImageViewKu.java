package com.paintology.lite.photorealistic.drawing.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewKu extends ImageView {

    public ImageViewKu(Context context) {
        super(context);
        setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        setSoundEffectsEnabled(false);
    }

    public ImageViewKu(Context context, int i, int j, int k, int l) {
        super(context);
        setFrame2(i, j, k, l);
        setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        setSoundEffectsEnabled(false);
    }

    public ImageViewKu(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        setSoundEffectsEnabled(false);
    }

    public ImageViewKu(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        setSoundEffectsEnabled(false);
    }

    private void checkLayoutParams() {
        if (getLayoutParams() == null)
            setLayoutParams(new android.widget.RelativeLayout.LayoutParams(0, 0));
    }

    public FrameKu getFrame() {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams LayoutParams = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        return new FrameKu(LayoutParams.leftMargin, LayoutParams.topMargin, LayoutParams.width, LayoutParams.height);
    }

    public float getX() {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).leftMargin;
    }

    public float getY() {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).topMargin;
    }

    public void move(int i, int j) {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams LayoutParams = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        LayoutParams.leftMargin = i;
        LayoutParams.topMargin = j;
        setLayoutParams(LayoutParams);
    }

    public ViewContainerKu parent() {
        return (ViewContainerKu) getParent();
    }

    public void removeFromParent() {
        parent().removeView(this);
    }

    public void resize(int i, int j) {
        checkLayoutParams();
        android.widget.RelativeLayout.LayoutParams LayoutParams = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        LayoutParams.width = i;
        LayoutParams.height = j;
    }

    public void setFrame2(int i, int j, int k, int l) {
        move(i, j);
        resize(k, l);
    }

    public void setFrame2(FrameKu frameku) {
        move(frameku.x, frameku.y);
        resize(frameku.width, frameku.height);
    }

    public int k_getHeight() {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).height;
    }

    public void setHeight(int i) {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).height = i;
    }

    public int k_getWidth() {
        checkLayoutParams();
        return ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).width;
    }

    public void setWidth(int i) {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).width = i;
    }

    public void setX(int i) {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).leftMargin = i;
    }

    public void setY(int i) {
        checkLayoutParams();
        ((android.widget.RelativeLayout.LayoutParams) getLayoutParams()).topMargin = i;
    }
}