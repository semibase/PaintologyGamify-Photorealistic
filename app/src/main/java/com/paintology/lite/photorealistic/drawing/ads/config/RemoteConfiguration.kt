package com.paintology.lite.photorealistic.drawing.ads.config

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.ads.im.InternetManager
import com.paintology.lite.photorealistic.drawing.ads.pref.SharedPreferenceUtils

class RemoteConfiguration(
    private val internetManager: InternetManager,
    private val sharedPreferenceUtils: SharedPreferenceUtils,
) {

    private val configTag = "REMOTE_CONFIG"
    private val remoteConfig = Firebase.remoteConfig

    fun checkRemoteConfig(callback: (fetchSuccessfully: Boolean) -> Unit) {
        if (internetManager.isInternetConnected) {
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 2 }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults_ads)
            fetchRemoteValues(callback)
        } else {
            callback.invoke(false)
        }
    }

    private fun fetchRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    updateRemoteValues(callback)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                    callback.invoke(false)
                }
            } else {
                Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                callback.invoke(false)
            }
        }
    }

    @Throws(Exception::class)
    private fun updateRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        sharedPreferenceUtils.apply {
            rcvInterSplash = remoteConfig[INTER_SPLASH_KEY].asLong().toInt()
            rcvOpenAd = remoteConfig[openApp].asLong().toInt()
            rcvBannerDashBoard = remoteConfig[BANNER_DASHBOARD_KEY].asLong().toInt()
            rcvNativeCommunity = remoteConfig[NATIVE_COMMUNITY_KEY].asLong().toInt()
            rcvInterRewardNotification = remoteConfig[interRewardNotification].asLong().toInt()
            rcvOpenAdID = remoteConfig[openAppIDs].asString()
            rcvAdmobAppID = remoteConfig[admobAppID].asString()
            rcvNativeID = remoteConfig[nativeIDs].asString()
            rcvBannerID = remoteConfig[bannerIDs].asString()
            rcvInterID = remoteConfig[interIDs].asString()
            rcvInterRewardID = remoteConfig[interRewardIDs].asString()
            interProgressActivityRewardID = remoteConfig[interRewardIDs].asString()
            interUserPointActivityRewardID = remoteConfig[interRewardIDs].asString()
            isFourStep = remoteConfig[fourStep].asBoolean()

            Log.d(
                configTag,
                "checkRemoteConfig: Fetched Successfully "+ rcvInterID
            )
        }
        callback.invoke(true)
    }
}