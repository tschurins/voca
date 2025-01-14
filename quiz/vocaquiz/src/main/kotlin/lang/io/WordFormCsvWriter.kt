package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*
import java.nio.charset.StandardCharsets

class WordFormCsvWriter {
    fun writeWordForms(wordForms: List<WordCasesPerGender>, stream: OutputStream) {
        val forms = getAllForms(wordForms)

        val pw = PrintWriter(stream, false, StandardCharsets.UTF_8)
        try {
            // header
            pw.print("gender")
            for (wordForm in forms) {
                pw.print("|" + wordForm.case + "," + wordForm.cardinality)
            }
            pw.println()

            // rows
            for (wordSuffixes in wordForms) {
                pw.print(wordSuffixes.gender)
                for (wordForm in forms) {
                    val suffix = wordSuffixes.forms[wordForm]
                    pw.print("|")
                    if (suffix != null) {
                        pw.print(suffix)
                    }
                }
                pw.println()
            }
        } finally {
            pw.close()
        }
    }

    private fun getAllForms(suffixes: List<WordCasesPerGender>) : List<WordForm> {
        val result: MutableList<WordForm> = mutableListOf()
        for (wordSuffixes in suffixes) {
            for (wordForm in wordSuffixes.forms.keys) {
                if (wordForm !in result) {
                    result.add(wordForm)
                } 
            }
        }
        return result
    }
}