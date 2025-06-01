package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 3/4/21.
 */
public class Response {

    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("UserEmail")
    @Expose
    private String userEmail;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Age")
    @Expose
    private String age;
    @SerializedName("Gender")
    @Expose
    private String gender;
    @SerializedName("ArtFavourite")
    @Expose
    private String artFavourite;
    @SerializedName("ArtMedium")
    @Expose
    private String artMedium;
    @SerializedName("ArtAbility")
    @Expose
    private String artAbility;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("FacebookLink")
    @Expose
    private String facebookLink;
    @SerializedName("YoutubeLink")
    @Expose
    private String youtubeLink;
    @SerializedName("TwitterLink")
    @Expose
    private String twitterLink;
    @SerializedName("InstagramLink")
    @Expose
    private String instagramLink;
    @SerializedName("LinkedinLink")
    @Expose
    private String linkedinLink;
    @SerializedName("TikTokLink")
    @Expose
    private String tikTokLink;
    @SerializedName("WebsiteLink")
    @Expose
    private String websiteLink;
    @SerializedName("QuoraURL")
    @Expose
    private String quoraURL;
    @SerializedName("OtherURL")
    @Expose
    private String otherURL;
    @SerializedName("PinterestLink")
    @Expose
    private String pinterestURL;
    @SerializedName("TikTokURL")
    @Expose
    private String tiktokURL;
    @SerializedName("Profile_Pic")
    @Expose
    private String profilePic;
    @SerializedName("TotalPost")
    @Expose
    private String totalPost;
    @SerializedName("TotalFollowers")
    @Expose
    private Integer totalFollowers;
    @SerializedName("TotalFollowing")
    @Expose
    private Integer totalFollowing;
    @SerializedName("Followers")
    @Expose
    private List<Follower> followers = null;
    @SerializedName("Following")
    @Expose
    private List<Following> following = null;
    @SerializedName("user_choices")
    @Expose
    private UserChoices userChoices;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getArtFavourite() {
        return artFavourite;
    }

    public void setArtFavourite(String artFavourite) {
        this.artFavourite = artFavourite;
    }

    public String getArtMedium() {
        return artMedium;
    }

    public void setArtMedium(String artMedium) {
        this.artMedium = artMedium;
    }

    public String getArtAbility() {
        return artAbility;
    }

    public void setArtAbility(String artAbility) {
        this.artAbility = artAbility;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }

    public String getTikTokLink() {
        return tikTokLink;
    }

    public void setTikTokLink(String tikTokLink) {
        this.tikTokLink = tikTokLink;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getQuoraURL() {
        return quoraURL;
    }

    public void setQuoraURL(String quoraURL) {
        this.quoraURL = quoraURL;
    }

    public String getOtherURL() {
        return otherURL;
    }

    public void setOtherURL(String otherURL) {
        this.otherURL = otherURL;
    }

    public String getPinterestURL() {
        return pinterestURL;
    }

    public void setPinterestURL(String pinterestURL) {
        this.pinterestURL = pinterestURL;
    }

    public String getTiktokURL() {
        return tiktokURL;
    }

    public void setTiktokURL(String tiktokURL) {
        this.tiktokURL = tiktokURL;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(String totalPost) {
        this.totalPost = totalPost;
    }

    public Integer getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(Integer totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public Integer getTotalFollowing() {
        return totalFollowing;
    }

    public void setTotalFollowing(Integer totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public List<Following> getFollowing() {
        return following;
    }

    public void setFollowing(List<Following> following) {
        this.following = following;
    }

    public UserChoices getUserChoices() {
        return userChoices;
    }

    public void setUserChoices(UserChoices userChoices) {
        this.userChoices = userChoices;
    }
}
