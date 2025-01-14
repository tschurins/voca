package jal.voca.lang

import jal.voca.lang.WordComparator.ComparatorResult
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class EnglishTest {
    @Test
    fun plural_butterfly() {
        val result = English().getWord(Word("butterfly", TypeInfo()), WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL), null)
        assertEquals("butterflies", result)
    }

    @Test
    fun plural_siblings() {
        val result = English().getWord(Word("siblings", TypeInfo(type = WordType.NOUN, cardinality = Cardinality.PLURAL)), WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL), null)
        assertEquals("siblings", result)
    }
}