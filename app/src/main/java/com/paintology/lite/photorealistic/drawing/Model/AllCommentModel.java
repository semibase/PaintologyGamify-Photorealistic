package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllCommentModel {

    @SerializedName("status")
    public String status = "";

    @SerializedName("response")
    public String response = "";

    @SerializedName("code")
    public int code = 0;

    @SerializedName("data")
    public data mainData;

    public data getMainData() {
        return mainData;
    }

    public void setMainData(data mainData) {
        this.mainData = mainData;
    }

    public class data {

        @SerializedName("total_comments")
        public String total_comments = "";

        @SerializedName("post_comment_lists")
        public ArrayList<all_comments> comment_lists;

        public String getTotal_comments() {
            return total_comments;
        }

        public void setTotal_comments(String total_comments) {
            this.total_comments = total_comments;
        }

        public ArrayList<all_comments> getComment_lists() {
            return comment_lists;
        }

        public void setComment_lists(ArrayList<all_comments> comment_lists) {
            this.comment_lists = comment_lists;
        }

        public class all_comments {


            @SerializedName("comment_id")
            public String comment_id = "";

            @SerializedName("post_id")
            public String post_id = "";

            @SerializedName("comment_author")
            public String username = "";


            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            @SerializedName("comment_date")
            public String comment_date = "";

            @SerializedName("comment_content")
            public String comment_content = "";

            public String getComment_id() {
                return comment_id;
            }

            public void setComment_id(String comment_id) {
                this.comment_id = comment_id;
            }

            public String getPost_id() {
                return post_id;
            }

            public void setPost_id(String post_id) {
                this.post_id = post_id;
            }

            public String getComment_date() {
                return comment_date;
            }

            public void setComment_date(String comment_date) {
                this.comment_date = comment_date;
            }

            public String getComment_content() {
                return comment_content;
            }

            public void setComment_content(String comment_content) {
                this.comment_content = comment_content;
            }
        }
    }

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



   /* public  class OptionsDeserilizer implements JsonDeserializer<AllCommentModel> {

        @Override
        public AllCommentModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            AllCommentModel options = new Gson().fromJson(json, AllCommentModel.class);
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("option_value")) {
                JsonElement elem = jsonObject.get("option_value");
                if (elem != null && !elem.isJsonNull()) {
                    String valuesString = elem.getAsString();
                    if (!TextUtils.isEmpty(valuesString)){
                        List<AllCommentModel> values = new Gson().fromJson(valuesString, new TypeToken<ArrayList<AllCommentModel>>() {}.getType());
                        options.setMainData(values);
                    }
                }
            }
            return options ;
        }
    }*/


}
