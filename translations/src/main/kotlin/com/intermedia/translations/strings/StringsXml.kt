package com.intermedia.translations.strings

interface StringsXml {
    fun readStringsNodes(): Map<String, StringXml>
    fun addString(string: StringXml)
    fun remove(name: String)
    fun save()
}
