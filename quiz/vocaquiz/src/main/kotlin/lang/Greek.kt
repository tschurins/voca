package jal.voca.lang

import jal.voca.lang.WordComparator.ComparatorResult
import jal.voca.lang.io.*
import java.io.*

class Greek: Language {
    override val name = "Greek"

    val greekCharacters = GreekCharacters()

    override val wordComparator = GreekWordComparator(greekCharacters)

    override val characterConvertor = { text: String -> greekCharacters.convertToGreek(text) }

    val wordForms: WordForms

    /**
     * Creates the Greek language based on the files packaged within the library
     * and the optional files present in the given directory.
     */
    constructor(dir: File? = null) {
        val gaFile = if (dir == null) null else File(dir, "greek-articles.csv")
        val inputArticles = if (gaFile != null && gaFile.exists()) {
            println("[voca] read articles from " + gaFile.absolutePath)
            FileInputStream(gaFile)
        } else {
            println("[voca] read articles from library")
            this::class.java.getResourceAsStream("/jal/voca/lang/greek-articles.csv")
        }
        val ifFile = if (dir == null) null else File(dir, "greek-forms.csv")
        val inputForms = if (ifFile != null && ifFile.exists()) {
            println("[voca] read forms from " + ifFile.absolutePath)
            FileInputStream(ifFile)
        } else {
            println("[voca] read forms from library")
            this::class.java.getResourceAsStream("/jal/voca/lang/greek-forms.csv")
        }
        try {
            val articles = ArticlesCsvReader().readArticles(inputArticles)
            val forms = WordFormCsvReader().readWordForms(inputForms)
            wordForms = WordForms(forms, articles)
        } finally {
            inputArticles.close()
            inputForms.close()
        }
        
        
    }
    

    override fun getArticle(word: Word, form: WordForm, articleType: ArticleType?): String {
        if (articleType == null) {
            return ""
        }
        val article = wordForms.getArticle(word, articleType, form)
        return if (article == null) "" else article
    }

    override fun getWord(word: Word, form: WordForm, articleType: ArticleType?): String {
        val article = getArticle(word, form, articleType)
        val prefix = if (article.length > 0) article + " " else ""
        
        val suffixes = wordForms.getSuffixes(word)
        if (suffixes == null) {
            return prefix + word.word
        } else {
            val cardinality = if (word.cardinality == null) Cardinality.SINGULAR else word.cardinality!!
            val toRemove = suffixes.forms[WordForm(WordCase.NOMINATIVE, cardinality)]
            val toAdd = suffixes.forms[form]
            return prefix + word.word.substring(0, word.word.length - toRemove!!.length) + toAdd
        }
    }

}

class GreekWordComparator(val greekCharacters: GreekCharacters): WordComparator {
    override fun compare(w1: String, w2: String): ComparatorResult {
        val s1 = greekCharacters.toNonAccentuated(w1.lowercase())
        val s2 = greekCharacters.toNonAccentuated(w2.lowercase())
        if (s1 == s2) {
            return ComparatorResult.EXACT_MATCH
        }
        val h1 = greekCharacters.toHomophone(s1)
        val h2 = greekCharacters.toHomophone(s2)
        if (h1 == h2) {
            return ComparatorResult.HOMOPHONE
        } else {
            return ComparatorResult.NO_MATCH
        }
    }
}

class GreekCharacters {
    fun convertToGreek(s: String): String {
        return s.split(" ").map { convertWordToGreek(it) }.joinToString(" ")
    }

    fun convertWordToGreek(s: String): String {
        val result = StringBuilder()
        var previous: Char? = null
        for (c in s) {
            if (c == 'h') {
                if (previous == null) {
                    throw RuntimeException("UnsupportedCharacter " + c)
                }
                try {
                    result.append(getGreekLetterFor(previous + "h"))
                    previous = null
                } catch (e: RuntimeException) {
                    result.append(getGreekLetterFor("" + previous))
                    previous = c
                }

            } else if (previous != null && c == 's' && previous == 'p') {
                result.append(getGreekLetterFor("ps"))
                previous = null

            } else {
                if (previous != null) {
                    result.append(getGreekLetterFor("" + previous))
                }
                previous = c
            }
        }
        if (previous != null) {
            if (previous == 's') {
                result.append(getGreekLetterFor("s."))
            } else {
                result.append(getGreekLetterFor("" + previous))
            }
        }
        return result.toString()
    }

    private fun getGreekLetterFor(c: String): Char {
        val wasUpper = Character.isUpperCase(c[0])
        val lower = if (wasUpper) c.lowercase() else c
        val result: Char = when(lower) {
            "a" -> 'α'
            "á", "à" -> 'ά'
            "b" -> 'β'
            "g" -> 'γ'
            "d" -> 'δ'
            "e" -> 'ε'
            "é", "è" -> 'έ'
            "z" -> 'ζ'
            "y" -> 'η'
            "ý", "ỳ" -> 'ή'
            "th" -> 'θ'
            "i" -> 'ι'
            "í", "ì" -> 'ί'
            "k", "c" -> 'κ'
            "l" -> 'λ'
            "m" -> 'μ'
            "n" -> 'ν'
            "x" -> 'ξ'
            "o" -> 'ο'
            "ó", "ò" -> 'ό'
            "p" -> 'π'
            "r" -> 'ρ'
            "s" -> 'σ'
            "s." -> 'ς'
            "t" -> 'τ'
            "u", "v" -> 'υ'
            "ú", "ù" -> 'ύ'
            "f", "ph" -> 'φ'
            "ch" -> 'χ'
            "ps" -> 'ψ'
            "oh", "w" -> 'ω'
            "óh", "òh" -> 'ώ'
            else -> {
                if (c.length == 1) c[0] else throw RuntimeException(c + " - not supported")
            }
        }
        return if (wasUpper) result.uppercaseChar() else result
    }

    fun toNonAccentuated(s: String): String {
        val sb = StringBuilder()
        for (c in s) {
            sb.append(getNonAccentuated(c))
        }
        return sb.toString()
    }

    private fun getNonAccentuated(c: Char): Char {
        return when(c) {
            'ά' -> 'α'
            'έ' -> 'ε'
            'ή' -> 'η'
            'ί' -> 'ι'
            'ό' -> 'ο'
            'ύ' -> 'υ'
            'ώ' -> 'ω'
            else -> c
        }
    }

    fun toHomophone(s: String): String {
        val r1 = s
            .replace("οι", "ι")
            .replace("ει", "ι")
            .replace("αι", "ε")
            .replace("-", "")
            .replace(" ", "")
        val sb = StringBuilder()
        for (c in r1) {
            sb.append(getHomophone(c))
        }
        return sb.toString()
    }

    private fun getHomophone(c: Char): Char {
        return when(c) {
            'η', 'υ' -> 'ι'
            'ω' -> 'ο'
            else -> c
        }
    }
}

fun main(args: Array<String>) {
    println(GreekCharacters().convertToGreek(args.joinToString(" ")))
}