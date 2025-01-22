package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

fun main(args: Array<String>) {
    val categories: Map<String, WordCategory>
    val wordForms: WordForms

    println("reading google sheet")
    val reader = DictionaryGoogleSheetReader.greek()
    categories = reader.readWords(listOf("Lettres", "Conjugation", "Forms"))

    wordForms = reader.readForms()


    val dirName = if (args.size > 0) args[0] else "./"
    val wordsFile = FileOutputStream(dirName + "greek-words.csv")
    println("writing greek-words")
    try {
        DictionaryCsvWriter().writeCategories(categories, wordsFile)

    } finally {
        wordsFile.close()
    }

    val formsFile = FileOutputStream(dirName + "greek-forms.csv")
    println("writing greek-forms")
    try {
        WordFormCsvWriter().writeWordForms(wordForms.suffixes, formsFile)

    } finally {
        formsFile.close()
    }

    val articlesFile = FileOutputStream(dirName + "greek-articles.csv")
    println("writing greek-articles")
    try {
        ArticlesCsvWriter().writeArticles(wordForms.articles, articlesFile)

    } finally {
        articlesFile.close()
    }
}
