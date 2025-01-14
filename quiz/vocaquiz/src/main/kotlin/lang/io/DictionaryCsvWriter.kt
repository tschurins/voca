package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Format is:
 * Category | Word | Translation | Word Type Info 
 */
class DictionaryCsvWriter {
    fun writeCategories(categories: Map<String, WordCategory>, stream: OutputStream) {
        val pw = PrintWriter(stream, false, StandardCharsets.UTF_8)
        try {
            for (category in categories.values) {
                for (translation in category.words) {
                    pw.println(category.name + "|" + translation.word.word + "|" + translation.translation.word + "|" + translation.word.typeInfo)
                }
            }
        } finally {
            pw.close()
        }
    }
}