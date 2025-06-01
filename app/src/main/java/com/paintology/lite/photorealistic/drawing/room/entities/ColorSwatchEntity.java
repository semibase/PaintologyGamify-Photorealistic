package com.paintology.lite.photorealistic.drawing.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ColorSwatchEntity {

//    @PrimaryKey(autoGenerate = true)
//    public int id;

    @PrimaryKey
    @ColumnInfo(name = "postId")
    public int postId;

    @ColumnInfo(name = "swatches")
    public String swatches;

}
