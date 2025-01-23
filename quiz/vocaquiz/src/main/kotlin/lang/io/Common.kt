package jal.voca.lang.io

import jal.voca.lang.*

inline fun <reified T : kotlin.Enum<T>> valueOf(type: String?): T? {
    return java.lang.Enum.valueOf(T::class.java, type)
}

class WordsBuilder {
    private val words: MutableMap<String, CategorizedTranslation> = mutableMapOf()

    fun getWords() = ArrayList(words.values)

    fun add(w: String, t: String, typeInfo: TypeInfo, category: String, unit: String) {
        val translation = Translation(
            Word(w, typeInfo),
            Word(t, TypeInfo(type = typeInfo.type, cardinality = typeInfo.cardinality))
        )
        val ct = CategorizedTranslation(translation, mutableListOf(category), unit)

        val existing = words.put(w, ct)
        if (existing != null) {
            if (existing.translation.word.typeInfo != typeInfo) {
                throw RuntimeException("not the same typeInfo for word " + w + "(was " + existing.translation.word.typeInfo + " while expecting " + typeInfo + ")")
            }
            if (existing.translation.translation.word != t) {
                throw RuntimeException("not the same translation for word " + w + "(was " + existing.translation.translation.word + " while expecting " + t + ")")
            }
            if (existing.unit != unit) {
                throw RuntimeException("not the same unit for word " + w + "(was " + existing.unit + " while expecting " + unit + ")")
            }
            (ct.categories as MutableList).addAll(existing.categories)
        }
    }
}