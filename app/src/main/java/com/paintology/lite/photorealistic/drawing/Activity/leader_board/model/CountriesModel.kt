package com.paintology.lite.photorealistic.drawing.Activity.leader_board.model

import java.util.Locale

data class CountriesModel(
    val flag: Int?,
    val countryName: String?,
    val countryContent: String?,
    val countryCode: String? = Locale.getISOCountries().find {
        Locale(Locale.ENGLISH.country, it).displayCountry.equals(countryName, ignoreCase = true)
    },
    val users: Long? = 0
)
