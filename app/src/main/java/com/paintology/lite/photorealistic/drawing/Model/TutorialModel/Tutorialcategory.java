package com.paintology.lite.photorealistic.drawing.Model.TutorialModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tutorialcategory {

@SerializedName("id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("thumbnail")
@Expose
private String thumbnail;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getThumbnail() {
return thumbnail;
}

public void setThumbnail(String thumbnail) {
this.thumbnail = thumbnail;
}

}