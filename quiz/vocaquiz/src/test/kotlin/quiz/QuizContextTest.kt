package jal.voca.quiz

import jal.voca.lang.*
import jal.voca.lang.WordComparator.ComparatorResult
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class QuizContextTest {
    @Test
    fun getSuccess_singleAnswer() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "Answer", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("answer")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_failure() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "Answer", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("wrong")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.FAILURE, result)
    }

    @Test
    fun getSuccess_homophone() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "Answer with Y", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("answer with i")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.SUCCESS_HOMOPHONE, result)
    }

    @Test
    fun getSuccess_comment() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "Answer (comment)", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("answer")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_multipleAnswers() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "Answer1 / Answer2", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("answer2")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_multipleAnswers_homophone() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "Q?", "pie / pye", ""))), getLanguage())
        context.nextItem()
        context.setAnswer("pye")
        val result = context.checkSuccess()
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_alternative() {
        val item = QuizItem("id", "Q?", "A[n] Answer", "")
        val context = QuizContext(Quiz(listOf(item)), getLanguage())
        context.nextItem()
        context.setAnswer("an answer")
        assertEquals(AnswerResult.SUCCESS, context.checkSuccess())
        context.setAnswer("a answer")
        assertEquals(AnswerResult.SUCCESS, context.checkSuccess())
    }

    @Test
    fun getSuccess_trim() {
        val item = QuizItem("id", "Q?", "A", "")
        val context = QuizContext(Quiz(listOf(item)), getLanguage())
        context.nextItem()
        context.setAnswer("A ")
        assertEquals(AnswerResult.SUCCESS, context.checkSuccess())
    }


    private fun getLanguage() : Language {
        return object : Language {
            override val name = "Lang"

            override fun getArticle(word: Word, form: WordForm, articleType: ArticleType?): String {
                return ""
            }

            override fun getWord(word: Word, form: WordForm, articleType: ArticleType?): String {
                return word.word
            }

            override val characterConvertor: (String) -> String = { it }

            override val wordComparator = object : WordComparator {
                override fun compare(w1: String, w2: String): ComparatorResult {
                    val s1 = w1.lowercase()
                    val s2 = w2.lowercase()
                    if (s1 == s2) {
                        return ComparatorResult.EXACT_MATCH
                    }
                    val r1 = s1.replace("y", "i")
                    val r2 = s2.replace("y", "i")
                    if (r1 == r2) {
                        return ComparatorResult.HOMOPHONE
                    }
                    return ComparatorResult.NO_MATCH

                }
            }
        }
    }
}