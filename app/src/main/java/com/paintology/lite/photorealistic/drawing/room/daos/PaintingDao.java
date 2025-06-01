package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.paintology.lite.photorealistic.drawing.room.entities.PaintingEntity;

import java.util.List;

@Dao
public interface PaintingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPainting(PaintingEntity painting);

    @Update
    void updatePainting(PaintingEntity painting);

    @Query("SELECT * FROM paintings WHERE fileName = :fileName")
    PaintingEntity getPaintingByFileName(String fileName);

    @Query("SELECT * FROM paintings ORDER BY id DESC LIMIT 1")
    PaintingEntity getLatestPainting();

    @Query("SELECT * FROM paintings WHERE isUploaded = :isUploaded")
    List<PaintingEntity> getPaintingsByUploadStatus(boolean isUploaded);
}
