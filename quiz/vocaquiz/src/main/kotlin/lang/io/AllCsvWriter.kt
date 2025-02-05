package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

class AllCsvWriter {
    /**
     * Writes all the files related to a language: words, articles and word-forms.
     */
    fun writeAll(full: FullReader, targetDir: File, languageName: String) {
        val words = full.getWords()
        val wordForms = full.getForms()
        writeAll(words, wordForms, targetDir, languageName)
    }

    /**
     * Writes all the files related to a language: words, articles and word-forms.
     */
    fun writeAll(words: List<CategorizedTranslation>, wordForms: WordForms, targetDir: File, languageName: String) {
        if (!targetDir.exists()) {
            val created = targetDir.mkdirs()
            if (!created) {
                throw RuntimeException("cannot create directory " + targetDir.absolutePath)
            }
        }

        val wordsFile = FileOutputStream(File(targetDir, languageName + "-words.csv"))
        try {
            DictionaryCsvWriter().writeWords(words, wordsFile)
        } finally {
            wordsFile.close()
        }

        val formsFile = FileOutputStream(File(targetDir, languageName + "-forms.csv"))
        try {
            WordFormCsvWriter().writeWordForms(wordForms.suffixes, formsFile)
        } finally {
            formsFile.close()
        }

        val articlesFile = FileOutputStream(File(targetDir, languageName + "-articles.csv"))
        try {
            ArticlesCsvWriter().writeArticles(wordForms.articles, articlesFile)
        } finally {
            articlesFile.close()
        }
    }

     /**
     * Writes all the files related to a language: words, articles and word-forms.
     * Only writes the difference with the given dictionary.
     *
     * @see Dictionary#diff
     */
   fun writeDiff(full: FullReader, targetDir: File, dico: Dictionary) {
        val fullWords = full.getWords()
        val fullDico = Dictionary(dico.wordLanguage, dico.translationLanguage, fullWords)
        val diffDico = fullDico.diff(dico)

        writeAll(diffDico.words, full.getForms(), targetDir, dico.wordLanguage.name)
    }

}