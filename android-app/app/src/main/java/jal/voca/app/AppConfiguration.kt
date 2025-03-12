package jal.voca.app

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.io.Reader
import java.io.Writer

data class AppConfiguration(var itemCount: Int = 10) {
    fun writeTo(dir: File) {
        val confFile = File(dir, "configuration.properties")
        FileWriter(confFile).use {
            writeTo(it)
        }
    }

    fun writeTo(out: Writer) {
        PrintWriter(out).use {
            it.println("itemCount=$itemCount")
        }
    }

    fun readFrom(dir: File) {
        val confFile = File(dir, "configuration.properties")
        if (confFile.exists()) {
            FileReader(confFile).use {
                readFrom(it)
            }
        }
    }

    fun readFrom(inR: Reader) {
        val values = mutableMapOf<String, String>()
        BufferedReader(inR).use {
            var line = it.readLine()
            while (line != null) {
                val equal = line.indexOf("=")
                values.put(line.substring(0, equal).trim(), line.substring(equal + 1).trim())

                line = it.readLine()
            }
        }

        val vIC = values.get("itemCount")
        if (vIC != null) {
            itemCount = vIC.toInt()
        }
    }
}

