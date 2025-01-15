package jal.voca.lang.io

import jal.voca.lang.*
import java.io.InputStream
import org.apache.poi.ss.usermodel.*

class WordFormExcelReader(val sheetName: String = "Forms") {
    fun readWordForms(inputStream: InputStream) : WordForms {
        val formsResult: MutableList<WordCasesPerGender> = mutableListOf()
        val articlesResult: MutableList<ArticlesPerGender> = mutableListOf()

        val workbook: Workbook = WorkbookFactory.create(inputStream)
        val formSheet = workbook.getSheet(sheetName)

        val caseRow = formSheet.getRow(0)
        val cardinalityRow = formSheet.getRow(1)
        val lastRow: Int = formSheet.getLastRowNum()
        var gender: Gender? = null
        var articles: Boolean = false
        for (i in 2..lastRow) {
            val row = formSheet.getRow(i)
            if (row != null) {
                val newGender = readCell(row, 0)
                if (newGender != null) {
                    gender = valueOf<Gender>(newGender)
                    articles = true
                }
                if (articles) {
                    if (newGender == null) {
                        // indefinite
                        articlesResult.add(ArticlesPerGender(gender!!, ArticleType.INDEFINITE, readAllForms(row, cardinalityRow, caseRow)))
                        articles = false
                    } else {
                        // definite
                        articlesResult.add(ArticlesPerGender(gender!!, ArticleType.DEFINITE, readAllForms(row, cardinalityRow, caseRow)))
                    }
                } else {
                    formsResult.add(WordCasesPerGender(gender!!, readAllForms(row, cardinalityRow, caseRow)))
                }
            }
        }

        return WordForms(formsResult, articlesResult)
    }

    private fun readAllForms(row: Row, cardinalityRow: Row, caseRow: Row) : Map<WordForm, String> {
        val lastColumn = row.getLastCellNum()
        val suffixes: MutableMap<WordForm, String> = mutableMapOf() 
        for (j in 1..lastColumn) {
            val suffix = readCell(row, j)
            if (suffix != null) {
                val cardinalityS = readCell(cardinalityRow, j)
                val cardinality = valueOf<Cardinality>(cardinalityS)
                val caseS = readCell(caseRow, ((j - 1) / 2) * 2 + 1)
                val case = valueOf<WordCase>(caseS)
                suffixes[WordForm(case!!, cardinality!!)] = clean(suffix)
            }
        }
        return suffixes
    }

    private fun clean(s: String): String {
        var result = s.trim()
        if (result.startsWith("-")) {
            result = result.substring(1)
        }
        return result;
    }

    private fun readCell(row: Row, index: Int): String? {
        val cell: Cell? = row.getCell(index)
        if (cell != null) {
            val value: String? = cell.stringCellValue
            return if (value == null || value.isEmpty()) null else value
        }
        return null
    }

}


inline fun <reified T : kotlin.Enum<T>> valueOf(type: String?): T {
    for (value in T::class.java.getEnumConstants()) {
        if (value.name.equals(type, true)) {
            return value
        }
    }
    throw RuntimeException("enum '" + type + "' not found")
}
