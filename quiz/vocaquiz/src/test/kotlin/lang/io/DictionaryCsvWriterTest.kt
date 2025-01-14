package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayOutputStream

class DictionaryCsvWriterTest {
    @Test
    fun writeWord() {
        val categories = mapOf("cat" to WordCategory("cat", listOf(Translation(
            Word("word", TypeInfo(type = WordType.NOUN, gender = Gender.NEUTER)), 
            Word("trans", TypeInfo())
        ))))
        val output = ByteArrayOutputStream()
        DictionaryCsvWriter().writeCategories(categories, output)
        assertEquals("cat|word|trans|n(n,,)\n", output.toString())
    }
}