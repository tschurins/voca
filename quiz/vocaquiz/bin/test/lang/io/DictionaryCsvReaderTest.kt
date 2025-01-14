package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayInputStream

class DictionaryCsvReaderTest {
    @Test
    fun readWord() {
        val stream = ByteArrayInputStream("cat|word|trans|n(n)".toByteArray())
        val categories = DictionaryCsvReader().readCategories(stream)

        assertEquals(1, categories.size)
        val translations = categories["cat"]
        assertNotNull(translations)
        assertEquals(1, translations!!.words.size)
        val translation = translations!!.words[0]
        assertEquals("word", translation.word.word)
        assertEquals("trans", translation.translation.word)
    }
}