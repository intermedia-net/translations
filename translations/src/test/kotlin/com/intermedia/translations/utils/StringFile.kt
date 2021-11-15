package com.intermedia.translations.utils

import java.io.StringWriter

class StringFile : File {

    private val writer = StringWriter()

    override fun line(content: String, endWithNewLine: Boolean) {
        if (endWithNewLine) {
            writer.appendLine(content)
        } else {
            writer.append(content)
        }
    }

    override fun save() {
        writer.close()
    }

    override fun toString(): String {
        return writer.toString()
    }

    companion object {
        fun build(body: File.() -> Unit): StringFile {
            val file = StringFile()
            file.body()
            return file
        }
    }
}
