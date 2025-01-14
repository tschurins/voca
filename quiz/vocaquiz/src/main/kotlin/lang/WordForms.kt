package jal.voca.lang

data class WordForms(
    val suffixes: List<WordCasesPerGender>,
    val articles: List<ArticlesPerGender>
) {

    fun getSuffix(word: Word, form: WordForm): String? {
        val found = getSuffixes(word)
        return if (found == null) null else found.forms[form]
    }

    fun getSuffixes(word: Word): WordCasesPerGender? {
        for (wordSuffix in suffixes) {
            if (word.pluralForm != null) {
                if (word.gender == wordSuffix.gender && word.word.endsWith(wordSuffix.baseSuffix)) {
                    if (word.pluralForm == wordSuffix.forms[WordForm(WordCase.NOMINATIVE, Cardinality.PLURAL)]) {
                        return wordSuffix
                    }
                }
            } else {
                if (word.gender == wordSuffix.gender && word.word.endsWith(wordSuffix.baseSuffix)) {
                    return wordSuffix
                }
            }
        }

        return null
    }

    fun getArticle(word: Word, articleType: ArticleType, form: WordForm): String? {
        for (article in articles) {
            if (article.gender == word.gender && article.type == articleType) {
                return article.forms[form]
            }
        }
        return null
    }
}

data class WordCasesPerGender(
    val gender: Gender,
    val forms: Map<WordForm, String>,
) {
    val baseSuffix: String = getBaseSuffix(forms)

    companion object {
        fun getBaseSuffix(suffixes: Map<WordForm, String>) : String {
            val nominativeSingular = suffixes[WordForm(WordCase.NOMINATIVE, Cardinality.SINGULAR)]
            if (nominativeSingular == null) {
                throw RuntimeException("No nominative singular?")
            }
            return nominativeSingular
        }
    }
}

data class ArticlesPerGender(
    val gender: Gender,
    val type: ArticleType,
    val forms: Map<WordForm, String>,
)