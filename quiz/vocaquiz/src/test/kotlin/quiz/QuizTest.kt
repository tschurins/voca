package jal.voca.quiz

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
    
class QuizTest {
    @Test
    fun createQuiz_all() {
        val words = mutableListOf(
            CategorizedTranslation(Translation(Word("w1", TypeInfo()), Word("t1", TypeInfo())), listOf("cat1"), "u1"),
            CategorizedTranslation(Translation(Word("w2", TypeInfo()), Word("t2", TypeInfo())), listOf("cat1"), "u1"),
            CategorizedTranslation(Translation(Word("w3", TypeInfo()), Word("t3", TypeInfo())), listOf("cat3"), "u1"),
        )
        val dico = Dictionary(greekLanguage, English(), words)
        val quiz = Quiz.newQuiz(dico, null, true)

        assertEquals(3, quiz.items.size)
    }

    @Test
    fun createQuiz_cat() {
        val words = mutableListOf(
            CategorizedTranslation(Translation(Word("w1", TypeInfo()), Word("t1", TypeInfo())), listOf("cat1"), "u1"),
            CategorizedTranslation(Translation(Word("w2", TypeInfo()), Word("t2", TypeInfo())), listOf("cat1"), "u1"),
            CategorizedTranslation(Translation(Word("w3", TypeInfo()), Word("t3", TypeInfo())), listOf("cat3"), "u1"),
        )
        val dico = Dictionary(greekLanguage, English(), words)
        val quiz = Quiz.newQuiz(dico, dico.getCategories()["cat1"], true)

        assertEquals(2, quiz.items.size)
    }

    @Test
    fun createQuiz_weighted() {
        val words = mapOf(
            Translation(Word("A1", TypeInfo()), Word("B1", TypeInfo())) to 1,
            Translation(Word("A2", TypeInfo()), Word("B2", TypeInfo())) to 2,
            Translation(Word("A3", TypeInfo()), Word("B3", TypeInfo())) to 3,
        )
        assertEquals(6, Quiz.getTotal(words))
        assertEquals("A2", Quiz.getWord(words, 3).word.word)
    }

}