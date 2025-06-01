package com.paintology.lite.photorealistic.drawing.painting;

public class PathQuadTo {
    public float[] brushData;
    public Point cp;
    public Point p2;

    public PathQuadTo() {
        Point lPoint1 = new Point();
        cp = lPoint1;
        Point lPoint2 = new Point();
        p2 = lPoint2;
        brushData = null;
    }

    public PathQuadTo(Point pPoint1, Point pPoint2) {
        Point lPoint1 = new Point();
        cp = lPoint1;
        Point lPoint2 = new Point();
        p2 = lPoint2;
        brushData = null;
        cp.set(pPoint1);
        p2.set(pPoint2);
    }

    public void storeBrushData(float[] pArrayOfFloat) {
        brushData = pArrayOfFloat;
    }
}
