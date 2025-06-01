package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategoryModel {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

    @SerializedName("data")
    public ArrayList<categoryData> categoryList;

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

    public ArrayList<categoryData> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<categoryData> categoryList) {
        this.categoryList = categoryList;
    }

    public static class categoryData {


        public boolean isTypeCategory = true;

        public boolean isTypeCategory() {
            return isTypeCategory;
        }

        public void setTypeCategory(boolean typeCategory) {
            isTypeCategory = typeCategory;
        }

        @SerializedName("Data")
        public Data obj_data;

        @SerializedName("Resize")
        public String Resize = "";

        @SerializedName("Childs")
        public ArrayList<categoryData> childs;

        public Data getObj_data() {
            return obj_data;
        }

        public void setObj_data(Data obj_data) {
            this.obj_data = obj_data;
        }

        public String getResize() {
            return Resize;
        }

        public void setResize(String resize) {
            Resize = resize;
        }

        public ArrayList<categoryData> getChilds() {
            return childs;
        }

        public void setChilds(ArrayList<categoryData> childs) {
            this.childs = childs;
        }
    }

    public static class Data {

        @SerializedName("term_id")
        public String cate_id;

        @SerializedName("name")
        public String categoryName;

        @SerializedName("categoryURL")
        public String categoryURL;

        @SerializedName("total_tutorials")
        public int totalTutorials;


        public String redirect_url = "";

        public String getRedirect_url() {
            return redirect_url;
        }

        public void setRedirect_url(String redirect_url) {
            this.redirect_url = redirect_url;
        }

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryURL() {
            return categoryURL;
        }

        public void setCategoryURL(String categoryURL) {
            this.categoryURL = categoryURL;
        }

        public int getTotalTutorials() {
            return totalTutorials;
        }

        public void setTotalTutorials(int totalTutorials) {
            this.totalTutorials = totalTutorials;
        }
    }

}
