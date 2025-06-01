package com.core.ext

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText

class NoSpaceInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // Disallow space character
        if (source?.contains(" ") == true) {
            return ""
        }
        return null
    }
}

fun EditText.setNoSpaceInputFilter() {
    val filters = this.filters.toMutableList()
    filters.add(NoSpaceInputFilter())
    this.filters = filters.toTypedArray()
}
