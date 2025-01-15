package jal.voca.quiz

import jal.voca.lang.*
import jal.voca.lang.WordComparator.ComparatorResult

class QuizContext {
    val score: Score = Score()
    private val _failures: MutableList<QuizItem> = mutableListOf()
    val failures: List<QuizItem>
        get() = _failures
    val answerLanguage: Language
    private val itemIterator: Iterator<QuizItem>
    var currentItem: QuizItem? = null
        private set
    var currentAnswer: String? = null
    
    constructor(quiz: Quiz, answerLanguage: Language) {
        this.answerLanguage = answerLanguage
        itemIterator = quiz.items.iterator()
    }

    private fun setSuccess(result: AnswerResult) {
        when (result) {
            AnswerResult.SUCCESS -> success()
            AnswerResult.SUCCESS_HOMOPHONE -> successHomophone()
            AnswerResult.FAILURE -> failure()
        }
    }
    
    fun nextItem() : QuizItem {
        currentAnswer = null
        currentItem = itemIterator.next()
        return currentItem!!
    }
    
    fun hasNextItem() : Boolean {
        return itemIterator.hasNext()
    }
    
    fun success() {
        score.success()
    }

    fun successHomophone() {
        score.success()
    }

    fun failure() {
        score.failure()
        _failures.add(currentItem!!)
    }

    fun setAnswer(answer: String) : String {
        val converted = answerLanguage.characterConvertor(answer.trim())
        currentAnswer = converted
        return converted
    }

    fun checkSuccess(): AnswerResult {
        var bestResult = AnswerResult.FAILURE
        for (possibleExpected in WordParts(currentItem!!.answer).all) {
            val alternativeIndex = possibleExpected.indexOf("[")
            val possibleResult: ComparatorResult
            if (alternativeIndex >= 0) {
                val alternativeEnd = possibleExpected.indexOf("]", alternativeIndex)
                // without
                val firstPossibility = possibleExpected.substring(0, alternativeIndex) + 
                        possibleExpected.substring(alternativeEnd + 1)
                val firstPossibleResult = answerLanguage.wordComparator.compare(firstPossibility.trim(), currentAnswer!!)
                when (firstPossibleResult) {
                    ComparatorResult.EXACT_MATCH -> {
                        bestResult = AnswerResult.SUCCESS
                        break
                    }
                    ComparatorResult.HOMOPHONE -> {
                        if (bestResult.order < AnswerResult.SUCCESS_HOMOPHONE.order) {
                            bestResult = AnswerResult.SUCCESS_HOMOPHONE
                        }
                    }
                    ComparatorResult.NO_MATCH -> { /* do nothing */ }
                }

                // with alternative
                val secondPossibility = possibleExpected.substring(0, alternativeIndex) +
                        possibleExpected.substring(alternativeIndex + 1, alternativeEnd) +
                        possibleExpected.substring(alternativeEnd + 1)
                possibleResult = answerLanguage.wordComparator.compare(secondPossibility.trim(), currentAnswer!!)

            } else {
                possibleResult = answerLanguage.wordComparator.compare(possibleExpected.trim(), currentAnswer!!)
            }
            when (possibleResult) {
                ComparatorResult.EXACT_MATCH -> {
                    bestResult = AnswerResult.SUCCESS
                    break
                }
                ComparatorResult.HOMOPHONE -> {
                    if (bestResult.order < AnswerResult.SUCCESS_HOMOPHONE.order) {
                        bestResult = AnswerResult.SUCCESS_HOMOPHONE
                    }
                }
                ComparatorResult.NO_MATCH -> { /* do nothing */ }
            }
        }
        setSuccess(bestResult)
        return bestResult
    }
}

enum class AnswerResult(val order: Int) {
    SUCCESS(10),
    FAILURE(0),
    SUCCESS_HOMOPHONE(7)
}