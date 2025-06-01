package com.paintology.lite.photorealistic.drawing.room.entities


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.Date


@Entity(tableName = "drawing_favorite")
@Parcelize
data class DrawingEntity(
    @PrimaryKey
    val id: String,
    val userName : String?,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val createdAt: Date?,
    val uid: String?,
    val tags: String?,
    val type: String?,
    val rating: Long?,
    val likes: String?,
    val comments: String?,
    val level : String?,
    val totalPoints : String?,
    val serverUserId : String?,
    val youtubeLink:String,
    val path : String?,
    val parentFolderPath : String?,
): Parcelable


