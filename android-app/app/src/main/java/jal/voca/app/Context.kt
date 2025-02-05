package jal.voca.app

import jal.voca.lang.Dictionary
import jal.voca.lang.WordCategory
import jal.voca.lang.io.DictionaryCsvReader
import jal.voca.quiz.QuizContext
import java.io.File


/**
 * Global dictionary for the application. It is used to create the quiz.
 * Singleton - loaded on demand.
 */
var globalDictionary: Dictionary? = null

/**
 * Loads and returns the application dictionary used to create the quiz.
 */
fun getGlobalDictionary(appDir: File? = null) : Dictionary {
    if (globalDictionary == null) {
        val dicoDir = if (appDir == null) null else getDictionaryDirectory(appDir)
        println("[voca] load dictionary; ext: " + dicoDir)
        globalDictionary = DictionaryCsvReader().readGreekDictionary(dicoDir)
    }
    return globalDictionary!!
}

fun getDictionaryDirectory(appDir: File) : File {
    return File(appDir, "voca")
}

/**
 * Current quiz context displayed in the QuizActivity
 */
var globalContext : QuizContext? = null

/**
 * Global configuration for the application.
 * Singleton.
 */
val globalConfiguration = AppConfiguration()

data class AppConfiguration(var itemCount: Int = 10)

/**
 * Loader to get the categories from a dictionary.
 * It is initialized based on the choice of category type to use (word-categories
 * or units).
 */
var globalCategoryLoader: ((dictionary: Dictionary, sortOptions: SortOptions) -> Map<String, WordCategory>)? = null

/**
 * The selected category for the quiz.
 */
var globalCategory: WordCategory? = null

val globalSortOptions = SortOptions()
data class SortOptions(
    var asc: Boolean = true,
    var numericPattern: String? = null,
) {
    fun getComparator(): Comparator<String> {
        var comp: Comparator<String>
        if (numericPattern != null) {
            val regex = numericPattern!!.toRegex()
            comp = Comparator.comparing {
                val match = regex.find(it)
                if (match != null) {
                    ("" + (match.groups[1]!!.value.toInt() * 1000 + match.groups[2]!!.value.toInt())).padStart(5, '0')
                } else {
                    it
                }
            }
        } else {
            comp = Comparator.comparing { it }
        }

        if (!asc) {
            comp = comp.reversed()
        }
        return comp
    }
}