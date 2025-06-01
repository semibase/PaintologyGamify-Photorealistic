package com.paintology.lite.photorealistic.drawing.Activity.favourite;

public class UserProfileFav {
    private int id;
    private String userId;
    private String username;
    private String description;
    private String country;

    public UserProfileFav(String country, String description, int id, String profileImage, String userId, String username) {
        this.country = country;
        this.description = description;
        this.id = id;
        this.profileImage = profileImage;
        this.userId = userId;
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String profileImage;




}

