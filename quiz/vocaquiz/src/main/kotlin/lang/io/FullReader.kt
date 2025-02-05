package jal.voca.lang.io

import jal.voca.lang.*

interface FullReader {
    fun getWords() : List<CategorizedTranslation>
    fun getForms() : WordForms
}