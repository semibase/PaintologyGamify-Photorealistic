package com.paintology.lite.photorealistic.drawing.challenge.model

data class Question(
    var answers: MutableList<String> = mutableListOf(),
    var correct_answer: String = "",
    var explanation: String = "",
    var key: String = "",
    var question: String = ""
)
