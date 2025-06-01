package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class likes implements Parcelable {

    @SerializedName("total_likes")
    public String total_likes = "";

    @SerializedName("post_likes_lists")
    public ArrayList<post_likes_lists> lst_post_likes;

    protected likes(Parcel in) {
        total_likes = in.readString();
    }

    public static final Creator<likes> CREATOR = new Creator<likes>() {
        @Override
        public likes createFromParcel(Parcel in) {
            return new likes(in);
        }

        @Override
        public likes[] newArray(int size) {
            return new likes[size];
        }
    };

    public String getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(String total_likes) {
        this.total_likes = total_likes;
    }

    public ArrayList<post_likes_lists> getLst_post_likes() {
        return lst_post_likes;
    }

    public void setLst_post_likes(ArrayList<post_likes_lists> lst_post_likes) {
        this.lst_post_likes = lst_post_likes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(total_likes);
    }
}
