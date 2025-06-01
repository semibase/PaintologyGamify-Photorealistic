package com.paintology.lite.photorealistic.drawing.Model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class ResponseBase {

    @SerializedName("status")
    public String status;

    @SerializedName("response")
    public String response;

    @SerializedName("code")
    public Integer code;

    @SerializedName("data")
    public Boolean data;

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

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }


    public class response {

        @ColumnInfo(name = "userID")
        public String userID = "";

        @ColumnInfo(name = "userEmail")
        public String userEmail = "";

        @ColumnInfo(name = "userName")
        public String userName = "";

        @ColumnInfo(name = "userDescription")
        public String userDescription = "";

        @ColumnInfo(name = "profilePicPath")
        public String profilePicPath = "";

        @ColumnInfo(name = "userAge")
        public int userAge = 0;

        @ColumnInfo(name = "gender")
        public int gender = 0;

        @ColumnInfo(name = "artFav")
        public String artFav = "";

        @ColumnInfo(name = "artAbility")
        public String artAbility = "";

        @ColumnInfo(name = "artMedium")
        public String artMedium = "";

        @ColumnInfo(name = "TotalFollowers")
        public String TotalFollowers = "";

        @ColumnInfo(name = "TotalFollowing")
        public String TotalFollowing = "";


        @ColumnInfo(name = "followStatus")
        public Boolean followStatus = false;

    }

}
