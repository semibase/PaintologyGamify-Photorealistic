package com.paintology.lite.photorealistic.drawing.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_publish")
data class PublishEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val type: String,
    var isUploadedGallery: Boolean = false,
    var isUploadedCommunity: Boolean = false
)