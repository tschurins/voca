package jal.voca.lang.io

import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import jal.voca.lang.*

class DictionaryExcelReaderTest {
    @Test @Ignore
    fun readGoogleSheet() {
        val greekSheet = DictionaryGoogleSheetReader.greek()
        val categories = greekSheet.getWords()
        println("cat: " + categories)
        val forms = greekSheet.getForms()
        println("forms: " + forms)
    }

    @Test @Ignore
    fun compareWithSources() {
        val fromSources = DictionaryCsvReader().readGreekDictionary()

        val greekSheet = DictionaryGoogleSheetReader.greek()
        val words = greekSheet.getWords()

        val fromGS = Dictionary(fromSources.wordLanguage, fromSources.translationLanguage, words)
        val diffGSToSources = fromGS.diff(fromSources)
        assertEquals(0, diffGSToSources.words.size, "remaining words:\n0" + diffGSToSources.words)
    }
}