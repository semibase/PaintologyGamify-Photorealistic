package com.paintology.lite.photorealistic.drawing.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

public class StringConstants {

    public static StringConstants constants = new StringConstants();

    public static StringConstants getInstance() {
        if (constants == null) {
            constants = new StringConstants();
        }
        return constants;
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    public static String app_banner_open = "app_banner_open";
    public static String app_banner_close = "app_banner_close";


    public static String CATE_ID = "3634";
    public static String APP_NAME = "photo-realistic";

    public static String daily_blog = "daily_blog";
    public static String learn_drawing_painting = "learn_drawing_painting";
    public static String online_tutorials = "online_tutorials";
    public static String youtube_paintology = "youtube_paintology";


    public static String isNewUser = "isNewUser";
    public static String isVideoShown = "isVideoShown";
    public static String isMenuShow = "isMenuShow";




    public static String intro_gallery = "00001";
    public static String intro_tutorials = "00002";
    public static String intro_community = "00003";
    public static String intro_draw = "00004";
    public static String intro_home = "00005";
    public static String intro_resources = "00006";
    public static String intro_leaderboard = "00007";
    public static String intro_paintings = "00008";
    public static String intro_points = "00009";
    public static String intro_progress = "00010";
    public static String intro_big_points = "00011";
    public static String intro_notifications = "00012";
    public static String intro_favorite = "00013";

    public static String user_level = "user_level";
    public static String beginner = "Beginner 1";
    // ACTIVITY

    public static String open_tutorial = "open_tutorial";

    public static String scribble_on_your_canvas = "scribble_on_your_canvas";
    public static String draw_strokes = "draw_strokes";
    public static String post_drawing_to_gallery = "post_drawing_to_gallery";
    public static String save_drawing = "save_drawing";


    public static String quora_paintology = "quora_paintology";
    public static String paintology_youtube = "paintology_youtube";
    public static String paintology_website = "paintology_website";
    public static String online_tutorial = "online_tutorial";
    public static String my_paintings_share = "my_paintings_share";
    public static String my_paintings_add_text = "my_paintings_add_text";
    public static String my_movies = "my_movies";
    public static String movies_share = "movies_share";
    public static String learn_drawing = "learn_drawing";
    public static String google_classroom = "google_classroom";
    public static String draw_trace_image = "draw_trace_image";
    public static String draw_overlay_image = "draw_overlay_image";
    public static String draw_camera_image = "draw_camera_image";
    public static String default_bonus_point = "default_bonus_point";
    public static String community_post_posted_hashtag = "community_post_posted_hashtag";
    public static String post_to_community = "post_to_community";
    public static String post_to_gallery = "post_to_gallery";

    public static String blog_website = "blog_website";
    public static String apps_by_paintology = "apps_by_paintology";


    // EVENTS

    // ## Video Intro
    public static String intro_video_watch = "intro_video_watch";
    public static String video_guides_open = "video_guides_open";
    public static String video_guides_close = "video_guides_close";

    // ## Gallery
    public static String gallery_post_open = "gallery_post_open";
    public static String gallery_post_open_author = "gallery_post_open_author";
    public static String gallery_post_open_author_level = "gallery_post_open_author_level";
    public static String gallery_post_share = "gallery_post_share";
    public static String gallery_post_do_tutorial = "gallery_post_do_tutorial";
    public static String gallery_post_learn_drawing = "gallery_post_learn_drawing";
    public static String gallery_post_add_favorite = "gallery_post_add_favorite";
    public static String gallery_post_filter = "gallery_post_filter";

    public static String gallery_post_enlarge = "gallery_post_enlarge";
    public static String gallery_post_like = "gallery_post_like";
    public static String gallery_post_unlike = "gallery_post_unlike";
    public static String gallery_post_comment = "gallery_post_comment";
    public static String gallery_post_reply_comment = "gallery_post_reply_comment";
    public static String gallery_post_rate = "gallery_post_like";



    // ## Tutorials
    public static String tutorials_post_filter = "tutorials_post_filter";
    public static String tutorials_open_category = "tutorials_open_category";
    public static String tutorials_open = "tutorials_open";
    public static String tutorials_share = "tutorials_share";
    public static String tutorials_open_link = "tutorials_open_link";
    public static String tutorials_rating = "tutorials_rating";
    public static String tutorials_add_favorite = "tutorials_add_favorite";
    public static String tutorials_remove_favorite = "tutorials_remove_favorite";

    // ## Notifications

    public static String notifications_read = "notifications_read";
    public static String notifications_open = "notifications_open";
    public static String notifications_delete = "notifications_delete";


    // ## Community
    public static String community_post_open = "community_post_open";
    public static String community_post_open_author = "community_post_open_author";
    public static String community_post_like = "community_post_like";
    public static String community_post_comment = "community_post_comment";
    public static String community_post_share = "community_post_share";
    public static String community_post_copy_link = "community_post_copy_link";
    public static String community_post_open_in_canvas = "community_post_open_in_canvas";
    public static String community_post_open_author_posts = "community_post_open_author_posts";
    public static String community_post_chat_author = "community_post_chat_author";
    public static String community_post_download = "community_post_download";
    public static String community_post_video_link = "community_post_video_link";
    public static String community_post_report = "community_post_report";
    public static String community_post_add_favorite = "community_post_add_favorite";
    public static String community_post_remove_favorite = "community_post_remove_favorite";

    // ## Leaderboards

    public static String leaderboards_filter = "leaderboards_filter";
    public static String leaderboards_open_user = "leaderboards_open_user";
    public static String ldbd_world_dialog_select = "ldbd_world_dialog_select";

    // ## Favorites
    public static String favorites_gallery_open = "favorites_gallery_open";
    public static String favorites_users_open = "favorites_users_open";
    public static String favorites_tutorials_open = "favorites_tutorials_open";
    public static String favorites_community_open = "favorites_community_open";

    // ## Chat
    public static String chat_send = "chat_send";

    // ## User
    public static String user_follow = "user_follow";
    public static String user_unfollow = "user_unfollow";


    // ## Strokes

    public static String canvas_draw_stroke = "canvas_draw_stroke";
    public static String canvas_undo_stroke = "canvas_undo_stroke";
    public static String canvas_redo_stroke = "canvas_redo_stroke";
    public static String canvas_strokes_draw_strokes = "canvas_strokes_draw_strokes";
    public static String canvas_strokes_undo_strokes = "canvas_strokes_undo_strokes";
    public static String canvas_tsstrokes_draw_strokes = "canvas_tsstrokes_draw_strokes";
    public static String canvas_tsstrokes_undo_strokes = "canvas_tsstrokes_undo_strokes";
    public static String canvas_colorbar_select = "canvas_colorbar_select";

    public static String big_points_open = "big_points_open";
    public static String big_points_do_activity = "big_points_do_activity";

    public static String home_button_gallery = "home_button_gallery";
    public static String home_screen_gallery = "home_gallery_press";


    public static String home_top_notifications = "home_top_notifications";
    public static String home_top_points_progress = "home_top_points_progress";
    public static String home_top_store = "home_top_store";
    public static String home_top_profile = "home_top_profile";
    public static String home_top_levels = "home_top_levels";
    public static String home_top_favorite = "home_top_favorite";
    public static String home_top_drawing_activity = "home_top_drawing_activity";
    public static String home_top_leaderboard = "home_top_leaderboard";
    public static String home_top_settings = "home_top_settings";



    public static String Freehand = "FREEHAND";
    public static String Tutorials = "TUTORAILS";

    public static String follower_following_more_menu_view_profile_click = "follower_following_more_menu_view_profile_click";
    public static String follower_following_more_menu_chat_click = "follower_following_more_menu_chat_click";
    public static String follower_following_more_menu_unfollow_click = "follower_following_more_menu_unfollow_click";

    public static Boolean IsFromDetailPage = false;


    public static String PREF = "myPref";
    public static String switch_to_tutorial = "switch_to_tutorial";
    public static String switch_to_strokes = "switch_to_strokes";
    public static String watch_video = "watch_video";

    String APP_START = "app_start";
    //    String PICK_IMAGE_FROM_ALBUM = "pick_image_my_paintings";
    public String Pick_Image_My_Paintings = "pick_image_my_paintings";
    public String Pick_Image_My_Movies = "pick_image_my_movies";

    String SHARE_IMAGE = "click_share_image";
    public static String SHARE_VIDEO = "click_share_video";

    public String DELETE_PAINTING_IMAGE = "delete_mypainting_image";
    public String DELETE_PAINTING_IMAGE_SUCCESS = "delete_mypainting_image_success";

    public String DELETE_MOVIE_SUCCES = "delete_mymovies_success";
    public String DELETE_MOVIE_FAIL = "delete_mymovies_fail";
    public String DELETE_MOVIE = "delete_mymovies";
    String PICK_COLOR_PICKER = "canvas_color_picker";
    String USE_ZOOM_FEATURE = "canvas_pinch_zoom";

    String PICK_NEW_CANVAS = "pick_canvas_selection";
    String CLOSE_APPLICATION = "Close_Application";
    String CLOSE_CANVAS = "close_canvas";


    ///BRUSH SELECTION EVENTS
    String PICK_HAZE_LIGHT_BRUSH = "brush_haze_light";
    String PICK_LINE_BRUSH = "brush_line";
    String PICK_HAZE_DARK_BRUSH = "brush_haze_dark";
    String PICK_MIST_BRUSH = "brush_mist";
    String PICK_LAND_PATCH_BRUSH = "brush_land_patch";
    String PICK_GRASS_BRUSH = "brush_grass";
    String PICK_MEADOW_BRUSH = "brush_meadown";
    String PICK_INDUSTRY_BRUSH = "brush_industry";
    String PICK_CHALK_BRUSH = "brush_chalk";
    String PICK_CHARCOAL_BRUSH = "brush_charcoal";
    String PICK_STICKS_BRUSH = "brush_sticks";
    String PICK_FLOWER_BRUSH = "brush_flower";
    String PICK_WAVE_BRUSH = "brush_wave";
    String PICK_ERASER_BRUSH = "brush_eraser";
    String PICK_SHADE_BRUSH = "brush_shade";
    String PICK_WATERCOLOR_BRUSH = "brush_watercolor";
    String PICK_SKETCH_OVAL_BRUSH = "brush_sketchoval";
    String PICK_SKETCH_FILL_BRUSH = "brush_sketch_fill";
    String PICK_SKETCH_PEN_BRUSH = "brush_sketch_pen";
    String PICK_SKETCH_WIRE_BRUSH = "brush_sketch_wired";
    String PICK_EMBOSS_BRUSH = "brush_emboss";

    public String formatType = "";

    public String OperationTypeComment = "doComment";
    public String OperationTypeLike = "Like";
    public String OperationTypeReport = "Report";
    public String OperationTypeChat = "Chat";
    public String OperationTypeView = "View";


    public String recent_Brush = "recent_Brush";
    public String manual_Brush = "manual_brush";
    public String last_patttern_number = "last_patttern_number";

    //Events for social media dialog.

    public String open_social_login_canvas_dialog = "social_dialog_canvas";
    public String open_social_login_canvas_dialog_link_click = "social_dialog_canvas_link_click";

    public String open_social_login_community_profile_dialog = "social_dialog_community_profile";

    public String open_social_login_community_like_dialog = "open_social_login_community_like_dialog";
    public String open_social_login_community_view_dialog = "open_social_login_community_like_dialog";

    public String open_social_login_community_comment_dialog = "social_dialog_community_post_comment";
    public String open_social_login_community = "You must be a social login user to use Community.";

    public String open_social_login_import_dialog = "open_social_login_import_dialog";
    public String open_social_login_import_dialog_link_click = "social_dialog_import_link_click";

    public String open_social_login_mypainting_dialog = "open_social_login_mypainting_dialog";
    public String open_social_login_mypainting_dialog_link_click = "social_dialog_mypaintings_link";

    public String open_report_dialog = "open_report_dialog";
    public String open_social_login_report_dialog = "open_social_login_report_dialog";
    public String open_social_login_community_new_post_dialog = "social_dialog_community_new_post";
    public String open_social_login_post_dialog_bgpls = "social_dialog_community_new_post_bigplus";
    public String click_community_menu_feedback = "click_community_menu_feedback";
    public String click_community_menu_googleplay_click = "click_community_menu_googleplay_click";

    public String open_social_login_profile_follow_dialog = "open_social_login_profile_follow_dialog";

    public String open_social_login_share_from_gallery_dialog = "social_dialog_share_from_gallery";
    public String open_social_login_share_from_gallery_link_click = "social_dialog_share_from_gallery_link";

    public String open_social_login_search_hashtag_profile_dialog = "social_dialog_search_hashtag_profile";
    public String open_social_login_search_hashtag_new_post_dialog = "social_dialog_search_hashtag_new_post_dialog";


    public String community_post_update_success = "community_post_update_success";
    public String community_post_update_failed = "community_post_update_fail";

    public String community_post_delete_success = "community_post_delete_success";
    public String community_post_delete_failed = "community_post_delete_fail";

    public String community_post_edit_click = "community_post_edit_click";

    public String feedback_sent_community = "feedback_sent_community";
    public String feedback_sent = "exit_feedback_press_sent";
    public String feedback_exit = "exit_feedback_press_exit";

    public String home_page_back_click_exit = "home_page_back_click_exit";
    public String home_page_back_click_cancel = "home_page_back_click_cancel";
    public String home_page_back_click_review = "home_page_back_click_review";
    public String home_page_back_click_share_app = "home_page_back_click_share_app";

    public String feedback_google_play_click = "feedback_google_play_click";
    public String feedback_google_play_click_community = "feedback_google_play_click_community";
    public String click_community_post_menu = "click_community_post_menu";
    public String feedback_close_community = "feedback_close_community";


    public String click_my_profile_menu_post = "click_my_profile_menu_post";
    public String click_my_profile_menu_feedback = "click_my_profile_menu_feedback";
    public String click_my_profile_menu_googleplay_click = "click_my_profile_menu_googleplay_click";
    public String click_my_profile_menu_logout = "click_my_profile_menu_logout";

    public String click_my_profile_menu_feedback_sent = "click_my_profile_menu_feedback_sent";
    public String click_my_profile_menu_feedback_close = "click_my_profile_menu_feedback_close";
    public String profile_header_menu_click = "profile_header_menu_click";

    public String community_header_menu_click = "community_header_menu_click";
    public String post_menu_click = "post_menu_click";

    public String collection_name = "user_sessions";
    public String document_name = "navigation_log";

    public String fireBaseToken = "fireBaseToken";

    public String _scree_width = "_screen_width";
    public String _scree_height = "_screen_height";

    //New Button Events
    public String HOME_BUTTON_CHALLENGE = "home_button_challenge";
    public String HOME_BUTTON_FIND_ABILITY = "home_button_find_ability";

    public String HOME_BUTTON_DRAW = "home_button_draw";
    public String MYRES_SCREEN_IMPORT_IMAGES = "myres_screen_import_images";
    public String CLICK_COMMUNITY = "click_community_button";
    public String HOME_BUTTON_COMMUNITY = "home_button_community";
    public String HOME_BUTTON_MY_RESOURCES = "home_button_my_resources";
    public String HOME_BUTTON_BIG_POINTS = "home_button_big_points";
    public String CLICK_HELP = "click_help_button";
    public String MYRES_SCREEN_ONLINE_TUTORIALS = "myres_screen_online_tutorials";
    public String MYRES_SCREEN_DAILY_BLOG = "myres_screen_daily_blog";


    public String HOME_BUTTON_SITES = "home_button_sites";

    public String CLOSE_MY_PAITING = "close_my_paintins";
    public String CLOSE_MY_PAITOLOGY_COLLECTION = "close_paintology_collection";

    //    public String CLICK_CONTACT_US = "Click Contact Us";
    public String CLICK_CONTACT_US = "url_contact_us";
    public String VISIT_HELP_GUIDE = "url_help_guide";
    public String VISIT_QUICK_GUIDE = "url_quick_guide";
    public String START_RECORDING = "canvas_record_start";
    public String PAUSE_RECORDING = "canvas_record_pause";
    public String STOP_RECORDING = "canvas_record_stop";
    public String SaveStrokeFile = "save_movies_strokes";
    public String SaveMovieFile = "canvas_save_movies";

    public String double_tap_image_community = "comm_post_zoom_dbltap";
    public String comm_post_zoom_close = "comm_post_zoom_close";

    public String firebase_deleted_user = "Deleted_Users";
    public String firebase_blocked_user = "Blocked_Users";
    public String firebase_user_list = "Users_List";
    public String firebase_chat_module = "Users_Chat";

    public String canvas_zoom_menuitem_reset_click = "canvas_zoom_menuitem_reset_click";
    public String canvas_zoom_menuitem_click = "canvas_zoom_menuitem_click";
    public String canvas_pinch_zoom = "canvas_pinch_zoom";
    public String comm_post_youtube_button_click = "comm_post_youtube_button_click";
    public static String gallery_post_youtube_button_click = "gallery_post_youtube_button_click";
    public String click_community_menu_share_paintology = "click_community_menu_share_paintology";

    public String canvas_zoom_menuitem_long_click = "canvas_zoom_menuitem_long_click";
    public String canvas_zoom_menuitem_dbl_tap = "canvas_zoom_menuitem_lock_dbl_click";
    public String canvas_zoom_unlock = "canvas_zoom_menuitem_unlock_click";
    public String auto_color_picker_activated = "Auto color picker activated";
    public String auto_color_picker_deactivated = "Auto color picker deactivated";


    public String post_menu_click_more_detail = "post_menu_click_more_detail";
    public String post_menu_click_share = "post_menu_click_share";
    public String post_menu_click_copy_link = "post_menu_click_copy_link";
    public String post_menu_click_open_canvas = "post_menu_click_open_canvas";
    public String post_menu_click_open_canvas_overlay = "post_menu_click_open_canvas_overlay";
    public String post_menu_click_open_canvas_trace = "post_menu_click_open_canvas_trace";
    public String post_menu_click_save = "post_menu_click_download";
    public String post_menu_click_report = "post_menu_click_report";
    public String post_menu_click_open_youtube = "post_menu_click_open_youtube";
    public String comm_post_youtube_image_click = "comm_post_youtube_image_click";

    String DEVICE_ID = "";

    //Event model fields
    String EVENT_NOTES = "EventNotes";
    String TIME_STAMP = "TimeStamp";
    String BRUSH_COLOR = "BrushColor";
    String IS_COLOR_CHANGE = "EventType";
    String BRUSH_FLOW = "BrushFlow";
    String BRUSH_ALPHA = "BrushAlpha";
    String BRUSH_SIZE = "BrushSize";
    String BRUSH_HARDNESS = "BrushHardness";
    String BRUSH_STYLE = "BrushStyle";
    String BRUSH_NAME = "BrushName";
    String BRUSH_EVENT_OBJECT = "ChangeBrushEventData";
    String FREQUENCY_DATA = "FrequencyData";
    String EVENTS_DATA = "EventsData";


    public String PICK_IMAGE_FOR_TRACE = "pick_trace_selection";
    public String PICK_IMAGE_FOR_OVERLAY = "pick_overlay_selection";
    public String draw_screen_blank_canvas = "draw_screen_blank_canvas";
    public String draw_screen_overlay_image = "draw_screen_overlay_image";
    public String draw_screen_trace_image = "draw_screen_trace_image";
    public String draw_screen_camera_mode = "draw_screen_camera_mode";


    public String DIALOG_PICK_CAMERA_OVERLAY = "dialog_pick_camera_overlay";
    public String DIALOG_PICK_CAMERA_TRACE = "dialog_pick_camera_trace";

    public String canvas_switch_mode_camere_overlay = "canvas_switch_mode_camere_overlay";
    public String canvas_switch_mode_camera_trace = "canvas_switch_mode_camera_trace";

    public String selected_language = "language";
    public String isLanguageSelected = "language_selected";


    //new event for stroke
    public String canvas_strokes_trace_slider_function = "canvas_strokes_trace_slider_function";
    public String canvas_Tstrokes_trace_slider_function = "canvas_Tstrokes_trace_slider_function";


    public String Canvas_color_picker_clear = "canvas_color_picker_clear";
    public String Canvas_color_picker_close = "canvas_color_picker_close";
    public String Canvas_color_picker_pick_color = "canvas_color_picker_pick_color";


    public String canvas_single_tap_on = "canvas_single_tap_on";
    public String canvas_single_tap_off = "canvas_single_tap_off";

    public String canvas_gray_scale_on = "canvas_gray_scale_on";
    public String canvas_gray_scale_off = "canvas_gray_scale_off";


    public String brush_dialog_line_off = "brush_dialog_line_off";
    public String brush_dialog_line_on = "brush_dialog_line_on";
    public String brush_dialog_create_brush_open = "brush_dialog_create_brush_open";
    public String custom_brush_menu_save = "custom_brush_menu_save";
    public String custom_brush_menu_freehand = "custom_brush_menu_freehand";
    public String custom_brush_menu_line = "custom_brush_menu_line";
    public String custom_brush_menu_square = "custom_brush_menu_square";
    public String custom_brush_menu_rectangle = "custom_brush_menu_rectangle";
    public String custom_brush_menu_circle = "custom_brush_menu_circle";
    public String custom_brush_menu_triangle = "custom_brush_menu_triangle";

    public String custom_brush_stroke_undo = "custom_brush_stroke_undo";
    public String custom_brush_stroke_redo = "custom_brush_stroke_redo";


    public String canvas_brush_plus_click = "canvas_brush_plus_click";
    public String canvas_brush_minus_click = "canvas_brush_minus_click";

    public String canvas_brush_plus_long_click = "canvas_brush_plus_long_click";
    public String canvas_brush_minus_long_click = "canvas_brush_minus_long_click";

    //Phase 2 new Events
    public String community_4x2_selection = "community_4x2_selection";
    public String community_3x2_selection = "community_3x2_selection";
    public String community_2x2_selection = "community_2x2_selection";
    public String community_1x1_selection = "community_1x1_selection";
    public String community_back_selection = "community_back_selection";


    public String user_profile_follow_sucess = "user_profile_follow_sucess";
    public String user_profile_follow_fail = "user_profile_follow_fail";
    public String user_profile_unfollow_success = "user_profile_unfollow_success";
    public String user_profile_unfollow_fail = "user_profile_unfollow_fail";

    public String user_profile_posts_clicks = "user_profile_posts_clicks";
    public String user_profile_followers_clicks = "user_profile_followers_clicks";
    public String user_profile_following_clicks = "user_profile_following_clicks";


    public String search_single_query = "search_single_query";
    public String search_multiple_query = "search_multiple_query";
    public String search_textentry_query = "search_textentry_query";
    public String search_multiple_textentry_query = "search_multiple_textentry_query";


    public String click_community_search_Mag = "click_community_search_Mag";
    public String click_community_search_Mag_close = "click_community_search_Mag_close";


    public String Click_Camera_Selection = "pick_camera_selection";

    public String community_OurAds_go_button_click = "community_OurAds_go_button_click";

    public String hashTagList = "HashTagList";

    public String IpAddress = "IpAddress";
    public String UserCity = "UserCity";
    public String UserCountry = "UserCountry";
    public String UserCountryCode = "UserCountryCode";

    public String IsFeedbackSent = "IsFeedbackSent";
    public String DeviceToken = "device_token";


    public static String user_profile_facebook_click = "user_profile_facebook_click";
    public static String user_profile_Instagram_click = "user_profile_Instagram_click";
    public static String user_profile_Youtube_click = "user_profile_Youtube_click";
    public static String user_profile_Twitter_click = "user_profile_Twitter_click";
    public static String user_profile_Linkedin_click = "user_profile_Linkedin_click";
    public static String user_profile_Website_click = "user_profile_Website_click";
    public static String user_profile_Quora_click = "user_profile_Quora_click";
    public static String user_profile_pinterest_click = "user_profile_pinterest_click";
    public static String user_profile_other_click = "user_profile_other_click";
    public static String user_profile_tiktok_click = "user_profile_tiktok_click";


    public String my_paintings_menuitem_open = "my_paintings_menuitem_open";
    public String my_paintings_menuitem_rename = "my_paintings_menuitem_rename";
    public String my_paintings_menuitem_open_in_trace = "my_paintings_menuitem_open_in_trace";
    public String my_paintings_menuitem_open_in_overlay = "my_paintings_menuitem_open_in_overlay";
    public String my_paintings_menuitem_add_text = "my_paintings_menuitem_add_text";
    public String my_paintings_menuitem_share = "my_paintings_menuitem_share";
    public String my_paintings_menuitem_download = "my_paintings_menuitem_download";
    public String my_paintings_menuitem_delete = "my_paintings_menuitem_delete";
    public String my_paintings_menuitem_cancel = "my_paintings_menuitem_cancel";
    public String my_paintings_icon_open = "my_paintings_icon_open";
    public String my_paintings_icon_delete = "my_paintings_icon_delete";
    public String my_paintings_icon_share = "my_paintings_icon_share";
    public String my_paintings_icon_community = "my_paintings_icon_community";
    public String my_paintings_draw_normal = "my_paintings_draw_normal";
    public String my_paintings_draw_overlay = "my_paintings_draw_overlay";
    public String my_paintings_draw_trace = "my_paintings_draw_trace";

    public String my_movies_menuitem_open = "my_movies_menuitem_open";
    public String my_movies_menuitem_rename = "my_movies_menuitem_rename";
    public String my_movies_menuitem_add_text = "my_movies_menuitem_add_text";
    public String my_movies_menuitem_share = "my_movies_menuitem_share";
    public String my_movies_menuitem_download = "my_movies_menuitem_download";
    public String my_movies_menuitem_delete = "my_movies_menuitem_delete";
    public String my_movies_menuitem_cancel = "my_movies_menuitem_cancel";

    public String my_movies_icon_open = "my_movies_icon_open";
    public String my_movies_icon_delete = "my_movies_icon_delete";
    public String my_movies_icon_share = "my_movies_icon_share";

    public String comm_screen_chat_icon = "comm_screen_chat_icon";
    public String comm_screen_profile_icon = "comm_screen_profile_icon";
    public String comm_screen_plus_icon = "comm_screen_plus_icon";
    public String comm_menuitem_view_mode = "comm_menuitem_view_mode";
    public String comm_menuitem_share_paintology = "comm_menuitem_share_paintology";
    public String comm_menuitem_share_feedback = "comm_menuitem_share_feedback";
    public String comm_menuitem_google_playstore = "comm_menuitem_google_playstore";

    public String comm_post_menuitem_press = "comm_post_menuitem_press";
    public String comm_post_menuitem_share = "comm_post_menuitem_share";
    public String comm_post_menuitem_copy_link = "comm_post_menuitem_copy_link";
    public String comm_post_menuitem_open_canvas_overlay = "comm_post_menuitem_open_canvas_overlay";
    public String comm_post_menuitem_open_canvas_trace = "comm_post_menuitem_open_canvas_trace";
    public String comm_post_menuitem_download = "comm_post_menuitem_download";
    public String comm_post_menuitem_report = "comm_post_menuitem_report";

    public void setHashTagList(String _lst) {
        hashTagList = _lst;
    }

    public String getHashTagList() {
        return hashTagList;
    }

    String TOTAL_EVENTS = "TotalEvents";
    String TOTAL_COLOR_CHANGE = "TotalColorChange";
    String TOTAL_Brush_CHANGE = "TotalBrushChange";

    String STROKE = "Stroke";
    String TraceList_Gson_Key = "TraceList";
    String OverlayList_Gson_Key = "OverlayList";
    String ImportImageList_Gson_Key = "ImportImageList";


    String HOME_BUTTON_NEW_DRAW = "home_button_new_draw";
    String HOME_BUTTON_MY_PAINTINGS = "home_button_my_paintings";
    public String MYRES_SCREEN_MY_MOVIES = "myres_screen_my_movies";
    String OPEN_My_Moviews = "myres_screen_my_movies";

    public String FACEBOOK_LOGIN = "social_fb_button_click";
    public String GOOGLE_LOGIN = "social_google_button_click";
    public String Social_Paintology_Login = "social_paintology_button_click";

    public String GoogleLoginFailed = "social_google_login_fail";
    public String GoogleLoginSuccess = "social_google_login_success";
    public String GoogleRegistration = "social_google_registration";

    public String PaintologyLoginSuccess = "social_paintology_login_success";
    public String PaintologyLoginFailed = "social_paintology_login_fail";
    public String PaintologyRegistration = "social_paintology_registration";


    public String FacebookLoginFailed = "social_fb_login_fail";
    public String FacebookLoginSuccess = "social_fb_login_success";
    public String FacebookRegister = "social_fb_registration";


    public String VISIT_SKETCH_PHOTO = "s3_view_files";
    public String IMPORT_SCREEN_IMPORT_IMAGES = "import_screen_import_images";
    public String IMPORT_SCREEN_SAVE_IMAGES = "import_screen_save_images";
    /*public String IMPORT_SKETCH_PHOTO_SUCCESS_MULTIPLE = "import_mltpl_images_to_paintings_success";
    public String IMPORT_SKETCH_PHOTO_SUCCESS = "click_import_images_to_paintings_success";
    public String IMPORT_SKETCH_PHOTO_FAIL = "click_import_images_to_paintings_fail";*/

    public String import_image_single_to_success = "import_image_single_to_success";
    public String import_image_single_to_fail = "import_image_single_to_fail";

    public String import_image_multiple_to_success = "import_image_multiple_to_success";
    public String import_image_multiple_to_fail = "import_image_multiple_to_fail";


    public String VISIT_HOW_TO_EXPORT = "url_how_to_export";

    String Canvas_Background_Color = "Canvas Background Color";

    public String CLICK_COLLECTION = "click_from_mycollection";
    public String LoadStrokeFile = "playback_strokes";

    public String getCanvas_Background_Color() {
        return Canvas_Background_Color;
    }

    public String Username = "username";
    public String Password = "password";
    public String Email = "email";
    public String UserId = "UserId";
    public String IsFileUploaded = "IsFileUploaded";
    public String IsGuestUser = "IsGuestUser";
    public String Salt = "salt";
    public String LoginInPaintology = "LoginInPaintology";
    public String ProfilePicsUrl = "ProfilePicUrl";
    public String UserGender = "userGender";
    public static String SelectedUserId = "SelectedUserId";


    public String MALE = "male";
    public String FEMALE = "female";

    public String DisplayedCollection = "IsDisplayedCollection";
    public String DisplayedMyPainting = "IsDisplayedMyPainting";
    public String DisplayedMyMovies = "IsDisplayedMyMovies";


    public String UploadZipFileSuccess = "s3_upload_files_success";
    public String UploadZipFileFail = "s3_upload_files_fail";

    public String allow_storage_permission = "permission_storage_allow";
    public String deny_storage_permission = "permission_storage_deny";

    public String allow_camera_permission = "permission_camera_allow";
    public String deny_camera_permission = "permission_camera_deny";

    public String allow_recording_permission = "permission_recording_allow";
    public String deny_recording_permission = "permission_recording_deny";


    public String lang_select_start_ = "lang_select_start_";
    public String lang_select_header_ = "lang_select_header_";
    public String lang_select_header_cancel = "lang_select_header_cancel";
    public String lang_select_header_click = "lang_select_header_click";

    public String tutorial_intermediary_thumb_clicks = "tutorial_intermediary_thumb_clicks";


    public String _android_device_id = "_android_device_id";
    public String is_dont_show_selected = "is_dont_show_selected";
    public String is_dont_show_selected_canvas = "is_dont_show_selected_canvas";


    //New Events
    public String strokes_dialog_message = "strokes_dialog_message";
    public String strokes_dialog_message_chkbox = "strokes_dialog_message_chkbox";
    public String strokes_dialog_message_close = "strokes_dialog_message_close";


    public String dialog_canvas_message = "dialog_canvas_message";
    public String dialog_canvas_message_chkbox = "dialog_canvas_message_chkbox";
    public String dialog_canvas_message_close = "dialog_canvas_message_close";


    public String mypaintings_youtube_url = "mypaintings_youtube_url";
    public String mypaintings_youtube_thumb = "mypaintings_youtube_thumb";

    public String mymovies_youtube_url = "mymovies_youtube_url";
    public String mymovies_youtube_thumb = "mymovies_youtube_thumb";

    public String links_for_you_website = "links_for_you_website";
    public String links_for_you_youtube = "links_for_you_youtube";
    public String links_for_you_learn_drawing = "links_for_you_learn_drawing";
    public String links_for_you_apps = "links_for_you_apps";

    public String mypaintings_open_video_guides = "mypaintings_open_video_guides";
    public String mymovies_open_video = "mymovies_open_video";

    public String mymovie_video_icon_click = "mymovie_video_icon_click";
    public String mypainting_video_icon_click = "mypainting_video_icon_click";

    public String gap_event = "gap";
    public String my_paintings_long_press = "my_paintings_long_press";

    public String chat_home_header_click = "chat_home_header_click";
    public String chat_community_header_click = "chat_community_header_click";
    public String chat_community_post_click = "chat_community_post_click";
    public String chat_open_user_click = "chat_open_user_click";

    public String profile_home_header_click = "profile_home_header_click";
    public String chat_user_menu_click = "chat_userlist_menu_click";


    public String chat_conversation_window = "chat_conversation_window";

    public String chat_menu_see_user_profile = "chat_menu_see_user_profile";
    public String chat_menu_see_user_posts = "chat_menu_see_user_posts";
    public String chat_menu_block_user = "chat_menu_block_user";
    public String chat_menu_block_user_success = "chat_menu_block_user_success";
    public String chat_menu_unblock_user_success = "chat_menu_unblock_user_success";
    public String chat_menu_chat_click = "chat_menu_chat_click";

    public String chat_menu_delete_user = "chat_menu_delete_user";
    public String chat_menu_delete_user_sucess = "chat_menu_delete_user_sucess";

    public String onboarding_completed = "onboarding_completed";
    public String help_open = "help_open";
    public String help_skip = "help_skip";
    public String help_close = "help_close";

//    public String chat_user_menu_click = "chat_user_menu_click";


    public String getAPP_START() {
        return APP_START;
    }

//    public String getPICK_IMAGE_FROM_ALBUM() {
//        return PICK_IMAGE_FROM_ALBUM;
//    }

    public String registration_url = "https://www.jumpdates.com/registration_one.php";

    //New Events
    public String click_home_minus = "click_home_minus";
    public String CLICK_DRAW_ICON = "click_draw_icon";
    public String HOME_DRAW_ICON = "home_draw_icon";
    public String url_ad_101_import = "ad_4th_amazon_import_click";
    public String url_ad_101_draw = "ad_4th_amazon_draw_click";
    public String drawpage_banner_paintology_youtube = "drawpage_banner_paintology_youtube";
    public String url_ad_101 = "url_ad_101";
    public String url_ad_102 = "url_ad_102";
    public String click_exit_app = "click_exit_app";
    public String exit_app = "exit_app";
    public String exit_cancel = "exit_cancel";
    public String home_page_back_click = "home_page_back_click_exit";
    public String canvas_back_home = "canvas_back_home";
    public String canvas_backpress = "canvas_backpress";
    public String canvas_save_painting = "canvas_save_painting";
    public String canvas_save_painting_success = "canvas_save_painting_success";
    public String canvas_save_painting_fail = "canvas_save_painting_fail";
    public String canvas_community_click = "canvas_community_click";

    public String canvas_save_new_click = "canvas_save_new_click";
    public String canvas_screenshot_click = "canvas_screenshot_click";
    public String canvas_trace_slider_function = "canvas_trace_slider_function";

    public String canvas_color_box_select = "canvas_color_box_select";
    public String canvas_background_box_select = "canvas_background_box_select";
    public String canvas_select_brush = "canvas_select_brush";
    public String canvas_share_click = "canvas_share_click";
    public String draw_trace_left_toggle1 = "draw_trace_left_toggle1";
    public String draw_trace_left_toggle2 = "draw_trace_left_toggle2";
    public String draw_trace_right_toggle1 = "draw_trace_right_toggle1";
    public String draw_trace_right_toggle2 = "draw_trace_right_toggle2";


    public static String subcat_banner_udemy = "subcat_banner_udemy";
    public static String drawpage_banner_udemy = "drawpage_banner_udemy";
    public String auto_save_drawing_10mins = "auto_save_drawing_10mins";


    public String canvas_record_mic_on = "canvas_record_mic_on";
    public String canvas_record_mic_of = "canvas_record_mic_off";

    public String canvas_close_movies = "close_my_movies";
    public String canvas_switch_mode_blank = "canvas_switch_mode_blank";
    public String canvas_switch_mode_overlay = "canvas_switch_mode_overlay";
    public String canvas_switch_mode_trace = "canvas_switch_mode_trace";
    public String canvas_switch_mode_camera = "canvas_switch_mode_camera";
    public String canvas_youtube_toggle = "canvas_youtube_toggle";
    public String share_movie_click = "click_share_movies";
    public String click_start_tutorial = "click_start_tutorial";
    //    public String switch_to_canvas_from_player = "click_player_toggle";
    public String switch_to_canvas = "click_player_toggle";

    public String get_ip_failed = "get_ip_failed";

    public String next_stroke = "next_stroke";
    public String previous_stroke = "previous_stroke";

    public String pause_stroke_playing = "pause_stroke_playing";
    public String resume_stroke_playing = "play_stroke_playing";


    public String userAbilityFromPref = "userAbility";
    public String userArtFavFromPref = "userArtFav";
    public String userArtMedFromPref = "userArtMed";


    public String toggle_pcollection_canvas_click = "toggle_pcollection_canvas_click";
    public String toggle_pcollection_video_click = "toggle_pcollection_video_click";

    public String toggle_user_canvas_click = "toggle_user_canvas_click";
    public String toggle_user_video_click = "toggle_user_video_click";


    public String click_logout = "my_profile_click_logout";
    public String logout_success = "my_profile_logout_success";
    public String logout_cancel = "my_profile_logout_cancel";

    //    public String view_my_profile = "view_my_profile";
    public String HOME_MY_PROFILE_VIEW = "home_my_profile_view";
    public String my_profile_save = "my_profile_save";
    public String my_profile_save_success = "my_profile_save_success";
    public String my_profile_save_failed = "my_profile_save_failed";
    public String my_profile_edit_status = "my_profile_edit_status";

    public String my_profile_posts_click = "my_profile_posts_click";
    public String my_profile_followers_click = "my_profile_followers_click";
    public String my_profile_following_click = "my_profile_following_click";
    public String my_profile_image_edit = "my_profile_image_edit";

    public String canvas_banner_ad_click = "canvas_banner_ad_click";
    public String ad_1st_comm_draw_click = "ad_1st_comm_draw_click";
    public String ad_1st_comm_import_click = "ad_1st_comm_import_click";
    public String drawpage_banner_ferdouse_youtube = "drawpage_banner_ferdouse_youtube";

    public String ad_XX_tutorial_banner_click = "ad_XX_tutorial_banner_click";
    public String ad_XX_draw_banner_click = "ad_XX_draw_banner_click";
    public String ad_XX_dashboard_banner_click = "ad_XX_dashboard_banner_click";
    public String ad_XX_import_banner_click = "ad_XX_import_banner_click";
    public String ad_XX_resources_banner_click = "ad_XX_resources_banner_click";



    public String open_post_detail = "open_post_detail";
    //    public String click_community_post = "click_community_post";
    public String click_community_post = "click_community_post_plus";
    public String click_community_post_Bigplus = "click_community_post_Bigplus";

    public String view_communty_page = "view_communty_page";
    public String search_communty_page = "comm_<";
    public String my_paintings_community_post_click = "my_paintings_community_post_click";


    public String post_image_click = "post_image_click";
    public String post_image_click_success = "post_image_click_success";
    public String post_image_click_failed = "post_image_click_failed";
    public String click_community_user_image = "click_community_user_image";

    public String brush_fountain = "brush_fountain";
    public String brush_lane = "brush_lane";
    public String brush_streak = "brush_streak";
    public String brush_foliage = "brush_foliage";
    public String brush_felt = "brush_felt";
    public String brush_halo = "brush_halo";
    public String brush_outline = "brush_outline";

    public String brush_cube_line = "brush_cube_line";
    public String brush_dash_line = "brush_dash_line";

    public String BrushInkPen = "brush_inkpen";
    public String BrushRainbow = "brush_rainbow";


    public String event_failed_to_adduser = "failed_to_add_user";
    public String event_service_un_available = "service_temporarily_unavailable";

    public String version_dialog_open = "version_dialog_open";
    public String version_dialog_cancel = "version_dialog_cancel";
    public String version_dialog_go_to_playstore = "version_dialog_go_to_playstore";

    public String view_community_page_profile = "view_community_page_profile";

    public static String search_header_textentry_query = "search_header_textentry_query";

    public String dont_show_dialog = "dont_show_dialog";

    public String community_posting_image_screen = "community_posting_image_screen";

    public String plus_home_press = "plus_home_press";
    public String plus_quick_draw = "plus_quick_draw";
    public String plus_rate_app = "plus_rate_app";
    public String plus_share_app = "plus_share_app";
    public String plus_about = "plus_about";
    public String plus_help = "plus_help";
    public String rate_the_app_total_users = "rate_the_app_total_users";
    public static String canvas_save_press = "canvas_save_press";
    public static String home_search_press = "home_search_press";

    public static String sites_banner_quora = "sites_banner_quora";
    public static String sites_banner_google = "sites_banner_google";
    public static String draw_scren_banner_google = "draw_scren_banner_google";
    public static String draw_screen_banner_quora = "draw_screen_banner_quora";

    public static String slidepop_profile = "slidepop_profile";
    public static String slidepop_quick_draw = "slidepop_quick_draw";
    public static String slidepop_draw = "slidepop_draw";

    public static String slidepop_leaderboard = "slidepop_leaderboard";
    public static String slidepop_levels = "slidepop_levels";
    public static String slidepop_my_drawings = "slidepop_my_drawings";
    public static String slidepop_big_points = "slidepop_big_points";
    public static String slidepop_community = "slidepop_community";
    public static String slidepop_tutorials = "slidepop_tutorials";
    public static String slidepop_gallery = "slidepop_gallery";
    public static String slidepop_notifications = "slidepop_notifications";
    public static String slidepop_notif_login = "slidepop_notif_login";
    public static String slidepop_notif_comm = "slidepop_notif_comm";
    public static String slidepop_notif_chat = "slidepop_notif_chat";
    public static String slidepop_chat = "slidepop_chat";
    public static String slidepop_chat_login = "slidepop_chat_login";
    public static String slidepop_store = "slidepop_store";
    public static String slidepop_fav = "slidepop_fav";
    public static String slidepop_rate = "slidepop_rate";
    public static String slidepop_share = "slidepop_share";
    public static String slidepop_support = "slidepop_support";
    public static String slidepop_settings = "slidepop_settings";
    public static String slidepop_help_intro = "slidepop_help_intro";
    public static String slidepop_video_guides = "slidepop_video_guides";
    public static String slidepop_lang = "slidepop_lang";
    public static String slidepop_about = "slidepop_about";
    public static String slidepop_exit = "slidepop_exit";
    public static String slidepop_login = "slidepop_login";
    public static String slidepop_logout = "slidepop_logout";
    public static String TUTORIAL_MENU_OPEN = "tutorial_menu_open";
    public static String TUTORIAL_MENU_SHARE = "tutorial_menu_share";
    public static String TUTORIAL_MENU_RATING = "tutorial_menu_rating";
    public String slidepop_community_post = "slidepop_community_post";

    public String user_chats = "user_chats";

    public String getSHARE_IMAGE() {
        return SHARE_IMAGE;
    }

    public String getPICK_COLOR_PICKER() {
        return PICK_COLOR_PICKER;
    }

    public String getUSE_ZOOM_FEATURE() {
        return USE_ZOOM_FEATURE;
    }



    public String getPICK_NEW_CANVAS() {
        return PICK_NEW_CANVAS;
    }

    public String getCLOSE_APPLICATION() {
        return CLOSE_APPLICATION;
    }

    public String getCLOSE_CANVAS() {
        return CLOSE_CANVAS;
    }

    public String getPICK_HAZE_LIGHT_BRUSH() {
        return PICK_HAZE_LIGHT_BRUSH;
    }

    public String getPICK_LINE_BRUSH() {
        return PICK_LINE_BRUSH;
    }

    public String getPICK_HAZE_DARK_BRUSH() {
        return PICK_HAZE_DARK_BRUSH;
    }

    public String getPICK_MIST_BRUSH() {
        return PICK_MIST_BRUSH;
    }

    public String getPICK_LAND_PATCH_BRUSH() {
        return PICK_LAND_PATCH_BRUSH;
    }

    public String getPICK_GRASS_BRUSH() {
        return PICK_GRASS_BRUSH;
    }

    public String getPICK_MEADOW_BRUSH() {
        return PICK_MEADOW_BRUSH;
    }

    public String getPICK_INDUSTRY_BRUSH() {
        return PICK_INDUSTRY_BRUSH;
    }

    public String getPICK_CHALK_BRUSH() {
        return PICK_CHALK_BRUSH;
    }

    public String getPICK_CHARCOAL_BRUSH() {
        return PICK_CHARCOAL_BRUSH;
    }

    public String getPICK_STICKS_BRUSH() {
        return PICK_STICKS_BRUSH;
    }

    public String getPICK_FLOWER_BRUSH() {
        return PICK_FLOWER_BRUSH;
    }

    public String getPICK_WAVE_BRUSH() {
        return PICK_WAVE_BRUSH;
    }

    public String getPICK_ERASER_BRUSH() {
        return PICK_ERASER_BRUSH;
    }

    public String getPICK_SHADE_BRUSH() {
        return PICK_SHADE_BRUSH;
    }

    public String getPICK_WATERCOLOR_BRUSH() {
        return PICK_WATERCOLOR_BRUSH;
    }

    public String getPICK_SKETCH_OVAL_BRUSH() {
        return PICK_SKETCH_OVAL_BRUSH;
    }

    public String getPICK_SKETCH_FILL_BRUSH() {
        return PICK_SKETCH_FILL_BRUSH;
    }

    public String getPICK_SKETCH_PEN_BRUSH() {
        return PICK_SKETCH_PEN_BRUSH;
    }

    public String getPICK_SKETCH_WIRE_BRUSH() {
        return PICK_SKETCH_WIRE_BRUSH;
    }

    public String getPICK_EMBOSS_BRUSH() {
        return PICK_EMBOSS_BRUSH;
    }

    public String getDEVICE_ID() {
        return DEVICE_ID;
    }

    public String getEVENT_NOTES() {
        return EVENT_NOTES;
    }

    public String getTIME_STAMP() {
        return TIME_STAMP;
    }

    public String getBRUSH_COLOR() {
        return BRUSH_COLOR;
    }

    public String getIS_COLOR_CHANGE() {
        return IS_COLOR_CHANGE;
    }

    public String getBRUSH_FLOW() {
        return BRUSH_FLOW;
    }

    public String getBRUSH_ALPHA() {
        return BRUSH_ALPHA;
    }

    public String getBRUSH_SIZE() {
        return BRUSH_SIZE;
    }

    public String getBRUSH_HARDNESS() {
        return BRUSH_HARDNESS;
    }

    public String getBRUSH_STYLE() {
        return BRUSH_STYLE;
    }

    public String getBRUSH_NAME() {
        return BRUSH_NAME;
    }

    public String getBRUSH_EVENT_OBJECT() {
        return BRUSH_EVENT_OBJECT;
    }

    public String getFREQUENCY_DATA() {
        return FREQUENCY_DATA;
    }

    public String getEVENTS_DATA() {
        return EVENTS_DATA;
    }

    public String getTOTAL_EVENTS() {
        return TOTAL_EVENTS;
    }

    public String getTOTAL_COLOR_CHANGE() {
        return TOTAL_COLOR_CHANGE;
    }

    public String getTOTAL_Brush_CHANGE() {
        return TOTAL_Brush_CHANGE;
    }

    public String getSTROKE() {
        return STROKE;
    }

    public String getTraceList_Gson_Key() {
        return TraceList_Gson_Key;
    }

    public String getOverlayList_Gson_Key() {
        return OverlayList_Gson_Key;
    }

    public String getImportImageList_Gson_Key() {
        return ImportImageList_Gson_Key;
    }

    public String getHOME_BUTTON_NEW_DRAW() {
        return HOME_BUTTON_NEW_DRAW;
    }

    public String getHOME_BUTTON_MY_PAINTINGS() {
        return HOME_BUTTON_MY_PAINTINGS;
    }

    public String getOPEN_My_Moviews() {
        return OPEN_My_Moviews;
    }


    public void putInt(String key, Integer value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public Integer getInt(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    public void putString(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    public void putBoolean(String key, Boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolean(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

}
