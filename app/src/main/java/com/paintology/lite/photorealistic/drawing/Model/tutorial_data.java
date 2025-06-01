package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class tutorial_data implements Parcelable {


    @SerializedName("tutorial_id")
    public String tutorial_id;

    @SerializedName("category_id")
    public String category_id;

    @SerializedName("tutorial_thumb")
    public String tutorial_thumb;

    @SerializedName("tutorial_image")
    public String tutorial_image;

    @SerializedName("tutorial_title")
    public String tutorial_title;

    protected tutorial_data(Parcel in) {
        tutorial_id = in.readString();
        category_id = in.readString();
        tutorial_thumb = in.readString();
        tutorial_image = in.readString();
        tutorial_title = in.readString();
    }

    public static final Creator<tutorial_data> CREATOR = new Creator<tutorial_data>() {
        @Override
        public tutorial_data createFromParcel(Parcel in) {
            return new tutorial_data(in);
        }

        @Override
        public tutorial_data[] newArray(int size) {
            return new tutorial_data[size];
        }
    };

    public String getTutorial_id() {
        return tutorial_id;
    }

    public void setTutorial_id(String tutorial_id) {
        this.tutorial_id = tutorial_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getTutorial_thumb() {
        return tutorial_thumb;
    }

    public void setTutorial_thumb(String tutorial_thumb) {
        this.tutorial_thumb = tutorial_thumb;
    }

    public String getTutorial_image() {
        return tutorial_image;
    }

    public void setTutorial_image(String tutorial_image) {
        this.tutorial_image = tutorial_image;
    }

    public String getTutorial_title() {
        return tutorial_title;
    }

    public void setTutorial_title(String tutorial_title) {
        this.tutorial_title = tutorial_title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tutorial_id);
        parcel.writeString(category_id);
        parcel.writeString(tutorial_thumb);
        parcel.writeString(tutorial_image);
        parcel.writeString(tutorial_title);
    }
}
