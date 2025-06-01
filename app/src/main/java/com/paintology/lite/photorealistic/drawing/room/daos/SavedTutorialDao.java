package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.paintology.lite.photorealistic.drawing.room.entities.SavedTutorialEntity;

import java.util.List;

@Dao
public interface SavedTutorialDao {

    @Query("SELECT * FROM savedtutorialentity")
    List<SavedTutorialEntity> getAll();

    @Query("SELECT * FROM savedtutorialentity WHERE postId IN (:postIds)")
    List<SavedTutorialEntity> getAllByIds(int[] postIds);

//    @Query("SELECT * FROM savedtutorialentity WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    SavedTutorialEntity findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        // or OnConflictStrategy.IGNORE
    void insertAll(SavedTutorialEntity... swatches);

    @Delete
    void delete(SavedTutorialEntity swatch);

    @Query("UPDATE savedtutorialentity SET localPath = :path WHERE postId IN (:postId)")
    void updateLocalPath(String path, int postId);

    @Query("SELECT * FROM savedtutorialentity WHERE postId IN (:postId)")
    List<SavedTutorialEntity> getSwatchById(int postId);

    @Query("SELECT * FROM savedtutorialentity WHERE localPath LIKE :path")
    List<SavedTutorialEntity> getSwatchByPath(String path);

    @Query("UPDATE savedtutorialentity SET localPath = :newPath WHERE localPath IN (:oldPath)")
    void updateLocalPathByOldPath(String newPath, String oldPath);

    @Query("DELETE FROM savedtutorialentity WHERE localPath IN (:path)")
    void deleteByPath(String path);
}
