package com.intermedia.translations.strings

import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class StringsXmlSimple(
    private val doc: Document,
    private val filePath: String
) : StringsXml {

    override fun readStringsNodes(): Map<String, StringXml> {
        val childNodes = doc.documentElement.childNodes
        val result = mutableMapOf<String, StringXml>()
        childNodes.forEachString { node ->
            val stringXml = StringXmlSimple(node)
            result[stringXml.name()] = stringXml
        }

        return result
    }

    override fun addString(string: StringXml) {
        val stringNode = string.asNode()
        val imported = doc.importNode(stringNode, true)
        doc.documentElement.appendChild(createIndentNode())
        doc.documentElement.appendChild(imported)
        doc.documentElement.appendChild(createNewLineNode())
    }

    override fun remove(name: String) {
        val childNodes = doc.documentElement.childNodes
        val nodesToRemove = mutableListOf<Node>()
        childNodes.forEachString { node ->
            val nodeName = node.nameAttr()
            if (nodeName == name) {
                nodesToRemove.add(node)
            }
        }
        nodesToRemove.forEach { nodeToRemove ->
            val previousSibling = nodeToRemove.previousSibling
            val isPreviousText = previousSibling != null && previousSibling.nodeType == Node.TEXT_NODE
            val isPreviousTextContainsOnlySpaces = isPreviousText && previousSibling.textContent.all {
                it == '\n' || it == ' '
            }
            if (isPreviousTextContainsOnlySpaces) {
                doc.documentElement.removeChild(previousSibling)
            }
            doc.documentElement.removeChild(nodeToRemove)
        }
    }

    override fun save() {
        val transformerFactory: TransformerFactory = TransformerFactory.newInstance()
        val transformer: Transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name())

        FileOutputStream(filePath).use { outputStream ->
            val streamResult = StreamResult(outputStream)

            transformer.transform(DOMSource(createHeaderNode()), streamResult)
            transformer.transform(DOMSource(createNewLineNode()), streamResult)
            // to omit standalone="no" declaration
            // from https://community.oracle.com/tech/developers/discussion/comment/6845084#Comment_6845084
            doc.xmlStandalone = true
            transformer.transform(DOMSource(doc), streamResult)
        }
    }

    private fun createIndentNode(): Node {
        return doc.createTextNode("    ")
    }

    private fun createNewLineNode(): Node {
        return doc.createTextNode("\n")
    }

    private fun createHeaderNode(): Node {
        return doc.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"utf-8\"")
    }
}
