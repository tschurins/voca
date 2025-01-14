package jal.voca.quiz

import jal.voca.lang.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
    
class QuizTest {
    @Test
    fun createQuiz_all() {
        val categories: MutableList<WordCategory> = mutableListOf()
        categories.add(WordCategory("cat1", mutableListOf(
            Translation(Word("w1", TypeInfo()), Word("t1", TypeInfo())),
            Translation(Word("w2", TypeInfo()), Word("t2", TypeInfo())),
        )))
        categories.add(WordCategory("cat3", mutableListOf(
            Translation(Word("w3", TypeInfo()), Word("t3", TypeInfo())),
        )))
        val dico = Dictionary(greekLanguage, English(), categories)
        val quiz = Quiz.newQuiz(dico, null, true)

        assertEquals(3, quiz.items.size)
    }

    @Test
    fun createQuiz_cat() {
        val categories: MutableList<WordCategory> = mutableListOf()
        val cat = WordCategory("cat1", mutableListOf(
            Translation(Word("w1", TypeInfo()), Word("t1", TypeInfo())),
            Translation(Word("w2", TypeInfo()), Word("t2", TypeInfo())),
        ))
        categories.add(cat)
        categories.add(WordCategory("cat3", mutableListOf(
            Translation(Word("w3", TypeInfo()), Word("t3", TypeInfo())),
        )))
        val dico = Dictionary(greekLanguage, English(), categories)
        val quiz = Quiz.newQuiz(dico, cat, true)

        assertEquals(2, quiz.items.size)
    }

}