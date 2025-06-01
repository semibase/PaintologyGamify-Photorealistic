package com.paintology.lite.photorealistic.drawing.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class XView extends View {

    public XView(Context context) {
        super(context);
    }

    public XView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas) {
        try {
            float width = getMeasuredWidth();
            float height = getMeasuredHeight();
            Paint paint = new Paint();
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            canvas.drawLine(0, 0, width, height, paint);
            canvas.drawLine(width, 0, 0, height, paint);
            invalidate();
        } catch (Exception e) {
            Log.e("TAG", "Exception at XView " + e.getMessage());
        }
    }
}
