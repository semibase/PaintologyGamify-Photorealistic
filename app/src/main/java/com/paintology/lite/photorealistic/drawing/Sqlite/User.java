package com.paintology.lite.photorealistic.drawing.Sqlite;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int Id = 0;

    @ColumnInfo(name = "userID")
    public String userID = "";

    @ColumnInfo(name = "userEmail")
    public String userEmail = "";

    @ColumnInfo(name = "userName")
    public String userName = "";

    @ColumnInfo(name = "userDescription")
    public String userDescription = "";

    @ColumnInfo(name = "profilePicPath")
    public String profilePicPath = "";

    @ColumnInfo(name = "userAge")
    public int userAge = 0;

    @ColumnInfo(name = "gender")
    public int gender = 0;

    @ColumnInfo(name = "artFav")
    public String artFav = "";

    @ColumnInfo(name = "artAbility")
    public String artAbility = "";

    @ColumnInfo(name = "artMedium")
    public String artMedium = "";

    @ColumnInfo(name = "TotalFollowers")
    public int TotalFollowers;

    @ColumnInfo(name = "TotalFollowing")
    public int TotalFollowing;


    @ColumnInfo(name = "followStatus")
    public Boolean followStatus = false;

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getArtFav() {
        return artFav;
    }

    public void setArtFav(String artFav) {
        this.artFav = artFav;
    }

    public String getArtAbility() {
        return artAbility;
    }

    public void setArtAbility(String artAbility) {
        this.artAbility = artAbility;
    }

    public String getArtMedium() {
        return artMedium;
    }

    public void setArtMedium(String artMedium) {
        this.artMedium = artMedium;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public Boolean getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(Boolean followStatus) {
        this.followStatus = followStatus;
    }

    public int getTotalFollowers() {
        return TotalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        TotalFollowers = totalFollowers;
    }

    public int getTotalFollowing() {
        return TotalFollowing;
    }

    public void setTotalFollowing(int totalFollowing) {
        TotalFollowing = totalFollowing;
    }
}
