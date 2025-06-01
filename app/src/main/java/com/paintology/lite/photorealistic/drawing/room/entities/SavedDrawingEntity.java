package com.paintology.lite.photorealistic.drawing.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SavedDrawingEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "postId")
    public int postId;

    @ColumnInfo(name = "swatches")
    public String swatches;

    @ColumnInfo(name = "localPath")
    public String localPath;

    @ColumnInfo(name = "originPath")
    public String originPath;

    @ColumnInfo(name = "youtubeVideoId")
    public String youtubeVideoId;

    @ColumnInfo(name = "strokeCount")
    public int strokeCount;

    @ColumnInfo(name = "colorPalette")
    public String colorPalette;

    @ColumnInfo(name = "modifiedDate")
    public long modifiedDate;

}
