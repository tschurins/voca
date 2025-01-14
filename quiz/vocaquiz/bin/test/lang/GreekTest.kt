package jal.voca.lang

import jal.voca.lang.WordComparator.ComparatorResult
import org.junit.jupiter.api.Assertions.*
import kotlin.test.*

class GreekTest {
    @Test
    fun convertCharacters() {
        val toConvert = "Agori"
        val result = convert(toConvert)
        assertEquals("Αγορι", result)
    }

    @Test
    fun convertCharacters_lastS() {
        val toConvert = "pohs"
        val result = convert(toConvert)
        assertEquals("πως", result)
    }

    @Test
    fun convertCharacters_accent() {
        val toConvert = "agóri"
        val result = convert(toConvert)
        assertEquals("αγόρι", result)
    }

    @Test
    fun compare_accent() {
        val result = greekLanguage.wordComparator.compare(
            convert("agóri"),
            convert("Agori"),
        )
        assertEquals(ComparatorResult.EXACT_MATCH, result)
    }

    @Test
    fun compare_homophone() {
        val result = greekLanguage.wordComparator.compare(
            convert("Pohs"),
            convert("pos"),
        )
        assertEquals(ComparatorResult.HOMOPHONE, result)
    }

    @Test
    fun compare_homophone_oi() {
        val result = greekLanguage.wordComparator.compare(
            convert("choirino"),
            convert("chirino"),
        )
        assertEquals(ComparatorResult.HOMOPHONE, result)
    }

    @Test
    fun compare_homophone_ei() {
        val result = greekLanguage.wordComparator.compare(
            convert("theía"),
            convert("thia"),
        )
        assertEquals(ComparatorResult.HOMOPHONE, result)
    }

    @Test
    fun getArticle_neuter() {
        val boy = Word(convert("agori"), TypeInfo(type = WordType.NOUN, gender = Gender.NEUTER))
        val article = greekLanguage.getArticle(boy, WordForm(WordCase.NOMINATIVE, Cardinality.SINGULAR), ArticleType.DEFINITE)
        assertEquals(convert("to"), article)
    }

    @Test
    fun getWord_neuterPluralNominative() {
        val boy = Word(convert("agori"), TypeInfo(type = WordType.NOUN, gender = Gender.NEUTER))
        val result = greekLanguage.getWord(boy, WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL), ArticleType.DEFINITE)
        assertEquals(convert("ta agoria"), result)
    }


    private fun convert(s: String): String {
        return greekLanguage.characterConvertor(s)
    }
}