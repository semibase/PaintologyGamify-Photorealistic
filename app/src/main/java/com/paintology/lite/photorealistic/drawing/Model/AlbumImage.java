package com.paintology.lite.photorealistic.drawing.Model;

import java.util.ArrayList;

public class AlbumImage {

    String post_id;
    String filePath = "", art_ability;
    String fileName = "", iv_caption = "", iv_description = "", youtube_url = "";

    int mode = 0;

    public boolean isLocalPath = false;


    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public boolean isLocalPath() {
        return isLocalPath;
    }

    public void setLocalPath(boolean localPath) {
        isLocalPath = localPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIv_caption() {
        return iv_caption;
    }

    public void setIv_caption(String iv_caption) {
        this.iv_caption = iv_caption;
    }

    public String getIv_description() {
        return iv_description;
    }

    public void setIv_description(String iv_description) {
        this.iv_description = iv_description;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<String> artFavList;

    public ArrayList<String> artMediumList;

    public ArrayList<String> getArtFavList() {
        return artFavList;
    }

    public void setArtFavList(ArrayList<String> artFavList) {
        this.artFavList = artFavList;
    }

    public ArrayList<String> getArtMediumList() {
        return artMediumList;
    }

    public void setArtMediumList(ArrayList<String> artMediumList) {
        this.artMediumList = artMediumList;
    }

    public String str_art_fav = "";
    public String str_art_med = "";

    public String getStr_art_fav() {
        return str_art_fav;
    }

    public void setStr_art_fav(String str_art_fav) {
        this.str_art_fav = str_art_fav;
    }

    public String getStr_art_med() {
        return str_art_med;
    }

    public void setStr_art_med(String str_art_med) {
        this.str_art_med = str_art_med;
    }

    public String getArt_ability() {
        return art_ability;
    }

    public void setArt_ability(String art_ability) {
        this.art_ability = art_ability;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }
}
