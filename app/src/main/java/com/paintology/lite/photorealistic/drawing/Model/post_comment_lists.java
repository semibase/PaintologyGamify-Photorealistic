package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class post_comment_lists implements Parcelable {

    public post_comment_lists() {
    }

    @SerializedName("comment_id")
    public String comment_id = "";

    @SerializedName("comment_date")
    public String comment_date = "";

    @SerializedName("comment_content")
    public String comment_content = "";

    @SerializedName("username")
    public String username = "";

    protected post_comment_lists(Parcel in) {
        comment_id = in.readString();
        comment_date = in.readString();
        comment_content = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment_id);
        dest.writeString(comment_date);
        dest.writeString(comment_content);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<post_comment_lists> CREATOR = new Creator<post_comment_lists>() {
        @Override
        public post_comment_lists createFromParcel(Parcel in) {
            return new post_comment_lists(in);
        }

        @Override
        public post_comment_lists[] newArray(int size) {
            return new post_comment_lists[size];
        }
    };

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
