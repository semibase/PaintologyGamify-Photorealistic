package com.paintology.lite.photorealistic.drawing.Model;

import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User;

import java.util.ArrayList;

public class OperationAfterLogin {

    public String operationType = "";

    public int position = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class CommentData {

        public String _username = "";

        public String _user_comment = "";

        public String _post_id = "";


        ArrayList<Firebase_User> _user_list = new ArrayList<>();

        public String get_username() {
            return _username;
        }

        public void set_username(String _username) {
            this._username = _username;
        }

        public String get_user_comment() {
            return _user_comment;
        }

        public void set_user_comment(String _user_comment) {
            this._user_comment = _user_comment;
        }

        public String get_post_id() {
            return _post_id;
        }

        public void set_post_id(String _post_id) {
            this._post_id = _post_id;
        }

        public ArrayList<Firebase_User> get_user_list() {
            return _user_list;
        }

        public void set_user_list(ArrayList<Firebase_User> _user_list) {
            this._user_list = _user_list;
        }
    }

    public static class LikeData {

        public String user_id = "";

        public String post_id = "";

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }
    }

    public static class ViewData {

        public String user_id = "";

        public String post_id = "";

        public String totalViews = "";

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public String getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(String totalViews) {
            this.totalViews = totalViews;
        }
    }

    public CommentData _obj_comment_data;

    public LikeData _obj_like_data;
    public ViewData _obj_view_data;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public CommentData get_obj_comment_data() {
        return _obj_comment_data;
    }

    public void set_obj_comment_data(CommentData _obj_comment_data) {
        this._obj_comment_data = _obj_comment_data;
    }

    public LikeData get_obj_like_data() {
        return _obj_like_data;
    }

    public void set_obj_like_data(LikeData _obj_like_data) {
        this._obj_like_data = _obj_like_data;
    }

    public ViewData get_obj_view_data() {
        return _obj_view_data;
    }

    public void set_obj_view_data(ViewData _obj_view_data) {
        this._obj_view_data = _obj_view_data;
    }
}
