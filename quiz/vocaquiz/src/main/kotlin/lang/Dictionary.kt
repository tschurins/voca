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
        return result.mapValues { WordCategory(it.key, it.value) }
    }
}

data class WordCategory(
    val name: String,
    val words: List<Translation>,
)