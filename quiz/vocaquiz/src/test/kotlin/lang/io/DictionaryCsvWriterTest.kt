package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayOutputStream

class DictionaryCsvWriterTest {
    @Test
    fun writeWord() {
        val words = listOf(
            CategorizedTranslation(Translation(
                Word("word", TypeInfo(type = WordType.NOUN, gender = Gender.NEUTER)), 
                Word("trans", TypeInfo())
            ), listOf("cat"), "U1"),
        )

        val output = ByteArrayOutputStream()
        DictionaryCsvWriter().writeWords(words, output)
        assertEquals("cat|U1|word|trans|n(n,,)\n", output.toString())
    }
}