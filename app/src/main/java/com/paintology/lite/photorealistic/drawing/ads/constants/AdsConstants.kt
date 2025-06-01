package com.paintology.lite.photorealistic.drawing.ads.constants

import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

object AdsConstants {
    var mAppOpenAd: AppOpenAd? = null
    var mAppOpenAdSplash: AppOpenAd? = null
    var rewardedAd: RewardedInterstitialAd? = null
    var mInterstitialAd: InterstitialAd? = null
    var adMobPreloadNativeAd: NativeAd? = null
    var adMobPreloadNativeAdHome: NativeAd? = null
    var adMobPreloadNativeAdFullScreen: NativeAd? = null

    var isRewardedLoading = false
    var isOpenAdLoading = false
    var isNativeLoading = false
    var isNativeLoadingHome = false
    var isNativeLoadingFullScreen = false
    var isInterstitialLoading = false
    var isCollapsibleOpen = false

    fun reset() {
        mAppOpenAd = null
        mAppOpenAdSplash = null
        rewardedAd = null
        mInterstitialAd = null
        adMobPreloadNativeAd?.destroy()
        adMobPreloadNativeAd = null
        adMobPreloadNativeAdHome?.destroy()
        adMobPreloadNativeAdHome = null
        adMobPreloadNativeAdFullScreen?.destroy()
        adMobPreloadNativeAdFullScreen = null

        isRewardedLoading = false
        isOpenAdLoading = false
        isCollapsibleOpen = false
        isInterstitialLoading = false
        isNativeLoading = false
        isNativeLoadingHome = false
        isNativeLoadingFullScreen = false
    }
}