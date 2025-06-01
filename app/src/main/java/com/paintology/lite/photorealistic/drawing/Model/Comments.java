package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comments implements Parcelable {

    @SerializedName("total_comments")
    public String total_comments = "";

    @SerializedName("post_comment_lists")
    public ArrayList<post_comment_lists> post_comment_lists;


    protected Comments(Parcel in) {
        total_comments = in.readString();
        post_comment_lists = in.createTypedArrayList(com.paintology.lite.photorealistic.drawing.Model.post_comment_lists.CREATOR);
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel in) {
            return new Comments(in);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

    public ArrayList<com.paintology.lite.photorealistic.drawing.Model.post_comment_lists> getPost_comment_lists() {
        return post_comment_lists;
    }

    public void setPost_comment_lists(ArrayList<com.paintology.lite.photorealistic.drawing.Model.post_comment_lists> post_comment_lists) {
        this.post_comment_lists = post_comment_lists;
    }

    public String getTotal_comments() {
        return total_comments;
    }

    public void setTotal_comments(String total_comments) {
        this.total_comments = total_comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(total_comments);
        parcel.writeTypedList(post_comment_lists);
    }
}
