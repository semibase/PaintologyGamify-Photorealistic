package com.paintology.lite.photorealistic.drawing.Chat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MyUsersModel {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

    @SerializedName("data")
    public ArrayList<data> _user_list = new ArrayList<>();

    public static class data {

        @SerializedName("post_author")
        public String user_id = "";

        @SerializedName("type")
        public String type = "";


        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

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

    public ArrayList<data> get_user_list() {
        return _user_list;
    }

    public void set_user_list(ArrayList<data> _user_list) {
        this._user_list = _user_list;
    }
}
