package com.intermedia.translations.lokalise

import com.google.gson.annotations.SerializedName

data class LanguageMapping(
    @SerializedName("original_language_iso")
    val originalLanguageIso: String,
    @SerializedName("custom_language_iso")
    val customLanguageIso: String
)
