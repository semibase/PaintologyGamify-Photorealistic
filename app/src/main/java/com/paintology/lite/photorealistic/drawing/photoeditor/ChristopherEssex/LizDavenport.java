package com.paintology.lite.photorealistic.drawing.photoeditor.ChristopherEssex;


import com.paintology.lite.photorealistic.drawing.photoeditor.ImageFilterUtils;

public class LizDavenport {

    private String title;
    private ImageFilterUtils.FilterType type;
    private int degree;
    private int FilterfileRaw;

    public LizDavenport(String title, ImageFilterUtils.FilterType type, int degree, int FilterFileRaw) {
        this.type = type;
        this.degree = degree;
        this.title = title;
        this.FilterfileRaw = FilterFileRaw;
    }

    public int getFilterfileRaw() {
        return FilterfileRaw;
    }

    public ImageFilterUtils.FilterType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getDegree() {
        return degree;
    }

}
