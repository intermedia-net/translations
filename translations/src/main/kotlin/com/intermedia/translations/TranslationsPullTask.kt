package com.intermedia.translations

import com.intermedia.translations.strings.StringXml
import com.intermedia.translations.strings.StringsXmlSimple
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

open class TranslationsPullTask : DefaultTask() {

    private val db: DocumentBuilder by lazy {
        val dbf = DocumentBuilderFactory.newInstance()
        dbf.newDocumentBuilder()
    }

    @Input
    lateinit var api: Api
    @Input
    lateinit var supportedLanguages: SupportedLanguages
    @InputFile
    lateinit var notTranslatedFile: File
    @Input
    lateinit var stringsFileName: String
    @InputDirectory
    lateinit var resourcesFolder: File

    @TaskAction
    fun pull() {
        logger.warn("Pull translations started")
        val downloadedTranslationsFolder: Path
        try {
            downloadedTranslationsFolder = api.fetchTranslations()
        } catch (e: TranslationsException) {
            throw GradleScriptException("Could not fetch translations.", e)
        }

        val notTranslatedStrings = StringsXmlSimple(
            db.parse(notTranslatedFile),
            notTranslatedFile.absolutePath
        )
        val notTranslatedNodes = notTranslatedStrings.readStringsNodes()

        val requiredLanguages = listOf("") + supportedLanguages.required
        val requiredLanguageToNewKeys = findNewTranslations(
            requiredLanguages,
            downloadedTranslationsFolder,
            db,
            notTranslatedNodes
        )
        val optionalLanguages = supportedLanguages.optional
        val optionalLanguageToNewKeys = findNewTranslations(
            optionalLanguages,
            downloadedTranslationsFolder,
            db,
            notTranslatedNodes
        )

        // check if we have translations for all required languages
        val groupedByNewTranslations = requiredLanguageToNewKeys.entries.groupBy { it.value.keys }
        if (groupedByNewTranslations.size == 1 && groupedByNewTranslations.values.first().first().value.isNotEmpty()) {
            val foundRequiredKeys = groupedByNewTranslations.keys.first()
            logger.warn("We have all new translations, let's apply them!")
            applyNewTranslations(
                requiredLanguageToNewKeys,
                optionalLanguageToNewKeys,
                notTranslatedStrings,
                foundRequiredKeys
            )
        } else {
            logger.warn("We have an incomplete translations! Check logs above.")
        }
    }

    private fun applyNewTranslations(
        requiredLanguageToNewKeys: Map<String, MutableMap<String, StringXml>>,
        optionalLanguageToNewKeys: Map<String, MutableMap<String, StringXml>>,
        notTranslatedStrings: StringsXmlSimple,
        foundRequiredKeys: MutableSet<String>
    ) {
        for ((language, newTranslations) in requiredLanguageToNewKeys) {
            val localStringsXml = localStringsXml(language)
            newTranslations.forEach { (name, stringXml) ->
                localStringsXml.addString(stringXml)
                notTranslatedStrings.remove(name)
            }
            localStringsXml.save()
        }

        for ((language, newTranslations) in optionalLanguageToNewKeys) {
            val localStringsXml = localStringsXml(language)

            newTranslations.forEach { (name, stringXml) ->
                if (foundRequiredKeys.contains(name)) {
                    localStringsXml.addString(stringXml)
                }
            }
            localStringsXml.save()
        }

        notTranslatedStrings.save()
    }

    private fun localStringsXml(language: String): StringsXmlSimple {
        val filePath = Paths.get(
            resourcesFolder.absolutePath,
            valuesFolderName(language),
            stringsFileName
        )
        return StringsXmlSimple(
            db.parse(filePath.toFile()),
            filePath.toString()
        )
    }

    private fun findNewTranslations(
        languages: List<String>,
        newTranslationsPath: Path,
        db: DocumentBuilder,
        notTranslatedStrings: Map<String, StringXml>
    ): Map<String, MutableMap<String, StringXml>> {
        val languageToNewKeys = mutableMapOf<String, MutableMap<String, StringXml>>()
        for (language in languages) {
            val newKeys = languageToNewKeys.getOrPut(language) { mutableMapOf() }

            val folderPath = Paths.get(newTranslationsPath.toString(), valuesFolderName(language))
            folderPath.toFile().listFiles()
                ?.asSequence()
                ?.map {
                    StringsXmlSimple(
                        db.parse(it),
                        it.absolutePath
                    ).readStringsNodes()
                }
                ?.forEach { downloadedLangKeys ->
                    findNewTranslationsForLanguage(notTranslatedStrings, downloadedLangKeys, language, newKeys)
                }
        }
        return languageToNewKeys
    }

    private fun findNewTranslationsForLanguage(
        notTranslatedStrings: Map<String, StringXml>,
        downloadedLangKeys: Map<String, StringXml>,
        language: String,
        newKeys: MutableMap<String, StringXml>
    ) {
        for (notTranslated in notTranslatedStrings) {
            if (downloadedLangKeys.containsKey(notTranslated.key)) {
                val newTranslation = downloadedLangKeys[notTranslated.key]!!
                logger.warn("We have a new translation: ${notTranslated.key} for language $language")
                val notTranslatedFormatters = notTranslated.value.formatSpecifiers()
                val newTranslationFormatters = newTranslation.formatSpecifiers()
                if (notTranslatedFormatters != newTranslationFormatters) {
                    throw GradleScriptException(
                        "Could not apply translations.",
                        IllegalStateException(
                            "New translation for string '${notTranslated.key}' has incorrect format specifiers. " +
                                    "Actual: $newTranslationFormatters, " +
                                    "expected: $notTranslatedFormatters"
                        )
                    )
                }
                newKeys[notTranslated.key] = newTranslation
            }
        }
    }

    private fun valuesFolderName(language: String): String {
        return if (language.isEmpty()) {
            VALUES_FOLDER_PREFIX
        } else {
            "$VALUES_FOLDER_PREFIX-$language"
        }
    }

    companion object {
        private const val VALUES_FOLDER_PREFIX = "values"
    }
}
