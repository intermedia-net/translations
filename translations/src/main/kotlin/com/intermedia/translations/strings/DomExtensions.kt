package com.intermedia.translations.strings

import org.w3c.dom.Node
import org.w3c.dom.NodeList

private const val STRING_TAG = "string"
private const val NAME_ATTR = "name"

fun Node.nameAttr(): String {
    return this.attributes.getNamedItem(NAME_ATTR)?.nodeValue ?: ""
}

fun NodeList.forEachString(action: (Node) -> Unit) {
    for (i in 0 until this.length) {
        val node = this.item(i)
        if (STRING_TAG == node.nodeName) {
            action(node)
        }
    }
}
