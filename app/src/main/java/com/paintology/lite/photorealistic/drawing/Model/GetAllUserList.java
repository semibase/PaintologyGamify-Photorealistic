package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAllUserList {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public Integer code = 0;

    @SerializedName("data")
    public Data ObjData;

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

    public Data getObjData() {
        return ObjData;
    }

    public void setObjData(Data objData) {
        ObjData = objData;
    }

    public class Data {

        @SerializedName("total_users")
        public String total_users = "";

        @SerializedName("user_list")
        public ArrayList<user_list> list_user;

        public class user_list {

            @SerializedName("Username")
            public String Username = "";

            @SerializedName("UserID")
            public String UserID = "";

            @SerializedName("Profile_Pic")
            public String Profile_Pic = "";

            @SerializedName("Description")
            public String Description = "";


            public String getUsername() {
                return Username;
            }

            public void setUsername(String username) {
                Username = username;
            }

            public String getUserID() {
                return UserID;
            }

            public void setUserID(String userID) {
                UserID = userID;
            }

            public String getProfile_Pic() {
                return Profile_Pic;
            }

            public void setProfile_Pic(String profile_Pic) {
                Profile_Pic = profile_Pic;
            }

            public String getDescription() {
                return Description;
            }

            public void setDescription(String description) {
                Description = description;
            }
        }

        public String getTotal_users() {
            return total_users;
        }

        public void setTotal_users(String total_users) {
            this.total_users = total_users;
        }

        public ArrayList<user_list> getList_user() {
            return list_user;
        }

        public void setList_user(ArrayList<user_list> list_user) {
            this.list_user = list_user;
        }
    }

}
