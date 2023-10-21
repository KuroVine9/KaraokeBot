package util

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun readFile(path: String): String {
    val file = File(path)
    if(!file.exists()) throw NoSuchFileException(file)

    return file.readLines().joinToString(separator = "")
}