package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseIncreaseCounter {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

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
}
