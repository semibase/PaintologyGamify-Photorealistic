package com.paintology.lite.photorealistic.drawing.Model.TutorialModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tutorialpage {

@SerializedName("size")
@Expose
private Long size;
@SerializedName("current")
@Expose
private Long current;
@SerializedName("total_elements")
@Expose
private Long totalElements;
@SerializedName("total_pages")
@Expose
private Long totalPages;

public Long getSize() {
return size;
}

public void setSize(Long size) {
this.size = size;
}

public Long getCurrent() {
return current;
}

public void setCurrent(Long current) {
this.current = current;
}

public Long getTotalElements() {
return totalElements;
}

public void setTotalElements(Long totalElements) {
this.totalElements = totalElements;
}

public Long getTotalPages() {
return totalPages;
}

public void setTotalPages(Long totalPages) {
this.totalPages = totalPages;
}

}