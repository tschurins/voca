package jal.voca.lang

data class Translation(
    val word: Word,
    val translation: Word
)

data class CategorizedTranslation(
    val translation: Translation,
    val categories: List<String>,
    val unit: String
)
