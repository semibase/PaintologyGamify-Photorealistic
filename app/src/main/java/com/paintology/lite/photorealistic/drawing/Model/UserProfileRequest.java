package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class UserProfileRequest {

    @SerializedName("first_name")
    public String first_name = "";

    @SerializedName("user_id")
    public String user_id = "";

    @SerializedName("last_name")
    public String last_name = "";


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
