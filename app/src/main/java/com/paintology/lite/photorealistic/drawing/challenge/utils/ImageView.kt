package com.paintology.lite.photorealistic.drawing.challenge.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.paintology.lite.photorealistic.drawing.R

fun ImageView.loadImage(@DrawableRes resId:Int?){
    resId?.let {
        Glide.with(this.context).load(it)
            .into(this@loadImage)
    }

}

fun ImageView.loadImage(uri:String?){
    uri?.let {
        Glide.with(this@loadImage)
            .load(it)
            .error(R.drawable.dummy_challenge_img)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this@loadImage)
    }

}

fun ImageView.loadImageProfile(uri:String?,resPlaceHolder:Int = R.drawable.ic_user_dummy_profile){
    uri?.let {
        Glide.with(this@loadImageProfile)
            .load(it)
            .placeholder(resPlaceHolder)
            .error(resPlaceHolder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this@loadImageProfile)
    }

}