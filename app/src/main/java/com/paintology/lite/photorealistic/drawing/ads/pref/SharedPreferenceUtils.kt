package com.paintology.lite.photorealistic.drawing.ads.pref

import android.content.SharedPreferences

class SharedPreferenceUtils(private val sharedPreferences: SharedPreferences) {

    private val billingRequireKey = "isAppPurchased"
    internal val INTER_SPLASH_KEY = "interOnOff_pr"
    internal val BANNER_DASHBOARD_KEY = "bannerOnOff_pr"
    internal val NATIVE_COMMUNITY_KEY = "nativeOnOff_pr"
    internal val interRewardNotification = "interRewardOnOff_pr"
    internal val openApp = "openAdOnOff_pr"
    internal val interIDs = "interIDs_pr"
    internal val admobAppID = "admobAppID_pr"
    internal val interRewardIDs = "interRewardIDs_pr"
    internal val interProgressActivityRewardIDs = "interProgressActivityRewardIDs_pr"
    internal val interUserPointActivityRewardIDs = "interUserPointActivityRewardIDs_pr"
    internal val nativeIDs = "nativeIDs_pr"
    internal val bannerIDs = "bannerIDs_pr"
    internal val openAppIDs = "openAdID_pr"
    internal val fourStep = "fourStep"
    internal val fourStepCompleted = "fourStepCompleted"

    var isAppPurchased: Boolean
        get() = sharedPreferences.getBoolean(billingRequireKey, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(billingRequireKey, value)
                apply()
            }
        }

    var isFourStep: Boolean
        get() = sharedPreferences.getBoolean(fourStep, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(fourStep, value)
                apply()
            }
        }

    var isFourStepCompleted: Boolean
        get() = sharedPreferences.getBoolean(fourStepCompleted, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(fourStepCompleted, value)
                apply()
            }
        }



    var rcvInterSplash: Int
        get() = sharedPreferences.getInt(INTER_SPLASH_KEY, 1)
        set(value) = sharedPreferences.edit().putInt(INTER_SPLASH_KEY, value).apply()

    var rcvOpenAd: Int
        get() = sharedPreferences.getInt(openApp, 1)
        set(value) = sharedPreferences.edit().putInt(openApp, value).apply()

    var rcvBannerDashBoard: Int
        get() = sharedPreferences.getInt(BANNER_DASHBOARD_KEY, 1)
        set(value) = sharedPreferences.edit().putInt(BANNER_DASHBOARD_KEY, value).apply()

    var rcvNativeCommunity: Int
        get() = sharedPreferences.getInt(NATIVE_COMMUNITY_KEY, 1)
        set(value) = sharedPreferences.edit().putInt(NATIVE_COMMUNITY_KEY, value).apply()

    var rcvInterRewardNotification: Int
        get() = sharedPreferences.getInt(interRewardNotification, 1)
        set(value) = sharedPreferences.edit().putInt(interRewardNotification, value).apply()

    var rcvAdmobAppID: String
        get() = sharedPreferences.getString(admobAppID, "ca-app-pub-3940256099942544~3347511713") ?: "ca-app-pub-3940256099942544~3347511713"
        set(value) {
            sharedPreferences.edit().apply {
                putString(admobAppID, value)
                apply()
            }
        }

    var rcvOpenAdID: String
        get() = sharedPreferences.getString(openAppIDs, "ca-app-pub-3940256099942544/9257395921") ?: "ca-app-pub-3940256099942544/9257395921"
        set(value) {
            sharedPreferences.edit().apply {
                putString(openAppIDs, value)
                apply()
            }
        }
    var rcvInterID: String
        get() = sharedPreferences.getString(interIDs, "ca-app-pub-3940256099942544/1033173712") ?: "ca-app-pub-3940256099942544/1033173712"
        set(value) {
            sharedPreferences.edit().apply {
                putString(interIDs, value)
                apply()
            }
        }

    var rcvInterRewardID: String
        get() = sharedPreferences.getString(interRewardIDs, "ca-app-pub-3940256099942544/5354046379") ?: "ca-app-pub-3940256099942544/5354046379"
        set(value) {
            sharedPreferences.edit().apply {
                putString(interRewardIDs, value)
                apply()
            }
        }

    var interProgressActivityRewardID: String
        get() = sharedPreferences.getString(interProgressActivityRewardIDs, "ca-app-pub-3940256099942544/5354046379") ?: "ca-app-pub-3940256099942544/5354046379"
        set(value) {
            sharedPreferences.edit().apply {
                putString(interUserPointActivityRewardIDs, value)
                apply()
            }
        }

    var interUserPointActivityRewardID: String
        get() = sharedPreferences.getString(interUserPointActivityRewardIDs, "ca-app-pub-3940256099942544/5354046379") ?: "ca-app-pub-3940256099942544/5354046379"
        set(value) {
            sharedPreferences.edit().apply {
                putString(interUserPointActivityRewardIDs, value)
                apply()
            }
        }

    var rcvNativeID: String
        get() = sharedPreferences.getString(nativeIDs, "ca-app-pub-3940256099942544/2247696110") ?: "ca-app-pub-3940256099942544/2247696110"
        set(value) {
            sharedPreferences.edit().apply {
                putString(nativeIDs, value)
                apply()
            }
        }

    var rcvBannerID: String
        get() = sharedPreferences.getString(bannerIDs, "ca-app-pub-3940256099942544/2014213617") ?: "ca-app-pub-3940256099942544/2014213617"
        set(value) {
            sharedPreferences.edit().apply {
                putString(bannerIDs, value)
                apply()
            }
        }
}