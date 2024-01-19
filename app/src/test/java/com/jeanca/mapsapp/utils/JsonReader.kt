package com.jeanca.mapsapp.utils

import com.jeanca.mapsapp.commons.Constants.DATA_TEST_PATH
import java.io.File
import java.io.FileInputStream

object JsonReader {

    fun readDataFromJsonFile(filename: String): String {
        val file = File("$DATA_TEST_PATH/$filename")
        return if (file.exists()) FileInputStream(file).bufferedReader().use { it.readText() }
        else String()
    }
}