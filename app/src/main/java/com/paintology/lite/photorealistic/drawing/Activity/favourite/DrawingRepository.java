package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawingRepository {
    private SQLiteDatabase db;
    private Context context;

    public DrawingRepository(Context context) {
        this.context = context;
        DrawingDbHelper dbHelper = new DrawingDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertDrawing(NewDrawing drawing) {

        if (CheckGalleryIdExists(drawing.getId())) {
            Toast.makeText(context, "Already added to Favorites!", Toast.LENGTH_SHORT).show();
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(DrawingContract.DrawingEntry.COLUMN_ID, drawing.getId());
        values.put(DrawingContract.DrawingEntry.COLUMN_TITLE, drawing.getTitle());
        values.put(DrawingContract.DrawingEntry.COLUMN_DESCRIPTION, drawing.getDescription());
        values.put(DrawingContract.DrawingEntry.COLUMN_CREATED_AT, drawing.getCreatedAt());
        values.put(DrawingContract.DrawingEntry.COLUMN_TYPE, drawing.getType());
        values.put(DrawingContract.DrawingEntry.COLUMN_REFERENCE_ID, drawing.getReferenceId());
        values.put(DrawingContract.DrawingEntry.COLUMN_TAGS, TextUtils.join(",", drawing.getTags()));
        values.put(DrawingContract.DrawingEntry.COLUMN_IMAGES_CONTENT, drawing.getImages().getContent());
        values.put(DrawingContract.DrawingEntry.COLUMN_METADATA_PATH, drawing.getMetadata().getPath());
        values.put(DrawingContract.DrawingEntry.COLUMN_METADATA_PARENT_FOLDER_PATH, drawing.getMetadata().getParentFolderPath());
        values.put(DrawingContract.DrawingEntry.COLUMN_METADATA_TUTORIAL_ID, drawing.getMetadata().getTutorialId());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_COMMENTS, drawing.getStatistic().getComments());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_LIKES, drawing.getStatistic().getLikes());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_RATINGS, drawing.getStatistic().getRatings());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_REVIEWS_COUNT, drawing.getStatistic().getReviewsCount());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_SHARES, drawing.getStatistic().getShares());
        values.put(DrawingContract.DrawingEntry.COLUMN_STATISTICS_VIEWS, drawing.getStatistic().getViews());
        values.put(DrawingContract.DrawingEntry.COLUMN_AUTHOR_USER_ID, drawing.getAuthor().getUserId());
        values.put(DrawingContract.DrawingEntry.COLUMN_AUTHOR_NAME, drawing.getAuthor().getName());
        values.put(DrawingContract.DrawingEntry.COLUMN_AUTHOR_AVATAR, drawing.getAuthor().getAvatar());
        values.put(DrawingContract.DrawingEntry.COLUMN_AUTHOR_COUNTRY, drawing.getAuthor().getCountry());
        values.put(DrawingContract.DrawingEntry.COLUMN_AUTHOR_LEVEL, drawing.getAuthor().getLevel());
        values.put(DrawingContract.DrawingEntry.COLUMN_LINKS_YOUTUBE, drawing.getLinks().getYoutube());

        Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();

