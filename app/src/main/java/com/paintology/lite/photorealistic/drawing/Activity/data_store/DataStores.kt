package com.example.claptofindphone.data.local.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.claptofindphone.data.local.data_store.DataStores.GamifyApp.isNeedToShowIntroScreen
import com.example.claptofindphone.data.local.data_store.DataStores.GamifyApp.languageFlag
import com.example.claptofindphone.data.local.data_store.DataStores.GamifyApp.languageName
import com.example.claptofindphone.data.local.data_store.DataStores.GamifyApp.languagePosition
import com.example.claptofindphone.data.local.data_store.DataStores.GamifyApp.selectedFilterItem
import com.paintology.lite.photorealistic.drawing.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DataStores {

    companion object {
        private var instance: DataStores? = null

        fun getInstance(): DataStores {
            return instance ?: synchronized(this) {
                instance ?: DataStores().also { instance = it }
            }
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myApp")

    private object GamifyApp {
        val languagePosition = intPreferencesKey("position")
        val languageName = stringPreferencesKey("name")
        val languageFlag = intPreferencesKey("flag")
        val selectedFilterItem = stringPreferencesKey("selectedFilterItem")
        val isNeedToShowIntroScreen = booleanPreferencesKey("isNeedToShowIntroScreen")
    }

    fun clearDataStore(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    fun readFilterItem(context: Context): Flow<String> {
        return context.dataStore.data.map {
            it[selectedFilterItem] ?: context.getResources().getString(R.string.date_descending)
        }
    }

    fun writeFilterItem(context: Context, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[selectedFilterItem] = value
            }
        }
    }
  fun readFlagId(context: Context): Flow<Int> {
        return context.dataStore.data.map {
            it[languageFlag] ?: R.drawable.img_world
        }
    }

    fun writeFlagId(context: Context, value: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[languageFlag] = value
            }
        }
    }


    fun readLangName(context: Context):Flow<String>{
        return context.dataStore.data.map {
            it[languageName] ?: "World"
        }
    }

    fun writeLangName(context: Context,value: String){
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[languageName] = value
            }
        }
    }

    fun readPosition(context: Context): Flow<Int> {
        return context.dataStore.data.map {
            it[languagePosition] ?: 0
        }
    }

    fun writePosition(context: Context, value: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[languagePosition] = value
            }
        }
    }

    fun readIntroScreenFlag(context: Context): Flow<Boolean> {
        return context.dataStore.data.map {
            it[isNeedToShowIntroScreen] ?: false
        }
    }

    fun writeIntroScreenFlag(context: Context, isNeedToShowIntro: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[isNeedToShowIntroScreen] = isNeedToShowIntro
            }
        }
    }

}