package com.intermedia.translations

import com.intermedia.translations.utils.RealFolder
import com.intermedia.translations.utils.StringFile
import org.gradle.api.GradleScriptException
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("LongMethod")
class TranslationsPullTaskTest {

    @Rule
    @JvmField
    var tmpFolder = TemporaryFolder()

    @Test
    fun successApplySimpleTranslations() {
        val valuesFolderName = "values"
        val valuesEnFolderName = "values-en"
        val stringsFileName = "strings.xml"
        val stringsNotTranslatedFileName = "strings_not_translated.xml"

        // create folder with API translations
        val apiFolder = tmpFolder.newFolder("api")
        RealFolder.root(apiFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>")
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Hello</string>")
                    line("</resources>")
                }
            }
        }

        // create folder with project translations
        val projectFolder = tmpFolder.newFolder("project")
        RealFolder.root(projectFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsNotTranslatedFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>", false)
                }
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Старое</string>")
                    line("</resources>", false)
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Old</string>")
                    line("</resources>", false)
                }
            }
        }

        val notTranslatedFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsNotTranslatedFileName
        )
        val valuesFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsFileName
        )
        val valuesEnFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesEnFolderName,
            stringsFileName
        )

        createPullTask(
            apiFolder,
            projectFolder,
            stringsFileName,
            notTranslatedFilePath.toFile()
        ).pull()

        MatcherAssert.assertThat(
            String(Files.readAllBytes(notTranslatedFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("</resources>", false)
                }.toString()
            )
        )
        MatcherAssert.assertThat(
            String(Files.readAllBytes(valuesFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Старое</string>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>", false)
                }.toString()
            )
        )
        MatcherAssert.assertThat(
            String(Files.readAllBytes(valuesEnFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Old</string>")
                    line("    <string name=\"hello\">Hello</string>")
                    line("</resources>", false)
                }.toString()
            )
        )
    }

    @Test
    fun dontApplyNewTranslationsIfItsNotFull() {
        val valuesFolderName = "values"
        val valuesEnFolderName = "values-en"
        val stringsFileName = "strings.xml"
        val stringsNotTranslatedFileName = "strings_not_translated.xml"

        // create folder with API translations
        val apiFolder = tmpFolder.newFolder("api")
        RealFolder.root(apiFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>")
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("</resources>")
                }
            }
        }

        // create folder with project translations
        val projectFolder = tmpFolder.newFolder("project")
        RealFolder.root(projectFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsNotTranslatedFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>", false)
                }
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Старое</string>")
                    line("</resources>", false)
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Old</string>")
                    line("</resources>", false)
                }
            }
        }

        val notTranslatedFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsNotTranslatedFileName
        )
        val valuesFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsFileName
        )
        val valuesEnFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesEnFolderName,
            stringsFileName
        )

        createPullTask(
            apiFolder,
            projectFolder,
            stringsFileName,
            notTranslatedFilePath.toFile()
        ).pull()

        MatcherAssert.assertThat(
            String(Files.readAllBytes(notTranslatedFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет</string>")
                    line("</resources>", false)
                }.toString()
            )
        )
        MatcherAssert.assertThat(
            String(Files.readAllBytes(valuesFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Старое</string>")
                    line("</resources>", false)
                }.toString()
            )
        )
        MatcherAssert.assertThat(
            String(Files.readAllBytes(valuesEnFilePath)),
            IsEqual.equalTo(
                StringFile.build {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Old</string>")
                    line("</resources>", false)
                }.toString()
            )
        )
    }

    @Test
    fun crashIfTranslationsHasErrorsInFormatSpecifiers() {
        val valuesFolderName = "values"
        val valuesEnFolderName = "values-en"
        val stringsFileName = "strings.xml"
        val stringsNotTranslatedFileName = "strings_not_translated.xml"

        // create folder with API translations
        val apiFolder = tmpFolder.newFolder("api")
        RealFolder.root(apiFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет %s</string>")
                    line("</resources>")
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Hello %d</string>")
                    line("</resources>")
                }
            }
        }

        // create folder with project translations
        val projectFolder = tmpFolder.newFolder("project")
        RealFolder.root(projectFolder.toPath()) {
            folder(valuesFolderName) {
                file(stringsNotTranslatedFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"hello\">Привет %s</string>")
                    line("</resources>", false)
                }
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Старое</string>")
                    line("</resources>", false)
                }
            }
            folder(valuesEnFolderName) {
                file(stringsFileName) {
                    line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    line("<resources>")
                    line("    <string name=\"old\">Old</string>")
                    line("</resources>", false)
                }
            }
        }

        val notTranslatedFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsNotTranslatedFileName
        )
        val valuesFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesFolderName,
            stringsFileName
        )
        val valuesEnFilePath = Paths.get(
            projectFolder.absolutePath,
            valuesEnFolderName,
            stringsFileName
        )

        try {
            createPullTask(
                apiFolder,
                projectFolder,
                stringsFileName,
                notTranslatedFilePath.toFile()
            ).pull()
            Assert.fail("Must crash, if the new translations has incorrect format specifiers")
        } catch (ignored: GradleScriptException) {
            // green
        }
    }

    private fun createPullTask(
        apiFolder: File,
        projectFolder: File,
        stringsFileName: String,
        notTranslatedFile: File,
    ): TranslationsPullTask {
        val taskName = "translationsPull"
        val project = ProjectBuilder.builder().build()
        project.tasks.create(taskName, TranslationsPullTask::class.java) { task ->
            task.api = Api.FolderApi(apiFolder)
            task.notTranslatedFile = notTranslatedFile
            task.resourcesFolder = projectFolder
            task.stringsFileName = stringsFileName
            task.supportedLanguages = SupportedLanguages(
                required = listOf("en"),
                optional = listOf()
            )
        }
        return project.tasks.getByName(taskName) as TranslationsPullTask
    }
}
