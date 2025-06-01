package com.paintology.lite.photorealistic.drawing.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPostList implements Parcelable {

    @SerializedName("post_id")
    public String post_id = "";

    @Expose
    @SerializedName("category_id")
    public String cat_id = "";

    @SerializedName("post_date_time")
    public String post_date_time = "";

    @SerializedName("image_title")
    public String image_title = "";

    @SerializedName("image_description")
    public String image_description = "";

    @SerializedName("image_hashtag")
    public String image_hashtag = "";

    @SerializedName("image")
    public String image_Url = "";

    @SerializedName("redirect_url")
    public String redirect_url = "";


    @Expose
    @SerializedName("thumb_image")
    public thumb_image thumbs;

    public boolean isDownloaded = false;


    protected UserPostList(Parcel in) {
        post_id = in.readString();
        cat_id = in.readString();
        post_date_time = in.readString();
        image_title = in.readString();
        image_description = in.readString();
        image_hashtag = in.readString();
        image_Url = in.readString();
        redirect_url = in.readString();
        thumbs = in.readParcelable(thumb_image.class.getClassLoader());
        isDownloaded = in.readByte() != 0;
        userInfo = in.readParcelable(UserInfoInAllPost.class.getClassLoader());
        isLiked = in.readByte() != 0;
        commentsList = in.readParcelable(Comments.class.getClassLoader());
        objLikes = in.readParcelable(likes.class.getClassLoader());
        objView = in.readParcelable(Views.class.getClassLoader());
        post_type = in.readInt();
    }

    public static final Creator<UserPostList> CREATOR = new Creator<UserPostList>() {
        @Override
        public UserPostList createFromParcel(Parcel in) {
            return new UserPostList(in);
        }

        @Override
        public UserPostList[] newArray(int size) {
            return new UserPostList[size];
        }
    };

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public thumb_image getThumbs() {
        return thumbs;
    }

    public void setThumbs(thumb_image thumbs) {
        this.thumbs = thumbs;
    }

    @SerializedName("user_info")
    public UserInfoInAllPost userInfo;

    @SerializedName("is_post_liked")
    public boolean isLiked = false;

    @SerializedName("comments")
    public Comments commentsList;

    @SerializedName("likes")
    public likes objLikes;

    @SerializedName("view")
    public Views objView;

    @SerializedName("post_type")
    public int post_type;

    @SerializedName("chat_status")
    public String chat_status = "0";

    @SerializedName("user_key")
    public String user_key = "";

    @SerializedName("youtube_url")
    public String youtube_url = "";

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    //    @SerializedName("tutorial_data")
//    public tutorial_data objtutorialData;

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

   /* public tutorial_data getObjtutorialData() {
        return objtutorialData;
    }

    public void setObjtutorialData(tutorial_data objtutorialData) {
        this.objtutorialData = objtutorialData;
    }*/


    public UserPostList() {
    }

    public UserInfoInAllPost getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoInAllPost userInfo) {
        this.userInfo = userInfo;
    }

    public String getPost_date_time() {
        return post_date_time;
    }

    public void setPost_date_time(String post_date_time) {
        this.post_date_time = post_date_time;
    }

    public String getImage_title() {
        return image_title;
    }

    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }

    public String getImage_description() {
        return image_description;
    }

    public void setImage_description(String image_description) {
        this.image_description = image_description;
    }

    public String getImage_hashtag() {
        return image_hashtag;
    }

    public void setImage_hashtag(String image_hashtag) {
        this.image_hashtag = image_hashtag;
    }

    public String getImage_Url() {
        return image_Url;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }


    public Comments getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(Comments commentsList) {
        this.commentsList = commentsList;
    }

    public likes getObjLikes() {
        return objLikes;
    }

    public void setObjLikes(likes objLikes) {
        this.objLikes = objLikes;
    }

    public Views getObjView() {
        return objView;
    }

    public void setObjView(Views objView) {
        this.objView = objView;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getChat_status() {
        return chat_status;
    }

    public void setChat_status(String chat_status) {
        this.chat_status = chat_status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(post_id);
        parcel.writeString(cat_id);
        parcel.writeString(post_date_time);
        parcel.writeString(image_title);
        parcel.writeString(image_description);
        parcel.writeString(image_hashtag);
        parcel.writeString(image_Url);
        parcel.writeString(redirect_url);
        parcel.writeParcelable(thumbs, i);
        parcel.writeByte((byte) (isDownloaded ? 1 : 0));
        parcel.writeParcelable(userInfo, i);
        parcel.writeByte((byte) (isLiked ? 1 : 0));
        parcel.writeParcelable(commentsList, i);
        parcel.writeParcelable(objLikes, i);
        parcel.writeParcelable(objView, i);
        parcel.writeInt(post_type);
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }
}
