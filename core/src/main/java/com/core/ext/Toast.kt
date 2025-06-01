package com.core.ext

import android.content.Context
import android.widget.Toast

fun Context.showLongToast(message:String){
    Toast.makeText(this@showLongToast,message,Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(message:String){
    Toast.makeText(this@showShortToast,message,Toast.LENGTH_SHORT).show()
}