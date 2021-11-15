# Translations gradle plugin

Translations gradle plugin automates work with lokalise translations in Android 
application code base.

[![Build Status][build-image]][build-url]
[![License: MIT][license-image]][license-url]
[![Code Coverage][coverage-image]][coverage-url]

## Getting started

Define plugin
```groovy
plugins {
    id("com.intermedia.translations").version("0.0.1")
}
```
Then set it up:
```groovy
import com.intermedia.translations.SupportedLanguages
import com.intermedia.translations.lokalise.LanguageMapping

translations {
    api {
        lokalise {
            setApiToken("<your lokalise api token>")
            setProjectId("<your lokalise project id>")
            setLanguageMappings([
                new LanguageMapping("fr_CA", "fr"),
                new LanguageMapping("nl_NL", "nl"),
                new LanguageMapping("es_419", "es")
            ])
        }
    }
    setSupportedLanguages(
        new SupportedLanguages(
            ['de', 'es', 'fr', 'it', 'ja', 'nl']
        )
    )
    setNotTranslatedFile(file("./src/main/res/values/strings_not_translated.xml"))
    setResourcesFolder(file("./src/main/res/"))
}

...

android {
    ...
}
```

- `api` - it's a service used to download up-to-date translations
  - `lokalise` - [Lokalise][lokalise] implementation. Plugin uses their 
[download files API][lokalise-download-api]
    - `apiToken` - lokalise api token
    - `projectId` - lokalise project identifier
    - `languageMappings` - optional mapping for lokalise api language ISO names
- `supportedLanguages` - list of supported languages in ISO format
- `notTranslatedFile` - resources file with strings, which you added for future 
  translating
- `resourcesFolder` - folder which contains `values-*` folders with your local 
  translations files

## Plugin tasks

### Pull new translations

To pull new translations run the task `translationsPull`:
```bash
./gradlew translationsPull
```

It will download fresh translations from the API, found new translations (based 
on strings from `notTranslatedFile`) and apply them.


### Push new translations
TBD

## Changelog
- `0.0.1` - initial version

[build-image]: https://github.com/intermedia-net/translations/actions/workflows/ci.yml/badge.svg
[build-url]: https://github.com/intermedia-net/translations/actions/workflows/ci.yml
[license-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[license-url]: https://github.com/intermedia-net/translations/blob/main/LICENSE
[coverage-image]: https://codecov.io/gh/intermedia-net/translations/branch/main/graph/badge.svg
[coverage-url]: https://codecov.io/gh/intermedia-net/translations

[lokalise]: https://lokalise.com/
[lokalise-download-api]: https://app.lokalise.com/api2docs/curl/#transition-download-files-post