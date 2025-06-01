package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.provider.BaseColumns;

public class DrawingContract {
    private DrawingContract() {}

    public static class DrawingEntry implements BaseColumns {

        // User Fav String

        public static final String TABLE_NAME_PROFILE = "user_profile";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_USER_DESCRIPTION = "description";
        public static final String COLUMN_PROFILE_IMAGE = "profile_image";
        public static final String COLUMN_USER_COUNTRY_FLAG = "country_flag";

        // Drawing DatabaseStrings
        public static final String TABLE_NAME = "drawings";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_REFERENCE_ID = "reference_id";
        public static final String COLUMN_TAGS = "tags";
        public static final String COLUMN_IMAGES_CONTENT = "images_content";
        public static final String COLUMN_METADATA_PATH = "metadata_path";
        public static final String COLUMN_METADATA_PARENT_FOLDER_PATH = "metadata_parent_folder_path";
        public static final String COLUMN_METADATA_TUTORIAL_ID = "metadata_tutorial_id";
        public static final String COLUMN_STATISTICS_COMMENTS = "statistics_comments";
        public static final String COLUMN_STATISTICS_LIKES = "statistics_likes";
        public static final String COLUMN_STATISTICS_RATINGS = "statistics_ratings";
        public static final String COLUMN_STATISTICS_REVIEWS_COUNT = "statistics_reviews_count";
        public static final String COLUMN_STATISTICS_SHARES = "statistics_shares";
        public static final String COLUMN_STATISTICS_VIEWS = "statistics_views";
        public static final String COLUMN_AUTHOR_USER_ID = "author_user_id";
        public static final String COLUMN_AUTHOR_NAME = "author_name";
        public static final String COLUMN_AUTHOR_AVATAR = "author_avatar";
        public static final String COLUMN_AUTHOR_COUNTRY = "author_country";
        public static final String COLUMN_AUTHOR_LEVEL = "author_level";
        public static final String COLUMN_LINKS_YOUTUBE = "links_youtube";
        public static final String COLUMN_BELONG_SCREEN = "belong_screen";
    }
}

