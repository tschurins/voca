package jal.voca.lang.io

import org.junit.jupiter.api.Assertions.*
import kotlin.test.*

class DictionaryExcelReaderTest {
    @Test @Ignore
    fun readGoogleSheet() {
        val greekSheet = DictionaryGoogleSheetReader.greek()
        val categories = greekSheet.getWords()
        println("cat: " + categories)
        val forms = greekSheet.getForms()
        println("forms: " + forms)
    }
}