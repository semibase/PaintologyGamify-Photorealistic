package com.core.ext

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> parseData(data: Any?): T? {
    val type = object : TypeToken<T?>() {}.type
    return try {
        Gson().fromJson(Gson().toJson(data), type)
    } catch (e: Exception) {
        Log.e("ParseData", e.message ?: "")
        null
    }
}
