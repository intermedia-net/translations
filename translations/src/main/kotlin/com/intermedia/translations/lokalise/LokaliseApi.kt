package com.intermedia.translations.lokalise

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intermedia.translations.Api
import com.intermedia.translations.TranslationsException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.gradle.api.logging.Logger
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class LokaliseApi(
    private val projectId: String,
    private val languageMapping: List<LanguageMapping>,
    private val lokaliseHttpClient: OkHttpClient,
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val logger: Logger
) : Api {

    @Throws(TranslationsException::class)
    override fun fetchTranslations(): Path {
        val filesRequest = Request.Builder()
            .url("$API_BASE_URL/projects/$projectId/files/download")
            .post(
                String.format(
                    Locale.US,
                    BUNDLE_REQUEST_BODY,
                    gson.toJson(languageMapping)
                ).toRequestBody(BUNDLE_REQUEST_MEDIA_TYPE)
            )
            .build()

        val response = lokaliseHttpClient.newCall(filesRequest).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && responseBody != null) {
            logger.warn("LokaliseApi files download response $responseBody")
            val responseJson = gson.fromJson(responseBody, JsonObject::class.java)
            if (responseJson.get(BUNDLE_URL_RESPONSE_KEY)?.asString == null) {
                throw TranslationsException(
                    "Lokalise files response contains unknown bundle format. " +
                            "Response body: $responseBody"
                )
            }
            val bundleUrl = responseJson.asJsonObject.get(BUNDLE_URL_RESPONSE_KEY).asString

            val downloadBundleRequest = Request.Builder().url(bundleUrl).get().build()
            val bundleResponse = httpClient.newCall(downloadBundleRequest).execute()
            val body = bundleResponse.body

            if (bundleResponse.isSuccessful && body != null) {
                val targetDir = Files.createTempDirectory(BUNDLE_DIR_NAME)
                unzipBundle(body, targetDir)
                logger.warn("Downloaded to $targetDir")
                return targetDir
            } else {
                throw TranslationsException(
                    "Lokalise bundle response is not success, " +
                            "response code: ${bundleResponse.code}, " +
                            "response body: $body"
                )
            }
        } else {
            throw TranslationsException(
                "Lokalise files response is not success, " +
                        "response code: ${response.code}, " +
                        "response body: $responseBody"
            )
        }
    }

    private fun unzipBundle(body: ResponseBody, targetDir: Path) {
        body.byteStream().use { bodyStream ->
            ZipInputStream(bodyStream).use { zipIn ->
                unzipFiles(zipIn, targetDir)
            }
        }
    }

    private fun unzipFiles(zipIn: ZipInputStream, targetDir: Path) {
        var ze: ZipEntry?
        while (zipIn.nextEntry.also { ze = it } != null) {
            val resolvedPath = targetDir.resolve(ze!!.name)
            if (ze!!.isDirectory) {
                Files.createDirectories(resolvedPath)
            } else {
                Files.createDirectories(resolvedPath.parent)
                Files.copy(zipIn, resolvedPath)
            }
        }
    }

    companion object {
        private const val API_BASE_URL = "https://api.lokalise.com/api2"
        private val BUNDLE_REQUEST_BODY = """{
            "format": "xml", 
            "original_filenames": true,
            "language_mapping": %s
        }""".trimIndent()
        private val BUNDLE_REQUEST_MEDIA_TYPE = "application/json".toMediaType()
        private const val BUNDLE_URL_RESPONSE_KEY = "bundle_url"
        private const val BUNDLE_DIR_NAME = "lokalise"
    }
}
