package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayInputStream

class DictionaryCsvReaderTest {
    @Test
    fun readWord() {
        val stream = ByteArrayInputStream("cat|unit|word|trans|n(n)".toByteArray())
        val allWords = DictionaryCsvReader().readWords(stream)

        assertEquals(1, allWords.size)
        val ct = allWords[0]
        assertEquals("unit", ct.unit)
        assertEquals(setOf("cat"), ct.categories)
        val translation = ct.translation
        assertEquals("word", translation.word.word)
        assertEquals("trans", translation.translation.word)
    }
}