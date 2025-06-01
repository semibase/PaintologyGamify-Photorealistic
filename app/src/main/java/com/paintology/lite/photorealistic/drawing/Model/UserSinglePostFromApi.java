package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 04/06/21.
 */
public class UserSinglePostFromApi implements Parcelable {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public int code = 0;

    @SerializedName("data")
    public SinglePostData ObjData = new SinglePostData();

    public UserSinglePostFromApi() {
    }

    protected UserSinglePostFromApi(Parcel in) {
        status = in.readString();
        response = in.readString();
        code = in.readInt();
        ObjData = (SinglePostData) in.readParcelable(SinglePostData.class.getClassLoader());
    }

    public static final Creator<UserSinglePostFromApi> CREATOR = new Creator<UserSinglePostFromApi>() {
        @Override
        public UserSinglePostFromApi createFromParcel(Parcel in) {
            return new UserSinglePostFromApi(in);
        }

        @Override
        public UserSinglePostFromApi[] newArray(int size) {
            return new UserSinglePostFromApi[size];
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

    public SinglePostData getObjData() {
        return ObjData;
    }

    public void setObjData(SinglePostData objData) {
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

    public class SinglePostData implements Parcelable {

        public SinglePostData() {
        }

        @SerializedName("total_posts")
        public String total_posts;

        @SerializedName("posts_list")
        public UserPostList post_list = new UserPostList();


        protected SinglePostData(Parcel in) {
            total_posts = in.readString();
            post_list = (UserPostList) in.readValue(UserPostList.class.getClassLoader());
        }

        public final Creator<SinglePostData> CREATOR = new Creator<SinglePostData>() {
            @Override
            public SinglePostData createFromParcel(Parcel in) {
                return new SinglePostData(in);
            }

            @Override
            public SinglePostData[] newArray(int size) {
                return new SinglePostData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(total_posts);
            parcel.writeValue(post_list);
        }

        public String getTotal_posts() {
            return total_posts;
        }

        public void setTotal_posts(String total_posts) {
            this.total_posts = total_posts;
        }

        public ArrayList<UserPostList> getPost_list() {
            ArrayList<UserPostList> postLists = new ArrayList<UserPostList>();
            postLists.add(post_list);
            return postLists;
        }

        public void setPost_list(UserPostList post_list) {
            this.post_list = post_list;
        }
    }
}
