package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCategoryPostModel {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

    @SerializedName("data")
    public ArrayList<postData> postList;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ArrayList<postData> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<postData> postList) {
        this.postList = postList;
    }

    public class postData {


        @SerializedName("Data")
        public Data objdata;

        @SerializedName("Resize")
        public String Resize;

        @SerializedName("Childs")
        public ArrayList<postData> childs;

        public Data getObjdata() {
            return objdata;
        }

        public void setObjdata(Data objdata) {
            this.objdata = objdata;
        }

        public String getResize() {
            return Resize;
        }

        public void setResize(String resize) {
            Resize = resize;
        }

        public ArrayList<postData> getChilds() {
            return childs;
        }

        public void setChilds(ArrayList<postData> childs) {
            this.childs = childs;
        }
    }

    public class Data {
        @SerializedName("ID")
        public String ID = "";

        @SerializedName("post_date")
        public String post_date = "";

        @SerializedName("post_title")
        public String post_title = "";

        @SerializedName("thumbImage")
        public String thumbImage = "";


        @SerializedName("redirect_url")
        public String redirect_url = "";

        @SerializedName("youtube_link")
        public String youtube_link = "";


        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getPost_date() {
            return post_date;
        }

        public void setPost_date(String post_date) {
            this.post_date = post_date;
        }

        public String getPost_title() {
            return post_title;
        }

        public void setPost_title(String post_title) {
            this.post_title = post_title;
        }

        public String getThumbImage() {
            return thumbImage;
        }

        public void setThumbImage(String thumbImage) {
            this.thumbImage = thumbImage;
        }

        public String getRedirect_url() {
            return redirect_url;
        }

        public void setRedirect_url(String redirect_url) {
            this.redirect_url = redirect_url;
        }

        public String getYoutube_link() {
            return youtube_link;
        }

        public void setYoutube_link(String youtube_link) {
            this.youtube_link = youtube_link;
        }
    }

}
