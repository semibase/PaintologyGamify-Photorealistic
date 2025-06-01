package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserPostData implements Parcelable {

    public UserPostData() {
    }

    @SerializedName("total_posts")
    public String total_posts;

    @SerializedName("posts_list")
    public ArrayList<UserPostList> post_list = new ArrayList<UserPostList>();


    protected UserPostData(Parcel in) {
        total_posts = in.readString();
        post_list = in.createTypedArrayList(UserPostList.CREATOR);
    }

    public static final Creator<UserPostData> CREATOR = new Creator<UserPostData>() {
        @Override
        public UserPostData createFromParcel(Parcel in) {
            return new UserPostData(in);
        }

        @Override
        public UserPostData[] newArray(int size) {
            return new UserPostData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(total_posts);
        parcel.writeTypedList(post_list);
    }

    public String getTotal_posts() {
        return total_posts;
    }

    public void setTotal_posts(String total_posts) {
        this.total_posts = total_posts;
    }

    public ArrayList<UserPostList> getPost_list() {
        return post_list;
    }

    public void setPost_list(ArrayList<UserPostList> post_list) {
        this.post_list = post_list;
    }
}