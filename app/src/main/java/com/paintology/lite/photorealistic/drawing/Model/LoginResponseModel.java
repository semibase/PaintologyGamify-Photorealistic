package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

public class LoginResponseModel {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public Integer code = 0;

    @SerializedName("data")
    public data objData;

    public class data {

        @SerializedName("ID")
        public Integer user_id = 0;

        @SerializedName("status")
        public String status = "";

        @SerializedName("Flag")
        public String isZipUploaded = "";

        @SerializedName("salt")
        public String salt = "";

        @SerializedName("action_type")
        public String actionType;

        @SerializedName("user_email")
        public String userEmail = "";

        @SerializedName("status_code")
        public String statusCode = "";

        @SerializedName("error_msg")
        public String errorMsg = "";

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public Integer getUser_id() {
            return user_id;
        }

        public void setUser_id(Integer user_id) {
            this.user_id = user_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIsZipUploaded() {
            return isZipUploaded;
        }

        public void setIsZipUploaded(String isZipUploaded) {
            this.isZipUploaded = isZipUploaded;
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

    public data getObjData() {
        return objData;
    }

    public void setObjData(data objData) {
        this.objData = objData;
    }
}
