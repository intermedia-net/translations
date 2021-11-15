package com.intermedia.translations

data class SupportedLanguages(
    val required: List<String>,
    val optional: List<String> = emptyList()
)
