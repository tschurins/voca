package jal.voca.lang

interface Language {
    val name: String
    val wordComparator: WordComparator
    val characterConvertor: (String) -> String
    fun getArticle(word: Word, form: WordForm, articleType: ArticleType?): String
    fun getWord(word: Word, form: WordForm, articleType: ArticleType?): String
}

interface WordComparator {
    fun compare(w1: String, w2: String): ComparatorResult

    enum class ComparatorResult {
        EXACT_MATCH,
        HOMOPHONE,
        NO_MATCH,
    }
}

enum class WordCase {
    NOMINATIVE,
    ACCUSATIVE,
    GENITIVE,
    VOCATIVE,
}

data class WordForm(val case: WordCase, val cardinality: Cardinality)

enum class ArticleType {
    DEFINITE,
    INDEFINITE,
}


class DefaultWordComparator: WordComparator {
    override fun compare(w1: String, w2: String): WordComparator.ComparatorResult {
        val s1 = w1.lowercase()
        val s2 = w2.lowercase()
        if (s1 == s2) {
            return WordComparator.ComparatorResult.EXACT_MATCH
        }
        return WordComparator.ComparatorResult.NO_MATCH
    }
}
