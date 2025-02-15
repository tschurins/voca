package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Format is:
 * Category | Word | Translation | Word Type Info 
 */
class DictionaryCsvWriter {
    fun writeWords(words: List<CategorizedTranslation>, stream: OutputStream) {
        val pw = PrintWriter(stream, false, StandardCharsets.UTF_8)
        try {
            for (ct in words) {
                for (cat in ct.categories) {
                    pw.println(cat.trim() 
                        + "|" + ct.unit.trim() 
                        + "|" + ct.translation.word.word.trim() 
                        + "|" + ct.translation.translation.word.trim() 
                        + "|" + ct.translation.word.typeInfo.toString().trim())
                }
            }
        } finally {
            pw.close()
        }
    }
}