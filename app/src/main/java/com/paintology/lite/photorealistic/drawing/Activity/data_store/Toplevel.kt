package com.example.claptofindphone.data.local.data_store


import android.content.Context
import com.example.claptofindphone.data.local.data_store.DataStores

class TopLevel(val application: Context) {

    private val dataStore = DataStores.getInstance()

    fun onCreate(): DataStores {
        return dataStore
    }

}