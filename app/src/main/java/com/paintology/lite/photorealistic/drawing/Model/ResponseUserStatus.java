package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseUserStatus {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

    @SerializedName("data")
    public data _objData;

    public class data {

        @SerializedName("status")
        public String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

    public data get_objData() {
        return _objData;
    }

    public void set_objData(data _objData) {
        this._objData = _objData;
    }
}
