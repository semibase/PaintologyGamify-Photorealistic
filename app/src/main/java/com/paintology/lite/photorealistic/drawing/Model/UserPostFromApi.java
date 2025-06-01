package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class UserPostFromApi implements Parcelable {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public int code = 0;

    @SerializedName("data")
    public UserPostData ObjData = new UserPostData();

    public UserPostFromApi() {
    }

    protected UserPostFromApi(Parcel in) {
        status = in.readString();
        response = in.readString();
        code = in.readInt();
        ObjData = (UserPostData) in.readParcelable(UserPostData.class.getClassLoader());
    }

    public static final Creator<UserPostFromApi> CREATOR = new Creator<UserPostFromApi>() {
        @Override
        public UserPostFromApi createFromParcel(Parcel in) {
            return new UserPostFromApi(in);
        }

        @Override
        public UserPostFromApi[] newArray(int size) {
            return new UserPostFromApi[size];
        }
    };

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

    public UserPostData getObjData() {
        return ObjData;
    }

    public void setObjData(UserPostData objData) {
        ObjData = objData;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(response);
        parcel.writeInt(code);
        parcel.writeParcelable(ObjData, i);
    }
}
