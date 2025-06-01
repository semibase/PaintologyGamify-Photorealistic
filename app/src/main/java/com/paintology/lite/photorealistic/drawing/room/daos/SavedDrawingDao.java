package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity;

import java.util.List;

@Dao
public interface SavedDrawingDao {

    @Query("SELECT * FROM saveddrawingentity WHERE postId IN (:postIds) ORDER BY modifiedDate DESC")
    List<SavedDrawingEntity> getAllByIds(int[] postIds);

    @Query("SELECT * FROM saveddrawingentity ORDER BY modifiedDate DESC")
    List<SavedDrawingEntity> getAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SavedDrawingEntity... swatches);

    @Delete
    void delete(SavedDrawingEntity swatch);

    @Query("UPDATE saveddrawingentity SET localPath = :newPath WHERE localPath IN (:oldPath)")
    void updateLocalPathByOldPath(String newPath, String oldPath);

    @Query("DELETE FROM saveddrawingentity WHERE localPath IN (:path)")
    void deleteByPath(String path);
}
