package com.paintology.lite.photorealistic.drawing.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class ColorPad extends View {
    Paint paint;
    Shader luar;
    public final float[] color = {1.f, 1.f, 1.f};

    Context context;
    StringConstants constants = new StringConstants();

    public ColorPad(Context context) {
        this(context, null);
        this.context = context;
    }

    public ColorPad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public ColorPad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (paint == null) {
            paint = new Paint();
            luar = new LinearGradient(0.f, 0.f, 0.f, getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);
        }
        int rgb = Color.HSVToColor(color);
        Shader dalam = new LinearGradient(0.f, 0.f, getMeasuredWidth(), 0.f, 0xffffffff, rgb, TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY);


        paint.setShader(shader);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        canvas.drawRect(0.f, 0.f, getMeasuredWidth(), getMeasuredHeight(), paint);
    }

    public void setHue(float hue) {
        color[0] = hue;
        Log.e("TAG", "SetHue Value " + color[0]);
        constants.putString("hueValue", color[0] + "", context);
        invalidate();
    }

}
