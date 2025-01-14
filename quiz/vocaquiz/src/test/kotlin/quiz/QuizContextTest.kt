package jal.voca.quiz

import jal.voca.lang.*
import jal.voca.lang.WordComparator.ComparatorResult
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class QuizContextTest {
    @Test
    fun getSuccess_singleAnswer() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "Answer", ""), "answer")
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_failure() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "Answer", ""), "wrong")
        assertEquals(AnswerResult.FAILURE, result)
    }

    @Test
    fun getSuccess_homophone() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "Answer with Y", ""), "answer with i")
        assertEquals(AnswerResult.SUCCESS_HOMOPHONE, result)
    }

    @Test
    fun getSuccess_comment() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "Answer (comment)", ""), "answer")
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_multipleAnswers() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "Answer1 / Answer2", ""), "answer2")
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_multipleAnswers_homophone() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val result = context.getSuccess(QuizItem("Q?", "pie / pye", ""), "pye")
        assertEquals(AnswerResult.SUCCESS, result)
    }

    @Test
    fun getSuccess_alternative() {
        val context = QuizContext(Quiz(listOf()), getLanguage())
        val item = QuizItem("Q?", "A[n] Answer", "")
        assertEquals(AnswerResult.SUCCESS, context.getSuccess(item, "an answer"))
        assertEquals(AnswerResult.SUCCESS, context.getSuccess(item, "a answer"))
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