package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayOutputStream

class WordFormCsvWriterTest {
    @Test
    fun writeForm() {
        val wordForms = listOf(WordCasesPerGender(Gender.NEUTER, mapOf(
            WordForm(WordCase.NOMINATIVE, Cardinality.SINGULAR) to "nosi",
            WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL)   to "nopl",
            WordForm(WordCase.ACCUSATIVE, Cardinality.SINGULAR) to "acsi",
            WordForm(WordCase.ACCUSATIVE, Cardinality.PLURAL)   to "acpl",
        )))
        val output = ByteArrayOutputStream()
        WordFormCsvWriter().writeWordForms(wordForms, output)
        assertEquals("gender|NOMINATIVE,SINGULAR|NOMINATIVE,PLURAL|ACCUSATIVE,SINGULAR|ACCUSATIVE,PLURAL\n" +
            "NEUTER|nosi|nopl|acsi|acpl\n", output.toString())
    }
}