package com.intermedia.translations

import java.io.File
import java.nio.file.Path

interface Api {

    /**
     * Return the directory with translations folders:
     * values
     * values-nl
     * values-fr
     * ...
     * etc
     */
    @Throws(TranslationsException::class)
    fun fetchTranslations(): Path

    class FolderApi(
        private val folder: File
    ) : Api {
        override fun fetchTranslations(): Path {
            return folder.toPath()
        }
    }
}
