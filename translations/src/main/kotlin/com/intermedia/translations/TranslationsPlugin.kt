package com.intermedia.translations

import org.gradle.api.Plugin
import org.gradle.api.Project

class TranslationsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(
            EXTENSION_NAME,
            TranslationsExtension::class.java
        )
        project.afterEvaluate {
            project.tasks.register(PULL_TASK_NAME, TranslationsPullTask::class.java) { task ->
                val args = project.extensions.getByType(TranslationsExtension::class.java)
                task.api = args.api.build(project.logger)
                task.supportedLanguages = args.supportedLanguages
                task.notTranslatedFile = args.notTranslatedFile
                task.stringsFileName = args.stringsFileName
                task.resourcesFolder = args.resourcesFolder
            }
        }
    }

    companion object {
        private const val EXTENSION_NAME = "translations"
        private const val PULL_TASK_NAME = "translationsPull"
    }
}
