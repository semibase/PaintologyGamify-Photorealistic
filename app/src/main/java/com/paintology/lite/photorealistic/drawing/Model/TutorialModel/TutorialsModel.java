package com.paintology.lite.photorealistic.drawing.Model.TutorialModel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TutorialsModel {

@SerializedName("data")
@Expose
private List<Tutorialdatum> tutorialdata;
@SerializedName("page")
@Expose
private Tutorialpage tutorialpage;

public List<Tutorialdatum> getTutorialdata() {
return tutorialdata;
}

public void setTutorialdata(List<Tutorialdatum> tutorialdata) {
this.tutorialdata = tutorialdata;
}

public Tutorialpage getTutorialpage() {
return tutorialpage;
}

public void setTutorialpage(Tutorialpage tutorialpage) {
this.tutorialpage = tutorialpage;
}

}