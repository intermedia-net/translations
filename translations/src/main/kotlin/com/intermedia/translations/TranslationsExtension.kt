package com.intermedia.translations

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

open class TranslationsExtension @Inject constructor(
    objects: ObjectFactory
) {
    val api = objects.newInstance(ApiFactoryExtension::class.java)

    lateinit var supportedLanguages: SupportedLanguages
    lateinit var notTranslatedFile: File
    var stringsFileName: String = DEFAULT_STRINGS_FILE_NAME
    lateinit var resourcesFolder: File

    fun api(action: Action<ApiFactoryExtension>) {
        action.execute(api)
    }

    companion object {
        private const val DEFAULT_STRINGS_FILE_NAME = "strings.xml"
    }
}
