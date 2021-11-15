package com.intermedia.translations

import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class TranslationsPluginTest {

    @Rule
    @JvmField
    var tmpFolder = TemporaryFolder()

    @Test
    fun folderApiConfiguration() {
        val projectFolder = tmpFolder.newFolder()
        val project = ProjectBuilder.builder()
            .withProjectDir(projectFolder)
            .build()
        project.pluginManager.apply("com.intermedia.translations")
        val translationsExtension = project.extensions.getByName(
            "translations"
        ) as TranslationsExtension
        translationsExtension.api.folder.folder = tmpFolder.newFolder()
        translationsExtension.supportedLanguages = SupportedLanguages(listOf("de"))
        translationsExtension.notTranslatedFile = tmpFolder.newFile()
        translationsExtension.resourcesFolder = tmpFolder.newFolder()
        project.evaluationDependsOn(":")

        MatcherAssert.assertThat(
            project.extensions.getByName("translations"),
            IsNot.not(IsNull.nullValue())
        )
        MatcherAssert.assertThat(
            project.tasks.getByName("translationsPull"),
            IsNot.not(IsNull.nullValue())
        )
    }
}
