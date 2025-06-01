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
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialcategory;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialimages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TutorialDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "DrawingFavouritesTutorial.db";
    private static final String TABLE_NAME = "tutorials";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORIES = "categories";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_IMAGES = "images";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TAGS = "tags";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_VISIBILITY = "visibility";
    private static final String CATE_NAME = "cate_name";

    public TutorialDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_CATEGORIES + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_CREATED_AT + " TEXT,"
                + COLUMN_IMAGES + " TEXT,"
                + COLUMN_LEVEL + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_TAGS + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_VISIBILITY + " TEXT,"
                + CATE_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTutorial(Tutorialdatum tutorial, String cate, Context context) {

        if (CheckTutIdExists(tutorial.getId())) {
            Toast.makeText(context, "Already added to Favorites!", Toast.LENGTH_SHORT).show();

            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Gson gson = new Gson();
        values.put(COLUMN_ID, tutorial.getId());
        values.put(COLUMN_CATEGORIES, gson.toJson(tutorial.getTutorialcategories()));
        values.put(COLUMN_CONTENT, tutorial.getContent());
        values.put(COLUMN_CREATED_AT, tutorial.getCreatedAt());
        values.put(COLUMN_IMAGES, gson.toJson(tutorial.getTutorialimages()));
        values.put(COLUMN_LEVEL, tutorial.getLevel());
        values.put(COLUMN_STATUS, tutorial.getStatus());
        values.put(COLUMN_TAGS, gson.toJson(tutorial.getTags()));
        values.put(COLUMN_TITLE, tutorial.getTitle());
        values.put(COLUMN_TYPE, tutorial.getType());
        values.put(COLUMN_VISIBILITY, tutorial.getVisibility());
        values.put(CATE_NAME, cate);

        db.insert(TABLE_NAME, null, values);
//        db.close();

        Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();

    }

    private boolean CheckTutIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{id});

        if (cursor.getCount() > 0) {
            db.close();
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public ArrayList<Tutorialdatum> getAllTutorials() {
        ArrayList<Tutorialdatum> tutorialList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Tutorialdatum tutorial = new Tutorialdatum();
                Gson gson = new Gson();

                tutorial.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                tutorial.setTutorialcategories(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORIES)), new TypeToken<List<Tutorialcategory>>() {
                }.getType()));
                tutorial.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                tutorial.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                tutorial.setTutorialimages(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES)), Tutorialimages.class));
                tutorial.setLevel(cursor.getString(cursor.getColumnIndex(COLUMN_LEVEL)));
                tutorial.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                tutorial.setTags(gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS)), new TypeToken<List<String>>() {
                }.getType()));
                tutorial.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                tutorial.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                tutorial.setVisibility(cursor.getString(cursor.getColumnIndex(COLUMN_VISIBILITY)));
                tutorial.setCateName(cursor.getString(cursor.getColumnIndex(CATE_NAME)));

                tutorialList.add(tutorial);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        if (!tutorialList.isEmpty()) {
            Collections.reverse(tutorialList);
        }
        return tutorialList;
    }


    public void RemoveTut(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id});

    }
}

