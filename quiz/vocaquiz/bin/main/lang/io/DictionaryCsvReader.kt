package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

/**
 * Format is:
 * Category | Word | Translation | Word Type Info 
 */
class DictionaryCsvReader {
    fun readGreekDictionary(): Dictionary {
        val input = this::class.java.getResourceAsStream("/jal/voca/lang/greek-words.csv")
        try {
            val categories = readCategories(input)
            return Dictionary(Greek(), English(), ArrayList(categories.values))
        } finally {
            input.close()
        }
    }

    fun readCategories(inputStream: InputStream): Map<String, WordCategory> {
        val result: MutableMap<String, MutableList<Translation>> = mutableMapOf()
        val br = BufferedReader(inputStream.reader())
        try {
            var line: String? = br.readLine()
            while (line != null) {
                val values = line.split("|")
                val translations = result.computeIfAbsent(values[0]) { mutableListOf() }
                val typeInfoN = if (values.size > 3) TypeInfo.parseTypeInfo(values[3]) else TypeInfo()
                val typeInfo = if (typeInfoN == null) TypeInfo() else typeInfoN
                translations.add(Translation(
                    Word(values[1], typeInfo),
                    Word(values[2], TypeInfo(type = typeInfo.type, cardinality = typeInfo.cardinality))
                ))
                line = br.readLine()
            }
        } finally {
            br.close() 
        }
        return result.mapValues { WordCategory(it.key, it.value) }
    }
}