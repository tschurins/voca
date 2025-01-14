package jal.voca.lang.io

import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import java.io.InputStream

class DictionaryExcelReaderTest {
    @Test @Ignore
    fun readExcelFile() {
        val inputStream: InputStream = this::class.java.getResourceAsStream("/jal/voca/lang/io/Duo-G.xlsx")
        val categories = DictonaryExcelReader(listOf("Lettres", "Conjugation", "Forms")).readCategories(inputStream)
        
    }
}