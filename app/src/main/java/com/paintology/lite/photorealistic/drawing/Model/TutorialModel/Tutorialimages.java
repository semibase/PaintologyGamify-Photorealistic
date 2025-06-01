package com.paintology.lite.photorealistic.drawing.Model.TutorialModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tutorialimages {

@SerializedName("content")
@Expose
private String content;
@SerializedName("content_resized")
@Expose
private String contentResized;
@SerializedName("thumbnail")
@Expose
private String thumbnail;
@SerializedName("thumbnail_resized")
@Expose
private String thumbnailResized;

public String getContent() {
return content;
}

public void setContent(String content) {
this.content = content;
}

public String getContentResized() {
return contentResized;
}

public void setContentResized(String contentResized) {
this.contentResized = contentResized;
}

public String getThumbnail() {
return thumbnail;
}

public void setThumbnail(String thumbnail) {
this.thumbnail = thumbnail;
}

public String getThumbnailResized() {
return thumbnailResized;
}

public void setThumbnailResized(String thumbnailResized) {
this.thumbnailResized = thumbnailResized;
}

}