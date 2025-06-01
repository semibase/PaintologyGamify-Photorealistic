package com.paintology.lite.photorealistic.drawing.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.paintology.lite.photorealistic.drawing.room.entities.PublishEntity;

@Dao
public interface PublishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPainting(PublishEntity painting);

    @Update
    void updatePainting(PublishEntity painting);

    @Query("SELECT * FROM tb_publish WHERE fileName = :fileName")
    PublishEntity getPaintingByFileName(String fileName);

    @Query("SELECT * FROM tb_publish ORDER BY id DESC LIMIT 1")
    PublishEntity getLatestPainting();
}
