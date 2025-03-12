package jal.voca.lang.io

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.*
import com.google.api.services.sheets.v4.Sheets.Builder
import com.google.api.services.sheets.v4.model.*
import jal.voca.lang.*
import jal.voca.lang.io.FullReader


class DictionaryGoogleSheetReader(
        val spreadsheetId: String,
        val service: Sheets,
        val spreadsheet: Spreadsheet,
        val ignoreSheets: List<String>,
) : FullReader {

    companion object Factory {
        fun create(spreadsheetId: String, apiKey: String, ignoreSheets: List<String>) : DictionaryGoogleSheetReader {
            val transport = NetHttpTransport.Builder().build()
            val service = Builder(transport, GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("voca")
                    .setGoogleClientRequestInitializer(SheetsRequestInitializer(apiKey))
                    .build()
            val spreadsheet = service.spreadsheets().get(spreadsheetId).execute()
            return DictionaryGoogleSheetReader(spreadsheetId, service, spreadsheet, ignoreSheets)
        }

        fun greek() : DictionaryGoogleSheetReader {
            val apiKey = DictionaryGoogleSheetReader::class.java.getResource("/jal/voca/lang/io/apikey").readText().trim()
            val spreadsheetId = "12Ca3QRBPWKtEspy1vTvRWGdS586d4a-E275-1gsCJjE"
            return create(spreadsheetId, apiKey, listOf("Lettres", "Conjugation", "Forms"))
        }
    }

    override fun getWords() : List<CategorizedTranslation> {
        val ranges = spreadsheet.getSheets()
                .map { it.getProperties().getTitle() }
                .filter { it !in ignoreSheets }
                .map { it + "!A:D" }
        val response = service.spreadsheets().values().batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute()
        
        val result = WordsBuilder()
        for (vr in response.getValueRanges()) {
            val sheetName = getSheetName(vr)
            for (row in vr.getValues()) {
                val ct = readTranslation(row)
                if (ct != null) {
                    result.add(ct.translation.word.word, ct.translation.translation.word, ct.translation.word.typeInfo, sheetName, ct.unit)
                }
            }
        }
        return result.getWords()
    }

    private fun readTranslation(row: List<Any>) : CategorizedTranslation? {
        val wordS = getValue(row, 0)
        if (wordS != null) {
            val translationS = getValue(row, 1)
            if (translationS == null) {
                throw RuntimeException("no translation for word " + wordS)
            }
            val typeInfoS = getValue(row, 2)
            val typeInfo: TypeInfo
            if (typeInfoS != null) {
                val typeInfoResult = TypeInfo.parseTypeInfo(typeInfoS)
                typeInfo = if (typeInfoResult == null) TypeInfo() else typeInfoResult
            } else {
                typeInfo = TypeInfo()
            }
            val word = Word(wordS, typeInfo)
            val translation = Word(translationS!!, TypeInfo(typeInfo.type, null, typeInfo.cardinality, null))
            val unit = getValue(row, 3);
            return CategorizedTranslation(Translation(word, translation), listOf(), if (unit == null) "" else unit)
        }
        return null
    }

    private fun getSheetName(vr: ValueRange) : String {
        val range = vr.getRange()
        val bang = range.indexOf("!")
        return range.substring(0, bang)
    }

    override fun getForms() : WordForms {
        val sheetData = service.spreadsheets().values().get(spreadsheetId, "Forms").execute().getValues()

        val formsResult: MutableList<WordCasesPerGender> = mutableListOf()
        val articlesResult: MutableList<ArticlesPerGender> = mutableListOf()

        var gender: Gender? = null
        var articles: Boolean = false
        val lastRow = sheetData.size
        for (row in 2..<lastRow) {
            val newGender = getValue(sheetData[row], 0)
            if (newGender != null) {
                gender = valueOf<Gender>(newGender)
                articles = true
            }
            if (articles) {
                if (newGender == null) {
                    // indefinite
                    articlesResult.add(ArticlesPerGender(gender!!, ArticleType.INDEFINITE, readAllForms(sheetData, row)))
                    articles = false
                } else {
                    // definite
                    articlesResult.add(ArticlesPerGender(gender!!, ArticleType.DEFINITE, readAllForms(sheetData, row)))
                }
            } else {
                formsResult.add(WordCasesPerGender(gender!!, readAllForms(sheetData, row)))
            }
        }
        return WordForms(formsResult, articlesResult)
    }

    private fun readAllForms(sheetData: List<List<Any>>, row: Int) : Map<WordForm, String> {
        val lastColumn = sheetData[row].size
        val suffixes: MutableMap<WordForm, String> = mutableMapOf() 
        for (j in 1..<lastColumn) {
            val suffix = getValue(sheetData[row], j)
            if (suffix != null) {
                val cardinalityS = getValue(sheetData[1], j)
                val cardinality = valueOf<Cardinality>(cardinalityS)
                val caseS = getValue(sheetData[0], ((j - 1) / 2) * 2 + 1)
                val case = valueOf<WordCase>(caseS)
                suffixes[WordForm(case!!, cardinality!!)] = clean(suffix)
            }
        }
        return suffixes
    }

    private fun getValue(row: List<Any>, index: Int) : String? {
        if (row.size > index) {
            val value = row[index] as String?
            if ("" == value) {
                return null
            } else {
                return value?.trim()
            }
        } else {
            return null;
        }
    }

    private fun clean(s: String): String {
        var result = s.trim()
        if (result.startsWith("-")) {
            result = result.substring(1)
        }
        return result;
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
