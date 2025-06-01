package com.paintology.lite.photorealistic.drawing.Model.firebase

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch

data class FirebaseTutorial(
    @SerializedName("id") val id: String,
    @SerializedName("canvas_color") val canvasColor: String,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("categories_id") val categoriesId: List<String>,
    @SerializedName("color_swatch") val colorSwatch: List<String>,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("files") val files: Files,
    @SerializedName("images") val images: Images,
    @SerializedName("level") val level: String,
    @SerializedName("links") val links: Links,
    @SerializedName("options") val options: Options,
    @SerializedName("ref") val ref: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("source") val source: String,
    @SerializedName("status") val status: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String,
    @SerializedName("updated_at") val updatedAt: Timestamp,
    @SerializedName("visibility") val visibility: String,
) {
    fun getArrayColorSwatch(): ArrayList<ColorSwatch> {
        val arrayColorSwatch = ArrayList<ColorSwatch>()

        for (color in colorSwatch) {
            val colorSwatch = ColorSwatch()
            colorSwatch.color_swatch = color
            arrayColorSwatch.add(colorSwatch)
        }

        return arrayColorSwatch
    }

}

data class Category(
    @SerializedName("id") val id: String = "",
    @SerializedName("level") val level: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("parent_id") val parentId: String? = null,
    @SerializedName("sorting_number") val sortingNumber: Int = 0,
    @SerializedName("thumbnail") val thumbnail: String = ""
)

data class Files(
    @SerializedName("text_file_1") val textFile1: TextFile? = null,
    @SerializedName("text_file_2") val textFile2: TextFile? = null,
)

data class TextFile(
    @SerializedName("name") val name: String? = null,
    @SerializedName("url") val url: String? = null,
)

data class Images(
    @SerializedName("content") val content: String = "",
    @SerializedName("thumbnail") val thumbnail: String = ""
)

data class Links(
    @SerializedName("external") val external: Any? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("youtube") val youtube: String? = null,
)

data class Options(
    @SerializedName("brush") val brush: Brush,
    @SerializedName("single_tap") val singleTap: Boolean,
    @SerializedName("straight_lines") val straightLines: Boolean,
    @SerializedName("grayscale") val grayscale: Boolean,
    @SerializedName("block_coloring") val blockColoring: Boolean,
)

data class Brush(
    @SerializedName("color") val color: String,
    @SerializedName("density") val density: Int,
    @SerializedName("hardness") val hardness: Int,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Int,
    @SerializedName("type") val type: String,
)

data class Timestamp(
    @SerializedName("seconds") val seconds: Long,
    @SerializedName("nanoseconds") val nanoseconds: Int,
)

fun getBrushMode(name: String): Int {
    var mode = 0

    when (name) {
        "sticks" -> {
            mode = 528
        }

        "meadow" -> {
            mode = 656
        }

        "haze light" -> {
            mode = 640
        }

        "haze dark" -> {
            mode = 642
        }

        "line" -> {
            mode = 81
        }

        "mist" -> {
            mode = 784
        }

        "land patch" -> {
            mode = 608
        }

        "grass" -> {
            mode = 624
        }

        "industry" -> {
            mode = 768
        }

        "chalk" -> {
            mode = 512
        }

        "charcoal" -> {
            mode = 576
        }

        "flower" -> {
            mode = 592
        }

        "wave" -> {
            mode = 560
        }

        "eraser" -> {
            mode = 112
        }


        "shade" -> {
            mode = 80
        }


        "watercolor" -> {
            mode = 55
        }

        "sketch oval" -> {
            mode = 272
        }


        "sketch fill" -> {
            mode = 256
        }

        "sketch pen" -> {
            mode = 264
        }

        "sketch wire" -> {
            mode = 257
        }

        "emboss" -> {
            mode = 96
        }

        "rainbow" -> {
            mode = 39
        }

        "inkpen" -> {
            mode = 56
        }

        "fountain" -> {
            mode = 561
        }

        "lane" -> {
            mode = 559
        }

        "streak" -> {
            mode = 562
        }


        "foliage" -> {
            mode = 563
        }

        "felt" -> {
            mode = 45
        }

        "halo" -> {
            mode = 46
        }

        "outline" -> {
            mode = 47
        }

        "cube line" -> {
            mode = 54
        }

        "dash line" -> {
            mode = 48
        }
    }
    Log.e("brushsize mode", mode.toString() + "")

    return mode
}
