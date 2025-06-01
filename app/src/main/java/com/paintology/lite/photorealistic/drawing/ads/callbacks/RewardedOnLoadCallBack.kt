package com.paintology.lite.photorealistic.drawing.ads.callbacks

interface RewardedOnLoadCallBack {
    fun onAdFailedToLoad(adError:String)
    fun onAdLoaded()
    fun onPreloaded()
}