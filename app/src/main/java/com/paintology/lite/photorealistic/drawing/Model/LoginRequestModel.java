package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {

    @SerializedName("user_name")
    public String user_name = "";

    @SerializedName("user_id")
    public String user_id = "";

    @SerializedName("user_email")
    public String user_email = "";

    @SerializedName("user_password")
    public String user_password = "";

    public LoginRequestModel(String user_id, String user_name, String user_email, String user_password) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_password = user_password;
    }
}
