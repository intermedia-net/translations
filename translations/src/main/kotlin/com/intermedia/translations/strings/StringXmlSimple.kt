package com.intermedia.translations.strings

import com.intermedia.translations.format.FormatSpecifiers
import org.w3c.dom.Node

class StringXmlSimple(
    private val node: Node
) : StringXml {

    override fun name(): String {
        return node.nameAttr()
    }

    override fun formatSpecifiers(): FormatSpecifiers {
        return FormatSpecifiers(node.textContent)
    }

    override fun asNode(): Node {
        return node
    }
}
