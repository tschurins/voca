package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayInputStream

class WordFormCsvReaderTest {
    @Test
    fun readForm() {
        val stream = ByteArrayInputStream(("gender|NOMINATIVE,SINGULAR|NOMINATIVE,PLURAL|ACCUSATIVE,SINGULAR|ACCUSATIVE,PLURAL\n" +
            "NEUTER|nosi|nopl|acsi|acpl\n").toByteArray())
        val suffixes = WordFormCsvReader().readWordForms(stream)

        assertEquals(1, suffixes.size)
        val wordForms = WordForms(suffixes, listOf())
        assertEquals("nopl", wordForms.getSuffix(Word("alphanosi", TypeInfo(WordType.NOUN, Gender.NEUTER)), WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL)))
    }
}