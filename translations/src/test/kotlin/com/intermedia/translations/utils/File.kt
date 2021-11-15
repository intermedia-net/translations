package com.intermedia.translations.utils

interface File {

    fun line(content: String = "", endWithNewLine: Boolean = true)

    fun save()
}
