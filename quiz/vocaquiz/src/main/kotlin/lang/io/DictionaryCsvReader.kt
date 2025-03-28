package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

/**
 * Format is:
 * Category | Unit | Word | Translation | Word Type Info 
 */
class DictionaryCsvReader {
    /**
     * Creates the Greek language based on the files packaged within the library
     * and the optional files present in the given directory.
     */
    fun readGreekDictionary(dir: File? = null): Dictionary {
        val input = this::class.java.getResourceAsStream("/jal/voca/lang/greek-words.csv")
        try {
            val base = readWords(input)
            println("[voca] read words from library -> " + base.size)
            val all = if (dir != null) {
                val wordFile = File(dir, "greek-words.csv")
                if (wordFile.exists()) {
                    val fin = FileInputStream(wordFile)
                    try {
                        val ext = readWords(fin)
                        println("[voca] read words from " + wordFile.absolutePath + " -> " + ext.size)
                        val both = ArrayList(base)
                        both.addAll(ext)
                        both
                    } finally {
                        fin.close()
                    }
                } else {
                    println("[voca] local file " + wordFile.absolutePath + " does not exist")
                    base
                }
            } else {
                base
            }
            return Dictionary(Greek(dir), English(), all)
        } finally {
            input.close()
        }
    }

    fun readWords(inputStream: InputStream): List<CategorizedTranslation> {
        val result = WordsBuilder()
        val br = BufferedReader(inputStream.reader())
        try {
            var line: String? = br.readLine()
            while (line != null) {
                val values = line.split("|")
                val category = values[0]
                val unit = values[1].trim()
                val w = values[2].trim()
                val t = values[3].trim()
                val typeInfoN = if (values.size > 4) TypeInfo.parseTypeInfo(values[4].trim()) else TypeInfo()
                val typeInfo = if (typeInfoN == null) TypeInfo() else typeInfoN

                result.add(w, t, typeInfo, category, unit)

                line = br.readLine()
            }
        } finally {
            br.close() 
        }
        return result.getWords()
    }
}