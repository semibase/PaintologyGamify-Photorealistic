package com.paintology.lite.photorealistic.drawing.Chat;

public class Firebase_User {


    public String login_type = "";
    public String user_id = "";
    public String user_email = "";
    public String user_name = "";
    public String profile_pic = "";
    public String key = "";
    public long updatedTime = 0;
    public String is_online = "false";
    public String is_online_chat = "false";
    public String is_typing = "false";
    public boolean isBlocked = false;
    public boolean isPending = false;

    public Firebase_User() {
    }

    public Firebase_User(String login_type, String user_id, String user_email, String user_name, String user_profile_pic, String is_online, String is_online_chat, String key, String is_typing) {
        this.login_type = login_type;
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_name = user_name;
        this.profile_pic = user_profile_pic;
        this.is_online = is_online;
        this.is_online_chat = is_online_chat;
        this.key = key;
        this.is_typing = is_typing;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_pic() {
        return profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.profile_pic = user_profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getIs_online_chat() {
        return is_online_chat;
    }

    public void setIs_online_chat(String is_online_chat) {
        this.is_online_chat = is_online_chat;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getIs_typing() {
        return is_typing;
    }

    public void setIs_typing(String is_typing) {
        this.is_typing = is_typing;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
