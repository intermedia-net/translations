package com.intermedia.translations.utils

import java.io.PrintWriter
import java.nio.file.Path

class RealFile(
    private val path: Path
) : File {

    private val writer = PrintWriter(path.toFile())

    override fun line(content: String, endWithNewLine: Boolean) {
        if (endWithNewLine) {
            writer.println(content)
        } else {
            writer.print(content)
        }
    }

    override fun save() {
        writer.close()
    }
}
