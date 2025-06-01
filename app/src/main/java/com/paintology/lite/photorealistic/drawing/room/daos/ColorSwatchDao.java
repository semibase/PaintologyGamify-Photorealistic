package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity;

import java.util.List;

@Dao
public interface ColorSwatchDao {

    @Query("SELECT * FROM colorswatchentity")
    List<ColorSwatchEntity> getAll();

    @Query("SELECT * FROM colorswatchentity WHERE postId IN (:postIds)")
    List<ColorSwatchEntity> getAllByIds(int[] postIds);

//    @Query("SELECT * FROM colorswatchentity WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    ColorSwatchEntity findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        // or OnConflictStrategy.IGNORE
    void insertAll(ColorSwatchEntity... swatches);

    @Delete
    void delete(ColorSwatchEntity swatch);

    @Query("SELECT * FROM colorswatchentity WHERE postId IN (:postId)")
    List<ColorSwatchEntity> getSwatchById(int postId);

}
