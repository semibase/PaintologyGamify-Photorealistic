package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PostDetailModel {

    public String ID = "";
    public String post_date = "";
    public String post_content = "";
    public String post_title = "";
    public String post_status = "";
    public String post_modified = "";
    public String external_link = "";
    public String canvas_color = "";
    public String VisitPage = "";
    public String post_type = "";
    public String thumb_url = "";
    public String categoryURL = "";
    public String categoryName = "";
    public String Rating = "";
    public String ResizeImage = "";
    public List<ColorSwatch> color_swatch = new ArrayList<>();

    public String getResizeImage() {
        return ResizeImage;
    }

    public void setResizeImage(String resizeImage) {
        ResizeImage = resizeImage;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getCategoryURL() {
        return categoryURL;
    }

    public void setCategoryURL(String categoryURL) {
        this.categoryURL = categoryURL;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @SerializedName("youtube_link")
    public String youtube_link_list;

    @SerializedName("youtube_video")
    public ArrayList<youtube_video> youtube_video_list;

    @SerializedName("videos_and_files")
    public ArrayList<videos_and_files> video_and_file_list;


    @SerializedName("RelatedPostsData")
    public ArrayList<RelatedPostsData> list_related_post;

    @SerializedName("text_descriptions")
    public String text_descriptions;

    @SerializedName("membership_plan")
    public String membership_plan;

    public ArrayList<ContentSectionModel> featuredImage = new ArrayList<>();

    public String getYoutube_link_list() {
        return youtube_link_list;
    }

    public void setYoutube_link_list(String youtube_link_list) {
        this.youtube_link_list = youtube_link_list;
    }


    public String getText_descriptions() {
        return text_descriptions;
    }

    public void setText_descriptions(String text_descriptions) {
        this.text_descriptions = text_descriptions;
    }

    public String getMembership_plan() {
        return membership_plan;
    }

    public void setMembership_plan(String membership_plan) {
        this.membership_plan = membership_plan;
    }

    public class upload_youtube_video {
        public int ID = 0;
        public String title = "";
        public String filename = "";
        public String filesize = "";
        public String url = "";
        public String link = "";
        public String alt = "";
        public String description = "";
        public String name = "";
        public String date = "";
        public String modified = "";
        public String icon = "";

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public class youtube_link {
        public String youtube_link = "";

        public String getYoutube_link() {
            return youtube_link;
        }

        public void setYoutube_link(String youtube_link) {
            this.youtube_link = youtube_link;
        }
    }

    public class youtube_video {

        @SerializedName("upload_youtube_video")
        public upload_youtube_video obj_upload_youtube;

        public PostDetailModel.upload_youtube_video getObj_upload_youtube() {
            return obj_upload_youtube;
        }

        public void setObj_upload_youtube(PostDetailModel.upload_youtube_video obj_upload_youtube) {
            this.obj_upload_youtube = obj_upload_youtube;
        }
    }


    public class text_descriptions {
        public String description = "";

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class membership_plan {
        public String plan = "";

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_status() {
        return post_status;
    }

    public void setPost_status(String post_status) {
        this.post_status = post_status;
    }

    public String getPost_modified() {
        return post_modified;
    }

    public void setPost_modified(String post_modified) {
        this.post_modified = post_modified;
    }
//
//    public String getGuid() {
//        return guid;
//    }
//
//    public void setGuid(String guid) {
//        this.guid = guid;
//    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }


    public ArrayList<youtube_video> getYoutube_video_list() {
        return youtube_video_list;
    }

    public void setYoutube_video_list(ArrayList<youtube_video> youtube_video_list) {
        this.youtube_video_list = youtube_video_list;
    }

    public ArrayList<videos_and_files> getVideo_and_file_list() {
        return video_and_file_list;
    }

    public void setVideo_and_file_list(ArrayList<videos_and_files> video_and_file_list) {
        this.video_and_file_list = video_and_file_list;
    }

    public ArrayList<RelatedPostsData> getList_related_post() {
        return list_related_post;
    }

    public void setList_related_post(ArrayList<RelatedPostsData> list_related_post) {
        this.list_related_post = list_related_post;
    }

    public ArrayList<ContentSectionModel> getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(ArrayList<ContentSectionModel> featuredImage) {
        this.featuredImage = featuredImage;
    }

    public String getExternal_link() {
        return external_link;
    }

    public void setExternal_link(String external_link) {
        this.external_link = external_link;
    }

    public String getVisitPage() {
        return VisitPage;
    }

    public void setVisitPage(String visitPage) {
        VisitPage = visitPage;
    }

    public String getCanvas_color() {
        return canvas_color;
    }

    public void setCanvas_color(String canvas_color) {
        this.canvas_color = canvas_color;
    }

    public List<ColorSwatch> getSwatches() {
        return color_swatch;
    }

    public void setSwatches(ArrayList<ColorSwatch> swatches) {
        this.color_swatch = swatches;
    }

}
