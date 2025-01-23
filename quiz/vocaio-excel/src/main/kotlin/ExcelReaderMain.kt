package jal.voca.lang.io

import jal.voca.lang.*
import java.io.*

fun main(args: Array<String>) {
    val words: List<CategorizedTranslation>
    val wordForms: WordForms

    val excelName = if (args.size > 0) args[0] else "./Duo-G.xlsx"

    val inputStream: InputStream = FileInputStream(excelName)
    try {
        words = DictonaryExcelReader(listOf("Lettres", "Conjugation", "Forms")).readWords(inputStream)
    } finally {
        inputStream.close()
    }
    val inputStream2: InputStream = FileInputStream(excelName)
    try {
        wordForms = WordFormExcelReader().readWordForms(inputStream2)
    } finally {
        inputStream.close()
    }


    val dirName = if (args.size > 1) args[1] else "./"
    val wordsFile = FileOutputStream(dirName + "greek-words.csv")
    println("writing greek-words")
    try {
        DictionaryCsvWriter().writeWords(words, wordsFile)

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
