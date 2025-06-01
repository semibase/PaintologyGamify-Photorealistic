package com.paintology.lite.photorealistic.drawing.Chat.Notification;

public class Data {

    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String isFromComment = "";
    private String post_id = "";

    public Data(String user, int icon, String body, String title, String sented, String isFromComment, String post_id) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.isFromComment = isFromComment;
        this.post_id = post_id;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getIsFromComment() {
        return isFromComment;
    }

    public void setIsFromComment(String isFromComment) {
        this.isFromComment = isFromComment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
