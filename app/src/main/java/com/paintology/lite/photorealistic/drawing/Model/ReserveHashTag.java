package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReserveHashTag {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public int code = 0;

    @SerializedName("data")
    ArrayList<String> _lst_hashTag;


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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<String> get_lst_hashTag() {
        return _lst_hashTag;
    }

    public void set_lst_hashTag(ArrayList<String> _lst_hashTag) {
        this._lst_hashTag = _lst_hashTag;
    }
}
