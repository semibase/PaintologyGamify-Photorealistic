package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class thumb_image implements Parcelable {


    @SerializedName("small")
    public String thumb_image_small = "";

    @SerializedName("medium")
    public String thumb_image_medium = "";

    @SerializedName("medium-large")
    public String thumb_image_medium_large = "";

    @SerializedName("large")
    public String thumb_image_large = "";


    protected thumb_image(Parcel in) {
        thumb_image_small = in.readString();
        thumb_image_medium = in.readString();
        thumb_image_medium_large = in.readString();
        thumb_image_large = in.readString();
    }

    public static final Creator<thumb_image> CREATOR = new Creator<thumb_image>() {
        @Override
        public thumb_image createFromParcel(Parcel in) {
            return new thumb_image(in);
        }

        @Override
        public thumb_image[] newArray(int size) {
            return new thumb_image[size];
        }
    };

    public String getThumb_image_small() {
        return thumb_image_small;
    }

    public void setThumb_image_small(String thumb_image_small) {
        this.thumb_image_small = thumb_image_small;
    }

    public String getThumb_image_medium() {
        return thumb_image_medium;
    }

    public void setThumb_image_medium(String thumb_image_medium) {
        this.thumb_image_medium = thumb_image_medium;
    }

    public String getThumb_image_medium_large() {
        return thumb_image_medium_large;
    }

    public void setThumb_image_medium_large(String thumb_image_medium_large) {
        this.thumb_image_medium_large = thumb_image_medium_large;
    }

    public String getThumb_image_large() {
        return thumb_image_large;
    }

    public void setThumb_image_large(String thumb_image_large) {
        this.thumb_image_large = thumb_image_large;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thumb_image_small);
        parcel.writeString(thumb_image_medium);
        parcel.writeString(thumb_image_medium_large);
        parcel.writeString(thumb_image_large);
    }
}
