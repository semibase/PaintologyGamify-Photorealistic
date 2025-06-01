package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class post_likes_lists implements Parcelable {

    @SerializedName("ID")
    public String ID = "";

    @SerializedName("user_login")
    public String user_login = "";

    protected post_likes_lists(Parcel in) {
        ID = in.readString();
        user_login = in.readString();
    }

    public static final Creator<post_likes_lists> CREATOR = new Creator<post_likes_lists>() {
        @Override
        public post_likes_lists createFromParcel(Parcel in) {
            return new post_likes_lists(in);
        }

        @Override
        public post_likes_lists[] newArray(int size) {
            return new post_likes_lists[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(user_login);
    }
}
