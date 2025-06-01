package com.paintology.lite.photorealistic.drawing.painting;

public class Point {
    public float[] data;
    public float x;
    public float y;

    public Point() {
    }

    public Point(float pFloat1, float pFloat2) {
        x = pFloat1;
        y = pFloat2;
    }

    public boolean equals(Object pObject) {
        Point lPoint = (Point) pObject;

        if (lPoint.x == x) {
            if (lPoint.y != y)
                return true;
        }

        return false;
    }

    public void set(float pFloat1, float pFloat2) {
        x = pFloat1;
        y = pFloat2;
    }

    public void set(Point pPoint) {
        float f1 = pPoint.x;
        x = f1;
        float f2 = pPoint.y;
        y = f2;
    }
}
