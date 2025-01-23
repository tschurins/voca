package jal.voca.lang.io

import jal.voca.lang.*
import java.io.InputStream
import org.apache.poi.ss.usermodel.*

class DictonaryExcelReader(val ignoreSheets: List<String>) {
    fun readWords(inputStream: InputStream): List<CategorizedTranslation> {
        val workbook: Workbook = WorkbookFactory.create(inputStream)
        val nbSheets: Int = workbook.getNumberOfSheets()

        val result = WordsBuilder()
        for (i in 0..<nbSheets) {
            val sheet: Sheet = workbook.getSheetAt(i)
            val sheetName: String = sheet.getSheetName()
            if (sheetName !in ignoreSheets) {
                for (translation in readSheet(sheet).words) {
                    result.add(translation.word.word, translation.translation.word, translation.word.typeInfo, sheetName, "")
                }
            }
        }
        return result.getWords()
    }

    fun readSheet(sheet: Sheet): WordCategory {
        val sheetName: String = sheet.getSheetName()
        val lastRow: Int = sheet.getLastRowNum()
        val translations: MutableList<Translation> = mutableListOf()
        for (i in 0..lastRow) {
            val row: Row? = sheet.getRow(i)
            if (row != null) {
                val translation = readTranslation(row)
                if (translation != null) {
                    translations.add(translation)
                }
            }
        }
        return WordCategory(sheetName, translations)
    }

    fun readTranslation(row: Row): Translation? {
        val wordS = readCell(row, 0)
        if (wordS != null) {
            val translationS = readCell(row, 1)
            val typeInfoS = readCell(row, 2)
            val typeInfo: TypeInfo
            if (typeInfoS != null) {
                val typeInfoResult = TypeInfo.parseTypeInfo(typeInfoS)
                typeInfo = if (typeInfoResult == null) TypeInfo() else typeInfoResult
            } else {
                typeInfo = TypeInfo()
            }
            val word = Word(wordS, typeInfo)
            val translation = Word(translationS!!, TypeInfo(typeInfo.type, null, typeInfo.cardinality, null))
            return Translation(word, translation)
        }
        return null
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