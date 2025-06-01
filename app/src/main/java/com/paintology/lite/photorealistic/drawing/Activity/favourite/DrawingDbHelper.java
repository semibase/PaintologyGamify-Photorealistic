package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DrawingDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DrawingFavourites.db";
    private static final int DATABASE_VERSION = 2;

    public DrawingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DRAWINGS_TABLE = "CREATE TABLE " + DrawingContract.DrawingEntry.TABLE_NAME + " (" +
                DrawingContract.DrawingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DrawingContract.DrawingEntry.COLUMN_ID + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_TITLE + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_DESCRIPTION + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_CREATED_AT + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_TYPE + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_REFERENCE_ID + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_TAGS + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_IMAGES_CONTENT + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_METADATA_PATH + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_METADATA_PARENT_FOLDER_PATH + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_METADATA_TUTORIAL_ID + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_COMMENTS + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_LIKES + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_RATINGS + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_REVIEWS_COUNT + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_SHARES + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_STATISTICS_VIEWS + " INTEGER, " +
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_USER_ID + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_NAME + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_AVATAR + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_COUNTRY + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_AUTHOR_LEVEL + " TEXT, " +
                DrawingContract.DrawingEntry.COLUMN_LINKS_YOUTUBE + " TEXT);";


        String SQL_CREATE_USER_PROFILE_TABLE = "CREATE TABLE " + DrawingContract.DrawingEntry.TABLE_NAME_PROFILE + " (" +
                DrawingContract.DrawingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DrawingContract.DrawingEntry.COLUMN_USER_ID + " TEXT," +
                DrawingContract.DrawingEntry.COLUMN_USERNAME + " TEXT," +
                DrawingContract.DrawingEntry.COLUMN_USER_DESCRIPTION + " TEXT," +
                DrawingContract.DrawingEntry.COLUMN_USER_COUNTRY_FLAG + " TEXT," +
                DrawingContract.DrawingEntry.COLUMN_PROFILE_IMAGE + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_DRAWINGS_TABLE);
        db.execSQL(SQL_CREATE_USER_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DrawingContract.DrawingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DrawingContract.DrawingEntry.TABLE_NAME_PROFILE);

        onCreate(db);
    }
}
