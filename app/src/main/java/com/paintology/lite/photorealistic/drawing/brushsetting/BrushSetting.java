package com.paintology.lite.photorealistic.drawing.brushsetting;

public class BrushSetting {

    public int brushStyle;
    public int flow;
    public int opacity;
    public float size;

    public void setElement(String pString1, String pString2) {
        if (pString1.equalsIgnoreCase("Size")) {
            float f = Float.parseFloat(pString2);
            size = f;
        } else if (pString1.equalsIgnoreCase("Flow")) {
            int i = Integer.parseInt(pString2);
            flow = i;
        } else if (pString1.equalsIgnoreCase("Opacity")) {
            int j = Integer.parseInt(pString2);
            opacity = j;
        } else if (pString1.equalsIgnoreCase("BrushType")) {
            int k = Integer.parseInt(pString2);
            brushStyle = k;
        }
    }
}
