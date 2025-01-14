package jal.voca.quiz

import jal.voca.lang.*
import java.util.Random

data class QuizItem(val question: String, val answer: String, val moreInfo: String)

data class Quiz(val items: List<QuizItem>) {
    companion object {
        private val random = Random()

        fun newQuiz(dico: Dictionary, category: WordCategory?, fromWordToTranslation: Boolean) : Quiz {
            val allWords = if (category == null) dico.allWords() else category.words
            return Quiz(getItems(dico, allWords, fromWordToTranslation))
        }

        private fun getItems(dico: Dictionary, words: List<Translation>, fromWordToTranslation: Boolean) : List<QuizItem> {
            val wordsToUse = ArrayList(words)
            val result: MutableList<QuizItem> = mutableListOf()
            val max = Math.min(10, words.size)
            for (i in 0..<max) {
                val index = random.nextInt(wordsToUse.size)
                val wt = wordsToUse[index]
                wordsToUse.removeAt(index)
                val word = wt.word
                val translation = wt.translation
                val moreInfo: MutableList<String> = mutableListOf()
                if (word.gender != null) {
                    moreInfo.add(word.gender.toString())
                }
                if (word.cardinality != null) {
                    moreInfo.add(word.cardinality.toString())
                }
                if (word.pluralForm != null) {
                    moreInfo.add("plural with " + word.pluralForm)
                }

                val fromWord: String
                val toWord: String
                if (word.wordType == WordType.NOUN) {
                    val cardinality = getQuestionCardinality(word)
                    val wordCase = getQuestionCase(word)
                    val articleType = getQuestionArticleType(word)

                    if (wordCase != WordCase.NOMINATIVE || (word.cardinality == null && cardinality != Cardinality.SINGULAR)) {
                        moreInfo.add("original: " + WordParts(word.word).noComment())
                    }

                    val nounWord = dico.wordLanguage.getWord(word, WordForm(wordCase, cardinality), articleType)
                    val nounTranslation = dico.translationLanguage.getWord(translation, WordForm(wordCase, cardinality), articleType)
                    if (fromWordToTranslation) {
                        fromWord = nounWord
                        toWord = nounTranslation
                    } else {
                        fromWord = nounTranslation
                        toWord = nounWord
                    }

                } else {
                    if (fromWordToTranslation) {
                        fromWord = word.word
                        toWord = translation.word
                    } else {
                        fromWord = translation.word
                        toWord = word.word
                    }
                }
                result.add(QuizItem(fromWord, toWord, moreInfo.joinToString()))
            }
            return result
        }

        private fun getQuestionCardinality(word: Word): Cardinality {
            if (word.cardinality != null) {
                return word.cardinality!!
            }
            return if (random.nextInt(3) < 2) Cardinality.SINGULAR else Cardinality.PLURAL
        }

        private fun getQuestionCase(word: Word): WordCase {
            return WordCase.NOMINATIVE
        }

        private fun getQuestionArticleType(word: Word): ArticleType {
            return if (random.nextInt(3) < 2) ArticleType.DEFINITE else ArticleType.INDEFINITE
        }
    }
}

class Score {
    var score = 0
        private set
    var total = 0
        private set
    
    fun success() {
        score++
        total++
    }

    fun failure() {
        total++
    }
}