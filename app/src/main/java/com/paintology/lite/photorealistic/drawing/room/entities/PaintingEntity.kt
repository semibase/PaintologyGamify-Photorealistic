package com.paintology.lite.photorealistic.drawing.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paintings")
data class PaintingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val type: String,
    var isUploaded: Boolean = false
)
