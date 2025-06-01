package com.paintology.lite.photorealistic.drawing.ads.callbacks

interface InterstitialOnLoadCallBack {
    fun onAdFailedToLoad(adError: String)
    fun onAdLoaded()
    fun onPreloaded()
}