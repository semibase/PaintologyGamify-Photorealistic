package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

//    @SerializedName("data")
//    public Boolean data;


    @SerializedName("data")
    public String dataAsString = "";

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

//    public Boolean getData() {
//        return data;
//    }
//
//    public void setData(Boolean data) {
//        this.data = data;
//    }

    public String getDataAsString() {
        return dataAsString;
    }

    public void setDataAsString(String dataAsString) {
        this.dataAsString = dataAsString;
    }
}
