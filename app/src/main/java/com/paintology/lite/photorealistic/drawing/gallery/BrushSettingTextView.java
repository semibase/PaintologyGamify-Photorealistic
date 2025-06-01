package com.paintology.lite.photorealistic.drawing.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.paintology.lite.photorealistic.drawing.R;

public class BrushSettingTextView extends View {

    private Context m_context;

    private String m_strText;
    private int m_nSize = 20;
//    private int m_nColor = 0xFFFFFFFF;

    public BrushSettingTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        m_context = context;
        Init();
    }

    public BrushSettingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        m_context = context;
        Init();
    }

    public void Init() {
        m_strText = "";
    }

    public void setText(String strText) {
        m_strText = strText;
        invalidate();
    }

    public int getSize() {
        return m_nSize;
    }

    public void setSize(int size) {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            m_nSize = 14;
        } else {
            m_nSize = 23;
        }
        //m_nSize = size;
    }

    public void setColor(int color) {
//        m_nColor = color;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int nWidth = getWidth();
        int nHeight = getHeight();

        if (nWidth == 0 || nHeight == 0)
            return;

        if (m_strText.length() == 0)
            return;

        Paint paint = new Paint();

        paint.setTextSize(m_nSize);
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);

        RectF rcCalc = new RectF();
        Path path = new Path();

        paint.getTextPath(m_strText, 0, m_strText.length(), nHeight / 2, nWidth / 2, path);
        path.computeBounds(rcCalc, true);
        paint.setAntiAlias(true);

        int ascent = (int) Math.ceil(-paint.ascent());
        int descent = (int) Math.ceil(paint.descent());
        int textHeight = ascent + descent;
        int nTop = (int) ((rcCalc.height() - textHeight) / 2);
        nTop = nTop + ascent;
        nTop = (int) (nTop + rcCalc.top);
        int nLeft = 0;
        nLeft = (int) (rcCalc.width() / 2);
        nLeft = (int) (nLeft + rcCalc.left);
        paint.setTextAlign(Paint.Align.CENTER);

        Bitmap bmp = Bitmap.createBitmap(nHeight, nWidth, Bitmap.Config.ARGB_8888);
        Canvas tmpCanvas = new Canvas(bmp);
        tmpCanvas.drawText(m_strText, nHeight / 2, nTop, paint);
        Matrix matrix = new Matrix();

        canvas.translate(nWidth / 2, nHeight / 2);
        matrix.postRotate(-90);
        canvas.concat(matrix);
        matrix.reset();

        if (bmp != null && !bmp.isRecycled())
            canvas.drawBitmap(bmp, -nHeight / 2, -nWidth / 7, null); // -nWidth / 9

        bmp.recycle();
        bmp = null;
    }
}