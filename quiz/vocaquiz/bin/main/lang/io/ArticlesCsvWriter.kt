package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*
import java.nio.charset.StandardCharsets

class ArticlesCsvWriter {
    fun writeArticles(articles: List<ArticlesPerGender>, stream: OutputStream) {
        val forms = getAllForms(articles)

        val pw = PrintWriter(stream, false, StandardCharsets.UTF_8)
        try {
            // header
            pw.print("gender|type")
            for (wordForm in forms) {
                pw.print("|" + wordForm.case + "," + wordForm.cardinality)
            }
            pw.println()

            // rows
            for (articleForms in articles) {
                pw.print("" + articleForms.gender + "|" + articleForms.type)
                for (wordForm in forms) {
                    val suffix = articleForms.forms[wordForm]
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

    private fun getAllForms(suffixes: List<ArticlesPerGender>) : List<WordForm> {
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