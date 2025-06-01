package com.paintology.lite.photorealistic.drawing.brushpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.paintology.lite.photorealistic.drawing.R;


public class CustomSeekBar extends ImageButton {
    public float m_nMin = 1;
    public float m_nMax = 100;
    public float m_nCurPos = 50;

    public Handler mHandler = null;
    public int m_nMessage = -1;
    Bitmap m_bmpThumb, m_bmpScaledThumb;

    public int m_nMargin = 0;
    public int m_nThumbHeight = 15;


    public float m_scaleX = 1.0F;
    public float m_scaleY = 1.0F;

    public boolean m_flag = false;

    public CustomSeekBar(Context pContext) {
        super(pContext);

        m_bmpThumb = BitmapFactory.decodeResource(getResources(), R.drawable.seekbarthumb);

    }

    public CustomSeekBar(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);

        m_bmpThumb = BitmapFactory.decodeResource(getResources(), R.drawable.seekbarthumb);

    }

    public void setHandler(Handler handler, int nMessage) {
        mHandler = handler;
        m_nMessage = nMessage;
    }

    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();

        paint.setColor(0xFFFFFFFF);
//        paint.setColor(getResources().getColor(android.R.color.holo_red_dark));
        canvas.drawRect(new Rect(0, 0, width, height), paint);

        height = height - m_nThumbHeight;

//        Log.e("TAG", "onDraw m_nCurPos " + m_nCurPos);

//        LinearGradient luar = new LinearGradient(m_nMargin, m_nThumbHeight / 2, width - m_nMargin, height + m_nThumbHeight / 2, 0xFFA0A0A0, 0xFF646464, TileMode.CLAMP);
        int color1 = getResources().getColor(R.color.custom_seekbar_empty_color);
        int color2 = getResources().getColor(R.color.custom_seekbar_empty_color2);

        if ((m_nCurPos * 100 / m_nMax) < 10) {
            color1 = getResources().getColor(R.color.custom_seekbar_critical_color);
            color2 = getResources().getColor(R.color.custom_seekbar_critical_color);
        }

        LinearGradient luar = new LinearGradient(m_nMargin, m_nThumbHeight / 2, width - m_nMargin, height + m_nThumbHeight / 2, color1, color2, TileMode.CLAMP);
        paint.setShader(luar);

        canvas.drawRoundRect(new RectF(m_nMargin, m_nThumbHeight / 2, width - m_nMargin, height + m_nThumbHeight / 2), 7, 7, paint);

        luar = new LinearGradient(m_nMargin, (height - height * m_nCurPos / m_nMax) + m_nThumbHeight / 2, width - m_nMargin, height + m_nThumbHeight / 2, getResources().getColor(R.color.custom_seekbar_fill_color), getResources().getColor(R.color.custom_seekbar_fill_color), TileMode.CLAMP);
        paint.setShader(luar);

        canvas.drawRoundRect(new RectF(m_nMargin, (height - height * m_nCurPos / m_nMax) + m_nThumbHeight / 2, width - m_nMargin, height + m_nThumbHeight / 2), 7, 7, paint);

        if (m_flag == false) {
            m_bmpScaledThumb = Bitmap.createScaledBitmap(m_bmpThumb, (int) (m_bmpThumb.getWidth() * m_scaleX), (int) (m_bmpThumb.getHeight() * m_scaleY), false);
            m_flag = true;
        }

        if (m_bmpScaledThumb != null && !m_bmpScaledThumb.isRecycled()) {
            canvas.drawBitmap(m_bmpScaledThumb, new Rect(0, 0, (int) ((m_bmpThumb.getWidth() * m_scaleX)), (int) (m_bmpThumb.getHeight() * m_scaleY)), new Rect(0, (int) (height - height * m_nCurPos / m_nMax), (int) (40 * m_scaleX), (int) (height - height * m_nCurPos / m_nMax) + (int) (15 * m_scaleY)), null);
        }
    }

    void redrawFromPlus() {

    }

    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int height = getHeight();
//		int nThumbHeight = 15;
//		height = height - nThumbHeight; 

        m_nCurPos = (height - y) * m_nMax / height;

        if (m_nCurPos < m_nMin) {
            m_nCurPos = m_nMin;
//            return false;
        }

        if (m_nCurPos > m_nMax)
            m_nCurPos = m_nMax;

        invalidate();

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = m_nMessage;
            msg.arg1 = (int) m_nCurPos;
            msg.obj = "" + (m_nCurPos * 100 / m_nMax);
            mHandler.sendMessage(msg);
        }

        return super.onTouchEvent(event);
    }

    public void setMin(int min) {
        m_nMin = min;
    }

    public void setMax(int max) {
        m_nMax = max;
    }

    public void setProgress(int pos) {
        m_nCurPos = pos;

        if (m_nMax < m_nCurPos)
            m_nCurPos = m_nMax;
    }

    public void onUpdateProgress(float progress, Handler handler, int nMessage) {

        mHandler = handler;
        m_nMessage = nMessage;

        m_nCurPos = progress;

        if (m_nCurPos < m_nMin) {
            m_nCurPos = m_nMin;
        }

        if (m_nCurPos > m_nMax)
            m_nCurPos = m_nMax;

        invalidate();

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();

            msg.what = m_nMessage;
            msg.arg1 = (int) m_nCurPos;
            msg.obj = "" + (m_nCurPos * 100 / m_nMax);
            mHandler.sendMessage(msg);
        }

    }
}
