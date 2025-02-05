package jal.voca.lang

data class Dictionary(
    val wordLanguage: Language,
    val translationLanguage: Language,
    val words: List<CategorizedTranslation>,
) {
    fun allWords() : List<Translation> {
        return words.map { it.translation }
    }

    fun getCategories() : Map<String, WordCategory> {
        val result: MutableMap<String, MutableList<Translation>> = mutableMapOf()
        for (ct in words) {
            for (cat in ct.categories) {
                result.getOrPut(cat) { mutableListOf() }.add(ct.translation)
            }
        }
        return result.mapValues { WordCategory(it.key, it.value) }
    }

    fun getUnits() : Map<String, WordCategory> {
        val result: MutableMap<String, MutableList<Translation>> = mutableMapOf()
        for (ct in words) {
            result.getOrPut(ct.unit) { mutableListOf() }.add(ct.translation)
        }
        result.remove("")
        return result.mapValues { WordCategory(it.key, it.value) }
    }

    /**
     * Computes the difference between this dictionary and the given one.
     * The returned dictionary will contain only the words present in this dictionary and
     * not in the given one.
     */
    fun diff(other: Dictionary) : Dictionary {
        val diffWords = ArrayList(words)
        diffWords.removeAll(other.words)
        return Dictionary(wordLanguage, translationLanguage, diffWords)
    }
}

data class WordCategory(
    val name: String,
    val words: List<Translation>,
)