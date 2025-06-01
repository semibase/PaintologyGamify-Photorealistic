package com.paintology.lite.photorealistic.drawing.challenge.utils

fun Int.toFormattedNumber(): String {
    return when {
        this < 1000 -> this.toString()
        this < 1000000 -> (this / 1000).toString() + " K"
        else -> String.format("%.1fM", this / 1000000)
    }

}