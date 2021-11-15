package com.intermedia.translations

import com.google.gson.Gson
import com.intermedia.translations.lokalise.LanguageMapping
import com.intermedia.translations.lokalise.LokaliseApi
import com.intermedia.translations.lokalise.LokaliseInterceptor
import okhttp3.OkHttpClient
import org.gradle.api.Action
import org.gradle.api.logging.Logger
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

open class ApiFactoryExtension @Inject constructor(
    objects: ObjectFactory
) {
    val lokalise = objects.newInstance(LokaliseExtension::class.java)
    val folder = objects.newInstance(FolderExtension::class.java)

    fun lokalise(action: Action<LokaliseExtension>) {
        action.execute(lokalise)
    }

    fun folder(action: Action<FolderExtension>) {
        action.execute(folder)
    }

    fun build(logger: Logger): Api {
        return if (lokalise.isInitialized()) {
            LokaliseApi(
                lokalise.projectId,
                lokalise.languageMappings,
                OkHttpClient.Builder()
                    .addInterceptor(LokaliseInterceptor(lokalise.apiToken))
                    .build(),
                OkHttpClient.Builder().build(),
                Gson(),
                logger
            )
        } else {
            Api.FolderApi(folder.folder)
        }
    }

    open class LokaliseExtension {
        lateinit var apiToken: String
        lateinit var projectId: String
        var languageMappings: List<LanguageMapping> = emptyList()

        fun isInitialized(): Boolean {
            return this::apiToken.isInitialized
        }
    }

    open class FolderExtension {
        lateinit var folder: File

        fun isInitialized(): Boolean {
            return this::folder.isInitialized
        }
    }
}
