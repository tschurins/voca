package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayOutputStream

class ArticlesCsvWriterTest {
    @Test
    fun writeForm() {
        val articles = listOf(ArticlesPerGender(Gender.NEUTER, ArticleType.DEFINITE, mapOf(
            WordForm(WordCase.NOMINATIVE, Cardinality.SINGULAR) to "nosi",
            WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL)   to "nopl",
            WordForm(WordCase.ACCUSATIVE, Cardinality.SINGULAR) to "acsi",
            WordForm(WordCase.ACCUSATIVE, Cardinality.PLURAL)   to "acpl",
        )))
        val output = ByteArrayOutputStream()
        ArticlesCsvWriter().writeArticles(articles, output)
        assertEquals("gender|type|NOMINATIVE,SINGULAR|NOMINATIVE,PLURAL|ACCUSATIVE,SINGULAR|ACCUSATIVE,PLURAL\n" +
            "NEUTER|DEFINITE|nosi|nopl|acsi|acpl\n", output.toString())
    }
}