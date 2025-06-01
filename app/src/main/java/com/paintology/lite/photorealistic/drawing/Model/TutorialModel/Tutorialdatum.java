package com.paintology.lite.photorealistic.drawing.Model.TutorialModel;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tutorialdatum {

    @SerializedName("categories")
    @Expose
    private List<Tutorialcategory> tutorialcategories;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private Tutorialimages tutorialimages;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tags")
    @Expose
    private List<String> tags;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    private String youtube_link = "";

    private String redirect = "";

    private String external = "";

    public String getYoutube_link() {
        return youtube_link;
    }

    public void setYoutube_link(String youtube_link) {
        this.youtube_link = youtube_link;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    private String cateName;

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public List<Tutorialcategory> getTutorialcategories() {
        return tutorialcategories;
    }

    public void setTutorialcategories(List<Tutorialcategory> tutorialcategories) {
        this.tutorialcategories = tutorialcategories;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Tutorialimages getTutorialimages() {
        return tutorialimages;
    }

    public void setTutorialimages(Tutorialimages tutorialimages) {
        this.tutorialimages = tutorialimages;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}