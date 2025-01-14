package jal.voca.lang

class English: Language {
    override val name = "English"

    override val wordComparator = DefaultWordComparator()

    override val characterConvertor = { text: String -> text }
    

    override fun getArticle(word: Word, form: WordForm, articleType: ArticleType?): String {
        if (word.wordType == WordType.NOUN) {
            return when(articleType) {
                ArticleType.DEFINITE -> "the"
                ArticleType.INDEFINITE -> if (form.cardinality == Cardinality.SINGULAR) "a[n]" else ""
                else -> ""
            }
        }
        return ""
    }

    override fun getWord(word: Word, form: WordForm, articleType: ArticleType?): String {
        val article = getArticle(word, form, articleType)
        val prefix = if (article.length > 0) article + " " else ""

        val parts = WordParts(word.word)
        val reformed: WordParts
        if (form.cardinality == Cardinality.PLURAL && word.cardinality == null) {
            reformed = parts.map { prefix + getPlural(it) }
        } else {
            reformed = parts.map { prefix + it }
        }
        return reformed.toString()
    }

    companion object {
        val pluralExceptions = mapOf(
            "photo" to "photos",
            "piano" to "pianos",
            "halo" to "halos",
            "child" to "children",
            "man" to "men",
            "woman" to "women",
            "tooth" to "teeth",
            "foot" to "feet",
            "goose" to "geese",
            "mouse" to "mice",
            "person" to "people",
            "bus" to "busses",
            "wife" to "wives",
            "wolf" to "wolves",
            "sheep" to "sheep",
            "series" to "series",
            "species" to "species",
            "deer" to "deer",
            "son" to "sons",
            "video" to "videos",
            "disco" to "discos",
            "pants" to "pants",
        )
        val consonants = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"
    }

    private fun getPlural(s: String): String {
        val plural = pluralExceptions[s]
        if (plural != null) {
            return plural
        } else if (s.endsWith("us")) {
            return s.substring(0, s.length - 2) + "i"
        } else if (s.endsWith("is")) {
            return s.substring(0, s.length - 2) + "es"
        } else if (s.endsWith("s") || s.endsWith("sh") || s.endsWith("ch") || s.endsWith("x") || s.endsWith("z")) {
            return s + "es"
        } else if (s.endsWith("y")) {
            val beforeY = s[s.length - 2]
            if (consonants.contains(beforeY)) {
                return s.substring(0, s.length - 1) + "ies"            
            }
        } else if (s.endsWith("o")) {
            return s + "es"
        /*} else if (s.endsWith("on") && !s.endsWith("tion")) {
            return s.substring(0, s.length - 2) + "a"*/
        }
        return s + "s"
    }
}