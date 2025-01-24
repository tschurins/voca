package jal.voca.quiz

import jal.voca.lang.*
import java.util.Random

data class QuizItem(val question: String, val answer: String, val moreInfo: String)

data class Quiz(val items: List<QuizItem>) {
    companion object {
        private var random = Random()

        // for testing purposes
        fun setRandom(random: Random) {
            this.random = random
        }

        fun newQuiz(dico: Dictionary, category: WordCategory?, fromWordToTranslation: Boolean) : Quiz {
            val allWords = if (category == null) null else toWeightedMap(category.words)
            return Quiz(getItems(QuizConfig(dico = dico, words = allWords, fromWordToTranslation = fromWordToTranslation)))
        }

        fun newQuiz(config: QuizConfig) : Quiz {
            return Quiz(getItems(config))
        }

        private fun getItems(config: QuizConfig) : List<QuizItem> {
            val words: Map<Translation, Int> = if (config.words == null) toWeightedMap(config.dico.allWords()) else config.words
            val wordsToUse = HashMap(words)
            val result: MutableList<QuizItem> = mutableListOf()
            val max = Math.min(config.itemCount, words.size)
            for (i in 0..<max) {
                val index = random.nextInt(getTotal(wordsToUse))
                val wt = getWord(wordsToUse, index)
                wordsToUse.remove(wt)
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

                    val nounWord = config.dico.wordLanguage.getWord(word, WordForm(wordCase, cardinality), articleType)
                    val nounTranslation = config.dico.translationLanguage.getWord(translation, WordForm(wordCase, cardinality), articleType)
                    if (config.fromWordToTranslation) {
                        fromWord = nounWord
                        toWord = nounTranslation
                    } else {
                        fromWord = nounTranslation
                        toWord = nounWord
                    }

                } else {
                    if (config.fromWordToTranslation) {
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

        private fun toWeightedMap(words: List<Translation>) : Map<Translation, Int> {
            return words.map { it to 1 }.toMap()
        }

        fun getTotal(wordsToUse: Map<Translation, Int>) : Int {
            return wordsToUse.values.sum()
        }

        fun getWord(wordsToUse: Map<Translation, Int>, index: Int) : Translation {
            var count: Int = 0
            for (entry in wordsToUse.entries.iterator()) {
                count += entry.value
                if (index <= count) {
                    return entry.key
                }
            }
            throw RuntimeException("value at index " + index + " not found in " + wordsToUse)
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

data class QuizConfig(
    val dico: Dictionary, 
    val words: Map<Translation, Int>? = null, 
    val categoryName: String? = null,
    val fromWordToTranslation: Boolean,
    val itemCount: Int = 10,
)
