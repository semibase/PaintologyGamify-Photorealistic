package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserInfoInAllPost implements Parcelable {

    @SerializedName("Username")
    public String userName = "";

    @SerializedName("UserID")
    public String userId = "";

    @SerializedName("Profile_Pic")
    public String userProfilePic = "";

    protected UserInfoInAllPost(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        userProfilePic = in.readString();
    }

    public static final Creator<UserInfoInAllPost> CREATOR = new Creator<UserInfoInAllPost>() {
        @Override
        public UserInfoInAllPost createFromParcel(Parcel in) {
            return new UserInfoInAllPost(in);
        }

        @Override
        public UserInfoInAllPost[] newArray(int size) {
            return new UserInfoInAllPost[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(userId);
        parcel.writeString(userProfilePic);
    }
}
