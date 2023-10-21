package util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun writeFile(path: String, text: String) {
    val file = File(path)
    if(!file.exists()) throw NoSuchFileException(file)

    FileWriter(path).use {
        BufferedWriter(it).use {
            it.write(text)
        }
    }
}