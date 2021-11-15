package com.intermedia.translations.utils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RealFolder(
    private val path: Path
) : Folder {

    init {
        Files.createDirectories(path)
    }

    override fun folder(name: String, body: Folder.() -> Unit) {
        RealFolder(Paths.get(path.toString(), name)).body()
    }

    override fun file(name: String, body: File.() -> Unit) {
        val file = RealFile(Paths.get(path.toString(), name))
        file.body()
        file.save()
    }

    companion object {
        fun root(root: Path, body: RealFolder.() -> Unit) {
            RealFolder(root).body()
        }
    }
}
