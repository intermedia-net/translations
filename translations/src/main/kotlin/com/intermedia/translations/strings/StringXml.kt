package com.intermedia.translations.strings

import com.intermedia.translations.format.FormatSpecifiers
import org.w3c.dom.Node

interface StringXml {
    fun name(): String
    fun formatSpecifiers(): FormatSpecifiers
    fun asNode(): Node
}
