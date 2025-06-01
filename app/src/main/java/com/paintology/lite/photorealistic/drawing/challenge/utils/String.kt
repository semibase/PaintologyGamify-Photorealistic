package com.paintology.lite.photorealistic.drawing.challenge.utils

import android.net.Uri
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode

private const val LEVEL_1 = "Beginner 1"
private const val LEVEL_2 = "Beginner 2"
private const val LEVEL_3 = "Beginner 3"
private const val LEVEL_4 = "Intermediate 1"
private const val LEVEL_5 = "Intermediate 2"
private const val LEVEL_6 = "Intermediate 3"
private const val LEVEL_7 = "Advanced 1"
private const val LEVEL_8 = "Advanced 2"
private const val LEVEL_9 = "Advanced 3"
private const val LEVEL_10 = "Expert"

private const val RESOURCE = "Resources"
private const val TUTORIALS = "Tutorials"
private const val QUIZ = "Quiz"


private const val Pencil_Drawing = "Pencil Drawing"
private const val Connect_The_Dots = "Connect the Dots"
private const val Paint_By_Numbers = "Paint by Numbers"
private const val Trace_Drawing = "Trace Drawing"

private const val EASY = "Easy"
private const val VIDEO = "Video"
private const val BLOG = "Blog"

const val APP_SCREEN ="app_screen"

fun String.getDifficultyType(): Int {
    return when (this@getDifficultyType) {
        LEVEL_1 -> R.drawable.img_challenge_beginner_1
        LEVEL_2 -> R.drawable.img_challenge_beginner_2
        LEVEL_3 -> R.drawable.img_challenge_beginner_3
        LEVEL_4 -> R.drawable.img_challenge_intermediate_1
        LEVEL_5 -> R.drawable.img_challenge_intermediate_2
        LEVEL_6 -> R.drawable.img_challenge_intermediate_3
        LEVEL_7 -> R.drawable.img_challenge_advanced_1
        LEVEL_8 -> R.drawable.img_challenge_advanced_2
        LEVEL_9 -> R.drawable.img_challenge_advanced_3
        LEVEL_10 -> R.drawable.img_challenge_expert
        else -> R.drawable.img_challenge_beginner_1
    }
}


fun String?.isResource(): Boolean {
    return this.equals(RESOURCE, true)
}

fun String?.isQuiz(): Boolean {
    return this.equals(QUIZ, true)
}

fun String?.isTutorials(): Boolean {
    return this.equals(TUTORIALS, true)
}

fun TutorialChallengeMode.getChallengeTitle(): String {
    return if (type.isQuiz()) {
        "Quiz ${custom_fields?.quiz_type}"
    } else if (type.isResource()) {
        custom_fields?.links?.firstOrNull()?.title ?: ""
    } else if (type.isTutorials()) {
        custom_fields?.category?.name ?: ""
    } else {
        ""
    }
}

fun TutorialChallengeMode.getCategoryIcon(): Int {
    return if (type.isTutorials()) {
        when (custom_fields?.category?.name) {
            Pencil_Drawing -> R.drawable.img_challenge_pencil
            Connect_The_Dots -> R.drawable.img_challenge_connect
            Paint_By_Numbers -> R.drawable.img_challenge_pbyno
            Trace_Drawing -> R.drawable.img_challenge_trace
            else -> {
                -1
            }
        }
    } else if (type.isResource()) {
        R.drawable.img_challenge_resource
    } else if (type.isQuiz()) {
        when (custom_fields?.quiz_type) {
            EASY -> R.drawable.img_challenge_quiz_easy
            VIDEO -> R.drawable.img_challenge_quiz_video
            BLOG -> R.drawable.img_challenge_quiz_blog
            else -> -1
        }
    } else {
        -1
    }
}



fun String.extractVideoIdFromUrl(): String? {
    if (this.startsWith("https://youtu.be/")) {
        return this.substringAfterLast("/")
    } else {
        val uri = Uri.parse(this)
        return uri.getQueryParameter("v")
    }
}