package jal.voca.lang

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class DictionaryTest {
    @Test
    fun getWords() {
        val t = TypeInfo()
        val words = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1", "C2"), "U1"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C2"), "U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C3"), "U2"),
        )
        val dico = Dictionary(English(), English(), words)
        val all = dico.allWords()
        assertEquals(4, all.size)
    }

    @Test
    fun getCategory() {
        val t = TypeInfo()
        val words = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1", "C2"), "U1"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C2"), "U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C3"), "U2"),
        )
        val dico = Dictionary(English(), English(), words)

        val categories = dico.getCategories()
        assertEquals(3, categories.size)
        val cat1 = categories["C1"]!!
        assertEquals(2, cat1.words.size)
    }

    @Test
    fun getUnit() {
        val t = TypeInfo()
        val words = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1", "C2"), "U1"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C2"), "U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C3"), "U2"),
        )
        val dico = Dictionary(English(), English(), words)

        val units = dico.getUnits()
        assertEquals(2, units.size)
        val unit2 = units["U2"]!!
        assertEquals(2, unit2.words.size)
    }
}