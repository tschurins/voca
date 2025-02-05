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
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C1"), "U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C1"), "U2"),
        )
        val dico = Dictionary(English(), English(), words)

        val units = dico.getUnits()
        assertEquals(2, units.size)
        val unit2 = units["U2"]!!
        assertEquals(2, unit2.words.size)
    }

    @Test
    fun getUnit_empty() {
        val t = TypeInfo()
        val words = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1"), "U1"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C1"), "U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C1"), ""),
        )
        val dico = Dictionary(English(), English(), words)

        val units = dico.getUnits()
        assertEquals(2, units.size)
    }

    @Test
    fun sortUnits() {
        val t = TypeInfo()
        val words = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "S1U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1"), "S1U2"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C1"), "S1U10"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C1"), "S2U1"),
        )
        val dico = Dictionary(English(), English(), words)

        val regex = Regex("S(\\d+)U(\\d+)")
        val unitComparator: Comparator<String> = Comparator.comparing({ 
            val match = regex.find(it)
            if (match != null) {
                ("" + (match.groups[1]!!.value.toInt() * 1000 + match.groups[2]!!.value.toInt())).padStart(5, '0')
            } else {
                it
            }
        })
        val sortedUnits = ArrayList(dico.getUnits().toSortedMap(unitComparator).keys)
        val expected = listOf("S1U1", "S1U2", "S1U10", "S2U1")
        assertEquals(expected, sortedUnits)
    }

    @Test
    fun diff() {
        val t = TypeInfo()
         val words1 = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "S1U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1"), "S1U2"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C1"), "S1U2"),
        )
        val dico1 = Dictionary(English(), English(), words1)

        val words2 = listOf(
            CategorizedTranslation(Translation(Word("A", t), Word("A", t)), listOf("C1"), "S1U1"),
            CategorizedTranslation(Translation(Word("B", t), Word("B", t)), listOf("C1"), "S1U2"),
            CategorizedTranslation(Translation(Word("C", t), Word("C", t)), listOf("C1"), "S1U2"),
            CategorizedTranslation(Translation(Word("D", t), Word("D", t)), listOf("C1"), "S2U1"),
        )
        val dico2 = Dictionary(English(), English(), words2)

        val diff = dico2.diff(dico1)
        assertEquals(1, diff.words.size)
   }
}