//        values.put(DrawingContract.DrawingEntry.COLUMN_BELONG_SCREEN, BelongScreen);

        return db.insert(DrawingContract.DrawingEntry.TABLE_NAME, null, values);
    }

    private boolean CheckGalleryIdExists(String id) {

        Cursor cursor = db.rawQuery("SELECT * FROM " + DrawingContract.DrawingEntry.TABLE_NAME + " WHERE " + DrawingContract.DrawingEntry.COLUMN_ID + " = ?", new String[]{id});

        if (cursor.getCount() > 0) {
//            db.close();
            return true;
        } else {
            return false;
        }
    }

    private boolean CheckUserIdExists(String id) {

        Cursor cursor = db.rawQuery("SELECT * FROM " + DrawingContract.DrawingEntry.TABLE_NAME_PROFILE + " WHERE " + DrawingContract.DrawingEntry.COLUMN_USER_ID + " = ?", new String[]{id});

        if (cursor.getCount() > 0) {
            db.close();
            return true;
        } else {
            return false;
        }
    }

    public void RemoveDrawing(String id) {
        db.delete(DrawingContract.DrawingEntry.TABLE_NAME, DrawingContract.DrawingEntry.COLUMN_ID + " = ?", new String[]{id});

    }

    // Implement other CRUD operations like update, query, delete

    public Cursor getAllDrawings() {
        String[] projection = {
                DrawingContract.DrawingEntry._ID,
                DrawingContract.DrawingEntry.COLUMN_ID,
                DrawingContract.DrawingEntry.COLUMN_TITLE,
                DrawingContract.DrawingEntry.COLUMN_DESCRIPTION,
                DrawingContract.DrawingEntry.COLUMN_CREATED_AT,
                DrawingContract.DrawingEntry.COLUMN_TYPE,
                DrawingContract.DrawingEntry.COLUMN_REFERENCE_ID,
                DrawingContract.DrawingEntry.COLUMN_TAGS,
                DrawingContract.DrawingEntry.COLUMN_IMAGES_CONTENT,
                DrawingContract.DrawingEntry.COLUMN_METADATA_PATH,
                DrawingContract.DrawingEntry.COLUMN_METADATA_PARENT_FOLDER_PATH,
                DrawingContract.DrawingEntry.COLUMN_METADATA_TUTORIAL_ID,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_COMMENTS,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_LIKES,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_RATINGS,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_REVIEWS_COUNT,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_SHARES,
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_VIEWS,
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_USER_ID,
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_NAME,
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_AVATAR,
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_COUNTRY,
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_LEVEL,
                DrawingContract.DrawingEntry.COLUMN_LINKS_YOUTUBE
        };

        return db.query(
                DrawingContract.DrawingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    public boolean isUserIdExists(String userId) {
        String selection = DrawingContract.DrawingEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {userId};

        Cursor cursor = db.query(
                DrawingContract.DrawingEntry.TABLE_NAME_PROFILE,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    public List<com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing> getAllDrawingsList() {
        List<com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing> drawings = new ArrayList<>();

        Cursor cursor = db.query(
                DrawingContract.DrawingEntry.TABLE_NAME,
                null, null, null, null, null, DrawingContract.DrawingEntry.COLUMN_ID + " DESC");

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_DESCRIPTION));
            @SuppressLint("Range") String createdAt = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_CREATED_AT));
            @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_TYPE));
            @SuppressLint("Range") String referenceId = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_REFERENCE_ID));
            @SuppressLint("Range") List<String> tags = Arrays.asList(cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_TAGS)).split(","));
            @SuppressLint("Range") com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images images = new com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images(cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_IMAGES_CONTENT)));


            @SuppressLint("Range") com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata metadata = new com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata(
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_METADATA_PATH)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_METADATA_PARENT_FOLDER_PATH)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_METADATA_TUTORIAL_ID))
            );
            @SuppressLint("Range")
            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic statistic = new
                    com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic(
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_COMMENTS)),
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_LIKES)),
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_RATINGS)),
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_REVIEWS_COUNT)),
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_SHARES)),
                    cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_STATISTICS_VIEWS))
            );
            @SuppressLint("Range") com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author author = new com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author(
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_AUTHOR_AVATAR)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_AUTHOR_COUNTRY)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_AUTHOR_LEVEL)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_AUTHOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_AUTHOR_USER_ID))


            );
            @SuppressLint("Range") com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links links = new com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links(cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_LINKS_YOUTUBE)));

            com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing drawing = new com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing(author, createdAt, description, id, images, links, metadata, referenceId, statistic, tags, title, type);
            drawings.add(drawing);
        }
        cursor.close();
        return drawings;
    }


    public List<UserProfileFav> getUserProfiles() {
        List<UserProfileFav> userProfiles = new ArrayList<>();

        Cursor cursor = db.query(
                DrawingContract.DrawingEntry.TABLE_NAME_PROFILE,
                null, null, null, null, null, DrawingContract.DrawingEntry._ID + " DESC");

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DrawingContract.DrawingEntry._ID));
            @SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_USER_ID));
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_USERNAME));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_USER_DESCRIPTION));
            @SuppressLint("Range") String flag = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_USER_COUNTRY_FLAG));
            @SuppressLint("Range") String profileImage = cursor.getString(cursor.getColumnIndex(DrawingContract.DrawingEntry.COLUMN_PROFILE_IMAGE));

            UserProfileFav userProfile = new UserProfileFav(flag, description, id, profileImage, userId, username);
            userProfiles.add(userProfile);
        }
        cursor.close();
        return userProfiles;
    }


    public long insertUserProfile(String userId, String username, String description, String profileImage, String CountryFlag) {

        if (isUserIdExists(userId)) {
            Toast.makeText(context, "Already added to Favorites!", Toast.LENGTH_SHORT).show();

            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(DrawingContract.DrawingEntry.COLUMN_USER_ID, userId);
        values.put(DrawingContract.DrawingEntry.COLUMN_USERNAME, username);
        values.put(DrawingContract.DrawingEntry.COLUMN_USER_DESCRIPTION, description);
        values.put(DrawingContract.DrawingEntry.COLUMN_PROFILE_IMAGE, profileImage);
        values.put(DrawingContract.DrawingEntry.COLUMN_USER_COUNTRY_FLAG, CountryFlag);

        Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();


        return db.insert(DrawingContract.DrawingEntry.TABLE_NAME_PROFILE, null, values);
    }

    public void deleteUserProfile(@NotNull String userID) {
        db.delete(DrawingContract.DrawingEntry.TABLE_NAME_PROFILE, DrawingContract.DrawingEntry.COLUMN_USER_ID + " = ?", new String[]{userID});

    }
}

