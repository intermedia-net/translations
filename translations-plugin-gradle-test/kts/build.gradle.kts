plugins {
    id("com.intermedia.translations")
}

import com.intermedia.translations.SupportedLanguages
import com.intermedia.translations.lokalise.LanguageMapping

translations {
    api {
        lokalise {
            apiToken = "<your lokalise api token>"
            projectId = "<your lokalise project id>"
            languageMappings = listOf(
                LanguageMapping("fr_CA", "fr"),
                LanguageMapping("nl_NL", "nl"),
                LanguageMapping("es_419", "es")
            )
        }
    }
    supportedLanguages = SupportedLanguages(
        listOf("de", "es", "fr", "it", "ja", "nl"),
        listOf("en-rAU", "en-rGB")
    )
    notTranslatedFile = file("./src/main/res/values/strings_not_translated.xml")
    resourcesFolder = file("./src/main/res/")
}