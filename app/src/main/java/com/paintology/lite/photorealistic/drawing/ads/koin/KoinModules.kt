package com.paintology.lite.photorealistic.drawing.ads.koin

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.paintology.lite.photorealistic.drawing.ads.AdmobAppOpen
import com.paintology.lite.photorealistic.drawing.ads.AdmobBannerAds
import com.paintology.lite.photorealistic.drawing.ads.AdmobInterstitialAds
import com.paintology.lite.photorealistic.drawing.ads.AdmobNativeAds
import com.paintology.lite.photorealistic.drawing.ads.AdmobRewardedInterstitialAds
import com.paintology.lite.photorealistic.drawing.ads.im.InternetManager
import com.paintology.lite.photorealistic.drawing.ads.config.RemoteConfiguration
import com.paintology.lite.photorealistic.drawing.ads.pref.SharedPreferenceUtils
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val managerModules = module {
    single { InternetManager(androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) }
}

private val utilsModules = module {
    single {
        SharedPreferenceUtils(
            androidContext().getSharedPreferences(
                "app_preferences",
                Application.MODE_PRIVATE
            )
        )
    }
}

private val firebaseModule = module {
    single { RemoteConfiguration(get(), get()) }
}

private val adsModule = module {
    single { AdmobAppOpen(get()) }
    single { AdmobInterstitialAds() }
    single { AdmobRewardedInterstitialAds() }
//    single { AdmobPreLoadNativeAds() }
    factory { AdmobNativeAds() }
    factory { AdmobBannerAds() }
}

val modulesList = listOf(utilsModules, managerModules, firebaseModule, adsModule)