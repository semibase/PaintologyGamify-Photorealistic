package com.paintology.lite.photorealistic.drawing.Model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunityPost {
    private Object post_id;

    private String id;

    private String drawing_id;

    private Statistic statistic;
    private Images images;
    private String user_id;
    private Author author;
    private Object created_at;
    private String description;
    private List<Comment> last_comments;
    private Links links;
    private String title;
    private LegacyData legacy_data;
    private List<String> tags;


    public boolean isDownloaded = false;
    public boolean isLiked = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrawingId() {
        return drawing_id;
    }

    public void setDrawingId(String id) {
        this.drawing_id = id;
    }

    public CommunityPost() {
        // Default constructor required for calls to DataSnapshot.getValue(CommunityPost.class)
    }

    public CommunityPost(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        this.post_id = map.get("id");
        this.statistic = map.containsKey("statistic") ? new Statistic((Map<String, Object>) map.get("statistic")) : null;
        this.images = map.containsKey("images") ? new Images((Map<String, Object>) map.get("images")) : null;
        this.user_id = (String) ((Map<String, Object>) map.get("author")).get("user_id");
        this.author = map.containsKey("author") ? new Author((Map<String, Object>) map.get("author")) : null;

        if (map.get("created_at") instanceof String) {
            this.created_at = (String) map.get("created_at");
        } else if (map.get("created_at") instanceof Timestamp) {
            this.created_at = formatTimestampToString((Timestamp) map.get("created_at"));
        } else {
            this.created_at = "";
        }
        this.description = (String) map.get("description");
        this.links = map.containsKey("links") ? new Links((Map<String, Object>) map.get("links")) : null;
        this.title = (String) map.get("title");
        this.last_comments = new ArrayList<>();
        if (map.containsKey("last_comments")) {
            List<Map<String, Object>> commentsList = (List<Map<String, Object>>) map.get("last_comments");
            for (Map<String, Object> commentMap : commentsList) {
                this.last_comments.add(new Comment(commentMap));
            }
        }
        this.legacy_data = map.containsKey("legacy_data") ? new LegacyData((Map<String, Object>) map.get("legacy_data")) : null;
        this.tags = (List<String>) map.get("tags");
        this.isDownloaded = map.containsKey("isDownloaded") ? (boolean) map.get("isDownloaded") : false;
        this.isLiked = map.containsKey("isLiked") ? (boolean) map.get("isLiked") : false;
    }

    private static String formatTimestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }

    // Getters and setters
    public String getPost_id() {

        if (post_id instanceof String) {
            return (String) post_id;
        } else if (post_id instanceof Integer) {
            return String.valueOf(post_id);
        } else {
            return String.valueOf("");
        }
    }

    public void setPost_id(Object post_id) {
        this.post_id = post_id;
    }


    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }


    public List<Comment> getLastComments() {
        return last_comments;
    }

    public void setLastComments(List<Comment> lastComments) {
        this.last_comments = lastComments;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Object getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Object created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LegacyData getLegacy_data() {
        return legacy_data;
    }

    public void setLegacy_data(LegacyData legacy_data) {
        this.legacy_data = legacy_data;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static class Statistic {
        private int comments = 0;
        private int views = 0;
        private int likes = 0;

        public Statistic() {
        }

        public Statistic(Map<String, Object> map) {
            this.comments = map.containsKey("comments") && map.get("comments") != null ? (int) map.get("comments") : 0;
            this.views = map.containsKey("views") && map.get("views") != null ? (int) map.get("views") : 0;
            this.likes = map.containsKey("likes") && map.get("likes") != null ? (int) map.get("likes") : 0;
        }

        // Getters and setters
        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public Integer getLikes() {
            return likes;
        }

        public void setLikes(Integer likes) {
            this.likes = likes;
        }
    }

    public static class Images {
        private String content_resized;
        private String content;

        public Images() {
            // Default constructor required for calls to DataSnapshot.getValue(Images.class)
        }

        public Images(Map<String, Object> map) {
            this.content_resized = map.containsKey("content_resized") && map.get("content_resized") != null ? (String) map.get("content_resized") : "";
            this.content = map.containsKey("content") && map.get("content") != null ? (String) map.get("content") : "";
        }

        public String getContent_resized() {
            return content_resized;
        }

        public void setContent_resized(String content_resized) {
        }

        //        public void setContent(Object content) {
//            this.content = content;
//        }
        public void setContent(String content) {
            this.content = content;
        }

        // Convenience method to get content as String
        public String getContent() {
            return content;
//            if (content instanceof String) {
//                return (String) content;
//            }
//            } else if (content instanceof Boolean) {
//                return String.valueOf(content);
//            } else {
//                return String.valueOf("");
//            }
        }
    }

    public static class Author {
        private String user_id;
        private String name;
        private String avatar;

        public Author() {
            // Default constructor required for calls to DataSnapshot.getValue(Author.class)
        }

        public Author(Map<String, Object> map) {
            this.user_id = (String) map.get("user_id");
            this.name = (String) map.get("name");
            this.avatar = (String) map.get("avatar");
        }

        // Getters and setters
        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Links {
        private String redirect;
        private String youtube;

        public Links() {
            // Default constructor required for calls to DataSnapshot.getValue(Links.class)
        }

        public Links(Map<String, Object> map) {
            this.redirect = (String) map.get("redirect");
            this.youtube = (String) map.get("youtube");
        }


        // Getters and setters
        public String getRedirect() {
            return redirect;
        }

        public void setRedirect(String redirect) {
            this.redirect = redirect;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }
    }

    public static class LegacyData {
        private Integer category_id; // Change to Integer to allow null values
        private int post_type;

        public LegacyData() {
            // Default constructor required for calls to DataSnapshot.getValue(LegacyData.class)
        }


        public LegacyData(Map<String, Object> map) {
            this.category_id = map.containsKey("category_id") ? (Integer) map.get("category_id") : null;
            this.post_type = map.containsKey("post_type") ? (int) map.get("post_type") : 0;
        }

        // Getters and setters
        public Integer getCategory_id() {
            return category_id;
        }

        public void setCategory_id(Integer category_id) {
            this.category_id = category_id;
        }

        public int getPost_type() {
            return post_type;
        }

        public void setPost_type(int post_type) {
            this.post_type = post_type;
        }
    }


    public static class Comment {
        private String country;
        private String userId;
        private String name;
        private String createdAt;
        private String comment;
        private String avatar;

        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
        public Comment() {
        }

        // Constructor
        public Comment(Map<String, Object> map) {
            this.country = map.containsKey("country") ? (String) map.get("country") : "";
            this.userId = map.containsKey("user_id") ? (String) map.get("user_id") : "";
            this.name = map.containsKey("name") ? (String) map.get("name") : "";
            this.createdAt = map.containsKey("created_at") ? (String) map.get("created_at") : "";
            this.comment = map.containsKey("comment") ? (String) map.get("comment") : "";
            this.avatar = map.containsKey("avatar") ? (String) map.get("avatar") : "";
        }

        // Getters and Setters
        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

    }

}
