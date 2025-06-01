package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.paintology.lite.photorealistic.drawing.room.entities.DrawingEntity;

import java.util.List;

@Dao
public interface FavDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    void insert(DrawingEntity drawingEntity);

    @Update
    void update(DrawingEntity drawingEntity);

    @Delete
    void delete(DrawingEntity drawingEntity);

    @Query("SELECT * FROM drawing_favorite")
    LiveData<List<DrawingEntity>> getFavourites();

    @Query("SELECT id FROM drawing_favorite")
    List<String> getFavouritesIds();
}