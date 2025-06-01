package com.core.ext

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return matches(emailRegex.toRegex())
}

fun String.isPasswordValid(minLength: Int = 6): Boolean {
    return length >= minLength
}