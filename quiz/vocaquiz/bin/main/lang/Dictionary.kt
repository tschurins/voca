package jal.voca.lang

data class Dictionary(
    val wordLanguage: Language,
    val translationLanguage: Language,
    val categories: List<WordCategory>,
) {
    fun allWords() : List<Translation> {
        val result: MutableList<Translation> = mutableListOf()
        for (category in categories) {
            result.addAll(category.words)
        }
        return result
    }
}

data class WordCategory(
    val name: String,
    val words: List<Translation>,
)