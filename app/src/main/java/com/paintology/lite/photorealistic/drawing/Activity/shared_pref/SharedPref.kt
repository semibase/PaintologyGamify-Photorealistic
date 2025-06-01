package com.paintology.lite.photorealistic.drawing.Activity.shared_pref

import android.content.Context
import android.content.SharedPreferences

class SharedPref(var context: Context) {

    var PRIVATE_MODE = 0

    fun getBoolean(id: String?, value: Boolean): Boolean? {
        return pref?.getBoolean(id, false)
    }

    fun putBoolean(id: String?, value: Boolean) {
        editor?.putBoolean(id, value)
        editor?.commit()
    }

    fun getString(id: String?, value: String?): String? {
        return pref?.getString(id, value)
    }

    fun putString(id: String?, value: String) {
        editor?.putString(id, value)
        editor?.commit()
    }

    fun getInputPosition(name: String): Int? {
        return pref?.getInt(name, 0)
    }

    fun setInputPosition(name: String, position: Int) {
        editor?.putInt(name, position)
        editor?.commit()
    }

    //Long

    fun getLongValue(name: String): Long? {
        return pref?.getLong(name, 0)
    }

    fun setLongValue(name: String, position: Long) {
        editor?.putLong(name, position)
        editor?.commit()
    }

    fun clearAll() {
        editor?.clear()
        editor?.commit()
    }

    companion object {
        private const val PREF_NAME = "PiantologyGamify"
        var editor: SharedPreferences.Editor? = null
        var pref: SharedPreferences? = null

    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref?.edit()
    }
}