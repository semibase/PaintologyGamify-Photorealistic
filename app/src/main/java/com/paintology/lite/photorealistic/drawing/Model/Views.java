package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Views implements Parcelable {

    @SerializedName("total_views")
    public String total_views = "";

    protected Views(Parcel in) {
        total_views = in.readString();
    }

    public static final Creator<Views> CREATOR = new Creator<Views>() {
        @Override
        public Views createFromParcel(Parcel in) {
            return new Views(in);
        }

        @Override
        public Views[] newArray(int size) {
            return new Views[size];
        }
    };

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(total_views);
    }
}
