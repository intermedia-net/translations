package com.intermedia.translations.utils

interface Folder {

    fun folder(name: String, body: Folder.() -> Unit)

    fun file(name: String, body: File.() -> Unit)
}
