package jal.voca.lang.io

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import java.io.ByteArrayInputStream

class ArticlesCsvReaderTest {
    @Test
    fun readForm() {
        val stream = ByteArrayInputStream(("gender|type|NOMINATIVE,SINGULAR|NOMINATIVE,PLURAL|ACCUSATIVE,SINGULAR|ACCUSATIVE,PLURAL\n" +
            "NEUTER|DEFINITE|nosi|nopl|acsi|acpl\n").toByteArray())
        val articles = ArticlesCsvReader().readArticles(stream)

        assertEquals(1, articles.size)
        val wordForms = WordForms(listOf(), articles)
        assertEquals("nopl", wordForms.getArticle(Word("alpha", TypeInfo(WordType.NOUN, Gender.NEUTER)), ArticleType.DEFINITE, WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL)))
    }
}