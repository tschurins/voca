package jal.voca.lang.io

import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import java.io.*

class WordFormExcelReaderTest {
    @Test @Ignore
    fun readExcelFile() {
        val inputStream: InputStream = FileInputStream("Duo-G.xlsx")
        val wordForms = WordFormExcelReader().readWordForms(inputStream)

        println(wordForms.suffixes.size)
        
    }
}