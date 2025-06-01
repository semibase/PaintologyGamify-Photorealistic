package com.paintology.lite.photorealistic.drawing.painting.file;

import com.paintology.lite.photorealistic.drawing.brush.BrushMetaData;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.PathQuadTo;
import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.painting.Stroker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class PaintingFile {

    private int VERSION = 2;
    public long mFileCreationTime;
    public int mFileVersion;
    private int mStrokeCount;

    protected void MyDbgLog(String pString1, String pString2) {
    }

    public void loadBrushMetaData(BrushMetaData pBrushMetaData, DataInputStream pDataInputStream)
            throws IOException {
        int i = pDataInputStream.readInt();
        pBrushMetaData.mBrushStyle = i;
        float f = pDataInputStream.readFloat();
        pBrushMetaData.mBrushSize = f;
        int j = pDataInputStream.readInt();
        pBrushMetaData.mBrushColor = j;
        int k = pDataInputStream.readInt();
        pBrushMetaData.mBrushAlphaValue = k;
        int m = pDataInputStream.readInt();
        pBrushMetaData.mBrushFlow = m;
        int n = pDataInputStream.readInt();
        pBrushMetaData.mBrushDirection = n;
        long l = pDataInputStream.readLong();
        pBrushMetaData.mRandomSeed = l;
        int i1 = pDataInputStream.readInt();
        pBrushMetaData.mRepeatDrawTimes = i1;
    }

    public void loadFileHeader(DataInputStream pDataInputStream)
            throws IOException {
        int i = pDataInputStream.readInt();
        mFileVersion = i;
        int m;
        if ((mFileVersion == 1) || (mFileVersion == 2)) {
            int k = pDataInputStream.readInt();
            long l = pDataInputStream.readLong();
            mFileCreationTime = l;

            for (m = 2; m < 12; m++) {
                int n = pDataInputStream.readInt();
            }
        }
    }

    public void loadPaintingFromStream(Painting pPainting, DataInputStream pDataInputStream)
            throws IOException {
        loadFileHeader(pDataInputStream);
        loadPaintingHeader(pPainting, pDataInputStream);
        loadStrokes(pPainting, pDataInputStream);
    }

    public void loadPaintingHeader(Painting pPainting, DataInputStream pDataInputStream)
            throws IOException {
        int m;
        if ((mFileVersion == 1) || (mFileVersion == 2)) {
            int i = pDataInputStream.readInt();
            pPainting.mPaintingWidth = i;
            int j = pDataInputStream.readInt();
            pPainting.mPaintingHeight = j;
            boolean bool = pDataInputStream.readBoolean();
            pPainting.setUseBackgroundBitmap(bool);
            int k = pDataInputStream.readInt();
            pPainting.setBackgroundColor(k);

            for (m = 4; m < 20; m++) {
                int n = pDataInputStream.readInt();
            }
        }
    }

    public void loadPoint(Point pPoint, int pInt, DataInputStream pDataInputStream)
            throws IOException {
        float f1 = pDataInputStream.readFloat();
        pPoint.x = f1;
        float f2 = pDataInputStream.readFloat();
        pPoint.y = f2;
        int i;
        if (pInt > 0) {
            float[] arrayOfFloat1 = new float[pInt];
            pPoint.data = arrayOfFloat1;

            for (i = 0; i < pInt; i++) {
                float[] arrayOfFloat2 = pPoint.data;
                float f3 = pDataInputStream.readFloat();
                arrayOfFloat2[i] = f3;
            }
        }
    }

    public void loadStroke(Stroker pStroker, DataInputStream pDataInputStream)
            throws IOException {
        BrushMetaData lBrushMetaData = new BrushMetaData();
        loadBrushMetaData(lBrushMetaData, pDataInputStream);
        pStroker.mBrushMetaData = lBrushMetaData;
        int i = pDataInputStream.readInt();
        int j = pDataInputStream.readInt();

        for (int k = 0; k < i; k++) {
            Point lPoint = new Point();
            loadPoint(lPoint, j, pDataInputStream);
            boolean bool = pStroker.mPoints.add(lPoint);
        }
    }

    public void loadStrokes(Painting pPainting, DataInputStream pDataInputStream)
            throws IOException {
        mStrokeCount = pDataInputStream.readInt();

        for (int j = 0; j < mStrokeCount; j++) {
            Stroker lStroker = new Stroker();
            loadStroke(lStroker, pDataInputStream);
            pPainting.addStroke(lStroker);
        }
    }

    public void storeBrushMetaData(BrushMetaData pBrushMetaData, DataOutputStream pDataOutputStream)
            throws IOException {
        int i = pBrushMetaData.mBrushStyle;
        pDataOutputStream.writeInt(i);
        float f = pBrushMetaData.mBrushSize;
        pDataOutputStream.writeFloat(f);
        int j = pBrushMetaData.mBrushColor;
        pDataOutputStream.writeInt(j);
        int k = pBrushMetaData.mBrushAlphaValue;
        pDataOutputStream.writeInt(k);
        int m = pBrushMetaData.mBrushFlow;
        pDataOutputStream.writeInt(m);
        int n = pBrushMetaData.mBrushDirection;
        pDataOutputStream.writeInt(n);
        long l = pBrushMetaData.mRandomSeed;
        pDataOutputStream.writeLong(l);
        int i1 = pBrushMetaData.mRepeatDrawTimes;
        pDataOutputStream.writeInt(i1);
    }

    public void storeFileHeader(DataOutputStream pDataOutputStream)
            throws IOException {
        int i = VERSION;
        pDataOutputStream.writeInt(i);
        int j;
        int k;
        if ((VERSION == 1) || (VERSION == 2)) {
            pDataOutputStream.writeInt(12);
            long l = System.currentTimeMillis();
            pDataOutputStream.writeLong(l);
            for (j = 2; j < 12; j++)
                pDataOutputStream.writeInt(0);
        }
    }

    public void storePaintingHeader(Painting pPainting, DataOutputStream pDataOutputStream)
            throws IOException {
        boolean i = true;
        int n;
        int i1;
        if ((VERSION == 1) || (VERSION == 2)) {
            int j = pPainting.mPaintingWidth;
            pDataOutputStream.writeInt(j);
            int k = pPainting.mPaintingHeight;
            pDataOutputStream.writeInt(k);
            if (pPainting.getBackgroundBitmap() == null)
                i = false;
            pDataOutputStream.writeBoolean(i);
            int m = pPainting.getBackgroundColor();
            pDataOutputStream.writeInt(m);

            for (n = 4; n < 20; n++) {
                pDataOutputStream.writeInt(0);
            }
        }
    }

    public void storePaintingToStream(Painting pPainting, DataOutputStream pDataOutputStream)
            throws IOException {
        storeFileHeader(pDataOutputStream);
        storePaintingHeader(pPainting, pDataOutputStream);
        storeStrokes(pPainting, pDataOutputStream);
    }

    public void storePoint(Point pPoint, DataOutputStream pDataOutputStream)
            throws IOException {
        float f1 = pPoint.x;
        pDataOutputStream.writeFloat(f1);
        float f2 = pPoint.y;
        pDataOutputStream.writeFloat(f2);
        int i;
        if (pPoint.data != null) {
            for (i = 0; i < pPoint.data.length; i++) {
                pDataOutputStream.writeFloat(pPoint.data[i]);
            }
        }
    }

    public void storeStoke(Stroker pStroker, DataOutputStream pDataOutputStream)
            throws IOException {
        int i = 0;
        BrushMetaData lBrushMetaData = pStroker.mBrushMetaData;
        storeBrushMetaData(lBrushMetaData, pDataOutputStream);
        int j = pStroker.mPoints.size();
        pDataOutputStream.writeInt(j);
        float[] arrayOfFloat = ((Point) pStroker.mPoints.get(i)).data;
        Iterator lIterator;
        if (arrayOfFloat == null) {
            pDataOutputStream.writeInt(i);
            lIterator = pStroker.mPoints.iterator();
        } else {
            pDataOutputStream.writeInt(arrayOfFloat.length);
            lIterator = pStroker.mPoints.iterator();
        }

        while (lIterator.hasNext()) {
            Point lPoint = (Point) lIterator.next();
            storePoint(lPoint, pDataOutputStream);
        }
    }

    public void storeStrokeSeg(PathQuadTo pPathQuadTo, DataOutputStream pDataOutputStream)
            throws IOException {
        Point lPoint1 = pPathQuadTo.cp;
        storePoint(lPoint1, pDataOutputStream);
        Point lPoint2 = pPathQuadTo.p2;
        storePoint(lPoint2, pDataOutputStream);
        int i;
        if (pPathQuadTo.brushData != null) {
            for (i = 0; i < pPathQuadTo.brushData.length; i++) {
                pDataOutputStream.writeFloat(pPathQuadTo.brushData[i]);
            }
        }
    }

    public void storeStrokes(Painting pPainting, DataOutputStream pDataOutputStream)
            throws IOException {
        int i = pPainting.getStrokeCount();
        pDataOutputStream.writeInt(i);
        Iterator lIterator = pPainting.getStrokeList().iterator();
        while (lIterator.hasNext()) {
            Stroker lStroker = (Stroker) lIterator.next();
            storeStoke(lStroker, pDataOutputStream);
        }
    }
}
