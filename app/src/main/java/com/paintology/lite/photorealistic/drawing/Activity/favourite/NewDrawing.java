package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import java.util.List;

public class NewDrawing {
    private String id;
    private String imageUrl;
    private String userName;
    private String title;
    private String description;
    private String createdAt;
    private String type;
    private String referenceId;
    private List<String> tags;
    private com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images images;
    private
    com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata metadata;
    private com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic statistic;
    private
    com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author author;
    private com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links links;


    public NewDrawing(
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author author, String createdAt, String description,
            String id, com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images images,
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links links,
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata metadata, String referenceId, com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic statistic,
            List<String> tags, String title, String type) {
        this.author = author;
        this.createdAt = createdAt;
        this.description = description;
        this.id = id;
        this.images = images;
        this.links = links;
        this.metadata = metadata;
        this.referenceId = referenceId;
        this.statistic = statistic;
        this.tags = tags;
        this.title = title;
        this.type = type;
    }

    public
    com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author getAuthor() {
        return author;
    }

    public void setAuthor(
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images getImages() {
        return images;
    }

    public void setImages(com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images images) {
        this.images = images;
    }

    public com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links getLinks() {
        return links;
    }

    public void setLinks(com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links links) {
        this.links = links;
    }

    public
    com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata metadata) {
        this.metadata = metadata;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic statistic) {
        this.statistic = statistic;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

class Images {
    private String content;
    // Constructor, getters, and setters


    public Images(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class Metadata {
    private String path;
    private String parentFolderPath;
    private String tutorialId;
    // Constructor, getters, and setters


    public Metadata(String parentFolderPath, String path, String tutorialId) {
        this.parentFolderPath = parentFolderPath;
        this.path = path;
        this.tutorialId = tutorialId;
    }

    public String getParentFolderPath() {
        return parentFolderPath;
    }

    public void setParentFolderPath(String parentFolderPath) {
        this.parentFolderPath = parentFolderPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }
}

class Statistic {
    private Integer comments;
    private int likes;
    private int ratings;
    private int reviewsCount;
    private int shares;
    private int views;
    // Constructor, getters, and setters


    public Statistic(Integer comments, int likes, int ratings, int reviewsCount, int shares, int views) {
        this.comments = comments;
        this.likes = likes;
        this.ratings = ratings;
        this.reviewsCount = reviewsCount;
        this.shares = shares;
        this.views = views;
    }


    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}

class Author {
    private String userId;
    private String name;
    private String avatar;
    private String country;
    private String level;
    // Constructor, getters, and setters


    public Author(String avatar, String country, String level, String name, String userId) {
        this.avatar = avatar;
        this.country = country;
        this.level = level;
        this.name = name;
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

class Links {
    private String youtube;
    // Constructor, getters, and setters


    public Links(String youtube) {
        this.youtube = youtube;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}

