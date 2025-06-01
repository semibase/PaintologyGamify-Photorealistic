package com.paintology.lite.photorealistic.drawing.Model;

public class ContentSectionModel {

    public String url = "", caption = "", youtube_url;
    public Boolean isVideoContent;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVideoContent() {
        return isVideoContent;
    }

    public void setVideoContent(Boolean videoContent) {
        isVideoContent = videoContent;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }
}
