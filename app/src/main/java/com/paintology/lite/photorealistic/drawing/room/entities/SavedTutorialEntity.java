package com.paintology.lite.photorealistic.drawing.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SavedTutorialEntity {

    @PrimaryKey
    @ColumnInfo(name = "postId")
    public int postId;

    @ColumnInfo(name = "swatches")
    public String swatches;

    @ColumnInfo(name = "localPath")
    public String localPath;

    @ColumnInfo(name = "colorPalette")
    public String colorPalette;
}
