package com.paintology.lite.photorealistic.drawing.challenge.view;

import android.util.Log;

import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch;
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel;
import com.paintology.lite.photorealistic.drawing.Model.Overlaid;
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel;
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData;
import com.paintology.lite.photorealistic.drawing.Model.sizes;
import com.paintology.lite.photorealistic.drawing.Model.text_files;
import com.paintology.lite.photorealistic.drawing.Model.trace_image;
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostModelConverter {

    public static PostDetailModel getPostDetailsResponse(String response){
        PostDetailModel _object = new PostDetailModel();
        try {
            JSONArray mainArray = new JSONArray(response);
            if (mainArray.length() > 0) {
                ArrayList<videos_and_files> _lst_video_file = new ArrayList<videos_and_files>();
                JSONObject objectFirst = mainArray.getJSONObject(0);

                _object.setID(objectFirst.has("ID") ? objectFirst.getString("ID") : "");
                _object.setCategoryName(objectFirst.has("categoryName") ? objectFirst.getString("categoryName") : "");
                _object.setCategoryURL(objectFirst.has("categoryURL") ? objectFirst.getString("categoryURL") : "");
                _object.setExternal_link(objectFirst.has("external_link") ? objectFirst.getString("external_link") : "");
                _object.setCanvas_color(objectFirst.has("canvas_color") ? objectFirst.getString("canvas_color") : "");
                _object.setVisitPage(objectFirst.has("VisitPage") ? objectFirst.getString("VisitPage") : "");
                _object.setMembership_plan(objectFirst.has("membership_plan") ? objectFirst.getString("membership_plan") : "");
                _object.setPost_content(objectFirst.has("post_content") ? objectFirst.getString("post_content") : "");
                _object.setPost_date(objectFirst.has("post_date") ? objectFirst.getString("post_date") : "");
                _object.setPost_title(objectFirst.has("post_title") ? objectFirst.getString("post_title") : "");
                _object.setRating(objectFirst.has("Rating") ? objectFirst.getString("Rating") : "");
                _object.setText_descriptions(objectFirst.has("text_descriptions") ? objectFirst.getString("text_descriptions") : "");
                _object.setThumb_url(objectFirst.has("thumb_url") ? objectFirst.getString("thumb_url") : "");
                _object.setYoutube_link_list(objectFirst.has("youtube_link") ? objectFirst.getString("youtube_link") : "");

                if (objectFirst.has("color_swatch") && !objectFirst.isNull("color_swatch")) {
                    JSONArray swatchesArray = objectFirst.getJSONArray("color_swatch");
                    ArrayList<ColorSwatch> swatches = new ArrayList<>();

                    if (swatchesArray != null && swatchesArray.length() > 0) {
                        for (int i = 0; i < swatchesArray.length(); i++) {

                            String swatch = swatchesArray.getJSONObject(i).getString("color_swatch");

                            ColorSwatch colorSwatch = new ColorSwatch();
                            colorSwatch.setColor_swatch(swatch);
                            swatches.add(colorSwatch);
                        }

                    }

                    _object.setSwatches(swatches);


                }

                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
                    _object.setResizeImage(objectFirst.getString("ResizeImage"));
                }
                if (objectFirst.has("RelatedPostsData")) {
                    JSONArray related_list_json = objectFirst.getJSONArray("RelatedPostsData");
                    ArrayList<RelatedPostsData> related_List = new ArrayList<RelatedPostsData>();
                    if (related_list_json != null && related_list_json.length() > 0) {
                        for (int i = 0; i < related_list_json.length(); i++) {
                            RelatedPostsData obj_related = new RelatedPostsData();
                            JSONObject obj = related_list_json.getJSONObject(i);
                            if (obj.has("ID")) {
                                obj_related.setID(obj.getInt("ID"));
                            }
                            if (obj.has("post_title") && obj.getString("post_title") != null) {
                                obj_related.setPost_title(obj.getString("post_title"));
                            }
                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
                                obj_related.setThumbImage(obj.getString("thumbImage"));
                            }
                            related_List.add(obj_related);
                        }
                        _object.setList_related_post(related_List);
                    }
                }
                ArrayList<ContentSectionModel> contentSectionList = new ArrayList<>();
                ContentSectionModel obj_content = new ContentSectionModel();
                obj_content.setUrl(_object.getThumb_url());
                obj_content.setCaption("Featured");
                obj_content.setVideoContent(false);
                contentSectionList.add(obj_content);

                if (objectFirst.has("EmbededData")) {
                    JSONArray embededVideoList = objectFirst.getJSONArray("EmbededData");
                    for (int i = 0; i < embededVideoList.length(); i++) {
                        obj_content = new ContentSectionModel();
                        JSONObject obj = embededVideoList.getJSONObject(i);
                        obj_content.setUrl(obj.has("EmbededPath") ? obj.getString("EmbededPath") : "");
                        obj_content.setCaption(obj.has("Caption") ? obj.getString("Caption") : "");

                        if (obj_content.getUrl() != null && !obj_content.getUrl().isEmpty() && obj_content.getUrl().contains("youtu.be")) {

                            if (obj_content.getUrl().contains("youtu.be")) {
                                obj_content.setVideoContent(true);
                                String _youtube_id = obj_content.getUrl().replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "");
                                obj_content.setYoutube_url("http://img.youtube.com/vi/" + _youtube_id + "/0.jpg");
                            }
                        }
                        contentSectionList.add(obj_content);
                    }
                }

                try {
                    if (objectFirst.has("EmbededImage")) {
                        JSONArray embededImageList = objectFirst.getJSONArray("EmbededImage");
                        for (int i = 0; i < embededImageList.length(); i++) {
                            JSONObject object = embededImageList.getJSONObject(i);
                            obj_content = new ContentSectionModel();
                            obj_content.setUrl(object.has("EmbededPath") ? object.getString("EmbededPath") : "");
                            obj_content.setCaption(object.has("Caption") ? object.getString("Caption") : "");
                            obj_content.setVideoContent(false);
                            contentSectionList.add(obj_content);
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception at parseembeddd image " + e.getMessage());
                }
                _object.setFeaturedImage(contentSectionList);
                if (objectFirst.has("videos_and_files")) {

                    JSONArray videoArray = null;
                    try {
                        videoArray = objectFirst.getJSONArray("videos_and_files");
                    } catch (Exception e) {

                    }
                    if (videoArray != null)
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject obj = videoArray.getJSONObject(i);
                            videos_and_files videos_and_files = new videos_and_files();
                            if (obj.has("text_file") && !obj.getString("text_file").toString().equalsIgnoreCase("false")) {
                                text_files obj_text_file = new text_files();
                                JSONObject obj_text = obj.getJSONObject("text_file");
                                obj_text_file.setID(obj_text.has("ID") ? obj_text.getInt("ID") : 0);
                                obj_text_file.setTitle(obj_text.has("title") ? obj_text.getString("title") : "");
                                obj_text_file.setIcon(obj_text.has("icon") ? obj_text.getString("icon") : "");
                                obj_text_file.setFilename(obj_text.has("filename") ? obj_text.getString("filename") : "");
                                obj_text_file.setUrl(obj_text.has("url") ? obj_text.getString("url") : "");
                                videos_and_files.setObj_text_files(obj_text_file);
                            } else
                                videos_and_files.setObj_text_files(null);

                            try {
                                if (obj.has("trace_image") && !obj.getString("trace_image").toString().equalsIgnoreCase("false")) {
                                    trace_image obj_trace = new trace_image();
                                    JSONObject obj_trace_object = obj.getJSONObject("trace_image");
                                    obj_trace.setID(obj_trace_object.has("ID") ? obj_trace_object.getInt("ID") : 0);
                                    obj_trace.setTitle(obj_trace_object.has("title") ? obj_trace_object.getString("title") : "");
                                    obj_trace.setIcon(obj_trace_object.has("icon") ? obj_trace_object.getString("icon") : "");
                                    obj_trace.setFilename(obj_trace_object.has("filename") ? obj_trace_object.getString("filename") : "");
                                    obj_trace.setUrl(obj_trace_object.has("url") ? obj_trace_object.getString("url") : "");
                                    if (obj_trace_object.has("sizes")) {
                                        JSONObject objSize = obj_trace_object.getJSONObject("sizes");
                                        sizes obj_size = new sizes();
                                        obj_size.setLarge(objSize.has("large") ? objSize.getString("large") : "");
                                        obj_trace.setObj_sizes(obj_size);
                                    } else {
                                        obj_trace.setObj_sizes(null);
                                    }
                                    videos_and_files.setObj_trace_image(obj_trace);
                                } else
                                    videos_and_files.setObj_trace_image(null);

                            } catch (Exception e) {
                                Log.e("TAGGG", "Exception at add traceImage " + e.getMessage());
                            }
                            try {
                                if (obj.has("overlay_image") && !obj.getString("overlay_image").toString().equalsIgnoreCase("false")) {
                                    Overlaid overlaid = new Overlaid();
                                    JSONObject obj_overlaid_object = obj.getJSONObject("overlay_image");
                                    if (obj_overlaid_object != null) {
                                        overlaid.setTitle(obj_overlaid_object.has("title") ? obj_overlaid_object.getString("title") : "");
                                        overlaid.setFilename(obj_overlaid_object.has("filename") ? obj_overlaid_object.getString("filename") : "");
                                        overlaid.setUrl(obj_overlaid_object.has("url") ? obj_overlaid_object.getString("url") : "");
                                    }
                                    videos_and_files.setObj_overlaid(overlaid);
                                } else
                                    videos_and_files.setObj_overlaid(null);

                            } catch (Exception e) {
                                Log.e("TAGG", "Exception at getoverlay " + e.getMessage());
                            }
                            _lst_video_file.add(videos_and_files);
                        }

                    if (_lst_video_file != null && !_lst_video_file.isEmpty())
                        _object.setVideo_and_file_list(_lst_video_file);
                } else
                    _object.setVideo_and_file_list(null);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return _object;
    }
}
