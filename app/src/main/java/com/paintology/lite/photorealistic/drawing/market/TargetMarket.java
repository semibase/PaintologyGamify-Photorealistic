package com.paintology.lite.photorealistic.drawing.market;

public class TargetMarket {
    public static final int AMAZON_MARKET = 2;
    public static final int GOOGLE_MARKET = 1;
    public static final int RIM_MARKET = 3;
    public static int targetMarket = 1;

    public static boolean isForAmazon() {
        if (targetMarket == 2)
            return true;

        return false;
    }

    public static boolean isForGoogle() {
        int i = 1;
        if (targetMarket == i)
            return true;

        return false;
    }

    public static boolean isForRIM() {
        if (targetMarket == 3)
            return true;

        return false;
    }
}
