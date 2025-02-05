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

        val wordsFile = File(targetDir, languageName + "-words.csv")
        val wordsIn = FileOutputStream(wordsFile)
        try {
            println("[voca] write words to " + wordsFile.absolutePath + " -> " + words.size)
            DictionaryCsvWriter().writeWords(words, wordsIn)
        } finally {
            wordsIn.close()
        }

        val formsFile = File(targetDir, languageName + "-forms.csv")
        val formsIn = FileOutputStream(formsFile)
        try {
            println("[voca] write forms to " + formsFile.absolutePath)
            WordFormCsvWriter().writeWordForms(wordForms.suffixes, formsIn)
        } finally {
            formsIn.close()
        }

        val articlesFile = File(targetDir, languageName + "-articles.csv")
        val articlesIn = FileOutputStream(articlesFile)
        try {
            println("[voca] write articles to " + articlesFile.absolutePath)
            ArticlesCsvWriter().writeArticles(wordForms.articles, articlesIn)
        } finally {
            articlesIn.close()
        }
    }

    fun deleteAll(targetDir: File, languageName: String) {
        if (!targetDir.exists()) {
            return;
        }

        val wordsFile = File(targetDir, languageName + "-words.csv")
        wordsFile.delete()

        val formsFile = File(targetDir, languageName + "-forms.csv")
        formsFile.delete()

        val articlesFile = File(targetDir, languageName + "-articles.csv")
        articlesFile.delete()
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

        if (!diffDico.words.isEmpty()) {
            println("[voca] diff: " + diffDico.words.map { it.translation.word })

            writeAll(diffDico.words, full.getForms(), targetDir, dico.wordLanguage.name.lowercase())
        }
    }

}