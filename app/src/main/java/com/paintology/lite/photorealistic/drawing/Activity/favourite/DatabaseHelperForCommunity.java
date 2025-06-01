package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelperForCommunity extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "DrawingFavouritesCommunity.db";
    private static final String TABLE_NAME = "community_posts";

    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DRAWING_ID = "drawing_id";
    private static final String COLUMN_STATISTIC = "statistic";
    private static final String COLUMN_IMAGES = "images";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LAST_COMMENTS = "last_comments";
    private static final String COLUMN_LINKS = "links";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LEGACY_DATA = "legacy_data";
    private static final String COLUMN_TAGS = "tags";
    private static final String COLUMN_IS_DOWNLOADED = "is_downloaded";
    private static final String COLUMN_IS_LIKED = "is_liked";

    public DatabaseHelperForCommunity(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_POST_ID + " TEXT,"
                + COLUMN_ID + " TEXT,"
                + COLUMN_DRAWING_ID + " TEXT,"
                + COLUMN_STATISTIC + " TEXT,"
                + COLUMN_IMAGES + " TEXT,"
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_AUTHOR + " TEXT,"
                + COLUMN_CREATED_AT + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_LAST_COMMENTS + " TEXT,"
                + COLUMN_LINKS + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_LEGACY_DATA + " TEXT,"
                + COLUMN_TAGS + " TEXT,"
                + COLUMN_IS_DOWNLOADED + " INTEGER,"
                + COLUMN_IS_LIKED + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private boolean CheckIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_POST_ID + " = ?", new String[]{id});

        if (cursor.getCount() > 0) {
            db.close();
            return true;
        } else {
            return false;
        }
    }

    public void addCommunityPost(CommunityPost post, Context context) {

        if (CheckIdExists(post.getPost_id())) {
            Toast.makeText(context, "Already added to Favorites!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Gson gson = new Gson();
        values.put(COLUMN_POST_ID, post.getPost_id());
        values.put(COLUMN_ID, post.getId());
        values.put(COLUMN_DRAWING_ID, post.getDrawingId());
        values.put(COLUMN_STATISTIC, gson.toJson(post.getStatistic()));
        values.put(COLUMN_IMAGES, gson.toJson(post.getImages()));
        values.put(COLUMN_USER_ID, post.getUser_id());
        values.put(COLUMN_AUTHOR, gson.toJson(post.getAuthor()));
        values.put(COLUMN_CREATED_AT, post.getCreated_at().toString());
        values.put(COLUMN_DESCRIPTION, post.getDescription());
        values.put(COLUMN_LAST_COMMENTS, gson.toJson(post.getLastComments()));
        values.put(COLUMN_LINKS, gson.toJson(post.getLinks()));
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_LEGACY_DATA, gson.toJson(post.getLegacy_data()));
        values.put(COLUMN_TAGS, gson.toJson(post.getTags()));
        values.put(COLUMN_IS_DOWNLOADED, post.isDownloaded ? 1 : 0);
        values.put(COLUMN_IS_LIKED, post.isLiked ? 1 : 0);

        db.insert(TABLE_NAME, null, values);
        db.close();
        Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();
    }

    public boolean checkCommunityPost(CommunityPost object) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_POST_ID + " = '" + object.getPost_id() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean ischeck = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ischeck;
    }


    @SuppressLint("Range")
    public List<CommunityPost> getAllCommunityPosts() {
        List<CommunityPost> postList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CommunityPost post = new CommunityPost();
                Gson gson = new Gson();

                post.setPost_id(cursor.getString(cursor.getColumnIndex(COLUMN_POST_ID)));
                post.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                post.setDrawingId(cursor.getString(cursor.getColumnIndex(COLUMN_DRAWING_ID)));

                post.setStatistic(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_STATISTIC)), CommunityPost.Statistic.class));
                post.setImages(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES)), CommunityPost.Images.class));
                post.setUser_id(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                post.setAuthor(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)), CommunityPost.Author.class));
                post.setCreated_at(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                post.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                post.setLastComments(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_COMMENTS)), new TypeToken<List<CommunityPost.Comment>>() {
                }.getType()));
                post.setLinks(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_LINKS)), CommunityPost.Links.class));
                post.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                post.setLegacy_data(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_LEGACY_DATA)), CommunityPost.LegacyData.class));
                post.setTags(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS)), new TypeToken<List<String>>() {
                }.getType()));
                post.setDownloaded(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DOWNLOADED)) == 1);
                post.setLiked(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_LIKED)) == 1);

                postList.add(post);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        if (!postList.isEmpty()) {
            Collections.reverse(postList);
        }
        return postList;
    }

    public void removeCommunityPost(CommunityPost object) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, COLUMN_POST_ID + " = ?", new String[]{object.getPost_id()});


    }
}

